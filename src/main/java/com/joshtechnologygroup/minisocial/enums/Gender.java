package com.joshtechnologygroup.minisocial.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    FEMALE, MALE;

    @JsonValue
    public String toValue() {
        return name().substring(0, 1).toUpperCase() +
                name().substring(1).toLowerCase();
    }
}
