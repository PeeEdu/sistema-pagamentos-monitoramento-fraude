package com.transfer.stub;

import com.transfer.event.TransferCompletedEvent;

public class TransferCompletedEventStub {

    private TransferCompletedEventStub() {
    }

    public static TransferCompletedEvent buildEvent() {
        return TransferCompletedEvent.builder()
                .transferId("transfer-123")
                .status("COMPLETED")
                .build();
    }
}