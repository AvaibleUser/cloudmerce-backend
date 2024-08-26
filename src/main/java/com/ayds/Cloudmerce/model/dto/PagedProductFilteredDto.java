package com.ayds.Cloudmerce.model.dto;

import java.util.Set;

import com.ayds.Cloudmerce.enums.ProductState;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.With;

public record PagedProductFilteredDto(
        @PositiveOrZero int page,
        @PositiveOrZero int size,
        boolean descending,
        String[] sortedBy,
        String name,
        String description,
        @Valid RangeDto<Float> price,
        @PositiveOrZero @With(onMethod_ = @NonNull) Long stock,
        ProductState state,
        Set<Long> categories) {
}
