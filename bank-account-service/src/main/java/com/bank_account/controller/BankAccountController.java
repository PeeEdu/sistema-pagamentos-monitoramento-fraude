package com.bank_account.controller;

import com.bank_account.dto.request.CreateAccountRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.BaseResponse;
import com.bank_account.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")

public record BankAccountController(
        BankAccountService bankAccountService
) {

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<BankAccountResponse>> createBankAccount(
            @RequestBody CreateAccountRequest account
    ) {
        BankAccountResponse response = bankAccountService.createBankAccount(account);

        BaseResponse<BankAccountResponse> baseResponse = BaseResponse.<BankAccountResponse>builder()
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }
}
