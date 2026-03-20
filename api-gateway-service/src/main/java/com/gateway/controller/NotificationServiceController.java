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
@Tag(name = "Notification Service", description = "Proxy para o serviço de notificações")
public class NotificationServiceController {

    private final ProxyService proxyService;

    @Value("${services.notification.url}")
    private String notificationServiceUrl;

    @RequestMapping(
            value = "/api/notifications/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    @Operation(summary = "Proxy Notification Service", description = "Redireciona requisições para o Notification Service")
    public Mono<ResponseEntity<String>> proxyNotificationService(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        log.info("Proxying to Notification Service: {} {}", request.getMethod(), request.getRequestURI());
        return proxyService.proxyRequest(notificationServiceUrl, request, body);
    }
}