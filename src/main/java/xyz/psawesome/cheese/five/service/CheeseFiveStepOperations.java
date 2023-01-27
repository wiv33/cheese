package xyz.psawesome.cheese.five.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

public interface CheeseFiveStepOperations {
    Mono<FiveStepDocument> addDocument(FiveStepDocument fiveStepDocument);
    Flux<FiveStepDocument> searchLastStepByAlgorithm(String userId, String subnetId, String algorithm, FiveType fiveType);
    Mono<FiveStepDocument> searchLastStepMonoByAlgorithm(String userId, String subnetId, String algorithm, FiveType fiveType);
    Flux<FiveStepDocument> searchLastStep(String userId, String subnetId, FiveType fiveType);
    Mono<FiveStepDocument> searchLastMono(String userId, String subnetId, FiveType fiveType);
    Flux<FiveStepDocument> searchById(String fiveStepId);
    Mono<FiveStepDocument> searchByIdMono(String fiveStepId);
}
