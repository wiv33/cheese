package xyz.psawesome.cheese.five.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

public interface FiveResultRepository extends ReactiveMongoRepository<FiveResultDocument, String> {

    Mono<FiveResultDocument> findByAlgorithm(String algorithm);
    Mono<FiveResultDocument> findByAlgorithmAndFiveType(String algorithm, FiveType fiveType);
}
