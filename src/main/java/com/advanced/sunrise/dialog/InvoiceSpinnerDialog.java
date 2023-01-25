package com.advanced.minhas.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.model.Invoice;
import com.advanced.minhas.textwatcher.TextValidator;

import java.util.ArrayList;

/**
 * Created by mentor on 22/6/17.
 * Edited By Mentor on 22/05/19
 *
 */

public class InvoiceSpinnerDialog {

    ArrayList<Invoice> items=null;

    Activity context;
    String dTitle;
    OnSpinerItemClick onSpinerItemClick;
    AlertDialog alertDialog;
    int pos;
    int style;

    ArrayList<Invoice> filteritems=null;

    String TAG="InvoiceSpinnerDialog";

    public InvoiceSpinnerDialog(Activity activity, ArrayList<Invoice> items, String dialogTitle, int style) {
        this.items = items;

        this.context = activity;
        this.dTitle=dialogTitle;
        this.style=style;
        this.filteritems = items;
    }

    public void bindOnSpinerListener(OnSpinerItemClick onSpinerItemClick1) {
        this.onSpinerItemClick = onSpinerItemClick1;
    }

    public void showSpinerDialog()
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose =(TextView) v.findViewById(R.id.close);
        TextView title = (TextView)v.findViewById(R.id.spinerTitle);
        title.setText(dTitle);
        final ListView listView = (ListView) v.findViewById(R.id.list);
        final EditText searchBox =(EditText) v.findViewById(R.id.searchBox);

        Log.e("array size", ""+items.size());

        ArrayAdapter<Invoice> adapter = new ArrayAdapter<Invoice>(context, R.layout.items_view, items);
        listView.setAdapter(adapter);
        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.getWindow().getAttributes().windowAnimations = style;//R.style.DialogAnimations_SmileWindow;
        alertDialog.setCancelable(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)  {
                TextView t= (TextView) view.findViewById(R.id.text1);
                for(int j=0;j<items.size();j++) {
                    if(t.getText().toString().equalsIgnoreCase(items.get(j).toString())){

                        pos=j;
                        onSpinerItemClick.onClick(items.get(pos),pos);
                        alertDialog.dismiss();
                    }
                }
                onSpinerItemClick.onClick(items.get(pos),pos);
                alertDialog.dismiss();
            }
        });

        searchBox.addTextChangedListener(new TextValidator(searchBox) {

            @Override
            public void validate(TextView textView, String text)
            {
                try {

                String charstring = searchBox.getText().toString();
                if (charstring.isEmpty()){
                filteritems = items;

                }else {
                    ArrayList<Invoice> list = new ArrayList<>();
                    for (Invoice s : items) {

                        if (s.getInvoiceNo().toLowerCase().contains(charstring)) {

                            list.add(s);
                        }
                    }
                    filteritems = list;
                }

                ArrayAdapter<Invoice> adapter = new ArrayAdapter<Invoice>(context, R.layout.items_view, filteritems);
                listView.setAdapter(adapter);

                }catch (Exception e){

                }
            }
        });

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
      }
    }

