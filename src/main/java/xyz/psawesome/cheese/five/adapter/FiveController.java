package xyz.psawesome.cheese.five.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.dto.FiveStepDto;
import xyz.psawesome.cheese.five.handler.FiveService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class FiveController {

    private final FiveService service;

    @RequestMapping(value = "/cheese/v1/mvc/connection/{connectionId}/subnet/{subnetId}/type/{type}", method = RequestMethod.GET,
            consumes = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @CrossOrigin(origins = "*", allowedHeaders = "*", originPatterns = "*", methods = RequestMethod.GET)
    public Mono<FiveStepDto.NextResponse> connection(@PathVariable String connectionId,
                                                     @PathVariable String subnetId,
                                                     @PathVariable String type) {
        log.info("request connection : {}, {}, {}", connectionId, subnetId, type);
        return service.connectionByRepo(connectionId, subnetId, type);
    }

    @RequestMapping(value = "/cheese/v1/mvc/five-next/step/{stepId}", method = RequestMethod.GET,
            consumes = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @CrossOrigin(origins = "*", allowedHeaders = "*", originPatterns = "*", methods = RequestMethod.GET)
    public Mono<FiveStepDto.NextResponse> next(@PathVariable String stepId) {
        log.info("request next : {}", stepId);
        return service.nextResponseByRepo(stepId);
    }
}
