package com.transfer.repository;

import com.transfer.entity.Transfer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends MongoRepository<Transfer, String> {
}
