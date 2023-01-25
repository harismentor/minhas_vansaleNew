package com.advanced.minhas.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import static com.advanced.minhas.config.ConfigSales.IS_PAYMENT_TYPE_CARD;
import static com.advanced.minhas.config.Generic.dateFormat;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;

/**
 * Created by mentor on 15/1/18.
 */

public class PaymentTypeDialog extends Dialog implements View.OnClickListener {


    String TAG = "PaymentTypeDialog";
    private paymentTypeClickListener listener;
    private Button btnOk, btnCancel;
    private TextView tvDate, tvTotalAmount,tvCreditLimit,tvcustomercrlimit;

    private double   totalAmount,creditLimit;
    String customer_crlimit ="";
    private RadioGroup rgType;

    private RadioButton rbType,rbCredit,rbCash,rbCard;
    String type="", CURRENCY="";
    SessionValue sessionValue;

    public PaymentTypeDialog(@NonNull Context context,  double limit,String customer_crlimit, double total,String type, paymentTypeClickListener listener) {
        super(context);

        this.totalAmount = total;
        this.creditLimit = limit;
        this.customer_crlimit = customer_crlimit;

        this.listener = listener;
        this.type=type;



    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_type);
        btnOk = (Button) findViewById(R.id.button_paymentTypeDialog_ok);
        btnCancel = (Button) findViewById(R.id.button_paymentTypeDialog_cancel);
        tvDate = (TextView) findViewById(R.id.textView_paymentTypeDialog_date);
        tvCreditLimit = (TextView) findViewById(R.id.textView_paymentTypeDialog_creditLimit);
        tvTotalAmount = (TextView) findViewById(R.id.textView_paymentTypeDialog_TotalAmount);
        rgType = (RadioGroup) findViewById(R.id.radioGroup_paymentTypeDialog);
        rbCash = (RadioButton) findViewById(R.id.radioButton_paymentTypeDialog_cash);
        rbCredit = (RadioButton) findViewById(R.id.radioButton_paymentTypeDialog_credit);
        rbCard = (RadioButton) findViewById(R.id.radioButton_paymentTypeDialog_card);

        tvcustomercrlimit = findViewById(R.id.textView_paymentTypeDialog_customercreditLimit);
        rgType.clearCheck();
        rbCash.setChecked(true);

        if(IS_PAYMENT_TYPE_CARD==true){
            rbCard.setVisibility(View.VISIBLE);
        }
        else{
            rbCard.setVisibility(View.GONE);
        }
        sessionValue = new SessionValue(getContext());

        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);


//        Log.d(TAG,"Total   "+getAmount(totalAmount)+" balance  "+getAmount(balanceAmount));
        tvTotalAmount.setText(String.valueOf(getAmount(totalAmount) + " " + CURRENCY));

        tvcustomercrlimit.setText(String.valueOf("Credit Limit  " +customer_crlimit) + " " + CURRENCY);
       // tvCreditLimit.setText(String.valueOf("Credit up to "+getAmount(creditLimit) + " " + CURRENCY));




        if (type.equals("Credit"))
            rbCredit.setChecked(true);
        else if (type.equals("Cash"))
            rbCash.setChecked(true);
        else if (type.equals("Card"))
            rbCard.setChecked(true);

        final String strDate = getTodayDate();
        tvDate.setText(strDate);





        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_paymentTypeDialog_ok:


                    rbType = (RadioButton) rgType.findViewById(rgType.getCheckedRadioButtonId());

                    if (rbType==null) {
                        Toast.makeText(getContext(), "Please select type", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (listener != null)
                        listener.onPaymentTypeClick(rbType.getText().toString());

                    dismiss();


                break;
            case R.id.button_paymentTypeDialog_cancel:

                dismiss();
                break;

        }

    }



    private static String getTodayDate() {

        return dateFormat.format(Calendar.getInstance().getTime());
    }



    public interface paymentTypeClickListener {

        void onPaymentTypeClick(String type);
    }
}
