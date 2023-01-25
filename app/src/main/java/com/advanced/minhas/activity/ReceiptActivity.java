package com.advanced.minhas.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvReceiptAdapter;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Banks;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Transaction;
import com.advanced.minhas.model.chequeReceipt;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.textwatcher.TextValidator;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_BILLWISE;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_TYPE;
import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigKey.VIEW_DATAVIEW;
import static com.advanced.minhas.config.ConfigKey.VIEW_ERRORVIEW;
import static com.advanced.minhas.config.ConfigKey.VIEW_PROGRESSBAR;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.RECEIPT_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dbDateFormat;
import static com.advanced.minhas.config.Generic.generateNewNumber;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getRoundOfAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_LATITUDE;
import static com.advanced.minhas.session.SessionValue.PREF_LONGITUDE;
import static com.advanced.minhas.webservice.WebService.webPaidReceipt;

public class ReceiptActivity extends AppCompatActivity implements View.OnClickListener, OnNotifyListener {

    private String EXECUTIVE_ID = "", dayRegId = "",str_payment_type="";

    private TextView tvToolBarShopName, tvTotalAmount,tvTotalCollection;
    private EditText etPaidAmount, dia_tvPaidAmount, dia_etCustomerbank,edt_voucherno ,edt_referno;
    LinearLayout ly_bankname;
    TextView dia_tvTotalAmount, dia_tvChequenumber,
                 dia_tvChequeReceivedDate, dia_tvChequeClearingDate; //dia_tvBalanceAmount
    float outstandingamount=0, cash_inhand = 0;
    String str_customerbankname ="";

    private ProgressBar progressBar;
    private ErrorView errorView;
    private ImageButton ibBack;
    private Button btnFinish, btn_submit_cheque;
    private RecyclerView recyclerView;
    private AppCompatSpinner spinner_paymentmode, spinner_companybank,spinner_bankname;
    private Calendar calendar;
    SimpleDateFormat dateFormat;
    Dialog dialog;

    private ArrayList<Receipt> receipts = new ArrayList<Receipt>();
    private ArrayList<Receipt> receiptssend = new ArrayList<Receipt>();
    ArrayList<chequeReceipt> ChequeReceipts  =new ArrayList<>();

    private ArrayList<Banks> banks = new ArrayList<Banks>();

    private ArrayList<String> array_bankname = new ArrayList<String>();
    private ArrayList<Integer> array_bankid = new ArrayList<Integer>();

    private RvReceiptAdapter adapter;

    private String TAG = "ReceiptActivity";
    String str_paymentmode="";

    private ViewGroup viewReceiptHistory,viewReceiptPayment;

    private Shop SELECTED_SHOP = null;

    private MyDatabase myDatabase;
    private SessionValue sessionValue;
    private SessionAuth sessionAuth;
    float balance_fl =0;

    String str_paidamount="", str_chequeNo="", str_cmpnybankid = "", str_cust_bank="",
            str_cheque_date ="", str_clearingdate="", CURRENCY="";
    private String provider;
    LocationManager locationManager;

    private String str_Latitude = "0", str_Longitude = "0" ,
            latitude_session="",longitude_session="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);

        tvToolBarShopName = (TextView) findViewById(R.id.textView_toolbar_shopNameAndCode);
        ly_bankname = findViewById(R.id.ly_bankname);
        btnFinish = (Button) findViewById(R.id.button_receipt_finish);
        tvTotalAmount = (TextView) findViewById(R.id.textView_receipt_totalAmount);
        tvTotalCollection = (TextView) findViewById(R.id.textView_receipt_collectionTotal);
        etPaidAmount = (EditText) findViewById(R.id.editText_receipt_paid);
        edt_voucherno = findViewById(R.id.editText_voucherno);
        edt_referno = findViewById(R.id.edt_referno);
        spinner_paymentmode = (AppCompatSpinner)findViewById(R.id.spinner_receipt_payment_mode);
        spinner_bankname = (AppCompatSpinner)findViewById(R.id.spinner_receipt_bank);
        errorView = (ErrorView) findViewById(R.id.errorView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_receipt);

        viewReceiptHistory = (ViewGroup) findViewById(R.id.view_receipt_lists);
        viewReceiptPayment = (ViewGroup) findViewById(R.id.view_receipt_paid);

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();

        this.sessionValue =new SessionValue(ReceiptActivity.this);
        myDatabase = new MyDatabase(ReceiptActivity.this);
        adapter = new RvReceiptAdapter(receipts);
        sessionAuth = new SessionAuth(ReceiptActivity.this);

        try {

            SELECTED_SHOP = (Shop) getIntent().getSerializableExtra(SHOP_KEY);
            EXECUTIVE_ID = new SessionAuth(ReceiptActivity.this).getExecutiveId();
            dayRegId = sessionValue.getDayRegisterId();
            cash_inhand = sessionAuth.getCashinHand();

            CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);

        } catch (Exception e) {
            e.getStackTrace();
        }

        if (SELECTED_SHOP == null) {
            finish();
            return;
        }
        Get_bank();
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(itemAnimator);


        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
//                .color(getResources().getColor(R.color.divider))
                .build());


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);


        tvToolBarShopName.setText(String.valueOf(SELECTED_SHOP.getShopName() + "\t" + SELECTED_SHOP.getShopCode()));
        tvToolBarShopName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvToolBarShopName.setSelected(true);

        getInvoiceList();

        ibBack.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        final float bal = myDatabase.getOutstandingAmountBalance(SELECTED_SHOP.getShopId());
        //Add to textWatcher



        etPaidAmount.addTextChangedListener(new TextValidator(etPaidAmount) {
            @Override
            public void validate(TextView textView, String text) {

                try {

                    float paid = TextUtils.isEmpty(text) ? 0 : Float.valueOf(text);

                    final float b = getRoundOfAmount(bal);

                    tvTotalAmount.setText(String.valueOf("" + getAmount(b-paid) ));

                } catch (NumberFormatException e) {

                    Log.v(TAG, "paid  balance Exception "+e.getMessage());
                }
            }
        });

//        spinner_paymentmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                str_paymentmode = spinner_paymentmode.getSelectedItem().toString();
//
//                if (str_paymentmode.equals("Cash")){
//
//                    btnFinish.setEnabled(true);
//
//                }else {
//
//                    btnFinish.setEnabled(false);
//                    Show_Dialog_cheque();
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        spinner_paymentmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                str_paymentmode = spinner_paymentmode.getSelectedItem().toString();

                if (str_paymentmode.equals("Cash")){
                    str_payment_type="1";
                    str_cmpnybankid ="0";
                    ly_bankname.setVisibility(View.GONE);
                    btnFinish.setEnabled(true);

                }else {
                    if(str_paymentmode.equals("Cheque")){
                        str_payment_type="3";
                        btnFinish.setEnabled(false);
                        Show_Dialog_cheque();
                        str_cmpnybankid ="";
                        ly_bankname.setVisibility(View.GONE);
                    }
                    if(str_paymentmode.equals("Bank")){
                        str_payment_type="2";
                        btnFinish.setEnabled(true);
                        ly_bankname.setVisibility(View.VISIBLE);
                        // Show_Dialog_cheque();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_bankname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                str_cmpnybankid = ""+array_bankid.get(position);
                str_customerbankname = spinner_bankname.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Log.v(TAG, "storePaidReceipt time  first  "+getDateTime());
    }

    private void Get_bank() {

        banks.addAll(myDatabase.getAllBanks());

        Log.e("banks size", ""+banks.size());

        for (int b =0; b<banks.size(); b++){
            array_bankname.add(banks.get(b).getBank_name());
            array_bankid.add(banks.get(b).getBank_id());
        }

        Log.e("banks", ""+array_bankname);



        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_background_dark , array_bankname);
        adapter1.setDropDownViewResource(R.layout.spinner_list);
        spinner_bankname.setAdapter(adapter1);
    }

    private void Show_Dialog_cheque(){

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_receipt);

     //  Get_banks();

        TextView rippleViewClose =(TextView) dialog.findViewById(R.id.close);
        dia_tvTotalAmount = (TextView)dialog.findViewById(R.id.textView_receipt_outstandingAmount);
        dia_tvPaidAmount = (EditText)dialog.findViewById(R.id.editText_receipt_amountPaid);
    //  dia_tvBalanceAmount = (TextView)dialog.findViewById(R.id.textView_receipt_balanceAmount);
        dia_tvChequenumber = (TextView)dialog.findViewById(R.id.editText_receipt_checkNo);
        spinner_companybank = (AppCompatSpinner)dialog.findViewById(R.id.spinner_receipt_company_bank);
        dia_etCustomerbank = (EditText) dialog.findViewById(R.id.editText_receipt_customerBank);
        dia_tvChequeReceivedDate = (TextView)dialog.findViewById(R.id.textView_receipt_cheque_receivedDate);
        dia_tvChequeClearingDate = (TextView)dialog.findViewById(R.id.textView_receipt_cheque_clearingDate);
        btn_submit_cheque = (Button)dialog.findViewById(R.id.button_receipt_submitApproval);

        Get_banks();

        dialog.setTitle("Cheque Details");
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations_SmileWindow;//R.style.DialogAnimations_SmileWindow;
        dialog.setCancelable(false);

        dia_tvTotalAmount.setText(String.valueOf("" + getAmount(outstandingamount) + " " + CURRENCY));

        balance_fl = myDatabase.getOutstandingAmountBalance(SELECTED_SHOP.getShopId());

       /* dia_tvPaidAmount.addTextChangedListener(new TextValidator(dia_tvPaidAmount) {
            @Override
            public void validate(TextView textView, String text) {
                try {

                   float paid = TextUtils.isEmpty(text) ? 0 : Float.valueOf(text);

                    final float b = getRoundOfAmount(balance_fl);

                    dia_tvBalanceAmount.setText(String.valueOf("" + getAmount(b-paid) ));

                } catch (NumberFormatException e) {

                    Log.v(TAG, "paid  balance Exception "+e.getMessage());
                }
            }
        });
*/
        dia_tvChequeReceivedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDateClick("RecieveDate");

            }
        });

        dia_tvChequeClearingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDateClick("Clearing");

            }
        });

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                btnFinish.setEnabled(true);

                ArrayAdapter companyBankAdapter = new ArrayAdapter(ReceiptActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.payment_type_array));
                spinner_paymentmode.setAdapter(companyBankAdapter);

            }
        });

        spinner_companybank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                str_cmpnybankid = ""+array_bankid.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_submit_cheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_paidamount = dia_tvPaidAmount.getText().toString().trim();
                str_chequeNo = dia_tvChequenumber.getText().toString().trim();
                str_cust_bank = dia_etCustomerbank.getText().toString().trim();
                str_cheque_date = dia_tvChequeReceivedDate.getText().toString();
                str_clearingdate = dia_tvChequeClearingDate.getText().toString();

                if (!str_paidamount.isEmpty()&&!str_chequeNo.isEmpty()&&!str_cust_bank.isEmpty()
                        &&!str_cheque_date.isEmpty()&&!str_clearingdate.isEmpty()) {

                    final String receiptNo = generateNewNumber(sessionValue.getReceiptCode(SELECTED_SHOP.getRouteCode()));

                    chequeReceipt cheque_receipt = new chequeReceipt();

                    cheque_receipt.setReceiptNo(SELECTED_SHOP.getRouteCode() + receiptNo);
                    cheque_receipt.setReceivedAmount(Float.parseFloat(str_paidamount));
                    cheque_receipt.setChequeNo(str_chequeNo);
                    cheque_receipt.setCustomerId(SELECTED_SHOP.getShopId());
                    cheque_receipt.setCompanyBankId(Integer.parseInt(str_cmpnybankid));
                    cheque_receipt.setCustomerBank(str_cust_bank);
                    cheque_receipt.setChequeDate(str_cheque_date);
                    cheque_receipt.setClearingdate(str_clearingdate);
                    cheque_receipt.setUploadStatus("N");
                    cheque_receipt.setLogDate(getDateTime());

                  //    boolean isExist = myDatabase.isExistInvoice(cheque_receipt.getReceiptNo());

                        myDatabase.insertChequeReceipt(cheque_receipt);

                        sessionValue.storeReceiptCode(SELECTED_SHOP.getRouteCode(), receiptNo);

                        boolean status = myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_RECEIPT_BILLWISE, "","","");

                        //Log.d(TAG,"storePaidReceipt   "+receiptNo+" received "+rec.getReceivedAmount()+"  currect Balance   "+rec.getCurrentBalanceAmount());
                        if (status) {
                            /*Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();*/

                            ChequeReceipts = myDatabase.getAllChequeReceipts();

                            if (isNetworkConnected()) {
                                chequeReceipt(ChequeReceipts);
                            }
                            else
                                {

                                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                     /* Intent intent = new Intent(ReceiptActivity.this, ReceiptPreviewActivity.class);
                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_OUTSTANDING_RECEIPT);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                        intent.putExtra(RECEIPT_KEY, rec);
                        startActivity(intent);
                        finish(); */

                        } else
                            Toast.makeText(getApplicationContext(), "insertion failed", Toast.LENGTH_SHORT).show();
                    }
                   else {
                   Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void getInvoiceList() {

        receipts.clear();

        if (SELECTED_SHOP.getShopId() != 0) {

            receipts.addAll(myDatabase.getCustomerWiseReceipt(SELECTED_SHOP.getShopId()));

//            printLog(TAG, "getCustomerWiseReceiptIds    "+  myDatabase. getCustomerWiseReceiptIds(SELECTED_SHOP.getShopId()));

            float balance=myDatabase.getOutstandingAmountBalance(SELECTED_SHOP.getShopId());
            float collection=myDatabase.getInvoiceCollectionAmount(SELECTED_SHOP.getShopId());

            outstandingamount = balance;

            tvTotalAmount.setText(String.valueOf("" + getAmount(balance) + " " + CURRENCY));

            tvTotalCollection.setText(String.valueOf("Total Collection : " + getAmount(collection) + " " + CURRENCY));

          //  viewReceiptPayment.setVisibility((balance==0)?View.GONE:View.VISIBLE);

        }
        if (receipts.isEmpty())
            setErrorView();
        else {
            updateViews(VIEW_DATAVIEW);
            adapter.notifyDataSetChanged();

        }
    }

    private void onDateClick(String tag){

        // Get Current Date
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (tag.equals("RecieveDate")) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(ReceiptActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                           // dia_tvChequeReceivedDate.setText(dateFormat.format(calendar.getTime()));
                            dia_tvChequeReceivedDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        else {

                    final DatePickerDialog datePickerDialog = new DatePickerDialog(ReceiptActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            calendar.set(year, monthOfYear, dayOfMonth);

                          //  dia_tvChequeClearingDate.setText(dateFormat.format(calendar.getTime()));
                             dia_tvChequeClearingDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);

                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();
        }
    }

    //set ErrorView
    private void setErrorView() {

        updateViews(VIEW_ERRORVIEW);
        errorView.setConfig(ErrorView.Config.create()
                .title("No Receipts")
//                .subtitle(subTitle)
                .retryVisible(false)
                .build());

    }

    public void updateViews(int viewCode) {

        switch (viewCode) {

            case VIEW_PROGRESSBAR:
                progressBar.setVisibility(View.VISIBLE);
                viewReceiptHistory.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);

                break;

            case VIEW_DATAVIEW:

                progressBar.setVisibility(View.GONE);
                viewReceiptHistory.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);

                break;

            case VIEW_ERRORVIEW:

                progressBar.setVisibility(View.GONE);
                viewReceiptHistory.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);

                break;

        }

    }

    private boolean isValidate() {
        boolean status = false;

        float paid = TextUtils.isEmpty(etPaidAmount.getText().toString().trim()) ? 0 : Float.valueOf(etPaidAmount.getText().toString().trim());

//        paid= getRoundOfAmount(paid);
        float bal = myDatabase.getOutstandingAmountBalance(SELECTED_SHOP.getShopId());

        bal= getRoundOfAmount(bal);

        etPaidAmount.setError(null);

//        if (TextUtils.isEmpty(etPaidAmount.getText().toString().trim()) || paid <= 0) {
//            etPaidAmount.setError("Invalid Amount..!");
//            status = false;
//        } else if (bal < paid) {
//            etPaidAmount.setError("Paid Amount is Over..!");
//            status = false;
//        } else
            status = true;
        return status;
    }

    //    updateReceipt
    private void storePaidReceipt() {

        Log.e("clicked", "1");

        if (!isValidate())
            return;

        Log.e("clicked", "2");
        final String receiptNo = generateNewNumber(sessionValue.getReceiptCode(SELECTED_SHOP.getRouteCode()));

//      Log.d(TAG, "storePaidReceipt   receiptCode   " + sessionValue.getReceiptCode(SELECTED_SHOP.getRouteCode()) + "   no....   " + receiptNo);
        float paid = TextUtils.isEmpty(etPaidAmount.getText().toString().trim()) ? 0 : Float.valueOf(etPaidAmount.getText().toString().trim());

        float cash = cash_inhand + paid ;
        sessionAuth.updateCashinHand(cash);

//      paid= getRoundOfAmount(paid);
        float bal = myDatabase.getOutstandingAmountBalance(SELECTED_SHOP.getShopId());

        Log.e("clicked", "3 :"+bal);

        bal= getRoundOfAmount(bal);

        Receipt rec = new Receipt();

        rec.setReceiptNo(SELECTED_SHOP.getRouteCode() + receiptNo);
        rec.setCustomerId(SELECTED_SHOP.getShopId());
        rec.setLogDate(getDateTime());
        rec.setReceivedAmount(paid);
        rec.setCurrentBalanceAmount(bal - paid);
        rec.setUploadStatus("N");
        rec.setLatitude(latitude_session);
        rec.setLongitude(longitude_session);
        rec.setCustomer_bank(str_customerbankname);
        rec.setReceipt_type(str_payment_type);
        rec.setPayment_mode(str_cmpnybankid);
        rec.setBankid(Integer.parseInt(str_cmpnybankid));
        rec.setBankname(str_customerbankname);
        rec.setVoucherno(edt_voucherno.getText().toString());
        rec.setReference_no(edt_referno.getText().toString());
        Log.e("", "Received Amount :"+paid);

        boolean isExist = myDatabase.isExistInvoice(rec.getReceiptNo());

        if (!isExist){
            if (myDatabase.insertReceipt_nw(rec))
            {
                Transaction t=new Transaction(SELECTED_SHOP.getShopId(),rec.getReceivedAmount(),0);
                if (!myDatabase.updateCustomerBalance(t))

                    Toast.makeText(getApplicationContext(), "Customer data update failure", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(getApplicationContext(), "Insertion Failed", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getApplicationContext(), "Invoice Number Already Exist", Toast.LENGTH_SHORT).show();

        sessionValue.storeReceiptCode(SELECTED_SHOP.getRouteCode(), receiptNo);

        boolean status = myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_RECEIPT_TYPE, "","","");
        //Log.d(TAG,"storePaidReceipt   "+receiptNo+" received "+rec.getReceivedAmount()+"  currect Balance   "+rec.getCurrentBalanceAmount());
        if (status) {
            receiptssend.clear();
            receiptssend = myDatabase.getAllReceipts();
            Log.e("sizee",""+receiptssend.size());

            if (isNetworkConnected()) {

                paidReceipt(rec);

            }else
            {
            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ReceiptActivity.this, ReceiptActivity_N.class);

            intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_OUTSTANDING_RECEIPT);
            intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
            intent.putExtra(RECEIPT_KEY, rec);
            startActivity(intent);
            finish();

            }

        } else
            Toast.makeText(this, "insertion failed", Toast.LENGTH_SHORT).show();
    }

  private void Get_banks(){

        banks.addAll(myDatabase.getAllBanks());

        Log.e("banks size", ""+banks.size());

        for (int b =0; b<banks.size(); b++){
            array_bankname.add(banks.get(b).getBank_name());
            array_bankid.add(banks.get(b).getBank_id());
        }

        Log.e("banks", ""+array_bankname);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark , array_bankname);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner_companybank.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageButton_toolbar_back:
                onBackPressed();
                break;

            case R.id.button_receipt_finish:
                get_latitudeandlongitude();

                if(!str_Latitude.equals("0")&& !str_Longitude.equals("0")){
                    sessionValue.save_latitude_and_longitude(str_Latitude,str_Longitude);
                }
                latitude_session = sessionValue.get_map_details().get(PREF_LATITUDE);  //latitude get from session
                longitude_session = sessionValue.get_map_details().get(PREF_LONGITUDE);  //longitude get from session

                storePaidReceipt();

                break;

        }
    }

    private void get_latitudeandlongitude() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Requires Permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                LocationListener mlocListener = new MyLocationListener();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 61000, 250,
                        mlocListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Error with location manager", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onNotified() {


//        tvPaidAmount.setText(getAmount(adapter. getNetTotal()));

    }


    /**
     * Hides the soft keyboard
     */
    private void hideKeyboard() {
        //Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        }
    }


    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    private static String getDateTime() {
        Date date = new Date();
        return dbDateFormat.format(date);
    }


    //    paid receipt
    private void paidReceipt(final Receipt rece) {

        final ProgressDialog pd = ProgressDialog.show(this, null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        JSONArray receiptArray = new JSONArray();
        try {

            for (Receipt receipt : receiptssend) {

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

                receiptArray.put(obj);
            }


            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("PaidList", receiptArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        printLog(TAG, "paidReceipt  object  " + object);

        webPaidReceipt(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, "paidReceipt  response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        for (Receipt s : receiptssend) {
                            myDatabase.UpdatePaidReceiptUploadStatus(s.getReceiptNo());
                        }

                        Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ReceiptActivity.this, ReceiptPreviewActivity.class);

                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_OUTSTANDING_RECEIPT);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                        intent.putExtra(RECEIPT_KEY, rece);


                        startActivity(intent);
                        finish();

                    } else{

                    }

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

    //   Cheque Receipt

    private void chequeReceipt(final ArrayList<chequeReceipt> chequereceipts){
        final ProgressDialog pd = ProgressDialog.show(ReceiptActivity.this, null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        JSONArray receiptarray = new JSONArray();

        try {
            for (chequeReceipt c : chequereceipts) {

                JSONObject obj = new JSONObject();
                obj.put("receipt_no", c.getReceiptNo());
                obj.put("amount", c.getReceivedAmount());
                obj.put("cheque_no", c.getChequeNo());
                obj.put(CUSTOMER_KEY, c.getCustomerId());
                obj.put("company_bank_id", c.getCompanyBankId());
                obj.put("customer_bank", c.getCustomerBank());
                obj.put("received_date", c.getCreatedDate());
                obj.put("clearing_date", c.getClearingdate());
                obj.put("cheque_date", c.getChequeDate());
                obj.put("type", "3");
                obj.put("datetime", c.getLogDate());

                receiptarray.put(obj);
            }
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("PaidList", receiptarray);

            pd.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "cheque Receipt object  " + object);


        webPaidReceipt(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, "paidReceipt  response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        for (chequeReceipt s : chequereceipts){

                            myDatabase.UpdateChequeReceiptUploadStatus(s.getReceiptNo());
                        }

                        Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    } else
                        Toast.makeText(getApplicationContext(), "Receipt "+response.getString("status"), Toast.LENGTH_SHORT).show();
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




    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
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
            Toast.makeText(getApplicationContext(), "Gps Disabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


}
