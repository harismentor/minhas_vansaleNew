package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.session.SessionValue;

import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigValue.PRODUCT_UNIT_CASE;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;

/**
 * Created by mentor on 30/10/17.
 */

public class RvPreviewCartAdapter extends RecyclerView.Adapter<RvPreviewCartAdapter.RvPreviewCartHolder> {

    private ArrayList<CartItem> cartItems;
    private Context context;
    String CURRENCY="";
    SessionValue sessionValue;

    public RvPreviewCartAdapter(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public RvPreviewCartHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_cart, parent, false);
        this.context=parent.getContext();

        sessionValue = new SessionValue(parent.getContext());

        try {
            CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);
            Log.e("Currency", "" + CURRENCY);

        } catch (Exception e) {
            Log.e("Session Error", "" + e.getMessage());
        }

        return new RvPreviewCartHolder(view);
    }

    @Override
    public void onBindViewHolder(RvPreviewCartHolder holder, int position) {


        final CartItem cartItem = cartItems.get(position);

        int s = position + 1;

        holder.tvCode.setText(cartItem.getProductCode());

        holder.tvSlNo.setText(String.valueOf(s));
        holder.tvProductName.setText(cartItem.getProductName());

          String qty=""+cartItem.getTypeQuantity();  //"0/"
        Log.e("netpr hr",""+cartItem.getNetPrice());
//        double netPrice = cartItem.getNetPrice();
        double netPrice = 0;
        if(cartItem.getTax_type().equals("TAX_INCLUSIVE")) {
           //  netPrice = cartItem.getProductTotal()/cartItem.getPieceQuantity_nw();
            netPrice = cartItem.getProductPrice();
        }
        else{
            netPrice = cartItem.getProductPrice()-cartItem.getProductDiscount();

        }
        Log.e("getProductPrice hr",""+cartItem.getProductPrice());
        try {
            if (cartItem.getOrderTypeName().equals(PRODUCT_UNIT_CASE)) {
                netPrice = netPrice * cartItem.getPiecepercart();
//                qty = cartItem.getTypeQuantity() + "/0";
                qty = cartItem.getTypeQuantity() + "";
            }

        }catch (Exception e){

        }

        holder.tvQty.setText(qty);

        holder.tvUnitPrice.setText(String.valueOf(getAmount(netPrice)+" "+CURRENCY));

      //  holder.tvTotal.setText(String.valueOf(getAmount(cartItem.getNetPrice()*cartItem.getPieceQuantity())+" "+context.getString(R.string.sr)));

        holder.tvTotal.setText(String.valueOf(getAmount(netPrice*cartItem.getTypeQuantity())+" "+CURRENCY));

      //  holder.tvTotal.setText(String.valueOf(getAmount(cartItem.getTotalPrice())+" "+context.getString(R.string.sr)));

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    public class RvPreviewCartHolder extends RecyclerView.ViewHolder {

        TextView tvSlNo, tvProductName, tvQty, tvUnitPrice, tvTotal,tvCode;
        public RvPreviewCartHolder(View itemView) {
            super(itemView);

            tvSlNo = (TextView) itemView.findViewById(R.id.textView_item_previewCart_no);
            tvProductName = (TextView) itemView.findViewById(R.id.textView_item_previewCart_productName);
            tvQty = (TextView) itemView.findViewById(R.id.textView_item_previewCart_qty);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.textView_item_previewCart_unitPrice);
            tvTotal = (TextView) itemView.findViewById(R.id.textView_item_previewCart_total);

            tvCode=(TextView)itemView.findViewById(R.id.textView_item_previewCart_code);

        }
    }
}
