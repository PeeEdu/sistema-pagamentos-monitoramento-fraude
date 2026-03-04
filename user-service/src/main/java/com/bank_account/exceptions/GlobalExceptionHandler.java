/*
package com.bank_account.exceptions;

import com.bank_account.dto.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========================================
    // 404 - Not Found
    // ========================================
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccountNotFound(
            AccountNotFoundException ex,
            HttpServletRequest request) {

        log.error("Account not found: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        BaseResponse<Object> response = BaseResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ========================================
    // 409 - Conflict (Duplicação)
    // ========================================
    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<BaseResponse<Object>> handleDuplicateAccount(
            DuplicateAccountException ex,
            HttpServletRequest request) {

        log.error("Duplicate account: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        BaseResponse<Object> response = BaseResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ========================================
    // 400 - Bad Request (Saldo Insuficiente)
    // ========================================
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<BaseResponse<Object>> handleInsufficientBalance(
            InsufficientBalanceException ex,
            HttpServletRequest request) {

        log.warn("Insufficient balance: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        BaseResponse<Object> response = BaseResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // 400 - Bad Request (Operação Inválida)
    // ========================================
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<BaseResponse<Object>> handleInvalidOperation(
            InvalidOperationException ex,
            HttpServletRequest request) {

        log.warn("Invalid operation: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        BaseResponse<Object> response = BaseResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // 400 - Validation Error
    // ========================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {} - Path: {}", errors, request.getRequestURI());

        BaseResponse<Object> response = BaseResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // 400 - JSON Malformado
    // ========================================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.error("Malformed JSON: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        BaseResponse<Object> response = BaseResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Corpo da requisição inválido ou malformado")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // 400 - IllegalArgumentException (genérico)
    // ========================================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Illegal argument: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        BaseResponse<Object> response = BaseResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // 500 - Internal Server Error
    // ========================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error: {} - Path: {}", ex.getMessage(), request.getRequestURI(), ex);

        BaseResponse<Object> response = BaseResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Erro interno do servidor. Tente novamente mais tarde.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}*/
