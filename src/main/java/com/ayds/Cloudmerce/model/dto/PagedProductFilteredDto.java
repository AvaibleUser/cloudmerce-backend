package com.ayds.Cloudmerce.model.dto;

import java.util.Optional;
import java.util.Set;

import com.ayds.Cloudmerce.enums.ProductState;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

public record PagedProductFilteredDto(
        Optional<@NotNull @PositiveOrZero Integer> page,
        Optional<@NotNull @PositiveOrZero Integer> size,
        Optional<@NotNull Boolean> descending,
        Optional<@NotEmpty String[]> sortedBy,
        Optional<@NotBlank String> name,
        Optional<@NotBlank String> description,
        Optional<@Valid RangeDto<Float>> price,
        @With Optional<@NotNull @PositiveOrZero Long> stock,
        @With Optional<@NotNull ProductState> state,
        Optional<@NotEmpty Set<Long>> categoryIds,
        Optional<@NotEmpty Set<String>> categoryNames) {
}
