package com.ayds.Cloudmerce.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationStatus {

    READ("read"),
    UNREAD("unread");

    @JsonValue
    private String status;
}
