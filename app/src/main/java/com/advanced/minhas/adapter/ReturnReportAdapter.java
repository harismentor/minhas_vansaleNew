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
import com.advanced.minhas.activity.ReturnPreviewActivity;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.INVOICE_RETURN_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;

public class ReturnReportAdapter extends RecyclerView.Adapter<ReturnReportAdapter.RvReturnReportHolder> {
    private Context context;
    private ArrayList<Sales> returnreport;
    private Shop SELECTED_SHOP = null;
    private MyDatabase myDatabase;
    String TAG="RvReturnReportAdapter";
    public ReturnReportAdapter(ArrayList<Sales> returnreport) {
        Log.e("reached",""+returnreport.size());
        this.returnreport = returnreport;
    }

    @Override
    public ReturnReportAdapter.RvReturnReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        myDatabase = new MyDatabase(context);
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_returnreport,parent,false);
        return new ReturnReportAdapter.RvReturnReportHolder(view);
    }

    @Override
    public void onBindViewHolder(ReturnReportAdapter.RvReturnReportHolder holder, int position) {
        final Sales p = returnreport.get(position);
        Log.e("shopname",""+p.getShopname());
        String st_total = getAmount(p.getTotal());
        int s = position + 1;

        holder.tv_sale_date.setText("Date : "+p.getDate());
        holder.tv_shopname.setText("Shop Name : "+p.getShopname());
        holder.tv_shopcode.setText("Shopcode : "+p.getShopcode());
        holder.tv_saletotal.setText("Return Total : "+st_total);
        holder.tv_ret_invoiceno.setText("Invoice No : "+p.getReturn_invoiceno());
        holder.tv_slno.setText("SL No : "+s);
        holder.cv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_SHOP = myDatabase.getIdWiseCustomer(p.getCustomerId());
                Intent intent = new Intent(context, ReturnPreviewActivity.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN);

                intent.putExtra(INVOICE_RETURN_VALUE_KEY, p);
                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return returnreport.size();
    }
    class RvReturnReportHolder extends RecyclerView.ViewHolder {

        TextView tv_sale_date, tv_shopname,tv_shopcode ,tv_saletotal ,tv_ret_invoiceno ,tv_slno;
        CardView cv_click;
        private RvReturnReportHolder(View itemView) {
            super(itemView);

            tv_sale_date= (TextView) itemView.findViewById(R.id.tv_sale_date);
            tv_shopname= (TextView) itemView.findViewById(R.id.tv_shopname);
            tv_shopcode = (TextView) itemView.findViewById(R.id.tv_shopcode);
            tv_saletotal = (TextView) itemView.findViewById(R.id.tv_saletotal);
            tv_ret_invoiceno =(TextView) itemView.findViewById(R.id.tv_ret_invoiceno);
            tv_slno = (TextView) itemView.findViewById(R.id.tv_slno);
            cv_click = itemView.findViewById(R.id.cv_click);
        }
    }
    //    double value to amount format
    public static String getAmount(double amount) {
        String str = "";
        try {
            DecimalFormat formatter = new DecimalFormat("##,##,##,###.000");
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



