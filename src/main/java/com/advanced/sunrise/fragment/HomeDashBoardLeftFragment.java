package com.advanced.minhas.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.AddShopActivity;
import com.advanced.minhas.activity.ContraVoucher;
import com.advanced.minhas.activity.Expense_Entry;
import com.advanced.minhas.activity.LedgerView;
import com.advanced.minhas.activity.OrderPlacing;
import com.advanced.minhas.activity.OtherCustomerActivity;
import com.advanced.minhas.activity.Stock_Transfer_Online;
import com.advanced.minhas.activity.VanStockActivity;
import com.advanced.minhas.activity.VanToWarehouse;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.rey.material.widget.Button;

import static com.advanced.minhas.config.ConfigKey.PRIVATE_VERSION_CODE;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;

public class HomeDashBoardLeftFragment extends Fragment implements View.OnClickListener {

    private Button btnSearchCustomer, btnNewShop, btnExpenseEntry, btnVanStock, btnReport,
            btnContraVoucher, button_ledger, button_stocktransfer, btnorderplace;
    private TextView tvVanStock, tvTodaySale, tvCollection, tvSaleTarget, tvDailyBonus, tv_version, tvMonthlyBonus, button_vantowarehouse;

    private SessionValue sessionValue;
    private SessionAuth sessionAuth;

    private MyDatabase myDatabase;

    private String TAG = "HomeDashBoardLeftFragment";

    public HomeDashBoardLeftFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeDashBoardLeftFragment.
     */

    public static HomeDashBoardLeftFragment newInstance(String param1, String param2) {
        HomeDashBoardLeftFragment fragment = new HomeDashBoardLeftFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_dash_board_left, container, false);

        btnSearchCustomer = (Button) view.findViewById(R.id.button_search_customer);
        btnNewShop = (Button) view.findViewById(R.id.button_createShop);
        btnVanStock = (Button) view.findViewById(R.id.button_vanStock);
        btnReport = (Button) view.findViewById(R.id.button_reports);
        btnorderplace = (Button) view.findViewById(R.id.button_order_placing);
        btnExpenseEntry = (Button) view.findViewById(R.id.button_expense);
        btnContraVoucher = (Button) view.findViewById(R.id.button_contra);
        button_ledger = (Button) view.findViewById(R.id.button_ledger);
        button_stocktransfer = view.findViewById(R.id.button_Stocktransfer);
        tv_version = (TextView) view.findViewById(R.id.textView_version);
        tvVanStock = (TextView) view.findViewById(R.id.textView_stock_dash);
        tvTodaySale = (TextView) view.findViewById(R.id.textView_todaySale_dash);
        tvCollection = (TextView) view.findViewById(R.id.textView_collection_dash);
        tvSaleTarget = (TextView) view.findViewById(R.id.textView_target_dash);
        tvDailyBonus = (TextView) view.findViewById(R.id.textView_target_dailybonus);
        tvMonthlyBonus = (TextView) view.findViewById(R.id.textView_monthlybonus);
        button_vantowarehouse = (Button) view.findViewById(R.id.button_vantowarehouse);

        sessionValue = new SessionValue(getContext());
        sessionAuth = new SessionAuth(getContext());

        myDatabase = new MyDatabase(getContext());

        btnSearchCustomer.setOnClickListener(this);
        btnNewShop.setOnClickListener(this);
        btnVanStock.setOnClickListener(this);
        btnReport.setOnClickListener(this);
        btnExpenseEntry.setOnClickListener(this);
        btnContraVoucher.setOnClickListener(this);
        button_vantowarehouse.setOnClickListener(this);
        button_ledger.setOnClickListener(this);
        button_stocktransfer.setOnClickListener(this);
        btnorderplace.setOnClickListener(this);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (sessionValue != null && myDatabase != null) {
            tvSaleTarget.setText(getAmount(sessionValue.getSaleTarget()));
            tvVanStock.setText(getAmount(myDatabase.getStockAmount()));

            printLog("van stock", "" + getAmount(myDatabase.getStockAmount()));

            tvTodaySale.setText(getAmount(myDatabase.getTodaySaleAmount()));
            tvCollection.setText(getAmount(myDatabase.getCollectionAmount() + myDatabase.getSalePaidAmount()));  // cash sales + received invoice amount + receivable opening balance amount
            tvDailyBonus.setText("" + sessionAuth.getBonus());
            float monthbonus = sessionAuth.getBonus() + sessionAuth.getMonthlyBonus();
            tvMonthlyBonus.setText("" + monthbonus);
            tv_version.setText("" + PRIVATE_VERSION_CODE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_order_placing:

                // if (new SessionValue(getContext()).isGroupRegister()) {
                startActivity(new Intent(getActivity(), OrderPlacing.class));
              /*  } else
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();*/

                break;

            case R.id.button_search_customer:

                if (new SessionValue(getContext()).isGroupRegister())
                    startActivity(new Intent(getContext(), OtherCustomerActivity.class));
                else
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();

                break;

            case R.id.button_createShop:
                if (new SessionValue(getContext()).isGroupRegister())
                    startActivity(new Intent(getActivity(), AddShopActivity.class));
                else
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();

                break;
            case R.id.button_vanStock:
                if (new SessionValue(getContext()).isGroupRegister())
                    startActivity(new Intent(getActivity(), VanStockActivity.class));
                else
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_expense:

//                startActivity(new Intent(getActivity(), ExpenseActivity.class));
                startActivity(new Intent(getActivity(), Expense_Entry.class));
                break;

            case R.id.button_contra:
                if (new SessionValue(getContext()).isGroupRegister())
                    startActivity(new Intent(getActivity(), ContraVoucher.class));
                else
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_ledger:
                if (new SessionValue(getContext()).isGroupRegister())
                    startActivity(new Intent(getActivity(), LedgerView.class));
                else
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_vantowarehouse:

                if (new SessionValue(getContext()).isGroupRegister()) {

                    startActivity(new Intent(getActivity(), VanToWarehouse.class));

                } else {
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_reports:
                if (new SessionValue(getContext()).isGroupRegister()) {
                    ReportFragment reportFragment = new ReportFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_home, reportFragment); // give your fragment container id in first parameter
                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.button_Stocktransfer:
//                if (new SessionValue(getContext()).isGroupRegister()) {
//                    startActivity(new Intent(getActivity(), StockTransfer.class));
//                }else {
//                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();
//                }

                //  startActivity(new Intent(getActivity(), StockTransfer.class));
                if (new SessionValue(getContext()).isGroupRegister()) {
                    startActivity(new Intent(getActivity(), Stock_Transfer_Online.class));
                } else {
                    Toast.makeText(getActivity(), "Please Register", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }
}
