package com.advanced.minhas.activity.report;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvOutstandingReportAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.OutStandingreport;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.advanced.minhas.webservice.WebService.webOutstandingReport;

public class ReportOutstanding extends AppCompatActivity {

    private AppCompatSpinner spin_customer;
    private RecyclerView recyclerView;
    RelativeLayout layout_data;

    private LinearLayout layout;
    private ErrorView errorView;
    private RvOutstandingReportAdapter adapter;

    private ProgressBar progressBar;
    String TAG = "ReportOutstanding";
    private MyDatabase myDatabase;
    private ImageButton ibBack;
    String str_cust_id="";
    final ArrayList<Shop> shops = new ArrayList<>();
    final ArrayList<OutStandingreport> outstandingReport = new ArrayList<>();

    private ArrayList<String> array_customername = new ArrayList<String>();
    private ArrayList<String> array_customerId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_outstanding);

        adapter = new RvOutstandingReportAdapter(outstandingReport);

        spin_customer = (AppCompatSpinner)findViewById(R.id.spinner_report_customer);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_bonus_report);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);

        layout_data = (RelativeLayout)findViewById(R.id.layout_data);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorView = (ErrorView) findViewById(R.id.errorView);

        myDatabase = new MyDatabase(ReportOutstanding.this);

        Get_Customers();

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        spin_customer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_cust_id = array_customerId.get(position);
                Log.e(TAG, ""+str_cust_id);

                if (!str_cust_id.equals("-1")) {
                    getOutStandingReport(str_cust_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //    Load Outstanding report from Server
    private void getOutStandingReport(String custid)
    {
        if (!checkConnection()) {
            setErrorView(getString(R.string.no_internet), "", false);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        outstandingReport.clear();

        final JSONObject object =new JSONObject();
        try {
            object.put("customer_id", custid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("O/S report object", ""+object);

        webOutstandingReport(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG,"getoutstanding report"+response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success") && !response.isNull("Invoice_list")) {

                        JSONArray listArray = response.getJSONArray("Invoice_list");
                        for (int i = 0; i < listArray.length(); i++) {

                            JSONObject listobject = listArray.getJSONObject(i);
                            OutStandingreport report = new OutStandingreport();

                            report.setCustomername(listobject.getString("customer_name"));
                            report.setInvoiceNo(listobject.getString("invoice_no"));
                            report.setSaleDate(listobject.getString("sale_date"));
                            report.setSaleAmount(listobject.getString("sale_amount"));
                            report.setPaidAmount(listobject.getString("paid"));
                            report.setBalance(listobject.getString("balance"));
                            report.setDays(listobject.getString("days"));

                            outstandingReport.add(report);
                        }

                        progressBar.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);

                        layout_data.setVisibility(View.VISIBLE);
                        //         Recycler View
                        recyclerView.setHasFixedSize(true);
                        //        Item Divider in recyclerView
                        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ReportOutstanding.this)
                                .showLastDivider()
                                .build());
                        recyclerView.setLayoutManager(new LinearLayoutManager(ReportOutstanding.this));
                        recyclerView.setAdapter(adapter);

                    }else {

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(null);

                        layout_data.setVisibility(View.GONE);
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

    //set ErrorView
    private void setErrorView(final String title, final String subTitle, boolean isRetry) {

        layout_data.setVisibility(View.GONE);
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

                if (!str_cust_id.equals("-1")){
                    getOutStandingReport(str_cust_id);
                }else {
                    Toast.makeText(getApplicationContext(), "Please Select Customer", Toast.LENGTH_SHORT).show();
                }

              //  storeVanStockFromServer();
//                getProductTypesList();

            }
        });
    }

}
