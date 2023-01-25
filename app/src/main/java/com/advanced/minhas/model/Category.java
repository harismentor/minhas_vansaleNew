package com.advanced.minhas.model;

import java.io.Serializable;

/**
 * Created by mentor on 21/8/17.
 */

public class Category implements Serializable {
    private int categoryId;
    private   String categoryName;


    /*public Category(int id, String categoryName) {

        this.categoryId = id;
        this.categoryName = categoryName;
    }*/


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
