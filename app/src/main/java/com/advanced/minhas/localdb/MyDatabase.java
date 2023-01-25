package com.advanced.minhas.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.advanced.minhas.model.Banks;
import com.advanced.minhas.model.BillwiseReceiptMdl;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.CartItemCode;
import com.advanced.minhas.model.CustomerProduct;
import com.advanced.minhas.model.DailyProductReport;
import com.advanced.minhas.model.DailyReport;
import com.advanced.minhas.model.District;
import com.advanced.minhas.model.Expense;
import com.advanced.minhas.model.InvoiceSales;
import com.advanced.minhas.model.NoSale;
import com.advanced.minhas.model.OrderItems;
import com.advanced.minhas.model.Product;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Route;
import com.advanced.minhas.model.RouteGroup;
import com.advanced.minhas.model.Sales;

import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.SizelistMasterstock;
import com.advanced.minhas.model.State;
import com.advanced.minhas.model.StocktransferDetails;
import com.advanced.minhas.model.Transaction;
import com.advanced.minhas.model.Vehicle;
import com.advanced.minhas.model.chequeReceipt;
import com.advanced.minhas.session.SessionValue;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.advanced.minhas.config.AmountCalculator.getSalePrice;
import static com.advanced.minhas.config.AmountCalculator.getTaxPrice;
import static com.advanced.minhas.config.AmountCalculator.getWithoutTaxPrice;
import static com.advanced.minhas.config.ConfigKey.REQ_ANY_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_BANK_DETAILS;
import static com.advanced.minhas.config.ConfigKey.REQ_CUSTOMER_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_DISTRICT;
import static com.advanced.minhas.config.ConfigKey.REQ_EDIT_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_EXPENSE;
import static com.advanced.minhas.config.ConfigKey.REQ_NO_SALE_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_QUOTATION_TEMP;
import static com.advanced.minhas.config.ConfigKey.REQ_QUOTATION_TEMPEDIT;
import static com.advanced.minhas.config.ConfigKey.REQ_QUOTATION_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_BILLWISE;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_CHEQUE;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_RETURN_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_SALE_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_STATE;
import static com.advanced.minhas.config.ConfigKey.REQ_VAN_STOCK;
import static com.advanced.minhas.config.ConfigKey.REQ_VEHICLE_DETAILS;
import static com.advanced.minhas.config.Generic.dbDateFormat;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;

/**
 * Created by mentor on 24/11/17.
 */

public class MyDatabase extends SQLiteOpenHelper {

    private static final String TAG = "MyDatabase";

    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "idrees_joma_db";
    //tbl_customer table name
    private static final String TABLE_CUSTOMER = "tbl_customer";
    private static final String TABLE_CUSTOMER_VISIT = "tbl_customer_visit";
    private static final String TABLE_RECEIPT = "tbl_receipts";
    private static final String TABLE_GROUPS = "tbl_groups";
    private static final String TABLE_VEHICLES = "tbl_vehicles";
    private static final String TABLE_STOCK = "tbl_stock";
    private static final String TABLE_SALE_CUSTOMER = "tbl_sales_customer";
    private static final String TABLE_SALE_PRODUCTS = "tbl_sales_products";
    private static final String TABLE_QUOTATION_PRODUCTS_TEMP = "tbl_quotation_products_temp";
    private static final String TABLE_QUOTATION_PRODUCTS_TEMP_EDIT = "tbl_quotation_products_temp_edit";

    private static final String TABLE_QUOTATION_CUSTOMER = "tbl_quotation_customer";

    private static final String TABLE_QUOTATION_PRODUCTS = "tbl_quotation_products";

    private static final String TABLE_WO_RETURN_CUSTOMER = "tbl_wo_return_customer"; //without invoice return
    private static final String TABLE_WO_RETURN_PRODUCTS = "tbl_wo_return_products";

    private static final String TABLE_BANKS = "tbl_banks";

    private static final String TABLE_SELLING_PRICE = "tbl_selling_price";
    private static final String TABLE_CUSTOMER_PRODUCTS = "tbl_customer_products";

    private static final String TABLE_CHEQUE_DETAILS = "tbl_cheque_details";

    private static final String TABLE_RETURN_OFFLINE = "tbl_return_offline";
    private static final String TABLE_RETURN_OFFLINE_PRODUCTS = "tbl_return_products";

    private static final String TABLE_EXPENSE = "tbl_expense";

    private static final String TABLE_EXPENSE_DETAILS = "tbl_expense_details";

    private static final String TABLE_STOCK_MASTER = "tbl_stock_master";

    private static final String TABLE_STOCK_SIZE_AND_QUANTITY_MASTER = "tbl_stocksize_and_quantity";

    private static final String TABLE_SALE_SIZE_AND_QUANTITY = "tbl_salesize_and_quantity";

    //haris added on 10-12-2020
    private static final String TABLE_STATE = "tbl_state";

    private static final String TABLE_DISTRICT = "tbl_district";

    private static final String TABLE_SIZE_MASTER = "tbl_size_master"; //for quotation

    private static final String TABLE_UNITS = "tbl_units";

    private static final String TABLE_STOCK_APPROVAL = "tbl_stock_approval";

    private static final String TABLE_BILLWISE_RECEIPTS = "tbl_billwise_receipts";

    private static final String TABLE_ROUTE = "tbl_route";

    private static final String TABLE_BILLWISE_RECEIPT_DETAILS = "tbl_billwise_receipt_details";

    private static final String TABLE_BILLWISE_RECEIPT_DETAILS_LIST = "tbl_billwise_receipt_details_list";
    /**
     * columns ids
     */

    private static final String WHERE_CLAUSE = " =?";

    private static final String COL_ID = "id_int";
    private static final String COL_FK_CUSTOMER_ID = "fk_customer_id_int";
    private static final String COL_FK_RETURN_ID = "fk_return_id_int";
    private static final String COL_CREATED_AT = "created_at_date";
    private static final String COL_FK_BILLWISE_ID = "fk_billwise_id_int";

    //TABLE_STOCK_SIZE_AND_QUANTITY_MASTER
    private static final String COL_STOCKSIZE = "stock_size";
    private static final String COL_STOCK_QTY = "stock_qty";

    //TABLE_SALE_SIZE_AND_QUANTITY
    private static final String COL_SALE_SIZE = "sale_size";
    private static final String COL_SALE_QTY = "sale_qty";

    //TABLE_STATE added on 10-12-2020 haris
    private static final String COL_STATE = "state_name";
    private static final String COL_STATE_ID = "state_id";

    //TABLE_DISTRICT added on 10-12-2020 haris
    private static final String COL_DISTRICT = "district_name";
    private static final String COL_DISTRICT_ID = "district_id";

    //TABLE_STOCKMASTER added on 02-02-2021 haris
    private static final String COL_SIZEMASTER_NAME = "sizemaster_name";
    private static final String COL_SIZEMASTER_ID = "sizemaster_id";
    private static final String COL_SALE_ROUNDOFFTYPE = "roundoff_type_txt";
    private static final String COL_TOTAL_DISCOUNT_TYPE = "total_discount_type";
    private static final String COL_DISCOUNT_TOTAL_AMOUNT = "total_discount_amount";
    //    customer visit status table
    private static final String COL_VISIT_SALE = "sale_status_int";
    private static final String COL_VISIT_QUOTATION = "quotation_status_int";
    private static final String COL_VISIT_RETURN = "return_status_int";
    private static final String COL_VISIT_RECEIPT = "receipt_status_int";
    private static final String COL_VISIT_RECEIPT_BILLWISE = "receiptbillwise_status_int";

    private static final String COL_VISIT_NO_SALE = "nosale_status_int";
    private static final String COL_VISIT_DATE_TIME = "sale_date_time";
    private static final String COL_VISIT_NO_SALE_REASON = "reason_status_txt";

    private static final String COL_VISIT_NO_SALE_LATITUDE = "nosale_latitude_txt";
    private static final String COL_VISIT_NO_SALE_LONGITUDE = "nosale_longitude_txt";

    //    Product table
    private static final String COL_PRODUCT_ID = "product_id_int";
    private static final String COL_BARCODE = "barcode_int";
    private static final String COL_PRODUCT_NAME = "product_name_txt";
    private static final String COL_P_NAME = "p_name_txt";
    private static final String COL_PRODUCT_ARABIC_NAME = "product_arab_name_txt";
    private static final String COL_PRODUCT_CODE = "product_code_txt";
    private static final String COL_PRODUCT_BONUS = "product_bonus";
    private static final String COL_PRODUCT_BRAND = "product_brand_txt";
    private static final String COL_PRODUCT_UNITS = "product_units_txt";
    private static final String COL_PRODUCT_TYPE = "product_type_txt";
    private static final String COL_PRODUCT_MRP = "product_mrp_real";
    private static final String COL_PRODUCT_NETPRICE = "product_neprice_real";
    private static final String COL_PRODUCT_WHOLESALE = "product_wholesale_real";
    private static final String COL_PRODUCT_COST = "product_cost_real";
    private static final String COL_PRODUCT_TAX = "product_tax_real";
    private static final String COL_PRODUCT_PEACE_PER_CART = "product_cart_quantity_int";
    private static final String COL_PRODUCT_QUANTITY = "product_quantity_int";
    private static final String COL_PRODUCT_HSNCODE = "product_hsncode_txt";
    private static final String COL_PRODUCT_TAX_AMOUNT = "product_tax_amount_real";
    private static final String COL_PRODUCT_CONFACTOR = "product_confactor_real";
    private static final String COL_PRODUCT_DISCOUNT = "product_discount";
    private static final String COL_PRODUCT_REPORTING_UNIT = "product_reportingunit_int";
    private static final String COL_PRODUCT_REPORTING_PRICE = "product_reportingprice_float";
    private static final String COL_PRODUCT_ITEM_ID = "product_item_id_real";
    private static final String COL_PRODUCT_TRANSFER_ID = "product_transer_id_real";
    private static final String COL_PRODUCT_MFGLIST = "product_mfglist_txt";
    private static final String COL_RETURN_PRODUCT_MFGDATE = "product_return_mfgdate_txt";
    private static final String COL_RETURN_PRODUCT_BARCODE = "product_return_barcode_txt";
    private static final String COL_PRODUCT_CGST = "product_sale_cgst";
    private static final String COL_PRODUCT_SGST = "product_sale_sgst";
    private static final String COL_PRODUCT_SALE_UNIT = "product_saleunit_int";
    private static final String COL_PRODUCT_CONFACTORKG = "product_confactorkg";
    private static final String COL_PRODUCT_DESCRIPTION = "product_description";
    private static final String COL_PRODUCT_DISC_PERCENTAGE = "product_disc_percentage";
    private static final String COL_SALE_PRODUCT_FREEQNTY = "sale_free_quantity_int";
    private static final String COL_PRODUCT_VAT_STATUS = "product_vat_status";

    //haris 21-11-2020
    private static final String COL_PRODUCT_RATE = "product_rate";
    ///

    private static final String COL_PRODUCT_SIZE = "product_size_txt";
    private static final String COL_PRODUCT_TAXLIST = "product_taxlist_txt";
    private static final String COL_PRODUCT_SIZE_MASTER = "product_sizemaster_txt";


    //       Customer  columns
    private static final String COL_CUSTOMER_ID = "customer_id_int";
    private static final String COL_CUSTOMER_NAME = "customer_name_txt";
    private static final String COL_CUSTOMER_NAME_ARABIC = "customer_name_arab_txt";
    private static final String COL_CUSTOMER_CODE = "customer_code_txt";
    private static final String COL_CUSTOMER_MAIL = "customer_mail_txt";
    private static final String COL_CUSTOMER_MOBILE = "customer_mobile_txt";
    private static final String COL_CUSTOMER_ADDRESS = "customer_address_txt";
    private static final String COL_CUSTOMER_LONGITUDE = "customer_longitude_txt";
    private static final String COL_CUSTOMER_LATITUDE = "customer_latitude_txt";
    private static final String COL_CUSTOMER_CREDIT = "customer_credit_real";
    private static final String COL_CUSTOMER_DEBIT = "customer_debit_real";
    private static final String COL_CUSTOMER_CREDITLIMIT = "customer_creditlimit_real";
    private static final String COL_CUSTOMER_CREDITLIMIT_REGISTER = "customer_creditlimit_register_real";
    private static final String COL_CUSTOMER_OPENINGBALANCE = "customer_openingbalance_real";
    private static final String COL_ROUTE_CODE = "customer_route_code_txt";
    private static final String COL_CUSTOMER_CONTACTPERSON = "customer_contactperson_txt";
    private static final String COL_CUSTOMER_OUTSTANDING_BALANCE = "outstanding_bal_real";
    private static final String COL_CUSTOMER_VISIT_STATUS = "customer_close_status_int";
    private static final String COL_CUSTOMER_REGISTERED_STATUS = "customer_register_status_int";
    private static final String COL_CATEGORY = "customer_category_txt";
    private static final String COL_CUSTOMER_TYPE = "customer_type_txt";
    private static final String COL_CUSTOMER_PREVIOUS_BALANCE = "previous_bal_real";
    private static final String COL_CUSTOMER_PLACEOFSUPPLY = "customer_placeof_supply";
    private static final String COL_CUSTOMER_CREDITPERIOD = "customer_creditperiod";
    private static final String COL_CUSTOMER_VAT = "customer_placeof_supply";
    private static final String COL_CUSTOMER_VAT_NO = "customer_vat_no";
    private static final String COL_CUSTOMER_STATEID = "customer_state_id";
    private static final String COL_CUSTOMER_STATECODE = "customer_state_code";

    private static final String COL_CUSTOMER_ROUTE = "customer_route";
    private static final String COL_CUSTOMER_GROUP = "customer_group";


    //       Receipt  columns

    private static final String COL_INVOICE_NO = "invoice_no_txt";
    private static final String COL_RECEIPT_BALANCE = "receipt_balance_real";
    private static final String COL_RECEIPT_BANK = "receipt_bank";
    private static final String COL_RECEIPT_VOUCHERNO = "receipt_voucherno";
    private static final String COL_RECEIPT_REFERNO = "receipt_referno";
    //  Receipt  transaction  columns
    private static final String COL_RECEIPT_NO = "receipt_no_txt";
    private static final String COL_RECEIVABLE_AMOUNT = "receipt_receivable_real";
    private static final String COL_RECEIPT_TYPE = "receipt_type_txt";
    private static final String COL_RECEIPT_BANKID = "receipt_bankid_txt";
    private static final String COL_RECEIPT_BANKNAME = "receipt_bank_txt";

    private static final String COL_RECEIPT_LATITUDE = "receipt_latitude_txt";
    private static final String COL_RECEIPT_LONGITUDE = "receipt_longitude_txt";

    //    TABLE_SALE_CUSTOMER  columns

    private static final String COL_INVOICE_CODE = "invoice_no_txt";
    private static final String COL_INVOICE_NO_INT = "invoice_no_real";
    private static final String COL_SALE_TYPE = "sale_type_txt";
    private static final String COL_SALE_TOTAL = "sale_total_real";
    private static final String COL_SALE_PAID = "sale_paid_real";
    private static final String COL_SALE_ROUNDOFF_TOT = "sale_roundofftot_real";
    private static final String COL_SALE_ROUNDOFF = "sale_roundoff_real";
    private static final String COL_PAYMENT_TYPE = "payment_type_txt";
    private static final String COL_SALE_LATITUDE = "sale_latitude";
    private static final String COL_SALE_LONGITUDE = "sale_longitude";
    private static final String COL_DISCOUNT = "discount";
    private static final String COL_WITH_DISCOUNT = "with_discount";
    private static final String COL_DISCOUNT_PERCENT = "discount_percentage";
    private static final String COL_RETURN_PAID = "return_paid_real";
    private static final String COL_SALE_TAXABLE_TOTAL = "sale_taxable_total";
    private static final String COL_SALE_CGST_TAX = "sale_cgst_tax";
    private static final String COL_SALE_SGST_TAX = "sale_sgst_tax";
    private static final String COL_SALE_TAX_RATE = "sale_cgst_tax_rate";
    private static final String COL_SALE_TAX_TYPE = "sale_cgst_tax_type";
    private static final String COL_SALE_SGST_TAX_RATE = "sale_sgst_tax_rate";
    private static final String COL_SALE_TOTAL_DISCOUNT = "sale_total_discount";
    private static final String COL_SALE_TOTAL_DISCOUNT_RATE = "sale_total_discount_rate";
    private static final String COL_SALE_HSN_CODE = "sale_hsn_code";
    private static final String COL_SALE_TOTALKG = "sale_total_kg";
    private static final String COL_SALE_VAT_STATUS = "sale_vat_status";


    private static final String COL_TAX_PERCENTAGE = "sale_tax_percentage";
    private static final String COL_TAX_AMOUNT = "sale_tax_amount";
    private static final String COL_WITHOUT_TAX = "sale_without_tax_amount";
    private static final String COL_WITH_TAX_TOTAL = "sale_withtax_total";
    private static final String COL_UPLOAD_STATUS = "upload_status";
    private static final String COL_UPLOAD_DATE = "upload_date";



    //    TABLE_SALE_PRODUCTS  columns
    private static final String COL_BONUS_PERCENTAGE = "sale_product_bonus";
    private static final String COL_PRODUCT_PRICE = "prod_price_real";
    private static final String COL_SALE_PRODUCT_QUANTITY = "sale_piece_quantity_int";
    private static final String COL_SALE_PRODUCT_SIZE_STRING = "sale_size_string";
    private static final String COL_SALE_PRODUCT_SIZEANDQTY_STRING = "sale_sizeandqty_string";
    private static final String COL_SALE_PRODUCT_UNIT_SELECTED = "sale_unit_string";
    private static final String COL_PRODUCT_TOTAL = "sale_product_real";
    private static final String COL_PRODUCT_TOTAL_VALUE = "sale_product_totalvalue_real";
    private static final String COL_PRODUCT_QNTY_BYUNIT = "product_qnty_byunit_txt";
    private static final String COL_SALE_PRODUCT_ORDER_TYPE = "sale_order_type_txt";
    private static final String COL_SALE_ORDER_TYPE_QUANTITY = "order_type_quantity_int";
    private static final String COL_SALE_PRODUCT_TAXTYPE = "order_type_tax_string";
    private static final String COL_SALE_PRODUCT_BARCODE = "order_barcode_string";
    private static final String COL_SALE_PRODUCT_MFGDATE = "order_mfgdate_string";
    private static final String COL_PRODUCT_UNITID = "product_unitid_txt";
    private static final String COL_PRODUCT_UNITCONFACTOR = "product_unitconfctr_txt";

    private static final String COL_PRODUCT_KGM_PRICE = "product_kgm_price_txt";
    private static final String COL_PRODUCT_PCS_PRICE = "product_pcs_price_txt";
    private static final String COL_PRODUCT_KGM_QNTY = "product_kgm_qnty_txt";
    private static final String COL_PRODUCT_INSERT_STATUS = "product_insert_status_txt";
    //    TABLE_WO_RETURN_CUSTOMER  columns
    private static final String COL_RETURN_CODE = "return_no_txt";
    private static final String COL_TAX_TOTAL = "tax_total_real";
    private static final String COL_RETURN_TOTAL = "return_total_real";
    private static final String COL_RETURN_REMARKS = "remarks_txt";
    private static final String COL_RETURN_TYPE = "return_type_txt";
    private static final String COL_RETURN_TYPE_WITHORWITHOUT = "return_type_withorwithout_txt";
    private static final String COL_RETURN_PRODUCT_ORDER_TYPE = "return_order_type_txt";
    private static final String COL_RETURN_ORDER_TYPE_QUANTITY = "return_type_quantity_int";
    private static final String COL_RETURN_PRODUCT_QUANTITY = "return_piece_quantity_int";
    private static final String COL_RETURN_PRODUCT_SIZE_STRING = "return_size_string";
    private static final String COL_RETURN_PRODUCT_SIZEANDQTY_STRING = "return_sizeandqty_string";
    private static final String COL_RETURN_PRODUCT_HSNCODE = "product_hsncode";
    private static final String COL_RETURN_PRODUCT_DISCOUNT = "product_discount";
    private static final String COL_RETURN_PRODUCT_UNIT = "product_unitselected";
    private static final String COL_RETURN_INVOICE_CODE = "return_invoiceno_txt";
    private static final String COL_RETURN_PRODUCT_TAXTYPE = "return_taxtype_text";
    private static final String COL_RETURN_TAXPERCENTAGE = "return_taxpercentage";
    private static final String COL_RETURN_TAXAMOUNT = "return_taxamount";
    private static final String COL_RETURN_WITHOUTTAX_TOTAL = "return_withouttax_total";
    private static final String COL_RETURN_WITHTAX_TOTAL = "return_with_taxtotal";
    private static final String COL_RETURN_HSNCODE = "return_hsncode";
    private static final String COL_RETURN_CGSTRATE = "return_cgstrate";
    private static final String COL_RETURN_SGSTRATE = "return_sgstrate";
    private static final String COL_RETURN_CGSTAMOUNT = "return_cgstamount";
    private static final String COL_RETURN_SGSTAMOUNT = "return_sgstamount";
    private static final String COL_RETURN_DISCOUNTPERCENTAGE = "return_discountpercentage";
    private static final String COL_RETURN_DISCOUNT_TOTAL = "return_discount_total";
    private static final String COL_RETURN_DISCOUNT = "return_discount";
    private static final String COL_RETURN_TAXABLE_AMOUNT = "return_taxable_amount";
    private static final String COL_RETURN_PRODUCT_UNITID = "return_product_unitid";
    private static final String COL_RETURN_PRODUCT_MRP = "return_product_mrp";
    private static final String COL_RETURN_PRODUCT_RATEKG = "return_product_ratekg";
    private static final String COL_RETURN_PRODUCT_RATEPCS = "return_product_ratepcs";


    private static final String COL_RETURN_PRODUCT_CONFACTORKG = "return_product_confactorkg";
    //  Bank Details columns
    private static final String COL_BANK_ID = "bank_id";
    private static final String COL_BANK_NAME = "bank_name";
    private static final String COL_SHOW_CONTRA = "show_contra";

    //  Groups Details columns
    private static final String COL_GROUP_ID = "group_id";
    private static final String COL_GROUP_NAME = "group_name";

    //  TABLE Selling Price columns
    private static final String COL_SELLING_PRICE = "selling_price";

    private static final String COL_CUSTOMER_COST = "customer_price_real";

    private static final String COL_CHEQUE_NUMBER = "cheque_number";
    private static final String COL_COMPANY_BANK = "company_bank";
    private static final String COL_CUSTOMER_BANK = "customer_bank";
    private static final String COL_RECEIVE_DATE = "receiving_date";
    private static final String COL_CLEARING_DATE = "clearing_date";

    private static final String COL_GRAND_TOTAL = "grand_total";
    private static final String COL_UNIT_PRICE = "unit_price";
    private static final String COL_RETURN_AMOUNT = "return_amount";

    //  Expense columns
    private static final String COL_EXPENSE_ID = "expense_id";
    private static final String COL_EXPENSE_NAME = "expense_name";

    // EXPENSE details columns
    private static final String COL_EXPENSE_AMOUNT= "expense_amount";
    private static final String COL_EXPENSE_RECEIPTNO= "expense_receiptno";
    private static final String COL_EXPENSE_REMARKS= "expense_remarks";

    //  VEHICLE Details columns
    private static final String COL_VEHICLE_ID = "vehicle_id";
    private static final String COL_VEHICLE_NO = "vehicle_no";

    private static final String COL_BILLWISE_RECCEIPT_INVOICENO = "receipt_invoiceno_txt";
    private static final String COL_BILLWISE_RECCEIPT_TOTALCASH = "receipt_totalcash_txt";
    private static final String COL_BILLWISE_RECCEIPT_AMOUNT = "receipt_amount_txt";
    private static final String COL_BILLWISE_RECCEIPT_REMARK = "receipt_remark_txt";
    private static final String COL_BILLWISE_RECCEIPT_CUSTOMERID = "receipt_customerid_txt";
    private static final String COL_BILLWISE_RECCEIPT_INVOICEAMNT = "receipt_invoiceamnt_txt";
    private static final String COL_BILLWISE_RECCEIPT_INVOICEDATE = "receipt_invoicedate_txt";
    private static final String COL_BILLWISE_RECCEIPT_DUEAMNT = "receipt_dueamnt_txt";
    private static final String COL_BILLWISE_RECCEIPT_SALEID = "receipt_saleid_txt";
    private static final String COL_BILLWISE_RECCEIPT_BILLDATE = "receipt_billdate_txt";
    private static final String COL_BILLWISE_RECCEIPT_CHEQUENO = "receipt_billwise_chequeno_txt";
    private static final String COL_BILLWISE_RECCEIPT_CHEQUEDATE = "receipt_billwise_chequedate_txt";
    private static final String COL_BILLWISE_RECCEIPT_CHEQUEBANK = "receipt_billwise_chequebank_txt";
    private static final String COL_BILLWISE_RECCEIPT_MODEBANK_NAME = "receipt_billwise_modebank_bank_txt";
    private static final String COL_BILLWISE_RECCEIPT_MODEBANK_REFERENCE = "receipt_billwise_modebank_reference_txt";
    private static final String COL_BILLWISE_RECCEIPT_AMOUNTENTERED = "receipt_billwise_amountentered_txt";
    private static final String COL_BILLWISE_RECCEIPT_INVOICEBALANCE = "receipt_invoicebalance_txt";
    private static final String COL_RECEIPT_MODE = "receipt_mode_txt";
    private static final String COL_BILLWISE_RECCEIPT_DISCOUNT = "receipt_billdiscount_txt";

    //  ROUTE Details columns
    private static final String COL_ROUTE_ID = "route_id";
    private static final String COL_ROUTE = "route_name";

    private Context context;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    public static String getDateTime() {

        Date date = new Date();
        return dbDateFormat.format(date);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //String CREATE_STOCK = "CREATE TABLE " + TABLE_STOCK + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + COL_BARCODE +" TEXT," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_BONUS + " REAL NOT NULL ," + COL_PRODUCT_BRAND + " TEXT ," + COL_PRODUCT_UNITS + " TEXT ,"+ COL_PRODUCT_TYPE + " TEXT ," + COL_PRODUCT_MRP + " REAL NOT NULL ," + COL_PRODUCT_WHOLESALE + " REAL NOT NULL ," + COL_PRODUCT_COST + " REAL NOT NULL ," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL )";
        //String CREATE_STOCK = "CREATE TABLE " + TABLE_STOCK + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + COL_BARCODE +" TEXT," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_BONUS + " REAL NOT NULL ," + COL_PRODUCT_BRAND + " TEXT ," + COL_PRODUCT_UNITS + " TEXT , " + COL_PRODUCT_SIZE +" TEXT ," + COL_PRODUCT_TYPE + " TEXT ," + COL_PRODUCT_MRP + " REAL NOT NULL ," + COL_PRODUCT_WHOLESALE + " REAL NOT NULL ," + COL_PRODUCT_COST + " REAL NOT NULL ," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL )";
        String CREATE_STOCK = "CREATE TABLE " + TABLE_STOCK + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + COL_BARCODE +" TEXT," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_P_NAME + " TEXT  ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_BONUS + " REAL NOT NULL ," + COL_PRODUCT_BRAND + " TEXT ," + COL_PRODUCT_UNITS + " TEXT , " + COL_PRODUCT_SIZE +" TEXT ," + COL_PRODUCT_TYPE + " TEXT ," + COL_PRODUCT_MRP + " REAL NOT NULL ," + COL_PRODUCT_WHOLESALE + " REAL NOT NULL ," + COL_PRODUCT_COST + " REAL NOT NULL , " + COL_PRODUCT_RATE + " REAL ," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_REPORTING_UNIT + " INTEGER NOT NULL," + COL_PRODUCT_SALE_UNIT + " INTEGER  ," + COL_PRODUCT_REPORTING_PRICE + " REAL NOT NULL, " + COL_PRODUCT_HSNCODE + " TEXT," + COL_PRODUCT_TAXLIST +" TEXT , " + COL_PRODUCT_MFGLIST + " TEXT);";


        String CREATE_CUSTOMER = "CREATE TABLE " + TABLE_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL UNIQUE ," + COL_CUSTOMER_NAME + " TEXT NOT NULL ,  " + COL_CUSTOMER_CREDITPERIOD + " TEXT , " + COL_CUSTOMER_GROUP + " TEXT , " + COL_CUSTOMER_ROUTE + " TEXT , " + COL_CUSTOMER_PLACEOFSUPPLY + " TEXT ," + COL_CUSTOMER_NAME_ARABIC + " TEXT DEFAULT '', " + COL_STATE_ID + " TEXT ," + COL_CUSTOMER_CODE + " TEXT NOT NULL ," + COL_CUSTOMER_MAIL + " TEXT NOT NULL ," + COL_CUSTOMER_MOBILE + " TEXT NOT NULL ," + COL_CUSTOMER_ADDRESS + " TEXT NOT NULL ," + COL_CUSTOMER_LATITUDE + " TEXT DEFAULT '' ," + COL_CUSTOMER_LONGITUDE + " TEXT DEFAULT '' ," + COL_ROUTE_CODE + " TEXT NOT NULL ," + COL_CUSTOMER_CREDIT + " REAL NOT NULL , " + COL_CUSTOMER_CREDITLIMIT_REGISTER + " TEXT ," + COL_CUSTOMER_DEBIT + " REAL NOT NULL , " + COL_CUSTOMER_CONTACTPERSON + " TEXT ," + COL_CUSTOMER_CREDITLIMIT + " REAL NOT NULL , " + COL_CUSTOMER_OPENINGBALANCE + " REAL NOT NULL ," + COL_CUSTOMER_STATEID + " REAL , " + COL_CUSTOMER_STATECODE + " TEXT ," + COL_CUSTOMER_OUTSTANDING_BALANCE + " REAL NOT NULL ," + COL_CUSTOMER_PREVIOUS_BALANCE + " REAL ," + COL_CUSTOMER_VISIT_STATUS + " flag INTEGER DEFAULT 0 ," + COL_CUSTOMER_VAT_NO + " TEXT," + COL_CUSTOMER_REGISTERED_STATUS + " flag INTEGER DEFAULT 0 ," + COL_CUSTOMER_TYPE + " TEXT );";

        //String CREATE_CUSTOMER_STATUS = "CREATE TABLE " + TABLE_CUSTOMER_VISIT + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_VISIT_SALE + " flag INTEGER DEFAULT 0 ," + COL_VISIT_QUOTATION + " flag INTEGER DEFAULT 0 ," + COL_VISIT_RETURN + " flag INTEGER DEFAULT 0 ," + COL_VISIT_RECEIPT + " flag INTEGER DEFAULT 0 ," + COL_VISIT_NO_SALE + " flag INTEGER DEFAULT 0 ," + COL_VISIT_DATE_TIME + "  DATETIME ,"+ COL_VISIT_NO_SALE_REASON + " TEXT NOT NULL ," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL UNIQUE, FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMER + " (" + COL_CUSTOMER_ID + ") ON DELETE CASCADE);";

        String CREATE_CUSTOMER_STATUS = "CREATE TABLE " + TABLE_CUSTOMER_VISIT + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_VISIT_SALE + " flag INTEGER DEFAULT 0 ," + COL_VISIT_QUOTATION + " flag INTEGER DEFAULT 0 ," + COL_VISIT_RETURN + " flag INTEGER DEFAULT 0 ," + COL_VISIT_RECEIPT + " flag INTEGER DEFAULT 0 ," + COL_VISIT_RECEIPT_BILLWISE + " flag INTEGER DEFAULT 0 ," + COL_VISIT_NO_SALE + " flag INTEGER DEFAULT 0 , " + COL_VISIT_NO_SALE_LATITUDE + " TEXT DEFAULT 0," + COL_VISIT_NO_SALE_LONGITUDE + " TEXT DEFAULT 0," + COL_VISIT_DATE_TIME + "  DATETIME ,"+ COL_VISIT_NO_SALE_REASON + " TEXT NOT NULL ," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL UNIQUE, FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMER + " (" + COL_CUSTOMER_ID + ") ON DELETE CASCADE);";

        String CREATE_TRANSACTIONS = "CREATE TABLE " + TABLE_RECEIPT + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_RECEIPT_NO + " TEXT NOT NULL UNIQUE ," + COL_RECEIPT_BANKID + " REAL NOT NULL," + COL_RECEIPT_BANK + " TEXT , " + COL_RECEIPT_REFERNO + " TEXT , " + COL_RECEIPT_TYPE + " TEXT NOT NULL," + COL_RECEIVABLE_AMOUNT + " REAL NOT NULL ," + COL_RECEIPT_BALANCE + " REAL NOT NULL ,"+ COL_UPLOAD_STATUS + " TEXT ,  " + COL_RECEIPT_VOUCHERNO + " TEXT ," + COL_RECEIPT_LATITUDE + " TEXT ," + COL_RECEIPT_LONGITUDE+ " TEXT ," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMER + " (" + COL_CUSTOMER_ID + ") ON DELETE CASCADE);";

        //String CREATE_SALE_CUSTOMER = "CREATE TABLE " + TABLE_SALE_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ," + COL_SALE_TYPE + " TEXT NOT NULL ," + COL_SALE_TOTAL + " REAL NOT NULL ," + COL_SALE_PAID + " REAL NOT NULL," + COL_INVOICE_CODE + " TEXT NOT NULL UNIQUE,"+ COL_UPLOAD_STATUS + " TEXT ," + COL_TAX_AMOUNT + " REAL NOT NULL ," + COL_TAX_PERCENTAGE + " REAL NOT NULL ," + COL_WITHOUT_TAX + " REAL NOT NULL ," + COL_WITH_TAX_TOTAL + " REAL NOT NULL ," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //String CREATE_SALE_CUSTOMER = "CREATE TABLE " + TABLE_SALE_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ," + COL_SALE_TYPE + " TEXT NOT NULL ," + COL_SALE_TOTAL + " REAL NOT NULL ," + COL_SALE_PAID + " REAL NOT NULL," + COL_INVOICE_CODE + " TEXT NOT NULL UNIQUE,"+ COL_UPLOAD_STATUS + " TEXT ," + COL_TAX_AMOUNT + " REAL NOT NULL ," + COL_TAX_PERCENTAGE + " REAL NOT NULL ," + COL_WITHOUT_TAX + " REAL NOT NULL ," + COL_WITH_TAX_TOTAL + " REAL NOT NULL ," + COL_SALE_TAXABLE_TOTAL + " TEXT ," + COL_SALE_CGST_TAX + " TEXT , " + COL_SALE_SGST_TAX + " TEXT ," + COL_SALE_CGST_TAX_RATE + " TEXT ," + COL_SALE_SGST_TAX_RATE + " TEXT ," + COL_SALE_HSN_CODE + " TEXT ," + COL_SALE_TOTAL_DISCOUNT + " TEXT ," + COL_SALE_TOTAL_DISCOUNT_RATE + " TEXT ," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP);";


        //String CREATE_SALE_CUSTOMER = "CREATE TABLE " + TABLE_SALE_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ," + COL_SALE_TYPE + " TEXT NOT NULL ," + COL_SALE_TOTAL + " REAL NOT NULL ," + COL_SALE_PAID + " REAL NOT NULL," + COL_INVOICE_CODE + " TEXT NOT NULL UNIQUE,"+ COL_UPLOAD_STATUS + " TEXT ," + COL_TAX_AMOUNT + " REAL NOT NULL ," + COL_TAX_PERCENTAGE + " REAL NOT NULL ," + COL_WITHOUT_TAX + " REAL NOT NULL ," + COL_WITH_TAX_TOTAL + " REAL NOT NULL ," + COL_SALE_TAXABLE_TOTAL + " TEXT ," + COL_SALE_CGST_TAX + " TEXT , " + COL_SALE_SGST_TAX + " TEXT ," + COL_SALE_TAX_RATE + " TEXT ," + COL_SALE_SGST_TAX_RATE + " TEXT ," + COL_SALE_HSN_CODE + " TEXT ," + COL_SALE_TOTAL_DISCOUNT + " TEXT ," + COL_SALE_TOTAL_DISCOUNT_RATE + " TEXT , " + COL_SALE_ROUNDOFF_TOT + " TEXT , " + COL_SALE_ROUNDOFF + " TEXT ," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP," + COL_SALE_LATITUDE + " TEXT," + COL_SALE_LONGITUDE + " TEXT);";
        String CREATE_SALE_CUSTOMER = "CREATE TABLE " + TABLE_SALE_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ," + COL_SALE_TYPE + " TEXT NOT NULL ," + COL_SALE_VAT_STATUS + " TEXT , " + COL_SALE_ROUNDOFFTYPE +" TEXT ," + COL_CUSTOMER_NAME + " TEXT ," + COL_CUSTOMER_CODE + " TEXT ," + COL_DISCOUNT_TOTAL_AMOUNT + " REAL ," + COL_INVOICE_NO_INT + " REAL ," + COL_SALE_TOTAL + " REAL NOT NULL ," + COL_SALE_PAID + " REAL NOT NULL," + COL_TOTAL_DISCOUNT_TYPE+ " TEXT," + COL_INVOICE_CODE + " TEXT NOT NULL UNIQUE,"+ COL_UPLOAD_STATUS + " TEXT ," + COL_PAYMENT_TYPE + " TEXT ," + COL_TAX_AMOUNT + " REAL NOT NULL ," + COL_TAX_PERCENTAGE + " REAL NOT NULL , " + COL_DISCOUNT + " REAL NOT NULL ," + COL_WITH_DISCOUNT + " REAL NOT NULL ," + COL_WITHOUT_TAX + " REAL NOT NULL ," + COL_WITH_TAX_TOTAL + " REAL NOT NULL ," + COL_SALE_TAXABLE_TOTAL + " TEXT ," + COL_SALE_CGST_TAX + " TEXT , " + COL_SALE_SGST_TAX + " TEXT ," + COL_SALE_TAX_RATE + " TEXT ," + COL_SALE_SGST_TAX_RATE + " TEXT ," + COL_SALE_HSN_CODE + " TEXT ," + COL_SALE_TOTAL_DISCOUNT + " TEXT ," + COL_SALE_TOTAL_DISCOUNT_RATE + " TEXT , " + COL_SALE_ROUNDOFF_TOT + " TEXT , " + COL_SALE_ROUNDOFF + " TEXT ," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP," + COL_SALE_LATITUDE + " TEXT," + COL_SALE_LONGITUDE + " TEXT);";


        //created by haris on 03-08-2021
        String CREATE_SALE_PRODUCTS = "CREATE TABLE " + TABLE_SALE_PRODUCTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_P_NAME + " TEXT , " + COL_SALE_PRODUCT_TAXTYPE + " TEXT NOT NULL , " + COL_SALE_PRODUCT_BARCODE + " TEXT , " + COL_SALE_PRODUCT_MFGDATE+ " TEXT , " + COL_PRODUCT_ARABIC_NAME + " TEXT, " + COL_PRODUCT_QNTY_BYUNIT + " INTEGER NOT NULL ," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_SGST + " REAL ," + COL_PRODUCT_DISC_PERCENTAGE + " REAL ," + COL_SALE_PRODUCT_FREEQNTY + " REAL  , " + COL_PRODUCT_DESCRIPTION + " TEXT , " + COL_PRODUCT_CONFACTOR + " REAL ," + COL_PRODUCT_PRICE + " REAL NOT NULL ," + COL_SALE_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_BONUS_PERCENTAGE + " REAL NOT NULL ," + COL_SALE_PRODUCT_ORDER_TYPE + " TEXT NOT NULL," + COL_SALE_ORDER_TYPE_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_SALE_TOTAL + " REAL NOT NULL," + COL_PRODUCT_MRP + " REAL NOT NULL," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL , " + COL_SALE_PRODUCT_SIZE_STRING +" TEXT , " + COL_SALE_PRODUCT_SIZEANDQTY_STRING +" TEXT  ," + COL_SALE_PRODUCT_UNIT_SELECTED +" TEXT , " + COL_PRODUCT_TAX_AMOUNT +" REAL , " + COL_PRODUCT_DISCOUNT + " REAL DEFAULT 0 ," + COL_PRODUCT_TOTAL + " REAL NOT NULL, " + COL_PRODUCT_HSNCODE +" TEXT ,FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_SALE_CUSTOMER + " (" + COL_ID + ") ON DELETE CASCADE);";
        /// COL_PRODUCT_TAX_AMOUNT
        // String CREATE_QUOTATION_CUSTOMER = "CREATE TABLE " + TABLE_QUOTATION_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ," + COL_SALE_TYPE + " TEXT NOT NULL ," + COL_SALE_TOTAL + " REAL NOT NULL," + COL_UPLOAD_STATUS + " TEXT ," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String CREATE_QUOTATION_CUSTOMER = "CREATE TABLE " + TABLE_QUOTATION_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ," + COL_SALE_TYPE + " TEXT NOT NULL , " + COL_SALE_VAT_STATUS + " TEXT , " + COL_SALE_LATITUDE + " TEXT , " + COL_SALE_TAX_TYPE + " TEXT ,  " + COL_SALE_ROUNDOFF_TOT + " TEXT , " + COL_SALE_ROUNDOFF + " TEXT ," + COL_SALE_LONGITUDE + " TEXT  ," + COL_DISCOUNT + " REAL NOT NULL, " + COL_WITH_DISCOUNT + " REAL NOT NULL , " + COL_SALE_TOTALKG + " REAL , " + COL_INVOICE_CODE + " TEXT NOT NULL ," + COL_SALE_TOTAL + " REAL NOT NULL," + COL_TAX_AMOUNT + " REAL NOT NULL ," + COL_TAX_PERCENTAGE + " REAL NOT NULL ," + COL_WITHOUT_TAX + " REAL NOT NULL ," + COL_WITH_TAX_TOTAL + " REAL NOT NULL ," + COL_SALE_TAXABLE_TOTAL + " TEXT ," + COL_SALE_CGST_TAX + " TEXT , " + COL_SALE_SGST_TAX + " TEXT ," + COL_SALE_TAX_RATE + " TEXT ," + COL_SALE_SGST_TAX_RATE + " TEXT ," + COL_SALE_HSN_CODE + " TEXT ," + COL_SALE_TOTAL_DISCOUNT + " TEXT ," + COL_SALE_TOTAL_DISCOUNT_RATE + " TEXT ," + COL_UPLOAD_STATUS + " TEXT ," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP)";

        // String CREATE_QUOTATION_PRODUCTS = "CREATE TABLE " + TABLE_QUOTATION_PRODUCTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_PRICE + " REAL NOT NULL ," + COL_SALE_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_TAX + " REAL NOT NULL DEFAULT 0," + COL_SALE_TOTAL + " REAL NOT NULL," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_QUOTATION_CUSTOMER + " (" + COL_ID + ") ON DELETE CASCADE);";

        String CREATE_QUOTATION_PRODUCTS = "CREATE TABLE " + TABLE_QUOTATION_PRODUCTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_CODE + " TEXT NOT NULL , " + COL_SALE_PRODUCT_TAXTYPE + " TEXT ," + COL_PRODUCT_QNTY_BYUNIT + " INTEGER NOT NULL ," + COL_PRODUCT_CONFACTORKG + " INTEGER NOT NULL ," + COL_SALE_PRODUCT_UNIT_SELECTED +" TEXT , " + COL_PRODUCT_KGM_PRICE +" TEXT , " + COL_PRODUCT_PCS_PRICE +" REAL ,"  + COL_PRODUCT_PRICE + " REAL NOT NULL , " + COL_PRODUCT_NETPRICE + " REAL ," + COL_PRODUCT_MRP + " REAL ," + COL_PRODUCT_NAME +" TEXT NOT NULL ," + COL_SALE_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_BONUS_PERCENTAGE + " REAL NOT NULL , " + COL_PRODUCT_SGST + " REAL , " + COL_PRODUCT_CGST + " REAL ," + COL_PRODUCT_TAX_AMOUNT + " REAL ," + COL_SALE_PRODUCT_ORDER_TYPE + " TEXT NOT NULL, " + COL_PRODUCT_UNITID + " TEXT ," + COL_PRODUCT_UNITCONFACTOR + " TEXT ," + COL_PRODUCT_TOTAL + " REAL NOT NULL ," + COL_PRODUCT_TOTAL_VALUE + " REAL NOT NULL ," +  COL_PRODUCT_DISCOUNT + " REAL , " + COL_SALE_ORDER_TYPE_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_SALE_TOTAL + " REAL NOT NULL," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, " + COL_SALE_PRODUCT_SIZE_STRING +" TEXT , " + COL_SALE_PRODUCT_SIZEANDQTY_STRING +" TEXT , " + COL_PRODUCT_HSNCODE +" TEXT ,FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_QUOTATION_CUSTOMER + " (" + COL_ID + ") ON DELETE CASCADE);";

        //String CREATE_QUOTATION_PRODUCTSTEMP = "CREATE TABLE " + TABLE_QUOTATION_PRODUCTS_TEMP + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_CODE + " TEXT NOT NULL , " + COL_PRODUCT_DESCRIPTION + " TEXT , " + COL_SALE_PRODUCT_TAXTYPE + " TEXT , " + COL_PRODUCT_VAT_STATUS + " TEXT ," + COL_PRODUCT_QNTY_BYUNIT + " INTEGER NOT NULL ," + COL_PRODUCT_CONFACTORKG + " INTEGER NOT NULL , " + COL_PRODUCT_CONFACTOR + " REAL , " + COL_PRODUCT_KGM_QNTY + " REAL ," + COL_PRODUCT_DISC_PERCENTAGE + " REAL ,"  + COL_SALE_PRODUCT_UNIT_SELECTED +" TEXT , " + COL_PRODUCT_KGM_PRICE + " TEXT ," + COL_PRODUCT_PRICE + " REAL NOT NULL , " + COL_PRODUCT_MRP + " REAL , " + COL_PRODUCT_NETPRICE + " REAL ,"  + COL_PRODUCT_NAME +" TEXT NOT NULL ," + COL_SALE_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_BONUS_PERCENTAGE + " REAL NOT NULL , " + COL_PRODUCT_SGST + " REAL , " + COL_PRODUCT_CGST + " REAL ," + COL_PRODUCT_TAX_AMOUNT + " REAL ," + COL_SALE_PRODUCT_ORDER_TYPE + " TEXT NOT NULL, " + COL_PRODUCT_UNITID + " TEXT ," + COL_PRODUCT_UNITCONFACTOR + " TEXT ," + COL_PRODUCT_TOTAL + " REAL NOT NULL ," + COL_PRODUCT_TOTAL_VALUE + " REAL NOT NULL ," +  COL_PRODUCT_DISCOUNT + " REAL , " + COL_SALE_ORDER_TYPE_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_SALE_TOTAL + " REAL NOT NULL," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, " + COL_SALE_PRODUCT_SIZE_STRING +" TEXT , " + COL_SALE_PRODUCT_SIZEANDQTY_STRING +" TEXT , " + COL_PRODUCT_HSNCODE +" TEXT );";

        String CREATE_QUOTATION_PRODUCTSTEMP = "CREATE TABLE " + TABLE_QUOTATION_PRODUCTS_TEMP + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_CODE + " TEXT NOT NULL , " + COL_PRODUCT_DESCRIPTION + " TEXT , " + COL_SALE_PRODUCT_TAXTYPE + " TEXT , " + COL_PRODUCT_VAT_STATUS + " TEXT ," + COL_PRODUCT_QNTY_BYUNIT + " INTEGER NOT NULL ," + COL_PRODUCT_CONFACTORKG + " INTEGER NOT NULL , " + COL_PRODUCT_CONFACTOR + " REAL , " + COL_PRODUCT_KGM_QNTY + " REAL ," + COL_PRODUCT_DISC_PERCENTAGE + " REAL ,"  + COL_SALE_PRODUCT_UNIT_SELECTED +" TEXT , " + COL_PRODUCT_KGM_PRICE + " TEXT ," + COL_PRODUCT_PRICE + " REAL NOT NULL , " + COL_PRODUCT_MRP + " REAL , " + COL_PRODUCT_NETPRICE + " REAL ,"  + COL_PRODUCT_NAME +" TEXT NOT NULL ,"  + COL_P_NAME +" TEXT  ," + COL_SALE_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_BONUS_PERCENTAGE + " REAL NOT NULL , " + COL_PRODUCT_SGST + " REAL , " + COL_PRODUCT_CGST + " REAL ," + COL_PRODUCT_TAX_AMOUNT + " REAL ," + COL_SALE_PRODUCT_ORDER_TYPE + " TEXT NOT NULL, " + COL_PRODUCT_UNITID + " TEXT ," + COL_PRODUCT_UNITCONFACTOR + " TEXT ," + COL_PRODUCT_TOTAL + " REAL NOT NULL ," + COL_PRODUCT_TOTAL_VALUE + " REAL NOT NULL ," +  COL_PRODUCT_DISCOUNT + " REAL , " + COL_SALE_ORDER_TYPE_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_SALE_TOTAL + " REAL NOT NULL," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, " + COL_SALE_PRODUCT_SIZE_STRING +" TEXT , " + COL_SALE_PRODUCT_SIZEANDQTY_STRING +" TEXT , " + COL_PRODUCT_HSNCODE +" TEXT );";

        String CREATE_QUOTATION_PRODUCTSTEMP_EDIT = "CREATE TABLE " + TABLE_QUOTATION_PRODUCTS_TEMP_EDIT + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_CODE + " TEXT NOT NULL , " + COL_PRODUCT_DESCRIPTION + " TEXT ," + COL_SALE_PRODUCT_TAXTYPE + " TEXT ," + COL_PRODUCT_VAT_STATUS + " TEXT ," + COL_PRODUCT_QNTY_BYUNIT + " INTEGER NOT NULL ," + COL_PRODUCT_CONFACTORKG + " INTEGER NOT NULL , " + COL_PRODUCT_CONFACTOR + " REAL , " + COL_PRODUCT_KGM_QNTY + " REAL ,"  + COL_SALE_PRODUCT_UNIT_SELECTED +" TEXT , " + COL_PRODUCT_KGM_PRICE + " TEXT ," + COL_PRODUCT_PRICE + " REAL NOT NULL , " + COL_PRODUCT_MRP + " REAL , " + COL_PRODUCT_NETPRICE + " REAL ,"  + COL_PRODUCT_NAME +" TEXT NOT NULL ," + COL_SALE_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_BONUS_PERCENTAGE + " REAL NOT NULL , " + COL_PRODUCT_SGST + " REAL , " + COL_PRODUCT_CGST + " REAL ," + COL_PRODUCT_TAX_AMOUNT + " REAL ," + COL_SALE_PRODUCT_ORDER_TYPE + " TEXT NOT NULL, " + COL_PRODUCT_UNITID + " TEXT ," + COL_PRODUCT_UNITCONFACTOR + " TEXT ," + COL_PRODUCT_TOTAL + " REAL NOT NULL ," + COL_PRODUCT_TOTAL_VALUE + " REAL NOT NULL ," +  COL_PRODUCT_DISCOUNT + " REAL , " + COL_SALE_ORDER_TYPE_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_SALE_TOTAL + " REAL NOT NULL," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, " + COL_SALE_PRODUCT_SIZE_STRING +" TEXT , " + COL_SALE_PRODUCT_SIZEANDQTY_STRING +" TEXT , " + COL_PRODUCT_HSNCODE +" TEXT );";


        //String CREATE_WO_RETURN_CUSTOMER = "CREATE TABLE IF NOT EXISTS " + TABLE_WO_RETURN_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ," +COL_RETURN_CODE +" TEXT NOT NULL DEFAULT '',"+COL_RETURN_TYPE + " TEXT NOT NULL ," + COL_RETURN_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_UPLOAD_STATUS + " TEXT ," + COL_TAX_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_RETURN_REMARKS + "  TEXT DEFAULT ''," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP);";
        String CREATE_WO_RETURN_CUSTOMER = "CREATE TABLE IF NOT EXISTS " + TABLE_WO_RETURN_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ," +COL_RETURN_CODE +" TEXT NOT NULL DEFAULT ''," + COL_PAYMENT_TYPE + " TEXT , " + COL_RETURN_INVOICE_CODE + " TEXT  DEFAULT ''," +COL_RETURN_TYPE + " TEXT DEFAULT '' ," + COL_RETURN_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_UPLOAD_STATUS + " TEXT , " + COL_RETURN_TYPE_WITHORWITHOUT + " TEXT ," + COL_TAX_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_RETURN_PAID + " REAL NOT NULL DEFAULT 0," + COL_RETURN_REMARKS + "  TEXT DEFAULT ''," + COL_CUSTOMER_NAME + "  TEXT DEFAULT '', " + COL_CUSTOMER_CODE + "  TEXT DEFAULT ''," + COL_RETURN_TAXPERCENTAGE +" TEXT DEFAULT ''," + COL_RETURN_TAXAMOUNT +" TEXT DEFAULT ''," + COL_RETURN_WITHOUTTAX_TOTAL +" TEXT DEFAULT ''," + COL_RETURN_WITHTAX_TOTAL +" TEXT DEFAULT ''," + COL_RETURN_HSNCODE +" TEXT DEFAULT ''," + COL_RETURN_CGSTRATE +" TEXT DEFAULT ''," + COL_RETURN_SGSTRATE +" TEXT DEFAULT ''," + COL_RETURN_CGSTAMOUNT +" TEXT DEFAULT ''," + COL_RETURN_SGSTAMOUNT +" TEXT DEFAULT ''," + COL_RETURN_DISCOUNTPERCENTAGE +" TEXT DEFAULT ''," + COL_WITH_DISCOUNT + " REAL NOT NULL ," + COL_SALE_ROUNDOFF_TOT + " REAL  ," + COL_RETURN_DISCOUNT_TOTAL +" TEXT DEFAULT '', " + COL_RETURN_DISCOUNT + " TEXT DEFAULT '' ," + COL_RETURN_TAXABLE_AMOUNT +" TEXT DEFAULT ''," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP ," + COL_SALE_LATITUDE +" TEXT ," + COL_SALE_LONGITUDE +" TEXT );";



        //String CREATE_WO_RETURN_PRODUCTS = "CREATE TABLE IF NOT EXISTS " + TABLE_WO_RETURN_PRODUCTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_PRICE + " REAL NOT NULL ," + COL_RETURN_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_RETURN_PRODUCT_ORDER_TYPE + " TEXT NOT NULL," + COL_RETURN_ORDER_TYPE_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_RETURN_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_FK_RETURN_ID + " INTEGER NOT NULL, FOREIGN KEY (" + COL_FK_RETURN_ID + ") REFERENCES " + TABLE_WO_RETURN_CUSTOMER + " (" + COL_ID + ") ON DELETE CASCADE);";
        String CREATE_WO_RETURN_PRODUCTS = "CREATE TABLE IF NOT EXISTS " + TABLE_WO_RETURN_PRODUCTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_RETURN_PRODUCT_TAXTYPE + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT , " + COL_RETURN_PRODUCT_BARCODE + " TEXT , " + COL_RETURN_PRODUCT_MFGDATE + " TEXT , " + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_PRICE + " REAL NOT NULL ," + COL_RETURN_PRODUCT_DISCOUNT + " REAL DEFAULT 0 ," + COL_RETURN_PRODUCT_UNITID + " REAL ," + COL_RETURN_PRODUCT_CONFACTORKG + " REAL ," + COL_RETURN_PRODUCT_UNIT + " TEXT NOT NULL ," + COL_PRODUCT_TOTAL + " REAL NOT NULL," + COL_RETURN_PRODUCT_QUANTITY + " INTEGER NOT NULL," +COL_PRODUCT_QNTY_BYUNIT + " INTEGER  ," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_PRODUCT_TAX_AMOUNT +" REAL NOT NULL ," + COL_RETURN_PRODUCT_ORDER_TYPE + " TEXT NOT NULL," + COL_RETURN_ORDER_TYPE_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_PEACE_PER_CART + " INTEGER ," + COL_RETURN_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_RETURN_PRODUCT_MRP + " REAL NOT NULL DEFAULT 0," + COL_RETURN_PRODUCT_RATEKG + " REAL  DEFAULT 0 , " + COL_RETURN_PRODUCT_RATEPCS + " REAL  DEFAULT 0 , "+ COL_RETURN_PRODUCT_SIZE_STRING +" TEXT , "+ COL_RETURN_PRODUCT_SIZEANDQTY_STRING +" TEXT , " + COL_RETURN_PRODUCT_HSNCODE +" TEXT ," + COL_FK_RETURN_ID + " INTEGER NOT NULL, FOREIGN KEY (" + COL_FK_RETURN_ID + ") REFERENCES " + TABLE_WO_RETURN_CUSTOMER + " (" + COL_ID + ") ON DELETE CASCADE);";

        String CREATE_BANK_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BANKS + "(" + COL_BANK_ID +" INTEGER NOT NULL ,"+ COL_BANK_NAME + " TEXT NOT NULL,"+ COL_SHOW_CONTRA + " TEXT NOT NULL )";
        String CREATE_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GROUPS + "(" + COL_GROUP_ID +" INTEGER NOT NULL ,"+ COL_GROUP_NAME + " TEXT NOT NULL)";

        String CREATE_SELLING_PRICE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SELLING_PRICE + "(" + COL_PRODUCT_ID +" INTEGER NOT NULL ,"+ COL_PRODUCT_CODE + " TEXT NOT NULL ,"+ COL_CUSTOMER_ID + " TEXT NOT NULL ,"+ COL_SELLING_PRICE + " REAL NOT NULL )";

        String CREATE_CUSTOMER_PRODUCTS = "CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMER_PRODUCTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_CUSTOMER_COST + " REAL NOT NULL DEFAULT 0 ," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMER + " (" + COL_CUSTOMER_ID + ") ON DELETE CASCADE);";

        String CREATE_CHEQUE_DETAILS = "CREATE TABLE " + TABLE_CHEQUE_DETAILS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_RECEIPT_NO + " TEXT NOT NULL UNIQUE ," + COL_RECEIVABLE_AMOUNT + " REAL NOT NULL ," + COL_CHEQUE_NUMBER + " TEXT NOT NULL , " + COL_UPLOAD_DATE + " TEXT  , " + COL_UPLOAD_STATUS +  " TEXT, " + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, " +COL_COMPANY_BANK + " INTEGER NOT NULL, " +COL_CUSTOMER_BANK + " TEXT NOT NULL, " +COL_RECEIVE_DATE + " TEXT NOT NULL, " +COL_CLEARING_DATE + " TEXT NOT NULL )";

        //String CREATE_RETURN_OFFLINE_CUSTOMER = "CREATE TABLE " + TABLE_RETURN_OFFLINE + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_INVOICE_NO + " TEXT NOT NULL ," + COL_CUSTOMER_ID + " TEXT NOT NULL ,"+ COL_UPLOAD_STATUS + " TEXT , "+ COL_SALE_TOTAL + " REAL NOT NULL," + COL_GRAND_TOTAL + "  REAL NOT NULL)";

        String CREATE_RETURN_OFFLINE_CUSTOMER = "CREATE TABLE " + TABLE_RETURN_OFFLINE + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_INVOICE_NO + " TEXT NOT NULL ," + COL_CUSTOMER_ID + " TEXT NOT NULL ,"+ COL_UPLOAD_STATUS + " TEXT , "+ COL_SALE_TOTAL + " REAL NOT NULL," + COL_GRAND_TOTAL + "  REAL NOT NULL ," + COL_SALE_LATITUDE +" TEXT ," + COL_SALE_LONGITUDE +" TEXT)";

        String CREATE_RETURN_OFFLINE_PRODUCTS = "CREATE TABLE " + TABLE_RETURN_OFFLINE_PRODUCTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_UNIT_PRICE + " REAL NOT NULL ," + COL_PRODUCT_UNITID + " REAL   ," + COL_PRODUCT_CONFACTORKG + " INTEGER ," + COL_PRODUCT_TAX + " TEXT," + COL_RETURN_PRODUCT_QUANTITY + " TEXT NOT NULL ," + COL_RETURN_AMOUNT + " REAL NOT NULL ," + COL_PRODUCT_BONUS + " REAL NOT NULL,"  + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL, FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_RETURN_OFFLINE + " (" + COL_ID + ") ON DELETE CASCADE);";

        String CREATE_EXPENSE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSE + "(" + COL_EXPENSE_ID +" TEXT NOT NULL ,"+ COL_EXPENSE_NAME + " TEXT NOT NULL )";

        String CREATE_EXPENSE_TABLE_DETAILS = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSE_DETAILS + "(" + COL_EXPENSE_ID +" TEXT NOT NULL ,"+ COL_EXPENSE_AMOUNT + " TEXT NOT NULL ,"+ COL_EXPENSE_RECEIPTNO + " TEXT,"+ COL_EXPENSE_REMARKS + " TEXT)";

        ////String CREATE_STOCK_MASTER = "CREATE TABLE " + TABLE_STOCK_MASTER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + COL_BARCODE +" TEXT," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_BONUS + " REAL NOT NULL ," + COL_PRODUCT_BRAND + " TEXT ," + COL_PRODUCT_UNITS + " TEXT ,"+ COL_PRODUCT_TYPE + " TEXT NOT NULL ," + COL_PRODUCT_MRP + " REAL NOT NULL ," + COL_PRODUCT_WHOLESALE + " REAL NOT NULL ," + COL_PRODUCT_COST + " REAL NOT NULL ," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL )";
        //String CREATE_STOCK_MASTER = "CREATE TABLE " + TABLE_STOCK_MASTER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + COL_BARCODE +" TEXT," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_BONUS + " REAL NOT NULL ," + COL_PRODUCT_BRAND + " TEXT ," + COL_PRODUCT_UNITS + " TEXT , " + COL_PRODUCT_SIZE + " TEXT ," + COL_PRODUCT_TYPE + " TEXT NOT NULL ," + COL_PRODUCT_MRP + " REAL NOT NULL ," + COL_PRODUCT_WHOLESALE + " REAL NOT NULL ," + COL_PRODUCT_COST + " REAL NOT NULL ," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL )";
        String CREATE_STOCK_MASTER = "CREATE TABLE " + TABLE_STOCK_MASTER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + COL_BARCODE +" TEXT," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_BONUS + " REAL NOT NULL ," + COL_PRODUCT_BRAND + " TEXT ," + COL_PRODUCT_UNITS + " TEXT , " + COL_PRODUCT_SIZE + " TEXT , " + COL_PRODUCT_MFGLIST + " TEXT , " + COL_PRODUCT_TYPE + " TEXT NOT NULL ," + COL_PRODUCT_MRP + " REAL NOT NULL ," + COL_PRODUCT_WHOLESALE + " REAL NOT NULL ," + COL_PRODUCT_COST + " REAL NOT NULL , " + COL_PRODUCT_RATE + " REAL ," + COL_PRODUCT_TAX + " REAL NOT NULL , " + COL_PRODUCT_SALE_UNIT + " INTEGER ," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL , " + COL_PRODUCT_HSNCODE + " TEXT , " + COL_PRODUCT_TAXLIST +" TEXT )";

        //String CREATE_STOCK_MASTER = "CREATE TABLE " + TABLE_STOCK_MASTER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + COL_BARCODE +" TEXT," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_BONUS + " REAL NOT NULL ," + COL_PRODUCT_BRAND + " TEXT ," + COL_PRODUCT_UNITS + " TEXT , " + COL_PRODUCT_SIZE + " TEXT , " + COL_PRODUCT_SIZE_MASTER + "TEXT ," + COL_PRODUCT_TYPE + " TEXT NOT NULL ," + COL_PRODUCT_MRP + " REAL NOT NULL ," + COL_PRODUCT_WHOLESALE + " REAL NOT NULL ," + COL_PRODUCT_COST + " REAL NOT NULL , " + COL_PRODUCT_RATE + " REAL ," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL , " + COL_PRODUCT_HSNCODE + " TEXT , " + COL_PRODUCT_TAXLIST +" TEXT )";

        String CREATE_STOCK_SIZE_AND_QUANTITY_MASTER = "CREATE TABLE IF NOT EXISTS " + TABLE_STOCK_SIZE_AND_QUANTITY_MASTER + "(" + COL_ID +" TEXT NOT NULL ,"+ COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE ,"+ COL_PRODUCT_NAME + " TEXT,"+ COL_STOCKSIZE + " TEXT , "+ COL_STOCK_QTY +" TEXT)";

        String CREATE_SALE_SIZE_AND_QUANTITY = "CREATE TABLE IF NOT EXISTS " + TABLE_SALE_SIZE_AND_QUANTITY + "(" + COL_ID +" TEXT NOT NULL ,"+ COL_PRODUCT_ID + " INTEGER NOT NULL UNIQUE ,"+ COL_PRODUCT_NAME + " TEXT,"+ COL_SALE_SIZE + " TEXT , "+ COL_SALE_QTY +" TEXT," + COL_FK_CUSTOMER_ID + " INTEGER NOT NULL , FOREIGN KEY (" + COL_FK_CUSTOMER_ID + ") REFERENCES " + TABLE_SALE_CUSTOMER + " (" + COL_ID + ") ON DELETE CASCADE);";

        String CREATE_STATE = "CREATE TABLE " + TABLE_STATE + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_STATE_ID+ " TEXT ," + COL_STATE + " TEXT );";
        String CREATE_DISTRICT = "CREATE TABLE " + TABLE_DISTRICT + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_DISTRICT_ID+ " TEXT ," + COL_DISTRICT + " TEXT );";

        String CREATE_SIZEMASTER = "CREATE TABLE " + TABLE_SIZE_MASTER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_SIZEMASTER_ID + " TEXT," + COL_SIZEMASTER_NAME + " TEXT );";

        String CREATE_VEHICLE_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_VEHICLES + "(" + COL_VEHICLE_ID + " INTEGER NOT NULL ," + COL_VEHICLE_NO + " TEXT NOT NULL)";
        String CREATE_STOCK_APPROVAL = "CREATE TABLE IF NOT EXISTS " + TABLE_STOCK_APPROVAL + "(" + COL_PRODUCT_ITEM_ID + " INTEGER NOT NULL ," + COL_PRODUCT_ID + " INTEGER NOT NULL  ," + COL_PRODUCT_TRANSFER_ID + " TEXT NOT NULL, " + COL_PRODUCT_REPORTING_UNIT + " TEXT ," + COL_PRODUCT_QUANTITY + " TEXT NOT NULL," + COL_PRODUCT_NAME + " TEXT NOT NULL," + COL_PRODUCT_CODE + " TEXT NOT NULL)";

        //String CREATE_BILLWISE_RECEIPTS = "CREATE TABLE " + TABLE_BILLWISE_RECEIPTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_BILLWISE_RECCEIPT_INVOICENO + " TEXT ," + COL_BILLWISE_RECCEIPT_CUSTOMERID +" TEXT, " + COL_BILLWISE_RECCEIPT_INVOICEAMNT + " TEXT , " + COL_BILLWISE_RECCEIPT_INVOICEDATE + " TEXT ," + COL_BILLWISE_RECCEIPT_DUEAMNT + " TEXT , " + COL_BILLWISE_RECCEIPT_SALEID + " TEXT , " + COL_BILLWISE_RECCEIPT_BILLDATE + " TEXT , " + COL_BILLWISE_RECCEIPT_CHEQUENO + " TEXT ," + COL_BILLWISE_RECCEIPT_CHEQUEBANK + " TEXT , " + COL_BILLWISE_RECCEIPT_CHEQUEDATE + " TEXT , " + COL_BILLWISE_RECCEIPT_MODEBANK_NAME + " TEXT , " + COL_BILLWISE_RECCEIPT_MODEBANK_REFERENCE + " TEXT , " + COL_BILLWISE_RECCEIPT_AMOUNTENTERED + " TEXT , " + COL_BILLWISE_RECCEIPT_INVOICEBALANCE + " TEXT , " + COL_BILLWISE_RECCEIPT_AMOUNT + " TEXT  ," + COL_BILLWISE_RECCEIPT_TOTALCASH + " TEXT," + COL_BILLWISE_RECCEIPT_REMARK + " TEXT )";
        String CREATE_BILLWISE_RECEIPTS = "CREATE TABLE " + TABLE_BILLWISE_RECEIPTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_BILLWISE_RECCEIPT_INVOICENO + " TEXT ," + COL_BILLWISE_RECCEIPT_CUSTOMERID +" TEXT, " + COL_BILLWISE_RECCEIPT_INVOICEAMNT + " TEXT , " + COL_BILLWISE_RECCEIPT_INVOICEDATE + " TEXT ," + COL_BILLWISE_RECCEIPT_DUEAMNT + " TEXT , " + COL_BILLWISE_RECCEIPT_SALEID + " TEXT , " + COL_BILLWISE_RECCEIPT_BILLDATE + " TEXT , " + COL_BILLWISE_RECCEIPT_CHEQUENO + " TEXT ," + COL_BILLWISE_RECCEIPT_CHEQUEBANK + " TEXT , " + COL_BILLWISE_RECCEIPT_CHEQUEDATE + " TEXT , " + COL_BILLWISE_RECCEIPT_MODEBANK_NAME + " TEXT , " + COL_BILLWISE_RECCEIPT_MODEBANK_REFERENCE + " TEXT , " + COL_BILLWISE_RECCEIPT_AMOUNTENTERED + " TEXT ," + COL_BILLWISE_RECCEIPT_DISCOUNT + " TEXT , " + COL_BILLWISE_RECCEIPT_INVOICEBALANCE + " TEXT , " + COL_BILLWISE_RECCEIPT_AMOUNT + " TEXT  ," + COL_BILLWISE_RECCEIPT_TOTALCASH + " TEXT," + COL_BILLWISE_RECCEIPT_REMARK + " TEXT )";
        String CREATE_BILLWISE_RECEIPTDETAILS = "CREATE TABLE " + TABLE_BILLWISE_RECEIPT_DETAILS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_BILLWISE_RECCEIPT_CUSTOMERID +" TEXT, " + COL_BILLWISE_RECCEIPT_DUEAMNT + " TEXT , " + COL_BILLWISE_RECCEIPT_BILLDATE + " TEXT ," + COL_RECEIPT_MODE + " TEXT , " + COL_BILLWISE_RECCEIPT_CHEQUENO + " TEXT ," + COL_BILLWISE_RECCEIPT_CHEQUEBANK + " TEXT , " + COL_BILLWISE_RECCEIPT_CHEQUEDATE + " TEXT , " + COL_BILLWISE_RECCEIPT_MODEBANK_NAME + " TEXT , " + COL_BILLWISE_RECCEIPT_MODEBANK_REFERENCE + " TEXT , " + COL_BILLWISE_RECCEIPT_AMOUNTENTERED + " TEXT , " + COL_BILLWISE_RECCEIPT_INVOICEBALANCE + " TEXT ," + COL_BILLWISE_RECCEIPT_TOTALCASH + " TEXT," + COL_BILLWISE_RECCEIPT_REMARK + " TEXT )";
        String CREATE_BILLWISE_RECEIPTDETAILS_LIST = "CREATE TABLE " + TABLE_BILLWISE_RECEIPT_DETAILS_LIST + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  , " + COL_BILLWISE_RECCEIPT_AMOUNTENTERED + " TEXT ," + COL_BILLWISE_RECCEIPT_INVOICENO + " TEXT ," + COL_FK_BILLWISE_ID + " INTEGER NOT NULL , " + COL_BILLWISE_RECCEIPT_REMARK + " TEXT,FOREIGN KEY (" + COL_FK_BILLWISE_ID + ") REFERENCES " + TABLE_BILLWISE_RECEIPT_DETAILS + " (" + COL_ID + ") ON DELETE CASCADE);";

        String CREATE_ROUTE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTE + "(" + COL_ROUTE_ID + " INTEGER NOT NULL ," + COL_ROUTE + " TEXT NOT NULL)";

        db.execSQL(CREATE_STOCK);
        db.execSQL(CREATE_STOCK_MASTER);
        db.execSQL(CREATE_CUSTOMER);
        db.execSQL(CREATE_CUSTOMER_STATUS);
        db.execSQL(CREATE_TRANSACTIONS);

        db.execSQL(CREATE_SALE_CUSTOMER);
        db.execSQL(CREATE_SALE_PRODUCTS);

        db.execSQL(CREATE_QUOTATION_CUSTOMER);
        db.execSQL(CREATE_QUOTATION_PRODUCTS);
        db.execSQL(CREATE_QUOTATION_PRODUCTSTEMP);
        db.execSQL(CREATE_QUOTATION_PRODUCTSTEMP_EDIT);

        db.execSQL(CREATE_WO_RETURN_CUSTOMER);
        db.execSQL(CREATE_WO_RETURN_PRODUCTS);
        db.execSQL(CREATE_BANK_DETAILS_TABLE);
        db.execSQL(CREATE_SELLING_PRICE_TABLE);
        db.execSQL(CREATE_CUSTOMER_PRODUCTS);
        db.execSQL(CREATE_CHEQUE_DETAILS);
        db.execSQL(CREATE_RETURN_OFFLINE_CUSTOMER);
        db.execSQL(CREATE_RETURN_OFFLINE_PRODUCTS);
        db.execSQL(CREATE_EXPENSE_TABLE);
        db.execSQL(CREATE_EXPENSE_TABLE_DETAILS);
        db.execSQL(CREATE_ROUTE_TABLE);

        //haris added on 22-10-2020
        db.execSQL(CREATE_STOCK_SIZE_AND_QUANTITY_MASTER);
        db.execSQL(CREATE_SALE_SIZE_AND_QUANTITY);
        /////
        //HARIS ADDED ON 10-12-2020

        db.execSQL(CREATE_STATE);
        db.execSQL(CREATE_DISTRICT);

        //HARIS ADDDED ON 02-02-2021
        db.execSQL(CREATE_SIZEMASTER);

        //haris added on 24-08-21
        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_VEHICLE_DETAILS_TABLE);
        db.execSQL(CREATE_STOCK_APPROVAL);
        db.execSQL(CREATE_BILLWISE_RECEIPTS);
        db.execSQL(CREATE_BILLWISE_RECEIPTDETAILS);
        db.execSQL(CREATE_BILLWISE_RECEIPTDETAILS_LIST);





    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE  " + TABLE_QUOTATION_PRODUCTS + " ADD COLUMN " + COL_PRODUCT_TAX + " REAL NOT NULL DEFAULT 0");
        }
        if (oldVersion < 3) {

            String CREATE_WO_RETURN_CUSTOMER = "CREATE TABLE IF NOT EXISTS " + TABLE_WO_RETURN_CUSTOMER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_CUSTOMER_ID + " INTEGER NOT NULL ,"+COL_RETURN_CODE +" TEXT NOT NULL UNIQUE," + COL_RETURN_TYPE + " TEXT NOT NULL ," + COL_RETURN_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_TAX_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_RETURN_REMARKS + "  TEXT DEFAULT ''," + COL_CREATED_AT + "  DATETIME DEFAULT CURRENT_TIMESTAMP);";
            String CREATE_WO_RETURN_PRODUCTS = "CREATE TABLE IF NOT EXISTS " + TABLE_WO_RETURN_PRODUCTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PRODUCT_ID + " INTEGER NOT NULL ," + COL_PRODUCT_NAME + " TEXT NOT NULL ," + COL_PRODUCT_ARABIC_NAME + " TEXT," + COL_PRODUCT_CODE + " TEXT NOT NULL ," + COL_PRODUCT_PRICE + " REAL NOT NULL ," + COL_RETURN_PRODUCT_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_TAX + " REAL NOT NULL ," + COL_RETURN_PRODUCT_ORDER_TYPE + " TEXT NOT NULL," + COL_RETURN_ORDER_TYPE_QUANTITY + " INTEGER NOT NULL," + COL_PRODUCT_PEACE_PER_CART + " INTEGER NOT NULL," + COL_RETURN_TOTAL + " REAL NOT NULL DEFAULT 0," + COL_FK_RETURN_ID + " INTEGER NOT NULL, FOREIGN KEY (" + COL_FK_RETURN_ID + ") REFERENCES " + TABLE_WO_RETURN_CUSTOMER + " (" + COL_ID + ") ON DELETE CASCADE);";

            db.execSQL(CREATE_WO_RETURN_CUSTOMER);
            db.execSQL(CREATE_WO_RETURN_PRODUCTS);

        } else {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_MASTER);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_VISIT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIPT);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALE_CUSTOMER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALE_PRODUCTS);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTATION_CUSTOMER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTATION_PRODUCTS);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WO_RETURN_CUSTOMER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WO_RETURN_PRODUCTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_PRODUCTS);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RETURN_OFFLINE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RETURN_OFFLINE_PRODUCTS);

            onCreate(db);

        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        final String FK_ON = "=ON;";
        try {
            if (!db.isReadOnly()) {
                // Enable foreign key constraints

                db.execSQL("PRAGMA foreign_keys" + FK_ON);
//                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        } catch (SQLiteException e) {

            e.getMessage();
        }
    }

    /**
     * stock functions
     */
    //    insert stock
    public boolean insertStock(Product product) {

        boolean isExist = isExistProduct(product.getProductId());  // check stock in table

        if (isExist)
            return false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Log.e("tax db",""+product.getTax());
            Log.e("retal db",""+product.getRetailPrice());
            Log.e("wholesale db",""+product.getWholeSalePrice());
            Log.e("getMfglist db",""+product.getMfglist());
            Log.e("rprtng price db",""+product.getProduct_reporting_Price());
            Log.e("rprtng unit db",""+product.getProduct_reporting_Unit());
            Log.e("salunitid van",""+product.getSale_unitid());
            Log.e("getP_name van",""+product.getP_name());


            ContentValues values = new ContentValues();
            values.put(COL_PRODUCT_ID, product.getProductId());
            values.put(COL_BARCODE, product.getBarcode());
            values.put(COL_PRODUCT_NAME, product.getProductName());
            values.put(COL_PRODUCT_ARABIC_NAME, product.getArabicName());
            values.put(COL_PRODUCT_CODE, product.getProductCode());
            values.put(COL_PRODUCT_BONUS, product.getProductBonus());
            values.put(COL_PRODUCT_BRAND, product.getBrandName());
            values.put(COL_P_NAME, product.getP_name());

            values.put(COL_PRODUCT_UNITS, product.getUnitslist());

            values.put(COL_PRODUCT_TYPE, product.getProductType());
            values.put(COL_PRODUCT_MRP, product.getRetailPrice());
            values.put(COL_PRODUCT_WHOLESALE, product.getWholeSalePrice());
            values.put(COL_PRODUCT_COST, product.getCost());
            values.put(COL_PRODUCT_TAX, product.getTax());
            values.put(COL_PRODUCT_PEACE_PER_CART, product.getPiecepercart());
            values.put(COL_PRODUCT_QUANTITY, product.getStockQuantity());

            //haris added on 26-10-2020
            values.put(COL_PRODUCT_SIZE, product.getSizelist());
            ////
            //haris added on 23-11-2020
            values.put(COL_PRODUCT_RATE, product.getProduct_rate());

            //haris added on 03-11-2020
            values.put(COL_PRODUCT_HSNCODE, product.getProduct_hsncode());
            values.put(COL_PRODUCT_TAXLIST, product.getTaxlist());
            //haris added on 24-02-2022
            values.put(COL_PRODUCT_REPORTING_UNIT, product.getProduct_reporting_Unit());
            values.put(COL_PRODUCT_REPORTING_PRICE, product.getProduct_reporting_Price());
            values.put(COL_PRODUCT_MFGLIST,product.getMfglist());
            values.put(COL_PRODUCT_SALE_UNIT,product.getSale_unitid());




            // Inserting Row
            long l = db.insert(TABLE_STOCK, null, values);

            db.close(); // Closing database connection

            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertStock  Exception  " + e.getMessage());
            return false;
        }
    }

    //    insert banks
    public boolean insertBanksName(Banks bank) {

        boolean isExist = isExistProduct(bank.getBank_id()); // check stock in table

        if (isExist)
            return false;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_BANK_ID, bank.getBank_id());
            values.put(COL_BANK_NAME, bank.getBank_name());
            values.put(COL_SHOW_CONTRA, bank.getShown_in_contra());

            long l = db.insert(TABLE_BANKS, null, values);
            db.close(); // Closing database connection
            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertStock  Exception  " + e.getMessage());
            return false;
        }
    }


    // INSERT EXPENSE

    public void insertExpense(Expense expense){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_EXPENSE_ID, expense.getId());
            values.put(COL_EXPENSE_NAME, expense.getName());

            long l = db.insert(TABLE_EXPENSE, null, values);
            db.close(); // Closing database connection

        } catch (SQLiteException e) {
            Log.e(TAG, "expense  Exception  " + e.getMessage());

        }
    }



    // INSERT EXPENSE DETAILS

    public void insertExpenseDetails(Expense expense){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(COL_EXPENSE_ID, expense.getId());
            values.put(COL_EXPENSE_AMOUNT, expense.getAmount());
            values.put(COL_EXPENSE_RECEIPTNO, expense.getReceiptNo());
            values.put(COL_EXPENSE_REMARKS, expense.getRemarks());

            long l = db.insert(TABLE_EXPENSE_DETAILS, null, values);
            db.close(); // Closing database connection

        } catch (SQLiteException e) {
            Log.e(TAG, "expense  Exception  " + e.getMessage());

        }
    }

    //   Get product_size from customer product table
    //haris added on 28-10-2020
    public ArrayList<CartItem> getProduct_sizelist(int productcode) {
        String st_sizelist="";
        final ArrayList<CartItem> cartItems = new ArrayList<>();
        try {




            String sql = "SELECT * FROM " + TABLE_STOCK + " WHERE "  + COL_PRODUCT_CODE + WHERE_CLAUSE;
            //String sql = "SELECT * FROM " + TABLE_STOCK + " WHERE "  + COL_PRODUCT_CODE + WHERE_CLAUSE + " ORDER BY " + COL_INVOICE_CODE + " DESC LIMIT 1 ";
            //String sql = "SELECT " + COL_INVOICE_CODE + " FROM " + TABLE_SALE_CUSTOMER + " ORDER BY " + COL_INVOICE_CODE + " DESC LIMIT 1 ";

            //String sql = "SELECT * FROM " + TABLE_STOCK ;

            SQLiteDatabase db = this.getReadableDatabase();
//          Cursor cursor = db.rawQuery(sql, null);

            Cursor cursor = db.rawQuery(sql, new String[]{ String.valueOf(productcode)});
            //Cursor cursor = db.rawQuery(sql,null);


            if (cursor.moveToFirst())
                do {

                    CartItem c = new CartItem();
                    //st_sizelist =cursor.getString(cursor.getColumnIndex(COL_PRODUCT_SIZE));
                    c.setSizelist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_SIZE)));
                    c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    c.setProduct_hsncode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_HSNCODE)));
                    c.setTaxlist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TAXLIST)));
                    Log.e("sizelist",""+cursor.getColumnIndex(COL_PRODUCT_SIZE));
//                    Log.e("prodid",""+cursor.getColumnIndex(COL_PRODUCT_ID));

                    cartItems.add(c);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();



        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {
            Log.v(TAG, "getCustomerProductPrice  Exception  " + e.getMessage());
        }

        //eturn st_sizelist;
        return cartItems;

    }

    // get all expense
    public ArrayList<Expense> getAllExpense() {

        final ArrayList<Expense> expensearray = new ArrayList<>();
        try {
            // String sql = "SELECT * FROM " + TABLE_EXPENSE;
            String sql = "SELECT distinct expense_id ,expense_name FROM " + TABLE_EXPENSE;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    Expense e = new Expense();

                    e.setId(cursor.getString(cursor.getColumnIndex(COL_EXPENSE_ID)));
                    e.setName(cursor.getString(cursor.getColumnIndex(COL_EXPENSE_NAME)));

                    expensearray.add(e);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "Expense  Exception  " + e.getMessage());
        }

        return expensearray;
    }


    // get all expense DETAILS
    public ArrayList<Expense> getAllExpenseDetails() {

        final ArrayList<Expense> expensearray = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_EXPENSE_DETAILS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    Expense e = new Expense();

                    e.setId(cursor.getString(cursor.getColumnIndex(COL_EXPENSE_ID)));
                    e.setAmount(cursor.getFloat(cursor.getColumnIndex(COL_EXPENSE_AMOUNT)));
                    e.setReceiptNo(cursor.getString(cursor.getColumnIndex(COL_EXPENSE_RECEIPTNO)));
                    e.setRemarks(cursor.getString(cursor.getColumnIndex(COL_EXPENSE_REMARKS)));

                    expensearray.add(e);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "Expense details Exception  " + e.getMessage());
        }

        return expensearray;
    }



    // get all banks
    public ArrayList<Banks> getAllBanks() {

        final ArrayList<Banks> banksarray = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_BANKS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    Banks b = new Banks();

                    b.setBank_name(cursor.getString(cursor.getColumnIndex(COL_BANK_NAME)));
                    b.setBank_id(cursor.getInt(cursor.getColumnIndex(COL_BANK_ID)));
                    b.setShown_in_contra(cursor.getString(cursor.getColumnIndex(COL_SHOW_CONTRA)));

                    //   Log.e("getBanks  Exceptio" , ""+cursor.getString(cursor.getColumnIndex(COL_BANK_NAME)));
                    banksarray.add(b);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "getBanks  Exception  " + e.getMessage());
        }

        return banksarray;
    }

    // get all banks
    public ArrayList<RouteGroup> getAllGroups() {

        final ArrayList<RouteGroup> groupsarray = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_GROUPS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    RouteGroup g = new RouteGroup();

                    g.setGroupName(cursor.getString(cursor.getColumnIndex(COL_GROUP_NAME)));
                    g.setGroupId(cursor.getInt(cursor.getColumnIndex(COL_GROUP_ID)));

                    groupsarray.add(g);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "getGroups  Exception  " + e.getMessage());
        }

        return groupsarray;
    }


    /*Insert customer product*/
    private void insertCustomerProducts(ArrayList<CustomerProduct> list) {

        if (list == null || list.isEmpty())
            return;

        long l = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            for (CustomerProduct cProduct : list) {

                ContentValues values = new ContentValues();
                values.put(COL_PRODUCT_ID, cProduct.getProductId());
                values.put(COL_CUSTOMER_COST, cProduct.getPrice());
                values.put(COL_FK_CUSTOMER_ID, cProduct.getCustomerId());

                l = db.insert(TABLE_CUSTOMER_PRODUCTS, null, values);

            }
            db.close(); // Closing database connection

        } catch (SQLiteException e) {
            printLog(TAG, "insertCustomerProducts  Exception  " + e.getMessage());
        }
    }


    //   Get customer product balance from customer product table
    public double getCustomerProductPrice(int customerId, int productId) {
        Log.e("reachedddd","ok");

        double price = 0;

        try {

//          String sql = "SELECT " + COL_CUSTOMER_COST + " FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + String.valueOf(customerId) + "'";
            String sql = "SELECT " + COL_CUSTOMER_COST + " FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + WHERE_CLAUSE + " AND " + COL_PRODUCT_ID + WHERE_CLAUSE;
            //String sql = "SELECT " + COL_PRODUCT_MRP + " FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + WHERE_CLAUSE + " AND " + COL_PRODUCT_ID + WHERE_CLAUSE;

            SQLiteDatabase db = this.getReadableDatabase();
//          Cursor cursor = db.rawQuery(sql, null);
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(customerId), String.valueOf(productId)});

            if (cursor != null) {
                cursor.moveToFirst();
                price = cursor.getDouble(cursor.getColumnIndex(COL_CUSTOMER_COST));
                cursor.close();

            }
        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {
            Log.v(TAG, "getCustomerProductPrice  Exception  " + e.getMessage());
        }

        Log.e("price db",""+price);
        return price;
    }

    //   Get customer product balance from customer product table for quotation
    //haris added on 31-10-2020
    public String getCustomerProductsizelist_string_quotaion(int customerid, int productId) {
        Log.e("customerid db",""+customerid);
        Log.e("productId db",""+productId);

        String st_sizelist = "";
        try {


//          String sql = "SELECT " + COL_CUSTOMER_COST + " FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + String.valueOf(customerId) + "'";
            String sql = "SELECT " + COL_PRODUCT_SIZE + " FROM " + TABLE_STOCK_MASTER + " WHERE "  + COL_PRODUCT_ID + WHERE_CLAUSE;

            SQLiteDatabase db = this.getReadableDatabase();
//          Cursor cursor = db.rawQuery(sql, null);
            Cursor cursor = db.rawQuery(sql, new String[]{ String.valueOf(productId)});


            if (cursor.moveToFirst())
                do {


                    st_sizelist=cursor.getString(cursor.getColumnIndex(COL_PRODUCT_SIZE));



                } while (cursor.moveToNext());

            cursor.close();
            db.close();



        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {
            Log.v(TAG, "getCustomerProductPrice  Exception  " + e.getMessage());
        }

        return st_sizelist;

    }

    //haris added on 02-02-2021
    public String getCustomerProductsizelistmaster_string_quotaion() {

        String st_sizelist = "";
        ArrayList<SizelistMasterstock>arr_sizemstr = new ArrayList<>();
        try {


//          String sql = "SELECT " + COL_CUSTOMER_COST + " FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + String.valueOf(customerId) + "'";
            String sql = "SELECT * FROM " + TABLE_SIZE_MASTER ;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            //Cursor cursor = db.rawQuery(sql, new String[]{ String.valueOf(productId)});


            if (cursor.moveToFirst())
                do {

                    SizelistMasterstock s = new SizelistMasterstock();
                    s.setSize_id(cursor.getInt(cursor.getColumnIndex(COL_SIZEMASTER_ID)));
                    s.setSize(cursor.getString(cursor.getColumnIndex(COL_SIZEMASTER_NAME)));
                    arr_sizemstr.add(s);




                } while (cursor.moveToNext());


            cursor.close();
            st_sizelist = new Gson().toJson(arr_sizemstr);
            db.close();



        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {
            Log.v(TAG, "getCustomerProductPrice  Exception  " + e.getMessage());
        }

        return st_sizelist;

    }



    //   Get customer product balance from customer product table
    //haris added on 31-10-2020
    public String getCustomerProductsizelist_string(int customerid, int productId) {
        Log.e("customerid db",""+customerid);
        Log.e("productId db",""+productId);

        String st_sizelist = "";
        try {


//          String sql = "SELECT " + COL_CUSTOMER_COST + " FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + String.valueOf(customerId) + "'";
            String sql = "SELECT " + COL_PRODUCT_SIZE + " FROM " + TABLE_STOCK + " WHERE "  + COL_PRODUCT_ID + WHERE_CLAUSE;

            SQLiteDatabase db = this.getReadableDatabase();
//          Cursor cursor = db.rawQuery(sql, null);
            Cursor cursor = db.rawQuery(sql, new String[]{ String.valueOf(productId)});


            if (cursor.moveToFirst())
                do {


                    st_sizelist=cursor.getString(cursor.getColumnIndex(COL_PRODUCT_SIZE));



                } while (cursor.moveToNext());

            cursor.close();
            db.close();



        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {
            Log.v(TAG, "getCustomerProductPrice  Exception  " + e.getMessage());
        }

        return st_sizelist;

    }

    //   Get customer product balance from customer product table
    public ArrayList getCustomerProductsizelist(int customerId, int productId) {

        final ArrayList<CartItem> cartItems = new ArrayList<>();
        try {


//          String sql = "SELECT " + COL_CUSTOMER_COST + " FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + String.valueOf(customerId) + "'";
            String sql = "SELECT " + COL_PRODUCT_SIZE + " FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + WHERE_CLAUSE + " AND " + COL_PRODUCT_ID + WHERE_CLAUSE;

            SQLiteDatabase db = this.getReadableDatabase();
//          Cursor cursor = db.rawQuery(sql, null);
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(customerId), String.valueOf(productId)});


            if (cursor.moveToFirst())
                do {

                    CartItem c = new CartItem();
                    c.setSizelist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_SIZE)));

                    cartItems.add(c);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();



        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {
            Log.v(TAG, "getCustomerProductPrice  Exception  " + e.getMessage());
        }

        return cartItems;

    }


    public boolean isExistCustomerProduct(int customerId, int productId) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER_PRODUCTS + " WHERE " + COL_PRODUCT_ID + WHERE_CLAUSE + " AND " + COL_FK_CUSTOMER_ID + WHERE_CLAUSE, new String[]{String.valueOf(productId), String.valueOf(customerId)});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            printLog(TAG, "isExistCustomerProduct Exception  " + e.getMessage());
        }

        printLog(TAG, "isExistCustomerProduct count  " + count);
        return count != 0;

    }


    //    update updateStock
    public boolean updateStock(CartItem cartItem, int type) {
        Log.e("typeeeeeeeeeeeee ",""+type);
        Log.e("qnty in",""+cartItem.getPieceQuantity());
        Log.e("qnty ret",""+cartItem.getReturnQuantity());

        boolean isExist = isExistProduct(cartItem.getProductId());  // check stock in table

        if (!isExist) {
            cartItem.setStockQuantity((int) cartItem.getReturnQuantity());
            insertStock(cartItem);
            return false;
        }

        Product p = getStockProduct(cartItem.getProductId());

        if (p == null)
            return false;

        float qty = p.getStockQuantity();
        Log.e("getStockQuantity ",""+p.getStockQuantity());
        //haris added on 29-10-2020
        String st_sizeqty = cartItem.getSizelist();
        ////

        switch (type) {
            case REQ_SALE_TYPE:
                //qty = p.getStockQuantity() - cartItem.getPieceQuantity();
                qty = p.getStockQuantity() -(cartItem.getPieceQuantity() + cartItem.getFreeqnty_piece());
                break;
            case REQ_RETURN_TYPE:
                qty = p.getStockQuantity() + cartItem.getReturnQuantity();
                break;
            case REQ_EDIT_TYPE:
                qty = p.getStockQuantity() + cartItem.getPieceQuantity();
                break;

        }

        Log.v(TAG, "updateStock  getStockQuantity  " + p.getStockQuantity() + "\t" + cartItem.getStockQuantity() + "\t" + cartItem.getReturnQuantity());

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_PRODUCT_QUANTITY, qty);
            //haris added on 29-10-2020
            values.put(COL_PRODUCT_SIZE,st_sizeqty);
            Log.e("st_sizeqty db",""+qty);
            Log.e("qty db",""+st_sizeqty);
            ///


            int result = db.update(TABLE_STOCK, values, COL_PRODUCT_ID + " =?", new String[]{String.valueOf(cartItem.getProductId())});

            db.close();

            return result != -1;
        } catch (SQLiteException e) {
            Log.v(TAG, "updateStock  Exception  " + e.getMessage());
            return false;
        }
    }


    //    update updateStock
    public boolean updateStockwhiledeletecart(int prod_id, int qnty ,CartItem cartItem) {
        Log.e("qnty ",""+qnty);
        Log.e("prod_id",""+prod_id);


        boolean isExist = isExistProduct(prod_id);  // check stock in table

        if (!isExist) {
            cartItem.setStockQuantity((int) cartItem.getTypeQuantity());
            insertStock(cartItem);
            return false;
        }

        Product p = getStockProduct(prod_id);

        if (p == null)
            return false;

        float qty = p.getStockQuantity();
        qty = p.getStockQuantity() + qnty;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_PRODUCT_QUANTITY, qty);

            int result = db.update(TABLE_STOCK, values, COL_PRODUCT_ID + " =?", new String[]{String.valueOf(prod_id)});

            db.close();

            return result != -1;
        } catch (SQLiteException e) {
            Log.v(TAG, "updateStock  Exception  " + e.getMessage());
            return false;
        }
    }


    // update status Y if uploaded to server
    public boolean UpdateSalesUploadStatus(String inv_code){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_UPLOAD_STATUS, "Y");

            int result = db.update(TABLE_SALE_CUSTOMER, values, COL_INVOICE_CODE + " =?", new String[]{String.valueOf(inv_code)});

            db.close();


            return true;

        }catch (SQLiteException e){

            return false;
        }

    }



    // update Quotation Y if uploaded to server
    public boolean UpdateQuotationUploadStatus(String inv_code){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_UPLOAD_STATUS, "Y");

            int result = db.update(TABLE_QUOTATION_CUSTOMER, values, COL_ID + " =?", new String[]{String.valueOf(inv_code)});

            db.close();

            return true;

        }catch (SQLiteException e){

            return false;
        }
    }

    // update status Y if uploaded to server
    public boolean UpdateWoInvoiceUploadStatus(String col_id){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_UPLOAD_STATUS, "Y");

            int result = db.update(TABLE_WO_RETURN_CUSTOMER, values, COL_RETURN_CODE + " =?", new String[]{String.valueOf(col_id)});

            db.close();

            return true;

        }catch (SQLiteException e){

            return false;
        }

    }



    // update status Y if uploaded to server
    public boolean UpdateInvoiceUploadStatus(String inv_code){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_UPLOAD_STATUS, "Y");

            int result = db.update(TABLE_RETURN_OFFLINE, values, COL_INVOICE_NO + " =?", new String[]{String.valueOf(inv_code)});

            db.close();

            return true;

        }catch (SQLiteException e){

            return false;
        }

    }


    // update status Y if uploaded to server
    public boolean UpdatePaidReceiptUploadStatus(String inv_code){
        Log.e("inside stat","ok");
        Log.e("inv_code stat","ok"+inv_code);

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_UPLOAD_STATUS, "Y");

            int result = db.update(TABLE_RECEIPT, values, COL_RECEIPT_NO + " =?", new String[]{String.valueOf(inv_code)});

            db.close();

            return true;

        }catch (SQLiteException e){

            return false;
        }
    }


    // update status Y if uploaded to server
    public boolean UpdateChequeReceiptUploadStatus(String inv_code){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_UPLOAD_STATUS, "Y");

            int result = db.update(TABLE_CHEQUE_DETAILS, values, COL_RECEIPT_NO + " =?", new String[]{String.valueOf(inv_code)});

            db.close();

            return true;

        }catch (SQLiteException e){

            return false;
        }
    }

    //    get GETSTATE list haris on 10-12-2020
    public ArrayList<State> getAllState() {

        final ArrayList<State> stateitems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_STATE;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    State s = new State();
                    s.setStateid(cursor.getInt(cursor.getColumnIndex(COL_STATE_ID)));
                    s.setName(cursor.getString(cursor.getColumnIndex(COL_STATE)));
                    Log.e("COL_STATE",""+cursor.getString(cursor.getColumnIndex(COL_STATE)));


                    stateitems.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.e(TAG, "getAllState  Exception  " + e.getMessage());
        }

        return stateitems;
    }

    //    get GETdistrict list haris on 10-12-2020
    public ArrayList<District> getAllDistricts() {

        final ArrayList<District> distritems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_DISTRICT;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    District d = new District();
                    d.setDistrictId(cursor.getInt(cursor.getColumnIndex(COL_DISTRICT_ID)));
                    d.setDistrict(cursor.getString(cursor.getColumnIndex(COL_DISTRICT)));


                    distritems.add(d);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.e(TAG, "getAllDistrict  Exception  " + e.getMessage());
        }

        return distritems;
    }

    //    get getAllStock list
    public ArrayList<CartItem> getAllStock() {

        final ArrayList<CartItem> cartItems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_STOCK;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    CartItem c = new CartItem();
                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setBarcode(cursor.getString(cursor.getColumnIndex(COL_BARCODE)));
                    c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    c.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                    c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_BONUS)));
                    c.setBrandName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_BRAND)));
                    c.setUnitslist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITS)));
                    c.setProductType(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TYPE)));
                    //haris added on 120321
                    // c.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                    //haris commeneted on 16-12-2021
                    //  c.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_RATE)));
                    c.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                    ///
                    c.setWholeSalePrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_WHOLESALE)));
                    c.setCost(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_COST)));
                    c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));

                    c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));

                    c.setStockQuantity(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY)));
                    //haris added on 26-10-2020
                    c.setSizelist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_SIZE)));
                    ///
                    //haris added on 03-11-2020
                    c.setProduct_hsncode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_HSNCODE)));
                    c.setTaxlist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TAXLIST)));
                    ///
                    //haris added on 20-04-21
                    c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));

                    //haris added on 24-02-22
                    c.setProduct_reporting_Unit(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_REPORTING_UNIT)));
                    c.setProduct_reporting_Price(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_REPORTING_PRICE)));
                    c.setMfglist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_MFGLIST)));
                    c.setSale_unitid(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_SALE_UNIT)));
                    c.setP_name(cursor.getString(cursor.getColumnIndex(COL_P_NAME)));


                    Log.e("tax dbb",""+c.getProduct_reporting_Unit());
//                    Log.e("namee dbb",""+cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
//                    Log.e("wholesale dbb",""+cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_WHOLESALE)));
//                    Log.e("reporting unit dbb",""+cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_REPORTING_UNIT)));
//                    if(c.getProductName().trim().equals("PRODUCT 2")) {
//                        Log.e("stock qnty HRRR", "" + c.getStockQuantity());
                    //   Log.e("unitlist qnty HRRR", "" + c.getMfglist());
//                    }
                    if(c.getStockQuantity()>0) {
                        cartItems.add(c);
                    }



                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.v(TAG, "getAllStock  Exception  " + e.getMessage());
        }

        return cartItems;
    }

    public ArrayList<DailyProductReport> getDailyProductReport() {

        ArrayList<DailyProductReport> list = new ArrayList<>();

        try {

            String sql = "SELECT sp.product_id_int,sp.product_name_txt,sum(sp.sale_piece_quantity_int) as sale_qty \n" +
                    "FROM tbl_sales_products sp \n" +
                    "GROUP BY sp.product_name_txt" ;

            printLog("frstsql",sql);

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    DailyProductReport s = new DailyProductReport();

                    s.setProduct(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));

                    int saleQty=cursor.getInt(cursor.getColumnIndex("sale_qty"));
                    s.setSaleQty(saleQty);

                    int prodID=cursor.getInt(cursor.getColumnIndex("product_id_int"));
                    String sqlRetunQty = "SELECT rp.product_id_int,\n" +
                            "    rp.product_name_txt,\n" +
                            "    SUM(rp.return_piece_quantity_int) as qty \n" +
                            "\tfrom tbl_wo_return_products rp\n" +
                            "     WHERE rp.product_id_int="+prodID+"\n" +
                            "     GROUP BY rp.product_id_int";



                    Log.e("sqlRetunQty",sqlRetunQty);
                    Cursor cursorReturnQty = db.rawQuery(sqlRetunQty, null);

                    cursorReturnQty.moveToFirst();
                    int returnQty=0;
                    if(cursorReturnQty.getCount()==1){
                        returnQty=cursorReturnQty.getInt(cursorReturnQty.getColumnIndex("qty"));

                    }

                    s.setReturnQty(String.valueOf(returnQty));

                    double salePercentage=(intToDouble(saleQty-returnQty) / intToDouble(saleQty))*100.0;
                    double returnPercentage=(intToDouble(returnQty) / intToDouble(saleQty))*100.0;

                    s.setSalePercentage(String.valueOf(new DecimalFormat("##.##").format(salePercentage)));
                    s.setReturnPercentage(String.valueOf(new DecimalFormat("##.##").format(returnPercentage)));


                    list.add(s);


                } while (cursor.moveToNext());

            String sqlWoReturn="SELECT t1.product_name_txt,t1.qty FROM \n" +
                    "(SELECT rp.product_id_int,rp.product_name_txt,SUM(rp.return_piece_quantity_int) as qty \n" +
                    "from tbl_wo_return_products rp \n" +
                    "GROUP BY rp.product_id_int) t1 LEFT JOIN \n" +
                    "(SELECT sp.product_id_int,sp.product_name_txt,sum(sp.sale_piece_quantity_int) as sale_qty \n" +
                    "FROM tbl_sales_products sp \n" +
                    "GROUP BY sp.product_name_txt) t2 \n" +
                    "ON t1.product_id_int=t2.product_id_int \n" +
                    "WHERE t2.product_id_int IS NULL";

            printLog("sqlWoReturn",sqlWoReturn);


            Cursor cursorWoReturn = db.rawQuery(sqlWoReturn, null);

            if(cursorWoReturn.moveToFirst())
                do {
                    DailyProductReport s = new DailyProductReport();
                    s.setProduct(cursorWoReturn.getString(cursorWoReturn.getColumnIndex(COL_PRODUCT_NAME)));
                    s.setSaleQty(0);
                    s.setReturnQty(String.valueOf(cursorWoReturn.getInt(cursorWoReturn.getColumnIndex("qty"))));
                    s.setSalePercentage(String.valueOf(new DecimalFormat("##.##").format(0.0)));
                    s.setReturnPercentage(String.valueOf(new DecimalFormat("##.##").format(100.0)));

                    list.add(s);

                }while (cursorWoReturn.moveToNext());



            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getCustomerCreditedAmount  Exception  " + e.getMessage());

        }

        return list;
    }

    public ArrayList<DailyProductReport> getDailyCustomerProductReport() {

        ArrayList<DailyProductReport> list = new ArrayList<>();

        try {

            String sql = "SELECT sc.customer_name_txt,sc.customer_id_int,\n" +
                    "sp.product_id_int,\n" +
                    "sp.product_name_txt,\n" +
                    "sum(sp.sale_piece_quantity_int) as sale_qty,\n" +
                    "sc.payment_type_txt\n" +
                    " FROM tbl_sales_products sp\n" +
                    " JOIN tbl_sales_customer sc\n" +
                    " ON sp.fk_customer_id_int=sc.id_int \n" +
                    " GROUP BY sp.product_name_txt,sc.customer_name_txt" ;

            printLog("frstsql",sql);

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    DailyProductReport s = new DailyProductReport();

                    s.setShop(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                    s.setProduct(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    s.setModeOfPay(cursor.getString(cursor.getColumnIndex("payment_type_txt")));

                    int saleQty=cursor.getInt(cursor.getColumnIndex("sale_qty"));
                    int custID=cursor.getInt(cursor.getColumnIndex("customer_id_int"));
                    s.setSaleQty(saleQty);

                    int prodID=cursor.getInt(cursor.getColumnIndex("product_id_int"));
                    String sqlRetunQty = "SELECT rp.product_id_int,\n" +
                            "rp.product_name_txt,\n" +
                            "SUM(rp.return_piece_quantity_int) as qty,\n" +
                            "rc.customer_id_int,\n" +
                            "rc.customer_name_txt\n" +
                            " from tbl_wo_return_products rp\n" +
                            " JOIN tbl_wo_return_customer rc\n" +
                            " ON rp.fk_return_id_int=rc.id_int \n" +
                            " WHERE rp.product_id_int="+prodID+" AND rc.customer_id_int="+custID+"\n" +
                            " GROUP BY rp.product_id_int,rc.customer_id_int";



                    Log.e("sqlRetunQty",sqlRetunQty);
                    Cursor cursorReturnQty = db.rawQuery(sqlRetunQty, null);

                    cursorReturnQty.moveToFirst();
                    int returnQty=0;
                    if(cursorReturnQty.getCount()==1){
                        returnQty=cursorReturnQty.getInt(cursorReturnQty.getColumnIndex("qty"));

                    }

                    s.setReturnQty(String.valueOf(returnQty));

                    double salePercentage=(intToDouble(saleQty-returnQty) / intToDouble(saleQty))*100.0;
                    double returnPercentage=(intToDouble(returnQty) / intToDouble(saleQty))*100.0;

                    s.setSalePercentage(String.valueOf(new DecimalFormat("##.##").format(salePercentage)));
                    s.setReturnPercentage(String.valueOf(new DecimalFormat("##.##").format(returnPercentage)));


                    list.add(s);


                } while (cursor.moveToNext());

            String sqlWoReturn="SELECT t1.product_name_txt,t1.qty,t1.customer_name_txt FROM (SELECT rp.product_id_int,\n" +
                    "    rp.product_name_txt,\n" +
                    "    SUM(rp.return_piece_quantity_int) as qty,\n" +
                    "    rc.customer_id_int,\n" +
                    "    rc.customer_name_txt\n" +
                    "     from tbl_wo_return_products rp\n" +
                    "     JOIN tbl_wo_return_customer rc\n" +
                    "     ON rp.fk_return_id_int=rc.id_int \n" +
                    "\t GROUP BY rp.product_id_int,rc.customer_id_int) t1 LEFT JOIN (SELECT sc.customer_name_txt,sc.customer_id_int,\n" +
                    "    sp.product_id_int,\n" +
                    "    sp.product_name_txt,\n" +
                    "    sum(sp.sale_piece_quantity_int) as sale_qty,\n" +
                    "    sc.payment_type_txt\n" +
                    "     FROM tbl_sales_products sp\n" +
                    "     JOIN tbl_sales_customer sc\n" +
                    "     ON sp.fk_customer_id_int=sc.id_int \n" +
                    "     GROUP BY sp.product_name_txt,sc.customer_name_txt) t2 \n" +
                    "\t ON t1.product_id_int=t2.product_id_int and t1.customer_id_int=t2.customer_id_int \n" +
                    "\t WHERE t2.customer_id_int IS NULL";

            printLog("sqlWoReturn",sqlWoReturn);


            Cursor cursorWoReturn = db.rawQuery(sqlWoReturn, null);

            if(cursorWoReturn.moveToFirst())
                do {
                    DailyProductReport s = new DailyProductReport();
                    s.setShop(cursorWoReturn.getString(cursorWoReturn.getColumnIndex(COL_CUSTOMER_NAME)));
                    s.setProduct(cursorWoReturn.getString(cursorWoReturn.getColumnIndex(COL_PRODUCT_NAME)));
                    s.setModeOfPay("");
                    s.setSaleQty(0);
                    s.setReturnQty(String.valueOf(cursorWoReturn.getInt(cursorWoReturn.getColumnIndex("qty"))));
                    s.setSalePercentage(String.valueOf(new DecimalFormat("##.##").format(0.0)));
                    s.setReturnPercentage(String.valueOf(new DecimalFormat("##.##").format(100.0)));

                    list.add(s);

                }while (cursorWoReturn.moveToNext());



            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getCustomerCreditedAmount  Exception  " + e.getMessage());

        }

        return list;
    }

    public ArrayList<DailyReport> getDailyReport() {

        ArrayList<DailyReport> list = new ArrayList<>();

        try {


            String sql="SELECT customer_id_int,customer_name_txt,sum(total_credit_sale) as tcrs,sum(total_cash_sale) as tcs,sum(total_return_sale) as total_return_sale,sum(cash_receipt) as cash_receipt,sum(bank_receipt) as bank_receipt,sum(cheque_receipt) as cheque_receipt \n" +
                    "    FROM (SELECT customer_id_int,customer_name_txt,CreditSale as total_credit_sale,CashSale as total_cash_sale,0.0 as total_return_sale,0.0 as cash_receipt,0.0 as bank_receipt,0.0 as cheque_receipt \n" +
                    "    FROM (select customer_id_int,customer_name_txt,case when payment_type_txt='CreditSale' then sale_withtax_total else 0.0 end CreditSale,case when payment_type_txt='CashSale' then sale_withtax_total else 0.0 end CashSale from tbl_sales_customer) \n" +
                    "    UNION ALL \n" +
                    "    SELECT customer_id_int,customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,sum(return_total_real) as total_return_sale,0.0 as cash_receipt,0.0 as bank_receipt,0.0 as cheque_receipt \n" +
                    "    FROM tbl_wo_return_customer group by customer_id_int \n" +
                    "    UNION All \n" +
                    "    SELECT r.customer_id_int,c.customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,0.0 as total_return_sale,sum(r.cash_receipt) as cash_receipt,sum(r.bank_receipt) as bank_receipt,0.0 as cheque_receipt \n" +
                    "    FROM (SELECT fk_customer_id_int as customer_id_int,case when receipt_type_txt='1' then receipt_receivable_real else 0.0 end cash_receipt,case when receipt_type_txt='2' then receipt_receivable_real else 0.0 end bank_receipt\n" +
                    "    FROM tbl_receipts) r  JOIN tbl_customer c ON r.customer_id_int=c.customer_id_int GROUP by r.customer_id_int\n" +
                    "    UNION ALL \n" +
                    "    SELECT fk_customer_id_int as customer_id_int,c.customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,0.0 as total_return_sale,0.0 as cash_receipt,0.0 as bank_receipt,sum(receipt_receivable_real) cheque_receipt \n" +
                    "    FROM tbl_cheque_details cd JOIN tbl_customer c ON cd.fk_customer_id_int=c.customer_id_int GROUP BY fk_customer_id_int)\n" +
                    "    GROUP BY customer_id_int";

            printLog("sqldaily",sql);

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    DailyReport s = new DailyReport();



                    s.setCustomer(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                    s.setTotalCashSale(cursor.getString(cursor.getColumnIndex("tcs")));
                    s.setTotalCreditSale(cursor.getString(cursor.getColumnIndex("tcrs")));
                    s.setTotalReturnSale(cursor.getString(cursor.getColumnIndex("total_return_sale")));
                    s.setTotalCashCollection(cursor.getString(cursor.getColumnIndex("cash_receipt")));
                    s.setTotalBankCollection(cursor.getString(cursor.getColumnIndex("bank_receipt")));
                    s.setTotalChequeCollection(cursor.getString(cursor.getColumnIndex("cheque_receipt")));




                    list.add(s);


                } while (cursor.moveToNext());




            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getDailyReport  Exception  " + e.getMessage());

        }

        return list;
    }

    public  String get_salereturn_cash(){
        String st_return_total ="";
        String sql = "SELECT sum(return_total_real) as total_return_cash \n" +
                "FROM tbl_wo_return_customer where payment_type_txt ='CashSale' group by customer_id_int ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst())
            do {

                 st_return_total = cursor.getString(cursor.getColumnIndex("total_return_cash"));
            } while (cursor.moveToNext());

        cursor.close();
        return  st_return_total;
    }

    public DailyReport getDaySummaryReport() {

        DailyReport list = new DailyReport();

        try {

            String sql = "SELECT customer_id_int,customer_name_txt,sum(total_credit_sale) as tcrs,sum(total_cash_sale) as tcs,sum(total_return_sale) as total_return_sale,sum(cash_receipt) as cash_receipt,sum(bank_receipt) as bank_receipt,sum(cheque_receipt) as cheque_receipt \n" +
                    "FROM (SELECT customer_id_int,customer_name_txt,CreditSale as total_credit_sale,CashSale as total_cash_sale,0.0 as total_return_sale,0.0 as cash_receipt,0.0 as bank_receipt,0.0 as cheque_receipt \n" +
                    "FROM (select customer_id_int,customer_name_txt,case when payment_type_txt='CreditSale' then sale_withtax_total else 0.0 end CreditSale,case when payment_type_txt='CashSale' then sale_withtax_total else 0.0 end CashSale from tbl_sales_customer) \n" +
                    "UNION ALL \n" +
                    "SELECT customer_id_int,customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,sum(return_total_real) as total_return_sale,0.0 as cash_receipt,0.0 as bank_receipt,0.0 as cheque_receipt \n" +
                    "FROM tbl_wo_return_customer group by customer_id_int \n" +
                    "UNION All \n" +
                    "SELECT r.customer_id_int,c.customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,0.0 as total_return_sale,sum(r.cash_receipt) as cash_receipt,sum(r.bank_receipt) as bank_receipt,0.0 as cheque_receipt \n" +
                    "FROM (SELECT fk_customer_id_int as customer_id_int,case when receipt_type_txt='1' then receipt_receivable_real else 0.0 end cash_receipt,case when receipt_type_txt='2' then receipt_receivable_real else 0.0 end bank_receipt\n" +
                    "FROM tbl_receipts) r  JOIN tbl_customer c ON r.customer_id_int=c.customer_id_int GROUP by r.customer_id_int\n" +
                    "UNION ALL \n" +
                    "SELECT fk_customer_id_int as customer_id_int,c.customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,0.0 as total_return_sale,0.0 as cash_receipt,0.0 as bank_receipt,sum(receipt_receivable_real) cheque_receipt \n" +
                    "FROM tbl_cheque_details cd JOIN tbl_customer c ON cd.fk_customer_id_int=c.customer_id_int GROUP BY fk_customer_id_int)" ;


//            String sql = "SELECT customer_id_int,customer_name_txt,sum(total_credit_sale) as tcrs,sum(total_cash_sale) as tcs,sum(total_return_sale) as total_return_sale,0.0 as total_return_cash ,sum(cash_receipt) as cash_receipt,sum(bank_receipt) as bank_receipt,sum(cheque_receipt) as cheque_receipt \n" +
//                    "FROM (SELECT customer_id_int,customer_name_txt,CreditSale as total_credit_sale,CashSale as total_cash_sale,0.0 as total_return_sale,0.0 as cash_receipt,0.0 as bank_receipt,0.0 as cheque_receipt \n" +
//                    "FROM (select customer_id_int,customer_name_txt,case when payment_type_txt='CreditSale' then sale_withtax_total else 0.0 end CreditSale,case when payment_type_txt='CashSale' then sale_withtax_total else 0.0 end CashSale from tbl_sales_customer) \n" +
//                    "UNION ALL \n" +
//                    "SELECT customer_id_int,customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,sum(return_total_real) as total_return_sale,0.0 as total_return_cash ,0.0 as cash_receipt,0.0 as bank_receipt,0.0 as cheque_receipt \n" +
//                    "FROM tbl_wo_return_customer group by customer_id_int \n" +
//                    "UNION All \n" +
//                    "SELECT customer_id_int,customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,sum(return_total_real) as total_return_sale,sum(return_total_real) as total_return_cash,0.0 as cash_receipt,0.0 as bank_receipt,0.0 as cheque_receipt \n" +
//                    "FROM tbl_wo_return_customer where payment_type_txt ='CashSale' group by customer_id_int \n" +
//                    "UNION All \n" +
//                    "SELECT r.customer_id_int,c.customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,0.0 as total_return_sale,0.0 as total_return_cash ,sum(r.cash_receipt) as cash_receipt,sum(r.bank_receipt) as bank_receipt,0.0 as cheque_receipt \n" +
//                    "FROM (SELECT fk_customer_id_int as customer_id_int,case when receipt_type_txt='1' then receipt_receivable_real else 0.0 end cash_receipt,case when receipt_type_txt='2' then receipt_receivable_real else 0.0 end bank_receipt\n" +
//                    "FROM tbl_receipts) r  JOIN tbl_customer c ON r.customer_id_int=c.customer_id_int GROUP by r.customer_id_int\n" +
//                    "UNION ALL \n" +
//                    "SELECT fk_customer_id_int as customer_id_int,c.customer_name_txt,0.0 as total_credit_sale,0.0 as total_cash_sale,0.0 as total_return_sale, 0.0 as total_return_cash ,0.0 as cash_receipt,0.0 as bank_receipt,sum(receipt_receivable_real) cheque_receipt \n" +
//                    "FROM tbl_cheque_details cd JOIN tbl_customer c ON cd.fk_customer_id_int=c.customer_id_int GROUP BY fk_customer_id_int)" ;

            printLog("sql",sql);

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    DailyReport s = new DailyReport();

                    s.setTotalCashSale(cursor.getString(cursor.getColumnIndex("tcs")));
                    s.setTotalCreditSale(cursor.getString(cursor.getColumnIndex("tcrs")));
                    s.setTotalReturnSale(cursor.getString(cursor.getColumnIndex("total_return_sale")));
                   // s.setTotalReturnCash(cursor.getString(cursor.getColumnIndex("total_return_cash")));

                    s.setTotalCashCollection(cursor.getString(cursor.getColumnIndex("cash_receipt")));
                    s.setTotalBankCollection(cursor.getString(cursor.getColumnIndex("bank_receipt")));
                    s.setTotalChequeCollection(cursor.getString(cursor.getColumnIndex("cheque_receipt")));




                    list=s;


                } while (cursor.moveToNext());




            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getDailyReport  Exception  " + e.getMessage());

        }

        return list;
    }

    public double intToDouble(int intigerValue){
        String stringValue=String.valueOf(intigerValue);
        double doubleValue=Double.parseDouble(stringValue);

        return  doubleValue;

    }

    //    get getAllStock list
    public ArrayList<CartItemCode> getAllStockCode() {

        final ArrayList<CartItemCode> cartItems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_STOCK;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    CartItemCode c = new CartItemCode();
                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setBarcode(cursor.getString(cursor.getColumnIndex(COL_BARCODE)));
                    c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    c.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                    c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_BONUS)));
                    c.setBrandName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_BRAND)));
                    c.setUnitslist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITS)));
                    c.setProductType(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TYPE)));
                    c.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                    c.setWholeSalePrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_WHOLESALE)));
                    c.setCost(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_COST)));
                    c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));

                    c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));

                    c.setStockQuantity(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY)));

                    cartItems.add(c);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.v(TAG, "getAllStock  Exception  " + e.getMessage());
        }

        return cartItems;
    }



    //    get getAllStock list
    public ArrayList<CartItemCode> getAllMasterStockCode() {

        final ArrayList<CartItemCode> cartItems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_STOCK_MASTER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    CartItemCode c = new CartItemCode();
                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setBarcode(cursor.getString(cursor.getColumnIndex(COL_BARCODE)));
                    c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    c.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                    c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_BONUS)));
                    c.setBrandName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_BRAND)));
                    c.setUnitslist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITS)));
                    c.setProductType(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TYPE)));
                    c.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                    c.setWholeSalePrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_WHOLESALE)));
                    c.setCost(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_COST)));
                    c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));

                    c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));

                    c.setStockQuantity(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY)));

                    cartItems.add(c);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.v(TAG, "getAllStock  Exception  " + e.getMessage());
        }

        return cartItems;
    }




    //    get getProductDetails corresponding id
    public CartItem getProductDetails(int productId) {

        CartItem c = new CartItem();
        try {
//            String sql = "SELECT * FROM " + TABLE_STOCK;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK + " WHERE " + COL_PRODUCT_ID + WHERE_CLAUSE, new String[]{String.valueOf(productId)});


            if (cursor != null) {
                cursor.moveToFirst();

                c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                c.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                c.setBrandName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_BRAND)));

                c.setProductType(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TYPE)));
                c.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                c.setWholeSalePrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_WHOLESALE)));
                c.setCost(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_COST)));
                c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));

                c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                c.setP_name(cursor.getString(cursor.getColumnIndex(COL_P_NAME)));
                c.setStockQuantity(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY)));

                cursor.close();
                db.close();
            }

        } catch (SQLiteException e) {

            Log.v(TAG, "getProductDetails  Exception  " + e.getMessage());
        }

        return c;
    }

    //    get products corresponding id
    private Product getStockProduct(int id) {

        Product p = new Product();
        try {
            String sql = "SELECT * FROM " + TABLE_STOCK + " WHERE " + COL_PRODUCT_ID + " = '" + id + "'";


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


//            Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(customerId)});


            if (cursor.moveToFirst())
                do {

                    p.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    p.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    p.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));

                    p.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    p.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_BONUS)));
                    p.setBrandName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_BRAND)));
                    p.setProductType(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TYPE)));

                    p.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));

                    p.setWholeSalePrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_WHOLESALE)));
                    p.setCost(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_COST)));
                    p.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));
                    p.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                    p.setP_name(cursor.getString(cursor.getColumnIndex(COL_P_NAME)));
                    p.setStockQuantity(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY)));

                    Log.v(TAG, "getStockProduct  product name  " + p.getProductName());

                    cursor.close();
                    db.close();
                    return p;

                } while (cursor.moveToNext());


        } catch (SQLiteException e) {
            Log.v(TAG, "getStockProduct  Exception  " + e.getMessage());

        }

        return p;
    }

    //   Get amount TABLE_STOCK
    public float getStockAmount() {


        float stockAmount = 0;


        try {

            //String sql = "SELECT " + COL_PRODUCT_QUANTITY + "," + COL_PRODUCT_MRP + " FROM " + TABLE_STOCK;
            //haris added on 23-11-2020
            String sql = "SELECT " + COL_PRODUCT_QUANTITY + "," + COL_PRODUCT_COST + " FROM " + TABLE_STOCK;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    int qty = cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY));
                    float f = cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_COST));

                    stockAmount += qty * f;


                } while (cursor.moveToNext());

            cursor.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getStockAmount  Exception  " + e.getMessage());

        }

        Log.v(TAG, "getStockAmount  stockAmount  " + stockAmount);
        return stockAmount;
    }

    //   Get stock Quantity TABLE_STOCK
    public long getStockQuantity() {


        long stockQuantity = 0;


        try {
            String sql = "SELECT " + COL_PRODUCT_QUANTITY + " FROM " + TABLE_STOCK;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    int qty = cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY));
                    stockQuantity += qty;

                } while (cursor.moveToNext());

            cursor.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getStockQuantity  Exception  " + e.getMessage());

        }


        return stockQuantity;
    }


    //      check isavailable stock
//     Count Row From to SQLite  "TABLE_STOCK" Table
    public boolean isExistProducts() {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_STOCK, new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;

    }

    private boolean isExistProduct(int productId) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_STOCK + " WHERE " + COL_PRODUCT_ID + WHERE_CLAUSE, new String[]{String.valueOf(productId)});

//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER , new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return count != 0;
    }


    private boolean isExistMasterProduct(int productId) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_STOCK_MASTER + " WHERE " + COL_PRODUCT_ID + WHERE_CLAUSE, new String[]{String.valueOf(productId)});

//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER , new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;
    }

    //haris added on 02-02-2021

    private boolean isExistMasterSize(int sizeid) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_SIZE_MASTER + " WHERE " + COL_SIZEMASTER_ID + WHERE_CLAUSE, new String[]{String.valueOf(sizeid)});

//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER , new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;
    }


    //    insert Groups
    public boolean insertgroups(RouteGroup group) {
        Log.e("groupname",""+group.getGroupName());

        boolean isExist = isExistGroup(group.getGroupId());  // check customer in table

        if (isExist)
            return false;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_GROUP_ID, group.getGroupId());
            values.put(COL_GROUP_NAME, group.getGroupName());

            // Inserting Row
            long l = db.insert(TABLE_GROUPS, null, values);

            db.close(); // Closing database connection


            if (l == -1) {
                return false;
            } else {
                return true;

            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertGroups  Exception  " + e.getMessage());
            return false;
        }
    }


    /**
     * customer functions
     */

    //    insert Registered customers
    public boolean insertRegisteredCustomer(Shop shop) {
        Log.e("getPlace_ofsupply",""+shop.getPlace_ofsupply());
        Log.e("isRegistered_status",""+shop.isRegistered_status());

        boolean isExist = isExistCustomer(shop.getShopId());  // check customer in table

        if (isExist)
            return false;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_CUSTOMER_ID, shop.getShopId());
            values.put(COL_CUSTOMER_NAME, shop.getShopName());
            values.put(COL_CUSTOMER_NAME_ARABIC, shop.getShopArabicName());
            values.put(COL_CUSTOMER_CODE, shop.getShopCode());
            values.put(COL_CUSTOMER_MAIL, shop.getShopMail());
            values.put(COL_CUSTOMER_MOBILE, shop.getShopMobile());
            values.put(COL_CUSTOMER_VAT_NO, shop.getVatNumber());
            values.put(COL_CUSTOMER_ADDRESS, shop.getShopAddress());
            values.put(COL_CUSTOMER_CREDITLIMIT, shop.getCreditLimit());
            values.put(COL_CUSTOMER_CREDIT, shop.getCredit());
            values.put(COL_CUSTOMER_DEBIT, shop.getDebit());
            values.put(COL_ROUTE_CODE, shop.getRouteCode());
            values.put(COL_CUSTOMER_OUTSTANDING_BALANCE, shop.getOutStandingBalance());
            values.put(COL_CUSTOMER_PREVIOUS_BALANCE, shop.getOutStandingBalance());
            values.put(COL_CUSTOMER_VISIT_STATUS, false);
//            values.put(COL_CUSTOMER_REGISTERED_STATUS, true);
            values.put(COL_CUSTOMER_REGISTERED_STATUS, shop.isRegistered_status());
            values.put(COL_CUSTOMER_LATITUDE, shop.getLatitude());
            values.put(COL_CUSTOMER_LONGITUDE, shop.getLongitude());
            values.put(COL_CUSTOMER_OPENINGBALANCE, shop.getOpeningbalance());
            values.put(COL_CUSTOMER_CONTACTPERSON, shop.getContactPerson());
            values.put(COL_CUSTOMER_PLACEOFSUPPLY, shop.getPlace_ofsupply());
            values.put(COL_CUSTOMER_CREDITLIMIT_REGISTER,shop.getCreditlimit_register());
            values.put(COL_CUSTOMER_CREDITPERIOD,shop.getCreditperiod());
            values.put(COL_CUSTOMER_STATEID,shop.getState_id());
            values.put(COL_CUSTOMER_ROUTE,shop.getRoute());
            values.put(COL_CUSTOMER_GROUP,shop.getGroup());
            values.put(COL_CUSTOMER_STATECODE ,shop.getState_code());
            // Inserting Row
            long l = db.insert(TABLE_CUSTOMER, null, values);

            db.close(); // Closing database connection


            if (l == -1) {
                return false;
            } else {

                insertVisitStatus(shop.getShopId());
//                insertInvoices(shop.getShopId(), shop.getInvoices());
                insertCustomerProducts(shop.getProducts());

                return true;

            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertCustomer  Exception  " + e.getMessage());
            return false;
        }
    }

    //    get getAll Denomination haris added on 12-05-2021
    public double get_outstandingbal_byshopid(int shopid) {

        double outstanding_bal = 0;
        try {
            String sql = "SELECT * FROM " + TABLE_CUSTOMER + " where " + COL_CUSTOMER_ID + " ='" + shopid + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {
                    outstanding_bal =cursor.getDouble(cursor.getColumnIndex(COL_CUSTOMER_OUTSTANDING_BALANCE));

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.e(TAG, "getAllDenominators  Exception  " + e.getMessage());
        }

        return outstanding_bal;
    }
    //    update customer table
    public boolean update_customer(Shop shop,double outstandingbal) {

        Log.e("outstandingbal edt", "" + outstandingbal);
        Log.e("getShopName edt", "" + shop.getShopName());
        Log.e("credtlmt edt", "" + shop.getCreditLimit());
        Log.e("previousbal edt2", "" + shop.getOutStandingBalance());

        double credtlmt = 0;



        boolean isExist = isExistCustomer(shop.getShopId());  // check customer in table
//
        if (!isExist)
            return false;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_CUSTOMER_ID, shop.getShopId());
            values.put(COL_CUSTOMER_NAME, shop.getShopName());
            values.put(COL_CUSTOMER_NAME_ARABIC, shop.getShopArabicName());
            // values.put(COL_CUSTOMER_CODE, shop.getShopCode());
            values.put(COL_CUSTOMER_MAIL, shop.getShopMail());
            values.put(COL_CUSTOMER_MOBILE, shop.getShopMobile());
            values.put(COL_CUSTOMER_VAT_NO, shop.getVatNumber());
            values.put(COL_CUSTOMER_ADDRESS, shop.getShopAddress());
            //haris added on 12-05-21
            values.put(COL_CUSTOMER_CREDITLIMIT, shop.getCreditLimit());
            //
//            values.put(COL_CUSTOMER_CREDIT, shop.getCredit());
//            values.put(COL_CUSTOMER_DEBIT, shop.getDebit());
            values.put(COL_ROUTE_CODE, shop.getRouteCode());
            values.put(COL_CUSTOMER_OUTSTANDING_BALANCE, outstandingbal);
            values.put(COL_CUSTOMER_PREVIOUS_BALANCE, shop.getOutStandingBalance());
            values.put(COL_CUSTOMER_VISIT_STATUS, false);
            values.put(COL_CUSTOMER_REGISTERED_STATUS, true);
            // values.put(COL_CUSTOMER_LATITUDE, shop.getLatitude());
            // values.put(COL_CUSTOMER_LONGITUDE, shop.getLongitude());
            //  values.put(COL_CUSTOMER_CONTACTPERSON, shop.getContactPerson());

            //haris added on 26-11-2020
            // values.put(COL_CATEGORY, shop.getShop_category());
            //haris added on 19-12-2020
            // values.put(COL_CUSTOMER_TYPE, shop.getCustomer_type());
            values.put(COL_CUSTOMER_OPENINGBALANCE, shop.getOpeningbalance());
            // values.put(COL_CUSTOMER_CREDITPERIOD,shop.getCreditperiod());
            //  values.put(COL_CUSTOMER_STATE, shop.getState());


            int result = db.update(TABLE_CUSTOMER,
                    values,
                    COL_CUSTOMER_ID + " = ? ",
                    new String[]{String.valueOf(shop.getShopId())});

            db.close();
            return result != -1;
        } catch (SQLiteException e) {
            Log.d(TAG, "updateCustomer Exception  " + e.getMessage());
            return false;
        }

    }

    //    update customer table
    public boolean update_customers(Shop shop) {

        boolean isExist = isExistCustomer(shop.getShopId());  // check customer in table
//
        if (!isExist)
            return false;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(COL_CUSTOMER_MAIL, shop.getShopMail());
            values.put(COL_CUSTOMER_MOBILE, shop.getShopMobile());
            values.put(COL_CUSTOMER_VAT_NO, shop.getVatNumber());
            values.put(COL_CUSTOMER_ADDRESS, shop.getShopAddress());
//            values.put(COL_CUSTOMER_CREDITLIMIT, shop.getCreditLimit());
//            values.put(COL_ROUTE_CODE, shop.getRouteCode());
//            values.put(COL_CUSTOMER_OUTSTANDING_BALANCE, outstandingbal);
//            values.put(COL_CUSTOMER_PREVIOUS_BALANCE, shop.getOutStandingBalance());
           // values.put(COL_CUSTOMER_VISIT_STATUS, false);
          //  values.put(COL_CUSTOMER_REGISTERED_STATUS, true);
           // values.put(COL_CUSTOMER_OPENINGBALANCE, shop.getOpeningbalance());


            int result = db.update(TABLE_CUSTOMER,
                    values,
                    COL_CUSTOMER_ID + " = ? ",
                    new String[]{String.valueOf(shop.getShopId())});

            db.close();
            return result != -1;
        } catch (SQLiteException e) {
            Log.d(TAG, "updateCustomers Exception  " + e.getMessage());
            return false;
        }

    }


    //    insert Un Registered customers
    public boolean insertUnRegisteredCustomer(Shop shop) {
            Log.e("statecode",""+shop.getState_code());

        boolean isExist = isExistCustomer(shop.getShopId());  // check customer in table

        if (isExist) {
            boolean b = update_customers(shop);
            return false;
        }

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_CUSTOMER_ID, shop.getShopId());
            values.put(COL_CUSTOMER_NAME, shop.getShopName());
            values.put(COL_CUSTOMER_NAME_ARABIC, shop.getShopArabicName());
            values.put(COL_CUSTOMER_CODE, shop.getShopCode());
            values.put(COL_CUSTOMER_MAIL, shop.getShopMail());
            values.put(COL_CUSTOMER_MOBILE, shop.getShopMobile());
            values.put(COL_CUSTOMER_ADDRESS, shop.getShopAddress());
            values.put(COL_CUSTOMER_CREDITLIMIT, shop.getCreditLimit());
            values.put(COL_CUSTOMER_CREDIT, shop.getCredit());
            values.put(COL_CUSTOMER_DEBIT, shop.getDebit());
            values.put(COL_ROUTE_CODE, shop.getRouteCode());
            values.put(COL_CUSTOMER_OUTSTANDING_BALANCE, shop.getOutStandingBalance());
            values.put(COL_CUSTOMER_PREVIOUS_BALANCE, shop.getOutStandingBalance());
            values.put(COL_CUSTOMER_VISIT_STATUS, true);
            values.put(COL_CUSTOMER_REGISTERED_STATUS, false);
            values.put(COL_CUSTOMER_LATITUDE, shop.getLatitude());
            values.put(COL_CUSTOMER_LONGITUDE, shop.getLongitude());
            values.put(COL_CUSTOMER_VAT_NO, shop.getVatNumber());
            values.put(COL_CUSTOMER_OPENINGBALANCE,shop.getOpeningbalance());
            values.put(COL_CUSTOMER_PLACEOFSUPPLY,shop.getPlace_ofsupply());
            values.put(COL_CUSTOMER_CREDITLIMIT_REGISTER,shop.getCreditlimit_register());
            values.put(COL_STATE_ID,shop.getState_id());
            values.put(COL_CUSTOMER_STATECODE,shop.getState_code());

            // Inserting Row
            long l = db.insert(TABLE_CUSTOMER, null, values);

            db.close(); // Closing database connection


            if (l == -1) {
                return false;
            } else {

                insertVisitStatus(shop.getShopId());
                insertCustomerProducts(shop.getProducts());

                return true;
            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertCustomer  Exception  " + e.getMessage());
            return false;
        }
    }

    //    update updateLocation
    public boolean updateLocation(int customerId, String latitude, String longitude) {

        boolean isExist = isExistCustomer(customerId);  // check customer in table

        if (!isExist)
            return false;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_CUSTOMER_LATITUDE, latitude);
            values.put(COL_CUSTOMER_LONGITUDE, longitude);
            int result = db.update(TABLE_CUSTOMER, values, COL_CUSTOMER_ID + " =?", new String[]{String.valueOf(customerId)});

            db.close();
            return result != -1;
        } catch (SQLiteException e) {
            Log.d(TAG, "updateLocation  Exception  " + e.getMessage());
            return false;
        }

    }

    //    Get un registered Customer list TABLE_CUSTOMER
    public ArrayList<Shop> getUnRegisteredCustomers() {

        ArrayList<Shop> list = new ArrayList<>();
        try {

            String sql = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_REGISTERED_STATUS + " = '" + "0" + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {
                    Shop s = new Shop();

                    s.setLocalId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setShopId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setShopName(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                    s.setShopArabicName(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME_ARABIC)));

                    s.setShopCode(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CODE)));

                    s.setShopMail(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_MAIL)));

                    s.setShopMobile(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_MOBILE)));

                    s.setShopAddress(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_ADDRESS)));

                    s.setVatNumber(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_VAT_NO)));

                    s.setCredit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDIT)));
                    s.setCreditLimit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDITLIMIT)));

                    s.setDebit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_DEBIT)));
                    s.setRouteCode(cursor.getString(cursor.getColumnIndex(COL_ROUTE_CODE)));
                    s.setOutStandingBalance(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_OUTSTANDING_BALANCE)));

                    s.setLatitude(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_LATITUDE)));
                    s.setLongitude(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_LONGITUDE)));
                    s.setPlace_ofsupply(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_PLACEOFSUPPLY)));
                    s.setState_id(cursor.getInt(cursor.getColumnIndex(COL_STATE_ID)));
                    s.setState_code(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_STATECODE)));
                    Boolean flag = (cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_VISIT_STATUS)) == 1);
                    s.setVisit(flag);

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {
            Log.v(TAG, "getAllCustomers  Exception  " + e.getMessage());

        }

        return list;
    }

    //    Get registered Customer list TABLE_CUSTOMER
    public ArrayList<Shop> getAllCustomers() {

        ArrayList<Shop> list = new ArrayList<>();
        try {

//            String sql = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_REGISTERED_STATUS + " = '" + "1" + "'";
            //String sql = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_REGISTERED_STATUS + " = '" + "1" + "' ORDER BY " + COL_CUSTOMER_NAME + " ASC ";
            String sql = "SELECT * FROM " + TABLE_CUSTOMER + " ORDER BY " + COL_CUSTOMER_NAME + " ASC ";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {
                    Shop s = new Shop();

                    s.setLocalId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setShopId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setShopName(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                    s.setShopArabicName(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME_ARABIC)));

                    s.setShopCode(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CODE)));

                    s.setShopMail(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_MAIL)));

                    s.setShopMobile(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_MOBILE)));

                    s.setShopAddress(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_ADDRESS)));

                    s.setCreditLimit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDITLIMIT)));
                    s.setCredit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDIT)));

                    s.setDebit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_DEBIT)));
                    s.setRouteCode(cursor.getString(cursor.getColumnIndex(COL_ROUTE_CODE)));
                    s.setOutStandingBalance(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_OUTSTANDING_BALANCE)));

                    s.setLatitude(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_LATITUDE)));
                    s.setLongitude(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_LONGITUDE)));
                    s.setVatNumber(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_VAT_NO)));
                    s.setState_id(cursor.getInt(cursor.getColumnIndex(COL_STATE_ID)));
                    s.setState_code(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_STATECODE)));

                    Boolean flag = (cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_VISIT_STATUS)) == 1);
                    s.setVisit(flag);

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {
            Log.v(TAG, "getAllCustomers  Exception  " + e.getMessage());

        }

        return list;
    }

    //    Get registered Customer list TABLE_CUSTOMER
    public ArrayList<Shop> getRegisteredCustomers() {

        ArrayList<Shop> list = new ArrayList<>();
        try {

//            String sql = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_REGISTERED_STATUS + " = '" + "1" + "'";
            String sql = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_REGISTERED_STATUS + " = '" + "1" + "' ORDER BY " + COL_CUSTOMER_NAME + " ASC ";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {
                    Shop s = new Shop();

                    s.setLocalId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setShopId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setShopName(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                    s.setShopArabicName(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME_ARABIC)));

                    s.setShopCode(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CODE)));

                    s.setShopMail(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_MAIL)));

                    s.setShopMobile(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_MOBILE)));

                    s.setShopAddress(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_ADDRESS)));

                    s.setCreditLimit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDITLIMIT)));
                    s.setCredit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDIT)));

                    s.setDebit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_DEBIT)));
                    s.setRouteCode(cursor.getString(cursor.getColumnIndex(COL_ROUTE_CODE)));
                    s.setOutStandingBalance(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_OUTSTANDING_BALANCE)));

                    s.setLatitude(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_LATITUDE)));
                    s.setLongitude(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_LONGITUDE)));
                    s.setVatNumber(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_VAT_NO)));
                    s.setContactPerson(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CONTACTPERSON)));
                    s.setPlace_ofsupply(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_PLACEOFSUPPLY)));
                    s.setCreditperiod(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CREDITPERIOD)));
                    s.setState_id(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_STATEID)));
                    s.setGroup(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_GROUP)));
                    s.setRoute(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_ROUTE)));
                    s.setState_code(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_STATECODE)));


                    Boolean flag = (cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_VISIT_STATUS)) == 1);
                    s.setVisit(flag);

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {
            Log.v(TAG, "getAllCustomers  Exception  " + e.getMessage());

        }

        return list;
    }

    //    Get a  Customer  TABLE_CUSTOMER
    public Shop getIdWiseCustomer(int customerId) {
        Log.e("custidd",""+customerId);


        Shop s = new Shop();

        try {

            String sql = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_ID + " = '" + String.valueOf(customerId) + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor != null) {
                cursor.moveToFirst();
                s.setLocalId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                s.setShopId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                s.setShopName(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                s.setShopArabicName(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME_ARABIC)));

                s.setShopCode(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CODE)));

                s.setShopMail(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_MAIL)));

                s.setShopMobile(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_MOBILE)));

                s.setCreditLimit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDITLIMIT)));
                s.setShopAddress(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_ADDRESS)));

                s.setVatNumber(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_VAT_NO)));

                s.setCredit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDIT)));

                s.setDebit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_DEBIT)));
                s.setRouteCode(cursor.getString(cursor.getColumnIndex(COL_ROUTE_CODE)));
                s.setOutStandingBalance(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_OUTSTANDING_BALANCE)));

                s.setLatitude(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_LATITUDE)));
                s.setLongitude(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_LONGITUDE)));
                s.setContactPerson(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CONTACTPERSON)));
                s.setPlace_ofsupply(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_PLACEOFSUPPLY)));
                s.setCreditlimit_register(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CREDITLIMIT_REGISTER)));
                s.setCreditperiod(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CREDITPERIOD)));
                s.setOpeningbalance(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_OPENINGBALANCE)));
                s.setState_id(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_STATEID)));
                s.setGroup(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_GROUP)));
                s.setRoute(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_ROUTE)));
                s.setState_code(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_STATECODE)));

                Boolean flag = (cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_VISIT_STATUS)) == 1);
                s.setVisit(flag);


            }


            assert cursor != null;
            cursor.close();
            db.close();


        } catch (SQLiteException e) {
            Log.v(TAG, "getCustomer  Exception  " + e.getMessage());

        }

        return s;
    }

    //      check isavailable contact
//      Count Row From to SQLite  "TABLE_CUSTOMER" Table
    private boolean isExistCustomer(int custId) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_ID + WHERE_CLAUSE, new String[]{String.valueOf(custId)});

//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER , new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;


    }

    private boolean isExistGroup(int groupid) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_GROUPS + " WHERE " + COL_GROUP_ID + WHERE_CLAUSE, new String[]{String.valueOf(groupid)});


            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;


    }

    /**
     * credit and debit
     */


    //    update debit balance
    public boolean updateCustomerBalance(Transaction transaction) {

        int customerId = transaction.getShopId();
        double debit = transaction.getDebit();
        double credit = transaction.getCredit();

        Log.e("DB", "credit :"+credit);
        Log.e("DB", "debit :"+debit);

        boolean isExist = isExistCustomer(customerId);  // check customer in table

        if (!isExist)
            return false;

        Transaction t = getCustomerTransactionBalance(customerId);

        double existCredit = t.getCredit();
        double existDebit = t.getDebit();
        double existOS = t.getOutStandingAmount();


        double newCredit = existCredit + credit;
        double newDebit = existDebit + debit;
        double newOS = newDebit - newCredit;

        Log.e("DB", "New Debit :"+newDebit);
        Log.e("DB", "New credit :"+newCredit);
        Log.e("DB", "New OutStanding :"+newOS);

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_CUSTOMER_DEBIT, newDebit);
            values.put(COL_CUSTOMER_CREDIT, newCredit);
            values.put(COL_CUSTOMER_OUTSTANDING_BALANCE, newOS);
            int result = db.update(TABLE_CUSTOMER, values, COL_CUSTOMER_ID + " =?", new String[]{String.valueOf(customerId)});

            db.close();
            return result != -1;
        } catch (SQLiteException e) {
            Log.v(TAG, "updateCustomerBalance  Exception  " + e.getMessage());
            return false;
        }
    }

    //   Get credit and debit outsatnding balance from customer table
    public Transaction getCustomerTransactionBalance(int customerId) {

        Transaction t = new Transaction();

        try {
            String sql = "SELECT " + COL_CUSTOMER_CREDIT + " , " + COL_CUSTOMER_DEBIT + " , " + COL_CUSTOMER_OUTSTANDING_BALANCE + " FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_ID + " = '" + String.valueOf(customerId) + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor != null) {
                cursor.moveToFirst();
                t.setShopId(customerId);
                t.setCredit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_CREDIT)));
                t.setDebit(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_DEBIT)));
                t.setOutStandingAmount(cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_OUTSTANDING_BALANCE)));

                cursor.close();
            }
            db.close();
        } catch (SQLiteException e) {

            Log.v(TAG, "getCreditBalance  Exception  " + e.getMessage());

        }

        return t;
    }

    /**
     * Opening balance functions
     */


    //   Get out standing balance from customer table
    public float getOutstandingAmountBalance(int customerId) {

        float balance = 0;

        try {
            String sql = "SELECT " + COL_CUSTOMER_OUTSTANDING_BALANCE + " FROM " + TABLE_CUSTOMER + " WHERE " + COL_CUSTOMER_ID + " = '" + String.valueOf(customerId) + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor != null) {
                cursor.moveToFirst();

                balance = cursor.getFloat(cursor.getColumnIndex(COL_CUSTOMER_OUTSTANDING_BALANCE));

            }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException e) {

            Log.v(TAG, "getOutstandingAmountBalance  Exception  " + e.getMessage());

        }

        return balance;
    }

    /*****/


    //   Get invoice wisecollections amount from TABLE_RECEIPT_TRANSACTION
    public float getInvoiceCollectionAmount(int customerId) {


        float receivedAmount = 0;


        try {
            String sql = "SELECT " + COL_RECEIVABLE_AMOUNT + " FROM " + TABLE_RECEIPT + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + customerId + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    float d = cursor.getFloat(cursor.getColumnIndex(COL_RECEIVABLE_AMOUNT));
                    receivedAmount += d;

                } while (cursor.moveToNext());

            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            Log.d(TAG, "getCollectionAmount  Exception  " + e.getMessage());

        }

        return receivedAmount;


    }


    //   Get invoice wisecollections amount from TABLE_RECEIPT_TRANSACTION
    public double getReceiptCollectionAmount(int customerId) {


        double receivedAmount = 0;


        try {
            String sql = "SELECT " + COL_RECEIVABLE_AMOUNT + " FROM " + TABLE_RECEIPT + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + customerId + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    double d = cursor.getDouble(cursor.getColumnIndex(COL_RECEIVABLE_AMOUNT));
                    receivedAmount += d;

                } while (cursor.moveToNext());

            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getCollectionAmount  Exception  " + e.getMessage());

        }

        return receivedAmount;


    }


    //   Get collections amount from TABLE_RECEIPT_TRANSACTION
    public float getCollectionAmount() {
        float receivedAmount = 0;

        try {
            String sql = "SELECT " + COL_RECEIVABLE_AMOUNT + " FROM " + TABLE_RECEIPT;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    float d = cursor.getFloat(cursor.getColumnIndex(COL_RECEIVABLE_AMOUNT));
                    receivedAmount += d;

                } while (cursor.moveToNext());

            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            Log.d(TAG, "getCollectionAmount  Exception  " + e.getMessage());

        }
        return receivedAmount;
    }


    public boolean insertChequeReceipt(chequeReceipt chequereceipt) {

        long l = -1;
        //  boolean isExist = isExistInvoice(receipt.getReceiptNo());
        if (chequereceipt.getReceivedAmount() == 0)
            return false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_RECEIPT_NO, chequereceipt.getReceiptNo());
            values.put(COL_RECEIVABLE_AMOUNT, chequereceipt.getReceivedAmount());
            values.put(COL_CHEQUE_NUMBER, chequereceipt.getChequeNo());
            values.put(COL_CREATED_AT,  getDateTime());
            values.put(COL_FK_CUSTOMER_ID, chequereceipt.getCustomerId());
            values.put(COL_COMPANY_BANK, chequereceipt.getCompanyBankId());
            values.put(COL_CUSTOMER_BANK, chequereceipt.getCustomerBank());
            values.put(COL_RECEIVE_DATE, chequereceipt.getChequeDate());
            values.put(COL_CLEARING_DATE, chequereceipt.getClearingdate());
            values.put(COL_UPLOAD_STATUS, chequereceipt.getUploadStatus());
            values.put(COL_UPLOAD_DATE, chequereceipt.getLogDate());

            l = db.insert(TABLE_CHEQUE_DETAILS, null, values);

            Log.e("insert cheque", "Success/"+chequereceipt.getReceiptNo());
            db.close();
        } catch (SQLiteException e) {
            Log.d(TAG, "insertTransactionLog  Exception  " + e.getMessage());
        }
        return l != -1;
    }

    //getAllChequeReceipts
    public ArrayList<chequeReceipt> getAllChequeReceipts() {

        final ArrayList<chequeReceipt> chequereceipts = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_CHEQUE_DETAILS+" WHERE " +COL_UPLOAD_STATUS+" ='N'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    chequeReceipt c = new chequeReceipt();

                    c.setReceiptNo(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_NO)));
                    c.setReceivedAmount(cursor.getFloat(cursor.getColumnIndex(COL_RECEIVABLE_AMOUNT)));
                    c.setChequeNo(cursor.getString(cursor.getColumnIndex(COL_CHEQUE_NUMBER)));
                    c.setCreatedDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
                    c.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_FK_CUSTOMER_ID)));
                    c.setCompanyBankId(cursor.getInt(cursor.getColumnIndex(COL_COMPANY_BANK)));
                    c.setCustomerBank(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_BANK)));
                    c.setChequeDate(cursor.getString(cursor.getColumnIndex(COL_RECEIVE_DATE)));
                    c.setClearingdate(cursor.getString(cursor.getColumnIndex(COL_CLEARING_DATE)));
                    c.setLogDate(cursor.getString(cursor.getColumnIndex(COL_UPLOAD_DATE)));

                    chequereceipts.add(c);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.d(TAG, "getAllReceipts  Exception  " + e.getMessage());
        }
        return chequereceipts;
    }


    public boolean insertReceipt_nw(Receipt receipt) {

        long l = -1;
     /*   boolean isExist = isExistInvoice(receipt.getReceiptNo());
        if (!isExist || receipt.getReceivedAmount() == 0)
            return false;*/

        if (receipt.getReceivedAmount() == 0)
            return false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_RECEIPT_NO, receipt.getReceiptNo());
            values.put(COL_RECEIVABLE_AMOUNT, receipt.getReceivedAmount());
            values.put(COL_RECEIPT_BALANCE, receipt.getCurrentBalanceAmount());
            values.put(COL_CREATED_AT, TextUtils.isEmpty(receipt.getLogDate()) ? getDateTime() : receipt.getLogDate());
            values.put(COL_FK_CUSTOMER_ID, receipt.getCustomerId());
            values.put(COL_UPLOAD_STATUS, receipt.getUploadStatus());
            values.put(COL_RECEIPT_LATITUDE, receipt.getLatitude());
            values.put(COL_RECEIPT_LONGITUDE, receipt.getLongitude());
            values.put(COL_RECEIPT_BANKID, receipt.getBankid());
            values.put(COL_RECEIPT_TYPE, receipt.getReceipt_type());
            values.put(COL_RECEIPT_BANK, receipt.getBankname());
            values.put(COL_RECEIPT_VOUCHERNO, receipt.getVoucherno());
            values.put(COL_RECEIPT_REFERNO, receipt.getReference_no());


            l = db.insert(TABLE_RECEIPT, null, values);
            db.close(); // Closing database connection
        } catch (SQLiteException e) {
            Log.d(TAG, "insertTransactionLog  Exception  " + e.getMessage());
        }

        return l != -1;

    }

    //getAllReceipts
    public ArrayList<Receipt> getAllReceipts() {

        final ArrayList<Receipt> receipts = new ArrayList<>();
        try {
//            String sql = "SELECT * FROM " + TABLE_RECEIPT_TRANSACTION + " WHERE " + COL_FK_INVOICE_COL_ID + " = '" + receiptLocId + "'";

            //String sql = "SELECT * FROM " + TABLE_RECEIPT+" WHERE " +COL_UPLOAD_STATUS+" ='Y'";
            String sql = "SELECT * FROM " + TABLE_RECEIPT+" WHERE " +COL_UPLOAD_STATUS+" ='N'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    Receipt r = new Receipt();
                    r.setLocalId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    r.setReceiptNo(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_NO)));
                    r.setReceivedAmount(cursor.getFloat(cursor.getColumnIndex(COL_RECEIVABLE_AMOUNT)));
                    r.setCurrentBalanceAmount(cursor.getFloat(cursor.getColumnIndex(COL_RECEIPT_BALANCE)));
                    r.setLogDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
                    r.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_FK_CUSTOMER_ID)));
                    r.setLatitude(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_LATITUDE)));
                    r.setLongitude(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_LONGITUDE)));
                    r.setReceipt_type(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_TYPE)));
                    r.setBankname(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_BANK)));
                    r.setBankid(cursor.getInt(cursor.getColumnIndex(COL_RECEIPT_BANKID)));
                    r.setVoucherno(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_VOUCHERNO)));
                    r.setReference_no(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_REFERNO)));

                    receipts.add(r);


                } while (cursor.moveToNext());


            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.d(TAG, "getAllReceipts  Exception  " + e.getMessage());

        }

        return receipts;
    }

    //getAllReceipts
    public ArrayList<Receipt> getCustomerWiseReceipt(int customerId) {

        final ArrayList<Receipt> receipts = new ArrayList<>();

        try {
            String sql = "SELECT * FROM " + TABLE_RECEIPT + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + customerId + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    Receipt r = new Receipt();
                    r.setLocalId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    r.setReceiptNo(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_NO)));
                    r.setReceivedAmount(cursor.getFloat(cursor.getColumnIndex(COL_RECEIVABLE_AMOUNT)));
                    r.setCurrentBalanceAmount(cursor.getFloat(cursor.getColumnIndex(COL_RECEIPT_BALANCE)));
                    r.setLogDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
                    r.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_FK_CUSTOMER_ID)));
                    receipts.add(r);

                } while (cursor.moveToNext());


            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.d(TAG, "getCustomerWiseReceipt  Exception  " + e.getMessage());

        }

        return receipts;
    }

    public boolean isExistInvoice(String receiptNo) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();                                         // COL_INVOICE_NO
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_RECEIPT + " WHERE " + COL_RECEIPT_NO + WHERE_CLAUSE, new String[]{String.valueOf(receiptNo)});


            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            Log.d(TAG, "isExistInvoice  Exception " + e.getMessage());
        }

        return count != 0;
    }

    /**
     * Sale functions
     */

    //insertQuotation
    //    insert sales
    public long insertSale(Sales sales) {
        Log.e("getTotal",""+sales.getTotal());
        Log.e("getTotal",""+sales.getDiscountAmount());

        Log.e("getPaid",""+sales.getPaid());
        Log.e("getInvoice_no",""+sales.getInvoice_no());

        sales.setTotal(sales.getDiscountAmount());



        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(COL_CUSTOMER_ID, sales.getCustomerId());
            values.put(COL_SALE_TYPE, sales.getSaleType());
            values.put(COL_SALE_TOTAL, sales.getTotal());
            values.put(COL_SALE_PAID, sales.getPaid());
            values.put(COL_INVOICE_CODE, sales.getInvoiceCode());
            values.put(COL_UPLOAD_STATUS, sales.getUploadStatus());
            values.put(COL_TAX_AMOUNT, sales.getTaxAmount());
            values.put(COL_TAX_PERCENTAGE, sales.getTaxPercentage());
            values.put(COL_WITHOUT_TAX, sales.getWithoutTaxTotal());
            values.put(COL_WITH_TAX_TOTAL, sales.getWithTaxTotal());
            values.put(COL_CUSTOMER_NAME,sales.getShopname());
            values.put(COL_CUSTOMER_CODE,sales.getShopcode());

            //values.put(COL_CREATED_AT, sales.getDate());

            //haris added on 06-11-2020
            values.put(COL_SALE_TAXABLE_TOTAL, sales.getTaxable_total());
            values.put(COL_SALE_CGST_TAX, sales.getCgst_tax());
            values.put(COL_SALE_SGST_TAX, sales.getSgst_tax());
            values.put(COL_SALE_TAX_RATE, sales.getCgst_tax_rate());
            values.put(COL_SALE_SGST_TAX_RATE, sales.getSgst_tax_rate());
            values.put(COL_SALE_TOTAL_DISCOUNT, sales.getDiscount_value());
            values.put(COL_SALE_TOTAL_DISCOUNT_RATE, sales.getDiscount_percentage());
            values.put(COL_SALE_ROUNDOFF_TOT, sales.getRoundofftot());
            values.put(COL_SALE_ROUNDOFF, sales.getRoundoff_value());
            values.put(COL_PAYMENT_TYPE, sales.getPayment_type());

            values.put(COL_DISCOUNT, sales.getDiscount());
            values.put(COL_WITH_DISCOUNT, sales.getDiscountAmount());

            values.put(COL_SALE_LATITUDE, sales.getSaleLatitude());
            values.put(COL_SALE_LONGITUDE, sales.getSaleLongitude());
            values.put(COL_SALE_ROUNDOFFTYPE, sales.getRoundoff_type());
            values.put(COL_TOTAL_DISCOUNT_TYPE,sales.getTotal_discount_type());
            values.put(COL_DISCOUNT_TOTAL_AMOUNT,sales.getDiscount_total_amount());
            values.put(COL_SALE_VAT_STATUS,sales.getVat_status());
            values.put(COL_INVOICE_NO_INT,sales.getInvoice_no());
            ////
            values.put(COL_CREATED_AT, getDateTime());

            // Inserting Row
            long l = db.insert(TABLE_SALE_CUSTOMER, null, values);

            db.close(); // Closing database connection

            if (l == -1) {
                return l;
            } else {

                updateVisitStatus(sales.getCustomerId(), REQ_SALE_TYPE, "","","");
                insertSaleProducts(Integer.valueOf(String.valueOf(l)), sales.getCartItems());
                return l;
            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertSale  Exception  " + e.getMessage());
            return -1;
        }
    }

    /*insert sale items*/
    private void insertSaleProducts(int customerId, ArrayList<CartItem> list) {

        long l = 0;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            for (CartItem cartItem : list) {

                Log.e("name",""+cartItem.getProductName());
                Log.e("Taxtype",""+cartItem.getTax_type());
                Log.e("getSgst",""+cartItem.getSgst());
                Log.e("txvalueeee",""+cartItem.getTaxValue());
                Log.e("getP_name",""+cartItem.getP_name());
                Log.e("prodtotal",""+ cartItem.getProductTotal());


                ContentValues values = new ContentValues();
                values.put(COL_PRODUCT_ID, cartItem.getProductId());
                values.put(COL_PRODUCT_PRICE, cartItem.getProductPrice());

                values.put(COL_PRODUCT_CODE, cartItem.getProductCode());
                values.put(COL_PRODUCT_NAME, cartItem.getProductName());
                values.put(COL_P_NAME, cartItem.getP_name());
                values.put(COL_PRODUCT_ARABIC_NAME, cartItem.getArabicName());

                values.put(COL_SALE_PRODUCT_QUANTITY, cartItem.getPieceQuantity());
                values.put(COL_SALE_TOTAL, cartItem.getTotalPrice());
                values.put(COL_PRODUCT_DISCOUNT, cartItem.getProductDiscount());
                values.put(COL_PRODUCT_TAX, cartItem.getTax());
                values.put(COL_BONUS_PERCENTAGE, cartItem.getProductBonus());
                values.put(COL_SALE_PRODUCT_ORDER_TYPE, cartItem.getOrderType());
                values.put(COL_SALE_ORDER_TYPE_QUANTITY, cartItem.getTypeQuantity());
                values.put(COL_PRODUCT_PEACE_PER_CART, cartItem.getPiecepercart());

                values.put(COL_SALE_PRODUCT_SIZE_STRING, cartItem.getSize_string());
                values.put(COL_SALE_PRODUCT_SIZEANDQTY_STRING, cartItem.getSizeandqty_string());
                values.put(COL_PRODUCT_HSNCODE, cartItem.getProduct_hsncode());
                //haris added on 20-04-21
                values.put(COL_PRODUCT_MRP, cartItem.getMrprate());
                values.put(COL_PRODUCT_TOTAL, cartItem.getProductTotal());
                values.put(COL_PRODUCT_TAX_AMOUNT, cartItem.getTaxValue());
                values.put(COL_SALE_PRODUCT_UNIT_SELECTED, cartItem.getUnitselected());

                values.put(COL_PRODUCT_QNTY_BYUNIT, cartItem.getPieceQuantity_nw());
                values.put(COL_SALE_PRODUCT_TAXTYPE, cartItem.getTax_type());
                values.put(COL_SALE_PRODUCT_BARCODE, cartItem.getBarcode());
                values.put(COL_SALE_PRODUCT_MFGDATE, cartItem.getMfg_date());
                values.put(COL_PRODUCT_SGST, cartItem.getSgst());
                values.put(COL_PRODUCT_CONFACTOR,cartItem.getCon_factor());
                values.put(COL_PRODUCT_DESCRIPTION,cartItem.getDescription());
                values.put(COL_PRODUCT_DISC_PERCENTAGE,cartItem.getDisc_percentage());
                values.put(COL_SALE_PRODUCT_FREEQNTY, cartItem.getFreeQty());
                values.put(COL_FK_CUSTOMER_ID, customerId);

                l = db.insert(TABLE_SALE_PRODUCTS, null, values);
            }

            if (db != null && db.isOpen()) {
                db.close(); // Closing database connection
            }

        } catch (SQLiteException | IllegalStateException e) {
            Log.v(TAG, "insertSaleProducts  Exception  " + e.getMessage());
        }
    }

    //    insert return offline
    public long insertOfflineReturn(Sales sales) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(COL_INVOICE_NO, sales.getInvoiceCode());
            values.put(COL_CUSTOMER_ID, sales.getCustomerId());
            values.put(COL_SALE_TOTAL, sales.getTotal());
            values.put(COL_GRAND_TOTAL, sales.getPaid());
            values.put(COL_UPLOAD_STATUS, sales.getUploadStatus());
            values.put(COL_SALE_LATITUDE, sales.getSaleLatitude());
            values.put(COL_SALE_LONGITUDE, sales.getSaleLongitude());

            // Inserting Row
            long l = db.insert(TABLE_RETURN_OFFLINE, null, values);

            db.close(); // Closing database connection

            if (l == -1) {
                return l;
            } else {

                updateVisitStatus(sales.getCustomerId(), REQ_SALE_TYPE, "","","");
                //  insertSaleProducts(Integer.valueOf(String.valueOf(l)), sales.getCartItems());
                insertOfflineReturnProducts(Integer.valueOf(String.valueOf(l)), sales.getCartItems());
                return l;
            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertSale  Exception  " + e.getMessage());
            return -1;
        }
    }

    /*insert offline return items*/

    private void insertOfflineReturnProducts(int customerId, ArrayList<CartItem> list) {

        long l = 0;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            for (CartItem cartItem : list) {

                ContentValues values = new ContentValues();

                values.put(COL_PRODUCT_ID, cartItem.getProductId());

                values.put(COL_UNIT_PRICE, cartItem.getProductPrice());
                values.put(COL_PRODUCT_TAX, cartItem.getTax());
                values.put(COL_RETURN_PRODUCT_QUANTITY, cartItem.getReturnQuantity());
                values.put(COL_RETURN_AMOUNT, cartItem.getTotalPrice());
                values.put(COL_PRODUCT_BONUS, cartItem.getProductBonus());
                values.put(COL_FK_CUSTOMER_ID, customerId);

                values.put(COL_PRODUCT_CONFACTORKG, cartItem.getConfactr_kg());
                values.put(COL_PRODUCT_UNITID, cartItem.getUnitid_selected());
                //values.put(COL_PRODUCT_UNITCONFACTOR, cartItem.getUnit_confactor());

                l = db.insert(TABLE_RETURN_OFFLINE_PRODUCTS, null, values);
            }

            if (db != null && db.isOpen())
            {
                db.close(); // Closing database connection
            }
        } catch (SQLiteException | IllegalStateException e) {
            Log.v(TAG, "insertSaleProducts  Exception  " + e.getMessage());
        }
    }


    //    Get All Sale list TABLE_SALE_CUSTOMER
    public ArrayList<Sales> getOfflineReturn() {

        ArrayList<Sales> list = new ArrayList<>();
        try {

            String sql = "SELECT * FROM " + TABLE_RETURN_OFFLINE+" WHERE " +COL_UPLOAD_STATUS+" ='N'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {
                    Sales s = new Sales();

                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_INVOICE_NO)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setTotal(cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL)));
                    s.setPaid(cursor.getDouble(cursor.getColumnIndex(COL_GRAND_TOTAL)));

                    getOfflineReturnProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.w(TAG, "getAllSales  Exception " + e.getMessage());
        }

        return list;
    }

    //    get offline return products corresponding to customer
    private Sales getOfflineReturnProduct(Sales sales, int id) {

        try {
            String sql = "SELECT * FROM " + TABLE_RETURN_OFFLINE_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + id + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            final ArrayList<CartItem> carts = new ArrayList<>();
            if (cursor.moveToFirst())
                do {

                    CartItem c = new CartItem();

                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setProductPrice(cursor.getInt(cursor.getColumnIndex(COL_UNIT_PRICE)));
                    c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));
                    c.setReturnQuantity(cursor.getInt(cursor.getColumnIndex(COL_RETURN_PRODUCT_QUANTITY)));
                    c.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_AMOUNT)));
                    c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_BONUS)));

                    Log.e("ProductPrice in db",""+c.getProductPrice());

                    carts.add(c);

                } while (cursor.moveToNext());

            sales.setCartItems(carts);

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getSaleProduct  Exception  " + e.getMessage());

        }

        return sales;
    }




    // get all invoice numbers from offline sale

    public ArrayList<InvoiceSales> getOfflineInvoiceNumber() {

        final ArrayList<InvoiceSales> invoicesarray = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_SALE_CUSTOMER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    InvoiceSales v = new InvoiceSales();

                    v.setInvoiceId(cursor.getString(cursor.getColumnIndex(COL_ID)));
                    v.setInvoiceNo(cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));

                    //   Log.e("getinvoice  Exceptio" , ""+cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));

                    invoicesarray.add(v);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "getinvoice  Exception  " + e.getMessage());
        }

        return invoicesarray;
    }

    // -------------------
    //    update sales

    public boolean updateSaleAmount(long salesId, float paidAmount) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_SALE_PAID, paidAmount);

            int result = db.update(TABLE_SALE_CUSTOMER, values, COL_ID + " =?", new String[]{String.valueOf(salesId)});

            db.close(); // Closing database connection

            if (result == -1) {
                return false;
            } else {
                return true;
            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertSale  Exception  " + e.getMessage());
            return false;
        }
    }

    //    Get All Sale list TABLE_SALE_CUSTOMER
    public ArrayList<Sales> getAllSales() {

        ArrayList<Sales> list = new ArrayList<>();
        try {

            // String sql = "SELECT * FROM " + TABLE_SALE_CUSTOMER;

           // String sql = "SELECT * FROM " + TABLE_SALE_CUSTOMER+" WHERE " +COL_UPLOAD_STATUS+" ='N'";
            String sql = "SELECT * FROM " + TABLE_SALE_CUSTOMER+" WHERE " +COL_UPLOAD_STATUS+" ='N'  ORDER BY " + COL_INVOICE_NO_INT + " ASC ";


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {
                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setInvoice_no(cursor.getInt(cursor.getColumnIndex(COL_INVOICE_NO_INT)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_SALE_TYPE)));
                    s.setDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
                    s.setTotal(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL)));

                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));
                    s.setUploadStatus(cursor.getString(cursor.getColumnIndex(COL_UPLOAD_STATUS)));
                    s.setPayment_type(cursor.getString(cursor.getColumnIndex(COL_PAYMENT_TYPE)));

                    s.setTaxAmount(cursor.getFloat(cursor.getColumnIndex(COL_TAX_AMOUNT)));
                    s.setTaxPercentage(cursor.getFloat(cursor.getColumnIndex(COL_TAX_PERCENTAGE)));
                    s.setWithoutTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITHOUT_TAX)));
                    s.setWithTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITH_TAX_TOTAL)));

                    s.setPaid(cursor.getFloat(cursor.getColumnIndex(COL_SALE_PAID)));

                    //haris added on 06-11-2020
                    s.setTaxable_total(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TAXABLE_TOTAL)));
                    s.setCgst_tax(cursor.getDouble(cursor.getColumnIndex(COL_SALE_CGST_TAX)));
                    s.setCgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_SALE_TAX_RATE)));

                    s.setDiscount_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL_DISCOUNT)));
                    s.setDiscount_percentage(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL_DISCOUNT_RATE)));
                    s.setRoundofftot(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF_TOT)));
                    s.setRoundoff_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF)));

                    s.setDiscount(cursor.getFloat(cursor.getColumnIndex(COL_DISCOUNT)));
                    s.setDiscountAmount(cursor.getFloat(cursor.getColumnIndex(COL_WITH_DISCOUNT)));

                    s.setSaleLatitude(cursor.getString(cursor.getColumnIndex(COL_SALE_LATITUDE)));
                    s.setSaleLongitude(cursor.getString(cursor.getColumnIndex(COL_SALE_LONGITUDE)));

                    s.setRoundoff_type(cursor.getString(cursor.getColumnIndex(COL_SALE_ROUNDOFFTYPE)));
                    s.setTotal_discount_type(cursor.getString(cursor.getColumnIndex(COL_TOTAL_DISCOUNT_TYPE)));
                    s.setDiscount_total_amount(cursor.getDouble(cursor.getColumnIndex(COL_DISCOUNT_TOTAL_AMOUNT)));


                    String str = cursor.getString(cursor.getColumnIndex(COL_CREATED_AT));
                    Log.w(TAG, "getAllSales  create date " + str);

                    getSaleProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.w(TAG, "getAllSales  Exception " + e.getMessage());
        }

        return list;
    }

    //    Get customer wise Sale list TABLE_SALE_CUSTOMER
    public ArrayList<Sales> getCustomerSales(String customerId) {

        ArrayList<Sales> list = new ArrayList<>();
        try {

//          String sql = "SELECT * FROM " + TABLE_SALE_CUSTOMER + " WHERE " + COL_CUSTOMER_ID + " = '" + customerId + "'";

            SQLiteDatabase db = this.getReadableDatabase();
//          Cursor cursor = db.rawQuery(sql, null);
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SALE_CUSTOMER + " WHERE " + COL_CUSTOMER_ID + WHERE_CLAUSE, new String[]{customerId});

            if (cursor.moveToFirst())
                do {
                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_SALE_TYPE)));

                    s.setTotal(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL)));

                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));
                    s.setPaid(cursor.getFloat(cursor.getColumnIndex(COL_SALE_PAID)));

                    //haris added on 06-11-2020
                    s.setTaxable_total(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TAXABLE_TOTAL)));
                    s.setCgst_tax(cursor.getDouble(cursor.getColumnIndex(COL_SALE_CGST_TAX)));
                    s.setCgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_SALE_TAX_RATE)));
                    s.setSgst_tax(cursor.getFloat(cursor.getColumnIndex(COL_SALE_SGST_TAX)));
                    s.setSgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_SALE_SGST_TAX_RATE)));
                    s.setDiscount_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL_DISCOUNT)));
                    s.setDiscount(cursor.getFloat(cursor.getColumnIndex(COL_DISCOUNT)));
                    s.setDiscount_percentage(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL_DISCOUNT_RATE)));
                    s.setTaxAmount(cursor.getFloat(cursor.getColumnIndex(COL_TAX_AMOUNT)));
                    s.setHsn_code(cursor.getString(cursor.getColumnIndex(COL_SALE_HSN_CODE)));
                    s.setRoundofftot(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF_TOT)));
                    s.setRoundoff_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF)));

                    s.setTaxPercentage(cursor.getFloat(cursor.getColumnIndex(COL_TAX_PERCENTAGE)));
                    s.setWithoutTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITHOUT_TAX)));
                    s.setWithTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITH_TAX_TOTAL)));
                    s.setPayment_type(cursor.getString(cursor.getColumnIndex(COL_PAYMENT_TYPE)));
                    s.setRoundoff_type(cursor.getString(cursor.getColumnIndex(COL_SALE_ROUNDOFFTYPE)));
                    s.setDiscount_total_amount(cursor.getDouble(cursor.getColumnIndex(COL_DISCOUNT_TOTAL_AMOUNT)));
                    s.setTotal_discount_type(cursor.getString(cursor.getColumnIndex(COL_TOTAL_DISCOUNT_TYPE)));
                    s.setUploadStatus(cursor.getString(cursor.getColumnIndex(COL_UPLOAD_STATUS)));
                    String createdDate = cursor.getString(cursor.getColumnIndex(COL_CREATED_AT));
                    s.setVat_status(cursor.getString(cursor.getColumnIndex(COL_SALE_VAT_STATUS)));
                    s.setDate(createdDate);
                    s.setInvoice_no(cursor.getInt(cursor.getColumnIndex(COL_INVOICE_NO_INT)));
                    getSaleProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));



                    Log.v(TAG, "getCustomerSales  date  " + createdDate);

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {

            Log.v(TAG, "getCustomerSales  Exception  " + e.getMessage());
        }

        return list;
    }

    //    get products list corresponding customer
    private Sales getSaleProduct(Sales sales, int id) {

        try {
            String sql = "SELECT * FROM " + TABLE_SALE_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + id + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            final ArrayList<CartItem> carts = new ArrayList<>();
            if (cursor.moveToFirst())
                do {

                    CartItem c = new CartItem();

                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setCartId(cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    c.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                    c.setProductPrice(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                    c.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL)));
                    c.setPieceQuantity(cursor.getInt(cursor.getColumnIndex(COL_SALE_PRODUCT_QUANTITY)));
                    c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));

                    c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_BONUS_PERCENTAGE)));
                    c.setTypeQuantity(cursor.getInt(cursor.getColumnIndex(COL_SALE_ORDER_TYPE_QUANTITY)));
                    c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));

                    c.setOrderType(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));
                    c.setOrderTypeName(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));
                    //haris added on 20-04-21
                    c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                    //
                    c.setTax_type(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_TAXTYPE)));
                    c.setSalePrice(getSalePrice(c.getProductPrice(), c.getTax(),c.getTax_type()));
                    c.setNetPrice(getWithoutTaxPrice(c.getProductPrice(), c.getTax()));
                    c.setProductTotal(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TOTAL)));
//                    c.setTaxValue(getTaxPrice(c.getProductPrice(), c.getTax()));

//                    c.setTaxValue(getTaxPrice(c.getProductTotal(),c.getTax(),c.getTax_type()));
                    c.setTaxValue(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TAX_AMOUNT)));
                    if(c.getTax_type().equals("TAX_INCLUSIVE")){
                        c.setProductPrice(c.getProductTotal()/c.getTypeQuantity());
                    }
                    //haris added on 06-11-2020
                    c.setProduct_hsncode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_HSNCODE)));

                    // c.setTaxValue(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX_AMOUNT)));
                    //haris added on 28-10-2020

                    c.setSize_string(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_SIZE_STRING)));
                    c.setSizeandqty_string(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_SIZEANDQTY_STRING)));
                    c.setUnitselected(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_UNIT_SELECTED)));
                    c.setProductDiscount(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_DISCOUNT)));
                    c.setPieceQuantity_nw(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QNTY_BYUNIT)));
                    c.setTax_type(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_TAXTYPE)));
                    c.setBarcode(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_BARCODE)));
                    c.setMfg_date(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_MFGDATE)));
                    c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                    c.setCon_factor(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_CONFACTOR)));
                    c.setDescription(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_DESCRIPTION)));
                    c.setDisc_percentage(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_DISC_PERCENTAGE)));
                    c.setFreeQty(cursor.getInt(cursor.getColumnIndex(COL_SALE_PRODUCT_FREEQNTY)));
                    c.setP_name(cursor.getString(cursor.getColumnIndex(COL_P_NAME)));
                    //   c.setMfg_date(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CGST)));

                    Log.e("txvalue innn",""+c.getTaxValue());
                    carts.add(c);

                } while (cursor.moveToNext());

            sales.setCartItems(carts);

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getSaleProduct  Exception  " + e.getMessage());

        }

        return sales;
    }


    //   Get collections wise TABLE_SALE_CUSTOMER
    public float getSalePaidAmount() {


        float paidAmount = 0;

        try {
            String sql = "SELECT " + COL_SALE_PAID + " FROM " + TABLE_SALE_CUSTOMER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    float d = cursor.getFloat(cursor.getColumnIndex(COL_SALE_PAID));
                    paidAmount += d;

                } while (cursor.moveToNext());

            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getSalePaidAmount  Exception  " + e.getMessage());

        }

        return paidAmount;
    }


    //   Get credit amount   TABLE_SALE_CUSTOMER customer wise in sales
    public double getCustomerSaleCreditedAmount(int customerId) {

        double paidAmount = 0, saleAmount = 0;

        try {

            String sql = "SELECT " + COL_SALE_PAID +","+COL_SALE_TOTAL+ " FROM " + TABLE_SALE_CUSTOMER+ " WHERE " + COL_CUSTOMER_ID + " = '" + customerId + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    double p = cursor.getDouble(cursor.getColumnIndex(COL_SALE_PAID));
                    paidAmount += p;
                    double s = cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL));
                    saleAmount += s;

                } while (cursor.moveToNext());

            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getCustomerCreditedAmount  Exception  " + e.getMessage());

        }

        return saleAmount-paidAmount;
    }


    /**
     * return functions
     */

    //    insert sales
    public long insertReturn(Sales sales) {
        Log.e("getRoundoff_value",""+sales.getRoundoff_value());
        Log.e("paymnt type",""+sales.getPayment_type());

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_CUSTOMER_ID, sales.getCustomerId());
            values.put(COL_RETURN_CODE, sales.getInvoiceCode());
            values.put(COL_RETURN_INVOICE_CODE, sales.getReturn_invoiceno());//autoincrement
            values.put(COL_UPLOAD_STATUS, sales.getUploadStatus());
            values.put(COL_RETURN_TYPE, sales.getSaleType());
            values.put(COL_RETURN_TOTAL, sales.getTotal());
            values.put(COL_TAX_TOTAL, sales.getTaxTotal());
            //values.put(COL_TAX_TOTAL, sales.getTaxTotal());

            values.put(COL_RETURN_REMARKS, "");
            values.put(COL_CREATED_AT, getDateTime());
            values.put(COL_CUSTOMER_NAME,sales.getShopname());
            values.put(COL_CUSTOMER_CODE,sales.getShopcode());
            values.put(COL_RETURN_TAXPERCENTAGE, sales.getDiscount_percentage());
            values.put(COL_RETURN_TAXAMOUNT, sales.getTaxAmount());
            values.put(COL_RETURN_WITHOUTTAX_TOTAL, sales.getWithoutTaxTotal());
            values.put(COL_RETURN_WITHTAX_TOTAL, sales.getWithTaxTotal());
            values.put(COL_RETURN_HSNCODE, sales.getHsn_code());
            values.put(COL_RETURN_CGSTRATE, sales.getCgst_tax_rate());
            values.put(COL_RETURN_SGSTRATE, sales.getSgst_tax_rate());
            values.put(COL_RETURN_CGSTAMOUNT, sales.getCgst_tax());
            values.put(COL_RETURN_SGSTAMOUNT, sales.getSgst_tax());
            values.put(COL_RETURN_DISCOUNTPERCENTAGE, sales.getDiscount_percentage());
            values.put(COL_RETURN_DISCOUNT_TOTAL, sales.getDiscount_value());
            values.put(COL_RETURN_DISCOUNT, sales.getDiscount());
            values.put(COL_PAYMENT_TYPE, sales.getPayment_type());
            values.put(COL_RETURN_TAXABLE_AMOUNT, sales.getTaxable_total());

            values.put(COL_SALE_LATITUDE, sales.getSaleLatitude());
            values.put(COL_SALE_LONGITUDE, sales.getSaleLongitude());
            values.put(COL_WITH_DISCOUNT, sales.getDiscountAmount());
            values.put(COL_SALE_ROUNDOFF_TOT, sales.getRoundoff_value());
            values.put(COL_RETURN_TYPE_WITHORWITHOUT,sales.getReturn_type());
            values.put(COL_RETURN_PAID, sales.getPaid());

            // Inserting Row
            long l = db.insert(TABLE_WO_RETURN_CUSTOMER, null, values);

            db.close(); // Closing database connection

            if (l == -1) {
                return l;
            } else {

                updateVisitStatus(sales.getCustomerId(), REQ_RETURN_TYPE, "","","");
                insertReturnProducts(Integer.valueOf(String.valueOf(l)), sales.getCartItems());
                return l;

            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertSale  Exception  " + e.getMessage());
            return -1;
        }
    }

    /*insert bank details*/

    public void  insertbankdetails(ArrayList<Banks> list){

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            for (Banks banks : list) {

                ContentValues values = new ContentValues();
                values.put(COL_BANK_ID, banks.getBank_id());
                values.put(COL_BANK_NAME, banks.getBank_name());
            }

            db.close();

        }catch (SQLiteException e){

            Log.v(TAG, "insertBanks  Exception  " + e.getMessage());

        }
    }


    /*insert return items*/
    private void insertReturnProducts(int returnId, ArrayList<CartItem> list) {
        long l = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();


            for (CartItem cartItem : list) {

                Log.e("qnt new prod",""+cartItem.getPieceQuantity_nw());
                Log.e("getTaxValue",""+cartItem.getTaxValue());
                ContentValues values = new ContentValues();
                values.put(COL_PRODUCT_ID, cartItem.getProductId());
                values.put(COL_PRODUCT_PRICE, cartItem.getProductPrice());

                values.put(COL_PRODUCT_CODE, cartItem.getProductCode());
                values.put(COL_PRODUCT_NAME, cartItem.getProductName());
                values.put(COL_PRODUCT_ARABIC_NAME, cartItem.getArabicName());
                values.put(COL_PRODUCT_TAX, cartItem.getTax());

                values.put(COL_RETURN_PRODUCT_QUANTITY, cartItem.getReturnQuantity());
                values.put(COL_RETURN_TOTAL, cartItem.getTotalPrice());


                values.put(COL_RETURN_PRODUCT_ORDER_TYPE, cartItem.getOrderType());  //order type use return type
                values.put(COL_RETURN_ORDER_TYPE_QUANTITY, cartItem.getTypeQuantity());
                values.put(COL_PRODUCT_PEACE_PER_CART, cartItem.getPiecepercart());

                values.put(COL_RETURN_PRODUCT_HSNCODE, cartItem.getProduct_hsncode());
                values.put(COL_RETURN_PRODUCT_SIZE_STRING, cartItem.getSize_string());
                values.put(COL_RETURN_PRODUCT_SIZEANDQTY_STRING, cartItem.getSizeandqty_string());
                values.put(COL_RETURN_PRODUCT_DISCOUNT, cartItem.getProductDiscount());
                values.put(COL_RETURN_PRODUCT_UNIT, cartItem.getUnitselected());
                values.put(COL_PRODUCT_QNTY_BYUNIT, cartItem.getPieceQuantity_nw());
                values.put(COL_PRODUCT_TOTAL, cartItem.getProductTotal());
                values.put(COL_RETURN_PRODUCT_TAXTYPE, cartItem.getTax_type());
                values.put(COL_PRODUCT_TAX_AMOUNT, cartItem.getTaxValue());
                values.put(COL_RETURN_PRODUCT_MFGDATE,cartItem.getMfg_date());
                values.put(COL_RETURN_PRODUCT_BARCODE,cartItem.getBarcode());
                values.put(COL_RETURN_PRODUCT_UNITID,cartItem.getUnitid_selected());
                values.put(COL_RETURN_PRODUCT_CONFACTORKG,cartItem.getConfactr_kg());
                values.put(COL_RETURN_PRODUCT_MRP,cartItem.getMrprate());
                values.put(COL_RETURN_PRODUCT_RATEKG,cartItem.getUnit_rate_kg());
                values.put(COL_RETURN_PRODUCT_RATEPCS,cartItem.getUnit_rate_pcs());
                values.put(COL_FK_RETURN_ID, returnId);

                l = db.insert(TABLE_WO_RETURN_PRODUCTS, null, values);

            }


            if (db != null && db.isOpen()) {
                db.close(); // Closing database connection
            }

        } catch (SQLiteException | IllegalStateException e) {
            Log.v(TAG, "insertReturnProducts  Exception  " + e.getMessage());
        }


    }


    //    Get All Sale list TABLE_SALE_CUSTOMER
    public ArrayList<Sales> getAllReturns() {

        ArrayList<Sales> list = new ArrayList<>();
        try {

            String sql = "SELECT * FROM " + TABLE_WO_RETURN_CUSTOMER+" WHERE " +COL_UPLOAD_STATUS+" ='N'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {
                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_RETURN_CODE)));
                    s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_RETURN_TYPE)));

                    s.setDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));

                    s.setTotal(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TOTAL)));
                    s.setTaxTotal(cursor.getDouble(cursor.getColumnIndex(COL_TAX_TOTAL)));

                    s.setTaxPercentage(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TAXPERCENTAGE)));
                    s.setTaxAmount(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TAXAMOUNT)));
                    s.setWithoutTaxTotal(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_WITHOUTTAX_TOTAL)));
                    s.setWithTaxTotal(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_WITHTAX_TOTAL)));
                    s.setHsn_code(cursor.getString(cursor.getColumnIndex(COL_RETURN_HSNCODE)));
                    s.setCgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_RETURN_CGSTRATE)));
                    s.setSgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_RETURN_SGSTRATE)));
                    s.setCgst_tax(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_CGSTAMOUNT)));
                    s.setSgst_tax(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_SGSTAMOUNT)));
                    s.setDiscount_percentage(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_DISCOUNTPERCENTAGE)));
                    s.setDiscount(cursor.getFloat(cursor.getColumnIndex(COL_RETURN_DISCOUNT)));
                    s.setTaxable_total(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TAXABLE_AMOUNT)));

                    s.setSaleLatitude(cursor.getString(cursor.getColumnIndex(COL_SALE_LATITUDE)));
                    s.setSaleLongitude(cursor.getString(cursor.getColumnIndex(COL_SALE_LONGITUDE)));
                    s.setTaxTotal(cursor.getDouble(cursor.getColumnIndex(COL_TAX_TOTAL)));
                    s.setDiscountAmount(cursor.getDouble(cursor.getColumnIndex(COL_WITH_DISCOUNT)));
                    s.setRoundoff_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF_TOT)));
                    s.setPaid(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_PAID)));

                    String remarks = cursor.getString(cursor.getColumnIndex(COL_RETURN_REMARKS));




                    getReturnProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));


                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {
            Log.w(TAG, "getAllSales  Exception " + e.getMessage());
        }

        return list;
    }

    //    Get customer wise return list TABLE_WO_RETURN_CUSTOMER
    public ArrayList<Sales> getCustomerReturns(String customerId) {

        ArrayList<Sales> list = new ArrayList<>();
        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WO_RETURN_CUSTOMER + " WHERE " + COL_CUSTOMER_ID + WHERE_CLAUSE, new String[]{customerId});

            if (cursor.moveToFirst())
                do {
                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_RETURN_CODE)));
                    s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_RETURN_TYPE)));

                    s.setDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));

                    s.setTotal(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TOTAL)));
                    s.setTaxTotal(cursor.getDouble(cursor.getColumnIndex(COL_TAX_TOTAL)));
                    s.setDiscount(cursor.getFloat(cursor.getColumnIndex(COL_RETURN_DISCOUNT)));
                    s.setDiscount_value(cursor.getFloat(cursor.getColumnIndex(COL_RETURN_DISCOUNT_TOTAL)));
                    s.setTaxAmount(cursor.getFloat(cursor.getColumnIndex(COL_RETURN_TAXAMOUNT)));
                    String remarks = cursor.getString(cursor.getColumnIndex(COL_RETURN_REMARKS));
                    s.setReturn_invoiceno(cursor.getString(cursor.getColumnIndex(COL_RETURN_INVOICE_CODE)));
                    s.setRoundoff_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF_TOT)));
                    s.setReturn_type(cursor.getString(cursor.getColumnIndex(COL_RETURN_TYPE_WITHORWITHOUT)));


                    String createdDate = cursor.getString(cursor.getColumnIndex(COL_CREATED_AT));

                    getReturnProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.e(TAG, "getCustomerSales  Exception  " + e.getMessage());
        }

        return list;
    }

    //    Get customer wise return list TABLE_WO_RETURN_CUSTOMER
    public ArrayList<Sales> getCustomerQuotation(String customerId) {

        ArrayList<Sales> list = new ArrayList<>();
        try {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUOTATION_CUSTOMER + " WHERE " + COL_CUSTOMER_ID + WHERE_CLAUSE, new String[]{customerId});

            if (cursor.moveToFirst())
                do {
                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_SALE_TYPE)));

                    s.setTotal(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL)));

                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));
                    //  s.setPaid(cursor.getFloat(cursor.getColumnIndex(COL_SALE_PAID)));

                    //haris added on 06-11-2020
                    s.setTaxable_total(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TAXABLE_TOTAL)));
                    s.setCgst_tax(cursor.getDouble(cursor.getColumnIndex(COL_SALE_CGST_TAX)));
                    s.setCgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_SALE_TAX_RATE)));
                    s.setSgst_tax(cursor.getFloat(cursor.getColumnIndex(COL_SALE_SGST_TAX)));
                    s.setSgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_SALE_SGST_TAX_RATE)));
                    s.setDiscount_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL_DISCOUNT)));
                    s.setDiscount(cursor.getFloat(cursor.getColumnIndex(COL_DISCOUNT)));
                    s.setDiscount_percentage(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL_DISCOUNT_RATE)));
                    s.setTaxAmount(cursor.getFloat(cursor.getColumnIndex(COL_TAX_AMOUNT)));
                    s.setHsn_code(cursor.getString(cursor.getColumnIndex(COL_SALE_HSN_CODE)));

                    s.setTaxPercentage(cursor.getFloat(cursor.getColumnIndex(COL_TAX_PERCENTAGE)));
                    s.setWithoutTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITHOUT_TAX)));
                    s.setWithTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITH_TAX_TOTAL)));
                    s.setRoundoff_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF)));
                    s.setRoundofftot(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF_TOT)));

                    String createdDate = cursor.getString(cursor.getColumnIndex(COL_CREATED_AT));
                    s.setDate(createdDate);
                    s.setTotalkg(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTALKG)));
                    s.setVat_status(cursor.getString(cursor.getColumnIndex(COL_SALE_VAT_STATUS)));

                    getQuotationsProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.e(TAG, "getQuotationsSales  Exception  " + e.getMessage());
        }

        return list;
    }

    //    get products list corresponding customer
    private Sales getReturnProduct(Sales returns, int returnId) {

        try {

            String sql = "SELECT * FROM " + TABLE_WO_RETURN_PRODUCTS + " WHERE " + COL_FK_RETURN_ID + " = '" + returnId + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            final ArrayList<CartItem> carts = new ArrayList<>();
            if (cursor.moveToFirst())
                do {

                    CartItem r = new CartItem();

                    r.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    r.setCartId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    r.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    r.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    r.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                    r.setProductPrice(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                    r.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TOTAL)));
                    r.setReturnQuantity(cursor.getInt(cursor.getColumnIndex(COL_RETURN_PRODUCT_QUANTITY)));
                    r.setPieceQuantity(r.getReturnQuantity());
                    r.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));
                    r.setProductTotal(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TOTAL)));
                    r.setTypeQuantity(cursor.getInt(cursor.getColumnIndex(COL_RETURN_ORDER_TYPE_QUANTITY)));
                    r.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                    r.setOrderType(cursor.getString(cursor.getColumnIndex(COL_RETURN_PRODUCT_ORDER_TYPE)));//order type use return type
                    r.setTax_type(cursor.getString(cursor.getColumnIndex(COL_RETURN_PRODUCT_TAXTYPE)));
                    r.setSalePrice(getSalePrice(r.getProductPrice(), r.getTax(), r.getTax_type()));
                    r.setNetPrice(getWithoutTaxPrice(r.getProductPrice(), r.getTax()));


                    //haris added on 16-11-2020
                    r.setProduct_hsncode(cursor.getString(cursor.getColumnIndex(COL_RETURN_PRODUCT_HSNCODE)));

                    r.setProductDiscount(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_PRODUCT_DISCOUNT)));
                    // r.setTaxValue(getTaxPrice((r.getProductPrice()-r.getProductDiscount()), r.getTax()));

                    r.setTaxValue(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TAX_AMOUNT)));

//                    if(r.getTax_type().equals("TAX_INCLUSIVE")){
//                        r.setProductPrice(r.getProductTotal()/r.getPieceQuantity_nw());
//                    }
                    r.setUnitselected(cursor.getString(cursor.getColumnIndex(COL_RETURN_PRODUCT_UNIT)));
                    r.setPieceQuantity_nw(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QNTY_BYUNIT)));
                    r.setMfg_date(cursor.getString(cursor.getColumnIndex(COL_RETURN_PRODUCT_MFGDATE)));
                    r.setBarcode(cursor.getString(cursor.getColumnIndex(COL_RETURN_PRODUCT_BARCODE)));
                    r.setUnitid_selected(cursor.getString(cursor.getColumnIndex(COL_RETURN_PRODUCT_UNITID)));
                    r.setConfactr_kg(cursor.getInt(cursor.getColumnIndex(COL_RETURN_PRODUCT_CONFACTORKG)));
                    r.setMrprate(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_PRODUCT_MRP)));
                    r.setUnit_rate_kg(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_PRODUCT_RATEKG)));
                    r.setUnit_rate_pcs(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_PRODUCT_RATEPCS)));

                    carts.add(r);

                } while (cursor.moveToNext());

            returns.setCartItems(carts);

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getReturnProduct  Exception  " + e.getMessage());

        }

        return returns;
    }




    public ArrayList<Sales> getCustomerInvoiceReturns(String customerId) {

        ArrayList<Sales> list = new ArrayList<>();
        try {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RETURN_OFFLINE + " WHERE " + COL_CUSTOMER_ID + WHERE_CLAUSE, new String[]{customerId});

            if (cursor.moveToFirst())
                do {
                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_INVOICE_NO)));

                    //   s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_RETURN_TYPE)));

                    //  s.setDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));

                    s.setTotal(cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL)));
                    s.setTaxTotal(cursor.getDouble(cursor.getColumnIndex(COL_GRAND_TOTAL)));

                    /*String remarks = cursor.getString(cursor.getColumnIndex(COL_RETURN_REMARKS));

                    getReturnProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    String createdDate = cursor.getString(cursor.getColumnIndex(COL_CREATED_AT));*/

                    getReturnInvoiceProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.e(TAG, "getCustomerSales  Exception  " + e.getMessage());
        }

        return list;
    }


    //    get products list corresponding customer
    private Sales getReturnInvoiceProduct(Sales returns, int returnId) {

        try {
            String sql = "SELECT * FROM " + TABLE_RETURN_OFFLINE_PRODUCTS + " WHERE " + COL_ID + " = '" + returnId + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            final ArrayList<CartItem> carts = new ArrayList<>();
            if (cursor.moveToFirst())
                do {

                    CartItem r = new CartItem();

                    r.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    r.setCartId(cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    r.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    r.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    r.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                    r.setProductPrice(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                    r.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TOTAL)));
                    r.setReturnQuantity(cursor.getInt(cursor.getColumnIndex(COL_RETURN_PRODUCT_QUANTITY)));
                    r.setPieceQuantity(r.getReturnQuantity());
                    r.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));

                    r.setTypeQuantity(cursor.getInt(cursor.getColumnIndex(COL_RETURN_ORDER_TYPE_QUANTITY)));
                    r.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                    r.setOrderType(cursor.getString(cursor.getColumnIndex(COL_RETURN_PRODUCT_ORDER_TYPE)));//order type use return type

                    r.setSalePrice(getSalePrice(r.getProductPrice(), r.getTax(),r.getTax_type()));
                    r.setNetPrice(getWithoutTaxPrice(r.getProductPrice(), r.getTax()));
                    r.setTaxValue(getTaxPrice(r.getProductPrice(), r.getTax(),r.getTax_type()));

                    carts.add(r);

                } while (cursor.moveToNext());

            returns.setCartItems(carts);

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getReturnProduct  Exception  " + e.getMessage());

        }

        return returns;
    }


    /**
     * invoice numbers
     */
    //   Get last Invoice number
    public String getLastInvoiceNumber() {


//        String lastInv = new SessionValue(context).getInvoicePrefix() + new SessionValue(context).getInvoiceNo();

        String lastInv = "bn11";
        if (!isExistSale())
            return lastInv;

        try {
//            String sql =  "SELECT " + COL_INVOICE_CODE + " FROM " + TABLE_SALE_CUSTOMER ;

            String sql = "SELECT " + COL_INVOICE_CODE + " FROM " + TABLE_SALE_CUSTOMER + " ORDER BY " + COL_INVOICE_CODE + " DESC LIMIT 1 ";


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor != null) {
                cursor.moveToLast();

                lastInv = (cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));
                Log.v(TAG, "getLastInvoiceNumber  -1   " + lastInv);
                cursor.close();
                db.close();

            }


        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {

            Log.v(TAG, "getLastInvoiceNumber  Exception  " + e.getMessage());

            return lastInv;
        }

        return lastInv;
    }

    //   Get last Invoice number
    public String getLastContainsInvoiceNumber(String routeCode) {


        String lastInv = routeCode + new SessionValue(context).getInvoiceCode(routeCode);

        if (!isExistSale())
            return lastInv;

        try {

            String sql = "SELECT * FROM " + TABLE_SALE_CUSTOMER + " WHERE " + COL_INVOICE_CODE + " LIKE '%" + routeCode + "%'" + " ORDER BY " + COL_INVOICE_CODE + " DESC LIMIT 1 ";


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

/*            Cursor cursor = db.query(TABLE_SALE_CUSTOMER, new String[]{"*"}, COL_INVOICE_CODE , new String[]{sereis},
                    "  LIKE '%" + sereis + "%'  ", null, "ORDER BY DESC LIMIT 1 ");*/

//            Cursor cursor = db.query(TABLE_SALE_CUSTOMER, new String[]{"*"}, COL_INVOICE_CODE , new String[]{"'%" + sereis + "%'" },
//                    "  LIKE " , null, COL_INVOICE_CODE+" DESC LIMIT 1 ");


            if (cursor != null) {
                cursor.moveToLast();

                lastInv = (cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));
                Log.v(TAG, "getInvoiceNumber  2   " + lastInv);
                cursor.close();
                db.close();

            }


        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {

            Log.v(TAG, "getInvoiceNumber  2  Exception  " + e.getMessage());

            return lastInv;
        }

        return lastInv;
    }

    //   Get  Invoice number
    public String getInvoiceNumber() {


        String lastInv = "";

        try {

            String sql = "SELECT " + COL_ID + "," + COL_INVOICE_CODE + " FROM " + TABLE_SALE_CUSTOMER;


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {
                    lastInv = (cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));
                    int id = (cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    Log.v(TAG, "getInvoiceNumber 1   id  " + id + " -  " + lastInv);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException | CursorIndexOutOfBoundsException e) {

            Log.v(TAG, "getInvoiceNumber 1  Exception  " + e.getMessage());

            return lastInv;
        }

        return lastInv;
    }

    /**
     * Count Row From to SQLite  "TABLE_SALE_CUSTOMER" Table
     */

    private boolean isExistSale() {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_SALE_CUSTOMER + " WHERE " + COL_CONTACT_ID + WHERE_CLAUSE, new String[]{contactId});

            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_SALE_CUSTOMER, new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;


    }

    /**
     * Quotation functions
     */
    //    insert Quotation
    public boolean insertQuotation(Sales sales) {
        Log.e("invcode",sales.getInvoiceCode());
        Log.e("roundoff",""+sales.getRoundoff_value());
        Log.e("roundofftotal",""+sales.getRoundofftot());
        Log.e("totalkg",""+sales.getTotalkg());


        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();



            values.put(COL_CUSTOMER_ID, sales.getCustomerId());
            values.put(COL_INVOICE_CODE, sales.getInvoiceCode());
            values.put(COL_SALE_TOTAL, sales.getTotal());
            // values.put(COL_SALE_PAID, sales.getPaid());
            values.put(COL_INVOICE_CODE, sales.getInvoiceCode());
            values.put(COL_UPLOAD_STATUS, sales.getUploadStatus());
            values.put(COL_TAX_AMOUNT, sales.getTaxAmount());
            values.put(COL_TAX_PERCENTAGE, sales.getTaxPercentage());
            values.put(COL_WITHOUT_TAX, sales.getWithoutTaxTotal());
            values.put(COL_WITH_TAX_TOTAL, sales.getWithTaxTotal());
            values.put(COL_SALE_TYPE, sales.getSaleType());

            //haris added on 06-11-2020
            values.put(COL_SALE_TAXABLE_TOTAL, sales.getTaxable_total());
            values.put(COL_SALE_CGST_TAX, sales.getCgst_tax());
            values.put(COL_SALE_SGST_TAX, sales.getSgst_tax());
            values.put(COL_SALE_TAX_RATE, sales.getCgst_tax_rate());
            values.put(COL_SALE_SGST_TAX_RATE, sales.getSgst_tax_rate());
            values.put(COL_SALE_TOTAL_DISCOUNT, sales.getDiscount_value());
            values.put(COL_SALE_TOTAL_DISCOUNT_RATE, sales.getDiscount_percentage());
            values.put(COL_SALE_ROUNDOFF_TOT, sales.getRoundofftot());
            values.put(COL_SALE_ROUNDOFF, sales.getRoundoff_value());

            values.put(COL_DISCOUNT, sales.getDiscount());
            values.put(COL_WITH_DISCOUNT, sales.getDiscountAmount());

            values.put(COL_SALE_LATITUDE, sales.getSaleLatitude());
            values.put(COL_SALE_LONGITUDE, sales.getSaleLongitude());
            values.put(COL_CREATED_AT, getDateTime());
            values.put(COL_SALE_TAX_TYPE, sales.getTax_type());
            values.put(COL_SALE_TOTALKG,sales.getTotalkg());
            values.put(COL_SALE_VAT_STATUS,sales.getVat_status());

            // Inserting Row

            long l = db.insert(TABLE_QUOTATION_CUSTOMER, null, values);

            db.close(); // Closing database connection

            if (l == -1) {
                return false;
            } else {

                updateVisitStatus(sales.getCustomerId(), REQ_QUOTATION_TYPE, "","","");
                insertQuotationProducts(Integer.valueOf(String.valueOf(l)), sales.getCartItems());
                return true;

            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertQuotation  Exception  " + e.getMessage());
            return false;
        }
    }

//    /*insert Quotation items*/
//    private void insertQuotationProducts(int saleId, ArrayList<CartItem> list) {
//
//        long l = 0;
//
//        try {
//
//            SQLiteDatabase db = this.getWritableDatabase();
//
//            for (CartItem cartItem : list) {
//
//                ContentValues values = new ContentValues();
//                values.put(COL_PRODUCT_ID, cartItem.getProductId());
//                values.put(COL_PRODUCT_PRICE, cartItem.getProductPrice());
//                values.put(COL_SALE_PRODUCT_QUANTITY, cartItem.getPieceQuantity());
//                values.put(COL_SALE_TOTAL, cartItem.getTotalPrice());
//                values.put(COL_FK_CUSTOMER_ID, saleId);
//
//                values.put(COL_PRODUCT_TAX, cartItem.getTax());
//
//
//                l = db.insert(TABLE_QUOTATION_PRODUCTS, null, values);
//
//            }
//            db.close(); // Closing database connection
//
//
//        } catch (SQLiteException e) {
//            Log.v(TAG, "insertQuotationProducts  Exception  " + e.getMessage());
//        }
//
//    }

    /*insert Quotation items*/ //haris edited on 29-01-2021
    private void insertQuotationProducts(int saleId, ArrayList<CartItem> list) {

        long l = 0;

        //  try {

        SQLiteDatabase db = this.getWritableDatabase();

        for (CartItem cartItem : list) {


            Log.e("getProductPrice",""+cartItem.getProductPrice());
            Log.e("getProductName",""+cartItem.getProductName());
            Log.e("getCgst",""+cartItem.getCgst());
            Log.e("txvluee" +"",""+getAmount(cartItem.getCgst()*2));
            Log.e("getNetPrice db",""+cartItem.getNetPrice());

            Log.e("getTaxValue",""+cartItem.getTaxValue());
            Log.e("cartItem.getMrprate()",""+ cartItem.getMrprate());

            ContentValues values = new ContentValues();
            values.put(COL_PRODUCT_ID, cartItem.getProductId());
            values.put(COL_PRODUCT_PRICE, cartItem.getProductPrice());

            values.put(COL_PRODUCT_CODE, cartItem.getProductCode());
            values.put(COL_PRODUCT_NAME, cartItem.getProductName());
            // values.put(COL_PRODUCT_ARABIC_NAME, cartItem.getArabicName());

            values.put(COL_SALE_PRODUCT_QUANTITY, cartItem.getPieceQuantity());
            values.put(COL_SALE_TOTAL, cartItem.getTotalPrice());
            values.put(COL_SALE_PRODUCT_UNIT_SELECTED, cartItem.getUnitselected());

            values.put(COL_PRODUCT_TAX, cartItem.getTax());
            values.put(COL_BONUS_PERCENTAGE, cartItem.getProductBonus());
            values.put(COL_SALE_PRODUCT_ORDER_TYPE, cartItem.getOrderType());
            values.put(COL_SALE_ORDER_TYPE_QUANTITY, cartItem.getTypeQuantity());
            values.put(COL_PRODUCT_PEACE_PER_CART, cartItem.getPiecepercart());

            values.put(COL_SALE_PRODUCT_SIZE_STRING, cartItem.getSize_string());
            values.put(COL_SALE_PRODUCT_SIZEANDQTY_STRING, cartItem.getSizeandqty_string());
            values.put(COL_PRODUCT_HSNCODE, cartItem.getProduct_hsncode());
            values.put(COL_FK_CUSTOMER_ID, saleId);

            values.put(COL_PRODUCT_DISCOUNT, cartItem.getProductDiscount());
            values.put(COL_PRODUCT_TOTAL, cartItem.getProductTotal());
            values.put(COL_PRODUCT_TOTAL_VALUE, cartItem.getProductTotalValue());
//            values.put(COL_PRODUCT_CGST, cartItem.getTaxValue()/2);
//            values.put(COL_PRODUCT_SGST, cartItem.getTaxValue()/2);
            values.put(COL_PRODUCT_CGST, cartItem.getCgst());
            values.put(COL_PRODUCT_SGST, cartItem.getCgst());
            values.put(COL_PRODUCT_QNTY_BYUNIT, cartItem.getPieceQuantity_nw());
            values.put(COL_SALE_PRODUCT_TAXTYPE, cartItem.getTax_type());
            values.put(COL_PRODUCT_TAX_AMOUNT, getAmount(cartItem.getCgst()*2));
            values.put(COL_PRODUCT_MRP, cartItem.getMrprate());
            values.put(COL_PRODUCT_CONFACTORKG, cartItem.getConfactr_kg());

            values.put(COL_PRODUCT_UNITID, cartItem.getUnitid_selected());
            values.put(COL_PRODUCT_UNITCONFACTOR, cartItem.getUnit_confactor());
            values.put(COL_PRODUCT_NETPRICE, cartItem.getNetPrice());
            values.put(COL_PRODUCT_KGM_PRICE,cartItem.getPrice_kgm());
            values.put(COL_PRODUCT_PCS_PRICE,cartItem.getPc_price());


            l = db.insert(TABLE_QUOTATION_PRODUCTS, null, values);

        }
        db.close(); // Closing database connection


//        } catch (SQLiteException e) {
//            Log.v(TAG, "insertQuotationProducts  Exception  " + e.getMessage());
//        }

    }

    //    Get All Sale list TABLE_QUOTATION_CUSTOMER
    public ArrayList<Sales> getAllQuotations() {

        ArrayList<Sales> list = new ArrayList<>();
        try {

            String sql = "SELECT * FROM " + TABLE_QUOTATION_CUSTOMER+" WHERE " +COL_UPLOAD_STATUS+" ='N'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {
                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_SALE_TYPE)));
                    s.setDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
                    s.setTotal(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL)));
                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));
                    s.setUploadStatus(cursor.getString(cursor.getColumnIndex(COL_UPLOAD_STATUS)));
                    s.setTaxAmount(cursor.getFloat(cursor.getColumnIndex(COL_TAX_AMOUNT)));
                    s.setTaxPercentage(cursor.getFloat(cursor.getColumnIndex(COL_TAX_PERCENTAGE)));
                    s.setWithoutTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITHOUT_TAX)));
                    s.setWithTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITH_TAX_TOTAL)));
                    //s.setPaid(cursor.getFloat(cursor.getColumnIndex(COL_SALE_PAID)));
                    //haris added on 06-11-2020
                    s.setTaxable_total(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TAXABLE_TOTAL)));
                    s.setCgst_tax(cursor.getDouble(cursor.getColumnIndex(COL_SALE_CGST_TAX)));
                    s.setCgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_SALE_TAX_RATE)));
                    s.setSgst_tax(cursor.getFloat(cursor.getColumnIndex(COL_SALE_SGST_TAX)));
                    s.setSgst_tax_rate(cursor.getString(cursor.getColumnIndex(COL_SALE_SGST_TAX_RATE)));
                    s.setDiscount_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL_DISCOUNT)));
                    s.setDiscount_percentage(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL_DISCOUNT_RATE)));
                    s.setDiscount(cursor.getFloat(cursor.getColumnIndex(COL_DISCOUNT)));
                    s.setDiscountAmount(cursor.getFloat(cursor.getColumnIndex(COL_WITH_DISCOUNT)));
                    s.setSaleLatitude(cursor.getString(cursor.getColumnIndex(COL_SALE_LATITUDE)));
                    s.setSaleLongitude(cursor.getString(cursor.getColumnIndex(COL_SALE_LONGITUDE)));
                    s.setRoundoff_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF)));
                    s.setRoundofftot(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF_TOT)));
                    s.setTax_type(cursor.getString(cursor.getColumnIndex(COL_SALE_TAX_TYPE)));
                    s.setTotalkg(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTALKG)));

                    String str = cursor.getString(cursor.getColumnIndex(COL_CREATED_AT));
                    Log.w(TAG, "getAllSales  create date " + str);

                    getQuotationsProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));


                    list.add(s);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {

            Log.v(TAG, "getAllQuotations  " + e.getMessage());
        }

        return list;
    }

    //    get Quotations products list corresponding customer
    private Sales getQuotationsProduct(Sales sales, int id) {

        //  try {
        String sql = "SELECT * FROM " + TABLE_QUOTATION_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + id + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        final ArrayList<CartItem> carts = new ArrayList<>();
        if (cursor.moveToFirst())
            do {

                CartItem c = new CartItem();

                c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                c.setCartId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                c.setProductPrice(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                c.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL)));
                c.setPieceQuantity(cursor.getInt(cursor.getColumnIndex(COL_SALE_PRODUCT_QUANTITY)));
                c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));
                c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_BONUS_PERCENTAGE)));
                c.setTypeQuantity(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ORDER_TYPE_QUANTITY)));
                c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                c.setOrderType(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));
                c.setOrderTypeName(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));
                //haris added on 20-04-21
//                    c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                c.setSalePrice(0);
                //c.setSalePrice(getSalePrice(c.getProductPrice(), c.getTax(),c.getTax_type()));
                c.setNetPrice(getWithoutTaxPrice(c.getProductPrice(), c.getTax()));
                c.setProductTotal(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TOTAL)));
                // c.setTaxValue(getTaxPrice(c.getProductTotal(),c.getTax(),c.getTax_type()));
                c.setTaxValue(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TAX_AMOUNT)));
                c.setProduct_hsncode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_HSNCODE)));
                c.setUnitselected(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_UNIT_SELECTED)));
                c.setProductDiscount(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_DISCOUNT)));
                c.setSgst((float) cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_SGST)));
                c.setCgst((float) cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_CGST)));
                c.setPieceQuantity_nw(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_QNTY_BYUNIT)));
                c.setTax_type(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_TAXTYPE)));
                c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                c.setConfactr_kg(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_CONFACTORKG)));
                c.setUnitid_selected(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITID)));
                c.setUnit_confactor(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITCONFACTOR)));
                c.setPrice_kgm(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_KGM_PRICE)));
                c.setPc_price(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PCS_PRICE)));

                carts.add(c);

            } while (cursor.moveToNext());

        sales.setCartItems(carts);

        cursor.close();

        db.close();
//        } catch (SQLiteException e) {
//            Log.v(TAG, "getQuotationsProduct  Exception  " + e.getMessage());
//
//        }

        return sales;
    }

    //   Get today sale wise TABLE_SALE_CUSTOMER
    public float getTodaySaleAmount() {


        float saleAmount = 0;


        try {
            //String sql = "SELECT " + COL_SALE_TOTAL + " FROM " + TABLE_SALE_CUSTOMER;
            String sql = "SELECT " + COL_WITH_TAX_TOTAL + " FROM " + TABLE_SALE_CUSTOMER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    //float f = cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL));
                    float f = cursor.getFloat(cursor.getColumnIndex(COL_WITH_TAX_TOTAL));
                    saleAmount += f;


                } while (cursor.moveToNext());

            cursor.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getTodaySaleAmount  Exception  " + e.getMessage());

        }

        return saleAmount;
    }

    /**
     * visiting status functions
     */


    /*Add customer visit status*/
    private void insertVisitStatus(int customerId) {

        long l = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_VISIT_SALE, false);
            values.put(COL_VISIT_QUOTATION, false);
            values.put(COL_VISIT_RETURN, false);
            values.put(COL_VISIT_RECEIPT, false);
            values.put(COL_VISIT_NO_SALE, false);
            values.put(COL_VISIT_DATE_TIME, "");
            values.put(COL_VISIT_NO_SALE_REASON, "");
            values.put(COL_VISIT_NO_SALE_LATITUDE, "");
            values.put(COL_VISIT_NO_SALE_LONGITUDE, "");

            values.put(COL_FK_CUSTOMER_ID, customerId);

            l = db.insert(TABLE_CUSTOMER_VISIT, null, values);



            db.close(); // Closing database connection


        } catch (SQLiteException e) {
            Log.v(TAG, "insertStatus  Exception  " + e.getMessage());
        }

    }

    //    update Visit Status
    public boolean updateVisitStatus(int id, int type, String reason, String latd, String longtd) {
        Log.e("latd",latd);
        Log.e("longtd",longtd);

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            switch (type) {
                case REQ_SALE_TYPE:
                    values.put(COL_VISIT_SALE, true);
                    values.put(COL_VISIT_NO_SALE, false);
                    values.put(COL_VISIT_NO_SALE_REASON, "");

                    break;
                case REQ_QUOTATION_TYPE:

                    values.put(COL_VISIT_QUOTATION, true);
                    values.put(COL_VISIT_NO_SALE, false);
                    values.put(COL_VISIT_NO_SALE_REASON, "");

                    break;
                case REQ_RETURN_TYPE:
                    values.put(COL_VISIT_RETURN, true);
                    values.put(COL_VISIT_NO_SALE, false);
                    values.put(COL_VISIT_NO_SALE_REASON, "");


                    break;
                case REQ_RECEIPT_TYPE:

                    values.put(COL_VISIT_RECEIPT, true);

                    values.put(COL_VISIT_NO_SALE, false);
                    values.put(COL_VISIT_NO_SALE_REASON, "");

                    break;
                case REQ_NO_SALE_TYPE:

                    values.put(COL_VISIT_NO_SALE, true);
                    values.put(COL_VISIT_NO_SALE_REASON, reason);
                    values.put(COL_VISIT_DATE_TIME, getDateTime());

                    values.put(COL_VISIT_NO_SALE_LATITUDE, latd);
                    values.put(COL_VISIT_NO_SALE_LONGITUDE, longtd);

                    break;

                case REQ_RECEIPT_BILLWISE:

                    values.put(COL_VISIT_RECEIPT_BILLWISE, true);
                    values.put(COL_VISIT_NO_SALE, false);
                    values.put(COL_VISIT_NO_SALE_REASON, "");

                    break;

            }


            int result = db.update(TABLE_CUSTOMER_VISIT, values, COL_FK_CUSTOMER_ID + " =?", new String[]{String.valueOf(id)});


            return result != -1;
        } catch (SQLiteException e) {
            Log.v(TAG, "updateVisitStatus  Exception  " + e.getMessage());
            return false;
        }

    }

    //   Get Visiting status TABLE_CUSTOMER_VISIT
    public boolean getVisitingStatus(int visitingType, int customerId) {


        boolean isExist = isExistCustomer(customerId);  // check customer in table

        if (!isExist)   //if  not exit customer return false
            return false;

        String col_name = "*";

        switch (visitingType) {
            case REQ_SALE_TYPE:

                col_name = COL_VISIT_SALE;
                break;

            case REQ_QUOTATION_TYPE:

                col_name = COL_VISIT_QUOTATION;

                break;

            case REQ_RETURN_TYPE:
                col_name = COL_VISIT_RETURN;
                break;

            case REQ_RECEIPT_TYPE:

                col_name = COL_VISIT_RECEIPT;
                break;

            case REQ_RECEIPT_BILLWISE:

                col_name = COL_VISIT_RECEIPT_BILLWISE;
                break;

            case REQ_NO_SALE_TYPE:
                col_name = COL_VISIT_NO_SALE;
                break;

            default:   //REQ_ANY_TYPE
                col_name = "*";
                break;
        }


        try {

            String selectQuery = "SELECT " + col_name + " FROM " + TABLE_CUSTOMER_VISIT + " WHERE " + COL_FK_CUSTOMER_ID + "=?";


//            String sql = "SELECT " + col_name + " FROM " + TABLE_CUSTOMER_VISIT + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + customerId + "'";

            SQLiteDatabase db = this.getReadableDatabase();


            Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(customerId)});
//            Cursor cursor = db.rawQuery(sql,null);


            cursor.moveToFirst();


            if (visitingType == REQ_ANY_TYPE) {


                Boolean sal = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_SALE)) == 1);

                Boolean quo = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_QUOTATION)) == 1);
                Boolean ret = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_RETURN)) == 1);
                Boolean rec = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_RECEIPT)) == 1);
                Boolean n_sal = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_NO_SALE)) == 1);
                Boolean n_billwiserec = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_RECEIPT_BILLWISE)) == 1);


                cursor.close();


                db.close();
                return sal || quo || ret || rec || n_sal || n_billwiserec;


            } else {

                Boolean flag = (cursor.getInt(cursor.getColumnIndex(col_name)) == 1);
//                Log.v(TAG, "getVisitingStatus   " + customerId + "\t" + flag);

                cursor.close();


                db.close();

                return flag;

            }

        } catch (SQLiteException e) {

            Log.v(TAG, "getVisitingStatus  Exception  " + e.getMessage());

        }

        return false;
    }

    //    update closeVisit
    public boolean updateVisitFinish(int customerId) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_CUSTOMER_VISIT_STATUS, true);
            int result = db.update(TABLE_CUSTOMER, values, COL_CUSTOMER_ID + " =?", new String[]{String.valueOf(customerId)});

            return result != -1;
        } catch (SQLiteException e) {
            Log.v(TAG, "updateVisitFinish  Exception  " + e.getMessage());
            return false;
        }

    }

    //    Get All Sale list TABLE_CUSTOMER_VISIT
    public ArrayList<NoSale> getAllNoSaleReasons() {

        ArrayList<NoSale> list = new ArrayList<>();
        try {

            String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER_VISIT + " WHERE " + COL_VISIT_NO_SALE + "=?";


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(1)});


            if (cursor.moveToFirst())
                do {
                    NoSale n = new NoSale();

                    n.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_FK_CUSTOMER_ID)));
                    n.setReason(cursor.getString(cursor.getColumnIndex(COL_VISIT_NO_SALE_REASON)));
                    n.setDatetime(cursor.getString(cursor.getColumnIndex(COL_VISIT_DATE_TIME)));

                    n.setLatitude(cursor.getString(cursor.getColumnIndex(COL_VISIT_NO_SALE_LATITUDE)));
                    n.setLongitude(cursor.getString(cursor.getColumnIndex(COL_VISIT_NO_SALE_LONGITUDE)));


                    list.add(n);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {

            Log.v(TAG, "getAllQuotations  " + e.getMessage());
        }

        return list;
    }

    //test
    public void getVisitingStatus() {


        try {

            String sql = "SELECT * FROM " + TABLE_CUSTOMER_VISIT;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {

                    Boolean a = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_SALE)) == 1);

                    Boolean b = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_QUOTATION)) == 1);
                    Boolean c = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_RETURN)) == 1);
                    Boolean d = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_RECEIPT)) == 1);
                    Boolean e = (cursor.getInt(cursor.getColumnIndex(COL_VISIT_NO_SALE)) == 1);


                    Log.v(TAG, "getVisitingStatus   a " + a + "\nb " + b + "\nc " + c + "\nd " + d + "\ne " + e);


                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {
            Log.v(TAG, "getVisitingStatus  Exception  " + e.getMessage());

        }


    }

    //    customer visit status Customer list TABLE_CUSTOMER
    public boolean isDayclosePrmission() {

        boolean status = true;
        try {


            String sql = "SELECT " + COL_CUSTOMER_VISIT_STATUS + " FROM " + TABLE_CUSTOMER;

//            String sql = "SELECT * FROM " + TABLE_CUSTOMER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {


                    Boolean isVisit = (cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_VISIT_STATUS)) == 1);

                    if (!isVisit)
                        return false;


                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {
            Log.v(TAG, "getAllCustomers  Exception  " + e.getMessage());

        }

        return status;
    }

    //    get students list corresponding time table
    public ArrayList<CartItem> getAllSaleProducts() {


        final ArrayList<CartItem> carts = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_SALE_PRODUCTS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    CartItem c = new CartItem();
                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setSalePrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                    c.setTotalPrice(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL)));
                    c.setPieceQuantity(cursor.getInt(cursor.getColumnIndex(COL_SALE_PRODUCT_QUANTITY)));


                    carts.add(c);

                } while (cursor.moveToNext());


            cursor.close();

        } catch (SQLiteException e) {

        }

        return carts;
    }

    //    delete TABLE
    public boolean deleteTableRequest(int deleteType) {

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            int deleteValue = -1;
            switch (deleteType) {
                case REQ_SALE_TYPE:
                    deleteValue = db.delete(TABLE_SALE_CUSTOMER, null, null);
                    break;
                case REQ_QUOTATION_TYPE:
                    deleteValue = db.delete(TABLE_QUOTATION_CUSTOMER, null, null);
                    break;
                case REQ_RETURN_TYPE:
                    deleteValue = db.delete(TABLE_WO_RETURN_CUSTOMER, null, null);
                    break;

                case REQ_RECEIPT_TYPE:
                    deleteValue = db.delete(TABLE_RECEIPT, null, null);
                    break;
                case REQ_CUSTOMER_TYPE:
                    deleteValue = db.delete(TABLE_CUSTOMER, null, null);
//                    deleteValue = db.delete(TABLE_CUSTOMER_VISIT, null, null);
                    break;

                case REQ_BANK_DETAILS:
                    deleteValue = db.delete(TABLE_BANKS, null, null);
                    break;

                case REQ_EXPENSE:
                    deleteValue = db.delete(TABLE_EXPENSE, null, null);
                    break;

                case REQ_STATE:
                    deleteValue = db.delete(TABLE_STATE, null, null);
                    break;
                case REQ_DISTRICT:
                    deleteValue = db.delete(TABLE_DISTRICT, null, null);
                    break;

                case REQ_VEHICLE_DETAILS:
                    deleteValue = db.delete(TABLE_VEHICLES, null, null);
                    break;
                case REQ_RECEIPT_BILLWISE:
                    deleteValue = db.delete(TABLE_BILLWISE_RECEIPTS, null, null);
                    break;

                case REQ_RECEIPT_CHEQUE:
                    deleteValue = db.delete(TABLE_CHEQUE_DETAILS, null, null);
                    break;

                case REQ_QUOTATION_TEMP:
                    deleteValue = db.delete(TABLE_QUOTATION_PRODUCTS_TEMP, null, null);
                    break;

                case REQ_QUOTATION_TEMPEDIT:
                    deleteValue = db.delete(TABLE_QUOTATION_PRODUCTS_TEMP_EDIT, null, null);
                    break;

                case REQ_VAN_STOCK:
                    deleteValue = db.delete(TABLE_STOCK, null, null);
                    break;


                case REQ_ANY_TYPE:

                    deleteValue = db.delete(TABLE_RECEIPT, null, null);

                    deleteValue = db.delete(TABLE_SALE_PRODUCTS, null, null);
                    deleteValue = db.delete(TABLE_SALE_CUSTOMER, null, null);

                    deleteValue = db.delete(TABLE_QUOTATION_PRODUCTS, null, null);
                    deleteValue = db.delete(TABLE_QUOTATION_CUSTOMER, null, null);

                    deleteValue = db.delete(TABLE_WO_RETURN_CUSTOMER, null, null);
                    deleteValue = db.delete(TABLE_WO_RETURN_PRODUCTS, null, null);

                    deleteValue = db.delete(TABLE_STOCK, null, null);
                    deleteValue = db.delete(TABLE_STOCK_MASTER, null, null);

                    deleteValue = db.delete(TABLE_CUSTOMER_VISIT, null, null);
                    deleteValue = db.delete(TABLE_CUSTOMER, null, null);

                    //  deleteValue = db.delete(TABLE_BANKS, null, null);

                    deleteValue = db.delete(TABLE_SELLING_PRICE, null, null);
                    deleteValue = db.delete(TABLE_CHEQUE_DETAILS, null, null);

                    deleteValue = db.delete(TABLE_RETURN_OFFLINE, null, null);
                    deleteValue = db.delete(TABLE_RETURN_OFFLINE_PRODUCTS, null, null);
                    deleteValue = db.delete(TABLE_STATE, null, null);
                    deleteValue = db.delete(TABLE_DISTRICT, null, null);
                    deleteValue = db.delete(TABLE_VEHICLES, null, null);
                    deleteValue = db.delete(TABLE_EXPENSE, null, null);
                    deleteValue = db.delete(TABLE_QUOTATION_PRODUCTS_TEMP, null, null);
                    deleteValue = db.delete(TABLE_QUOTATION_PRODUCTS_TEMP_EDIT, null, null);
                    break;

            }


            db.close();

            return deleteValue != -1;

        } catch (SQLiteException e) {
            e.getStackTrace();
            return false;
        }
    }

    //    delete TABLE
    public boolean deleteAllTable() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            int stockDlt = db.delete(TABLE_STOCK, null, null);
            int saleCustomertDlt = db.delete(TABLE_SALE_CUSTOMER, null, null);
            int customerstDlt = db.delete(TABLE_CUSTOMER, null, null);

            db.close();

            return saleCustomertDlt != -1;

        } catch (SQLiteException e) {
            e.getStackTrace();
            return false;
        }
    }


    //    get products list corresponding customer offline for return

    public ArrayList<CartItem> getOfflineSaleProductReturn(int id ) {

        ArrayList<CartItem> carts = new ArrayList<>();

        try {
            String sql = "SELECT * FROM " + TABLE_SALE_PRODUCTS + " WHERE " + COL_FK_CUSTOMER_ID + " = '" + id + "'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    CartItem c = new CartItem();

                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setCartId(cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    c.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                    c.setProductPrice(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                    c.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL)));
                    c.setPieceQuantity(cursor.getInt(cursor.getColumnIndex(COL_SALE_PRODUCT_QUANTITY)));
                    c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));

                    c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_BONUS_PERCENTAGE)));

                    c.setTypeQuantity(cursor.getInt(cursor.getColumnIndex(COL_SALE_ORDER_TYPE_QUANTITY)));
                    c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                    c.setOrderType(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));

                    c.setSalePrice(getSalePrice(c.getProductPrice(), c.getTax(),c.getTax_type()));
                    c.setNetPrice(getWithoutTaxPrice(c.getProductPrice(), c.getTax()));
                    c.setTaxValue(getTaxPrice(c.getProductPrice(), c.getTax(),c.getTax_type()));

                    carts.add(c);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getSaleProduct  Exception  " + e.getMessage());

        }
        return carts;
    }

    //    insert STATE ADDED ON 10-12-2020
    public boolean insertstate(State state) {

        boolean isExist = isExistMasterProduct(state.getStateid());  // check stock in table

        if (isExist)
            return false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            // Log.e("getProduct_taxlist",""+product.getTaxlist());
            values.put(COL_STATE_ID, state.getStateid());
            values.put(COL_STATE, state.getName());

            // Inserting Row
            long l = db.insert(TABLE_STATE, null, values);

            db.close(); // Closing database connection

            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertState  Exception  " + e.getMessage());
            return false;
        }
    }

    //    insert DISTRICT ADDED ON 10-12-2020
    public boolean insertdistrict(District dist) {
        Log.e(TAG, "District   " + dist.getDistrict());
        boolean isExist = isExistMasterProduct(dist.getDistrictId());  // check stock in table

        if (isExist)
            return false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            // Log.e("getProduct_taxlist",""+product.getTaxlist());
            values.put(COL_DISTRICT_ID, dist.getDistrictId());
            values.put(COL_DISTRICT, dist.getDistrict());

            // Inserting Row
            long l = db.insert(TABLE_DISTRICT, null, values);

            db.close(); // Closing database connection

            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertDistrict  Exception  " + e.getMessage());
            return false;
        }
    }

    //    insert STOCK MASTER ADDED ON 02-02-2021
    public boolean insertsizemasterstock(SizelistMasterstock sizemstr) {
        Log.e("getSize_id db",""+sizemstr.getSize_id());
        boolean isExist = isExistMasterSize(sizemstr.getSize_id());  // check size in table

        if (isExist)
            return false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(COL_SIZEMASTER_ID, sizemstr.getSize_id());
            values.put(COL_SIZEMASTER_NAME, sizemstr.getSize());

            // Inserting Row
            long l = db.insert(TABLE_SIZE_MASTER, null, values);

            db.close(); // Closing database connection

            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertSize  Exception  " + e.getMessage());
            return false;
        }
    }


    /**
     * stock functions
     */
    //    insert stock
    public boolean insertMasterStock(Product product) {
        Log.e("insidemaster","1");
        Log.e("name",""+product.getProductName());
        Log.e("mrp",""+product.getRetailPrice());
        Log.e("salunitid",""+product.getSale_unitid());
        Log.e("unit_list haris",""+product.getUnitslist());


        boolean isExist = isExistMasterProduct(product.getProductId());  // check stock in table

        if (isExist)
            return false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();


            values.put(COL_PRODUCT_ID, product.getProductId());
            values.put(COL_BARCODE, product.getBarcode());
            values.put(COL_PRODUCT_NAME, product.getProductName());
            values.put(COL_PRODUCT_ARABIC_NAME, product.getArabicName());
            values.put(COL_PRODUCT_CODE, product.getProductCode());
            values.put(COL_PRODUCT_BONUS, product.getProductBonus());
            values.put(COL_PRODUCT_BRAND, product.getBrandName());

            values.put(COL_PRODUCT_UNITS, product.getUnitslist());

            values.put(COL_PRODUCT_TYPE, product.getProductType());
            values.put(COL_PRODUCT_MRP, product.getRetailPrice());
            values.put(COL_PRODUCT_WHOLESALE, product.getWholeSalePrice());
            values.put(COL_PRODUCT_COST, product.getCost());
            values.put(COL_PRODUCT_TAX, product.getTax());
            values.put(COL_PRODUCT_PEACE_PER_CART, product.getPiecepercart());
            values.put(COL_PRODUCT_QUANTITY, product.getStockQuantity());

            //haris added on 03-11-2020
            values.put(COL_PRODUCT_HSNCODE, product.getProduct_hsncode());
            values.put(COL_PRODUCT_TAXLIST, product.getTaxlist());
            //
            //haris added on 21-11-2020
            //haris 23-11-2020
            values.put(COL_PRODUCT_RATE, product.getProduct_rate());
            ///

            //haris added on 26-10-2020
            values.put(COL_PRODUCT_SIZE, product.getSizelist());
            values.put(COL_PRODUCT_MFGLIST, product.getMfglist());
            values.put(COL_PRODUCT_SALE_UNIT,product.getSale_unitid());
            ////
            //haris added on 02-02-2021
            //  values.put(COL_PRODUCT_SIZE_MASTER, product.getSizelist_master());

            // Inserting Row
            long l = db.insert(TABLE_STOCK_MASTER, null, values);

            db.close(); // Closing database connection

            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertStock  Exception  " + e.getMessage());
            return false;
        }
    }

    //    get getAllStock list
    public ArrayList<CartItem> getAllMasterStock() {
        Log.e("inside","1");

        final ArrayList<CartItem> cartItems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_STOCK_MASTER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    CartItem c = new CartItem();
                    Log.e("inside","2");
                    c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                    c.setBarcode(cursor.getString(cursor.getColumnIndex(COL_BARCODE)));
                    c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    c.setArabicName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                    c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                    c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_BONUS)));
                    c.setBrandName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_BRAND)));
                    c.setUnitslist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITS)));
                    c.setProductType(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TYPE)));
                    c.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                    //haris added on 26-11-2020
                    //c.setRetailPrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_RATE)));

                    c.setWholeSalePrice(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_WHOLESALE)));
                    c.setCost(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_COST)));
                    c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));

                    c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));

                    c.setStockQuantity(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY)));
                    //haris added on 26-10-2020
                    // c.setSizelist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_SIZE)));
                    ///
                    //haris added on 03-11-2020
                    c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                    c.setProduct_hsncode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_HSNCODE)));
                    c.setTaxlist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TAXLIST)));
                    c.setMfglist(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_MFGLIST)));
                    c.setSale_unitid(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_SALE_UNIT)));
                    ///
                    Log.e("producytname",""+c.getProductName());
                    cartItems.add(c);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.v(TAG, "getAllStock  Exception  " + e.getMessage());
        }

        return cartItems;
    }


    private boolean isExistInvoice_Bilwise(String invoiceno) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_BILLWISE_RECEIPTS + " WHERE " + COL_BILLWISE_RECCEIPT_INVOICENO + WHERE_CLAUSE, new String[]{String.valueOf(invoiceno)});

//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER , new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return count != 0;
    }

    //      check isavailable stock
//     Count Row From to SQLite  "TABLE_STOCK" Table
    public boolean isExistMasterProducts() {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_STOCK_MASTER, new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;

    }


    // Check if return invoice exist

    private boolean isExistReturn(int productId) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_STOCK + " WHERE " + COL_PRODUCT_ID + WHERE_CLAUSE, new String[]{String.valueOf(productId)});

//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER , new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;

    }


    //    Get full Sale list TABLE_SALE_CUSTOMER
    public ArrayList<Sales> getFullSales() {

        ArrayList<Sales> list = new ArrayList<>();
        // double paidAmount = 0, saleAmount = 0;

        try {

            //String sql = "SELECT a." + COL_CUSTOMER_ID +",a."+COL_CREATED_AT+ ",a." + COL_INVOICE_CODE + ", a." + COL_WITH_TAX_TOTAL + ",b." + COL_CUSTOMER_NAME + ",b." + COL_CUSTOMER_CODE + " FROM " + TABLE_SALE_CUSTOMER + " as a , " + TABLE_CUSTOMER  + " as b "+" WHERE "+"a."+COL_CUSTOMER_ID +"= b."+ COL_CUSTOMER_ID;
            String sql = "SELECT * FROM "+TABLE_SALE_CUSTOMER ;


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {


                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_SALE_TYPE)));
                    s.setDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
                    s.setShopname(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                    s.setShopcode(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CODE)));
                    s.setTotal(cursor.getFloat(cursor.getColumnIndex(COL_SALE_TOTAL)));
                    s.setUploadStatus(cursor.getString(cursor.getColumnIndex(COL_UPLOAD_STATUS)));

                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_INVOICE_CODE)));
                    s.setPaid(cursor.getFloat(cursor.getColumnIndex(COL_SALE_PAID)));

                    String createdDate = cursor.getString(cursor.getColumnIndex(COL_CREATED_AT));

                    // --------------------------------------

                    s.setTaxAmount(cursor.getFloat(cursor.getColumnIndex(COL_TAX_AMOUNT)));
                    s.setTaxPercentage(cursor.getFloat(cursor.getColumnIndex(COL_TAX_PERCENTAGE)));
                    s.setWithoutTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITHOUT_TAX)));
                    s.setWithTaxTotal(cursor.getFloat(cursor.getColumnIndex(COL_WITH_TAX_TOTAL)));
                    s.setVat_status(cursor.getString(cursor.getColumnIndex(COL_SALE_VAT_STATUS)));
                    s.setPayment_type(cursor.getString(cursor.getColumnIndex(COL_PAYMENT_TYPE)));
                    s.setDiscount(cursor.getFloat(cursor.getColumnIndex(COL_DISCOUNT)));
                    s.setDiscountAmount(cursor.getFloat(cursor.getColumnIndex(COL_WITH_DISCOUNT)));
                    s.setRoundoff_type(cursor.getString(cursor.getColumnIndex(COL_SALE_ROUNDOFFTYPE)));
                    s.setInvoice_no(cursor.getInt(cursor.getColumnIndex(COL_INVOICE_NO_INT)));

                    Log.v(TAG, "shopname " + s.getShopname());
                    Log.v(TAG, "shopcode " + s.getShopcode());
                    Log.v(TAG, "getInvoiceCode " + s.getInvoiceCode());
                    // ----------------------------------------------

                    getSaleProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    Log.v(TAG, "getCustomerSales  date  " + createdDate);

                    list.add(s);


                } while (cursor.moveToNext());

            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getCustomerCreditedAmount  Exception  " + e.getMessage());

        }

        return list;
    }

    //    Get full Return list TABLE_SALE_CUSTOMER
    public ArrayList<Sales> getFullReturn() {

        ArrayList<Sales> list = new ArrayList<>();
        // double paidAmount = 0, saleAmount = 0;

        try {


            String sql = "SELECT * FROM "+ TABLE_WO_RETURN_CUSTOMER;


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {
//                    SaleReport s = new SaleReport();
//                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
//                    s.setDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
//                    s.setTotal(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TOTAL)));
//                    s.setShopname(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
//                    s.setShopcode(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CODE)));
//                    s.setInvoicecode(cursor.getString(cursor.getColumnIndex(COL_RETURN_CODE)));
//                    list.add(s);

                    Sales s = new Sales();

                    s.setLocId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    s.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_CUSTOMER_ID)));
                    s.setInvoiceCode(cursor.getString(cursor.getColumnIndex(COL_RETURN_CODE)));
                    s.setSaleType(cursor.getString(cursor.getColumnIndex(COL_RETURN_TYPE)));

                    s.setDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));

                    s.setTotal(cursor.getDouble(cursor.getColumnIndex(COL_RETURN_TOTAL)));
                    s.setTaxTotal(cursor.getDouble(cursor.getColumnIndex(COL_TAX_TOTAL)));
                    s.setDiscount(cursor.getFloat(cursor.getColumnIndex(COL_RETURN_DISCOUNT)));
                    s.setDiscount_value(cursor.getFloat(cursor.getColumnIndex(COL_RETURN_DISCOUNT_TOTAL)));
                    s.setTaxAmount(cursor.getFloat(cursor.getColumnIndex(COL_RETURN_TAXAMOUNT)));
                    String remarks = cursor.getString(cursor.getColumnIndex(COL_RETURN_REMARKS));
                    s.setReturn_invoiceno(cursor.getString(cursor.getColumnIndex(COL_RETURN_INVOICE_CODE)));
                    s.setShopcode(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CODE)));
                    s.setShopname(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                    s.setRoundoff_value(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ROUNDOFF_TOT)));

                    getReturnProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    String createdDate = cursor.getString(cursor.getColumnIndex(COL_CREATED_AT));

                    getReturnProduct(s, cursor.getInt(cursor.getColumnIndex(COL_ID)));

                    list.add(s);


                } while (cursor.moveToNext());

            cursor.close();

            db.close();

        } catch (SQLiteException e) {
            printLog(TAG, "getCustomerCreditedAmount  Exception  " + e.getMessage());

        }

        return list;
    }
    //getAllReceipts
    public ArrayList<Receipt> getReceiptDetails() {

        final ArrayList<Receipt> receipts = new ArrayList<>();

        try {
            String sql = "SELECT distinct a.id_int,a.receipt_no_txt,a.receipt_receivable_real,a.receipt_balance_real,a.created_at_date,a.fk_customer_id_int,b.customer_name_txt,b.customer_code_txt  FROM  tbl_receipts a,tbl_customer b where a.fk_customer_id_int=b.customer_id_int" ;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    Receipt r = new Receipt();
                    r.setLocalId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                    r.setReceiptNo(cursor.getString(cursor.getColumnIndex(COL_RECEIPT_NO)));
                    r.setReceivedAmount(cursor.getFloat(cursor.getColumnIndex(COL_RECEIVABLE_AMOUNT)));
                    r.setCurrentBalanceAmount(cursor.getFloat(cursor.getColumnIndex(COL_RECEIPT_BALANCE)));
                    r.setLogDate(cursor.getString(cursor.getColumnIndex(COL_CREATED_AT)));
                    r.setCustomerId(cursor.getInt(cursor.getColumnIndex(COL_FK_CUSTOMER_ID)));
                    r.setCustomername(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_NAME)));
                    r.setCustomercode(cursor.getString(cursor.getColumnIndex(COL_CUSTOMER_CODE)));

                    receipts.add(r);

                } while (cursor.moveToNext());


            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.d(TAG, "getReceiptDetails  Exception  " + e.getMessage());

        }

        return receipts;
    }
    //    insert vehicles
    public boolean insertVehicledetails(Vehicle veh) {


        // boolean isExist = isExistProduct(veh.getVehicle_id()); // check stock in table

//        if (isExist)
//            return false;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_VEHICLE_ID, veh.getVehicle_id());
            values.put(COL_VEHICLE_NO, veh.getVehicle_no());


            long l = db.insert(TABLE_VEHICLES, null, values);
            db.close(); // Closing database connection
            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertvehicles Exception  " + e.getMessage());
            return false;
        }
    }
    // get all vehicles
    public ArrayList<Vehicle> getAllVehicles() {

        final ArrayList<Vehicle> vehiclearray = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_VEHICLES;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    Vehicle v = new Vehicle();

                    v.setVehicle_id(cursor.getInt(cursor.getColumnIndex(COL_VEHICLE_ID)));
                    v.setVehicle_no(cursor.getString(cursor.getColumnIndex(COL_VEHICLE_NO)));


                    Log.e("getVehicles  ", "" + cursor.getString(cursor.getColumnIndex(COL_VEHICLE_NO)));
                    vehiclearray.add(v);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getvehicle  Exception  " + e.getMessage());
        }

        return vehiclearray;
    }
    //   Get stock Quantity TABLE_STOCK
    public String getUnitbyproduct(int id) {
        String unitlist = "";

        try {
            String sql = "SELECT " + COL_PRODUCT_UNITS + " FROM " + TABLE_STOCK +" WHERE " + COL_PRODUCT_ID + " = '" + id + "'";


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    unitlist = cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITS));


                } while (cursor.moveToNext());

            cursor.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getStockQuantity  Exception  " + e.getMessage());

        }


        return unitlist;
    }


    //   Get stock Quantity TABLE_STOCK
    public String getUnitbyproductMaster(int id) {
        String unitlist = "";

        try {
            String sql = "SELECT " + COL_PRODUCT_UNITS + " FROM " + TABLE_STOCK_MASTER +" WHERE " + COL_PRODUCT_ID + " = '" + id + "'";


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    unitlist = cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITS));


                } while (cursor.moveToNext());

            cursor.close();

        } catch (SQLiteException e) {
            Log.v(TAG, "getmasterstock_unitlist_byproductid  Exception  " + e.getMessage());

        }


        return unitlist;
    }


    /**
     * stock functions
     */
    //    insert stock approval
    public boolean insertStock_byapproval(StocktransferDetails st) {
        Log.e("insert stappr","ok"+st.getProductname());
        Log.e("insert stappr","ok2"+st.getQuantity());
        Log.e("insert prodid","ok3"+st.getProductid());

        boolean isExist = isExistStockApprovalProduct(st.getProductid());  // check stock in table

        if (isExist)
            return false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(COL_PRODUCT_ITEM_ID, st.getItemid());
            values.put(COL_PRODUCT_ID, st.getProductid());
            values.put(COL_PRODUCT_TRANSFER_ID, st.getTransferid());
            values.put(COL_PRODUCT_QUANTITY, st.getQuantity());
            values.put(COL_PRODUCT_NAME, st.getProductname());
            values.put(COL_PRODUCT_CODE, st.getProductcode());
            values.put(COL_PRODUCT_REPORTING_UNIT, st.getProduct_reportingunit());

            // Inserting Row
            long l = db.insert(TABLE_STOCK_APPROVAL, null, values);

            db.close(); // Closing database connection

            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertStockApproval  Exception  " + e.getMessage());
            return false;
        }
    }
    private boolean isExistStockApprovalProduct(int productId) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_STOCK_APPROVAL + " WHERE " + COL_PRODUCT_ID + WHERE_CLAUSE, new String[]{String.valueOf(productId)});

//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER , new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return count != 0;
    }

    /**
     * stock functions
     */
    //    update stock approval
    public boolean updateStock_byapproval(StocktransferDetails st) {
        Log.e("approval reached","ok");
        boolean isExist = isExistProduct(st.getProductid());  // check stock in table
        Log.e("insert stocknme hrfrst","prodid"+ st.getProductname());
        if (!isExist) {
            Log.e("insert stock hr","prodid"+ st.getProductid());
            Log.e("insert stocknme hr","nme"+ st.getProductname());
            Log.e("insert stockqty hr","qty"+ st.getQuantity());
            //return false;
            // String sql = "SELECT  distinct a.product_id_int,a.barcode_int,a.product_traycapacity,a.product_name_txt,a.product_arab_name_txt,a.product_priority_master_int,a.product_code_txt,a.product_bonus,a.product_brand_txt,a.product_units_txt,a.product_type_txt,a.product_mrp_real,a.product_cost_real,a.product_tax_real,a.product_cart_quantity_int,case when b.product_quantity_int is not null then b.product_quantity_int else 0 end as product_quantity_int, a.product_wholesale_real ,a.type FROM tbl_stock_master a  left outer JOIN tbl_stock_approval b on a.product_id_int=b.product_id_int order by a.product_priority_master_int ASC ";

            String sql = "SELECT  distinct b.product_id_int,a.barcode_int,b.product_name_txt,a.product_reportingunit_int,a.product_arab_name_txt," +
                    "a.product_code_txt,a.product_bonus,a.product_brand_txt," +
                    "a.product_units_txt,a.product_type_txt,a.product_mrp_real,a.product_cost_real," +
                    "a.product_tax_real,a.product_cart_quantity_int," +
                    "case when b.product_quantity_int is not null then b.product_quantity_int else 0 end as product_quantity_int," +
                    " a.product_wholesale_real  FROM tbl_stock_master a  JOIN tbl_stock_approval b on a.product_id_int=b.product_id_int where a.product_id_int= "+st.getProductid()+"";

            try {
                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues values = new ContentValues();

                Cursor cursor = db.rawQuery(sql, null);

                if (cursor.moveToFirst())
                    do {


                        Log.e("insert stock hr","2"+cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                        values.put(COL_PRODUCT_ID, cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                        values.put(COL_BARCODE, cursor.getString(cursor.getColumnIndex(COL_BARCODE)));
                        values.put(COL_PRODUCT_NAME, cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                        values.put(COL_PRODUCT_ARABIC_NAME, cursor.getString(cursor.getColumnIndex(COL_PRODUCT_ARABIC_NAME)));
                        values.put(COL_PRODUCT_CODE,cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                        values.put(COL_PRODUCT_BONUS,cursor.getString(cursor.getColumnIndex(COL_PRODUCT_BONUS)));
                        values.put(COL_PRODUCT_BRAND,cursor.getString(cursor.getColumnIndex(COL_PRODUCT_BRAND)));
                        values.put(COL_PRODUCT_UNITS, cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITS)));
                        values.put(COL_PRODUCT_TYPE, cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TYPE)));
                        values.put(COL_PRODUCT_MRP, cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                        values.put(COL_PRODUCT_WHOLESALE, cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_WHOLESALE)));
                        values.put(COL_PRODUCT_COST, cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_COST)));
                        values.put(COL_PRODUCT_TAX, cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));
                        values.put(COL_PRODUCT_PEACE_PER_CART,cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                        values.put(COL_PRODUCT_QUANTITY, cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY)));
                        values.put(COL_PRODUCT_REPORTING_UNIT, cursor.getString(cursor.getColumnIndex(COL_PRODUCT_REPORTING_UNIT)));
                        //values.put(COL_TYPE, cursor.getString(cursor.getColumnIndex(COL_TYPE)));

                        // Inserting Row
                        long l = db.insert(TABLE_STOCK, null, values);

                    } while (cursor.moveToNext());


                db.close(); // Closing database connection

                return true;

            } catch (SQLiteException e) {
                Log.v(TAG, "insertStockApproval  Exception  " + e.getMessage());
                return false;
            }
        }else {
            int result=0;
            try {

                Product p = getStockProduct(st.getProductid());
                int qty = 0;
                qty = p.getStockQuantity() + st.getApproval_qty();
                Log.e("getStockname else", "" + p.getProductName());
                Log.e("getStockQuantity else", "" + p.getStockQuantity());
                Log.e("getApproval_qty else", "" + st.getApproval_qty());
                Log.e("qty else", "" + qty);
                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(COL_PRODUCT_QUANTITY, qty);
                // values.put(COL_PRO, qty);


                result = db.update(TABLE_STOCK, values, COL_PRODUCT_ID + " =?", new String[]{String.valueOf(st.getProductid())});

                db.close();

                return result != -1;
            } catch (SQLiteException e) {
                Log.v(TAG, "updateStock  Exception  " + e.getMessage());
                return false;
            }

        }

    }
    // get all expense
    public double get_billamount_total() {


        double dbl_bill_total = 0;
        try {
            String sql = "SELECT * FROM " + TABLE_BILLWISE_RECEIPTS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    BillwiseReceiptMdl bl = new BillwiseReceiptMdl();

                    bl.setCustomerid(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_CUSTOMERID)));
                    bl.setRemarks(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_REMARK)));
                    bl.setInvoiceno(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_INVOICENO)));
                    bl.setTotalcash(cursor.getFloat(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_TOTALCASH)));
                    bl.setBillamount(cursor.getFloat(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_AMOUNTENTERED)));

                    dbl_bill_total = dbl_bill_total + bl.getBillamount();



                    //  bills_list.add(bl);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "getbillwise_receipts  Exception  " + e.getMessage());
        }

        return dbl_bill_total;
    }
    //    insert banks
    public boolean insertbillwisereceiptdetails(ArrayList<BillwiseReceiptMdl> list, String customer_id, String total , String advnce , String modeofpay, String referno, String billdate) {

        //  boolean isExist = isExistInvoice_Bilwise(list.getInvoiceno()); // check stock in table

//        if (isExist)
//            return false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();


            values.put(COL_BILLWISE_RECCEIPT_CUSTOMERID, customer_id);
            values.put(COL_BILLWISE_RECCEIPT_REMARK, referno);
            values.put(COL_BILLWISE_RECCEIPT_TOTALCASH, total);
            values.put(COL_RECEIPT_MODE, modeofpay);
            values.put(COL_BILLWISE_RECCEIPT_DUEAMNT, "");
//            values.put(COL_BILLWISE_RECCEIPT_SALEID,"");
            values.put(COL_BILLWISE_RECCEIPT_BILLDATE,billdate);
            values.put(COL_BILLWISE_RECCEIPT_CHEQUENO,"");
            values.put(COL_BILLWISE_RECCEIPT_CHEQUEDATE,"");
            values.put(COL_BILLWISE_RECCEIPT_CHEQUEBANK,"");
            values.put(COL_BILLWISE_RECCEIPT_MODEBANK_NAME,"");
            values.put(COL_BILLWISE_RECCEIPT_MODEBANK_REFERENCE,"");
            values.put(COL_BILLWISE_RECCEIPT_INVOICEBALANCE,"");



            long l = db.insert(TABLE_BILLWISE_RECEIPT_DETAILS, null, values);

            insertbillwiselist(Integer.valueOf(String.valueOf(l)),list );
            db.close(); // Closing database connection
            return l != -1;



        } catch (SQLiteException e) {
            Log.v(TAG, "insertbillwisereceipt  Exception  " + e.getMessage());
            return false;
        }
    }
    private void insertbillwiselist(Integer id, ArrayList<BillwiseReceiptMdl> list) {
        long l = 0;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            for (BillwiseReceiptMdl bills : list) {

                Log.e("getInvoiceno", "" + bills.getInvoiceno());
                Log.e("id", "" + id);
                Log.e("getReceipt_enetered hr", "" + bills.getReceipt_enetered());

                ContentValues values = new ContentValues();
                values.put(COL_BILLWISE_RECCEIPT_AMOUNTENTERED, bills.getReceipt_enetered());
                values.put(COL_BILLWISE_RECCEIPT_INVOICENO, bills.getInvoiceno());

                values.put(COL_BILLWISE_RECCEIPT_REMARK, bills.getRemarks());

                values.put(COL_FK_BILLWISE_ID, id);

                l = db.insert(TABLE_BILLWISE_RECEIPT_DETAILS_LIST, null, values);
            }

            if (db != null && db.isOpen()) {
                db.close(); // Closing database connection
            }

        } catch (SQLiteException | IllegalStateException e) {
            Log.v(TAG, "insertbillwiselist  Exception  " + e.getMessage());
        }
    }


    //    insert banks
    public boolean insertbillwisereceipt(BillwiseReceiptMdl list) {

        boolean isExist = isExistInvoice_Bilwise(list.getInvoiceno()); // check stock in table

        if (isExist)
            return false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(COL_BILLWISE_RECCEIPT_AMOUNT, list.getBillamount());
            values.put(COL_BILLWISE_RECCEIPT_CUSTOMERID, list.getCustomerid());
            values.put(COL_BILLWISE_RECCEIPT_INVOICENO, list.getInvoiceno());
            values.put(COL_BILLWISE_RECCEIPT_REMARK, list.getRemarks());
            values.put(COL_BILLWISE_RECCEIPT_TOTALCASH, list.getTotalcash());
            values.put(COL_BILLWISE_RECCEIPT_INVOICEAMNT, list.getInvoice_amnt());
            values.put(COL_BILLWISE_RECCEIPT_INVOICEDATE, list.getInvoicedate());
            values.put(COL_BILLWISE_RECCEIPT_DUEAMNT, list.getDue_amnt());
            values.put(COL_BILLWISE_RECCEIPT_SALEID,list.getSale_id());
            values.put(COL_BILLWISE_RECCEIPT_BILLDATE,list.getBill_date());
            values.put(COL_BILLWISE_RECCEIPT_CHEQUENO,list.getChequeno());
            values.put(COL_BILLWISE_RECCEIPT_CHEQUEDATE,list.getChequedate());
            values.put(COL_BILLWISE_RECCEIPT_CHEQUEBANK,list.getChequebank());
            values.put(COL_BILLWISE_RECCEIPT_MODEBANK_NAME,list.getModebank_name());
            values.put(COL_BILLWISE_RECCEIPT_MODEBANK_REFERENCE,list.getModebank_referenceno());
            values.put(COL_BILLWISE_RECCEIPT_AMOUNTENTERED,list.getModebank_referenceno());
            values.put(COL_BILLWISE_RECCEIPT_INVOICEBALANCE,list.getInvoicebalance());


            long l = db.insert(TABLE_BILLWISE_RECEIPTS, null, values);
            db.close(); // Closing database connection
            return l != -1;

        } catch (SQLiteException e) {
            Log.v(TAG, "insertbillwisereceipt  Exception  " + e.getMessage());
            return false;
        }
    }

    // get all expense
    public ArrayList<BillwiseReceiptMdl> getbillwise_receipts() {

        final ArrayList<BillwiseReceiptMdl> bills_list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + TABLE_BILLWISE_RECEIPTS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    BillwiseReceiptMdl bl = new BillwiseReceiptMdl();

                    bl.setCustomerid(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_CUSTOMERID)));
                    bl.setRemarks(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_REMARK)));
                    bl.setInvoiceno(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_INVOICENO)));
                    bl.setTotalcash(cursor.getFloat(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_TOTALCASH)));
                    bl.setBillamount(cursor.getFloat(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_AMOUNT)));
                    bl.setInvoice_amnt(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_INVOICEAMNT)));
                    bl.setSale_id(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_SALEID)));
                    bl.setBill_date(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_BILLDATE)));
                    bl.setChequebank(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_CHEQUEBANK)));
                    bl.setChequedate(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_CHEQUEDATE)));
                    bl.setChequeno(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_CHEQUENO)));
                    bl.setModebank_name(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_MODEBANK_NAME)));
                    bl.setModebank_referenceno(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_MODEBANK_REFERENCE)));
                    bl.setReceipt_enetered(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_AMOUNTENTERED)));
                    bl.setDiscount(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_DISCOUNT)));
                    // bl.setInvoicebalance(cursor.getString(cursor.getColumnIndex(COL_BILL)));
                    Log.e("receiptentrd",""+bl.getReceipt_enetered());
                    double dbl_amnt = TextUtils.isEmpty(bl.getInvoice_amnt().toString().trim()) ? 0 : Double.valueOf(bl.getInvoice_amnt().toString().trim());
                    if(dbl_amnt>0) {
                        bills_list.add(bl);
                    }

                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "getbillwise_receipts  Exception  " + e.getMessage());
        }

        return bills_list;
    }
    public String get_billamntby_invoiceno(String invoice_no){
        Log.e("inv finalbal",""+invoice_no);
        String billamnt ="";

        try {
            String sql = "SELECT * FROM " + TABLE_BILLWISE_RECEIPTS + " WHERE "  + COL_BILLWISE_RECCEIPT_INVOICENO + WHERE_CLAUSE;


            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(sql, new String[]{ String.valueOf(invoice_no)});
            if (cursor.moveToFirst())
                do {

                    BillwiseReceiptMdl bl = new BillwiseReceiptMdl();


                    bl.setBillamount(cursor.getFloat(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_AMOUNTENTERED)));

                    billamnt = ""+bl.getBillamount();

                    Log.e("billamnt",""+billamnt);
                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "get_billamntby_invoiceno  Exception  " + e.getMessage());
        }

        return billamnt;
    }

    // update status Y if uploaded to server
    public boolean Update_receipt_amountbyInvoice(String inv_code,String amount, String remarks ,String st_date,String discnt){
        Log.e("invoicecode hr",""+inv_code);
        Log.e("st_date hr",""+st_date);

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_BILLWISE_RECCEIPT_AMOUNTENTERED, amount);
            values.put(COL_BILLWISE_RECCEIPT_REMARK, remarks);
            values.put(COL_BILLWISE_RECCEIPT_BILLDATE, st_date);
            values.put(COL_BILLWISE_RECCEIPT_DISCOUNT, discnt);

            int result = db.update(TABLE_BILLWISE_RECEIPTS, values, COL_BILLWISE_RECCEIPT_INVOICENO + " =?", new String[]{String.valueOf(inv_code)});

            db.close();


            return true;

        }catch (SQLiteException e){

            return false;
        }

    }
    public String getfinal_balanceby_invoiceno(String invoice_no){
        Log.e("inv finalbal",""+invoice_no);
        String final_bal ="";
        float invoice_bal =0;
        float final_balnce =0;
        float receipt_entered =0;
        try {
            //   String sql = "SELECT * FROM " + TABLE_BILLWISE_RECEIPTS +" WHERE "+COL_BILLWISE_RECCEIPT_INVOICENO +" = "+invoice_no;


            String sql = "SELECT * FROM " + TABLE_BILLWISE_RECEIPTS + " WHERE "  + COL_BILLWISE_RECCEIPT_INVOICENO + WHERE_CLAUSE;


            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(sql, new String[]{ String.valueOf(invoice_no)});
            if (cursor.moveToFirst())
                do {

                    BillwiseReceiptMdl bl = new BillwiseReceiptMdl();


                    bl.setReceipt_enetered(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_AMOUNTENTERED)));
                    bl.setInvoicebalance(cursor.getString(cursor.getColumnIndex(COL_BILLWISE_RECCEIPT_INVOICEBALANCE)));
                    try{
                        invoice_bal = Float.parseFloat(bl.getInvoicebalance());
                        receipt_entered = Float.parseFloat(bl.getReceipt_enetered());
                    }catch (Exception e){

                    }
                    final_balnce = invoice_bal-receipt_entered;
                    final_bal =""+final_balnce;
                    Log.e("finalbal",""+final_bal);
                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e)
        {
            Log.v(TAG, "getfinalbal_receipts  Exception  " + e.getMessage());
        }

        return final_bal;
    }

    /*insert Quotation items*/ //haris edited on 29-01-2021
    public void insertQuotationProducts_temp(int customerid, ArrayList<CartItem> list) {
        Log.e("insertttttttttttt","ok");


        long l = 0;

        //  try {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUOTATION_PRODUCTS_TEMP, null, null);
        for (CartItem cartItem : list) {

            Log.e("insertttttttttttt", cartItem.getProductName());
            Log.e("getTaxValue","ok"+cartItem.getTaxValue());
            ContentValues values = new ContentValues();
            values.put(COL_PRODUCT_ID, cartItem.getProductId());
            values.put(COL_PRODUCT_PRICE, cartItem.getProductPrice());
            values.put(COL_PRODUCT_CODE, cartItem.getProductCode());
            values.put(COL_PRODUCT_NAME, cartItem.getProductName());
            values.put(COL_SALE_PRODUCT_QUANTITY, cartItem.getPieceQuantity());
            values.put(COL_SALE_TOTAL, cartItem.getTotalPrice());
            values.put(COL_SALE_PRODUCT_UNIT_SELECTED, cartItem.getUnitselected());
            values.put(COL_PRODUCT_TAX, cartItem.getTax());
            values.put(COL_BONUS_PERCENTAGE, cartItem.getProductBonus());
            values.put(COL_SALE_PRODUCT_ORDER_TYPE, cartItem.getOrderType());
            values.put(COL_SALE_ORDER_TYPE_QUANTITY, cartItem.getTypeQuantity());
            values.put(COL_PRODUCT_PEACE_PER_CART, cartItem.getPiecepercart());
            values.put(COL_SALE_PRODUCT_SIZE_STRING, cartItem.getSize_string());
            values.put(COL_SALE_PRODUCT_SIZEANDQTY_STRING, cartItem.getSizeandqty_string());
            values.put(COL_PRODUCT_HSNCODE, cartItem.getProduct_hsncode());
            values.put(COL_FK_CUSTOMER_ID, customerid);
            values.put(COL_PRODUCT_DISCOUNT, cartItem.getProductDiscount());
            values.put(COL_PRODUCT_TOTAL, cartItem.getProductTotal());
            values.put(COL_PRODUCT_TOTAL_VALUE, cartItem.getProductTotalValue());
            values.put(COL_PRODUCT_CGST, cartItem.getTaxValue()/2);
            values.put(COL_PRODUCT_SGST, cartItem.getTaxValue()/2);
            values.put(COL_PRODUCT_QNTY_BYUNIT, cartItem.getPieceQuantity_nw());
            values.put(COL_SALE_PRODUCT_TAXTYPE, cartItem.getTax_type());
            values.put(COL_PRODUCT_TAX_AMOUNT, cartItem.getTaxValue());
            values.put(COL_PRODUCT_MRP, cartItem.getMrprate());
            values.put(COL_PRODUCT_CONFACTORKG, cartItem.getConfactr_kg());
            values.put(COL_PRODUCT_UNITID, cartItem.getUnitid_selected());
            values.put(COL_PRODUCT_UNITCONFACTOR, cartItem.getUnit_confactor());
            values.put(COL_PRODUCT_NETPRICE, cartItem.getNetPrice());
            values.put(COL_PRODUCT_CONFACTOR,cartItem.getCon_factor());
            values.put(COL_PRODUCT_KGM_PRICE,cartItem.getPrice_kgm());
            values.put(COL_PRODUCT_KGM_QNTY,cartItem.getQnty_kgm());
            values.put(COL_PRODUCT_DESCRIPTION,cartItem.getDescription());
            values.put(COL_PRODUCT_DISC_PERCENTAGE,cartItem.getDisc_percentage());
            values.put(COL_PRODUCT_VAT_STATUS,cartItem.getVat_status());
            values.put(COL_P_NAME, cartItem.getP_name());

            l = db.insert(TABLE_QUOTATION_PRODUCTS_TEMP, null, values);

        }
        db.close(); // Closing database connection


//        } catch (SQLiteException e) {
//            Log.v(TAG, "insertQuotationProducts  Exception  " + e.getMessage());
//        }

    }


    /*insert Quotation items*/ //haris edited on 29-01-2021
    public void insertQuotationProducts_tempedit(int customerid, ArrayList<CartItem> list) {
        Log.e("inserttttttttttttedt","ok");

        long l = 0;

        //  try {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUOTATION_PRODUCTS_TEMP_EDIT, null, null);
        for (CartItem cartItem : list) {
            Log.e("insertttttttttttt", cartItem.getProductName());
            ContentValues values = new ContentValues();
            values.put(COL_PRODUCT_ID, cartItem.getProductId());
            values.put(COL_PRODUCT_PRICE, cartItem.getProductPrice());
            values.put(COL_PRODUCT_CODE, cartItem.getProductCode());
            values.put(COL_PRODUCT_NAME, cartItem.getProductName());
            values.put(COL_SALE_PRODUCT_QUANTITY, cartItem.getPieceQuantity());
            values.put(COL_SALE_TOTAL, cartItem.getTotalPrice());
            values.put(COL_SALE_PRODUCT_UNIT_SELECTED, cartItem.getUnitselected());
            values.put(COL_PRODUCT_TAX, cartItem.getTax());
            values.put(COL_BONUS_PERCENTAGE, cartItem.getProductBonus());
            values.put(COL_SALE_PRODUCT_ORDER_TYPE, cartItem.getOrderType());
            values.put(COL_SALE_ORDER_TYPE_QUANTITY, cartItem.getTypeQuantity());
            values.put(COL_PRODUCT_PEACE_PER_CART, cartItem.getPiecepercart());
            values.put(COL_SALE_PRODUCT_SIZE_STRING, cartItem.getSize_string());
            values.put(COL_SALE_PRODUCT_SIZEANDQTY_STRING, cartItem.getSizeandqty_string());
            values.put(COL_PRODUCT_HSNCODE, cartItem.getProduct_hsncode());
            values.put(COL_FK_CUSTOMER_ID, customerid);
            values.put(COL_PRODUCT_DISCOUNT, cartItem.getProductDiscount());
            values.put(COL_PRODUCT_TOTAL, cartItem.getProductTotal());
            values.put(COL_PRODUCT_TOTAL_VALUE, cartItem.getProductTotalValue());
            values.put(COL_PRODUCT_CGST, cartItem.getTaxValue()/2);
            values.put(COL_PRODUCT_SGST, cartItem.getTaxValue()/2);
            values.put(COL_PRODUCT_QNTY_BYUNIT, cartItem.getPieceQuantity_nw());
            values.put(COL_SALE_PRODUCT_TAXTYPE, cartItem.getTax_type());
            values.put(COL_PRODUCT_TAX_AMOUNT, cartItem.getTaxValue());
            values.put(COL_PRODUCT_MRP, cartItem.getMrprate());
            values.put(COL_PRODUCT_CONFACTORKG, cartItem.getConfactr_kg());
            values.put(COL_PRODUCT_UNITID, cartItem.getUnitid_selected());
            values.put(COL_PRODUCT_UNITCONFACTOR, cartItem.getUnit_confactor());
            values.put(COL_PRODUCT_NETPRICE, cartItem.getNetPrice());
            values.put(COL_PRODUCT_CONFACTOR,cartItem.getCon_factor());
            values.put(COL_PRODUCT_KGM_PRICE,cartItem.getPrice_kgm());
            values.put(COL_PRODUCT_KGM_QNTY,cartItem.getQnty_kgm());
            values.put(COL_PRODUCT_DESCRIPTION,cartItem.getDescription());


            l = db.insert(TABLE_QUOTATION_PRODUCTS_TEMP_EDIT, null, values);

        }
        db.close(); // Closing database connection


//        } catch (SQLiteException e) {
//            Log.v(TAG, "insertQuotationProducts  Exception  " + e.getMessage());
//        }

    }


    //public boolean update_productdetails_byprodid(int prodid,double qnty , double prod_price, double net_price){
        public boolean update_productdetails_byprodid(int prodid,double qnty,double db_qnty_kgm ){
        Log.e("prodid db",""+prodid);
        Log.e("qty db",""+qnty);
//        Log.e("net_price db",""+net_price);
//        Log.e("prod_price db",""+prod_price);
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_SALE_ORDER_TYPE_QUANTITY, qnty);
            values.put(COL_PRODUCT_KGM_QNTY,db_qnty_kgm);
//            values.put(COL_PRODUCT_NETPRICE,net_price);



            int result = db.update(TABLE_QUOTATION_PRODUCTS_TEMP, values, COL_PRODUCT_ID + " =?", new String[]{String.valueOf(prodid)});

            db.close();

            return result != -1;
        } catch (SQLiteException e) {
            Log.v(TAG, "updateProductdetails  Exception  " + e.getMessage());
            return false;
        }

    }

    public boolean update_productvat_byprodid(int prodid,double taxvalue,double qty ){
        Log.e("prodid db",""+prodid);
        Log.e("qty db",""+taxvalue);

//        Log.e("net_price db",""+net_price);
//        Log.e("prod_price db",""+prod_price);
//COL_PRODUCT_TAX_AMOUNT,COL_PRODUCT_SGST,COL_PRODUCT_CGST
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(COL_PRODUCT_TAX_AMOUNT,taxvalue);
            values.put(COL_PRODUCT_SGST,taxvalue/2);
            values.put(COL_PRODUCT_CGST,taxvalue/2);
            values.put(COL_PRODUCT_QNTY_BYUNIT,qty);


            int result = db.update(TABLE_QUOTATION_PRODUCTS_TEMP, values, COL_PRODUCT_ID + " =?", new String[]{String.valueOf(prodid)});

            db.close();

            return result != -1;
        } catch (SQLiteException e) {
            Log.v(TAG, "updateProductdetails  Exception  " + e.getMessage());
            return false;
        }

    }

    public double get_net_totalfrom_temp(int custid){
        Log.e("custid  db",""+custid);
        double net_total=0;
        double netPrice =0;
        String sql = "SELECT * FROM " + TABLE_QUOTATION_PRODUCTS_TEMP + " WHERE "+COL_FK_CUSTOMER_ID +" ="+custid;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);


        if (cursor.moveToFirst())
            do {

                CartItem c = new CartItem();

                c.setProductPrice(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                c.setNetprice_draft(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_NETPRICE)));
                c.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL)));
                c.setTax_type(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_TAXTYPE)));
                c.setTypeQuantity(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ORDER_TYPE_QUANTITY)));
                c.setProductDiscount(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_DISCOUNT)));;

                if(c.getTax_type().equals("TAX_INCLUSIVE")) {
                netPrice = c.getProductPrice() * c.getTypeQuantity();
                 }
                else{
                 netPrice = (c.getProductPrice()* c.getTypeQuantity())- (c.getProductDiscount() * c.getTypeQuantity());
                }
                net_total  += netPrice;


            } while (cursor.moveToNext());


        cursor.close();
        return net_total;
    }

    public double get_cart_total_kg(int custid){

        double count =0;
        //String sql = "SELECT SUM(product_kgm_qnty_txt) FROM " + TABLE_QUOTATION_PRODUCTS_TEMP + " WHERE "+COL_FK_CUSTOMER_ID +" ="+custid;
        SQLiteDatabase db = this.getReadableDatabase();



        Cursor cursor = db.rawQuery("SELECT SUM(product_kgm_qnty_txt) FROM " + TABLE_QUOTATION_PRODUCTS_TEMP + " WHERE " + COL_FK_CUSTOMER_ID + WHERE_CLAUSE , new String[]{String.valueOf(custid)});
        if (null != cursor)
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getDouble(0);
            }
        assert cursor != null;
        cursor.close();

        return count;
    }


    //    get Quotations products temp list corresponding customer
    public ArrayList<CartItem> getQuotationsProducttemp(int customereid) {
        Log.e("reached","getqtndetails"+customereid);

         // try {
        String sql = "SELECT * FROM " + TABLE_QUOTATION_PRODUCTS_TEMP + " WHERE "+COL_FK_CUSTOMER_ID +" ="+customereid;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        final ArrayList<CartItem> carts = new ArrayList<>();
        if (cursor.moveToFirst())
            do {

                CartItem c = new CartItem();

                c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                c.setCartId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                c.setProductPrice(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                c.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL)));
                c.setPieceQuantity(cursor.getInt(cursor.getColumnIndex(COL_SALE_PRODUCT_QUANTITY)));
                c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));
                c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_BONUS_PERCENTAGE)));
                c.setTypeQuantity(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ORDER_TYPE_QUANTITY)));
                c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                c.setOrderType(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));
                c.setOrderTypeName(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));
                c.setSalePrice(0);
                //c.setSalePrice(getSalePrice(c.getProductPrice(), c.getTax(),c.getTax_type()));
                c.setNetPrice(getWithoutTaxPrice(c.getProductPrice(), c.getTax()));
                c.setProductTotal(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TOTAL)));
                c.setProductTotalValue(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TOTAL_VALUE)));
                // c.setTaxValue(getTaxPrice(c.getProductTotal(),c.getTax(),c.getTax_type()));
                c.setTaxValue(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TAX_AMOUNT)));
                c.setProduct_hsncode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_HSNCODE)));
                c.setUnitselected(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_UNIT_SELECTED)));
                c.setProductDiscount(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_DISCOUNT)));
                c.setSgst((float) cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_SGST)));
                c.setCgst((float) cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_CGST)));
                c.setPieceQuantity_nw(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_QNTY_BYUNIT)));
                c.setTax_type(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_TAXTYPE)));
                c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                c.setConfactr_kg(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_CONFACTORKG)));
                c.setUnitid_selected(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITID)));
                c.setUnit_confactor(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITCONFACTOR)));
                c.setNetprice_draft(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_NETPRICE)));
                c.setCon_factor(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_CONFACTOR)));
                c.setPrice_kgm(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_KGM_PRICE)));
                c.setQnty_kgm(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_KGM_QNTY)));
                c.setDescription(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_DESCRIPTION)));
                c.setDisc_percentage(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_DISC_PERCENTAGE)));
                c.setP_name(cursor.getString(cursor.getColumnIndex(COL_P_NAME)));

                Log.e("prod_price",""+c.getProductPrice());
                carts.add(c);

            } while (cursor.moveToNext());


        cursor.close();

        db.close();
//        } catch (SQLiteException e) {
//            Log.v(TAG, "getQuotationsProduct TEMP Exception  " + e.getMessage());
//
//        }

        return carts;
    }

    //    get Quotations vat_status corresponding customer
    public String getvatstatus_from_quotationtemp(int customereid) {
        Log.e("reached","getqtndetails"+customereid);
        String vat_status ="";

        // try {
        String sql = "SELECT * FROM " + TABLE_QUOTATION_PRODUCTS_TEMP + " WHERE "+COL_FK_CUSTOMER_ID +" ="+customereid;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        final ArrayList<CartItem> carts = new ArrayList<>();
        if (cursor.moveToFirst())
            do {
                vat_status = cursor.getString(cursor.getColumnIndex(COL_PRODUCT_VAT_STATUS));

            } while (cursor.moveToNext());


        cursor.close();

        db.close();
//        } catch (SQLiteException e) {
//            Log.v(TAG, "getQuotationsProduct TEMP Exception  " + e.getMessage());
//
//        }

        return vat_status;
    }


    //    get Quotations products temp list corresponding customer
    public ArrayList<CartItem> getQuotationsProducttempedit(int customereid) {
        Log.e("reached","getqtndetails"+customereid);

        // try {
        String sql = "SELECT * FROM " + TABLE_QUOTATION_PRODUCTS_TEMP_EDIT + " WHERE "+COL_FK_CUSTOMER_ID +" ="+customereid;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        final ArrayList<CartItem> carts = new ArrayList<>();
        if (cursor.moveToFirst())
            do {

                CartItem c = new CartItem();

                c.setProductId(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)));
                c.setCartId(cursor.getInt(cursor.getColumnIndex(COL_ID)));

                c.setProductCode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_CODE)));
                c.setProductName(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                c.setProductPrice(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_PRICE)));
                c.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COL_SALE_TOTAL)));
                c.setPieceQuantity(cursor.getInt(cursor.getColumnIndex(COL_SALE_PRODUCT_QUANTITY)));
                c.setTax(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_TAX)));
                c.setProductBonus(cursor.getFloat(cursor.getColumnIndex(COL_BONUS_PERCENTAGE)));
                c.setTypeQuantity(cursor.getFloat(cursor.getColumnIndex(COL_SALE_ORDER_TYPE_QUANTITY)));
                c.setPiecepercart(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_PEACE_PER_CART)));
                c.setOrderType(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));
                c.setOrderTypeName(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_ORDER_TYPE)));
                //haris added on 20-04-21
//                    c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                c.setSalePrice(0);
                //c.setSalePrice(getSalePrice(c.getProductPrice(), c.getTax(),c.getTax_type()));
                c.setNetPrice(getWithoutTaxPrice(c.getProductPrice(), c.getTax()));
                c.setProductTotal(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TOTAL)));
                c.setProductTotalValue(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TOTAL_VALUE)));
                // c.setTaxValue(getTaxPrice(c.getProductTotal(),c.getTax(),c.getTax_type()));
                c.setTaxValue(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_TAX_AMOUNT)));

                c.setProduct_hsncode(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_HSNCODE)));

                c.setUnitselected(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_UNIT_SELECTED)));
                c.setProductDiscount(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_DISCOUNT)));
                c.setSgst((float) cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_SGST)));
                c.setCgst((float) cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_CGST)));
                c.setPieceQuantity_nw(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_QNTY_BYUNIT)));
                c.setTax_type(cursor.getString(cursor.getColumnIndex(COL_SALE_PRODUCT_TAXTYPE)));
                c.setMrprate(cursor.getFloat(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                c.setConfactr_kg(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_CONFACTORKG)));
                c.setUnitid_selected(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITID)));
                c.setUnit_confactor(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_UNITCONFACTOR)));
                c.setNetprice_draft(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_NETPRICE)));
                c.setCon_factor(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_CONFACTOR)));
                c.setPrice_kgm(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_KGM_PRICE)));
                c.setQnty_kgm(cursor.getDouble(cursor.getColumnIndex(COL_PRODUCT_KGM_QNTY)));
                c.setDescription(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_DESCRIPTION)));

                Log.e("prod_price",""+c.getProductPrice());
                carts.add(c);

            } while (cursor.moveToNext());


        cursor.close();

        db.close();
//        } catch (SQLiteException e) {
//            Log.v(TAG, "getQuotationsProduct TEMP Exception  " + e.getMessage());
//
//        }

        return carts;
    }


    //    get Quotations products temp list corresponding customer
    public void deleteproduct_fromtemp(int productid) {
        Log.e("reached","delete"+productid);

         try {
        String sql = "DELETE FROM " + TABLE_QUOTATION_PRODUCTS_TEMP + " WHERE "+COL_PRODUCT_ID +" = "+productid;

        Log.e("reached","qry"+sql);
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL(sql);




        db.close();
        } catch (SQLiteException e) {
            Log.v(TAG, "delete  Exception  " + e.getMessage());

        }

        return ;
    }

    //    get Quotations products temp list corresponding customer
    public void deleteproduct_fromtempedit(int productid) {
        Log.e("reached","delete"+productid);

        try {
            String sql = "DELETE FROM " + TABLE_QUOTATION_PRODUCTS_TEMP_EDIT + " WHERE "+COL_PRODUCT_ID +" = "+productid;

            Log.e("reached","qry"+sql);
            SQLiteDatabase db = this.getReadableDatabase();

            db.execSQL(sql);




            db.close();
        } catch (SQLiteException e) {
            Log.v(TAG, "delete  Exception  " + e.getMessage());

        }

        return ;
    }

    //    get Quotations products temp list corresponding customer
    public void deleteinvoice_from_sale(String custid) {
        Log.e("reached","delete"+custid);

        try {
            String sql = "DELETE FROM " + TABLE_SALE_CUSTOMER + " WHERE "+COL_INVOICE_CODE +" = "+"'"+custid+"'";

            Log.e("reached","qry"+sql);
            SQLiteDatabase db = this.getReadableDatabase();

            db.execSQL(sql);




            db.close();
        } catch (SQLiteException e) {
            Log.v(TAG, "delete  Exception  " + e.getMessage());

        }

        return ;
    }



    //    get Quotations products temp list corresponding customer
    public void deleteinvoice_from_saleproducts(String custid) {
        Log.e("reached","delete"+custid);

        try {
            String sql = "DELETE FROM " + TABLE_SALE_PRODUCTS + " WHERE "+COL_FK_CUSTOMER_ID +" = "+"'"+custid+"'";

            Log.e("reached","qry"+sql);
            SQLiteDatabase db = this.getReadableDatabase();

            db.execSQL(sql);




            db.close();
        } catch (SQLiteException e) {
            Log.v(TAG, "delete  Exception  " + e.getMessage());

        }

        return ;
    }

    //    insert route haris added 10-09-2020
    public boolean insertRoute(Route route) {

        boolean isExist = isExistCustomer(route.getId());  // check customer in table

        if (isExist)
            return false;


        if (isExistRoute(route.getId())) {

            // delete if already added
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_ROUTE + " where " + COL_ROUTE_ID + " ='" + route.getId() + "'");

        }
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            //Log.e("rout db", route.getRoute());
            values.put(COL_ROUTE_ID, route.getId());
            values.put(COL_ROUTE, route.getRoute());


            // Inserting Row
            long l = db.insert(TABLE_ROUTE, null, values);

            db.close(); // Closing database connection


            if (l == -1) {
                return false;
            } else {


                return true;

            }

        } catch (SQLiteException e) {

            Log.v(TAG, "insertCustomer  Exception  " + e.getMessage());
            return false;
        }
    }

    //    Get routes list TABBLE_ROUTE
    public ArrayList<Route> get_routs() {

        ArrayList<Route> list = new ArrayList<>();
        try {

            String sql = "SELECT * FROM " + TABLE_ROUTE + " ";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);


            if (cursor.moveToFirst())
                do {
                    Route r = new Route();

                    r.setId(cursor.getInt(cursor.getColumnIndex(COL_ROUTE_ID)));
                    r.setRoute(cursor.getString(cursor.getColumnIndex(COL_ROUTE)));

                    list.add(r);

                } while (cursor.moveToNext());

            cursor.close();
            db.close();


        } catch (SQLiteException e) {
            Log.v(TAG, "getAllCustomers  Exception  " + e.getMessage());

        }

        return list;
    }

    //      Route Row From to SQLite  "TABLE_ROUTE" Table
    private boolean isExistRoute(int routid) {

        int count = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_ROUTE + " WHERE " + COL_ROUTE_ID + WHERE_CLAUSE, new String[]{String.valueOf(routid)});

//            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_CUSTOMER , new String[]{});

            if (null != cursor)
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
            assert cursor != null;
            cursor.close();

            db.close();
        } catch (SQLiteException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }

        return count != 0;

    }

    //    get getAllStock list
    public ArrayList<OrderItems> getOrderPlaceProducts() {

        final ArrayList<OrderItems> cartItems = new ArrayList<>();
        try {
            String sql = "SELECT   a.product_wholesale_real,case when b.product_quantity_int is not null then b.product_quantity_int else 0 end as product_quantity_int,case when a.product_id_int is not null then a.product_id_int else 0 end as product_id_int,a.product_name_txt FROM tbl_stock_master a  LEFT JOIN tbl_stock b on a.product_id_int=b.product_id_int  ";
            // String sql = "SELECT * FROM " + TABLE_STOCK_MASTER + " ORDER BY " + COL_PRODUCT_PRIORITY_MASTER + " ASC ";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst())
                do {

                    OrderItems c = new OrderItems();
                    c.setProductid(String.valueOf(cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID))));
                    c.setProductname(cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME)));
                    c.setStockqty( cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY)));

                    cartItems.add(c);
                    // }
                } while (cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (SQLiteException e) {

            Log.v(TAG, "getAllStock  Exception  " + e.getMessage());
        }

        return cartItems;
    }

}
