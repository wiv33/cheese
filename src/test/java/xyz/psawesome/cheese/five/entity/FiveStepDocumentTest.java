package xyz.psawesome.cheese.five.entity;

import org.junit.jupiter.api.Test;

class FiveStepDocumentTest {

    @Test
    void testCalc() {

        var power = FiveStepDocument.forInitSave("", "", "", "POWER");

        System.out.println("bigDecimal = " + power.getAmount());

        var next1 = power.next(new FiveResultDocument("", FiveType.POWER, new Answer()));
        System.out.println("next1 = " + next1.getAmount());


    }
}