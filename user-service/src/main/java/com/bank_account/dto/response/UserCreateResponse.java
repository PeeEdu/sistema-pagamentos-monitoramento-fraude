package com.bank_account.dto.response;

import com.bank_account.entities.vo.Address;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserCreateResponse(
        UUID id,
        String name,
        String email,
        String phone,
        Address address
) {
}
