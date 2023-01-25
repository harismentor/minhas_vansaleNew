package com.advanced.minhas.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnInputKiloMeterListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Vehicle;
import com.advanced.minhas.session.SessionAuth;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Date;

import static com.advanced.minhas.config.Generic.dateFormat;

public class DayRegisterDialog extends Dialog implements View.OnClickListener {

    private String TAG = "DayRegisterDialog";
    private Button btnOk, btnCancel;

    private TextView tvDate, tvExecutiveName,tv_vehiclenoadd;
    private EditText etKiloMeter,etVehicleno,edt_vehiclenoadd,edt_drivername;
    Spinner sp_vehiclecno;

    private RadioGroup rgDutyType;

    private OnInputKiloMeterListener listener;
    String st_vehicle_cno="",st_vehicleId="";

    private SessionAuth sessionAuth;
    ArrayList<String> array_vehicleid = new ArrayList<>();
    ArrayList<String> array_vehicleno = new ArrayList<>();
    private ArrayList<Vehicle> vehicle_list = new ArrayList<Vehicle>();
    private MyDatabase myDatabase;

    public DayRegisterDialog(@NonNull Context context, OnInputKiloMeterListener mListener) {
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
        setContentView(R.layout.dialog_day_register);
        myDatabase = new MyDatabase(getContext());
        btnOk = findViewById(R.id.button_dayRegister_Dialog_ok);
        btnCancel = findViewById(R.id.button_dayRegister_Dialog_cancel);
        tvDate = findViewById(R.id.textView_dayRegister_Dialog_date);
        tvExecutiveName = findViewById(R.id.textView_dayRegister_Dialog_executiveName);
        sp_vehiclecno = findViewById(R.id.sp_vehicleno);
        rgDutyType = findViewById(R.id.radioGroup_dayRegister_Dialog_dutyType);

        etKiloMeter = findViewById(R.id.editText_dayRegister_Dialog_kilometer);
        etVehicleno = findViewById(R.id.editText_dayRegister_Dialog_vehicleno);
        tv_vehiclenoadd = findViewById(R.id.tv_vehiclenoadd);
        edt_vehiclenoadd = findViewById(R.id.edt_vehiclenoadd);
        edt_drivername = findViewById(R.id.edt_drivername);
        this.sessionAuth = new SessionAuth(getContext());

        tvExecutiveName.setText("Hi..!  " + sessionAuth.getExecutiveName());

        tvDate.setText(getDateTime());

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        vehicle_list.addAll(myDatabase.getAllVehicles());
        array_vehicleno.clear();
        array_vehicleid.clear();
        array_vehicleid.add(("-1"));
        array_vehicleno.add("Select Vehicle");
        array_vehicleid.add(("0"));
        array_vehicleno.add("Other");
        for(Vehicle v :vehicle_list){
            array_vehicleid.add(""+v.getVehicle_id());
            array_vehicleno.add(v.getVehicle_no());
        }
        Log.e("vehiclenos",""+array_vehicleno);



        ArrayAdapter adapter1 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, array_vehicleno);
        sp_vehiclecno.setAdapter(adapter1);

        sp_vehiclecno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                st_vehicle_cno = sp_vehiclecno.getSelectedItem().toString().trim();
                st_vehicleId = array_vehicleid.get(i);

                if(st_vehicleId.equals("0")){
                    tv_vehiclenoadd.setVisibility(View.VISIBLE);
                    edt_vehiclenoadd.setVisibility(View.VISIBLE);

                }
                else{
                    tv_vehiclenoadd.setVisibility(View.GONE);
                    edt_vehiclenoadd.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rgDutyType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {

                    int type = checkedRadioButton.getId();
                    etKiloMeter.setEnabled(type == R.id.radioButton_dayRegister_Dialog_yes);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_dayRegister_Dialog_ok:

                try {

                    int selectedId = rgDutyType.getCheckedRadioButtonId();
                    String stringKm = etKiloMeter.getText().toString().trim();
                    String st_drivername = edt_drivername.getText().toString().trim();
                    //  String st_vehicleno = etVehicleno.getText().toString();

                    if(st_vehicleId.equals("0")){
                        st_vehicle_cno=edt_vehiclenoadd.getText().toString();
                    }

                    if (((selectedId == R.id.radioButton_dayRegister_Dialog_yes) && !TextUtils.isEmpty(stringKm)) || (selectedId == R.id.radioButton_dayRegister_Dialog_no)) {

                        //if(st_vehicle_cno.equals("Select Vehicle")){

                        if (listener != null) {
                            listener.onEnterKiloMeter((selectedId == R.id.radioButton_dayRegister_Dialog_yes) ? 1 : 0, (selectedId == R.id.radioButton_dayRegister_Dialog_yes) ? stringKm : "0",st_vehicle_cno,st_drivername);
                            dismiss();
                        }

                    } else

                        etKiloMeter.setError("Invalid Kilometer");

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Some Wrong", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_dayRegister_Dialog_cancel:
                dismiss();

                break;
        }

    }


}
