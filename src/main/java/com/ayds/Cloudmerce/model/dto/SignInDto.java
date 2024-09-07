package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInDto(
        @NotBlank String email,
        @NotBlank String password) {
}
