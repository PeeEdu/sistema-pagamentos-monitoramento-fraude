package com.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Enumeration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyService {

    private final WebClient.Builder webClientBuilder;

    public Mono<ResponseEntity<String>> proxyRequest(
            String targetServiceUrl,
            HttpServletRequest request,
            String body) {

        String targetUrl = buildTargetUrl(targetServiceUrl, request);
        log.debug("Proxying to: {}", targetUrl);

        WebClient.RequestBodySpec requestSpec = webClientBuilder.build()
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(targetUrl)
                .headers(headers -> copyHeaders(request, headers));

        if (body != null && !body.isEmpty()) {
            requestSpec.bodyValue(body);
        }

        return requestSpec
                .retrieve()
                .toEntity(String.class)
                .doOnSuccess(response -> log.info("Response status: {}", response.getStatusCode()))
                .doOnError(error -> log.error("Error proxying request: {}", error.getMessage()));
    }

    private String buildTargetUrl(String targetServiceUrl, HttpServletRequest request) {
        String targetUrl = targetServiceUrl + request.getRequestURI();
        if (request.getQueryString() != null) {
            targetUrl += "?" + request.getQueryString();
        }
        return targetUrl;
    }

    private void copyHeaders(HttpServletRequest request, HttpHeaders headers) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            if (!headerName.equalsIgnoreCase("host") &&
                    !headerName.equalsIgnoreCase("content-length")) {
                headers.add(headerName, request.getHeader(headerName));
            }
        }
    }
}