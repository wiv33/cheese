package xyz.psawesome.cheese.five.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;

public interface FiveStepRepository extends ReactiveMongoRepository<FiveStepDocument, String> {
}
