package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.model.ExpenseDetails;

import java.util.ArrayList;

public class RvExpenseEntryDetails extends RecyclerView.Adapter<RvExpenseEntryDetails.RvOutstandingHolder> {

    private Context context;
    private ArrayList<ExpenseDetails> expenseentry;

    String TAG="RvVanStockAdapter";

    public RvExpenseEntryDetails(ArrayList<ExpenseDetails> expenseentry) {
        this.expenseentry = expenseentry;
    }

    @Override
    public RvOutstandingHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_details, parent,false);
        return new RvOutstandingHolder(view);
    }

    @Override
    public void onBindViewHolder(RvOutstandingHolder holder, int position) {

        ExpenseDetails p = expenseentry.get(position);

        //   int s = position + 1;

        Log.e("list item", ""+p.getRemarks()+"/"+p.getAmount()+"/"+p.getStatus());

        holder.tvexpenseName.setText(""+p.getName());
        holder.tvAmount.setText(""+p.getAmount());
        holder.tvRemarks.setText(""+p.getRemarks());

        if (p.getStatus().equalsIgnoreCase("pending")){
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorNewShop));
        }else if (p.getStatus().equalsIgnoreCase("Approved")){
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.text_green));
        }else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        holder.tvStatus.setText(""+p.getStatus());
    }

    @Override
    public int getItemCount() {
        return (null != expenseentry ? expenseentry.size() : 0);
    }

    class RvOutstandingHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvexpenseName, tvAmount, tvRemarks, tvStatus;

        private RvOutstandingHolder(View itemView) {
            super(itemView);



            //  tvDate= (TextView) itemView.findViewById(R.id.tv_outstanding_date);
            tvexpenseName= (TextView) itemView.findViewById(R.id.textView_expense);
            tvAmount= (TextView) itemView.findViewById(R.id.textView_amount);
            tvRemarks=(TextView)  itemView.findViewById(R.id.textView_remarks);
            tvStatus= (TextView) itemView.findViewById(R.id.textView_status);
        }
    }
}