package com.transferencia_service.repository;

import com.transferencia_service.entity.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    List<NotificationLog> findByUserId(String userId);
    List<NotificationLog> findByStatus(String userId);
}
