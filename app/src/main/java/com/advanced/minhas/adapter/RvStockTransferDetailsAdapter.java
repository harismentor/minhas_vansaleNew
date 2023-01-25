package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.StocktransferDetails;
import com.advanced.minhas.session.SessionValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



/**
 * Created by Hk on 16/04/2020.
 */

public class RvStockTransferDetailsAdapter extends RecyclerView.Adapter<RvStockTransferDetailsAdapter.RvVanStockHolder>{

    private Context context;
    private MyDatabase myDatabase;
    private ArrayList<StocktransferDetails> filteredList;
    SessionValue sessionValue;

    String TAG="RvVanStockAdapter";

    public RvStockTransferDetailsAdapter(ArrayList<StocktransferDetails> products) {
        this.filteredList = products;
    }

    @Override
    public RvVanStockHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        myDatabase=new MyDatabase(context);
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_approval_view,parent,false);

        return new RvVanStockHolder(view);
    }

    @Override
    public void onBindViewHolder(RvVanStockHolder holder, int position) {

        final StocktransferDetails p=filteredList.get(position);


        final int s = position + 1;
        int available_qty=0;
        int bal_qty = 0;
        String name ="";
        String str_unitlist = myDatabase.getUnitbyproduct(p.getProductid());
        Log.e("str_unitlist",str_unitlist);
        try {
            JSONArray arr = new JSONArray(str_unitlist);


            for (int i = 0; i < arr.length(); i++) {

                JSONObject jObj = arr.getJSONObject(i);

                String id = jObj.getString("unitId");

                if(id.equals(p.getProductunitid())) {
//                    String price = jObj.getString("unitPrice");
                    String confctr = jObj.getString("con_factor");
                    name = jObj.getString("unitName");
                    Log.e("Unit selected", "" + confctr);
                    available_qty = Integer.parseInt(p.getQuantity())/Integer.parseInt(confctr);
                    bal_qty = Integer.parseInt(p.getQuantity())%Integer.parseInt(confctr);
                    Log.e("available_qtyhr", "" +available_qty );



                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.tvSlNo.setText(""+s);
        holder.tvName.setText(""+p.getProductname());
        //holder.tvQty.setText(""+p.getQuantity());
        holder.tvQty.setText(""+available_qty +" - "+ bal_qty +" (" +name+ ")");
    }


    @Override
    public int getItemCount() {
        return (null != filteredList ? filteredList.size() : 0);

    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }


    class RvVanStockHolder extends RecyclerView.ViewHolder {

        TextView tvSlNo, tvName, tvQty;


        private RvVanStockHolder(View itemView) {
            super(itemView);

            tvSlNo = (TextView) itemView.findViewById(R.id.textView_item_stock_serialNo);
            tvName = (TextView) itemView.findViewById(R.id.textView_item_stock_name);
            tvQty = (TextView) itemView.findViewById(R.id.textView_item_stock_qty);



        }
    }
}
