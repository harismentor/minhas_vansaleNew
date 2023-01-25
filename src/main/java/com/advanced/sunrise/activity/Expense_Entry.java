
package com.advanced.minhas.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvExpenseEntryDetails;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Expense;
import com.advanced.minhas.model.ExpenseDetails;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;

public class Expense_Entry extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    MyDatabase myDatabase;
    Button button_add;
    EditText edt_amount, edt_remarks;
    TextView textView_toolbar_shopNameAndCode;

    AppCompatSpinner spinner_expense;
    SessionAuth sessionAuth;
    SessionValue sessionValue;
    RecyclerView recyclerView;
    RvExpenseEntryDetails adapter;
    float cash_inhand = 0;
    String EXECUTIVE_ID="", dayRegId="", expense_ID="", str_expense_id="", str_amount="", str_remarks="", str_RouteID = "" , st_vehicleno ="";
    ArrayList<Expense> array_expense = new ArrayList<Expense>();
    ArrayList<ExpenseDetails> array_expenseDetails = new ArrayList<ExpenseDetails>();
    private ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense__entry);
        spinner_expense = (AppCompatSpinner) findViewById(R.id.spinner_expense);
        button_add = findViewById(R.id.button_add);
        edt_amount = findViewById(R.id.edt_amount);
        edt_remarks = findViewById(R.id.edt_remarks);
        recyclerView = findViewById(R.id.recyclerview_data);
        textView_toolbar_shopNameAndCode = findViewById(R.id.textView_toolbar_shopNameAndCode);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);


        myDatabase = new MyDatabase(this);
        sessionAuth = new SessionAuth(this);
        sessionValue = new SessionValue(this);

        adapter = new RvExpenseEntryDetails(array_expenseDetails);

        textView_toolbar_shopNameAndCode.setText("Expense Entry");
        EXECUTIVE_ID =sessionAuth.getExecutiveId();
        dayRegId=sessionValue.getDayRegisterId();
        str_RouteID = sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID);

        try {
            array_expense.addAll(myDatabase.getAllExpense());

            if (!array_expense.isEmpty()){
                setExpense(array_expense);
            }else {
                Toast.makeText(getApplicationContext(), "Error getting expense", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){

        }
//        try{
//            st_vehicleno = sessionValue.get_registered_vehicledetails();
//            Log.e("st_vehicleno haris",""+st_vehicleno);
//
//        }catch (Exception e){
//
//        }
        GetExpenseDetails();

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button_add.setOnClickListener(this);
    }



    private void setExpense(ArrayList<Expense> list) {

        // Initializing an ArrayAdapter
        final ArrayAdapter<Expense> expenseAdapter = new ArrayAdapter<Expense>(this, R.layout.spinner_brand_background, list) {

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                return view;
            }
        };

        expenseAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinner_expense.setAdapter(expenseAdapter);

        expenseAdapter.notifyDataSetChanged();
        //  spinnerBrand.setSelection(0);
        spinner_expense.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_expense:

                Expense w = (Expense) parent.getSelectedItem();

                str_expense_id = ""+w.getId();
                Log.e("Ware house", ""+expense_ID);


                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add:

                str_amount = edt_amount.getText().toString().trim();
                String str_remark = edt_remarks.getText().toString().trim();
                str_remarks = str_remark ;

                if (validateExpense()){

                    Log.e("expense", " ok" );
                    cash_inhand = sessionAuth.getCashinHand();
                    float paid = Float.parseFloat(str_amount);
                    float cash = cash_inhand - paid ;
                    sessionAuth.updateCashinHand(cash);
                    ExpenseSave();
                }

                break;
        }

    }

    public boolean validateExpense(){

        boolean result = false;

        if (str_expense_id.isEmpty()){
            Toast.makeText(getApplicationContext(), "Invalid Expense", Toast.LENGTH_SHORT).show();
            result = false;
        }else if (str_amount.isEmpty()){
            Toast.makeText(getApplicationContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
            result = false;
        }else if (Double.parseDouble(str_amount)<=0){
            Toast.makeText(getApplicationContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
            result = false;
        }else if (!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), "Check Connectivity", Toast.LENGTH_SHORT).show();
            result = false;
        }else {
            result = true;
        }

        return result;
    }


    private void ExpenseSave(){

        Log.e("expense", " calling" );
        final ProgressDialog pd = ProgressDialog.show(Expense_Entry.this, null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        // JSONArray expensearray = new JSONArray();

        try {
            /*for (Expense c : expenseEntry) {

                JSONObject obj = new JSONObject();

                obj.put("expense_id", c.getId());
                obj.put("amount", c.getAmount());
                obj.put("receipt_no", c.getReceiptNo());
                obj.put("remarks", c.getRemarks());

                expensearray.put(obj);
           }*/

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("route_id", str_RouteID);
            object.put("expense_id", ""+str_expense_id);
            object.put("amount", ""+str_amount);
            object.put("remarks", ""+str_remarks);


            pd.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("expense", " entry object  " + object);


        WebService.webExpenseEntry(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("expense", "response  " + response);

                try {
                    if (response.getString("result").equalsIgnoreCase("Success")) {

                        //  boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RECEIPT_TYPE);
                        Log.e("save response", "Success  ");
                        Toast.makeText(getApplicationContext(), "Expense entry "+response.getString("result"), Toast.LENGTH_SHORT).show();
                        edt_amount.setText("");
                        edt_remarks.setText("");

                        str_amount="";
                        str_remarks="";
                        GetExpenseDetails();

                    } else
                        Toast.makeText(getApplicationContext(), "Expense "+response.getString("result"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("save catch", ""+e.getMessage());
                    e.printStackTrace();
                }

                pd.dismiss();

            } // direct purchase, unit transfer

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }


    private void GetExpenseDetails()
    {
        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Check Connectivity", Toast.LENGTH_SHORT).show();
            return;
        }

        array_expenseDetails.clear();

        final ProgressDialog pd = ProgressDialog.show(Expense_Entry.this, null, "Please wait...", false, false);

        final JSONObject object =new JSONObject();
        try {
            object.put("executive_id", EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("route_id", str_RouteID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("expnse report object", ""+object);

        WebService.GetExpenseDetails(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Expense","Details"+response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")&& !response.isNull("expense_list")) {

                        JSONArray listArray = response.getJSONArray("expense_list");
                        for (int i = 0; i < listArray.length(); i++) {

                            JSONObject listobject = listArray.getJSONObject(i);
                            ExpenseDetails report = new ExpenseDetails();

                            report.setDate(listobject.getString("date"));
                            report.setName(listobject.getString("expense_head"));
                            if (listobject.getString("remarks").isEmpty()){
                                report.setRemarks("Nil");
                            }else {
                                report.setRemarks(listobject.getString("remarks"));
                            }
                            report.setAmount(listobject.getString("amount"));
                            report.setStatus(listobject.getString("status"));


                            array_expenseDetails.add(report);
                        }

                        pd.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        //         Recycler View
                        recyclerView.setHasFixedSize(true);
                        //        Item Divider in recyclerView
                        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(Expense_Entry.this)
                                .showLastDivider()
                                .build());
                        recyclerView.setLayoutManager(new LinearLayoutManager(Expense_Entry.this));
                        recyclerView.setAdapter(adapter);


                    }else {

                        recyclerView.setAdapter(null);


                        recyclerView.setVisibility(View.GONE);
                        pd.dismiss();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
