package xyz.psawesome.cheese.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;


public class CheeseBaseDocument {

    @Field("created_at")
    @CreatedDate
    protected Instant createdAt;

    @Field("updated_at")
    @LastModifiedDate
    protected Instant updatedAt;

}
