package com.advanced.minhas.model;

public class DailyReport {


    private String customer;
    private String totalCreditSale;
    private String totalCashSale;
    private String totalReturnSale;
    private String totalCashCollection;
    private String totalBankCollection;
    private String totalChequeCollection;

    public String getTotalReturnCash() {
        return totalReturnCash;
    }

    public void setTotalReturnCash(String totalReturnCash) {
        this.totalReturnCash = totalReturnCash;
    }

    private String totalReturnCash;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTotalCreditSale() {
        return totalCreditSale;
    }

    public void setTotalCreditSale(String totalCreditSale) {
        this.totalCreditSale = totalCreditSale;
    }

    public String getTotalCashSale() {
        return totalCashSale;
    }

    public void setTotalCashSale(String totalCashSale) {
        this.totalCashSale = totalCashSale;
    }

    public String getTotalReturnSale() {
        return totalReturnSale;
    }

    public void setTotalReturnSale(String totalReturnSale) {
        this.totalReturnSale = totalReturnSale;
    }

    public String getTotalCashCollection() {
        return totalCashCollection;
    }

    public void setTotalCashCollection(String totalCashCollection) {
        this.totalCashCollection = totalCashCollection;
    }

    public String getTotalBankCollection() {
        return totalBankCollection;
    }

    public void setTotalBankCollection(String totalBankCollection) {
        this.totalBankCollection = totalBankCollection;
    }

    public String getTotalChequeCollection() {
        return totalChequeCollection;
    }

    public void setTotalChequeCollection(String totalChequeCollection) {
        this.totalChequeCollection = totalChequeCollection;
    }
}
