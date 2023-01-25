package com.advanced.minhas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.advanced.minhas.model.CartItem;

import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * Created by mentor on 25/5/17.
 *
 */

    public class CartCodeAdapter extends ArrayAdapter<CartItem> {
    private final String TAG = "CartCodeAdapter";
    private ArrayList<CartItem> items;
    private ArrayList<CartItem> my_search_items;


    private int viewResourceId;


    public CartCodeAdapter(Context context, int viewResourceId, ArrayList<CartItem> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.viewResourceId = viewResourceId;
        this.my_search_items = items;
        this.items = new ArrayList<CartItem>();
        this.items.addAll(my_search_items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater.from(parent.getContext()));

//            v = inflater.inflate(R.layout.item_textview, null);

//            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(viewResourceId, null);
        }
        CartItem cartItem = my_search_items.get(position);
        if (cartItem != null) {
            TextView textView = (TextView) v;
            textView.setPadding(10, 10, 10, 10);

//            TextView customerNameLabel = (TextView) v.findViewById(R.id.textView_item_textView);
            if (textView != null) {
                textView.setText(cartItem.getProductCode());
            }
        }
        return v;
    }
/*
    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }*/

    /*private Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((CartItem) (resultValue)).getProductCode();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint != null) {
                suggestions.clear();
                for (CartItem cartItem : itemsAll) {
                    if (cartItem.getProductCode().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(cartItem);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                FilterResults filterResults = new FilterResults();
                filterResults.values = itemsAll;
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

           ArrayList<CartItem> filteredList = (ArrayList<CartItem>) results.values;
            if (results.count > 0) {
                clear();
                for (CartItem c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };*/

    public void getFilterItems(String toString) {

        my_search_items.clear();
        String serachstring = toString.toLowerCase();
        if (serachstring.length() == 0) {
            my_search_items.addAll(items);
        } else {
            for (CartItem b : items) {
                if (b.getProductCode().toLowerCase(Locale.getDefault())
                        .startsWith(serachstring)) {
                    my_search_items.add(b);
                }
            }

            /*for (All_items b : arraylist) {
                if ((!(b.getMenuname().toLowerCase(Locale.getDefault())
                        .startsWith(searchString))  && (b.getMenuname().toLowerCase(Locale.getDefault())
                        .contains(searchString)))){
                    my_search_items.add(b);
                }
            }*/
        }
        //notify();
        notifyDataSetChanged();



    }

/*

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {



                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    suggestions = itemsAll;
                } else {

                    ArrayList<CartItem> list = new ArrayList<>();

                    for (CartItem c : itemsAll) {



                        if (c.getProductCode().toLowerCase().startsWith(charString.toLowerCase())) {
                            list.add(c);
                        }
                    }

                    suggestions = list;
                }



                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                suggestions = (ArrayList<CartItem>) filterResults.values;
//                notifyDataSetChanged();


            }
        };
    }
*/
}
