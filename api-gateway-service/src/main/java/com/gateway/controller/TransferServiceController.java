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
@Tag(name = "Transfer Service", description = "Proxy para o serviço de transferências")
public class TransferServiceController {

    private final ProxyService proxyService;

    @Value("${services.transfer.url}")
    private String transferServiceUrl;

    @RequestMapping(
            value = "/api/transfer/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    @Operation(summary = "Proxy Transfer Service", description = "Redireciona requisições para o Transfer Service")
    public Mono<ResponseEntity<String>> proxyTransferService(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        log.info("Proxying to Transfer Service: {} {}", request.getMethod(), request.getRequestURI());
        return proxyService.proxyRequest(transferServiceUrl, request, body);
    }
}