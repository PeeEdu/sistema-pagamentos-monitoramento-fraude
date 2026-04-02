package com.bank_account.controller;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.request.DepositRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.BaseResponse;
import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.dto.response.PixKeysListResponse;
import com.bank_account.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank-account")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping("{userId}")
    public ResponseEntity<BaseResponse<BankAccountResponse>> getBankAccountByUserId(@PathVariable("userId") String userId) {
        final var bankAccount = bankAccountService.getBankAccountByUserId(userId);

        return ResponseEntity.ok(BaseResponse.<BankAccountResponse>builder()
                .data(bankAccount)
                .message("Conta bancaria encontrada com sucesso")
                .build());
    }

    @PatchMapping("deposit/{accountNumber}")
    public ResponseEntity<BaseResponse<BankAccountResponse>> depositValue(
            @PathVariable("accountNumber") String accountNumber,
            @RequestBody DepositRequest depositRequest) {

        final var deposit = bankAccountService.deposit(accountNumber, depositRequest);

        return ResponseEntity.ok(BaseResponse.<BankAccountResponse>builder()
                .data(deposit)
                .message("Deposito feito com sucesso")
                .build());
    }

    @PatchMapping("/pix/{userId}")
    public ResponseEntity<BaseResponse<CreatePixKeyResponse>> createPixKey(
            @PathVariable("userId") String userId,
            @RequestBody @Valid CreatePixKeyRequest createPixKeyRequest) {

        final var pixKey = bankAccountService.createPixKey(userId, createPixKeyRequest);

        return ResponseEntity.ok(BaseResponse.<CreatePixKeyResponse>builder()
                .data(pixKey)
                .message("Chave PIX criada com sucesso")
                .build());
    }

    @GetMapping("/pix/{accountNumber}")
    public ResponseEntity<BaseResponse<PixKeysListResponse>> getPixKeysByAccountNumber(
            @PathVariable("accountNumber") String accountNumber) {

        final var pixKeys = bankAccountService.getPixKeysByAccountNumber(accountNumber);

        return ResponseEntity.ok(BaseResponse.<PixKeysListResponse>builder()
                .data(pixKeys)
                .message("Chaves PIX encontradas com sucesso")
                .build());
    }

    @DeleteMapping("/pix/{accountNumber}/{pixKey}")
    public ResponseEntity<BaseResponse<String>> deletePixKey(
            @PathVariable("accountNumber") String accountNumber,
            @PathVariable("pixKey") String pixKey) {

        bankAccountService.deletePixKey(accountNumber, pixKey);

        return ResponseEntity.ok(BaseResponse.<String>builder()
                .data("Chave PIX removida")
                .message("Chave PIX excluída com sucesso")
                .build());
    }
}