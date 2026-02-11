package com.lucast.vetcare.auth.dto;

public record UserStatsResponse(
        long total,
        long active,
        long inactive,
        long admin,
        long vet,
        long reception
) {
}
