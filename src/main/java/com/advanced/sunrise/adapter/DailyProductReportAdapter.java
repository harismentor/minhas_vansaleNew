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
import com.advanced.minhas.model.DailyProductReport;
import com.advanced.minhas.model.Shop;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DailyProductReportAdapter extends RecyclerView.Adapter<DailyProductReportAdapter.RvDailyProductReportHolder> {

    private Context context;
    private ArrayList<DailyProductReport> salereport;
    private Shop SELECTED_SHOP = null;
    private MyDatabase myDatabase;
    String TAG="RvSaleReportAdapter";
    public DailyProductReportAdapter(ArrayList<DailyProductReport> salereport) {
        Log.e("reached",""+salereport.size());
        this.salereport = salereport;

    }

    @Override
    public RvDailyProductReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        myDatabase = new MyDatabase(context);
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_product_report,parent,false);
        return new RvDailyProductReportHolder(view);

    }

    @Override
    public void onBindViewHolder(RvDailyProductReportHolder holder, int position) {
        final DailyProductReport p = salereport.get(position);


        holder.txt_products.setText(p.getProduct());
        holder.txt_sale_qty.setText(String.valueOf(p.getSaleQty()));
        holder.txt_return_qty.setText(p.getReturnQty());
        holder.txt_sale_percent.setText(p.getSalePercentage());
        holder.txt_return_percent.setText(p.getReturnPercentage());
        holder.txt_foc.setText("");
        holder.txt_remarks.setText("");




    }

    @Override
    public int getItemCount() {
        return salereport.size();
    }
    class RvDailyProductReportHolder extends RecyclerView.ViewHolder {

        TextView  txt_products,txt_sale_qty ,txt_sale_percent,txt_return_percent,txt_foc,txt_remarks,txt_return_qty ;

        private RvDailyProductReportHolder(View itemView) {
            super(itemView);

            txt_products= (TextView) itemView.findViewById(R.id.txt_products);
            txt_sale_qty = (TextView) itemView.findViewById(R.id.txt_sale_qty);
            txt_return_qty = (TextView) itemView.findViewById(R.id.txt_return_qty);
            txt_sale_percent =(TextView) itemView.findViewById(R.id.txt_sale_percent);
            txt_return_percent = (TextView) itemView.findViewById(R.id.txt_return_percent);
            txt_foc = (TextView) itemView.findViewById(R.id.txt_foc);
            txt_remarks = (TextView) itemView.findViewById(R.id.txt_remarks);
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
