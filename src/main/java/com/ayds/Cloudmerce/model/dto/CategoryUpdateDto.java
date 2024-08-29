package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateDto(
        @NotBlank String description) {
}
