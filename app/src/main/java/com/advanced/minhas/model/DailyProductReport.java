package com.advanced.minhas.model;

public class DailyProductReport {


    private String shop;
    private String product;
    private int saleQty;
    private String returnQty;
    private String modeOfPay;
    private String salePercentage;
    private String returnPercentage;
    private String foc;
    private String remarks;

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(int saleQty) {
        this.saleQty = saleQty;
    }

    public String getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(String returnQty) {
        this.returnQty = returnQty;
    }

    public String getModeOfPay() {
        return modeOfPay;
    }

    public void setModeOfPay(String modeOfPay) {
        this.modeOfPay = modeOfPay;
    }

    public String getSalePercentage() {
        return salePercentage;
    }

    public void setSalePercentage(String salePercentage) {
        this.salePercentage = salePercentage;
    }

    public String getReturnPercentage() {
        return returnPercentage;
    }

    public void setReturnPercentage(String returnPercentage) {
        this.returnPercentage = returnPercentage;
    }

    public String getFoc() {
        return foc;
    }

    public void setFoc(String foc) {
        this.foc = foc;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
