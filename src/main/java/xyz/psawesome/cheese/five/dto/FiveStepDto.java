package xyz.psawesome.cheese.five.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import xyz.psawesome.cheese.five.entity.ChoiceValue;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

import java.math.BigDecimal;

@Getter
public class FiveStepDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record NextResponse(String id, String connectionId, String subnetId, String algorithm, BigDecimal amount,
                               FiveType fiveType,
                               ChoiceValue choice, int step) {
        public NextResponse(FiveStepDocument prevStep) {
            this(prevStep.getFiveStepId(), prevStep.getUserId(), prevStep.getSubnetId(), prevStep.getAlgorithm(), prevStep.getAmount(), prevStep.getFiveType(), prevStep.getChoice(), prevStep.getStep());
        }

    }
}
