package xyz.psawesome.cheese.five.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public enum FiveType {

    POWER("power", FiveType::initOddEvenValues),
    BASIC("basic", FiveType::initOddEvenValues),
    POWER_UD("power_ud", FiveType::initUnderOverValues),
    BASIC_UD("basic_ud", FiveType::initUnderOverValues);

    private final String code;
    private final Supplier<List<ChoiceValue>> generateChoiceValuesSupplier;

    public static FiveType getType(String fiveType) {
        return FiveType.valueOf(fiveType.toUpperCase());
    }



    public List<ChoiceValue> initChoiceValues() {
        return this.generateChoiceValuesSupplier.get();
    }

    private static List<ChoiceValue> initOddEvenValues() {
        var base = new ArrayList<>(List.of(
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ThreadLocalRandom.current().nextBoolean() ? ChoiceValue.ODD : ChoiceValue.EVEN));
        Collections.shuffle(base);
        return base;
    }

    private static List<ChoiceValue> initUnderOverValues() {
        var base = new ArrayList<>(List.of(
                ChoiceValue.UNDER, ChoiceValue.OVER,
                ChoiceValue.UNDER, ChoiceValue.OVER,
                ChoiceValue.UNDER, ChoiceValue.OVER,
                ChoiceValue.UNDER, ChoiceValue.OVER,
                ChoiceValue.UNDER, ChoiceValue.OVER,
                ChoiceValue.UNDER, ChoiceValue.OVER,
                ChoiceValue.UNDER, ChoiceValue.OVER,
                ThreadLocalRandom.current().nextBoolean() ? ChoiceValue.UNDER : ChoiceValue.OVER));
        Collections.shuffle(base);
        return base;
    }

 }
