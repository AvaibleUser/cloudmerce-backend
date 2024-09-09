package com.ayds.Cloudmerce.model.dto;

import lombok.NonNull;

public record GoogleAuthDto(
        @NonNull String qrCodeUrl,
        @NonNull String secret) {
}
