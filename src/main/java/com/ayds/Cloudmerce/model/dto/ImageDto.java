package com.ayds.Cloudmerce.model.dto;

import lombok.NonNull;

public record ImageDto(
        @NonNull String url,
        @NonNull Long productId) {
}
