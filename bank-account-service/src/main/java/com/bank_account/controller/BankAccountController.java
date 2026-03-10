package com.bank_account.controller;


import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.BaseResponse;
import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank-account")
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @GetMapping("{userId}")
    public ResponseEntity<BaseResponse<BankAccountResponse>> getBankAccountByUserId(@PathVariable("userId") String userId){
        final var bankAccount = bankAccountService.getBankAccountByUserId(userId);

        return ResponseEntity.ok(BaseResponse.<BankAccountResponse>builder()
                        .data(bankAccount)
                        .message("Conta bancaria encontrada com sucesso")
                .build());

    }

    @PatchMapping("/create/pix/{userId}")
    public ResponseEntity<BaseResponse<CreatePixKeyResponse>> createPixKey(
            @PathVariable("userId") String userId,
            @RequestBody @Valid CreatePixKeyRequest createPixKeyRequest
            ){
        final var pixkey = bankAccountService.createPixKey(userId, createPixKeyRequest);

        return ResponseEntity.ok(BaseResponse.<CreatePixKeyResponse>builder()
                .data(pixkey)
                .message("Pix criado com sucesso")
                .build());
    }

}
