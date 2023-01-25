package com.advanced.minhas.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.advanced.minhas.config.AmountCalculator.getSalePrice;
import static com.advanced.minhas.config.AmountCalculator.getTaxPrice;
import static com.advanced.minhas.config.AmountCalculator.getWithoutTaxPrice;
import static com.advanced.minhas.config.AmountCalculator.productprice_inclusive;
import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.EXECUTIVE_KEY;
import static com.advanced.minhas.config.ConfigKey.INVOICE_NO_KEY;
import static com.advanced.minhas.config.ConfigKey.REQ_RETURN_TYPE;
import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigSales.IS_GST_ENABLED;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.INVOICE_RETURN_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.PRODUCT_UNIT_PIESE;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.dbDateFormat;
import static com.advanced.minhas.config.Generic.generateNewNumber;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_PRODUCT_TYPE;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_LATITUDE;
import static com.advanced.minhas.session.SessionValue.PREF_LONGITUDE;
import static com.advanced.minhas.session.SessionValue.PREF_VATPERCENT;
import static com.advanced.minhas.webservice.WebService.webGetAllReceipt;
import static com.advanced.minhas.webservice.WebService.webGetReturnInvoiceDetails;
import static com.advanced.minhas.webservice.WebService.webOfflineReturn;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.activity.BillwiseReceipt;
import com.advanced.minhas.activity.ReturnPreviewActivity;
import com.advanced.minhas.adapter.InvoiceAdapter;
import com.advanced.minhas.adapter.Return_SizeAndQuantityAdapter;
import com.advanced.minhas.adapter.RvReturnInvoiceAdapter;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.dialog.CartSpinnerDialog;
import com.advanced.minhas.dialog.InvoiceSpinnerDialog;
import com.advanced.minhas.dialog.OnSpinerItemClick;
import com.advanced.minhas.dialog.PaymentTypeDialog;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Invoice;
import com.advanced.minhas.model.InvoiceSales;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
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


public class InvoiceFragment extends Fragment implements View.OnClickListener {
    String st_sizelist = "", st_each_size_qty = "", returnNumber = "", st_size_string = "", st_taxlist = "", st_hsn_code = "",
            st_productcode = "", VATPERCENTAGE = "", st_executive_code = "";
    String st_tax = "", st_cgst = "", st_sgst = "" ;
    private ArrayList<String> sizeitems_continous = new ArrayList<>();
    private ArrayList<String> sizeitems_selected_array = new ArrayList<>();
    private ArrayList<Size_Return> array_sizeanqty_aftersale = new ArrayList<>();
    private ArrayList<CartItem> array_sizelist = new ArrayList<>();
    private ArrayList<Size_Return> array_sizestock = new ArrayList<>();
    private ArrayList<Taxes> array_tax = new ArrayList<>();
    ArrayList<String> size_finalarray = new ArrayList<>();
    public static final String ARG_CUSTOMER = "customer_key_arg";
    int int_product_id = 0, prod_id_dialogue = 0, piece_qty = 0;
    String TAG = "InvoiceFragment";
    Dialog dialog;
    private ArrayList<String> array_unitname = new ArrayList<>();
    private ArrayList<String> array_unitid = new ArrayList<>();
    private ArrayList<String> array_unitprice = new ArrayList<>();
    private ArrayList<String> array_unitconfactor = new ArrayList<>();
    private Context context;
    private Return_SizeAndQuantityAdapter sizeadapter;
    private AppCompatSpinner sp_totaldisc, spinnerCartUnit;
    private TextView tvProductSpinner, tvCodeSpinner, tvInvoiceTitle, tvActualQuantity,
            tvTotalRefund, tv_invoicelist, tvNetTotal, tvVat_cgst, tvVat_sgst, textView_sales_size,tvVat,
            tv_totalqty, etReturnQuantity, textView_withoutdiscount, tvQtyTotal;
    RecyclerView rec_list;
    EditText edittext_discount, etProductDiscount, etUnitPrice;
    String unit_id = "", unit_name = "",
            unit_confctr = " ", unit_price = " ", unit_list = "";
    int confctr_kg_unit =0;
    LinearLayout ly_sgst,ly_prod_discount,ly_taxindia, ly_taxoutside;
    private ErrorView evMain, evInvoiceDetals;
    private InvoiceAdapter adapter;

    private ProgressBar pbMain, pbInvoiceDetails;

    private ViewGroup viewLayout, viewInvoiceDetails;

    private Button btnFinish, btnAddCart ,btnMakePayment;
    Button plusbutton;
    int tax_typeflag = 0;
    private RecyclerView rvProduct;

    private RvReturnInvoiceAdapter returnProductAdapter;

    private ArrayList<CartItem> selectedItems = new ArrayList<>();

    private CartItem SELECTED_CART = null;

    private Invoice SELECTED_INVOICE = null;

    private Shop SELECTED_SHOP;

    private String dayRegId = "", EXECUTIVE_ID = "", CURRENCY = "", st_disc = "";

    private String PAYMENT_TYPE = "";

    private float pref_bonus = 0, cash_inhand = 0;

    private ListView invoicesListView;

    private ArrayList<Invoice> invoices = new ArrayList<>();

    final ArrayList<CartItem> cartItems = new ArrayList<>();
    private SwitchCompat switchreceipt_type;
    ArrayList<CartItem> cartItemsOffline = new ArrayList<>();
    double dbl_tax_value = 0;
    ArrayList<Sales> returnOfflineList = new ArrayList<>();
    ArrayList<Size_Return> arr_size_return_full = new ArrayList<>();
    TextView textView_sales_vat;
    private MyDatabase myDatabase;
    private SessionAuth sessionAuth;
    SessionValue sessionValue;
    private ArrayList<String> array_totaldisc_tye = new ArrayList<>();
    private ArrayList<InvoiceSales> invoicesales = new ArrayList<InvoiceSales>();
    private ArrayList<String> invoiceNos = new ArrayList<>();
    double db_net_total = 0, db_discount = 0, db_disc_total = 0, db_vat_amount = 0, db_grand_total = 0,
            disc_value = 0, dbl_tax_amount = 0, dbl_withouttax_total = 0, dbl_withtax_total = 0,
            discount_price = 0, less_discount = 0, db_total_disc = 0, disc_perentge = 0, price_kg=0 ,price_pcs =0; // db_vat = 0,  db_withtax_total=0,
    private SwitchCompat switch_tax;
    String str_Latitude = "0", str_Longitude = "0", latitude_session = "", longitude_session = "", st_totaldisctype = "";
    private String provider , st_paytype ="";
    private double paid_amount = 0;
    LocationManager locationManager;

    public static InvoiceFragment newInstance(Shop shop) {
        InvoiceFragment fragment = new InvoiceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CUSTOMER, shop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {

                SELECTED_SHOP = (Shop) getArguments().getSerializable(ARG_CUSTOMER);
            } catch (ClassCastException e) {
                e.getMessage();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice, container, false);
        dialog = new Dialog(getActivity());
        tvProductSpinner = (TextView) view.findViewById(R.id.textView_sales_return_invoice_product);
        tvCodeSpinner = (TextView) view.findViewById(R.id.textView_sales_return_invoice_code);
        viewLayout = (ViewGroup) view.findViewById(R.id.layout_sales_return_invoice);
        viewInvoiceDetails = (ViewGroup) view.findViewById(R.id.view_invoice_details);
        switch_tax = (SwitchCompat) view.findViewById(R.id.switch_tax);
        tvInvoiceTitle = (TextView) view.findViewById(R.id.textView_sales_return_invoice_title);
        etUnitPrice = (EditText) view.findViewById(R.id.edt_sales_return_invoice_unitPrice);
        tvActualQuantity = (TextView) view.findViewById(R.id.textView_sales_return_invoice_actualQty);
        tvTotalRefund = (TextView) view.findViewById(R.id.textView_sales_return_invoice_refundTotal);
        tv_invoicelist = (TextView) view.findViewById(R.id.textView_invoice_list);
        // textView_sales_size = (TextView) view.findViewById(R.id.textView_sales_return_invoice_size);
        tvNetTotal = (TextView) view.findViewById(R.id.textView_sales_netTotal);
        tvVat_cgst = (TextView) view.findViewById(R.id.textView_return_cgst);
        tvVat_sgst = (TextView) view.findViewById(R.id.textView_return_sgst);
        plusbutton = view.findViewById(R.id.bt_addqty);
        textView_withoutdiscount = (TextView) view.findViewById(R.id.textView_withoutdiscount);
        invoicesListView = (ListView) view.findViewById(R.id.listView_drawer_sales_return_invoice);
        tvQtyTotal = (TextView) view.findViewById(R.id.textView_sales_qtyTotal);
        etReturnQuantity = view.findViewById(R.id.editText_sales_return_invoice_returnQty);
        btnAddCart = (Button) view.findViewById(R.id.button_sales_return_invoice_addCart);
        rvProduct = (RecyclerView) view.findViewById(R.id.recyclerView_sales_return_invoice_product);
        spinnerCartUnit = view.findViewById(R.id.spinner_sales_return_withInvoice_orderUnit);
        pbInvoiceDetails = (ProgressBar) view.findViewById(R.id.progressView_invoice_details);
        pbMain = (ProgressBar) view.findViewById(R.id.progressView_fragment_invoice);
        btnFinish = (Button) view.findViewById(R.id.button_sales_return_invoice_finish);
        evMain = (ErrorView) view.findViewById(R.id.errorView_fragment_invoice);
        evInvoiceDetals = (ErrorView) view.findViewById(R.id.errorView_invoice_details);
        switchreceipt_type = view.findViewById(R.id.switch_receiptmode);
        ly_prod_discount = view.findViewById(R.id.ly_prod_discount);
        // textView_sales_vat = view.findViewById(R.id.textView_sales_cgst);
        tvVat =view.findViewById(R.id.textView_sales_vat);
        // textView_sales_sgst = view.findViewById(R.id.textView_sales_sgst);
        etProductDiscount = (EditText) view.findViewById(R.id.edittext_product_discount);
        edittext_discount = view.findViewById(R.id.edittext_discount);
        sp_totaldisc = view.findViewById(R.id.sp_totaldisc);
        ly_taxindia = view.findViewById(R.id.ly_tax_india);
        ly_taxoutside = view.findViewById(R.id.ly_tax_outside);
        btnMakePayment = view.findViewById(R.id.button_return_makePayment);
        myDatabase = new MyDatabase(getContext());
        sessionAuth = new SessionAuth(getContext());
        sessionValue = new SessionValue(getContext());

        this.dayRegId = new SessionValue(getContext()).getDayRegisterId();
        EXECUTIVE_ID = sessionAuth.getExecutiveId();


        CURRENCY = "" + sessionValue.getControllSettings().get(PREF_CURRENCY);

        VATPERCENTAGE = sessionValue.getControllSettings().get(PREF_VATPERCENT);
        Log.e("VATPERCENTAGE", VATPERCENTAGE);
        //  setShopTypeSpinner();
        //haris added on 27/07/2021 condition for checking size wise or not.
        String st_size_wiseflag = sessionValue.getCompanyDetails().get(PREF_COMPANY_PRODUCT_TYPE);
        Log.e("st_size_wiseflag", st_size_wiseflag);
        array_totaldisc_tye.add("%");
        array_totaldisc_tye.add("Amnt");
        if (st_size_wiseflag.equals("size")) {
            plusbutton.setVisibility(View.VISIBLE);
            // plusbutton.setVisibility(View.VISIBLE);
            etReturnQuantity.setFocusable(false);

        } else {
            plusbutton.setVisibility(View.GONE);
            // plusbutton.setVisibility(View.GONE);
            etReturnQuantity.setFocusable(true);

        }
        if (IS_GST_ENABLED) {
            ly_taxindia.setVisibility(View.GONE);
            ly_taxoutside.setVisibility(View.VISIBLE);
        } else {
            ly_taxindia.setVisibility(View.VISIBLE);
            ly_taxoutside.setVisibility(View.GONE);
        }

        switch_tax.setChecked(true);
        switch_tax.setText(R.string.discnt_inclsve);
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
        switch_tax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    switch_tax.setText(getContext().getText(R.string.discnt_inclsve));
                    tax_typeflag = 0;
                    ly_prod_discount.setVisibility(View.GONE);
                } else {
                    switch_tax.setText(getContext().getText(R.string.discnt_exclsve));
                    tax_typeflag = 1;
                    ly_prod_discount.setVisibility(View.VISIBLE);
                }

            }
        });
//        if(sessionAuth.getExecutiveRole().equals("Executive")) {
//            switchreceipt_type.setChecked(true);
//            switchreceipt_type.setText(getContext().getText(R.string.return_billwise));
//        }
//        else{
//            switchreceipt_type.setVisibility(View.GONE);
//        }


        switchreceipt_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    switchreceipt_type.setText(getContext().getText(R.string.return_billwise));
                else
                    switchreceipt_type.setText(getContext().getText(R.string.return_advnce));

            }
        });

        etReturnQuantity.addTextChangedListener(new TextValidator(etReturnQuantity) {
            @Override
            public void validate(TextView textView, String qtyString) {
                Log.e("reachd", "retn");
            //    try {

                    String netamount = "";
                    double netAmount = 0;

                    etProductDiscount.setText("");
                    float actualqnty = TextUtils.isEmpty(tvActualQuantity.getText().toString().trim()) ? 0 : Float.valueOf(tvActualQuantity.getText().toString().trim());
                    Log.e("actqunty", "" + actualqnty);

                    // if (!TextUtils.isEmpty(qtyString) && SELECTED_CART != null) {
                    if (!TextUtils.isEmpty(qtyString)) {

                        int quantity = TextUtils.isEmpty(qtyString) ? 0 : Integer.valueOf(qtyString);
                        Log.e("qunty", "" + quantity);
                        Log.e("actqunty", "" + actualqnty);
                        if (quantity <= actualqnty)
                            tvQtyTotal.setText(getAmount(SELECTED_CART.getProductPrice() * quantity));
                        else {
                            etReturnQuantity.setText("");
                            Toast.makeText(getActivity(), "Actual quantity " + actualqnty, Toast.LENGTH_LONG).show();
                        }

                    } else {
                        tvQtyTotal.setText(getAmount(0.0f));
                    }
//                } catch (Exception ignored) {
//
//                }
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
                double db_total = returnProductAdapter.getNetTotal() - returnProductAdapter.getDiscountTotal();
                //  st_disc = ""+disc;

                if (st_totaldisctype.equals("%")) {
                    Log.e("nettotal", "" + returnProductAdapter.getNetTotal());


                    double db_disc_tot = returnProductAdapter.getWithoutTaxTotal();
                    double discnt = (db_total+db_vat_amount) * db_discount / 100;
                    //  Log.e("db_total_disc hhh",""+db_total_disc);
                    disc = "" + discnt;

                }


                try {
                    if (disc.isEmpty()) {
                        disc = "0";
                        // st_disc = "0";
                    }

                    //adapter.st_disc = "" + disc;

                    // discount_tot = adapter.getDiscountTotal();
                    //applicable_total = adapter.getApplicableTotal();

                    less_discount = Double.parseDouble(disc);
                    Log.e("less_discount in",""+less_discount);
                    setTotalPriceView();
                } catch (Exception e) {

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Add to textWatcher

//        edittext_discount.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//
//
//                double discount = 0;
//                disc_value = 0;
//
//                try {
//
//                        less_discount = 0;
//                        String disc = "" + edittext_discount.getText().toString().trim();
//                        try {
//                            db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());
//                        } catch (Exception e) {
//
//                        }
//                        double discount_tot = 0;
//                        double applicable_total = 0;
//                        if (disc.equals("")) {
//                            disc = "0";
//                        }
//                        Log.e("discount hr", "" + disc);
//                        Log.e("discount tot", "" + returnProductAdapter.getDiscountTotal());
//
//                        //  st_disc = ""+disc;
//
//                        if(st_totaldisctype.equals("%")){
//                            Log.e("nettotal",""+returnProductAdapter.getNetTotal());
//
//
//                            double db_disc_tot = (returnProductAdapter.getWithoutTaxTotal() - returnProductAdapter.getDiscountTotal());
//                            Log.e("db_disc_total",""+(returnProductAdapter.getWithoutTaxTotal() - returnProductAdapter.getDiscountTotal()));
//                            Log.e("db_discount",""+db_discount);
//                            double discnt = db_disc_tot * db_discount/100;
//                           // Log.e("db_total_disc hhh",""+db_total_disc);
//                            disc = ""+discnt;
//
//                        }
//
//
//                        try {
//                            if (disc.isEmpty()) {
//                                disc = "0";
//                               // st_disc = "0";
//                            }
//
//                            //adapter.st_disc = "" + disc;
//
//                            // discount_tot = adapter.getDiscountTotal();
//                            //applicable_total = adapter.getApplicableTotal();
//
//                            less_discount = Double.parseDouble(disc) ;
//                            setTotalPriceView();
//                        }catch (Exception e){
//
//                        }
//
//
//
//
//                    //}
//
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

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


        etProductDiscount.addTextChangedListener(new TextValidator(etProductDiscount) {
            @Override
            public void validate(TextView textView, String product_discount) {
                try {

                    if (!TextUtils.isEmpty(product_discount) && SELECTED_CART != null) {

//                        int pro_discount = TextUtils.isEmpty(product_discount) ? 0 : Integer.valueOf(product_discount);
                        float pro_discount = TextUtils.isEmpty(product_discount) ? 0 : Float.valueOf(product_discount);
                        Log.e("proddisc", "" + pro_discount);
                        int quatity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());

                        double netprice = Double.parseDouble(getAmount(SELECTED_CART.getProductPrice() * quatity));
                        pro_discount = pro_discount * quatity;

                        discount_price = netprice - pro_discount;
//                        tvNetTotal.setText("Total : " + getAmount(discount_price) + " " + CURRENCY);
                        tvNetTotal.setText(getAmount(discount_price));

                        // tvNetTotal.setText(""+discount_price);

                    } else {
                        Log.e("prodpriceeeee", "" + SELECTED_CART.getProductPrice());
                        discount_price = 0;
                        int quatity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());
                        tvNetTotal.setText(getAmount(SELECTED_CART.getProductPrice() * quatity));
                    }
                } catch (Exception ignored) {

                }

            }
        });


        plusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    int_product_id = SELECTED_CART.getProductId();
                    if (int_product_id != 0) {
                        st_sizelist = "";
                        st_taxlist = "";
                        array_sizelist.clear();
                        //haris get_stocksize list
                        Log.e("ProductCode", "" + SELECTED_CART.getProductCode());
                        Log.e("ProductCode 1", "" + st_productcode);
                        array_sizelist.addAll(myDatabase.getProduct_sizelist(Integer.parseInt(st_productcode)));
                        st_sizelist = array_sizelist.get(0).getSizelist();
                        st_taxlist = array_sizelist.get(0).getTaxlist();
                        Log.e("st_sizelist stock", st_sizelist);
                        Log.e("st_taxlist stock", st_taxlist);
                        get_stock_sizes();
                        get_taxlist();
                        add_quantity_dialogue();
                    } else
                        Toast.makeText(getActivity(), "Choose Product ", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }
            }
        });

        Log.e("Setting adapter", "start");
        invoicesListView.setAdapter(adapter);

        returnProductAdapter = new RvReturnInvoiceAdapter(selectedItems, new OnNotifyListener() {
            @Override
            public void onNotified() {
                setTotalPriceView();


            }
        });


        if (SELECTED_SHOP != null) {
            getInvoiceList(SELECTED_SHOP.getShopId());
        }


        //   Get_OfflineInvoices();

        initView();

        btnFinish.setOnClickListener(this);
        btnAddCart.setOnClickListener(this);
        btnMakePayment.setOnClickListener(this);

        return view;
    }

    private void setShopTypeSpinner() {

        //order type spinner
        try {
            String unit = SELECTED_CART.getUnitId();

            JSONArray arr = new JSONArray(SELECTED_CART.getUnitslist());
            array_unitid.clear();
            array_unitname.clear();
            array_unitprice.clear();
            array_unitconfactor.clear();

            for (int i = 0; i < arr.length(); i++) {

                Units units = new Units();

                JSONObject jObj = arr.getJSONObject(i);
                String name = jObj.getString("unitName");
                String id = jObj.getString("unitId");
                String price = jObj.getString("unitPrice");
                String confctr = jObj.getString("con_factor");

                int unitid = TextUtils.isEmpty(id) ? 0 : Integer.valueOf(id);

                if(unitid==10){
                    confctr_kg_unit =TextUtils.isEmpty(confctr) ? 0 : Integer.valueOf(confctr);
                     price_kg = TextUtils.isEmpty(price) ? 0 : Double.valueOf(price);
                }
                if(unitid==1){
                  //  confctr_pcs_unit =TextUtils.isEmpty(confctr) ? 0 : Integer.valueOf(confctr);
                    price_pcs = TextUtils.isEmpty(price) ? 0 : Double.valueOf(price);
                }
              //  if (SELECTED_CART.getSale_unitid() == unitid) {
                    array_unitname.add(name);
                    array_unitid.add(id);
                    array_unitconfactor.add(confctr);
                    array_unitprice.add(price);
//                }
//                if (SELECTED_CART.getBase_unitid() == unitid) {
//                    array_unitname.add(name);
//                    array_unitid.add(id);
//                    array_unitconfactor.add(confctr);
//                    array_unitprice.add(price);
//                }


                //}


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        if (array_unitid.get(0).equals("" + SELECTED_CART.getBase_unitid())) {
//            String st_unitname = array_unitname.get(0);
//            String st_confactr = array_unitconfactor.get(0);
//            String st_unitprce = array_unitprice.get(0);
//            String st_unit_id = array_unitid.get(0);
//            array_unitname.remove(0);
//            array_unitid.remove(0);
//            array_unitconfactor.remove(0);
//            array_unitprice.remove(0);
//
//            array_unitname.add(st_unitname);
//            array_unitid.add(st_unit_id);
//            array_unitconfactor.add(st_confactr);
//            array_unitprice.add(st_unitprce);
//        }


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

                Log.e("Unit", "" + unit_name + "/" + unit_id);
                Log.e("confctr", "" + unit_confctr + "/" + unit_price);
                Log.e("stockqty", "" + SELECTED_CART.getStockQuantity());
                //  available_qty = SELECTED_CART.getStockQuantity()/Integer.parseInt(unit_confctr);
                //  int bal_qty = SELECTED_CART.getStockQuantity()%Integer.parseInt(unit_confctr);
                //  Log.e("available_qty", "" + available_qty );
                // Log.e("bal_qty", "" + bal_qty );
                //    edittext_availbleqty.setText(""+available_qty +"-"+bal_qty);
                //setCartProduct();
                etReturnQuantity.setFocusable(true);
//                if(available_qty>0) {
//
//                    setCartProduct();
//                    etQuantity.setFocusable(true);
//                }
//                else{
//                    etQuantity.setFocusable(false);
//                    Toast.makeText(getActivity(),"Insufficient Quantity",Toast.LENGTH_LONG).show();
//                }
                setCartValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etUnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
                Log.e("discount tot", "" + returnProductAdapter.getDiscountTotal());

                //  st_disc = ""+disc;

                if (st_totaldisctype.equals("%")) {
                    Log.e("nettotal", "" + returnProductAdapter.getNetTotal());


                    double db_disc_tot = (returnProductAdapter.getWithoutTaxTotal() - returnProductAdapter.getDiscountTotal());
                    Log.e("db_disc_total", "" + (returnProductAdapter.getWithoutTaxTotal() - returnProductAdapter.getDiscountTotal()));
                    Log.e("db_discount", "" + db_discount);
                    double discnt = db_disc_tot * db_discount / 100;
                    disc_perentge = db_discount;
                    Log.e("db_total_disc hhh", "" + db_total_disc);
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

    }

//    private void setShopTypeSpinner() {
//
//
//        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_unitname);
//        //ArrayAdapter.createFromResource(getActivity(), array_unitname, R.layout.spinner_background_dark); //R.array.cart_type
//        orderTypeAdapter.setDropDownViewResource(R.layout.spinner_list);
//        spinnerCartUnit.setAdapter(orderTypeAdapter);
//
//        try {
//
//            spinnerCartUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                    if(array_unitid.size()>0) {
//                       // Log.e("unit name",""+spinnerCartUnit.getSelectedItem().toString());
//                       // Log.e("unit from dtls",""+SELECTED_CART.getUnitselected());
//                        Log.e("Unit ID", "" + SELECTED_CART.getUnitId());
//                        Log.e("Unit PRICE", "" + SELECTED_CART.getProductPrice());
//                       // if(array_unitid.get(position).equals(SELECTED_CART.getUnitId())){
//
////                            unit_id = array_unitid.get(position);
////                            unit_name = array_unitname.get(position);
////                            unit_confctr = array_unitconfactor.get(position);
////                            //unit_price = ""+SELECTED_CART.getProductPrice();
////                            unit_price = array_unitprice.get(position);
//
//                            unit_id = array_unitid.get(position);
//                            unit_name = array_unitname.get(position);
//                            unit_confctr = ""+SELECTED_CART.getCon_factor();
//                            //unit_price = ""+SELECTED_CART.getProductPrice();
//                            unit_price = ""+(SELECTED_CART.getProductPrice()/SELECTED_CART.getCon_factor());
////                        }
////                        else{
//////                            unit_id = array_unitid.get(position);
//////                            unit_name = array_unitname.get(position);
//////                            unit_confctr = array_unitconfactor.get(position);
//////                            unit_price = array_unitprice.get(position);
////                        }
//
//
//
//                        Log.e("Unit", "" + unit_name + "/" + unit_id);
//                        Log.e("confctr", "" + unit_confctr + "/" + unit_price);
//                    }
//                    setCartValue();
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//        }catch (Exception e){
//
//        }
//
//    }

    private double roundTwoDecimals(double netPrice) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(netPrice));
    }

    private void setTotalPriceView() {
        String st_disctype = "";
        if (tax_typeflag == 0) {
            st_disctype = "TAX_INCLUSIVE";

        } else {
            st_disctype = "TAX_EXCLUSIVE";
        }
        dbl_tax_amount = 0;


        try {
            double doublee_tax = Double.parseDouble(st_tax);
            dbl_tax_amount = (returnProductAdapter.getNetTotal() * doublee_tax) / 100;
        } catch (Exception e) {

        }
        Log.e("reached", "1234");

        db_net_total = returnProductAdapter.getNetTotal();
        Log.e("db_net_total hr", "" + db_net_total);
        db_discount = edittext_discount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edittext_discount.getText().toString());

        // db_disc_total = db_net_total - returnProductAdapter.getDiscountTotal();
        db_disc_total = db_net_total;
        Log.e("discprodtot", "" + returnProductAdapter.getDiscountTotal());
//        if(discount_price>0) {
//            db_disc_total = discount_price;
//        }
        if (less_discount > 0) {
            db_disc_total = db_disc_total - less_discount;
        }

        // db_vat_amount = Double.parseDouble(""+dbl_tax_amount);
        tvNetTotal.setText("Total : " + getAmount(db_disc_total) + " " + CURRENCY);

        String net = String.valueOf("TOTAL : " + getAmount(returnProductAdapter.getNetTotal()) + " " + CURRENCY);
        // String vat = "GST  : " + getAmount(returnProductAdapter.getTaxTotal()) + " " + CURRENCY;
        String vat = " 00 " + CURRENCY;
        String grandTotal = String.valueOf("REFUND TOTAL : " + getAmount(returnProductAdapter.getGrandTotal()) + " " + CURRENCY);

        Log.e("nrtttt hr", "" + returnProductAdapter.getNetTotal());

        // textView_withoutdiscount.setText("" +db_disc_total +" "+ CURRENCY); //net total
        textView_withoutdiscount.setText("" + getAmount(returnProductAdapter.getNetTotal()) + " " + CURRENCY); //net total
        try {
            double doublee_tax = Double.parseDouble(st_tax);
            dbl_tax_amount = (returnProductAdapter.getNetTotal() * doublee_tax) / 100;
        } catch (Exception e) {

        }


        if (returnProductAdapter.getTaxTotal() != 0)
            vat = "" + getAmount(returnProductAdapter.getTaxTotal()) + " " + CURRENCY;


        dbl_tax_amount = returnProductAdapter.getTaxTotal();
        db_vat_amount = Double.parseDouble("" + returnProductAdapter.getTaxTotal());
        Log.e("db_vat_amount hrct", "" + db_vat_amount);
        db_vat_amount = roundTwoDecimals(db_vat_amount);
        //haris added on 08-09-2021
        double db_vatpercntg = 0;
        if (!VATPERCENTAGE.equals("")) {
            db_vatpercntg = Double.parseDouble(VATPERCENTAGE);
        }

//        if(SELECTED_SHOP.getState_id()>1){
//
//            tvVat_cgst.setText("IGST : " + getAmount(db_vat_amount));
//            tvVat_sgst.setVisibility(View.GONE);
//        }
//        else {
            tvVat_sgst.setVisibility(View.VISIBLE);
            tvVat_cgst.setText("CGST : " + getAmount(db_vat_amount / 2));
            tvVat_sgst.setText("SGST : " + getAmount(db_vat_amount / 2));

        if (IS_GST_ENABLED) {
            ly_taxindia.setVisibility(View.GONE);
            ly_taxoutside.setVisibility(View.VISIBLE);
            tvVat_cgst.setText("CGST : " + getAmount(db_vat_amount / 2));
            tvVat_sgst.setText("SGST : " + getAmount(db_vat_amount / 2));
        } else {
            ly_taxindia.setVisibility(View.VISIBLE);
            ly_taxoutside.setVisibility(View.GONE);
            tvVat.setText("VAT  : " + getAmount(db_vat_amount));
        }

       // }
//        tvVat_cgst.setText("CGST : " + db_vat_amount / 2);
//        tvVat_sgst.setText("SGST : " + db_vat_amount / 2);


//        if( less_discount>0) {
//            db_disc_total = 0;
//          //  db_disc_total = (returnProductAdapter.getWithoutTaxTotal() - returnProductAdapter.getDiscountTotal()) - less_discount;
//            db_disc_total = returnProductAdapter.getNetTotal()-returnProductAdapter.getDiscountTotal() - less_discount;
//            Log.e("haris 12", "" + db_disc_total);
//            double db_vat_tot = db_disc_total * db_vatpercntg/100;
//            Log.e("db_vat_tot new", "" + db_vat_tot);
//            Log.e("db_vatpercntg new", "" + db_vatpercntg);
//            Log.e("less_discount new", "" + less_discount);
//            Log.e("getWithoutTaxTotal new", "" + returnProductAdapter.getWithoutTaxTotal());
//            vat = "" +getAmount(db_vat_tot) +" " + CURRENCY;
//            Log.e("haris vat", "" + vat);
//            db_vat_amount = db_vat_tot;
//
//            db_vat_amount = roundTwoDecimals(db_vat_amount);
//            dbl_tax_amount= db_vat_amount;
//        }


//        if (less_discount > 0) {
//            db_disc_total = 0;
//            db_disc_total = (returnProductAdapter.getWithoutTaxTotal() - returnProductAdapter.getDiscountTotal()) - less_discount;
//
//            Log.e("haris 12", "" + db_disc_total);
//            Log.e("getDiscountTotal ", "" + returnProductAdapter.getDiscountTotal());
//            double db_vat_tot = db_disc_total * db_vatpercntg / 100;
//            Log.e("db_vat_tot new", "" + db_vat_tot);
//            Log.e("less_discount new", "" + less_discount);
//            Log.e("getWithoutTaxTotal new", "" + returnProductAdapter.getWithoutTaxTotal());
//
//            vat = "" + getAmount(db_vat_tot) + " " + CURRENCY;
//            Log.e("haris vat", "" + vat);
//            db_vat_amount = db_vat_tot;
//            tvVat_cgst.setText("VAT : " + db_vat_amount / 2);
//            tvVat_sgst.setText("VAT : " + db_vat_amount / 2);
//        }


        //vat = "" + getAmount(dbl_tax_amount) + " " + CURRENCY;


        /////////tvNetTotal.setText("" + net);

        //   db_grand_total = db_disc_total;
        Log.e("db_disctot", "" + db_disc_total);
        Log.e("db_vat_amount", "" + db_vat_amount);
        Log.e("dbl_tax_amount", "" + getAmount(returnProductAdapter.getTaxTotal()));
        Log.e("vat", "" + vat);
        Log.e("db_grand_total hr", "" + (db_grand_total));
        db_grand_total = db_disc_total + db_vat_amount;
        tvTotalRefund.setText("" + getAmount(db_grand_total) + " " + CURRENCY);


        // tvTotalRefund.setText("" + getAmount(returnProductAdapter.getGrandTotal()) + " " + CURRENCY);


        dbl_withouttax_total = returnProductAdapter.getNetTotal(); // Double.parseDouble(getAmount(adapter.getNetTotal()));


        dbl_withtax_total = returnProductAdapter.getGrandTotal();
        tvTotalRefund.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvTotalRefund.setSelected(true);
    }

    private void get_taxlist() {
        {

            //   try {
//
//                JSONArray arr_tx = new JSONArray(st_taxlist);
//                Log.e("taxlist hr",""+st_taxlist);
//                array_tax.clear();
//
//                for (int i =0;i<arr_tx.length(); i++){
//
//
//                    JSONObject jObj = arr_tx.getJSONObject(i);
//                    st_tax = jObj.getString("tax");
//                    st_cgst = jObj.getString("cgst");
//                    st_sgst = jObj.getString("sgst");
//
//                    Taxes s = new Taxes();
//                    s.setTax(st_tax);
//                    s.setCgst(st_cgst);
//                    s.setSgst(st_sgst);
//
//                    array_tax.add(s);
//
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            st_tax = "" + SELECTED_CART.getTax();
            Log.e("st_tax harisrah ", "" + st_tax);
            //////  textView_sales_vat.setText(""+SELECTED_CART.getTax());
//            textView_sales_cgst.setText(""+st_cgst);
//        textView_sales_sgst.setText(""+st_sgst);

        }
    }


    private void get_stock_sizes() {

        String qnty = "";
        try {

            JSONArray arr_size = new JSONArray(st_sizelist);

            array_sizestock.clear();


            for (int i = 0; i < arr_size.length(); i++) {

                Size_Return size = new Size_Return();

                JSONObject jObj = arr_size.getJSONObject(i);
                Integer size_st = jObj.getInt("sizeId");
                qnty = jObj.getString("quantity");
                String available_qty = jObj.getString("available_stock");

                Size_Return s = new Size_Return();
                s.setQuantity(qnty);
                s.setSizeId(size_st);
                s.setTotal_qty("0");
                s.setSelected_sizes("");
                s.setSize_after_edit(Float.parseFloat(qnty));
                array_sizestock.add(s);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("qnty  ddd", "" + qnty);

    }

    private void add_quantity_dialogue() {

        if (prod_id_dialogue == int_product_id) {

        }
        prod_id_dialogue = int_product_id;

        ////get_sizelist();
        etReturnQuantity.setText("");

        //   textView_sales_size.setText("");

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
        // textView_sales_size.setText(st_size_string);


    }

    private void set_recycler() {

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

                String net = String.valueOf("" + getAmount(returnProductAdapter.getWithoutTaxTotal()) + " " + CURRENCY);
                textView_withoutdiscount.setText("" + net);
                int second = 0;
                try {
                    sizeitems_selected_array.clear();
                    String st_main = sizeadapter.get_selected_sizes();
                    sizeitems_selected_array.addAll(Arrays.asList(st_main.split(",")));


                    Log.e("sizeitems_array", "" + sizeitems_selected_array);


                    // Log.e("sizeitems_array",""+sizeitems_selected_array);


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

    private void initView() {

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);

        //       return Product Recycler View
        rvProduct.setHasFixedSize(true);
        rvProduct.setItemAnimator(itemAnimator);

        //        Item Divider in recyclerView
        rvProduct.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .showLastDivider()
//                .color(getResources().getColor(R.color.divider))
                .build());

        rvProduct.setLayoutManager(new LinearLayoutManager(getContext()));

        rvProduct.setAdapter(returnProductAdapter);

    }

    private void getInvoiceList(int customerId) {

        setProgressBarMain(true);

        invoices.clear();

        if (!checkConnection()) {

            //  setErrorView(this.getString(R.string.no_internet), "", false);

            for (int v = 0; v < invoicesales.size(); v++) {

                Invoice invoice = new Invoice();

                invoice.setInvoiceId(invoicesales.get(v).getInvoiceId());
                invoice.setInvoiceNo(invoicesales.get(v).getInvoiceNo());

                invoice.setBalanceAmount(0.0f);
                invoice.setTotalAmount(0.0f);

                invoices.add(invoice);

            }


            if (invoices.isEmpty()) {
                setErrorView("No Invoices", "", false);
            } else {

                SELECTED_INVOICE = invoices.get(0);
                tvInvoiceTitle.setText(String.valueOf(SELECTED_INVOICE.getInvoiceNo()));
                setSearchableInvoiceList(invoices);

                setProgressBarInvoice(false);
                evMain.setVisibility(View.GONE);
                viewLayout.setVisibility(View.VISIBLE);

            }

            return;
        }

        final JSONObject object = new JSONObject();
        try {
            object.put(CUSTOMER_KEY, customerId);
            object.put("executive_id", EXECUTIVE_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "invoice object " + object);


        webGetAllReceipt(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "invoice response hr " + response);
                try {

                    if (response.getString("status").equals("Success")) {
                        JSONObject receiptObj = response.getJSONObject("receipt");
                        JSONArray array = receiptObj.getJSONArray("Sale");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject obj = array.getJSONObject(i);


                            if (!invoiceNos.contains(obj.getString("invoice_no"))) {

                                Invoice invoice = new Invoice();

                                invoice.setInvoiceId(obj.getString("sale_id")); ////both same
                                invoice.setInvoiceNo(obj.getString("invoice_no")); //both same

                                printLog(TAG, "invoice nos : " + obj.getString("invoice_no"));

                                invoice.setBalanceAmount(0.0f);
                                invoice.setTotalAmount(0.0f);

                                invoices.add(invoice);
                            }

                        }
                    }

                    for (int v = 0; v < invoicesales.size(); v++) {

                        Invoice invoice = new Invoice();

                        invoice.setInvoiceId(invoicesales.get(v).getInvoiceId());
                        invoice.setInvoiceNo(invoicesales.get(v).getInvoiceNo());

                        invoice.setBalanceAmount(0.0f);
                        invoice.setTotalAmount(0.0f);

                        invoices.add(invoice);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (invoices.isEmpty())
                    setErrorView("No Invoices", "", false);
                else {
                    SELECTED_INVOICE = invoices.get(0);

                    if (SELECTED_INVOICE != null) {

                        printLog("invoice selected", "::" + String.valueOf(SELECTED_INVOICE.getInvoiceNo()));

                        //  onInvoiceChange(SELECTED_INVOICE);

                        tvInvoiceTitle.setText(String.valueOf(SELECTED_INVOICE.getInvoiceNo()));

                        setSearchableInvoiceList(invoices);

                    }

                    setProgressBarInvoice(false);
                    evMain.setVisibility(View.GONE);
                    viewLayout.setVisibility(View.VISIBLE);

                    //  adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onErrorResponse(String error) {

                setErrorView(error, "", true);

            }
        }, object);
    }

    //get invoice details from webServer
    private void getInvoiceDetails(String invoiceNo) {


        setProgressBarInvoice(true);

        selectedItems.clear();
        returnProductAdapter.notifyDataSetChanged();

        clearProductView();

        tvTotalRefund.setText(String.valueOf("REFUND TOTAL : " + getAmount(returnProductAdapter.getGrandTotal()) + " " + CURRENCY));

        JSONObject object = new JSONObject();
        try {
            object.put(INVOICE_NO_KEY, invoiceNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cartItems.clear();
        Log.e(TAG, "getInvoiceDetails object " + object);
        webGetReturnInvoiceDetails(new WebService.webObjectCallback() {

            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG, "getInvoiceDetails response " + response.toString());

                try {

                    if (response.getString("status").equals("success") && !response.isNull("Sale")) {

                        JSONArray array = response.getJSONArray("Sale");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject cartObj = array.getJSONObject(i);

                            CartItem cartItem = new CartItem();

                            printLog(TAG, "getInvoiceDetails cartObj " + cartObj);

                            cartItem.setProductId(cartObj.getInt("id"));


                            // double unitPrice = cartObj.getDouble("unit_price");
                            double unitPrice = cartObj.getDouble("invoice_price");
                            cartItem.setUnitId(cartObj.getString("unit_id"));
                            double taxPrice = cartObj.getDouble("tax_amount");
                            cartItem.setProductName(cartObj.getString("name"));
                            cartItem.setProductCode(cartObj.getString("code"));
                            cartItem.setReturnQuantity(0);
                            cartItem.setSale_item_id(cartObj.getInt("saleitem_id"));
//                            cartItem.setPieceQuantity(cartObj.getInt("quantity"));
                            piece_qty = cartObj.getInt("quantity");
                            cartItem.setCon_factor(cartObj.getInt("con_factor"));
                            // cartItem.setMfg_date(cartObj.getString("mfg_date"));
                            // cartItem.setBarcode(cartObj.getString("product_barcode"));

                            cartItem.setCost((float) cartObj.getDouble("cost"));
                            cartItem.setTotalPrice(cartObj.getDouble("total"));
                          //  cartItem.setSale_unitid(cartObj.getInt("sale_unit_id"));
                           // cartItem.setBase_unitid(cartObj.getInt("base_unit_id"));
                           // cartItem.setMrprate(cartObj.getDouble("mrp_pcs"));
                           // cartItem.setProduct_hsncode(cartObj.getString("hsn_code"));
                            ArrayList<Units> unit_array = new ArrayList<>();

                            JSONArray unitArray = cartObj.getJSONArray("Units_list");
                            for (int k = 0; k < unitArray.length(); k++) {

                                JSONObject unitObject = unitArray.getJSONObject(k);

                                Units unit = new Units();

                                unit.setUnitId(unitObject.getInt("id"));
                                unit.setUnitName(unitObject.getString("name"));
                                unit.setCon_factor(unitObject.getInt("con_factor"));
                                unit.setUnitPrice(unitObject.getString("unit_price"));
                                unit_array.add(unit);
                            }

                            String gsonvalue = new Gson().toJson(unit_array);

                            Log.e("gsonvalue", gsonvalue);

                            cartItem.setUnitslist(gsonvalue);
                            float vat = (float) cartObj.getDouble("tax");

                            cartItem.setProductBonus(cartObj.getInt("single_item_bonus_amount"));

                            cartItem.setTax(vat);
                            Log.e("vat hr", "" + vat);

                            double salePrice = getSalePrice(unitPrice, cartItem.getTax(), "TAX_EXCLUSIVE");
                            cartItem.setProductPrice(unitPrice);
                            cartItem.setProductPrice_temp(unitPrice);
                            cartItem.setNetPrice(getWithoutTaxPrice(unitPrice, cartItem.getTax()));
                            cartItem.setTaxValue(getTaxPrice(unitPrice, cartItem.getTax(), "TAX_EXCLUSIVE"));
                            cartItem.setSalePrice(salePrice);
                            cartItem.setOrderType(PRODUCT_UNIT_PIESE);
                            cartItem.setOrderTypeName(PRODUCT_UNIT_PIESE);
                            cartItem.setPieceQuantity(piece_qty);
                            cartItem.setTax_type("TAX_EXCLUSIVE");


                            cartItems.add(cartItem);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (cartItems.isEmpty())
                    setErrorViewInvoiceDetails("No Data", "", false);
                else
                    setSearchableCart(cartItems);
                //get_size_string();
            }

            @Override
            public void onErrorResponse(String error) {

                setErrorViewInvoiceDetails(error, getString(R.string.error_subtitle_failed_one_more_time), true);
            }
        }, object);
    }

    public void getScannedBarcode(String str_barcode) {

        //  Toast.makeText(getActivity(), "scanned invoice: "+str_barcode, Toast.LENGTH_LONG).show();
        //  str_barcode = "8901425055335";

        Log.e("invoice cartsize", "" + cartItems.size());

        clearProductView();
        int flag = 0;

        for (int i = 0; i < cartItems.size(); i++) {

            Log.e("barcode check " + i, "" + cartItems.get(i).getBarcode());   //productList.get(i).getBarcode()

            if (str_barcode.equalsIgnoreCase(cartItems.get(i).getBarcode())) {

                flag = 1;
                SELECTED_CART = (CartItem) cartItems.get(i);

                setCartValue();

            } else {
                printLog("Scan ", "Barcode Not found");
            }
        }
        if (flag == 0) {
            Toast.makeText(getActivity(), "Barcode Not found", Toast.LENGTH_LONG).show();
        }
    }

    private void setSearchableCart(final ArrayList<CartItem> list) {

        final CartSpinnerDialog spinnerCart = new CartSpinnerDialog(getActivity(), list, "Select Product", R.style.DialogAnimations_SmileWindow);// With 	Animation

        tvProductSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerCart.showSpinerDialog("with_invoice");
            }
        });

        tvCodeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerCart.showCodeSpinerDialog("with_invoice");
            }
        });

        spinnerCart.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(Object item, int position) {
//                Log.e("position in",""+position);
//                Log.e("position qtyy",""+list.get(position).getPieceQuantity());

                // SELECTED_CART = (CartItem) list.get(position);

                SELECTED_CART = (CartItem) item;
//                Log.e("prod_id_selected crt",""+SELECTED_CART.getProductId());
                Log.e("prodprice_selected crt", "" + SELECTED_CART.getProductPrice());
                //unit_list = myDatabase.getUnitbyproduct(SELECTED_CART.getProductId());
                SELECTED_CART.setProductPrice(SELECTED_CART.getProductPrice_temp());
                //SELECTED_CART.setUnitslist(unit_list);
                unit_list = myDatabase.getUnitbyproductMaster(SELECTED_CART.getProductId());
                SELECTED_CART.setUnitslist(unit_list);

                SELECTED_CART.setSale_unitid(SELECTED_CART.getSale_unitid());
                Log.e("unit_list in", "" + SELECTED_CART.getUnitslist());
                array_unitconfactor.clear();
                array_unitname.clear();
                array_unitprice.clear();
                array_unitid.clear();
                get_unitlist(unit_list);
                array_unitid.add("1");
                array_unitname.add("Pieces");
                setShopTypeSpinner();
                if(SELECTED_SHOP.getState_id()>1){

                    tvVat_cgst.setText("IGST");
                    tvVat_sgst.setVisibility(View.GONE);
                }
                else {
                    tvVat_sgst.setVisibility(View.VISIBLE);
                    tvVat_cgst.setText("CGST");

                }
                ///setCartValue();
            }
        });

        setProgressBarInvoice(false);

        viewInvoiceDetails.setVisibility(View.VISIBLE);
        evInvoiceDetals.setVisibility(View.GONE);

    }

    private void setCartValue() {

        //try {
        Log.e("pieceqtyyyy", "" + SELECTED_CART.getPieceQuantity());
        Log.e("unit_confctr", "" + unit_confctr);
        Log.e("unit_price", "" + unit_price);

        etReturnQuantity.setText("");
        etUnitPrice.setText(getAmount(SELECTED_CART.getNetPrice()));
        float qnty_dbl = 0;
        // try {
        qnty_dbl = SELECTED_CART.getPieceQuantity() / Integer.parseInt(unit_confctr);
//            }catch (Exception e){
//
//            }
        //  int qnty_dbl = SELECTED_CART.getPieceQuantity() ;
        //tvActualQuantity.setText(String.valueOf(SELECTED_CART.getPieceQuantity()));
        tvActualQuantity.setText(String.valueOf("" + qnty_dbl));
        tvProductSpinner.setText(SELECTED_CART.getProductName());
        tvCodeSpinner.setText(SELECTED_CART.getProductCode());
        int quantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());

        st_productcode = SELECTED_CART.getProductCode();
        double productPrice = Double.parseDouble(unit_price);

        //////////////////////
        Log.e("QTY 2", "" + quantity);
        double salePrice = getSalePrice(productPrice, SELECTED_CART.getTax(), SELECTED_CART.getTax_type());
        Log.e("SALE Price", "" + salePrice);

        double netPrice = getWithoutTaxPrice(productPrice, SELECTED_CART.getTax());
        netPrice = roundTwoDecimals(netPrice);
        Log.e("NET Price", "" + netPrice);

        SELECTED_CART.setNetPrice(netPrice);

//            if (unit_name.equals(PRODUCT_UNIT_CASE)) {
//                netPrice = netPrice * SELECTED_CART.getPiecepercart();
//                SELECTED_CART.setNetPrice(netPrice);
//            }

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


    }

    private void get_unitlist(String unitlist) {
        Log.e("reached", "okkkk" + unitlist);
        Log.e("getBase_unitid", "okkkk" + SELECTED_CART.getBase_unitid());
        try {
            JSONArray arr = new JSONArray(unitlist);
            array_unitid.clear();
            array_unitname.clear();
            array_unitprice.clear();
            array_unitconfactor.clear();

            for (int i = 0; i < arr.length(); i++) {

                Units units = new Units();

                JSONObject jObj = arr.getJSONObject(i);
                String name = jObj.getString("unitName");
                String id = jObj.getString("unitId");
                String price = jObj.getString("unitPrice");
                String confctr = jObj.getString("con_factor");


                array_unitname.add(name);
                array_unitid.add(id);
                array_unitconfactor.add(confctr);
                array_unitprice.add(price);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_background_dark, array_unitname);
        //ArrayAdapter.createFromResource(getActivity(), array_unitname, R.layout.spinner_background_dark); //R.array.cart_type
        orderTypeAdapter.setDropDownViewResource(R.layout.spinner_list);
        spinnerCartUnit.setAdapter(orderTypeAdapter);
    }

    private void clearProductView() {

        tvProductSpinner.setText("");
        tvCodeSpinner.setText("");
        etReturnQuantity.setText("");
        etUnitPrice.setText("");
        tvActualQuantity.setText("");
        etProductDiscount.setText("");
        // textView_sales_size.setText("");
        // textView_sales_vat.setText("");
        SELECTED_CART = null;

        tvTotalRefund.setFocusable(true);

        hideSoftKeyboard();

    }

    private void setSearchableInvoiceList(final ArrayList<Invoice> list) {

       /* setCartProgressBar(false);
        errorView.setVisibility(View.GONE);*/

        final InvoiceSpinnerDialog spinnerCart = new InvoiceSpinnerDialog(getActivity(), list, "Select Invoice", R.style.DialogAnimations_SmileWindow);// With 	Animation

        tv_invoicelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinnerCart.showSpinerDialog();

            }
        });


        spinnerCart.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(Object item, int position) {

                //  select_Cart = (CartItem) item;//flagNoExtractUi

                Log.e("Invoice Click", "Clicked");

                SELECTED_INVOICE = (Invoice) item;

                tv_invoicelist.setText(SELECTED_INVOICE.getInvoiceNo());

                //    onInvoiceChange(SELECTED_INVOICE);

                Log.e("Selected Invoice", "" + SELECTED_INVOICE.getInvoiceNo());

                if (invoiceNos.contains(SELECTED_INVOICE.getInvoiceNo())) {
                    Log.e("Offline invoice", "" + SELECTED_INVOICE.getInvoiceNo());
                    Get_OfflineInvoiceDetails(SELECTED_INVOICE.getInvoiceId());

                } else {
                    onInvoiceChange(SELECTED_INVOICE);
                }
            }
        });

        /*code Adapter*/
       /* setCartProgressBar(false);
        lytCart.setVisibility(View.VISIBLE);*/
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

        } else if (returnQuantity > SELECTED_CART.getPieceQuantity()) {
            etReturnQuantity.setError("Actual Quantity is " + SELECTED_CART.getPieceQuantity());
            status = false;

        } else
            status = true;

        return status;
    }

    private boolean isValidate() {

        boolean status = false;
        if (SELECTED_INVOICE == null) {
            Toast.makeText(getContext(), "Please Select Invoice", Toast.LENGTH_SHORT).show();
            status = false;

        } else if (returnProductAdapter.getReturnItems().isEmpty()) {
            Toast.makeText(getContext(), "Return Products is Empty..!", Toast.LENGTH_SHORT).show();
            status = false;
        } else if (SELECTED_SHOP == null) {
            Toast.makeText(getContext(), "Please Select Customer", Toast.LENGTH_SHORT).show();
            status = false;
        } else
            status = true;
        return status;
    }


    /*private void postSaleReturn() {

        if (!isValidate())
            return;
        final ProgressDialog pd = ProgressDialog.show(getContext(), null, "Please wait...", false, false);

        final String strDate = getDateTime();
        pref_bonus = sessionAuth.getBonus();

        final Sales saleReturn = new Sales();

        saleReturn.setCustomerId(SELECTED_SHOP.getShopId());
        saleReturn.setDate(strDate);
        saleReturn.setTotal(returnProductAdapter.getGrandTotal());
        saleReturn.setSaleType("Invoice Wise");
        saleReturn.setPaid(0);
        saleReturn.setTaxTotal(returnProductAdapter.getTaxTotal());
        saleReturn.setInvoiceCode(SELECTED_INVOICE.getInvoiceNo());
        saleReturn.setCartItems(returnProductAdapter.getReturnItems());



        for (int i=0; i<returnProductAdapter.getReturnItems().size(); i++)

        {
            try {
                double amount = returnProductAdapter.getReturnItems().get(i).getNetPrice()*returnProductAdapter.getReturnItems().get(i).getPieceQuantity();
                float bonus = returnProductAdapter.getReturnItems().get(i).getProductBonus();

                Log.e("Bonus:", ""+bonus);

                double bonusamount = returnProductAdapter.getReturnItems().get(i).getPieceQuantity()*bonus;
                pref_bonus = (float) (pref_bonus - bonusamount); //returnProductAdapter.getReturnItems().get(i).getProductBonus()

                Log.e("amount tot", ""+amount);
                Log.e("Bonus Amount", ""+bonusamount);
                Log.e("pref_bonus", ""+pref_bonus);

            }catch (Exception e){

            }
        }

        sessionAuth.updateBonus(pref_bonus);

    //  String returnNumber = generateNewNumber(sessionValue.getReturnCode(SELECTED_SHOP.getRouteCode()));

        final ArrayList<CartItem> list = returnProductAdapter.getReturnItems();

        JSONObject object = new JSONObject();
        JSONObject returnObj = new JSONObject();
        final JSONArray productArray = new JSONArray();

        try {
            returnObj.put("invoice_no", SELECTED_INVOICE.getInvoiceNo());
            returnObj.put(CUSTOMER_KEY, SELECTED_SHOP.getShopId());
            returnObj.put("total", returnProductAdapter.getNetTotal());
            returnObj.put("grand_total", returnProductAdapter.getGrandTotal());

            // invoice id, shop id, net total, grand total,
            // productID, unitPrice, tax, return_quantity, refund_Amount, product_bonus

            for (CartItem c : list) {

                JSONObject obj = new JSONObject();
                obj.put("product_id", c.getProductId());
                obj.put("unit_price", c.getProductPrice());
                obj.put("tax", c.getTax()); //percentage
                obj.put("return_quantity", c.getReturnQuantity());
                obj.put("refund_Amount", c.getTotalPrice());
                obj.put("product_bonus", c.getProductBonus());
                productArray.put(obj);
            }

            returnObj.put("ReturnedProduct", productArray);

            object.put(EXECUTIVE_KEY,new SessionAuth(getContext()).getExecutiveId());
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("SalesReturn", returnObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"return data cart"+object);

        webSaleReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG,"return data RESPONSE :"+response.toString());

                try {
                    if (response.getString("status").equals("Success")) {

                        myDatabase.updateVisitStatus(SELECTED_SHOP.getShopId(), REQ_RETURN_TYPE, "");  // update sales return status to local db

                        for (CartItem c : list) {
                            myDatabase.updateStock(c,REQ_RETURN_TYPE);
                        }


                        Transaction t=new Transaction(SELECTED_SHOP.getShopId() , saleReturn.getTotal() , 0);
                        if (myDatabase.updateCustomerBalance(t)) {


                            Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ReturnPreviewActivity.class);
                            intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_INVOICE_RETURN);
                            intent.putExtra(INVOICE_RETURN_VALUE_KEY, saleReturn);
                            intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                            getActivity().startActivity(intent);
                            getActivity().finish();

                            // Reload current fragment
                       *//* Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag(FRAGMENT_INVOICE);
                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();*//*
                        }else {
                            Toast.makeText(getContext(), "Error updating customer balance", Toast.LENGTH_SHORT).show();
                        }

                    } else
                        Toast.makeText(getContext(), response.getString("status"), Toast.LENGTH_SHORT).show();

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
    }*/

    private void SaveOfflineReturn() {

        if (!isValidate())
            return;


        /*final Sales saleReturn = new Sales();

        saleReturn.setCustomerId(SELECTED_SHOP.getShopId());
        saleReturn.setDate(strDate);
        saleReturn.setTotal(returnProductAdapter.getGrandTotal());
        saleReturn.setSaleType("Invoice Wise");
        saleReturn.setPaid(0);
        saleReturn.setTaxTotal(returnProductAdapter.getTaxTotal());
        saleReturn.setInvoiceCode(SELECTED_INVOICE.getInvoiceNo());
        saleReturn.setCartItems(returnProductAdapter.getReturnItems());*/

        /*cash_inhand = sessionAuth.getCashinHand();
        float return_amount = Float.parseFloat(getAmount(returnProductAdapter.getGrandTotal()));
        float cash = cash_inhand - return_amount ;
        sessionAuth.updateCashinHand(cash);*/

        final String strDate = getDateTime();
        pref_bonus = sessionAuth.getBonus();

        for (int i = 0; i < returnProductAdapter.getReturnItems().size(); i++) {
            try {
                double amount = returnProductAdapter.getReturnItems().get(i).getNetPrice() * returnProductAdapter.getReturnItems().get(i).getPieceQuantity();
                float bonus = returnProductAdapter.getReturnItems().get(i).getProductBonus();

                Log.e("Bonus:", "" + bonus);

                double bonusamount = returnProductAdapter.getReturnItems().get(i).getPieceQuantity() * bonus;
                pref_bonus = (float) (pref_bonus - bonusamount); //returnProductAdapter.getReturnItems().get(i).getProductBonus()

                Log.e("amount tot", "" + amount);
                Log.e("Bonus Amount", "" + bonusamount);
                Log.e("pref_bonus", "" + pref_bonus);
            } catch (Exception e) {

            }
        }

        sessionAuth.updateBonus(pref_bonus);

        final ArrayList<CartItem> list = returnProductAdapter.getReturnItems();

        Sales sales = new Sales();

        sales.setInvoiceCode(SELECTED_INVOICE.getInvoiceNo());
        sales.setCustomerId(SELECTED_SHOP.getShopId());
        sales.setTotal(returnProductAdapter.getNetTotal());

        sales.setPaid(returnProductAdapter.getGrandTotal());
        sales.setDate(strDate);
        sales.setCartItems(returnProductAdapter.getCartItems());
        sales.setUploadStatus("N");

        returnOfflineList.clear();
        returnOfflineList.add(sales);

        long insertStatus = myDatabase.insertOfflineReturn(sales);

        if (insertStatus != -1) {

            for (CartItem c : sales.getCartItems()) {

                Log.e("Product Type", "" + c.getProductType());

                myDatabase.updateStock(c, REQ_RETURN_TYPE);
            }
        }

        Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), ReturnPreviewActivity.class);
        intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_INVOICE_RETURN);
        intent.putExtra(INVOICE_RETURN_VALUE_KEY, sales);
        intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

        getActivity().startActivity(intent);
        getActivity().finish();


       /* if (isNetworkConnected()) {

            returnOfflineList.clear();
            returnOfflineList.add(sales);

            OfflineSaleReturn(sales);

        }else
        {
            Toast.makeText(getContext(), "No connection", Toast.LENGTH_SHORT).show();

        }*/
    }


    private void SaveOfflineReturnNew() {
        st_executive_code = "";
        db_grand_total = roundTwoDecimals(db_grand_total);

        /////////////round off calculation////////////////////////////
        double db_roundoff = 0;
        double dbl_grand_total = db_grand_total;
//        double dbl_grand_total = Math.round(db_grand_total);
//        if (dbl_grand_total > db_grand_total) {
//            db_roundoff = dbl_grand_total - db_grand_total;
//        } else {
//            db_roundoff = dbl_grand_total - db_grand_total;
//        }
//
//        db_roundoff = roundTwoDecimals(db_roundoff);
//        Log.e("db_roundoff aftr", "" + db_roundoff);

        ////////////////////////////////////////


        Log.e("dbl_tax_amount", "" + dbl_tax_amount);
        disc_value = returnProductAdapter.getDiscountTotal() + less_discount;
        // double taxable_value = (dbl_withouttax_total - disc_value)+dbl_tax_amount;
        double taxable_value = (dbl_withouttax_total - returnProductAdapter.getDiscountTotal());
        Log.e("taxable_value", "" + taxable_value);
        //dbl_withouttax_total = dbl_withouttax_total-disc_value;
        dbl_withtax_total = dbl_withouttax_total + dbl_tax_amount;

        db_vat_amount = roundTwoDecimals(db_vat_amount);
        dbl_withtax_total = roundTwoDecimals(dbl_withtax_total);

        double db_taxtotal = roundTwoDecimals(returnProductAdapter.getTaxTotal());
        dbl_withouttax_total = roundTwoDecimals(dbl_withouttax_total);
        double db_disc_total_product = roundTwoDecimals(returnProductAdapter.getDiscountTotal());
        taxable_value = roundTwoDecimals(taxable_value);

        st_executive_code = sessionValue.getexecutivecode();
       // st_executive_code = sessionValue.getPrefRoutecode();
        //  String returnNumber = generateNewNumber(sessionValue.getReturnCode(SELECTED_SHOP.getRouteCode()));
        returnNumber = generateNewNumber(sessionValue.getReturnCode(st_executive_code));
        // returnNumber = generateNewNumber(sessionValue.getReturnCode(SELECTED_SHOP.getRouteCode()));
        double db_diff = 0;

        if (!isValidate())
            return;

        final String strDate = getDateTime();
        String st_diff = getAmount(db_diff);
        Sales sales = new Sales();
        sales.setTaxPercentage(dbl_tax_value);
        //sales.setReturn_invoiceno(SELECTED_SHOP.getRouteCode()+returnNumber);
        sales.setReturn_invoiceno(st_executive_code + returnNumber);
        sales.setWithoutTaxTotal(dbl_withouttax_total);
        //  sales.setWithTaxTotal(dbl_withtax_total);

        sales.setWithoutDiscount(db_net_total);

        sales.setInvoiceCode(SELECTED_INVOICE.getInvoiceNo());

        sales.setCustomerId(SELECTED_SHOP.getShopId());

        sales.setTotal(dbl_grand_total);
        // sales.setPaid(dbl_withtax_total);
        sales.setPaid(paid_amount);
        sales.setDate(strDate);
        sales.setTaxAmount(dbl_tax_amount);
        sales.setTaxTotal(returnProductAdapter.getTaxTotal());
        sales.setDiscount_value(disc_value);
        sales.setTaxable_total(taxable_value);
        sales.setPayment_type(st_paytype);
        sales.setTax_amount(returnProductAdapter.getTaxTotal());
        sales.setSaleLatitude("" + latitude_session);
        sales.setSaleLongitude("" + longitude_session);
        sales.setCartItems(returnProductAdapter.getCartItems());
        sales.setUploadStatus("Y");

        sales.setShopname(SELECTED_SHOP.getShopName());
        sales.setShopcode(SELECTED_SHOP.getShopCode());
        sales.setRoundoff_value(Float.parseFloat("" + db_roundoff));
        sales.setReturn_type("with");
        if (returnProductAdapter.getDiscountTotal() > 0)

            sales.setDiscount_value((float) db_disc_total_product + (float) less_discount);
        else {
            sales.setDiscount_value((float) less_discount);

        }
        sales.setDiscount((float) less_discount);
        ////
        sales.setDiscountAmount(db_disc_total);
        if (switchreceipt_type.isChecked()) {
            sales.setReturn_billtype("billwise");
        } else {
            sales.setReturn_billtype("advance");
        }

        returnOfflineList.clear();
        returnOfflineList.add(sales);
        long insertStatus = myDatabase.insertReturn(sales);

        if (switchreceipt_type.isChecked()) {
            Intent intent = new Intent(getActivity(), BillwiseReceipt.class);
            intent.putExtra(SHOP_KEY, SELECTED_SHOP);
            intent.putExtra("return_total", sales.getTotal());
            intent.putExtra("retrn_page", "with");
            intent.putExtra(INVOICE_RETURN_VALUE_KEY, sales);
            intent.putExtra("returnNumber", returnNumber);
            intent.putExtra("route_code", st_executive_code);

            startActivity(intent);
            getActivity().finish();
        } else {
            OfflineSaleReturn(sales);
        }
        // OfflineSaleReturn(sales);


    }


    //     ProgressBar Main
    private void setProgressBarMain(boolean isVisible) {

        if (isVisible) {
            pbMain.setVisibility(View.VISIBLE);
            viewLayout.setVisibility(View.GONE);
            evMain.setVisibility(View.GONE);
        } else {
            pbMain.setVisibility(View.GONE);
        }
    }

    private void setProgressBarInvoice(boolean isVisible) {

        if (isVisible) {
            pbInvoiceDetails.setVisibility(View.VISIBLE);
            viewInvoiceDetails.setVisibility(View.GONE);
            evInvoiceDetals.setVisibility(View.GONE);

        } else {
            pbInvoiceDetails.setVisibility(View.GONE);
        }
    }

    //set ErrorView
    private void setErrorView(final String title, final String subTitle, boolean isRetry) {

        viewLayout.setVisibility(View.GONE);
        evMain.setVisibility(View.VISIBLE);

        setProgressBarMain(false);
        evMain.setConfig(ErrorView.Config.create()
                .title(title)
                .subtitle(subTitle)
                .retryVisible(isRetry)
                .build());

        evMain.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {

                if (SELECTED_SHOP != null)
                    getInvoiceList(SELECTED_SHOP.getShopId());
            }
        });
    }

    //set ErrorView
    private void setErrorViewInvoiceDetails(final String title, final String subTitle, boolean isRetry) {

        viewInvoiceDetails.setVisibility(View.GONE);
        evInvoiceDetals.setVisibility(View.VISIBLE);

        setProgressBarInvoice(false);
        evInvoiceDetals.setConfig(ErrorView.Config.create()
                .title(title)
                .subtitle(subTitle)
                .retryVisible(isRetry)
                .build());

        evInvoiceDetals.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {

                if (SELECTED_INVOICE != null)
                    getInvoiceDetails(SELECTED_INVOICE.getInvoiceNo());


            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_sales_return_invoice_addCart:
                if (addCartValidate()) {
                    addToCart();
                }


                break;
            case R.id.button_sales_return_invoice_finish:
                get_latitudeandlongitude();
                if (!str_Latitude.equals("0") && !str_Longitude.equals("0")) {
                    sessionValue.save_latitude_and_longitude(str_Latitude, str_Longitude);
                }
                latitude_session = sessionValue.get_map_details().get(PREF_LATITUDE);  //latitude get from session
                longitude_session = sessionValue.get_map_details().get(PREF_LONGITUDE);  //longitude get from session

                //   SaveOfflineReturn();

                if (isNetworkConnected()) {

                    SaveOfflineReturnNew();

                } else {
                    Toast.makeText(getActivity(), "Check connectivity", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.button_return_makePayment:

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

        }
    }

    private boolean addCartValidate() {
        boolean status = false;
        int typeQuantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());
        if (TextUtils.isEmpty(tvQtyTotal.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Total Amount is Empty...!", Toast.LENGTH_SHORT).show();
            status = false;

        } else if (TextUtils.isEmpty(etUnitPrice.getText().toString().trim()) || (etUnitPrice.getText().toString().trim().equals("0"))) {
            Toast.makeText(getActivity(), "Unit price cannot be Empty...!", Toast.LENGTH_SHORT).show();
            status = false;

        } else if (TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) || (etReturnQuantity.getText().toString().trim().equals("0"))) {
            Toast.makeText(getActivity(), "Return Quantity cannot be Empty...!", Toast.LENGTH_SHORT).show();
            status = false;
        } else {
            status = true;
        }


        return status;
    }

    private void addToCart() {
        String st_disctype = "";
        if (tax_typeflag == 0) {
            st_disctype = "TAX_INCLUSIVE";

        } else {
            st_disctype = "TAX_EXCLUSIVE";
        }
        st_disctype = "TAX_EXCLUSIVE";
        Log.e("Netprice", "" + SELECTED_CART.getNetPrice());
        int quantityy = 0;
        int returnQuantity = TextUtils.isEmpty(etReturnQuantity.getText().toString().trim()) ? 0 : Integer.valueOf(etReturnQuantity.getText().toString().trim());
        float actual_qty = TextUtils.isEmpty(tvActualQuantity.getText().toString().trim()) ? 0 : Float.valueOf(tvActualQuantity.getText().toString().trim());
        if (!unit_confctr.equals(""))
            quantityy = returnQuantity * Integer.parseInt(unit_confctr);
        Log.e("qnty aftr", "" + quantityy);


        SELECTED_CART.setTypeQuantity(returnQuantity);
        //SELECTED_CART.setReturnQuantity(returnQuantity);
        // SELECTED_CART.setPieceQuantity_nw(returnQuantity);
        SELECTED_CART.setTotalPrice(SELECTED_CART.getSalePrice() * returnQuantity);
        SELECTED_CART.setProductBonus(SELECTED_CART.getProductBonus());

        SELECTED_CART.setCon_factor(Integer.parseInt(unit_confctr));
        SELECTED_CART.setPieceQuantity_nw(returnQuantity);
        SELECTED_CART.setReturnQuantity(quantityy);

        // SELECTED_CART.setPieceQuantity(quantityy);
        SELECTED_CART.setCon_factor(Integer.parseInt(unit_confctr));
        SELECTED_CART.setTax_type(st_disctype);
        SELECTED_CART.setUnitselected(unit_name);
        SELECTED_CART.setProduct_hsncode(SELECTED_CART.getProduct_hsncode());
        if (etProductDiscount.getText().toString().isEmpty()) {
            SELECTED_CART.setProductDiscount(0);
        } else {
            SELECTED_CART.setProductDiscount(Double.parseDouble(etProductDiscount.getText().toString().trim()));
        }
        SELECTED_CART.setProductTotal(Double.parseDouble(getAmount(Double.parseDouble(tvNetTotal.getText().toString().trim()))));
        SELECTED_CART.setProductTotalValue(Double.parseDouble(getAmount(Double.parseDouble(tvNetTotal.getText().toString().trim()))));


        Log.e("getTaxValue ", "" + SELECTED_CART.getTaxValue());

//                    SELECTED_CART.setProductTotal(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
//                    SELECTED_CART.setProductTotalValue(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
//
//                    //haris added on 25-11-2020
//                    SELECTED_CART.setProductTotal_withoutdisc(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));
//                    SELECTED_CART.setTax_valuewithoutdisc(Double.parseDouble(getAmount(Double.parseDouble(tvQtyTotal.getText().toString().trim()))));

        try {

            SELECTED_CART.setProductPrice(Double.parseDouble("" + etUnitPrice.getText().toString()));
        } catch (Exception e) {

        }
        if (st_disctype.equals("TAX_INCLUSIVE")) {
            SELECTED_CART.setProductTotal(SELECTED_CART.getProductTotalValue() - (SELECTED_CART.getTaxValue() * SELECTED_CART.getPieceQuantity_nw()));
            SELECTED_CART.setProductPriceNew(SELECTED_CART.getProductTotalValue() - SELECTED_CART.getTaxValue());
            Log.e("getProductTotal 123", "" + SELECTED_CART.getProductTotal());
        }


        //   double netPrice = getWithoutTaxPrice(productPrice, select_Cart.getTax());
        //  tvNetTotal.setText("Total : " + getAmount(SELECTED_CART.getProductTotal()) + " " + CURRENCY);
        double netPrice = 0;
        if (!etUnitPrice.getText().toString().isEmpty()) {
            netPrice = Double.parseDouble(etUnitPrice.getText().toString().trim());
        } else {
            netPrice = 0;
        }
        SELECTED_CART.setNetPrice(netPrice);

        if(st_disctype.equals("TAX_INCLUSIVE")) {
            double unitprice =productprice_inclusive(SELECTED_CART.getNetPrice(),SELECTED_CART.getTax());
            if(unitprice>0){
                unitprice = Double.parseDouble(getAmount(unitprice));
            }
            Log.e("unitprice..",""+unitprice);
            SELECTED_CART.setProductPrice(unitprice);
            netPrice = SELECTED_CART.getProductPrice();
            Log.e("prodtot..1",""+SELECTED_CART.getProductTotal());
            Log.e("netPrice..1",""+netPrice);
            //  netPrice = Double.parseDouble(getAmount(netPrice));
            Log.e("netPrice..2",""+netPrice);
            SELECTED_CART.setProductTotal((netPrice*returnQuantity)-SELECTED_CART.getProductDiscount()*returnQuantity);
            SELECTED_CART.setProductTotalValue((netPrice*returnQuantity)-SELECTED_CART.getProductDiscount()*returnQuantity);

        }
        SELECTED_CART.setConfactr_kg(confctr_kg_unit);
        SELECTED_CART.setUnitid_selected(unit_id);
        SELECTED_CART.setUnit_confactor(unit_confctr);
        SELECTED_CART.setUnit_rate_kg(price_kg);
        SELECTED_CART.setUnit_rate_pcs(price_pcs);
        SELECTED_CART.setTaxValue(getTaxPrice((SELECTED_CART.getProductPrice() - SELECTED_CART.getProductDiscount()), SELECTED_CART.getTax(), SELECTED_CART.getTax_type()));
        returnProductAdapter.returnItem(SELECTED_CART);
        //setShopTypeSpinner();
        clearProductView();
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

    private String update_sizeand_quantity_aftresale() {

        String gsonvalue_size = "";

        ArrayList<Size_Return> size_array = new ArrayList<>();
        for (Size_Return s : array_sizeanqty_aftersale) {
            s.setSizeId(Integer.parseInt(s.getReturn_size()));
            s.setQuantity("" + s.getNew_qnty_aftersale()); //sale affected stock
            s.setNew_qnty_aftersale(s.getNew_qnty_aftersale());
            s.setSize_stock("" + s.getReturn_qnty()); //sale not affected stock
            s.setSize_after_edit(Float.parseFloat(s.getReturn_qnty())); //for edit after cart add

            s.setTotal_qty(s.getTotal_qty()); //for edit after cart add

            //s.setAvailable_stock(int_product_qty);
            size_array.add(s);
        }

        gsonvalue_size = new Gson().toJson(size_array);
        //product.setSizelist(gsonvalue_size);
        Log.e("harisssssssssss", "" + gsonvalue_size);
        return gsonvalue_size;


    }

    private String update_sizeand_quantity_aftresalenew() {

        String gsonvalue_siz = "";
        int if_flag = 0, else_flag = 0;

        ArrayList<Size_Return> size_array = new ArrayList<>();
        ArrayList<String> sizearray_string = new ArrayList<>();

        int st_sizeid = 0;
        int newqnty = 0;
        size_array.clear();
        Log.e("array_sizestocksz", "" + array_sizestock.size());
        Log.e("array_s_aftersalesz", "" + array_sizeanqty_aftersale.size());
        int flag_check = 0;

        for (Size_Return s : array_sizestock) {
            flag_check = 0;
            for (Size_Return r : array_sizeanqty_aftersale) {

                if (s.getSizeId().equals(r.getSizeId())) {

                    Log.e("getTotal_qty", "" + r.getTotal_qty());

                    if (!r.getTotal_qty().equals("0")) {
                        Log.e("hrin", "" + r.getTotal_qty());
                        //float no = Float.parseFloat(r.getTotal_qty());
                        //int qnty = Math.round(no);

                        flag_check = 1; //check same size get or not
                        newqnty = Integer.parseInt(r.getTotal_qty()) + Integer.parseInt(s.getQuantity());

                        else_flag = 0;
                        if_flag = 1;
                        st_sizeid = r.getSizeId();


                    } else {
                        Log.e("sze else s", "" + s.getSizeId());


                        if_flag = 0;
                        else_flag = 1;

                    }
                } else {

                    if (flag_check == 0) {
                        if_flag = 0;
                        else_flag = 1;
                    }

                }


            }

            Size_Return n = new Size_Return();
            if (if_flag == 1) {
                if_flag = 0;
                else_flag = 0;
                Log.e("if_flag", "" + newqnty);
                n.setSizeId(st_sizeid);
                n.setQuantity("" + newqnty); //sale affected stock
                n.setNew_qnty_aftersale(s.getNew_qnty_aftersale());
                n.setSize_stock("" + s.getReturn_qnty()); //sale not affected stock
                //n.setSize_after_edit(Float.parseFloat(s.getReturn_qnty())); //for edit after cart add
                n.setTotal_qty(s.getTotal_qty()); //for edit after cart add
//
//                        //s.setAvailable_stock(int_product_qty);
//                        size_array.add(n);

                sizearray_string.add("" + s.getSizeId());
                sizearray_string.add("" + newqnty);

                st_sizeid = 0;
                newqnty = 0;

            }
            if (else_flag == 1) {
                if_flag = 0;
                else_flag = 0;
                Log.e("else_flag", "" + s.getQuantity());
                n.setSizeId(s.getSizeId());
                n.setQuantity("" + s.getQuantity()); //sstock qty
                n.setNew_qnty_aftersale(s.getNew_qnty_aftersale());
                n.setSize_stock("" + s.getReturn_qnty()); //sale not affected stock
                // n.setSize_after_edit(Float.parseFloat(s.getReturn_qnty())); //for edit after cart add
                n.setTotal_qty(s.getTotal_qty()); //for edit after cart add
//                    size_array.add(n);

                sizearray_string.add("" + s.getSizeId());
                sizearray_string.add("" + s.getQuantity());
            }
            size_array.add(n);


        }

        gsonvalue_siz = new Gson().toJson(size_array);
        Log.e("gsonvalue_siz", gsonvalue_siz);
        Log.e("sizearray_string", "" + sizearray_string);


        return gsonvalue_siz;

    }

    public void onInvoiceChange(Invoice invoice) {

        this.SELECTED_INVOICE = invoice;
        getInvoiceDetails(SELECTED_INVOICE.getInvoiceNo());
        tvInvoiceTitle.setText(String.valueOf(SELECTED_INVOICE.getInvoiceNo()));


    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        try {
            if (getActivity().getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }

        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private static String getDateTime() {
        Date date = new Date();
        return dbDateFormat.format(date);
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

    /**
     * Check InterNet
     */
    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }


    private void Get_OfflineInvoices() {

        Log.e("inside invoice", "1");
        invoiceNos.clear();
        invoicesales.clear();

        invoicesales.addAll(myDatabase.getOfflineInvoiceNumber());

        Log.e("inside invoice", "" + invoicesales.size());

        if (!invoicesales.isEmpty()) {

            for (int b = 0; b < invoicesales.size(); b++) {

                invoiceNos.add(invoicesales.get(b).getInvoiceNo());

                Log.e("Invoice num", "" + invoicesales.get(b).getInvoiceNo());

            }

            getInvoiceList(SELECTED_SHOP.getShopId());

        } else {
            getInvoiceList(SELECTED_SHOP.getShopId());
        }
    }

    private void Get_OfflineInvoiceDetails(String id) {

        cartItems.clear();
        cartItemsOffline.clear();

        selectedItems.clear();
        returnProductAdapter.notifyDataSetChanged();

        clearProductView();

        returnProductAdapter.notifyDataSetChanged();

        cartItemsOffline = myDatabase.getOfflineSaleProductReturn(Integer.parseInt(id));

        Log.e("cart items size", "" + cartItemsOffline.size());

        for (int i = 0; i < cartItemsOffline.size(); i++) {

            CartItem cartItem = new CartItem();

            cartItem.setProductId(cartItemsOffline.get(i).getProductId());
            cartItem.setProductName(cartItemsOffline.get(i).getProductName());
            cartItem.setProductCode(cartItemsOffline.get(i).getProductCode());
            cartItem.setReturnQuantity(0);
            cartItem.setPieceQuantity(cartItemsOffline.get(i).getPieceQuantity());
            cartItem.setPieceQuantity_nw(cartItemsOffline.get(i).getPieceQuantity());
            cartItem.setCost(cartItemsOffline.get(i).getCost());
            cartItem.setTotalPrice(cartItemsOffline.get(i).getTotalPrice());

            cartItem.setProductBonus(cartItemsOffline.get(i).getProductBonus());
            cartItem.setTax(cartItemsOffline.get(i).getTax());
            cartItem.setProductPrice(cartItemsOffline.get(i).getProductPrice());
            cartItem.setNetPrice(cartItemsOffline.get(i).getNetPrice());
            cartItem.setTaxValue(cartItemsOffline.get(i).getTaxValue());
            cartItem.setSalePrice(cartItemsOffline.get(i).getSalePrice());
            cartItem.setOrderType(PRODUCT_UNIT_PIESE);
            cartItem.setOrderTypeName(PRODUCT_UNIT_PIESE);

            Log.e("Returned Codes", "//" + cartItemsOffline.get(i).getProductCode());
            Log.e("netprice", "//" + cartItem.getNetPrice());

            cartItems.add(cartItem);

        }

        if (cartItems.isEmpty()) {
            setErrorViewInvoiceDetails("No Data", "", false);
        } else {
            setSearchableCart(cartItems);
        }
    }

    //   Offline Return

    private void OfflineSaleReturn(final Sales salesdone) {
        final ProgressDialog pd = ProgressDialog.show(context, null, "Please wait...", false, false);

        JSONObject object = new JSONObject();
        JSONArray returntarray = new JSONArray();

        try {
            final String strDate = getDateTime();
            for (Sales s : returnOfflineList) {

                final JSONObject returnObj = new JSONObject();
                returnObj.put("invoice_id", s.getInvoiceCode());
                //returnObj.put("invoice_id", "0");
                returnObj.put(CUSTOMER_KEY, s.getCustomerId());
                returnObj.put("tax_total", getAmount(s.getTaxAmount()));
                returnObj.put("total_amount", getAmount(s.getTotal()));
                returnObj.put("return_date", strDate);
                returnObj.put("without_tax_total", s.getTaxable_total());
                returnObj.put("discount", s.getDiscount());
                returnObj.put("discount_total", getAmount(s.getDiscountAmount()));
                returnObj.put("latitude", s.getSaleLatitude());
                returnObj.put("longitude", s.getSaleLongitude());
                returnObj.put("invoice_type", s.getPayment_type());
                returnObj.put("return_invoiceno", s.getReturn_invoiceno());
                returnObj.put("round_off", getAmount(s.getRoundoff_value()));
                returnObj.put(EXECUTIVE_KEY, EXECUTIVE_ID);
                returnObj.put("return_receipt_type", s.getReturn_billtype());
                //added by haris on 15-12-2022
                returnObj.put("invoice_type", s.getPayment_type());
                returnObj.put("amount_return", s.getPaid());


                JSONArray productArray = new JSONArray();

                for (CartItem c : s.getCartItems()) {
                    int tx_type = 0;
                    double unit_price = c.getProductPrice();
                    if (c.getTax_type().equals("TAX_INCLUSIVE")) {
                        tx_type = 1;
                        unit_price = c.getProductTotal() / c.getPieceQuantity_nw();

                    }

                    unit_price = roundTwoDecimals(unit_price);
                    //  double tax_prod = getTaxPrice(c.getProductTotal(),c.getTax(),c.getTax_type());
                    double tax_prod = getTaxPrice(c.getProductTotalValue(), c.getTax(), c.getTax_type());

                    JSONObject obj = new JSONObject();

                    obj.put("product_id", c.getProductId());
                    obj.put("unit_price", unit_price);
                    obj.put("tax_rate", c.getTax());
                    obj.put("tax_amount", tax_prod);
                    obj.put("return_quantity", c.getPieceQuantity_nw());
                    obj.put("product_unit", c.getUnitselected());

                    obj.put("product_discount", c.getProductDiscount());
                    obj.put("product_total", c.getProductTotal());
                    obj.put("saleitem_id", c.getSale_item_id());
                    obj.put("tax_type", tx_type);


                    productArray.put(obj);
                }

                returnObj.put("ReturnedProduct", productArray);
                returntarray.put(returnObj);
            }

            object.put(EXECUTIVE_KEY, EXECUTIVE_ID);
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("SalesReturn", returntarray);

            pd.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        printLog(TAG, "Offline Return object  " + object);


        webOfflineReturn(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

                printLog(TAG, "Offline Return Response  " + response);

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {

                        long insertStatus = myDatabase.insertOfflineReturn(salesdone);
                        //  sessionValue.storeReturnCode(SELECTED_SHOP.getRouteCode(), returnNumber);
                        sessionValue.storeReturnCode(st_executive_code, returnNumber);
                        Log.e("retinvoiceno", "" + salesdone.getReturn_invoiceno());
                        if (insertStatus != -1) {

                            for (CartItem c : salesdone.getCartItems()) {

                                Log.e("Product Type", "" + c.getProductType());

                                myDatabase.updateStock(c, REQ_RETURN_TYPE);
                            }
                        }


                        pref_bonus = sessionAuth.getBonus();

                        Transaction t = new Transaction(SELECTED_SHOP.getShopId(), salesdone.getTotal(), 0);
                        if (myDatabase.updateCustomerBalance(t)) {

                            for (int i = 0; i < returnProductAdapter.getReturnItems().size(); i++) {
                                try {
                                    double amount = returnProductAdapter.getReturnItems().get(i).getNetPrice() * returnProductAdapter.getReturnItems().get(i).getPieceQuantity();
                                    float bonus = returnProductAdapter.getReturnItems().get(i).getProductBonus();

                                    Log.e("Bonus:", "" + bonus);

                                    double bonusamount = returnProductAdapter.getReturnItems().get(i).getPieceQuantity() * bonus;
                                    pref_bonus = (float) (pref_bonus - bonusamount); //returnProductAdapter.getReturnItems().get(i).getProductBonus()

                                    Log.e("amount tot", "" + amount);
                                    Log.e("Bonus Amount", "" + bonusamount);
                                    Log.e("pref_bonus", "" + pref_bonus);
                                } catch (Exception e) {

                                }
                            }

                            sessionAuth.updateBonus(pref_bonus);

                            Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ReturnPreviewActivity.class);
                            intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_INVOICE_RETURN);
                            intent.putExtra(INVOICE_RETURN_VALUE_KEY, salesdone);
                            intent.putExtra(SHOP_VALUE_KEY, SELECTED_SHOP);

                            getActivity().startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Error updating customer balance", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(context, "Return " + response.getString("status"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        }, object);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
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

