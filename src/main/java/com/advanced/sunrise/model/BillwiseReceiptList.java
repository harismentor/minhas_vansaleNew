package com.advanced.minhas.model;

import java.io.Serializable;

public class BillwiseReceiptList implements Serializable {

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getInvoice_amnt() {
        return invoice_amnt;
    }

    public void setInvoice_amnt(String invoice_amnt) {
        this.invoice_amnt = invoice_amnt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String invoice_no;
    public String invoice_amnt;
    public String remarks;

}
