package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SignUpDto(
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank String address,
        @NotBlank String nit,
        @NotBlank String password,
        @Positive @NotNull Long roleId,
        @Positive @NotNull Long paymentPreferenceId) {
}
