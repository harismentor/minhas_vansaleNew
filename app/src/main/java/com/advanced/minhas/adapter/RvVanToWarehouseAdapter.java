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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;


import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.model.CartItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by mentor on 26/04/19.
 */

//public class RvVanToWarehouseAdapter extends RecyclerView.Adapter<RvVanToWarehouseAdapter.RvOutstandingHolder> {
    public class RvVanToWarehouseAdapter extends RecyclerView.Adapter<RvVanToWarehouseAdapter.RvOutstandingHolder>implements Filterable {

    private Context context;
    private ArrayList<CartItem> cartItems_array;
    String confctr="";
    int available_qty =0;

    String TAG="RvVanStockAdapter";
    OnNotifyListener listener;

    public RvVanToWarehouseAdapter(ArrayList<CartItem> cartItems,OnNotifyListener listener) {
        this.cartItems_array = cartItems;
        this.listener=listener;
    }



    @Override
    public RvOutstandingHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_van_towarehouse,parent,false);
        return new RvOutstandingHolder(view);
    }

    @Override
    public void onBindViewHolder(final RvOutstandingHolder holder, final int position) {

        final CartItem p = cartItems_array.get(position);

        Log.e("name", ""+p.getProductName());
        Log.e("stock", ""+p.getStockQuantity());
        Log.e("qty", ""+p.getReturnQuantity());

        //   int s = position + 1;
        try {
            JSONArray arr = new JSONArray(p.getUnitslist());


            for (int i = 0; i < arr.length(); i++) {

                JSONObject jObj = arr.getJSONObject(i);

                String id = jObj.getString("unitId");

                if (id.equals("" + p.getProduct_reporting_Unit())) {
//                    String price = jObj.getString("unitPrice");
                    confctr = jObj.getString("con_factor");
                   String name = jObj.getString("unitName");
                    Log.e("Unit selected", "" + confctr);
                    available_qty = p.getStockQuantity() / Integer.parseInt(confctr);
                   // bal_qty = p.getStockQuantity() % Integer.parseInt(confctr);
                    Log.e("available_qtyhr", "" + available_qty);
                    Log.e("name hr", "" + name);


                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        holder.textView_productname.setText(""+p.getProductName());
      // holder.textView_item_stock.setText(""+p.getStockQuantity());
        holder.textView_item_stock.setText(""+available_qty);
        // holder.edittext_movement_qty.setText(""+p.getReturnQuantity());

        holder.edittext_movement_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int position, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    String st_qty = holder.edittext_movement_qty.getText().toString();
                    int qnty=0;
                    if(holder.edittext_movement_qty.getText().toString().trim().equals("")){
                        Log.e("if",""+st_qty);
                        qnty=0;
                    }
                    else {

                        qnty = Integer.parseInt(holder.edittext_movement_qty.getText().toString());
                        Log.e("else",""+qnty);
                    }

                    if (qnty > p.getStockQuantity()) {
                        holder.edittext_movement_qty.setText("");
                        qnty =0;
                        Toast.makeText(context, "Invalid Quantity !", Toast.LENGTH_SHORT).show();
                    } else {


                        p.setPieceQuantity(qnty);
                        p.setReturnQuantity(qnty);

                        Log.e("else ret", "" + p.getReturnQuantity());
                    }


                }catch (Exception e){

                }
                if (listener != null)
                    listener.onNotified();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        if (listener != null)
            listener.onNotified();


//        holder.imageButton_item_cart_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                delete(position);
//            }
//        });

   /*     holder.textView_productname.setText("Pro");
        holder.textView_item_stock.setText("7");
        holder.textView_movement_qty.setText("3");*/

    }



    @Override
    public int getItemCount() {
        return (null != cartItems_array ? cartItems_array.size() : 0);
    }



//    public void changeItem(CartItem item){
//
//        boolean isAdd=false;
//        if (cartItems.isEmpty()) {
//            addItem(item);
//        }else {
//            for (int i = 0; i < cartItems.size(); i++) {
//
//                if (item.getProductId()==cartItems.get(i).getProductId()) {
//
//                    updateItem(item, i);
//                    isAdd=true;
//
//                }
//            }
//            if (!isAdd){
//                addItem(item);
//            }
//        }
//    }
//
//    private void addItem(CartItem item) {
//        cartItems.add(item);
//        notifyItemInserted(cartItems.size() - 1);
//        notifyDataSetChanged();
//    }
//    public void updateItem(CartItem item, int pos) {
//        cartItems.set(pos,item);
//        notifyItemChanged(pos);
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void delete(int position) { //removes the row

        try {
            cartItems_array.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems_array.size());

            /*if (cartItems.size()                                                                                                                                                                                                                                                                                                                                                                                                                                          == position) {
                if (listener!=null)
                    listener.onNotified();
            }*/
        }catch (IndexOutOfBoundsException e){
            Log.v(TAG,"delete   Exception "+e.getMessage());
        }
    }

    public int get_total_quantity() {
        int total_qnty = 0;
        try {

            // Log.e("hr", "" + cartItems_array.get(2).getProductName());
            for (CartItem p : cartItems_array) {
                Log.e("getReturnQuantity", "" + p.getReturnQuantity());
                total_qnty = (int) (total_qnty + p.getReturnQuantity());
            }
        }catch (Exception e){

        }

        return total_qnty;


    }


    public ArrayList<CartItem> getCartItems(){
        return cartItems_array;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    class RvOutstandingHolder extends RecyclerView.ViewHolder {

        TextView textView_productname, textView_item_stock;
        // ImageButton imageButton_item_cart_delete;
        EditText edittext_movement_qty;

        private RvOutstandingHolder(View itemView) {
            super(itemView);

            textView_productname= (TextView) itemView.findViewById(R.id.textView_productname);
            textView_item_stock= (TextView) itemView.findViewById(R.id.textView_item_stock);
            edittext_movement_qty= (EditText) itemView.findViewById(R.id.edittext_movement_qty);
            //imageButton_item_cart_delete= (ImageButton) itemView.findViewById(R.id.imageButton_item_cart_delete);

        }
    }



}