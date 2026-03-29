package com.lucast.vetcare.cashregister.occurrence;

import com.lucast.vetcare.cashregister.occurrence.dto.CashRegisterOccurrenceResponse;
import com.lucast.vetcare.cashregister.occurrence.dto.CreateCashRegisterOccurrenceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash-registers/{cashRegisterId}/occurrences")
@Tag(
        name = "Cash Register Occurrences",
        description = "Operations related to cash register occurrences"
)
public class CashRegisterOccurrenceController {

    private final CashRegisterOccurrenceService service;

    public CashRegisterOccurrenceController(CashRegisterOccurrenceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create cash register occurrence",
            description = "Registers an occurrence for a cash register"
    )
    public CashRegisterOccurrenceResponse create(
            @PathVariable Long cashRegisterId,
            @RequestBody @Valid CreateCashRegisterOccurrenceRequest req
    ) {
        return service.create(cashRegisterId, req);
    }

    @GetMapping
    @Operation(
            summary = "List cash register occurrences",
            description = "Lists occurrences of a cash register with pagination"
    )
    public Page<CashRegisterOccurrenceResponse> list(
            @PathVariable Long cashRegisterId,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return service.list(cashRegisterId, pageable);
    }
}
