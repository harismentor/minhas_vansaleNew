package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.model.Size_Return;

import java.util.ArrayList;

public class Return_SizeAndQuantityAdapter extends RecyclerView.Adapter<Return_SizeAndQuantityAdapter.RvSizeHolder> {
    private Context context;

    private String TAG = "SizeAndQuantityAdapter";

    String st_each_sizeqty = "";
    private ArrayList<String> total_size_array = new ArrayList<>();

    private ArrayList<Size_Return> size_arrayfull;

    int total_qty = 0;

    OnNotifyListener listener;
    public Return_SizeAndQuantityAdapter(ArrayList<Size_Return> size_arrayfull1, OnNotifyListener listener) {

        this.size_arrayfull= size_arrayfull1;
        this.listener=listener;

    }
    @Override
    public Return_SizeAndQuantityAdapter.RvSizeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_salequantity, parent, false);

        this.context = parent.getContext();

        return new Return_SizeAndQuantityAdapter.RvSizeHolder(view);
    }
    @Override
    public void onBindViewHolder(final Return_SizeAndQuantityAdapter.RvSizeHolder holder, final int position) {

        final Size_Return s = size_arrayfull.get(position);

        holder.tv_size.setText(""+s.getReturn_size());

        holder.tv_availableqty.setText(""+s.getReturn_qnty());
        //holder.tv_qty.setText(sizeandqty_array.get(position));
        try {
            size_arrayfull.get(position).setTotal_qty("0");
        }catch (Exception e){

        }

        holder.edt_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    float available_qty = Float.parseFloat(holder.tv_availableqty.getText().toString());

                    int qty = Integer.parseInt(holder.edt_qty.getText().toString().trim().isEmpty() ? "0" : holder.edt_qty.getText().toString().trim());
                    // int qty = Integer.parseInt(holder.edt_qty.getText().toString());

                    // Log.e("qty",""+qty);
                    Log.e("available_qty",""+available_qty);
                    Log.e("position",""+count);

                    try {
                        if(available_qty<qty){
                            holder.edt_qty.setText("0");
                            Toast.makeText(context,"Quantity Exceeds Available Quantity",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.e("else",""+qty);
                            size_arrayfull.get(position).setTotal_qty(holder.edt_qty.getText().toString().trim().isEmpty() ? "0" : holder.edt_qty.getText().toString().trim());
                            size_arrayfull.get(position).setSelected_sizes(holder.tv_size.getText().toString().trim().isEmpty() ? "0" : holder.tv_size.getText().toString().trim());

                            size_arrayfull.get(position).setNew_qnty_aftersale(Float.parseFloat(holder.tv_availableqty.getText().toString().trim().isEmpty() ? "0" : holder.tv_availableqty.getText().toString().trim()) + (Float.parseFloat(holder.edt_qty.getText().toString().trim().isEmpty() ? "0" : holder.edt_qty.getText().toString().trim())));
                        }



                    }catch (Exception e){

                    }


                    if (listener!=null)
                        listener.onNotified();
                }


                //}
                catch (Exception e){

                }

                if (listener!=null)
                    listener.onNotified();


            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (listener!=null)
            listener.onNotified();

    }


    @Override
    public int getItemCount() {
        return size_arrayfull.size();
    }

    public ArrayList<Size_Return> get_new_qnty_aftersale(){

        try {
            for(Size_Return s : size_arrayfull){
                int in = Integer.parseInt(s.getTotal_qty());
                Log.e("getQuantity 11",""+s.getQuantity());
                Log.e("getQuantity 22",""+in);
                Log.e("getTotal_qty 11",""+s.getTotal_qty());
                s.setSize_stock(s.getQuantity());
                if(s.getNew_qnty_aftersale()==0 ){
                    if(s.getTotal_qty().equals(""+in) && in>0){
                        Log.e("aftersale_qty if",""+s.getNew_qnty_aftersale());
                    }
                    else {

                        s.setNew_qnty_aftersale(Float.parseFloat(s.getQuantity()));
                    }

                }
                Log.e("getQuantity",""+s.getQuantity());
                Log.e("aftersale_qty",""+s.getNew_qnty_aftersale());
                Log.e("setSize_stock",""+s.getSize_stock());
            }




        }catch (Exception e){

        }



        return size_arrayfull;
    }

    public String getTotal_quantity(){

        String qty = "";
        int fl_qty =0;
        try {

            for (Size_Return s : size_arrayfull) {
                //Log.e("qty", "1" + s.getTotal_qty());
                fl_qty = fl_qty + Integer.parseInt(""+s.getTotal_qty());
                //Log.e("fl_qty", "" + fl_qty);
            }
            //  qty = String.format("%.2f", fl_qty);
            qty = "" + fl_qty;
            //Log.e("total", "total" + fl_qty);

            // return total_qty;
        }catch (Exception e){
            //Log.e("error","error");
            qty = "" + fl_qty;
            //Log.e("total", "total" + fl_qty);
        }
        return qty;
    }

    public String get_selected_sizes(){
        String st_selected_size ="";
        String size_sel = "";
        st_each_sizeqty = "";

        for (Size_Return s : size_arrayfull) {
            // Log.e("sizes", "1" + s.getSelected_sizes());

            try {
                if(Integer.parseInt(s.getSelected_sizes())>0) {
                    if(!s.getTotal_qty().equals("0")) {
                        //st_selected_size = st_selected_size + Integer.parseInt("" + s.getSelected_sizes());
                        st_selected_size = st_selected_size +","+ Integer.parseInt("" + s.getSelected_sizes());
                        size_sel = s.getSelected_sizes();
                        total_size_array.add(s.getSelected_sizes());
                    }
                }
            }catch (Exception e){

            }
            try{
                //total_eachsize_qty_array
                if(!s.getTotal_qty().equals("0")) {
                    st_each_sizeqty = st_each_sizeqty + s.getSelected_sizes() + "-" + s.getTotal_qty()+",";
                }

            }catch (Exception e){

            }
            //Log.e("st_each_sizeqty", "" + st_each_sizeqty);
            // Log.e("getSelected_sizes", "" + st_selected_size);

        }


       // total_size_array.add(size_sel);

        String value = "";
        value = st_selected_size.substring((st_selected_size.length()-(st_selected_size.length()-1)));
        Log.e("value",value);

//        return st_selected_size;
        return value;

       // return st_selected_size;

    }
    public ArrayList<String>get_sizearray_selected(){
        return total_size_array;

    }
    public String get_each_size_qty(){
        return st_each_sizeqty;
    }
    public class RvSizeHolder extends RecyclerView.ViewHolder {

        TextView tv_size, tv_availableqty;
        EditText edt_qty;

        public RvSizeHolder(View itemView) {
            super(itemView);

            tv_size =  (TextView)itemView.findViewById(R.id.tv_size);
            tv_availableqty = (TextView) itemView.findViewById(R.id.tv_availableqty);
            edt_qty =  (EditText) itemView.findViewById(R.id.edt_qty);


        }
    }

}
