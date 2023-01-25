package com.advanced.minhas.model;

import java.io.Serializable;

public class Ledger implements Serializable {

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    private String date;

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    private String invoiceNo;
    private String invoiceAmount;
    private String received;
    private String balance;

}
