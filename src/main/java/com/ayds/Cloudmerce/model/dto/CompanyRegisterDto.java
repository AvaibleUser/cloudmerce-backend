package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

public record CompanyRegisterDto(
        @NotBlank String name,
        @NotBlank String address,
        @NotNull @PositiveOrZero Float shippingCost,
        @With String logoPath) {
}
