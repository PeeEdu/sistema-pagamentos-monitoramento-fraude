package com.transfer.controller;

import com.transfer.dto.request.CreatePixTransferRequest;
import com.transfer.dto.response.BaseResponse;
import com.transfer.dto.response.PixResponse;
import com.transfer.entity.Transfer;
import com.transfer.service.StatementService;  // ← NOVO
import com.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
@Slf4j
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final StatementService statementService;

    @PostMapping("/pix")
    public ResponseEntity<?> createTransfer(@RequestBody @Valid CreatePixTransferRequest createPixTransferRequest){
        log.info("iniciando operação de criar transferencia");

        var pix = transferService.createPixTransferRequest(createPixTransferRequest);

        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/extract/account/{accountNumber}")
    public ResponseEntity<BaseResponse<Page<Transfer>>> getExtractByAccountNumber(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Transfer> statement = statementService.getAccountStatementByAccountNumber(
                accountNumber, page, size);

        return ResponseEntity.ok(BaseResponse.<Page<Transfer>>builder()
                .data(statement)
                .message("Extrato encontrado com sucesso")
                .build());
    }

    @GetMapping("/extract/user/{userId}")
    public ResponseEntity<BaseResponse<Page<Transfer>>> getSmartExtractByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Transfer> statement = statementService.getAccountStatementByUserId(userId, page, size);

        return ResponseEntity.ok(BaseResponse.<Page<Transfer>>builder()
                .data(statement)
                .message("Extrato inteligente encontrado com sucesso")
                .build());
    }

    @GetMapping("/extract/user/{userId}/all")
    public ResponseEntity<BaseResponse<Page<Transfer>>> getAllExtractByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Transfer> statement = statementService.getAllTransfersByUserId(userId, page, size);

        return ResponseEntity.ok(BaseResponse.<Page<Transfer>>builder()
                .data(statement)
                .message("Extrato completo encontrado com sucesso")
                .build());
    }
}