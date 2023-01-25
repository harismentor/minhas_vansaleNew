package com.advanced.minhas.model;



/**
 * Created by sadiquekolakkal on 09-05-2017.
 */

public class CartItemCode extends Product {

    private int cartId;

    private int typeQuantity;
    private int pieceQuantity;
    private int stockQuantity;
    private int returnQuantity;
    private double productPrice;
    private double netPrice;
    private double salePrice;
    private double totalPrice;
    private double taxValue;
    private String orderType;

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    private String orderTypeName;

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getTypeQuantity() {
        return typeQuantity;
    }

    public void setTypeQuantity(int typeQuantity) {
        this.typeQuantity = typeQuantity;
    }

    public int getPieceQuantity() {
        return pieceQuantity;
    }

    public void setPieceQuantity(int pieceQuantity) {
        this.pieceQuantity = pieceQuantity;
    }

    @Override
    public int getStockQuantity() {
        return stockQuantity;
    }

    @Override
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(int returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(double netPrice) {
        this.netPrice = netPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(double taxValue) {
        this.taxValue = taxValue;
    }


    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }


    @Override
    public String toString() {
        return getProductCode();
    }


/*
    public double getUnitPrice() {

//        unitPrice= getWithoutTaxPrice(getSalePrice(),getTax());
        return unitPrice;
    }*/
}
