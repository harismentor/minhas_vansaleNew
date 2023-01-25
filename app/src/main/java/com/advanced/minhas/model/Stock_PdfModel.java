package com.advanced.minhas.model;

import java.io.Serializable;
import java.util.List;

public class Stock_PdfModel implements Serializable {

    private int lineNumber;

    private List<Product> cartItems;

    public Stock_PdfModel() {
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<Product> cartItems) {
        this.cartItems = cartItems;
    }


    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


}
