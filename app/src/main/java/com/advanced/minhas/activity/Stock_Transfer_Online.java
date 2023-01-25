package com.advanced.minhas.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.MfgDate;
import com.advanced.minhas.model.Product;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Size;
import com.advanced.minhas.model.Taxes;
import com.advanced.minhas.model.Units;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;
import com.google.gson.Gson;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.advanced.minhas.R;
import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_QUOTATION_TEMP;
import static com.advanced.minhas.config.ConfigKey.REQ_VAN_STOCK;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.localdb.MyDatabase.getDateTime;
import static com.advanced.minhas.webservice.WebService.webGetVanStock;
import static com.advanced.minhas.webservice.WebService.webPaidReceipt;
import static com.advanced.minhas.webservice.WebService.webPlaceOrder;
import static com.advanced.minhas.webservice.WebService.webReturn;

public class Stock_Transfer_Online extends AppCompatActivity {
    private MyDatabase myDatabase;
    Button bttn_approve, bttn_reject;
    private String EXECUTIVE_ID = "" ,dayRegId ="" ,TAG = "StockApproval";
    String statusFlag = "";
    ImageButton ibBack;
    SessionValue sessionValue;
    private SessionAuth sessionAuth;
    ArrayList<Sales> saleList =new ArrayList<>();

    ArrayList<Sales> quotationList = new ArrayList<>();
    ArrayList<Sales> returnList = new ArrayList<>();
    ArrayList<Receipt> receipts  =new ArrayList<>();
    MyDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer_online);
        db = new MyDatabase(Stock_Transfer_Online.this);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);

        bttn_approve= findViewById(R.id.button_approvestock);

        sessionValue = new SessionValue(this);
        sessionAuth =new SessionAuth(this);
        myDatabase = new MyDatabase(this);
        try {
            EXECUTIVE_ID = sessionAuth.getExecutiveId();
        } catch (Exception e) {
            e.getMessage();
        }

        bttn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayRegId = sessionValue.getDayRegisterId();
                saleList = myDatabase.getAllSales();
                returnList = myDatabase.getAllReturns();
                if((saleList.size()>0)|| (returnList.size()>0)){
                    syncConfirmationDialogue();
                }
                else{
                    storeVanStock();
                }


            }
        });



    }

    private void syncConfirmationDialogue() {

        new AlertDialog.Builder(Stock_Transfer_Online.this)
                .setMessage("Better network is avalable now ?  \nConfirm Sync First Then Transfer Stock..!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (isNetworkConnected()) {
                            new AsyncTaskLoadDataSync().execute();

                        }
                        else{
                            Toast.makeText(Stock_Transfer_Online.this, "No Network ", Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to register
                    }
                })
                .show();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Stock_Transfer_Online.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private class AsyncTaskLoadDataSync extends AsyncTask<JSONArray, String, String> {
        private final static String TAG = "AsyncTaskLoadImage";

        @Override
        protected String doInBackground(JSONArray... jsonArrays) {

            Sync_pending();

            return "";
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(Stock_Transfer_Online.this,"Updload Completed..!",Toast.LENGTH_LONG).show();
            // Toast.makeText(getContext(), "data updated", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                }
            }, 500);

        }
    }

    private void Sync_pending() {

        Log.e("inside","jj");
        this.saleList = myDatabase.getAllSales();
        this.quotationList = myDatabase.getAllQuotations();
        this.returnList = myDatabase.getAllReturns();
        this.receipts = myDatabase.getAllReceipts();
        //this.chequereceipts = myDatabase.getAllChequeReceipts();

//        if (!quotationList.isEmpty())
//            placeQuotation();

          if (!saleList.isEmpty())
            placeOrder();
        else if (!returnList.isEmpty())
            placeReturn();
        else if (!receipts.isEmpty())
            paidReceipt();


            //    else if (!quotationList.isEmpty())
            //placeQuotation();


    }
    private void placeOrder() {

        // final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);

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
                    //obj.put("product_total", ""+getAmount(c.getProductTotal())); // c.getTotalPrice()
                    obj.put("product_total", ""+getAmount((c.getProductPrice()- c.getProductDiscount())*  c.getTypeQuantity())); // c.getTotalPrice()
                    //obj.put("product_bonus_percentage", c.getProductBonus());
                    obj.put("product_unit", c.getOrderType());
                    obj.put("tax", getAmount(c.getTax()));
                    obj.put("tax_amont", getAmount(c.getTaxValue()));
                    obj.put("product_discount", ""+getAmount(c.getProductDiscount()));
                    obj.put("tax_type",tx_type);
                    obj.put("description",c.getDescription());
                    obj.put("discount_per",c.getDisc_percentage());
                    obj.put("mfg_date",c.getMfg_date());
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

        Log.e("placeOrder cart object",""+object);

        webPlaceOrder(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "placeOrder  response   " + response);
                try {

                    if (response.getString("status").equalsIgnoreCase("Success")) {

                        for (Sales s : saleList) {
                            myDatabase.UpdateSalesUploadStatus("" + s.getInvoiceCode());
                        }
                        saleList.clear();
                        if (!returnList.isEmpty())
                            placeReturn();
                        else if (!receipts.isEmpty())
                            paidReceipt();
                        // printLog(TAG, "placeOrder  deleteStatus   " + deleteStatus);

                        // if (!quotationList.isEmpty())
                        //  placeQuotation();
//                        else if (!returnList.isEmpty())
//                            placeReturn();
//                        else if (!receipts.isEmpty())
//                            paidReceipt();
//                        else if (!chequereceipts.isEmpty())
//                            chequeReceipts();
//                        else if (!returnOfflineList.isEmpty())
//                            OfflineSaleReturn();
//                        else if (!noSales.isEmpty())
//                            noSaleRequest();

                    } else
                        Toast.makeText(Stock_Transfer_Online.this, "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //    pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                //   pd.dismiss();
                Toast.makeText(Stock_Transfer_Online.this, error, Toast.LENGTH_SHORT).show();
            }
        }, object);
    }

    private void paidReceipt() {

        //final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);


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

        // printLog(TAG, "paidReceipt  object  " + object);
        Log.e(TAG, "paidReceipt  object  " + object);


        webPaidReceipt(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.v(TAG, "paidReceipt  response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        // boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RECEIPT_TYPE);

                        //Log.v(TAG, "paidReceipt  deleteStatus   " + deleteStatus);
                        Toast.makeText(Stock_Transfer_Online.this,"Updload Completed..!",Toast.LENGTH_LONG).show();
                        for (Receipt s : receipts) {
                            myDatabase.UpdatePaidReceiptUploadStatus(s.getReceiptNo());
                        }
                        if (!returnList.isEmpty()) {
                            placeReturn();
                        }

                    } else
                        Toast.makeText(Stock_Transfer_Online.this, "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                //pd.dismiss();
                Toast.makeText(Stock_Transfer_Online.this, error, Toast.LENGTH_SHORT).show();

            }
        }, object);


    }
    private void placeReturn() {

        //   final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

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

                        // boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RETURN_TYPE);

                        //  printLog(TAG, "placeReturn  deleteStatus   " + deleteStatus);
                        for (Sales s : returnList) {
                            myDatabase.UpdateWoInvoiceUploadStatus("" + s.getInvoiceCode());
                        }



                    } else
                        Toast.makeText(Stock_Transfer_Online.this, "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                //pd.dismiss();
                Toast.makeText(Stock_Transfer_Online.this, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }
    private double roundTwoDecimals(double taxValue) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(taxValue));
    }

    private void storeVanStock() {
        Log.e("reched hr","1");

//        if (myDatabase.isExistProducts()) {
//            return;
//        }

        if (!isNetworkConnected()) {
            Toast.makeText(Stock_Transfer_Online.this, "No Network ", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(Stock_Transfer_Online.this, null, "Please wait...", false, false);

        final ArrayList<Product> products = new ArrayList<>();
        final JSONObject object = new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"storeVanStock Object   "+object);

        webGetVanStock(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG,"storeVanStock response   "+response.toString());

                try {
                    int int_product_qty = 0;

                    if (response.getString("status").equalsIgnoreCase(
                            "success") && !response.isNull("Stocks")) {
                        JSONArray stockArray = response.getJSONArray("Stocks");
                        for (int i = 0; i < stockArray.length(); i++) {

                            JSONObject stockObject = stockArray.getJSONObject(i);
                            Product product = new Product();

                            //    Log.v(TAG,"storeVanStock   stockObject   "+stockObject);

                            product.setProductId(stockObject.getInt("product_id"));
                            product.setBarcode(stockObject.getString("product_barcode"));
                            product.setProductCode(stockObject.getString("product_code"));

                            //  Log.e("product Name", ""+stockObject.getString("product_name"));

                            product.setProductName(stockObject.getString("product_name"));
                            product.setP_name(stockObject.getString("p_name"));

                            String bonuspercentage = stockObject.getString("product_bonus_percentage");

                            product.setProductBonus(Float.parseFloat(bonuspercentage));
                            product.setProduct_hsncode(stockObject.getString("product_hsncode"));
                            // printLog("bonus", ""+bonuspercentage);

                            String arb = "";
                            if (!stockObject.isNull("arabic_name"))
                                arb = stockObject.getString("arabic_name");
                            if (TextUtils.isEmpty(arb) || arb.equals("null"))
                                arb = " ";
                            product.setArabicName(arb);

                            product.setProductType(stockObject.getString("product_type_name"));
                            product.setProduct_reporting_Unit(stockObject.getInt("reporting_unit"));
                            product.setProduct_reporting_Price((float) stockObject.getDouble("reporting_price"));
                            //  product.setSale_unitid(stockObject.getInt("sale_unit_id"));
                            try {
                                product.setSale_unitid(stockObject.getInt("sale_unit_id"));
                            }catch (Exception e){

                            }
                            ArrayList<Units> unit_array = new ArrayList<>();
                            ArrayList<MfgDate> mfg_array = new ArrayList<>();

                            JSONArray unitArray = stockObject.getJSONArray("Units_list");
                            for (int k=0; k<unitArray.length(); k++){

                                JSONObject unitObject = unitArray.getJSONObject(k);

                                Units unit = new Units();

                                unit.setUnitId(unitObject.getInt("id"));
                                unit.setUnitName(unitObject.getString("name"));
                                unit.setCon_factor(unitObject.getInt("con_factor"));
                                unit.setUnitPrice(unitObject.getString("unit_price"));
                                unit.setUnitWholesalePrice(unitObject.getString("wholesale_price"));
                                unit_array.add(unit);
                            }

                            String gsonvalue = new Gson().toJson(unit_array);

                            Log.e("gsonvalue",gsonvalue);

                            product.setUnitslist(gsonvalue);


                            ArrayList<Taxes> taxes_array = new ArrayList<>();

                            JSONArray taxarray = stockObject.getJSONArray("Tax_list");
                            for (int k=0; k<taxarray.length(); k++){

                                JSONObject taxObject = taxarray.getJSONObject(k);

                                Taxes taxes = new Taxes();

                                taxes.setTax(taxObject.getString("tax"));
                                taxes.setCgst(taxObject.getString("cgst"));
                                taxes.setSgst(taxObject.getString("sgst"));
                                taxes_array.add(taxes);
                            }

                            String gsontax = new Gson().toJson(taxes_array);
                            product.setTaxlist(gsontax);

                            product.setBrandName(stockObject.getString("brand_name"));

                            float wholeSale = (float) stockObject.getDouble("product_wholesale_price");
                            float retailPrice = (float) stockObject.getDouble("product_mrp");
                            //haris added on 21-11-2020
                            float product_rate = (float) stockObject.getDouble("product_rate");
                            //
                            float vat = (float) stockObject.getDouble("product_vat");

                            product.setWholeSalePrice(wholeSale);
                            product.setRetailPrice(retailPrice);
                            product.setTax(vat);

                            product.setProduct_rate(product_rate);

                            product.setCost((float) stockObject.getDouble("product_cost"));
                            product.setPiecepercart(stockObject.getInt("piece_per_cart"));

                            product.setStockQuantity(stockObject.getInt("stock_quantity"));
                            int_product_qty=stockObject.getInt("stock_quantity");

                            //haris added on 26-10-2020
                            Log.e("int_product_qty",""+int_product_qty);
                            Log.e("int_product_vat",""+vat);

                            ArrayList<Size> size_array = new ArrayList<>();
                            JSONArray sizeArray = stockObject.getJSONArray("Sizes_list");
                            for (int k=0; k<sizeArray.length(); k++){

                                JSONObject unitObject = sizeArray.getJSONObject(k);

                                Size size = new Size();

                                size.setSizeId(unitObject.getInt("size"));
                                size.setQuantity(unitObject.getString("quantity"));
                                size.setAvailable_stock(int_product_qty);
                                size_array.add(size);
                            }
                            String gsonvalue_size = new Gson().toJson(size_array);
                            product.setSizelist(gsonvalue_size);

                            /////////

                            products.add(product);
                        }

                        float toatalStock = response.getLong("total_stock");
                        int stockQuantity = response.getInt("total_quantity");

                    } else
                    {
                        Toast.makeText(Stock_Transfer_Online.this, response.getString("status"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.getMessage();

                    pd.dismiss();//
                    printLog(TAG, "storeVanStock Exception  " + e.getMessage());
                }

                if (!products.isEmpty()) {
                    boolean deleteStatus1 = myDatabase.deleteTableRequest(REQ_QUOTATION_TEMP);
                    boolean deleteStatus = myDatabase.deleteTableRequest(REQ_VAN_STOCK);
                    for (Product p : products) {

                        try {
                            //  Log.e("Product Insert Db", ""+p.getProductName());

                            myDatabase.insertStock(p);   //storeVanStock to local
                        }catch (Exception e){
                            Log.e("Db insert error", ""+e.getMessage());
                        }

                    }
                }

                pd.dismiss();
                Toast.makeText(Stock_Transfer_Online.this, "VanStock Transfer Completed..! " , Toast.LENGTH_SHORT).show();
                //   getAllRouteCustomer();



            }

            @Override
            public void onErrorResponse(String error) {

                Toast.makeText(Stock_Transfer_Online.this, "VanStock  " + error, Toast.LENGTH_SHORT).show();

                pd.dismiss();
            }
        }, object);
    }
}