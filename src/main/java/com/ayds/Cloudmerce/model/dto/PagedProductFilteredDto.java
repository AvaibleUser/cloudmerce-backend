package com.ayds.Cloudmerce.model.dto;

import java.util.Optional;
import java.util.Set;

import com.ayds.Cloudmerce.enums.ProductState;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

public record PagedProductFilteredDto(
        Optional<@PositiveOrZero Integer> page,
        Optional<@PositiveOrZero Integer> size,
        Optional<Boolean> descending,
        Optional<String[]> sortedBy,
        Optional<String> name,
        Optional<String> description,
        Optional<@Valid RangeDto<Float>> price,
        @With Optional<@PositiveOrZero Long> stock,
        @With Optional<ProductState> state,
        Optional<Set<@Positive Long>> categoryIds,
        Optional<Set<@NotBlank String>> categoryNames) {
}
