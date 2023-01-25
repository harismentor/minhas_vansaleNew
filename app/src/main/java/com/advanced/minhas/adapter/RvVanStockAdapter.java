package com.advanced.minhas.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.advanced.minhas.R;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Product;
import com.advanced.minhas.model.Size;
import com.advanced.minhas.session.SessionValue;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;


/**
 * Created by mentor on 26/10/17.
 */

public class RvVanStockAdapter extends RecyclerView.Adapter<RvVanStockAdapter.RvVanStockHolder> implements Filterable {

    private Context context;
    private ArrayList<Product> products;

    private ArrayList<Product> filteredList;
    SessionValue sessionValue;
    private Stock_size_adapter sizeadapter;

    final ArrayList<Size> array_sizefull=new ArrayList<>();
    final ArrayList<CartItem> array_sizelist=new ArrayList<>();
    RecyclerView rec_list;
    String st_sizelist ="",st_prodname="",st_prodId ="";

    int int_totalqty=0;
    TextView tv_totalqty,tv_productid,tv_productname;
    Dialog dialog;
    private MyDatabase myDatabase;
    String TAG="RvVanStockAdapter", CURRENCY="";
    String confctr ="";

    public RvVanStockAdapter(ArrayList<Product> products) {
        Log.e("products sze",""+products.size());

        this.products = products;
        this.filteredList = products;

    }

    @Override
    public RvVanStockHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_van_stock,parent,false);
        myDatabase=new MyDatabase(context);
        sessionValue = new SessionValue(parent.getContext());

        try {
            CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);
            Log.e("Currency", "" + CURRENCY);

        } catch (Exception e) {
            Log.e("Session Error", "" + e.getMessage());
        }
        return new RvVanStockHolder(view);
    }

    @Override
    public void onBindViewHolder(RvVanStockHolder holder, int position) {

        final Product p=filteredList.get(position);
        int available_qty =0,bal_qty=0;
        int_totalqty=0;
        String name ="";
        String wholesale_unitprice = "";


        try {
            JSONArray arr = new JSONArray(p.getUnitslist());


            for (int i = 0; i < arr.length(); i++) {

                JSONObject jObj = arr.getJSONObject(i);

                String id = jObj.getString("unitId");

                if(id.equals(""+p.getProduct_reporting_Unit())) {
//                    String price = jObj.getString("unitPrice");
                    confctr = jObj.getString("con_factor");
                    name = jObj.getString("unitName");
                    Log.e("Unit selected", "" + confctr);
                    available_qty = p.getStockQuantity()/Integer.parseInt(confctr);
                    bal_qty = p.getStockQuantity()%Integer.parseInt(confctr);
                    wholesale_unitprice = jObj.getString("unitWholesalePrice");

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.tvProductUnit.setText(String.valueOf(""+available_qty +"-"+bal_qty)+"(" +name+ ")");
        //  Product p=products.get(position);

        int s = position + 1;
        //  holder.tvSlNo.setText(String.valueOf(s));

        holder.tvProductType.setText(""+String.valueOf(s)+" ."+p.getProductType());
        // holder.tvProductCode.setText(p.getProductCode());
        holder.tvProductName.setText(p.getProductName());

       // holder.tvRetailPrice.setText(String.valueOf(getAmount(p.getProduct_reporting_Price())+" "+ CURRENCY));
        double wholesaleprice = TextUtils.isEmpty(wholesale_unitprice) ? 0 : Double.valueOf(wholesale_unitprice);

        holder.tvRetailPrice.setText(String.valueOf(getAmount(wholesaleprice)+" "+ CURRENCY));

        int bo=0,bal=0;
        if (p.getPiecepercart()>1&& p.getPiecepercart()<=p.getStockQuantity()) {
            bo = p.getStockQuantity() / p.getPiecepercart();
        }

        bal = p.getStockQuantity() -(bo*p.getPiecepercart());

        holder.tvQuantity.setText(String.valueOf(bal));
        //holder.tvQuantity.setText(String.valueOf(available_qty));
        Log.e("available_qty hr", "" + available_qty);
        p.setProduct_qntybyconfactor(available_qty);

        // int_totalqty= (int) (int_totalqty+p.getProduct_reporting_Price());
        Log.e("tot hr", "" + int_totalqty);
//        Log.e("available_qty", "" + p.getProduct_qntybyconfactor() );
        Log.e("bal_qty", "" + bal_qty );

    }

    private void dialogue_productdetails(String prod_code) {

        st_sizelist="";

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialogue_productdetails);

        android.widget.Button bt_save;


        android.widget.Button bt_close;



        rec_list = dialog.findViewById(R.id.recyclerview);
        tv_productid=dialog.findViewById(R.id.tv_productid);
        tv_productname=dialog.findViewById(R.id.tv_productname);


        tv_totalqty=dialog.findViewById(R.id.tv_qty);

        // st_sizelist =myDatabase.getProduct_sizelist(Integer.parseInt(prod_code));
        array_sizelist.clear();
        array_sizelist.addAll(myDatabase.getProduct_sizelist(Integer.parseInt(prod_code)));
        st_sizelist = array_sizelist.get(0).getSizelist();
        st_prodname = array_sizelist.get(0).getProductName();
        st_prodId = array_sizelist.get(0).getProductCode();

        tv_productname.setText(st_prodname);
        tv_productid.setText(st_prodId);

        bt_close = dialog.findViewById(R.id.bt_close);


        dialog.setTitle("Product Details");
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations_SmileWindow;//R.style.DialogAnimations_SmileWindow;
        dialog.setCancelable(false);

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        set_recycler();
        dialog.show();

    }

    private void set_recycler() {




        try {

            rec_list.setHasFixedSize(true);
            rec_list.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context)
                    .showLastDivider()
                    .build());
            rec_list.setLayoutManager(new LinearLayoutManager(context));
            rec_list.setAdapter(sizeadapter);

        }catch (Exception e){

        }



    }


    @Override
    public int getItemCount() {
        return (null != filteredList ? filteredList.size() : 0);

    }


    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                Log.e("charseq",""+charString);

                if (charString.isEmpty()) {
                    Log.e("if 0","ok");
                    filteredList = products;
                } else {

                    ArrayList<Product> list = new ArrayList<>();

                    for (Product s : products) {

                        if (s.getProductName().toUpperCase().contains(charString) || s.getProductCode().toUpperCase().contains(charString)) {
                            Log.e("if 1",""+s.getProductName());
                            list.clear();
                            list.add(s);
                        }
                        if (s.getProductName().toLowerCase().contains(charString) || s.getProductCode().toLowerCase().contains(charString)) {
                            Log.e("if 2",""+s.getProductName());
                            list.clear();
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
                filteredList = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };


    }

    class RvVanStockHolder extends RecyclerView.ViewHolder {

        TextView tvSlNo,tvProductType, tvProductName,tvRetailPrice,tvQuantity,tvProductCode,tvProductUnit;
        CardView cd_list;

        private RvVanStockHolder(View itemView) {
            super(itemView);

            //     tvSlNo= (TextView) itemView.findViewById(R.id.textView_item_vanStock_slNo);
            tvProductType= (TextView) itemView.findViewById(R.id.textView_item_vanStock_productType);
            tvProductCode= (TextView) itemView.findViewById(R.id.textView_item_vanStock_productCode);
//            tvBrand= (TextView) itemView.findViewById(R.id.textView_item_vanStock_productBrand);
            tvProductName=(TextView)  itemView.findViewById(R.id.textView_item_vanStock_productName);
            tvRetailPrice= (TextView) itemView.findViewById(R.id.textView_item_vanStock_RetailPrice);
//            tvWholeSalePrice= (TextView) itemView.findViewById(R.id.textView_item_vanStock_wholeSalePrice);
            tvQuantity=(TextView)  itemView.findViewById(R.id.textView_item_vanStock_totalQty);
            cd_list= itemView.findViewById(R.id.cd_list);
            tvProductUnit = itemView.findViewById(R.id.textView_item_vanStock_productUnit);



        }
    }
    public int getInt_totalqty() {
        Log.e("reached adptr","ok");
        if (!filteredList.isEmpty()) {
            for (Product pr : filteredList) {
                Log.e("reached adptr","ok"+pr.getProduct_qntybyconfactor());
                if (pr.getProduct_qntybyconfactor() != 0.0) {
                    double f = pr.getProduct_qntybyconfactor();
                    int_totalqty += f;
                }
            }
        }
        Log.e("reached adptr","okhr"+int_totalqty);
        return int_totalqty;
    }
}
