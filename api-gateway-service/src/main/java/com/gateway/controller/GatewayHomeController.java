package com.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Gateway", description = "API Gateway - Informações e Health Check")
public class GatewayHomeController {

    @GetMapping("/")
    @Operation(summary = "Informações do Gateway", description = "Retorna informações básicas do API Gateway")
    public ResponseEntity<Map<String, String>> home() {
        return ResponseEntity.ok(Map.of(
                "message", "API Gateway está funcionando!",
                "swagger", "http://localhost:8080/swagger-ui.html",
                "actuator", "http://localhost:8080/actuator",
                "version", "1.0.0"
        ));
    }

    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Verifica se o gateway está funcionando")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "API Gateway"
        ));
    }
}