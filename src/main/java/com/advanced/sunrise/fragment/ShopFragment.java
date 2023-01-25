package com.advanced.minhas.fragment;

import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.PREF_KEY_USER_NAME;
import static com.advanced.minhas.config.ConfigKey.REQ_ANY_TYPE;
import static com.advanced.minhas.config.ConfigKey.REQ_BANK_DETAILS;
import static com.advanced.minhas.config.ConfigKey.REQ_DISTRICT;
import static com.advanced.minhas.config.ConfigKey.REQ_EXPENSE;
import static com.advanced.minhas.config.ConfigKey.REQ_STATE;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.localdb.MyDatabase.getDateTime;
import static com.advanced.minhas.webservice.WebService.webAllExpense;
import static com.advanced.minhas.webservice.WebService.webAllRouteShop;
import static com.advanced.minhas.webservice.WebService.webChangePassword;
import static com.advanced.minhas.webservice.WebService.webGetBanks;
import static com.advanced.minhas.webservice.WebService.webGetVanMasterStock;
import static com.advanced.minhas.webservice.WebService.webGetVanStock;
import static com.advanced.minhas.webservice.WebService.webGroupList;
import static com.advanced.minhas.webservice.WebService.webGroupRegister;
import static com.advanced.minhas.webservice.WebService.webPaidReceipt;
import static com.advanced.minhas.webservice.WebService.webPlaceOrder;
import static com.advanced.minhas.webservice.WebService.webRegisteredShop;
import static com.advanced.minhas.webservice.WebService.webReturn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.DataServiceOperations;
import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvShopAdapter;
import com.advanced.minhas.config.ConfigKey;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.dialog.DayCloseDialog;
import com.advanced.minhas.dialog.DayRegisterDialog;
import com.advanced.minhas.listener.OnDayCloseListener;
import com.advanced.minhas.listener.OnInputKiloMeterListener;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Banks;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.CustomerProduct;
import com.advanced.minhas.model.District;
import com.advanced.minhas.model.Expense;
import com.advanced.minhas.model.MfgDate;
import com.advanced.minhas.model.Product;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Route;
import com.advanced.minhas.model.RouteGroup;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Size;
import com.advanced.minhas.model.SizelistMasterstock;
import com.advanced.minhas.model.State;
import com.advanced.minhas.model.Taxes;
import com.advanced.minhas.model.Units;
import com.advanced.minhas.model.Vehicle;
import com.advanced.minhas.model.chequeReceipt;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.textwatcher.TextValidator;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.google.gson.Gson;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShopFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    OnNotifyListener mListener;

    String TAG = "ShopFragment" , str_Latitude = "0", str_Longitude = "0" ,str_beet_selected =" ",str_route_selected="";
    private String provider;
    boolean isRegistered = false;
    private ArrayList<Shop> shops = new ArrayList<>();
    private ImageButton ibMore;
    private AppCompatSpinner spinnerRoute, spinnerGroup;
    private String dayRegId;
    private SearchView searchView;
    ArrayList<Sales> saleList =new ArrayList<>();

    ArrayList<Sales> quotationList = new ArrayList<>();
    ArrayList<Sales> returnList = new ArrayList<>();
    ArrayList<Receipt> receipts  =new ArrayList<>();
    ArrayList<chequeReceipt> chequereceipts  =new ArrayList<>();
    private SwitchCompat switchPrintType;

    private Button btnRegister, btnDayClose;
    private SessionValue sessionValue;
    private SessionAuth sessionAuth;
    private RvShopAdapter shopAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ErrorView errorView;
    private MyDatabase myDatabase;
    private String EXECUTIVE_ID = "";
    String gsonvaluemaster_size = "";
    private ArrayList<Banks> banks = new ArrayList<Banks>();
    ArrayList<Expense> array_expense = new ArrayList<Expense>();
    LocationManager locationManager;
    private android.support.v7.app.AlertDialog createDialog;
    private ImageButton ib_syncbtn;
    LinearLayout ly_sync;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        spinnerRoute = (AppCompatSpinner) view.findViewById(R.id.spinner_home_route);
        spinnerGroup = (AppCompatSpinner) view.findViewById(R.id.spinner_home_group);
        searchView = (SearchView) view.findViewById(R.id.searchView_shopList);
        btnRegister = (Button) view.findViewById(R.id.button_home_register);
        ibMore = (ImageButton) view.findViewById(R.id.imageButton_home_menu);

        btnDayClose = (Button) view.findViewById(R.id.button_day_close);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        errorView = (ErrorView) view.findViewById(R.id.errorView);
        ib_syncbtn = view.findViewById(R.id.ib_syncbtn);
        ly_sync = view.findViewById(R.id.ly_sync);

        switchPrintType = (SwitchCompat) view.findViewById(R.id.switch_print_mode);  //custom layout set navigation item

        myDatabase = new MyDatabase(getContext());
        sessionValue =new SessionValue(getContext());
        sessionAuth =new SessionAuth(getContext());

        shopAdapter = new RvShopAdapter(shops);

        switchPrintType.setChecked(sessionValue.isPOSPrint());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(shopAdapter);

        Log.e("Monthly bonus pref", ""+sessionAuth.getMonthlyBonus());

        try {
            EXECUTIVE_ID = sessionAuth.getExecutiveId();
        } catch (Exception e) {
            e.getMessage();
        }
        check_permission();
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
        if (sessionValue.isGroupRegister()) {
            ly_sync.setVisibility(View.VISIBLE);
        }
        else{
            ly_sync.setVisibility(View.GONE);
        }

        btnRegister.setOnClickListener(this);
        ibMore.setOnClickListener(this);
        ib_syncbtn.setOnClickListener(this);
        btnDayClose.setOnClickListener(this);


        if (sessionValue.isPOSPrint())
            switchPrintType.setText(getContext().getText(R.string.pos_print));
        else
            switchPrintType.setText(getContext().getText(R.string.a4_print));

        switchPrintType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                sessionValue.setPOSPrint(isChecked);


                if (isChecked)
                    switchPrintType.setText(getContext().getText(R.string.pos_print));
                else
                    switchPrintType.setText(getContext().getText(R.string.a4_print));

            }
        });


        enableViews();
        return view;
    }
    private void check_permission() {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Requires Permission if", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                LocationListener mlocListener = new MyLocationListener();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 61000, 250,
                        mlocListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
            }
        }catch (Exception e){

        }
    }

    private void getGroupAndRoutList() {

        if (!checkConnection()) {
            setErrorView(getContext().getString(R.string.no_internet), "", false);
            return;
        }

        setProgressBar(true);

        final ArrayList<RouteGroup> groups = new ArrayList<>();
        groups.clear();

        RouteGroup g = new RouteGroup();
        g.setGroupName("SELECT GROUP ");
        g.setGroupId(-1);
        groups.add(0, g);

        final ArrayList<Route> routs = new ArrayList<>();
        routs.clear();

        Route route = new Route();
        route.setRoute("SELECT ROUTE ");
        route.setId(-1);
        routs.add(0, route);

        final JSONObject object = new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        webGroupList(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("get groups",""+object);

                try {
                    Log.e("get groups resp",""+response);
                    if (response.getString("status").equals("success")) {

//                        groups
                        if (!response.isNull("Groups")) {
                            JSONArray array = response.getJSONArray("Groups");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject groupObj = array.getJSONObject(i);
                                RouteGroup g = new RouteGroup();
                                g.setGroupName(groupObj.getString("name"));
                                g.setGroupId(groupObj.getInt("id"));
                                Log.e("grpnme hr",""+g.getGroupName());
                                groups.add(g);
                                myDatabase.insertgroups(g);
                            }

                        }

                        //           groups

                        if (!response.isNull("Routes")) {
                            JSONArray array = response.getJSONArray("Routes");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject groupObj = array.getJSONObject(i);
                                Route r = new Route();
                                r.setRoute(groupObj.getString("name"));
                                r.setId(groupObj.getInt("id"));
                                Log.e("route hr",""+r.getRoute());
                                routs.add(r);

                            }
                        }
                        for (Route s : routs) {
                            boolean b = myDatabase.insertRoute(s);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (groups.isEmpty() && groups.isEmpty())
                    setErrorView("No Groups", "", false);
                else
                    setRouteSpinner(groups, routs);
            }

            @Override
            public void onErrorResponse(String error) {

                setErrorView(error, getString(R.string.error_subtitle_failed_one_more_time), true);
                setRouteSpinner(groups, routs);
            }
        }, object);
    }

    private void setLocalGroups() {

        final ArrayList<Route> routs = new ArrayList<>();
        Route route = new Route();
        route.setId(Integer.parseInt(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID)));
        route.setRoute(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE));
        routs.add(route);

        final ArrayList<RouteGroup> groups = new ArrayList<>();

        RouteGroup g = new RouteGroup();
        g.setGroupName(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_GROUP));
        g.setGroupId(Integer.parseInt(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_GROUP_ID)));

        groups.add(g);

        setRouteSpinner(groups, routs);

    }


    private void setRouteSpinner(ArrayList<RouteGroup> groups, ArrayList<Route> routs) {

        // Initializing an ArrayAdapter
        final ArrayAdapter<RouteGroup> groupAdapter = new ArrayAdapter<RouteGroup>(getActivity(), R.layout.spinner_background, groups) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray

                    tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
                }

                return view;
            }
        };

        groupAdapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerGroup.setAdapter(groupAdapter);

        spinnerGroup.setSelection(0);

        spinnerGroup.setOnItemSelectedListener(this);

        // Initializing an ArrayAdapter

        final ArrayAdapter<Route> routeAdapter = new ArrayAdapter<Route>(getActivity(), R.layout.spinner_background, routs) {
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

                    tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
                }

                return view;
            }
        };

        routeAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerRoute.setAdapter(routeAdapter);

        spinnerRoute.setSelection(0);
        spinnerRoute.setOnItemSelectedListener(this);

        setProgressBar(false);

    }

    //get Shops
    private void getRegisteredShops() {

        printLog("getRegisteredShops", "in");

        final ArrayList<Shop> list = myDatabase.getRegisteredCustomers();

        /*for (int i = 0; i>list.size(); i++){

            Log.e("Vat from db", ""+list.get(i).getVatNumber());

        }*/


        if (!list.isEmpty()) {
            setRecyclerView(list);
            return;
        }

        if (!checkConnection()) {
            setErrorView(getContext().getString(R.string.no_internet), "", false);
            return;
        }

        setProgressBar(true);
        Route r = (Route) spinnerRoute.getSelectedItem();
        RouteGroup g = (RouteGroup) spinnerGroup.getSelectedItem();
        JSONObject object = new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("route_id", r.getId());
            object.put("group_id",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        list.clear();
        //  selling_list.clear();
        Log.e(TAG, "getRegisteredShops  object " + object);
        webRegisteredShop(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    Log.e(TAG, "getRegisteredShops  response " + response);
                    if (response.getString("status").equals("success") && !response.isNull("Customers")) {

                        JSONArray array = response.getJSONArray("Customers");
                        Log.e(TAG, "getRegisteredShops  response " + response);
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject shopObj = array.getJSONObject(i);

                            Shop shop = new Shop();
                            int custId = shopObj.getInt("id");

                            shop.setShopId(custId);
                            shop.setShopCode(shopObj.getString("shope_code"));
                            shop.setShopName(shopObj.getString("name"));
                            shop.setShopArabicName(shopObj.getString("arabic_name"));
                            shop.setShopMail(shopObj.getString("email"));
                            shop.setShopMobile(shopObj.getString("contact_no"));
                            shop.setShopAddress(shopObj.getString("address"));
                            shop.setVatNumber(shopObj.getString("vat_no"));

                            shop.setLatitude(shopObj.getString("latitude"));
                            shop.setLongitude(shopObj.getString("longitude"));

                            shop.setRouteCode(shopObj.getString("route_code"));
                            shop.setCreditLimit((float) shopObj.getDouble("credit_limit"));
                            shop.setCredit((float) shopObj.getDouble("credit"));
                            shop.setDebit((float) shopObj.getDouble("debit"));
                            shop.setOutStandingBalance((float) shopObj.getDouble("balance"));
                            shop.setOpeningbalance((float) shopObj.getDouble("opening_balance"));
                            shop.setPlace_ofsupply(shopObj.getString("place_of_supply"));
                            shop.setCreditperiod(shopObj.getString("credit_period"));
                            shop.setGroup(str_beet_selected);
                            shop.setRoute(str_route_selected);
                            try {
                                shop.setState_id(shopObj.getInt("state_id"));
                                shop.setState_code(shopObj.getString("state_code"));
                            }catch (Exception e){

                            }
                            //

                            if(shop.getCreditLimit()==0) {
                                shop.setCreditlimit_register("0");
                                Log.e("if",""+shop.getCreditLimit());
                            }
                            else{
                              //  shop.setCreditlimit_register("1");
                                shop.setCreditlimit_register(""+shop.getCreditLimit());
                                Log.e("else",""+shop.getCreditLimit());
                            }

                            ArrayList<CustomerProduct> selling_list = new ArrayList<>();
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
                            shop.setRegistered_status(true);

                            shop.setVisit(false);

                            list.add(shop);
                        }
                    }
                    else{
                        if (response.getString("status").equals("Empty")) {
                            storeVanStock();
                        }
                    }

                } catch (JSONException e) {
                    printLog(TAG, "getRegisteredShops  Exception " + e.getLocalizedMessage());

                    e.getMessage();
                }

                if (list.isEmpty())
                    setRecyclerView(list);
                else
                    storeCustomersToOffline(list);
            }

            @Override
            public void onErrorResponse(String error) {
                setErrorView(error, getString(R.string.error_subtitle_failed_one_more_time), true);
            }
        }, object);
    }

    //    customer list with invoices to sqllite db
    private void storeCustomersToOffline(ArrayList<Shop> list) {

        for (Shop s : list) {
            boolean b = myDatabase.insertRegisteredCustomer(s);
        }

        setRecyclerView(myDatabase.getRegisteredCustomers());

        storeVanStock();  // store van stock after storing customer

    }

    //    Load Stock All List from Server
    private void storeVanStock() {
        Log.e("reched hr","1");

        if (myDatabase.isExistProducts()) {
            return;
        }

        if (!checkConnection()) {
            setErrorView(getContext().getString(R.string.no_internet), "", false);
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(getContext(), null, "Please wait...", false, false);

        final ArrayList<Product> products = new ArrayList<>();
        final JSONObject object = new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"storeVanStock Object   "+object);

        webGetVanStock(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG,"storeVanStock response   "+response.toString());

                try {
                    int int_product_qty = 0;

                    if (response.getString("status").equalsIgnoreCase(
                            "success") && !response.isNull("Stocks")) {
                        JSONArray stockArray = response.getJSONArray("Stocks");
                        for (int i = 0; i < stockArray.length(); i++) {

                            JSONObject stockObject = stockArray.getJSONObject(i);
                            Product product = new Product();

                            //    Log.v(TAG,"storeVanStock   stockObject   "+stockObject);

                            product.setProductId(stockObject.getInt("product_id"));
                            product.setBarcode(stockObject.getString("product_barcode"));
                            product.setProductCode(stockObject.getString("product_code"));

                            //  Log.e("product Name", ""+stockObject.getString("product_name"));

                            product.setProductName(stockObject.getString("product_name"));
                            product.setP_name(stockObject.getString("p_name"));

                            String bonuspercentage = stockObject.getString("product_bonus_percentage");

                            product.setProductBonus(Float.parseFloat(bonuspercentage));
                            product.setProduct_hsncode(stockObject.getString("product_hsncode"));
                            // printLog("bonus", ""+bonuspercentage);

                            String arb = "";
                            if (!stockObject.isNull("arabic_name"))
                                arb = stockObject.getString("arabic_name");
                            if (TextUtils.isEmpty(arb) || arb.equals("null"))
                                arb = " ";
                            product.setArabicName(arb);

                            product.setProductType(stockObject.getString("product_type_name"));
                            product.setProduct_reporting_Unit(stockObject.getInt("reporting_unit"));
                            product.setProduct_reporting_Price((float) stockObject.getDouble("reporting_price"));
                          //  product.setSale_unitid(stockObject.getInt("sale_unit_id"));
                            try {
                                product.setSale_unitid(stockObject.getInt("sale_unit_id"));
                            }catch (Exception e){

                            }
                            ArrayList<Units> unit_array = new ArrayList<>();
                            ArrayList<MfgDate> mfg_array = new ArrayList<>();

                            JSONArray unitArray = stockObject.getJSONArray("Units_list");
                            for (int k=0; k<unitArray.length(); k++){

                                JSONObject unitObject = unitArray.getJSONObject(k);

                                Units unit = new Units();

                                unit.setUnitId(unitObject.getInt("id"));
                                unit.setUnitName(unitObject.getString("name"));
                                unit.setCon_factor(unitObject.getInt("con_factor"));
                                unit.setUnitPrice(unitObject.getString("unit_price"));
                                unit.setUnitWholesalePrice(unitObject.getString("wholesale_price"));
                                unit_array.add(unit);
                            }

                            String gsonvalue = new Gson().toJson(unit_array);

                               Log.e("gsonvalue",gsonvalue);

                            product.setUnitslist(gsonvalue);


//                            JSONArray mfgArray = stockObject.getJSONArray("Mfg_list");
//                            for (int k=0; k<mfgArray.length(); k++){
//
//                                JSONObject mfgObject = mfgArray.getJSONObject(k);
//
//                                MfgDate mfg = new MfgDate();
//
//                                mfg.setProd_id(mfgObject.getInt("product_id"));
//                                mfg.setMfgdate(mfgObject.getString("mfg_date"));
//
//                                mfg_array.add(mfg);
//                            }
//
//                            String mfg_gson = new Gson().toJson(mfg_array);
//
//                            Log.e("mfg_gson",mfg_gson);
//
//                            product.setMfglist(mfg_gson);

                            ArrayList<Taxes> taxes_array = new ArrayList<>();

                            JSONArray taxarray = stockObject.getJSONArray("Tax_list");
                            for (int k=0; k<taxarray.length(); k++){

                                JSONObject taxObject = taxarray.getJSONObject(k);

                                Taxes taxes = new Taxes();

                                taxes.setTax(taxObject.getString("tax"));
                                taxes.setCgst(taxObject.getString("cgst"));
                                taxes.setSgst(taxObject.getString("sgst"));
                                taxes_array.add(taxes);
                            }

                            String gsontax = new Gson().toJson(taxes_array);
                            product.setTaxlist(gsontax);

                            product.setBrandName(stockObject.getString("brand_name"));

                            float wholeSale = (float) stockObject.getDouble("product_wholesale_price");
                            float retailPrice = (float) stockObject.getDouble("product_mrp");
                            //haris added on 21-11-2020
                            float product_rate = (float) stockObject.getDouble("product_rate");
                            //
                            float vat = (float) stockObject.getDouble("product_vat");

                            product.setWholeSalePrice(wholeSale);
                            product.setRetailPrice(retailPrice);
                            product.setTax(vat);

                            product.setProduct_rate(product_rate);

                            product.setCost((float) stockObject.getDouble("product_cost"));
                            product.setPiecepercart(stockObject.getInt("piece_per_cart"));

                            product.setStockQuantity(stockObject.getInt("stock_quantity"));
                            int_product_qty=stockObject.getInt("stock_quantity");

                            //haris added on 26-10-2020
                            Log.e("int_product_qty",""+int_product_qty);
                            Log.e("int_product_vat",""+vat);

                            ArrayList<Size> size_array = new ArrayList<>();
                            JSONArray sizeArray = stockObject.getJSONArray("Sizes_list");
                            for (int k=0; k<sizeArray.length(); k++){

                                JSONObject unitObject = sizeArray.getJSONObject(k);

                                Size size = new Size();

                                size.setSizeId(unitObject.getInt("size"));
                                size.setQuantity(unitObject.getString("quantity"));
                                size.setAvailable_stock(int_product_qty);
                                size_array.add(size);
                            }
                            String gsonvalue_size = new Gson().toJson(size_array);
                            product.setSizelist(gsonvalue_size);

                            /////////

                            products.add(product);
                        }

                        float toatalStock = response.getLong("total_stock");
                        int stockQuantity = response.getInt("total_quantity");

                    } else
                    {
                        Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.getMessage();

                    pd.dismiss();//
                    printLog(TAG, "storeVanStock Exception  " + e.getMessage());
                }

                if (!products.isEmpty()) {

                    for (Product p : products) {

                        try {
                            //  Log.e("Product Insert Db", ""+p.getProductName());
                            myDatabase.insertStock(p);   //storeVanStock to local
                        }catch (Exception e){
                            Log.e("Db insert error", ""+e.getMessage());
                        }

                    }
                }

                pd.dismiss();

                //   getAllRouteCustomer();

                storeVanMasterStock();

            }

            @Override
            public void onErrorResponse(String error) {

                Toast.makeText(getContext(), "VanStock  " + error, Toast.LENGTH_SHORT).show();

                pd.dismiss();
            }
        }, object);
    }

    private void get_Banks_List(){

        Log.e("get bank","response Banks calling 1");

        final ArrayList<Banks> bankarray = new ArrayList<>();

        if (!checkConnection()) {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        setProgressBar(true);

        webGetBanks(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response","response Banks:"+response.toString());
                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {


                        JSONArray bankArray = response.getJSONArray("Banks");
                        for (int i = 0; i < bankArray.length(); i++) {

                            JSONObject bankObject = bankArray.getJSONObject(i);

                            Banks banks = new Banks();

                            banks.setBank_id(bankObject.getInt("id"));
                            banks.setBank_name(bankObject.getString("name"));
                            banks.setShown_in_contra(bankObject.getString("shwn_in_vouchr"));

                            bankarray.add(banks);
                        }
                        setProgressBar(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!bankarray.isEmpty()) {

                    //  myDatabase.insertbankdetails(bankarray);

                    myDatabase.deleteTableRequest(REQ_BANK_DETAILS);

                    for (Banks b : bankarray ) {

                        Log.e("Banks array insert ", "/"+b.getBank_name());
                        myDatabase.insertBanksName(b) ; //store bank names to local
                    }

                    banks.addAll(myDatabase.getAllBanks());

                    get_Expense_List();

                    Log.e("returned banks", ":"+banks.size());
                }else {

                    get_Expense_List();

                }
            }
            @Override
            public void onErrorResponse(String error) {
                setProgressBar(false);
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void get_Expense_List(){

        Log.e("get expense","response expense calling 1");

        array_expense.clear();

        /*if (!checkConnection()) {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }*/

        setProgressBar(true);

        webAllExpense(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        Log.e("response","response Expense:"+response.toString());

                        JSONArray expenseArray = response.getJSONArray("Expense");

                        for (int i = 0; i < expenseArray.length(); i++) {

                            JSONObject expenseobject = expenseArray.getJSONObject(i);

                            Expense expense = new Expense();

                            expense.setId(expenseobject.getString("id"));
                            expense.setName(expenseobject.getString("name"));
                            Log.e("expid",""+expenseobject.getString("id"));
                            Log.e("expname",""+expenseobject.getString("name"));

                            array_expense.add(expense);

                        }

                        setProgressBar(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!array_expense.isEmpty()) {

                    myDatabase.deleteTableRequest(REQ_EXPENSE);

                    for (Expense exp : array_expense ) {

                        Log.e("expense array insert ", "/"+exp.getName());

                        myDatabase.insertExpense(exp); //store bank names to local
                    }

                    enableViews();

                    HomeDashBoardLeftFragment dashboardFragment = new HomeDashBoardLeftFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_dash_board_left, dashboardFragment ); // give your fragment container id in first parameter
                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                    transaction.commit();

                }else {

                    enableViews();

                    HomeDashBoardLeftFragment dashboardFragment = new HomeDashBoardLeftFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_dash_board_left, dashboardFragment ); // give your fragment container id in first parameter
                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                    transaction.commit();

                }
            }
            @Override
            public void onErrorResponse(String error) {

                setProgressBar(false);
                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //  get group wise Shops
    private void getGroupByShops() {

        RouteGroup g = (RouteGroup) spinnerGroup.getSelectedItem();
        Route r = (Route) spinnerRoute.getSelectedItem();

        if (g.getGroupId() == -1 && r.getId() == -1) {
            Toast.makeText(getActivity(), "Please Select Group and Route", Toast.LENGTH_SHORT).show();
            return;

        } else if (g.getGroupId() == -1 && r.getId() != -1) {
            Toast.makeText(getActivity(), "Please Select Group", Toast.LENGTH_SHORT).show();
            return;
        } else if (g.getGroupId() != -1 && r.getId() == -1) {
            Toast.makeText(getActivity(), "Please Select Route", Toast.LENGTH_SHORT).show();
            return;
        } else if (!checkConnection()) {
            setErrorView(getContext().getString(R.string.no_internet), "", false);
            return;
        }

      //  setProgressBar(true);

        final ArrayList<Shop> list = new ArrayList<>();

        list.clear();

//        JSONObject object = new JSONObject();
//
//        try {
//            object.put("customer_group_id", g.getGroupId());
//            object.put("route_id", r.getId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.e("Customers object", ""+object);
//
//        webGroupByShop(new WebService.webObjectCallback() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                printLog("Add customer response", ""+response);
//                try {
//
//                    Log.e("Customers response", ""+response.toString());
//
//                    if (response.getString("status").equals("success") && !response.isNull("Customers")) {
//
//                        JSONArray array = response.getJSONArray("Customers");
//                        for (int i = 0; i < array.length(); i++) {
//
//                            JSONObject shopObj = array.getJSONObject(i);
//
//                            Shop shop = new Shop();
//
//                            int custId = shopObj.getInt("id");
//                            shop.setShopId(custId);
//                            shop.setShopCode(shopObj.getString("shope_code"));
//                            shop.setShopName(shopObj.getString("name"));
//                            shop.setShopArabicName(shopObj.getString("arabic_name"));
//                            shop.setShopMail(shopObj.getString("email"));
//                            shop.setShopMobile(shopObj.getString("mobile"));
//                            shop.setShopAddress(shopObj.getString("address"));
//                            shop.setVatNumber(shopObj.getString("vat_no"));
//
//                            // Log.e("VAtttt ", "shop id "+shopObj.getInt("id")+"/"+shopObj.getString("vat_no"));
//
//                            shop.setLatitude(shopObj.getString("latitude"));
//                            shop.setLongitude(shopObj.getString("longitude"));
//                            shop.setRouteCode(shopObj.getString("route_code"));
//                            shop.setCreditLimit((float) shopObj.getDouble("credit_limit"));
//                            shop.setCredit((float) shopObj.getDouble("credit"));
//                            shop.setDebit((float) shopObj.getDouble("debit"));
//                            shop.setOutStandingBalance((float) shopObj.getDouble("balance"));
//
//                            final ArrayList<CustomerProduct> selling_list = new ArrayList<>();
//
//                            JSONArray arrayproductlist = shopObj.getJSONArray("customer_product_list");
//
//                           /*if (arrayproductlist.length()>0) {
//
//                                for (int k = 0; k < arrayproductlist.length(); k++) {
//
//                                    JSONObject productobj = arrayproductlist.getJSONObject(k);
//
//                                    CustomerProduct sellingPrice = new CustomerProduct();
//
//                                    sellingPrice.setProductId(productobj.getInt("product_id"));
//                                    sellingPrice.setCustomerId(custId);
//                                    sellingPrice.setPrice(productobj.getLong("product_selling_rate"));
//
//                                    selling_list.add(sellingPrice);
//                                }
//                            }*/
//
//                            shop.setProducts(selling_list);
//                            list.add(shop);
//                        }
//                    }
//                } catch (JSONException e) {
//
//                    e.getMessage();
//                }
//
//               /* if (selling_list.isEmpty()){
//
//                }else {
//                    for (SellingPrice s : selling_list) {
//
//                        *//* printLog("cust id", ""+s.getCustomerid());
//                        printLog("product id", ""+s.getProductid());
//                        printLog("product code", ""+s.getProductcode());
//                        printLog("sell price", ""+s.getSellingprice());*//*
//
//                       myDatabase.insertSellingPrice(s);
//
//                    }00
//                }
//*/
//                setRecyclerView(list);
//            }
//
//            @Override
//            public void onErrorResponse(String error) {
//
//                setErrorView(error, getString(R.string.error_subtitle_failed_one_more_time), true);
//            }
//        }, object);
    }

    private void setRecyclerView(ArrayList<Shop> list) {

        if (list.isEmpty()) {
            setErrorView("No Shops", "", false);
            return;
        }

        shops.clear();
        shops.addAll(list);
        searchView.setVisibility(View.VISIBLE);
        searchView.setQuery("", false);
        shopAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        setProgressBar(false);
        errorView.setVisibility(View.GONE);

    }

    private void setProgressBar(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

        } else
            progressBar.setVisibility(View.GONE);
    }

    //set ErrorView
    private void setErrorView(final String title, final String subTitle, boolean isRetry) {

        errorView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);

        setProgressBar(false);
        errorView.setConfig(ErrorView.Config.create()
                .title(title)
                .subtitle(subTitle)
                .retryVisible(isRetry)
                .build());

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {

                enableViews();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_day_close:

                final DayCloseDialog dayCloseDialog = new DayCloseDialog(getActivity(), new OnDayCloseListener() {
                    @Override
                    public void onEnterKiloMeter(String kilometer) {

                        DataServiceOperations operations = new DataServiceOperations(getContext(), new OnNotifyListener() {
                            @Override
                            public void onNotified() {
                                enableViews();
                            }
                        });

                        operations.dayClose(kilometer);
                    }
                });

                if (!checkConnection()) {
                    Toast.makeText(getActivity(), getContext().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

                } else {

                    dayCloseDialog.show();

                  /* if (myDatabase.isDayclosePrmission())
                         dayCloseDialog.show();
                    else0
                        Toast.makeText(getActivity(), "Please visit finish all shops", Toast.LENGTH_SHORT).show();*/
                }

                break;

            case R.id.button_home_register:
                check_permission();
                if(!str_Latitude.equals("0")&& !str_Longitude.equals("0")){
                    Log.e("str_Latitude",str_Latitude);
                    Log.e("str_Longitude",str_Longitude);
                    sessionValue.save_latitude_and_longitude(str_Latitude,str_Longitude);
                }

                try {

                    RouteGroup g = (RouteGroup) spinnerGroup.getSelectedItem();
                    Route r = (Route) spinnerRoute.getSelectedItem();

                    if (g.getGroupId() != -1 && r.getId() != -1) {

                        final DayRegisterDialog dayRegisterDialog = new DayRegisterDialog(getActivity(), new OnInputKiloMeterListener() {
                            @Override
                            public void onEnterKiloMeter(int present, String kilometer,String vehicleno, String drivername) {
                                Log.e("vehicleno",""+vehicleno);
                                if(vehicleno.equals("Select Vehicle")){
                                    vehicleno =" ";
                                }

                                registerGroup(present, kilometer,vehicleno,drivername);
                            }
                        });

                        dayRegisterDialog.show();

                    } else if (g.getGroupId() == -1 && r.getId() != -1)
                        Toast.makeText(getActivity(), "Please Select Group", Toast.LENGTH_SHORT).show();
                    else if (g.getGroupId() != -1 && r.getId() == -1)
                        Toast.makeText(getActivity(), "Please Select Route", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Please Select Route and Group", Toast.LENGTH_SHORT).show();
                } catch (NullPointerException e) {
                    e.getMessage();
                }

                break;
            case R.id.ib_syncbtn:
                dayRegId = sessionValue.getDayRegisterId();
                syncConfirmationDialogue();
                break;

            case R.id.imageButton_home_menu:
                showMenu(v);
                break;
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void syncConfirmationDialogue() {

        new AlertDialog.Builder(getActivity())
                .setMessage("Better network is avalable now ?  Confirm Sync ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (isNetworkConnected()) {
                            new AsyncTaskLoadDataSync().execute();

                        }
                        else{
                            Toast.makeText(getActivity(), "No Network ", Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to register
                    }
                })
                .show();
    }


    private void showMenu(View view) {

        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getActivity(), view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_actions, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_change_password:

                        changePasswordDialog();

                        break;

                    case R.id.action_logout:

                        //    logoutDialogue();

                        isRegistered = sessionValue.isGroupRegister();

                        // Log.e("Registered", ":"+isRegistered);

                        if (!isRegistered) {

                            logoutDialogue();

                        } else {

                            Toast.makeText(getActivity(), "Please Day close before logout", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case R.id.action_exit:
                        getActivity().finish();

                        break;
                }

                return true;
            }
        });
        popup.show(); //showing popup menu
        //closing the setOnClickListener method

        //End PopUp Menu
    }

    //    register confirmation dialog

    private void registerConfirmationDialogue() {

        new AlertDialog.Builder(getActivity())
                .setMessage("Confirm Registration?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to register
                    }
                })
                .show();
    }

    private void registerGroup(final int present, final String kiloMeter, final String vehicleno , final String drivername) {
        final ArrayList<State> state_array = new ArrayList<>();
        final ArrayList<District> district_array = new ArrayList<>();
        if (!checkConnection()) {
            setErrorView(getContext().getString(R.string.no_internet), "", false);
            return;
        }

        sessionValue.clearCode();  //clear invoice code

        myDatabase.deleteTableRequest(REQ_ANY_TYPE);

        RouteGroup g = (RouteGroup) spinnerGroup.getSelectedItem();
        Route r = (Route) spinnerRoute.getSelectedItem();

        final JSONObject object = new JSONObject();
        try {
            str_beet_selected = g.getGroupName();
            str_route_selected = r.getRoute();

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("customer_group_id", g.getGroupId());
            object.put("route_id", r.getId());
            object.put("present", present);
            object.put("km", kiloMeter);
            object.put("datetime", myDatabase.getDateTime());
            object.put("vehicleno", vehicleno);
            object.put("drivername", drivername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sessionAuth.store_opening_time(""+myDatabase.getDateTime());

        printLog(TAG, "registerGroup  Obj" + object);

        final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);

        webGroupRegister(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "registerGroup  " + response);

                try {

                    switch (response.getString("status")) {
                        case "success": {

                            printLog(TAG, "registerGroup Success " + response);
                            if (present == 0) {
                                Toast.makeText(getActivity(), "Absent Successful", Toast.LENGTH_SHORT).show();
                            } else {

                                if (!response.isNull("ExProfile")) {
                                    JSONObject executiveObj = response.getJSONObject("ExProfile");

                                    String exeName = executiveObj.getString("name");
                                    String exeMobile = executiveObj.getString("mobile");
                                    String exeId = executiveObj.getString("id");
                                    String cashinhand = executiveObj.getString("cash_in_hand");

                                    sessionValue.createExecutiveDetails(exeName, exeId, exeMobile);

                                    Log.e("Cssh in hand", ""+cashinhand);


                                    sessionAuth.store_bonus(0);
                                    sessionAuth.store_CashinHand(Float.parseFloat(cashinhand));
                                }

                                String dayRegId = response.getString("day_register_id");
                                String vehicleno = response.getString("vehicleno");
                                float saleTarget = (float) response.getDouble("SaleTargets");
                                float monthlybonus = (float) response.getDouble("ExecutiveMonthlyBonus");
                                String st_link = response.getString("qr_code_link");
                                String NewlnkString = st_link.replaceAll("'\'", "");
                                //    Log.e("monthly bonus get", ""+monthlybonus);

                                sessionAuth.store_Monthlybonus(monthlybonus);

                                String routeMobile =  response.getString("route_mobile");
                                 String producttype =  response.getString("CaseSizeList");
                                if (!response.isNull("CompProfile")) {
                                    JSONObject companyObj = response.getJSONObject("CompProfile");

                                    String compName = companyObj.getString("company_name");
                                    String compNameArab = companyObj.getString("company_name_arabic");

                                    String compAddress1 = companyObj.getString("address_line_1");

                                    String compAddress1Arabic = companyObj.getString("address_line_1_arabic");

                                    String compAddress2 = companyObj.getString("address_line_2");
                                    String compAddress2Arabic = companyObj.getString("address_line_2_arabic");

                                    String compMobile = companyObj.getString("mobile");
                                    String compEmail = companyObj.getString("mail_address");
                                    String compCr = companyObj.getString("cr_no");
                                    String compVat = companyObj.getString("vat_code");
                                    String compweb ="";
                                    //  String compweb = companyObj.getString("website");
                                    //haris added on 06-11-2020
                                    String compPan_No = companyObj.getString("company_pan");
                                    String tax_card_no = companyObj.getString("tax_card_no");
                                  //  String comp_fssai_no = companyObj.getString("fssai_no");
//                                    String comp_bankname = companyObj.getString("bank_name");
//                                    String comp_bankaccname = companyObj.getString("bank_acc_name");
//                                    String comp_bankaccno = companyObj.getString("acc_no");
//                                    String comp_bankifsc = companyObj.getString("ifsc");
//                                    String comp_bankbranch = companyObj.getString("bank_branch");

                                    sessionValue.createCompanyDetails(compName, compNameArab, compAddress1, compAddress1Arabic, compAddress2, compAddress2Arabic, compMobile, compEmail, compCr,
                                            compVat,compPan_No,producttype ,NewlnkString,tax_card_no,compweb,"00","comp_bankname","comp_bankaccname","comp_bankaccno","comp_bankifsc","comp_bankbranch");
                                }

                                if(state_array.size()==0) {
                                    JSONArray statearray = response.getJSONArray("State_list");
                                    for (int k = 0; k < statearray.length(); k++) {

                                        JSONObject stateObject = statearray.getJSONObject(k);

                                        State state = new State();

                                        state.setStateid(stateObject.getInt("id"));
                                        state.setName(stateObject.getString("name"));

                                        state_array.add(state);
                                    }
                                }


                                if(district_array.size()==0) {
                                    JSONArray districtarray = response.getJSONArray("District_list");
                                    for (int k = 0; k < districtarray.length(); k++) {

                                        JSONObject distObject = districtarray.getJSONObject(k);

                                        District distr = new District();

                                        distr.setDistrictId(distObject.getInt("id"));
                                        distr.setDistrict(distObject.getString("name"));
                                        district_array.add(distr);

                                    }
                                }

                                if (!state_array.isEmpty()) {
                                    boolean deleteStatus = myDatabase.deleteTableRequest(REQ_STATE);
                                    for (State s : state_array) {

                                        try {

                                            myDatabase.insertstate(s);
                                        }catch (Exception e){
                                            Log.e("STATE DB ERROR", ""+e.getMessage());
                                        }

                                    }
                                }

                                if (!district_array.isEmpty()) {
                                    boolean deleteStatus = myDatabase.deleteTableRequest(REQ_DISTRICT);
                                    for (District d : district_array) {

                                        try {

                                            myDatabase.insertdistrict(d);
                                        }catch (Exception e){
                                            Log.e("DISTRICT DB ERROR", ""+e.getMessage());
                                        }

                                    }
                                }
                                if (!response.isNull("RoutesList")) {
                                    JSONArray invCodeArray = response.getJSONArray("RoutesList");
                                    parceInvoiceCode(invCodeArray);
                                }

                                sessionValue.storeGroupAndRoute((RouteGroup) spinnerGroup.getSelectedItem(), (Route) spinnerRoute.getSelectedItem(),saleTarget, dayRegId, kiloMeter,routeMobile,vehicleno,drivername);

                                Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                searchView.setVisibility(View.VISIBLE);
                            }

                            break;
                        }
                        case "Already Registered": {

                            printLog(TAG, "registerGroup Already registered " + response);
                            searchView.setVisibility(View.VISIBLE);

                            Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();

//                          rote values
                            Route r = new Route();
                            r.setId(response.getInt("route_id"));
                            r.setRoute(response.getString("route_name"));

                            RouteGroup g = new RouteGroup();
                            g.setGroupId(response.getInt("group_nid"));
                            g.setGroupName(response.getString("group_name"));

                            String dayRegId = response.getString("day_register_id");

                            String kMeter = response.getString("km");
                            float saleTarget = (float) response.getDouble("SaleTargets");
                            float monthlybonus = (float) response.getDouble("ExecutiveMonthlyBonus");
                            String st_link = response.getString("qr_code_link");
                            String NewlnkString = st_link.replaceAll("'\'", "");
                            //    Log.e("mnthly bonus Alrdy get", ""+monthlybonus);

                            sessionAuth.store_Monthlybonus(monthlybonus);

                            String routeMobile =  response.getString("route_mobile");
                            String producttype =  response.getString("CaseSizeList");
                            if (!response.isNull("ExProfile")) {
                                JSONObject executiveObj = response.getJSONObject("ExProfile");
                                String exeName = executiveObj.getString("name");
                                String exeMobile = executiveObj.getString("mobile");
                                String exeId = executiveObj.getString("id");
                                String cashinhand = executiveObj.getString("cash_in_hand");

                                sessionValue.createExecutiveDetails(exeName, exeId, exeMobile);

                                Log.e("Cssh in hand 2", ""+cashinhand);

                                sessionAuth.store_CashinHand(Float.parseFloat(cashinhand));

                            }
                            if (!response.isNull("CompProfile")) {

                                JSONObject companyObj = response.getJSONObject("CompProfile");
                                String compName = companyObj.getString("company_name");
                                String compNameArab = companyObj.getString("company_name_arabic");

                                String compAddress1 = companyObj.getString("address_line_1");
                                String compAddress1Arabic = companyObj.getString("address_line_1_arabic");
                                String compAddress2 = companyObj.getString("address_line_2");
                                String compAddress2Arabic = companyObj.getString("address_line_2_arabic");

                                String compMobile = companyObj.getString("mobile");
                                String compEmail = companyObj.getString("mail_address");
                                String compCr = companyObj.getString("cr_no");
                                String compVat = companyObj.getString("vat_code");
                                String compPan_No = companyObj.getString("company_pan");
                               // String comp_fssai_no = companyObj.getString("fssai_no");
                                String compweb = "";
//                                String compweb = companyObj.getString("website");
                                String tax_card_no = companyObj.getString("tax_card_no");
                              //  String comp_bankname = companyObj.getString("bank_name");
                               // String comp_bankaccname = companyObj.getString("bank_acc_name");
                               // String comp_bankaccno = companyObj.getString("acc_no");
                               // String comp_bankifsc = companyObj.getString("ifsc");
                               // String comp_bankbranch = companyObj.getString("bank_branch");

                                sessionValue.createCompanyDetails(compName, compNameArab, compAddress1, compAddress1Arabic, compAddress2, compAddress2Arabic, compMobile, compEmail, compCr,
                                        compVat,compPan_No,producttype ,NewlnkString,tax_card_no,compweb,"00","comp_bankname","comp_bankaccname","comp_bankaccno","comp_bankifsc","comp_bankbranch");

                            }
                            if(state_array.size()==0) {
                                JSONArray statearray = response.getJSONArray("State_list");
                                for (int k = 0; k < statearray.length(); k++) {

                                    JSONObject stateObject = statearray.getJSONObject(k);

                                    State state = new State();

                                    state.setStateid(stateObject.getInt("id"));
                                    state.setName(stateObject.getString("name"));

                                    state_array.add(state);
                                }
                            }


                            if(district_array.size()==0) {
                                JSONArray districtarray = response.getJSONArray("District_list");
                                for (int k = 0; k < districtarray.length(); k++) {

                                    JSONObject distObject = districtarray.getJSONObject(k);

                                    District distr = new District();

                                    distr.setDistrictId(distObject.getInt("id"));
                                    distr.setDistrict(distObject.getString("name"));
                                    district_array.add(distr);

                                }
                            }

                            if (!state_array.isEmpty()) {
                                boolean deleteStatus = myDatabase.deleteTableRequest(REQ_STATE);
                                for (State s : state_array) {

                                    try {

                                        myDatabase.insertstate(s);
                                    }catch (Exception e){
                                        Log.e("STATE DB ERROR", ""+e.getMessage());
                                    }

                                }
                            }

                            if (!district_array.isEmpty()) {
                                boolean deleteStatus = myDatabase.deleteTableRequest(REQ_DISTRICT);
                                for (District d : district_array) {

                                    try {

                                        myDatabase.insertdistrict(d);
                                    }catch (Exception e){
                                        Log.e("DISTRICT DB ERROR", ""+e.getMessage());
                                    }

                                }
                            }
                            if (!response.isNull("RoutesList")) {
                                JSONArray invCodeArray = response.getJSONArray("RoutesList");
                                parceInvoiceCode(invCodeArray);
                            }

                            sessionValue.storeGroupAndRoute(g, r, saleTarget, dayRegId, kMeter,routeMobile,vehicleno,drivername);


                            break;
                        }
                        default:
                            Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (JSONException e) {
                    printLog(TAG,"registerGroup Exception "+e.getMessage());
                }

                pd.dismiss();

                get_Banks_List();

               /* enableViews();

                HomeDashBoardLeftFragment dashboardFragment = new HomeDashBoardLeftFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_dash_board_left, dashboardFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();*/

            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    private void parceInvoiceCode(JSONArray array) {

        try {
            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                String code = object.getString("route_code");
                String invoiceNumber = object.getString("invoice_no");
                String receiptNumber = object.getString("receipt_no");
                String return_invoiceNumber = object.getString("return_invoice_no");
                String exec_code = object.getString("executive_code");
               // "},"RoutesList":[{"route_code":"01","return_invoice_no":"MN -","invoice_no":"116","executive_code":"MN -","receipt_no":null}],"

                sessionValue.storeInvoiceCode(exec_code, invoiceNumber);
                sessionValue.storeReceiptCode(exec_code, receiptNumber);
                sessionValue.storeExecutiveCode( exec_code);
                //sessionValue.storeReturnCode(exec_code, return_invoiceNumber);
                sessionValue.storeReturnCode(exec_code, return_invoiceNumber);
                //haris added on 10-02-2020
                sessionValue.storeRoutecode(code);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //    logout app
    private void logoutDialogue() {

        new AlertDialog.Builder(getActivity())
                .setMessage("Would you like to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // logout

                        sessionAuth.logoutUser();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to logout

                        dialog.dismiss();
                    }
                })
                .show();
    }

    //    logout app
    private void dayCloseConfirmationDialogue() {

        new AlertDialog.Builder(getActivity())
                .setMessage("Confirm DayClose?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        serviceOperations. dayClose();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to register
                    }
                })
                .show();
    }

    private void enableViews() {

        isRegistered = sessionValue.isGroupRegister();

        spinnerRoute.setEnabled(!isRegistered);
        spinnerGroup.setEnabled(!isRegistered);
        btnRegister.setEnabled(!isRegistered);
        btnRegister.setEnabled(!isRegistered);

        get_Vehicle_List();

        if (isRegistered) {
            btnRegister.setText("Registered");
            btnDayClose.setVisibility(View.VISIBLE);
            setLocalGroups();
            getRegisteredShops();

        } else {

            btnRegister.setText("Register");
            btnDayClose.setVisibility(View.GONE);
            getGroupAndRoutList();
            //storeVanStock();
            //  get_Banks_List();
        }
    }

    private void get_Vehicle_List() {

        //Log.e("get vehicle","response Vehicle calling 1");

        final ArrayList<Vehicle> vehiclearray = new ArrayList<>();

        if (!checkConnection()) {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        //setProgressBar(true);
        // final ProgressDialog pdveh = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);
        WebService.webGetVehicledetails(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        Log.e("response","response Vehicles:"+response.toString());
                        JSONArray vehicleArray = response.getJSONArray("vehicle");
                        for (int i = 0; i < vehicleArray.length(); i++) {

                            JSONObject bankObject = vehicleArray.getJSONObject(i);

                            Vehicle veh = new Vehicle();

                            veh.setVehicle_id(bankObject.getInt("id"));
                            veh.setVehicle_no(bankObject.getString("VehicleNo"));


                            vehiclearray.add(veh);
                        }
                        //setProgressBar(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //w  3 pdveh.dismiss();
                if (!vehiclearray.isEmpty()) {

                    //  myDatabase.insertbankdetails(bankarray);

                    myDatabase.deleteTableRequest(ConfigKey.REQ_VEHICLE_DETAILS);

                    for (Vehicle v : vehiclearray ) {

                        // Log.e("vehicle array insert ", "/"+v.getVehicle_id());
                        //Log.e("vehicle name ", "/"+v.getVehicle_no());
                        myDatabase.insertVehicledetails(v) ; //store bank names to local
                    }



                    // get_Expense_List();

                    //Log.e("returned vehicles", ":"+banks.size());
                }else {

                    // get_Expense_List();

                }
            }
            @Override
            public void onErrorResponse(String error) {
                // setProgressBar(false);
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNotifyListener) {
            mListener = (OnNotifyListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_home_group:

                if (!isRegistered)
                    getGroupByShops();


                break;
            case R.id.spinner_home_route:


                if (!isRegistered) {
                    getGroupByShops();
                }

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

    @Override
    public void onStart() {
        super.onStart();

        searchView.setQuery("", false);

        if (shopAdapter != null) {
            final ArrayList<Shop> list = myDatabase.getRegisteredCustomers();



            setRecyclerView(list);

        }
    }

    // add new Customer details dialog
    @SuppressLint("SetTextI18n")
    private void changePasswordDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Change Password");

        /** Create TextView and EditText */

        final TextView tvExecutiveId = new TextView(getActivity());

        final TextView tvOldPwd = new TextView(getActivity());
        final EditText etOldPwd = new EditText(getActivity());

        final TextView tvNewPass = new TextView(getActivity());
        final EditText etNewPass = new EditText(getActivity());

        final TextView tvConfirmPass = new TextView(getActivity());
        final EditText etConfirmPass = new EditText(getActivity());

        /** set Configure TextView and EditText */

        tvExecutiveId.setTextColor(getActivity().getResources().getColor(R.color.primaryText));
        tvExecutiveId.setPadding(0, 5, 0, 5);

        etOldPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etOldPwd.setBackgroundResource(R.color.colorTransparent);
        etOldPwd.setLines(1);

        etNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etNewPass.setBackgroundResource(R.color.colorTransparent);
        etNewPass.setLines(1);

        etConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etConfirmPass.setBackgroundResource(R.color.colorTransparent);
        etConfirmPass.setLines(1);


        tvExecutiveId.setText("User : " + sessionAuth.getUserDetails().get(PREF_KEY_USER_NAME));
        tvOldPwd.setText("Old Password");
        tvNewPass.setText("Password");
        tvConfirmPass.setText("Confirm Password");


        /** Set Positive and Negative Buttons to Dialog */
        alertDialog.setPositiveButton("Change Password", null);
        alertDialog.setNegativeButton("Cancel", null);

        /** Create LinearLayout */
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(20, 20, 20, 20);
//        ll.addView(tvExecutiveId);
//        ll.addView(etNewShopName);

        /** Add  TextView and EditText to LinearLayout*/

        ll.addView(tvExecutiveId);


        ll.addView(tvOldPwd);
        ll.addView(etOldPwd);

        ll.addView(tvNewPass);
        ll.addView(etNewPass);

        ll.addView(tvConfirmPass);
        ll.addView(etConfirmPass);
        /** Set LinearLayout to Dialog*/

        alertDialog.setView(ll);

        /** Comparisons PassWord and ConfirmPassWord using  TextValidator Class*/
        etConfirmPass.addTextChangedListener(new TextValidator(etConfirmPass) {
            @Override
            public void validate(TextView textView, String text) {
                /* Validation code here */
                if (!passwordStatus(etNewPass.getText().toString().trim(), text)) {
                    textView.setError("Password is Mismatch");
                }

            }
        });

        createDialog = alertDialog.create();

        //        show input keyboard
        etOldPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    createDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });


        createDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                android.widget.Button positive = createDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                android.widget.Button negative = createDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String old = etOldPwd.getText().toString().trim();
                        String newPwd = etNewPass.getText().toString().trim();


                        if (validateCredential(etOldPwd, etNewPass, etConfirmPass))
                            changePasswordRequest(old, newPwd);


                    }
                });

                negative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }

        /*    @Override
            public void onShow(final DialogInterface dialog) {
                *//** Initial Buttons *//*
                android.widget.Button positive = createDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                android.widget.Button negative = createDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {



                            if (validateCredential(etOldPwd,etNewPass,etConfirmPass))
                                Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();
//                                createUserRequest(object);


                    }
                });

                negative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }*/
        });
        createDialog.show();
    }

    //  check equal two string
    private boolean passwordStatus(String newPass, String confirmPass) {

        boolean status = false;
        status = newPass.equals(confirmPass);
        return status;
    }


    /**
     * Validation change passwordDialog
     */
    private boolean validateCredential(EditText oldPassword, EditText password, EditText confirmPass) {

        boolean status = false;

        if (TextUtils.isEmpty(oldPassword.getText().toString().trim())) {
            oldPassword.setError("Old Password is Empty");
            status = false;

        } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
            password.setError("Password is Empty");
            status = false;

        } else if (TextUtils.isEmpty(confirmPass.getText().toString().trim())) {
            confirmPass.setError("Confirm Password is Empty");
            status = false;
        } else if (!passwordStatus(password.getText().toString().trim(), confirmPass.getText().toString().trim())) {
            confirmPass.setError("Password is Mismatch");
            status = false;
        } else {
            status = true;
        }

        return status;
    }

    //    change password request
    private void changePasswordRequest(String oldPwd, final String newPwd) {


        final JSONObject object = new JSONObject();
        try {


            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("old_password", oldPwd);
            object.put("new_password", newPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);


        webChangePassword(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {


//                Log.v(TAG,"changePasswordRequest  "+response);

                try {
                    if (response.getString("status").equals("success")) {
                        sessionAuth.updatePassword(newPwd);
                        if (createDialog != null)
                            createDialog.dismiss();

                    } else
                        Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);

    }

    /**
     *     customer search
     *
     */

    //get Shops
    private void getAllRouteCustomer() {

        if (!sessionValue.isGroupRegister())
            return;


        if (!checkConnection()) {
            Toast.makeText(getContext(), getContext().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(getContext(), null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        webAllRouteShop(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("status").equals("success") && !response.isNull("Customers")) {

                        Log.v("Calling Async task", "getAllRouteCustomer  response ");
                        JSONArray array = response.getJSONArray("Customers");
                        new AsyncTaskLoadData().execute(array);

                    }

                } catch (JSONException e) {

                    pd.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }

                if (pd.isShowing())
                    pd.dismiss();
//                Toast.makeText(getContext(), "data updated", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }, object);
    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskLoadData extends AsyncTask<JSONArray, String, String> {
        private final static String TAG = "AsyncTaskLoadImage";
        ProgressDialog progress;

        @Override
        protected String doInBackground(JSONArray... jsonArrays) {
            JSONArray array = jsonArrays[0];
            try {

                for (int i = 0; i < array.length(); i++) {

                    JSONObject shopObj = array.getJSONObject(i);

                    Log.e(TAG, "getAllRouteCustomer register  response " + shopObj);

                    Shop shop = new Shop();

                    int custId = shopObj.getInt("id");

                    shop.setShopId(custId);
                    shop.setShopCode(shopObj.getString("shope_code"));
                    shop.setShopName(shopObj.getString("name"));
                    shop.setShopMail(shopObj.getString("email"));
                    shop.setShopMobile(shopObj.getString("mobile"));
                    shop.setShopAddress(shopObj.getString("address"));
                    shop.setCreditLimit((float) shopObj.getDouble("credit_limit"));
                    shop.setCredit((float) shopObj.getDouble("credit"));
                    shop.setDebit((float) shopObj.getDouble("debit"));

                    //  Log.e("getAllRouteCustomer Vat", ""+shopObj.getString("vat_no"));
                    shop.setVatNumber(shopObj.getString("vat_no"));
                    shop.setRouteCode(shopObj.getString("route_code"));
                    try {
                        shop.setState_id(shopObj.getInt("state_id"));
                        shop.setState_code(shopObj.getString("state_code"));
                    }catch (Exception e){

                    }
                    shop.setOutStandingBalance((float) shopObj.getDouble("balance"));
                    if(shop.getCreditLimit()==0) {
                        shop.setCreditlimit_register("0");
                        Log.e("if",""+shop.getCreditLimit());
                    }
                    else{
                        shop.setCreditlimit_register(""+shop.getCreditLimit());

                        Log.e("else",""+shop.getCreditLimit());
                    }
                    final ArrayList<CustomerProduct> selling_list = new ArrayList<>();

                    JSONArray arrayproductlist = shopObj.getJSONArray("customer_product_list");

                    if (arrayproductlist.length()>0) {

                        for (int k = 0; k < arrayproductlist.length(); k++) {

                                    /*JSONObject productobj = arrayproductlist.getJSONObject(k);

                                    CustomerProduct sellingPrice = new CustomerProduct();

                                    sellingPrice.setProductId(productobj.getInt("product_id"));
                                    sellingPrice.setCustomerId(custId);
                                    sellingPrice.setPrice(productobj.getLong("product_selling_rate"));

                                    selling_list.add(sellingPrice);*/


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
/*
                        ArrayList<Invoice> invoices = new ArrayList<>();

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

                    }

                    */

                    shop.setProducts(selling_list);

                    shop.setVisit(true);
//                        boolean b=myDatabase.insertUnRegisteredCustomer(shop);

                    boolean b = new MyDatabase(getContext()).insertUnRegisteredCustomer(shop);
                }

            } catch (JSONException e) {

                e.getMessage();

            }

            return "";
        }

        @Override
        protected void onPreExecute() {
            //show progress dialog while image is loading
            progress = new ProgressDialog(getContext());
            progress.setMessage("Please Wait....");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                }
            }, 1000);

            Toast.makeText(getContext(), "data updated", Toast.LENGTH_SHORT).show();
        }
    }


    //    Load Stock All List from Server
    private void storeVanMasterStock() {

        if (myDatabase.isExistMasterProducts()) {
            return;
        }

        if (!checkConnection()) {
            setErrorView(getContext().getString(R.string.no_internet), "", false);
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(getContext(), null, "Please wait...", false, false);

        final ArrayList<Product> products = new ArrayList<>();
        final ArrayList<State> state_array = new ArrayList<>();
        final ArrayList<District> district_array = new ArrayList<>();
        final ArrayList<SizelistMasterstock> size_array_master = new ArrayList<>();

        final JSONObject object = new JSONObject();
        try {
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG,"storeVanStockMaster Object   "+object);

        webGetVanMasterStock(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {
                state_array.clear();
                district_array.clear();
                Log.e(TAG,"storeVanStockMaster response   "+response);

                try {

                    if (response.getString("status").equalsIgnoreCase(
                            "success") && !response.isNull("Stocks")) {
                        JSONArray stockArray = response.getJSONArray("Stocks");
                        for (int i = 0; i < stockArray.length(); i++) {

                            JSONObject stockObject = stockArray.getJSONObject(i);
                            Product product = new Product();

                            //    Log.v(TAG,"storeVanStock   stockObject   "+stockObject);

                            product.setProductId(stockObject.getInt("product_id"));
                            product.setBarcode(stockObject.getString("product_barcode"));
                            product.setProductCode(stockObject.getString("product_code"));

                            //  Log.e("PRODUCT NAME", ""+stockObject.getString("product_name"));
                            product.setProductName(stockObject.getString("product_name"));

                            String bonuspercentage = stockObject.getString("product_bonus_percentage");

                            product.setProductBonus(Float.parseFloat(bonuspercentage));
                            product.setProduct_hsncode(stockObject.getString("product_hsncode"));
                            // printLog("bonus", ""+bonuspercentage);
                            try {
                                product.setSale_unitid(stockObject.getInt("sale_unit_id"));
                            }catch (Exception e){

                            }
                            String arb = "";
                            if (!stockObject.isNull("arabic_name"))
                                arb = stockObject.getString("arabic_name");
                            if (TextUtils.isEmpty(arb) || arb.equals("null"))
                                arb = " ";
                            product.setArabicName(arb);

                            product.setProductType(stockObject.getString("product_type_name"));


                            /////////////
                            ArrayList<Units> unit_array = new ArrayList<>();

                            JSONArray unitArray = stockObject.getJSONArray("Units_list");
                            for (int k=0; k<unitArray.length(); k++){

                                JSONObject unitObject = unitArray.getJSONObject(k);

                                Units unit = new Units();

                                unit.setUnitId(unitObject.getInt("id"));
                                unit.setUnitName(unitObject.getString("name"));
                                unit.setCon_factor(unitObject.getInt("con_factor"));
                                unit.setUnitPrice(unitObject.getString("unit_price"));
                                unit.setUnitWholesalePrice(unitObject.getString("wholesale_price"));
                                try {
                                    if (unit.getUnitId() == 10) {
                                        unit.setUnit_confactorkg(unitObject.getString("con_factor1"));
                                    }
                                }catch (Exception e){

                                }
                                unit_array.add(unit);
                            }

                            String gsonvalue = new Gson().toJson(unit_array);

                            //   Log.e("gsonvalue",gsonvalue);

                            product.setUnitslist(gsonvalue);


                            //haris added on 10-12-2020


                            if(state_array.size()==0) {
                                JSONArray statearray = stockObject.getJSONArray("State_list");
                                for (int k = 0; k < statearray.length(); k++) {

                                    JSONObject stateObject = statearray.getJSONObject(k);

                                    State state = new State();

                                    state.setStateid(stateObject.getInt("id"));
                                    state.setName(stateObject.getString("name"));

                                    state_array.add(state);
                                }
                            }





                            if(district_array.size()==0) {
                                JSONArray districtarray = stockObject.getJSONArray("District_list");
                                for (int k = 0; k < districtarray.length(); k++) {

                                    JSONObject distObject = districtarray.getJSONObject(k);

                                    District distr = new District();

                                    distr.setDistrictId(distObject.getInt("id"));
                                    distr.setDistrict(distObject.getString("name"));
                                    district_array.add(distr);



                                }
                            }

                            product.setBrandName(stockObject.getString("brand_name"));

                            float wholeSale = (float) stockObject.getDouble("product_wholesale_price");
                            float retailPrice = (float) stockObject.getDouble("product_mrp");//product_mrp
                            //haris added on 23-11-2020
                            float product_rate = (float) stockObject.getDouble("product_rate");
                            float vat = (float) stockObject.getDouble("product_vat");

                            product.setWholeSalePrice(wholeSale);
                            product.setRetailPrice(retailPrice);
                            product.setTax(vat);
                            product.setProduct_rate(product_rate);

                            product.setCost((float) stockObject.getDouble("product_cost"));
                            product.setPiecepercart(stockObject.getInt("piece_per_cart"));

                            product.setStockQuantity(stockObject.getInt("stock_quantity"));

                            products.add(product);
                        }

//                        float toatalStock = response.getLong("total_stock");
//                        int stockQuantity = response.getInt("total_quantity");

                    } else
                    {
                        Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.getMessage();

                    pd.dismiss();//
                    printLog(TAG, "storeVanStock Exception  " + e.getMessage());
                }

                if (!products.isEmpty()) {

                    for (Product p : products) {

                       // try {
                              Log.e("PDODUCT DB", ""+p.getProductName());
                            myDatabase.insertMasterStock(p);  //storeVanStock to local
//                        }catch (Exception e){
//                            Log.e("PRODUCT DB ERROR", ""+e.getMessage());
//                        }

                    }
                }
                if (!size_array_master.isEmpty()) {
                    for (SizelistMasterstock s : size_array_master) {

                        try {
                            Log.e("sizeiddd", ""+s.getSize_id());
                            myDatabase.insertsizemasterstock(s);
                        } catch (Exception e) {
                            Log.e("PRODUCT DB ERROR", "" + e.getMessage());
                        }
                    }

                }



//                if (!state_array.isEmpty()) {
//                    boolean deleteStatus = myDatabase.deleteTableRequest(REQ_STATE);
//                    for (State s : state_array) {
//
//                        try {
//
//                            myDatabase.insertstate(s);
//                        }catch (Exception e){
//                            Log.e("STATE DB ERROR", ""+e.getMessage());
//                        }
//
//                    }
//                }
//
//                if (!district_array.isEmpty()) {
//                    boolean deleteStatus = myDatabase.deleteTableRequest(REQ_DISTRICT);
//                    for (District d : district_array) {
//
//                        try {
//
//                            myDatabase.insertdistrict(d);
//                        }catch (Exception e){
//                            Log.e("DISTRICT DB ERROR", ""+e.getMessage());
//                        }
//
//                    }
//                }

                pd.dismiss();

                getAllRouteCustomer();

                //   getAllRouteCustomer();


            }

            @Override
            public void onErrorResponse(String error) {

                Toast.makeText(getContext(), "VanStock  " + error, Toast.LENGTH_SHORT).show();

                pd.dismiss();
            }
        }, object);
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String Text = "My current location is: " + "Latitude = "
                    + loc.getLatitude() + "\nLongitude = " + loc.getLongitude();

            str_Latitude = "" + loc.getLatitude();
            str_Longitude = "" + loc.getLongitude();

            //  tvNetTotal.setText("Lat : "+str_Latitude+" / Long : "+str_Longitude);
            /*Toast.makeText(getActivity(), Text, Toast.LENGTH_SHORT)
                    .show();*/
            Log.d("TAG", "Starting..");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getActivity(), "Gps Disabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getActivity(), "Gps Enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private class AsyncTaskLoadDataSync extends AsyncTask<JSONArray, String, String> {
        private final static String TAG = "AsyncTaskLoadImage";

        @Override
        protected String doInBackground(JSONArray... jsonArrays) {

            Sync_pending();

            return "";
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(getActivity(),"Updload Completed..!",Toast.LENGTH_LONG).show();
            // Toast.makeText(getContext(), "data updated", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                }
            }, 500);

        }
    }

    private void Sync_pending() {

        Log.e("inside","jj");
        this.saleList = myDatabase.getAllSales();
        this.quotationList = myDatabase.getAllQuotations();
        this.returnList = myDatabase.getAllReturns();
        this.receipts = myDatabase.getAllReceipts();
        this.chequereceipts = myDatabase.getAllChequeReceipts();


//        if (!quotationList.isEmpty())
//            placeQuotation();

           if (!saleList.isEmpty())
            placeOrder();
           else if (!returnList.isEmpty())
            placeReturn();
           else if (!receipts.isEmpty())
               paidReceipt();

    }

    private void placeReturn() {

        //   final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray returnArray = new JSONArray();

        try {
            for (Sales r : returnList) {
                final String strDate = getDateTime();
                final JSONObject returnObj = new JSONObject();


                returnObj.put("invoice_id", "0");
                returnObj.put(CUSTOMER_KEY, r.getCustomerId());
                returnObj.put("tax_total", r.getTaxAmount());
                returnObj.put("total_amount", r.getTotal());
                returnObj.put("return_date",strDate);
                returnObj.put("without_tax_total", r.getTaxable_total());
                returnObj.put("discount", r.getDiscount());
                returnObj.put("discount_total", r.getDiscountAmount());
                returnObj.put("latitude", r.getSaleLatitude());
                returnObj.put("longitude", r.getSaleLongitude());
                returnObj.put("invoice_type", r.getPayment_type());
                returnObj.put("return_invoiceno", r.getInvoiceCode());
                returnObj.put("executive_id", EXECUTIVE_ID);
                returnObj.put("round_off", getAmount(r.getRoundoff_value()));
                returnObj.put("amount_return", r.getPaid());
                JSONArray cartArray = new JSONArray();

                for (CartItem c : r.getCartItems()) {
                    double tax_prod = roundTwoDecimals(c.getTaxValue()*c.getPieceQuantity_nw());
                    // double unit_price = roundTwoDecimals(c.getProductPrice());

                    int tx_type =0;
                    double unit_price =c.getProductPrice();
                    if(c.getTax_type().equals("TAX_INCLUSIVE")){
                        tx_type =1;
                        unit_price = c.getProductTotal()/c.getPieceQuantity_nw();

                    }

                    unit_price =roundTwoDecimals(unit_price);
                    JSONObject obj = new JSONObject();

                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", unit_price);
                    obj.put("tax_rate", c.getTax());
                    obj.put("tax_amount", tax_prod);
                    obj.put("return_quantity", c.getPieceQuantity_nw());
                    obj.put("product_unit", c.getUnitselected());

                    obj.put("product_discount", c.getProductDiscount());
                    obj.put("product_total", getAmount(c.getProductTotal()));
                    obj.put("tax_type", tx_type);

                    cartArray.put(obj);
                }

                returnObj.put("ReturnedProduct", cartArray);

                returnArray.put(returnObj);

            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("SalesReturn", returnArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "placeReturn  object   " + object);

        webReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {


                //   printLog(TAG, "placeReturn  response   " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("Success")) {

                        // boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RETURN_TYPE);

                        //  printLog(TAG, "placeReturn  deleteStatus   " + deleteStatus);
                        for (Sales s : returnList) {
                            myDatabase.UpdateWoInvoiceUploadStatus("" + s.getInvoiceCode());
                        }



                    } else
                        Toast.makeText(getActivity(), "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                //pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    private void placeOrder() {

        // final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray saleArray = new JSONArray();

        try {
            for (Sales s : saleList) {

                Log.e("Upload Status", "//"+s.getUploadStatus());

                final JSONObject saleObj = new JSONObject();

                saleObj.put(CUSTOMER_KEY, s.getCustomerId());
                saleObj.put("bill_date", s.getDate());

                saleObj.put("total_amount",  getAmount(s.getWithTaxTotal()));

                saleObj.put("sale_type", s.getSaleType());
                saleObj.put("paid_amount", getAmount(s.getPaid()));
                saleObj.put("invoice_no", s.getInvoiceCode());
                saleObj.put("without_tax_total", getAmount(s.getWithoutTaxTotal()));

                saleObj.put("discount", getAmount(s.getDiscount()));
                saleObj.put("discount_total", getAmount(s.getDiscountAmount()));

                saleObj.put("tax_amount", getAmount(s.getTaxAmount()));
                saleObj.put("with_tax_total", getAmount(s.getWithTaxTotal()));
                saleObj.put("invoice_type", s.getPayment_type());
                saleObj.put("round_off", getAmount(s.getRoundoff_value()));
                saleObj.put("driver_name", sessionValue.getdrivername());
                saleObj.put("vehicle_no", sessionValue.getvehicleno());

                JSONArray cartArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {
                    int tx_type =0;
                    JSONObject obj = new JSONObject();
                    if(c.getTax_type().equals("TAX_INCLUSIVE")){
                        tx_type =1;
                    }


                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", getAmount(c.getProductPrice()));
//                    obj.put("product_quantity", c.getPieceQuantity());
                    obj.put("product_quantity", c.getTypeQuantity());

                    // obj.put("product_total", c.getProductPrice()*c.getPieceQuantity()); // c.getTotalPrice()
                    //obj.put("product_total", ""+getAmount(c.getProductTotal())); // c.getTotalPrice()
                    obj.put("product_total", ""+getAmount((c.getProductPrice()- c.getProductDiscount())*  c.getTypeQuantity())); // c.getTotalPrice()
                    //obj.put("product_bonus_percentage", c.getProductBonus());
                    obj.put("product_unit", c.getOrderType());
                    obj.put("tax", getAmount(c.getTax()));
                    obj.put("tax_amont", getAmount(c.getTaxValue()));
                    obj.put("product_discount", ""+getAmount(c.getProductDiscount()));
                    obj.put("tax_type",tx_type);
                    obj.put("description",c.getDescription());
                    obj.put("discount_per",c.getDisc_percentage());
                    obj.put("mfg_date",c.getMfg_date());
                    obj.put("foc",c.getFreeQty());

                    cartArray.put(obj);

                }

                saleObj.put("ordered_products", cartArray);

                saleArray.put(saleObj);
            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("Sale", saleArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("placeOrder cart object",""+object);

        webPlaceOrder(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "placeOrder  response   " + response);
                try {

                    if (response.getString("status").equalsIgnoreCase("Success")) {

                        for (Sales s : saleList) {
                            myDatabase.UpdateSalesUploadStatus("" + s.getInvoiceCode());
                        }
                        saleList.clear();
                        if (!returnList.isEmpty())
                            placeReturn();
                        else if (!receipts.isEmpty())
                            paidReceipt();
                        // printLog(TAG, "placeOrder  deleteStatus   " + deleteStatus);

                        // if (!quotationList.isEmpty())
                        //  placeQuotation();
//                        else if (!returnList.isEmpty())
//                            placeReturn();
//                        else if (!receipts.isEmpty())
//                            paidReceipt();
//                        else if (!chequereceipts.isEmpty())
//                            chequeReceipts();
//                        else if (!returnOfflineList.isEmpty())
//                            OfflineSaleReturn();
//                        else if (!noSales.isEmpty())
//                            noSaleRequest();

                    } else
                        Toast.makeText(getActivity(), "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //    pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                //   pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        }, object);
    }

    private void paidReceipt() {

        //final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);


        JSONObject object = new JSONObject();
        JSONArray receiptArray = new JSONArray();
        try {

            for (Receipt receipt : receipts) {

                final String receiptNo = receipt.getReceiptNo();
                final float receivableAmount = receipt.getReceivedAmount();
                int customerId = receipt.getCustomerId();


                JSONObject obj = new JSONObject();
                obj.put("amount", receivableAmount);
                obj.put("receipt_no", receiptNo);
                obj.put(CUSTOMER_KEY, customerId);
                obj.put("datetime", ""+receipt.getLogDate());

                obj.put("latitude", ""+receipt.getLatitude());
                obj.put("longitude", ""+receipt.getLongitude());
                obj.put("type", ""+receipt.getReceipt_type());
                obj.put("bank", ""+receipt.getBankname());
                obj.put("bankid", ""+receipt.getBankid());
                obj.put("voucherno", ""+receipt.getVoucherno());
                obj.put("reference_no", ""+receipt.getReference_no());


//                        if (receivableAmount != 0)
                receiptArray.put(obj);
            }


            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("PaidList", receiptArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        // printLog(TAG, "paidReceipt  object  " + object);
        Log.e(TAG, "paidReceipt  object  " + object);


        webPaidReceipt(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.v(TAG, "paidReceipt  response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        // boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RECEIPT_TYPE);

                        //Log.v(TAG, "paidReceipt  deleteStatus   " + deleteStatus);
                        Toast.makeText(getActivity(),"Updload Completed..!",Toast.LENGTH_LONG).show();
                        for (Receipt s : receipts) {
                            myDatabase.UpdatePaidReceiptUploadStatus(s.getReceiptNo());
                        }
                        if (!returnList.isEmpty()) {
                            placeReturn();
                        }

                    } else
                        Toast.makeText(getActivity(), "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                //pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);


    }

    private double roundTwoDecimals(double taxValue) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(taxValue));
    }
}






