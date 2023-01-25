package com.advanced.minhas;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
//
//import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Expense;
import com.advanced.minhas.model.NoSale;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.chequeReceipt;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_ANY_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_CUSTOMER_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_QUOTATION_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_CHEQUE;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_RETURN_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_SALE_TYPE;
import static com.advanced.minhas.config.Generic.PRINT_FORMAT;
import static com.advanced.minhas.config.Generic.dbDateFormat;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getAmountthree;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.webservice.WebService.webDayClose;
import static com.advanced.minhas.webservice.WebService.webExpenseEntry;
import static com.advanced.minhas.webservice.WebService.webNoSale;
import static com.advanced.minhas.webservice.WebService.webOfflineReturn;

import static com.advanced.minhas.webservice.WebService.webPaidReceipt;
import static com.advanced.minhas.webservice.WebService.webPlaceOrder;
import static com.advanced.minhas.webservice.WebService.webPlaceQuotation;
import static com.advanced.minhas.webservice.WebService.webReturn;

/**
 * Created by mentor on 24/11/17.
 */

public class DataServiceOperations {

    OnNotifyListener listener;
    MyDatabase myDatabase;
    private Context context;
    private String TAG = "DataServiceOperations";
    private String EXECUTIVE_ID = "";

    private SessionValue sessionValue;
    private SessionAuth sessionAuth;
    private String dayRegId;
    private String kilometer="";
    private String day_OpenTime="";

    ArrayList<Sales> saleList =new ArrayList<>();
    ArrayList<Sales> quotationList = new ArrayList<>();
    ArrayList<Sales> returnList = new ArrayList<>();
    ArrayList<Receipt> receipts  =new ArrayList<>();
    ArrayList<chequeReceipt> chequereceipts  =new ArrayList<>();
    ArrayList<NoSale> noSales  =new ArrayList<>();

    ArrayList<Expense> expenseEntry  =new ArrayList<>();

    ArrayList<Sales> returnOfflineList = new ArrayList<>();

    String db_todaysale = "", db_todaycollection="";

    public DataServiceOperations(Context context, OnNotifyListener mListener) {
        this.context = context;
        this.sessionValue = new SessionValue(context);
        this.sessionAuth = new SessionAuth(context);
        this.EXECUTIVE_ID =sessionAuth.getExecutiveId();
        this.myDatabase =new MyDatabase(context);

        this.listener = mListener;
    }

    public void dayClose(String km) {

        db_todaysale = ""+getAmount(myDatabase.getTodaySaleAmount());
        db_todaycollection = ""+getAmount(myDatabase.getCollectionAmount() + myDatabase.getSalePaidAmount());

        this.dayRegId=sessionValue.getDayRegisterId();
        this.saleList = myDatabase.getAllSales();
        this.quotationList = myDatabase.getAllQuotations();
        this.returnList = myDatabase.getAllReturns();
        this.receipts = myDatabase.getAllReceipts();
        this.chequereceipts = myDatabase.getAllChequeReceipts();
        this.noSales = myDatabase.getAllNoSaleReasons();

        this.expenseEntry = myDatabase.getAllExpenseDetails();

      //  this.returnOfflineList = myDatabase.getOfflineReturn();

        this.kilometer=km;

      //  expenseDetails();

        if (!saleList.isEmpty())
            placeOrder();
        else if (!quotationList.isEmpty())
            placeQuotation();
        else if (!returnList.isEmpty())
            placeReturn();
        else if (!receipts.isEmpty())
            paidReceipt();
        else if (!chequereceipts.isEmpty())
            chequeReceipt();
        else if (!returnOfflineList.isEmpty())
            OfflineSaleReturn();
        else if (!noSales.isEmpty())
            noSaleRequest();
        else
            dayCloseRequest();
    }

//    //    place order
//    private void placeOrder() {
//
//        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);
//
//        final JSONObject object = new JSONObject();
//
//        final JSONArray saleArray = new JSONArray();
//
//        try {
//            for (Sales s : saleList) {
//
//                Log.e("Upload Status", "//"+s.getUploadStatus());
//
//                final JSONObject saleObj = new JSONObject();
//
//                saleObj.put(CUSTOMER_KEY, s.getCustomerId());
//                saleObj.put("bill_date", s.getDate());
//                saleObj.put("total_amount", s.getTotal());
//                saleObj.put("sale_type", s.getSaleType());
//                saleObj.put("paid_amount", s.getPaid());
//                saleObj.put("invoice_no", s.getInvoiceCode());
//
//               // saleObj.put("tax_percentage", s.getTaxPercentage());
//                saleObj.put("tax_amount", s.getTaxAmount());
//                saleObj.put("without_tax_total", s.getWithoutTaxTotal());
//                saleObj.put("with_tax_total", s.getWithTaxTotal());
//
//                //haris added on 06-11-2020
//
//                saleObj.put("taxable_value", s.getTaxable_total());
//
//                //saleObj.put("tax_rate", s.getCgst_tax_rate());
//
//                saleObj.put("discount", s.getDiscount_value());
//                saleObj.put("discount_percentage", s.getDiscount_percentage());
//                saleObj.put("roundoff_tot", s.getRoundofftot());
//                saleObj.put("roundoff", s.getRoundoff_value());
//
//                saleObj.put("latitude", s.getSaleLatitude());
//                saleObj.put("longitude", s.getSaleLongitude());
//                saleObj.put("invoice_type", s.getPayment_type());
//                saleObj.put(" cgst_tax_rate","0");
////                if (s.getPaid() == s.getTotal()) {
////                    Log.e("getpaid if",""+s.getPaid());
////                    Log.e("getTotal if",""+s.getTotal());
////                    saleObj.put("invoice_type", "CashSale");
////                }
////                else {
////                    Log.e("getpaid else",""+s.getPaid());
////                    Log.e("getTotal else",""+s.getTotal());
////                    saleObj.put("invoice_type", "CreditSale");
////                }
//
//                JSONArray cartArray = new JSONArray();
//
//                for (CartItem c : s.getCartItems()) {
//
//                    JSONObject obj = new JSONObject();
//
//                    obj.put("product_id", c.getProductId());
//                    obj.put("unit_price", c.getProductPrice());
//                    obj.put("product_quantity", c.getPieceQuantity());
//                    obj.put("product_total", c.getTotalPrice());
//                    obj.put("product_bonus_percentage", c.getProductBonus());
//                    obj.put("product_unit", c.getOrderType());
//                    obj.put("product_tax_rate", c.getTax());
//                    obj.put("product_tax_amnt", c.getTaxValue());
//                    obj.put("product_discount", ""+c.getProductDiscount());
//                    obj.put("each_size_qty", c.getSizeandqty_string());
//                    obj.put("size_id", c.getSize_string());
//
//                    cartArray.put(obj);
//
//                }
//
//                saleObj.put("ordered_products", cartArray);
//
//                saleArray.put(saleObj);
//            }
//
//            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
//            object.put(DAY_REGISTER_KEY, dayRegId);
//            object.put("Sale", saleArray);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    //    place order
    private void placeOrder() {

        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray saleArray = new JSONArray();

        try {
            for (Sales s : saleList) {

                Log.e("Upload Status", "//"+s.getUploadStatus());

                final JSONObject saleObj = new JSONObject();

                saleObj.put(CUSTOMER_KEY, s.getCustomerId());
                saleObj.put("bill_date", s.getDate());

                saleObj.put("total_amount",  getAmount(s.getWithTaxTotal()));

                saleObj.put("sale_type", s.getSaleType());
                saleObj.put("paid_amount", getAmount(s.getPaid()));
                saleObj.put("invoice_no", s.getInvoiceCode());
                saleObj.put("without_tax_total", getAmount(s.getWithoutTaxTotal()));

                saleObj.put("discount", getAmount(s.getDiscount()));
                saleObj.put("discount_total", getAmount(s.getDiscountAmount()));

                saleObj.put("tax_amount", getAmount(s.getTaxAmount()));
                saleObj.put("with_tax_total", getAmount(s.getWithTaxTotal()));
                saleObj.put("invoice_type", s.getPayment_type());
                saleObj.put("round_off", getAmount(s.getRoundoff_value()));
                saleObj.put("driver_name", sessionValue.getdrivername());
                saleObj.put("vehicle_no", sessionValue.getvehicleno());
                JSONArray cartArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {
                    int tx_type =0;
                    JSONObject obj = new JSONObject();
                    if(c.getTax_type().equals("TAX_INCLUSIVE")){
                         tx_type =1;
                    }


                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", getAmount(c.getProductPrice()));
//                    obj.put("product_quantity", c.getPieceQuantity());
                    obj.put("product_quantity", c.getTypeQuantity());

                    // obj.put("product_total", c.getProductPrice()*c.getPieceQuantity()); // c.getTotalPrice()
                   // obj.put("product_total", ""+getAmount(c.getProductTotal())); // c.getTotalPrice()
                    obj.put("product_total", ""+getAmount((c.getProductPrice()- c.getProductDiscount())*  c.getTypeQuantity())); // c.getTotalPrice()
                    //obj.put("product_bonus_percentage", c.getProductBonus());
                    obj.put("product_unit", c.getOrderType());
                    obj.put("tax", getAmount(c.getTax()));
                    obj.put("tax_amont", getAmount(c.getTaxValue()));
                    obj.put("product_discount", ""+getAmount(c.getProductDiscount()));
                    obj.put("tax_type",tx_type);
                    obj.put("mfg_date",c.getMfg_date());
                    obj.put("description",c.getDescription());
                    obj.put("discount_per",c.getDisc_percentage());
                    obj.put("foc",c.getFreeQty());
                    cartArray.put(obj);

                }

                saleObj.put("ordered_products", cartArray);

                saleArray.put(saleObj);
            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("Sale", saleArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

       printLog(TAG, "placeOrder cart object  " + object);
        Log.e("placeOrder cart object",""+object);

        webPlaceOrder(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "placeOrder  response   " + response);
                try {
                    // 6238277238
                    if (response.getString("status").equalsIgnoreCase("Success")) {

                        boolean deleteStatus = myDatabase.deleteTableRequest(REQ_SALE_TYPE);

                        printLog(TAG, "placeOrder  deleteStatus   " + deleteStatus);

                       if (!quotationList.isEmpty())
                            placeQuotation();
                        else if (!returnList.isEmpty())
                            placeReturn();
                        else if (!receipts.isEmpty())
                            paidReceipt();
                       else if (!chequereceipts.isEmpty())
                            chequeReceipt();
                        else if (!returnOfflineList.isEmpty())
                            OfflineSaleReturn();
                        else if (!noSales.isEmpty())
                            noSaleRequest();
                        else
                            dayCloseRequest();
                    } else
                        Toast.makeText(context, "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        }, object);
    }

    //    place quotation
    private void placeQuotation() {

        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray quotationArray = new JSONArray();

        try {
            for (Sales s : quotationList) {


                Log.e("Upload Status", "//"+s.getUploadStatus());

                final JSONObject saleObj = new JSONObject();
                ////////////////////////////////////
                saleObj.put(CUSTOMER_KEY, s.getCustomerId());
                saleObj.put("bill_date", s.getDate());
                saleObj.put("total_amount",  getAmount(s.getWithTaxTotal()));
                saleObj.put("sale_type", s.getSaleType());
                saleObj.put("paid_amount", getAmount(s.getPaid()));
                saleObj.put("invoice_no", s.getInvoiceCode());
                saleObj.put("without_tax_total", getAmount(s.getWithoutTaxTotal()));
                saleObj.put("discount", getAmount(s.getDiscount()));
                saleObj.put("discount_total", getAmount(s.getDiscountAmount()));
                saleObj.put("tax_amount", getAmount(s.getTaxAmount()));
                saleObj.put("with_tax_total", getAmount(s.getWithTaxTotal()));
                saleObj.put("invoice_type", "CreditSale");
                saleObj.put("round_off", getAmount(s.getRoundoff_value()));

                //////////////////////////////////




                JSONArray cartArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {

                    JSONObject obj = new JSONObject();
                    int tx_type =0;

                    if(c.getTax_type().equals("TAX_INCLUSIVE")){
                        tx_type =1;
                    }
                    ////////////////////////////////
                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", getAmountthree(c.getProductPrice()));
                    obj.put("product_quantity", c.getPieceQuantity_nw());
                    // obj.put("product_total", c.getProductPrice()*c.getPieceQuantity()); // c.getTotalPrice()
                    obj.put("product_total", ""+getAmountthree(c.getProductPrice()*c.getPieceQuantity_nw()));
                    //obj.put("product_bonus_percentage", c.getProductBonus());
                    obj.put("product_unit", c.getOrderType());
                    obj.put("tax", getAmount(c.getTax()));
                    obj.put("tax_amont", getAmount(c.getTaxValue()));
                    obj.put("product_discount", ""+getAmount(c.getProductDiscount()));
                    obj.put("cgst", getAmount(c.getCgst()));
                    obj.put("sgst", getAmount(c.getSgst()));
                    obj.put("tax_type",tx_type);
                    ///////////////////////////////////////


                    cartArray.put(obj);

                }

                saleObj.put("ordered_products", cartArray);

                quotationArray.put(saleObj);

            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("Sale", quotationArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "placeQuotation  Object" + object);
        Log.e(TAG, "placeQuotation  Object" + object);

        webPlaceQuotation(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "placeQuotation  response   " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        boolean deleteStatus = myDatabase.deleteTableRequest(REQ_QUOTATION_TYPE);
                        Log.v(TAG, "placeQuotation  deleteStatus   " + deleteStatus);

                    if (!returnList.isEmpty())
                            placeReturn();
                        else if (!receipts.isEmpty())
                            paidReceipt();
                       else if (!chequereceipts.isEmpty())
                            chequeReceipt();
                        else if (!returnOfflineList.isEmpty())
                            OfflineSaleReturn();
                        else if (!noSales.isEmpty())
                            noSaleRequest();
                        else
                            dayCloseRequest();

                    } else
                        Toast.makeText(context, "Quotation " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    //    place return
    private void placeReturn() {

        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray returnArray = new JSONArray();

        try {
            for (Sales r : returnList) {
                final String strDate = getDateTime();
                final JSONObject returnObj = new JSONObject();


                returnObj.put("invoice_id", "0");
                returnObj.put(CUSTOMER_KEY, r.getCustomerId());
                returnObj.put("tax_total", r.getTaxAmount());
                returnObj.put("total_amount", r.getTotal());
                returnObj.put("return_date",strDate);
                returnObj.put("without_tax_total", r.getTaxable_total());
                returnObj.put("discount", r.getDiscount());
                returnObj.put("discount_total", r.getDiscountAmount());
                returnObj.put("latitude", r.getSaleLatitude());
                returnObj.put("longitude", r.getSaleLongitude());
                returnObj.put("invoice_type", r.getPayment_type());
                returnObj.put("return_invoiceno", r.getInvoiceCode());
                returnObj.put("executive_id", EXECUTIVE_ID);
                returnObj.put("round_off", getAmount(r.getRoundoff_value()));
                returnObj.put("amount_return", r.getPaid());

                JSONArray cartArray = new JSONArray();

                for (CartItem c : r.getCartItems()) {
                    double tax_prod = roundTwoDecimals(c.getTaxValue()*c.getPieceQuantity_nw());
                   // double unit_price = roundTwoDecimals(c.getProductPrice());

                    int tx_type =0;
                    double unit_price =c.getProductPrice();
                    if(c.getTax_type().equals("TAX_INCLUSIVE")){
                        tx_type =1;
                        unit_price = c.getProductTotal()/c.getPieceQuantity_nw();

                    }

                    unit_price =roundTwoDecimals(unit_price);
                    JSONObject obj = new JSONObject();

                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", unit_price);
                    obj.put("tax_rate", c.getTax());
                    obj.put("tax_amount", tax_prod);
                    obj.put("return_quantity", c.getPieceQuantity_nw());
                    obj.put("product_unit", c.getUnitselected());

                    obj.put("product_discount", c.getProductDiscount());
                    obj.put("product_total", getAmount(c.getProductTotal()));
                    obj.put("tax_type", tx_type);

                    cartArray.put(obj);
                }

                returnObj.put("ReturnedProduct", cartArray);

                returnArray.put(returnObj);

            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("SalesReturn", returnArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

       printLog(TAG, "placeReturn  object   " + object);

        webReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {


             //   printLog(TAG, "placeReturn  response   " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("Success")) {

                       boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RETURN_TYPE);

                       printLog(TAG, "placeReturn  deleteStatus   " + deleteStatus);

                       if (!receipts.isEmpty())
                            paidReceipt();
                       else if (!chequereceipts.isEmpty())
                            chequeReceipt();
                        else if (!returnOfflineList.isEmpty())
                            OfflineSaleReturn();
                        else if (!noSales.isEmpty())
                            noSaleRequest();
                        else
                            dayCloseRequest();

                    } else
                        Toast.makeText(context, "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    private double roundTwoDecimals(double taxValue) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(taxValue));
    }

    //   Cheque Receipt

    private void chequeReceipt(){
        Log.e("inside","1");
        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);
        JSONObject object = new JSONObject();
        JSONArray receiptarray = new JSONArray();

        try {
            for (chequeReceipt c : chequereceipts) {

                JSONObject obj = new JSONObject();
                obj.put("receipt_no", c.getReceiptNo());
                obj.put("amount", c.getReceivedAmount());
                obj.put("cheque_no", c.getChequeNo());
                obj.put(CUSTOMER_KEY, c.getCustomerId());
                obj.put("company_bank_id", c.getCompanyBankId());
                obj.put("customer_bank", c.getCustomerBank());
                obj.put("received_date", c.getCreatedDate());
                obj.put("clearing_date", c.getClearingdate());
                obj.put("cheque_date", c.getChequeDate());
                obj.put("type", "3");
                obj.put("datetime", c.getLogDate());

                receiptarray.put(obj);
            }
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("PaidList", receiptarray);

            pd.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "cheque Receipt object  " + object);


        webPaidReceipt(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.v(TAG, "paidReceipt  response  " + response);

                try {
                    if (response.getString("result").equalsIgnoreCase("success")) {

                        boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RECEIPT_CHEQUE);

                      //  Log.v(TAG, "paidReceipt  deleteStatus   " + deleteStatus);


                         if(!returnOfflineList.isEmpty())
                            OfflineSaleReturn();
                        else if (!noSales.isEmpty())
                            noSaleRequest();
                        else
                            dayCloseRequest();

                    } else
                        Toast.makeText(context, "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }


    //   Expense Details

    private void expenseDetails(){
        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        JSONArray expensearray = new JSONArray();

        try {
            for (Expense c : expenseEntry) {

                JSONObject obj = new JSONObject();

                obj.put("expense_id", c.getId());
                obj.put("amount", c.getAmount());
                obj.put("receipt_no", c.getReceiptNo());
                obj.put("remarks", c.getRemarks());

                expensearray.put(obj);
            }
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("expense_list", expensearray);

            pd.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "expense entry object  " + object);


        webExpenseEntry(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.v(TAG, "paidReceipt  response  " + response);

                try {
                    if (response.getString("result").equalsIgnoreCase("success")) {

                        //  boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RECEIPT_TYPE);

                        //  Log.v(TAG, "paidReceipt  deleteStatus   " + deleteStatus);


                        if(!returnOfflineList.isEmpty())
                            OfflineSaleReturn();
                        else if (!noSales.isEmpty())
                            noSaleRequest();
                        else
                            dayCloseRequest();

                    } else
                        Toast.makeText(context, "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();

            } // direct purchase, unit transfer

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }



    //   Offline Return

    private void OfflineSaleReturn(){
        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        JSONArray returntarray = new JSONArray();

        try {

            for (Sales s : returnOfflineList) {

                final JSONObject returnObj = new JSONObject();

                returnObj.put("invoice_id", s.getInvoiceCode());
                returnObj.put(CUSTOMER_KEY, s.getCustomerId());
                returnObj.put("tax_total", s.getTotal());
                returnObj.put("grand_total", s.getPaid());

                JSONArray productArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {

                    JSONObject obj = new JSONObject();

                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", c.getProductPrice());
                    obj.put("tax", c.getTax()); //percentage
                    obj.put("return_quantity", c.getReturnQuantity());
                    obj.put("refund_Amount", c.getTotalPrice());
                    obj.put("product_bonus", c.getProductBonus());

                    productArray.put(obj);
                }

                returnObj.put("ReturnedProduct", productArray);
                returntarray.put(returnObj);
            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("SalesReturn", returntarray);

            pd.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "Offline Return object  " + object);


        webOfflineReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG, "offline return  response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        //  boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RECEIPT_TYPE);
                        //  Log.v(TAG, "paidReceipt  deleteStatus   " + deleteStatus);

                        if (!noSales.isEmpty())
                            noSaleRequest();
                        else
                            dayCloseRequest();


                    } else
                        Toast.makeText(context, "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }


    //    paid receipt
    private void paidReceipt() {

        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);


        JSONObject object = new JSONObject();
        JSONArray receiptArray = new JSONArray();
        try {

            for (Receipt receipt : receipts) {

                final String receiptNo = receipt.getReceiptNo();
                final float receivableAmount = receipt.getReceivedAmount();
                int customerId = receipt.getCustomerId();


                JSONObject obj = new JSONObject();
                obj.put("amount", receivableAmount);
                obj.put("receipt_no", receiptNo);
                obj.put(CUSTOMER_KEY, customerId);
                obj.put("datetime", ""+receipt.getLogDate());

                obj.put("latitude", ""+receipt.getLatitude());
                obj.put("longitude", ""+receipt.getLongitude());
                obj.put("type", ""+receipt.getReceipt_type());
                obj.put("bank", ""+receipt.getBankname());
                obj.put("bankid", ""+receipt.getBankid());
                obj.put("voucherno", ""+receipt.getVoucherno());
                obj.put("reference_no", ""+receipt.getReference_no());


//                        if (receivableAmount != 0)
                receiptArray.put(obj);
            }


            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("PaidList", receiptArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "paidReceipt  object  " + object);
        Log.e(TAG, "paidReceipt  object  " + object);


        webPaidReceipt(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.v(TAG, "paidReceipt  response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RECEIPT_TYPE);

                        Log.v(TAG, "paidReceipt  deleteStatus   " + deleteStatus);

                        if (!chequereceipts.isEmpty())
                            chequeReceipt();
                        else if (!noSales.isEmpty())
                            noSaleRequest();
                        else
                            dayCloseRequest();

                    } else
                        Toast.makeText(context, "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);


    }





    //    no sale
    private void noSaleRequest() {

        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        try {

            for (NoSale noSale : noSales) {

                JSONObject obj = new JSONObject();
                obj.put(CUSTOMER_KEY, noSale.getCustomerId());
                obj.put("reason", noSale.getReason());
                obj.put("datetime", noSale.getDatetime());

                obj.put("latitude", noSale.getLatitude());
                obj.put("longitude", noSale.getLongitude());

                array.put(obj);

            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("Visits", array);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "noSaleRequest object  " + object);

        printLog("NoSale Request", ""+object);

        webNoSale(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.v(TAG, "noSaleRequest  response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        boolean deleteStatus = myDatabase.deleteTableRequest(REQ_CUSTOMER_TYPE);

                        if (deleteStatus)

                            dayCloseRequest();
                        else

                            Toast.makeText(context, "No Sale  failed", Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(context, "No Sale "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    //    day close request
    private void dayCloseRequest() {


        JSONObject object = new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("closing_stock", myDatabase.getStockAmount());
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("km", kilometer);
            object.put("datetime", myDatabase.getDateTime());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "dayCloseRequest  object " + object);

        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        webDayClose(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

//                   printLog(TAG, "dayCloseRequest  response   " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        // change status
                        sessionValue.save_latitude_and_longitude("","");
                        sessionValue.storeGroupAndRoute(null, null, 0,"","","","","");
                        boolean deleteStatus = myDatabase.deleteTableRequest(REQ_ANY_TYPE);
                        printLog(TAG, "dayCloseRequest  deleteStatus   " + deleteStatus);

                        if (listener != null)
                            listener.onNotified();

                        Toast.makeText(context, "Day Close Successful", Toast.LENGTH_SHORT).show();

                        sessionAuth.updateBonus(0);

                        //  sendEmail();

                    } else if (response.getString("status").equals("Already Closed")) {
                        boolean deleteStatus = myDatabase.deleteTableRequest(REQ_ANY_TYPE);
                        printLog(TAG, "dayCloseRequest  deleteStatus   " + deleteStatus);

                        // change status
                        sessionValue.storeGroupAndRoute(null, null, 0,"","","","","");

                        if (listener != null)
                            listener.onNotified();
                        Toast.makeText(context, response.getString("status"), Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(context, "Day Close "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(context, "Day Close Unknown wrong..!", Toast.LENGTH_SHORT).show();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);

    }

//    private void sendEmail(){
//
//        String mail="hudaibah.distribution@gmail.com";
//        String password="hudaibah@2019";
//
//        BackgroundMail.newBuilder(context)
//                .withUsername(mail)
//                .withPassword(password)
//                .withMailto("sheheedk@gmail.com") // Alhudaibah sheheedk@gmail.com
//                .withType(BackgroundMail.TYPE_PLAIN)
//                .withSubject("Alhudaibah Executive Day Close Status")
//              //.withBody(sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME)+" ("+sessionAuth.getUserDetails().get(PREF_KEY_USER_NAME))
//                .withBody(sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME)+" ("+sessionAuth.getUserDetails().get(PREF_KEY_USER_NAME)+") Day Close on "+getPrintDate()+
//                        "\n"+"Day Open Time : "+sessionAuth.getOpeningTime()+"\n"+"Today Sale : "+db_todaysale+
//                        "\n"+"Today Collection : "+db_todaycollection)
//                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
//                    @Override
//                    public void onSuccess() {
//
//                        Log.v(TAG,"mail  success ");
//                    }
//                })
//                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
//                    @Override
//                    public void onFail() {
//                        //do some
//                        Log.v(TAG,"mail  false ");
//                    }
//                })
//                .send();
//    }

    private static String getPrintDate() {

        return PRINT_FORMAT.format(Calendar.getInstance().getTime());
    }

    private static String getDateTime() {

        Date date = new Date();
        return dbDateFormat.format(date);
    }

}
