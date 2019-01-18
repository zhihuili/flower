package com.ly.train.flower.common.service.message;

public class FlowMessage {
    String TransactionId;
    Object message;

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        this.TransactionId = transactionId;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
