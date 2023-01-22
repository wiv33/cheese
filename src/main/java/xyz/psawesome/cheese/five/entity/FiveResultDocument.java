package xyz.psawesome.cheese.five.entity;


import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import xyz.psawesome.cheese.entity.CheeseBaseDocument;

import java.time.Instant;


@Getter
@ToString
@Document(collection = "five_results")
public class FiveResultDocument extends CheeseBaseDocument implements Persistable<String> {
    @Id
    private String fiveResultId;

    private String algorithm;

    private FiveType fiveType;

    private Answer answer;

    public FiveResultDocument(String algorithm, FiveType fiveType, Answer answer) {
        this.algorithm = algorithm;
        this.fiveType = fiveType;
        this.answer = answer;
    }

    public boolean isAnswer(FiveType type, ChoiceValue choiceValue) {
        return fiveType == type && choiceValue.match(answer.getOddEven());
    }

    @Override
    public String getId() {
        return fiveResultId;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

    public Instant createdDate() {
        return createdAt;
    }
}
