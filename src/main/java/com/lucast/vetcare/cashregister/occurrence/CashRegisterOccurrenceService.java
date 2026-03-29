package com.lucast.vetcare.cashregister.occurrence;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.cashregister.CashRegisterRepository;
import com.lucast.vetcare.cashregister.occurrence.dto.CashRegisterOccurrenceResponse;
import com.lucast.vetcare.cashregister.occurrence.dto.CreateCashRegisterOccurrenceRequest;
import com.lucast.vetcare.common.enums.CashRegisterOccurrenceType;
import com.lucast.vetcare.common.enums.CashRegisterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Service
public class CashRegisterOccurrenceService {

    private static final Set<CashRegisterOccurrenceType> OPEN_ONLY_EVENTS = Set.of(
            CashRegisterOccurrenceType.SUPPLY,
            CashRegisterOccurrenceType.WITHDRAWAL,
            CashRegisterOccurrenceType.ADJUSTMENT,
            CashRegisterOccurrenceType.PAYMENT_REVERSAL
    );

    private final CashRegisterOccurrenceRepository repository;
    private final CashRegisterRepository cashRegisterRepository;

    public CashRegisterOccurrenceService(
            CashRegisterOccurrenceRepository repository,
            CashRegisterRepository cashRegisterRepository
    ) {
        this.repository = repository;
        this.cashRegisterRepository = cashRegisterRepository;
    }

    @Transactional
    public CashRegisterOccurrenceResponse create(Long cashRegisterId, CreateCashRegisterOccurrenceRequest req) {
        Long currentUserId = AuthContext.requireUserId();

        var cashRegister = cashRegisterRepository.findById(cashRegisterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cash register not found"));

        if (OPEN_ONLY_EVENTS.contains(req.eventType()) && cashRegister.getStatus() != CashRegisterStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Occurrence type allowed only when cash register is OPEN");
        }

        var entity = new CashRegisterOccurrenceEntity();
        entity.setCashRegisterId(cashRegisterId);
        entity.setEventType(req.eventType());
        entity.setAmount(req.amount().setScale(2, RoundingMode.HALF_UP));
        entity.setDescription(blankToNull(req.description()));
        entity.setPerformedBy(currentUserId);
        entity.setApprovedBy(req.approvedBy());

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<CashRegisterOccurrenceResponse> list(Long cashRegisterId, Pageable pageable) {
        if (!cashRegisterRepository.existsById(cashRegisterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cash register not found");
        }
        return repository.findByCashRegisterId(cashRegisterId, pageable).map(this::toResponse);
    }

    private CashRegisterOccurrenceResponse toResponse(CashRegisterOccurrenceEntity e) {
        return new CashRegisterOccurrenceResponse(
                e.getId(),
                e.getCashRegisterId(),
                e.getEventType(),
                nvl(e.getAmount()),
                e.getDescription(),
                e.getPerformedBy(),
                e.getApprovedBy(),
                e.getCreatedAt()
        );
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
