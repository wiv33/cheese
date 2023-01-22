package xyz.psawesome.cheese.five.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.dto.FiveStepDto;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.repository.FiveResultRepository;
import xyz.psawesome.cheese.five.repository.FiveStepRepository;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Service
@RequiredArgsConstructor
@Slf4j
public class FiveService {
    private final FiveResultRepository resultRepository;
    private final FiveStepRepository stepRepository;

    private final ReactiveMongoTemplate mongoTemplate;

    @NotNull
    public Mono<FiveStepDto.NextResponse> connectionResponseByTemplate(String connectionId, String subnetId, String fiveType) {
        return mongoTemplate.findAll(FiveResultDocument.class).next().flatMap(resultDoc ->
                        mongoTemplate.findOne(Query.query(
                                                where("userId").is(connectionId)
                                                        .and("subnetId").is(subnetId)
                                                        .and("algorithm").is(resultDoc.getAlgorithm())
                                                        .and("fiveType").is(fiveType)),
                                        FiveStepDocument.class
                                )
                                .switchIfEmpty(mongoTemplate.save(FiveStepDocument.forInitSave(connectionId, subnetId, resultDoc.getAlgorithm(), fiveType)))
                                .map(FiveStepDto.NextResponse::new)
                )
                .doOnNext(s -> log.info("final result: {}", s));
    }

    @NotNull
    public Mono<FiveStepDto.NextResponse> nextResponseByTemplate(String stepId) {
        return mongoTemplate.findById(stepId, FiveStepDocument.class)
                .flatMap(existNext ->
                        mongoTemplate.findAll(FiveResultDocument.class).sort(((o1, o2) -> o2.createdDate().compareTo(o1.createdDate())))
                                .next()
                                .map(existNext::next))
                .flatMap(mongoTemplate::save)
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
                        stepRepository.findOne(Example.of(FiveStepDocument.forLastFind(connectionId, subnetId, resultDoc.getAlgorithm(), fiveType)))
                                .switchIfEmpty(stepRepository.save(FiveStepDocument.forInitSave(connectionId, subnetId, resultDoc.getAlgorithm(), fiveType)))
                                .map(FiveStepDto.NextResponse::new)

                )
                .doOnNext(s -> log.info("final result: {}", s));
    }

}
