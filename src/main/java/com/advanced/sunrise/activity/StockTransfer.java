package com.advanced.minhas.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvStockTransferAdapter;
import com.advanced.minhas.model.Ordertransfer;
import com.advanced.minhas.model.Stocktransfer;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.webservice.WebService;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.advanced.minhas.webservice.WebService.webGetStocktransfer;
import static com.advanced.minhas.webservice.WebService.webGetStocktransferwithorderlist;

public class StockTransfer extends AppCompatActivity {
    String EXECUTIVE_ID = "";
    ArrayList<Stocktransfer> stockarray = new ArrayList<>();
    ArrayList<Stocktransfer> stockorderarray = new ArrayList<>();
    ArrayList<Stocktransfer> stocktransferarray = new ArrayList<>();
    ArrayList<Ordertransfer> orderarray = new ArrayList<>();
    RvStockTransferAdapter adapter;
    RecyclerView recyclerView;
    ImageButton ibBack;
    TextView textview_heading;
    SessionAuth sessionAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer);
        recyclerView = findViewById(R.id.recyclerView_stock_transfer);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        textview_heading = findViewById(R.id.textview_heading);
        sessionAuth = new SessionAuth(this);

        EXECUTIVE_ID = new SessionAuth(this).getExecutiveId();

        try {
            Date c = Calendar.getInstance().getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);

            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            String time = dateFormat.format(c);

            textview_heading.setText("Stock Transfer of "+sessionAuth.getExecutiveName()+" as on "+time+" "+formattedDate);

        }catch (Exception e){

        }


        adapter = new RvStockTransferAdapter(stockarray);

        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
                .build());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

      //  GetStockTransfer();
        GetStockTransferwith_orderlist();

    }

    private void GetStockTransferwith_orderlist() {

        stockarray.clear();
        orderarray.clear();

        JSONObject object = new JSONObject();
        try {
            object.put("executive_id", EXECUTIVE_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("stock transfer object", ""+object.toString());

        final ProgressDialog pd = ProgressDialog.show(StockTransfer.this, null, "Please wait...", false, false);

        webGetStocktransferwithorderlist(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", ""+response.toString());
                String st_response = "";
                try {

                    st_response = response.getString("status");

                    if (response.getString("status").equalsIgnoreCase("Success")){
                        try {

                            JSONArray array = response.getJSONArray("transfer_list");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject shopObj = array.getJSONObject(i);


                                Stocktransfer stock = new Stocktransfer();

                                stock.setId(shopObj.getString("id"));
                                stock.setTransferNo(shopObj.getString("transfer_no"));
                                stock.setOrderNo("");
                                //stock.setTransferNo("1");
                                stock.setDate(shopObj.getString("date"));
                                stock.setStockFrom(shopObj.getString("WarehouseFrom"));
                                stock.setStockTo(shopObj.getString("WarehouseTo"));
                                stock.setDescription(shopObj.getString("remarks"));

                                stockarray.add(stock);

                            }
                        }catch (Exception e){

                        }

                        try {
                            JSONArray arrayorder = response.getJSONArray("order_transfer_list");

                            for (int i = 0; i < arrayorder.length(); i++) {

                                JSONObject shopObj = arrayorder.getJSONObject(i);


                                Ordertransfer order = new Ordertransfer();

                                order.setId(shopObj.getString("id"));
                                order.setOrderNo(shopObj.getString("order_no"));
                                //order.setOrderNo("3");
                                order.setDate(shopObj.getString("date"));
                                order.setStockFrom(shopObj.getString("WarehouseFrom"));
                                order.setStockTo(shopObj.getString("WarehouseTo"));
                                order.setDescription(shopObj.getString("remarks"));
                              //  order.setDescription("test");
                                orderarray.add(order);

                            }
                        }catch (Exception e){

                        }

                      //  stockarray.addAll(stocktransferarray);
                      //  stockarray.addAll(stockorderarray);




                    }
                    else{

                        Toast.makeText(getApplicationContext(),st_response,Toast.LENGTH_LONG).show();

                    }




                } catch (JSONException e) {
//                    Toast.makeText(StockTransfer.this, "Error getting stock transfer..!", Toast.LENGTH_SHORT).show();


                    Toast.makeText(getApplicationContext(),st_response,Toast.LENGTH_LONG).show();

                }
                if(orderarray.size()>0) {
                    Log.e("sizeorer",""+orderarray.size());
                    //   for (Stocktransfer s : stockarray) {

                    for (Ordertransfer o : orderarray) {
                        Stocktransfer stock = new Stocktransfer();
                        stock.setTransferNo(o.getOrderNo());
                        stock.setOrderNo(o.getOrderNo());
                        stock.setDescription(o.getDescription());
                        stock.setId(o.getId());
                        stock.setStockFrom(o.getStockFrom());
                        stock.setStockTo(o.getStockTo());
                        stock.setDate(o.getDate());
                        stockarray.add(stock);
                    }

                    // }
                }
                if (stockarray.size()>0){

                    Log.e("inside if", ""+stockarray.size());
                    Log.e("inside 0", ""+stockarray.get(0).getDescription());
                    Log.e("inside 01", ""+stockarray.get(1).getDescription());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("inside else", "1");
                    stockarray.clear();
                    adapter.notifyDataSetChanged();
                }
                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();

                Toast.makeText(StockTransfer.this, error, Toast.LENGTH_SHORT).show();
            }
        }, object);

    }


    //    Get Stock transfer
    private void GetStockTransfer() {

        stockarray.clear();

        JSONObject object = new JSONObject();
        try {
            object.put("executive_id", EXECUTIVE_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("stock transfer object", ""+object.toString());

        final ProgressDialog pd = ProgressDialog.show(StockTransfer.this, null, "Please wait...", false, false);

        webGetStocktransfer(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", ""+response.toString());
                String st_response = "";
                try {

                    st_response = response.getString("status");

                    if (response.getString("status").equalsIgnoreCase("Success")){

                        JSONArray array = response.getJSONArray("transfer_list");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject shopObj = array.getJSONObject(i);

                            Stocktransfer stock = new Stocktransfer();

                            stock.setId(shopObj.getString("id"));
                            stock.setTransferNo(shopObj.getString("transfer_no"));
                            stock.setDate(shopObj.getString("date"));
                            stock.setStockFrom(shopObj.getString("WarehouseFrom"));
                            stock.setStockTo(shopObj.getString("WarehouseTo"));
                            stock.setDescription(shopObj.getString("remarks"));

                            stockarray.add(stock);

                        }


                    }
                    else{

                        Toast.makeText(getApplicationContext(),st_response,Toast.LENGTH_LONG).show();

                    }

                    if (stockarray.size()>0){

                        Log.e("inside", "1");
                        adapter.notifyDataSetChanged();
                    } else {

                        stockarray.clear();
                        adapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
//                    Toast.makeText(StockTransfer.this, "Error getting stock transfer..!", Toast.LENGTH_SHORT).show();


                    Toast.makeText(getApplicationContext(),st_response,Toast.LENGTH_LONG).show();

                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();

                Toast.makeText(StockTransfer.this, error, Toast.LENGTH_SHORT).show();
            }
        }, object);

    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}