package com.advanced.minhas.model;

import java.io.Serializable;

/**
 * Created by mentor on 1/6/17.
 */

public class Brand implements Serializable {

    private int brandId;
    private String brandName;

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @Override
    public String toString() {
        return brandName;
    }
}
