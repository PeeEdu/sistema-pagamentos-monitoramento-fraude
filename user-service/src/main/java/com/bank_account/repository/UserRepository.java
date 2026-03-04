package com.bank_account.repository;

import com.bank_account.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
