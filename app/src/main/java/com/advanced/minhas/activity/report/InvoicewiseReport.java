package com.advanced.minhas.activity.report;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.InvoicewiseReportAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.InvoicewiseReportMdl;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.advanced.minhas.webservice.WebService.webInvoicewiseReport;

public class InvoicewiseReport extends AppCompatActivity {
    private MyDatabase myDatabase;
    String TAG = "InvoicewiseReport";
    private InvoicewiseReportAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<InvoicewiseReportMdl> list = new ArrayList<>();
    private ArrayList<String> array_customername = new ArrayList<String>();
    private ArrayList<String> array_customerId = new ArrayList<String>();
    final ArrayList<Shop> shops = new ArrayList<>();
    private ImageButton ibBack;
    String str_fromdate="",str_todate="" ,str_cust_id ="";
    private LinearLayout lyt_share;
    private TextView textView_fromDate, textView_toDate;
    private Button bttn_fetch;
    private Calendar calendar;
    private ErrorView errorView;
    private ProgressBar progressBar;
    private String EXECUTIVE_ID = "" ,routeid= "";
    private SessionValue sessionValue;
    private AppCompatSpinner spin_customer;
    //private Sheet sheet = null;
    private static String EXCEL_SHEET_NAME = "Sheet1";
    // private Cell cell = null;
    // Workbook workbook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoicewise_report);
        calendar = Calendar.getInstance();
        recyclerView = findViewById(R.id.recyclerview_prouct_report);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        spin_customer = (AppCompatSpinner)findViewById(R.id.spinner_report_customer);
        //lyt_share =  findViewById(R.id.lyt_share);
        myDatabase = new MyDatabase(InvoicewiseReport.this);
        textView_fromDate = findViewById(R.id.textView_fromDate);
        textView_toDate = findViewById(R.id.textView_toDate);

        bttn_fetch = (Button)findViewById(R.id.button_fetch);
        adapter = new InvoicewiseReportAdapter(list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sessionValue =new SessionValue(getApplicationContext());
        // list=myDatabase.getDailyReport();

        Log.e("sizelist",""+list.size());
        // sales.addAll(myDatabase.getFullSales());
        Get_Customers();
        setrecyclerview();
        spin_customer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_cust_id = array_customerId.get(position);
                Log.e(TAG, ""+str_cust_id);
                str_fromdate = textView_fromDate.getText().toString().trim();
                str_todate = textView_toDate.getText().toString().trim();
                if (!str_cust_id.equals("-1")) {
                    list.clear();
                    getProductwiseReport(str_cust_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        bttn_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_fromdate = textView_fromDate.getText().toString().trim();
                str_todate = textView_toDate.getText().toString().trim();

                if (!str_fromdate.isEmpty() && !str_todate.isEmpty()){



                }else {
                    Toast.makeText(getApplicationContext(), "Please select date", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textView_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateClick("FromDate");
            }
        });

        textView_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateClick("ToDate");
            }
        });

        try {
            EXECUTIVE_ID = new SessionAuth(InvoicewiseReport.this).getExecutiveId();
            routeid = sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID);


        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void Get_Customers(){

        array_customername.clear();
        array_customerId.clear();

        array_customername.add("<-- Select Customer -->");
        array_customerId.add("-1");

        shops.addAll(myDatabase.getRegisteredCustomers());

        Log.e("Cust array size", ""+shops.size());

        for (int b =0; b<shops.size(); b++)
        {
            array_customername.add(shops.get(b).getShopName());
            array_customerId.add(""+shops.get(b).getShopId());
        }

        Log.e("Customers", ""+array_customername);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark , array_customername);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spin_customer.setAdapter(adapter);
    }

    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }

    private void getProductwiseReport(String custid) {
        if (!checkConnection()) {
            //setErrorView(getString(R.string.no_internet), "", false);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        final JSONObject object =new JSONObject();
        try {
            object.put("executive_id", EXECUTIVE_ID);//
            object.put("customer_id",custid );//
            object.put("from_date", str_fromdate);
            object.put("to_date", str_todate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("bonus report object", ""+object);

        webInvoicewiseReport(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG,"bonus report"+response);

                try {
                    // if (response.getString("status").equalsIgnoreCase("success") && !response.isNull("ExecutiveBonusDetail")) {

                    JSONArray listArray = response.getJSONArray("InvoiceWiseDetails");
                    for (int i = 0; i < listArray.length(); i++) {

                        JSONObject listobject = listArray.getJSONObject(i);
                        InvoicewiseReportMdl report = new InvoicewiseReportMdl();

                        report.setAccname(listobject.getString("acc_name"));

                        report.setSumofgty(listobject.getString("sum_of_qty"));
                        report.setSumofvalue(listobject.getString("sum_of_value"));
                        report.setTax(listobject.getString("tax"));
                        report.setGrossvalue(listobject.getString("gross_value"));
                        report.setInvoiceno(listobject.getString("invoice_no"));

                        list.add(report);
                    }

                    progressBar.setVisibility(View.GONE);
                    //   errorView.setVisibility(View.GONE);

                    recyclerView.setVisibility(View.VISIBLE);
                    //         Recycler View
                    recyclerView.setHasFixedSize(true);
                    //        Item Divider in recyclerView
                    recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(InvoicewiseReport.this)
                            .showLastDivider()
                            .build());
                    recyclerView.setLayoutManager(new LinearLayoutManager(InvoicewiseReport.this));
                    recyclerView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onErrorResponse(String error) {

               // setErrorView(error,getString(R.string.error_view_retry),true);
                progressBar.setVisibility(View.GONE);

            }
        }, object);
    }

    private void onDateClick(String tag) {

        // Get Current Date
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (tag.equals("FromDate")) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(InvoicewiseReport.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                            // dia_tvChequeReceivedDate.setText(dateFormat.format(calendar.getTime()));
                            textView_fromDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        else {

            final DatePickerDialog datePickerDialog = new DatePickerDialog(InvoicewiseReport.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                            //  dia_tvChequeClearingDate.setText(dateFormat.format(calendar.getTime()));
                            textView_toDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);

            // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }


    private void setrecyclerview() {
        adapter = new InvoicewiseReportAdapter(list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(InvoicewiseReport.this)
                .showLastDivider()
                .build());
        recyclerView.setLayoutManager(new LinearLayoutManager(InvoicewiseReport.this));
        recyclerView.setAdapter(adapter);

    }
    //set ErrorView
    private void setErrorView(final String title, final String subTitle, boolean isRetry) {

        recyclerView.setVisibility(View.GONE);
//        errorView.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
        // setProgressBar(false);
        errorView.setConfig(ErrorView.Config.create()
                .title(title)
                .subtitle(subTitle)
                .retryVisible(isRetry)
                .build());

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {


                //  getBonusReport();

                //  storeVanStockFromServer();
//                getProductTypesList();

            }
        });
    }

}