package xyz.psawesome.cheese.five.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Answer {
    private String result;
    private String sum;
    private String section;
    private String size;
    private String oddEven;
    private String underOver;

    public static Answer fromPower(String result, String section, String oddEven, String underOver) {
        return new Answer(result, "", section, "", oddEven, underOver);
    }

    public static Answer fromBasic(String result, String sum, String size, String section, String oddEven, String underOver) {
        return new Answer(result, sum, section, size, oddEven, underOver);
    }
}
