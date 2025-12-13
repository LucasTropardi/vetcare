package com.lucast.vetcare.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {}
