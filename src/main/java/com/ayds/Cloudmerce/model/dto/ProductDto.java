package com.ayds.Cloudmerce.model.dto;

import java.time.Instant;
import java.util.List;

import com.ayds.Cloudmerce.enums.ProductState;

import lombok.NonNull;

public record ProductDto(
        @NonNull Long id,
        @NonNull String name,
        @NonNull String description,
        @NonNull Float price,
        @NonNull Long stock,
        @NonNull ProductState state,
        @NonNull Instant creationAt,
        @NonNull List<String> categories,
        @NonNull List<String> imageUrls) {
}
