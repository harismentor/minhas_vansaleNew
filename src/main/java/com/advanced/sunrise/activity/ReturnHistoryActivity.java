package com.advanced.minhas.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvReturnHistoryAdapter;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.view.ErrorView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigKey.VIEW_ERRORVIEW;
import static com.advanced.minhas.config.ConfigKey.VIEW_RECYCLERVIEW;

public class ReturnHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ErrorView errorView;
    private ViewGroup viewSalesReport;
    private MyDatabase myDatabase;
    private RvReturnHistoryAdapter adapter;

    private ArrayList<Sales> returnList;
    private TextView tvToolBarShopName;
    private ImageButton ibBack;
    private Shop SELECTED_SHOP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_history);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_salesReturn_history);

        errorView = (ErrorView) findViewById(R.id.errorView_salesReturn_history);

        viewSalesReport=(ViewGroup) findViewById(R.id.view_salesReturn_history);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        tvToolBarShopName=(TextView) findViewById(R.id.textView_toolbar_shopNameAndCode);

        myDatabase=new MyDatabase(ReturnHistoryActivity.this);
        returnList=new ArrayList<>();

        try {
            SELECTED_SHOP = (Shop) getIntent().getSerializableExtra(SHOP_KEY);
        } catch (Exception e) {
            e.getStackTrace();
        }

        if (SELECTED_SHOP==null){
            finish();
            return;
        }

        adapter=new RvReturnHistoryAdapter(returnList,SELECTED_SHOP);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
//                .color(getResources().getColor(R.color.divider))
                .build());
        recyclerView.setAdapter(adapter);

        tvToolBarShopName.setText(String.valueOf(SELECTED_SHOP.getShopName()+"\t"+SELECTED_SHOP.getShopCode()));
        tvToolBarShopName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvToolBarShopName.setSelected(true);

        setRecyclerView();

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setRecyclerView() {

        ArrayList<Sales> list=myDatabase.getCustomerReturns(String.valueOf(SELECTED_SHOP.getShopId()));

      //  ArrayList<Sales> list=myDatabase.getCustomerInvoiceReturns(String.valueOf(SELECTED_SHOP.getShopId()));

        if (list.isEmpty()) {
            setErrorView();
            return;
        }

        returnList.clear();
        returnList.addAll(list);
        adapter.notifyDataSetChanged();
        updateViews(VIEW_RECYCLERVIEW);

    }

    //set ErrorView
    private void setErrorView() {

        updateViews(VIEW_ERRORVIEW);
        errorView.setConfig(ErrorView.Config.create()
                .title("No Returns")
                .retryVisible(false)
                .build());
    }

    public void updateViews(int viewCode) {

        switch (viewCode) {

            case VIEW_RECYCLERVIEW:

                viewSalesReport.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);

                break;

            case VIEW_ERRORVIEW:

                viewSalesReport.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);

                break;

        }
    }
}
