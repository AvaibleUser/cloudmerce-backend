package com.ayds.Cloudmerce.model.dto;

import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

public record UserChangeDto(
        Optional<@NotBlank String> address,
        Optional<@NotBlank String> paymentMethod,
        Optional<@NotBlank String> currentPassword,
        Optional<@NotBlank String> newPassword) {
}
