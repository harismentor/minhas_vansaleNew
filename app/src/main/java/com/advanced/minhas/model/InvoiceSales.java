package com.advanced.minhas.model;

import java.io.Serializable;

public class InvoiceSales implements Serializable {

    private String invoiceId;
    private String invoiceNo;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

}
