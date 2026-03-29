package com.lucast.vetcare.cashregister;

import com.lucast.vetcare.cashregister.dto.CashRegisterResponse;
import com.lucast.vetcare.cashregister.dto.CloseCashRegisterRequest;
import com.lucast.vetcare.cashregister.dto.OpenCashRegisterRequest;
import com.lucast.vetcare.common.enums.CashRegisterStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash-registers")
@Tag(
        name = "Cash Registers",
        description = "Operations related to opening, closing and listing cash registers"
)
public class CashRegisterController {

    private final CashRegisterService service;

    public CashRegisterController(CashRegisterService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Open cash register",
            description = "Opens a new cash register session"
    )
    public CashRegisterResponse open(@RequestBody @Valid OpenCashRegisterRequest req) {
        return service.open(req);
    }

    @PostMapping("/{id}/close")
    @Operation(
            summary = "Close cash register",
            description = "Closes an open cash register session"
    )
    public CashRegisterResponse close(
            @PathVariable Long id,
            @RequestBody @Valid CloseCashRegisterRequest req
    ) {
        return service.close(id, req);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get cash register by ID",
            description = "Returns details of a cash register session"
    )
    public CashRegisterResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/current")
    @Operation(
            summary = "Get current open cash register",
            description = "Returns the currently open cash register for the given company and register code"
    )
    public ResponseEntity<CashRegisterResponse> current(
            @RequestParam Long companyId,
            @RequestParam String registerCode
    ) {
        var response = service.getCurrentOpen(companyId, registerCode);
        return response == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "List cash registers",
            description = "Lists cash registers with pagination and optional filters"
    )
    public Page<CashRegisterResponse> list(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) CashRegisterStatus status,
            @RequestParam(required = false) String registerCode,
            @ParameterObject
            @PageableDefault(sort = "openedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return service.list(companyId, status, registerCode, pageable);
    }
}
