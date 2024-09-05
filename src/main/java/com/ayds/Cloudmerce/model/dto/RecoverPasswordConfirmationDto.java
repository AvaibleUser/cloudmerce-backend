package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public record RecoverPasswordConfirmationDto(
        @NotBlank String email,
        @NotBlank String code) {
}
