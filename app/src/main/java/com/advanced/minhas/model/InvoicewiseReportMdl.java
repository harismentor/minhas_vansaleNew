package com.advanced.minhas.model;

import java.io.Serializable;

public class InvoicewiseReportMdl implements Serializable {
    public String getAccname() {
        return accname;
    }

    public void setAccname(String accname) {
        this.accname = accname;
    }

    public String getSumofgty() {
        return sumofgty;
    }

    public void setSumofgty(String sumofgty) {
        this.sumofgty = sumofgty;
    }

    public String getSumofvalue() {
        return sumofvalue;
    }

    public void setSumofvalue(String sumofvalue) {
        this.sumofvalue = sumofvalue;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getGrossvalue() {
        return grossvalue;
    }

    public void setGrossvalue(String grossvalue) {
        this.grossvalue = grossvalue;
    }

    private String accname;
    private String sumofgty;
    private String sumofvalue;
    private String tax;
    private String grossvalue;

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    private String invoiceno;



}
