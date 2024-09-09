package com.ayds.Cloudmerce.model.dto;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

public record PermissionsChangeDto(
        @NotEmpty Set<@NotEmpty String> permissions) {
}
