package com.advanced.minhas.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CustomerProduct;
import com.advanced.minhas.model.Division;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.State;
import com.advanced.minhas.model.Type;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;

public class EditCustomer extends AppCompatActivity {
    protected TextInputEditText etShopName,et_creditlimit, editText_contctno , editText_vatno,editText_creditperiod;
    protected TextInputLayout tilShopName,til_creditlimit ,til_contctno ,til_vatno,til_creditperiod;
    private Shop SELECTED_SHOP = null;
    private SessionAuth sessionAuth;
    protected Button editShop;
    String st_state_sel=" ",st_categ_sel ="",st_type_sel ="";
    int categid_sel=0 ,typeid_sel=0;

    protected TextInputEditText  etContactPerson, etPhone, etMail, etAddress, et_openingbal  ;
    protected TextInputLayout  tilContactPerson, tilPhone, tilMail, tilAddress, til_openingbal;

    protected TextView tvRoute, tvGroup;
    protected AppCompatSpinner spinnerDivision, spinnerType, spinnerState;


    private SessionValue sessionValue;
    private String TAG = "AddShopActivity";
    private String EXECUTIVE_ID = "", st_creditlmt ="" , st_opngbal ="";
    final ArrayList<State> statelist = new ArrayList<>();
    private MyDatabase myDatabase;
    float credit_tot =0 , fl_exec_creditlmt =0 ,fl_credit_differ = 0 , fl_openingbal = 0 ,fl_creditlmt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);
        myDatabase = new MyDatabase(getApplicationContext());
        tvRoute = (TextView) findViewById(R.id.textView_edtNewShop_route);
        tvGroup = (TextView) findViewById(R.id.textView_edtNewShop_group);
        tilShopName = (TextInputLayout) findViewById(R.id.til_edtNewShop_name);
        til_creditlimit = (TextInputLayout) findViewById(R.id.til_edtshop_creditlimit);
        til_creditperiod =  (TextInputLayout) findViewById(R.id.til_edtshop_creditperiod);
        tilContactPerson = (TextInputLayout) findViewById(R.id.til_edtNewShop_contact_person);
        tilPhone = (TextInputLayout) findViewById(R.id.til_edtNewShop_phone);
        tilMail = (TextInputLayout) findViewById(R.id.til_edtNewShop_mail);
        tilAddress = (TextInputLayout) findViewById(R.id.til_edtNewShop_address);
        til_openingbal = (TextInputLayout) findViewById(R.id.til_edtshop_openingbal);
        til_contctno = (TextInputLayout) findViewById(R.id.til_edtNewShop_contctno);
        til_vatno = (TextInputLayout) findViewById(R.id.til_edtNewShop_vatno);

        etShopName = (TextInputEditText) findViewById(R.id.editText_edtShop_name);
        etContactPerson = (TextInputEditText) findViewById(R.id.editText_edtNewShop_contact_person);
        etPhone = (TextInputEditText) findViewById(R.id.editText_edtNewShop_phone);
        etMail = (TextInputEditText) findViewById(R.id.editText_edtNewShop_mail);
        et_creditlimit = (TextInputEditText) findViewById(R.id.editText_edtShop_creditlimit);
        etAddress = (TextInputEditText) findViewById(R.id.editText_addNewShop_address);
        et_openingbal = (TextInputEditText) findViewById(R.id.editText_edtShop_openingbal);
        editText_contctno = (TextInputEditText) findViewById(R.id.editText_edtShop_openingbal);
        editText_vatno = (TextInputEditText) findViewById(R.id.editText_edtNewShop_vatno);
        editText_creditperiod =(TextInputEditText) findViewById(R.id.editText_edtShop_creditperiod);

        spinnerDivision = (AppCompatSpinner) findViewById(R.id.spinner_edtNewShop_division);
        spinnerType = (AppCompatSpinner) findViewById(R.id.spinner_edtNewShop_type);
        spinnerState = (AppCompatSpinner) findViewById(R.id.spinner_edtNewShop_state);

        this.sessionAuth = new SessionAuth(EditCustomer.this);
        this.sessionValue = new SessionValue(EditCustomer.this);

        editShop = findViewById(R.id.button_Shop_edit);
        tvGroup.setText(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_GROUP));

        tvRoute.setText(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE));


        try {
            SELECTED_SHOP = (Shop) getIntent().getSerializableExtra(SHOP_KEY);
        } catch (Exception e) {
            e.getStackTrace();
        }

        Log.e("contctperson",""+SELECTED_SHOP.getContactPerson());
        etShopName.setText(""+SELECTED_SHOP.getShopName());
        et_creditlimit.setText(""+SELECTED_SHOP.getCreditLimit());
        etAddress.setText(""+SELECTED_SHOP.getShopAddress());
        etMail.setText(""+SELECTED_SHOP.getShopMail());
        etPhone.setText(""+SELECTED_SHOP.getShopMobile());
        etContactPerson.setText(""+SELECTED_SHOP.getContactPerson());

       // editText_contctno.setText(""+SELECTED_SHOP.getco);
        editText_vatno.setText(""+SELECTED_SHOP.getVatNumber());

        et_openingbal.setText(""+SELECTED_SHOP.getOpeningbalance());
        editText_creditperiod.setText(""+SELECTED_SHOP.getCreditperiod());
//        if(SELECTED_SHOP.getOpeningbalance()>0){
//            et_openingbal.setFocusable(false);
//
//        }
//        else{
//            et_openingbal.setFocusable(true);
//        }


       // st_state_sel = SELECTED_SHOP.getState();
       // st_categ_sel = SELECTED_SHOP.getShop_category();
       // st_type_sel = SELECTED_SHOP.getType();


        try {
            EXECUTIVE_ID = sessionAuth.getExecutiveId();
        } catch (Exception e) {
            e.getMessage();
        }
        editShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // updatecustomer();
               // String st_executive_creditlimit = sessionValue.getexecutive_creditlimit();
               // Log.e("st_exe_creditlimit", "" + st_executive_creditlimit);
                if(TextUtils.isEmpty(et_creditlimit.getText().toString().trim())){
                    st_creditlmt = "0";
                }
                else{
                    st_creditlmt = et_creditlimit.getText().toString().trim();
                }
                if(TextUtils.isEmpty(et_openingbal.getText().toString().trim())){
                    st_opngbal = "0";
                }
                else{
                    st_opngbal = et_openingbal.getText().toString().trim();
                }
                updatecustomer();
                }
        });

        loadCustomerType();

    }



    private void loadCustomerType() {


        if (!checkConnection()) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        final ArrayList<Division> divisions = new ArrayList<>();
        final ArrayList<Type> types = new ArrayList<>();

//        Division d0 = new Division();
//        d0.setDivisionName("CUSTOMER CATEGORY ");
//        d0.setDivisionId(-1);
//        divisions.add(0, d0);

//        Type t0 = new Type();
//        t0.setTypeName("CUSTOMER TYPE ");
//        t0.setTypeId(-1);
//        types.add(0, t0);

        final ProgressDialog pd = ProgressDialog.show(EditCustomer.this, null, "Please wait...", false, false);

        WebService.webGetCustomerDivisionanType(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG, "loadCustomerType   " + response);

                try {
                    if (response.getString("status").equals("success")) {

                        if (!response.isNull("Divisions")) {

                            JSONArray divisionArray = response.getJSONArray("Divisions");
                            for (int i = 0; i < divisionArray.length(); i++) {
                                JSONObject divObj = divisionArray.getJSONObject(i);

                                Division d = new Division();


                                d.setDivisionId(divObj.getInt("id"));
                                d.setDivisionName(divObj.getString("name"));
                                if (d.getDivisionName().equals(st_categ_sel)) {
                                    categid_sel = d.getDivisionId();
                                    d.setDivisionName(st_categ_sel);
                                    d.setDivisionId(categid_sel);
                                    divisions.add(0,d);
                                }
                                else{
                                    divisions.add(d);
                                }



                            }

                        }


                        if (!response.isNull("Types")) {

                            JSONArray typeArray = response.getJSONArray("Types");
                            for (int i = 0; i < typeArray.length(); i++) {

                                JSONObject typeObj = typeArray.getJSONObject(i);

                                Type t = new Type();

                                t.setTypeId(typeObj.getInt("id"));
                                t.setTypeName(typeObj.getString("name"));

                                if (t.getTypeName().equals(st_type_sel)) {
                                    typeid_sel = t.getTypeId();
                                    t.setTypeName(st_type_sel);
                                    t.setTypeId(typeid_sel);
                                    //ypes.add(t);
                                    types.add(0, t);

                                }
                                else{
                                    types.add(t);
                                }




                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                for(Division d  : divisions){
//                    Log.e("divname",d.getDivisionName());
//                    Log.e("divname sel",st_categ_sel);
//
//
//                        Log.e("categid",""+d.getDivisionId());
//
//                        d.setDivisionName(st_type_sel);
//                        d.setDivisionId(categid_sel);
//                        divisions.add(d);
//
//                        //        Type t0 = new Type();
////        t0.setTypeName("CUSTOMER TYPE ");
////        t0.setTypeId(-1);
////        types.add(0, t0);
//
//                }

                setTypeSpinner(divisions, types);


                pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                setTypeSpinner(divisions, types);

                pd.dismiss();
                Toast.makeText(EditCustomer.this, error, Toast.LENGTH_SHORT).show();

            }
        });

    }
    public void set_staes(ArrayList<State> states){
        // Initializing an ArrayAdapter
        final ArrayAdapter<State> statedapter = new ArrayAdapter<State>(this, R.layout.spinner_background_dark, states) {
            @Override
            public boolean isEnabled(int position) {
                return position >= 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    //19
                    // Set the hint text color gray

                    //tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
                }

                return view;
            }
        };


//
        statedapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerState.setAdapter(statedapter);
    }

    private void setTypeSpinner(ArrayList<Division> divisions, ArrayList<Type> types) {


        if (divisions.size() == 0 && types.size() == 0) {
            Toast.makeText(EditCustomer.this, "No Divisions and Types", Toast.LENGTH_SHORT).show();
        }
//        else if (divisions.size() == 0) {
//
//            Toast.makeText(EditCustomer.this, "No Divisions", Toast.LENGTH_SHORT).show();
         else if (types.size() == 0) {

            Toast.makeText(EditCustomer.this, "No Types", Toast.LENGTH_SHORT).show();
        }



        // Initializing an ArrayAdapter
        final ArrayAdapter<Division> divisionAdapter = new ArrayAdapter<Division>(this, R.layout.spinner_background_dark, divisions) {
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


//
        divisionAdapter.setDropDownViewResource(R.layout.spinner_list);


        spinnerDivision.setAdapter(divisionAdapter);


        // Initializing an ArrayAdapter
        final ArrayAdapter<Type> typeAdapter = new ArrayAdapter<Type>(this, R.layout.spinner_background_dark, types) {
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


//order type spinner
//        ArrayAdapter<CharSequence> orderTypeAdapter = ArrayAdapter.createFromResource(this, R.array.cart_type, R.layout.spinner_background_dark);

        typeAdapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerType.setAdapter(typeAdapter);

        //loadStates();
    }


    private boolean isValidate() {
        boolean status = false;


        tilShopName.setErrorEnabled(false);
        tilContactPerson.setErrorEnabled(false);
        tilPhone.setErrorEnabled(false);
        tilMail.setErrorEnabled(false);
        tilAddress.setErrorEnabled(false);


       // try {

            Division d = (Division) spinnerDivision.getSelectedItem();
            Type t = (Type) spinnerType.getSelectedItem();

//            if (d==null) {
//                Toast.makeText(this, "Division not Loaded", Toast.LENGTH_SHORT).show();
//                status = false;
//            }else

            if (t==null) {
                Toast.makeText(this, "Type not Loaded", Toast.LENGTH_SHORT).show();
                status = false;

            //}
//            else  if (d.getDivisionId() == -1) {
//                Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
//                status = false;

            } else if (t.getTypeId() == -1) {
                Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();
                status = false;

            } else if (TextUtils.isEmpty(etShopName.getText().toString().trim())) {
                tilShopName.setError("Shop Name Required..!");
                status = false;
            } else if (TextUtils.isEmpty(etContactPerson.getText().toString().trim())) {
                tilContactPerson.setError("Contact Person Required..!");
                status = false;
            } else if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
                tilPhone.setError("Phone Number Required..!");
                status = false;
            } else if (!TextUtils.isEmpty(etMail.getText().toString().trim()) && !isValidEmail(etMail.getText().toString().trim())) {
                tilMail.setError("Invalid Mail ..!");
                status = false;
            } else if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
                tilAddress.setError("Address  Required..!");
                status = false;
//            } else if (TextUtils.isEmpty(et_creditlimit.getText().toString().trim())) {
//                    til_creditlimit.setError("Credit Limit  Required..!");
//            status = false;
            } else
                status = true;


                /*}else if(TextUtils.isEmpty(etTrn.getText().toString().trim())){
                tiltrn.setError("TRN Required");
                status = false;*/

//        }catch (NullPointerException e){
//            e.getMessage();
//        }
        return status;
    }


    // validating email
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void updatecustomer() {

        if (!isValidate())
            return;

        else if (!checkConnection()) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }


        final ProgressDialog pd = ProgressDialog.show(EditCustomer.this, null, "Please wait...", false, false);


        final Division d = (Division) spinnerDivision.getSelectedItem();
        final Type t = (Type) spinnerType.getSelectedItem();

        final State s = (State) spinnerState.getSelectedItem();


        final JSONObject object = new JSONObject();
        JSONObject customerObj = new JSONObject();
        Log.e(TAG, "Add customer object " + customerObj);
        try {

            customerObj.put("route_id", sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID));
            customerObj.put("customer_group_id", sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_GROUP_ID));
            customerObj.put("name", etShopName.getText().toString().trim());
            //customerObj.put("arabic_name", ""+etArabicName.getText().toString().trim());
            //customerObj.put("trn_no", ""+etTrn.getText().toString().trim());
            customerObj.put("contact_person", etContactPerson.getText().toString().trim());
            customerObj.put("mobile", etPhone.getText().toString().trim());
            customerObj.put("email", etMail.getText().toString().trim());
            customerObj.put("place", etAddress.getText().toString().trim());

            customerObj.put("customer_type_id", t.getTypeId());
            //customerObj.put("division_id", d.getDivisionId());   //d.getDivisionId());
            customerObj.put("credit_limit", st_creditlmt);
            customerObj.put("opening_balance", et_openingbal.getText().toString());
            customerObj.put("description", "demo");
            customerObj.put("vatno",editText_vatno.getText().toString().trim());
            customerObj.put("credit_period", editText_creditperiod.getText().toString().trim());
            //customerObj.put("state_id", ""+s.getState_id());
            object.put("Customer", customerObj);
            //  object.put("state_id", s.getState_id());
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("Customerid", SELECTED_SHOP.getShopId());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Edt customer object " + object);
        WebService.webEditCustomer(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

               // price_list.clear();
                Log.e(TAG, "Edt customer response " + response);

                try {
                    if (response.getString("status").equals("Success")) {
                        Log.e(TAG, "Edt customer response1 " + response);
                        /*********************/
                        JSONObject shopObj = response.getJSONObject("customer");

                        Shop shop = new Shop();

                        int custId = shopObj.getInt("id");

                        shop.setShopId(custId);
                        shop.setShopCode(shopObj.getString("shope_code"));
                        shop.setShopName(shopObj.getString("name"));
//                        shop.setShopArabicName(shopObj.getString("arabic_name"));
                        // shop.setShopArabicName(etArabicName.getText().toString().trim());
                        shop.setShopMail(shopObj.getString("email"));
                        shop.setShopMobile(shopObj.getString("mobile"));
                        shop.setShopAddress(shopObj.getString("address"));

//                        shop.setCredit((float) shopObj.getDouble("credit"));
//                        shop.setDebit((float) shopObj.getDouble("debit"));
                        shop.setRouteCode(shopObj.getString("route_code"));
                        shop.setContactPerson(etContactPerson.getText().toString().trim());
                       // shop.setState(shopObj.getString("State"));

                        shop.setCreditLimit((float) shopObj.getDouble("credit_limit"));
                        shop.setShopMobile(etPhone.getText().toString().trim());
                        shop.setVatNumber(shopObj.getString("vat_no"));
                        shop.setCreditperiod(editText_creditperiod.getText().toString());

                        shop.setCustomer_type(""+t.getTypeName());
                        shop.setOpeningbalance((float) shopObj.getDouble("opening_balance"));

                        shop.setOutStandingBalance(0);
                        //haris added on 26-11-2020
                       // shop.setShop_category(shopObj.getString("Division"));

                        shop.setVisit(false);

                        ArrayList<CustomerProduct> selling_list = new ArrayList<>();

                        CustomerProduct sellingPrice = new CustomerProduct();
                        selling_list.add(sellingPrice);



                     //   JSONArray arraypricelist = shopObj.getJSONArray("stock");


                        // shop.setProducts(selling_list);
                        double outstandingbal =myDatabase.get_outstandingbal_byshopid(shop.getShopId());
                        boolean b = new MyDatabase(EditCustomer.this).update_customer(shop,outstandingbal);

                        //storePricelistToOffline(price_list);
                        Toast.makeText(EditCustomer.this, "Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Log.e(TAG, "Edit customer response1 " + response);
                    Toast.makeText(EditCustomer.this, response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Log.e("error","123");
                }

                pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(EditCustomer.this, error, Toast.LENGTH_SHORT).show();

            }
        }, object);

    }



    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }

}

