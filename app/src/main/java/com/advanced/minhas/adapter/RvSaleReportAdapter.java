package com.advanced.minhas.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.PreviewActivity;

import com.advanced.minhas.activity.SalesActivity;
import com.advanced.minhas.holder.RvHistoryHolder;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Transaction;

import java.util.ArrayList;
import java.util.Date;

import static com.advanced.minhas.config.ConfigKey.REQ_EDIT_TYPE;
import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.SALES_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dateToFormat;
import static com.advanced.minhas.config.Generic.stringToDate;
import static com.advanced.minhas.config.Generic.timeToFormat;

/**
 * Created by mentor on 15/1/18.
 */

public class RvSaleReportAdapter extends RecyclerView.Adapter<RvHistoryHolder> {

    private ArrayList<Sales> salesList;
    private Context context;
    private MyDatabase myDatabase;

    private Shop SELECTED_SHOP;
    public RvSaleReportAdapter(ArrayList<Sales> salesList, Shop shop) {
        this.salesList = salesList;
        this.SELECTED_SHOP=shop;
    }

    @Override
    public RvHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_report, parent, false);

        this.context = parent.getContext();
        myDatabase = new MyDatabase(context);
        return new RvHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(RvHistoryHolder holder, int position) {
        final Sales sale=salesList.get(position);

        int s = position + 1;
        Log.e("date in",""+sale.getDate());
        final Date date1 =stringToDate(sale.getDate());
        final String date =""+sale.getDate();
        Log.e("date in",""+date);

        holder.tvSlNo.setText(String.valueOf(s));
        holder.tvInvoiceNo.setText(sale.getInvoiceCode());
        holder.tvDate.setText(dateToFormat(date1));
        //holder.tvDate.setText((date));
        holder.tvTime.setText(timeToFormat(date1));
        //holder.tvTime.setText("");
        holder.tvQty.setText(String.valueOf(sale.getCartItems().size()));

        if(sale.getUploadStatus().equals("Y")){
            holder.ibEdit.setVisibility(View.GONE);
            holder.ibDelete.setVisibility(View.GONE);
        }
        else{
            holder.ibEdit.setVisibility(View.VISIBLE);
            holder.ibDelete.setVisibility(View.VISIBLE);
        }
        holder.ibView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALE_REPORT);

                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                intent.putExtra(SALES_VALUE_KEY, sale);
                context.startActivity(intent);
//              getActivity().finish();
            }
        });

        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("edit in",""+sale.getVat_status());
                //if(sale.getInvoiceCode().contains("R11")){
                // if(sale.getInvoiceCode().contains(SELECTED_SHOP.getRouteCode())){
                Intent intent = new Intent(context, SalesActivity.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_EDITSALE);
                intent.putExtra(SALES_VALUE_KEY, sale);
                intent.putExtra(SHOP_KEY, SELECTED_SHOP);
                context.startActivity(intent);
                ((Activity) context).finish();

            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("delete in", "" + sale.getVat_status());
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setMessage("Are you confirm to delete..?");

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("totalin",""+salesList.get(position).getWithTaxTotal());
                        Log.e("paidin",""+salesList.get(position).getPaid());
                        Transaction t = new Transaction(salesList.get(position).getCustomerId(),salesList.get(position).getWithTaxTotal(), salesList.get(position).getPaid());
                        if (myDatabase.updateCustomerBalance(t)) {
                            for (CartItem c : salesList.get(position).getCartItems()) {
                                myDatabase.updateStock(c, REQ_EDIT_TYPE);
                            }
                        }
                        // Write your code here to execute after dialog closed
                        myDatabase.deleteinvoice_from_sale(sale.getInvoiceCode());
                        myDatabase.deleteinvoice_from_saleproducts(sale.getInvoiceCode());
                        ArrayList<Sales> list=myDatabase.getCustomerSales(String.valueOf(SELECTED_SHOP.getShopId()));
                        salesList.clear();
                        salesList.addAll(list);

                        notifyDataSetChanged();

                        alertDialog.dismiss();

                    }
                });

                // Showing Alert Message
                alertDialog.show();

            }
        });



    }
    public void updateItems( int pos) {
        Log.e("pos innnss",""+pos);

        notifyItemChanged(pos);
    }
    @Override
    public int getItemCount() {
        return salesList.size();
    }

/*
    public class RvSaleReportHolder extends RecyclerView.ViewHolder {

        TextView tvSlNo, tvInvoiceNo,  tvDate, tvTime,tvQty;
        ImageButton ibView;


        public RvSaleReportHolder(View itemView) {
            super(itemView);
            tvSlNo =  (TextView)itemView.findViewById(R.id.textView_item_sale_report_no);
            tvInvoiceNo = (TextView) itemView.findViewById(R.id.textView_item_sale_report_invoice);
            tvQty =  (TextView)itemView.findViewById(R.id.textView_item_sale_report_quantity);
            tvDate = (TextView) itemView.findViewById(R.id.textView_item_sale_report_date);
            tvTime =  (TextView)itemView.findViewById(R.id.textView_item_sale_report_time);

            ibView = (ImageButton) itemView.findViewById(R.id.imageButton_item_sale_report_view);

        }
    }

    */
}
