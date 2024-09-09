package com.ayds.Cloudmerce.model.dto;

import java.util.Optional;
import java.util.Set;

import com.ayds.Cloudmerce.enums.ProductState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

public record ProductUpdateDto(
        Optional<@NotBlank String> name,
        Optional<@NotBlank String> description,
        Optional<@NotNull @PositiveOrZero Float> price,
        Optional<@NotNull @PositiveOrZero Long> stock,
        Optional<@NotNull ProductState> state,
        Optional<@NotEmpty Set<@NotBlank String>> categories,
        Optional<@NotEmpty Set<@Positive @NotNull Long>> categoryIds,
        @With Set<Long> imageUrls) {
}
