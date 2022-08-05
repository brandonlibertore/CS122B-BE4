package com.github.klefstad_teaching.cs122b.gateway.routes;

import com.github.klefstad_teaching.cs122b.gateway.config.GatewayServiceConfig;
import com.github.klefstad_teaching.cs122b.gateway.filter.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GatewayRouteLocator
{
    private final GatewayServiceConfig config;
    private final AuthFilter           authFilter;

    @Autowired
    public GatewayRouteLocator(GatewayServiceConfig config,
                               AuthFilter authFilter)
    {
        this.config = config;
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder)
    {
        return builder.routes()
                      .route("idm", r -> r.path("/idm/**").filters(f -> f.stripPrefix(1)).uri(config.getIdm()))
                      .route("movies", r -> r.path("/movies/**").filters(f -> f.stripPrefix(1).filter(authFilter)).uri(config.getMovies()))
                      .route("billing", r -> r.path("/billing/**").filters(f -> f.stripPrefix(1).filter(authFilter)).uri(config.getBilling()))
                      .build();
    }
}
