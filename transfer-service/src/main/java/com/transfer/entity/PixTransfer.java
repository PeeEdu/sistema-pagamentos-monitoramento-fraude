package com.transfer.entity;

import com.transfer.enums.PixKeyType;
import com.transfer.enums.TransferType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "transfers")
public class PixTransfer extends Transfer{
    @Indexed
    private String pixKey;

    private PixKeyType pixKeyType;

    @Override
    public TransferType getType() {
        return TransferType.PIX;
    }
}
