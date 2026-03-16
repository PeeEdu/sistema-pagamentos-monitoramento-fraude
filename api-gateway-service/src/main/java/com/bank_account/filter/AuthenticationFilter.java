package com.bank_account.filter;

import com.bank_account.util.JwtUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            log.debug("Authenticating request to: {}", request.getURI());

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Missing Authorization header");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token não fornecido");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid Authorization header format");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Formato do token inválido");
            }

            String token = authHeader.substring(7);

            try {
                jwtUtil.validateToken(token);

                String userId = jwtUtil.extractUserId(token);
                String username = jwtUtil.extractEmail(token);

                log.info("Request authenticated for user: {} (ID: {})", username, userId);

                String email = jwtUtil.extractEmail(token);
                String name = jwtUtil.extractName(token);

                ServerHttpRequest modifiedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Email", email)
                        .header("X-User-Name", name)
                        .header("X-Auth-Token", token)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("Token validation failed: {}", e.getMessage());
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Token inválido ou expirado: " + e.getMessage()
                );
            }
        };
    }

    @Setter
    @Getter
    public static class Config {
        private boolean enabled = true;
    }
}