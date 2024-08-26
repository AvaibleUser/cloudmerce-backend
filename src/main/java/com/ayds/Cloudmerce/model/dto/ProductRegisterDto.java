package com.ayds.Cloudmerce.model.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;

public record ProductRegisterDto(
        @NotBlank @NonNull String name,
        @NotBlank @NonNull String description,
        @PositiveOrZero @NonNull Float price,
        @PositiveOrZero @NonNull Long stock,
        @NotEmpty @NonNull Set<Long> categories) {
}
