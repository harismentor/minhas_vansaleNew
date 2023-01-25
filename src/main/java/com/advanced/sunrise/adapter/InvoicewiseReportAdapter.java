package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.InvoicewiseReportMdl;
import com.advanced.minhas.model.Shop;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class InvoicewiseReportAdapter extends RecyclerView.Adapter<InvoicewiseReportAdapter.RvProductReportHolder> {

    String TAG = "RvSaleReportAdapter";
    private Context context;
    private ArrayList<InvoicewiseReportMdl> salereport;
    private Shop SELECTED_SHOP = null;
    private MyDatabase myDatabase;

    public InvoicewiseReportAdapter(ArrayList<InvoicewiseReportMdl> salereport) {
        Log.e("reached", "" + salereport.size());
        this.salereport = salereport;

    }

    //    double value to amount format
    public static String getAmount(double amount) {
        String str = "";
        try {
            DecimalFormat formatter = new DecimalFormat("##,##,##,###.00");
            str = formatter.format(amount);
            if (str.contains(",")) {
                str = str.replaceAll(",", "");
            }
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return str;
    }

    @Override
    public RvProductReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        myDatabase = new MyDatabase(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoicewise_report, parent, false);
        return new RvProductReportHolder(view);

    }

    @Override
    public void onBindViewHolder(RvProductReportHolder holder, int position) {
        final InvoicewiseReportMdl p = salereport.get(position);
        int s = position + 1;

        holder.txt_slno.setText("" + s);
        holder.txt_accname.setText(p.getAccname());
        holder.txt_sumof_qty.setText(String.valueOf(p.getSumofgty()));
        holder.txt_sumofvalue.setText(p.getSumofvalue());
        holder.txt_tax.setText(p.getTax());
        holder.txt_grossvalue.setText(p.getGrossvalue());
        holder.txt_invoiceno.setText(p.getInvoiceno());

    }

    @Override
    public int getItemCount() {
        return salereport.size();
    }

    class RvProductReportHolder extends RecyclerView.ViewHolder {

        TextView txt_slno, txt_accname, txt_invoiceno, txt_sumof_qty, txt_sumofvalue, txt_tax, txt_grossvalue;

        private RvProductReportHolder(View itemView) {
            super(itemView);

            txt_slno = (TextView) itemView.findViewById(R.id.tv_slno);
            txt_accname = (TextView) itemView.findViewById(R.id.tv_accname);
            txt_sumof_qty = (TextView) itemView.findViewById(R.id.tv_sumofqty);
            txt_sumofvalue = (TextView) itemView.findViewById(R.id.tv_sumofvalue);
            txt_tax = (TextView) itemView.findViewById(R.id.tv_tax);
            txt_grossvalue = (TextView) itemView.findViewById(R.id.tv_grossvalue);
            txt_invoiceno = (TextView) itemView.findViewById(R.id.tv_invoiceno);

        }
    }
}
