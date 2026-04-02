package com.transfer.repository;

import com.transfer.entity.AccountPixKey;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccountPixKeyRepository extends MongoRepository<AccountPixKey, String> {

    List<AccountPixKey> findByUserId(String userId);

    List<AccountPixKey> findByAccountNumber(String accountNumber);

    void deleteByPixKey(String pixKey);

    boolean existsByPixKey(String pixKey);
}