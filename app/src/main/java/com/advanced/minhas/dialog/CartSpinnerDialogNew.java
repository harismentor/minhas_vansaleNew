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
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.CartItemCode;
import com.advanced.minhas.textwatcher.TextValidator;

import java.util.ArrayList;

/**
 * Created by mentor on 22/6/17.
 * Edited By Mentor on 22/05/19
 *
 */

public class CartSpinnerDialogNew {

    ArrayList<CartItem> items=null;
    ArrayList<CartItemCode> codeitems=null;
    Activity context;
    String dTitle;
    OnSpinerItemClick onSpinerItemClick;
    AlertDialog alertDialog;
    int pos;
    int style;
    int prod_click =0;

    ArrayList<CartItem> filteritems=null;

    String TAG="CartSpinnerDialog";

    public CartSpinnerDialogNew(Activity activity, ArrayList<CartItem> items, ArrayList<CartItemCode> codeitems , String dialogTitle, int style) {
        this.items = items;
        this.codeitems = codeitems;
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
        prod_click=1;
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose =(TextView) v.findViewById(R.id.close);
        TextView title = (TextView)v.findViewById(R.id.spinerTitle);
        title.setText(dTitle);
        final ListView listView = (ListView) v.findViewById(R.id.list);
        final EditText searchBox =(EditText) v.findViewById(R.id.searchBox);

        Log.e("array size", ""+items.size());

        ArrayAdapter<CartItem> adapter = new ArrayAdapter<CartItem>(context, R.layout.items_view, items);
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
                        Log.e("net price inn",""+items.get(pos).getNetPrice());
                        prod_click=0;
                        alertDialog.dismiss();
                    }
                }
                onSpinerItemClick.onClick(items.get(pos),pos);
                alertDialog.dismiss();
            }
        });

        /*searchBox.addTextChangedListener(new TextValidator(searchBox) {

            @Override
            public void validate(TextView textView, String text)
            {
                adapter.getFilter().filter(searchBox.getText().toString());
            }
        });*/


        searchBox.addTextChangedListener(new TextValidator(searchBox) {

            @Override
            public void validate(TextView textView, String text)
            {
                try {

                String charstring = searchBox.getText().toString();
                if (charstring.isEmpty()){
                filteritems = items;

                }else {
                    ArrayList<CartItem> list = new ArrayList<>();
                    for (CartItem s : items) {
                       // Log.e("prodname",""+s.getP_name());

                        if (s.getProductName().toLowerCase().contains(charstring)) {

                            list.add(s);
                        }
                    }
                    filteritems = list;
                }
             //  adapter.getFilter().filter(searchBox.getText().toString());
             //   Log.e("adapter : ", ""+filteritems);

                ArrayAdapter<CartItem> adapter = new ArrayAdapter<CartItem>(context, R.layout.items_view, filteritems);
                listView.setAdapter(adapter);

                }catch (Exception e){

                }
            }
        });

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prod_click=0;
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void showCodeSpinerDialog()
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose =(TextView) v.findViewById(R.id.close);
        TextView title = (TextView)v.findViewById(R.id.spinerTitle);
        title.setText(dTitle);
        final ListView listView = (ListView) v.findViewById(R.id.list);
        final EditText searchBox =(EditText) v.findViewById(R.id.searchBox);

        Log.e("array size", ""+items.size());

        final ArrayAdapter<CartItemCode> adaptercode = new ArrayAdapter<CartItemCode>(context, R.layout.items_view, codeitems);
        listView.setAdapter(adaptercode);
        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.getWindow().getAttributes().windowAnimations = style;//R.style.DialogAnimations_SmileWindow;
        alertDialog.setCancelable(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)  {
                TextView t= (TextView) view.findViewById(R.id.text1);
                for(int j=0;j<codeitems.size();j++) {



                    if(t.getText().toString().equalsIgnoreCase(codeitems.get(j).getProductCode())){

                        pos=j;

                        onSpinerItemClick.onClick(items.get(pos),pos);
                      // onSpinerItemClick.onCodeClick(codeitems.get(pos),pos);
                        prod_click=0;
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
                adaptercode.getFilter().filter(searchBox.getText().toString());
            }
        });

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prod_click =0;
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    public int getclick_status(){
        return prod_click;
    }


/*    public void showCodeSpinerDialog()
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
                    if(t.getText().toString().equalsIgnoreCase(codeitems.get(j).getProductCode())){
                        pos=j;
                        onSpinerItemClick.onClick(codeitems.get(pos),pos);
                        alertDialog.dismiss();
                    }
                }
                onSpinerItemClick.onClick(codeitems.get(pos),pos);
                alertDialog.dismiss();
            }
        });

        searchBox.setVisibility(View.GONE);

        searchBox.addTextChangedListener(new TextValidator(searchBox) {

            @Override
            public void validate(TextView textView, String text)
            {

                productCodeAdapter1.getFilterItems(searchBox.getText().toString());

                //productCodeAdapter1.getFilter().filter(searchBox.getText().toString());
            }
        });

       *//* searchBox.addTextChangedListener(new TextWatcher() {
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

        });*//*

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }*/
}
