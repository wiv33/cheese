package xyz.psawesome.cheese.five.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import xyz.psawesome.cheese.entity.CheeseBaseDocument;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("domain_bat_info")
public class DomainBatInfoDocument extends CheeseBaseDocument implements Persistable<String> {

    @Id
    @Field("domain_bat_id")
    private String domainBatId;

    private String domain;
    @Field("min_amount")
    private int minAmount;
    @Field("max_amount")
    private int maxAmount;

    @Field("ratio")
    public static BigDecimal DEFAULT_RATIO = BigDecimal.valueOf(2.2);
    public static BigDecimal DEFAULT_MIN_AMOUNT = BigDecimal.valueOf(100);
    public static BigDecimal DEFAULT_MAX_AMOUNT = BigDecimal.valueOf(5_000_000);

    @Override
    public String getId() {
        return domainBatId;
    }

    @Override
    public boolean isNew() {
        return super.createdAt == null;
    }
}
