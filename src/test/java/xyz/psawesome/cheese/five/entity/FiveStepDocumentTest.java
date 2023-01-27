package xyz.psawesome.cheese.five.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class FiveStepDocumentTest {

    @Test
    void testMatch() {
        var match = new FiveResultDocument("nnss", FiveType.POWER, new Answer("", "", "", "", "홀", ""));
        var last = new FiveResultDocument("nnss", FiveType.POWER, new Answer("", "", "", "", "짝", ""));

        var answer = match.isAnswer(FiveType.POWER, ChoiceValue.ODD);
        System.out.println("answer = " + answer);

        var fiveStepDocument = new FiveStepDocument("11", "11", "12", BigDecimal.valueOf(100), FiveType.POWER, ChoiceValue.EVEN, 0,
                List.of(ChoiceValue.EVEN, ChoiceValue.ODD, ChoiceValue.EVEN, ChoiceValue.ODD, ChoiceValue.EVEN, ChoiceValue.ODD, ChoiceValue.EVEN, ChoiceValue.ODD, ChoiceValue.EVEN, ChoiceValue.ODD, ChoiceValue.EVEN, ChoiceValue.ODD));
        System.out.println("fiveStepDocument = " + fiveStepDocument);

        var next = FiveStepDocument.next(last, match, fiveStepDocument);
        System.out.println("next = " + next);

        var next1 = FiveStepDocument.next(last, match, next);
        System.out.println("next1 = " + next1);

        var next2 = FiveStepDocument.next(last, match, next1);
        System.out.println("next2 = " + next2);
    }
}