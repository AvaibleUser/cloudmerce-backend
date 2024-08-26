package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record RangeDto<T extends Number>(
        @PositiveOrZero T min,
        @PositiveOrZero T max) {
}
