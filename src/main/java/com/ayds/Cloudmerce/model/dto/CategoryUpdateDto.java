package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record CategoryUpdateDto(
        @NotBlank @NonNull String description) {
}
