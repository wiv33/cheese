package xyz.psawesome.cheese.five.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

public interface CheeseFiveResultOperations {

    Mono<FiveResultDocument> addDocument(FiveResultDocument document);

    Flux<FiveResultDocument> searchTermQueryByAlgorithmAndFiveType(String algorithm, FiveType fiveType);
    Flux<FiveResultDocument> searchMatchPhraseAlgorithm(String algorithm, FiveType fiveType);
    Mono<FiveResultDocument> searchMatchPhraseAlgorithmMono(String algorithm, FiveType fiveType);

    Mono<FiveResultDocument> searchLastResult(FiveType fiveType);
}
