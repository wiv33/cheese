package xyz.psawesome.cheese.five.router;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import xyz.psawesome.cheese.five.handler.FiveHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RequiredArgsConstructor
@Configuration
public class FiveRouterFunc {

    private final FiveHandler fiveHandler;

    @Bean
    RouterFunction<?> cheeseRouter() {
        return route(GET("/cheese/v1/connection/{userId}/subnet/{subnetId}/type/{fiveType}")
                        .and(accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM,
                                MediaType.APPLICATION_OCTET_STREAM)),
                fiveHandler::connection2)
                .andRoute(GET("/cheese/v1/five-next/step/{stepId}")
                                .and(accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM,
                                        MediaType.APPLICATION_OCTET_STREAM)),
                        fiveHandler::nextByTemplate);
    }
}
