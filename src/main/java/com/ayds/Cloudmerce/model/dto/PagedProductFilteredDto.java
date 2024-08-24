package com.ayds.Cloudmerce.model.dto;

import java.util.Set;

import com.ayds.Cloudmerce.enums.ProductState;

public record PagedProductFilteredDto(
        int page,
        int size,
        boolean descending,
        String sortedBy,
        String name,
        String description,
        RangeDto<Float> price,
        Long stock,
        ProductState state,
        Set<Long> categories) {
}
