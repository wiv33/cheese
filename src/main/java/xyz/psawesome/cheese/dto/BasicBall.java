package xyz.psawesome.cheese.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BasicBall(String result, String sum, String section, String size,
                        @JsonProperty("odd_even") String oddEven,
                        @JsonProperty("under_over") String underOver) implements PBResult {
    public BasicBall {}

    @Override
    public String getOddEven() {
        return this.oddEven;
    }
}
