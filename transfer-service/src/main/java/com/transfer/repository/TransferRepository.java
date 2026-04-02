package com.transfer.repository;

import com.transfer.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface TransferRepository extends MongoRepository<Transfer, String> {
    @Query("{ $or: [ { 'fromAccountNumber': ?0 }, { 'pixKey': { $in: ?1 } } ] }")
    Page<Transfer> findByFromAccountNumberOrPixKeyIn(String accountNumber, List<String> pixKeys, Pageable pageable);

    @Query("{ $or: [ " +
            "  { 'fromAccountNumber': ?0 }, " +
            "  { $and: [ " +
            "    { 'pixKey': { $in: ?1 } }, " +
            "    { 'status': 'COMPLETED' } " +
            "  ] } " +
            "] }")
    Page<Transfer> findSmartStatement(String accountNumber, List<String> pixKeys, Pageable pageable);


}