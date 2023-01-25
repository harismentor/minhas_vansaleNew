package com.advanced.minhas.model;



/**
 * Created by mentor on 29/11/17.
 */

public class Division  {
    private int divisionId;
    private   String divisionName;


    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }


    @Override
    public String toString() {
        return getDivisionName();
    }
}
