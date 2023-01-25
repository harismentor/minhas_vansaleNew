package com.advanced.minhas.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ReceiptHistory implements Serializable {
    private String receiptKey;

    private ArrayList<Receipt> receipts;

    public String getReceiptKey() {
        return receiptKey;
    }

    public void setReceiptKey(String receiptKey) {
        this.receiptKey = receiptKey;
    }

    public ArrayList<Receipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(ArrayList<Receipt> receipts) {
        this.receipts = receipts;
    }
}
