package com.advanced.minhas.model;

public class BonusReport {

    private String invoiceNo;
    private String creditNoteNo;
    private String customerName;
    private String bonusAmount;
    private String bonusReturnAmount;

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getCreditNoteNo() {
        return creditNoteNo;
    }

    public void setCreditNoteNo(String creditNoteNo) {
        this.creditNoteNo = creditNoteNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(String bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public String getBonusReturnAmount() {
        return bonusReturnAmount;
    }

    public void setBonusReturnAmount(String bonusReturnAmount) {
        this.bonusReturnAmount = bonusReturnAmount;
    }

}
