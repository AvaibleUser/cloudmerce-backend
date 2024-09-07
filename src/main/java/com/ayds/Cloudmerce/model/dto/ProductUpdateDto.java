package com.ayds.Cloudmerce.model.dto;

import java.util.Set;

import com.ayds.Cloudmerce.enums.ProductState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

public record ProductUpdateDto(
        String name,
        String description,
        @PositiveOrZero Float price,
        @PositiveOrZero Long stock,
        ProductState state,
        Set<@NotBlank String> categories,
        Set<@Positive @NotNull Long> categoryIds,
        @With Set<Long> imageUrls) {
}
