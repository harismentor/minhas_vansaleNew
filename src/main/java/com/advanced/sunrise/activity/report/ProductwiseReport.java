package com.advanced.minhas.activity.report;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.ProductwiseReportAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.ProductwiseReportMdl;
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

import static com.advanced.minhas.webservice.WebService.webDailyProductReport;

public class ProductwiseReport extends AppCompatActivity {
    private MyDatabase myDatabase;
    String TAG = "ProductwiseReport";
    private ProductwiseReportAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<ProductwiseReportMdl> list = new ArrayList<>();
    private ImageButton ibBack;
    String str_fromdate="",str_todate="";
    private LinearLayout lyt_share;
    private TextView textView_fromDate, textView_toDate;
    private Button bttn_fetch;
    private Calendar calendar;
    private ErrorView errorView;
    private ProgressBar progressBar;
    private String EXECUTIVE_ID = "" ,routeid= "";
    private SessionValue sessionValue;
    //private Sheet sheet = null;
    private static String EXCEL_SHEET_NAME = "Sheet1";
   // private Cell cell = null;
   // Workbook workbook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productwise_report);
        calendar = Calendar.getInstance();
        recyclerView = findViewById(R.id.recyclerview_prouct_report);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        //lyt_share =  findViewById(R.id.lyt_share);
        myDatabase = new MyDatabase(ProductwiseReport.this);
        textView_fromDate = findViewById(R.id.textView_fromDate);
        textView_toDate = findViewById(R.id.textView_toDate);
        bttn_fetch = (Button)findViewById(R.id.button_fetch);
        adapter = new ProductwiseReportAdapter(list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sessionValue =new SessionValue(getApplicationContext());
       // list=myDatabase.getDailyReport();

        Log.e("sizelist",""+list.size());
        // sales.addAll(myDatabase.getFullSales());

        setrecyclerview();

        bttn_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_fromdate = textView_fromDate.getText().toString().trim();
                str_todate = textView_toDate.getText().toString().trim();

                if (!str_fromdate.isEmpty() && !str_todate.isEmpty()){
                    list.clear();
                    getProductwiseReport();
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
            EXECUTIVE_ID = new SessionAuth(ProductwiseReport.this).getExecutiveId();
             routeid = sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }

    private void getProductwiseReport() {
        if (!checkConnection()) {
           // setErrorView(getString(R.string.no_internet), "", false);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
//        bonusReport.clear();
//        bonusamount=0;
//        bonusreturn=0;

        final JSONObject object =new JSONObject();
        try {
            object.put("executive_id", EXECUTIVE_ID);
            object.put("route_id", routeid);
            object.put("from_date", str_fromdate);
            object.put("to_date", str_todate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("bonus report object", ""+object);

        webDailyProductReport(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG,"bonus report"+response);

                try {
                   // if (response.getString("status").equalsIgnoreCase("success") && !response.isNull("ExecutiveBonusDetail")) {

                        JSONArray listArray = response.getJSONArray("ProductWiseDetails");
                        for (int i = 0; i < listArray.length(); i++) {

                            JSONObject listobject = listArray.getJSONObject(i);
                            ProductwiseReportMdl report = new ProductwiseReportMdl();

                            report.setCategory(listobject.getString("category_name"));
                            report.setItemname(listobject.getString("product_name"));
                            report.setSumofqnty(listobject.getString("sum_of_qty"));
                            report.setSumofvalue(listobject.getString("sum_of_value"));
                            report.setTax(listobject.getString("tax"));
                            report.setGrossvalue(listobject.getString("gross_value"));

                            list.add(report);
                        }

                        progressBar.setVisibility(View.GONE);
                     //   errorView.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.VISIBLE);
                        //         Recycler View
                        recyclerView.setHasFixedSize(true);
                        //        Item Divider in recyclerView
                        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ProductwiseReport.this)
                                .showLastDivider()
                                .build());
                        recyclerView.setLayoutManager(new LinearLayoutManager(ProductwiseReport.this));
                        recyclerView.setAdapter(adapter);



//                    }else {
//
//                        progressBar.setVisibility(View.GONE);
//                        recyclerView.setAdapter(null);
//
//
//                        recyclerView.setVisibility(View.GONE);
//                        errorView.setVisibility(View.VISIBLE);
//                        setErrorView("","No Data",true);
//
//                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onErrorResponse(String error) {

              //  setErrorView(error,getString(R.string.error_view_retry),true);
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(ProductwiseReport.this,
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

            final DatePickerDialog datePickerDialog = new DatePickerDialog(ProductwiseReport.this,
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
        adapter = new ProductwiseReportAdapter(list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ProductwiseReport.this)
                .showLastDivider()
                .build());
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductwiseReport.this));
        recyclerView.setAdapter(adapter);

    }
    //set ErrorView
    private void setErrorView(final String title, final String subTitle, boolean isRetry) {

        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

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