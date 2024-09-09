package com.ayds.Cloudmerce.model.dto;

import java.util.Optional;

public record UserChangeDto(
        Optional<String> address,
        Optional<String> paymentMethod,
        Optional<String> currentPassword,
        Optional<String> newPassword) {
}
