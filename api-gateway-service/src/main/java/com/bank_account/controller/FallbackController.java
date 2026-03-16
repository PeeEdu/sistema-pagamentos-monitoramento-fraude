package com.bank_account.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        log.warn("Auth Service fallback triggered");
        return buildFallbackResponse("Auth Service está temporariamente indisponível");
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        log.warn("User Service fallback triggered");
        return buildFallbackResponse("User Service está temporariamente indisponível");
    }

    @GetMapping("/accounts")
    public ResponseEntity<Map<String, Object>> accountServiceFallback() {
        log.warn("Bank Account Service fallback triggered");
        return buildFallbackResponse("Bank Account Service está temporariamente indisponível");
    }

    @GetMapping("/transfers")
    public ResponseEntity<Map<String, Object>> transferServiceFallback() {
        log.warn("Transfer Service fallback triggered");
        return buildFallbackResponse("Transfer Service está temporariamente indisponível");
    }

    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", message);
        response.put("suggestion", "Por favor, tente novamente em alguns instantes");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}