package com.ayds.Cloudmerce.model.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRegisterDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @PositiveOrZero Float price,
        @NotNull @PositiveOrZero Long stock,
        @NotEmpty Set<Long> categories) {
}
