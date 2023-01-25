package com.advanced.minhas.model;

import java.io.Serializable;

public class ProductwiseReportMdl implements Serializable {
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getSumofqnty() {
        return sumofqnty;
    }

    public void setSumofqnty(String sumofqnty) {
        this.sumofqnty = sumofqnty;
    }

    public String getSumofvalue() {
        return sumofvalue;
    }

    public void setSumofvalue(String sumofvalue) {
        this.sumofvalue = sumofvalue;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getGrossvalue() {
        return grossvalue;
    }

    public void setGrossvalue(String grossvalue) {
        this.grossvalue = grossvalue;
    }

    private String category;
    private String itemname;
    private String sumofqnty;
    private String sumofvalue;
    private String tax;
    private String grossvalue;
}
