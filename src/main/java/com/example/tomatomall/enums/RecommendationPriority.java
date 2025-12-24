package com.example.tomatomall.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 推荐优先级枚举：用于标识某条推荐是“同校/同城/其他”
 */
public enum RecommendationPriority {
    SAME_SCHOOL("same_school"),
    SAME_CITY("same_city"),
    OTHER("other");

    private final String value;

    RecommendationPriority(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}


