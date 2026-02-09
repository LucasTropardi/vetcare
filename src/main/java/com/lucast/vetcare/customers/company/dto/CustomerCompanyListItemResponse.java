package com.lucast.vetcare.customers.company.dto;

public record CustomerCompanyListItemResponse(
        Long id,
        Long tutorId,
        String legalName,
        String tradeName,
        String cnpj,
        boolean active
) {}
