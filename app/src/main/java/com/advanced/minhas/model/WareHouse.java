package com.advanced.minhas.model;

import java.io.Serializable;

/**
 * Created by mentor on 1/6/17.
 */

public class WareHouse implements Serializable {

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    private int warehouseId;
    private String warehouseName;



    @Override
    public String toString() {
        return warehouseName;
    }
}
