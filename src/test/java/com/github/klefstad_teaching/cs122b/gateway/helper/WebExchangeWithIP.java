package com.github.klefstad_teaching.cs122b.gateway.helper;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

public class WebExchangeWithIP extends ServerWebExchangeDecorator
{
    public WebExchangeWithIP(ServerWebExchange delegate)
    {
        super(delegate.mutate()
                      .request(
                          delegate.getRequest()
                                  .mutate()
                                  .remoteAddress(
                                      new InetSocketAddress(
                                          "localhost",
                                          3000)
                                  )
                                  .build()
                      )
                      .build()
        );
    }

    @Nonnull
    public static Mono<Void> filter(ServerWebExchange exchange, @Nonnull WebFilterChain chain)
    {
        return chain.filter(new WebExchangeWithIP(exchange));
    }
}
