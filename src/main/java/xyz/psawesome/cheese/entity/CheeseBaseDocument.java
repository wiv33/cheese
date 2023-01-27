package xyz.psawesome.cheese.entity;

import java.time.Instant;


public class CheeseBaseDocument {
    protected Instant createdAt = Instant.now();
    protected Instant updatedAt = Instant.now();

}
