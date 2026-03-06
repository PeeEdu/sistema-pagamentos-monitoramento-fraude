package com.bank_account.repository;

import com.bank_account.entity.BankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends MongoRepository<BankAccount, String> {
    BankAccount findByUserId(String userId);

    boolean existsByUserId(String userId);
}
