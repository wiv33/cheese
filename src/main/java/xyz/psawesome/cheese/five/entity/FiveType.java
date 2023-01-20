package xyz.psawesome.cheese.five.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FiveType {

    POWER("power"),
    BASIC("basic");

    private final String code;


    public static FiveType getType(String fiveType) {
        return FiveType.valueOf(fiveType.toUpperCase());
    }
}
