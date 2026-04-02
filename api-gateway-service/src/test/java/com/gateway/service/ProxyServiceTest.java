package com.gateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProxyServiceTest {

    @Test
    void proxyRequest_DeveMontarRequisicaoComMetodoUriHeadersEBody_QuandoBodyNaoForVazio() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        ProxyService proxyService = new ProxyService(builder);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setQueryString("page=1");
        request.addHeader("Authorization", "Bearer token");
        request.addHeader("host", "localhost");
        request.addHeader("content-length", "100");
        request.addHeader("access-control-allow-origin", "*");

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("success");

        when(builder.build()).thenReturn(webClient);
        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("http://target-service/api/test?page=1")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).bodyValue("request-body");
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(Mono.just(expectedResponse));

        ResponseEntity<String> response = proxyService
                .proxyRequest("http://target-service", request, "request-body")
                .block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody());

        verify(builder).build();
        verify(webClient).method(HttpMethod.POST);
        verify(requestBodyUriSpec).uri("http://target-service/api/test?page=1");
        verify(requestBodySpec).bodyValue("request-body");
        verify(requestBodySpec).retrieve();
        verify(responseSpec).toEntity(String.class);

        verify(requestBodySpec).headers(argThat((Consumer<HttpHeaders> consumer) -> {
            HttpHeaders headers = new HttpHeaders();
            consumer.accept(headers);

            return "Bearer token".equals(headers.getFirst("Authorization"))
                    && !headers.containsKey("host")
                    && !headers.containsKey("content-length")
                    && !headers.containsKey("access-control-allow-origin");
        }));
    }

    @Test
    void proxyRequest_DeveMontarRequisicaoSemBody_QuandoBodyForNulo() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        ProxyService proxyService = new ProxyService(builder);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("success");

        when(builder.build()).thenReturn(webClient);
        when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("http://target-service/api/test")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(Mono.just(expectedResponse));

        ResponseEntity<String> response = proxyService
                .proxyRequest("http://target-service", request, null)
                .block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody());

        verify(builder).build();
        verify(webClient).method(HttpMethod.GET);
        verify(requestBodyUriSpec).uri("http://target-service/api/test");
        verify(requestBodySpec, never()).bodyValue(anyString());
        verify(requestBodySpec).retrieve();
        verify(responseSpec).toEntity(String.class);
    }

    @Test
    void proxyRequest_DeveMontarRequisicaoSemBody_QuandoBodyForVazio() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        ProxyService proxyService = new ProxyService(builder);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("success");

        when(builder.build()).thenReturn(webClient);
        when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("http://target-service/api/test")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(Mono.just(expectedResponse));

        ResponseEntity<String> response = proxyService
                .proxyRequest("http://target-service", request, "")
                .block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody());

        verify(requestBodySpec, never()).bodyValue(anyString());
        verify(requestBodySpec).retrieve();
    }

    @Test
    void proxyRequest_DevePropagarErro_QuandoWebClientFalhar() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        ProxyService proxyService = new ProxyService(builder);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        when(builder.build()).thenReturn(webClient);
        when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("http://target-service/api/test")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(Mono.error(new RuntimeException("Erro no proxy")));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> proxyService.proxyRequest("http://target-service", request, null).block()
        );

        assertEquals("Erro no proxy", exception.getMessage());
    }
}