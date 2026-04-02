package com.bank_account.stub;

import com.bank_account.dto.request.DepositRequest;

import java.math.BigDecimal;

public class DepositRequestStub {

    private DepositRequestStub() {
    }

    public static DepositRequest buildRequest() {
        return new DepositRequest(new BigDecimal("100.00"));
    }
}