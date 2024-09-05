package com.ayds.Cloudmerce.model.dto;

import java.time.Instant;
import java.util.List;

public record RegisteredUserDto(
        Long id,
        String name,
        String email,
        String address,
        String nit,
        String googleAuthKey,
        Instant createdAt,
        String role,
        String authQrUrl,
        String paymentMethod,
        List<String> permissions) {
}
