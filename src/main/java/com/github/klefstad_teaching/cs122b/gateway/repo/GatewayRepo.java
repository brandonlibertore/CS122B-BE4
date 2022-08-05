package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.GatewayRequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GatewayRepo
{

    private NamedParameterJdbcTemplate template;

    private static final String SQL = "INSERT INTO gateway.request (ip_address, call_time, path) VALUES (:ip_address, :call_time, :path)";

    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public int[] insert(List<GatewayRequestObject> requests){
        MapSqlParameterSource[] arrayOfSources =
                requests
                        .stream()
                        .map(
                                request ->
                                        new MapSqlParameterSource()
                                                .addValue("ip_address", request.getIp_address())
                                                .addValue("call_time", request.getCall_time())
                                                .addValue("path", request.getPath())).toArray(MapSqlParameterSource[]::new);

        return this.template.batchUpdate(SQL, arrayOfSources);
    }

    public Mono<int[]> insertRequests(List<GatewayRequestObject> requests)
    {
        return Mono.fromCallable(() -> insert(requests));
    }
}
