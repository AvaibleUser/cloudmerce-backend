package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpConfirmationDto(
        @NotBlank String email,
        @NotBlank String code) {
}
