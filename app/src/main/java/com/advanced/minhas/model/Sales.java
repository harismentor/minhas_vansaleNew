package com.advanced.minhas.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mentor on 24/11/17.
 */

public class Sales implements Serializable {

    private String invoiceCode;

    public String getReturn_invoiceno() {
        return return_invoiceno;
    }

    public void setReturn_invoiceno(String return_invoiceno) {
        this.return_invoiceno = return_invoiceno;
    }

    private String return_invoiceno;

    public float getTotalkg() {
        return totalkg;
    }

    public void setTotalkg(float totalkg) {
        this.totalkg = totalkg;
    }

    private float totalkg;
    private   int locId;
    private int customerId;
    private String customerName;
    private double total;
    private double taxTotal;
    private double paid;
    private String date;
    private String saleType;
    private float discount;

    public String getVat_status() {
        return vat_status;
    }

    public void setVat_status(String vat_status) {
        this.vat_status = vat_status;
    }

    private String vat_status;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getReturn_type() {
        return return_type;
    }

    public void setReturn_type(String return_type) {
        this.return_type = return_type;
    }

    private String return_type;

    public String getRoundoff_type() {
        return roundoff_type;
    }

    public void setRoundoff_type(String roundoff_type) {
        this.roundoff_type = roundoff_type;
    }

    private String roundoff_type;




    private double taxPercentage;
    private double taxAmount;
    private double withoutTaxTotal;
    private double withTaxTotal;
    private String uploadStatus;

    public String getShopcode() {
        return shopcode;
    }

    public void setShopcode(String shopcode) {
        this.shopcode = shopcode;
    }

    private String shopcode;

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    private String shopname;

    public String getTax_type() {
        return tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    private String tax_type;

    public String getPayment_type() {
        return Payment_type;
    }

    public void setPayment_type(String payment_type) {
        Payment_type = payment_type;
    }

    private String Payment_type;


    public float getRoundofftot() {
        return roundofftot;
    }

    public void setRoundofftot(float roundofftot) {
        this.roundofftot = roundofftot;
    }

    private float roundofftot;

    public float getRoundoff_value() {
        return roundoff_value;
    }

    public void setRoundoff_value(float roundoff_value) {
        this.roundoff_value = roundoff_value;
    }

    private float roundoff_value;

    public double getTaxable_total() {
        return taxable_total;
    }

    public void setTaxable_total(double taxable_total) {
        this.taxable_total = taxable_total;
    }

    private double taxable_total;


    public double getCgst_tax() {
        return cgst_tax;
    }

    public void setCgst_tax(double cgst_tax) {
        this.cgst_tax = cgst_tax;
    }

    public double getSgst_tax() {
        return sgst_tax;
    }

    public void setSgst_tax(double sgst_tax) {
        this.sgst_tax = sgst_tax;
    }

    private double cgst_tax;
    private double sgst_tax;

    public double getTax_amount() {
        return tax_amount;
    }

    public void setTax_amount(double tax_amount) {
        this.tax_amount = tax_amount;
    }

    private double tax_amount;

    public String getHsn_code() {
        return hsn_code;
    }

    public void setHsn_code(String hsn_code) {
        this.hsn_code = hsn_code;
    }

    private String hsn_code;


    public String getCgst_tax_rate() {
        return cgst_tax_rate;
    }

    public void setCgst_tax_rate(String cgst_tax_rate) {
        this.cgst_tax_rate = cgst_tax_rate;
    }

    public String getSgst_tax_rate() {
        return sgst_tax_rate;
    }

    public void setSgst_tax_rate(String sgst_tax_rate) {
        this.sgst_tax_rate = sgst_tax_rate;
    }

    private String cgst_tax_rate;
    private String sgst_tax_rate;

    public String getProduct_tax_rate() {
        return product_tax_rate;
    }

    public void setProduct_tax_rate(String product_tax_rate) {
        this.product_tax_rate = product_tax_rate;
    }

    private String product_tax_rate;

    public double getDiscount_total_amount() {
        return discount_total_amount;
    }

    public void setDiscount_total_amount(double discount_total_amount) {
        this.discount_total_amount = discount_total_amount;
    }

    private double discount_total_amount;
    public double getDiscount_value() {
        return discount_value;
    }

    public void setDiscount_value(double discount_value) {
        this.discount_value = discount_value;
    }

    public String getSaleLatitude() {
        return saleLatitude;
    }

    public void setSaleLatitude(String saleLatitude) {
        this.saleLatitude = saleLatitude;
    }

    public String getSaleLongitude() {
        return saleLongitude;
    }

    public void setSaleLongitude(String saleLongitude) {
        this.saleLongitude = saleLongitude;
    }

    private String saleLatitude;
    private String saleLongitude;

    public String getReturn_billtype() {
        return return_billtype;
    }

    public void setReturn_billtype(String return_billtype) {
        this.return_billtype = return_billtype;
    }

    private String return_billtype;
    public double getWithoutDiscount() {
        return withoutDiscount;
    }

    public void setWithoutDiscount(double withoutDiscount) {
        this.withoutDiscount = withoutDiscount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getWithDiscount() {
        return withDiscount;
    }

    public void setWithDiscount(double withDiscount) {
        this.withDiscount = withDiscount;
    }

    private double withoutDiscount;
    private double discountAmount;
    private double withDiscount;
    private double discount_value;
    public String getTotal_discount_type() {
        return total_discount_type;
    }

    public void setTotal_discount_type(String total_discount_type) {
        this.total_discount_type = total_discount_type;
    }

    private String total_discount_type;
    public double getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(double discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    private double discount_percentage;


    public String getSize_string() {
        return size_string;
    }

    public void setSize_string(String size_string) {
        this.size_string = size_string;
    }

    public String getEach_size_qty() {
        return each_size_qty;
    }

    public void setEach_size_qty(String each_size_qty) {
        this.each_size_qty = each_size_qty;
    }

    private String size_string;
    private String each_size_qty;

    public int getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(int invoice_no) {
        this.invoice_no = invoice_no;
    }

    private  int invoice_no;


    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }



    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getWithoutTaxTotal() {
        return withoutTaxTotal;
    }

    public void setWithoutTaxTotal(double withoutTaxTotal) {
        this.withoutTaxTotal = withoutTaxTotal;
    }

    public double getWithTaxTotal() {
        return withTaxTotal;
    }

    public void setWithTaxTotal(double withTaxTotal) {
        this.withTaxTotal = withTaxTotal;
    }

    private ArrayList<CartItem> cartItems=new ArrayList<>();


    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public int getLocId() {
        return locId;
    }

    public void setLocId(int locId) {
        this.locId = locId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(double taxTotal) {
        this.taxTotal = taxTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }



    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
