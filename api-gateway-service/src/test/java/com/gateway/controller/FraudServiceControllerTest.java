package com.gateway.controller;

import com.gateway.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class FraudServiceControllerTest {

    @Test
    void proxyFraudService_DeveDelegarRequisicaoParaProxyService_QuandoRequestForRecebido() {
        ProxyService proxyService = mock(ProxyService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("success");

        FraudServiceController controller = new FraudServiceController(proxyService);
        ReflectionTestUtils.setField(controller, "fraudServiceUrl", "http://fraud-service");

        when(proxyService.proxyRequest("http://fraud-service", request, "body"))
                .thenReturn(Mono.just(expectedResponse));

        ResponseEntity<String> response = controller.proxyFraudService(request, "body").block();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody());

        verify(proxyService).proxyRequest("http://fraud-service", request, "body");
    }
}