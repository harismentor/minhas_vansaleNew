package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.model.Receipt;

import java.util.ArrayList;

public class ReportReceiptAdapter extends RecyclerView.Adapter<ReportReceiptAdapter.ReportReceiptHolder> {
    private Context context;
    private ArrayList<Receipt> returnreport;

    String TAG="ReportReceiptAdapter";
    public ReportReceiptAdapter(ArrayList<Receipt> returnreport) {
        Log.e("reached",""+returnreport.size());
        this.returnreport = returnreport;
    }

    @Override
    public ReportReceiptAdapter.ReportReceiptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_returnreport,parent,false);
        return new ReportReceiptAdapter.ReportReceiptHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportReceiptAdapter.ReportReceiptHolder holder, int position) {
        Receipt p = returnreport.get(position);
        Log.e("shopname",""+p.getCustomername());

        int s = position + 1;

        holder.tv_sale_date.setText("Date : "+p.getLogDate());
        holder.tv_shopname.setText("Shop Name : "+p.getCustomername());
        holder.tv_shopcode.setText("Shopcode : "+p.getCustomercode());
        holder.tv_saletotal.setText("Receipt Total : "+p.getReceivedAmount());
        holder.tv_ret_invoiceno.setText("Receipt No : "+p.getReceiptNo());
        holder.tv_slno.setText("SL No : "+s);
    }

    @Override
    public int getItemCount() {
        return returnreport.size();
    }
    class ReportReceiptHolder extends RecyclerView.ViewHolder {

        TextView tv_sale_date, tv_shopname,tv_shopcode ,tv_saletotal ,tv_ret_invoiceno ,tv_slno;

        private ReportReceiptHolder(View itemView) {
            super(itemView);

            tv_sale_date= (TextView) itemView.findViewById(R.id.tv_sale_date);
            tv_shopname= (TextView) itemView.findViewById(R.id.tv_shopname);
            tv_shopcode = (TextView) itemView.findViewById(R.id.tv_shopcode);
            tv_saletotal = (TextView) itemView.findViewById(R.id.tv_saletotal);
            tv_ret_invoiceno =(TextView) itemView.findViewById(R.id.tv_ret_invoiceno);
            tv_slno = (TextView) itemView.findViewById(R.id.tv_slno);
        }
    }
}

