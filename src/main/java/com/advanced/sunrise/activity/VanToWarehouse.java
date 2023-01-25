package com.advanced.minhas.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvVanToWarehouseAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.dialog.CartSpinnerDialogNew;
import com.advanced.minhas.dialog.OnSpinerItemClick;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.CartItemCode;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.WareHouse;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;
import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_SALE_TYPE;
import static com.advanced.minhas.webservice.WebService.webVanToWarehouseTransferApprove;


public class VanToWarehouse extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    TextView tvProductSpinner, tv_stock, textview_date, tv_time,tv_totalqty;
    private ArrayList<CartItem> productList = new ArrayList<>();
    private ArrayList<CartItemCode> productCodeList = new ArrayList<>();
    private ArrayList<CartItem> cartItems = new ArrayList<>();
    private ArrayList<String> array_type = new ArrayList<>();
    MyDatabase myDatabase;
    RecyclerView recyclerview_data;
    LinearLayout ly_warehouse;
    private CartItem select_Cart = null;
    private Shop SELECTED_SHOP = null;
    int tot =0;
    int addflag = 0, dialogflag = 0;
    Button button_sales_addCart, button_sales_finish;
    String str_enter_qty="";
    int warehouse_ID ;
    String EXECUTIVE_ID="";
    String dayRegId="";
    String st_transfertype ="";
    EditText EditText_sales_Qty;
    ArrayList<WareHouse> warehouseArray = new ArrayList<>();
    AppCompatSpinner spinner_stocktype,spinner_warehouse;
    RvVanToWarehouseAdapter adapter;
    SessionAuth sessionAuth;
    SessionValue sessionValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_van_to_warehouse);

        textview_date = (TextView)findViewById(R.id.textview_date);
        tv_time = (TextView)findViewById(R.id.tv_time);
        tvProductSpinner = (TextView)findViewById(R.id.textView_sales_item);
        tv_stock = (TextView)findViewById(R.id.tv_stock);
        EditText_sales_Qty = (EditText) findViewById(R.id.EditText_sales_Qty);
        button_sales_addCart = (Button) findViewById(R.id.button_sales_addCart);
        button_sales_finish = (Button) findViewById(R.id.button_sales_finish);
        spinner_stocktype = (AppCompatSpinner) findViewById(R.id.spinner_stocktype);
        recyclerview_data = (RecyclerView) findViewById(R.id.recyclerview_data);
        tv_totalqty = findViewById(R.id.tv_totalqty);
        ly_warehouse = findViewById(R.id.ly_warehouse);
        spinner_warehouse = findViewById(R.id.spinner_warehouse);
        myDatabase = new MyDatabase(this);
        sessionAuth = new SessionAuth(this);
        sessionValue = new SessionValue(this);

        EXECUTIVE_ID =sessionAuth.getExecutiveId();
        dayRegId=sessionValue.getDayRegisterId();

        /////////////////////get_Warehouse();

        try {
            Date c = Calendar.getInstance().getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);

            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            String time = dateFormat.format(c);

            textview_date.setText(""+formattedDate);

            //   tv_executivename.setText("Van Stock of "+sessionAuth.getExecutiveName()+" as on "+time+" "+formattedDate);

        }catch (Exception e){

        }
        try {
          //  st_transfertype = (String) getIntent().getSerializableExtra(TRANSFER_TYPE);
            st_transfertype ="Vantowarehouse";
            if(st_transfertype.equals("Vantowarehouse")){
                ly_warehouse.setVisibility(View.GONE);

            }
            else {
                ly_warehouse.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.getStackTrace();
        }

        array_type.add("Good");
        array_type.add("Damage");

        ArrayAdapter adapter = new ArrayAdapter(VanToWarehouse.this, android.R.layout.simple_spinner_dropdown_item, array_type);
        spinner_stocktype.setAdapter(adapter);

        spinner_stocktype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                tvProductSpinner.setText("");
                tv_stock.setText("");
                EditText_sales_Qty.setText("");

                getVanStockList();
                setRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_warehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(WareHouse w : warehouseArray) {

                    warehouse_ID = warehouseArray.get(position).getWarehouseId();
                }
                Log.e("warehouse_ID sse",""+warehouse_ID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getVanStockList();

       /* if (isNetworkConnected()) {

            get_Warehouse();

        }else {
            Toast.makeText(getApplicationContext(), "Check Connectivity", Toast.LENGTH_SHORT).show();
        }*/

        setRecyclerView();

        button_sales_addCart.setOnClickListener(this);
        button_sales_finish.setOnClickListener(this);

    }

    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }



    private void getVanStockList() {

        /*productList.clear();

            productList.addAll(myDatabase.getAllStock());
         //   productCodeList.addAll(myDatabase.getAllStockCode());

        if (productList.isEmpty())
            Toast.makeText(getApplicationContext(), "No Stock", Toast.LENGTH_SHORT).show();
        else

            for (int i = 0; i < productList.size(); i++) {

                // printLog("gson data", ": "+productList.get(i).getUnitslist());

            }*/

        productList.clear();

        String type = ""+spinner_stocktype.getSelectedItem().toString();

        if (type.equalsIgnoreCase("Good")) {

            Log.e("Good","Stock");

            productList.addAll(myDatabase.getAllStock());
            //   productCodeList.addAll(myDatabase.getAllStockCode());

        }else {

            Log.e("Damage","Stock");
            ////////productList.addAll(myDatabase.getAllDamageStock());
        }

        if (productList.isEmpty())
            Toast.makeText(getApplicationContext(), "No Stock", Toast.LENGTH_SHORT).show();
        else

            setSearchableProductList(productList, productCodeList);
    }


    private void setSearchableProductList(final ArrayList<CartItem> list,  final ArrayList<CartItemCode> listCode) {


        final CartSpinnerDialogNew spinnerCart = new CartSpinnerDialogNew(this, list,listCode, "Select Product", R.style.DialogAnimations_SmileWindow);// With 	Animation

        tvProductSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinnerCart.showSpinerDialog();

            }
        });


        spinnerCart.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(Object item, int position) {

                select_Cart = (CartItem) item;//flagNoExtractUi

                refreshProductType();

                //    refreshProductType();

                tvProductSpinner.setText(""+select_Cart.getProductName());

                //    Log.e("Selected Vat no", "" + SELECTED_SHOP.getVatNumber());

                // ****************************************************************************************

                addflag = 0;
                for (int i = 0; i < cartItems.size(); i++) {
                    if (select_Cart.getProductId() == cartItems.get(i).getProductId()) {

                        addflag = 1;

                    }
                }
                if (addflag == 0) {

                    setCartProduct();


                } else {

                    if (dialogflag == 0) {

                        dialogflag = 1;
                        final AlertDialog alertDialog = new AlertDialog.Builder(VanToWarehouse.this).create();
                        alertDialog.setMessage("Product already added ! Please edit from list if needed");

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed

                                dialogflag = 0;
                                alertDialog.dismiss();
                                refreshProductType();
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();

                    }

                }


            }
        });
        /*code Adapter*/
//        setCartProgressBar(false);
//        lytCart.setVisibility(View.VISIBLE);

    }


//    private void get_Warehouse(){
//
//        //  Log.e("get Settings","response Banks calling 1");
//        String st_pref_exec_warehouseid = sessionAuth.getexecutivewarehouseid();
//        Log.e("pref_exec_warehouseid",st_pref_exec_warehouseid);
//
//        warehouseArray.clear();
//        final ProgressDialog pd = ProgressDialog.show(VanToWarehouse.this, null, "Please wait...", false, false);
//        webGetVanWareHouseList(new WebService.webObjectCallback() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                Log.e("warehouse response", ":"+response);
//
//                try {
//
//                    if (response.getString("status").equalsIgnoreCase("success")){
//
//                        JSONArray jsonArray = response.getJSONArray("warehouse");
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                            WareHouse warehouse = new WareHouse();
//                            warehouse.setWarehouseId(jsonObject.getInt("id"));
//                            warehouse.setWarehouseName(jsonObject.getString("name"));
//                            if(warehouse.getWarehouseId()!=Integer.parseInt(sessionAuth.getexecutivewarehouseid())) {
//
//                                warehouseArray.add(warehouse);
//                            }
//                        }
//                    }
//
//                    pd.dismiss();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                pd.dismiss();
//                if (warehouseArray.size()>0) {
//
//                    setWareHouse(warehouseArray);
//                }else {
//                    Toast.makeText(getApplicationContext(), "Error getting warehouse", Toast.LENGTH_SHORT).show();
//                    setWareHouse(warehouseArray);
//
//                }
//            }
//            @Override
//            public void onErrorResponse(String error) {
//                //  showSnackBar("Error getting settings controls");
//                pd.dismiss();
//
//            }
//        });
//    }


    private void setWareHouse(ArrayList<WareHouse> list) {

        // Initializing an ArrayAdapter
        final ArrayAdapter<WareHouse> productBrandAdapter = new ArrayAdapter<WareHouse>(this, R.layout.spinner_brand_background, list) {

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                return view;
            }
        };

        productBrandAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinner_warehouse.setAdapter(productBrandAdapter);

        productBrandAdapter.notifyDataSetChanged();
        //  spinnerBrand.setSelection(0);
        spinner_warehouse.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_stocktype:

                getVanStockList();


                break;
            case R.id.spinner_warehouse:

                for(WareHouse w : warehouseArray) {

                    warehouse_ID = warehouseArray.get(position).getWarehouseId();
                }
                Log.e("warehouse_ID sse",""+warehouse_ID);


                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void refreshProductType() {

        tvProductSpinner.setText("");
        tv_stock.setText("");
        EditText_sales_Qty.setText("");
        str_enter_qty = "";

        hideSoftKeyboard();

    }


    private void setCartProduct() {

        if (select_Cart == null)
            return;

        tv_stock.setText(""+select_Cart.getStockQuantity());

    }


    private void setRecyclerView() {


        adapter = new RvVanToWarehouseAdapter(productList, new OnNotifyListener() {
            @Override
            public void onNotified() {
                tot = adapter.get_total_quantity();
                tv_totalqty.setText("Total Qty : "+tot);
//                arr_denom_afteredit = denom_adapter.get_denomination_array();
//                tv_totalqty.setText(""+tot);
                Log.e("total",""+tot);
//                Log.e("arr_denom_afteredit",""+arr_denom_afteredit.get(0).getDenom_total());


            }
        });

        try {

            recyclerview_data.setHasFixedSize(true);
            recyclerview_data.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getApplicationContext())
                    .showLastDivider()
                    .build());
            recyclerview_data.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerview_data.setAdapter(adapter);

        }catch (Exception e){

        }
        // adapter = new RvVanToWarehouseAdapter(cartItems);

//        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
//        itemAnimator.setAddDuration(1000);
//        itemAnimator.setRemoveDuration(1000);
//        recyclerview_data.setHasFixedSize(true);
//        recyclerview_data.setItemAnimator(itemAnimator);
//
//        //        Item Divider in recyclerView
//        recyclerview_data.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
//                .showLastDivider()
////                .color(getResources().getColor(R.color.divider))
//                .build());
//
//        recyclerview_data.setLayoutManager(new LinearLayoutManager(this));
//
//        recyclerview_data.setAdapter(adapter);
////        adapter.changeItem(cartItems);
//        adapter.notifyDataSetChanged();

    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_sales_addCart:

                str_enter_qty = EditText_sales_Qty.getText().toString().trim();
                if (!str_enter_qty.isEmpty()&&Float.parseFloat(str_enter_qty)!=0) {
                    if (Float.parseFloat(str_enter_qty) > select_Cart.getStockQuantity()) {
                        Toast.makeText(getApplicationContext(), "Invalid Quantity !", Toast.LENGTH_SHORT).show();
                    } else {

                        spinner_stocktype.setEnabled(false);

                        CartItem cart = select_Cart;

                        Log.e("pro name","//"+select_Cart.getProductName());

                        /*cart.setProductName(select_Cart.getProductName());
                        cart.setStockQuantity(select_Cart.getStockQuantity());*/
                        cart.setPieceQuantity(Integer.parseInt(str_enter_qty));
                        cart.setReturnQuantity(Integer.parseInt(str_enter_qty));

                        // adapter.changeItem(cart);

                        refreshProductType();

                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Invalid Quantity !", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.button_sales_finish:

                if (validateOrder()){
                    if(st_transfertype.equals("Vantowarehouse")) {
                        ApproveStockTransfer();
                    }
                    else{
                        /////////////Approve_vantovan_stocktransfer();
                    }
                }

                break;
        }
    }

//    private void Approve_vantovan_stocktransfer() {
//        Log.e("total",""+tv_totalqty.getText().toString());
//
//        JSONObject object = new JSONObject();
//        try {
//
//            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
//            object.put(DAY_REGISTER_KEY, dayRegId);
//            object.put("to_warehouse", ""+warehouse_ID);
//            object.put("stock_type", ""+spinner_stocktype.getSelectedItem().toString());
//
//            JSONArray array = new JSONArray();
//
//            for (int i = 0; i < adapter.getCartItems().size(); i++) {
//
//                JSONObject objt = new JSONObject();
//                if(adapter.getCartItems().get(i).getReturnQuantity()!=0) {
//                    objt.put("id", "" + adapter.getCartItems().get(i).getProductId());
//                    objt.put("movementQty", "" + adapter.getCartItems().get(i).getReturnQuantity());
//                    array.put(objt);
//                }
//            }
//
//            object.put("product_list", array);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.e("stock Movement object", ""+object.toString());
//
//        final ProgressDialog pd = ProgressDialog.show(VanToWarehouse.this, null, "Please wait...", false, false);
//
//        webVanToVanTransferApprove(new WebService.webObjectCallback() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                Log.e("Response", ""+response.toString());
//
//                try {
//
//                    if (response.getString("status").equalsIgnoreCase("Success")){
//
//                        /*for (CartItem c : adapter.getCartItems()) {
//
//                            Log.e("Product Type", ""+c.getProductId());
//
//                            myDatabase.updateStock(c, REQ_SALE_TYPE);
//                        }
//*/
//
//                        String type = ""+spinner_stocktype.getSelectedItem().toString();
//
//                        if (type.equalsIgnoreCase("Good")) {
//
//                            for (CartItem c : adapter.getCartItems()) {
//
//                                Log.e("Product Type if", "" + type);
//
//                                myDatabase.updateStock(c, REQ_SALE_TYPE);
//                            }
//                        }else
//                        {
//                            for (CartItem c : adapter.getCartItems()) {
//
//                                Log.e("Product Type else", "" + type);
//
//                                myDatabase.updateDamageReturnStock(c);
//                            }
//                        }
//
//
//                        Toast.makeText(VanToWarehouse.this ,"Stock Transfer Success", Toast.LENGTH_SHORT).show();
//                        pd.dismiss();
//
//
//                        Intent intent = new Intent(VanToWarehouse.this, HomeActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }else {
//                        pd.dismiss();
//                        Toast.makeText(VanToWarehouse.this ,"Error Stock transfer", Toast.LENGTH_SHORT).show();
//                    }
//
//
//
//                } catch (JSONException e) {
//                    Toast.makeText(VanToWarehouse.this, "Error Approving Stock transfer.!", Toast.LENGTH_SHORT).show();
//                    pd.dismiss();
//                }
//
//                pd.dismiss();
//            }
//
//            @Override
//            public void onErrorResponse(String error) {
//
//                pd.dismiss();
//
//                Toast.makeText(VanToWarehouse.this, error, Toast.LENGTH_SHORT).show();
//            }
//        }, object);
//
//    }

    private void ApproveStockTransfer() {
        Log.e("total",""+tv_totalqty.getText().toString());

        JSONObject object = new JSONObject();
        try {

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            // object.put("warehouse_id", ""+warehouse_ID);
            object.put("stock_type", ""+spinner_stocktype.getSelectedItem().toString());

            JSONArray array = new JSONArray();

            for (int i = 0; i < adapter.getCartItems().size(); i++) {

                JSONObject objt = new JSONObject();
                if(adapter.getCartItems().get(i).getReturnQuantity()!=0) {
                    objt.put("id", "" + adapter.getCartItems().get(i).getProductId());
                    objt.put("movementQty", "" + adapter.getCartItems().get(i).getReturnQuantity());
                    array.put(objt);
                }
            }

            object.put("product_list", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("stock Movement object", ""+object.toString());

        final ProgressDialog pd = ProgressDialog.show(VanToWarehouse.this, null, "Please wait...", false, false);

        webVanToWarehouseTransferApprove(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", ""+response.toString());

                try {

                    if (response.getString("result").equalsIgnoreCase("Success")){

                        /*for (CartItem c : adapter.getCartItems()) {

                            Log.e("Product Type", ""+c.getProductId());

                            myDatabase.updateStock(c, REQ_SALE_TYPE);
                        }
*/

                        String type = ""+spinner_stocktype.getSelectedItem().toString();

                        if (type.equalsIgnoreCase("Good")) {

                            for (CartItem c : adapter.getCartItems()) {

                                Log.e("Product Type if", "" + type);

                                myDatabase.updateStock(c, REQ_SALE_TYPE);
                            }
                        }else
                        {
                            for (CartItem c : adapter.getCartItems()) {

                                Log.e("Product Type else", "" + type);

                               /////////////// myDatabase.updateDamageReturnStock(c);
                            }
                        }


                        Toast.makeText(VanToWarehouse.this ,"Stock Transfer Success", Toast.LENGTH_SHORT).show();
                        pd.dismiss();


                        Intent intent = new Intent(VanToWarehouse.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        pd.dismiss();
                        Toast.makeText(VanToWarehouse.this ,"Error Stock transfer", Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    Toast.makeText(VanToWarehouse.this, "Error Approving Stock transfer.!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();

                Toast.makeText(VanToWarehouse.this, error, Toast.LENGTH_SHORT).show();
            }
        }, object);

    }



    private boolean validateOrder() {

        boolean status = false;

        if (adapter.getCartItems().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Cart is Empty", Toast.LENGTH_SHORT).show();
            status = false;
        }else if(!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), "Check Connectivity", Toast.LENGTH_SHORT).show();
            status = false;
        }
        else if (tot==0){
            Toast.makeText(getApplicationContext(), "Enter Quantity", Toast.LENGTH_SHORT).show();
            status = false;
        }
        else {
            status = true;
        }


        return status;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
