package com.lucast.vetcare.customers.company;

import com.lucast.vetcare.customers.company.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer-companies")
@Tag(
        name = "Customer Companies",
        description = "Operations related to customer companies (tutor business data)"
)
public class CustomerCompanyController {

    private final CustomerCompanyService service;

    public CustomerCompanyController(CustomerCompanyService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create customer company",
            description = "Create a new customer company associated with a tutor"
    )
    public CustomerCompanyResponse create(@RequestBody @Valid CreateCustomerCompanyRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get customer company by ID",
            description = "Retrieve a customer company by its ID"
    )
    public CustomerCompanyResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(
            summary = "List customer companies",
            description = "List customer companies with pagination, optional tutorId and search query"
    )
    public Page<CustomerCompanyListItemResponse> list(
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) String query,
            @ParameterObject
            @PageableDefault(size = 20, sort = "legalName")
            Pageable pageable
    ) {
        return service.list(tutorId, query, pageable);
    }

    @GetMapping("/by-tutor/{tutorId}")
    @Operation(
            summary = "List customer companies by tutor",
            description = "Returns active customer companies associated with a tutor"
    )
    public java.util.List<CustomerCompanyListItemResponse> listByTutor(@PathVariable Long tutorId) {
        return service.listByTutor(tutorId);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update customer company",
            description = "Update an existing customer company by ID"
    )
    public CustomerCompanyResponse update(@PathVariable Long id, @RequestBody @Valid UpdateCustomerCompanyRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete customer company",
            description = "Logically delete a customer company by ID"
    )
    public void deleteLogical(@PathVariable Long id) {
        service.deleteLogical(id);
    }
}
