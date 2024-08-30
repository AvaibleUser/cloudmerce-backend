package com.ayds.Cloudmerce.model.dto;

import java.time.Instant;

import com.ayds.Cloudmerce.enums.NotificationStatus;

import lombok.NonNull;

public record ProductNotificationDto(
        @NonNull Long id,
        @NonNull NotificationStatus status,
        @NonNull String description,
        @NonNull Instant createdAt,
        @NonNull Long productId) {
}
