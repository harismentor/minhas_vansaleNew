package com.advanced.minhas.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvShopAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CustomerProduct;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.VIEW_ERRORVIEW;
import static com.advanced.minhas.config.ConfigKey.VIEW_PROGRESSBAR;
import static com.advanced.minhas.config.ConfigKey.VIEW_RECYCLERVIEW;
import static com.advanced.minhas.webservice.WebService.webAllRouteShop;

public class ShopSearchFragment extends Fragment implements View.OnClickListener{




    String TAG = "ShopSearchFragment";
    private ArrayList<Shop> shops = new ArrayList<>();


    private SessionValue sessionValue;
    private SessionAuth sessionAuth;
    private RvShopAdapter shopAdapter;
    private ImageButton ibRefresh,ibBack;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ErrorView errorView;
    private SearchView searchView;
    private MyDatabase myDatabase;
    private String EXECUTIVE_ID = "";

    private String mParam1;
    private String mParam2;


    public static ShopSearchFragment newInstance(String param1, String param2) {
        ShopSearchFragment fragment = new ShopSearchFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_shop_search, container, false);


        searchView = (SearchView) view.findViewById(R.id.searchView_shopList);
        ibBack=(ImageButton)view.findViewById(R.id.imageButton_shop_search_back);
        ibRefresh=(ImageButton)view.findViewById(R.id.imageButton_shop_search_refresh);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        errorView = (ErrorView) view.findViewById(R.id.errorView);

        sessionValue =new SessionValue(getContext());
        sessionAuth =new SessionAuth(getContext());


        myDatabase=new MyDatabase(getContext());

        shopAdapter = new RvShopAdapter( shops);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(shopAdapter);


        try {
            EXECUTIVE_ID = sessionAuth.getExecutiveId();
        } catch (Exception e) {
            e.getMessage();
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                shopAdapter.getFilter().filter(newText);
                return true;

            }
        });

        ibBack.setOnClickListener(this);
        ibRefresh.setOnClickListener(this);

        setRecyclerView();


        return view;
    }

    //get Shops
    private void getAllRouteCustomer() {


        if (!checkConnection()) {
            setErrorView(getContext().getString(R.string.no_internet), "", false);
            return;
        }

        updateViews(VIEW_PROGRESSBAR);

        JSONObject object = new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "getAllRouteCustomer  object " + object);

        final ArrayList<Shop> list = new ArrayList<>();
        list.clear();
        webAllRouteShop(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {


                Log.v(TAG, "getAllRouteCustomer  response " + response);

                try {

                    if (response.getString("status").equals("success") && !response.isNull("Customers")) {


                        JSONArray array = response.getJSONArray("Customers");
                        new AsyncTaskLoadData().execute(array);

                    }


                } catch (JSONException e) {


                    e.getMessage();
                }





            }

            @Override
            public void onErrorResponse(String error) {


                setErrorView(error, getString(R.string.error_subtitle_failed_one_more_time), true);
            }
        }, object);


    }




    private void setRecyclerView() {

        ArrayList<Shop> list=myDatabase.getUnRegisteredCustomers();
        if (list.isEmpty()) {
            setErrorView("No Shops", "", false);
            return;
        }


            shops.clear();
            shops.addAll(list);
            shopAdapter.notifyDataSetChanged();
            updateViews(VIEW_RECYCLERVIEW);

    }



    //set ErrorView
    private void setErrorView(final String title, final String subTitle, boolean isRetry) {

        updateViews(VIEW_ERRORVIEW);
        errorView.setConfig(ErrorView.Config.create()
                .title(title)
                .subtitle(subTitle)
                .retryVisible(isRetry)
                .build());


        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {

                getAllRouteCustomer();

            }
        });
    }




    public void updateViews(int viewCode) {



        switch (viewCode) {
            case VIEW_PROGRESSBAR:
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                break;
            case VIEW_RECYCLERVIEW:
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);

                break;

            case VIEW_ERRORVIEW:
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);

                break;


        }


    }




    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskLoadData extends AsyncTask<JSONArray, String, String> {
        private final static String TAG = "AsyncTaskLoadImage";

        @Override
        protected String doInBackground(JSONArray... jsonArrays) {


            JSONArray array=jsonArrays[0];
            try {

                for (int i = 0; i < array.length(); i++) {

                    JSONObject shopObj = array.getJSONObject(i);


                    Log.v(TAG, "getAllRouteCustomer  response " + shopObj);

                    Shop shop = new Shop();


                    int custId=shopObj.getInt("id");

                    shop.setShopId(custId);
                    shop.setShopCode(shopObj.getString("shope_code"));
                    shop.setShopName(shopObj.getString("name"));
                    shop.setShopArabicName(shopObj.getString("arabic_name"));
                    shop.setShopMail(shopObj.getString("email"));
                    shop.setShopMobile(shopObj.getString("mobile"));
                    shop.setShopAddress(shopObj.getString("address"));
                    shop.setVatNumber(shopObj.getString("vat_no"));
                    shop.setLatitude(shopObj.getString("latitude"));
                    shop.setLongitude(shopObj.getString("longitude"));
                    shop.setRouteCode(shopObj.getString("route_code"));
                    shop.setCreditLimit((float) shopObj.getDouble("credit_limit"));
                    shop.setCredit((float) shopObj.getDouble("credit"));
                    shop.setDebit((float) shopObj.getDouble("debit"));
                    shop.setOpeningbalance((float) shopObj.getDouble("opening_balance"));
                    shop.setPlace_ofsupply( shopObj.getString("place_of_supply"));
                    shop.setOutStandingBalance((float) shopObj.getDouble("balance"));

                    if(shop.getCreditLimit()==0) {
                        shop.setCreditlimit_register("0");
                        Log.e("if",""+shop.getCreditLimit());
                    }
                    else{
                       // shop.setCreditlimit_register("1");
                        shop.setCreditlimit_register(""+shop.getCreditLimit());
                        Log.e("else",""+shop.getCreditLimit());
                    }

                    final ArrayList<CustomerProduct> selling_list = new ArrayList<>();

                    JSONArray arrayproductlist = shopObj.getJSONArray("customer_product_list");

                    if (arrayproductlist.length()>0) {

                        for (int k = 0; k < arrayproductlist.length(); k++) {

                            JSONObject productobj = arrayproductlist.getJSONObject(k);
                            CustomerProduct sellingPrice = new CustomerProduct();

                            sellingPrice.setProductId(productobj.getInt("product_id"));
                            sellingPrice.setCustomerId(custId);

                            Log.e("Selling price object", ""+productobj.getString("product_selling_rate"));
                            sellingPrice.setPrice(Float.parseFloat(productobj.getString("product_selling_rate")));

                            Log.e("Selling Price", ""+sellingPrice.getProductId()+"/"+sellingPrice.getPrice());
                            selling_list.add(sellingPrice);

                        }
                    }

                    shop.setProducts(selling_list);
                    shop.setVisit(true);

                    boolean b=myDatabase.insertUnRegisteredCustomer(shop);

               /*     ArrayList<Invoice> invoices = new ArrayList<>();

                    if (!shopObj.isNull("receipt")) {


                        JSONArray invoiceArray = shopObj.getJSONArray("receipt");


                        for (int j = 0; j < invoiceArray.length(); j++) {
                            JSONObject invObj = invoiceArray.getJSONObject(j);

                            Invoice inv = new Invoice();
                            inv.setInvoiceId(invObj.getString("sale_id"));
                            inv.setInvoiceNo(invObj.getString("invoice_no"));
                            inv.setTotalAmount((float) invObj.getDouble("total"));
                            inv.setBalanceAmount((float) invObj.getDouble("balance"));
                            invoices.add(inv);

                        }

                        shop.setInvoices(invoices);


                    }*/
                }

            } catch (JSONException e) {

                e.getMessage();

            }

            return "";
        }

        @Override
        protected void onPreExecute() {
            updateViews(VIEW_PROGRESSBAR);
            /*
            //show progress dialog while image is loading
            progress = new ProgressDialog(getContext());
            progress.setMessage("Please Wait....");
            progress.setCancelable(false);
            progress.show();
            */
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRecyclerView();

                }
            }, 500);

        }
    }

    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imageButton_shop_search_refresh:

                getAllRouteCustomer();

            break;
           case  R.id.imageButton_shop_search_back:
            getActivity().onBackPressed();

            break;

        }

    }


}
