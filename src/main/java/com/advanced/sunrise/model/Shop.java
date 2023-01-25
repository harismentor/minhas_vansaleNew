package com.advanced.minhas.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mentor on 23/10/17.
 */

public class Shop implements Serializable{



    private int localId;
    private int shopId;
    private String shopCode;
    private String shopName;
    private String shopArabicName;
    private String vatNumber;
    private String shopMail;
    private String shopMobile;
    private String shopAddress;
    private String latitude;
    private String longitude;
    private String routeCode;

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    private String contactPerson;
    public float getOpeningbalance() {
        return openingbalance;
    }

    public void setOpeningbalance(float openingbalance) {
        this.openingbalance = openingbalance;
    }

    private float openingbalance;
    private boolean isVisit;
    private float credit;
    private float debit;
    private float outStandingBalance;
    private float creditLimit;

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }

    private int state_id;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    private String state_code;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    private String group;
    private String route;


    public boolean isRegistered_status() {
        return registered_status;
    }

    public void setRegistered_status(boolean registered_status) {
        this.registered_status = registered_status;
    }

    private boolean registered_status;

    public String getCreditperiod() {
        return creditperiod;
    }

    public void setCreditperiod(String creditperiod) {
        this.creditperiod = creditperiod;
    }

    private String creditperiod;

    public String getCreditlimit_register() {
        return creditlimit_register;
    }

    public void setCreditlimit_register(String creditlimit_register) {
        this.creditlimit_register = creditlimit_register;
    }

    private String creditlimit_register;

    public String getPlace_ofsupply() {
        return place_ofsupply;
    }

    public void setPlace_ofsupply(String place_ofsupply) {
        this.place_ofsupply = place_ofsupply;
    }

    private String place_ofsupply;

    public String getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(String customer_type) {
        this.customer_type = customer_type;
    }

    private String customer_type;
    public float getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(float previousBalance) {
        this.previousBalance = previousBalance;
    }

    private float previousBalance;

    private ArrayList<CustomerProduct> products;


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

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopArabicName() {
        return shopArabicName;
    }

    public void setShopArabicName(String shopArabicName) {
        this.shopArabicName = shopArabicName;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getShopMail() {
        return shopMail;
    }

    public void setShopMail(String shopMail) {
        this.shopMail = shopMail;
    }

    public String getShopMobile() {
        return shopMobile;
    }

    public void setShopMobile(String shopMobile) {
        this.shopMobile = shopMobile;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }


    public ArrayList<CustomerProduct> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<CustomerProduct> products) {
        this.products = products;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public boolean isVisit() {
        return isVisit;
    }

    public void setVisit(boolean visit) {
        isVisit = visit;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public float getDebit() {
        return debit;
    }

    public void setDebit(float debit) {
        this.debit = debit;
    }

    public float getOutStandingBalance() {
        return outStandingBalance;
    }

    public void setOutStandingBalance(float outStandingBalance) {
        this.outStandingBalance = outStandingBalance;
    }

    public float getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(float creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Override
    public String toString() {
        return getShopName();
    }
}
