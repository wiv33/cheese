package xyz.psawesome.cheese.five.handler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import xyz.psawesome.cheese.five.dto.FiveStepDto;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.repository.FiveResultRepository;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Slf4j
@Service
public class FiveHandler {

    private final FiveResultRepository resultRepository;
    private final FiveService service;
    @Getter
    private final Sinks.Many<FiveResultDocument> syncProcessor;


    public FiveHandler(FiveService service, FiveResultRepository resultRepository) {
        this.resultRepository = resultRepository;
        this.service = service;
        this.syncProcessor = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Mono<ServerResponse> connection(ServerRequest request) {
        var connectionId = request.pathVariable("userId");
        var subnetId = request.pathVariable("subnetId");
        var fiveType = request.pathVariable("fiveType");

        return ok().body(
                service.connectionByRepo(connectionId, subnetId, fiveType)
                , FiveStepDto.NextResponse.class
        ).doOnError(throwable -> {
            log.info("throw info : {}", throwable.getMessage());
            throw new RuntimeException(throwable);
        });
    }

    public Mono<ServerResponse> connection2(ServerRequest request) {
        var connectionId = request.pathVariable("userId");
        var subnetId = request.pathVariable("subnetId");
        var fiveType = request.pathVariable("fiveType");

        return ok().body(service.connectionResponseByTemplate(connectionId, subnetId, fiveType)
                , FiveStepDto.NextResponse.class
        ).doOnError(throwable -> {
            log.info("throw info : {}", throwable.getMessage());
            throw new RuntimeException(throwable);
        });
    }


    public Mono<ServerResponse> nextByRepo(ServerRequest request) {
        var stepId = request.pathVariable("stepId");
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .allow(HttpMethod.GET)
                .body(service.nextResponseByTemplate(stepId),
                        FiveStepDto.NextResponse.class);
    }


    public Mono<ServerResponse> nextByTemplate(ServerRequest request) {
        var stepId = request.pathVariable("stepId");
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .allow(HttpMethod.GET)
                .body(service.nextResponseByTemplate(stepId),
                        FiveStepDto.NextResponse.class);
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
