package xyz.psawesome.cheese.five.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.dto.FiveStepDto;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Slf4j
@Service
public class FiveHandler {


    private final FiveService service;

    public FiveHandler(FiveService service) {
        this.service = service;
    }

    public Mono<ServerResponse> nextStep(ServerRequest request) {
        var userId = request.pathVariable("userId");
        var subnetId = request.pathVariable("subnetId");
        var fiveType = request.pathVariable("fiveType");

        log.info("next step : {}, {}, {}", userId, subnetId, fiveType);
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .allow(HttpMethod.GET)
                .body(service.nextResponse2(userId, subnetId, FiveType.getType(fiveType)),
                        FiveStepDto.NextResponse.class);
    }

    public Mono<ServerResponse> retrieveCurrentResultDoc(ServerRequest request) {
        return ok()
                .allow(HttpMethod.GET)
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(service.currentResultDocument(), FiveResultDocument.class);
    }

}
