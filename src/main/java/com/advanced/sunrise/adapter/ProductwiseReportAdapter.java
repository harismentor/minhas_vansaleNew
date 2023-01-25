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
import com.advanced.minhas.model.ProductwiseReportMdl;
import com.advanced.minhas.model.Shop;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductwiseReportAdapter extends RecyclerView.Adapter<ProductwiseReportAdapter.RvProductReportHolder> {

    String TAG = "RvSaleReportAdapter";
    private Context context;
    private ArrayList<ProductwiseReportMdl> salereport;
    private Shop SELECTED_SHOP = null;
    private MyDatabase myDatabase;

    public ProductwiseReportAdapter(ArrayList<ProductwiseReportMdl> salereport) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_productwisereport, parent, false);
        return new RvProductReportHolder(view);

    }

    @Override
    public void onBindViewHolder(RvProductReportHolder holder, int position) {
        final ProductwiseReportMdl p = salereport.get(position);
        int s = position+1;

        holder.txt_slno.setText(""+s);
        holder.txt_category.setText(p.getCategory());
        holder.txt_sumof_qty.setText(String.valueOf(p.getSumofqnty()));
        holder.txt_sumofvalue.setText(p.getSumofvalue());
        holder.txt_tax.setText(p.getTax());
        holder.txt_item.setText(p.getItemname());
        holder.txt_grossvalue.setText(p.getGrossvalue());

    }

    @Override
    public int getItemCount() {
        return salereport.size();
    }

    class RvProductReportHolder extends RecyclerView.ViewHolder {

        TextView txt_slno ,txt_category,txt_item, txt_sumof_qty, txt_sumofvalue, txt_tax, txt_grossvalue;

        private RvProductReportHolder(View itemView) {
            super(itemView);

            txt_slno = (TextView) itemView.findViewById(R.id.txt_slno);
            txt_category = (TextView) itemView.findViewById(R.id.txt_category);
            txt_sumof_qty = (TextView) itemView.findViewById(R.id.txt_sumof_qty);
            txt_sumofvalue = (TextView) itemView.findViewById(R.id.txt_sumofvalue);
            txt_tax = (TextView) itemView.findViewById(R.id.txt_tax);
            txt_grossvalue = (TextView) itemView.findViewById(R.id.txt_grossvalue);
            txt_item = itemView.findViewById(R.id.txt_item);

        }
    }
}
