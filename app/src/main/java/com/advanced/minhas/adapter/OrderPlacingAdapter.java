package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.model.OrderItems;
import com.advanced.minhas.R;


import java.util.ArrayList;

/**
 * Created by Hari on 15/08/19.
 */

public class OrderPlacingAdapter extends RecyclerView.Adapter<OrderPlacingAdapter.ReceiptHolder> {

    private Context context;
    ArrayList<OrderItems> array_product = new ArrayList<>();
    private ArrayList<OrderItems> filteredList;
    private ArrayList<String> orderlist;
    int total_required_products = 0;
    String TAG = "ReceiptAdapter";
    OnNotifyListener listener;

    int flag = 0;

    public OrderPlacingAdapter(ArrayList<OrderItems> array_product, OnNotifyListener listener) {
        this.array_product = array_product;
        this.listener = listener;
        this.filteredList = array_product;
    }

    @Override
    public ReceiptHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_list, parent, false);
        return new ReceiptHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReceiptHolder holder,final int position) {




        final OrderItems p = filteredList.get(position);

        holder.tv_product.setText("" + p.getProductname());
        //holder.textviewv_tray.setText("" + p.getTray_count_each());

        if (p.getStockqty() == 0) {
            holder.edt_qty.setText("0");
        } else {
            holder.edt_qty.setText("" + p.getStockqty());
        }
        if (p.getTotal_req_products() == 0) {
            holder.edt_req_qty.setText("");
        } else {
            holder.edt_req_qty.setText("" + p.getTotal_req_products());
        }

        holder.edt_req_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    int qty = TextUtils.isEmpty(holder.edt_req_qty.getText().toString()) ? 0 : Integer.valueOf(holder.edt_req_qty.getText().toString());
                    p.setRequired_qty(qty);
                    filteredList.get(position).setTotal_req_products(TextUtils.isEmpty(holder.edt_req_qty.getText().toString()) ? 0 : Integer.valueOf(holder.edt_req_qty.getText().toString()));


                } catch (Exception e) {

                }
                if (listener != null)
                    listener.onNotified();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (listener != null)
            listener.onNotified();



    }


    @Override
    public int getItemCount() {
        return (null != filteredList ? filteredList.size() : 0);
    }




    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int get_total_count() {
        // Log.e("inside...","hrct"+1);
        total_required_products = 0;
        for (OrderItems o : array_product) {

            total_required_products = total_required_products + o.getTotal_req_products();

        }
        return total_required_products;
    }




    public ArrayList<OrderItems> getOrderItems(){
//        for(int i =0;i<=array_product.size();i++){
//            Log.e("name_array_product",""+array_product.get(i).getProductname());
//        }

        return filteredList;
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    filteredList = array_product;
                } else {

                    ArrayList<OrderItems> list = new ArrayList<>();

                    for (OrderItems s : array_product) {

                        if (s.getProductname().toLowerCase().contains(charString) || s.getProductname().toUpperCase().contains(charString)) {

                            list.add(s);
                        }
                    }

                    filteredList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<OrderItems>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }
    public class ReceiptHolder extends RecyclerView.ViewHolder {

        TextView tv_product ,edt_qty;
        EditText edt_req_qty;


        private ReceiptHolder(View itemView) {
            super(itemView);

            tv_product = (TextView) itemView.findViewById(R.id.textviewv_productname);
            edt_qty = (TextView) itemView.findViewById(R.id.edittext_qty);
            edt_req_qty = (EditText) itemView.findViewById(R.id.edt_req_qty);

        }
    }
}
