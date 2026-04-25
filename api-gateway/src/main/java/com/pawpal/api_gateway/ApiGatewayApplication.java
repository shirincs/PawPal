package com.pawpal.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    
    RouterFunction<ServerResponse> gatewayRoutes() {
        return route()
            .route(RequestPredicates.path("/auth/**"), http())
                .before(uri("http://localhost:8080"))

            .route(RequestPredicates.path("/owners/**"), http())
                .before(uri("http://localhost:8081"))

            .route(RequestPredicates.path("/services/**"), http())
                .before(uri("http://localhost:8082"))

            .route(RequestPredicates.path("/veterinarians/**"), http())
                .before(uri("http://localhost:8082"))

            .route(RequestPredicates.path("/bookings/**"), http())
                .before(uri("http://localhost:8082"))

            .route(RequestPredicates.path("/internal/**"), http())
                .before(uri("http://localhost:8082"))

            .build();
    }
}