package com.advanced.minhas.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.advanced.minhas.R;
import com.advanced.minhas.model.Expense;
import com.advanced.minhas.textwatcher.TextValidator;

import java.util.ArrayList;

public class RvExpenseAdapter extends RecyclerView.Adapter<RvExpenseAdapter.RvExpenseHolderAdapter> {

    private Context context;
    private ArrayList<Expense> array_expense;

    private ArrayList<Expense> expenseList;

    public RvExpenseAdapter(Context mContext, ArrayList<Expense> expense) {

        this.context = mContext;
        this.array_expense = expense;

        /*for (int i = 0; i<array_expense.size(); i++){
            Expense exp = array_expense.get(0);
            Log.e("exp" , "/"+exp.getName());

        }*/

    }

    public float total_expense(){

        float nettotal = 0.0f;

        if (!expenseList.isEmpty()){

            for (Expense ep : expenseList){

                float f = ep.getAmount();

                nettotal += f;


            }
        }

        return nettotal;

    }

    public ArrayList<Expense> getExpenseList() {

        expenseList = new ArrayList<Expense>();

        if (!array_expense.isEmpty()){
           for (Expense e : array_expense){

               if (e.getAmount()!=0.0f) {
                   expenseList.add(e);

                  /* Log.e("Rec No entered", ""+e.getName()+"/"+e.getReceiptNo());
                   Log.e("Amount entered", ""+e.getName()+"/"+e.getAmount());
                   Log.e("Remark entered", ""+e.getName()+"/"+e.getRemarks());*/

               }
           }
        }

        return expenseList;
    }

    @Override
    public RvExpenseHolderAdapter onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);

        return new RvExpenseHolderAdapter(view);
    }

    @Override
    public void onBindViewHolder(final RvExpenseHolderAdapter holder, final int position) {

            final Expense expense = array_expense.get(position);

            holder.tv_expense_name.setText(""+expense.getName());

            holder.etAmount.addTextChangedListener(new TextValidator(holder.etAmount) {
                @Override
                public void validate(TextView textView, String text) {


                    if (!TextUtils.isEmpty(text)) {
                        float amount = Float.parseFloat(text);
                        expense.setAmount(amount);

                        Log.e("entered amount", ""+amount);
                    }
                    else {

                        Log.e("entered amount else", "0.00");
                        expense.setAmount(0.0f);
                    }

                    array_expense.set(position, expense);
                }
            });

            holder.etReceiptNumber.addTextChangedListener(new TextValidator(holder.etReceiptNumber) {
                @Override
                public void validate(TextView textView, String text) {

                        String receiptNo = ""+text;
                        expense.setReceiptNo(receiptNo);

                        Log.e("entered rec no", ""+receiptNo);

                        array_expense.set(position, expense);
                }
            });

            holder.etRemark.addTextChangedListener(new TextValidator(holder.etRemark) {
                @Override
                public void validate(TextView textView, String text) {

                        String remarks = ""+text;
                        expense.setRemarks(remarks);

                        Log.e("entered remarks no", ""+remarks);

                        array_expense.set(position, expense);
                }
            });
    }

    @Override
    public int getItemCount() {
        return array_expense.size();
    }

    class RvExpenseHolderAdapter extends RecyclerView.ViewHolder {

        TextView tv_expense_name;
        EditText etAmount, etReceiptNumber, etRemark;

        RvExpenseHolderAdapter(View itemView) {
            super(itemView);

            tv_expense_name = itemView.findViewById(R.id.textView_expenseType);

            etAmount = itemView.findViewById(R.id.editText_expense_amount);
            etReceiptNumber = itemView.findViewById(R.id.editText_expense_receiptNo);
            etRemark = itemView.findViewById(R.id.editText_expense_remark);

        }
    }
}
