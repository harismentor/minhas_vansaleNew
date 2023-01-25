package com.advanced.minhas.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.model.Ledger;

import java.util.ArrayList;

import static com.advanced.minhas.adapter.SaleReportAdapter.getAmount;

public class RvLedgerAdapter extends RecyclerView.Adapter<RvLedgerAdapter.RvVanStockHolder> implements Filterable {

    private Context context;
    private ArrayList<Ledger> ledger;

    private ArrayList<Ledger> filteredList;

    String TAG="RvVanStockAdapter", cust_id="";

    public RvLedgerAdapter(ArrayList<Ledger> ledger, String cust_id) {
        this.ledger = ledger;
        this.filteredList = ledger;
        this.cust_id = cust_id;
    }

    @Override
    public RvVanStockHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ledger,parent,false);
        return new RvVanStockHolder(view);
    }

    @Override
    public void onBindViewHolder(RvVanStockHolder holder, int position) {

        final Ledger l = filteredList.get(position);
        String new_invoiceAmnt = "";

        /*try {

            String[] datearray = l.getDate().split("-");
            String new_date = datearray[2]+"/"+datearray[1]+"/"+datearray[0];
            holder.tvDate.setText(""+new_date);

        }catch (Exception e){

        }*/

        if (l.getDate().isEmpty()){

            holder.tvinvoiceno.setTypeface(holder.tvinvoiceno.getTypeface(), Typeface.BOLD);
            holder.tvamount.setTypeface(holder.tvamount.getTypeface(), Typeface.BOLD);
            holder.tvreceived.setTypeface(holder.tvreceived.getTypeface(), Typeface.BOLD);
            holder.tvbalance.setTypeface(holder.tvbalance.getTypeface(), Typeface.BOLD);

            holder.tvinvoiceno.setTextSize(15);
            holder.tvamount.setTextSize(15);
            holder.tvreceived.setTextSize(15);
            holder.tvbalance.setTextSize(15);

        }else
        {

            holder.tvinvoiceno.setTypeface(holder.tvinvoiceno.getTypeface(), Typeface.NORMAL);
            holder.tvamount.setTypeface(holder.tvamount.getTypeface(), Typeface.NORMAL);
            holder.tvreceived.setTypeface(holder.tvreceived.getTypeface(), Typeface.NORMAL);
            holder.tvbalance.setTypeface(holder.tvbalance.getTypeface(), Typeface.NORMAL);
        }
        double db_invoice =0;
        new_invoiceAmnt = l.getInvoiceAmount();
//        if (l.getInvoiceAmount().contains(".")){
//            new_invoiceAmnt = l.getInvoiceAmount().substring(0, l.getInvoiceAmount().indexOf(".")); //  l.getInvoiceAmount().split(".")[0];
//        }else {
//            new_invoiceAmnt = l.getInvoiceAmount();
//        }
        if(new_invoiceAmnt.length()>0){
            db_invoice =Double.parseDouble(new_invoiceAmnt);
        }


        holder.tvDate.setText(l.getDate());
        holder.tvinvoiceno.setText(l.getInvoiceNo());
        holder.tvamount.setText(getAmount(db_invoice));
        holder.tvreceived.setText(l.getReceived());
        holder.tvbalance.setText(l.getBalance());


        /*holder.layout_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Toast.makeText(context, "customer: "+cust_id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, LedgerDetailsView.class);
                intent.putExtra("voucher", l);
                intent.putExtra("custID", cust_id);
                context.startActivity(intent);

            }
        });*/

    }

    /*@Override
    public int getItemCount() {
        return (null != products ? products.size() : 0);
    }*/

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

                    filteredList = ledger;
                } else {

                    ArrayList<Ledger> list = new ArrayList<>();

                    for (Ledger s : ledger) {

                       /* if (s.getProductName().toLowerCase().contains(charString) || s.getProductCode().toLowerCase().contains(charString)) {

                            list.add(s);
                        }*/
                    }

                    filteredList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<Ledger>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }



    class RvVanStockHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvinvoiceno, tvamount, tvreceived, tvbalance;
        LinearLayout layout_main;

        private RvVanStockHolder(View itemView) {
            super(itemView);

            layout_main = (LinearLayout)itemView.findViewById(R.id.layout_main);

            tvDate= (TextView) itemView.findViewById(R.id.txt_date);
            tvinvoiceno= (TextView) itemView.findViewById(R.id.text_invoiceno);
            tvamount= (TextView) itemView.findViewById(R.id.text_amount);
            tvreceived= (TextView) itemView.findViewById(R.id.textreceived);
            tvbalance= (TextView) itemView.findViewById(R.id.text_balance);

        }
    }
}