package com.advanced.minhas.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvStockTransferDetailsAdapter;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.StocktransferDetails;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;
import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_STOCK_APROVAL;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.localdb.MyDatabase.getDateTime;
import static com.advanced.minhas.webservice.WebService.webGetOrdertransferDetails;
import static com.advanced.minhas.webservice.WebService.webGetStocktransferApprove;
import static com.advanced.minhas.webservice.WebService.webGetStocktransferDetails;
import static com.advanced.minhas.webservice.WebService.webPaidReceipt;
import static com.advanced.minhas.webservice.WebService.webPlaceOrder;
import static com.advanced.minhas.webservice.WebService.webReturn;

public class StockApproval extends AppCompatActivity {
    RecyclerView recyclerView;
    String str_id= "", str_date="", str_from="", str_to="", str_desc  = "" ,str_orderid ="";
    TextView tv_id, tv_date, tv_from, tv_to, tv_desc;
    ArrayList<StocktransferDetails> arrayList = new ArrayList<>();
    ArrayList<Sales> saleList =new ArrayList<>();

    ArrayList<Sales> quotationList = new ArrayList<>();
    ArrayList<Sales> returnList = new ArrayList<>();
    ArrayList<Receipt> receipts  =new ArrayList<>();
    RvStockTransferDetailsAdapter adapter;
    private MyDatabase myDatabase;
    Button bttn_approve, bttn_reject;
    private String EXECUTIVE_ID = "" ,dayRegId ="" ,TAG = "StockApproval";
    String statusFlag = "";
    ImageButton ibBack;
    SessionValue sessionValue;
    private SessionAuth sessionAuth;
    MyDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_approval);
        db = new MyDatabase(StockApproval.this);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        tv_id = findViewById(R.id.textView_transfer_id);
        tv_date = findViewById(R.id.textView_date);
        tv_from = findViewById(R.id.textView_from);
        tv_to = findViewById(R.id.textView_to);
        tv_desc = findViewById(R.id.textView_description);
        bttn_approve= findViewById(R.id.button_approve);
        bttn_reject= findViewById(R.id.button_reject);

        recyclerView = findViewById(R.id.recyclerView_stock_transfer_approval);
        sessionValue = new SessionValue(this);
        sessionAuth =new SessionAuth(this);
        myDatabase = new MyDatabase(this);

        try {
            EXECUTIVE_ID = sessionAuth.getExecutiveId();
        } catch (Exception e) {
            e.getMessage();
        }
        adapter = new RvStockTransferDetailsAdapter(arrayList);

        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
                .build());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        try {

            str_id = getIntent().getStringExtra("id");
            str_date = getIntent().getStringExtra("date");
            str_from = getIntent().getStringExtra("from");
            str_to = getIntent().getStringExtra("to");
            str_desc = getIntent().getStringExtra("desc");
            str_orderid = getIntent().getStringExtra("order_id");

            tv_id.setText(str_id);
            tv_date.setText(str_date);
            tv_from.setText(str_from);
            tv_to.setText(str_to);
            tv_desc.setText(str_desc);


            if(!str_orderid.equals("")) {
                GetOrderTransfer();
            }
            else{
                GetStockTransfer();
            }

        }catch (Exception e){

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
                    statusFlag = "approve";
                    ShowConfirmDialog();
                }


            }
        });

        bttn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statusFlag = "reject";
                ShowConfirmDialog();
            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    private void syncConfirmationDialogue() {

        new AlertDialog.Builder(StockApproval.this)
                .setMessage("Better network is avalable now ?  Confirm Sync ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (isNetworkConnected()) {
                            new AsyncTaskLoadDataSync().execute();

                        }
                        else{
                            Toast.makeText(StockApproval.this, "No Network ", Toast.LENGTH_SHORT).show();
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
        ConnectivityManager cm = (ConnectivityManager) StockApproval.this.getSystemService(Context.CONNECTIVITY_SERVICE);

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

            Toast.makeText(StockApproval.this,"Updload Completed..!",Toast.LENGTH_LONG).show();
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
        if (!receipts.isEmpty())
            paidReceipt();

        else if (!saleList.isEmpty())
            placeOrder();
            //    else if (!quotationList.isEmpty())
            //placeQuotation();
        else if (!returnList.isEmpty())
            placeReturn();

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
                        Toast.makeText(StockApproval.this, "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                //pd.dismiss();
                Toast.makeText(StockApproval.this, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    private double roundTwoDecimals(double taxValue) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(taxValue));
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
                        Toast.makeText(StockApproval.this, "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //    pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                //   pd.dismiss();
                Toast.makeText(StockApproval.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StockApproval.this,"Updload Completed..!",Toast.LENGTH_LONG).show();
                        for (Receipt s : receipts) {
                            myDatabase.UpdatePaidReceiptUploadStatus(s.getReceiptNo());
                        }
                        if (!returnList.isEmpty()) {
                            placeReturn();
                        }

                    } else
                        Toast.makeText(StockApproval.this, "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                //pd.dismiss();
                Toast.makeText(StockApproval.this, error, Toast.LENGTH_SHORT).show();

            }
        }, object);


    }

    private void GetOrderTransfer() {

        arrayList.clear();

        JSONObject object = new JSONObject();
        try {
            object.put("orderid", str_orderid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("stock transfer object", ""+object.toString());

        final ProgressDialog pd = ProgressDialog.show(StockApproval.this, null, "Please wait...", false, false);

        webGetOrdertransferDetails(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", ""+response.toString());

                try {

                    if (response.getString("status").equalsIgnoreCase("Success")){

                        JSONArray array = response.getJSONArray("transfer_item_list");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject shopObj = array.getJSONObject(i);

                            StocktransferDetails stock = new StocktransferDetails();

                            stock.setItemid(shopObj.getString("item_id"));
                            stock.setProductid(shopObj.getInt("product_id"));
                            stock.setTransferid(shopObj.getString("transfer_id"));
                            stock.setQuantity(shopObj.getString("quantity"));
                            stock.setApproval_qty(shopObj.getInt("quantity"));
                            stock.setProductname(shopObj.getString("Product_name"));
                            stock.setProductcode(shopObj.getString("Product_code"));
                            stock.setProductunitid(shopObj.getString("unit_id"));
                            stock.setProduct_reportingunit(shopObj.getString("reporting_unit"));

                            arrayList.add(stock);

                        }
                    }


                    if (arrayList.size()>0){

                        Log.e("inside", ""+arrayList.size());
                        adapter.notifyDataSetChanged();
                        boolean deleteStatus = db.deleteTableRequest(REQ_STOCK_APROVAL);
                        insert_approval_stock();
                    } else {

                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }



                } catch (JSONException e) {
                    Toast.makeText(StockApproval.this, "Error getting details..!", Toast.LENGTH_SHORT).show();

                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();

                Toast.makeText(StockApproval.this, error, Toast.LENGTH_SHORT).show();
            }
        }, object);

    }


    private void GetStockTransfer() {

        arrayList.clear();

        JSONObject object = new JSONObject();
        try {
            object.put("transfer_id", str_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("stock transfer object", ""+object.toString());

        final ProgressDialog pd = ProgressDialog.show(StockApproval.this, null, "Please wait...", false, false);

        webGetStocktransferDetails(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", ""+response.toString());

                try {

                    if (response.getString("status").equalsIgnoreCase("Success")){

                        JSONArray array = response.getJSONArray("transfer_item_list");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject shopObj = array.getJSONObject(i);

                            StocktransferDetails stock = new StocktransferDetails();

                            stock.setItemid(shopObj.getString("item_id"));
                            stock.setProductid(shopObj.getInt("product_id"));
                            stock.setTransferid(shopObj.getString("transfer_id"));
                            stock.setQuantity(shopObj.getString("quantity"));
                            stock.setApproval_qty(shopObj.getInt("quantity"));
                            stock.setProductname(shopObj.getString("Product_name"));
                            stock.setProductcode(shopObj.getString("Product_code"));
                            stock.setProductunitid(shopObj.getString("unit_id"));
                            stock.setProduct_reportingunit(shopObj.getString("reporting_unit"));

                            arrayList.add(stock);

                        }
                    }

                   
                    if (arrayList.size()>0){

                        Log.e("inside", ""+arrayList.size());
                        adapter.notifyDataSetChanged();
                        boolean deleteStatus = db.deleteTableRequest(REQ_STOCK_APROVAL);
                        insert_approval_stock();
                    } else {

                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }



                } catch (JSONException e) {
                    Toast.makeText(StockApproval.this, "Error getting details..!", Toast.LENGTH_SHORT).show();

                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();

                Toast.makeText(StockApproval.this, error, Toast.LENGTH_SHORT).show();
            }
        }, object);

    }

    private void insert_approval_stock() {
        for (StocktransferDetails st : arrayList) {

            try {
                Log.e("prodname hr",st.getProductname());
                Log.e("prodqtyy hr",st.getQuantity());
                db.insertStock_byapproval(st); //saved into stockapproval table

            } catch (Exception e) {

            }
        }


    }


    private void ApproveStockTransfer() {

        JSONObject object = new JSONObject();
        try {
            object.put("transfer_id", str_id);
            object.put("status", statusFlag);

            JSONArray array = new JSONArray();

            for (StocktransferDetails st : arrayList){

                JSONObject objt = new JSONObject();
                objt.put("id", st.getItemid());
                objt.put("quantity", st.getQuantity());

                array.put(objt);
            }

            object.put("item_list", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("stock approve object", ""+object.toString());

        final ProgressDialog pd = ProgressDialog.show(StockApproval.this, null, "Please wait...", false, false);

        webGetStocktransferApprove(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", ""+response.toString());

                try {

                    if (response.getString("status").equalsIgnoreCase("Success")){

                        Toast.makeText(StockApproval.this ,"Stock Transfer Approved", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        sessionValue.storeApproveFlag("Approve");
                        if(statusFlag.equals("approve")) {
                            Toast.makeText(StockApproval.this ,"Stock Transfer Approved", Toast.LENGTH_SHORT).show();
                            update_stock();
                        }
                        else{
                            Toast.makeText(StockApproval.this ,"Stock Transfer Rejected", Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(StockApproval.this, HomeActivity.class);
                        //  intent.putExtra("Approval", "Approve");
                        startActivity(intent);
                        finish();
                    }else {
                        pd.dismiss();
                        Toast.makeText(StockApproval.this ,"Error Approving Stock transfer", Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    Toast.makeText(StockApproval.this, "Error Approving Stock transfer.!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();

                Toast.makeText(StockApproval.this, error, Toast.LENGTH_SHORT).show();
            }
        }, object);

    }

    private void update_stock() {
        try {
            for (StocktransferDetails st : arrayList) {
                db.updateStock_byapproval(st); //update stock table
            }
        }catch (Exception e){

        }
    }

    public void  ShowConfirmDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm ?");
        if (statusFlag.equals("approve")){

            builder.setMessage("Are you sure to approve transfer");
        }else {
            builder.setMessage("Are you sure to reject transfer");
        }


        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                update_stock();
                //ApproveStockTransfer();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}