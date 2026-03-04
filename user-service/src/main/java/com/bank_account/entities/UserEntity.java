package com.bank_account.entities;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document("users")
public class UserEntity {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;

    private String cpf;
    private String phone;


    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
