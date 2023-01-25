package com.advanced.minhas.model;

import java.io.Serializable;

public class PendingReceipt implements Serializable {

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getDueamnt() {
        return dueamnt;
    }

    public void setDueamnt(String dueamnt) {
        this.dueamnt = dueamnt;
    }

    public String getReceiptdate() {
        return receiptdate;
    }

    public void setReceiptdate(String receiptdate) {
        this.receiptdate = receiptdate;
    }

    String balance;
    String amount;
    String invoiceno;
    String dueamnt;
    String receiptdate;

    public String getAdvanced_amnt() {
        return advanced_amnt;
    }

    public void setAdvanced_amnt(String advanced_amnt) {
        this.advanced_amnt = advanced_amnt;
    }

    String advanced_amnt;
}
