package com.advanced.minhas.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.advanced.minhas.config.AmountCalculator.getBonusAmount;
import static com.advanced.minhas.config.AmountCalculator.getSalePrice;
import static com.advanced.minhas.config.AmountCalculator.getTaxPrice;
import static com.advanced.minhas.config.AmountCalculator.getWithoutTaxPrice;
import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_RETURN_TYPE;
import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigSales.IS_GST_ENABLED;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.FRAGMENT_WITOUTINVOICE;
import static com.advanced.minhas.config.ConfigValue.INVOICE_RETURN_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.PRODUCT_UNIT_CASE;
import static com.advanced.minhas.config.ConfigValue.SALE_WHOLESALE;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dbDateFormat;
import static com.advanced.minhas.config.Generic.generateNewNumber;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getAmountNew;
import static com.advanced.minhas.config.Generic.getRoundOfAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_PRODUCT_TYPE;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_LATITUDE;
import static com.advanced.minhas.session.SessionValue.PREF_LONGITUDE;
import static com.advanced.minhas.session.SessionValue.PREF_VATPERCENT;
import static com.advanced.minhas.webservice.WebService.webReturn;
import static com.advanced.minhas.webservice.WebService.webSaleReturn;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.BillwiseReceipt;
import com.advanced.minhas.activity.ReturnPreviewActivity;
import com.advanced.minhas.adapter.Return_SizeAndQuantityAdapter;
import com.advanced.minhas.adapter.RvReturnWithoutInvoiceAdapter;
import com.advanced.minhas.dialog.CartSpinnerDialog;
import com.advanced.minhas.dialog.OnSpinerItemClick;
import com.advanced.minhas.dialog.PaymentTypeDialog;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Size;
import com.advanced.minhas.model.Size_Return;
import com.advanced.minhas.model.Taxes;
import com.advanced.minhas.model.Transaction;
import com.advanced.minhas.model.Units;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.textwatcher.TextValidator;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.google.gson.Gson;
import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class WithoutInvoiceFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String st_sizelist = "", st_each_size_qty = "", st_totaldisctype = "", st_size_string = "", st_taxlist = "", st_hsn_code = "", VATPERCENTAGE = "",
            st_productcode = "", st_disc = "0";
    double db_net_total = 0, db_discount = 0, db_disc_total = 0, db_vat_amount = 0, db_grand_total = 0,
            disc_value = 0 , price_pcs=0 ,price_kg = 0; // db_vat = 0,  db_withtax_total=0,
    private double paid_amount = 0, dbl_tax_value = 0, dbl_tax_amount = 0, dbl_withouttax_total = 0, dbl_withtax_total = 0,
            disc_perentge = 0;
    String st_tax = "", st_cgst = "", st_sgst = "";
    private String PAYMENT_TYPE = "" ,st_paytype ="";
    int discnt_typeflag = 0;
    private ArrayList<String> sizeitems_continous = new ArrayList<>();
    private ArrayList<String> sizeitems_selected_array = new ArrayList<>();
    private ArrayList<Size_Return> array_sizeanqty_aftersale = new ArrayList<>();
    private ArrayList<CartItem> array_sizelist = new ArrayList<>();
    private ArrayList<Size_Return> array_sizestock = new ArrayList<>();
    private ArrayList<Taxes> array_tax = new ArrayList<>();
    ArrayList<String> size_finalarray = new ArrayList<>();
    ArrayList<Size_Return> arr_size_return_full = new ArrayList<>();
    private ArrayList<String> array_size = new ArrayList<>();
    private ArrayList<String> array_sizeqnty = new ArrayList<>();
    private ArrayList<String> array_totaldisc_tye = new ArrayList<>();

    public static final String ARG_CUSTOMER_NAME = "customer_key_arg";
    private static final String ARG_SALETYPE = "saleType";
    String TAG = "WithoutInvoiceFragment";
    private Shop SELECTED_SHOP = null;
    private String RETURN_TYPE = null;
    private TextView tvProductSpinner, tvCodeSpinner, tvTotalRefund, tvNetTotal, tvVatcgst,tvVatsgst,
            tv_totalqty, textView_sales_size, etReturnQuantity, tvQtyTotal, textView_withoutdiscount,tvVat;
    RecyclerView rec_list;

    LinearLayout ly_taxindia, ly_taxoutside;
    TextView textView_sales_tax;
    private ProgressBar progressBar;
    private ErrorView errorView;
    private SessionValue sessionValue;
    EditText edittext_discount, etProductDiscount, etUnitPrice;
    private AppCompatSpinner spinnerCartUnit, sp_totaldisc;

    private Button btnFinish, btnAddCart ,btnMakePayment;
    Button plusbutton;

    private SessionAuth sessionAuth;

    private RecyclerView recyclerView;

    private ViewGroup viewLayout;

    private RvReturnWithoutInvoiceAdapter withoutInvoiceAdapter;
    private Return_SizeAndQuantityAdapter sizeadapter;
    private ArrayList<CartItem> cartItems = new ArrayList<>();
    private SwitchCompat switchreceipt_type;
    private ArrayList<CartItem> cartItemscopy = new ArrayList<>();
    private ArrayList<String> array_unitname = new ArrayList<>();
    private ArrayList<String> array_unitid = new ArrayList<>();
    private ArrayList<String> array_unitprice = new ArrayList<>();
    private ArrayList<String> array_unitconfactor = new ArrayList<>();
    ArrayList<Sales> returnList = new ArrayList<>();
    String unit_id = "", unit_name = "",
            unit_confctr = " ", unit_price = " ";
    private CartItem SELECTED_CART = null;
    private float pref_bonus = 0, cash_inhand = 0;
    int int_product_id = 0, prod_id_dialogue = 0;

    private String EXECUTIVE_ID = "", dayRegId = "", CURRENCY = "";
    private SwitchCompat switch_tax_type;
    private MyDatabase myDatabase;
    int addflag = 0, dialogflag = 0;
    double less_discount = 0;
    int SAVE_STATUS = 0;
    Dialog dialog;
    Sales salesReturn = new Sales();
    String str_Latitude = "0", str_Longitude = "0", latitude_session = "", longitude_session = "";
    private String provider;
    LocationManager locationManager;

    // TODO: Rename and change types and number of parameters
    public static WithoutInvoiceFragment newInstance(Shop cus, String type) {
        WithoutInvoiceFragment fragment = new WithoutInvoiceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CUSTOMER_NAME, cus);
        args.putString(ARG_SALETYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {

                SELECTED_SHOP = (Shop) getArguments().getSerializable(ARG_CUSTOMER_NAME);
                RETURN_TYPE = getArguments().getString(ARG_SALETYPE);
            } catch (ClassCastException e) {
                e.getMessage();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_without_invoice, container, false);
        dialog = new Dialog(getActivity());
        tvProductSpinner = (TextView) view.findViewById(R.id.textView_sales_return_withoutInvoice_product);
        tvCodeSpinner = (TextView) view.findViewById(R.id.textView_sales_return_withoutInvoice_code);
        viewLayout = (ViewGroup) view.findViewById(R.id.layout_sales_return_withoutInvoice);
        etUnitPrice = (EditText) view.findViewById(R.id.unitprice);
        switch_tax_type = (SwitchCompat) view.findViewById(R.id.switch_taxtype);
        tvTotalRefund = (TextView) view.findViewById(R.id.textView_sales_return_withoutInvoice_refundTotal);
        etReturnQuantity = view.findViewById(R.id.editText_sales_return_withoutInvoice_returnQty);
        btnAddCart = (Button) view.findViewById(R.id.button_sales_return_withoutInvoice_addCart);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_sales_return_withoutInvoice_product);
        switchreceipt_type = view.findViewById(R.id.switch_receiptmode);
        spinnerCartUnit = (AppCompatSpinner) view.findViewById(R.id.spinner_sales_return_withoutInvoice_orderUnit);
        tvQtyTotal = (TextView) view.findViewById(R.id.textView_sales_qtyTotal);
        ly_taxindia = view.findViewById(R.id.ly_tax_india);
        ly_taxoutside = view.findViewById(R.id.ly_tax_outside);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        errorView = (ErrorView) view.findViewById(R.id.errorView);
        btnFinish = (Button) view.findViewById(R.id.button_sales_return_withoutInvoice_finish);
        btnMakePayment = view.findViewById(R.id.button_salesreturn_withoutinvoicemakePayment);
        //plusbutton = view.findViewById(R.id.bt_addqty);
        tvNetTotal = (TextView) view.findViewById(R.id.textView_sales_netTotal);
        tvVatcgst = (TextView) view.findViewById(R.id.textView_return_cgst);
        tvVatsgst = (TextView) view.findViewById(R.id.textView_return_sgst);
        // textView_sales_tax = view.findViewById(R.id.textView_sales_cgst);
        textView_sales_size = (TextView) view.findViewById(R.id.textView_sales_return_invoice_size);
        sp_totaldisc = view.findViewById(R.id.sp_totaldisc);
        textView_withoutdiscount = view.findViewById(R.id.textView_withoutdiscount_ret);
        ly_taxindia = view.findViewById(R.id.ly_tax_india);
        ly_taxoutside = view.findViewById(R.id.ly_tax_outside);
        tvVat =view.findViewById(R.id.textView_return_vat);
        edittext_discount = view.findViewById(R.id.edittext_discount);

        etProductDiscount = (EditText) view.findViewById(R.id.edittext_product_discount);

        edittext_discount.setText("0");

        sessionValue = new SessionValue(getContext());
        sessionAuth = new SessionAuth(getContext());

        myDatabase = new MyDatabase(getContext());

        CURRENCY = "" + sessionValue.getControllSettings().get(PREF_CURRENCY);

        try {
            EXECUTIVE_ID = sessionAuth.getExecutiveId();

            dayRegId = sessionValue.getDayRegisterId();

        } catch (Exception e) {
            e.getMessage();
        }
        VATPERCENTAGE = sessionValue.getControllSettings().get(PREF_VATPERCENT);
        Log.e("VATPERCENTAGE", VATPERCENTAGE);
        //haris added on 27/07/2021 condition for checking size wise or not.
        String st_size_wiseflag = sessionValue.getCompanyDetails().get(PREF_COMPANY_PRODUCT_TYPE);
        Log.e("st_size_wiseflag", st_size_wiseflag);
        switch_tax_type.setText(R.string.discnt_exclsve);
        if (st_size_wiseflag.equals("size")) {
            // plusbutton.setVisibility(View.VISIBLE);
            // plusbutton.setVisibility(View.VISIBLE);
            etReturnQuantity.setFocusable(false);

        } else {
            // plusbutton.setVisibility(View.GONE);
            // plusbutton.setVisibility(View.GONE);
            etReturnQuantity.setFocusable(true);

        }

        switchreceipt_type.setText(getContext().getText(R.string.return_advnce));

        switchreceipt_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    switchreceipt_type.setText(getContext().getText(R.string.return_billwise));
                else
                    switchreceipt_type.setText(getContext().getText(R.string.return_advnce));

            }
        });

        if (IS_GST_ENABLED) {
            ly_taxindia.setVisibility(View.GONE);
            ly_taxoutside.setVisibility(View.VISIBLE);
        } else {
            ly_taxindia.setVisibility(View.VISIBLE);
            ly_taxoutside.setVisibility(View.GONE);
        }

        array_totaldisc_tye.add("%");
        array_totaldisc_tye.add("Amnt");
        withoutInvoiceAdapter = new RvReturnWithoutInvoiceAdapter(cartItems, new OnNotifyListener() {
            @Override
            public void onNotified() {
                edittext_discount.setText("");
                String net = String.valueOf(" " + getAmount(withoutInvoiceAdapter.getNetTotal()) + " " + CURRENCY);
                Log.e("st_tax hrr", "" + st_tax);
//              //  Log.e("st_tax hrr22",""+SELECTED_CART.getTax());
//
//                try {
//                    double doublee_tax = Double.parseDouble(st_tax);
//                    dbl_tax_amount = (withoutInvoiceAdapter.getNetTotal() * doublee_tax)/100;
//                }catch (Exception e){
//
//                }
//                String vat="";
//                if (withoutInvoiceAdapter.getTaxTotal() != 0)
//                    vat = "" + getAmount(withoutInvoiceAdapter.getTaxTotal()) + " " + CURRENCY;
//
////                String vat = "GST  : " + getAmount(dbl_tax_amount) + " " + CURRENCY;
//                db_vat_amount = Double.parseDouble(""+withoutInvoiceAdapter.getTaxTotal());
//                String grandTotal = String.valueOf("REFUND TOTAL : " + getAmount(withoutInvoiceAdapter.getGrandTotal()) + " " + CURRENCY);
//                // tvTotalRefund.setText(String.valueOf(net + ",\t\t" + vat + ",\t\t" + grandTotal));
//
//                tvNetTotal.setText("TOTAL : " + net);
//                tvVat.setText("VAT : " + vat);
//                dbl_withouttax_total = withoutInvoiceAdapter.getNetTotal(); // Double.parseDouble(getAmount(adapter.getNetTotal()));
//               // tvTotalRefund.setText("" + getAmount(withoutInvoiceAdapter.getGrandTotal()) + " " + CURRENCY);
//                db_net_total = withoutInvoiceAdapter.getNetTotal();
//                db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());
//                db_disc_total = db_net_total - db_discount;
//                db_grand_total = db_disc_total + db_vat_amount;
//
//               tvTotalRefund.setText("" + getAmount(db_grand_total) + " " + CURRENCY);

                tvTotalRefund.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tvTotalRefund.setSelected(true);
                setTotalPriceView();

            }
        });

        btnFinish.setOnClickListener(this);
        btnMakePayment.setOnClickListener(this);
        btnAddCart.setOnClickListener(this);

        initView();

        getVanStockList();
        setShopTypeSpinner();

        cartItems.clear();
        withoutInvoiceAdapter.notifyDataSetChanged();
        etUnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etUnitPrice.setSelectAllOnFocus(true);
                String unitprice = etUnitPrice.getText().toString().trim();
                double unitpr = 0;

                String qtyString = etReturnQuantity.getText().toString().trim();

                etProductDiscount.setText("");

                if (!unitprice.isEmpty()) {

                    unitpr = Double.parseDouble(unitprice);
                    int quantity = TextUtils.isEmpty(qtyString) ? 0 : Integer.valueOf(qtyString);
                    tvQtyTotal.setText(getAmount(unitpr * quantity));

                    SELECTED_CART.setProductPrice((float) unitpr);

                } else {

                    unitpr = 0;
                    int quantity = TextUtils.isEmpty(qtyString) ? 0 : Integer.valueOf(qtyString);
                    tvQtyTotal.setText(getAmount(unitpr * quantity));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        switch_tax_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    switch_tax_type.setText(getContext().getText(R.string.discnt_inclsve));
                    discnt_typeflag = 1;
                } else {
                    switch_tax_type.setText(getContext().getText(R.string.discnt_exclsve));
                    discnt_typeflag = 0;
                }

            }
        });


        etProductDiscount.addTextChangedListener(new TextValidator(etProductDiscount) {
            @Override
            public void validate(TextView textView, String product_discount) {
                try {

                    if (!TextUtils.isEmpty(product_discount) && SELECTED_CART != null) {

//                        int pro_discount = TextUtils.isEmpty(product_discount) ? 0 : Integer.valueOf(product_discount);
                        float pro_discount = TextUtils.isEmpty(product_discount) ? 0 : Float.valueOf(product_discount);
                        int quatity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());

                        double netprice = Double.parseDouble(getAmount(SELECTED_CART.getProductPrice() * quatity));
                        pro_discount = pro_discount * quatity;

                        double discount_price = netprice - pro_discount;

                        tvQtyTotal.setText("" + getAmount(discount_price));

                    } else {

                        int quatity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());
                        tvQtyTotal.setText(getAmount(SELECTED_CART.getProductPrice() * quatity));
                    }
                } catch (Exception ignored) {

                }

            }
        });

        ArrayAdapter<String> totaldiscAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_totaldisc_tye);
        totaldiscAdapter.setDropDownViewResource(R.layout.spinner_list);
        sp_totaldisc.setAdapter(totaldiscAdapter);


        sp_totaldisc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edittext_discount.setText("");
                st_totaldisctype = sp_totaldisc.getSelectedItem().toString();
                Log.e("st_totaldisctype", "" + st_totaldisctype);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etReturnQuantity.addTextChangedListener(new TextValidator(etReturnQuantity) {
            @Override
            public void validate(TextView textView, String qtyString) {

                try {

                    String netamount = "";
                    double netAmount = 0;

                    etProductDiscount.setText("");

                    if (!TextUtils.isEmpty(qtyString) && SELECTED_CART != null) {

                        int quantity = TextUtils.isEmpty(qtyString) ? 0 : Integer.valueOf(qtyString);

                        //   tvNetPrice.setText(getAmount(getWithoutTaxPrice(select_Cart.getRetailPrice(), select_Cart.getTax())));
                        netAmount = roundTwoDecimals(SELECTED_CART.getProductPrice());
                        if (SELECTED_CART.getProductPrice() > 0) {
                            etUnitPrice.setText("" + netAmount);
                        } else {
                            etUnitPrice.setText("");
                        }

                        //  select_Cart.setProductPrice(getWithoutTaxPrice(select_Cart.getRetailPrice(), select_Cart.getTax()));

                           /* netamount = getAmount(getWithoutTaxPrice(select_Cart.getRetailPrice(), select_Cart.getTax()));
                            Log.e("Net Amount else", ""+netamount);

                            select_Cart.setProductPrice(netAmount);*/

                        tvQtyTotal.setText(getAmount(SELECTED_CART.getProductPrice() * quantity));

                    } else {
                        tvQtyTotal.setText(getAmount(0.0f));
                    }
                } catch (Exception ignored) {

                }
            }
        });


        edittext_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                less_discount = 0;
                String disc = "" + edittext_discount.getText().toString().trim();
                try {
                    db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());
                } catch (Exception e) {

                }
                double discount_tot = 0;
                double applicable_total = 0;
                if (disc.equals("")) {
                    disc = "0";
                }
                Log.e("discount hr", "" + disc);
                Log.e("discount tot", "" + withoutInvoiceAdapter.getDiscountTotal());

                //  st_disc = ""+disc;

                if (st_totaldisctype.equals("%")) {
                    Log.e("nettotal", "" + withoutInvoiceAdapter.getNetTotal());


                    double db_disc_tot = (withoutInvoiceAdapter.getWithoutTaxTotal() - withoutInvoiceAdapter.getDiscountTotal());
                    Log.e("db_disc_total", "" + (withoutInvoiceAdapter.getWithoutTaxTotal() - withoutInvoiceAdapter.getDiscountTotal()));
                    Log.e("db_discount", "" + db_discount);
                    double discnt = db_disc_tot * db_discount / 100;
                    Log.e("db_total_disc hhh", "" + discnt);
                    disc = "" + discnt;

                }


                try {
                    if (disc.isEmpty()) {
                        disc = "0";
                        st_disc = "0";
                    }

                    //adapter.st_disc = "" + disc;

                    // discount_tot = adapter.getDiscountTotal();
                    //applicable_total = adapter.getApplicableTotal();

                    less_discount = Double.parseDouble(disc);
                    setTotalPriceView();
                } catch (Exception e) {

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;

    }

//    private void setTotalPriceView() {
//
//        String net = String.valueOf("" + getAmount(withoutInvoiceAdapter.getNetTotal()) + " " + CURRENCY);
//        // String net = String.valueOf("" + getAmount(adapter.getWithoutTaxTotal()) + " " + CURRENCY);
//        String vat = " 00 " + CURRENCY;
//        double dbvat = 0;
//        if (withoutInvoiceAdapter.getTaxTotal() != 0)
//             dbvat = roundTwoDecimals(withoutInvoiceAdapter.getTaxTotal());
//            vat = "" + dbvat + " " + CURRENCY;
//            Log.e("vat set",""+vat);
//
//            String vatamnt = getAmount(withoutInvoiceAdapter.getTaxTotal());
//         vatamnt = vatamnt.replace(",","");
//        db_vat_amount = Double.parseDouble(""+vatamnt);
//
//        String grandTotal = String.valueOf("GRAND TOTAL : " + getAmount(withoutInvoiceAdapter.getGrandTotal()) + " " + CURRENCY);
//
//        //  tvNetTotal.setText(String.valueOf(net + ",\t\t" + vat + ",\t\t" + grandTotal));
//
//
//        textView_withoutdiscount.setText("" + net);
//
//
//        db_net_total = withoutInvoiceAdapter.getNetTotal();
//
//        Log.e("db_net_total", "" + db_net_total);
//       // Log.e("getApplicableTotal", "" + withoutInvoiceAdapter.getApplicableTotal());
//        Log.e("getDiscountTotal", "" + withoutInvoiceAdapter.getDiscountTotal());
//        String s = "" + db_net_total;
////        double value = 2.75;
//        double fraction=db_net_total%1;//Give you 0.75 as remainder
//        //  int integer=(int)value;//give you 2 fraction part will be removed
//        Log.e("fraction", "" + fraction);
//
//        if(fraction>=0.99){
//            db_net_total = Math.round(db_net_total);
//        }
//        db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());
//
//
//        //  db_withtax_total = db_net_total+db_vat_amount;
//        db_disc_total = db_net_total; // - db_discount;
//
//        Log.e("db_disc_total",""+db_disc_total);
//
//        double db_vatpercntg = 0;
//        if(!VATPERCENTAGE.equals("")){
//            db_vatpercntg = Double.parseDouble(VATPERCENTAGE);
//        }
//        Log.e("vat hrrr",""+db_vatpercntg);
//
//        dbl_tax_amount = withoutInvoiceAdapter.getTaxTotal();
//        if( less_discount>0) {
//            db_disc_total = 0;
//            db_disc_total = (withoutInvoiceAdapter.getWithoutTaxTotal() - withoutInvoiceAdapter.getDiscountTotal()) - less_discount;
//            Log.e("getWithoutTaxTotal 12", "" + withoutInvoiceAdapter.getWithoutTaxTotal());
//            Log.e("haris 12", "" + db_disc_total);
//            Log.e("less_discount 12", "" + less_discount);
//            Log.e("getDiscountTotal 12", "" + withoutInvoiceAdapter.getDiscountTotal());
//            double db_vat_tot = db_disc_total * db_vatpercntg/100;
//            Log.e("db_vat_tot new", "" + db_vat_tot);
//            Log.e("less_discount new", "" + less_discount);
//            Log.e("getWithoutTaxTotal new", "" + withoutInvoiceAdapter.getWithoutTaxTotal());
//            db_vat_tot=roundTwoDecimals(db_vat_tot);
//            vat = "" +db_vat_tot  +" " + CURRENCY;
//            Log.e("haris vat", "" + vat);
//            db_vat_amount = db_vat_tot;
//        }
//
//        tvNetTotal.setText("Total : " + getAmount(db_disc_total) + " " + CURRENCY);
//
//        //   db_vat_amount = getTaxPrice(db_disc_total, Float.parseFloat("" + db_vat));
//
//
//        tvVat.setText("VAT : " + vat);
//
//
//        //   db_grand_total = db_disc_total;
//        db_grand_total = db_disc_total + db_vat_amount;
//        tvTotalRefund.setText("" + getAmount(db_grand_total) + " " + CURRENCY);
//
//
//        dbl_withouttax_total = withoutInvoiceAdapter.getWithoutTaxTotal(); // Double.parseDouble(getAmount(adapter.getNetTotal()));
//
//        dbl_withtax_total = withoutInvoiceAdapter.getGrandTotal();
//
//        tvNetTotal.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        tvNetTotal.setSelected(true);
//
//
//    }

    private void setTotalPriceView() {

        String net = String.valueOf("" + getAmount(withoutInvoiceAdapter.getNetTotal()) + " " + CURRENCY);
        // String net = String.valueOf("" + getAmount(adapter.getWithoutTaxTotal()) + " " + CURRENCY);
        String vat = " 00 " + CURRENCY;
        if (withoutInvoiceAdapter.getTaxTotal() != 0)
            vat = "" + getAmount(withoutInvoiceAdapter.getTaxTotal()) + " " + CURRENCY;

        db_vat_amount = Double.parseDouble("" + getAmount(withoutInvoiceAdapter.getTaxTotal()));

        String grandTotal = String.valueOf("GRAND TOTAL : " + getAmount(withoutInvoiceAdapter.getGrandTotal()) + " " + CURRENCY);

        //  tvNetTotal.setText(String.valueOf(net + ",\t\t" + vat + ",\t\t" + grandTotal));


        textView_withoutdiscount.setText("" + net);


        db_net_total = withoutInvoiceAdapter.getNetTotal();


        Log.e("db_net_total", "" + db_net_total);
        //Log.e("getApplicableTotal", "" + withoutInvoiceAdapter.getApplicableTotal());
        Log.e("getDiscountTotal", "" + withoutInvoiceAdapter.getDiscountTotal());
        String s = "" + db_net_total;
//        double value = 2.75;
        double fraction = db_net_total % 1;//Give you 0.75 as remainder
        //  int integer=(int)value;//give you 2 fraction part will be removed
        Log.e("fraction", "" + fraction);

        if (fraction >= 0.99) {
            db_net_total = Math.round(db_net_total);
        }
        db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());


        //  db_withtax_total = db_net_total+db_vat_amount;
        db_disc_total = db_net_total; // - db_discount;

        Log.e("db_disc_total", "" + db_disc_total);

        double db_vatpercntg = 0;
        if (!VATPERCENTAGE.equals("")) {
            db_vatpercntg = Double.parseDouble(VATPERCENTAGE);
        }
        tvVatcgst.setText("CGST : " + db_vat_amount/2 +" " + CURRENCY);
        tvVatsgst.setText("SGST : " + db_vat_amount/2 +" " + CURRENCY);

        // if(withoutInvoiceAdapter.getApplicableTotal()>0 && less_discount>0) {
        if (less_discount > 0) {
            db_disc_total = 0;
            db_disc_total = (withoutInvoiceAdapter.getWithoutTaxTotal() - withoutInvoiceAdapter.getDiscountTotal()) - less_discount;

            Log.e("haris 12", "" + db_disc_total);
            double db_vat_tot = db_disc_total * db_vatpercntg / 100;
            Log.e("db_vat_tot new", "" + db_vat_tot);
            Log.e("less_discount new", "" + less_discount);
            Log.e("getWithoutTaxTotal new", "" + withoutInvoiceAdapter.getWithoutTaxTotal());

            vat = "" + getAmount(db_vat_tot) + " " + CURRENCY;
            Log.e("haris vat", "" + vat);
            Log.e("db_vat_amount vat", "" + db_vat_amount);
           // db_vat_amount = db_vat_tot;
            tvVatcgst.setText("CGST : " + db_vat_amount/2 +" " + CURRENCY);
            tvVatsgst.setText("SGST : " + db_vat_amount/2 +" " + CURRENCY);
        }

        if (IS_GST_ENABLED) {
            ly_taxindia.setVisibility(View.VISIBLE);
            ly_taxoutside.setVisibility(View.GONE);
            tvVatcgst.setText("CGST : " + db_vat_amount/2 +" " + CURRENCY);
            tvVatsgst.setText("SGST : " + db_vat_amount/2 +" " + CURRENCY);
        } else {
            ly_taxindia.setVisibility(View.GONE);
            ly_taxoutside.setVisibility(View.VISIBLE);
            tvVat.setText("VAT  : " + getAmount(db_vat_amount));
        }
//        if(st_totaldisctype.equals("%")){
//            db_total_disc = db_net_total * db_discount/100;
//            db_disc_total = db_net_total-(db_net_total * db_discount/100);
//        }
//        else{
//            db_total_disc = db_discount;
//            db_net_total = db_net_total-db_discount;
//            //  db_withtax_total = db_net_total+db_vat_amount;
//            db_disc_total = db_net_total; // - db_discount;
//        }

        tvNetTotal.setText("Total  : " + getAmount(db_disc_total) + " " + CURRENCY);

        //   db_vat_amount = getTaxPrice(db_disc_total, Float.parseFloat("" + db_vat));




        //   db_grand_total = db_disc_total;
        db_grand_total = db_disc_total + db_vat_amount;
        tvTotalRefund.setText("" + getAmount(db_grand_total) + " " + CURRENCY);


        dbl_withouttax_total = withoutInvoiceAdapter.getWithoutTaxTotal(); // Double.parseDouble(getAmount(adapter.getNetTotal()));
        dbl_tax_amount = withoutInvoiceAdapter.getTaxTotal();
        dbl_withtax_total = withoutInvoiceAdapter.getGrandTotal();

        tvNetTotal.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvNetTotal.setSelected(true);

       /* String disc = "" + edittext_discount.getText().toString().trim();
        double discount_tot = 0;
        double applicable_total = 0;
        try {
            if (disc.isEmpty()) {
                disc = "0";
            }

            discount_tot = adapter.getDiscountTotal();
            applicable_total = adapter.getApplicableTotal();

            if (Double.parseDouble(disc)>=discount_tot){
                if (applicable_total>0) {

                    double less_discount = Double.parseDouble(disc) - discount_tot;

                    Log.e("Less Disc", "" + less_discount);
                    Log.e("Applicable ", "" + applicable_total);

                    adapter.AddDiscount(less_discount, applicable_total);

                }

            }else {

            }

        } catch (Exception e) {

        }*/


    }


    private double roundTwoDecimals(double netPrice) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(netPrice));
    }

    private void add_quantity_dialogue() {

        if (prod_id_dialogue == int_product_id) {

        }
        prod_id_dialogue = int_product_id;

        ////get_sizelist();
        etReturnQuantity.setText("");

        textView_sales_size.setText("");

        dialog.setContentView(R.layout.dial_addquantity);

        android.widget.Button bt_save;


        android.widget.Button bt_cancel;


        bt_save = dialog.findViewById(R.id.bt_save);
        rec_list = dialog.findViewById(R.id.recyclerview);

        tv_totalqty = dialog.findViewById(R.id.tv_qty);
        bt_cancel = dialog.findViewById(R.id.bt_cancel);


        //dialog.setTitle("Complaint Details");
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations_SmileWindow;//R.style.DialogAnimations_SmileWindow;
        dialog.setCancelable(false);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                etReturnQuantity.setText("" + sizeadapter.getTotal_quantity());

                set_sizes();
            }
        });


        set_recycler();

        dialog.show();


    }

    private void set_recycler() {
        get_sizelist();

        sizeadapter = new Return_SizeAndQuantityAdapter(arr_size_return_full, new OnNotifyListener() {
            @Override
            public void onNotified() {
                tv_totalqty.setText("" + sizeadapter.getTotal_quantity());
                // Log.e("st_selected_siz",""+sizeadapter.get_selected_sizes());
                // Log.e("each_size_qty",""+sizeadapter.get_each_size_qty());
                st_each_size_qty = sizeadapter.get_each_size_qty();
                array_sizeanqty_aftersale = sizeadapter.get_new_qnty_aftersale();
                Log.e("array_size_select", "" + array_sizeanqty_aftersale);

                if (st_each_size_qty.length() > 0)
                    st_each_size_qty = st_each_size_qty.substring(0, st_each_size_qty.length() - 1);

                Log.e("each_size_qty 22", st_each_size_qty);
                //Log.e("sizeitem arr",""+sizeadapter.get_sizearray_selected());
                int second = 0;
                try {
                    sizeitems_selected_array.clear();
                    String st_main = sizeadapter.get_selected_sizes();
                    sizeitems_selected_array.addAll(Arrays.asList(st_main.split(",")));


                    Log.e("sizeitems_array", "" + sizeitems_selected_array);


                } catch (Exception e) {
//                    Log.e("first_sizeee",""+first_size);
//                    Log.e("second",""+second);
                }

            }
        });


        try {

            rec_list.setHasFixedSize(true);
            rec_list.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                    .showLastDivider()
                    .build());
            rec_list.setLayoutManager(new LinearLayoutManager(getActivity()));
            rec_list.setAdapter(sizeadapter);

        } catch (Exception e) {

        }


    }


    private void get_sizelist() {

        String qnty = "";
        try {

            JSONArray arr_size = new JSONArray(st_sizelist);
            Log.e("sgetSizelist hr", "" + st_sizelist);
            array_size.clear();
            array_sizeqnty.clear();
            arr_size_return_full.clear();


            for (int i = 0; i < arr_size.length(); i++) {

                Size size = new Size();

                JSONObject jObj = arr_size.getJSONObject(i);
                Integer size_st = jObj.getInt("sizeId");
                qnty = jObj.getString("quantity");
                String available_qty = jObj.getString("available_stock");
                float qty = Float.parseFloat(qnty);
                int q = (int) getRoundOfAmount(qty);
                Size_Return s = new Size_Return();
                s.setReturn_qnty("" + q);
                s.setReturn_size("" + size_st);
                s.setTotal_qty("0");
                s.setSelected_sizes("");

                s.setSize_after_edit(Float.parseFloat(qnty));
                arr_size_return_full.add(s);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("qnty  ddd", "" + qnty);
    }

    private void set_sizes() {
        //Log.e("list sz", "" + sizeitems_selected_array.size());
        //Log.e("list 1", "" + sizeitems_selected_array);
        size_finalarray.clear();
        String first = "", second = "", st_fullstring = "";
        int flag = 0;
        int full_flag = 0;

        ArrayList<String> arr_new = new ArrayList<>();
        arr_new.clear();
        try {
            for (int i = 0; i < sizeitems_selected_array.size(); i++) {
                //Log.e("list", "" + sizeitems_selected_array.get(i));
                if (Integer.parseInt(sizeitems_selected_array.get(i + 1)) == (Integer.parseInt(sizeitems_selected_array.get(i))) + 1) {

                    flag = 1;


                    if (first.equals("")) {
                        first = sizeitems_selected_array.get(i);
                    } else {

                        Log.e("removee haris 2", "" + sizeitems_selected_array.get(i));
                        arr_new.add(sizeitems_selected_array.get(i));

                    }
                    if ((i + 1) == sizeitems_selected_array.size() - 1) {

                        full_flag = 1;
                    }

                } else {

                    full_flag = 0;

                    if (flag == 1) {

                        st_fullstring = first + "-" + sizeitems_selected_array.get(i);
                        size_finalarray.add(st_fullstring);
                        sizeitems_continous.add(first);
                        sizeitems_continous.add(sizeitems_selected_array.get(i));
                        first = "";
                        flag = 0;

                    } else {


                    }

                }

                if (full_flag == 1) {
                    st_fullstring = first + "-" + sizeitems_selected_array.get(i + 1);
                    size_finalarray.add(st_fullstring);
                    sizeitems_continous.add(first);
                    sizeitems_continous.add(sizeitems_selected_array.get(i + 1));
                    first = "";
                    flag = 0;
                    full_flag = 0;
                }


            }
        } catch (Exception e) {

        }
//        Log.e("sizeitems_continous", "" + sizeitems_continous);
//
//        Log.e("size_finalarray", "" + size_finalarray);
        Log.e("arr_new", "" + arr_new);
        Log.e("sizeite_array", "" + sizeitems_selected_array);
        Log.e("continarray", "" + sizeitems_continous);
        try {
            for (int i = 0; i < sizeitems_selected_array.size(); i++) {

                for (int j = 0; j < arr_new.size(); j++) {
                    if (sizeitems_selected_array.get(i) == arr_new.get(j)) {
                        sizeitems_selected_array.remove(i);
                    }
                }
            }
        } catch (Exception e) {

        }
        Log.e("sizeitselec_array last", "" + sizeitems_selected_array);
        get_odd_size();

    }

    private void get_odd_size() {

        try {


            for (int i = 0; i < sizeitems_selected_array.size(); i++) {
                if (sizeitems_continous.contains(sizeitems_selected_array.get(i))) {
                    //do something for equals
                } else {
                    //do something for not equals
                    int index = sizeitems_continous.indexOf(sizeitems_selected_array.get(i));

                    //sizeitems_continous_odd.add(sizeitems_selected_array.get(i));
                    size_finalarray.add(sizeitems_selected_array.get(i));
                }
            }

        } catch (Exception e) {

        }

        st_size_string = "";
        for (int i = 0; i < size_finalarray.size(); i++) {
            st_size_string = st_size_string + size_finalarray.get(i) + ",";

        }
        if (st_size_string.length() > 0)
            st_size_string = st_size_string.substring(0, st_size_string.length() - 1);

        Log.e("st_size_string", "" + st_size_string);
        textView_sales_size.setText(st_size_string);


    }


    private void get_taxlist() {

//        try {
//
//            JSONArray arr_tx = new JSONArray(st_taxlist);
//            Log.e("taxlist hr",""+st_taxlist);
//            array_tax.clear();
//
//            for (int i =0;i<arr_tx.length(); i++){
//
//
//                JSONObject jObj = arr_tx.getJSONObject(i);
//                st_tax = jObj.getString("tax");
//                st_cgst = jObj.getString("cgst");
//                st_sgst = jObj.getString("sgst");
//
//                Taxes s = new Taxes();
//                s.setTax(st_tax);
//                s.setCgst(st_cgst);
//                s.setSgst(st_sgst);
//
//                array_tax.add(s);
//
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        Log.e("st_tax harisr ", "" + st_tax);
        st_tax = "" + SELECTED_CART.getTax();
//            textView_sales_cgst.setText(""+st_cgst);
//        textView_sales_sgst.setText(""+st_sgst);


    }

//    private void get_stock_sizes() {
//
//                 String qnty = "";
//            try {
//
//                JSONArray arr_size = new JSONArray(st_sizelist);
//
//                array_sizestock.clear();
//
//
//                for (int i = 0; i < arr_size.length(); i++) {
//
//                    Size_Return size = new Size_Return();
//
//                    JSONObject jObj = arr_size.getJSONObject(i);
//                    Integer size_st = jObj.getInt("sizeId");
//                    qnty = jObj.getString("quantity");
//                    String available_qty = jObj.getString("available_stock");
//
//                    Size_Return s = new Size_Return();
//                    s.setQuantity(qnty);
//                    s.setSizeId(size_st);
//                    s.setTotal_qty("0");
//                    s.setSelected_sizes("");
//                    s.setSize_after_edit(Float.parseFloat(qnty));
//                    array_sizestock.add(s);
//
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.e("qnty  ddd", "" + qnty);
//
//    }

    private void initView() {

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);

//
        //       return Product Recycler View
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(itemAnimator);

        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .showLastDivider()
//                .color(getResources().getColor(R.color.divider))
                .build());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(withoutInvoiceAdapter);
//        productAdapter.notifyDataSetChanged();

    }

    private void setShopTypeSpinner() {


        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_unitname);
        //ArrayAdapter.createFromResource(getActivity(), array_unitname, R.layout.spinner_background_dark); //R.array.cart_type
        orderTypeAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerCartUnit.setAdapter(orderTypeAdapter);

        try {

            spinnerCartUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (array_unitid.size() > 0) {
                        unit_id = array_unitid.get(position);
                        unit_name = array_unitname.get(position);
                        unit_confctr = array_unitconfactor.get(position);
                        unit_price = array_unitprice.get(position);

                        Log.e("Unit", "" + unit_name + "/" + unit_id);
                        Log.e("confctr", "" + unit_confctr + "/" + unit_price);
                    }
                    setCartProduct();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {

        }

    }


    //    Load Stock  List from Local
    private void getVanStockList() {

        // final ArrayList<CartItem> list =myDatabase.getAllStock();

        final ArrayList<CartItem> list = myDatabase.getAllMasterStock();

        if (list.isEmpty())

            setErrorView("No Stock", "", false);
        else
            cartItemscopy = (ArrayList<CartItem>) list.clone();

        setProductList(list);

        setProgressBar(false);


    }


    private void setProductList(ArrayList<CartItem> list) {


        final CartSpinnerDialog spinnerCart = new CartSpinnerDialog(getActivity(), list, "Select Product", R.style.DialogAnimations_SmileWindow);// With 	Animation

        tvProductSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerCart.showSpinerDialog("without_invoice");
            }
        });

        tvCodeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerCart.showCodeSpinerDialog("without_invoice");
            }
        });

        spinnerCart.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(Object item, int position) {

                SELECTED_CART = (CartItem) item;

                addflag = 0;


                for (int i = 0; i < cartItems.size(); i++) {
                    if (SELECTED_CART.getProductId() == cartItems.get(i).getProductId()) {
                        addflag = 1;
                    }
                }

                if (addflag == 0) {
                    Log.e("Add cart", "inside");
                    get_sizelist();
                    get_taxlist();
                    get_unitlist();
                    setShopTypeSpinner();
                    setCartProduct();
                } else {
                    //  Log.e("inside select cart",""+cartItems.get(i).getProductId());

                    if (dialogflag == 0) {

                        dialogflag = 1;
                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setMessage("Product already added ! Please edit from list if needed");

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed

                                dialogflag = 0;
                                alertDialog.dismiss();
                                refreshProductType();
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();

                    }
                }
            }

        });


        setProgressBar(false);
        viewLayout.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    private void get_unitlist() {
        String st_unit = "", st_unitid = "", st_unitprice = "", st_confactor = "";
        Log.e("unit_list", "" + SELECTED_CART.getUnitslist());
        try {
            JSONArray arr = new JSONArray(SELECTED_CART.getUnitslist());
            array_unitid.clear();
            array_unitname.clear();
            array_unitprice.clear();
            array_unitconfactor.clear();
            st_unit = "";
            st_unitid = "";
            st_unitprice = "";
            st_confactor = "";
            for (int i = 0; i < arr.length(); i++) {

                Units units = new Units();

                JSONObject jObj = arr.getJSONObject(i);
                String name = jObj.getString("unitName");
                String id = jObj.getString("unitId");
                String price = jObj.getString("unitPrice");
                String confctr = jObj.getString("con_factor");

                Log.e("Unit selected", "" + name);


//                if (id.equals("" + SELECTED_CART.getSale_unitid())) {
//                    st_unit = name;
//                    st_confactor = confctr;
//                    st_unitprice = price;
//                    st_unitid = id;
//                } else {
                    array_unitname.add(name);
                    array_unitid.add(id);
                    array_unitconfactor.add(confctr);
                    array_unitprice.add(price);

              //  }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        String st_unitname = array_unitname.get(0);
//        String st_confactr = array_unitconfactor.get(0);
//        String st_unitprce = array_unitprice.get(0);
//        String st_unit_id = array_unitid.get(0);
//
//        array_unitname.remove(0);
//        array_unitid.remove(0);
//        array_unitconfactor.remove(0);
//        array_unitprice.remove(0);
//
//        array_unitname.add(0, st_unit);
//        array_unitid.add(0, st_unitid);
//        array_unitconfactor.add(0, st_confactor);
//        array_unitprice.add(0, st_unitprice);
//
//        array_unitname.add(st_unitname);
//        array_unitid.add(st_unit_id);
//        array_unitconfactor.add(st_confactr);
//        array_unitprice.add(st_unitprce);


        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_unitname);
        //ArrayAdapter.createFromResource(getActivity(), array_unitname, R.layout.spinner_background_dark); //R.array.cart_type
        orderTypeAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerCartUnit.setAdapter(orderTypeAdapter);
    }


    public void getScannedBarcode(String str_barcode) {
        Log.e("bacode value fragment", str_barcode);

        // Toast.makeText(getActivity(), "scanned without invoice: "+str_barcode, Toast.LENGTH_LONG).show();
        //  str_barcode = "8901425055335";

        clearProductView();
        int flag = 0;

        for (int i = 0; i < cartItemscopy.size(); i++) {

            Log.e("barcode check " + i, "" + cartItemscopy.get(i).getBarcode());   //productList.get(i).getBarcode()

            if (str_barcode.equalsIgnoreCase(cartItemscopy.get(i).getBarcode())) {

                flag = 1;

                SELECTED_CART = (CartItem) cartItemscopy.get(i);

                setCartProduct();

            } else {
                printLog("Scan ", "Barcode Not found");
            }
        }

        if (flag == 0) {
            Toast.makeText(getActivity(), "Barcode Not found", Toast.LENGTH_LONG).show();
        }
    }


    private void refreshProductType() {

        tvProductSpinner.setText("");
        tvCodeSpinner.setText("");
        etUnitPrice.setText("");
        etReturnQuantity.setText("");
        etUnitPrice.setText("");
        SELECTED_CART = null;

//      etQuantity.setFocusable(false);
        //  tvNetTotal.setFocusable(true);

        //  edt_barcode.setFocusable(true);

        // edt_barcode.requestFocus();

        hideSoftKeyboard();

    }


    //    private void setCartProduct() {
//
//        if (SELECTED_CART == null)
//            return;
//
//        double productPrice =0;
//        try {
//             productPrice = Double.parseDouble(unit_price);
//        }catch (Exception e){
//
//        }
//
////        if (RETURN_TYPE.equals(SALE_WHOLESALE))
////            productPrice = SELECTED_CART.getWholeSalePrice();
//
//        int returnQuantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());
//
//        if (spinnerCartUnit.getSelectedItem().toString().equals(PRODUCT_UNIT_CASE))
//            returnQuantity = returnQuantity * SELECTED_CART.getPiecepercart();
//        Log.e("getTax",""+SELECTED_CART.getTax());
//
//        double salePrice = getSalePrice(productPrice, SELECTED_CART.getTax());
//
//        double netPrice = getWithoutTaxPrice(productPrice, SELECTED_CART.getTax());
//
//        SELECTED_CART.setNetPrice(netPrice);
//        Log.e("netpricee hr",""+netPrice);
//        SELECTED_CART.setSalePrice(salePrice);
//        SELECTED_CART.setProductPrice(productPrice);
//        SELECTED_CART.setUnitselected("Case");
//        tvUnitPrice.setText(getAmount(SELECTED_CART.getNetPrice()));
//
//        tvProductSpinner.setTag(SELECTED_CART);
//        tvCodeSpinner.setTag(SELECTED_CART);
//
//        tvProductSpinner.setText(SELECTED_CART.getProductName());
//        tvCodeSpinner.setText(SELECTED_CART.getProductCode());
//
//       // textView_sales_tax.setText("" +SELECTED_CART.getTax());
//
////      etReturnQuantity.requestFocus();
//////        etReturnQuantity.setFocusableInTouchMode(true);
//
//    }
    private void setCartProduct() {

        if (SELECTED_CART == null)
            return;

        etReturnQuantity.setText("");
        //  etQuantity.requestFocus();
        etReturnQuantity.setFocusableInTouchMode(true);
        etReturnQuantity.requestFocus();

        Log.e("RETAIL PRICE     sale", "" + SELECTED_CART.getRetailPrice());
        Log.e("wholesale PRICE    ", "" + SELECTED_CART.getWithouttaxTotal());
        Log.e("unit_name", unit_name);
        Log.e("unit_price", unit_price);
        try {

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(etReturnQuantity, InputMethodManager.SHOW_IMPLICIT);

            //double productPrice = select_Cart.getRetailPrice();
            //haris added on 17-12-21
            double productPrice = Double.parseDouble(unit_price);

            //commented by haris on 16-12-2021
//            if (SALE_TYPE.equals(SALE_WHOLESALE))
//                productPrice = select_Cart.getWholeSalePrice();

            int quantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());

            if (spinnerCartUnit.getSelectedItem().toString().equals(PRODUCT_UNIT_CASE))
                quantity = quantity * SELECTED_CART.getPiecepercart();

            Log.e("QTY 2", "" + quantity);
            double salePrice = getSalePrice(productPrice, SELECTED_CART.getTax(), "TAX_EXCLUSIVE");
            Log.e("SALE Price", "" + salePrice);

            double netPrice = getWithoutTaxPrice(productPrice, SELECTED_CART.getTax());
            netPrice = roundTwoDecimals(netPrice);
            Log.e("NET Price", "" + netPrice);

            SELECTED_CART.setNetPrice(netPrice);

            if (unit_name.equals(PRODUCT_UNIT_CASE)) {
                netPrice = netPrice * SELECTED_CART.getPiecepercart();
                SELECTED_CART.setNetPrice(netPrice);
            }

            Log.e("check", "1");
            SELECTED_CART.setSalePrice(salePrice);
            SELECTED_CART.setProductPrice(productPrice);
            //  select_Cart.setUnitId(unit_id);
            //unit_name = "Case";
            SELECTED_CART.setUnitselected(unit_name);
            Log.e("check", "2");
            tvQtyTotal.setText(getAmount(SELECTED_CART.getNetPrice() * quantity));

            Log.e("check", "3" + SELECTED_CART.getNetPrice());

            //      tvNetPrice.setText(""+getAmount(select_Cart.getNetPrice()));

            if (SELECTED_CART.getNetPrice() > 0) {

                etUnitPrice.setText("" + SELECTED_CART.getNetPrice());
            } else {
                etUnitPrice.setText("");
            }


            Log.e("check", "4");

            tvProductSpinner.setTag(SELECTED_CART);
            tvCodeSpinner.setTag(SELECTED_CART);

            tvProductSpinner.setText(SELECTED_CART.getProductName());
            tvCodeSpinner.setText(SELECTED_CART.getProductCode());

        } catch (Exception e) {
            Log.e("setcart error", "" + e.getMessage());
        }

    }

    private void addToCart1() {

        if (SELECTED_CART != null) {

            CartItem cart = SELECTED_CART;

            int returnQuantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());
            int retquantityy = 0;
            cart.setTypeQuantity(returnQuantity);

            if (spinnerCartUnit.getSelectedItem().toString().equals(PRODUCT_UNIT_CASE))
                returnQuantity = returnQuantity * cart.getPiecepercart();

            Log.e("qnty before", "" + returnQuantity);
            if (!unit_confctr.equals(""))
                retquantityy = returnQuantity * Integer.parseInt(unit_confctr);
            Log.e("qnty aftr", "" + returnQuantity);

            double productPrice = cart.getRetailPrice();

            if (RETURN_TYPE.equals(SALE_WHOLESALE))
                productPrice = cart.getWholeSalePrice();

            if (etProductDiscount.getText().toString().isEmpty()) {
                cart.setProductDiscount(0);
            } else {
                cart.setProductDiscount(Double.parseDouble(etProductDiscount.getText().toString().trim()));
            }
            String st_total = tvQtyTotal.getText().toString();
            if (st_total.equals("")) {
                st_total = "0";
            }
            String net = st_total.replace(",", "");
            Log.e("net hr", "" + net);
            cart.setProductTotal(Double.parseDouble(net));
            cart.setProductTotalValue(Double.parseDouble(net));

            cart.setProductTotal_withoutdisc(Double.parseDouble(net));
            cart.setTax_valuewithoutdisc(Double.parseDouble(net));
            productPrice = SELECTED_CART.getNetPrice();
            cart.setProductPrice(Double.parseDouble("" + tvQtyTotal.getText().toString()));
            //cart.setProductPrice(productPrice);

            double netPrice = getWithoutTaxPrice(productPrice, cart.getTax());
            Log.e("netpricee hr2", "" + netPrice);
            cart.setNetPrice(netPrice);
            st_hsn_code = cart.getProduct_hsncode();
            double salePrice = getSalePrice(netPrice, cart.getTax(), cart.getTax_type());

            //cart.setTaxValue(getTaxPrice(netPrice, cart.getTax()));
            cart.setTaxValue(getTaxPrice((SELECTED_CART.getProductTotal() - SELECTED_CART.getTaxValue()), SELECTED_CART.getTax(), SELECTED_CART.getTax_type()));
            cart.setProductTotal_withoutdisc(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
            cart.setTax_valuewithoutdisc(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));

            cart.setSalePrice(salePrice);

            cart.setReturnQuantity(returnQuantity);
            cart.setPieceQuantity(returnQuantity);

            cart.setTotalPrice(salePrice * returnQuantity);

            //haris added on 28-10-2020
            cart.setSizeandqty_string(st_each_size_qty);
            cart.setSize_string(st_size_string);
            cart.setProduct_hsncode(st_hsn_code);
            Log.e("st_hsn_code", "" + st_hsn_code);
            Log.e("st_each_size_qty", "" + st_each_size_qty);
            //haris added on 29-10-2020
            String gson_newquantity = update_sizeand_quantity_aftresale();
            Log.e("taxvalue haris", "" + cart.getTaxValue());

            cart.setSizelist(gson_newquantity);


            cart.setOrderType(spinnerCartUnit.getSelectedItem().toString());

            withoutInvoiceAdapter.returnItem(cart);

            clearProductView();

        }


    }

    private void addToCart() {

        //try {

        if (SELECTED_CART != null) {
            String st_disctype = "";
            if (discnt_typeflag == 0) {
                st_disctype = "TAX_EXCLUSIVE";
            } else {
                st_disctype = "TAX_INCLUSIVE";
            }
            CartItem cart = SELECTED_CART;
            int quantityy = 0;
            int quantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());
            int freequantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());

            cart.setTypeQuantity(quantity);
            cart.setDiscount_percent(disc_perentge);


            if (spinnerCartUnit.getSelectedItem().toString().equals(PRODUCT_UNIT_CASE))
                quantity = quantity * cart.getPiecepercart();

            //haris added on 17-12-21
            Log.e("qnty before", "" + quantity);
            if (!unit_confctr.equals(""))
                quantityy = quantity * Integer.parseInt(unit_confctr);
            Log.e("qnty aftr", "" + quantityy);
            ////////////////////////////
            cart.setCon_factor(Integer.parseInt(unit_confctr));
            cart.setUnit_rate_pcs(price_pcs);
            SELECTED_CART.setUnit_rate_kg(price_kg);
            double productPrice = cart.getRetailPrice();

//                if (SALE_TYPE.equals(SALE_WHOLESALE))
//                    productPrice = cart.getWholeSalePrice();


            Log.e("productprice", etUnitPrice.getText().toString());
            try {

                cart.setProductPrice(Double.parseDouble("" + etUnitPrice.getText().toString()));
            } catch (Exception e) {

            }

            //   double netPrice = getWithoutTaxPrice(productPrice, select_Cart.getTax());

            double netPrice = 0;
            if (!etUnitPrice.getText().toString().isEmpty()) {
                netPrice = Double.parseDouble(etUnitPrice.getText().toString().trim());
            } else {
                netPrice = 0;
            }

            cart.setNetPrice(netPrice);

            if (etProductDiscount.getText().toString().isEmpty()) {
                cart.setProductDiscount(0);
            } else {
                cart.setProductDiscount(Double.parseDouble(etProductDiscount.getText().toString().trim()));
            }


            double proDisc = cart.getProductDiscount();
            if (proDisc > 0 || freequantity > 0) {
                //haris added on 25-08-21
                // cart.setDiscountStatus(false);
                cart.setDiscountStatus(true);
            } else {
                cart.setDiscountStatus(true);
            }
            cart.setProductTotal(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
            cart.setProductTotalValue(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
            cart.setTax_type(st_disctype);
            cart.setPieceQuantity_nw(quantity);
            cart.setReturnQuantity(quantityy);
            cart.setPieceQuantity(quantityy);
            cart.setCon_factor(Integer.parseInt(unit_confctr));
            //haris added on 25-11-2020
            cart.setProductTotal_withoutdisc(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
            cart.setTax_valuewithoutdisc(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
            SELECTED_CART.setTax_type(cart.getTax_type());
            cart.setTaxValue(getTaxPrice((cart.getProductPrice() - cart.getProductDiscount()), SELECTED_CART.getTax(), SELECTED_CART.getTax_type()));
            //cart.setTaxValue(getTaxPrice(netPrice, cart.getTax(),SELECTED_CART.getTax_type()));
            if (st_disctype.equals("TAX_INCLUSIVE")) {
                cart.setProductTotal(SELECTED_CART.getProductTotalValue() - (cart.getTaxValue() * cart.getPieceQuantity_nw()));
                cart.setProductPriceNew(SELECTED_CART.getProductTotalValue() - SELECTED_CART.getTaxValue());
                Log.e("getProductTotal 123", "" + cart.getProductTotal());
                Log.e("SELECTED_CART 123", "" + SELECTED_CART.getTaxValue());
                Log.e("cart 123", "" + cart.getTaxValue());
            }
            ////

            double salePrice = getSalePrice(netPrice, SELECTED_CART.getTax(), SELECTED_CART.getTax_type());


            dbl_tax_value = SELECTED_CART.getTax();

            double amnt = SELECTED_CART.getNetPrice() * quantity;


            double bonusamount = getBonusAmount(amnt, SELECTED_CART.getProductBonus());

            //   Log.e("Bonus amount", "" + bonusamount);

            cart.setProductBonus(SELECTED_CART.getProductBonus());

            cart.setSalePrice(salePrice);
            cart.setUnitid_selected(unit_id);

            //added on 22-02-2022

            //cart.setPieceQuantity(quantity);
            //cart.setFreeQty(freequantity);

            cart.setTotalPrice(salePrice * quantity);


            cart.setOrderType(spinnerCartUnit.getSelectedItem().toString());
            //  cart.setOrderType(unit_id);

            cart.setOrderTypeName(unit_name);

            // Binds all strings into an array
            withoutInvoiceAdapter.returnItem(cart);

            refreshProductType();
        }
//        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Error in Quantity", Toast.LENGTH_SHORT).show();
//        }
    }

    private void clearProductView() {
        tvCodeSpinner.setText("");
        tvProductSpinner.setText("");
        etReturnQuantity.setText("");
        etUnitPrice.setText("");
        textView_sales_size.setText("");
        SELECTED_CART = null;


        // etReturnQuantity.setFocusable(false);
        tvTotalRefund.setFocusable(true);


        hideSoftKeyboard();

    }

    private boolean returnValidate() {

        boolean status = false;

        String s = tvProductSpinner.getText().toString().trim();
        int returnQuantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());


        tvProductSpinner.setError(null);
        etReturnQuantity.setError(null);


        if (SELECTED_CART == null) {
            status = false;
            tvProductSpinner.setError("Select Product");

        } else if (TextUtils.isEmpty(s)) {
            status = false;
            tvProductSpinner.setError("Select Product");

        } else if (TextUtils.isEmpty(etReturnQuantity.getText().toString().trim())) {
            etReturnQuantity.setError("Enter Quantity");
            status = false;
        } else if (TextUtils.isEmpty(etUnitPrice.getText().toString().trim()) || (etUnitPrice.getText().toString().trim().equals("0"))) {
            Toast.makeText(getActivity(), "Unit price cannot be Empty...!", Toast.LENGTH_SHORT).show();
            status = false;

        } else
            status = true;

        return status;
    }


    //    change sale type

    public void changeSaleType(String type) {

        RETURN_TYPE = type;
        ArrayList<CartItem> list = withoutInvoiceAdapter.getReturnItems();

        if (!list.isEmpty()) {

            for (int i = 0; i < list.size(); i++) {

                CartItem c = list.get(i);


                float salePrice = c.getRetailPrice();

                if (RETURN_TYPE.equals(SALE_WHOLESALE))
                    salePrice = c.getWholeSalePrice();


                c.setSalePrice(salePrice);

                c.setTotalPrice(salePrice * c.getReturnQuantity());

                withoutInvoiceAdapter.updateItem(c, i);
            }

        }

    }

    private String update_sizeand_quantity_aftresale() {
        String gsonvalue_size = "";

        ArrayList<Size_Return> size_array = new ArrayList<>();
        for (Size_Return s : array_sizeanqty_aftersale) {
            if (!s.getTotal_qty().equals("0")) {
                s.setSizeId(Integer.parseInt(s.getReturn_size()));
                int newqnty = Integer.parseInt(s.getReturn_qnty()) + Integer.parseInt(s.getTotal_qty());
                s.setQuantity("" + newqnty); //sale affected stock
                s.setNew_qnty_aftersale(s.getNew_qnty_aftersale());

            } else {
                s.setSizeId(Integer.parseInt(s.getReturn_size()));
                s.setQuantity("" + s.getReturn_qnty()); //sale affected stock
                s.setNew_qnty_aftersale(s.getNew_qnty_aftersale());


            }
            size_array.add(s);
        }

        gsonvalue_size = new Gson().toJson(size_array);
        Log.e("gsonvalue_size", "" + gsonvalue_size);
        //product.setSizelist(gsonvalue_size);
        return gsonvalue_size;


    }

    //        method can pass sale data to local db class
    private void saveToLocalReturn() {

        disc_value = withoutInvoiceAdapter.getDiscountTotal() + less_discount;
//        double taxable_value = (dbl_withouttax_total - disc_value)+db_vat_amount;
        double taxable_value = (dbl_withouttax_total - withoutInvoiceAdapter.getDiscountTotal());
        Log.e("taxable_value", "" + taxable_value);
        //dbl_withouttax_total = dbl_withouttax_total-disc_value;
        dbl_withtax_total = dbl_withouttax_total + db_vat_amount;

        Log.e("dbl_withtax_total hr", "" + dbl_withtax_total);
        db_vat_amount = roundTwoDecimals(db_vat_amount);
        dbl_withtax_total = roundTwoDecimals(dbl_withtax_total);
        db_grand_total = roundTwoDecimals(db_grand_total);
        double db_taxtotal = roundTwoDecimals(withoutInvoiceAdapter.getTaxTotal());
        dbl_withouttax_total = roundTwoDecimals(dbl_withouttax_total);
        double db_disc_total_product = roundTwoDecimals(withoutInvoiceAdapter.getDiscountTotal());
        taxable_value = roundTwoDecimals(taxable_value);

        //roundoff_calculation
        String[] arr = String.valueOf(getAmount(db_grand_total)).split("\\.");
        Log.e("arr", "" + arr[0]);
        Log.e("arr", "" + arr[1]);
        int[] intArr = new int[2];
        intArr[0] = Integer.parseInt(arr[0]); // 1
        intArr[1] = Integer.parseInt(arr[1]); // 9

        Log.e("intArr[0]", "" + intArr[0]);
        Log.e("intArr[1]", "" + intArr[1]);
        String st_decimal = "" + intArr[1];
        if (st_decimal.length() < 2) {
            if (arr[1].startsWith("0")) {
                Log.e("if st_decml", "" + arr[1]);
                st_decimal = "0" + st_decimal;
            } else {
                Log.e("else st_decml", "" + arr[1]);
                st_decimal = st_decimal + "0";
            }
        }
        String substring = st_decimal.substring(0, 2);
        Log.e("substring 1", "" + substring);
        float decimalpart = Float.parseFloat(substring);
        decimalpart = (float) (decimalpart / 100);
        Log.e("decimalpart 1", "" + decimalpart);
        int round_off = 0;
        double total_aftr = 0;
        double db_diff = 0;
        String st_decml = getAmountNew(Double.parseDouble("" + decimalpart));
        Log.e("st_decml", "" + st_decml);
        double dec = Double.parseDouble(st_decml);
        //dec= roundTwoDecimals(dec);
        Log.e("decdec", "" + dec);
        if (dec > 0) {
            if (dec > 0 && dec <= 0.12) {
                db_diff = dec;
                total_aftr = db_grand_total - dec;
            }
            if (dec > 0.12 && dec <= 0.25) {
                // round_off = decimalpart-25;
                db_diff = 0.25 - decimalpart;
                //db_diff = db_diff/100;
                total_aftr = db_grand_total + db_diff;
            }
            if (dec > 0.25 && dec <= 0.50) {
                db_diff = 0.50 - decimalpart;
                // db_diff = db_diff/100;
                total_aftr = db_grand_total + db_diff;
            }
            if (dec > 0.50 && dec <= 0.75) {
                db_diff = 0.75 - decimalpart;
                // db_diff = db_diff/100;
                total_aftr = db_grand_total + db_diff;
            }
            if (dec > 0.75 && dec <= 0.99) {
                db_diff = 1 - decimalpart;
                // db_diff = db_diff/100;
                total_aftr = db_grand_total + db_diff;
                //dbl_tax_amount=dbl_tax_amount+0.75;
            }
            Log.e("idbl_tax_amount aftr", "" + total_aftr);
            db_grand_total = total_aftr;

        }


        if (withoutInvoiceAdapter.getReturnItems().isEmpty()) {
            Toast.makeText(getContext(), "Return Products is Empty..!", Toast.LENGTH_SHORT).show();
            return;
        }

      //  double paid_amount = 0;

        //  pref_bonus = sessionAuth.getBonus();

        final String strDate = getDateTime();
        String st_diff = getAmount(db_diff);
        Sales saleReturn = new Sales();

        saleReturn.setCustomerId(SELECTED_SHOP.getShopId());
        saleReturn.setDate(strDate);
        saleReturn.setTotal(db_grand_total);
        saleReturn.setSaleType(RETURN_TYPE);
        saleReturn.setPaid(paid_amount);
        saleReturn.setTaxTotal(db_taxtotal);
        saleReturn.setCartItems(withoutInvoiceAdapter.getReturnItems());
        saleReturn.setRoundoff_value(Float.parseFloat("" + st_diff));

        saleReturn.setTaxPercentage(dbl_tax_value);
        saleReturn.setTaxAmount(db_vat_amount);
        saleReturn.setWithTaxTotal(db_grand_total);
        saleReturn.setHsn_code(st_hsn_code);
        //String st_cgst_tax = textView_sales_tax.getText().toString();
        //String st_sgst_tax = textView_sales_sgst.getText().toString();

        saleReturn.setWithoutTaxTotal(dbl_withouttax_total - withoutInvoiceAdapter.getDiscountTotal());
        saleReturn.setCgst_tax(0);
        saleReturn.setSgst_tax(0);
        saleReturn.setDiscount_value(disc_value);
        saleReturn.setDiscount_percentage(db_discount);
        saleReturn.setTaxable_total(taxable_value);
        saleReturn.setCgst_tax_rate(st_cgst);
        saleReturn.setSgst_tax_rate(st_sgst);
        //
        saleReturn.setSaleLatitude("" + latitude_session);
        saleReturn.setSaleLongitude("" + longitude_session);
        saleReturn.setShopname(SELECTED_SHOP.getShopName());
        saleReturn.setShopcode(SELECTED_SHOP.getShopCode());
        saleReturn.setReturn_type("without");

        db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());

        if (withoutInvoiceAdapter.getDiscountTotal() > 0)

            saleReturn.setDiscount_value((float) db_disc_total_product + (float) less_discount);
        else {
            saleReturn.setDiscount_value((float) less_discount);

        }
        saleReturn.setDiscount((float) less_discount);
        ////
        //saleReturn.setDiscountAmount(db_disc_total);
        saleReturn.setDiscountAmount((taxable_value) - less_discount);
        String st_executive_code = sessionValue.getexecutivecode();
        //  String returnNumber = generateNewNumber(sessionValue.getReturnCode(SELECTED_SHOP.getRouteCode()));
        String returnNumber = generateNewNumber(sessionValue.getReturnCode(st_executive_code));
        saleReturn.setReturn_invoiceno(st_executive_code + returnNumber);
        saleReturn.setInvoiceCode(st_executive_code + returnNumber);
        saleReturn.setPayment_type(st_paytype);

        cash_inhand = sessionAuth.getCashinHand();
        //float return_amount = Float.parseFloat(getAmount(withoutInvoiceAdapter.getGrandTotal()));
        // float return_amount = Float.parseFloat(getAmount(db_grand_total));
//haris
        int return_amnt = (int) getRoundOfAmount(Float.parseFloat("" + db_grand_total));//float return_amount =2000;
        float cash = cash_inhand - Float.parseFloat("" + return_amnt);
        //float cash = cash_inhand - return_amount ;
        sessionAuth.updateCashinHand(cash);

        //  saleReturn.setInvoiceCode("0");
        saleReturn.setUploadStatus("N");

//        long insertStatus = myDatabase.insertReturn(saleReturn);
//
//        if (insertStatus != -1) {
//            sessionValue.storeReturnCode(st_executive_code, returnNumber);
//            Transaction t=new Transaction(SELECTED_SHOP.getShopId() , saleReturn.getTotal() , 0);
//            if (myDatabase.updateCustomerBalance(t)) {
//
//                myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_RETURN_TYPE, "","","");  // update sales return status to local db
//
//                for (CartItem c : saleReturn.getCartItems()) {
//                    myDatabase.updateStock(c, REQ_RETURN_TYPE);
//                }
//
//                Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
//
//                SAVE_STATUS = 1;
//
//                salesReturn = saleReturn;
//
//                Intent intent = new Intent(getActivity(), ReturnPreviewActivity.class);
//                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN);
//
//                intent.putExtra(INVOICE_RETURN_VALUE_KEY, saleReturn);
//                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
//
//                getActivity().startActivity(intent);
//                getActivity().finish();
//
//            }else {
//                Toast.makeText(getContext(), "Error updating customer balance", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getActivity(), "Insertion Failed", Toast.LENGTH_SHORT).show();
//
//        }


        salesReturn = saleReturn;

        sessionValue.storeReturnCode(st_executive_code, returnNumber);
        if (switchreceipt_type.isChecked()) {
            Intent intent = new Intent(getActivity(), BillwiseReceipt.class);
            intent.putExtra(SHOP_KEY, SELECTED_SHOP);
            intent.putExtra("return_total", db_grand_total);
            intent.putExtra("retrn_page", "without");
            intent.putExtra(INVOICE_RETURN_VALUE_KEY, saleReturn);
            startActivity(intent);
            getActivity().finish();
        } else {

            //long insertStatus = myDatabase.insertReturnnew(saleReturn);
            long insertStatus = myDatabase.insertReturn(saleReturn);
            if (insertStatus != -1) {

                sessionValue.storeReturnCode(st_executive_code, returnNumber);
                Transaction t = new Transaction(SELECTED_SHOP.getShopId(), saleReturn.getWithTaxTotal(), 0);
                if (myDatabase.updateCustomerBalance(t)) {

                    myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_RETURN_TYPE, "", "", "");  // update sales return status to local db

                    for (CartItem c : saleReturn.getCartItems()) {

                        myDatabase.updateStock(c, REQ_RETURN_TYPE);

                    }

                    Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();

                    SAVE_STATUS = 1;

                    Intent intent = new Intent(getActivity(), ReturnPreviewActivity.class);
                    intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN);

                    intent.putExtra(INVOICE_RETURN_VALUE_KEY, saleReturn);
                    intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                    getActivity().startActivity(intent);
                    getActivity().finish();
                }

            } else {
                Toast.makeText(getContext(), "Error updating customer balance", Toast.LENGTH_SHORT).show();
            }

        }
         /* if (isNetworkConnected()){

            placeReturn(saleReturn);

           }else {

            Toast.makeText(getContext(), "No connection", Toast.LENGTH_SHORT).show();

             }*/

    }

    //online return
    private void postSaleReturn_() {


        if (withoutInvoiceAdapter.getReturnItems().isEmpty()) {
            Toast.makeText(getContext(), "Return Products is Empty..!", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(getContext(), null, "Please wait...", false, false);

        final ArrayList<CartItem> list = withoutInvoiceAdapter.getReturnItems();

        JSONObject object = new JSONObject();
        JSONObject returnObj = new JSONObject();

        JSONArray productArray = new JSONArray();

        try {
            returnObj.put("invoice_id", "0");
            returnObj.put(CUSTOMER_KEY, SELECTED_SHOP.getShopId());
            returnObj.put("total", withoutInvoiceAdapter.getNetTotal());
            returnObj.put("grand_total", withoutInvoiceAdapter.getGrandTotal());


            for (CartItem c : list) {

                JSONObject obj = new JSONObject();

                obj.put("product_id", c.getProductId());
                obj.put("unit_price", c.getSalePrice());
                obj.put("tax", c.getTax());
                obj.put("return_quantity", c.getReturnQuantity());
                obj.put("refund_Amount", c.getTotalPrice());
                productArray.put(obj);

            }

            returnObj.put("ReturnedProduct", productArray);
            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("SalesReturn", returnObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        webSaleReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    if (response.getString("status").equals("Success")) {

                        myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_RETURN_TYPE, "", "", "");  // update sales return status to local db


                        for (CartItem c : list) {
                            myDatabase.updateStock(c, REQ_RETURN_TYPE);
                        }


                        Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
                        // Reload current fragment
                        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag(FRAGMENT_WITOUTINVOICE);
                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();
                    } else
                        Toast.makeText(getContext(), response.getString("result"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();

            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    private void ShowPrintAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("Already Saved");

        // Setting Dialog Message
        alertDialog.setMessage("Return already saved. Do you want to print invoice ?");

        // Setting Icon to Dialog
        //  alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Write your code here to invoke YES event
                //  Toast.makeText(getActivity(), "You clicked on YES", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), ReturnPreviewActivity.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN);

                intent.putExtra(INVOICE_RETURN_VALUE_KEY, salesReturn);
                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    //    place return
    private void placeReturn(final Sales salereturn) {
        Log.e("return list method", "1");
        final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray returnArray = new JSONArray();

        try {

            Log.e("return list method", "1/" + salereturn.getCartItems().size());

            final JSONObject returnObj = new JSONObject();

            returnObj.put("invoice_id", "0");
            returnObj.put(CUSTOMER_KEY, salereturn.getCustomerId());
            returnObj.put("tax_total", salereturn.getTaxTotal());
            returnObj.put("grand_total", salereturn.getTotal());

            JSONArray cartArray = new JSONArray();

            for (CartItem c : salereturn.getCartItems()) {

                JSONObject obj = new JSONObject();

                obj.put("product_id", c.getProductId());
                obj.put("unit_price", c.getProductPrice());
                obj.put("tax", c.getTax());
                obj.put("return_quantity", c.getReturnQuantity());
                obj.put("refund_Amount", c.getTotalPrice());
                obj.put("product_bonus", c.getProductBonus());
                cartArray.put(obj);
            }

            returnObj.put("ReturnedProduct", cartArray);

            returnArray.put(returnObj);

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("SalesReturn", returnArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "placeReturn  object   " + object);

        webReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "placeReturn  response " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("Success")) {

                        long insertStatus = myDatabase.insertReturn(salereturn);

                        if (insertStatus != -1) {

                            myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_RETURN_TYPE, "", "", "");  // update sales return status to local db

                            for (CartItem c : salereturn.getCartItems()) {
                                myDatabase.updateStock(c, REQ_RETURN_TYPE);
                            }

                            Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();

                            SAVE_STATUS = 1;

                            salesReturn = salereturn;

                            Intent intent = new Intent(getActivity(), ReturnPreviewActivity.class);
                            intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN);

                            intent.putExtra(INVOICE_RETURN_VALUE_KEY, salereturn);
                            intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                            getActivity().startActivity(intent);
                            getActivity().finish();

                        } else {
                            Toast.makeText(getActivity(), "Insertion Failed", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(getActivity(), "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    //     ProgressBar
    private void setProgressBar(boolean isVisible) {

        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
            viewLayout.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);

        } else {
            progressBar.setVisibility(View.GONE);
        }

    }


    //set ErrorView
    private void setErrorView(final String title, final String subTitle, boolean isRetry) {

        viewLayout.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        setProgressBar(false);
        errorView.setConfig(ErrorView.Config.create()
                .title(title)
                .subtitle(subTitle)
                .retryVisible(false)
                .build());


        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {


                getVanStockList();

            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_sales_return_withoutInvoice_addCart:

                if (returnValidate())
                    addToCart();


                break;
            case R.id.button_salesreturn_withoutinvoicemakePayment:

                double saleCredit = myDatabase.getCustomerSaleCreditedAmount(SELECTED_SHOP.getShopId());
                double collection = myDatabase.getReceiptCollectionAmount(SELECTED_SHOP.getShopId());

                double maxCredit = SELECTED_SHOP.getCreditLimit() - saleCredit + collection;
                final PaymentTypeDialog paymentDialog = new PaymentTypeDialog(getActivity(), maxCredit, SELECTED_SHOP.getCreditlimit_register(), db_grand_total, PAYMENT_TYPE, new PaymentTypeDialog.paymentTypeClickListener() {
                    @Override
                    public void onPaymentTypeClick(String type) {

                        PAYMENT_TYPE = type;

                        if (type.equals("Cash")) {
                            //  paid_amount = adapter.getGrandTotal();
                            paid_amount = db_grand_total;
                            st_paytype = "CashSale";
                        }
                        else if(type.equals("Credit")) {
                            paid_amount = 0;
                            st_paytype = "CreditSale";
                        }

                    }

                });

                paymentDialog.show();


                break;


            case R.id.button_sales_return_withoutInvoice_finish:

                get_latitudeandlongitude();
                if (!str_Latitude.equals("0") && !str_Longitude.equals("0")) {
                    sessionValue.save_latitude_and_longitude(str_Latitude, str_Longitude);
                }
                latitude_session = sessionValue.get_map_details().get(PREF_LATITUDE);  //latitude get from session
                longitude_session = sessionValue.get_map_details().get(PREF_LONGITUDE);  //longitude get from session

                if (SAVE_STATUS == 0) {
                    saveToLocalReturn();
                } else {

                    ShowPrintAlert();
                }

                //  saveToLocalReturn();
/*
                if (withoutInvoiceAdapter.getReturnItems().isEmpty()) {
                    Toast.makeText(getContext(), "Return Products is Empty..!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                // Reload current fragment
                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag(FRAGMENT_WITOUTINVOICE);
                final FragmentTransaction ft =getActivity(). getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();

*/
                break;


        }
    }

    private void get_latitudeandlongitude() {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Requires Permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                LocationListener mlocListener = new MyLocationListener();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 61000, 250,
                        mlocListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
            }
        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error with location manager", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        if (inputMethodManager != null) {
//            inputMethodManager.showSoftInput(view, 0);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private static String getDateTime() {
        Date date = new Date();
        return dbDateFormat.format(date);
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String Text = "My current location is: " + "Latitude = "
                    + loc.getLatitude() + "\nLongitude = " + loc.getLongitude();

            str_Latitude = "" + loc.getLatitude();
            str_Longitude = "" + loc.getLongitude();

            //  tvNetTotal.setText("Lat : "+str_Latitude+" / Long : "+str_Longitude);
            /*Toast.makeText(getActivity(), Text, Toast.LENGTH_SHORT)
                    .show();*/
            Log.d("TAG", "Starting..");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getActivity(), "Gps Disabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getActivity(), "Gps Enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}