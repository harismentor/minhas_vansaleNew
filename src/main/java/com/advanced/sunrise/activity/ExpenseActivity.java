package com.advanced.minhas.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvExpenseAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Expense;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigKey.REQ_EXPENSE;
import static com.advanced.minhas.webservice.WebService.webAllExpense;

public class ExpenseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RvExpenseAdapter adapter;
    private ProgressBar progressBar;
    private ErrorView errorView;
    ImageButton img_reload;
    MyDatabase myDatabase;
    Button bttn_expense_approval;

    ArrayList<Expense> array_expense = new ArrayList<Expense>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        myDatabase = new MyDatabase(this);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_expense);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        errorView = (ErrorView)findViewById(R.id.errorView);
        img_reload = (ImageButton) findViewById(R.id.imageButton_retry);
        bttn_expense_approval = (Button)findViewById(R.id.button_expense_approve);

        img_reload.setVisibility(View.VISIBLE);

        try {
            array_expense.addAll(myDatabase.getAllExpense());
            }catch (Exception e){

            }

        adapter = new RvExpenseAdapter(this, array_expense);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(itemAnimator);

//                Item Divider in recyclerView
//                recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
//                .showLastDivider()`
//                .color(getResources().getColor(R.color.divider))
//                .build());

        recyclerView.setLayoutManager(new LinearLayoutManager(ExpenseActivity.this));
        recyclerView.setAdapter(adapter);

        img_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                get_Expense_List();

            }
        });

        bttn_expense_approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapter.getExpenseList();

                for (Expense e : adapter.getExpenseList()){

                    Log.e("Expense Id", "/"+e.getId());
                    Log.e("Rec No entered", "/"+e.getReceiptNo());
                    Log.e("Amount entered", "/"+e.getAmount());
                    Log.e("Remark entered", "/"+e.getRemarks());

                    myDatabase.insertExpenseDetails(e);

                    Toast.makeText(getApplicationContext(), "Expense saved successfully", Toast.LENGTH_SHORT).show();

                    finish();
                }

                Log.e("Total expense", ""+adapter.total_expense());
            }
        });
    }

    private void get_Expense_List(){

        Log.e("get expense","response expense calling 1");

        array_expense.clear();

        if (!checkConnection()) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        setProgressBar(true);

        webAllExpense(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        Log.e("response","response Banks:"+response.toString());

                        JSONArray expenseArray = response.getJSONArray("Expense");

                        for (int i = 0; i < expenseArray.length(); i++) {

                            JSONObject expenseobject = expenseArray.getJSONObject(i);

                        Expense expense = new Expense();

                        expense.setId(expenseobject.getString("id"));
                        expense.setName(expenseobject.getString("name"));


                        }

                        setProgressBar(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!array_expense.isEmpty()) {

                    myDatabase.deleteTableRequest(REQ_EXPENSE);

                    for (Expense exp : array_expense ) {

                     //   Log.e("expense array insert ", "/"+exp.getName());

                        myDatabase.insertExpense(exp); ; //store expenses to local

                        array_expense.clear();
                    }

                    array_expense.clear();
                    array_expense.addAll(myDatabase.getAllExpense());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onErrorResponse(String error) {

                setProgressBar(false);
                Toast.makeText(getApplicationContext(), ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }

    private void setProgressBar(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

        } else
            progressBar.setVisibility(View.GONE);
           recyclerView.setVisibility(View.VISIBLE);
    }

}
