package com.advanced.minhas.model;


public class StocktransferDetails {

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }



    public String getTransferid() {
        return transferid;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    String itemid = "";

    public int getProductid() {
        return productid;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    int productid = 0;
    String transferid = "";
    String quantity = "";
    String productname = "";
    String productcode = "";

    public int getApproval_qty() {
        return approval_qty;
    }

    public void setApproval_qty(int approval_qty) {
        this.approval_qty = approval_qty;
    }

    int approval_qty;
    public String getProductunitid() {
        return productunitid;
    }

    public void setProductunitid(String productunitid) {
        this.productunitid = productunitid;
    }

    String productunitid;

    public String getProduct_reportingunit() {
        return product_reportingunit;
    }

    public void setProduct_reportingunit(String product_reportingunit) {
        this.product_reportingunit = product_reportingunit;
    }

    String product_reportingunit;

}
