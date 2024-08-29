package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRegisterDto(
        @NotBlank String name,
        @NotBlank String description) {
}
