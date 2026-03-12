package com.fraud.repository;

import com.fraud.entity.FraudEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudRepository extends MongoRepository<FraudEntity, String> {
}