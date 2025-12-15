package com.lucast.vetcare.auth.dto;

import com.lucast.vetcare.common.enums.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        boolean active
) {}
