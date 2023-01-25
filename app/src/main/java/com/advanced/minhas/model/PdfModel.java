package com.advanced.minhas.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mentor on 1/7/17.
 */

public class PdfModel implements Serializable {


    private int lineNumber;

    private List<CartItem> cartItems;

    public PdfModel() {
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }


    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
