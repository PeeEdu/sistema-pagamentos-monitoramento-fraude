package com.bank_account.repository;

import com.bank_account.entity.BankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends MongoRepository<BankAccount, String> {
    BankAccount findByUserId(String userId);
    BankAccount findByAccountNumber(String accountNumber);
    boolean existsByUserId(String userId);

    @Query("{ 'pixKey.key': ?0 }")
    Optional<BankAccount> findByPixKeyKey(String pixKey);
}
