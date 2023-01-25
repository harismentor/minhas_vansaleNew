package com.advanced.minhas.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BillwiseReceiptMdl implements Serializable {
    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getInvoicedate() {
        return invoicedate;
    }

    public void setInvoicedate(String invoicedate) {
        this.invoicedate = invoicedate;
    }



    public float getBillamount() {
        return billamount;
    }

    public void setBillamount(float billamount) {
        this.billamount = billamount;
    }

    private String invoiceno;
    private String invoicedate;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    private String discount;

    public String getChequedate() {
        return chequedate;
    }

    public void setChequedate(String chequedate) {
        this.chequedate = chequedate;
    }

    public String getChequeno() {
        return chequeno;
    }

    public void setChequeno(String chequeno) {
        this.chequeno = chequeno;
    }

    public String getChequebank() {
        return chequebank;
    }

    public void setChequebank(String chequebank) {
        this.chequebank = chequebank;
    }

    private String chequedate;
    private String chequeno;
    private String chequebank;

    public String getModebank_name() {
        return modebank_name;
    }

    public void setModebank_name(String modebank_name) {
        this.modebank_name = modebank_name;
    }

    public String getModebank_referenceno() {
        return modebank_referenceno;
    }

    public void setModebank_referenceno(String modebank_referenceno) {
        this.modebank_referenceno = modebank_referenceno;
    }

    private String modebank_name;
    private String modebank_referenceno;

    public String getReceipt_enetered() {
        return receipt_enetered;
    }

    public void setReceipt_enetered(String receipt_enetered) {
        this.receipt_enetered = receipt_enetered;
    }

    private String receipt_enetered;

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    private String bill_date;

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getInvoice_amnt() {
        return invoice_amnt;
    }

    public void setInvoice_amnt(String invoice_amnt) {
        this.invoice_amnt = invoice_amnt;
    }

    private String invoice_date;
    private String invoice_amnt;

    public String getSale_id() {
        return sale_id;
    }

    public void setSale_id(String sale_id) {
        this.sale_id = sale_id;
    }

    private String sale_id;

    public String getInvoicebalance() {
        return invoicebalance;
    }

    public void setInvoicebalance(String invoicebalance) {
        this.invoicebalance = invoicebalance;
    }

    private String invoicebalance;
    private float billamount;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    private String remarks;

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    private String customerid;

    public String getDue_amnt() {
        return due_amnt;
    }

    public void setDue_amnt(String due_amnt) {
        this.due_amnt = due_amnt;
    }

    private String due_amnt;

    public float getTotalcash() {
        return totalcash;
    }

    public void setTotalcash(float totalcash) {
        this.totalcash = totalcash;
    }

    private float totalcash;

    public ArrayList<com.advanced.minhas.model.BillwiseReceiptList> getReceiptlist() {
        return receiptlist;
    }

    public void setReceiptlist(ArrayList<com.advanced.minhas.model.BillwiseReceiptList> receiptlist) {
        this.receiptlist = receiptlist;
    }

    private ArrayList<com.advanced.minhas.model.BillwiseReceiptList> receiptlist=new ArrayList<>();
}
