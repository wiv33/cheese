package xyz.psawesome.cheese.five.repository;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

public interface FiveStepRepository extends ReactiveElasticsearchRepository<FiveStepDocument, String> {

    Mono<FiveStepDocument> findByFiveStepId(String fiveStepId);

    Mono<FiveStepDocument> findByUserIdAndSubnetIdAndAlgorithmAndFiveType(String userId, String subnet, String algorithm, FiveType fiveType);
}
