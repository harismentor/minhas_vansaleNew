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
import com.advanced.minhas.adapter.CartCodeAdapter;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.textwatcher.TextValidator;

import java.util.ArrayList;

/**
 * Created by mentor on 22/6/17.
 */

public class CartSpinnerDialog {

    ArrayList<CartItem> items=null;
    ArrayList<CartItem> codeitems=null;
    Activity context;
    String dTitle;
    OnSpinerItemClick onSpinerItemClick;
    AlertDialog alertDialog;
    int pos;
    int style;

    String TAG="CartSpinnerDialog";

    public CartSpinnerDialog(Activity activity, ArrayList<CartItem> items, String dialogTitle, int style) {
        this.items = items;
        this.codeitems = items;
        this.context = activity;
        this.dTitle=dialogTitle;
        this.style=style;
    }

    public void bindOnSpinerListener(OnSpinerItemClick onSpinerItemClick1) {
        this.onSpinerItemClick = onSpinerItemClick1;
    }

    public void showSpinerDialog(final String return_type)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        final View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        final TextView rippleViewClose =(TextView) v.findViewById(R.id.close);
        TextView title = (TextView)v.findViewById(R.id.spinerTitle);
        title.setText(dTitle);
        final ListView listView = (ListView) v.findViewById(R.id.list);
        final EditText searchBox =(EditText) v.findViewById(R.id.searchBox);

        Log.e("array size", ""+items.size());

        final ArrayAdapter<CartItem> adapter = new ArrayAdapter<CartItem>(context, R.layout.items_view, items);
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
                    if(return_type.equals("with_invoice")) {
                        // if(t.getText().toString().equalsIgnoreCase(items.get(j).toString())){

                        //pos=j;
                        pos = i;
                        onSpinerItemClick.onClick(items.get(pos), pos);
                        alertDialog.dismiss();
                    }
                    else {
                         if(t.getText().toString().equalsIgnoreCase(items.get(j).toString())){

                        pos=j;
                        //pos = i;
                        onSpinerItemClick.onClick(items.get(pos), pos);
                        alertDialog.dismiss();
                    }
                    }
                    //}
                }
                onSpinerItemClick.onClick(items.get(pos),pos);
                alertDialog.dismiss();
            }
        });

        searchBox.addTextChangedListener(new TextValidator(searchBox) {

            @Override
            public void validate(TextView textView, String text)
            {
                adapter.getFilter().filter(searchBox.getText().toString());
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

    public void showCodeSpinerDialog(final String return_type)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose =(TextView) v.findViewById(R.id.close);
        TextView title = (TextView)v.findViewById(R.id.spinerTitle);
        title.setText("Product Code");
        final ListView listView = (ListView)v.findViewById(R.id.list);
        final EditText searchBox = (EditText)v.findViewById(R.id.searchBox);

     // android.R.layout.simple_dropdown_item_1line

        Log.e("array code size", ""+codeitems.size());

        listView.setAdapter(null);

        final CartCodeAdapter productCodeAdapter1 = new CartCodeAdapter(context, R.layout.items_view, codeitems);
        listView.setAdapter(productCodeAdapter1);

        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.getWindow().getAttributes().windowAnimations = style;//R.style.DialogAnimations_SmileWindow;
        alertDialog.setCancelable(false);

     // productCodeAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)  {

                TextView t= (TextView)view.findViewById(R.id.text1);

                for(int j=0;j<codeitems.size();j++)
                {
//                    if(t.getText().toString().equalsIgnoreCase(codeitems.get(j).getProductCode())){
//                        pos=j;
//                        onSpinerItemClick.onClick(codeitems.get(pos),pos);
//                        alertDialog.dismiss();
//                    }
                    if(return_type.equals("with_invoice")) {
                        // if(t.getText().toString().equalsIgnoreCase(items.get(j).toString())){

                        //pos=j;
                        pos = i;
                        onSpinerItemClick.onClick(items.get(pos), pos);
                        alertDialog.dismiss();
                    }
                    else {
                        if(t.getText().toString().equalsIgnoreCase(items.get(j).toString())){

                            pos=j;
                            //pos = i;
                            onSpinerItemClick.onClick(items.get(pos), pos);
                            alertDialog.dismiss();
                        }
                    }
                }
                onSpinerItemClick.onClick(codeitems.get(pos),pos);
                alertDialog.dismiss();
            }
        });

        searchBox.setVisibility(View.GONE);

       /* searchBox.addTextChangedListener(new TextValidator(searchBox) {

            @Override
            public void validate(TextView textView, String text)
            {

                productCodeAdapter1.getFilterItems(searchBox.getText().toString());

                //productCodeAdapter1.getFilter().filter(searchBox.getText().toString());
            }
        });*/

       /* searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                productCodeAdapter.getFilter().filter(searchBox.getText().toString());
            }

        });*/

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
