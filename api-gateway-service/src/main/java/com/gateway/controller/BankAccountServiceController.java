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
@Tag(name = "Bank Account Service", description = "Proxy para o serviço de contas bancárias")
public class BankAccountServiceController {

    private final ProxyService proxyService;

    @Value("${services.bank-account.url}")
    private String bankAccountServiceUrl;

    @RequestMapping(
            value = "/api/bank-account/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    @Operation(summary = "Proxy Bank Account Service", description = "Redireciona requisições para o Bank Account Service")
    public Mono<ResponseEntity<String>> proxyBankAccountService(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        log.info("Proxying to Bank Account Service: {} {}", request.getMethod(), request.getRequestURI());
        return proxyService.proxyRequest(bankAccountServiceUrl, request, body);
    }
}