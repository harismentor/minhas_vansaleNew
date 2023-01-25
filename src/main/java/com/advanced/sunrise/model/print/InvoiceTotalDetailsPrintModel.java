package com.advanced.minhas.model.print;

public class InvoiceTotalDetailsPrintModel {

    String itemTotal;
    String discountTotal;
    String totalAmount;
    String vat_gstTotal;
    String roundoff;
    String netAmount;


    public String getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
    }

    public String getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(String discountTotal) {
        this.discountTotal = discountTotal;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getVat_gstTotal() {
        return vat_gstTotal;
    }

    public void setVat_gstTotal(String vat_gstTotal) {
        this.vat_gstTotal = vat_gstTotal;
    }

    public String getRoundoff() {
        return roundoff;
    }

    public void setRoundoff(String roundoff) {
        this.roundoff = roundoff;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }
}
