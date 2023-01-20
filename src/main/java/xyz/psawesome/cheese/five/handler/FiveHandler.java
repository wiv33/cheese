package xyz.psawesome.cheese.five.handler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import xyz.psawesome.cheese.five.dto.FiveStepDto;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.repository.FiveResultRepository;
import xyz.psawesome.cheese.five.repository.FiveStepRepository;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Slf4j
@Service
public class FiveHandler {

    private final FiveResultRepository resultRepository;
    private final FiveStepRepository stepRepository;
    @Getter
    private final Sinks.Many<FiveResultDocument> syncProcessor;

    public FiveHandler(FiveResultRepository resultRepository, FiveStepRepository stepRepository) {
        this.resultRepository = resultRepository;
        this.stepRepository = stepRepository;
        this.syncProcessor = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Mono<ServerResponse> connection(ServerRequest request) {
        var connectionId = request.pathVariable("userId");
        var subnetId = request.pathVariable("subnetId");
        var fiveType = request.pathVariable("fiveType");
        // 1. user 마지막 배팅 정보
        // 2. 배팅 정보와 일치하는 algorithm 가져오기.
        // 3. 정답이라면 next amount, 아니라면 MIN price

        return ok().body(
                stepRepository.findOne(Example.of(FiveStepDocument.forFind(connectionId, subnetId, fiveType)))
                        .switchIfEmpty(stepRepository.save(FiveStepDocument.forInitSave(connectionId, subnetId, fiveType)))
                        .map(FiveStepDto.NextResponse::new)
                , FiveStepDto.NextResponse.class
        ).doOnError(throwable -> {
            log.info("throw info : {}", throwable.getMessage());
            throw new RuntimeException(throwable);
        });
    }

    public Mono<ServerResponse> next(ServerRequest request) {
        var connectionId = request.pathVariable("userId");
        var subnetId = request.pathVariable("subnetId");
        var fiveType = request.pathVariable("fiveType");

        var result = stepRepository.findOne(Example.of(FiveStepDocument.forFind(connectionId, subnetId, fiveType)))
                .switchIfEmpty(stepRepository.save(FiveStepDocument.forInitSave(connectionId, subnetId, fiveType)))
                .flatMap(s ->
                        isBlank(s.getAlgorithm()) ?
                                resultRepository.findAll(Sort.by("createdAt").descending()).next().map(s::next)
                                : resultRepository.findByAlgorithmAndFiveType(s.getAlgorithm(), s.getFiveType()).map(s::next)
                ).map(FiveStepDto.NextResponse::new);

        return ok().body(result, FiveStepDto.NextResponse.class);
    }


    @Bean
    CommandLineRunner syncFiveResult() {
        return args -> {
            syncProcessor.asFlux()
                    .log("sync process run ->>>>>>>>>>>>>>>>>>>")
                    .flatMap(s -> resultRepository.findByAlgorithmAndFiveType(s.getAlgorithm(), s.getFiveType())
                            .switchIfEmpty(resultRepository.save(s)))
                    .subscribe();
        };
    }
}
