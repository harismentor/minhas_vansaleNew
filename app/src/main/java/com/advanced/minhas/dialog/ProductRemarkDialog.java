package com.advanced.minhas.dialog;

import static com.advanced.minhas.config.Generic.dateFormat;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.advanced.minhas.R;
import com.advanced.minhas.session.SessionValue;
import com.rey.material.widget.Button;

import java.util.Calendar;

public class ProductRemarkDialog extends Dialog implements View.OnClickListener {
    String TAG = "ProductRemarkDialog";
    private ProductRemarkDialog.productRemarkClickListener listener;
    private Button btnOk, btnCancel;

    EditText edt_remarks;
    private double   totalAmount,creditLimit;
    String customer_crlimit ="";
    String type="", CURRENCY="";
    SessionValue sessionValue;

    public ProductRemarkDialog(@NonNull Context context, ProductRemarkDialog.productRemarkClickListener listener) {
        super(context);
        this.listener = listener;
        this.type=type;

    }


    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_product_remark);
        btnOk = (Button) findViewById(R.id.button_remarkTypeDialog_ok);
        btnCancel = (Button) findViewById(R.id.button_remarkDialog_cancel);
        edt_remarks = findViewById(R.id.edt_remark);

        sessionValue = new SessionValue(getContext());
        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_remarkTypeDialog_ok:

                String st_descrptn = edt_remarks.getText().toString();
                Log.e("st_descrptn",st_descrptn);

                if (listener != null)
                    listener.onremarkClick(st_descrptn);

                dismiss();


                break;
            case R.id.button_remarkDialog_cancel:

                dismiss();
                break;

        }

    }

    private static String getTodayDate() {

        return dateFormat.format(Calendar.getInstance().getTime());
    }



    public interface productRemarkClickListener {

        void onremarkClick(String type);
    }
}
