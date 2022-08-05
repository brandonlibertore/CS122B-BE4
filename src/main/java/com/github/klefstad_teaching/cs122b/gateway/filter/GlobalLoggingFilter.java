package com.github.klefstad_teaching.cs122b.gateway.filter;

import com.github.klefstad_teaching.cs122b.gateway.GatewayRequestObject;
import com.github.klefstad_teaching.cs122b.gateway.config.GatewayServiceConfig;
import com.github.klefstad_teaching.cs122b.gateway.repo.GatewayRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered
{
    private static final Logger    LOG          = LoggerFactory.getLogger(GlobalLoggingFilter.class);
    private static final Scheduler DB_SCHEDULER = Schedulers.boundedElastic();

    private final GatewayRepo          gatewayRepo;
    private final GatewayServiceConfig config;
    private final LinkedBlockingQueue<GatewayRequestObject> request = new LinkedBlockingQueue<>();

    @Autowired
    public GlobalLoggingFilter(GatewayRepo gatewayRepo, GatewayServiceConfig config)
    {
        this.gatewayRepo = gatewayRepo;
        this.config = config;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        // Get request object in order to obtain information to construct GatewayRequestObject:
        ServerHttpRequest requestHttp = exchange.getRequest();

        // Take request object and construct GatewayRequestObject to be stored into LinkedBlockingQueue:
        GatewayRequestObject gateway = new GatewayRequestObject();
        gateway.setIp_address(requestHttp.getRemoteAddress().getAddress().getHostAddress());
        gateway.setCall_time(Timestamp.from(Instant.now()));
        gateway.setPath(requestHttp.getPath().value());

        // Add to LinkedBlockingQueue:
        request.add(gateway);

        // Check if the max size has been exceeded, then we need to drain and insert into table:
        if (request.size() >= config.getMaxLogs()){
            drainRequests();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder()
    {
        return -1;
    }

    public void drainRequests()
    {
        List<GatewayRequestObject> drainedRequests = new ArrayList<>();
        request.drainTo(drainedRequests);
        gatewayRepo.insertRequests(drainedRequests)
                .subscribeOn(DB_SCHEDULER)
                .subscribe();
    }

}
