package com.bank_account.stub;

import com.bank_account.dto.response.ValidationResult;

public class ValidationResultStub {

    private ValidationResultStub() {
    }

    public static ValidationResult buildSuccess() {
        return ValidationResult.success();
    }

    public static ValidationResult buildFailure(String message) {
        return ValidationResult.failure(message);
    }
}