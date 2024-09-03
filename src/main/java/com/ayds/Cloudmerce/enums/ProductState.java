package com.ayds.Cloudmerce.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductState {

    VISIBLE("visible"),
    HIDDEN("hidden"),
    DELETED("deleted");

    @JsonValue
    private String state;
}
