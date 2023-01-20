package xyz.psawesome.cheese.five.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import xyz.psawesome.cheese.entity.CheeseBaseDocument;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static xyz.psawesome.cheese.five.entity.DomainBatInfoDocument.DEFAULT_MIN_AMOUNT;
import static xyz.psawesome.cheese.five.entity.DomainBatInfoDocument.DEFAULT_RATIO;

@NoArgsConstructor
@Getter
@Document
public class FiveStepDocument extends CheeseBaseDocument implements Persistable<String> {
    @Id
    private String fiveStepId;
    @Field("user_id")
    private String userId;
    @Field("subnet_id")
    private String subnetId;
    private String algorithm;
    private BigDecimal amount;
    @Field(value = "five_type", targetType = FieldType.STRING)
    private FiveType fiveType;
    @Field(value = "five_choice", targetType = FieldType.STRING)
    private ChoiceValue choice;
    private int step;

    public FiveStepDocument(String userId, String subnetId) {
        this.userId = userId;
        this.subnetId = subnetId;
    }

    public FiveStepDocument(String userId, String subnetId, FiveType fiveType) {
        this.userId = userId;
        this.subnetId = subnetId;
        this.fiveType = fiveType;
    }

    /**
     * All Argument constructor
     */
    public FiveStepDocument(String userId, String subnetId, String algorithm, BigDecimal amount, FiveType fiveType,
                            ChoiceValue choice, int step) {
        this.userId = userId;
        this.subnetId = subnetId;
        this.algorithm = algorithm;
        this.amount = amount;
        this.fiveType = fiveType;
        this.choice = choice;
        this.step = step;
    }

    public static FiveStepDocument forFind(String userId, String subnetId, String fiveType) {
        return new FiveStepDocument(userId, subnetId, FiveType.getType(fiveType));
    }

    public static FiveStepDocument forInitSave(String userId, String subnetId, String fiveType) {
        return new FiveStepDocument(userId, subnetId, "", DEFAULT_MIN_AMOUNT, FiveType.getType(fiveType), ChoiceValue.ODD, 0);
    }

    public FiveStepDocument next(FiveResultDocument resultDocument) {
        var nextAmount = resultDocument.isAnswer(fiveType, choice) ? DEFAULT_MIN_AMOUNT : nextAmount(DEFAULT_RATIO);
        this.step += 1;
        return new FiveStepDocument(userId, subnetId, resultDocument.getAlgorithm(), nextAmount, resultDocument.getFiveType(),
                nextChoice(), step);
    }

    private ChoiceValue nextChoice() {
        List<ChoiceValue> staticChoiceList = new ArrayList<>(List.of(
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD, ChoiceValue.EVEN,
                ChoiceValue.ODD));
        return staticChoiceList.get(step);
    }

    public BigDecimal nextAmount(BigDecimal ratio) {
        return amount.multiply(ratio)
                .divide(BigDecimal.TEN, RoundingMode.HALF_UP)
                .multiply(BigDecimal.TEN);
    }

    @Override
    public String getId() {
        return fiveStepId;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
