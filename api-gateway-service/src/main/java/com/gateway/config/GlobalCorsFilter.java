package com.gateway.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");

        // ✅ Adicionar headers CORS em TODAS as respostas
        response.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");

        log.debug("CORS filter applied for: {} {} from origin: {}",
                request.getMethod(), request.getRequestURI(), origin);

        // ✅ Se for OPTIONS, retornar 200 OK imediatamente
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("Handling OPTIONS request for: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("GlobalCorsFilter initialized");
    }

    @Override
    public void destroy() {
        log.info("GlobalCorsFilter destroyed");
    }
}