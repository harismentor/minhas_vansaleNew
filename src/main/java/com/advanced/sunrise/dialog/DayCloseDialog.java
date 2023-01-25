package com.advanced.minhas.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnDayCloseListener;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.rey.material.widget.Button;

import java.util.Date;

import static com.advanced.minhas.config.Generic.dateFormat;

public class DayCloseDialog extends Dialog implements View.OnClickListener {
    private String TAG = "DayCloseDialog";
    private Button btnOk, btnCancel;
    private TextView tvFromKilometer, tvDate, tvExecutiveName;

    private EditText etKiloMeter;

    private OnDayCloseListener listener;

    private SessionValue sessionValue;
    private SessionAuth sessionAuth;

    public DayCloseDialog(@NonNull Context context, OnDayCloseListener mListener) {
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
        setContentView(R.layout.dialog_day_close);

        btnOk = findViewById(R.id.button_dayClose_Dialog_ok);
        btnCancel = findViewById(R.id.button_dayClose_Dialog_cancel);
        tvDate = findViewById(R.id.textView_dayClose_Dialog_date);
        tvExecutiveName = findViewById(R.id.textView_dayClose_Dialog_executiveName);
        tvFromKilometer = findViewById(R.id.textView_dayClose_Dialog_from_kilometer);

        etKiloMeter = findViewById(R.id.editText_dayClose_Dialog_kilometer);

        this.sessionValue = new SessionValue(getContext());
        this.sessionAuth = new SessionAuth(getContext());

        tvFromKilometer.setText("From " + sessionValue.getRegisteredKM() + " KM");
        tvExecutiveName.setText("Hi..!  " + sessionAuth.getExecutiveName());

        tvDate.setText(getDateTime());

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_dayClose_Dialog_ok:

                String stringKm = etKiloMeter.getText().toString().trim();

                try {

                    float toKm = Float.valueOf(stringKm);

                    float fromKm = 0;

                    String km = ""+sessionValue.getRegisteredKM();
                    if (km.equalsIgnoreCase("null")){
                        fromKm = 0;
                    }else {
                        fromKm = Float.valueOf(sessionValue.getRegisteredKM());
                    }

                    if (TextUtils.isEmpty(stringKm)) {
                        etKiloMeter.setError("Invalid Kilometer");
                    } else if (toKm < fromKm) {

                        etKiloMeter.setError("Closing Kilometer must be greater than Starting Kilometer");

                    } else {

                        if (listener != null) {
                            listener.onEnterKiloMeter( stringKm);
                            dismiss();
                        }
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Some Wrong", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_dayClose_Dialog_cancel:
                dismiss();

                break;

        }

    }

}
