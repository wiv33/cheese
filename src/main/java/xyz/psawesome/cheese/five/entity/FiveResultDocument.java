package xyz.psawesome.cheese.five.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import xyz.psawesome.cheese.entity.CheeseBaseDocument;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;


@Getter
@ToString
public class FiveResultDocument extends CheeseBaseDocument {
    @Setter
    private String fiveResultId = UUID.randomUUID().toString();
    private final String algorithm;
    private final FiveType fiveType;
    private final Answer answer;
    protected Instant createdAt = Instant.now(Clock.systemUTC());
    protected Instant updatedAt = Instant.now(Clock.systemUTC());
    @Setter @JsonIgnore
    private boolean isNew = false;

    public FiveResultDocument(String algorithm, FiveType fiveType, Answer answer) {
        this.algorithm = algorithm;
        this.fiveType = fiveType;
        this.answer = answer;
    }

    public boolean isAnswer(FiveType type, ChoiceValue choiceValue) {
        return fiveType == type && choiceValue.match(answer.getOddEven());
    }

    public Instant createdDate() {
        return createdAt;
    }


    public String getOddEvenAnswer() {
        return answer.getOddEven();
    }
}
