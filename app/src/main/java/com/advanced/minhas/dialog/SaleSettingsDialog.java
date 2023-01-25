package com.advanced.minhas.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import android.widget.RadioGroup;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.SalesettingsClickListner;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.session.SessionAuth;
import com.rey.material.widget.Button;

import java.util.Date;

import static com.advanced.minhas.config.Generic.dateFormat;

public class SaleSettingsDialog extends Dialog implements View.OnClickListener {

    private String TAG = "SaleSettingsDialog";
    private Button btnOk, btnCancel;
    private RadioGroup rgVatType;
    private SalesettingsClickListner listener;
    String st_vehicle_cno="",st_vehicleId="";

    private SessionAuth sessionAuth;

    private MyDatabase myDatabase;

    public SaleSettingsDialog(@NonNull Context context, SalesettingsClickListner mListener) {
        super(context);
        this.listener = mListener;
    }

    private static String getDateTime() {
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sale_settings);
        myDatabase = new MyDatabase(getContext());
        btnOk = findViewById(R.id.button_Settings_Dialog_ok);
        btnCancel = findViewById(R.id.button_Settings_Dialog_cancel);
        rgVatType = findViewById(R.id.radioGroup_dayRegister_Dialog_vatType);
        this.sessionAuth = new SessionAuth(getContext());

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_Settings_Dialog_ok:

                try {
                    int selectedId = rgVatType.getCheckedRadioButtonId();
                    if (((selectedId == R.id.radioButton_vat) ) || (selectedId == R.id.radioButton_noVat)) {
                        String st_vat_status ="";

                        if ((selectedId == R.id.radioButton_vat)) {
                            st_vat_status ="Vat";
                        }
                        else{
                            st_vat_status ="No Vat";
                        }

                        if (listener != null) {
                            listener.salesettingsclick(st_vat_status);
                            dismiss();
                        }

                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Some Wrong", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_Settings_Dialog_cancel:
                dismiss();

                break;
        }

    }


}
