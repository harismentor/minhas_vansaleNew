package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.model.Size;
import com.advanced.minhas.textwatcher.TextValidator;

import java.util.ArrayList;

public class Edit_SaleqtyAdapter extends RecyclerView.Adapter<Edit_SaleqtyAdapter.RvHolder> {
    private Context context;

    private String TAG = "Edit_SaleqtyAdapter";

    String st_each_sizeqty="";
    private ArrayList<Size> size_array_afteredit = new ArrayList<>();
    private ArrayList<String> total_eachsize_qty_array = new ArrayList<>();
    private ArrayList<Size> size_arrayfull ;
    private ArrayList<String>size_continous_array = new ArrayList<>();
    int total_qty = 0;

    OnNotifyListener listener;


    public Edit_SaleqtyAdapter(ArrayList<Size> size_arrayfull1, OnNotifyListener listener) {

        this.size_arrayfull= size_arrayfull1;
        this.listener=listener;

    }
    @Override
    public Edit_SaleqtyAdapter.RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_edit, parent, false);

        this.context = parent.getContext();

        return new Edit_SaleqtyAdapter.RvHolder(view);
    }
    @Override
    public void onBindViewHolder(final Edit_SaleqtyAdapter.RvHolder holder, final int position) {

        final Size s = size_arrayfull.get(position);

        holder.tv_size.setText(""+s.getSizeId());

        Log.e("getSize_stock",""+s.getSizeId());
        Log.e("getQuantity",""+s.getTotal_qty());
        Log.e("getSize_after_edit",""+s.getSize_after_edit());
        Log.e("tv_availableqty",""+s.getSize_stock());
        holder.tv_availableqty.setText(""+s.getSize_stock());

        holder.tv_qty.setText(s.getTotal_qty());
        try {
            size_arrayfull.get(position).setSize_after_edit(Float.parseFloat(holder.tv_availableqty.getText().toString().trim().isEmpty() ? "0" : holder.tv_availableqty.getText().toString().trim()) - (Float.parseFloat(holder.tv_qty.getText().toString().trim().isEmpty() ? "0" : holder.tv_qty.getText().toString().trim())));

        }catch (Exception e){

        }
        if(!s.getTotal_qty().equals("0") ) {
            if (!s.getTotal_qty().equals("0.0")) {
                size_arrayfull.get(position).setSelected_sizes(holder.tv_size.getText().toString().trim().isEmpty() ? "0" : holder.tv_size.getText().toString().trim());
            }
        }
        holder.tv_qty.addTextChangedListener(new TextValidator(holder.tv_qty) {
            @Override
            public void validate(TextView textView, String qtyString) {
                Log.e("cart quan", "" + "in"+qtyString);


                    int quantity = TextUtils.isEmpty(qtyString) ? 0 : Integer.valueOf(qtyString);
                    size_arrayfull.get(position).setTotal_qty(""+quantity);
                    //size_arrayfull.get(position).setSizeId(s.getSizeId());
                if(!s.getTotal_qty().equals("0") ) {
                    if (!s.getTotal_qty().equals("0.0")) {
                        size_arrayfull.get(position).setSelected_sizes(holder.tv_size.getText().toString().trim().isEmpty() ? "0" : holder.tv_size.getText().toString().trim());
                    }
                }
                //size_arrayfull.get(position).setSelected_sizes(holder.tv_size.getText().toString().trim().isEmpty() ? "0" : holder.tv_size.getText().toString().trim());

                size_arrayfull.get(position).setSize_after_edit(Float.parseFloat(holder.tv_availableqty.getText().toString().trim().isEmpty() ? "0" : holder.tv_availableqty.getText().toString().trim()) - (Float.parseFloat(holder.tv_qty.getText().toString().trim().isEmpty() ? "0" : holder.tv_qty.getText().toString().trim())));

                    Log.e("cart quantity", "" + quantity);
                Log.e("setSize_after_edit", "" + size_arrayfull.get(position).getSize_after_edit());

               // }
                if (listener!=null)
                    listener.onNotified();
            }


        });

        holder.bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String text = holder.tv_qty.getText().toString().trim();

                    int nextSize = 0;

                    if (!text.isEmpty())
                        nextSize = Integer.parseInt(text);

                    /**increment numbers */

                        nextSize++;
                        Log.e("availqty",s.getSize_stock());
                    if(nextSize<=Float.parseFloat(s.getSize_stock())) {
                        holder.tv_qty.setText(String.valueOf(nextSize));
                    }
                      else
                      Toast.makeText(context,"Limit reached..!",Toast.LENGTH_LONG).show();


                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });

        holder.bt_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String text = holder.tv_qty.getText().toString().trim();
                    int previousSize = 0;

                    if (!text.isEmpty())
                        previousSize = Integer.parseInt(text);

                    /**decrement numbers up to 1*/
                    if (previousSize >0) {
                        previousSize--;
                        Log.e("availqty",s.getSize_stock());
                        Log.e("previousSize",""+previousSize);
                        if(previousSize<=Float.parseFloat(s.getSize_stock()))
                        holder.tv_qty.setText(String.valueOf(previousSize));
                        else
                            Toast.makeText(context,"Limit reached..!",Toast.LENGTH_LONG).show();
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });


        if (listener!=null)
            listener.onNotified();

    }
    public ArrayList<Size> get_sizequantity_array(){
        size_array_afteredit.clear();

        try {
            for(Size s : size_arrayfull){
//                if(!s.getTotal_qty().equals("0.0") ) {
//                    if(!s.getTotal_qty().equals("0")) {
                        Log.e("haris", "" + s.getSize_after_edit());
                        s.setSize_after_edit(s.getSize_after_edit());
                        s.setSizeId(s.getSizeId());
                        s.setTotal_qty(s.getTotal_qty());

                        size_array_afteredit.add(s);
                        Log.e("setSize_after_edit", "" + s.getSize_after_edit());
                        Log.e("afteredit_qqty", "" + s.getQuantity());
//                    }
//                }

            }


        }catch (Exception e){

        }


        return size_array_afteredit;
    }


    public String getTotal_quantity(){

        String qty = "";
        float fl_qty =0;
        try {

            for (Size s : size_arrayfull) {
                //Log.e("qty", "1" + s.getTotal_qty());
                fl_qty = fl_qty + Float.parseFloat(""+s.getTotal_qty());
                //Log.e("fl_qty", "" + fl_qty);
            }
            //  qty = String.format("%.2f", fl_qty);
            qty = "" + fl_qty;
            //Log.e("total", "total" + fl_qty);

            // return total_qty;
        }catch (Exception e){
            //Log.e("error","error");
            qty = "" + fl_qty;

        }
        Log.e("total", "total" + qty);
        return qty;
    }

    @Override
    public int getItemCount() {
        return size_arrayfull.size();
    }

    public class RvHolder extends RecyclerView.ViewHolder {

        TextView tv_size, tv_availableqty,tv_qty;
        ImageButton bt_add,bt_minus;


        public RvHolder(View itemView) {
            super(itemView);

            tv_size =  (TextView)itemView.findViewById(R.id.tv_size);
            tv_availableqty = (TextView) itemView.findViewById(R.id.tv_availableqty);
            tv_qty =  (TextView) itemView.findViewById(R.id.tv_qty);
            bt_add =itemView.findViewById(R.id.bt_add);
            bt_minus =itemView.findViewById(R.id.bt_minus);


        }
    }
}
