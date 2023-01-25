package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.model.Quantity;

import java.util.ArrayList;

public class Quantity_adapter extends RecyclerView.Adapter<Quantity_adapter.RvHolder> {
    private Context context;

    private String TAG = "RvCartAdapter";

    private ArrayList<Quantity> cartItems;

    OnNotifyListener listener;

    public Quantity_adapter(ArrayList<Quantity> cartItems, OnNotifyListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @Override
    public RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_salequantity, parent, false);

        this.context = parent.getContext();

        return new RvHolder(view);
    }

    @Override
    public void onBindViewHolder(RvHolder holder, final int position) {

    }


    public ArrayList<Quantity> getCartItems() {
        return cartItems;
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class RvHolder extends RecyclerView.ViewHolder {

        TextView tv_size, tv_availableqty, tv_qty;

        public RvHolder(View itemView) {
            super(itemView);

            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_availableqty = (TextView) itemView.findViewById(R.id.tv_availableqty);
            tv_qty = (TextView) itemView.findViewById(R.id.tv_qty);

        }
    }
}
