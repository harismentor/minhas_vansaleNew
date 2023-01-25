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
import com.advanced.minhas.adapter.RvBonusReportAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.BonusReport;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.advanced.minhas.webservice.WebService.webBonusReport;

public class ReportBonus extends AppCompatActivity {

        private RecyclerView recyclerView;
        private LinearLayout layout, layout_total;
        private ErrorView errorView;
        private ImageButton ibBack;
        private ProgressBar progressBar;
        String TAG = "ReportBonus";
        private MyDatabase myDatabase;
        private Calendar calendar;
        private Button bttn_fetch;
        private RvBonusReportAdapter adapter;

        TextView tv_fromdate, tv_todate, tv_bonus_total, tv_return_total;
        private String EXECUTIVE_ID = "", str_fromdate="", str_todate="";
        float bonusamount = 0, bonusreturn = 0;

    final ArrayList<Shop> shops = new ArrayList<>();

    private ArrayList<String> array_customername = new ArrayList<String>();
    private ArrayList<String> array_customerId = new ArrayList<String>();

    final ArrayList<BonusReport> bonusReport = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bonus);

        adapter = new RvBonusReportAdapter(bonusReport);

        calendar = Calendar.getInstance();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        tv_fromdate = (TextView)findViewById(R.id.textView_fromDate);
        tv_todate = (TextView)findViewById(R.id.textView_toDate);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorView = (ErrorView) findViewById(R.id.errorView);
        bttn_fetch = (Button)findViewById(R.id.button_fetch);
        layout_total = (LinearLayout)findViewById(R.id.layout_total);
        tv_bonus_total = (TextView)findViewById(R.id.total_bonus_amount);
        tv_return_total = (TextView)findViewById(R.id.tv_total_return);

        myDatabase = new MyDatabase(ReportBonus.this);

        try {
            EXECUTIVE_ID = new SessionAuth(ReportBonus.this).getExecutiveId();

        } catch (Exception e) {
            e.getStackTrace();
        }

        tv_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDateClick("FromDate");

            }
        });

        tv_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDateClick("ToDate");

            }
        });

        bttn_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_fromdate = tv_fromdate.getText().toString().trim();
                str_todate = tv_todate.getText().toString().trim();

                if (!str_fromdate.isEmpty() && !str_todate.isEmpty()){

                          getBonusReport();

                }else {
                    Toast.makeText(getApplicationContext(), "Please select date", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    //    Load bonus report from Server
    private void getBonusReport()
    {
        if (!checkConnection()) {
            setErrorView(getString(R.string.no_internet), "", false);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        bonusReport.clear();
        bonusamount=0;
        bonusreturn=0;

        final JSONObject object =new JSONObject();
        try {
            object.put("executive_id", EXECUTIVE_ID);
            object.put("from_date", str_fromdate);
            object.put("to_date", str_todate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("bonus report object", ""+object);

        webBonusReport(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG,"bonus report"+response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success") && !response.isNull("ExecutiveBonusDetail")) {

                        JSONArray listArray = response.getJSONArray("ExecutiveBonusDetail");
                        for (int i = 0; i < listArray.length(); i++) {

                            JSONObject listobject = listArray.getJSONObject(i);
                            BonusReport report = new BonusReport();

                            report.setInvoiceNo(listobject.getString("invoice_no"));
                            report.setCreditNoteNo(listobject.getString("credit_note_no"));
                            report.setCustomerName(listobject.getString("customer_name"));
                            report.setBonusAmount(listobject.getString("bonus_amount"));

                            float bamount = Float.parseFloat(listobject.getString("bonus_amount"));
                            bonusamount = bonusamount+bamount;

                            report.setBonusReturnAmount(listobject.getString("bonus_return_amount"));
                            float retamount = Float.parseFloat(listobject.getString("bonus_return_amount"));
                            bonusreturn = bonusreturn+retamount;

                            bonusReport.add(report);
                        }

                        progressBar.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.VISIBLE);
                        //         Recycler View
                        recyclerView.setHasFixedSize(true);
                        //        Item Divider in recyclerView
                        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ReportBonus.this)
                                .showLastDivider()
                                .build());
                        recyclerView.setLayoutManager(new LinearLayoutManager(ReportBonus.this));
                        recyclerView.setAdapter(adapter);

                        layout_total.setVisibility(View.VISIBLE);

                        tv_bonus_total.setText(""+bonusamount);
                        tv_return_total.setText(""+bonusreturn);

                    }else {

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(null);

                        layout_total.setVisibility(View.GONE);
                        bonusamount=0;
                        bonusreturn=0;

                        recyclerView.setVisibility(View.GONE);
                        errorView.setVisibility(View.VISIBLE);
                        setErrorView("","No Data",true);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onErrorResponse(String error) {

                setErrorView(error,getString(R.string.error_view_retry),true);
                progressBar.setVisibility(View.GONE);

            }
        }, object);
    }


    private void onDateClick(String tag){

        // Get Current Date
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (tag.equals("FromDate")) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(ReportBonus.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                            // dia_tvChequeReceivedDate.setText(dateFormat.format(calendar.getTime()));
                            tv_fromdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        else {

            final DatePickerDialog datePickerDialog = new DatePickerDialog(ReportBonus.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                            //  dia_tvChequeClearingDate.setText(dateFormat.format(calendar.getTime()));
                            tv_todate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);

           // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }

    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
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


                    getBonusReport();

                //  storeVanStockFromServer();
//                getProductTypesList();

            }
        });
    }

}
