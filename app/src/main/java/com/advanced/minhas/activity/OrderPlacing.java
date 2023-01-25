package com.advanced.minhas.activity;

import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.webservice.WebService.websave_saleorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.OrderPlacingAdapter;
import com.advanced.minhas.dialog.CustomerSpinnerDialog;
import com.advanced.minhas.dialog.OnSpinerItemClick;
import com.advanced.minhas.dialog.ProddaysSpinnerDialog;
import com.advanced.minhas.dialog.RouteSpinnerDialog;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.OrderItems;
import com.advanced.minhas.model.Orderplace;
import com.advanced.minhas.model.ProductionDays;
import com.advanced.minhas.model.Route;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderPlacing extends AppCompatActivity implements View.OnClickListener {
    private MyDatabase myDatabase;
    TextView text_customer_spinner, text_spinner_route, tv_totaltray, text_spinner_productiondays;
    String TAG = "orderplace";
    RecyclerView recyclerView;
    OrderPlacingAdapter adapter;
    ArrayList<Shop> list = new ArrayList<>();
    private ArrayList<Orderplace> routelist_cart = new ArrayList<>();
    private ArrayList<OrderItems> productList = new ArrayList<>();
    private ArrayList<OrderItems> productListCart = new ArrayList<>();
    private ArrayList<Route> routlist = new ArrayList<>();
    private ArrayList<ProductionDays> prod_dayslist = new ArrayList<>();
    private SessionValue sessionValue;
    private SessionAuth sessionAuth;
    SearchView searchView_product;
    Shop select_Customer = null;
    private String EXECUTIVE_ID = "";
    Route select_route = null;
    ProductionDays prod_days = null;
    FloatingActionButton button_cart;
    Button button_adtocart, button_save;
    String add_status = "N", str_cust_id = "", strDate = "", str_cust_name = "", str_rout_id = "", str_rout_name = "",
            str_prod_daysid = "", str_prod_days = "" ,search_view_text ="";
    ImageButton button_back, button_history;
    int order_count = 0;
    private ArrayList<OrderItems> productList_order = new ArrayList<>();
    String st_editflag = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placing);
        sessionValue = new SessionValue(getApplicationContext());
        sessionAuth = new SessionAuth(getApplicationContext());
        text_customer_spinner = findViewById(R.id.spinner_customer);
        text_spinner_route = findViewById(R.id.spinner_route);
        tv_totaltray = findViewById(R.id.tv_totaltray);
        recyclerView = findViewById(R.id.recycler_products);
        button_cart = findViewById(R.id.button_cart);
        button_save = findViewById(R.id.button_save);
        button_adtocart = findViewById(R.id.button_addtocart);
        button_back = findViewById(R.id.imageButton_toolbar_back);
        button_history = findViewById(R.id.imageButton_toolbar_history);
        text_spinner_productiondays = findViewById(R.id.spinner_productiondays);
        searchView_product = findViewById(R.id.searchView_product);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        myDatabase = new MyDatabase(this);
        EXECUTIVE_ID = new SessionAuth(getApplicationContext()).getExecutiveId();
        try {

            str_cust_name = getIntent().getExtras().getString("customer");
            str_cust_id = getIntent().getExtras().getString("customerid");
            str_prod_days = getIntent().getExtras().getString("production_days");

            Log.e("str_prod_days ", "received//" + str_prod_days);

        } catch (Exception e) {

        }
        try {
            setrecyclerview();
        }catch (Exception e){

        }

        try {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });
        }catch (Exception e){

        }

        searchView_product.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_view_text =newText;

                adapter.getFilter().filter(newText);
                return true;

            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrderPlacing.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });
        button_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(OrderPlacing.this, OrderHistory.class);
//                startActivity(intent);
                // finish();
            }
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int custid = TextUtils.isEmpty(str_cust_id.trim()) ? 0 : Integer.valueOf(str_cust_id.trim());
                try {
                    Date d = new Date();
                    String dateWithoutTime = d.toString().substring(0, 10);
                    //SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat currentDate = new SimpleDateFormat("yyyy/MM/dd");
                    Date todayDate = new Date();
                    String thisDate = currentDate.format(todayDate);
                    strDate = thisDate;
                } catch (Exception e) {

                }
                if(search_view_text.equals("")) {


                    if(order_count>0) {
                        save_order();
                    }
                    else{
                        Log.e("order_count",""+order_count);
                        Log.e("szee",""+productList_order.size());

                        Toast.makeText(getApplicationContext(),"Order Atleast One Product..!",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Clear Search Area",Toast.LENGTH_LONG).show();
                }
//                if (custid > 0) {
//                    save_order();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Select customer", Toast.LENGTH_LONG).show();
//                }
            }
        });


        button_cart.setOnClickListener((View.OnClickListener) this);
        button_adtocart.setOnClickListener(this);

    }

    private void setrecyclerview() {

        try {

            productList.clear();
            productList_order.clear();
            recyclerView.setAdapter(null);
            productList.addAll(myDatabase.getOrderPlaceProducts());



            adapter = new OrderPlacingAdapter(productList, new OnNotifyListener() {
                @Override
                public void onNotified() {

                    try {
                         order_count = adapter.get_total_count();

                        productList_order = adapter.getOrderItems();
                    } catch (Exception e) {

                    }
                }
            });

            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(OrderPlacing.this)
                    .showLastDivider()
                    .build());
            recyclerView.setLayoutManager(new LinearLayoutManager(OrderPlacing.this));
            recyclerView.setAdapter(adapter);


            //productList.clear();
            //recyclerView.setAdapter(null);





        } catch (Exception e) {

        }

    }

    private void save_order() {

        final ProgressDialog pd = ProgressDialog.show(OrderPlacing.this, null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray saleorderArray = new JSONArray();

        try {

            for (int i = 0; i <= 0; i++) {

                final JSONObject saleObj = new JSONObject();

                saleObj.put(CUSTOMER_KEY, str_cust_id);
                saleObj.put("order_date", strDate);

                JSONArray cartArray = new JSONArray();
                for (OrderItems o : productList_order) {


                    final JSONObject Obj = new JSONObject();
                    if (o.getTotal_req_products() > 0) {
                        Log.e("getTotal_req_products",""+o.getTotal_req_products());
                        Log.e("getProductname",""+o.getProductname());

                        Obj.put("product_id", o.getProductid());
                        Obj.put("product_quantity", o.getTotal_req_products());
                        cartArray.put(Obj);

                    }

                }
                saleObj.put("order_products", cartArray);
                saleorderArray.put(saleObj);
            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("day_register_id", sessionValue.getDayRegisterId());
            object.put("route_id", sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID));
            object.put("Order", saleorderArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "saleorder  Object" + object);

        websave_saleorder(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "saleorder  response   " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        Intent i = new Intent(OrderPlacing.this,HomeActivity.class);
                        startActivity(i);
                        finish();


                    } else {


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        }, object);
    }


    private void setSearchableProductList(final ArrayList<Shop> list) {


        final CustomerSpinnerDialog spinnerCart = new CustomerSpinnerDialog(this, list, "Select Customer", R.style.DialogAnimations_SmileWindow);// With 	Animation
        final RouteSpinnerDialog routeCart = new RouteSpinnerDialog(this, routlist, "Select Route", R.style.DialogAnimations_SmileWindow);// With 	Animation
        final ProddaysSpinnerDialog prod_daysCart = new ProddaysSpinnerDialog(this, prod_dayslist, "Select Production Days", R.style.DialogAnimations_SmileWindow);// With 	Animation

        text_customer_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinnerCart.showSpinnerDialog();

            }
        });

        text_spinner_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                routeCart.showSpinerDialog();

            }
        });

        text_spinner_productiondays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prod_daysCart.showSpinerDialog();

            }
        });


        spinnerCart.bindOnSpinnerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(Object item, int position) {

                select_Customer = (Shop) item;//flagNoExtractUi

                str_cust_id = "" + select_Customer.getShopId(); //select_Customer.getShopCode();
                str_cust_name = "" + select_Customer.getShopName();

                text_customer_spinner.setText(str_cust_name);

                // Log.e("Selected Shop", "" + str_cust_id);

            }
        });


        routeCart.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(Object item, int position) {

                select_route = (Route) item;//flagNoExtractUi

                str_rout_id = "" + select_route.getId(); //select_Customer.getShopCode();
                str_rout_name = "" + select_route.getRoute();

                text_spinner_route.setText(str_rout_name);

                //  Log.e("Selected Route", "" + str_rout_id);

            }
        });

        prod_daysCart.bindOnSpinnerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(Object item, int position) {

                prod_days = (ProductionDays) item;//flagNoExtractUi

                str_prod_daysid = "" + prod_days.getId(); //prod_days.ge();
                str_prod_days = "" + prod_days.getName();

                text_spinner_productiondays.setText(str_prod_days);

                //Log.e(" selected day ", "" + str_prod_days);

            }
        });
        text_spinner_route.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("reached sprte 1", "ok");


                recyclerView.setAdapter(null);
                prod_dayslist.clear();
                //haris added 03-03-2021
                try {
                    ArrayList<ProductionDays> temp_prod_days = new ArrayList<>();
                    //    temp_prod_days = myDatabase.getAllProductiondays();

                    for (ProductionDays p : temp_prod_days) {
                        //   Log.e("routes", p.getName());
                        prod_dayslist.add(p);
                    }
//                    Route rt=new Route();

                } catch (Exception e) {

                }
                text_spinner_productiondays.setText("Select Production Days");
                str_prod_days = "";
                str_prod_daysid = "";
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(OrderPlacing.this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onClick(View v) {

    }
}
