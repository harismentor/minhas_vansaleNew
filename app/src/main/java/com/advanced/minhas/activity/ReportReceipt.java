package com.advanced.minhas.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.ReportReceiptAdapter;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Receipt;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

public class ReportReceipt extends AppCompatActivity {
    private MyDatabase myDatabase;

    private ReportReceiptAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<Receipt> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_receipt);
        recyclerView = findViewById(R.id.recyclerview_receipt_report);
        myDatabase = new MyDatabase(ReportReceipt.this);
        adapter = new ReportReceiptAdapter(list);
        list=myDatabase.getReceiptDetails();
        Log.e("sizelist",""+list.size());
        // sales.addAll(myDatabase.getFullSales());

        setrecyclerview();
    }
    private void setrecyclerview() {
        adapter = new ReportReceiptAdapter(list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ReportReceipt.this)
                .showLastDivider()
                .build());
        recyclerView.setLayoutManager(new LinearLayoutManager(ReportReceipt.this));
        recyclerView.setAdapter(adapter);

    }
}