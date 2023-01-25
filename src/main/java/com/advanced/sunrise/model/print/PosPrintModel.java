package com.advanced.minhas.model.print;



import com.advanced.minhas.model.CartItem;

import java.util.ArrayList;

public class PosPrintModel {


    CompanyDetailsPrintModel companyDetailsPrintModel;
    CustomerDetailsPrintModel customerDetailsPrintModel;
    InvoiceDetailsPrintModel invoiceDetailsPrintModel;
    ArrayList<CartItem> itemDetails;
    InvoiceTotalDetailsPrintModel invoiceTotalDetailsPrintModel;
    FooterDetailsPrintModel footerDetailsPrintModel;
    ReceiptModel receiptModel;

    public String getSale_type() {
        return sale_type;
    }

    public void setSale_type(String sale_type) {
        this.sale_type = sale_type;
    }

    String sale_type;


    public ReceiptModel getReceiptModel() {
        return receiptModel;
    }

    public void setReceiptModel(ReceiptModel receiptModel) {
        this.receiptModel = receiptModel;
    }

    public CompanyDetailsPrintModel getCompanyDetailsPrintModel() {
        return companyDetailsPrintModel;
    }

    public void setCompanyDetailsPrintModel(CompanyDetailsPrintModel companyDetailsPrintModel) {
        this.companyDetailsPrintModel = companyDetailsPrintModel;
    }

    public CustomerDetailsPrintModel getCustomerDetailsPrintModel() {
        return customerDetailsPrintModel;
    }

    public void setCustomerDetailsPrintModel(CustomerDetailsPrintModel customerDetailsPrintModel) {
        this.customerDetailsPrintModel = customerDetailsPrintModel;
    }

    public InvoiceDetailsPrintModel getInvoiceDetailsPrintModel() {
        return invoiceDetailsPrintModel;
    }

    public void setInvoiceDetailsPrintModel(InvoiceDetailsPrintModel invoiceDetailsPrintModel) {
        this.invoiceDetailsPrintModel = invoiceDetailsPrintModel;
    }

    public ArrayList<CartItem> getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ArrayList<CartItem> itemDetails) {
        this.itemDetails = itemDetails;
    }

    public InvoiceTotalDetailsPrintModel getInvoiceTotalDetailsPrintModel() {
        return invoiceTotalDetailsPrintModel;
    }

    public void setInvoiceTotalDetailsPrintModel(InvoiceTotalDetailsPrintModel invoiceTotalDetailsPrintModel) {
        this.invoiceTotalDetailsPrintModel = invoiceTotalDetailsPrintModel;
    }

    public FooterDetailsPrintModel getFooterDetailsPrintModel() {
        return footerDetailsPrintModel;
    }

    public void setFooterDetailsPrintModel(FooterDetailsPrintModel footerDetailsPrintModel) {
        this.footerDetailsPrintModel = footerDetailsPrintModel;
    }
}
