package com.advanced.minhas.model;

import java.io.Serializable;


/**
 * Created by mentor on 6/5/17.
 */

public class Product implements Serializable{

    private int productId;
    private String barcode;
    private String productName;
    private String arabicName;
    private float retailPrice;
    private float wholeSalePrice;
    private float cost;
    private float tax;  //percentage
    private String brandName;
    private String productType;

    public int getBase_unitid() {
        return base_unitid;
    }

    public void setBase_unitid(int base_unitid) {
        this.base_unitid = base_unitid;
    }

    private int base_unitid;

    public int getSale_unitid() {
        return sale_unitid;
    }

    public void setSale_unitid(int sale_unitid) {
        this.sale_unitid = sale_unitid;
    }

    private int sale_unitid;



    public int getProduct_reporting_Unit() {
        return product_reporting_Unit;
    }

    public void setProduct_reporting_Unit(int product_reporting_Unit) {
        this.product_reporting_Unit = product_reporting_Unit;
    }

    private int product_reporting_Unit;

    public float getProduct_reporting_Price() {
        return product_reporting_Price;
    }

    public void setProduct_reporting_Price(float product_reporting_Price) {
        this.product_reporting_Price = product_reporting_Price;
    }

    private float product_reporting_Price;
    private String productCode;
    private float productBonus;
    private  int stockQuantity;
    private int piecepercart;



    public String getProduct_hsncode() {
        return product_hsncode;
    }

    public void setProduct_hsncode(String product_hsncode) {
        this.product_hsncode = product_hsncode;
    }

    private String product_hsncode;

    public String getProduct_taxlist() {
        return product_taxlist;
    }

    public void setProduct_taxlist(String product_taxlist) {
        this.product_taxlist = product_taxlist;
    }

    private String product_taxlist;

    public String getSizelist() {
        return sizelist;
    }

    public void setSizelist(String sizelist) {
        this.sizelist = sizelist;
    }

    private String sizelist;


    public String getSizelist_foredit() {
        return sizelist_foredit;
    }

    public void setSizelist_foredit(String sizelist_foredit) {
        this.sizelist_foredit = sizelist_foredit;
    }

    private String sizelist_foredit;





    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    private String unitId;

    public String getUnitselected() {
        return unitselected;
    }

    public void setUnitselected(String unitselected) {
        this.unitselected = unitselected;
    }

    private String unitselected;

    public String getUnitslist() {
        return unitslist;
    }

    public void setUnitslist(String unitslist) {
        this.unitslist = unitslist;
    }

    private String unitslist;



    public String getTaxlist() {
        return taxlist;
    }

    public void setTaxlist(String taxlist) {
        this.taxlist = taxlist;
    }

    private String taxlist;

    public float getProduct_rate() {
        return product_rate;
    }

    public void setProduct_rate(float product_rate) {
        this.product_rate = product_rate;
    }

    private float product_rate;

    public double getProduct_qntybyconfactor() {
        return product_qntybyconfactor;
    }

    public void setProduct_qntybyconfactor(double product_qntybyconfactor) {
        this.product_qntybyconfactor = product_qntybyconfactor;
    }

    private double product_qntybyconfactor;


    public Product()
    {
    }

    public float getProductBonus() {
        return productBonus;
    }

    public void setProductBonus(float productBonus) {
        this.productBonus = productBonus;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getArabicName() {
        return arabicName;
    }

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
    }

    public float getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(float retailPrice) {
        this.retailPrice = retailPrice;
    }

    public float getWholeSalePrice() {
        return wholeSalePrice;
    }

    public void setWholeSalePrice(float wholeSalePrice) {
        this.wholeSalePrice = wholeSalePrice;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getPiecepercart() {
        return piecepercart;
    }

    public void setPiecepercart(int piecepercart) {
        this.piecepercart = piecepercart;
    }
    public String getMfglist() {
        return mfglist;
    }

    public void setMfglist(String mfglist) {
        this.mfglist = mfglist;
    }

    private String mfglist;


    public float getCgst() {
        return cgst;
    }

    public void setCgst(float cgst) {
        this.cgst = cgst;
    }

    public float getSgst() {
        return sgst;
    }

    public void setSgst(float sgst) {
        this.sgst = sgst;
    }

    private float cgst;
    private float sgst;

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    private String p_name;



    public double getPc_price() {
        return pc_price;
    }

    public void setPc_price(double pc_price) {
        this.pc_price = pc_price;
    }

    private double pc_price;

    public float getMrp() {
        return mrp;
    }

    public void setMrp(float mrp) {
        this.mrp = mrp;
    }

    private float mrp;
    @Override
    public String toString() {
//        return super.toString();

        return productName;
    }

}
