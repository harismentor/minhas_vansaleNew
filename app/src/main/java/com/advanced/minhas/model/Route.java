package com.advanced.minhas.model;

import java.io.Serializable;

/**
 * Created by mentor on 25/10/17.
 */

public class Route implements Serializable {

    private int id;
    private String route;

    public Route() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return getRoute();
    }
}
