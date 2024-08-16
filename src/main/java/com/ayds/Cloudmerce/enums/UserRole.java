package com.ayds.Cloudmerce.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    ADMIN("admin"),
    NO_ADMIN("no_admin");

    @JsonValue
    private String role;
}
