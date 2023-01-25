package com.advanced.minhas.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mentor on 1/6/17.
 */

public class ProductType implements Serializable {



   private int typeId;
    private String typeName;

    private ArrayList<Brand> brands;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public ArrayList<Brand> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<Brand> brands) {
        this.brands = brands;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
