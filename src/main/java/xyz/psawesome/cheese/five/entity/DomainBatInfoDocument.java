package xyz.psawesome.cheese.five.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.psawesome.cheese.entity.CheeseBaseDocument;

import java.math.BigDecimal;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class DomainBatInfoDocument extends CheeseBaseDocument {

    private String domainBatId;

    private String domain;
    private int minAmount;
    private int maxAmount;

    protected Instant createdAt = Instant.now();
    protected Instant updatedAt = Instant.now();
    public static BigDecimal DEFAULT_RATIO = BigDecimal.valueOf(2.2);
    public static BigDecimal DEFAULT_MIN_AMOUNT = BigDecimal.valueOf(100);
    public static BigDecimal DEFAULT_MAX_AMOUNT = BigDecimal.valueOf(5_000_000);

}
