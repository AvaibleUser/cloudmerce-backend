package com.ayds.Cloudmerce.model.dto;

public record RangeDto<T>(
        T min,
        T max) {
}
