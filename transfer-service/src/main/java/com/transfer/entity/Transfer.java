package com.transfer.entity;

import com.transfer.enums.TransferStatus;
import com.transfer.enums.TransferType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transfers")
public abstract class Transfer {

    @Id
    private String id;

    @Indexed
    private String fromAccountId;

    private BigDecimal amount;

    @Indexed
    private TransferStatus status;

    private String description;

    private String failureReason;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Indexed
    private String initiatedBy;

    @Indexed
    private String geoLocatization;

    public abstract TransferType getType();
}