package com.advanced.minhas.adapter;

import static com.advanced.minhas.activity.BillwiseReceipt.tv_balance;
import static com.advanced.minhas.activity.BillwiseReceipt.tv_totalamnt;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.dialog.BillwiseReceiptDialogue;
import com.advanced.minhas.listener.BillClicklistner;
import com.advanced.minhas.listener.OnBillwisedetailslistner;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.BillwiseReceiptMdl;
import com.advanced.minhas.session.SessionValue;


import java.util.ArrayList;

public class RvBillwiseAdapter extends RecyclerView.Adapter<RvBillwiseAdapter.ViewHolder>implements Filterable {

    private final ArrayList<BillwiseReceiptMdl> receipts;
    float fl_final_bal=0;
    double dbl_tot_amnt=0;

    String TAG="RvShopAdapter";
    private ArrayList<BillwiseReceiptMdl> filteredList;
    private ArrayList<BillwiseReceiptMdl> bill_list = new ArrayList<>();
    private SessionValue sessionValue;
    private MyDatabase myDatabase;
    BillClicklistner listener;
    double amount_entered =0;
    int customer_id=0;
    String return_no ="";

    private Context context;
    public RvBillwiseAdapter(ArrayList<BillwiseReceiptMdl> recpts, double tot_amnt, int custid,String return_no) {
        this.receipts=recpts;
        this.filteredList = recpts;
        this.dbl_tot_amnt = tot_amnt;
        this.customer_id = custid;
        this.return_no = return_no;
        Log.e("recpt sze",""+receipts.size());
        Log.e("tot_amnt",""+tot_amnt);
        Log.e("return_no",""+return_no);

      //  notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        this.sessionValue = new SessionValue(context);
        myDatabase = new MyDatabase(context);
        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.billwisereceipt_items, parent, false);

       // listener=(BillClicklistner)parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final BillwiseReceiptMdl receipts= filteredList.get(position);
        holder.tv_invoiceno.setText(receipts.getInvoiceno());
        holder.tv_invoicedate.setText(receipts.getInvoicedate());
        holder.tv_invoicebal.setText(""+receipts.getInvoicebalance());
        holder.tv_finalbal.setText(""+myDatabase.getfinal_balanceby_invoiceno(receipts.getInvoiceno()));


        holder.tv_billamnt.setText(""+receipts.getBillamount());
        holder.tv_amountentered.setText(""+myDatabase.get_billamntby_invoiceno(receipts.getInvoiceno()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final BillwiseReceiptDialogue billdialogue = new BillwiseReceiptDialogue(context,dbl_tot_amnt,receipts.getInvoice_amnt(),receipts.getInvoiceno(),return_no, new OnBillwisedetailslistner() {
                    @Override
                    public void onEnterbilldetails(float amount, String remarks) {
                        Log.e("amnt",""+amount);
                        Log.e("rmrks",""+remarks);
                        Log.e("invcno",""+receipts.getInvoiceno());
                        amount_entered = amount;
                        holder.tv_amountentered.setText(""+amount);
                        String bal = receipts.getInvoice_amnt();
                         fl_final_bal = Float.parseFloat(bal)-Float.parseFloat(String.valueOf(amount));

                        notifyDataSetChanged();
                        double dbl_billamnt_total = myDatabase.get_billamount_total();
                        double dbl_tot_amnt = TextUtils.isEmpty(tv_totalamnt.getText().toString()) ? 0 : Double.valueOf(tv_totalamnt.getText().toString());
                        double total = dbl_tot_amnt - dbl_billamnt_total;
                        tv_balance.setText(""+total);
                    }


                });

                billdialogue.show();
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

                    filteredList = receipts;
                } else {

                    ArrayList<BillwiseReceiptMdl> list = new ArrayList<>();

                    for (BillwiseReceiptMdl s : receipts) {

                        if (s.getInvoiceno().toLowerCase().contains(charString) || s.getInvoiceno().toLowerCase().contains(charString)) {

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
                filteredList = (ArrayList<BillwiseReceiptMdl>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_invoiceno,tv_invoicedate,tv_invoicebal,tv_billamnt,tv_finalbal,tv_amountentered;
        EditText edt_totalamnt;
        //   public View viewIndicate;
        public ImageView viewIndicate;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_invoiceno=(TextView)itemView.findViewById(R.id.textView_invoiceno);
            tv_invoicedate=(TextView)itemView.findViewById(R.id.textView_invoicedate);
            tv_invoicebal=(TextView)itemView.findViewById(R.id.textView_invoicebalance);
            tv_billamnt=(TextView)itemView.findViewById(R.id.textView_invoice_billamnt);
            tv_finalbal =itemView.findViewById(R.id.textView_invoicefinalbalance);
            tv_amountentered = itemView.findViewById(R.id.textView_amountentered);


            viewIndicate =(ImageView) itemView.findViewById(R.id.view_shop_status_image);

            // viewIndicate =(View)itemView.findViewById(R.id.view_shop_item_status_indicate);

        }


    }
}
