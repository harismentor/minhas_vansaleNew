package com.advanced.minhas.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.advanced.minhas.R;

public class RvHistoryHolder extends RecyclerView.ViewHolder {

    public TextView tvSlNo, tvInvoiceNo,  tvDate, tvTime,tvQty;
    public ImageButton ibView ,ibEdit ,ibDelete;
    public LinearLayout ll_invoice,lyt_edit;



    public RvHistoryHolder(View itemView) {
        super(itemView);
        tvSlNo = itemView.findViewById(R.id.textView_item_sale_report_no);
        tvInvoiceNo = itemView.findViewById(R.id.textView_item_sale_report_invoice);
        tvQty = itemView.findViewById(R.id.textView_item_sale_report_quantity);
        tvDate = itemView.findViewById(R.id.textView_item_sale_report_date);
        tvTime = itemView.findViewById(R.id.textView_item_sale_report_time);
        ibEdit = itemView.findViewById(R.id.imageButton_item_sale_report_edit);
        ibView = itemView.findViewById(R.id.imageButton_item_sale_report_view);
        ibDelete = itemView.findViewById(R.id.imageButton_item_sale_report_delete);
        lyt_edit = itemView.findViewById(R.id.lyt_edit);
        ll_invoice = itemView.findViewById(R.id.layout_invoice);

    }
}
