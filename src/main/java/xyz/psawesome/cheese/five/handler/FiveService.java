package xyz.psawesome.cheese.five.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.dto.FiveStepDto;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.entity.FiveType;
import xyz.psawesome.cheese.five.repository.FiveResultRepository;
import xyz.psawesome.cheese.five.repository.FiveStepRepository;


@Service
@RequiredArgsConstructor
@Slf4j
public class FiveService {
    private final FiveResultRepository resultRepository;
    private final FiveStepRepository stepRepository;

    @NotNull
    public Mono<FiveStepDto.NextResponse> connectionResponseByTemplate(String connectionId, String subnetId, String fiveType) {
        return resultRepository.findAll(Sort.by("createdAt").descending())
                .next().flatMap(resultDoc ->
                        stepRepository.findByUserIdAndSubnetIdAndAlgorithmAndFiveType(
                                        connectionId, subnetId, resultDoc.getAlgorithm(), FiveType.getType(fiveType))
                                .switchIfEmpty(stepRepository.save(FiveStepDocument.forInitSave(connectionId, subnetId, resultDoc.getAlgorithm(), fiveType)))
                                .map(FiveStepDto.NextResponse::new)
                )
                .doOnNext(s -> log.info("final result: {}", s));
    }

    @NotNull
    public Mono<FiveStepDto.NextResponse> nextResponseByTemplate(String stepId) {
        return stepRepository.findById(stepId)
                .flatMap(existNext ->
                        resultRepository.findAll(Sort.by("createdAt").descending())
                                .next()
                                .map(existNext::next))
                .flatMap(stepRepository::save)
                .map(FiveStepDto.NextResponse::new)
                .doOnNext(s -> log.info("final result: {}", s))
                ;
    }

    @NotNull
    public Mono<FiveStepDto.NextResponse> nextResponseByRepo(String stepId) {
        return stepRepository.findById(stepId)
                .flatMap(existNext ->
                        resultRepository.findAll(Sort.by("createdAt").descending())
                                .next()
                                .map(existNext::next))
                .flatMap(stepRepository::save)
                .map(FiveStepDto.NextResponse::new)
                .doOnNext(s -> log.info("final result: {}", s))
                ;
    }

    @NotNull
    public Mono<FiveStepDto.NextResponse> connectionByRepo(String connectionId, String subnetId, String fiveType) {
        return resultRepository.findAll().next().flatMap(resultDoc ->
                        stepRepository.findByUserIdAndSubnetIdAndAlgorithmAndFiveType(connectionId, subnetId, resultDoc.getAlgorithm(), FiveType.getType(fiveType))
                                .switchIfEmpty(stepRepository.save(FiveStepDocument.forInitSave(connectionId, subnetId, resultDoc.getAlgorithm(), fiveType)))
                                .map(FiveStepDto.NextResponse::new)
                )
                .doOnNext(s -> log.info("final result: {}", s));
    }

}
