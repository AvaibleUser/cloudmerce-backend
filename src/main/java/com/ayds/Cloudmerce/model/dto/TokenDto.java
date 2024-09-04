package com.ayds.Cloudmerce.model.dto;

import lombok.NonNull;

public record TokenDto(
        @NonNull String accessToken) {
}
