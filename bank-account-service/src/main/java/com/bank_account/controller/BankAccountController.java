package com.bank_account.controller;


import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.BaseResponse;
import com.bank_account.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
