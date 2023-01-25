package com.advanced.minhas.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.advanced.minhas.R;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import static com.advanced.minhas.config.ConfigKey.PASSWORD_KEY;
import static com.advanced.minhas.config.ConfigKey.USERNAME_KEY;
import static com.advanced.minhas.webservice.WebService.webGetSettingsParameter;

public class LoginActivity extends AppCompatActivity {

    int SPLASH_TIME_OUT = 3000;
    private TextInputEditText etUsername, etPassword;
    private TextInputLayout tilUsername, tilPassword;
    private Button fabLogin;
    private String TAG = "LoginActivity", Str_currency = "", Strconnection_mode = "", st_vatpervent = "";
    private ProgressDialog progressDialog;
    String str_company_ID="",str_company_name="",str_company_mobile="",str_company_address="",str_company_VatNo="",
    str_companyNo="",str_company_cust_care ="", str_company_addressline2="",str_company_name_arabic="",
    company_address_arabic="",company_address2_arabic="",str_company_showlogo="",str_company_bankname="",
    str_company_accno="",str_company_iban_no="";
    private SessionAuth sessionAuth;
    private SessionValue sessionValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilUsername = (TextInputLayout) findViewById(R.id.til_login_username);
        tilPassword = (TextInputLayout) findViewById(R.id.til_login_password);

        etUsername = (TextInputEditText) findViewById(R.id.editText_login_username);
        etPassword = (TextInputEditText) findViewById(R.id.editText_login_password);

        fabLogin = (Button) findViewById(R.id.fab_login);

        sessionAuth = new SessionAuth(LoginActivity.this);
        sessionValue = new SessionValue(LoginActivity.this);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);

        //        show input keyboard
        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        fabLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                get_Settings_controller();
             //   postLogin();

            }
        });
    }

    private void postLogin() {

        if (!isValidate())
            return;

        hideKeyboard();

        progressDialog.show();

        final String userName = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        final JSONObject object = new JSONObject();

        try {
            object.put(USERNAME_KEY, userName);
            object.put(PASSWORD_KEY, password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        fabLogin.setEnabled(false);

        WebService.webLogin(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG,"post Login object //"+object);

                Log.v(TAG,"postLogin  "+response);

                try {
                    if (response.getString("status").equals("Login Successful")) {

//                        String name = response.getString("executive_name");
                        String id = response.getString("id");
                        String userName = response.getString("name");
                       // String userRole = response.getString("role");

//                        JSONObject kafeelObj = response.getJSONObject("company");
//
//                        str_company_ID = "" + kafeelObj.getInt("id");
//                        str_company_name = kafeelObj.getString("name");
//                        str_company_mobile = kafeelObj.getString("mobile");
//                        str_company_address = kafeelObj.getString("address");
//                        str_company_VatNo = kafeelObj.getString("vat_no");
//                        str_companyNo = kafeelObj.getString("cr_no");
//                        str_company_cust_care = kafeelObj.getString("customer_care");





                        progressDialog.dismiss();
                        sessionAuth.createLoginSession(userName, password, id,"","Executive");
//                        sessionValue.createcompanydetails_login(str_company_ID, str_company_name, str_company_mobile, str_company_address,
//                                str_company_VatNo, str_companyNo, str_company_cust_care);

                        sessionValue.createControllSettings(Str_currency, Strconnection_mode,"piece",st_vatpervent); // piece

                    } else
                        showSnackBar(response.getString("status"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                fabLogin.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                showSnackBar(error);
            }
        }, object);
    }




    private void get_Settings_controller(){

        //  Log.e("get Settings","response Banks calling 1");

        if (!isValidate())
            return;

        progressDialog.show();

        webGetSettingsParameter(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Settings response", ":"+response);

                try {
                    Str_currency = response.getString("currency"); //
                    Strconnection_mode = response.getString("mode");
                     st_vatpervent = response.getString("vat_per");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                if (!Str_currency.isEmpty()) {

                    postLogin();
                }else {
                    showSnackBar("Error getting settings controls");
                }
            }
            @Override
            public void onErrorResponse(String error) {
                showSnackBar("Error getting settings controls");

            }
        });
    }


    //    validation
    private boolean isValidate() {

        boolean status = false;

        String userName = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tilUsername.setErrorEnabled(false);
        tilPassword.setErrorEnabled(false);

        if (TextUtils.isEmpty(userName)) {

            tilUsername.setError("Invalid Username");
          //  Toast.makeText(getApplicationContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
            status = false;

        } else if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Invalid Password");

          //  Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
            status = false;
        } else
            status = true;

        return status;
    }


    private void showSnackBar(String text) {
        progressDialog.dismiss();
        // The SMS quota for the project has been exceeded
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
        fabLogin.setEnabled(true);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



}
