package com.ayds.Cloudmerce.model.dto;

import java.time.Instant;
import java.util.List;

public record UserDto(
        Long id,
        String name,
        String email,
        String address,
        String nit,
        Instant createdAt,
        Boolean hasMultiFactorAuth,
        String role,
        String paymentMethod,
        List<String> permissions) {
}
