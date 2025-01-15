package com.smoothstack.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-route", predicateSpec -> predicateSpec.path("/api/v1/users/**")
                        .uri("lb://USER-SERVICE"))
                .route("user-service-route", predicateSpec -> predicateSpec.path("/api/v1/auth/**")
                        .uri("lb://USER-SERVICE"))
                .route("accounts-service-route", predicateSpec -> predicateSpec.path("/api/v1/accounts/**")
                        .uri("lb://ACCOUNTS-SERVICE"))
                .route("accounts-service-route", predicateSpec -> predicateSpec.path("/api/v1/transactions/**")
                        .uri("lb://ACCOUNTS-SERVICE"))
                .route("cards-service-route", predicateSpec -> predicateSpec.path("/api/v1/cards/**")
                        .uri("lb://CARDS-SERVICE"))
                .route("cards-service-route", predicateSpec -> predicateSpec.path("/api/v1/loans/**")
                        .uri("lb://CARDS-SERVICE"))
                .route("branch-service-route", predicateSpec -> predicateSpec.path("/api/v1/branch/**")
                        .uri("lb://BRANCH-SERVICE"))
                .route("branch-service-route", predicateSpec -> predicateSpec.path("/api/v1/appointment/**")
                        .uri("lb://BRANCH-SERVICE"))
                .route("branch-service-route", predicateSpec -> predicateSpec.path("/api/v1/banker/**")
                        .uri("lb://BRANCH-SERVICE"))
                .route("branch-service-route", predicateSpec -> predicateSpec.path("/api/v1/queue/**")
                        .uri("lb://BRANCH-SERVICE"))
                .route("branch-service-route", predicateSpec -> predicateSpec.path("/api/v1/serviceType/**")
                        .uri("lb://BRANCH-SERVICE"))
                .build();
    }

}