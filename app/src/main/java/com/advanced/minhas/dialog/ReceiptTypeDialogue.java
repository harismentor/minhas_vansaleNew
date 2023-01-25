package com.advanced.minhas.dialog;


import static com.advanced.minhas.config.Generic.dateFormat;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.session.SessionValue;
import com.rey.material.widget.Button;

import java.util.Calendar;

public class ReceiptTypeDialogue extends Dialog implements View.OnClickListener {
    String TAG = "ReceiptTypeDialogue";
    private ReceiptTypelistner listener;
    private Button btnOk, btnCancel;
    private TextView tvDate, tvTotalAmount,tvCreditLimit;

    private double   totalAmount,creditLimit;
    private RadioGroup rgType;

    private RadioButton rbType,rbRcptbill,rbRecpt;
    String type="", CURRENCY="";
    SessionValue sessionValue;
    public ReceiptTypeDialogue(@NonNull Context context, ReceiptTypelistner listener) {
        super(context);
        this.listener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_receipt_type);


        btnOk = (Button) findViewById(R.id.button_receiptTypeDialog_ok);
        btnCancel = (Button) findViewById(R.id.button_receiptTypeDialog_cancel);

        rgType = (RadioGroup) findViewById(R.id.radioGroup_paymentTypeDialog);
        rbRecpt = (RadioButton) findViewById(R.id.radioButton_receiptTypeDialog);
        rbRcptbill = (RadioButton) findViewById(R.id.radioButton_receiptTypeDialog_billwise);

        rgType.clearCheck();


        sessionValue = new SessionValue(getContext());

        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);


//        Log.d(TAG,"Total   "+getAmount(totalAmount)+" balance  "+getAmount(balanceAmount));
        //tvTotalAmount.setText(String.valueOf(getAmount(totalAmount) + " " + CURRENCY));


        //  tvCreditLimit.setText(String.valueOf("Credit up to "+getAmount(creditLimit) + " " + CURRENCY));




        if (type.equals("Receipt"))
            rbRecpt.setChecked(true);
        else if (type.equals("Billwise"))
            rbRcptbill.setChecked(true);


        final String strDate = getTodayDate();
//        tvDate.setText(strDate);





        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_receiptTypeDialog_ok:


                rbType = (RadioButton) rgType.findViewById(rgType.getCheckedRadioButtonId());

                if (rbType==null) {
                    Toast.makeText(getContext(), "Please select type", Toast.LENGTH_SHORT).show();
                    break;
                }

                if (listener != null)
                    listener.onReceiptTypeclick(rbType.getText().toString());
                Log.e("type",rbType.getText().toString());


                dismiss();


                break;
            case R.id.button_receiptTypeDialog_cancel:

                dismiss();
                break;

        }

    }



    private static String getTodayDate() {

        return dateFormat.format(Calendar.getInstance().getTime());
    }



    public interface ReceiptTypelistner {

        void onReceiptTypeclick(String type);
    }
}

