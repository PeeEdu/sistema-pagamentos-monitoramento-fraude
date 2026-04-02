package com.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GatewayHomeControllerTest {

    private final GatewayHomeController controller = new GatewayHomeController();

    @Test
    void home_DeveRetornarStatus200ComInformacoesDoGateway() {
        ResponseEntity<Map<String, String>> response = controller.home();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("API Gateway está funcionando!", response.getBody().get("message"));
        assertEquals("http://localhost:8080/swagger-ui.html", response.getBody().get("swagger"));
        assertEquals("http://localhost:8080/actuator", response.getBody().get("actuator"));
        assertEquals("1.0.0", response.getBody().get("version"));
    }

    @Test
    void health_DeveRetornarStatus200ComStatusUp() {
        ResponseEntity<Map<String, String>> response = controller.health();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("API Gateway", response.getBody().get("service"));
    }
}