package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SignIn2faDto(
        @NotBlank String email,
        @PositiveOrZero @NotNull Integer code) {
}
