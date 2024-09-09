package com.ayds.Cloudmerce.model.dto;

import java.util.Optional;

import jakarta.validation.constraints.PositiveOrZero;

public record CompanyUpdateDto(
        Optional<String> name,
        Optional<String> address,
        Optional<@PositiveOrZero Float> shippingCost) {
}
