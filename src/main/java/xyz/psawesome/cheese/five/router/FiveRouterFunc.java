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
        return route(
                GET("/five/v1/connection/{userId}/subnet/{subnetId}/type/{fiveType}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        fiveHandler::nextStep)
                .andRoute(
                        GET("/five/v1/result")
                                .and(accept(MediaType.APPLICATION_NDJSON)), fiveHandler::retrieveCurrentResultDoc);
    }
}
