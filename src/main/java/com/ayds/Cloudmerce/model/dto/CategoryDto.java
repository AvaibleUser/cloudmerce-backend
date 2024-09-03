package com.ayds.Cloudmerce.model.dto;

import lombok.NonNull;

public record CategoryDto(
        @NonNull Long id,
        @NonNull String name,
        @NonNull String description) {
}
