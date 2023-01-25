package com.advanced.minhas.model;

/**
 * Created by mentor on 29/11/17.
 */

public class Type  {

    private int typeId;
    private   String typeName;


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


    @Override
    public String toString() {
        return getTypeName();
    }
}
