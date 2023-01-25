package com.advanced.minhas.model;

public class OrderItems {
    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
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

    public String getProductquantity() {
        return productquantity;
    }

    public void setProductquantity(String productquantity) {
        this.productquantity = productquantity;
    }

    public double getProductprice() {
        return productprice;
    }

    public void setProductprice(double productprice) {
        this.productprice = productprice;
    }

    public int getTray_count_each() {
        return tray_count_each;
    }

    public void setTray_count_each(int tray_count_each) {
        this.tray_count_each = tray_count_each;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    private String productid;
    private String productname;
    private String productcode;
    private String productquantity;
    private double productprice;
    private int tray_count_each;
    private String custid;
    private String product_type;


    public int getRequired_qty() {
        return required_qty;
    }

    public void setRequired_qty(int required_qty) {
        this.required_qty = required_qty;
    }

    private int required_qty;

    public int getTotal_req_products() {
        return total_req_products;
    }

    public void setTotal_req_products(int total_req_products) {
        this.total_req_products = total_req_products;
    }

    private int total_req_products;

    public int getStockqty() {
        return stockqty;
    }

    public void setStockqty(int stockqty) {
        this.stockqty = stockqty;
    }

    private int stockqty;
}
