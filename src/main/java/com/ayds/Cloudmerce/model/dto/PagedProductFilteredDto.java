package com.ayds.Cloudmerce.model.dto;

import java.util.Optional;
import java.util.Set;

import com.ayds.Cloudmerce.enums.ProductState;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

public record PagedProductFilteredDto(
        Optional<@PositiveOrZero Integer> page,
        Optional<@PositiveOrZero Integer> size,
        Optional<Boolean> descending,
        Optional<String[]> sortedBy,
        String name,
        String description,
        @Valid RangeDto<Float> price,
        @PositiveOrZero @With Long stock,
        @With ProductState state,
        Set<Long> categories) {
}
