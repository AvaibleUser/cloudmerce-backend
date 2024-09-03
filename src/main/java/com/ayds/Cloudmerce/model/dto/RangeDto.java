package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RangeDto<T extends Number>(
        @NotNull @PositiveOrZero T min,
        @NotNull @PositiveOrZero T max) {
}
