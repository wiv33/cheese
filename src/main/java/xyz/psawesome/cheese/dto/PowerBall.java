package xyz.psawesome.cheese.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PowerBall(String result,
                        String section,
                        @JsonProperty("odd_even") String oddEven,
                        @JsonProperty("under_over") String underOver) implements PBResult {
    public PowerBall {}

    @Override
    public String getOddEven() {
        return oddEven;
    }
}
