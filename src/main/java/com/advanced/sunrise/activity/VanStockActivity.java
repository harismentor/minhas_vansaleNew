package com.advanced.minhas.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvVanStockAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Brand;
import com.advanced.minhas.model.Product;
import com.advanced.minhas.model.ProductType;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.PRODUCT_BRAND_KEY;
import static com.advanced.minhas.config.ConfigKey.PRODUCT_TYPE_KEY;
import static com.advanced.minhas.config.ConfigValue.SALES_VALUE_KEY;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.webservice.WebService.webGetVanStock;

public class VanStockActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener{

    private ProgressBar progressBar;
    String TAG = "VanStockActivity";
    private String EXECUTIVE_ID="", CURRENCY="";
    private RecyclerView recyclerView;
    private AppCompatSpinner spinnerProductType, spinnerProductBrand;

    private RvVanStockAdapter adapter;
    private LinearLayout layout;
    private ErrorView errorView;
    private TextView tvTotalStock, tvTotalQuantity;
    private ImageButton ibUpdate,img_print;
    private SearchView searchView;
    SwitchCompat switch_stock_type;
    private SessionAuth sessionAuth;
    SessionValue sessionValue;
    CheckBox cb_stock;

    final ArrayList<Product> products=new ArrayList<>();

    private MyDatabase myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_van_stock);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_vanStock_product);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layout = (LinearLayout) findViewById(R.id.layout_vanStock);
        errorView = (ErrorView) findViewById(R.id.errorView);
        ibUpdate=(ImageButton)findViewById(R.id.imageButton_vanstock_update);
        searchView = (SearchView)findViewById(R.id.searchView_productList);
        img_print = findViewById(R.id.img_print);

        spinnerProductType = (AppCompatSpinner) findViewById(R.id.spinner_vanStock_productType);
        spinnerProductBrand = (AppCompatSpinner) findViewById(R.id.spinner_vanStock_brand);

        tvTotalStock = (TextView) findViewById(R.id.textView_vanStock_totalStockAmount);
        tvTotalQuantity = (TextView) findViewById(R.id.textView_vanStock_totalStockQuantity);
       // switch_stock_type = (SwitchCompat) findViewById(R.id.switch_stock_type);
        sessionAuth = new SessionAuth(VanStockActivity.this);
        sessionValue = new SessionValue(VanStockActivity.this);
        //cb_stock = findViewById(R.id.cb_stock);

        myDatabase=new MyDatabase(VanStockActivity.this);
        adapter = new RvVanStockAdapter(products);

        EXECUTIVE_ID =sessionAuth.getExecutiveId();

        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);

//         Recycler View
        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
                .build());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);


        products.addAll(myDatabase.getAllStock());
        //switch_stock_type.setChecked(true);
        //switch_stock_type.setText("Van Stock");
        if (!products.isEmpty()) {
            setRecyclerView();

            tvTotalStock.setText(String.valueOf("" + getAmount(myDatabase.getStockAmount()) + " " + CURRENCY));
            tvTotalQuantity.setText(String.valueOf("" + String.valueOf(myDatabase.getStockQuantity())));

        }else

            setErrorView("No Stock for Offline","",true);

        ibUpdate.setVisibility(View.GONE);
//        switch_stock_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if (isChecked) {
//                    switch_stock_type.setText("Van Stock");
//
//                    products.clear();
//                    products.addAll(myDatabase.getAllStock());
//
//                    if (!products.isEmpty()) {
//                        setRecyclerView();
//
//                        tvTotalStock.setText(String.valueOf("" + getAmount(myDatabase.getStockAmount()) + " " + CURRENCY));
//                        tvTotalQuantity.setText(String.valueOf("" + String.valueOf(myDatabase.getStockQuantity())));
//
//                    }else {
//
//                        setErrorView("No Stock for Offline", "", true);
//
//                    }
//
//                }
//                else {
////                    switch_stock_type.setText("Damage Stock");
////
////                    products.clear();
////                    products.addAll(myDatabase.get());
////
////                    if (!products.isEmpty()) {
////                        setRecyclerView();
////
////                        tvTotalStock.setText(String.valueOf("" + getAmount(myDatabase.getDamageStockAmount()) + " " + CURRENCY));
////                        tvTotalQuantity.setText(String.valueOf("" + String.valueOf(myDatabase.getDamageStockQuantity())));
////
////                    }else {
////
////                        setRecyclerView();
////                        tvTotalStock.setText("");
////                        tvTotalQuantity.setText("");
////                        Toast.makeText(getApplicationContext(), "No Stock", Toast.LENGTH_SHORT).show();
////
////                        //  setErrorView("No Stock for Offline", "", true);
////
////                    }
////                }
//
//
//                }
//            }
//        });
//        cb_stock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                products.clear();
//                if(cb_stock.isChecked()){
//                    Log.e("if","reched");
//                    products.addAll(myDatabase.getAllstock_products());
//                    setRecyclerView();
//                }
//                else{
//                    Log.e("else","reched");
//                    products.addAll(myDatabase.getstock_products());
//
//                    setRecyclerView();
//                }
//            }
//        });

        ibUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        img_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product pr = new Product();
//               for(Product p : products){
//                   p.set
//
//               }



                Intent intent = new Intent(getApplicationContext(), StockPreview.class);
                //intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALES);

                intent.putExtra(SALES_VALUE_KEY, products);
                // intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                startActivity(intent);
                finish();


            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return true;

            }
        });
    }

    private void setProductBrand(ArrayList<Brand> list) {

        // Initializing an ArrayAdapter
        final ArrayAdapter<Brand> productBrandAdapter = new ArrayAdapter<Brand>(this, R.layout.spinner_background, list) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
//                    tv.setTextColor(Color.GRAY);
                    tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
                }

                return view;
            }
        };

        productBrandAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerProductBrand.setAdapter(productBrandAdapter);

        productBrandAdapter.notifyDataSetChanged();

        spinnerProductBrand.setSelection(0);
        spinnerProductBrand.setOnItemSelectedListener(this);

    }


    //    Load Stock All List from Server
    private void getVanStockList(int typeId,int brandId ) {

        setProgressBar(true);

        products.clear();

        final JSONObject object =new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);

            if (brandId!=-1 && typeId!=-1){
                object.put(PRODUCT_TYPE_KEY,typeId);
                object.put(PRODUCT_BRAND_KEY,brandId);
            }else if (brandId==-1 && typeId!=-1){
                object.put(PRODUCT_BRAND_KEY,brandId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        webGetVanStock(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG,"getVanStockList hr   "+response);

                try {

                    if (response.getString("status").equals("success") && !response.isNull("Stocks")) {
                        JSONArray stockArray = response.getJSONArray("Stocks");
                        for (int i = 0; i < stockArray.length(); i++) {

                            JSONObject stockObject = stockArray.getJSONObject(i);
                            Product product = new Product();

                            product.setProductId(stockObject.getInt("product_id"));
                            product.setProductCode(stockObject.getString("product_code"));
                            product.setProductName(stockObject.getString("product_name"));

                            String arb = "";
                            if(!stockObject.isNull("arabic_name"))
                                arb = stockObject.getString("arabic_name");
                            if (TextUtils.isEmpty(arb) || arb.equals("null"))
                                arb = " ";
                            product.setArabicName(arb);


                            product.setBrandName(stockObject.getString("brand_name"));
                            product.setProductType(stockObject.getString("product_type_name"));
                            product.setWholeSalePrice(stockObject.getLong("product_wholesale_price"));
                            product.setRetailPrice(stockObject.getLong("product_mrp"));

                            product.setStockQuantity(stockObject.getInt("stock_quantity"));

                            products.add(product);

                        }

                        float toatalStock = response.getLong("total_stock");
                        int stockQuantity = response.getInt("total_quantity");

                        tvTotalStock.setText(String.valueOf("" + getAmount(toatalStock) + " " + CURRENCY));
                        tvTotalQuantity.setText("" + String.valueOf(stockQuantity));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (products.isEmpty())
                    setErrorView("No Stock","",false);
                else
                    setRecyclerView();

            }

            @Override
            public void onErrorResponse(String error) {

                setErrorView(error,getString(R.string.error_view_retry),true);

            }
        }, object);
    }


    private void setRecyclerView() {

        adapter.notifyDataSetChanged();
        layout.setVisibility(View.VISIBLE);
        setProgressBar(false);
        errorView.setVisibility(View.GONE);

    }

    //set ErrorView
    private void setErrorView(final String title, final String subTitle, boolean isRetry) {

        layout.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        setProgressBar(false);
        errorView.setConfig(ErrorView.Config.create()
                .title(title)
                .subtitle(subTitle)
                .retryVisible(isRetry)
                .build());

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {

                storeVanStockFromServer();
//                getProductTypesList();

            }
        });
    }

    //    Load Stock All List from Server
    private void storeVanStockFromServer() {

        if (!checkConnection()) {
            setErrorView(getString(R.string.no_internet), "", false);
            return;
        }

        if (myDatabase.isExistProducts()) {
            return;
        }

        setProgressBar(true);


        final JSONObject object =new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        products.clear();

        webGetVanStock(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {



//                Log.v(TAG,"storeVanStock   response   "+response);


                try {

                    if (response.getString("status").equals("success") && !response.isNull("Stocks")) {
                        JSONArray stockArray = response.getJSONArray("Stocks");
                        for (int i = 0; i < stockArray.length(); i++) {

                            JSONObject stockObject = stockArray.getJSONObject(i);
                            Product product = new Product();


                            product.setProductId(stockObject.getInt("product_id"));
                            product.setProductCode(stockObject.getString("product_code"));
                            product.setProductName(stockObject.getString("product_name"));


                            product.setProductType(stockObject.getString("product_type_name"));
                            product.setBrandName(stockObject.getString("brand_name"));

                            product.setWholeSalePrice(stockObject.getLong("product_wholesale_price"));
                            product.setRetailPrice(stockObject.getLong("product_mrp"));


                            product.setPiecepercart(stockObject.getInt("piece_per_cart"));

                            product.setStockQuantity(stockObject.getInt("stock_quantity"));

                            products.add(product);

                        }


                        float toatalStock = response.getLong("total_stock");
                        int stockQuantity = response.getInt("total_quantity");

                        tvTotalStock.setText(String.valueOf("" + getAmount(toatalStock) + " " + CURRENCY));
                        tvTotalQuantity.setText("" + String.valueOf(stockQuantity));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (products.isEmpty())
                    setErrorView("No Stock","",false);
                else{
                    setRecyclerView();

                }
            }

            @Override
            public void onErrorResponse(String error) {

                setErrorView(error,getString(R.string.error_view_retry),true);

            }
        }, object);
    }

    //  ProgressBar
    private void setProgressBar(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            spinnerProductBrand.setEnabled(false);
            spinnerProductType.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            spinnerProductBrand.setEnabled(true);
            spinnerProductType.setEnabled(true);


        }

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_vanStock_productType:

                if (position == 1)
                    getVanStockList(-1,-1);


                ProductType type = (ProductType) parent.getSelectedItem();
                setProductBrand(type.getBrands());

                Log.d(TAG, "ProductType  " + type.getTypeName());

                break;
            case R.id.spinner_vanStock_brand:

                ProductType t = (ProductType) spinnerProductType.getSelectedItem();
                Brand b = (Brand) parent.getSelectedItem();

                if (position != 0)
                    getVanStockList(t.getTypeId(), b.getBrandId());

                Log.d(TAG, "Brand  " + b.getBrandName());

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }

}
