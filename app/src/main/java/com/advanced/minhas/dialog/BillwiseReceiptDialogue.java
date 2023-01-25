package com.advanced.minhas.dialog;

import static com.advanced.minhas.localdb.MyDatabase.getDateTime;
import static com.advanced.minhas.session.SessionValue.PREF_TOTAL_AMNT_RECEIPT;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnBillwisedetailslistner;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.BillwiseReceiptMdl;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.textwatcher.TextValidator;

import com.rey.material.widget.Button;

import java.util.ArrayList;

public class BillwiseReceiptDialogue extends Dialog implements View.OnClickListener {
    private Button btnOk, btnCancel;
    EditText edt_amount, edt_remarks, edt_discount;
    String st_amount = "", st_invoiceno = "", st_returno = "";
    double dbl_amnt_entered = 0;
    private ArrayList<BillwiseReceiptMdl> bill_list = new ArrayList<>();
    double dbl_amnt = 0, dbl_total_amnt = 0, db_total_bill = 0;
    private SessionValue sessionValue;
    private MyDatabase myDatabase;
    private OnBillwisedetailslistner listener;

    public BillwiseReceiptDialogue(@NonNull Context context, double total_amnt, String amnt_entered, String invoice_no, String return_no, OnBillwisedetailslistner mListener) {
        super(context);
        this.listener = mListener;
        dbl_amnt_entered = TextUtils.isEmpty(amnt_entered) ? 0 : Double.valueOf(amnt_entered);
        this.dbl_total_amnt = total_amnt;
        this.st_invoiceno = invoice_no;
        this.st_returno = return_no;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialoge_billwisereceipt);
        sessionValue = new SessionValue(getContext());
        myDatabase = new MyDatabase(getContext());
        btnOk = findViewById(R.id.button_billwise_Dialog_cancel);
        btnCancel = findViewById(R.id.button_billwise_Dialog_ok);
        edt_amount = findViewById(R.id.edt_amount);
        edt_remarks = findViewById(R.id.edt_remarks);
        edt_discount = findViewById(R.id.edt_discount);
        if (st_returno.length() > 0) {
            edt_discount.setVisibility(View.GONE);
        }
        String amnt_fromdb = myDatabase.get_billamntby_invoiceno(st_invoiceno);
        double dbl_fromdb = TextUtils.isEmpty(amnt_fromdb) ? 0 : Double.valueOf(amnt_fromdb);
        if (dbl_fromdb == 0) {
            edt_amount.setText("");
        } else {
            edt_amount.setText("" + amnt_fromdb);
        }
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        edt_amount.setSelectAllOnFocus(true);
        edt_amount.setFocusableInTouchMode(true);
        edt_amount.setFocusable(true);
        db_total_bill = myDatabase.get_billamount_total();
        edt_amount.addTextChangedListener(new TextValidator(edt_amount) {
            @Override
            public void validate(TextView textView, String text) {
                edt_amount.setFocusableInTouchMode(true);
                edt_amount.setSelectAllOnFocus(true);
                edt_amount.requestFocus();
                dbl_amnt = TextUtils.isEmpty(edt_amount.getText().toString()) ? 0 : Double.valueOf(edt_amount.getText().toString());
                // String total_amnt = String.valueOf(sessionValue.get_billwise_total_amnt().get(PREF_TOTAL_AMNT_RECEIPT));
                dbl_total_amnt = TextUtils.isEmpty(sessionValue.get_billwise_total_amnt().get(PREF_TOTAL_AMNT_RECEIPT)) ? 0 : Double.valueOf(sessionValue.get_billwise_total_amnt().get(PREF_TOTAL_AMNT_RECEIPT));
                // double db_total_bill =0;
                if (dbl_total_amnt != 0) {
                    if (dbl_amnt > dbl_total_amnt) {
                        Toast.makeText(getContext(), "Amount Exceeds the limit..!", Toast.LENGTH_LONG).show();
                        st_amount = "0";
                        edt_amount.setText("0");
                    } else {
                        if (dbl_amnt > dbl_amnt_entered) {
                            Toast.makeText(getContext(), "Amount Exceeds the Bill amount..!", Toast.LENGTH_LONG).show();
                            st_amount = "0";
                            edt_amount.setText("0");
                        } else {
                            double db_total_bills = 0;
                            db_total_bills = db_total_bill + dbl_amnt;
                            if (db_total_bill > 0 && db_total_bills > dbl_total_amnt) {
                                Toast.makeText(getContext(), "Amount Exceeds the Bill amount..!" + db_total_bills, Toast.LENGTH_LONG).show();
                                st_amount = "0";
                                //edt_amount.setText("0");
                            } else {
                                st_amount = edt_amount.getText().toString();
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Amount Cannot be Zero..!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_billwise_Dialog_ok:

                try {
                    if (listener != null) {
                        st_amount = edt_amount.getText().toString();
                        String st_remarks = edt_remarks.getText().toString();
                        String st_date = getDateTime();
                        String st_discnt = edt_discount.getText().toString();
                        myDatabase.Update_receipt_amountbyInvoice(st_invoiceno, st_amount, st_remarks, st_date, st_discnt);
                        db_total_bill = myDatabase.get_billamount_total();
                        float dblamnt = TextUtils.isEmpty(st_amount) ? 0 : Float.valueOf(st_amount);
                        listener.onEnterbilldetails(dblamnt, st_remarks);
                        dismiss();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Some Wrong", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_billwise_Dialog_cancel:
                dismiss();
                break;
        }

    }

}
