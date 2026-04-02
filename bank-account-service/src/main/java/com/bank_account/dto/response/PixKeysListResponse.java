package com.bank_account.dto.response;

import java.util.List;

public record PixKeysListResponse(
        String userId,
        String accountNumber,
        List<PixKeyResponse> pixKeys,
        int totalKeys
) {
}
