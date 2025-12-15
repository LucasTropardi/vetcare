package com.lucast.vetcare.auth.dto;

import com.lucast.vetcare.common.enums.Role;
import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        String name,
        @Email String email,
        String password,
        Role role,
        Boolean active
) {}
