package xyz.psawesome.cheese.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class PowerBallResult {

    private LocalDate eventDate;
    private String algorithm;
    String powerBallResult;
    String powerBallSection;
    String powerBallOddEven;
    String powerBallUnderOver;
    String basicBallResult;
    String basicBallSum;
    String basicBallSection;
    String basicBallSize;
    String basicBallOddEven;
    String basicBallUnderOver;
    LocalDateTime createdAt;

    public PowerBallResult(PBMessage requestMessage) {
        this.eventDate = requestMessage.getDate();
        this.algorithm = requestMessage.getAlgo();
        this.powerBallResult = requestMessage.getPowerBall().result();
        this.powerBallSection = requestMessage.getPowerBall().section();
        this.powerBallOddEven = requestMessage.getPowerBall().oddEven();
        this.powerBallUnderOver = requestMessage.getPowerBall().underOver();

        this.basicBallResult = requestMessage.getBasicBall().result();
        this.basicBallSum = requestMessage.getBasicBall().sum();
        this.basicBallSection = requestMessage.getBasicBall().section();
        this.basicBallSize = requestMessage.getBasicBall().size();
        this.basicBallOddEven = requestMessage.getBasicBall().oddEven();
        this.basicBallUnderOver = requestMessage.getBasicBall().underOver();
    }

    public PowerBallResult(String algorithm) {
        this.algorithm = algorithm;
    }
}
