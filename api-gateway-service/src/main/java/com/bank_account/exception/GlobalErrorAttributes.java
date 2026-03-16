package com.bank_account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        Throwable error = getError(request);

        errorAttributes.put("timestamp", LocalDateTime.now());
        errorAttributes.put("path", request.path());
        errorAttributes.put("requestId", request.headers().firstHeader("X-Request-Id"));

        if (error instanceof ResponseStatusException rse) {
            errorAttributes.put("status", rse.getStatusCode().value());
            errorAttributes.put("error", rse.getStatusCode());
            errorAttributes.put("message", rse.getReason());
        } else {
            errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorAttributes.put("error", "Internal Server Error");
            errorAttributes.put("message", "Ocorreu um erro inesperado");
        }

        log.error("Error processing request to {}: {}", request.path(), error.getMessage());

        return errorAttributes;
    }
}