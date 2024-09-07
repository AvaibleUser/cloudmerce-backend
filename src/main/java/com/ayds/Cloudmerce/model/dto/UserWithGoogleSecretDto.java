package com.ayds.Cloudmerce.model.dto;

import lombok.NonNull;

public record UserWithGoogleSecretDto(
    @NonNull Long id,
    @NonNull String name,
    @NonNull String email,
    @NonNull String role,
    @NonNull String paymentMethod,
    @NonNull String googleAuthKey) {
}
