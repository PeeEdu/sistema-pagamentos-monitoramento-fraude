package com.gateway.controller;

import com.gateway.service.ProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User Service", description = "Proxy para o serviço de usuários e autenticação")
public class UserServiceController {

    private final ProxyService proxyService;

    @Value("${services.user.url}")
    private String userServiceUrl;

    @RequestMapping(
            value = {"/api/user/**", "/api/auth/**"},
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    @Operation(summary = "Proxy User Service", description = "Redireciona requisições para o User Service")
    public Mono<ResponseEntity<String>> proxyUserService(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        log.info("Proxying to User Service: {} {}", request.getMethod(), request.getRequestURI());
        return proxyService.proxyRequest(userServiceUrl, request, body);
    }
}