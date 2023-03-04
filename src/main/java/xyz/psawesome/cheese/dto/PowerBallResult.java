package xyz.psawesome.cheese.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class PowerBallResult {

    private String eventDate;
    @JsonProperty("algo")
    private String algorithm;
    @JsonProperty("power_result")
    String powerBallResult;
    @JsonProperty("power_section")
    String powerBallSection;
    @JsonProperty("power_odd_even")
    String powerBallOddEven;
    @JsonProperty("power_under_over")
    String powerBallUnderOver;
    @JsonProperty("basic_result")
    String basicBallResult;
    @JsonProperty("basic_sum")
    String basicBallSum;
    @JsonProperty("basic_section")
    String basicBallSection;
    @JsonProperty("basic_size")
    String basicBallSize;
    @JsonProperty("basic_odd_even")
    String basicBallOddEven;
    @JsonProperty("basic_under_over")
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
