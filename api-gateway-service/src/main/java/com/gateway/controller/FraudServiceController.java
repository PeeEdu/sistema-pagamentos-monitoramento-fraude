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
@Tag(name = "Fraud Service", description = "Proxy para o serviço de detecção de fraudes")
public class FraudServiceController {

    private final ProxyService proxyService;

    @Value("${services.fraud.url}")
    private String fraudServiceUrl;

    @RequestMapping(
            value = "/api/fraud/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    @Operation(summary = "Proxy Fraud Service", description = "Redireciona requisições para o Fraud Service")
    public Mono<ResponseEntity<String>> proxyFraudService(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        log.info("Proxying to Fraud Service: {} {}", request.getMethod(), request.getRequestURI());
        return proxyService.proxyRequest(fraudServiceUrl, request, body);
    }
}