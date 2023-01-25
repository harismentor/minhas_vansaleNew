package com.advanced.minhas.model;

import java.io.Serializable;

public class Transaction implements Serializable {

    private int localId;
    private int shopId;
    private double credit;
    private double debit;
    private double outStandingAmount;

    public Transaction() {
    }

    public Transaction(int shopId, double credit, double debit) {
        this.shopId = shopId;
        this.credit = credit;
        this.debit = debit;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getOutStandingAmount() {
        return outStandingAmount;
    }

    public void setOutStandingAmount(double outStandingAmount) {
        this.outStandingAmount = outStandingAmount;
    }
}
