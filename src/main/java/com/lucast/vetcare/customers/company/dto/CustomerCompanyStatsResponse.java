package com.lucast.vetcare.customers.company.dto;

public record CustomerCompanyStatsResponse(
        long total,
        long active,
        long inactive,
        long withAddress,
        long withFiscal,
        long withoutContact
) {}
