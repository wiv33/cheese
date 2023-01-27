package xyz.psawesome.cheese.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import xyz.psawesome.cheese.five.entity.Answer;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

@ToString
@NoArgsConstructor
@Getter
public class PBMessage {

    String date;
    String algo;
    @JsonProperty("powerBall")
    PowerBall powerBall;
    @JsonProperty("basicBall")
    BasicBall basicBall;

    public PBMessage(PowerBallResult result) {
        this(result.getEventDate(), result.getAlgorithm(),
                new PowerBall(result.getPowerBallResult(), result.getPowerBallSection(), result.getPowerBallOddEven(), result.getPowerBallUnderOver()),
                new BasicBall(result.getBasicBallResult(), result.getBasicBallSum(), result.getBasicBallSection(), result.getBasicBallSize(), result.getBasicBallOddEven(),
                        result.getBasicBallUnderOver()));
    }

    public PBMessage(String eventDate, String algorithm, PowerBall powerBall, BasicBall basicBall) {
        this.date = eventDate;
        this.algo = algorithm;
        this.powerBall = powerBall;
        this.basicBall = basicBall;
    }

    public Flux<Tuple2<FiveResultDocument, FiveResultDocument>> toFiveResultDocument() {
        return Flux.just(
                Tuples.of(
                        new FiveResultDocument(algo, FiveType.POWER, Answer.fromPower(powerBall.result(), powerBall.section(), powerBall.getOddEven(), powerBall.underOver())),
                        new FiveResultDocument(algo, FiveType.BASIC, Answer.fromBasic(basicBall.result(), basicBall.sum(), basicBall.size(), basicBall.section(), basicBall.oddEven(), basicBall.underOver())))
        );
    }
}
