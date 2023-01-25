package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.model.BonusReport;

import java.util.ArrayList;


/**
 * Created by mentor on 26/04/19.
 */

public class RvBonusReportAdapter extends RecyclerView.Adapter<RvBonusReportAdapter.RvBonusHolder> {

    private Context context;
    private ArrayList<BonusReport> bonusreport;

    String TAG="RvVanStockAdapter";

    public RvBonusReportAdapter(ArrayList<BonusReport> bonusreport) {
        this.bonusreport = bonusreport;
    }

    @Override
    public RvBonusHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bonus_report,parent,false);
        return new RvBonusHolder(view);
    }

    @Override
    public void onBindViewHolder(RvBonusHolder holder, int position) {

        BonusReport p = bonusreport.get(position);

     //   int s = position + 1;

        holder.tvInvoiceno.setText(p.getInvoiceNo());
        holder.tvCreditnote.setText(p.getCreditNoteNo());
        holder.tvcustName.setText(p.getCustomerName());
        holder.tvbonusamount.setText(p.getBonusAmount());
        holder.tvreturnamount.setText(p.getBonusReturnAmount());

    }


    @Override
    public int getItemCount() {
        return (null != bonusreport ? bonusreport.size() : 0);
    }

    class RvBonusHolder extends RecyclerView.ViewHolder {

        TextView tvInvoiceno, tvCreditnote, tvcustName, tvbonusamount, tvreturnamount;

        private RvBonusHolder(View itemView) {
            super(itemView);

            tvInvoiceno= (TextView) itemView.findViewById(R.id.text_bonus_invoiceno);
            tvCreditnote= (TextView) itemView.findViewById(R.id.text_bonus_creditnoteno);
            tvcustName= (TextView) itemView.findViewById(R.id.text_bonus_customername);
            tvbonusamount=(TextView)  itemView.findViewById(R.id.text_bonus_bonusamount);
            tvreturnamount= (TextView) itemView.findViewById(R.id.text_bonus_returnamount);

        }
    }
}
