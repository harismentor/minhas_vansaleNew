package com.advanced.minhas.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.ReturnReportAdapter;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Sales;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

public class ReportReturn extends AppCompatActivity {
    private MyDatabase myDatabase;

    private ReturnReportAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<Sales> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_return);
        recyclerView = findViewById(R.id.recyclerview_return_report);
        myDatabase = new MyDatabase(ReportReturn.this);
        adapter = new ReturnReportAdapter(list);
        list=myDatabase.getFullReturn();
        Log.e("sizelist",""+list.size());
        // sales.addAll(myDatabase.getFullSales());

        setrecyclerview();

    }

    private void setrecyclerview() {
        adapter = new ReturnReportAdapter(list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ReportReturn.this)
                .showLastDivider()
                .build());
        recyclerView.setLayoutManager(new LinearLayoutManager(ReportReturn.this));
        recyclerView.setAdapter(adapter);

    }
}