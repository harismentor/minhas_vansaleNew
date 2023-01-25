package com.advanced.minhas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.InvoiceClickListener;
import com.advanced.minhas.model.Invoice;

import java.util.ArrayList;

/**
 * Created by mentor on 30/10/17.
 */

public class InvoiceAdapter extends ArrayAdapter<Invoice> {


    Context mContext;
    int layoutResourceId;
    private ArrayList<Invoice> invoices = null;

    private InvoiceClickListener listener;

    private int selectedPosition = 0;

    public InvoiceAdapter(Context mContext, int layoutResourceId, ArrayList<Invoice> data, InvoiceClickListener listener) {

        super(mContext, 0, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.invoices = data;
        this.listener=listener;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
/*
        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);


        TextView tvInvoiceTitle = (TextView) listItem.findViewById(R.id.textView_item_textView);

        Invoice invoice = invoices.get(position);


        tvInvoiceTitle.setText(invoice.getInvoiceNo());

        return listItem;*/


         // Get the data item for this position
        final Invoice invoice = invoices.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_textview, parent, false);
        }
        // Lookup view for data population
        TextView tvInvoiceTitle = (TextView) convertView.findViewById(R.id.textView_item_textView);

      //  Log.e("invoice adapter ia", ""+String.valueOf(invoice.getInvoiceNo()));

        tvInvoiceTitle.setText(String.valueOf(invoice.getInvoiceNo()));



        if (selectedPosition == position)
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.colorGrayLight));
        else
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.colorWhite));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedPosition = position;
                notifyDataSetChanged();
                if (listener != null)
                    listener.onInvoiceClick(invoice, position);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }


}

