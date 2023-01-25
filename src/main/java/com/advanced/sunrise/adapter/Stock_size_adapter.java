package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.model.Size;

import java.util.ArrayList;

public class Stock_size_adapter extends RecyclerView.Adapter<Stock_size_adapter.RvSizeHolder> {
    private Context context;

    private String TAG = "Stock_size_adapter";

    private ArrayList<Size> size_arrayfull ;

    OnNotifyListener listener;
    public Stock_size_adapter(ArrayList<Size> size_arrayfull1, OnNotifyListener listener) {

        this.size_arrayfull= size_arrayfull1;
        this.listener=listener;
        Log.e("size_arrayfull1",""+size_arrayfull1);

    }
    @Override
    public Stock_size_adapter.RvSizeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_size, parent, false);

        this.context = parent.getContext();

        return new Stock_size_adapter.RvSizeHolder(view);
    }

    @Override
    public void onBindViewHolder(final Stock_size_adapter.RvSizeHolder holder, final int position) {

        final Size s = size_arrayfull.get(position);
        Log.e("getSizeId",""+s.getSizeId());

        holder.tv_size.setText(""+s.getSizeId());
        float fl_qty = Float.parseFloat(s.getQuantity());
        int int_available_qty = Math.round(fl_qty);

        holder.tv_availableqty.setText(""+int_available_qty);
        //holder.tv_qty.setText(sizeandqty_array.get(position));

        size_arrayfull.get(position).setTotal_qty(holder.tv_availableqty.getText().toString().trim().isEmpty() ? "0" : holder.tv_availableqty.getText().toString().trim());

        if (listener!=null)
            listener.onNotified();

    }

    public String getTotal_quantity(){

        String qty = "";
        float fl_qty =0;
        try {

            for (Size s : size_arrayfull) {
                Log.e("qty", "" + s.getQuantity());
                Log.e("sze", "" + s.getSelected_sizes());

                fl_qty = fl_qty + Float.parseFloat(""+s.getQuantity());
                Log.e("fl_qty", "" + fl_qty);
            }
            //  qty = String.format("%.2f", fl_qty);
            qty = "" + fl_qty;
            Log.e("total", "total" + fl_qty);

            // return total_qty;
        }catch (Exception e){
            Log.e("error","error");
            qty = "" + fl_qty;
            Log.e("total", "total" + fl_qty);
        }
        return qty;
    }
    @Override
    public int getItemCount() {
        return size_arrayfull.size();
    }
    public class RvSizeHolder extends RecyclerView.ViewHolder {

        TextView tv_size, tv_availableqty;


        public RvSizeHolder(View itemView) {
            super(itemView);

            tv_size =  (TextView)itemView.findViewById(R.id.tv_size);
            tv_availableqty = (TextView) itemView.findViewById(R.id.tv_availableqty);


        }
    }
}
