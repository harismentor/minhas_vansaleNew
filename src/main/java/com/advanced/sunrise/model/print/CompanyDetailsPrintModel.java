package com.advanced.minhas.model.print;

import android.graphics.Bitmap;

public class CompanyDetailsPrintModel {

    String compName;
    String companyAddress;
    String companyPhone;
    String companyEmail;
    String thanksMessage;
    String vat_gst_no ;
    String execName;
    String execMob ;
    String execId;
    Bitmap logo;

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getThanksMessage() {
        return thanksMessage;
    }

    public void setThanksMessage(String thanksMessage) {
        this.thanksMessage = thanksMessage;
    }

    public String getVat_gst_no() {
        return vat_gst_no;
    }

    public void setVat_gst_no(String vat_gst_no) {
        this.vat_gst_no = vat_gst_no;
    }

    public String getExecName() {
        return execName;
    }

    public void setExecName(String execName) {
        this.execName = execName;
    }

    public String getExecMob() {
        return execMob;
    }

    public void setExecMob(String execMob) {
        this.execMob = execMob;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }
}
