package com.lucast.vetcare.cashregister;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.cashregister.dto.CashRegisterResponse;
import com.lucast.vetcare.cashregister.dto.CloseCashRegisterRequest;
import com.lucast.vetcare.cashregister.dto.OpenCashRegisterRequest;
import com.lucast.vetcare.common.enums.CashRegisterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CashRegisterService {

    private final CashRegisterRepository repository;

    public CashRegisterService(CashRegisterRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CashRegisterResponse open(OpenCashRegisterRequest req) {
        Long userId = AuthContext.requireUserId();

        String registerCode = req.registerCode().trim();
        if (registerCode.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "registerCode is required");
        }

        if (repository.existsByCompanyIdAndRegisterCodeAndStatus(req.companyId(), registerCode, CashRegisterStatus.OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already an OPEN cash register for this code");
        }

        var entity = new CashRegisterEntity();
        entity.setCompanyId(req.companyId());
        entity.setRegisterCode(registerCode);
        entity.setStatus(CashRegisterStatus.OPEN);
        entity.setOpeningAmount(scaleMoney(req.openingAmount()));
        entity.setExpectedClosingAmount(scaleMoney(nvl(req.expectedClosingAmount())));
        entity.setOpenedBy(userId);
        entity.setNotes(blankToNull(req.notes()));

        return toResponse(repository.save(entity));
    }

    @Transactional
    public CashRegisterResponse close(Long id, CloseCashRegisterRequest req) {
        Long userId = AuthContext.requireUserId();

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cash register not found"));

        if (entity.getStatus() != CashRegisterStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN cash register can be closed");
        }

        entity.setStatus(CashRegisterStatus.CLOSED);
        entity.setClosingAmount(scaleMoney(req.closingAmount()));
        entity.setClosedBy(userId);
        entity.setClosedAt(java.time.OffsetDateTime.now());

        String notes = blankToNull(req.notes());
        if (notes != null) {
            entity.setNotes(notes);
        }

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public CashRegisterResponse get(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cash register not found"));
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public CashRegisterResponse getCurrentOpen(Long companyId, String registerCode) {
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required");
        }

        String normalizedRegisterCode = blankToNull(registerCode);
        if (normalizedRegisterCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "registerCode is required");
        }

        return repository.findFirstByCompanyIdAndRegisterCodeAndStatusOrderByOpenedAtDesc(
                        companyId,
                        normalizedRegisterCode,
                        CashRegisterStatus.OPEN
                )
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<CashRegisterResponse> list(Long companyId, CashRegisterStatus status, String registerCode, Pageable pageable) {
        return repository.search(companyId, status, blankToNull(registerCode), pageable)
                .map(this::toResponse);
    }

    private CashRegisterResponse toResponse(CashRegisterEntity e) {
        return new CashRegisterResponse(
                e.getId(),
                e.getCompanyId(),
                e.getRegisterCode(),
                e.getStatus(),
                e.getOpeningAmount(),
                e.getExpectedClosingAmount(),
                e.getClosingAmount(),
                e.getOpenedBy(),
                e.getOpenedAt(),
                e.getClosedBy(),
                e.getClosedAt(),
                e.getNotes(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static BigDecimal scaleMoney(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
