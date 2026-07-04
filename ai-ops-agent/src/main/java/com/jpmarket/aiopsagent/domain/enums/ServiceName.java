package com.jpmarket.aiopsagent.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ServiceName {

    PRODUCT("product"),
    ORDER("order"),
    INVENTORY("inventory"),
    NOTIFICATION("notification"),
    GATEWAY("gateway");

    private final String propertyName;

    public static ServiceName from(String value) {
        return Arrays.stream(values())
                .filter(s ->
                        s.name().equalsIgnoreCase(value)
                                || s.propertyName.equalsIgnoreCase(value)
                                || (s.propertyName + "-service").equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown service: " + value));
    }
}
