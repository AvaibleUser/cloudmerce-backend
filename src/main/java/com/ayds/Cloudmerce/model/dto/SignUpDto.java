package com.ayds.Cloudmerce.model.dto;

import com.ayds.Cloudmerce.enums.UserRole;

public record SignUpDto(
        String username,
        String password,
        UserRole role) {
}
