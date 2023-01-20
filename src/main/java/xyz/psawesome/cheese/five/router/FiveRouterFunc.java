package xyz.psawesome.cheese.five.router;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import xyz.psawesome.cheese.five.handler.FiveHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RequiredArgsConstructor
@Configuration
public class FiveRouterFunc {

    private final FiveHandler fiveHandler;

    @Bean
    RouterFunction<?> cheeseRouter() {
        return route(GET("/cheese/v1/connection/{userId}/subnet/{subnetId}/type/{fiveType}"), fiveHandler::connection)
                .andRoute(GET("/cheese/v1/connection/{userId}/subnet/{subnetId}/type/{fiveType}/next"), fiveHandler::next);
    }
}
