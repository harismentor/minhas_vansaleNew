package com.advanced.minhas.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.StockApproval;
import com.advanced.minhas.model.Stocktransfer;
import com.advanced.minhas.session.SessionValue;

import java.util.ArrayList;

public class RvStockTransferAdapter extends RecyclerView.Adapter<RvStockTransferAdapter.RvVanStockHolder>{

    private Context context;

    private ArrayList<Stocktransfer> filteredList;
    SessionValue sessionValue;

    String TAG="RvVanStockAdapter", CURRENCY="";

    public RvStockTransferAdapter(ArrayList<Stocktransfer> products) {
        this.filteredList = products;
    }

    @Override
    public RvVanStockHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_approval,parent,false);

        return new RvVanStockHolder(view);
    }

    @Override
    public void onBindViewHolder(RvVanStockHolder holder, int position) {
        Log.e("szeeeeee",""+filteredList.size());
        final Stocktransfer p=filteredList.get(position);


        final int s = position + 1;

        Log.e("id",""+p.getTransferNo());
        Log.e("desc",""+p.getDescription());

        holder.tvSlNo.setText(""+s);
        if(p.getOrderNo().equals("")){
            holder.text_id.setText("Transfer ID : ");
        }
        else{
            holder.text_id.setText("Order ID : ");
        }
        holder.tvTransferID.setText(""+p.getTransferNo());
        holder.tvDate.setText(""+p.getDate());
        holder.tvFrom.setText(""+p.getStockFrom());
        holder.tvTo.setText(""+p.getStockTo());
        holder.tvDescription.setText(""+p.getDescription());

        holder.ll_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, StockApproval.class);
                intent.putExtra("id", ""+p.getTransferNo());
                intent.putExtra("date", ""+p.getDate());
                intent.putExtra("from", ""+p.getStockFrom());
                intent.putExtra("to", ""+p.getStockTo());
                intent.putExtra("desc", ""+p.getDescription());
                intent.putExtra("order_id", ""+p.getOrderNo());
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return (null != filteredList ? filteredList.size() : 0);

    }




    class RvVanStockHolder extends RecyclerView.ViewHolder {

        TextView tvSlNo, tvTransferID, tvDate, tvFrom, tvTo, tvDescription ,text_id;
        LinearLayout ll_layout;

        private RvVanStockHolder(View itemView) {
            super(itemView);

            ll_layout = (LinearLayout)itemView.findViewById(R.id.layout_view);

            tvSlNo = (TextView) itemView.findViewById(R.id.textView_item_stock_serialNo);
            tvTransferID = (TextView) itemView.findViewById(R.id.textView_item_stock_transferid);
            tvDate = (TextView) itemView.findViewById(R.id.textView_item_stock_date);
            tvFrom = (TextView) itemView.findViewById(R.id.textView_item_stock_from);
            tvTo = (TextView) itemView.findViewById(R.id.textView_item_stock_to);
            tvDescription = (TextView) itemView.findViewById(R.id.textView_item_stock_description);
            text_id =  (TextView) itemView.findViewById(R.id.textid);


        }
    }
}
