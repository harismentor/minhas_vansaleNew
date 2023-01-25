package com.advanced.minhas.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.PreviewActivity_F;
import com.advanced.minhas.holder.RvHistoryHolder;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;

import java.util.ArrayList;
import java.util.Date;

import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.SALES_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dateToFormat;
import static com.advanced.minhas.config.Generic.stringToDate;
import static com.advanced.minhas.config.Generic.timeToFormat;

public class RvQuotationHistoryAdapter extends RecyclerView.Adapter<RvHistoryHolder> {

    private String TAG="RvQuotationHistoryAdapter";
    private ArrayList<Sales> quotationlst;
    private Context context;

    private Shop SELECTED_SHOP;
    public RvQuotationHistoryAdapter(ArrayList<Sales> quotationLst, Shop SELECTED_SHOP) {
        this.quotationlst = quotationLst;
        this.SELECTED_SHOP = SELECTED_SHOP;
    }

    @Override
    public RvHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_report, parent, false);

        this.context = parent.getContext();
        return new RvHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(RvHistoryHolder holder, int position) {
        final Sales qtn=quotationlst.get(position);

        int s = position + 1;

        final Date date =stringToDate(qtn.getDate());

        holder.tvInvoiceNo.setVisibility(View.GONE);
        holder.ll_invoice.setVisibility(View.GONE);

        holder.tvSlNo.setText(String.valueOf(s));

        //   holder.tvDate.setText("date");
        holder.tvDate.setText(dateToFormat(date));
        holder.tvTime.setText(timeToFormat(date));
        //   holder.tvTime.setText("time");
        holder.tvQty.setText(String.valueOf(qtn.getCartItems().size()));
        holder.ibView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PreviewActivity_F.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_QUOTATION);
                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                intent.putExtra(SALES_VALUE_KEY, qtn);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

        return quotationlst.size();
    }
}
