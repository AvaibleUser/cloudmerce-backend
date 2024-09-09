package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeDto(
        @NotBlank String password,
        @NotBlank String repeatedPassword) {
}
