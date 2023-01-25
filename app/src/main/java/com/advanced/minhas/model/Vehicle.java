package com.advanced.minhas.model;

import java.io.Serializable;

public class Vehicle implements Serializable {


    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    private int vehicle_id;
    private String vehicle_no;
}
