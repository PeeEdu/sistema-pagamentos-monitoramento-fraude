package com.bank_account.exceptions;

import com.bank_account.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Erros de validação de request (cliente)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.builder()
                        .message(errorMessage)
                        .build());
    }

    // Erros de negócio ou cliente
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.builder()
                        .message(e.getMessage())
                        .build());
    }

    // Qualquer outro erro inesperado (servidor)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleServerError(Exception e) {
        log.error("Erro inesperado: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.builder()
                        .message("Ocorreu um erro no servidor. Tente novamente mais tarde.")
                        .build());
    }
}
