package com.advanced.minhas.model;

import java.io.Serializable;

public class Banks implements Serializable {

    private int bank_id;
    private String bank_name;

    public String getShown_in_contra() {
        return shown_in_contra;
    }

    public void setShown_in_contra(String shown_in_contra) {
        this.shown_in_contra = shown_in_contra;
    }

    private String shown_in_contra;

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

}
