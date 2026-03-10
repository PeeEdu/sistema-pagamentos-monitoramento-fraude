package com.bank_account.entity;

import com.bank_account.PixKeyType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PixKey {
    private PixKeyType type;
    private String key;
}
