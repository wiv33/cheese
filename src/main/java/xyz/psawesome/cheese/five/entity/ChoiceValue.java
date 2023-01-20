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
    //    UNDER("언더"),
    //    OVER("오버")
    ;
    private final String description;

    public static ChoiceValue matchDescription(String description) {
        return Arrays.stream(ChoiceValue.values())
                     .filter(s -> s.getDescription().equalsIgnoreCase(description))
                     .findFirst()
                     .orElse(ODD);
    }

    public static ChoiceValue next(List<ChoiceValue> historyChoiceList, List<ChoiceValue> staticChoiceList) {
        if (true) {
            return staticChoiceList.get(historyChoiceList.size());
        }

        var result = ChoiceValue.values()[new SecureRandom().nextInt(0, ChoiceValue.values().length)];
        return historyChoiceList.stream().filter(f -> f == result).count() > 7 ? result.other() : result;
    }

    public ChoiceValue other() {
        return Arrays.stream(ChoiceValue.values())
                     .filter(s -> s != this)
                     .findFirst()
                     .orElse(EVEN);
    }

    public boolean match(String oddEven) {
        return this.description.equalsIgnoreCase(oddEven);
    }
}

