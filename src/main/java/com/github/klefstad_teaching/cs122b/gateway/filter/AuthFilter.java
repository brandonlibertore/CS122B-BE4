package com.github.klefstad_teaching.cs122b.gateway.filter;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.core.result.ResultMap;
import com.github.klefstad_teaching.cs122b.core.security.JWTAuthenticationFilter;
import com.github.klefstad_teaching.cs122b.gateway.AuthResponseModel;
import com.github.klefstad_teaching.cs122b.gateway.Posts;
import com.github.klefstad_teaching.cs122b.gateway.config.GatewayServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import wiremock.org.eclipse.jetty.http.HttpHeader;

import java.util.List;
import java.util.Optional;

@Component
public class AuthFilter implements GatewayFilter
{
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

    private final GatewayServiceConfig config;
    private final WebClient            webClient;

    @Autowired
    public AuthFilter(GatewayServiceConfig config)
    {
        this.config = config;
        this.webClient = WebClient.builder().build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        Optional<String> auth = getAccessTokenFromHeader(exchange);
        if (auth.isEmpty()){
            return setToFail(exchange);
        }
        return authenticate(auth.get()).flatMap(result -> result.code() == 1040 ? chain.filter(exchange) : setToFail(exchange));
    }


    private Mono<Void> setToFail(ServerWebExchange exchange)
    {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return Mono.empty();
    }

    /**
     * Takes in a accessToken token and creates Mono chain that calls the idm and maps the value to
     * a Result
     *
     * @param accessToken a encodedJWT
     * @return a Mono that returns a Result
     */
    private Mono<Result> authenticate(String accessToken)
    {
        Posts postsBody = new Posts()
                .setAccessToken(accessToken);
        return webClient.post().uri(config.getIdmAuthenticate())
                .bodyValue(postsBody)
                .retrieve()
                .bodyToMono(AuthResponseModel.class)
                .map(response -> ResultMap.fromCode(response.getResult().getCode()));
    }

    private Optional<String> getAccessTokenFromHeader(ServerWebExchange exchange)
    {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();

        List<String> values = httpHeaders.get(HttpHeaders.AUTHORIZATION);

        if (values == null || values.size() != 1){
            return Optional.empty();
        }

        String authHeader = values.get(0);

        if (authHeader.startsWith(JWTAuthenticationFilter.BEARER_PREFIX)){
            return Optional.of(authHeader.substring(JWTAuthenticationFilter.BEARER_PREFIX.length()));
        }
        else{
            return Optional.empty();
        }
    }
}
