package com.example.timecapsule_backend.domain.capsule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ThemeType {
    CHRISTMAS_TREE("크리스마스 트리"),
    BIRTHDAY_CAKE("생일 케이크"),
    EXAM_CALENDAR("수능 달력"),
    ANNIVERSARY_PHOTO_FRAME("기념일 사진액자"),
    TIME_CAPSULE_VAULT("타임캡슐 금고");

    private final String displayName;

    ThemeType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static ThemeType from(String value) {
        for (ThemeType type : values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown theme type: " + value);
    }
}
