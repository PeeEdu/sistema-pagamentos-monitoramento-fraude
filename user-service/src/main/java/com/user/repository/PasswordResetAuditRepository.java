package com.user.repository;

import com.user.entities.PasswordResetAuditEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetAuditRepository extends MongoRepository<PasswordResetAuditEntity, String> {
    Optional<PasswordResetAuditEntity> findByToken(String token);
}