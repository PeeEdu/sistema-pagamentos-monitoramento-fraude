package com.bank_account.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bank_accounts")
public class BankAccount {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed(unique = true)
    private String accountNumber;

    private BigDecimal balance;

    private String accountType;

    private String status;

    private String currency;

    @CreatedDate
    @Builder.Default
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    private Instant updatedAt;
}
