package com.advanced.minhas.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.Dailysummary_Preview;
import com.advanced.minhas.activity.ReportReceipt;
import com.advanced.minhas.activity.ReportReturn;
import com.advanced.minhas.activity.ReportSale;
import com.advanced.minhas.activity.report.InvoicewiseReport;
import com.advanced.minhas.activity.report.ProductwiseReport;
import com.advanced.minhas.activity.report.ReportBonus;
import com.advanced.minhas.activity.report.ReportDailyProductActivity;
import com.advanced.minhas.activity.report.ReportOutstanding;
import com.advanced.minhas.activity.report.Report_Daily;
import com.advanced.minhas.config.ConfigReport;


public class ReportFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    LinearLayout cardView_report_daily,cardView_report_day_summary,cardView_report_day_products;

    CardView card_report_outstanding, card_report_bonus,card_report_sale,
            card_report_return,cardView_report_receipt ,cardView_productwise_report,cardView_invoicewise_report;
    ImageButton ibBack;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        ibBack = (ImageButton)view.findViewById(R.id.imageButton_toolbar_back);

        card_report_outstanding = (CardView)view.findViewById(R.id.cardView_report_outstanding);
        card_report_bonus = (CardView)view.findViewById(R.id.cardView_report_bonus);
        card_report_sale =  (CardView)view.findViewById(R.id.cardView_report_sale);
        card_report_return =  (CardView)view.findViewById(R.id.cardView_report_return);
        cardView_report_receipt = view.findViewById(R.id.cardView_report_receipt);
        cardView_productwise_report = view.findViewById(R.id.cardView_report_productwise);
        cardView_invoicewise_report = view.findViewById(R.id.cardView_report_invoicewise);
        cardView_report_day_products = view.findViewById(R.id.cardView_report_daily_products);
        cardView_report_daily = view.findViewById(R.id.cardView_report_daily);
        cardView_report_day_summary = view.findViewById(R.id.cardView_report_day_summary);


        ibBack.setOnClickListener(this);
        card_report_outstanding.setOnClickListener(this);
        card_report_bonus.setOnClickListener(this);
        card_report_sale.setOnClickListener(this);
        card_report_return.setOnClickListener(this);
        cardView_report_receipt.setOnClickListener(this);
        cardView_productwise_report.setOnClickListener(this);
        cardView_invoicewise_report.setOnClickListener(this);
        cardView_report_day_products.setOnClickListener(this);
        cardView_report_daily.setOnClickListener(this);
        cardView_report_day_summary.setOnClickListener(this);


        setEnabledReports();

        return view;
    }


    private void setEnabledReports(){
        if(!ConfigReport.DAY_REPORT_ENABLED)
            cardView_report_daily.setVisibility(View.GONE);
        if(!ConfigReport.DAY_SUMMARY_REPORT_ENABLED)
            cardView_report_day_summary.setVisibility(View.GONE);
        if(!ConfigReport.DAYILY_PRODUCT_REPORT_ENABLED)
            cardView_report_day_products.setVisibility(View.GONE);


    }

/*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imageButton_toolbar_back:
                getActivity().onBackPressed();

                break;

            case R.id.cardView_report_outstanding:

                getActivity().startActivity(new Intent(getActivity(), ReportOutstanding.class));

                break;

            case R.id.cardView_report_bonus:

                getActivity().startActivity(new Intent(getActivity(), ReportBonus.class));

                break;
            case R.id.cardView_report_sale:
                getActivity().startActivity(new Intent(getActivity(), ReportSale.class));


                break;

            case  R.id.cardView_report_return:
                getActivity().startActivity(new Intent(getActivity(), ReportReturn.class));


                break;

            case R.id.cardView_report_receipt:
                getActivity().startActivity(new Intent(getActivity(), ReportReceipt.class));

                break;

            case R.id.cardView_report_productwise:

                getActivity().startActivity(new Intent(getActivity(), ProductwiseReport.class));

                break;

            case R.id.cardView_report_invoicewise:

                getActivity().startActivity(new Intent(getActivity(), InvoicewiseReport.class));

                break;
            case R.id.cardView_report_daily_products:
                getActivity().startActivity(new Intent(getActivity(), ReportDailyProductActivity.class));

                break;
            case R.id.cardView_report_daily:
                getActivity().startActivity(new Intent(getActivity(), Report_Daily.class));

                break;
            case R.id.cardView_report_day_summary:
                getActivity().startActivity(new Intent(getActivity(), Dailysummary_Preview.class));

                break;
        }


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
