package com.advanced.minhas.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.ReceiptPreviewActivity;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Shop;

import java.util.ArrayList;
import java.util.Date;

import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.RECEIPT_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dateToFormat;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.stringToDate;
import static com.advanced.minhas.config.Generic.timeToFormat;


/**
 * Created by mentor on 27/10/17.
 */

public class RvReceiptAdapter extends RecyclerView.Adapter<RvReceiptAdapter.RvReceiptHolder> {

    private Context context;

    private String TAG="RvReceiptAdapter";


    private ArrayList<Receipt> receipts;
    private OnNotifyListener listener;

    private MyDatabase myDatabase;

    public RvReceiptAdapter(ArrayList<Receipt> receipts) {
        this.receipts = receipts;

    }


    @Override
    public RvReceiptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false);

        this.listener=(OnNotifyListener)(parent.getContext());
        this.myDatabase=new MyDatabase(context);
        return new RvReceiptHolder(view);
    }


    @Override
    public void onBindViewHolder(final RvReceiptHolder holder, @SuppressLint("RecyclerView") final int position) {

        final Receipt receipt =  receipts.get(position);


        int s = position + 1;

        final Date date = stringToDate(receipt.getLogDate());


        holder.tvSlNo.setText(String.valueOf(s));
        holder.tvReceiptNo.setText(receipt.getReceiptNo());
        holder.tvDate.setText(dateToFormat(date));
        holder.tvTime.setText(timeToFormat(date));
        holder.tvReceivedAmount.setText(getAmount(receipt.getReceivedAmount()));
        holder.tvReceivedBalance.setText(getAmount(receipt.getCurrentBalanceAmount()));


        holder.ibView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Intent intent = new Intent(context, Preview_Receipt.class);
                Intent intent = new Intent(context, ReceiptPreviewActivity.class);

                Shop s=myDatabase.getIdWiseCustomer(receipt.getCustomerId());
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_OUTSTANDING_HISTORY_RECEIPT);
                intent.putExtra(SHOP_VALUE_KEY, s);

                intent.putExtra(RECEIPT_KEY, receipt);

                context.startActivity(intent);
            }
        });
    }


    public void updateItem(final Receipt item, final int pos) {

        this.receipts.set(pos,item);
        notifyItemChanged(pos);
    }


    private void delete(int position) { //removes the row

        receipts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, receipts.size());
    }

    private void addItem(Receipt item) {
        receipts.add(item);
        notifyItemInserted(receipts.size() - 1);
        notifyDataSetChanged();
    }


    public float getNetTotal(){
        float netTotal=0.0f;
        try {

            for (Receipt receipt :receipts) {
                if (receipt.getReceivedAmount() != 0.0f) {
                    float d = receipt.getReceivedAmount();
                    netTotal += d;
                }
            }

        }catch (NullPointerException e){
            Log.d(TAG,"getNetTotal Exception  "+e.getMessage());

        }
        return netTotal;
    }


    public ArrayList<Receipt> getPaymentList(){

        return receipts;
    }



    public ArrayList<Receipt> getRecievableList(){

        ArrayList<Receipt> list=new ArrayList<>();

        try {

            for (Receipt rec:receipts){

                if (rec.getReceivedAmount()!=0.0)
                    list.add(rec);
            }

        }catch (NullPointerException e){
            Log.d(TAG,"getRecievableList Exception  "+e.getMessage());

        }
        return list;
    }

    @Override
    public int getItemCount() {

        return (null != receipts ? receipts.size() : 0);
    }


    class RvReceiptHolder extends RecyclerView.ViewHolder {
        TextView tvSlNo, tvReceiptNo,  tvDate, tvTime,tvReceivedAmount,tvReceivedBalance;
        ImageButton ibView;

        RvReceiptHolder(View itemView) {
            super(itemView);

            tvSlNo = itemView.findViewById(R.id.textView_item_receiptHistory_slNo);
            tvReceiptNo = itemView.findViewById(R.id.textView_item_receiptHistory_receipt);
            tvDate = itemView.findViewById(R.id.textView_item_receiptHistory_date);
            tvTime = itemView.findViewById(R.id.textView_item_receiptHistory_time);
            tvReceivedAmount = itemView.findViewById(R.id.textView_item_receiptHistory_receivedAmount);
            tvReceivedBalance = itemView.findViewById(R.id.textView_item_receiptHistory_balance);

            ibView = itemView.findViewById(R.id.imageButton_item_receiptHistory_view);

        }
    }




}
