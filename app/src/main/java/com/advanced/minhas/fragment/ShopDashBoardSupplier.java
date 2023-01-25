package com.advanced.minhas.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.ReceiptActivity;
import com.advanced.minhas.activity.ReportActivity;
import com.advanced.minhas.activity.ReturnHistoryActivity;
import com.advanced.minhas.activity.SalesActivity;
import com.advanced.minhas.activity.SalesReturnActivity;
import com.advanced.minhas.animation.MyBounceInterpolator;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.CustomerProduct;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.chequeReceipt;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;
import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_ANY_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_NO_SALE_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_QUOTATION_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_RETURN_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_SALE_TYPE;
import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.localdb.MyDatabase.getDateTime;
import static com.advanced.minhas.session.SessionValue.PREF_LATITUDE;
import static com.advanced.minhas.session.SessionValue.PREF_LONGITUDE;
import static com.advanced.minhas.webservice.WebService.webPaidReceipt;
import static com.advanced.minhas.webservice.WebService.webPlaceOrder;
import static com.advanced.minhas.webservice.WebService.webReturn;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopDashBoardSupplier#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopDashBoardSupplier extends Fragment implements View.OnClickListener,View.OnLongClickListener {

    private String TAG = "ShopDashBoardFragment", str_Latitude = "0", str_Longitude = "0", latitude_session = "", longitude_session ="";;

    private static final String ARG_SHOP = "shop_key_arg";

    private Shop SELECTED_SHOP=null;
    private String provider;
    private String EXECUTIVE_ID = "";
    private CardView cvSales, cvReceipt,cvSalesReturn,cvNoSale,cvMapView,cvViewMore,cvQuotation,cvFinish;

    //  private ImageView ivSales, ivReceipt,ivSalesReturn,ivNoSale,ivMapView,ivViewMore,ivQuotation,ivFinish;
    ArrayList<Sales> saleList =new ArrayList<>();

    ArrayList<Sales> quotationList = new ArrayList<>();
    ArrayList<Sales> returnList = new ArrayList<>();
    ArrayList<Receipt> receipts  =new ArrayList<>();
    ArrayList<chequeReceipt> chequereceipts  =new ArrayList<>();
    private Animation myAnim;
    private MyBounceInterpolator interpolator;
    private ImageButton ibBack;
    private MyDatabase myDatabase;
    LocationManager lm ;
    boolean gps_enabled = false;
    private int ANIM_TIME_OUT = 200;
    private SessionValue sessionValue ;
    private SessionAuth sessionAuth;
    LocationManager locationManager;
    private String dayRegId;
    private ImageButton ibSync;
    TextView txt_sync;
    public ShopDashBoardSupplier() {
        // Required empty public constructor
    }

    public static ShopDashBoardSupplier newInstance(Shop shop) {
        ShopDashBoardSupplier fragment = new ShopDashBoardSupplier();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SHOP, shop);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            SELECTED_SHOP =(Shop) getArguments().getSerializable(ARG_SHOP);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_shop_dash_board_supplier, container, false);

//      initialise container
        sessionValue = new SessionValue(getActivity());
        cvSales=(CardView)view.findViewById(R.id.cardView_sales) ;

        sessionAuth = new SessionAuth(getActivity());
        this.EXECUTIVE_ID =sessionAuth.getExecutiveId();
        //cvQuotation=(CardView)view.findViewById(R.id.cardView_quotation) ;

        cvReceipt =(CardView)view.findViewById(R.id.cardView_receipt) ;
        cvSalesReturn=(CardView)view.findViewById(R.id.cardView_return) ;
        cvNoSale=(CardView)view.findViewById(R.id.cardView_noSale) ;
        cvMapView= view.findViewById(R.id.cardView_mapView);
        cvViewMore= view.findViewById(R.id.cardView_viewMore);

        //cvQuotation=(CardView)view.findViewById(R.id.cardView_quotation) ;
        cvFinish=(CardView)view.findViewById(R.id.cardView_finish) ;

        ibBack=(ImageButton) view.findViewById(R.id.imageButton_toolbar_back);

        TextView  tvToolBarShopName = (TextView)view.findViewById(R.id.textView_toolbar_shopNameAndCode);
        ibSync = view.findViewById(R.id.textView_toolbar_shopSync);
        txt_sync = view.findViewById(R.id.txt_sync);
        this.myDatabase=new MyDatabase(getContext());

        if (SELECTED_SHOP==null) {
            getActivity().onBackPressed();
            return view;
        }

        tvToolBarShopName.setText(String.valueOf(SELECTED_SHOP.getShopName()+"\t"+SELECTED_SHOP.getShopCode()));
        tvToolBarShopName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvToolBarShopName.setSelected(true);

        myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

//      set ClickAction
        cvSales.setOnClickListener(this);
        cvReceipt.setOnClickListener(this);
        cvSalesReturn.setOnClickListener(this);
        cvNoSale.setOnClickListener(this);
       // cvQuotation.setOnClickListener(this);
        cvMapView.setOnClickListener(this);
        cvViewMore.setOnClickListener(this);
        cvFinish.setOnClickListener(this);
        ibBack.setOnClickListener(this);
//        cvEditcustomer.setOnClickListener(this);
//      set long ClickAction
        cvSales.setOnLongClickListener(this);
        cvSalesReturn.setOnLongClickListener(this);
        //cvQuotation.setOnLongClickListener(this);
        latitude_session = sessionValue.get_map_details().get(PREF_LATITUDE);  //latitude get from session
        longitude_session = sessionValue.get_map_details().get(PREF_LONGITUDE);  //longitude get from session
        Log.e("latitude_session",latitude_session);
        Log.e("longitude_session",longitude_session);
       // ibSync.setVisibility(View.VISIBLE);
       // txt_sync.setVisibility(View.VISIBLE);
        ibSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fetch_customer_details();
                syncConfirmationDialogue();
            }
        });
        check_permission();

        return view;
    }

    private void syncConfirmationDialogue() {

        new android.app.AlertDialog.Builder(getActivity())
                .setMessage("Better network is avalable now ?  Confirm Sync ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (isNetworkConnected()) {
                            new ShopDashBoardSupplier.AsyncTaskLoadData().execute();
                            //new AsyncTaskExample().execute();
                            // Sync_pending();
                        }
                        else{
                            Toast.makeText(getActivity(), "No Network ", Toast.LENGTH_SHORT).show();
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
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private class AsyncTaskLoadData extends AsyncTask<JSONArray, String, String> {
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

            Toast.makeText(getActivity(),"Updload Completed..!",Toast.LENGTH_LONG).show();
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
        this.chequereceipts = myDatabase.getAllChequeReceipts();


        if (!saleList.isEmpty())
            placeOrder();
            //    else if (!quotationList.isEmpty())
            //placeQuotation();
        else if (!returnList.isEmpty())
            placeReturn();
        else if (!receipts.isEmpty())
            paidReceipt();
//        else if (!chequereceipts.isEmpty())
//            chequeReceipts();
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
                        Toast.makeText(getActivity(),"Updload Completed..!",Toast.LENGTH_LONG).show();
                        for (Receipt s : receipts) {
                            myDatabase.UpdatePaidReceiptUploadStatus(s.getReceiptNo());
                        }

                    } else
                        Toast.makeText(getActivity(), "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                //pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

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
                        if (!receipts.isEmpty())
                            paidReceipt();


                    } else
                        Toast.makeText(getActivity(), "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                //pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

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
                    obj.put("product_total", ""+getAmount(c.getProductTotal())); // c.getTotalPrice()
                    //obj.put("product_bonus_percentage", c.getProductBonus());
                    obj.put("product_unit", c.getOrderType());
                    obj.put("tax", getAmount(c.getTax()));
                    obj.put("tax_amont", getAmount(c.getTaxValue()));
                    obj.put("product_discount", ""+getAmount(c.getProductDiscount()));
                    obj.put("tax_type",tx_type);
                    obj.put("mfg_date",c.getMfg_date());
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
                        Toast.makeText(getActivity(), "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //    pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                //   pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        }, object);
    }

    private void fetch_customer_details() {


        final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        // Log.e(TAG, "Add customer object " + customerObj);
        try {

            //object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("Customerid", SELECTED_SHOP.getShopId());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Edt customer object " + object);
        WebService.webGetCustomerDetailsbyCustId(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                // price_list.clear();
                Log.e(TAG, "Get customer response " + response);

                try {
                    if (response.getString("status").equals("Success")) {
                        Log.e(TAG, "Edt customer response1 " + response);
                        /*********************/
                        JSONObject shopObj = response.getJSONObject("customer");

                        Shop shop = new Shop();

                        int custId = shopObj.getInt("id");

                        shop.setShopId(custId);
                        // shop.setShopCode(shopObj.getString("shope_code"));
                        shop.setShopName(shopObj.getString("name"));

                        shop.setShopMail(shopObj.getString("email"));
                        shop.setShopMobile(shopObj.getString("mobile"));
                        shop.setShopAddress(shopObj.getString("address"));

                        shop.setRouteCode(shopObj.getString("route_code"));
                        //shop.setContactPerson(etContactPerson.getText().toString().trim());


                        shop.setCreditLimit((float) shopObj.getDouble("credit_limit"));
                        //shop.setShopMobile(etPhone.getText().toString().trim());
                        shop.setVatNumber(shopObj.getString("vat_no"));
                        // shop.setCreditperiod(editText_creditperiod.getText().toString());

                        //    shop.setCustomer_type(""+t.getTypeName());
                        shop.setOpeningbalance((float) shopObj.getDouble("opening_balance"));

                        shop.setOutStandingBalance(0);
                        //haris added on 26-11-2020
                        // shop.setShop_category(shopObj.getString("Division"));

                        shop.setVisit(false);

                        ArrayList<CustomerProduct> selling_list = new ArrayList<>();

                        CustomerProduct sellingPrice = new CustomerProduct();
                        selling_list.add(sellingPrice);


                        double outstandingbal =myDatabase.get_outstandingbal_byshopid(shop.getShopId());
                        boolean b = new MyDatabase(getActivity()).update_customer(shop,outstandingbal);


                        Toast.makeText(getActivity(), "Customer Details Fetch Completed..!", Toast.LENGTH_SHORT).show();
                        // finish();
                    } else
                        Log.e(TAG, "Refresh customer response1 " + response);
                    Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Log.e("error","123");
                }

                pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);

    }

    private void updateView(){

        SELECTED_SHOP=myDatabase.getIdWiseCustomer(SELECTED_SHOP.getShopId());

        boolean _sale= myDatabase.getVisitingStatus(REQ_SALE_TYPE,SELECTED_SHOP.getShopId());

        boolean _quotation=  myDatabase.getVisitingStatus(REQ_QUOTATION_TYPE,SELECTED_SHOP.getShopId());

        boolean _return=  myDatabase.getVisitingStatus(REQ_RETURN_TYPE,SELECTED_SHOP.getShopId());

        boolean _receipt=  myDatabase.getVisitingStatus(REQ_RECEIPT_TYPE,SELECTED_SHOP.getShopId());

        boolean _no_sale=  myDatabase.getVisitingStatus(REQ_NO_SALE_TYPE,SELECTED_SHOP.getShopId());

        boolean _finish=myDatabase.getVisitingStatus(REQ_ANY_TYPE,SELECTED_SHOP.getShopId());

        cvSales.setCardBackgroundColor(_sale ?getActivity().getResources().getColor(R.color.colorGrayLight) : getActivity().getResources().getColor(R.color.colorWhite));

//        cvQuotation.setCardBackgroundColor(_quotation ?getActivity().getResources().getColor(R.color.colorGrayLight) : getActivity().getResources().getColor(R.color.colorWhite));
        cvSalesReturn.setCardBackgroundColor(_return ?getActivity().getResources().getColor(R.color.colorGrayLight) : getActivity().getResources().getColor(R.color.colorWhite));
        cvReceipt.setCardBackgroundColor(_receipt ?getActivity().getResources().getColor(R.color.colorGrayLight) : getActivity().getResources().getColor(R.color.colorWhite));

        cvNoSale.setCardBackgroundColor(_no_sale ?getActivity().getResources().getColor(R.color.colorGrayLight) : getActivity().getResources().getColor(R.color.colorWhite));

        cvFinish.setEnabled(_finish);
        cvFinish.setCardBackgroundColor(!_finish ?getActivity().getResources().getColor(R.color.colorGrayLight) : getActivity().getResources().getColor(R.color.colorWhite));

    }

    @Override
    public void onClick(View v) {

        final boolean _finish=myDatabase.getVisitingStatus(REQ_ANY_TYPE,SELECTED_SHOP.getShopId());

        clearAnimations(); //animation stop another click time
        switch (v.getId()){


            case R.id.cardView_sales:
                // ivSales.startAnimation(myAnim);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            return;
                        } else {

                            try {
                                lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                // network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                                Log.e("Gps Enable", "result : " + gps_enabled);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Gps Error", "" + e.getMessage());
                            }

                            //  && !network_enabled
                            if (!gps_enabled) {
                                Log.e("Gps Enable 2", "result : " + gps_enabled);
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("GPS Enable")
                                        .setPositiveButton("Settings", new
                                                DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                                    }
                                                })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            } else {
                                Log.e("latitude_session", latitude_session);
                                Log.e("longitude_session", longitude_session);
                                if (!str_Latitude.equals("0") && !str_Longitude.equals("0")) {
                                    sessionValue.save_latitude_and_longitude(str_Latitude, str_Longitude);
                                }

                                Intent intent = new Intent(getActivity(), SalesActivity.class);
                                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALES);
                                intent.putExtra(SHOP_KEY, SELECTED_SHOP);
                                startActivity(intent);
                            }
                        }
                    }
                }, ANIM_TIME_OUT);

                break;

//            case R.id.cardView_quotation:
//                check_permission();
//                if (!str_Latitude.equals("0") && !str_Longitude.equals("0")) {
//                    sessionValue.save_latitude_and_longitude(str_Latitude, str_Longitude);
//                }
//
//                //  ivQuotation.startAnimation(myAnim);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        Intent intent =new Intent(getActivity(),SalesActivity.class);
//                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_QUOTATION);
//                        intent.putExtra(SHOP_KEY,SELECTED_SHOP);
//                        startActivity(intent);
//                    }
//                }, ANIM_TIME_OUT);
//
//                break;

            case R.id.cardView_return:
                check_permission();
                if (!str_Latitude.equals("0") && !str_Longitude.equals("0")) {
                    sessionValue.save_latitude_and_longitude(str_Latitude, str_Longitude);
                }
                //  ivSalesReturn.startAnimation(myAnim);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent =new Intent(getActivity(), SalesReturnActivity.class);
                        intent.putExtra(SHOP_KEY,SELECTED_SHOP);
                        startActivity(intent);
                    }
                }, ANIM_TIME_OUT);

                break;
            case R.id.cardView_receipt:
                check_permission();
                if (!str_Latitude.equals("0") && !str_Longitude.equals("0")) {
                    sessionValue.save_latitude_and_longitude(str_Latitude, str_Longitude);
                }

                // ivReceipt.startAnimation(myAnim);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        Intent intent =new Intent(getActivity(),ReceiptActivity.class);
//                        intent.putExtra(SHOP_KEY,SELECTED_SHOP);
//                        startActivity(intent);
//                    }
//                }, ANIM_TIME_OUT);


//                final ReceiptTypeDialogue rcptDialog = new ReceiptTypeDialogue(getActivity(), new ReceiptTypeDialogue.ReceiptTypelistner() {
//                    @Override
//                    public void onReceiptTypeclick(String type) {
//                        //Log.e("type",type);
//                        if (type.equals("Receipt")) {
//                            Log.e("type", type);
//                            Intent intent = new Intent(getActivity(), ReceiptActivity.class);
//                            intent.putExtra(SHOP_KEY, SELECTED_SHOP);
//                            startActivity(intent);
//                        } else {
//                            Log.e("type else", type);
//                            Intent intent = new Intent(getActivity(), BillwiseReceipt.class);
//                            intent.putExtra(SHOP_KEY, SELECTED_SHOP);
//                            intent.putExtra("return_total", 0);
//                            startActivity(intent);
//                        }
//
//
//                        //  paid_amount = adapter.getGrandTotal();
//                        // paid_amount = db_grand_total;
//
//                        //paid_amount = 0;
//                    }
//
//
//                });
//
//                rcptDialog.show();
                Intent intent = new Intent(getActivity(), ReceiptActivity.class);
                intent.putExtra(SHOP_KEY, SELECTED_SHOP);
                startActivity(intent);

                break;
            case R.id.cardView_mapView:

                // ivMapView.startAnimation(myAnim);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        Intent intent =new Intent(getActivity(),MapViewActivity.class);
//                        intent.putExtra(SHOP_KEY,SELECTED_SHOP);
//                        startActivity(intent);
//                    }
//                }, ANIM_TIME_OUT);
//
//                break;

            case R.id.cardView_viewMore:

                // ivViewMore.startAnimation(myAnim);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

//                        Intent intent =new Intent(getActivity(),ReceiptActivity.class);
//                        intent.putExtra(SHOP_KEY,SELECTED_SHOP);
//                        startActivity(intent);
                    }
                }, ANIM_TIME_OUT);

                break;

            case R.id.cardView_noSale:

                //  ivNoSale.startAnimation(myAnim);
                get_latitudeandlongitude();

                check_permission();
                if (!str_Latitude.equals("0") && !str_Longitude.equals("0")) {
                    sessionValue.save_latitude_and_longitude(str_Latitude, str_Longitude);
                }
                latitude_session = sessionValue.get_map_details().get(PREF_LATITUDE);  //latitude get from session
                longitude_session = sessionValue.get_map_details().get(PREF_LONGITUDE);  //longitude get from session

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (!_finish)
                            noSaleDialog();
                        else
                            Toast.makeText(getContext(), "No sale Can't work", Toast.LENGTH_SHORT).show();

                    }
                }, ANIM_TIME_OUT);

                break;

            case R.id.cardView_finish:

                // ivFinish.startAnimation(myAnim);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (_finish)
                            finishConfirmationDialogue();

                    }
                }, ANIM_TIME_OUT);

                break;

            case R.id.imageButton_toolbar_back:

                getActivity().onBackPressed();

                break;

//            case R.id.cardView_editcustomer:
//
//                // ivViewMore.startAnimation(myAnim);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        Intent intent = new Intent(getActivity(), EditCustomer.class);
//                        intent.putExtra(SHOP_KEY, SELECTED_SHOP);
//                        startActivity(intent);
//                    }
//                }, ANIM_TIME_OUT);
//
//                break;
        }
    }

    private void get_latitudeandlongitude() {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Requires Permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                LocationListener mlocListener = new ShopDashBoardSupplier.MyLocationListener();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 61000, 250,
                        mlocListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
            }
        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error with location manager", Toast.LENGTH_SHORT).show();
        }

    }

    //    Visit close
    private void finishConfirmationDialogue() {

        new android.app.AlertDialog.Builder(getActivity())
                .setMessage("Confirm finish ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean updateStatus= myDatabase.updateVisitFinish(SELECTED_SHOP.getShopId());  // update no sales reason status to local db

                        if (updateStatus)
                            getActivity().onBackPressed();
                        else
                            Toast.makeText(getActivity(), "Finish failed", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to register
                    }
                })
                .show();
    }

    private void noSaleDialog(){
        latitude_session = sessionValue.get_map_details().get(PREF_LATITUDE);  //latitude get from session
        longitude_session = sessionValue.get_map_details().get(PREF_LONGITUDE);  //longitude get from session

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("No Need");
        arrayAdapter.add("Not Open");
        arrayAdapter.add("Stock Available");
        arrayAdapter.add("Others...");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strReason = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                builderInner.setMessage(strReason);
                builderInner.setTitle("Your Selected Reason is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        if ( myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_NO_SALE_TYPE,strReason,latitude_session,longitude_session))  // update no sales reason status to local db
                            updateView();

                        dialog.dismiss();
                    }
                });
                builderInner.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        clearAnimations();

        updateView();
/*

        try {

            final HomeDashBoardLeftFragment dashBoardLeftFragment = new HomeDashBoardLeftFragment();

// Let's first dynamically add a fragment into a frame container
            getActivity().getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_dash_board_left, dashBoardLeftFragment, FRAGMENT_DASHBOARD_LEFT)
                    .commit();


        }catch (Exception e){
            e.getLocalizedMessage();
        }
*/
    }

    private void clearAnimations(){

        /*ivSales.clearAnimation();
        ivReceipt.clearAnimation();
        ivSalesReturn.clearAnimation();
        ivNoSale.clearAnimation();
        ivMapView.clearAnimation();
        ivViewMore.clearAnimation();
        ivQuotation.clearAnimation();
        ivFinish.clearAnimation();*/

    }

    @Override
    public boolean onLongClick(View v) {


        clearAnimations(); //animation stop another click time
        switch (v.getId()){

            case R.id.cardView_sales: {
                // ivSales.startAnimation(myAnim);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getContext(), ReportActivity.class);
                        intent.putExtra(SHOP_KEY, SELECTED_SHOP);
                        startActivity(intent);
                    }
                }, ANIM_TIME_OUT);

                return true;

            }

            case R.id.cardView_return: {
                //  ivSales.startAnimation(myAnim);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getContext(), ReturnHistoryActivity.class);
                        intent.putExtra(SHOP_KEY, SELECTED_SHOP);
                        startActivity(intent);
                    }
                }, ANIM_TIME_OUT);

                return true;

            }
//            case R.id.cardView_quotation: {
//                //  ivSales.startAnimation(myAnim);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(getContext(), QuotationHistoryActivity.class);
//                        intent.putExtra(SHOP_KEY, SELECTED_SHOP);
//                        startActivity(intent);
//                    }
//                }, ANIM_TIME_OUT);
//
//                return true;
//
//            }

        }
        return false;
    }
    private void check_permission() {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Requires Permission if", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                LocationListener mlocListener = new ShopDashBoardSupplier.MyLocationListener();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 61000, 250,
                        mlocListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
            }
        }catch (Exception e){

        }
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String Text = "My current location is: " + "Latitude = "
                    + loc.getLatitude() + "\nLongitude = " + loc.getLongitude();

            str_Latitude = "" + loc.getLatitude();
            str_Longitude = "" + loc.getLongitude();

            //  tvNetTotal.setText("Lat : "+str_Latitude+" / Long : "+str_Longitude);
            /*Toast.makeText(getActivity(), Text, Toast.LENGTH_SHORT)
                    .show();*/
            Log.d("TAG", "Starting..");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getActivity(), "Gps Disabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getActivity(), "Gps Enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

}
