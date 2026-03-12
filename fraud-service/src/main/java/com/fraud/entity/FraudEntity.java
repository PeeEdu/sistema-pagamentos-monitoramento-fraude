package com.fraud.entity;

import com.fraud.enums.FraudStatus;
import com.fraud.enums.FraudType;
import com.fraud.enums.RiskLevel;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "frauds")
public class FraudEntity {

    @Id
    private String id;

    @Indexed
    private String transactionId;

    private BigDecimal amount;

    @Indexed
    private List<FraudType> fraudTypes;

    @Indexed
    private FraudStatus status;

    private RiskLevel riskLevel;

    private Double riskScore;

    @CreatedDate
    private LocalDateTime detectedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String detectionMethod;

    private List<String> triggeredRules;

    private Map<String, Object> metadata;

    private String ipAddress;

    private String deviceId;

    private String location;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    private String observation;
}