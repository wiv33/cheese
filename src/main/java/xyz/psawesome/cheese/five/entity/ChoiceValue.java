package xyz.psawesome.cheese.five.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ChoiceValue {

    ODD("홀"),
    EVEN("짝"),
    UNDER("언더"),
    OVER("오버");
    private final String description;


    public boolean match(String oddEven) {
        return this.description.equalsIgnoreCase(oddEven);
    }

}

