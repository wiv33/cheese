package xyz.psawesome.cheese.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.Instant;


public class CheeseBaseDocument {

    @Field("created_at")
    @CreatedDate
    protected Instant createdAt;

    @Field("updated_at")
    @LastModifiedDate
    protected Instant updatedAt;

}
