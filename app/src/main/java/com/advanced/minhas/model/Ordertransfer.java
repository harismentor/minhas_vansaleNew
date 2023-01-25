package com.advanced.minhas.model;

public class Ordertransfer {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStockFrom() {
        return stockFrom;
    }

    public void setStockFrom(String stockFrom) {
        this.stockFrom = stockFrom;
    }

    public String getStockTo() {
        return stockTo;
    }

    public void setStockTo(String stockTo) {
        this.stockTo = stockTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String id;
    private String orderNo;
    private String date;
    private String stockFrom;
    private String stockTo;
    private String description;
}
