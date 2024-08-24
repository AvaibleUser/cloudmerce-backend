package com.ayds.Cloudmerce.model.dto;

import java.util.Set;

import com.ayds.Cloudmerce.enums.ProductState;

import lombok.With;

public record ProductUpdateDto(
        String name,
        String description,
        Float price,
        Long stock,
        ProductState state,
        Set<Long> categories,
        @With Set<Long> imageUrls) {
}
