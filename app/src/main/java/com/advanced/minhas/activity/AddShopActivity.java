package com.advanced.minhas.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.District;
import com.advanced.minhas.model.Division;
import com.advanced.minhas.model.RouteGroup;
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

import static com.advanced.minhas.webservice.WebService.webAddCustomer;
import static com.advanced.minhas.webservice.WebService.webGetCustomerDivisionanType;

public class AddShopActivity extends AppCompatActivity {
    protected Button addShop;
    protected TextInputEditText etShopName, etArabicName,etContactPerson, etPhone, etMail, etAddress, etTrn,et_district,et_pincode,et_state,
    et_contactno,et_gstin,et_creditlimit,editText_openingbal;
    protected TextInputLayout tilShopName, tilContactPerson, tilPhone, tilMail, tilAddress, tiltrn,til_district,til_pincode,til_state,til_contactno,
    til_gstin,til_creditlimit,til_addNewShop_openingbal;
    float fl_creditlmt = 0;

    protected TextView tvRoute, tvGroup;
    protected AppCompatSpinner spinnerDivision, spinnerType, spinnerdistrict,spinnerstate,spinnergroup;

    private SessionAuth sessionAuth;
    private SessionValue sessionValue;
    MyDatabase myDatabase;
    private String TAG = "AddShopActivity";
    ArrayList<State>arr_states =new ArrayList<>();
    ArrayList<District>arr_district =new ArrayList<>();
    ArrayList<String>arr_st=new ArrayList<>();
    ArrayList<String>arr_stid=new ArrayList<>();
    ArrayList<String>arr_districts=new ArrayList<>();
    ArrayList<String>arr_districtsid=new ArrayList<>();
    ArrayList<RouteGroup>arr_groups=new ArrayList<>();
    ArrayList<String>arr_st_group=new ArrayList<>();
    ArrayList<String>arr_st_groupID=new ArrayList<>();
    String st_district_sel="0",st_state_sel ="0",st_group_sel="0" , st_openingbal = "" ,st_group="";
    CheckBox cb_gps;
    LocationManager locationManager;
    String latitude, longitude;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);
        myDatabase = new MyDatabase(getApplicationContext());
        spinnerDivision = (AppCompatSpinner) findViewById(R.id.spinner_addNewShop_division);
        spinnerType = (AppCompatSpinner) findViewById(R.id.spinner_addNewShop_type);
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        tvRoute = (TextView) findViewById(R.id.textView_addNewShop_route);
        tvGroup = (TextView) findViewById(R.id.textView_addNewShop_group);
        etShopName = (TextInputEditText) findViewById(R.id.editText_addNewShop_name);
        etArabicName= findViewById(R.id.editText_addNewShop_arabic_name);
        cb_gps= findViewById(R.id.cb_gps);

        etContactPerson = (TextInputEditText) findViewById(R.id.editText_addNewShop_contact_person);
        etPhone = (TextInputEditText) findViewById(R.id.editText_addNewShop_phone);
        etMail = (TextInputEditText) findViewById(R.id.editText_addNewShop_mail);
        etAddress = (TextInputEditText) findViewById(R.id.editText_addNewShop_address);
        etTrn = (TextInputEditText)findViewById(R.id.editText_addNewShop_TrnNo);
        et_creditlimit = (TextInputEditText)findViewById(R.id.editText_addNewShop_creditlimit);
        editText_openingbal = (TextInputEditText)findViewById(R.id.editText_addNewShop_openingbal);

       // et_district = (TextInputEditText)findViewById(R.id.editText_addNewShop_district);
        et_pincode = (TextInputEditText)findViewById(R.id.editText_addNewShop_pincode);
       // et_state = (TextInputEditText)findViewById(R.id.editText_addNewShop_state);
        et_contactno = (TextInputEditText)findViewById(R.id.editText_addNewShop_contact_contactno);
        et_gstin = (TextInputEditText)findViewById(R.id.editText_addNewShop_contact_gstin);

        tilShopName = (TextInputLayout) findViewById(R.id.til_addNewShop_name);
        tilContactPerson = (TextInputLayout) findViewById(R.id.til_addNewShop_contact_person);
        tilPhone = (TextInputLayout) findViewById(R.id.til_addNewShop_phone);
        tilMail = (TextInputLayout) findViewById(R.id.til_addNewShop_mail);
        tilAddress = (TextInputLayout) findViewById(R.id.til_addNewShop_address);
        tiltrn = (TextInputLayout)findViewById(R.id.til_addNewShop_trnNo);
        til_creditlimit = (TextInputLayout)findViewById(R.id.til_addNewShop_creditlimit);
        til_addNewShop_openingbal = (TextInputLayout)findViewById(R.id.til_addNewShop_openingbal);

       // til_district = (TextInputLayout)findViewById(R.id.til_addNewShop_district);
        til_pincode = (TextInputLayout)findViewById(R.id.til_addNewShop_pincode);
       // til_state = (TextInputLayout)findViewById(R.id.til_addNewShop_state);
        til_contactno = (TextInputLayout)findViewById(R.id.til_addNewShop_contact_contactno);
        til_gstin = (TextInputLayout)findViewById(R.id.til_addNewShop_contact_gstin);
        spinnerdistrict =findViewById(R.id.sp_addNewShop_district);
        spinnerstate =findViewById(R.id.sp_addNewShop_state);
        spinnergroup = findViewById(R.id.spinner_addNewShop_group);

        addShop = (Button) findViewById(R.id.button_addNewShop_add);

        this.sessionAuth = new SessionAuth(AddShopActivity.this);
        this.sessionValue = new SessionValue(AddShopActivity.this);

        tvGroup.setText(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_GROUP));

        tvRoute.setText(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE));

        addShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(cb_gps.isChecked()){
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        OnGPS();
                    } else {
                        getLocation();
                    }
                }else{
                    addCustomer("","");

                }


            }
        });

        spinnerdistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 st_district_sel = arr_districtsid.get(i);
                Log.e("st_district_sel",st_district_sel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                 st_state_sel = arr_stid.get(i);
                 Log.e("st_state_sel",st_state_sel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnergroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                st_group_sel = arr_st_groupID.get(i);
                st_group = spinnergroup.getSelectedItem().toString();
                Log.e("st_group_sel",st_group_sel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        arr_states=myDatabase.getAllState();
        arr_district=myDatabase.getAllDistricts();
        arr_groups.addAll(myDatabase.getAllGroups());
        Log.e("arr_statesszee",""+arr_states.size());

        arr_st.add("CUSTOMER STATE");
        arr_stid.add("0");

        arr_districts.add("CUSTOMER DISTRICT");
        arr_districtsid.add("0");

        for(State s : arr_states){
            Log.e("arr_statesszee",""+arr_states.size());
           // Log.e("state hr",""+s.getName());

            arr_st.add(s.getName());
            arr_stid.add(""+s.getStateid());
        }
        for(District d : arr_district){
           // Log.e("state District",""+d.getDistrict());

            arr_districts.add(d.getDistrict());
            arr_districtsid.add(""+d.getDistrictId());
        }
        arr_st_group.add("CUSTOMER GROUP");
        arr_st_groupID.add("0");

        for(RouteGroup g : arr_groups){
            // Log.e("state District",""+d.getDistrict());

            arr_st_group.add(g.getGroupName());
            arr_st_groupID.add(""+g.getGroupId());
        }



        loadCustomerType();
        set_stateand_district();

    }


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                AddShopActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                AddShopActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                addCustomer(latitude,longitude);

                Log.e("Location","Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
               // Toast.makeText(AddShopActivity.this,"Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude,Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void set_stateand_district() {
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark, arr_st) {
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

                  //  tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
                }

                return view;
            }
        };

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark, arr_districts) {
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

                    //tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
                }

                return view;
            }
        };

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark, arr_st_group) {
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

                    //  tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
                }

                return view;
            }
        };

        groupAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnergroup.setAdapter(groupAdapter);

        stateAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerstate.setAdapter(stateAdapter);

        districtAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerdistrict.setAdapter(districtAdapter);

    }




    private void loadCustomerType() {


        if (!checkConnection()) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        final ArrayList<Division> divisions = new ArrayList<>();
        final ArrayList<Type> types = new ArrayList<>();
        Division d0 = new Division();
        d0.setDivisionName("CUSTOMER DIVISION ");
        d0.setDivisionId(-1);
        divisions.add(0, d0);





        Type t0 = new Type();
        t0.setTypeName("CUSTOMER TYPE ");
        t0.setTypeId(-1);
        types.add(0, t0);

        final ProgressDialog pd = ProgressDialog.show(AddShopActivity.this, null, "Please wait...", false, false);

        webGetCustomerDivisionanType(new WebService.webObjectCallback() {
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

                                divisions.add(d);

                            }

                        }


                        if (!response.isNull("Types")) {

                            JSONArray typeArray = response.getJSONArray("Types");
                            for (int i = 0; i < typeArray.length(); i++) {

                                JSONObject typeObj = typeArray.getJSONObject(i);

                                Type t = new Type();

                                t.setTypeId(typeObj.getInt("id"));
                                t.setTypeName(typeObj.getString("name"));

                                types.add(t);


                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                setTypeSpinner(divisions, types);


                pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                setTypeSpinner(divisions, types);

                pd.dismiss();
                Toast.makeText(AddShopActivity.this, error, Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void setTypeSpinner(ArrayList<Division> divisions, ArrayList<Type> types) {



        if (divisions.size() == 1 && types.size() == 1) {
            Toast.makeText(AddShopActivity.this, "No Divisions and Types", Toast.LENGTH_SHORT).show();
        } else if (divisions.size() == 1) {

            Toast.makeText(AddShopActivity.this, "No Divisions", Toast.LENGTH_SHORT).show();
        } else if (types.size() == 1) {

            Toast.makeText(AddShopActivity.this, "No Types", Toast.LENGTH_SHORT).show();
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

                    //tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
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

                   // tv.setTextColor(getResources().getColor(R.color.colorGrayLight));
                }

                return view;
            }
        };


//order type spinner
//        ArrayAdapter<CharSequence> orderTypeAdapter = ArrayAdapter.createFromResource(this, R.array.cart_type, R.layout.spinner_background_dark);

        typeAdapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerType.setAdapter(typeAdapter);


    }


    private boolean isValidate() {
        boolean status = false;


        tilShopName.setErrorEnabled(false);
        tilContactPerson.setErrorEnabled(false);
        tilPhone.setErrorEnabled(false);
        tilMail.setErrorEnabled(false);
        tilAddress.setErrorEnabled(false);
        tiltrn.setErrorEnabled(false);


        try {


            Division d = (Division) spinnerDivision.getSelectedItem();
            Type t = (Type) spinnerType.getSelectedItem();

            if (d==null) {
                Toast.makeText(this, "Division not Loaded", Toast.LENGTH_SHORT).show();
                status = false;
            }else  if (t==null) {
                Toast.makeText(this, "Type not Loaded", Toast.LENGTH_SHORT).show();
                status = false;

            }else  if (st_group_sel.equals("0")) {
                Toast.makeText(this, "Group not Selected", Toast.LENGTH_SHORT).show();
                status = false;



//            }else  if (d.getDivisionId() == -1) {
//                    Toast.makeText(this, "Select Division", Toast.LENGTH_SHORT).show();
//                    status = false;

            } else if (t.getTypeId() == -1) {
                Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();
                status = false;

            } else if (TextUtils.isEmpty(etShopName.getText().toString().trim())) {
                tilShopName.setError("Shop Name Required..!");
                status = false;
            }
                else if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
                    tilAddress.setError("Address  Required..!");
                    status = false;
                }

//                else if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
//                    tilPhone.setError("Phone Number Required..!");
//                    status = false;

            //}
        else if (TextUtils.isEmpty(etContactPerson.getText().toString().trim())) {
                tilContactPerson.setError("Contact Person Required..!");
                status = false;
            }
            else if (TextUtils.isEmpty(et_contactno.getText().toString().trim())) {
                til_contactno.setError("Contact No  Required..!");
                status = false;
            }
//            else if (TextUtils.isEmpty(editText_openingbal.getText().toString().trim())) {
//                til_addNewShop_openingbal.setError("Opening Balance  Required..!");
//                status = false;
//            }

//            else if (TextUtils.isEmpty(et_gstin.getText().toString().trim())) {
//                if(t.getTypeName().equals("B2B")) {
//                    til_gstin.setError("VAT NO  Required..!");
//                    status = false;
//                }
//                else{
//                    status = true;
//                }
//            }
            else if (!TextUtils.isEmpty(etMail.getText().toString().trim()) && !isValidEmail(etMail.getText().toString().trim())) {


                if(t.getTypeName().equals("B2B")) {
                    tilMail.setError("Invalid Mail ..!");

                    status = false;
                }
                else{
                    status = true;
                }

            }else
                status = true;


                /*}else if(TextUtils.isEmpty(etTrn.getText().toString().trim())){
                tiltrn.setError("TRN Required");
                status = false;*/

        }catch (NullPointerException e){
            e.getMessage();
        }
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

    private void addCustomer(String latitude,String longitude) {
        String st_credtlmt = et_creditlimit.getText().toString();
        if(st_credtlmt.equals("")){
            st_credtlmt ="0";
        }
        try {
             fl_creditlmt = Float.parseFloat(et_creditlimit.getText().toString());
        }catch(Exception e){

        }

        st_openingbal = editText_openingbal.getText().toString().trim();
        if(st_openingbal.equals("")){
            st_openingbal= "0";
        }
        if (!isValidate())
            return;

        else if (!checkConnection()) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(AddShopActivity.this, null, "Please wait...", false, false);


        Division d = (Division) spinnerDivision.getSelectedItem();
        Type t = (Type) spinnerType.getSelectedItem();

        JSONObject object = new JSONObject();
        JSONObject customerObj = new JSONObject();
        try {

            customerObj.put("route_id", sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID));
            customerObj.put("customer_group_id", st_group_sel);
            customerObj.put("name", etShopName.getText().toString().trim());
            customerObj.put("trn_no", et_gstin.getText().toString().trim());
            customerObj.put("contact_person", etContactPerson.getText().toString().trim());
           // customerObj.put("mobile", etPhone.getText().toString().trim());
            customerObj.put("email", etMail.getText().toString().trim());
            customerObj.put("place", etAddress.getText().toString().trim());
            customerObj.put("customer_type_id", t.getTypeId());
            customerObj.put("group_id", st_group_sel);
           // customerObj.put("division_id", d.getDivisionId());
            customerObj.put("opening_balance", st_openingbal);
            customerObj.put("credit_limit",0);
            customerObj.put("description", "");
            customerObj.put("District",0);
            customerObj.put("latitude",latitude);
            customerObj.put("longitude",longitude);
            customerObj.put("Pincode", et_pincode.getText().toString().trim());
            customerObj.put("State",  st_state_sel);
            customerObj.put("Contact_No",  et_contactno.getText().toString().trim());


            object.put("Customer", customerObj);


            Log.e(TAG, "Add customer object " + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        webAddCustomer(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG, "Add customer response " + response);



                try {
                    if (response.getString("status").equals("success")) {

                      /*********************/
                        JSONObject shopObj = response.getJSONObject("customer");

                        Shop shop = new Shop();

                        int custId = shopObj.getInt("id");

                        shop.setShopId(custId);
                        shop.setShopCode(shopObj.getString("shope_code"));
                        shop.setShopName(shopObj.getString("name"));
//                        shop.setShopArabicName(shopObj.getString("arabic_name"));
                        shop.setShopArabicName(etArabicName.getText().toString().trim());
                        shop.setShopMail(shopObj.getString("email"));
                        shop.setShopMobile(shopObj.getString("mobile"));
                        shop.setShopAddress(shopObj.getString("address"));
                        shop.setLatitude(latitude);
                        shop.setLongitude(longitude);
                        shop.setState_id(TextUtils.isEmpty(st_state_sel) ? 0 : Integer.valueOf(st_state_sel));
                        shop.setCredit((float) shopObj.getDouble("credit"));
                        shop.setDebit((float) shopObj.getDouble("debit"));
                        shop.setRouteCode(shopObj.getString("route_code"));
                        shop.setCreditLimit(fl_creditlmt);
                        shop.setOutStandingBalance((float) shopObj.getDouble("opening_balance"));
                        shop.setOpeningbalance((float) shopObj.getDouble("opening_balance"));
                        shop.setContactPerson(etContactPerson.getText().toString());
                        shop.setVatNumber(et_gstin.getText().toString());
                        shop.setCreditlimit_register("0");
                        shop.setRoute(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE));
                        shop.setGroup(st_group_sel);
                        if(sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_GROUP_ID).equals(st_group_sel))
                        {
                            shop.setRegistered_status(true);
                            Log.e("if","ok");
                        }
                        else{
                            Log.e("else","ok");
                            shop.setRegistered_status(false);
                        }
                        shop.setGroup(st_group);
                        shop.setVisit(false);
                        boolean b = new MyDatabase(AddShopActivity.this).insertRegisteredCustomer(shop);

                        Toast.makeText(AddShopActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Toast.makeText(AddShopActivity.this, response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(AddShopActivity.this, error, Toast.LENGTH_SHORT).show();

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
