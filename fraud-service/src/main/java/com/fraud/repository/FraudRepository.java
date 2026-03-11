package com.fraud.repository;

import com.fraud.entity.FraudEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudRepository extends MongoRepository<FraudEntity, String> {

    List<FraudEntity> findByUserId(String userId);

    List<FraudEntity> findByTransactionId(String transactionId);

    long countByUserId(String userId);
}