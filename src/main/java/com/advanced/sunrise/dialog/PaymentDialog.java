package com.advanced.minhas.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.session.SessionValue;
import com.rey.material.widget.Button;

import java.util.Calendar;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.advanced.minhas.config.Generic.dateFormat;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;


/**
 * Created by sadiquekolakkal on 08-05-2017.
 */

public class PaymentDialog extends Dialog implements View.OnClickListener {


    String TAG = "PaymentDialog", CURRENCY="";
    private paymentClickListener listener;
    private Button btnOk, btnCancel;
    private TextView tvDate, tvTotalAmount, tvBalancelAmount;
    private EditText etPaid;
    private float tempPaid,  totalAmount, balanceAmount;
    SessionValue sessionValue;


    public PaymentDialog(@NonNull Context context, float paid,  float total, float balance, paymentClickListener listener) {
        super(context);

        this.totalAmount = total;
        this.balanceAmount = balance;

        this.listener = listener;
        this.tempPaid = paid;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_make_payment);


        btnOk = (Button) findViewById(R.id.button_paymentDialog_ok);
        btnCancel = (Button) findViewById(R.id.button_paymentDialog_cancel);
        tvDate = (TextView) findViewById(R.id.textView_paymentDialog_date);
        tvTotalAmount = (TextView) findViewById(R.id.textView_paymentDialog_TotalAmount);
        tvBalancelAmount = (TextView) findViewById(R.id.textView_paymentDialog_BalanceAmount);
        etPaid = (EditText) findViewById(R.id.editText_paymentDialog_paidAmount);

        sessionValue = new SessionValue(getContext());

        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);

//        Log.d(TAG,"Total   "+getAmount(totalAmount)+" balance  "+getAmount(balanceAmount));
        tvTotalAmount.setText(getAmount(totalAmount) + " " + CURRENCY);

        tvBalancelAmount.setText(getAmount(balanceAmount) + " " + CURRENCY);


        etPaid.setHint(String.valueOf(tempPaid));



        final String strDate = getTodayDate();
        tvDate.setText(strDate);

        /*etPaid.requestFocus();
        etPaid.setFocusableInTouchMode(true);
//
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etPaid, InputMethodManager.RESULT_SHOWN);

*/


        //        show input keyboard
        etPaid.requestFocus();
        etPaid.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_paymentDialog_ok:
                if (isValidate()) {
                    float paid = TextUtils.isEmpty(etPaid.getText().toString().trim()) ? 0 : Float.valueOf(etPaid.getText().toString().trim());

                    if (listener != null)
                        listener.onPaymentClick(paid);

                    dismiss();
                }

                break;
            case R.id.button_paymentDialog_cancel:

                dismiss();
                break;

        }

    }


    boolean isValidate() {

        boolean status = false;

        etPaid.setError(null);



        float paid = TextUtils.isEmpty(etPaid.getText().toString().trim()) ? 0 : Float.valueOf(etPaid.getText().toString().trim());




        if ( TextUtils.isEmpty(etPaid.getText().toString().trim())) {
            status = false;
            etPaid.setError("PaidAmount is empty");


        } else if   (paid > totalAmount) {
                status = false;

                etPaid.setError("Amount should be less than due Total Amount..!");
            } else
                status = true;



        return status;
    }

    @Override
    protected void onStop() {
        super.onStop();

        hideSoftKeyboard();
    }

/*
    private void hideKeyboard() {
        //Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        }


    }

*/

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(this.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getContext(). getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }




    public interface paymentClickListener {

        void onPaymentClick(float paid);
    }




    private static String getTodayDate() {

        return dateFormat.format(Calendar.getInstance().getTime());
    }

}
