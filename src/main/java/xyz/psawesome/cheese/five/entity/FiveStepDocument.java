package xyz.psawesome.cheese.five.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import xyz.psawesome.cheese.entity.CheeseBaseDocument;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static xyz.psawesome.cheese.five.entity.DomainBatInfoDocument.*;

@ToString
@NoArgsConstructor
@Getter
public class FiveStepDocument extends CheeseBaseDocument {
    private String fiveStepId = UUID.randomUUID().toString();
    private String userId;
    private String subnetId;
    @Setter
    private String algorithm;
    private BigDecimal amount;
    private FiveType fiveType;
    private ChoiceValue choice;
    private int step;
    protected Instant createdAt = Instant.now(Clock.systemUTC());
    protected Instant updatedAt = Instant.now(Clock.systemUTC());
    @JsonIgnore
    @Setter
    private boolean isNew = true;
    private List<ChoiceValue> choiceList;
    private BigDecimal previousAmount;
    private ChoiceValue previousChoice;
    private String previousAnswer;
    private String previousAlgorithm;

    /**
     * 초기 생성
     */
    public FiveStepDocument(String userId, String subnetId, String algorithm, BigDecimal amount, FiveType fiveType,
                            ChoiceValue choice, int step, List<ChoiceValue> choiceList) {
        this.userId = userId;
        this.subnetId = subnetId;
        this.algorithm = algorithm;
        this.amount = amount;
        this.fiveType = fiveType;
        this.choice = choice;
        this.step = step;
        this.choiceList = choiceList;
    }

    /**
     * next step
     */
    public FiveStepDocument(String userId, String subnetId, String algorithm, BigDecimal amount, FiveType fiveType,
                            ChoiceValue choice, int step,
                            BigDecimal previousAmount, ChoiceValue previousChoice, String previousAnswer, String previousAlgorithm,
                            List<ChoiceValue> choiceList) {
        this.userId = userId;
        this.subnetId = subnetId;
        this.algorithm = algorithm;
        this.amount = amount;
        this.fiveType = fiveType;
        this.choice = choice;
        this.step = step;
        this.previousAmount = previousAmount;
        this.previousChoice = previousChoice;
        this.previousAnswer = previousAnswer;
        this.previousAlgorithm = previousAlgorithm;
        this.choiceList = choiceList;
    }

    private static List<ChoiceValue> initChoiceList() {
        // tood 변경 필요
        List<ChoiceValue> staticRatioValues = new ArrayList<>(List.of(
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ThreadLocalRandom.current().nextBoolean() ? ChoiceValue.ODD : ChoiceValue.EVEN));
        ;

        var result = new ArrayList<ChoiceValue>();
        while (!staticRatioValues.isEmpty()) {
            var nextInt = ThreadLocalRandom.current().nextInt(0, staticRatioValues.size());
            var choiceValue = staticRatioValues.get(nextInt);
            result.add(choiceValue);
            staticRatioValues.remove(choiceValue);
        }
        assert result.size() == 15;
        return result;
    }

    public static FiveStepDocument forInitSave(String userId, String subnetId, String algorithm, FiveType fiveType) {
        var initChoiceList = initChoiceList();
        int initStep = 0;
        return new FiveStepDocument(userId, subnetId,
                algorithm, DEFAULT_MIN_AMOUNT,
                fiveType,
                initChoiceList.get(initStep), initStep,
                initChoiceList);
    }

    public static FiveStepDocument next(FiveResultDocument lastResult, FiveResultDocument batMatchResult, FiveStepDocument lastStep) {
        var nextAmount = batMatchResult.isAnswer(lastStep.getFiveType(), lastStep.getChoice()) ? DEFAULT_MIN_AMOUNT : nextAmount(lastStep.getAmount(), DEFAULT_RATIO);
        var lastStepCnt = lastStep.getStep() > 13 ? 0 : lastStep.getStep() + 1;
        return new FiveStepDocument(lastStep.getUserId(), lastStep.getSubnetId(),
                lastResult.getAlgorithm(),
                nextAmount, lastStep.getFiveType(),
                lastStep.getChoiceList().get(lastStepCnt), lastStepCnt,
                lastStep.getAmount(),
                lastStep.getChoice(), batMatchResult.getOddEvenAnswer(), batMatchResult.getAlgorithm(),
                lastStep.getChoiceList());
    }

    public boolean isAlreadyBat(FiveResultDocument lastResult) {
        this.algorithm = isBlank(algorithm) ? lastResult.getAlgorithm() : algorithm;
        return lastResult.getAlgorithm().equalsIgnoreCase(algorithm);
    }

    public static BigDecimal nextAmount(BigDecimal amount, BigDecimal ratio) {
        return amount.multiply(ratio)
                .divide(BigDecimal.TEN, RoundingMode.HALF_UP)
                .multiply(BigDecimal.TEN)
                .min(DEFAULT_MAX_AMOUNT);
    }

}
