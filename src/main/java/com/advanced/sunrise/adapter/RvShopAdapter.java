package com.advanced.minhas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.ShopClickListener;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionValue;

import java.util.ArrayList;

import static com.advanced.minhas.config.Generic.getAmount;


public class RvShopAdapter extends RecyclerView.Adapter<RvShopAdapter.ViewHolder>implements Filterable {

    private final ArrayList<Shop> shops;

    String TAG="RvShopAdapter";
    private ArrayList<Shop> filteredList;
    private SessionValue sessionValue;

    ShopClickListener listener;

    private Context context;
    public RvShopAdapter(ArrayList<Shop> shops) {
        this.shops=shops;
        this.filteredList = shops;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        this.sessionValue = new SessionValue(context);
        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.shop_item, parent, false);

        listener=(ShopClickListener)parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Shop shop= filteredList.get(position);
        holder.tvShopName.setText(shop.getShopName());
        holder.tvAddress.setText(shop.getShopAddress());
        holder.tvCode.setText(shop.getShopCode());


        holder.tvBalance.setText(getAmount(shop.getOutStandingBalance()));
        holder.tv_shopvatno.setText(shop.getVatNumber());

       // holder.viewIndicate.setBackgroundColor(shop.isVisit() ?context.getResources().getColor(R.color.colorGreen) : context.getResources().getColor(R.color.colorRed));

        holder.viewIndicate.setImageDrawable(shop.isVisit() ?context.getResources().getDrawable(R.drawable.ic_round_green_12dp) : context.getResources().getDrawable(R.drawable.ic_round_red_12dp));

        holder.tvShopName.setTextColor(shop.isVisit() ?context.getResources().getColor(R.color.colorGreen) : context.getResources().getColor(R.color.colorRed));



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (sessionValue.isGroupRegister()) {
                   sessionValue.storeShopid( ""+shop.getShopId());
                   if (listener!=null)
                       listener.onShopClick(shop);
                   Log.e("shopPhone",shop.getShopMobile());


                       /*
                   // Let's first dynamically add a fragment into a frame container
                   ShopDashBoardFragment productDetailsFragment = new ShopDashBoardFragment().newInstance(shop);

                   AppCompatActivity activity = (AppCompatActivity) v.getContext();
                   FragmentManager fragmentManager = activity.getSupportFragmentManager();
                   FragmentTransaction ft = fragmentManager.beginTransaction();
                   ft.replace(R.id.fragment_home, productDetailsFragment, FRAGMENT_SHOP_DASHBOARD);
                   ft.addToBackStack(null);
                   ft.commit();
                   */


               }else
                   Toast.makeText(context, "Please Register", Toast.LENGTH_SHORT).show();

            }
        });
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

                if (charString.isEmpty()) {

                    filteredList = shops;
                } else {

                    ArrayList<Shop> list = new ArrayList<>();

                    for (Shop s : shops) {

                        if (s.getShopName().toLowerCase().contains(charString) || s.getShopCode().toLowerCase().contains(charString)) {

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
                filteredList = (ArrayList<Shop>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvShopName,tvAddress,tvCode,tvBalance,tv_shopvatno;
     //   public View viewIndicate;
        public ImageView viewIndicate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvShopName=(TextView)itemView.findViewById(R.id.textView_shop_name);
            tvAddress=(TextView)itemView.findViewById(R.id.textView_shop_address);
            tvCode=(TextView)itemView.findViewById(R.id.textView_shop_code);
            tvBalance=(TextView)itemView.findViewById(R.id.textView_shop_balance);
            tv_shopvatno = (TextView)itemView.findViewById(R.id.textView_shop_vatno);

            viewIndicate =(ImageView) itemView.findViewById(R.id.view_shop_status_image);

           // viewIndicate =(View)itemView.findViewById(R.id.view_shop_item_status_indicate);

        }


    }
}
