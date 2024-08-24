package com.ayds.Cloudmerce.model.dto;

import lombok.NonNull;

public record CategoryRegisterDto(
        @NonNull String name,
        @NonNull String description) {
}
