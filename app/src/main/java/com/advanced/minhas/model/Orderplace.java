package com.advanced.minhas.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Orderplace implements Serializable {

    private String customerid;
    private String customename;
    private String date;

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    private String route_id;

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    private String route;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private ArrayList<OrderItems> orderItems=new ArrayList<>();

    public String getProduction_days() {
        return production_days;
    }

    public void setProduction_days(String production_days) {
        this.production_days = production_days;
    }

    private  String production_days;


    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getCustomename() {
        return customename;
    }

    public void setCustomename(String customename) {
        this.customename = customename;
    }

    public ArrayList<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }

}
