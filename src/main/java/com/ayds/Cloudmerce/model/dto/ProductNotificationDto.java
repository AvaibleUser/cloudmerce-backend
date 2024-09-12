package com.ayds.Cloudmerce.model.dto;

import java.time.Instant;

import com.ayds.Cloudmerce.enums.NotificationStatus;

import lombok.NonNull;

public record ProductNotificationDto(
        @NonNull Long id,
        @NonNull NotificationStatus status,
        @NonNull Integer stock,
        @NonNull Instant createdAt,
        @NonNull String product) {
}
