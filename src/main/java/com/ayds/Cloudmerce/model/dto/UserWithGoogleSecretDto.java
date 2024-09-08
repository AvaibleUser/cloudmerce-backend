package com.ayds.Cloudmerce.model.dto;

import lombok.NonNull;
import lombok.With;

public record UserWithGoogleSecretDto(
        @NonNull Long id,
        @NonNull String name,
        @NonNull String email,
        @NonNull String role,
        @NonNull String paymentMethod,
        @With String googleAuthKey) {
}
