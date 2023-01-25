package com.advanced.minhas.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.ReturnPreviewActivity;
import com.advanced.minhas.holder.RvHistoryHolder;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;

import java.util.ArrayList;
import java.util.Date;

import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.INVOICE_RETURN_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dateToFormat;
import static com.advanced.minhas.config.Generic.stringToDate;
import static com.advanced.minhas.config.Generic.timeToFormat;

public class RvReturnHistoryAdapter extends RecyclerView.Adapter<RvHistoryHolder> {

    private String TAG="RvReturnHistoryAdapter";
    private ArrayList<Sales> returnList;
    private Context context;

    private Shop SELECTED_SHOP;

    public RvReturnHistoryAdapter(ArrayList<Sales> returnList, Shop SELECTED_SHOP) {
        this.returnList = returnList;
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
        final Sales saleReturn=returnList.get(position);

        int s = position + 1;

        final Date date =stringToDate(saleReturn.getDate());

        holder.lyt_edit.setVisibility(View.GONE);
        holder.tvInvoiceNo.setVisibility(View.GONE);
        holder.ll_invoice.setVisibility(View.GONE);

        holder.tvSlNo.setText(String.valueOf(s));

     //   holder.tvDate.setText("date");
        holder.tvDate.setText(dateToFormat(date));
        holder.tvTime.setText(timeToFormat(date));
     //   holder.tvTime.setText("time");
        holder.tvQty.setText(String.valueOf(saleReturn.getCartItems().size()));
        holder.ibView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ReturnPreviewActivity.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN);

                intent.putExtra(INVOICE_RETURN_VALUE_KEY, saleReturn);
                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

        return returnList.size();
    }
}
