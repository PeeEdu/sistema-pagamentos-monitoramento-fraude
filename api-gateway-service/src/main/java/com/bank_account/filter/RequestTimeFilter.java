package com.bank_account.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestTimeFilter extends AbstractGatewayFilterFactory<RequestTimeFilter.Config> {

    public RequestTimeFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

            long startTime = System.currentTimeMillis();
            String requestId = java.util.UUID.randomUUID().toString();

            // Adiciona header na request
            exchange.getRequest().mutate()
                    .header("X-Request-Id", requestId)
                    .build();

            log.info("Request [{}] started: {} {}",
                    requestId,
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI());

            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;

                        log.info("Request [{}] completed in {} ms - Status: {}",
                                requestId,
                                duration,
                                exchange.getResponse().getStatusCode());

                        // Adiciona headers na response
                        HttpHeaders headers = exchange.getResponse().getHeaders();
                        headers.set("X-Response-Time", duration + "ms");
                        headers.set("X-Request-Id", requestId);
                    })
            );
        };
    }

    @Data
    public static class Config {
        private boolean enabled = true;
    }
}