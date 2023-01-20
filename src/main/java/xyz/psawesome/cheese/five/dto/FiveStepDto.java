package xyz.psawesome.cheese.five.dto;

import lombok.Getter;
import xyz.psawesome.cheese.five.entity.ChoiceValue;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

import java.math.BigDecimal;

@Getter
public class FiveStepDto {

    public record NextResponse(String connectionId, String subnetId, String algorithm, BigDecimal amount, FiveType fiveType,
                               ChoiceValue choice, int step) {
        public NextResponse(FiveStepDocument prevStep) {
            this(prevStep.getUserId(), prevStep.getSubnetId(), prevStep.getAlgorithm(), prevStep.getAmount(), prevStep.getFiveType(), prevStep.getChoice(), prevStep.getStep());
        }
        public NextResponse(String connectionId, String subnetId) {
            this(connectionId, subnetId, null, null, null, null, 0);
        }
    }
}
