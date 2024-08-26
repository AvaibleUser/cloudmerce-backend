package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record CategoryRegisterDto(
        @NotBlank @NonNull String name,
        @NotBlank @NonNull String description) {
}
