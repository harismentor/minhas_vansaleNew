package com.advanced.minhas.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.PreviewActivity;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.SALES_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;

public class SaleReportAdapter extends RecyclerView.Adapter<SaleReportAdapter.RvSaleReportHolder> {

    private Context context;
    private ArrayList<Sales> salereport;
    private Shop SELECTED_SHOP = null;
    private MyDatabase myDatabase;
    String TAG="RvSaleReportAdapter";
    public SaleReportAdapter(ArrayList<Sales> salereport) {
        Log.e("reached",""+salereport.size());
        this.salereport = salereport;

    }

    @Override
    public SaleReportAdapter.RvSaleReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        myDatabase = new MyDatabase(context);
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salereport,parent,false);
        return new SaleReportAdapter.RvSaleReportHolder(view);

    }

    @Override
    public void onBindViewHolder(SaleReportAdapter.RvSaleReportHolder holder, int position) {
        final Sales p = salereport.get(position);
        Log.e("shopname",""+p.getShopname());
        String st_total = getAmount(p.getWithTaxTotal());
        Log.e("st_total",""+st_total);
        int s = position + 1;

        holder.tv_sale_date.setText("Date : "+p.getDate());
        holder.tv_shopname.setText("Shop Name : "+p.getShopname());
        holder.tv_shopcode.setText("Shopcode : "+p.getShopcode());
        holder.tv_saletotal.setText("Sale Total : "+st_total);
        holder.tv_sale_invoiceno.setText("Invoice No : "+p.getInvoiceCode());
        holder.tv_slno.setText("SL No : "+s);


        holder.cv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_SHOP = myDatabase.getIdWiseCustomer(p.getCustomerId());
                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALE_REPORT);
                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                intent.putExtra(SALES_VALUE_KEY, p);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return salereport.size();
    }
    class RvSaleReportHolder extends RecyclerView.ViewHolder {

        TextView tv_sale_date, tv_shopname,tv_shopcode ,tv_saletotal ,tv_sale_invoiceno,tv_slno ;
        CardView cv_click;

        private RvSaleReportHolder(View itemView) {
            super(itemView);

            tv_sale_date= (TextView) itemView.findViewById(R.id.tv_sale_date);
            tv_shopname= (TextView) itemView.findViewById(R.id.tv_shopname);
            tv_shopcode = (TextView) itemView.findViewById(R.id.tv_shopcode);
            tv_saletotal = (TextView) itemView.findViewById(R.id.tv_saletotal);
            tv_sale_invoiceno =(TextView) itemView.findViewById(R.id.tv_sale_invoiceno);
            tv_slno = (TextView) itemView.findViewById(R.id.tv_slno);
            cv_click = itemView.findViewById(R.id.cv_click);
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
