package com.bank_account.event;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBankAccountEvent {
    private String userId;
}
