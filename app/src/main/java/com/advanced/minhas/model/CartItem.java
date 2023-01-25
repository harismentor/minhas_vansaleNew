package com.advanced.minhas.model;



/**
 * Created by sadiquekolakkal on 09-05-2017.
 */

public class CartItem extends Product {

    private int cartId;

    public float getTypeQuantity() {
        return typeQuantity;
    }

    public void setTypeQuantity(float typeQuantity) {
        this.typeQuantity = typeQuantity;
    }

    private float typeQuantity;

    public float getPieceQuantity() {
        return pieceQuantity;
    }

    public void setPieceQuantity(float pieceQuantity) {
        this.pieceQuantity = pieceQuantity;
    }

    private float pieceQuantity;
    private int stockQuantity;
    private float returnQuantity;
    private double productPrice;

    public String getVat_status() {
        return vat_status;
    }

    public void setVat_status(String vat_status) {
        this.vat_status = vat_status;
    }

    private String vat_status;


    public float getFreeQty() {
        return FreeQty;
    }

    public void setFreeQty(float freeQty) {
        FreeQty = freeQty;
    }

    private float FreeQty;


    public float getFreeqnty_piece() {
        return Freeqnty_piece;
    }

    public void setFreeqnty_piece(float freeqnty_piece) {
        Freeqnty_piece = freeqnty_piece;
    }

    private float Freeqnty_piece;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public double getProductPriceNew() {
        return productPriceNew;
    }

    public void setProductPriceNew(double productPriceNew) {
        this.productPriceNew = productPriceNew;
    }

    private double productPriceNew;

    public int getSale_item_id() {
        return sale_item_id;
    }

    public void setSale_item_id(int sale_item_id) {
        this.sale_item_id = sale_item_id;
    }

    private int sale_item_id;

    public double getProductPrice_temp() {
        return productPrice_temp;
    }

    public void setProductPrice_temp(double productPrice_temp) {
        this.productPrice_temp = productPrice_temp;
    }

    private double productPrice_temp;
    private double netPrice;
    private double salePrice;
    private double totalPrice;
    private double taxValue;
    private String orderType;

    public String getMfg_date() {
        return mfg_date;
    }

    public void setMfg_date(String mfg_date) {
        this.mfg_date = mfg_date;
    }

    @Override
    public String getBarcode() {
        return barcode;
    }

    @Override
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    private String mfg_date;
    private String barcode;

    public String getTax_type() {
        return tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    private String tax_type;


    public int getConfactr_kg() {
        return confactr_kg;
    }

    public void setConfactr_kg(int confactr_kg) {
        this.confactr_kg = confactr_kg;
    }

    private int confactr_kg ;


    public String getPrice_kgm() {
        return price_kgm;
    }

    public void setPrice_kgm(String price_kgm) {
        this.price_kgm = price_kgm;
    }

    private String price_kgm;


    public String getUnit_confactor() {
        return unit_confactor;
    }

    public void setUnit_confactor(String unit_confactor) {
        this.unit_confactor = unit_confactor;
    }

    public String getUnitid_selected() {
        return unitid_selected;
    }

    public void setUnitid_selected(String unitid_selected) {
        this.unitid_selected = unitid_selected;
    }

    private String unit_confactor;
    private String unitid_selected;

    public String getConfactor_kg() {
        return confactor_kg;
    }

    public void setConfactor_kg(String confactor_kg) {
        this.confactor_kg = confactor_kg;
    }

    private String confactor_kg;


    public Double getUnit_rate_kg() {
        return unit_rate_kg;
    }

    public void setUnit_rate_kg(Double unit_rate_kg) {
        this.unit_rate_kg = unit_rate_kg;
    }

    private Double unit_rate_kg;

    public Double getUnit_rate_pcs() {
        return unit_rate_pcs;
    }

    public void setUnit_rate_pcs(Double unit_rate_pcs) {
        this.unit_rate_pcs = unit_rate_pcs;
    }

    private Double unit_rate_pcs;


    public double getCon_factor() {
        return con_factor;
    }

    public void setCon_factor(double con_factor) {
        this.con_factor = con_factor;
    }

    private double  con_factor;


    public float getPieceQuantity_nw() {
        return pieceQuantity_nw;
    }

    public void setPieceQuantity_nw(float pieceQuantity_nw) {
        this.pieceQuantity_nw = pieceQuantity_nw;
    }

    private float pieceQuantity_nw;

    public double getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(double discount_percent) {
        this.discount_percent = discount_percent;
    }

    private double discount_percent;

    public double getProductTotal_withoutdisc() {
        return productTotal_withoutdisc;
    }

    public void setProductTotal_withoutdisc(double productTotal_withoutdisc) {
        this.productTotal_withoutdisc = productTotal_withoutdisc;
    }

    public double getTax_valuewithoutdisc() {
        return tax_valuewithoutdisc;
    }

    public void setTax_valuewithoutdisc(double tax_valuewithoutdisc) {
        this.tax_valuewithoutdisc = tax_valuewithoutdisc;
    }

    private double productTotal_withoutdisc;
    private double tax_valuewithoutdisc;
    public boolean isDiscountStatus() {
        return DiscountStatus;
    }

    public void setDiscountStatus(boolean discountStatus) {
        DiscountStatus = discountStatus;
    }

    public double getProductTotalValue() {
        return productTotalValue;
    }

    public void setProductTotalValue(double productTotalValue) {
        this.productTotalValue = productTotalValue;
    }

    public double getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(double productDiscount) {
        this.productDiscount = productDiscount;
    }

    public double getDisc_percentage() {
        return disc_percentage;
    }

    public void setDisc_percentage(double disc_percentage) {
        this.disc_percentage = disc_percentage;
    }

    private double disc_percentage;

    private boolean DiscountStatus;

    private double productTotalValue;
    private double productDiscount;
    public double getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(double productTotal) {
        this.productTotal = productTotal;
    }

    private double productTotal;
    public double getMrprate() {
        return mrprate;
    }

    public void setMrprate(double mrprate) {
        this.mrprate = mrprate;
    }

    private double mrprate;

    public String getEach_size_qty() {
        return each_size_qty;
    }

    public void setEach_size_qty(String each_size_qty) {
        this.each_size_qty = each_size_qty;
    }

    private String each_size_qty;

    public String getEach_size_qty_return() {
        return each_size_qty_return;
    }

    public void setEach_size_qty_return(String each_size_qty_return) {
        this.each_size_qty_return = each_size_qty_return;
    }

    private String each_size_qty_return;

    public double getTax_cgst() {
        return tax_cgst;
    }

    public void setTax_cgst(double tax_cgst) {
        this.tax_cgst = tax_cgst;
    }

    public double getTax_sgst() {
        return tax_sgst;
    }

    public void setTax_sgst(double tax_sgst) {
        this.tax_sgst = tax_sgst;
    }

    private double tax_cgst;
    private double tax_sgst;



    public String getSize_string() {
        return size_string;
    }

    public void setSize_string(String size_string) {
        this.size_string = size_string;
    }

    public String getSizeandqty_string() {
        return sizeandqty_string;
    }

    public void setSizeandqty_string(String sizeandqty_string) {
        this.sizeandqty_string = sizeandqty_string;
    }

    private String size_string;
    private String sizeandqty_string;

    public double getWithouttaxTotal() {
        return withouttaxTotal;
    }

    public void setWithouttaxTotal(double withouttaxTotal) {
        this.withouttaxTotal = withouttaxTotal;
    }

    private double withouttaxTotal;

    public double getNetprice_draft() {
        return netprice_draft;
    }

    public void setNetprice_draft(double netprice_draft) {
        this.netprice_draft = netprice_draft;
    }

    private double netprice_draft;

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    private String orderTypeName;

    public double getQnty_kgm() {
        return Qnty_kgm;
    }

    public void setQnty_kgm(double qnty_kgm) {
        Qnty_kgm = qnty_kgm;
    }

    private double Qnty_kgm;

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }



    @Override
    public int getStockQuantity() {
        return stockQuantity;
    }

    @Override
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public float getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(float returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(double netPrice) {
        this.netPrice = netPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(double taxValue) {
        this.taxValue = taxValue;
    }


    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }


    @Override
    public String toString() {
        return getProductName();
    }


/*
    public double getUnitPrice() {

//        unitPrice= getWithoutTaxPrice(getSalePrice(),getTax());
        return unitPrice;
    }*/
}
