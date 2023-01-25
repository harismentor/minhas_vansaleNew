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
import com.advanced.minhas.model.DailyReport;
import com.advanced.minhas.model.Shop;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DailyReportAdapter extends RecyclerView.Adapter<DailyReportAdapter.RvDailyReportHolder> {

    private Context context;
    private ArrayList<DailyReport> dailyReports;
    private Shop SELECTED_SHOP = null;
    private MyDatabase myDatabase;
    String TAG="RvSaleReportAdapter";
    public DailyReportAdapter(ArrayList<DailyReport> dailyReports) {
        Log.e("reached",""+dailyReports.size());
        this.dailyReports = dailyReports;

    }

    @Override
    public RvDailyReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        myDatabase = new MyDatabase(context);
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_report,parent,false);
        return new RvDailyReportHolder(view);

    }

    @Override
    public void onBindViewHolder(RvDailyReportHolder holder, int position) {
        final DailyReport p = dailyReports.get(position);


        holder.txt_outlet.setText(p.getCustomer());
        holder.txt_cash_sale.setText(p.getTotalCashSale());
        holder.txt_credit_sale.setText(String.valueOf(p.getTotalCreditSale()));
        holder.txt_return_sale.setText(p.getTotalReturnSale());
        holder.txt_cash_collection.setText(p.getTotalCashCollection());
        holder.txt_bank_collection.setText(p.getTotalBankCollection());
        holder.txt_cheque_collection.setText(p.getTotalChequeCollection());





    }

    @Override
    public int getItemCount() {
        return dailyReports.size();
    }
    class RvDailyReportHolder extends RecyclerView.ViewHolder {

        TextView txt_outlet, txt_cash_sale,txt_credit_sale ,txt_return_sale ,txt_cash_collection,txt_bank_collection,txt_cheque_collection ;

        private RvDailyReportHolder(View itemView) {
            super(itemView);

            txt_outlet= (TextView) itemView.findViewById(R.id.txt_outlet);
            txt_cash_sale= (TextView) itemView.findViewById(R.id.txt_cash_sale);
            txt_credit_sale = (TextView) itemView.findViewById(R.id.txt_credit_sale);
            txt_return_sale = (TextView) itemView.findViewById(R.id.txt_return_sale);
            txt_cash_collection =(TextView) itemView.findViewById(R.id.txt_cash_collection);
            txt_bank_collection =(TextView) itemView.findViewById(R.id.txt_bank_collection);
            txt_cheque_collection =(TextView) itemView.findViewById(R.id.txt_cheque_collection);

        }
    }
    //    double value to amount format
    public static String getAmount(double amount) {
        String str = "";
        try {
            DecimalFormat formatter = new DecimalFormat("##,##,##,###.00");
            str = formatter.format(amount);
            if (str.contains(",")){
                str = str.replaceAll(",","");
            }
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return str;
    }
}
