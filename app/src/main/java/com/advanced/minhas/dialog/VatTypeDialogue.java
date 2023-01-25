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

public class VatTypeDialogue extends Dialog implements View.OnClickListener {
    String TAG = "VatTypeDialogue";
    private VatTypeDialogue.VatTypelistner listener;
    private Button btnOk, btnCancel;
    private TextView tvDate, tvTotalAmount,tvCreditLimit;

    private double totalAmount,creditLimit;
    private RadioGroup rgType;

    private RadioButton rbType,rbnovat,rbvat;
    String type="", CURRENCY="" ,st_vat_status ="";
    SessionValue sessionValue;

    public VatTypeDialogue(@NonNull Context context,String vat_type_temp, VatTypeDialogue.VatTypelistner listener) {
        super(context);
        st_vat_status = vat_type_temp;
        this.listener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sale_vat_type);

        btnOk = (Button) findViewById(R.id.button_vatTypeDialog_ok);
        btnCancel = (Button) findViewById(R.id.button_vatTypeDialog_cancel);

        rgType = (RadioGroup) findViewById(R.id.radioGroup_paymentTypeDialog);
        rbvat = (RadioButton) findViewById(R.id.radioButton_vatDialog);
        rbnovat = (RadioButton) findViewById(R.id.radioButton_novatDialog);

        rgType.clearCheck();


        sessionValue = new SessionValue(getContext());

        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);

        if(st_vat_status.equals("")) {

            if (type.equals("Vat"))
                rbvat.setChecked(true);
            else if (type.equals("No Vat"))
                rbnovat.setChecked(true);
        }
        else{
            if (st_vat_status.equals("Vat"))
                rbvat.setChecked(true);
            else if (st_vat_status.equals("No Vat"))
                rbnovat.setChecked(true);
        }


        final String strDate = getTodayDate();

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_vatTypeDialog_ok:


                rbType = (RadioButton) rgType.findViewById(rgType.getCheckedRadioButtonId());

                if (rbType==null) {
                    Toast.makeText(getContext(), "Please select vat", Toast.LENGTH_SHORT).show();
                    break;
                }

                if (listener != null)
                    listener.onVatTypeclick(rbType.getText().toString());
                Log.e("type",rbType.getText().toString());


                dismiss();


                break;
            case R.id.button_vatTypeDialog_cancel:

                dismiss();
                break;

        }

    }



    private static String getTodayDate() {

        return dateFormat.format(Calendar.getInstance().getTime());
    }



    public interface VatTypelistner {

        void onVatTypeclick(String type);
    }

}
