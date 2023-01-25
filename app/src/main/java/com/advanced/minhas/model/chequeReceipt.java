package com.advanced.minhas.model;

import java.io.Serializable;

public class chequeReceipt implements Serializable {

    private String receiptNo;
    private float receivedAmount;
    private String chequeNo;
    private String createdDate;
    private int customerId;
    private int companyBankId;
    private String customerBank;
    private String chequeDate;
    private String clearingdate;
    private String UploadStatus;

    public String getLogDate() {
        return LogDate;
    }

    public void setLogDate(String logDate) {
        LogDate = logDate;
    }

    private String LogDate;

    public String getUploadStatus() {
        return UploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        UploadStatus = uploadStatus;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public float getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(float receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCompanyBankId() {
        return companyBankId;
    }

    public void setCompanyBankId(int companyBankId) {
        this.companyBankId = companyBankId;
    }

    public String getCustomerBank() {
        return customerBank;
    }

    public void setCustomerBank(String customerBank) {
        this.customerBank = customerBank;
    }

    public String getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate = chequeDate;
    }

    public String getClearingdate() {
        return clearingdate;
    }

    public void setClearingdate(String clearingdate) {
        this.clearingdate = clearingdate;
    }


}
