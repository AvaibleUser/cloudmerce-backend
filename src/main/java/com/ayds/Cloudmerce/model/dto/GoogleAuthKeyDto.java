package com.ayds.Cloudmerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthKeyDto(
        @NotBlank String authKey) {
}
