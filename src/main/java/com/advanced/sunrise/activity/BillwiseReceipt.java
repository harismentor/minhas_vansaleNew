package com.advanced.minhas.activity;

import static com.advanced.minhas.config.AmountCalculator.getTaxPrice;
import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_RECEIPT_BILLWISE;
import static com.advanced.minhas.config.ConfigKey.REQ_RETURN_TYPE;
import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigValue.BILLWISE_RECEIPT_KEY;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.INVOICE_RETURN_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dbDateFormat;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.webservice.WebService.webBillwiseReceipt;
import static com.advanced.minhas.webservice.WebService.webGetAllPendingInvoices;
import static com.advanced.minhas.webservice.WebService.webOfflineReturn;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvBillwiseAdapter;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Banks;
import com.advanced.minhas.model.BillwiseReceiptMdl;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Invoice;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Transaction;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.textwatcher.TextValidator;
import com.advanced.minhas.webservice.WebService;

import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BillwiseReceipt extends AppCompatActivity {
    public static TextView tv_balance, tv_totalamnt,tv_mode;
    TextView tv_receiptinvoices;
    EditText edt_totalamnt, edt_referno;
    String[] invoicestringarry = new String[50];
    double total_return = 0;
    String st_advncd_amnt = "", returnNumber = "" , st_return_page="" ,routecode="";
    String st_selected_mode = "";
    String str_paidamount = "", str_chequeNo = "", str_cmpnybankid = "", str_cust_bank = "",str_cmpnybank ="",
            str_cheque_date = "", str_clearingdate = "", CURRENCY = "", str_bank_refno = "",
            st_receipt_type = "" , st_invoiceno = "" ,st_date ="";
    Dialog dialog;
    ArrayList<BillwiseReceiptMdl> bill_list = new ArrayList<>();
    ArrayList<BillwiseReceiptMdl> bills_sumbit = new ArrayList<>();
    double dbl_totalamnt = 0;
    RecyclerView rec_list;
    TextView tv_advanceamnt;
    TextView dia_tvTotalAmount, dia_tvChequenumber,
            dia_tvChequeReceivedDate, dia_tvChequeClearingDate; //dia_tvBalanceAmount
    LinearLayout ly_chequedate, ly_refno, ly_chequeno;
    EditText dia_etCustomerbank, dia_etbankrefno;
    Button button_submit, bbutton_cancel, btn_submit_cheque;
    ArrayList<Sales> returnOfflineList = new ArrayList<>();

    private SessionValue sessionValue;
    private Calendar calendar;
    private Shop SELECTED_SHOP = null;
    private Sales SELECTED_RETURN = null;
    private Invoice SELECTED_INVOICE = null;
    private String EXECUTIVE_ID = "", dayRegId = "";
    private RvBillwiseAdapter adapter;
    private AppCompatSpinner spinner_paymentmode, spinner_companybank;
    private String TAG = "BillwiseReceipt";
    // ArrayList<PendingReceipt> invoices=new ArrayList<>();
    private ArrayList<Banks> banks = new ArrayList<Banks>();
    private MyDatabase myDatabase;
    private ArrayList<String> array_bankname = new ArrayList<String>();
    private ArrayList<Integer> array_bankid = new ArrayList<Integer>();
    private SessionAuth sessionAuth;
    private float pref_bonus = 0, cash_inhand = 0;
    //private RvReturnInvoiceAdapter returnProductAdapter;

    private static String getDateTime() {
        Date date = new Date();
        return dbDateFormat.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billwise_receipt);
        sessionValue = new SessionValue(BillwiseReceipt.this);
        sessionAuth = new SessionAuth(BillwiseReceipt.this);
        myDatabase = new MyDatabase(BillwiseReceipt.this);
        calendar = Calendar.getInstance();
        rec_list = findViewById(R.id.rec_list);
        edt_totalamnt = findViewById(R.id.edt_totalamnt);
        edt_referno = findViewById(R.id.edt_referno);
        tv_advanceamnt = findViewById(R.id.tv_advncamnt);
        tv_totalamnt = findViewById(R.id.tv_totalamnt);
        tv_balance = findViewById(R.id.tv_balance);
        button_submit = findViewById(R.id.button_bill_submit);
        bbutton_cancel = findViewById(R.id.button_bill_cancel);
        spinner_paymentmode = findViewById(R.id.spinner_receipt_payment_mode);
        tv_mode = findViewById(R.id.tv_mode);
        dayRegId = sessionValue.getDayRegisterId();

        try {
            SELECTED_SHOP = (Shop) getIntent().getSerializableExtra(SHOP_KEY);

            SELECTED_SHOP = (Shop) getIntent().getSerializableExtra(SHOP_KEY);
            EXECUTIVE_ID = new SessionAuth(BillwiseReceipt.this).getExecutiveId();
            // SELECTED_INVOICE =(Invoice) (getIntent().getSerializableExtra(INVOICE_VALUE_KEY));
            total_return = (double) getIntent().getSerializableExtra("return_total");
            returnNumber = (String) getIntent().getSerializableExtra("returnNumber");
            routecode = (String) getIntent().getSerializableExtra("route_code");
            st_return_page = getIntent().getStringExtra("retrn_page");
            dayRegId = sessionValue.getDayRegisterId();
            SELECTED_RETURN = (Sales) (getIntent().getSerializableExtra(INVOICE_RETURN_VALUE_KEY));

            Log.e("invoice_amount", "" + total_return);
            Log.e("st_return_page",st_return_page);


        } catch (Exception e) {

        }
        bill_list.clear();
        myDatabase.deleteTableRequest(REQ_RECEIPT_BILLWISE);

        get_invoicelist(SELECTED_SHOP.getShopId());
        if(returnNumber.length()>0){
            spinner_paymentmode.setVisibility(View.GONE);
            tv_mode.setVisibility(View.GONE);
        }
        else{
            spinner_paymentmode.setVisibility(View.VISIBLE);
            tv_mode.setVisibility(View.VISIBLE);
        }


        adapter = new RvBillwiseAdapter(bill_list, dbl_totalamnt, SELECTED_SHOP.getShopId(),returnNumber);
        rec_list.setLayoutManager(new LinearLayoutManager(BillwiseReceipt.this));
        rec_list.setAdapter(adapter);

        // sessionValue.save_receipt_totalamnt(""+0);
        // double dbl_total_amnt = TextUtils.isEmpty(sessionValue.get_billwise_total_amnt().get(PREF_TOTAL_AMNT_RECEIPT)) ? 0 : Double.valueOf(sessionValue.get_billwise_total_amnt().get(PREF_TOTAL_AMNT_RECEIPT));
        double dbl_total_amnt = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Double.valueOf(edt_totalamnt.getText().toString());
        double dbl_advncd_amnt = TextUtils.isEmpty(tv_advanceamnt.getText().toString()) ? 0 : Double.valueOf(tv_advanceamnt.getText().toString());
        double dbl_total = roundTwoDecimals(dbl_total_amnt + dbl_advncd_amnt);
        if (dbl_total == 0) {
            edt_totalamnt.setText("");
        } else {
            edt_totalamnt.setText("" + dbl_total);
        }
        edt_totalamnt.addTextChangedListener(new TextValidator(edt_totalamnt) {
            @Override
            public void validate(TextView textView, String text) {
                double dbl_total_amnt = 0;

                dbl_totalamnt = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Double.valueOf(edt_totalamnt.getText().toString());
                adapter.notifyDataSetChanged();
                Log.e("dbl_totalamnt", "" + dbl_totalamnt);
                setrecyclerview();

            }
        });
        tv_totalamnt.addTextChangedListener(new TextValidator(tv_totalamnt) {
            @Override
            public void validate(TextView textView, String text) {
                double dbl_amnt = TextUtils.isEmpty(tv_totalamnt.getText().toString()) ? 0 : Double.valueOf(tv_totalamnt.getText().toString());
               // double dbl_total_amntwithadvnc = roundTwoDecimals(dbl_advncd_amnt + dbl_amnt);
                Log.e("dbl_total_amntwithadvnc", "" + dbl_amnt);
                sessionValue.save_receipt_totalamnt("" + dbl_amnt);
            }
        });
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bills_sumbit.clear();
                returnOfflineList.clear();
                bills_sumbit = myDatabase.getbillwise_receipts();
                Log.e("bill_submit", "" + bills_sumbit.size());
                st_receipt_type = "";
                if (total_return > 0) {

                    returnOfflineList.add(SELECTED_RETURN);
                    st_receipt_type = "billwise";
                    if(st_return_page.equals("with")) {
                        OfflineSaleReturn(SELECTED_RETURN);
                    }
                    if(st_return_page.equals("without")) {
                        OfflineSaleReturn_without(SELECTED_RETURN);
                    }

                } else {
                    if (myDatabase.get_billamount_total() > 0) {
                        st_receipt_type = "";
                        save_billwise_receipt(bills_sumbit);
                    } else {

                        Toast.makeText(getApplicationContext(), "Paid Amount is Zero..!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        bbutton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabase.deleteTableRequest(REQ_RECEIPT_BILLWISE);
                bill_list.clear();
                sessionValue.save_receipt_totalamnt("" + 0);
                edt_totalamnt.setText("");
                get_invoicelist(SELECTED_SHOP.getShopId());
            }
        });
        spinner_paymentmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st_selected_mode = spinner_paymentmode.getSelectedItem().toString();
                if (st_selected_mode.equals("Cash")) {

                    //btnFinish.setEnabled(true);

                } else {

                    if (myDatabase.get_billamount_total() > 0) {
                        //btnFinish.setEnabled(false);
                        Show_Dialog_cheque();
                    } else {
                        ArrayAdapter companyBankAdapter = new ArrayAdapter(BillwiseReceipt.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.payment_types_array));
                        spinner_paymentmode.setAdapter(companyBankAdapter);
                        Toast.makeText(getApplicationContext(), "Paid Amount is Zero..!", Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setrecyclerview();


    }

    private void OfflineSaleReturn_without(final Sales selected_return) {

        final ProgressDialog pd = ProgressDialog.show(BillwiseReceipt.this, null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray returnArray = new JSONArray();
        final String strDate = getDateTime();
        String st_date="";
        try {
            for (Sales r : returnOfflineList) {

                final JSONObject returnObj = new JSONObject();
                st_invoiceno = r.getInvoiceCode();
                Log.e("st_invoiceno",st_invoiceno);
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

                JSONArray receiptArray = new JSONArray();
                for (BillwiseReceiptMdl receipt : bills_sumbit) {
                    st_date = receipt.getBill_date();
                    double dbl_invoiceamnt = TextUtils.isEmpty(receipt.getInvoice_amnt()) ? 0 : Double.valueOf(receipt.getInvoice_amnt().toString());
                    double dbl_enteredamnt = TextUtils.isEmpty(receipt.getReceipt_enetered()) ? 0 : Double.valueOf(receipt.getReceipt_enetered().toString());
                    double bal_mnt = (dbl_invoiceamnt);
                    JSONObject obj = new JSONObject();
                    st_date = receipt.getBill_date();

                    if(dbl_enteredamnt>0) {
                        obj.put("paid_amount", receipt.getReceipt_enetered());
                        obj.put("invoice_no", receipt.getInvoiceno());
                        obj.put("invoice_balance", bal_mnt);
                        obj.put("invoice_amount", receipt.getInvoice_amnt());
                        obj.put("sale_id", receipt.getSale_id());

                        receiptArray.put(obj);
                    }


                }
                double bal_total = 0;
                try {
                    double dbl_total_amnt = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Double.valueOf(edt_totalamnt.getText().toString());
                    double dbl_amnt = TextUtils.isEmpty(tv_balance.getText().toString()) ? 0 : Double.valueOf(tv_balance.getText().toString());
                    double bill_total = myDatabase.get_billamount_total();
                    bal_total = dbl_total_amnt - bill_total;
                    bal_total = roundTwoDecimals(bal_total);
                } catch (Exception e) {

                }


                returnObj.put("PaidList", receiptArray);

                returnObj.put("mode", st_selected_mode);
                returnObj.put(CUSTOMER_KEY, SELECTED_SHOP.getShopId());
                returnObj.put("reference_no", "" + edt_referno.getText().toString());
                returnObj.put("total_amount", "" + edt_totalamnt.getText().toString());
                returnObj.put("balance", "" + bal_total);
                returnObj.put("date_time", st_date);

                if (st_selected_mode.equals("Cheque")) {
                    returnObj.put("cheque_date", str_cheque_date);
                    returnObj.put("cheque_no", str_chequeNo);
                    returnObj.put("cheque_bank", str_cmpnybankid);
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

        printLog(TAG, "placeReturnwithout  object   " + object);

        WebService.webReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {


                //   printLog(TAG, "placeReturn  response   " + response);
                Log.e("responce",""+response);

                try {
                    if (response.getString("status").equalsIgnoreCase("Success")) {

                        // boolean deleteStatus = myDatabase.deleteTableRequest(REQ_RETURN_TYPE);
                        // boolean s = myDatabase.UpdateReturnwithoutInvoiceNo(st_invoiceno);
                        //  printLog(TAG, "placeReturn  deleteStatus   " + deleteStatus);
                        float fl_total = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Float.valueOf(edt_totalamnt.getText().toString());
                        Transaction t1=new Transaction(SELECTED_SHOP.getShopId(),fl_total,0);

                        bill_list.clear();
                        myDatabase.deleteTableRequest(REQ_RECEIPT_BILLWISE);
                        get_invoicelist(SELECTED_SHOP.getShopId());
                        Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ReturnPreviewActivity.class);
                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN);
                        intent.putExtra(INVOICE_RETURN_VALUE_KEY, selected_return);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                        startActivity(intent);
                        finish();

                    } else
                        Toast.makeText(BillwiseReceipt.this, "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(BillwiseReceipt.this, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    private void Show_Dialog_cheque() {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_billwise_receipt);

        //  Get_banks();

        TextView rippleViewClose = (TextView) dialog.findViewById(R.id.close);

        dia_tvChequenumber = (TextView) dialog.findViewById(R.id.editText_receiptbillwise_checkNo);
        spinner_companybank = dialog.findViewById(R.id.spinner_receiptbillwise_company_bank);
        //dia_etCustomerbank = (EditText) dialog.findViewById(R.id.editText_receiptbillwise_customerBank);
        dia_tvChequeReceivedDate = (TextView) dialog.findViewById(R.id.textView_receiptbillwise_cheque_receivedDate);
        dia_tvChequeClearingDate = (TextView) dialog.findViewById(R.id.textView_receiptbillwise_cheque_clearingDate);
        btn_submit_cheque = (Button) dialog.findViewById(R.id.button_receiptbillwise_submitApproval);
        dia_etbankrefno = dialog.findViewById(R.id.edt_receiptbillwise_bank_refno);
        ly_chequedate = dialog.findViewById(R.id.ly_chequedate);
        ly_refno = dialog.findViewById(R.id.ly_refno);
        ly_chequeno = dialog.findViewById(R.id.lychequeno);
        Get_banks();

        dialog.setTitle("Cheque Details");
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations_SmileWindow;//R.style.DialogAnimations_SmileWindow;
        dialog.setCancelable(false);

        // dia_tvTotalAmount.setText(String.valueOf("" + getAmount(outstandingamount) + " " + CURRENCY));

        if (st_selected_mode.equals("Bank")) {
            ly_chequedate.setVisibility(View.GONE);
            ly_refno.setVisibility(View.VISIBLE);
            ly_chequeno.setVisibility(View.GONE);

        } else {
            ly_chequedate.setVisibility(View.VISIBLE);
            ly_refno.setVisibility(View.GONE);
            ly_chequeno.setVisibility(View.VISIBLE);

        }

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
                // btnFinish.setEnabled(true);

                ArrayAdapter companyBankAdapter = new ArrayAdapter(BillwiseReceipt.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.payment_types_array));
                spinner_paymentmode.setAdapter(companyBankAdapter);

            }
        });

        spinner_companybank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                str_cmpnybankid = "" + array_bankid.get(position);
                str_cmpnybank = "" + array_bankname.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_submit_cheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // str_paidamount = dia_tvPaidAmount.getText().toString().trim();
                str_chequeNo = "";
                str_cust_bank = "";
                str_cheque_date = "";
                str_clearingdate = "";
                str_bank_refno = "";

                str_chequeNo = dia_tvChequenumber.getText().toString().trim();
                // str_cust_bank = dia_etCustomerbank.getText().toString().trim();
                str_cheque_date = dia_tvChequeReceivedDate.getText().toString();
                str_clearingdate = dia_tvChequeClearingDate.getText().toString();
                str_bank_refno = dia_etbankrefno.getText().toString();
                dialog.dismiss();


            }
        });

        dialog.show();
    }

    private void onDateClick(String tag) {

        // Get Current Date
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (tag.equals("RecieveDate")) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(BillwiseReceipt.this,
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
        } else {

            final DatePickerDialog datePickerDialog = new DatePickerDialog(BillwiseReceipt.this,
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

    private void Get_banks() {

        banks.addAll(myDatabase.getAllBanks());

        Log.e("banks size", "" + banks.size());

        for (int b = 0; b < banks.size(); b++) {
            array_bankname.add(banks.get(b).getBank_name());
            array_bankid.add(banks.get(b).getBank_id());
        }

        Log.e("banks", "" + array_bankname);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark, array_bankname);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner_companybank.setAdapter(adapter);
    }

    private void save_billwise_receipt(final ArrayList<BillwiseReceiptMdl> arr_receipts) {

        final ProgressDialog pd = ProgressDialog.show(this, null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();
        final JSONArray receiptArray = new JSONArray();
        try {

            for (BillwiseReceiptMdl receipt : arr_receipts) {

                double dbl_invoiceamnt = TextUtils.isEmpty(receipt.getInvoice_amnt()) ? 0 : Double.valueOf(receipt.getInvoice_amnt().toString());
                double dbl_enteredamnt = TextUtils.isEmpty(receipt.getReceipt_enetered()) ? 0 : Double.valueOf(receipt.getReceipt_enetered().toString());
                double bal_mnt = (dbl_invoiceamnt);
                if(dbl_enteredamnt>0) {

                    JSONObject obj = new JSONObject();
                    st_date = receipt.getBill_date();


                    obj.put("paid_amount", dbl_enteredamnt);
                    obj.put("invoice_no", receipt.getInvoiceno());
                    obj.put("invoice_balance", bal_mnt);
                    obj.put("invoice_amount", receipt.getInvoice_amnt());
                    obj.put("sale_id", receipt.getSale_id());
                    obj.put("discount", receipt.getDiscount());


                    receiptArray.put(obj);
                }
            }

            double dbl_total_amnt = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Double.valueOf(edt_totalamnt.getText().toString());
            double dbl_amnt = TextUtils.isEmpty(tv_balance.getText().toString()) ? 0 : Double.valueOf(tv_balance.getText().toString());
            double bill_total = myDatabase.get_billamount_total();
            double bal_total = dbl_total_amnt - bill_total;

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("mode", st_selected_mode);
            object.put(CUSTOMER_KEY, SELECTED_SHOP.getShopId());
            object.put("reference_no", "" + edt_referno.getText().toString());
            object.put("total_amount", "" + edt_totalamnt.getText().toString());
            object.put("balance", "" + bal_total);
            object.put("date_time", st_date);
            object.put("PaidList", receiptArray);
            if (st_selected_mode.equals("Cheque")) {
                object.put("cheque_date", str_cheque_date);
                object.put("cheque_no", str_chequeNo);
                object.put("cheque_bank", str_cmpnybankid);
            }
            if (st_selected_mode.equals("Bank")) {
                object.put("bank_id", str_cmpnybankid);
                object.put("bank_referno", str_bank_refno);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "paidReceipt  object  " + object);

        final String finalSt_date = st_date;
        webBillwiseReceipt(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG, "paidReceipt  response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        for (BillwiseReceiptMdl lst : arr_receipts) {
                            myDatabase.insertbillwisereceiptdetails(arr_receipts,""+SELECTED_SHOP.getShopId(),edt_totalamnt.getText().toString(),"","","","");
                        }
//                        for (Receipt s : receiptssend) {
//                            myDatabase.UpdatePaidReceiptUploadStatus(s.getReceiptNo());
//                        }
                        boolean status = myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_RECEIPT_BILLWISE, "","","");
                        float fl_total = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Float.valueOf(edt_totalamnt.getText().toString());
                        Transaction t=new Transaction(SELECTED_SHOP.getShopId(),fl_total,0);
                        if (!myDatabase.updateCustomerBalance(t)){

                        }
                        myDatabase.deleteTableRequest(REQ_RECEIPT_BILLWISE);
                        Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                        bill_list.clear();


                        sessionValue.save_receipt_totalamnt("" + 0);
                        ArrayAdapter companyBankAdapter = new ArrayAdapter(BillwiseReceipt.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.payment_types_array));
                        spinner_paymentmode.setAdapter(companyBankAdapter);
//
                        Intent intent = new Intent(BillwiseReceipt.this, BillwiseReceiptPreview.class);

                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_OUTSTANDING_RECEIPT);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                        intent.putExtra(BILLWISE_RECEIPT_KEY,arr_receipts);
                        intent.putExtra("receipt_mode",st_selected_mode);
                        intent.putExtra("cheque_no",str_chequeNo);
                        intent.putExtra("cheque_bank",str_cmpnybank);
                        intent.putExtra("cheque_date",str_cheque_date);
                        intent.putExtra("cheque_cleardate",str_clearingdate);
                        intent.putExtra("bank_referno",str_bank_refno);
                        intent.putExtra("total_amnt",edt_totalamnt.getText().toString());
                        intent.putExtra("reference_no",edt_referno.getText().toString());
                        intent.putExtra("date", st_date);
                        startActivity(intent);
                        finish();
//                        edt_totalamnt.setText("");
//                        tv_totalamnt.setText("");
//                        str_chequeNo = "";
//                        str_cust_bank = "";
//                        str_cheque_date = "";
//                        str_clearingdate = "";
//                        str_bank_refno = "";
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
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);


    }

    private void OfflineSaleReturn(final Sales salesdone) {
        final ProgressDialog pd = ProgressDialog.show(BillwiseReceipt.this, null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        JSONArray returntarray = new JSONArray();
        //String st_receipt_type ="";
        String st_date = "";

        try {
            final String strDate = getDateTime();
            for (Sales s : returnOfflineList) {
                double db_invoice_id = 0;
                try {
                    db_invoice_id = TextUtils.isEmpty(s.getInvoiceCode()) ? 0 : Double.valueOf(s.getInvoiceCode().toString());
                }catch (Exception e){

                }
                final JSONObject returnObj = new JSONObject();

                returnObj.put("invoice_id", s.getInvoiceCode());
                //returnObj.put("invoice_id", "0");
                returnObj.put(CUSTOMER_KEY, s.getCustomerId());
                returnObj.put("tax_total", getAmount(s.getTaxAmount()));
                returnObj.put("total_amount", getAmount(s.getTotal()));
                returnObj.put("return_date",strDate);
                returnObj.put("without_tax_total", s.getTaxable_total());
                returnObj.put("discount", s.getDiscount());
                returnObj.put("discount_total", getAmount(s.getDiscountAmount()));
                returnObj.put("latitude", s.getSaleLatitude());
                returnObj.put("longitude", s.getSaleLongitude());
                returnObj.put("invoice_type", s.getPayment_type());
                returnObj.put("return_invoiceno", s.getReturn_invoiceno());
                returnObj.put("round_off", getAmount(s.getRoundoff_value()));
                returnObj.put("return_receipt_type", s.getReturn_billtype());
                returnObj.put(EXECUTIVE_KEY, EXECUTIVE_ID);



                JSONArray productArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {
                    int tx_type =0;
                    double unit_price =c.getProductPrice();
                    if(c.getTax_type().equals("TAX_INCLUSIVE")){
                        tx_type =1;
                        unit_price = c.getProductTotal()/c.getPieceQuantity_nw();

                    }

                    unit_price =roundTwoDecimals(unit_price);
                    //  double tax_prod = getTaxPrice(c.getProductTotal(),c.getTax(),c.getTax_type());
                    double tax_prod = getTaxPrice(c.getProductTotalValue(),c.getTax(),c.getTax_type());

                    JSONObject obj = new JSONObject();

                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", unit_price);
                    obj.put("tax_rate", c.getTax());
                    obj.put("tax_amount", tax_prod);
                    obj.put("return_quantity", c.getPieceQuantity_nw());
                    obj.put("product_unit", c.getUnitselected());

                    obj.put("product_discount", c.getProductDiscount());
                    obj.put("product_total", c.getProductTotal());
                    obj.put("saleitem_id", c.getSale_item_id());
                    obj.put("tax_type", tx_type);

                    productArray.put(obj);
                }
                // if(st_receipt_type.equals("billwise")) {

                JSONArray receiptArray = new JSONArray();
                for (BillwiseReceiptMdl receipt : bills_sumbit) {
                    st_date = receipt.getBill_date();
                    double dbl_invoiceamnt = TextUtils.isEmpty(receipt.getInvoice_amnt()) ? 0 : Double.valueOf(receipt.getInvoice_amnt().toString());
                    double dbl_enteredamnt = TextUtils.isEmpty(receipt.getReceipt_enetered()) ? 0 : Double.valueOf(receipt.getReceipt_enetered().toString());
                    double bal_mnt = (dbl_invoiceamnt);
                    JSONObject obj = new JSONObject();
                    st_date = receipt.getBill_date();

                    if(dbl_enteredamnt>0) {
                        obj.put("paid_amount", receipt.getReceipt_enetered());
                        obj.put("invoice_no", receipt.getInvoiceno());
                        obj.put("invoice_balance", bal_mnt);
                        obj.put("invoice_amount", receipt.getInvoice_amnt());
                        obj.put("sale_id", receipt.getSale_id());


                        receiptArray.put(obj);
                    }
                }
                double bal_total = 0;
                try {
                    double dbl_total_amnt = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Double.valueOf(edt_totalamnt.getText().toString());
                    double dbl_amnt = TextUtils.isEmpty(tv_balance.getText().toString()) ? 0 : Double.valueOf(tv_balance.getText().toString());
                    double bill_total = myDatabase.get_billamount_total();
                    bal_total = dbl_total_amnt - bill_total;
                    bal_total = roundTwoDecimals(bal_total);
                } catch (Exception e) {

                }

                returnObj.put("PaidList", receiptArray);
                returnObj.put("ReturnedProduct", productArray);
                returnObj.put("mode", st_selected_mode);
                returnObj.put(CUSTOMER_KEY, SELECTED_SHOP.getShopId());
                returnObj.put("reference_no", "" + edt_referno.getText().toString());
                returnObj.put("total_amount", "" + edt_totalamnt.getText().toString());
                returnObj.put("balance", "" + bal_total);
                returnObj.put("date_time", st_date);

                if (st_selected_mode.equals("Cheque")) {
                    returnObj.put("cheque_date", str_cheque_date);
                    returnObj.put("cheque_no", str_chequeNo);
                    returnObj.put("cheque_bank", str_cmpnybankid);
                }

                returntarray.put(returnObj);
            }

            // }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("SalesReturn", returntarray);


            pd.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "Offline Return object  " + object);


        webOfflineReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "Offline Return Response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("Success")) {

                        long insertStatus = myDatabase.insertOfflineReturn(salesdone);
                        //sessionValue.storeReturnCode(SELECTED_SHOP.getRouteCode(), returnNumber);
                        sessionValue.storeReturnCode(routecode, returnNumber);
                        // String st_invoiceno = response.getString("invoice_no");
                        // boolean s = myDatabase.UpdateReturnwithInvoiceNo("" + salesdone.getReturn_invoiceno(), st_invoiceno);
                        //  Log.e("retinvoiceno", "" + salesdone.getReturn_invoiceno());
                        //  float fl_total = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Float.valueOf(edt_totalamnt.getText().toString());
//                        Transaction t1=new Transaction(SELECTED_SHOP.getShopId(),fl_total,0);
//                        if (!myDatabase.updateCustomerBalance(t1)){
//
//                        }
                        if (insertStatus != -1) {
                            salesdone.setReturn_invoiceno(st_invoiceno);
                            for (CartItem c : salesdone.getCartItems()) {

                                Log.e("Product Type", "" + c.getProductType());

                                myDatabase.updateStock(c, REQ_RETURN_TYPE);
                            }
                        }


                        pref_bonus = sessionAuth.getBonus();

                        Transaction t = new Transaction(SELECTED_SHOP.getShopId(), salesdone.getTotal(), 0);
                        if (myDatabase.updateCustomerBalance(t)) {


                            bill_list.clear();
                            myDatabase.deleteTableRequest(REQ_RECEIPT_BILLWISE);
                            get_invoicelist(SELECTED_SHOP.getShopId());
                            Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ReturnPreviewActivity.class);
                            intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_INVOICE_RETURN);
                            intent.putExtra(INVOICE_RETURN_VALUE_KEY, salesdone);
                            intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "Error updating customer balance", Toast.LENGTH_SHORT).show();
                        }


                    } else {

                        Toast.makeText(getApplicationContext(), "Return " + response.getString("status"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error resp",""+e.getMessage());
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

    private double roundTwoDecimals(double taxValue) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(taxValue));
    }

    private void get_invoicelist(int shopId) {

        //  setProgressBarMain(true);

        //    invoices.clear();


        final JSONObject object = new JSONObject();
        try {
            object.put(CUSTOMER_KEY, shopId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("objct", "invoice object " + object);


        webGetAllPendingInvoices(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("respnse", "invoice response " + response);
                try {

                    if (response.getString("result").equals("Success")) {
                        st_advncd_amnt = response.getString("advance_amount");
                        JSONArray array = response.getJSONArray("sale_list");

                        for (int i = 0; i < array.length(); i++) {
//
                            JSONObject obj = array.getJSONObject(i);

                            Log.e("invoice_no", obj.getString("invoice_no"));

                            BillwiseReceiptMdl br = new BillwiseReceiptMdl();
                            br.setBillamount((float) obj.getDouble("bill_amount"));
                            br.setInvoicebalance(obj.getString("balance"));
                            br.setInvoiceno(obj.getString("invoice_no"));
                            br.setTotalcash((float) 0);
                            br.setCustomerid("" + SELECTED_SHOP.getShopId());
                            br.setDue_amnt(obj.getString("due"));
                            br.setRemarks("");
                            br.setInvoice_amnt(obj.getString("amount"));
                            br.setInvoicedate(obj.getString("date"));
                            br.setSale_id(obj.getString("sale_id"));
                            br.setBill_date("");
                            br.setReceipt_enetered("0");

                            bill_list.add(br);

                            myDatabase.insertbillwisereceipt(br);
                        }
                        Log.e("invoicestringarry", "" + invoicestringarry[0]);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    if (total_return != 0) {
                        edt_totalamnt.setText("" + total_return);
                        edt_totalamnt.setFocusable(false);
                    }
                    double dbl_advamnt = TextUtils.isEmpty(st_advncd_amnt) ? 0 : Double.valueOf(st_advncd_amnt);
                    dbl_advamnt = roundTwoDecimals(dbl_advamnt);
                    tv_advanceamnt.setText(""+getAmount(dbl_advamnt));
                    double dbl_advncd_amnt = TextUtils.isEmpty(tv_advanceamnt.getText().toString()) ? 0 : Double.valueOf(tv_advanceamnt.getText().toString());
                    double dbl_amnt = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Double.valueOf(edt_totalamnt.getText().toString());
                    double dbl_total_amnt = roundTwoDecimals(dbl_advncd_amnt + dbl_amnt);

                    tv_totalamnt.setText("" + getAmount(dbl_total_amnt));
                } catch (Exception e) {

                }


                setrecyclerview();

            }

            @Override
            public void onErrorResponse(String error) {

                // setErrorView(error, "", true);

            }
        }, object);

    }

    private void setrecyclerview() {

        double dbl_amnt = TextUtils.isEmpty(edt_totalamnt.getText().toString()) ? 0 : Double.valueOf(edt_totalamnt.getText().toString());
        double dbl_adv_amnt = TextUtils.isEmpty(st_advncd_amnt.toString()) ? 0 : Double.valueOf(st_advncd_amnt.toString());
        //Log.e("dbl_amnt",""+dbl_amnt);
        // Log.e("dbl_advncd_amnt",""+st_advncd_amnt);
        double dbl_total_amnt = dbl_adv_amnt + dbl_totalamnt;
        // Log.e("dbl_total_amnttttttttt",""+dbl_total_amnt);
        dbl_total_amnt = roundTwoDecimals(dbl_total_amnt);
        tv_totalamnt.setText("" + dbl_total_amnt);

        double dbl_billamnt_total = dbl_total_amnt - myDatabase.get_billamount_total();
        dbl_billamnt_total = roundTwoDecimals(dbl_billamnt_total);
        tv_balance.setText("" + getAmount(dbl_billamnt_total));
        adapter = new RvBillwiseAdapter(bill_list, dbl_totalamnt, SELECTED_SHOP.getShopId(),returnNumber);

        rec_list.setHasFixedSize(true);
        //        Item Divider in recyclerView
        rec_list.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
                .build());

        rec_list.setLayoutManager(new LinearLayoutManager(this));
    }


}