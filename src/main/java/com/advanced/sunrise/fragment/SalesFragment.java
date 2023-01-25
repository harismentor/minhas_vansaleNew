package com.advanced.minhas.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.advanced.minhas.config.AmountCalculator.getBonusAmount;
import static com.advanced.minhas.config.AmountCalculator.getSalePrice;
import static com.advanced.minhas.config.AmountCalculator.getTaxPricefl;
import static com.advanced.minhas.config.AmountCalculator.getWithoutTaxPrice;
import static com.advanced.minhas.config.AmountCalculator.productprice_inclusive;
import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_QUOTATION_TEMP;
import static com.advanced.minhas.config.ConfigKey.REQ_SALE_TYPE;
import static com.advanced.minhas.config.ConfigSales.IS_GST_ENABLED;
import static com.advanced.minhas.config.ConfigSales.IS_PRODUCT_DESCRIPTION_ENABLED;
import static com.advanced.minhas.config.ConfigSales.IS_PRODUCT_DISOUNTTYPE_ENABLED;
import static com.advanced.minhas.config.ConfigSales.IS_PRODUCT_DISOUNT_ENABLED;
import static com.advanced.minhas.config.ConfigSales.IS_PRODUCT_FOC_ENABLED;
import static com.advanced.minhas.config.ConfigSales.IS_SALE_ONLINE;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.PRODUCT_UNIT_CASE;
import static com.advanced.minhas.config.ConfigValue.SALES_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.SALE_RETAIL;
import static com.advanced.minhas.config.ConfigValue.SALE_WHOLESALE;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dbDateFormat;
import static com.advanced.minhas.config.Generic.generateNewNumber;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getAmountthree;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_VATPERCENT;
import static com.advanced.minhas.webservice.WebService.webPlaceOrder;
import static com.advanced.minhas.webservice.WebService.webPlaceQuotation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.advanced.minhas.activity.PreviewActivity;

import com.advanced.minhas.adapter.RvCartAdapter;
import com.advanced.minhas.dialog.CartSpinnerDialogNew;
import com.advanced.minhas.dialog.OnSpinerItemClick;
import com.advanced.minhas.dialog.PaymentTypeDialog;
import com.advanced.minhas.dialog.ProductRemarkDialog;

import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.CartItemCode;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Transaction;
import com.advanced.minhas.model.Units;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.textwatcher.TextValidator;
import com.advanced.minhas.view.ErrorView;
import com.advanced.minhas.webservice.WebService;
import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SalesFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_SHOP = "customer_arg";

    String TAG = "SalesFragment";
    int callingActivity = 0, available_qty = 0, prod_click = 0;
    private Shop SELECTED_SHOP = null;
    private static String st_vat_status = "";
    private String EXECUTIVE_ID = "";
    String unit_id = "", unit_name = "", invoiceNumber = "", CURRENCY = "", VATPERCENTAGE = "", st_totaldisctype = "",
            unit_confctr = " ", unit_price = " ", mfg_prodid = "", mfg_date = "", confctr_kgm = "", confctr_kg = "",
            kgm_unitprice = "" ,st_prod_disctype ="" ,st_descrption ="",unit_wholesale_prce= "";
    private String dayRegId = "", st_paytype = "", st_executive_code = "";
    int tax_typeflag = 0, confctr_kg_unit = 0;
    float fl_afterroundoff = 0, fl_roundoff = 0 ,pro_discount = 0 ,prod_disc =0;
    private ViewGroup lytCart, lyprod_disc,ly_prod_discount_typesale;
    LinearLayout ly_sgst, ly_taxindia, ly_taxoutside ,ly_descrptn ,ly_freeqnty;
    private Button btnAddToCart, btnMakePayment, btnFinish ,bt_desc ,btn_salecancel;
    private TextView tvProductSpinner, tvCodeSpinner, tvQtyTotal, tvNetTotal, tv_cgst_head, tv_totalkg,
            tvVat, tvGrandTotal, textView_withoutdiscount, tv_crlmt, tv_balance, tv_cr_balance, tv_cgst, tv_sgst, tv_vat;
    private EditText etQuantity, tvNetPrice, etFreeQuantity, etProductDiscount, edittext_discount,
            edittext_availbleqty, edt_sale_roundoff; // edt_barcode // edt_sales_vatpercentage
    private RecyclerView recyclerView;
    private RvCartAdapter adapter;

    private ErrorView errorView;
    private ProgressBar progressBar;
    private AppCompatSpinner spinnerCartUnit, sp_totaldisc, spinnermfgunit,sp_disctype;
    double less_discount = 0, dbl_cgstnew = 0, db_qnty_kgm = 0;
    int flag = 0;
    private ArrayList<CartItem> productList = new ArrayList<>();
    private ArrayList<CartItem> cartItems = new ArrayList<>();

    private ArrayList<CartItemCode> productCodeList = new ArrayList<>();

    ArrayList<Sales> quotationList = new ArrayList<>();

    private ArrayList<String> array_unitname = new ArrayList<>();
    private ArrayList<String> array_unitid = new ArrayList<>();
    private ArrayList<String> array_unitprice = new ArrayList<>();
    private ArrayList<String> array_unitconfactor = new ArrayList<>();
    private ArrayList<String> array_unitconfctrkg = new ArrayList<>();
    private ArrayList<String> array_unitwholesaleprice = new ArrayList<>();
    private ArrayList<String> array_mfg_prodid = new ArrayList<>();
    private ArrayList<String> array_mfg_date = new ArrayList<>();
    private ArrayList<String> array_totaldisc_tye = new ArrayList<>();
    private ArrayList<CartItem> productList_temp = new ArrayList<>();
    private ArrayList<CartItem> productList_temp1 = new ArrayList<>();
    ArrayList<Sales> saleList = new ArrayList<>();
    Sales salesPrint = new Sales();

    private CartItem select_Cart = null;
    private double paid_amount = 0, dbl_tax_value = 0, dbl_tax_amount = 0,
            dbl_withouttax_total = 0, dbl_withtax_total = 0, dblcr_balance = 0,
            dbl_outstndbalance = 0, dbl_crlimit = 0;

   // private String SALE_TYPE = SALE_RETAIL;
    private String SALE_TYPE = "";
    private MyDatabase myDatabase;
    private SessionValue sessionValue;
    private SessionAuth sessionAuth;
    private SwitchCompat switch_tax;

    private float pref_bonus = 0, cash_inhand = 0;

    private String PAYMENT_TYPE = "", st_disc = "0";

    int SAVE_STATUS = 0;

    int scan_cout = 0, sale_flag = 0;

    int addflag = 0, dialogflag = 0;

    double db_net_total = 0, db_discount = 0, db_disc_total = 0, db_vat_amount = 0, db_disc_total_grnd = 0,
            db_grand_total = 0, db_total_disc = 0, disc_perentge = 0, db_grand_total_new = 0, db_discpercentage = 0; // db_vat = 0,  db_withtax_total=0,

    LocationManager locationManager;

    public static SalesFragment newInstance(Shop shop,String st_vat_stat) {
        SalesFragment fragment = new SalesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SHOP, shop);
        st_vat_status = st_vat_stat;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            SELECTED_SHOP = (Shop) getArguments().getSerializable(ARG_SHOP);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);

        lytCart = (ViewGroup) view.findViewById(R.id.layout_sales_cart);
        ly_taxindia = view.findViewById(R.id.ly_tax_india);
        ly_taxoutside = view.findViewById(R.id.ly_tax_outside);
        ly_descrptn = view.findViewById(R.id.ly_descrptn);
        ly_freeqnty = view.findViewById(R.id.ly_freeqnty);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_sales_products);
        errorView = (ErrorView) view.findViewById(R.id.errorView_sales_products);
        btnAddToCart = (Button) view.findViewById(R.id.button_sales_addCart);
        btn_salecancel = (Button) view.findViewById(R.id.button_sales_cancel);
        etQuantity = (EditText) view.findViewById(R.id.EditText_sales_Qty);
        edittext_availbleqty = view.findViewById(R.id.edittext_availbleqty);
        tvProductSpinner = (TextView) view.findViewById(R.id.textView_sales_item);
        tvQtyTotal = (TextView) view.findViewById(R.id.textView_sales_qtyTotal);
        tvNetTotal = (TextView) view.findViewById(R.id.textView_sales_netTotal);
        tvVat = (TextView) view.findViewById(R.id.textView_sales_vat);
        tv_cgst = view.findViewById(R.id.textView_sales_cgst);
        tv_sgst = view.findViewById(R.id.textView_sales_sgst);
        tv_totalkg = view.findViewById(R.id.tv_totalkg);
        ly_sgst = view.findViewById(R.id.ly_sgst);
        //tv_cgst_head = view.findViewById(R.id.tv_cgst_head);
        lyprod_disc = view.findViewById(R.id.ly_prod_discount_sale);
        ly_prod_discount_typesale = view.findViewById(R.id.ly_prod_discount_typesale);
        tvNetPrice = (EditText) view.findViewById(R.id.textView_sales_unitPrice);
        //   edt_sales_vatpercentage = (EditText) view.findViewById(R.id.textView_sales_vatpercentage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_sales);
        btnMakePayment = (Button) view.findViewById(R.id.button_sales_makePayment);
        btnFinish = (Button) view.findViewById(R.id.button_sales_finish);
        tvCodeSpinner = (TextView) view.findViewById(R.id.textView_sales_code);
        tvGrandTotal = (TextView) view.findViewById(R.id.textView_grand_total);
        textView_withoutdiscount = (TextView) view.findViewById(R.id.textView_withoutdiscount);
        switch_tax = (SwitchCompat) view.findViewById(R.id.switch_tax);
        spinnerCartUnit = (AppCompatSpinner) view.findViewById(R.id.spinner_sales_orderUnit);
        spinnermfgunit = (AppCompatSpinner) view.findViewById(R.id.sp_sales_mfgdate);
        sp_totaldisc = view.findViewById(R.id.sp_totaldisc);
        sp_disctype =  (AppCompatSpinner) view.findViewById(R.id.sp_disctype);
        edittext_discount = (EditText) view.findViewById(R.id.edittext_discount);
        etFreeQuantity = (EditText) view.findViewById(R.id.edittext_free_qty);
        etProductDiscount = (EditText) view.findViewById(R.id.edittext_product_discount);
        tv_crlmt = view.findViewById(R.id.tv_crlmt);
        tv_balance = view.findViewById(R.id.tv_balance);
        tv_cr_balance = view.findViewById(R.id.tv_crbalance);
        edt_sale_roundoff = view.findViewById(R.id.edt_sale_roundoff);
        bt_desc = view.findViewById(R.id.button_sales_add_descrptn);

        edittext_discount.setText("0");
        array_totaldisc_tye.add("%");
        array_totaldisc_tye.add("Amnt");
        myDatabase = new MyDatabase(getContext());
        sessionValue = new SessionValue(getContext());
        sessionAuth = new SessionAuth(getContext());

        dayRegId = sessionValue.getDayRegisterId();

        pref_bonus = sessionAuth.getBonus();
        cash_inhand = sessionAuth.getCashinHand();


        CURRENCY = "" + sessionValue.getControllSettings().get(PREF_CURRENCY);
        VATPERCENTAGE = sessionValue.getControllSettings().get(PREF_VATPERCENT);

        switch_tax.setChecked(true);
        switch_tax.setText(R.string.discnt_inclsve);


        EXECUTIVE_ID = new SessionAuth(getContext()).getExecutiveId();

        callingActivity = getActivity().getIntent().getIntExtra(CALLING_ACTIVITY_KEY, 0);
        switch (callingActivity) {
            case ActivityConstants.ACTIVITY_SALES:
                // btnMakePayment.setVisibility(View.VISIBLE);
                break;
            case ActivityConstants.ACTIVITY_QUOTATION:

                sale_flag = 1002;
                btnMakePayment.setVisibility(View.GONE);
                break;
            default:
                btnMakePayment.setVisibility(View.GONE);
        }
        productList_temp.clear();
        cartItems.clear();
        try {
            productList_temp = myDatabase.getQuotationsProducttemp(Integer.parseInt(sessionValue.getselected_shopid()));
            Log.e("sizeee", "" + productList_temp.size());
            productList_temp1 = productList_temp;
        } catch (Exception e) {

        }

        if(callingActivity ==ActivityConstants.ACTIVITY_SALES ) {
            if (productList_temp.size() > 0) {
                //if(flag==0) {
                Log.e("if frst", "ok");
                cartItems = productList_temp;

                adapter = new RvCartAdapter(productList_temp, st_disc, new OnNotifyListener() {
                    @Override
                    public void onNotified() {
                        flag = 1;
                        setTotalPriceView();

                        if (adapter.getItemCount() == 0)
                            btnMakePayment.setEnabled(false);
                        else
                            btnMakePayment.setEnabled(true);
                    }
                });
                // }
            } else {

                adapter = new RvCartAdapter(cartItems, st_disc, new OnNotifyListener() {
                    @Override
                    public void onNotified() {

                        setTotalPriceView();

                        if (adapter.getItemCount() == 0)
                            btnMakePayment.setEnabled(false);
                        else
                            btnMakePayment.setEnabled(true);
                    }
                });
            }
        }else{
            productList_temp.clear();
            adapter = new RvCartAdapter(cartItems, st_disc, new OnNotifyListener() {
                @Override
                public void onNotified() {

                    setTotalPriceView();

                    if (adapter.getItemCount() == 0)
                        btnMakePayment.setEnabled(false);
                    else
                        btnMakePayment.setEnabled(true);
                }
            });
        }

        getsellingprice();
        getVanStockList();
        setRecyclerView();


        if (IS_GST_ENABLED) {
            ly_taxindia.setVisibility(View.GONE);
            ly_taxoutside.setVisibility(View.VISIBLE);
        } else {
            ly_taxindia.setVisibility(View.VISIBLE);
            ly_taxoutside.setVisibility(View.GONE);
        }
        if (IS_PRODUCT_DISOUNT_ENABLED) {
            lyprod_disc.setVisibility(View.VISIBLE);
            ly_prod_discount_typesale.setVisibility(View.VISIBLE);
        }
        else{
            lyprod_disc.setVisibility(View.GONE);
            ly_prod_discount_typesale.setVisibility(View.GONE);
        }

        if(IS_PRODUCT_DISOUNTTYPE_ENABLED){
            ly_prod_discount_typesale.setVisibility(View.VISIBLE);
        }
        else{
            ly_prod_discount_typesale.setVisibility(View.GONE);
        }
        if(IS_PRODUCT_DESCRIPTION_ENABLED){
            ly_descrptn.setVisibility(View.VISIBLE);
        }
        else{
            ly_descrptn.setVisibility(View.GONE);
        }

        if(IS_PRODUCT_FOC_ENABLED){
            ly_freeqnty.setVisibility(View.VISIBLE);
        }
        else{
            ly_freeqnty.setVisibility(View.GONE);
        }



        switch_tax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    switch_tax.setText(getContext().getText(R.string.discnt_inclsve));
                    tax_typeflag = 1;
                    lyprod_disc.setVisibility(View.GONE);
                    ly_prod_discount_typesale.setVisibility(View.GONE);
                } else {
                    switch_tax.setText(getContext().getText(R.string.discnt_exclsve));
                    tax_typeflag = 0;
                    lyprod_disc.setVisibility(View.VISIBLE);
                    ly_prod_discount_typesale.setVisibility(View.VISIBLE);

                }

            }
        });

        ArrayAdapter<String> discAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_totaldisc_tye);
        discAdapter.setDropDownViewResource(R.layout.spinner_list);
        sp_disctype.setAdapter(discAdapter);


        sp_disctype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //etProductDiscount.setText("");
                st_prod_disctype = sp_disctype.getSelectedItem().toString();
                Log.e("st_prod_disctype", "" + st_prod_disctype);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        edt_sale_roundoff.addTextChangedListener(new TextValidator(edt_sale_roundoff) {
            @Override
            public void validate(TextView textView, String text) {
                fl_afterroundoff = 0;
                fl_roundoff = 0;
                try {
                    if (edt_sale_roundoff.getText().toString().isEmpty()) {
                        float fl_grandtot = Float.parseFloat("" + db_grand_total);
                        fl_afterroundoff = fl_grandtot;
                        fl_roundoff = 0;
                        tvGrandTotal.setText("" + getAmount(fl_afterroundoff) + " " + CURRENCY);
                    } else {


                        fl_roundoff = Float.parseFloat(edt_sale_roundoff.getText().toString());
                        fl_roundoff = TextUtils.isEmpty(text) ? 0 : Float.valueOf(text);
                        Log.e("fl_roundoff", "" + fl_roundoff);
                        float fl_grandtot = Float.parseFloat("" + db_grand_total);
                        fl_afterroundoff = fl_grandtot + fl_roundoff;
                        tvGrandTotal.setText("" + getAmount(fl_afterroundoff) + " " + CURRENCY);

                    }

                } catch (NumberFormatException e) {

                    Log.v(TAG, "paid  balance Exception " + e.getMessage());
                }
            }
        });
        // Add to textWatcher


        etQuantity.addTextChangedListener(new TextValidator(etQuantity) {
            @Override
            public void validate(TextView textView, String qtyString) {


                try {
                    if (etQuantity.getText().toString().equals("Qty")) {
                        etQuantity.setText("");
                    }

                    String netamount = "";
                    double netAmount = 0;

                    etProductDiscount.setText("");

                    if (!TextUtils.isEmpty(qtyString) && select_Cart != null) {

                        float quantity = TextUtils.isEmpty(qtyString) ? 0 : Float.valueOf(qtyString);
                        //   tvNetPrice.setText(getAmount(getWithoutTaxPrice(select_Cart.getRetailPrice(), select_Cart.getTax())));
                        float tot_qty = quantity * Float.parseFloat(unit_confctr);
                        Log.e("tot_qty in", "" + tot_qty);

                        netAmount = roundTwoDecimals(select_Cart.getProductPrice());
                        if (select_Cart.getProductPrice() > 0) {
                            tvNetPrice.setText("" + netAmount);
                        } else {
                            tvNetPrice.setText("");
                        }

                        tvQtyTotal.setText(getAmount(select_Cart.getProductPrice() * quantity));

                    } else {
                        tvQtyTotal.setText(getAmount(0.0f));
                    }

                } catch (Exception ignored) {

                }
            }
        });


        etProductDiscount.addTextChangedListener(new TextValidator(etProductDiscount) {
            @Override
            public void validate(TextView textView, String product_discount) {
                try {

                    if (!TextUtils.isEmpty(product_discount) && select_Cart != null) {

//                        int pro_discount = TextUtils.isEmpty(product_discount) ? 0 : Integer.valueOf(product_discount);
                        pro_discount = TextUtils.isEmpty(product_discount) ? 0 : Float.valueOf(product_discount);
                        int quatity = TextUtils.isEmpty(etQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etQuantity.getText().toString().trim());
                        double netprice = Double.parseDouble(getAmount(select_Cart.getProductPrice() * quatity));
                        disc_perentge =0;
                        if (st_prod_disctype.equals("%")) {


                            double discnt = netprice * pro_discount / 100;
                            disc_perentge = pro_discount;
                            // pro_discount = (float) discnt;
                            pro_discount = (float) discnt/quatity;
                            Log.e("db_prodtotal_disc hhh", "" + pro_discount);
                            prod_disc = pro_discount;
                        }
                        else{
                            prod_disc = pro_discount;
                        }
//                        else{
//                            pro_discount = pro_discount * quatity;
//                        }
                        pro_discount = pro_discount * quatity;
                        double discount_price = netprice - pro_discount;

                        tvQtyTotal.setText("" + getAmount(discount_price));

                    } else {

                        int quatity = TextUtils.isEmpty(etQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etQuantity.getText().toString().trim());
                        tvQtyTotal.setText(getAmount(select_Cart.getProductPrice() * quatity));
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
                Log.e("discount hr", "" + disc);
                // try {
                db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());
//                } catch (Exception e) {
//
//                }
                double discount_tot = 0;
                double applicable_total = 0;
                if (disc.equals("")) {
                    disc = "0";
                }
                //  st_disc = ""+disc;
                db_discpercentage = Double.parseDouble(disc);
                if (st_totaldisctype.equals("%")) {

                    double db_disc_tot = 0;
                    // db_disc_tot = adapter.getTaxTotal("TAX_EXCLUSIVE");

                    db_disc_tot = (adapter.getWithoutTaxTotal() - adapter.getDiscountTotal()) + adapter.getTaxTotal("TAX_EXCLUSIVE");
                    Log.e("db_disc_total", "" + (adapter.getWithoutTaxTotal() - adapter.getDiscountTotal()));
                    Log.e("db_discount", "" + db_discount);
                    Log.e("db_disc_totalllll", "" + db_disc_tot);
                    Log.e("db_vat_amount", "" + db_vat_amount);
                    db_disc_tot = db_disc_tot ;
                    double discnt = db_disc_tot * db_discount / 100;
                    disc_perentge = db_discount;
                    Log.e("db_total_disc hhh", "" + db_total_disc);
                    disc = "" + discnt;

                }


                //  try {
                if (disc.isEmpty()) {
                    disc = "0";
                    st_disc = "0";
                }

                less_discount = Double.parseDouble(disc);
                Log.e("less_discount", "" + less_discount);
                setTotalPriceView();
//                }catch (Exception e){
//
//                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnAddToCart.setOnClickListener(this);
        btnMakePayment.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        bt_desc.setOnClickListener(this);
        btn_salecancel.setOnClickListener(this);

        hideSoftKeyboard();

        return view;

    }

    private void set_cartproducts() {
        productList_temp.clear();
        cartItems.clear();

        try {
            productList_temp = myDatabase.getQuotationsProducttemp(Integer.parseInt(sessionValue.getselected_shopid()));
            Log.e("sizeee", "" + productList_temp.size());
        } catch (Exception e) {

        }


        if (productList_temp.size() > 0) {
            //if(flag==0) {
            Log.e("if frst", "ok");
            cartItems = productList_temp;

            adapter = new RvCartAdapter(cartItems, st_disc, new OnNotifyListener() {
                @Override
                public void onNotified() {
                    flag = 1;
                    setTotalPriceView();

                    if (adapter.getItemCount() == 0)
                        btnMakePayment.setEnabled(false);
                    else
                        btnMakePayment.setEnabled(true);
                }
            });
            setRecyclerView();
            // }
        }
    }

    private void setShopTypeSpinner() {

        //order type spinner
        String st_unit = "", st_unitid = "", st_unitprice = "", st_confactor = "", st_confactorkg = "";
        try {
            JSONArray arr = new JSONArray(select_Cart.getUnitslist());
            Log.e("Unit lst", "" + select_Cart.getUnitslist());
            array_unitid.clear();
            array_unitname.clear();
            array_unitprice.clear();
            array_unitconfactor.clear();
            array_unitconfctrkg.clear();
            array_unitwholesaleprice.clear();

            confctr_kgm = "";
            String id = "";
            for (int i = 0; i < arr.length(); i++) {

                Units units = new Units();

                JSONObject jObj = arr.getJSONObject(i);
                String name = jObj.getString("unitName");
                id = jObj.getString("unitId");
                String price = jObj.getString("unitPrice");
                String confctr = jObj.getString("con_factor");
                String wholesale_unitprice = jObj.getString("unitWholesalePrice");

                if (id.equals("10")) {
                    confctr_kgm = jObj.getString("unit_confactorkg");
                    kgm_unitprice = jObj.getString("unitPrice");
                }
//
//
//                Log.e("confctr_kg selected", "" + confctr_kgm);
//                Log.e("Unit selected", "" + name);
//                Log.e("Unit iddd", "" + select_Cart.getSale_unitid());
                //if(id.equals("10")){
                confctr_kg_unit = TextUtils.isEmpty(confctr) ? 0 : Integer.valueOf(confctr);
                ;
                array_unitname.add(name);
                array_unitid.add(id);
                array_unitconfactor.add(confctr);
                array_unitprice.add(price);
                array_unitconfctrkg.add(confctr_kgm);
                array_unitwholesaleprice.add(wholesale_unitprice);
                //  }

//                if(id.equals(""+select_Cart.getSale_unitid())){
//                    st_unit = name;
//                    st_confactor = confctr;
//                    st_unitprice = price;
//                    st_unitid = id;
//                    st_confactorkg = confctr_kgm;
//                }
//                else{

//                    array_unitname.add(name);
//                    array_unitid.add(id);
//                    array_unitconfactor.add(confctr);
//                    array_unitprice.add(price);
//                    array_unitconfctrkg.add(confctr_kgm);
//
//                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (array_unitid.size() > 1) {
            if (!array_unitid.get(0).equals("" + select_Cart.getSale_unitid())) {
                String st_unitname = array_unitname.get(0);
                String st_confactr = array_unitconfactor.get(0);
                String st_unitprce = array_unitprice.get(0);
                String st_unit_id = array_unitid.get(0);
                String st_unit_wholesaleprce = array_unitwholesaleprice.get(0);
                array_unitname.remove(0);
                array_unitid.remove(0);
                array_unitconfactor.remove(0);
                array_unitprice.remove(0);
                array_unitwholesaleprice.remove(0);

                array_unitname.add(st_unitname);
                array_unitid.add(st_unit_id);
                array_unitconfactor.add(st_confactr);
                array_unitprice.add(st_unitprce);
                array_unitwholesaleprice.add(st_unit_wholesaleprce);
            }
        }


        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_unitname);
        //ArrayAdapter.createFromResource(getActivity(), array_unitname, R.layout.spinner_background_dark); //R.array.cart_type
        orderTypeAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerCartUnit.setAdapter(orderTypeAdapter);


        spinnerCartUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                unit_id = array_unitid.get(position);
                unit_name = array_unitname.get(position);
                unit_confctr = array_unitconfactor.get(position);
                unit_price = array_unitprice.get(position);
                confctr_kg = array_unitconfctrkg.get(position);
                unit_wholesale_prce = array_unitwholesaleprice.get(position);
                Log.e("Unit", "" + unit_name + "/" + unit_id);
                Log.e("confctr", "" + unit_confctr + "/" + unit_price);
                Log.e("stockqty", "" + select_Cart.getStockQuantity());
                Log.e("confctr_kg", "" + confctr_kg_unit);
                try {
                    if (unit_id.equals("10")) {

                        confctr_kg = array_unitconfctrkg.get(position);
                        confctr_kg_unit = TextUtils.isEmpty(confctr_kg) ? 0 : Integer.valueOf(confctr_kg);
                    }
                    available_qty = select_Cart.getStockQuantity() / Integer.parseInt(unit_confctr);
                    int bal_qty = select_Cart.getStockQuantity() % Integer.parseInt(unit_confctr);

                    edittext_availbleqty.setText("" + available_qty + "-" + bal_qty);
                    //setCartProduct();
                    etQuantity.setFocusable(true);
                } catch (Exception e) {

                }

                setCartProduct();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        try {
//            JSONArray arra = new JSONArray(select_Cart.getMfglist());
//            array_mfg_prodid.clear();
//            array_mfg_date.clear();
//
//
//            for (int i = 0; i < arra.length(); i++) {
//
//                JSONObject jObj = arra.getJSONObject(i);
//
//                String prod_id = jObj.getString("prod_id");
//                String mfg_date = jObj.getString("mfgdate");
//
//                Log.e("mfg_date selected", "" + mfg_date);
//                array_mfg_date.add(mfg_date);
//                array_mfg_prodid.add(prod_id);
//
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ArrayAdapter<String> ordermfg = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_mfg_date);
//        //ArrayAdapter.createFromResource(getActivity(), array_unitname, R.layout.spinner_background_dark); //R.array.cart_type
//        orderTypeAdapter.setDropDownViewResource(R.layout.spinner_list);
//        spinnermfgunit.setAdapter(ordermfg);
//
//        spinnermfgunit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                mfg_prodid = array_mfg_prodid.get(position);
//                 mfg_date = array_mfg_date.get(position);
//                Log.e("mfg_date",""+mfg_date);
//
//
//
//              //  setCartProduct();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        tvNetPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String unitprice = tvNetPrice.getText().toString().trim();
                double unitpr = 0;

                String qtyString = etQuantity.getText().toString().trim();

                etProductDiscount.setText("");

                if (!unitprice.isEmpty()) {

                    unitpr = Double.parseDouble(unitprice);
                    float quantity = TextUtils.isEmpty(qtyString) ? 0 : Float.valueOf(qtyString);
                    tvQtyTotal.setText(getAmount(unitpr * quantity));

                    select_Cart.setProductPrice((float) unitpr);

                } else {

                    unitpr = 0;
                    float quantity = TextUtils.isEmpty(qtyString) ? 0 : Float.valueOf(qtyString);
                    tvQtyTotal.setText(getAmount(unitpr * quantity));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    public String getDiscount() {

        String disc = "";
        disc = "" + edittext_discount.getText().toString().trim();

        if (disc.isEmpty()) {
            disc = "0";
        }
        return disc;
    }

    private void getsellingprice() {

        boolean value = myDatabase.isExistCustomerProduct(41, 7);
        printLog("selling price", "boolean:" + value);

    }

    //    Load Stock All List from Server
    private void getVanStockList() {

        setCartProgressBar(true);

        productList.clear();
        productCodeList.clear();

        if (sale_flag == 1002) {

            Log.e("inside If", "/" + ActivityConstants.ACTIVITY_QUOTATION);

            productList.addAll(myDatabase.getAllMasterStock());
            productCodeList.addAll(myDatabase.getAllMasterStockCode());

        } else {
            Log.e("inside else", "/ sale");

            productList.addAll(myDatabase.getAllStock());
            productCodeList.addAll(myDatabase.getAllStockCode());
        }

        if (productList.isEmpty())
            setCartErrorView("No Stock");
        else

            setSearchableProductList(productList, productCodeList);
    }

    private void setRecyclerView() {

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(itemAnimator);

        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .showLastDivider()
//                .color(getResources().getColor(R.color.divider))
                .build());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter.notifyDataSetChanged();


        setTotalPriceView();

    }

    private void setTotalPriceView() {
        String st_taxtype = "";
        if (tax_typeflag == 0) {
            st_taxtype = "TAX_EXCLUSIVE";
        } else {
            st_taxtype = "TAX_INCLUSIVE";
        }
        //  String net = String.valueOf("" + getAmount(adapter.getNetTotal()) + " " + CURRENCY);
        if(callingActivity == ActivityConstants.ACTIVITY_SALES) {
            db_net_total = myDatabase.get_net_totalfrom_temp(Integer.parseInt(sessionValue.getselected_shopid()));
        }
        else{
            db_net_total =0;
            db_net_total = adapter.getNetTotal();
        }
        String net = getAmount(db_net_total);
        String vat = " 00 " + CURRENCY;
        String cgst = "", sgst = "";
        if (adapter.getTaxTotal(st_taxtype) != 0)
            vat = "" + getAmount(adapter.getTaxTotal(st_taxtype)) + " " + CURRENCY;

        db_vat_amount = Double.parseDouble("" + (adapter.getTaxTotal(st_taxtype)));
        Log.e("db_vat_amount hrct", "" + db_vat_amount);

        String grandTotal = String.valueOf("GRAND TOTAL : " + getAmount(adapter.getGrandTotal()) + " " + CURRENCY);

        textView_withoutdiscount.setText("Gr. Total  : " + net);


        //db_net_total = adapter.getNetTotal();

        String s = "" + db_net_total;

        double fraction = db_net_total % 1;//Give you 0.75 as remainder
        //  int integer=(int)value;//give you 2 fraction part will be removed
        Log.e("fraction", "" + fraction);

        db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());

        db_disc_total = db_net_total; // - db_discount;
        double db_vatpercntg = 0;
        if (!VATPERCENTAGE.equals("")) {
            db_vatpercntg = Double.parseDouble(VATPERCENTAGE);
        }

        db_disc_total_grnd = 0;
        //if(adapter.getApplicableTotal()>0 && less_discount>0) {
        if (less_discount > 0) {
            Log.e("less_discount inside", "" + less_discount);

            // db_disc_tot = (adapter.getWithoutTaxTotal() - adapter.getDiscountTotal())+adapter.getTaxTotal("TAX_EXCLUSIVE");
            db_disc_total_grnd = ((adapter.getWithoutTaxTotal() - adapter.getDiscountTotal()) + adapter.getTaxTotal("TAX_EXCLUSIVE")) - less_discount;

            db_grand_total = db_disc_total_grnd;
            Log.e("db_grand_total inside", "" + db_grand_total);
        } else {
            // Log.e("db_disc_total frst", "" + db_disc_total);
            // Log.e("db_vat_amount new", "" + db_vat_amount);
            Log.e("db_vat_amount hr", "" + getAmount(db_vat_amount / 2));
            db_grand_total = db_disc_total + db_vat_amount;
            double dblcgst = 0;
            if (db_vat_amount > 0) {
                dblcgst = TextUtils.isEmpty(getAmount(db_vat_amount / 2).trim()) ? 0 : Double.valueOf(getAmount(db_vat_amount / 2).trim());
            }
            db_grand_total = db_disc_total + dblcgst + dblcgst;
            db_grand_total_new = db_grand_total;
        }


        tvNetTotal.setText("" + getAmount(db_disc_total) + " " + CURRENCY);
        //    tv_totalkg.setText(""+adapter.get_totalkg());
        tv_totalkg.setText("" + getAmount(myDatabase.get_cart_total_kg(Integer.parseInt(sessionValue.getselected_shopid()))));

        try {
            Log.e("getState_id()", "" + SELECTED_SHOP.getState_id());
            Log.e("group()", "" + SELECTED_SHOP.getGroup());
            Log.e("route()", "" + SELECTED_SHOP.getRoute());
//            if (SELECTED_SHOP.getState_id() > 1) {
//                tv_cgst.setText("IGST  : " + db_vat_amount);
//                tv_sgst.setText("");
//            } else {
            cgst = "" + getAmount(db_vat_amount / 2) + " " + CURRENCY;
            //dbl_cgstnew = getAmount(db_vat_amount / 2);
            db_vat_amount = ((db_vat_amount / 2) + (db_vat_amount / 2));
            Log.e("db_vat_amount next", "" + getAmount(db_vat_amount));
            tv_cgst.setText("CGST  : " + cgst);
            tv_sgst.setText("SGST  : " + cgst);

            ///////////////////////////////////////////new ////////////////////////////////
            ArrayList<CartItem> arry_crt = new ArrayList<CartItem>();
            arry_crt = adapter.getCartItems();
            double total_taxamnt_cgst = 0;
            for (CartItem c : arry_crt) {
                Log.e("getSgst next", "" + c.getSgst());
                double dblsgst = TextUtils.isEmpty(getAmount(c.getSgst()).trim()) ? 0 : Double.valueOf(getAmount(c.getSgst()).trim());
                total_taxamnt_cgst = total_taxamnt_cgst + (dblsgst);
            }

            Log.e("total_taxamnt next", "" + total_taxamnt_cgst);
            Log.e("total_taxamnt next", "" + getAmount(total_taxamnt_cgst));



            if(st_vat_status.equals("No Vat")){
                ly_taxindia.setVisibility(View.GONE);
                ly_taxoutside.setVisibility(View.GONE);
            }
            else{
                if (IS_GST_ENABLED) {
                    ly_taxindia.setVisibility(View.GONE);
                    ly_taxoutside.setVisibility(View.VISIBLE);
                    tv_cgst.setText("CGST  : " + getAmount(total_taxamnt_cgst));
                    tv_sgst.setText("SGST  : " + getAmount(total_taxamnt_cgst));
                } else {
                    ly_taxindia.setVisibility(View.VISIBLE);
                    ly_taxoutside.setVisibility(View.GONE);
                    tvVat.setText("VAT  : " + getAmount(total_taxamnt_cgst + total_taxamnt_cgst));
                }
            }


            db_grand_total = 0;
            if(st_vat_status.equals("No Vat")){
                total_taxamnt_cgst =0;
            }
            if (db_disc_total_grnd > 0) {
                db_grand_total = db_disc_total_grnd + total_taxamnt_cgst + total_taxamnt_cgst;
            } else {

                db_grand_total = db_disc_total + total_taxamnt_cgst + total_taxamnt_cgst;
            }

            db_grand_total_new = db_grand_total;
            db_vat_amount = total_taxamnt_cgst + total_taxamnt_cgst;
            // }
        } catch (Exception e) {

        }

        Log.e("db_vat_amount frst", "" + db_vat_amount);
        double cgst_dbl = TextUtils.isEmpty("" + getAmount(db_vat_amount / 2)) ? 0 : Double.valueOf(getAmount(db_vat_amount / 2));
        db_vat_amount = (cgst_dbl) + cgst_dbl;

        tvGrandTotal.setText("" + getAmount(db_grand_total) + " " + CURRENCY);

        try {
            dblcr_balance = dblcr_balance - db_grand_total_new;
            dblcr_balance = roundTwoDecimals(dblcr_balance);
            tv_cr_balance.setText("Balance : " + getAmount(dbl_outstndbalance + db_grand_total_new));
            double dbl_bal = dbl_crlimit - (dbl_outstndbalance + db_grand_total_new);
            if (dbl_bal < 0) {
                tv_balance.setTextColor(getResources().getColor(R.color.colorRed));
                //  Toast.makeText(getActivity(),"Insufficient Creditlimit..!",Toast.LENGTH_LONG).show();
            } else {
                tv_balance.setTextColor(getResources().getColor(R.color.colorBlack));
            }
            tv_balance.setText("Credit Bal  : " + getAmount(dbl_crlimit - (dbl_outstndbalance + db_grand_total_new)));
        } catch (Exception e) {
            Log.e("set total priceview bal", "catch");
        }
        dbl_withouttax_total = adapter.getWithoutTaxTotal(); // Double.parseDouble(getAmount(adapter.getNetTotal()));
        dbl_tax_amount = adapter.getTaxTotal(st_taxtype);
        dbl_withtax_total = adapter.getGrandTotal();

        tvNetTotal.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvNetTotal.setSelected(true);


    }

    private void setSearchableProductList(final ArrayList<CartItem> list, final ArrayList<CartItemCode> listCode) {

        setCartProgressBar(false);
        errorView.setVisibility(View.GONE);

        final CartSpinnerDialogNew spinnerCart = new CartSpinnerDialogNew(getActivity(), list, listCode, "Select Product", R.style.DialogAnimations_SmileWindow);// With 	Animation

        tvProductSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click st", "" + spinnerCart.getclick_status());
                Log.e("click st", "SALE_TYPE" + SALE_TYPE);

                if (spinnerCart.getclick_status() == 0) {
                    spinnerCart.showSpinerDialog();
                }

            }
        });

        tvCodeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinnerCart.showCodeSpinerDialog();
            }
        });

        spinnerCart.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(Object item, int position) {

                select_Cart = (CartItem) item;//flagNoExtractUi
                dblcr_balance = 0;
                dbl_outstndbalance = SELECTED_SHOP.getOutStandingBalance();
                dbl_crlimit = SELECTED_SHOP.getCreditLimit();
                Log.e("Selected Vat no", "" + SELECTED_SHOP.getVatNumber());
                double total_crlmt = 0;
                try {
                    total_crlmt = Double.parseDouble(SELECTED_SHOP.getCreditlimit_register()) + SELECTED_SHOP.getOutStandingBalance();
                    dblcr_balance = SELECTED_SHOP.getCreditLimit() - SELECTED_SHOP.getOutStandingBalance();
                } catch (Exception e) {

                }
                Log.e("total_crlmt Vat no", "" + total_crlmt);
                Log.e("getState_id haris", "" + SELECTED_SHOP.getState_id());
                tv_crlmt.setText("Credit Limit : " + roundTwoDecimals(SELECTED_SHOP.getCreditLimit()));
                tv_cr_balance.setText("Balance : " + roundTwoDecimals(SELECTED_SHOP.getOutStandingBalance()));
                tv_balance.setText("Credit Balance  : " + roundTwoDecimals(dblcr_balance));
                // ****************************************************************************************

                addflag = 0;


                // if (addflag == 0) {
                Log.e("Add cart", "inside");

                if (myDatabase.isExistCustomerProduct(SELECTED_SHOP.getShopId(), select_Cart.getProductId())) {
                    double p = myDatabase.getCustomerProductPrice(SELECTED_SHOP.getShopId(), select_Cart.getProductId());
                    select_Cart.setRetailPrice((float) p);
                    select_Cart.setWholeSalePrice((float) p);

                    Log.e("Retail Price", "" + select_Cart.getRetailPrice());
                    Log.e("Wholesale Price", "" + select_Cart.getWholeSalePrice());
                }

                setShopTypeSpinner();
                setCartProduct();

                //   addToCart();
//                } else {
//
//                    if (dialogflag == 0) {
//
//                        dialogflag = 1;
//                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                        alertDialog.setMessage("Product already added ! Please edit from list if needed");
//
//                        // Setting OK Button
//                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Write your code here to execute after dialog closed
//
//                                dialogflag = 0;
//                                alertDialog.dismiss();
//                                refreshProductType();
//                            }
//                        });
//
//                        // Showing Alert Message
//                        alertDialog.show();
//
//                    }
//
//                }


            }
        });

        setCartProgressBar(false);
        lytCart.setVisibility(View.VISIBLE);

    }


    private void setCartProduct() {

        if (select_Cart == null)
            return;

        etQuantity.setText("");
        //  etQuantity.requestFocus();
        etQuantity.setFocusableInTouchMode(true);
        etQuantity.requestFocus();

        try {

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(etQuantity, InputMethodManager.SHOW_IMPLICIT);

            //double productPrice = select_Cart.getRetailPrice();
            double productPrice = 0;
            if(SALE_TYPE.equals("WholeSale")){
                Log.e("wholesaleprce ",""+unit_wholesale_prce);
                 productPrice = Double.parseDouble(unit_wholesale_prce);
            }
            else {
                //haris added on 17-12-21
                Log.e("retailprce ",""+unit_price);
                 productPrice = Double.parseDouble(unit_price);
            }

            int quantity = TextUtils.isEmpty(etQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etQuantity.getText().toString().trim());
            Log.e("QTY hr", "" + quantity);
            if (spinnerCartUnit.getSelectedItem().toString().equals(PRODUCT_UNIT_CASE))
                quantity = quantity * select_Cart.getPiecepercart();

            Log.e("QTY 2", "" + quantity);
            double salePrice = getSalePrice(productPrice, select_Cart.getTax(), "TAX_EXCLUSIVE");
            Log.e("SALE Price", "" + salePrice);

            double netPrice = getWithoutTaxPrice(productPrice, select_Cart.getTax());
            // netPrice=roundTwoDecimals(netPrice);
            Log.e("NET Price", "" + netPrice);

            select_Cart.setNetPrice(netPrice);

            if (unit_name.equals(PRODUCT_UNIT_CASE)) {
                netPrice = netPrice * select_Cart.getPiecepercart();
                select_Cart.setNetPrice(netPrice);
            }

            Log.e("check", "1");
            select_Cart.setSalePrice(salePrice);


            select_Cart.setProductPrice(productPrice);

            //  select_Cart.setUnitId(unit_id);
            //unit_name = "Case";
            select_Cart.setUnitselected(unit_name);
            Log.e("check", "2");
            tvQtyTotal.setText(getAmount(select_Cart.getNetPrice() * quantity));

            Log.e("check", "3" + select_Cart.getNetPrice());

            //      tvNetPrice.setText(""+getAmount(select_Cart.getNetPrice()));

            if (select_Cart.getNetPrice() > 0) {

                tvNetPrice.setText("" + select_Cart.getNetPrice());
            } else {
                tvNetPrice.setText("");
            }


            Log.e("check", "4");

            tvProductSpinner.setTag(select_Cart);
            tvCodeSpinner.setTag(select_Cart);

            tvProductSpinner.setText(select_Cart.getProductName());
            tvCodeSpinner.setText(select_Cart.getProductCode());

        } catch (Exception e) {
            Log.e("setcart error", "" + e.getMessage());
        }

    }

    private double roundTwoDecimals(double netPrice) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(netPrice));
    }

    private boolean validatePlaceOrder() {

        double saleCredit = myDatabase.getCustomerSaleCreditedAmount(SELECTED_SHOP.getShopId());
        double collection = myDatabase.getReceiptCollectionAmount(SELECTED_SHOP.getShopId());

        double maxCredit = SELECTED_SHOP.getCreditLimit() - saleCredit + collection;

        boolean status = false;

        if (SELECTED_SHOP == null) {
            Toast.makeText(getContext(), "Please Select Customer", Toast.LENGTH_SHORT).show();
            status = false;
        } else if (adapter.getCartItems().isEmpty()) {
            Toast.makeText(getContext(), SELECTED_SHOP.getShopName() + ",s Cart is Empty", Toast.LENGTH_SHORT).show();
            status = false;

        } else if (db_grand_total == 0.0) {
            Toast.makeText(getContext(), "Total Amount is 0.0", Toast.LENGTH_SHORT).show();
            status = false;

        } else if (TextUtils.isEmpty(PAYMENT_TYPE)) {
            Toast.makeText(getContext(), "Please Select payment type", Toast.LENGTH_SHORT).show();
            status = false;

        } else if (PAYMENT_TYPE.equals("Credit") && adapter.getGrandTotal() > maxCredit) {

            if (SELECTED_SHOP.getCreditlimit_register().equals("0")) {
                status = true;
            } else {
                Toast.makeText(getContext(), "Insufficient Credit Limit", Toast.LENGTH_SHORT).show();
                status = false;
            }

        } else
            status = true;

        return status;
    }

    //        method can pass sale data to local db class
    private void saveToLocalSales() {
        //Log.e("getDiscountTotal()",""+adapter.getDiscountTotal());

        db_grand_total = db_grand_total + fl_roundoff;
        //db_grand_total =(dbl_withouttax_total-adapter.getDiscountTotal())+dbl_tax_amount;
        // final String strDate = getDateTime();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);

        final String strDate = thisDate;
        Sales sales = new Sales();

        sales.setCustomerId(SELECTED_SHOP.getShopId());
        sales.setShopname(SELECTED_SHOP.getShopName());
        sales.setShopcode(SELECTED_SHOP.getShopCode());
        sales.setDate(strDate);
        sales.setTotal(db_grand_total);
        // sales.setTotal(adapter.getNetTotal());
        sales.setSaleType(SALE_TYPE);
        sales.setPaid(paid_amount);
        sales.setCartItems(adapter.getCartItems());
        sales.setInvoiceCode("");
        sales.setUploadStatus("N");

        sales.setRoundoff_value(Float.parseFloat("" + fl_roundoff));
        //sales.setCartItems(adapter.getCartItems());
        //////////////////////
        sales.setTaxPercentage(dbl_tax_value);
        //  sales.setTaxAmount(dbl_tax_amount);

        //getDiscountAmount,getWithoutTaxTotal
        sales.setWithoutTaxTotal(dbl_withouttax_total - adapter.getDiscountTotal());
        //  sales.setWithTaxTotal(dbl_withtax_total);

        sales.setWithoutDiscount(db_net_total);

        db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());
        if (db_discount == 0) {
            sales.setDiscount((float) adapter.getDiscountTotal());

        } else {
//            sales.setDiscount((float) db_discount);
            sales.setDiscount((float) db_total_disc);
        }
        if (adapter.getDiscountTotal() > 0)

            sales.setDiscount_value((float) adapter.getDiscountTotal() + (float) less_discount);
        else {
            sales.setDiscount_value((float) less_discount);
        }
        sales.setDiscount((float) less_discount);
        if (getAmount(sales.getDiscount()).equals(".00")) {
            sales.setDiscount(0);
        }
        ////
        db_disc_total = dbl_withouttax_total - less_discount;
        //sales.setDiscountAmount(db_disc_total);
        /////////////////////////////////////////////////////////////////////////////
        sales.setDiscountAmount((dbl_withouttax_total - adapter.getDiscountTotal()) - less_discount);
        ////////////////////////////////////////////////////////////////////////////////


        //haris doubt
        sales.setTaxPercentage(0);
        /////////////
        sales.setTaxAmount(db_vat_amount);
        sales.setWithTaxTotal(db_grand_total);

        sales.setPayment_type(st_paytype);

        if (tax_typeflag == 1) {
            sales.setTax_type("inclusive");
        }
        if (tax_typeflag == 1) {
            sales.setTax_type("exclusive");
        }
        sales.setTotalkg(adapter.get_totalkg());
        sales.setDiscount_total_amount(db_discpercentage);
        st_executive_code = "";
        st_executive_code = sessionValue.getexecutivecode();
        // st_executive_code = sessionValue.getPrefRoutecode();
        Log.e("getPrefRoutecode", "" + st_executive_code);
        invoiceNumber = generateNewNumber(sessionValue.getInvoiceCode(st_executive_code));
        int inv_no = TextUtils.isEmpty(invoiceNumber.toString().trim()) ? 0 : Integer.valueOf(invoiceNumber.toString().trim());
        Log.e("Invoice Number", "/" + invoiceNumber);
        Log.e("inv_no ", "/" + inv_no);
        if (invoiceNumber.length() == 1) {
            invoiceNumber = "00" + invoiceNumber;
        }
        if (invoiceNumber.length() == 2) {
            invoiceNumber = "0" + invoiceNumber;
        }
        sales.setVat_status(st_vat_status);
        //sales.setInvoiceCode(SELECTED_SHOP.getRouteCode() + invoiceNumber);
        sales.setInvoiceCode(st_executive_code + invoiceNumber);
        sales.setInvoice_no(inv_no);
        sales.setTotal_discount_type(st_totaldisctype);
        saleList.clear();

        saleList.add(sales);

        long insertStatus = myDatabase.insertSale(sales);

        if (insertStatus != -1) {

            //sessionValue.storeInvoiceCode(SELECTED_SHOP.getRouteCode(), invoiceNumber);
            sessionValue.storeInvoiceCode(st_executive_code, invoiceNumber);

            for (CartItem c : sales.getCartItems()) {
                myDatabase.updateStock(c, REQ_SALE_TYPE);
            }
            saleList.clear();
            saleList = myDatabase.getAllSales();


            if (sales.getPayment_type().equals("CreditSale")) {


//        added by haris 27-08-2020
                Log.e("getPaid hr", "" + sales.getPaid());
                Transaction t = new Transaction(sales.getCustomerId(), sales.getPaid(), db_grand_total);
                if (myDatabase.updateCustomerBalance(t)) {
                    Toast.makeText(getActivity(), "Successfully", Toast.LENGTH_SHORT).show();

                    SAVE_STATUS = 1;

                    salesPrint = sales;

                    if (IS_SALE_ONLINE) {
                        if (isNetworkConnected()) {

                            place_sale(sales);
                        }
                    } else {

                        Intent intent = new Intent(getActivity(), PreviewActivity.class);
                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALES);

                        intent.putExtra(SALES_VALUE_KEY, sales);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                        startActivity(intent);
                        getActivity().finish();
                    }


                } else {
                    Toast.makeText(getContext(), "Customer data update failure", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Transaction t = new Transaction(SELECTED_SHOP.getShopId(), sales.getWithoutTaxTotal(), 0);
                Transaction t = new Transaction(sales.getCustomerId(), sales.getPaid(), db_grand_total);
                if (myDatabase.updateCustomerBalance(t)) {
                    Toast.makeText(getActivity(), "Successfully", Toast.LENGTH_SHORT).show();
                    float paid = (float) sales.getPaid();
                    cash_inhand = sessionAuth.getCashinHand();
                    Log.e("paid", "" + paid);
                    Log.e("cash_inhand", "" + cash_inhand);
                    float cash = cash_inhand + paid;
                    sessionAuth.updateCashinHand(cash);

                    SAVE_STATUS = 1;

                    salesPrint = sales;


                    if (IS_SALE_ONLINE) {
                        if (isNetworkConnected()) {


                            place_sale(sales);
                        }
                    } else {

                        Intent intent = new Intent(getActivity(), PreviewActivity.class);
                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALES);

                        /// intent.putExtra(SALES_TOTAL_DISCOUNT, sales);
                        intent.putExtra(SALES_VALUE_KEY, sales);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                        startActivity(intent);
                        getActivity().finish();

                    }
                }

            }

        } else {

            Toast.makeText(getActivity(), "Insertion Failed", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validateQuotation() {

        boolean status = false;

        if (SELECTED_SHOP == null) {
            Toast.makeText(getContext(), "Please Select Customer", Toast.LENGTH_SHORT).show();
            status = false;
        } else if (adapter.getCartItems().isEmpty()) {
            Toast.makeText(getContext(), SELECTED_SHOP.getShopName() + "'s Cart is Empty", Toast.LENGTH_SHORT).show();
            status = false;

        } else
            status = true;

        return status;
    }

    //        method can pass quotation data to local db class
    private void saveToLocalQuotation() {


        //////////////////////////////////////////////////////////////////////////////////////////

        db_grand_total = db_grand_total + fl_roundoff;
        //db_grand_total =(dbl_withouttax_total-adapter.getDiscountTotal())+dbl_tax_amount;
        // final String strDate = getDateTime();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);

        final String strDate = thisDate;
        Sales sales = new Sales();

        sales.setCustomerId(SELECTED_SHOP.getShopId());
        sales.setCustomerName(SELECTED_SHOP.getShopName());
        sales.setShopname(SELECTED_SHOP.getShopName());
        sales.setShopcode(SELECTED_SHOP.getShopCode());
        sales.setDate(strDate);
        sales.setTotal(db_grand_total);
        // sales.setTotal(adapter.getNetTotal());
        sales.setSaleType(SALE_TYPE);
        sales.setPaid(0);
        sales.setCartItems(adapter.getCartItems());
        sales.setInvoiceCode("");
        sales.setUploadStatus("N");

        sales.setRoundoff_value(Float.parseFloat("" + fl_roundoff));
        //sales.setCartItems(adapter.getCartItems());
        //////////////////////
        sales.setTaxPercentage(dbl_tax_value);
        //  sales.setTaxAmount(dbl_tax_amount);

        //getDiscountAmount,getWithoutTaxTotal
        sales.setWithoutTaxTotal(dbl_withouttax_total - adapter.getDiscountTotal());
        //  sales.setWithTaxTotal(dbl_withtax_total);

        sales.setWithoutDiscount(db_net_total);

        db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());
        if (db_discount == 0) {
            sales.setDiscount((float) adapter.getDiscountTotal());

        } else {
//            sales.setDiscount((float) db_discount);
            sales.setDiscount((float) db_total_disc);
        }
        if (adapter.getDiscountTotal() > 0)

            sales.setDiscount_value((float) adapter.getDiscountTotal() + (float) less_discount);
        else {
            sales.setDiscount_value((float) less_discount);

        }
        sales.setDiscount((float) less_discount);
        if (getAmount(sales.getDiscount()).equals(".00")) {
            sales.setDiscount(0);
        }
        ////
        db_disc_total = dbl_withouttax_total - less_discount;
        //sales.setDiscountAmount(db_disc_total);
        /////////////////////////////////////////////////////////////////////////////
        sales.setDiscountAmount((dbl_withouttax_total - adapter.getDiscountTotal()) - less_discount);
        ////////////////////////////////////////////////////////////////////////////////


        //haris doubt
        sales.setTaxPercentage(0);
        /////////////
        sales.setTaxAmount(db_vat_amount);
        sales.setWithTaxTotal(db_grand_total);

        sales.setPayment_type(st_paytype);

        if (tax_typeflag == 1) {
            sales.setTax_type("inclusive");
        }
        if (tax_typeflag == 1) {
            sales.setTax_type("exclusive");
        }
        sales.setTotalkg(adapter.get_totalkg());

        st_executive_code = "";
        // String st_executive_code = sessionValue.getexecutivecode();
        st_executive_code = sessionValue.getPrefRoutecode();
        Log.e("getPrefRoutecode", "" + st_executive_code);
        invoiceNumber = generateNewNumber(sessionValue.getInvoiceCode(st_executive_code));

        Log.e("Invoice Number", "/" + invoiceNumber);
        if (invoiceNumber.length() == 1) {
            invoiceNumber = "00" + invoiceNumber;
        }
        if (invoiceNumber.length() == 2) {
            invoiceNumber = "0" + invoiceNumber;
        }

        //sales.setInvoiceCode(SELECTED_SHOP.getRouteCode() + invoiceNumber);
        sales.setInvoiceCode(st_executive_code + invoiceNumber);
        sales.setVat_status(st_vat_status);

        //////////////////

        boolean insertStatus = myDatabase.insertQuotation(sales);

        if (insertStatus) {
            // sessionValue.storeInvoiceCode(SELECTED_SHOP.getRouteCode(), invoiceNumber);
            sessionValue.storeInvoiceCode(st_executive_code, invoiceNumber);
            quotationList.clear();

            quotationList = myDatabase.getAllQuotations();
            Log.e("", "total" + sales.getTotal());

            if (isNetworkConnected()) {

                placeQuotation(sales);

            } else {


                //   Toast.makeText(getActivity(), "Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), PreviewActivity.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_QUOTATION);

                intent.putExtra(SALES_VALUE_KEY, sales);
                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                startActivity(intent);
                getActivity().finish();


            }

        } else
            Toast.makeText(getActivity(), "Insertion Failed", Toast.LENGTH_SHORT).show();

        Log.v(TAG, "saveToLocalQuotation status  " + insertStatus);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_sales_addCart:


                // addToCart();

                addflag = 0;

                if (select_Cart != null) {

                    for (int i = 0; i < cartItems.size(); i++) {
                        if (select_Cart.getProductId() == cartItems.get(i).getProductId()) {

                            addflag = 1;

                            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setMessage("Product already added ! Please edit from list if needed");

                            // Setting OK Button
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog closed
                                    alertDialog.dismiss();
                                    refreshProductType();
                                }
                            });

                            // Showing Alert Message
                            alertDialog.show();
                        }
                    }

                    if (addflag == 0) {
                        Log.e("Add cart", "inside");
                        addToCart();
                    }
                } else {
                    tvProductSpinner.setError("Select Product");
                }

                break;
            case R.id.button_sales_makePayment:

                double saleCredit = myDatabase.getCustomerSaleCreditedAmount(SELECTED_SHOP.getShopId());
                double collection = myDatabase.getReceiptCollectionAmount(SELECTED_SHOP.getShopId());

                double maxCredit = SELECTED_SHOP.getCreditLimit() - saleCredit + collection;

                double currentbal = saleCredit + collection;


                final PaymentTypeDialog paymentDialog = new PaymentTypeDialog(getActivity(), maxCredit, SELECTED_SHOP.getCreditlimit_register(), db_grand_total, PAYMENT_TYPE, new PaymentTypeDialog.paymentTypeClickListener() {
                    @Override
                    public void onPaymentTypeClick(String type) {

                        PAYMENT_TYPE = type;

                        if (type.equals("Cash")) {
                            //  paid_amount = adapter.getGrandTotal();
                            paid_amount = db_grand_total;
                            st_paytype = "CashSale";
                        }
                        else if (type.equals("Card")) {
                            paid_amount = db_grand_total;
                            st_paytype = "CardSale";
                        }
                        else if(type.equals("Credit")) {
                            paid_amount = 0;
                            st_paytype = "CreditSale";
                        }
                    }

                });

                paymentDialog.show();

                break;

            case R.id.button_sales_cancel:

                final AlertDialog alertDialogcancel = new AlertDialog.Builder(getActivity()).create();
                alertDialogcancel.setMessage("Are You Confirm..?");

                // Setting OK Button
                alertDialogcancel.setButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        clear_cartitems();


                        alertDialogcancel.dismiss();

                    }
                });

                // Showing Alert Message
                alertDialogcancel.show();


                        // }
                    break;
            case R.id.button_sales_finish:

                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (validatePlaceOrder()) {
                            boolean deleteStatus = myDatabase.deleteTableRequest(REQ_QUOTATION_TEMP);

                            if (SAVE_STATUS == 0) {
                                saveToLocalSales();
                            } else {
                                ShowPrintAlert();
                            }
                        }

                        break;
                    case ActivityConstants.ACTIVITY_QUOTATION:
                        Log.e("click ", "qtn");
                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setMessage("Are You Confirm..?");

                        // Setting OK Button
                        alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                if (validateQuotation()) {
                                    boolean deleteStatus = myDatabase.deleteTableRequest(REQ_QUOTATION_TEMP);

                                    saveToLocalQuotation();
                                }

                                alertDialog.dismiss();

                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();
//                        if (validateQuotation())
//                            saveToLocalQuotation();

                        break;


                }
                break;

            case R.id.button_sales_add_descrptn:
                final ProductRemarkDialog remarkdialog = new ProductRemarkDialog(getActivity(), new ProductRemarkDialog.productRemarkClickListener() {
                    @Override
                    public void onremarkClick(String desc) {

                        Log.e("desc",desc);
                        st_descrption = desc;


                    }

                });

                remarkdialog.show();
                break;
        }
    }

    private void clear_cartitems() {
        boolean deleteStatus1 = myDatabase.deleteTableRequest(REQ_QUOTATION_TEMP);

        productList_temp.clear();
        cartItems.clear();
        try {
            productList_temp = myDatabase.getQuotationsProducttemp(Integer.parseInt(sessionValue.getselected_shopid()));
            Log.e("sizeee", "" + productList_temp.size());

        } catch (Exception e) {

        }
        cartItems = productList_temp;
        adapter = new RvCartAdapter(productList_temp, st_disc, new OnNotifyListener() {
            @Override
            public void onNotified() {
                flag = 1;
                setTotalPriceView();

                if (adapter.getItemCount() == 0)
                    btnMakePayment.setEnabled(false);
                else
                    btnMakePayment.setEnabled(true);
            }
        });
        adapter.notifyDataSetChanged();
        setRecyclerView();

    }

    private void ShowPrintAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("Already Saved");

        // Setting Dialog Message
        alertDialog.setMessage("Invoice already saved. Do you want to print invoice ?");

        // Setting Icon to Dialog
        //  alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Write your code here to invoke YES event
                //  Toast.makeText(getActivity(), "You clicked on YES", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), PreviewActivity.class);
                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALES);

                intent.putExtra(SALES_VALUE_KEY, salesPrint);
                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                startActivity(intent);
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

    private void addToCart() {
//        productList_temp1.clear();
//        productList_temp1 = myDatabase.getQuotationsProducttemp(Integer.parseInt(sessionValue.getselected_shopid()));
//        //try {
//        for(int j=0; j<productList_temp1.size();j++){
//
//            Log.e("if","crt if"+productList_temp1.get(j).getNetPrice());
//            Log.e("if","crt if"+productList_temp1.get(j).getProductPrice());
//
//        }
        if (addCartValidate() && select_Cart != null) {


            String st_disctype = "";
            if (tax_typeflag == 0) {
                st_disctype = "TAX_EXCLUSIVE";
            } else {
                st_disctype = "TAX_INCLUSIVE";
            }

            //CartItem cart = select_Cart;
            CartItem cart = new CartItem();
            Log.e("cartin", "okk");
            float quantityy = 0,freequantityy =0;
            float quantity = TextUtils.isEmpty(etQuantity.getText().toString().trim()) ? 0 : Float.valueOf(etQuantity.getText().toString().trim());
            float freequantity = TextUtils.isEmpty(etFreeQuantity.getText().toString().trim()) ? 0 : Float.valueOf(etFreeQuantity.getText().toString().trim());

            cart.setTypeQuantity(quantity);
            cart.setDiscount_percent(disc_perentge);
            cart.setProductName(select_Cart.getProductName());
            cart.setProductId(select_Cart.getProductId());
            cart.setProductCode(select_Cart.getProductCode());
            cart.setStockQuantity(select_Cart.getStockQuantity());
            cart.setTax(select_Cart.getTax());
            cart.setTaxValue(select_Cart.getTaxValue());
            cart.setUnitselected(select_Cart.getUnitselected());
            cart.setTax_type(st_disctype);
            cart.setBarcode(select_Cart.getBarcode());
            cart.setMfg_date(mfg_date);
            cart.setArabicName(select_Cart.getArabicName());
            cart.setRetailPrice(select_Cart.getRetailPrice());
            cart.setWholeSalePrice(select_Cart.getWholeSalePrice());
            cart.setMrprate(select_Cart.getMrprate());
            cart.setConfactr_kg(confctr_kg_unit);
            cart.setUnitid_selected(unit_id);
            cart.setUnit_confactor(unit_confctr);
            cart.setProduct_hsncode(select_Cart.getProduct_hsncode());
            cart.setPrice_kgm(kgm_unitprice);
            cart.setConfactor_kg(confctr_kgm);
            cart.setDescription(st_descrption);
            cart.setFreeQty(freequantity);
            cart.setP_name(select_Cart.getP_name());

            if (!unit_confctr.equals("")) {
                quantityy = quantity * Integer.parseInt(unit_confctr);
                freequantityy = freequantity * Integer.parseInt(unit_confctr);
            }
            cart.setFreeqnty_piece(freequantityy);

            Log.e("confctr_kg_unit aftr", "" + confctr_kgm);
            if (unit_id.equals("10")) {

                db_qnty_kgm = cart.getTypeQuantity();
            } else {
                float confctr = TextUtils.isEmpty(confctr_kgm) ? 0 : Float.valueOf(confctr_kgm);

                if (confctr != 0) {
                    db_qnty_kgm = cart.getTypeQuantity() / confctr;
                } else {
                    db_qnty_kgm = 0;
                }
            }

            cart.setQnty_kgm(db_qnty_kgm);

            if (spinnerCartUnit.getSelectedItem().toString().equals(PRODUCT_UNIT_CASE))
                quantity = quantity * cart.getPiecepercart();


            if (!unit_confctr.equals(""))
                quantityy = quantity * Integer.parseInt(unit_confctr);
            Log.e("qnty aftr", "" + quantityy);
            Log.e("wholesale prce", ""+select_Cart.getWholeSalePrice());
            ////////////////////////////

            double productPrice = cart.getRetailPrice();

            try {

                cart.setProductPrice(Double.parseDouble("" + tvNetPrice.getText().toString()));

            } catch (Exception e) {

            }

            double netPrice = 0;
            if (!tvNetPrice.getText().toString().isEmpty()) {
                netPrice = Double.parseDouble(tvNetPrice.getText().toString().trim());
            } else {
                netPrice = 0;
            }

            cart.setNetPrice(netPrice);

            if (etProductDiscount.getText().toString().isEmpty()) {
                cart.setProductDiscount(0);
            } else {
                cart.setProductDiscount(prod_disc);
            }

            if (st_prod_disctype.equals("%")) {
                cart.setDisc_percentage(disc_perentge);
            }
            else{
                cart.setDisc_percentage(0);
            }

            double proDisc = cart.getProductDiscount();
            if (proDisc > 0 || freequantity > 0) {
                cart.setDiscountStatus(true);
            } else {
                cart.setDiscountStatus(true);
            }

            cart.setProductTotal(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
            cart.setProductTotalValue(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));

            //haris added on 25-11-2020
            cart.setProductTotal_withoutdisc(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
            cart.setTax_valuewithoutdisc(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));

            double salePrice = getSalePrice(netPrice, select_Cart.getTax(), cart.getTax_type());

            ////////////////////////////////////////////////
            if(st_vat_status.equals("No Vat")){
                cart.setTaxValue(0);
                cart.setCgst((float) 0);
                cart.setSgst((float) 0);
            }
            else {

                cart.setTaxValue(getTaxPricefl(cart.getProductTotalValue(), select_Cart.getTax(), st_disctype));
                cart.setTaxValue(Double.parseDouble(getAmount(cart.getTaxValue())));
                float fl_cgst = (float) (cart.getTaxValue() / 2);
                double tothalf = 0;
                if (fl_cgst > 0) {
                    tothalf = Double.parseDouble(getAmount(cart.getTaxValue() / 2));
                }

                cart.setCgst((float) tothalf);
                cart.setSgst((float) tothalf);
            }

            dbl_tax_value = select_Cart.getTax();

            if (st_disctype.equals("TAX_INCLUSIVE")) {

                cart.setProductTotal(roundTwoDecimals(cart.getProductTotalValue() - (cart.getTaxValue())));
                cart.setProductPriceNew(cart.getProductTotalValue() - cart.getTaxValue());
            }

            double net_total = 0;
            if (st_disctype.equals("TAX_INCLUSIVE")) {
                double unitprice = productprice_inclusive(cart.getNetPrice(), cart.getTax());
                if (unitprice > 0) {
                    unitprice = Double.parseDouble(getAmount(unitprice));
                }

                cart.setProductPrice(unitprice);
                netPrice = cart.getProductPrice();
                cart.setProductTotal((netPrice * quantity) - cart.getProductDiscount() * quantity);
            } else {
                netPrice = cart.getNetPrice();
                cart.setProductTotal((netPrice * quantity) - cart.getProductDiscount() * quantity);
            }

            double amnt = select_Cart.getNetPrice() * quantity;
            double bonusamount = getBonusAmount(amnt, select_Cart.getProductBonus());
            cart.setProductBonus(select_Cart.getProductBonus());
            cart.setSalePrice(salePrice);
            cart.setPieceQuantity_nw(quantity);
            cart.setPieceQuantity(quantityy);
            cart.setCon_factor(Integer.parseInt(unit_confctr));
            cart.setTotalPrice(salePrice * quantity);
            cart.setOrderType(spinnerCartUnit.getSelectedItem().toString());
            cart.setOrderTypeName(unit_name);
            cart.setVat_status(st_vat_status);


            adapter.changeItem(cart);
            //setRecyclerView();
            adapter.notifyDataSetChanged();

            myDatabase.insertQuotationProducts_temp(SELECTED_SHOP.getShopId(), adapter.getCartItems());

            refreshProductType();
            //set_cartproducts();
        }
//        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Error in Quantity", Toast.LENGTH_SHORT).show();
//        }
    }

    private void setCartProgressBar(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
            lytCart.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
        } else
            progressBar.setVisibility(View.GONE);
    }


    //set ErrorView
    private void setCartErrorView(final String title) {

        lytCart.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        setCartProgressBar(false);
        errorView.setConfig(ErrorView.Config.create()
                .title(title)
//                .subtitle(subTitle)
                .retryVisible(false)
                .build());

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {

                getVanStockList();

            }
        });
    }


    //    sales type selection time changing all related values`
    public void initSelectedCustomer(Shop shop) {

        this.SELECTED_SHOP = shop;

    }

    //    sales type selection time changing all related values`
    public void changeSaleType(String type) {

        this.SALE_TYPE = type;
        // ArrayList<CartItem> list = adapter.getCartItems();
        ArrayList<CartItem> list = adapter.getCartItems();

        Log.e("type", "" + type);

        if (!list.isEmpty()) {
            Log.e("list", "" + list);
            for (int i = 0; i < list.size(); i++) {

                CartItem c = list.get(i);

                float quantity = c.getTypeQuantity();

                if (c.getOrderTypeName().equals(PRODUCT_UNIT_CASE))
                    quantity = quantity * c.getPiecepercart();

                double productPrice = c.getRetailPrice();
                Log.e("retil", "" + productPrice);
                if (SALE_TYPE.equals(SALE_WHOLESALE)) {
                    productPrice = c.getWholeSalePrice();
                    Log.e("wholesalprce", "" + productPrice);
                }


                double netPrice = getWithoutTaxPrice(productPrice, c.getTax());
                c.setNetPrice(netPrice);

                double salePrice = getSalePrice(netPrice, c.getTax(), c.getTax_type());

                c.setSalePrice(salePrice);

                c.setTotalPrice(salePrice * quantity);

                double disc_tot = c.getProductDiscount() * quantity;
                c.setProductTotal((salePrice * quantity) - disc_tot);


                adapter.updateItem(c, i);
            }
        }
    }

   public void change_vat_status(String st_vat_sta){
        Log.e("vat_st",st_vat_sta);
       clear_cartitems();
        st_vat_status = st_vat_sta;
       setTotalPriceView();

    }

//    change sale type retail or wholesale

    //    validation add to cart
    private boolean addCartValidate() {
        Log.e("reached", "validate");

        boolean status = false;

        tvProductSpinner.setError(null);
        tvCodeSpinner.setError(null);
        etQuantity.setError(null);

        if (select_Cart == null) {
            tvProductSpinner.setError("Select Product");
            return false;
        }

        float typeQuantity = TextUtils.isEmpty(etQuantity.getText().toString().trim()) ? 0 : Float.valueOf(etQuantity.getText().toString().trim());
        float freeQuantity = TextUtils.isEmpty(etFreeQuantity.getText().toString().trim()) ? 0 : Float.valueOf(etFreeQuantity.getText().toString().trim());

        float productQuantity = typeQuantity + freeQuantity;

        if (spinnerCartUnit.getSelectedItem().toString().equals(PRODUCT_UNIT_CASE))
            productQuantity = typeQuantity * select_Cart.getPiecepercart();

        CartItem c1 = (CartItem) tvProductSpinner.getTag();
        CartItem c2 = (CartItem) tvCodeSpinner.getTag();
        if (c1 == null) {
            status = false;
            tvProductSpinner.setError("Select Product");
        } else if (c2 == null) {
            tvCodeSpinner.setError("Select Code");
            status = false;
        } else if (typeQuantity == 0) {
            etQuantity.setError("Invalid Quantity");
            status = false;

        } else if (callingActivity == ActivityConstants.ACTIVITY_SALES && productQuantity > available_qty) {
            etQuantity.setError("Maximum Stock is " + available_qty);
            status = false;
//
//            if (spinnerCartUnit.getSelectedItem().toString().equals(PRODUCT_UNIT_CASE)) {
//                int stock = select_Cart.getStockQuantity() / select_Cart.getPiecepercart();
//                etQuantity.setError("Maximum Stock is " + stock);
//                status = false;
//            } else {
//                etQuantity.setError("Maximum Stock is " + select_Cart.getStockQuantity());
//                status = false;
//            }
//
        } else if (TextUtils.isEmpty(tvQtyTotal.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Total Amount is Empty...!", Toast.LENGTH_SHORT).show();
            status = false;

        } else if (TextUtils.isEmpty(tvNetPrice.getText().toString().trim()) || (tvNetPrice.getText().toString().trim().equals("0"))) {
            Toast.makeText(getActivity(), "Unit price cannot be Empty...!", Toast.LENGTH_SHORT).show();
            status = false;

        } else
            status = true;

        return status;

    }

    private void refreshProductType() {

        tvProductSpinner.setText("");
        tvCodeSpinner.setText("");
        etQuantity.setText("");
        tvNetPrice.setText("");
        tvQtyTotal.setText("");
        etFreeQuantity.setText("");
        etProductDiscount.setText("");
        edittext_discount.setText("");
        edittext_availbleqty.setText("");
        unit_confctr = "0";
        st_descrption ="";

        select_Cart = null;

//      etQuantity.setFocusable(false);
        tvNetTotal.setFocusable(true);

        //  edt_barcode.setFocusable(true);
        //  edt_barcode.requestFocus();

        array_unitname.clear();
        array_unitid.clear();
        array_unitconfactor.clear();
        array_unitprice.clear();

        try {

            ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_unitname);
            //ArrayAdapter.createFromResource(getActivity(), array_unitname, R.layout.spinner_background_dark); //R.array.cart_type
            orderTypeAdapter.setDropDownViewResource(R.layout.spinner_list);
            spinnerCartUnit.setAdapter(orderTypeAdapter);
        } catch (Exception e) {

        }


        hideSoftKeyboard();


    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
//            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
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

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {


//        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            inputMethodManager.showSoftInput(view, 0);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private static String getDateTime() {

        Date date = new Date();
        return dbDateFormat.format(date);
    }


    //    place order
    private void placeOrder(final Sales sales) {

        final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray saleArray = new JSONArray();

        try {
            for (Sales s : saleList) {

                Log.e("Upload Status", "//" + s.getUploadStatus());

                final JSONObject saleObj = new JSONObject();

                saleObj.put(CUSTOMER_KEY, s.getCustomerId());
                saleObj.put("bill_date", s.getDate());
                saleObj.put("total_amount", s.getTotal());
                saleObj.put("sale_type", s.getSaleType());
                saleObj.put("paid_amount", s.getPaid());
                saleObj.put("invoice_no", s.getInvoiceCode());
                saleObj.put("without_tax_total", s.getWithoutTaxTotal());

                saleObj.put("net_total", s.getWithoutDiscount());
                saleObj.put("discount", s.getDiscount());
                saleObj.put("discount_total", s.getDiscountAmount());

                saleObj.put("tax_percentage", s.getTaxPercentage());
                saleObj.put("tax_amount", s.getTaxAmount());
                saleObj.put("with_tax_total", s.getWithTaxTotal());

                if (s.getPaid() == s.getTotal())
                    saleObj.put("invoice_type", "CashSale");
                else
                    saleObj.put("invoice_type", "CreditSale");

                JSONArray cartArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {

                    JSONObject obj = new JSONObject();

                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", c.getProductPrice());
                    obj.put("product_quantity", c.getPieceQuantity());


                    obj.put("product_total", c.getProductPrice() * c.getPieceQuantity()); // c.getTotalPrice()
                    obj.put("product_bonus_percentage", c.getProductBonus());
                    obj.put("product_unit", c.getOrderType());

                    cartArray.put(obj);

                }

                saleObj.put("ordered_products", cartArray);

                saleArray.put(saleObj);
            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("Sale", saleArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "placeOrder cart object  " + object);

        webPlaceOrder(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "placeOrder  response   " + response);
                try {

                    if (response.getString("status").equalsIgnoreCase("Success")) {

                        long insertStatus = myDatabase.insertSale(sales);

                        if (insertStatus != -1) {

                            myDatabase.UpdateSalesUploadStatus(sales.getInvoiceCode());
                            sessionValue.storeInvoiceCode(SELECTED_SHOP.getRouteCode(), invoiceNumber);

                            for (CartItem c : sales.getCartItems()) {
                                myDatabase.updateStock(c, REQ_SALE_TYPE);
                            }


                            for (int i = 0; i < adapter.getCartItems().size(); i++) {
//                                printLog("item name", "" + adapter.getCartItems().get(i).getProductName());
//                                printLog("unit price", "" + adapter.getCartItems().get(i).getProductPrice());
                                double amount = adapter.getCartItems().get(i).getNetPrice() * adapter.getCartItems().get(i).getPieceQuantity();
                                float bonus = adapter.getCartItems().get(i).getProductBonus();
                                double bonusamount = getBonusAmount(amount, bonus);
                                pref_bonus = (float) (pref_bonus + bonusamount);
                            }

                            sessionAuth.updateBonus(pref_bonus);

                            Transaction t = new Transaction(sales.getCustomerId(), sales.getPaid(), sales.getWithTaxTotal());
                            if (myDatabase.updateCustomerBalance(t)) {
                                Toast.makeText(getActivity(), "Successfully", Toast.LENGTH_SHORT).show();

                                SAVE_STATUS = 1;

                                salesPrint = sales;

                                Intent intent = new Intent(getActivity(), PreviewActivity.class);
                                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALES);

                                intent.putExtra(SALES_VALUE_KEY, sales);
                                intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                                startActivity(intent);
                                getActivity().finish();

                            } else {
                                Toast.makeText(getContext(), "Customer data update failure", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getContext(), "Insertion failed", Toast.LENGTH_SHORT).show();
                        }

                    } else
                        Toast.makeText(getActivity(), "Orders " + response.getString("status"), Toast.LENGTH_SHORT).show();

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


    //    place quotation
    private void placeQuotation(final Sales sales) {

        final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray quotationArray = new JSONArray();

        try {
            for (Sales s : quotationList) {

                final JSONObject saleObj = new JSONObject();

                ///////////////////////////
                saleObj.put(CUSTOMER_KEY, s.getCustomerId());
                saleObj.put("bill_date", s.getDate());
                saleObj.put("total_amount", getAmount(s.getWithTaxTotal()));
                saleObj.put("sale_type", s.getSaleType());
                saleObj.put("paid_amount", getAmount(s.getPaid()));
                saleObj.put("invoice_no", s.getInvoiceCode());
                saleObj.put("without_tax_total", getAmount(s.getWithoutTaxTotal()));
                saleObj.put("discount", getAmount(s.getDiscount()));
                saleObj.put("discount_total", getAmount(s.getDiscountAmount()));
                saleObj.put("tax_amount", getAmount(s.getTaxAmount()));
                saleObj.put("with_tax_total", getAmount(s.getWithTaxTotal()));
                saleObj.put("invoice_type", "CreditSale");
                saleObj.put("round_off", getAmount(s.getRoundoff_value()));

                JSONArray cartArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {
                    JSONObject obj = new JSONObject();
                    int tx_type = 0;

                    if (c.getTax_type().equals("TAX_INCLUSIVE")) {
                        tx_type = 1;
                    }


                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", getAmountthree(c.getProductPrice()));
                    obj.put("product_quantity", c.getPieceQuantity_nw());
                    // obj.put("product_total", c.getProductPrice()*c.getPieceQuantity()); // c.getTotalPrice()
                    // obj.put("product_total", ""+getAmount(c.getProductTotal())); // c.getTotalPrice()
                    obj.put("product_total", "" + getAmountthree(c.getProductPrice() * c.getPieceQuantity_nw())); // c.getTotalPrice()
                    //obj.put("product_bonus_percentage", c.getProductBonus());
                    obj.put("product_unit", c.getOrderType());
                    obj.put("tax", getAmount(c.getTax()));
                    obj.put("tax_amont", getAmount(c.getTaxValue()));
                    obj.put("product_discount", "" + getAmount(c.getProductDiscount()));
                    obj.put("cgst", getAmount(c.getCgst()));
                    obj.put("sgst", getAmount(c.getSgst()));
                    obj.put("tax_type", tx_type);
                    ///////////////////

                    cartArray.put(obj);
                }

                saleObj.put("ordered_products", cartArray);
                quotationArray.put(saleObj);

            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("route_id", sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID));
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("Sale", quotationArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "placeQuotation  Object" + object);

        webPlaceQuotation(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "placeQuotation  response   " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        sessionValue.storeInvoiceCode(st_executive_code, invoiceNumber);
                        for (Sales s : quotationList) {
                            myDatabase.UpdateQuotationUploadStatus("" + s.getLocId());
                        }

                        Intent intent = new Intent(getActivity(), PreviewActivity.class);
                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_QUOTATION);

                        intent.putExtra(SALES_VALUE_KEY, sales);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                        startActivity(intent);
                        getActivity().finish();

                    } else {
                        Log.e("", "total" + sales.getTotal());
                        Intent intent = new Intent(getActivity(), PreviewActivity.class);
                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_QUOTATION);

                        intent.putExtra(SALES_VALUE_KEY, sales);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                        startActivity(intent);
                        getActivity().finish();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        }, object);
    }


    //    place quotation
    private void place_sale(final Sales sales) {

        final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "Please wait...", false, false);

        final JSONObject object = new JSONObject();

        final JSONArray quotationArray = new JSONArray();

        try {
            for (Sales s : saleList) {

                final JSONObject saleObj = new JSONObject();

                ///////////////////////////
                saleObj.put(CUSTOMER_KEY, s.getCustomerId());
                saleObj.put("bill_date", s.getDate());
                saleObj.put("total_amount", getAmount(s.getWithTaxTotal()));
                saleObj.put("sale_type", s.getSaleType());
                saleObj.put("paid_amount", getAmount(s.getPaid()));
                saleObj.put("invoice_no", s.getInvoiceCode());
                saleObj.put("without_tax_total", getAmount(s.getWithoutTaxTotal()));
                saleObj.put("discount", getAmount(s.getDiscount()));
                saleObj.put("discount_total", getAmount(s.getDiscountAmount()));
                saleObj.put("tax_amount", getAmount(s.getTaxAmount()));
                saleObj.put("with_tax_total", getAmount(s.getWithTaxTotal()));
                saleObj.put("invoice_type", s.getPayment_type());
                saleObj.put("round_off", getAmount(s.getRoundoff_value()));

                JSONArray cartArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {
                    JSONObject obj = new JSONObject();
                    int tx_type = 0;

                    if (c.getTax_type().equals("TAX_INCLUSIVE")) {
                        tx_type = 1;
                    }


                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", getAmountthree(c.getProductPrice()));
                    obj.put("product_quantity", c.getPieceQuantity_nw());
                    // obj.put("product_total", c.getProductPrice()*c.getPieceQuantity()); // c.getTotalPrice()
                    // obj.put("product_total", ""+getAmount(c.getProductTotal())); // c.getTotalPrice()
                    obj.put("product_total", "" + getAmountthree(c.getProductPrice() * c.getPieceQuantity_nw())); // c.getTotalPrice()
                    //obj.put("product_bonus_percentage", c.getProductBonus());
                    obj.put("product_unit", c.getOrderType());
                    obj.put("tax", getAmount(c.getTax()));
                    obj.put("tax_amont", getAmount(c.getTaxValue()));
                    obj.put("product_discount", "" + getAmount(c.getProductDiscount()));
                    obj.put("cgst", getAmount(c.getCgst()));
                    obj.put("sgst", getAmount(c.getSgst()));
                    obj.put("tax_type", tx_type);
                    ///////////////////

                    cartArray.put(obj);
                }

                saleObj.put("ordered_products", cartArray);
                quotationArray.put(saleObj);

            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put("route_id", sessionValue.getStoredValuesDetails().get(SessionValue.PREF_SELECTED_ROUTE_ID));
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("Sale", quotationArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "placeQuotation  Object" + object);

        webPlaceOrder(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "placeQuotation  response   " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        sessionValue.storeInvoiceCode(st_executive_code, invoiceNumber);
                        for (Sales s : saleList) {
                            myDatabase.UpdateSalesUploadStatus("" + s.getInvoiceCode());
                        }

                        // Intent intent = new Intent(getActivity(), PreviewActivity_F.class);
                        Intent intent = new Intent(getActivity(), PreviewActivity.class);
                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALES);
                        intent.putExtra(SALES_VALUE_KEY, sales);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                        startActivity(intent);
                        getActivity().finish();

                    } else {
                        Log.e("", "total" + sales.getTotal());
                        // Intent intent = new Intent(getActivity(), PreviewActivity_F.class);
                        Intent intent = new Intent(getActivity(), PreviewActivity.class);
                        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_SALES);
                        intent.putExtra(SALES_VALUE_KEY, sales);
                        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);
                        startActivity(intent);
                        getActivity().finish();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        }, object);
    }
}
