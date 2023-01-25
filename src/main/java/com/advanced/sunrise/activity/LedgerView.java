package com.advanced.minhas.activity;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvLedgerAdapter;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Ledger;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.advanced.minhas.config.Generic.getAmount;

public class LedgerView extends AppCompatActivity {
    private SearchableSpinner spinnerShop;
    private RecyclerView recyclerView;
    private ErrorView errorView;
    private ProgressBar progressBar;
    EditText edt_toDate;  //edt_fromDate
    private LinearLayout ll_shoplist, ll_date;
    private Calendar calendar;
    private MyDatabase myDatabase;
    private WebService webServices;
    private SessionValue session; // SharedPreferences
    String type = "",  regId="", str_cust_id="0";
    private RvLedgerAdapter adapter;
    ArrayList<Ledger> array_ledger = new ArrayList<>();
    TextView tv_balance_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_view);
        spinnerShop=(SearchableSpinner)findViewById(R.id.spinner_cart_shops);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_ledger);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_ledger);
        errorView=(ErrorView)findViewById(R.id.error_view_ledger);
        ll_shoplist = (LinearLayout)findViewById(R.id.layout_customer);
        ll_date = (LinearLayout)findViewById(R.id.layout_date);
        myDatabase=new MyDatabase(LedgerView.this);
        tv_balance_total = findViewById(R.id.tv_balance_total);

        /*  edt_fromDate = (EditText)findViewById(R.id.edittext_fromdate);*/

        edt_toDate = (EditText)findViewById(R.id.edittext_todate);

        session=new SessionValue(this);
        webServices= new WebService();
        calendar = Calendar.getInstance();

//        regId = session.getUserDetails().get(SessionManager.KEY_ID);
//        type = session.getUserDetails().get(SessionManager.KEY_USER_TYPE);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            edt_toDate.setText(""+dateFormat.format(date));
        }catch (Exception e){

        }

        adapter = new RvLedgerAdapter(array_ledger, str_cust_id);
        //    Recycler View
        recyclerView.setHasFixedSize(true);
        //    Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
                .build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

//        if (type.equals(Config.KEY_EXCECUTIVE)) {
//
//            Log.e("Inside", "executive");
//
//            ll_shoplist.setVisibility(View.VISIBLE);
//
//            getAllShopList();
            final ArrayList<Shop> shops=new ArrayList<>();
            shops.clear();
           shops.addAll(myDatabase.getAllCustomers());


        setSpinnerShop(shops);

//        } else {
//
//            ll_shoplist.setVisibility(View.GONE);
//            Log.e("Inside", "else");
//            str_cust_id = ""+regId;
//
//        }

        //  getLedger();

        /*edt_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDateClick("From");

            }
        });*/

        edt_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDateClick("to");

            }
        });

        spinnerShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Shop s = (Shop) spinnerShop.getSelectedItem();

                str_cust_id = ""+s.getShopId();

                Log.e("cust id", ""+str_cust_id);

                if (!str_cust_id.equals("-1")) {
                    //if (!edt_fromDate.getText().toString().isEmpty()) {
                    if (!edt_toDate.getText().toString().isEmpty()) {
                        tv_balance_total.setText("Balance : ");
                        getLedger();


                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please select To date", Toast.LENGTH_SHORT).show();
                    }
                    /*}else {
                        Toast.makeText(getApplicationContext(), "Please select From date", Toast.LENGTH_SHORT).show();
                    }*/
                }else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



//    private void getAllShopList(){
//
//        setProgressBar(true);
//        spinnerShop.setVisibility(View.VISIBLE);
//
//        Log.e("get shops", "calling...");
//
//        final ArrayList<Shop> shops=new ArrayList<>();
//        shops.clear();
//
//        spinnerShop.setTitle("Select Shop");
//
//        Shop shop = new Shop();
//        shop.setShopId(-1);
//        shop.setShopName(" Select Customer ");
//        shops.add(0, shop);
//
//        JSONObject object=new JSONObject();
//
//        try {
//            object.put("executive_id", regId);
//
//        } catch (JSONException e) {
//
//            e.printStackTrace();
//        }
//
//        Log.e("Object get shops", ":"+object);
//
//        webServices.webListShop(new WebServices.webObjectCallback() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                try {
//                    Log.e("Response", ":"+response);
//
//                    if (response.getString("status").equalsIgnoreCase("success")) {
//
//                        JSONArray array = response.getJSONArray("Shopname");
//
//                        for (int i = 0; i < array.length(); i++) {
//
//                            JSONObject object = array.getJSONObject(i);
//
//                            Shop shop = new Shop();
//
//                            shop.setShopId(object.getInt("id"));
//                            shop.setShopName(object.getString("name"));
//                            shops.add(shop);
//
//                        }
//
//                    }else {
//
//                        setErrorView("No Shops","",false, "shop");
//                    }
//                    if (shops.size()!=0)
//                        setSpinnerShop(shops);
//
//                    else
//                        setErrorView("No Shops","",false, "shop");
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onErrorResponse(String error) {
//
//                setErrorView(error,"",true, "shop");
//
//            }
//        },object);
//    }

    private void getLedger(){

        setProgressBar(true);

        // Log.e("get ledger", "calling...");

        // final ArrayList<Ledger> ledger=new ArrayList<>();

        array_ledger.clear();

        JSONObject object=new JSONObject();

        try {
            object.put("from_date", "01-01-1900");
            object.put("to_date", edt_toDate.getText().toString().trim());
            object.put("customer_id", str_cust_id);

        } catch (JSONException e) {

            e.printStackTrace();
        }

        Log.e("Object get ledger", ":"+object);

        webServices.webGetLedger(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.e("Response ledger", ":"+response);

                    if (response.getString("result").equalsIgnoreCase("Success")) {
                        JSONArray stockArray = response.getJSONArray("Ledger");
                        for (int i = 0; i < stockArray.length(); i++) {

                            JSONObject object = stockArray.getJSONObject(i);
                            Ledger lg = new Ledger();
                                    lg.setDate(object.getString("date"));
                                    lg.setInvoiceAmount(object.getString("debit"));//debit
                                    lg.setReceived(object.getString("credit"));//credit
                                String st_remrks = object.getString("remarks");
                                try {
                                    if (!st_remrks.equals("")) {
                                        String[] parts = st_remrks.split(":");
                                        lg.setInvoiceNo(parts[1]);//remarks
                                    } else {
                                        lg.setInvoiceNo("");//remarks
                                    }
                                }catch (Exception e){

                                }
                                    array_ledger.add(lg);
                        }


//                    Iterator<String> iter = response.keys();
//                    while (iter.hasNext()) {
//                        String key = iter.next();
//
//                            try {
//                                JSONObject array = (JSONObject) response.get(key);
//
//                           // JSONObject array = (JSONObject) value;
//
//                            Iterator iterator = array.keys();
//                            while(iterator.hasNext()) {
//                                String key2 = (String) iterator.next();
//                                JSONObject object = array.getJSONObject(key2);
//                               // for (int i = 0; i < array.getJSONObject(key2).length(); i++) {
//                                    Ledger lg = new Ledger();
//                                    lg.setDate(object.getString("date"));
//                                    //lg.setInvoiceNo(object.getString("remarks"));//remarks
//                                    lg.setInvoiceAmount(object.getString("debit"));//debit
//                                    lg.setReceived(object.getString("credit"));//credit
//                                String st_remrks = object.getString("remarks");
//                                if(!st_remrks.equals("")) {
//                                    String[] parts = st_remrks.split(":");
//                                    lg.setInvoiceNo(parts[1]);//remarks
//                                }
//                                else{
//                                    lg.setInvoiceNo("");//remarks
//                                }
//                                    array_ledger.add(lg);
//                                }
//                            }catch (Exception e){
//
//                            }
//
//                    }
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (array_ledger.size()!=0) {
                        float fl_tot = 0 ,fltotal_ivoice = 0, fltotal_recvd = 0 ;
                        for( Ledger a :  array_ledger){
                            Log.e("Invoiceamnt",""+a.getInvoiceAmount());
                            Log.e("recevd",""+a.getReceived());
                            fltotal_ivoice = fltotal_ivoice + Float.parseFloat(""+a.getInvoiceAmount());
                            fltotal_recvd =fltotal_recvd + Float.parseFloat(""+a.getReceived());

                            Log.e("fl_tot",""+fl_tot);
                        }
                        fl_tot = fltotal_ivoice - fltotal_recvd;

                        tv_balance_total.setText("Balance : "+getAmount(fl_tot));
                        setProgressBar(false);
                        // recyclerView.setAdapter(adapter);

                        adapter = new RvLedgerAdapter(array_ledger, str_cust_id);
                        //    Recycler View
                        recyclerView.setHasFixedSize(true);
                        //    Item Divider in recyclerView
                        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(LedgerView.this)
                                .showLastDivider()
                                .build());
                        recyclerView.setLayoutManager(new LinearLayoutManager(LedgerView.this));
                        recyclerView.setAdapter(adapter);

                    }
                    else{
                        setErrorView("No Data","",false, "l");
                        setProgressBar(false);
                    }


            }

            @Override
            public void onErrorResponse(String error) {

                setErrorView(error,"",true, "l");
                setProgressBar(false);

            }
        },object);
    }


    private void setSpinnerShop(ArrayList<Shop> shops){

        recyclerView.setVisibility(View.VISIBLE);
        ll_date.setVisibility(View.VISIBLE);

        setProgressBar(false);

        // Initializing an ArrayAdapter
        final ArrayAdapter<Shop> productTypeAdapter = new ArrayAdapter<Shop>(this,R.layout.spinner_background,shops);

        productTypeAdapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerShop.setAdapter(productTypeAdapter);

    }

    //    progressbar visible or invisible
    private void setProgressBar(boolean isVisible){

        if (isVisible){
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
        }else
        {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
    }


    //    set error view
    private void setErrorView(final String title, final String subTitle,boolean isRetry, String type){

        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        if (type.equals("shop")) {
            ll_shoplist.setVisibility(View.GONE);
            ll_date.setVisibility(View.GONE);
        }else {
            ll_shoplist.setVisibility(View.VISIBLE);
            ll_date.setVisibility(View.VISIBLE);
        }

//        if (type.equals(Config.KEY_EXCECUTIVE)) {
//            ll_shoplist.setVisibility(View.VISIBLE);
//
//        }else {
//            ll_shoplist.setVisibility(View.GONE);
//
//        }


        setProgressBar(false);

        errorView.setConfig(ErrorView.Config.create()
                .title(title)
                .subtitle(subTitle)
                .retryVisible(isRetry)
                .retryText(getString(R.string.error_view_retry))
                .build());

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {

              //  getAllShopList();


            }
        });
    }

    private void onDateClick(String tag){

        // Get Current Date
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (tag.equals("From")) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(LedgerView.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                            //   edt_fromDate.setText(dateFormat.format(calendar.getTime()));
                            // edt_fromDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            if (!str_cust_id.equals("-1")) {
                                /*if (!edt_toDate.getText().toString().isEmpty()) {

                                    getLedger();

                                }else {
                                    Toast.makeText(getApplicationContext(), "Select to date", Toast.LENGTH_SHORT).show();
                                }*/
                            }else {
                                Toast.makeText(getApplicationContext(), "Select Customer", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        else {

            final DatePickerDialog datePickerDialog = new DatePickerDialog(LedgerView.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                            edt_toDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            if (!str_cust_id.equals("-1")) {
                                //   if (!edt_fromDate.getText().toString().isEmpty()) {

                                getLedger();

                               /* }else {
                                    Toast.makeText(getApplicationContext(), "Select from date", Toast.LENGTH_SHORT).show();
                                }*/
                            }else {
                                Toast.makeText(getApplicationContext(), "Select Customer", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, mYear, mMonth, mDay);

            //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

}
