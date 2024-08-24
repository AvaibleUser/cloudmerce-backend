package com.ayds.Cloudmerce.model.dto;

import java.util.Set;

import lombok.NonNull;
import lombok.With;

public record ProductRegisterDto(
        @NonNull String name,
        @NonNull String description,
        @NonNull Float price,
        long stock,
        @NonNull Set<Long> categories,
        @With @NonNull Set<Long> images) {
}
