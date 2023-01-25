package com.advanced.minhas.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Banks;
import com.advanced.minhas.model.Contra;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.webservice.WebService.webContraVoucher;
import static com.advanced.minhas.webservice.WebService.webGetCashInHand;


public class ContraVoucher extends AppCompatActivity implements View.OnClickListener {

    EditText edt_amount, edt_remarks, edt_externalvoucher;
    TextView txt_date, text_balance, text_cashinhand;
    AppCompatSpinner spinner_frombank, spinner_tobank;
    Button bttn_submit;
    private String EXECUTIVE_ID = "", dayRegId="", routeId="", str_cashinHand="0.00", CURRENCY="";

    String str_paidamount="", str_chequeNo="", str_Frombankid = "", str_Tobankid = "", str_cust_bank="";

    private ArrayList<Banks> banks = new ArrayList<Banks>();

    private ArrayList<String> array_bankname = new ArrayList<String>();
    private ArrayList<Integer> array_bankid = new ArrayList<Integer>();
    private ArrayList<Contra> array_contra = new ArrayList<>();
    MyDatabase myDatabase;
    private Calendar calendar;
    private SessionValue sessionValue;
    private SessionAuth sessionAuth;
    private ImageButton ibBack;
    float cashin_hand = 0, amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contra_voucher);

        txt_date = (TextView)findViewById(R.id.textView_contradate);
        text_balance= (TextView)findViewById(R.id.textView_balance);
        text_cashinhand = (TextView)findViewById(R.id.text_cashinhand);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);

        edt_amount = (EditText) findViewById(R.id.editText_contraPaid);
        edt_remarks = (EditText) findViewById(R.id.editText_remarks);
        edt_externalvoucher = (EditText) findViewById(R.id.editText_externalvoucher);

        spinner_frombank = (AppCompatSpinner)findViewById(R.id.spinner_from);
        spinner_tobank = (AppCompatSpinner)findViewById(R.id.spinner_to);
        bttn_submit = (Button) findViewById(R.id.button_contra_submit);

        myDatabase = new MyDatabase(this);
        calendar = Calendar.getInstance();
        sessionValue = new SessionValue(this);
        sessionAuth = new SessionAuth(this);

        try {

            CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);
            EXECUTIVE_ID = sessionAuth.getExecutiveId();
            dayRegId = sessionValue.getDayRegisterId();
            routeId = sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID);

            text_cashinhand.setText(""+getAmount(sessionAuth.getCashinHand())+" "+CURRENCY);


          // cashin_hand = Float.parseFloat(""+getAmount(sessionAuth.getCashinHand()));


            cashin_hand = sessionAuth.getCashinHand();



            Log.e("Route Id", "/"+routeId);
        }catch (Exception e){

        }

      //  GetCashInHand();


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat postFormater = new SimpleDateFormat("dd-MM-yyyy");
        String newDateStr = postFormater.format(c);
        txt_date.setText(""+newDateStr);

        Get_banks();

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDateClick();

            }
        });

        /*spinner_frombank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                str_Frombankid = ""+array_bankid.get(position);

                Log.e("From bank", ""+str_Frombankid);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        spinner_tobank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                str_Tobankid = ""+array_bankid.get(position);
                Log.e("To bank", ""+str_Tobankid);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bttn_submit.setOnClickListener(this);

    }

            @Override
            public void onClick(View v) {

                switch (v.getId()){

                    case  R.id.button_contra_submit:

                        if(validate()){

                            array_contra.clear();

                            Contra c = new Contra();

                            c.setDate(""+txt_date.getText().toString());
                            c.setFrom_bank("0");  // str_Frombankid
                            c.setTo_bank(str_Tobankid);
                            c.setExternal_Voucher(""+edt_externalvoucher.getText().toString().trim());
                            c.setRemarks(""+edt_remarks.getText().toString());
                            c.setAmount(""+edt_amount.getText().toString().trim());

                            array_contra.add(c);

                            if (checkConnection()) {

                                SaveContra();

                            }else {
                                Toast.makeText(getApplicationContext(), "Check connectivity", Toast.LENGTH_SHORT).show();
                            }
                        }

                    break;
                }
            }


    private void Get_banks(){

        banks.addAll(myDatabase.getAllBanks());

        array_bankname.clear();
        array_bankid.clear();

        array_bankname.add("<-- Select Bank -->");
        array_bankid.add(-1);

        Log.e("banks size", ""+banks.size());  // 167

        for (int b =0; b<banks.size(); b++){

            String bankid = ""+banks.get(b).getShown_in_contra();

            if(bankid.equals("1")) {

                array_bankname.add(banks.get(b).getBank_name());
                array_bankid.add(banks.get(b).getBank_id());
            }
        }

        Log.e("banks", ""+array_bankname);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark , array_bankname);
        adapter.setDropDownViewResource(R.layout.spinner_list);
      //spinner_frombank.setAdapter(adapter);

        /*ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_background_dark , array_bankname);
        adapter.setDropDownViewResource(R.layout.spinner_list);*/
        spinner_tobank.setAdapter(adapter);
      }

    private void onDateClick(){

        // Get Current Date
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(ContraVoucher.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                            // dia_tvChequeReceivedDate.setText(dateFormat.format(calendar.getTime()));
                            txt_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        }


    //    place quotation
    private void SaveContra() {

        final ProgressDialog pd = ProgressDialog.show(ContraVoucher.this, null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray ContraArray = new JSONArray();

        amount = 0;

        try {
            for (Contra s : array_contra) {

                final JSONObject contraObj = new JSONObject();

                contraObj.put("date", s.getDate());
                contraObj.put("from", s.getFrom_bank());
                contraObj.put("to", s.getTo_bank());
                contraObj.put("externalvoucher", s.getExternal_Voucher());
                contraObj.put("remarks", s.getRemarks());
                contraObj.put("amount", s.getAmount());
                amount = Float.parseFloat(s.getAmount());

                        ContraArray.put(contraObj);

            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("route_id", routeId);
            object.put("ContraVoucher", ContraArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog("Contra", "Contra  Object" + object);

        webContraVoucher(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog("Contra", "Contra  response   " + response);

                try {
                    if (response.getString("result").equalsIgnoreCase("success")) {



                        float cash = cashin_hand - amount ;
                        sessionAuth.updateCashinHand(cash);

                        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ContraVoucher.this, HomeActivity.class);
                        startActivity(intent);

                    } else
                        Toast.makeText(getApplicationContext(), "" + response.getString("result"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    //    Get Cash in Hand
    private void GetCashInHand() {

        JSONObject object = new JSONObject();
        try {
            object.put("route_id", routeId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Cash in hand object", ""+object.toString());

        final ProgressDialog pd = ProgressDialog.show(ContraVoucher.this, null, "Please wait...", false, false);

        webGetCashInHand(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", ""+response.toString());

                try {
                    str_cashinHand = response.getString("cash_in_hand");
                    text_cashinhand.setText(""+str_cashinHand);

                } catch (JSONException e) {
                    Toast.makeText(ContraVoucher.this, "Cash in Hand Unknown wrong..!", Toast.LENGTH_SHORT).show();
                    text_cashinhand.setText(""+str_cashinHand);
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                text_cashinhand.setText(""+str_cashinHand);
                Toast.makeText(ContraVoucher.this, error, Toast.LENGTH_SHORT).show();
            }
        }, object);

    }

    private boolean validate(){

        boolean status = false;

        if (edt_amount.getText().toString().isEmpty()){

            status = false;
            Toast.makeText(getApplicationContext(), "Please Enter Amount", Toast.LENGTH_SHORT).show();
        }else if (str_Frombankid.equals("-1")){
            status = false;
            Toast.makeText(getApplicationContext(), "Select From Account", Toast.LENGTH_SHORT).show();
        }else if (str_Tobankid.equals("-1")){
            status = false;
            Toast.makeText(getApplicationContext(), "Select To Account", Toast.LENGTH_SHORT).show();
        }else if (str_Frombankid.equals(str_Tobankid)) {
            status = false;
            Toast.makeText(getApplicationContext(), "From account and to account cannot be same", Toast.LENGTH_SHORT).show();
        }else if(edt_externalvoucher.getText().toString().isEmpty()){
            status = false;
            Toast.makeText(getApplicationContext(), "Please Enter external voucher", Toast.LENGTH_SHORT).show();
        }else {

            status = true;
        }

        return status;
        }


    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }

    }
