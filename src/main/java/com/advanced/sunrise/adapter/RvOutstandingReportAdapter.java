package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.model.OutStandingreport;

import java.util.ArrayList;


/**
 * Created by mentor on 26/04/19.
 */

public class RvOutstandingReportAdapter extends RecyclerView.Adapter<RvOutstandingReportAdapter.RvOutstandingHolder> {

    private Context context;
    private ArrayList<OutStandingreport> outstandingreport;

    String TAG="RvVanStockAdapter";

    public RvOutstandingReportAdapter(ArrayList<OutStandingreport> outstandingreport) {
        this.outstandingreport = outstandingreport;
    }

    @Override
    public RvOutstandingHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outstanding_report,parent,false);
        return new RvOutstandingHolder(view);
    }

    @Override
    public void onBindViewHolder(RvOutstandingHolder holder, int position) {

        OutStandingreport p = outstandingreport.get(position);

     //   int s = position + 1;

        holder.tvDate.setText(p.getSaleDate());
        holder.tvInvoiceno.setText(p.getInvoiceNo());
        holder.tvAmount.setText(p.getSaleAmount());
        holder.tvPaid.setText(p.getPaidAmount());
        holder.tvBalance.setText(p.getBalance());
        holder.tvDays.setText(p.getDays()+" Days");
    }



    @Override
    public int getItemCount() {
        return (null != outstandingreport ? outstandingreport.size() : 0);
    }

    class RvOutstandingHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvInvoiceno, tvAmount, tvPaid, tvBalance, tvDays;

        private RvOutstandingHolder(View itemView) {
            super(itemView);

            tvDate= (TextView) itemView.findViewById(R.id.tv_outstanding_date);
            tvInvoiceno= (TextView) itemView.findViewById(R.id.tv_outstanding_invoiceno);
            tvAmount= (TextView) itemView.findViewById(R.id.tv_outstanding_amount);
            tvPaid=(TextView)  itemView.findViewById(R.id.tv_outstanding_paid);
            tvBalance= (TextView) itemView.findViewById(R.id.tv_outstanding_balance);
            tvDays=(TextView)  itemView.findViewById(R.id.tv_outstanding_days);
        }
    }
}
