package com.advanced.minhas.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.RvPreviewCartAdapter;
import com.advanced.minhas.config.ConfigValue;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.PdfModel;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Transaction;
import com.advanced.minhas.printerconnect.connecter.P25ConnectionException;
import com.advanced.minhas.printerconnect.connecter.P25Connector;
import com.advanced.minhas.printerconnect.pockdata.PocketPos;
import com.advanced.minhas.printerconnect.printutil.DateUtil;
import com.advanced.minhas.printerconnect.printutil.FontDefine;
import com.advanced.minhas.printerconnect.printutil.Printer;
import com.advanced.minhas.qrtags.InvoiceDate;
import com.advanced.minhas.qrtags.InvoiceTaxAmount;
import com.advanced.minhas.qrtags.InvoiceTotalAmount;
import com.advanced.minhas.qrtags.QRBarcodeEncoder;
import com.advanced.minhas.qrtags.Seller;
import com.advanced.minhas.qrtags.TaxNumber;
import com.advanced.minhas.session.SessionValue;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;
import com.itextpdf.text.pdf.languages.LanguageProcessor;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.rey.material.widget.Button;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.INVOICE_RETURN_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.PRODUCT_UNIT_CASE;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.CustomConverter.convertNumberToEnglishWords;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getAmountthree;
import static com.advanced.minhas.config.Generic.getMaximumChar;
import static com.advanced.minhas.config.Generic.getPrintDate;
import static com.advanced.minhas.config.Generic.splitToNChar;
import static com.advanced.minhas.config.Generic.stringToDate;
import static com.advanced.minhas.config.PrintConsole.printLog;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ACCNO;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_1;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_1_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_2;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_2_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_BANKACCNAME;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_BANKBRANCH;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_BANKNAME;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_CR;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_EMAIL;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_FSSAI_NO;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_IFSC;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME_LOGIN;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_PAN_NO;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_VAT;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_WEBSITE;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_ID;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_QRCODE_LINK;

public class ReturnPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "ReturnPreviewActivity";
    String strShopName = "",strShopNameArabic = "",strCustomerVat="",strCustomerNo="",
            strBillNumber = "", strShopLocation = "", strDate = "", CURRENCY = "",
            cust_outstanding ="",str_kafeel_VatNo="",str_paymenttype = "",
            new_Outstanding = "", str_Previous_balance="",  st_sizelist_stock ="",str_qrcodelink = "",
            str_bankname="",str_bankaccname="",str_accno="",str_ifsc="",str_bankbranch="",companyFssai_no="";
    int callingActivity = 0;
    private Shop SELECTED_SHOPE = null;
    private Sales SELECTED_RETURN = null;
    private RecyclerView recyclerView;
    private Button btnPrint, btnHome, btnConnect, btnEnable,btnbackground_print;
    private TextView tvTitle, tvNetTotal, tvInvoiceNo, tvDate, tvShopDetails,
            tv_grandTotal, tv_Vat,textView_discount,tv_afterdscnt,tv_roundoff;
    private AppCompatSpinner spinnerDevice;
    MyDatabase myDatabase;
    private double paid_amount=0 , db_grandtot =0 ,disc_percent=0,net_Amount =0;
    float fl_discount = 0;
    double discnt = 0 ,after_disc =0;
    private ViewGroup connectionView;

    private SessionValue sessionValue;

    private ArrayList<CartItem> cartItems = null;

    private static String FILE_PATH = Environment.getExternalStorageDirectory() + "/icresp_return.pdf";

    private final int MAX_LINE = 16;//25

    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;

    private BluetoothAdapter mBluetoothAdapter;

    private P25Connector mConnector;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    showDisabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED) {
                    showToast("Paired");

                    connect();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_preview);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_returnPreview);

        btnPrint = (Button) findViewById(R.id.button_returnPreview_print);
        btnHome = (Button) findViewById(R.id.button_returnPreview_home);
        btnConnect = (Button) findViewById(R.id.button_returnPreview_connect);

        btnEnable = (Button) findViewById(R.id.button_returnPreview_connection_enabled);

        tvTitle = (TextView) findViewById(R.id.textView_returnPreview_title);
        tvNetTotal = (TextView) findViewById(R.id.textView_returnPreview_netTotal);
        tvShopDetails = (TextView) findViewById(R.id.textView_returnPreview_shopDetails);
        tvInvoiceNo = (TextView) findViewById(R.id.textView_returnPreview_invoiceNo);
        tvDate = (TextView) findViewById(R.id.textView_returnPreview_date);
        btnbackground_print = findViewById(R.id.button_background_print);
        spinnerDevice = (AppCompatSpinner) findViewById(R.id.spinner_returnPreview_devices);
        connectionView=(ViewGroup)findViewById(R.id.connectionView_returnPreview);

        tv_grandTotal = (TextView) findViewById(R.id.textView_grand_total);
        tv_Vat = (TextView) findViewById(R.id.textView_sales_vat);
        textView_discount = findViewById(R.id.textView_preview_discount);
        tv_afterdscnt = (TextView)findViewById(R.id.textView_return_afterdiscount);
        tv_roundoff = findViewById(R.id.textView_return_roundoff);
        sessionValue =new SessionValue(ReturnPreviewActivity.this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        str_kafeel_VatNo = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);
        myDatabase = new MyDatabase(this);
        str_qrcodelink = sessionValue.getCompanyDetails().get(PREF_QRCODE_LINK);
        if (isStoragePermissionGranted()){

        }
        if (sessionValue.isPOSPrint()){
            connectionView.setVisibility(View.VISIBLE);
            if (mBluetoothAdapter == null) {
                showUnsupported();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    showDisabled();
                } else {
                    showEnabled();

                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    if (pairedDevices != null) {
                        mDeviceList.addAll(pairedDevices);

                        updateDeviceList();
                    }
                }

                mProgressDlg = new ProgressDialog(this);

                mProgressDlg.setMessage("Scanning...");
                mProgressDlg.setCancelable(false);
                mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        mBluetoothAdapter.cancelDiscovery();
                    }
                });

                mConnectingDlg = new ProgressDialog(this);

                mConnectingDlg.setMessage("Connecting...");
                mConnectingDlg.setCancelable(false);

                mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {

                    @Override
                    public void onStartConnecting() {
                        mConnectingDlg.show();
                    }

                    @Override
                    public void onConnectionSuccess() {
                        mConnectingDlg.dismiss();

                        showConnected();
                    }

                    @Override
                    public void onConnectionFailed(String error) {
                        mConnectingDlg.dismiss();
                    }

                    @Override
                    public void onConnectionCancelled() {
                        mConnectingDlg.dismiss();
                    }

                    @Override
                    public void onDisconnected() {
                        showDisonnected();
                    }
                });


            }


            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

            registerReceiver(mReceiver, filter);
            btnPrint.setText("Print POS");
        }else {
            connectionView.setVisibility(View.GONE);
            btnPrint.setEnabled(true);
            btnPrint.setText("Print PDF");

        }

/******/


        try {

            SELECTED_SHOPE = (Shop) getIntent().getSerializableExtra(SHOP_VALUE_KEY);

            SELECTED_RETURN = (Sales) (getIntent().getSerializableExtra(INVOICE_RETURN_VALUE_KEY));


            strShopName = SELECTED_SHOPE.getShopName();
            strShopLocation = SELECTED_SHOPE.getShopAddress();
            str_paymenttype = SELECTED_RETURN.getPayment_type();
            if (SELECTED_SHOPE.getShopArabicName() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopArabicName()))
                strShopNameArabic = SELECTED_SHOPE.getShopArabicName();

            if (SELECTED_SHOPE.getVatNumber() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getVatNumber()))
                strCustomerVat = SELECTED_SHOPE.getVatNumber();

            if (SELECTED_SHOPE.getShopCode() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopCode()))
                strCustomerNo = SELECTED_SHOPE.getShopCode();

            if (SELECTED_RETURN == null)
                finish();



            //bank details

            str_bankname = sessionValue.getCompanyDetails().get(PREF_COMPANY_BANKNAME);
            str_bankaccname = sessionValue.getCompanyDetails().get(PREF_COMPANY_BANKACCNAME);
            str_accno = sessionValue.getCompanyDetails().get(PREF_COMPANY_ACCNO);
            str_ifsc = sessionValue.getCompanyDetails().get(PREF_COMPANY_IFSC);
            str_bankbranch = sessionValue.getCompanyDetails().get(PREF_COMPANY_BANKBRANCH);
             companyFssai_no = sessionValue.getCompanyDetails().get(PREF_COMPANY_FSSAI_NO);
            final Date date = stringToDate(SELECTED_RETURN.getDate());
            strDate = getPrintDate(date);


            cartItems = SELECTED_RETURN.getCartItems();
            // strBillNumber = SELECTED_RETURN.getInvoiceCode();
            strBillNumber = SELECTED_RETURN.getReturn_invoiceno();


            paid_amount = SELECTED_RETURN.getPaid();


            callingActivity = getIntent().getIntExtra(CALLING_ACTIVITY_KEY,0);

            switch (callingActivity) {
                case ActivityConstants.ACTIVITY_INVOICE_RETURN:

                    tvTitle.setText("INVOICE RETURN");

                    break;
                case ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN:

                    tvTitle.setText("WITHOUT INVOICE RETURN");

                    break;
                default:

            }
        } catch (NullPointerException e) {
            printLog(TAG, "NullPointerException exception   " + e.getMessage());
        }

        if(SELECTED_RETURN.getReturn_type().equals("with")){
            tvTitle.setText("INVOICE RETURN");
        }
        else{
            tvTitle.setText("WITHOUT INVOICE RETURN");
        }

        tvDate.setText(strDate);

        tvShopDetails.setText(strShopName + "\n" + strShopLocation);
        tvInvoiceNo.setText(strBillNumber);

        if (cartItems != null)
            setRecyclerView();


        btnPrint.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnEnable.setOnClickListener(this);
        btnbackground_print.setOnClickListener(this);
    }


    @SuppressLint("SetTextI18n")
    private void setRecyclerView() {



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
//                .color(getResources().getColor(R.color.divider))
                .build());

        recyclerView.setAdapter(new RvPreviewCartAdapter(cartItems));

        //String net = String.valueOf("TOTAL     : " + getAmount(getNetTotal()) + " " + CURRENCY);
        String net = String.valueOf("TOTAL     : " + getAmount(getNetTotal() -(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount())) + " " + CURRENCY);
        //String vat = "VAT    : " + getAmount(getTaxTotal()) + " " + CURRENCY;

        String vat = "VAT    : " + getAmount(SELECTED_RETURN.getTaxAmount()) + " " + CURRENCY;
        String grandTotal = String.valueOf("GRAND TOTAL : " + getAmount(getGrandTotal()) + " " + CURRENCY);
        net_Amount= getNetTotal() -(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount());
        Log.e("getNetTotal",""+getNetTotal());
        Log.e("getNetTotal",""+SELECTED_RETURN.getDiscount());
        //  tvNetTotal.setText(String.valueOf(net + ",\t\t" + vat + ",\t\t" + grandTotal));


        tvNetTotal.setText(""+net);
        tv_Vat.setText(""+vat);
        // tv_grandTotal.setText(""+ getAmount(getGrandTotal()) + " " + CURRENCY);
        tv_grandTotal.setText(""+  getAmount(SELECTED_RETURN.getTotal())  +" " + CURRENCY);
        db_grandtot = Double.parseDouble(getAmount(SELECTED_RETURN.getTotal()));
        Log.e("fl_discount else",""+fl_discount);
        fl_discount = SELECTED_RETURN.getDiscount();
        //fl_discount = (float)SELECTED_SALES.getDiscount_value();
        discnt=roundTwoDecimals(fl_discount);
        Log.e("fl_discount else 2",""+discnt);
        //discnt = discnt
        after_disc = getNetTotal() -(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount())-discnt;
        textView_discount.setText("Discount  : "+discnt);
        tv_afterdscnt.setText("AFTER DISCOUNT : "+ getAmount(after_disc)+ " " + CURRENCY);
        tv_roundoff.setText("RoundOff      :  "+getAmount(SELECTED_RETURN.getRoundoff_value()));

//        printer connect
        if (sessionValue.isPOSPrint())
            connect();
    }

    public double getNetTotal() {

        double netTotal = 0;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getNetPrice() != 0.0) {
                //double d = cartItem.getNetPrice() * cartItem.getReturnQuantity();
//                double d = cartItem.getNetPrice() * cartItem.getPieceQuantity_nw();
//                netTotal += d;
                double net = cartItem.getNetPrice();
                if(cartItem.getTax_type().equals("TAX_INCLUSIVE")){
                    net = cartItem.getProductTotal()/cartItem.getPieceQuantity_nw();
                }
                double d = net * cartItem.getPieceQuantity_nw();
                netTotal += d;
            }
        }
        return netTotal;
    }

    public double getGrandTotal() {

        double grandTotal = 0.0;

        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                if (cartItem.getTotalPrice() != 0.0) {
                    double f = cartItem.getTotalPrice();
                    grandTotal += f;
                }
            }
        }
        return grandTotal;
    }


    public double getTaxTotal() {
        double totalTax = 0.0;

        if (!cartItems.isEmpty()) {
            for (CartItem c : cartItems) {
                if (c.getTaxValue() != 0.0) {
                    double f = c.getTaxValue() * c.getReturnQuantity();
                    totalTax += f;
                }
            }
        }
        return totalTax;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.button_returnPreview_print:

                if (sessionValue.isPOSPrint())
                    printReturnPOS(cartItems);
                else{
                    if (isStoragePermissionGranted())
                        // printReturnPDF(getPdfModels(cartItems));
                        //  printwithbackgroundInvoice(getPdfModels(cartItems));

                        if(SELECTED_SHOPE.getState_id()>1) {
                            printwithbackgroundInvoiceigst(getPdfModels(cartItems));

                        }
                        else{
                            printwithbackgroundInvoice(getPdfModels(cartItems));
                        }
                }


                break;
            case R.id.button_returnPreview_home:

                onBackPressed();

                break;
            case R.id.button_returnPreview_connect:
                connect();
                break;

            case R.id.button_returnPreview_connection_enabled:

                if (sessionValue.isPOSPrint()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1000);
                }

                break;
            case R.id.button_background_print :
                if (isStoragePermissionGranted())
                    printReturnPDF(getPdfModels(cartItems));


                break;

        }
    }

    private void printwithbackgroundInvoiceigst(List<PdfModel> pdfModels) {

        //flag ="0";
        File myFile = null;

        PdfWriter writer;
        try {
            String compName ="";

            compName = sessionValue.getCompanyDetails_login().get(PREF_COMPANY_NAME_LOGIN);
            if(compName.equals("")) {
                compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
            }
            String compNameArab = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME_ARAB);  //name get from session
            String address1Str = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
            String address1ArabStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1_ARAB);  //address get from session
            String address2Str = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_2);  //address get from session
            String address2ArabStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_2_ARAB);  //address get from session
            String compEmailStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_EMAIL);  //address get from session
            String mobileStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_MOBILE);  //address get from session
            String comp_web = sessionValue.getCompanyDetails().get(PREF_COMPANY_WEBSITE);  //address get from session
            String compDateStr = strDate;  //date get from session
            String compbillStr = strBillNumber;  //date get from session


            String compny_phone =sessionValue.getCompanyDetails().get(PREF_COMPANY_MOBILE);
            String comp_mail =  sessionValue.getCompanyDetails().get(PREF_COMPANY_EMAIL);  //address get from session

            String compny_name = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);
            String address_new = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);


            String compRegisterStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_CR);
            String companyVatStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
            //haris added on 06-11-2020
            String companyPan_No = sessionValue.getCompanyDetails().get(PREF_COMPANY_PAN_NO);
            Log.e("companyPan_No",""+companyPan_No);

            String execName = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
            String execMob = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
            String routeMob = sessionValue.getRegisteredMobile();  //route mob get from session
            String drivername = sessionValue.getdrivername();
            String vehicleno = sessionValue.getvehicleno();
            // Create New Blank Document

            Document document = new Document(PageSize.A4); //A4

            writer = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH));

            myFile = new File(FILE_PATH);

            document.open();


//            BaseFont bf = BaseFont.createFont("/assets/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont bf = BaseFont.createFont("/assets/dejavu_sans_condensed.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);


            Font font20 = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);

            Font font18 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

            Font font14 = new Font(Font.FontFamily.TIMES_ROMAN, 14);


            Font font10Bold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);

            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
            Font font8Italicunderline = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.ITALIC);
            Font font8bold = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.BOLD);

            Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);

            Font font12Bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);


            LanguageProcessor arabicPro = new ArabicLigaturizer();
            Font fontArb8 = new Font(bf, 8);
            Font fontArb10 = new Font(bf, 10);
            Font fontcompany10 = new Font(bf, 10, Font.BOLD);
            Font fontArb14 = new Font(bf, 14);
            Font fontArb10bl = new Font(bf, 10);
            fontArb10bl.setColor(BaseColor.BLACK);
            for (int i = 0; i < pdfModels.size(); i++) {


                PdfModel pdfData = pdfModels.get(i);

                List<CartItem> cartList = pdfData.getCartItems();

                String netTotal = "", discount = "",  roundOff = "", hsn_code_total ="";

                String grandTotal = "";
                String paid = "";
                String balance = "";
                //haris
                String central_tax_rate = "";
                String central_tax_amnt = "";
                String state_tax_rate = "";
                String state_tax_amnt = "";
                String taxable_total = "";
                String total_tax = "";
                String total_discount = "";
                String stdb_grandtot ="";
                String discount_percntge = "";
                String st_data = "";
                String val_in_english = "";
                String val_in_english_vat ="";
                String valtaxableamnt_in_english = "";
                String val_in_Arabic = "";
                double taxable_amnt =0;


                String total_discountnw = "";
                String totalVat = "";

                if (pdfModels.size() == i + 1) {

                    netTotal = getAmount(getNetTotal()-(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount()));

                    if(SELECTED_RETURN.getRoundoff_value()!=0){
                        grandTotal = ""+SELECTED_RETURN.getRoundofftot();
                    }
                    else{
                        grandTotal = getAmount(SELECTED_RETURN.getWithTaxTotal());
                    }


                    st_data = str_qrcodelink+""+strBillNumber;
                    taxable_total = getAmount(SELECTED_RETURN.getTaxable_total());
                    total_tax = getAmount(SELECTED_RETURN.getTaxAmount());
                    //total_discount = getAmount(SELECTED_SALES.getDiscount_value());
                    total_discount = ""+discnt;
                    total_discountnw = ""+discnt;
                    discount_percntge = getAmount(SELECTED_RETURN.getDiscount_percentage());
                    hsn_code_total = SELECTED_RETURN.getHsn_code();

                    String s = total_discount.replace(",", "");

                    taxable_amnt = Double.parseDouble(""+netTotal)-Double.parseDouble(s);

                    taxable_amnt=roundTwoDecimals(taxable_amnt);



                    //  new_Outstanding = getAmount(SELECTED_SHOPE.getOutStandingBalance());

                    Transaction t = myDatabase.getCustomerTransactionBalance(SELECTED_SHOPE.getShopId());

                    new_Outstanding = getAmount(t.getOutStandingAmount());

                    if (SELECTED_RETURN.getPaid() == 0){

                        double prevbal = t.getOutStandingAmount() - SELECTED_RETURN.getTotal();
                        str_Previous_balance = getAmount(prevbal);

                        Log.e("Credit Prev Bal" , ""+str_Previous_balance);
                        Log.e("New OutStand" , ""+new_Outstanding);

                    }else {

                        str_Previous_balance = new_Outstanding;

                        Log.e("Cash Prev Bal" , ""+str_Previous_balance);
                        Log.e("New OutStand" , ""+new_Outstanding);

                    }

                }

                Paragraph compNamePhone = new Paragraph("Contact No:"+compny_phone, font8);
                compNamePhone.setAlignment(Element.ALIGN_LEFT);

                Paragraph compMail = new Paragraph("E-Mail:"+comp_mail, font8);
                compMail.setAlignment(Element.ALIGN_LEFT);

                Paragraph compGst = new Paragraph("GSTIN:"+companyVatStr +"  State Code : KA", font8);
                compGst.setAlignment(Element.ALIGN_LEFT);

                Paragraph compfssai = new Paragraph("FSSAI Number : "+companyFssai_no +companyPan_No , font8);
                compfssai.setAlignment(Element.ALIGN_LEFT);

                Paragraph compweb = new Paragraph(comp_web, font8);
                compweb.setAlignment(Element.ALIGN_LEFT);

                Paragraph comBillNohead = new Paragraph("Bill No:", font8bold);
                comBillNohead.setAlignment(Element.ALIGN_LEFT);

                Paragraph comBillNo = new Paragraph("Bill No:"+SELECTED_RETURN.getInvoiceCode(), font8);
                comBillNo.setAlignment(Element.ALIGN_LEFT);

                Paragraph comBillDatehead = new Paragraph("Bill Date:", font8bold);
                comBillDatehead.setAlignment(Element.ALIGN_LEFT);

                Paragraph comBillDate = new Paragraph("Bill Date:"+SELECTED_RETURN.getDate(), font8bold);
                comBillDate.setAlignment(Element.ALIGN_LEFT);

                Paragraph customeridhead = new Paragraph("Customer ID:", font8bold);
                customeridhead.setAlignment(Element.ALIGN_LEFT);

                Paragraph customerid = new Paragraph("Customer ID:"+SELECTED_SHOPE.getShopCode(), font8bold);
                customerid.setAlignment(Element.ALIGN_LEFT);


                Paragraph customerbeat = new Paragraph("Beat:", font8bold);
                customerbeat.setAlignment(Element.ALIGN_LEFT);

                Paragraph customerroute = new Paragraph("Route : "+SELECTED_SHOPE.getRoute(), font8bold);
                customerroute.setAlignment(Element.ALIGN_LEFT);

                Paragraph customerbeathead = new Paragraph("Beat : "+SELECTED_SHOPE.getGroup(), font8bold);
                customerbeathead.setAlignment(Element.ALIGN_LEFT);

                Paragraph referhead = new Paragraph("Reference:", font8bold);
                referhead.setAlignment(Element.ALIGN_LEFT);

                Paragraph referid = new Paragraph("Reference:", font8bold);
                referid.setAlignment(Element.ALIGN_LEFT);

                Paragraph compname = new Paragraph("Name:"+compny_name, font8bold);
                compname.setAlignment(Element.ALIGN_LEFT);

                Paragraph compaddress = new Paragraph("Address:"+address_new, font8bold);
                compaddress.setAlignment(Element.ALIGN_LEFT);

                Paragraph compaddress2 = new Paragraph(address2Str, font8);
                compaddress2.setAlignment(Element.ALIGN_LEFT);



                Paragraph compPlaceTag = new Paragraph(address1Str, fontArb8);
                compPlaceTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compMobileTag = new Paragraph(address2Str+",Mob : "+mobileStr+", CR :"+compRegisterStr+" "+arabicPro.process(address2ArabStr), fontArb8);
                compMobileTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
                compEmailEng.setAlignment(Element.ALIGN_CENTER);

                Paragraph compdate = new Paragraph(compDateStr, font8);
                compdate.setAlignment(Element.ALIGN_RIGHT);

                Paragraph secondcoln = new Paragraph(comBillNohead+""+comBillNo+"\n"+comBillDatehead+" "+comBillDate+"\n"+customeridhead+""+customerid+"\n"+customerbeathead+""+customerbeat+"\n"+referhead+""+referid, font8);
                secondcoln.setAlignment(Element.ALIGN_LEFT);

                Paragraph compVatTag = new Paragraph("                                                             VAT Number "+companyVatStr +"                Original For Customer", fontArb8);



                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (paid_amount == SELECTED_RETURN.getTotal()) {
                            //  paragraph = new Paragraph(arabicPro.process("الفاتورة النقدية - " + " CASH INVOICE"), fontArb14);
                            // Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                        } else {
                            //Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                            // paragraph = new Paragraph(arabicPro.process("بطاقة الائتمان - " + " CREDIT INVOICE"), fontArb14);
                        }


                        break;

                    case ActivityConstants.ACTIVITY_QUOTATION:

                        Paragraph  paragraph = new Paragraph(" QUOTATION", fontArb14);

                        break;
                }

                //////////////////////////////////////////

                PdfPCell cell;  //default cell

                //space cell
                PdfPCell cellSpace = new PdfPCell();
                cellSpace.setPadding(10);
                cellSpace.setBorder(PdfPCell.BOTTOM);
                cellSpace.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{5});

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP  );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                Paragraph paragraph = new Paragraph("CREDIT NOTE", font8bold);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);
                table.addCell(cell);


                document.add(table);

                //Create the table which will be 2 Columns wide and make it 100% of the page
                table = new PdfPTable(3);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{8, 8, 4});

                PdfPCell cellLogo = new PdfPCell();
                cellLogo.setBorder(PdfPCell.TOP | PdfPCell.RIGHT );
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setPaddingTop(10);
                cellLogo.setPaddingBottom(5);
//                cellLogo.setPaddingRight(5);
//                cellLogo.setPaddingLeft(5);

                try {

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icresp_newlogo);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image img = Image.getInstance(stream.toByteArray());
//                  img.setAbsolutePosition(25f, 735f);
                    img.scalePercent(25f);
                    img.setAlignment(Element.ALIGN_CENTER);
                    cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellLogo.addElement(img);


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                PdfPCell cellTitle = new PdfPCell();
                cellTitle.setBorder( PdfPCell.TOP );

                cellTitle.setPadding(1);

                paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICESSS"), fontArb14);

                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (paid_amount == SELECTED_RETURN.getTotal()) {
                            //  paragraph = new Paragraph(arabicPro.process("الفاتورة النقدية - " + " CASH INVOICE"), fontArb14);
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                        } else {
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                            // paragraph = new Paragraph(arabicPro.process("بطاقة الائتمان - " + " CREDIT INVOICE"), fontArb14);
                        }


                        break;
                    case ActivityConstants.ACTIVITY_SALE_REPORT:

                        if (paid_amount == SELECTED_RETURN.getTotal()) {
                            // paragraph = new Paragraph(arabicPro.process("الفاتورة النقدية - " + " CASH INVOICE"), fontArb14);
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                        } else {
                            //  paragraph = new Paragraph(arabicPro.process("بطاقة الائتمان - " + " CREDIT INVOICE"), fontArb14);
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                        }


                        break;
                    case ActivityConstants.ACTIVITY_QUOTATION:

                        paragraph = new Paragraph(arabicPro.process("" + " QUOTATION"), fontArb14);

                        break;
                }

                paragraph.setAlignment(Element.ALIGN_CENTER);
                cellTitle.setHorizontalAlignment(Element.ALIGN_LEFT);


                cellTitle.addElement(comBillNo);
                cellTitle.addElement(comBillDate);
                cellTitle.addElement(customerid);
                cellTitle.addElement(customerroute);
                cellTitle.addElement(customerbeathead);
                cellTitle.addElement(referid);
                // cellTitle.addElement(compweb);



                ////////////////3rd column

                PdfPCell cellthirdcolumn = new PdfPCell();
                cellthirdcolumn.setBorder(PdfPCell.TOP | PdfPCell.LEFT);
                cellthirdcolumn.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellthirdcolumn.setPadding(1);



                cellthirdcolumn.addElement(compname);
                cellthirdcolumn.addElement(compaddress);
                cellthirdcolumn.addElement(compaddress2);
                cellthirdcolumn.addElement(compGst);
                cellthirdcolumn.addElement(compfssai);
                cellthirdcolumn.addElement(compMail);
                cellthirdcolumn.addElement(compNamePhone);

                table.addCell(cellthirdcolumn);
                table.addCell(cellTitle);
                table.addCell(cellLogo);
                document.add(table);



                //second table


                //Create the table which will be 3 Columns wide and make it 100% of the page
                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1,6});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.TOP | PdfPCell.LEFT | PdfPCell.BOTTOM);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //230

                //            customer Address  label
                paragraph = new Paragraph("Buyer (Bill To)  :" , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.TOP | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //230

                //            customer Address  label
                paragraph = new Paragraph(" " , font8);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                paragraph = new Paragraph("Name :" +SELECTED_SHOPE.getShopName(), font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                paragraph = new Paragraph("" , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                document.add(table);


                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{4});
                // customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(0);

                paragraph = new Paragraph("Address  :"+SELECTED_SHOPE.getShopAddress() , font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("" , font8);//strDate
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);
                cell.setPaddingRight(10);

                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("Contact No  :"+SELECTED_SHOPE.getShopMobile() , font8);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);


                table.addCell(cell);

                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(2);
                //230

                //            customer Address  label
                paragraph = new Paragraph("GSTIN  :"+SELECTED_SHOPE.getVatNumber() , font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);


                table.addCell(cell);



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(1);
                //230

                paragraph = new Paragraph("State Code :  "+SELECTED_SHOPE.getState_code() , font8);//SELECTED_SALES.getPayment_type()
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(1);

                table.addCell(cell);


                document.add(table);

                //Create the table which will be 8 Columns wide and make it 100% of the page
//                table = new PdfPTable(17);
//                table.setWidths(new int[]{2,3,6,3, 3,3, 3, 3, 3,3,3,2,3,2,3,2,3});
//                table.setWidthPercentage(100);

                table = new PdfPTable(13);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,3,3,4,4});
                table.setWidthPercentage(100);
//            table.setSpacingBefore(10);


                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Sl No\n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("HSN/SAC\n Code" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);



//3
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                paragraph = new Paragraph("Discription of Goods/Services \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);
//4
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                paragraph = new Paragraph("Qty\n(Kg)" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);
                //5

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                paragraph = new Paragraph("Qty\n(Pcs)" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                //8
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("MRP/Pc" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                // 6           temporary
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Rate/Kg" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Rate/Pc" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);

                table.addCell(cell);




//9

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Disc" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

//10
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Taxable Amt \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

//11

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("IGST \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setColspan(2);
                table.addCell(cell);


                //12
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Gross Amt \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                //13
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("%" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Rs" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);


                String str_vatRate_tot="",str_unit_tot = " ",str_vatRate_total="",justifiedtax_5="",justifiedtax_12="",justifiedtax_18="";;

                float tot_qnty = 0;
                double  total_grossamnt =0 , total_taxamnt=0 , totalcgst=0 ,total_taxable_amnt =0 ,total_qtypcs=0 ,
                        total_grand =0 ,dbl_taxtotal_5 = 0,dbl_taxtotal_12 = 0, dbl_taxtotal_18 = 0 ,dbl_cgst_tot_5 =0 ,
                        dbl_cgst_tot_12 = 0,dbl_cgst_tot_18=0 ,dbl_grossamnt_5 =0 ,dbl_grossamnt_12 =0 ,dbl_grossamnt_18=0,
                        dbl_taxtotal_gross=0 ,dbl_cgst_total=0 ,total_gst =0 ,taxable_total_amnt =0 ,dbl_totalkg_qty =0;
                // double prod_discnt_totl=0;

                for (int j = 0; j < MAX_LINE; j++) {
                    double piece_price=0;

                    String strSl_No = " ", strP_Code = " ",strP_Name = " ", strP_Arabic = "  ", strQty = " ", strqty_pieces =" " ,strNetPrice = " ", strNetTotal = " ",
                            strAmount = " ", strTotalPrice = " ", str_vatAmount = " ",str_unit ="",str_vatRate ="",
                            str_prod_disc ="" , strbarcode ="",str_mfgdate ="",str_cgst_rate ="",str_cgst="",str_mrp="",
                            st_taxable_amnt="" ,strpieceprice ="" ,str_Grossamnt ="",str_tax_5="",str_tax_12="",
                            str_tax_18="" ,st_taxableamnt ="" ,str_rateperkg ="" ,str_hsncode="";

                    if (cartList.size() > j) {
                        CartItem cartItem = cartList.get(j);

                        int slNo = i * MAX_LINE + j + 1;
                        strSl_No = String.valueOf(slNo);
                        strP_Name = cartItem.getProductName();
                        strP_Arabic = cartItem.getArabicName();
                        strP_Code=cartItem.getProductCode();
                        str_unit = cartItem.getUnitselected();
                        str_unit_tot =cartItem.getUnitselected();
                        str_prod_disc = String.valueOf(cartItem.getProductDiscount());
                        // prod_discnt_totl = prod_discnt_totl +(cartItem.getProductDiscount()*cartItem.getPieceQuantity_nw());
                        strbarcode = cartItem.getBarcode();
                        str_mfgdate = cartItem.getMfg_date();
                        str_hsncode = cartItem.getProduct_hsncode();
                        Log.e("strP_Arabic",""+strP_Arabic);

                        if (strP_Arabic == null || TextUtils.isEmpty(strP_Arabic.trim()) || strP_Arabic.equals("null"))
                            strP_Arabic = "  ";

                        // strQty = "0/" + String.valueOf(cartItem.getTypeQuantity()); // case and piece

                        //  strQty = ""+String.valueOf(getAmountthree(cartItem.getTypeQuantity())); // piece only
                        Log.e("unitselected hr",""+cartItem.getUnitid_selected());
                        Log.e("confctr hr",""+cartItem.getConfactr_kg());
                        Log.e("qnty  hr",""+cartItem.getTypeQuantity());
                        Log.e("pcs  hr",""+cartItem.getPieceQuantity());
                        float pieces =0;
                        if(cartItem.getUnitid_selected().equals("1")){
                            strqty_pieces = ""+cartItem.getTypeQuantity();
                            pieces =cartItem.getTypeQuantity();
                            strQty = ""+getAmountthree(cartItem.getTypeQuantity()/Integer.valueOf(cartItem.getConfactr_kg()));
                            dbl_totalkg_qty =  (cartItem.getTypeQuantity()/Integer.valueOf(cartItem.getConfactr_kg()));
                        }
                        else {
                            strqty_pieces = ""+cartItem.getPieceQuantity();
                            strQty = ""+String.valueOf(getAmountthree(cartItem.getTypeQuantity())); // piece only
                            dbl_totalkg_qty = cartItem.getTypeQuantity();
                            pieces =cartItem.getPieceQuantity();
                        }


                        if(SELECTED_RETURN.getReturn_type().equals("with")){
                            str_cgst= ""+getAmount(roundTwoDecimalsbytwo(cartItem.getTaxValue()*cartItem.getTypeQuantity()));
                        }
                        else{
                            str_cgst= ""+getAmount(roundTwoDecimalsbytwo(cartItem.getTaxValue()/2));
                        }
                        str_cgst_rate = ""+roundTwoDecimalsbytwo(cartItem.getTax());
                        //  str_cgst= ""+getAmount(roundTwoDecimalsbytwo(cartItem.getTaxValue())/2);

                        double mrp_price = cartItem.getMrprate();
                        str_mrp = ""+cartItem.getMrprate();
                        Log.e("mrppppp",""+cartItem.getMrprate());
                        Log.e("str_cgst",""+str_cgst);
                        total_gst =0;
                        if(SELECTED_RETURN.getReturn_type().equals("with")) {
                            total_gst = total_gst + (cartItem.getTaxValue()*cartItem.getTypeQuantity());
                            Log.e("total_gst",""+total_gst);
                        }
                        else{
                            total_gst = total_gst + cartItem.getTaxValue() ;
                        }
                        double dbl_vat5 = 0 , dbl_vat12=0,dbl_vat18 =0;
                        if(cartItem.getTax()==5 && cartItem.getTaxValue()>0){
                            dbl_taxtotal_5 = dbl_taxtotal_5 + cartItem.getProductTotal();

                            dbl_cgst_tot_5 = dbl_cgst_tot_5 + (cartItem.getTaxValue()* cartItem.getTypeQuantity()) ;

                            dbl_vat5 = dbl_vat5 + cartItem.getTaxValue();
                            dbl_grossamnt_5 = dbl_taxtotal_5 + (dbl_cgst_tot_5);

                        }
                        if(cartItem.getTax()==12 && cartItem.getTaxValue()>0){
                            dbl_taxtotal_12 = dbl_taxtotal_12 + cartItem.getProductTotal();

                            dbl_cgst_tot_12 = dbl_cgst_tot_12 +(cartItem.getTaxValue()* cartItem.getTypeQuantity());

                            dbl_grossamnt_12 = dbl_taxtotal_12 +  dbl_cgst_tot_12;
                            dbl_vat12 = dbl_vat12 + cartItem.getTaxValue();

                        }
                        if(cartItem.getTax()==18 && cartItem.getTaxValue()>0){
                            dbl_taxtotal_18 = dbl_taxtotal_18 + cartItem.getProductTotal();

                            dbl_cgst_tot_18 = dbl_cgst_tot_18 + (cartItem.getTaxValue()* cartItem.getTypeQuantity());

                            dbl_grossamnt_18 = dbl_taxtotal_18 +  dbl_cgst_tot_18;
                            dbl_vat18 = dbl_vat18 + cartItem.getTaxValue();

                        }
                        dbl_taxtotal_gross = dbl_taxtotal_5+dbl_taxtotal_12+dbl_taxtotal_18;
                        dbl_cgst_total = dbl_cgst_tot_5 + dbl_cgst_tot_12 +dbl_cgst_tot_18;
                        Log.e("dbl_taxtotal_5",""+dbl_taxtotal_5);
                        Log.e("dbl_grossamnt_18",""+dbl_grossamnt_18);

                        double prod_taxable_amount = Double.parseDouble(getAmount(cartItem.getWithouttaxTotal()))-(cartItem.getProductDiscount()*cartItem.getTypeQuantity());
                        st_taxable_amnt = ""+prod_taxable_amount;
//                        double netPrice = cartItem.getNetPrice();
                        double netPrice = cartItem.getProductPrice();
                        Log.e("netpriceeeeeeeeeee",""+netPrice);

                        double net_total = cartItem.getProductTotal();

//                        if(cartItem.getTax_type().equals("TAX_INCLUSIVE")) {
//                            netPrice = cartItem.getProductTotal()/cartItem.getPieceQuantity_nw();
//                        }
                        // For case and piece

                        if (cartItem.getOrderType().equals(ConfigValue.PRODUCT_UNIT_CASE)) {
                            netPrice = netPrice * cartItem.getPiecepercart();
//                            strQty = cartItem.getTypeQuantity() + "/0";
                            strQty = cartItem.getTypeQuantity() +"";
                        }
                        //piece_price = ((cartItem.getProductPrice() * cartItem.getTypeQuantity())-(cartItem.getProductDiscount() * cartItem.getTypeQuantity())/cartItem.getPieceQuantity());


                        //  tot_qnty =tot_qnty + cartItem.getTypeQuantity();
                        tot_qnty = Float.parseFloat(getAmount(tot_qnty + (float) dbl_totalkg_qty));


//haris chaned on 04-08-2021
                        //  strNetTotal = getAmount(cartItem.getProductPrice() * cartItem.getPieceQuantity());

                        str_vatRate = String.valueOf(cartItem.getTax() + " %");
                        str_vatRate_tot= String.valueOf(cartItem.getTax() + " %");
                        str_vatRate_total= String.valueOf(SELECTED_RETURN.getTaxAmount());
                        // cartItem.setTaxValue(AmountCalculator.getTaxPrice(cartItem.getProductPrice(), cartItem.getTax(),cartItem.getTax_type()));

                        ///////////////////////added by haris on 24-02-22
                        strNetTotal = getAmount((cartItem.getProductPrice() * cartItem.getTypeQuantity())-(cartItem.getProductDiscount() * cartItem.getTypeQuantity()));
                        float total = (float) ((cartItem.getProductPrice() * cartItem.getTypeQuantity())-(cartItem.getProductDiscount() * cartItem.getTypeQuantity()));
                        piece_price =total/ cartItem.getPieceQuantity();
                        piece_price = cartItem.getUnit_rate_pcs();
                        //  piece_price = cartItem.getPieceQuantity();

                        ///////////////////////////////////////////
                        double net = roundTwoDecimalsbytwo(cartItem.getNetPrice());
                        if(cartItem.getTax_type().equals("TAX_INCLUSIVE")){
                            net = (roundTwoDecimalsbytwo(cartItem.getProductTotal())/cartItem.getPieceQuantity_nw());
                            Log.e("tax invclusiveee",""+net);
                        }
                        Log.e("taxableeee",""+net);
                        // double d = net * cartItem.getPieceQuantity_nw();

                        //double d = cartItem.getProductPrice() * cartItem.getReturnQuantity();
                        double d = (net * cartItem.getTypeQuantity());
                        //   double d = roundTwoDecimalsbytwo(cartItem.getProductPrice() * cartItem.getTypeQuantity())-roundTwoDecimalsbytwo(cartItem.getTypeQuantity()*cartItem.getProductDiscount());
                        st_taxableamnt = ""+getAmount(d);

                        // double nettotalwithvat = roundTwoDecimalsbytwo(d+dbl_vat5+dbl_vat12+dbl_vat18);
                        double nettotalwithvat=0;
                        Log.e("total",""+getAmount(d));
                        Log.e("totalhrct",""+(d));
                        Log.e("totalharis",""+(d + (total_gst)));
//                        if(d+(total_gst)>0) {
//                             nettotalwithvat = roundTwoDecimalsbytwo(d + (total_gst));
//                        }
//                        else{
                        nettotalwithvat = (d + (total_gst));
                        // }

                        ////////////////////////////////////////////////////////


                        // cartItem.setTaxValue(AmountCalculator.getTaxPrice(cartItem.getProductPrice(), cartItem.getTax(),cartItem.getTax_type()));
                        str_vatAmount = getAmount(cartItem.getTaxValue()* cartItem.getPieceQuantity_nw());

                        strpieceprice = getAmount(piece_price);
                        strTotalPrice = getAmount((cartItem.getProductPrice() * cartItem.getPieceQuantity_nw())+(cartItem.getTaxValue()* cartItem.getPieceQuantity_nw()));


                        total_taxamnt =0;
                        total_taxamnt = total_taxamnt +SELECTED_RETURN.getTaxAmount();

                        totalcgst = roundTwoDecimals(total_taxamnt/2);
                        str_Grossamnt = ""+roundTwoDecimalsbytwo(nettotalwithvat);
                        total_grossamnt = total_grossamnt + nettotalwithvat;

                        total_taxable_amnt = total_taxable_amnt+d;
                        total_taxable_amnt = getNetTotal() -(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount());
                        total_qtypcs = total_qtypcs+pieces;

                        if(SELECTED_RETURN.getRoundoff_value()>0) {
                            total_grossamnt =(SELECTED_RETURN.getTotal() - (SELECTED_RETURN.getDiscount_value() - SELECTED_RETURN.getDiscount())) - SELECTED_RETURN.getRoundoff_value();
                        }
                        else{
                            total_grossamnt = (SELECTED_RETURN.getTotal() - (SELECTED_RETURN.getDiscount_value() - SELECTED_RETURN.getDiscount())) - SELECTED_RETURN.getRoundoff_value();
                        }
                        total_grand = SELECTED_RETURN.getTotal();
                        ////////////////////
                        strNetPrice = getAmount(netPrice);

                        str_rateperkg = getAmount(cartItem.getUnit_rate_kg());

                    }


                    if (strP_Name.length() > 40)
                        strP_Name = getMaximumChar(strP_Name, 40);



                    if (strP_Arabic.length() > 42)
                        strP_Arabic = getMaximumChar(strP_Arabic, 42);

                    taxable_total_amnt =(getNetTotal() -(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount()));
                    String justifiedSlNo = String.format("%-3s", strSl_No);
                    String justifiedCode = String.format("%-5s", strP_Code);

                    String justifiedQuantity = String.format("%-5s", strQty);
                    String justifiedNetPrice = String.format("%-5s", strNetPrice);
                    String justifiedNetTotal = String.format("%-5s", strNetTotal);
                    String justifiedVatRate = String.format("%-5s", str_vatRate);
                    String justifiedVatAmount = String.format("%-5s", str_vatAmount);
                    String justifiedTotal = String.format("%-5s", strTotalPrice);
                    String justifiedunit = String.format("%-5s", str_unit);
                    String justifiedprod_disc = String.format("%-5s", str_prod_disc);
                    String justifiedQntyPieces = String.format("%-5s",strqty_pieces);
                    String justifiedPricePieces = String.format("%-5s",strpieceprice);
                    String justifiedGrossamnt = String.format("%-5s",str_Grossamnt);
                    String justifiedtaxablamnt = String.format("%-5s",st_taxableamnt);
                    String justifiedmrp = String.format("%-5s",str_mrp);
                    String justifiedmrpperkg = String.format("%-5s",str_rateperkg);

                    String justifiedhsncode = String.format("%-5s",str_hsncode);

                    if (strP_Name.length() > 40)
                        strP_Name = getMaximumChar(strP_Name, 40);

                    if (strP_Arabic.length() > 42)
                        strP_Arabic = getMaximumChar(strP_Arabic, 42);


                    //strSl_No

                    cell = new PdfPCell(new Phrase(strSl_No, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);

//sl number
                    cell = new PdfPCell(new Phrase(""+justifiedhsncode, font8));//strP_Code
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);





                    cell = new PdfPCell(new Phrase(""+strP_Name, font8));

                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    //Qty

//                    if(strQty.length()>0 && str_unit.length()>1) {
//                        cell = new PdfPCell(new Phrase(strQty , font8));
//                        cell.setBorder(Rectangle.RIGHT);
//                        cell.setPadding(2);
//                        cell.setRowspan(1);
//                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                        table.addCell(cell);
//                    }
//                    else{
                    cell = new PdfPCell(new Phrase(justifiedQuantity, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    //   }

                    //Qty pcs
                    cell = new PdfPCell(new Phrase(""+justifiedQntyPieces, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //mrp/pc
                    cell = new PdfPCell(new Phrase(justifiedmrp, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    //Rate/Kg
                    cell = new PdfPCell(new Phrase(""+justifiedmrpperkg, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //Rate/Pc
                    cell = new PdfPCell(new Phrase(""+justifiedPricePieces, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);




                    // discnt
                    cell = new PdfPCell(new Phrase(justifiedprod_disc, font8));//justifiedTotal //getTaxAmount
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    // taxable amnt
                    cell = new PdfPCell(new Phrase(""+justifiedtaxablamnt, font8));//justifiedTotal //getTaxAmount
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);



                    // igst rate
                    cell = new PdfPCell(new Phrase(""+str_cgst_rate, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);


                    //igst mrp
                    cell = new PdfPCell(new Phrase( ""+str_cgst, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


//  gross amnt
                    cell = new PdfPCell(new Phrase(""+justifiedGrossamnt, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);



                }

                document.add(table);
                double discnt_withoutprod_disc=0;
//                if(SELECTED_SALES.getDiscount_value()>0){
//                     discnt_withoutprod_disc = SELECTED_SALES.getDiscount_value()-prod_discnt_totl;
//
//                }
//                Log.e("getDiscount_value",""+SELECTED_SALES.getDiscount_value());
//                Log.e("prod_discnt_totl",""+prod_discnt_totl);

                if(SELECTED_RETURN.getDiscount_percentage()==0) {

                    if(discnt_withoutprod_disc>0) {
                        disc_percent = (discnt_withoutprod_disc * 100) / net_Amount;
                        Log.e("if dscnt", "" + disc_percent);
                    }
                }
                else{
                    disc_percent = SELECTED_RETURN.getDiscount_percentage();
                    Log.e("else dscnt",""+disc_percent);
                }

                disc_percent = roundTwoDecimals(disc_percent);
                val_in_english = convertNumberToEnglishWords(String.valueOf(total_grand));
                val_in_english_vat = convertNumberToEnglishWords(String.valueOf(str_vatRate_total));
                Log.e("number inn",""+db_grandtot);


                table = new PdfPTable(13);

                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,3,3,4,4});

                table.setWidthPercentage(100);

//1
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                table.addCell(cell);

                //2
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //3
                cell = new PdfPCell();


                paragraph = new Paragraph("Total" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);
//4
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" +getAmountthree(tot_qnty), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //5
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" +total_qtypcs, font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //6
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //9
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //10
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + getAmount(taxable_total_amnt), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12

                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" +getAmount(SELECTED_RETURN.getTaxAmount()), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



                //17
                //total_grossamnt = total_grossamnt + netPrice+SELECTED_SALES.getTaxAmount();
                //                //                        str_Grossamnt = ""+(netPrice+SELECTED_SALES.getTaxAmount());
                //                //                        total_taxamnt = total_taxamnt +SELECTED_SALES.getTaxAmount();
                //                //                        totalcgst = total_taxamnt/2;
                //                //                        total_taxable_amnt = total_taxable_amnt+netPrice;
                //                //                        total_qtypcs = total_qtypcs+cartItem.getPieceQuantity();
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + getAmount(total_grossamnt), font8bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);



                document.add(table);

                table = new PdfPTable(13);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,3,3,4,4});
                table.setWidthPercentage(100);


//second row
                //1
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //2

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //3

                cell = new PdfPCell();
                paragraph = new Paragraph("Less:" + arabicPro.process(""), font8Italicunderline);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setBorder(PdfPCell.TOP |PdfPCell.RIGHT);
                table.addCell(cell);

                //4
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.addElement(paragraph);
                table.addCell(cell);

                //5
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //    6        temporary
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);


                //9
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



                //10
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



//                //15
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.BOTTOM);
//                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//                paragraph = new Paragraph("" + arabicPro.process(""), font8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//                table.addCell(cell);
//
//                //16
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.BOTTOM);
//                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//                paragraph = new Paragraph("" + arabicPro.process(""), font8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//                table.addCell(cell);

                //17
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP | PdfPCell.BOTTOM);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(13);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,3,3,4,4});
                table.setWidthPercentage(100);
                //third row

                //1
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //2
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //3

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP);
                paragraph = new Paragraph("TCS" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //4
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //5
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);
                //            temporary
                //6
                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //9

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);


                //10
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



                //17
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOX);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(13);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,3,3,4,4});
                table.setWidthPercentage(100);
                //fourth row

                //1
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                table.addCell(cell);

                //2
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                //3

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("Variable Disc" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //4
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                table.addCell(cell);

                //5
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                table.addCell(cell);

                //  6          temporary
                cell = new PdfPCell();

                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //9
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                //10
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                //17
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOX);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph(""+getAmount(discnt), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                document.add(table);

                table = new PdfPTable(13);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,3,3,4,4});
                table.setWidthPercentage(100);
                //fifth row
                //1
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                table.addCell(cell);

                //2
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



//3
                cell = new PdfPCell();
                paragraph = new Paragraph("Round off" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                cell = new PdfPCell();
//4
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);


                //5
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //    6        temporary
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);


                //9
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);


                //10

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);


                //17
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOX);
                paragraph = new Paragraph(""+getAmount(SELECTED_RETURN.getRoundoff_value()), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);


                document.add(table);

                /////////6th row
                table = new PdfPTable(13);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,3,3,4,4});
                table.setWidthPercentage(100);

                //fifth row 1
                cell = new PdfPCell();

                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //2
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);


//3

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP | PdfPCell.RIGHT);
                paragraph = new Paragraph("Grand Total" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //4

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //5

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //     6       temporary
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //9
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);


                //10
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);




                //17

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOX);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph(""+getAmount(total_grand), font8bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);



                document.add(table);
                //new rows for output vat

                //////////////////////////////

                table = new PdfPTable(2);
                table.setWidths(new int[]{9,6});
                table.setWidthPercentage(100);

                cell = new PdfPCell();

                paragraph = new Paragraph("Amount in Words: \n"+val_in_english, font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingLeft(5);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.LEFT);
                table.addCell(cell);



                cell = new PdfPCell();
                paragraph = new Paragraph("Bank Details :", font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT );

                table.addCell(cell);


                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );

                table.addCell(cell);


                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT);

                table.addCell(cell);



                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);

                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );

                table.addCell(cell);


                cell = new PdfPCell();
                paragraph = new Paragraph("Name : "+str_bankaccname, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT );

                table.addCell(cell);
                document.add(table);


                table = new PdfPTable(2);
                table.setWidths(new int[]{9,6});
                table.setWidthPercentage(100);



                cell = new PdfPCell();

                paragraph = new Paragraph("GST Details:", font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT | PdfPCell.LEFT | PdfPCell.TOP);


                table.addCell(cell);



                cell = new PdfPCell();

                paragraph = new Paragraph("Bank :"+str_bankname, font8);
                cell.setBorder(PdfPCell.RIGHT );
                cell.setPaddingLeft(5);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("Tax Rate", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);

                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("Taxable Amount", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER );
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("CGST", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("SGST", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("IGST", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("Gross Amount", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);



                cell = new PdfPCell();
                //paragraph = new Paragraph("A/c No :\n IFSC CODE \n branch", font8bold);
                paragraph = new Paragraph("A/c No : "+str_accno, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT );
                table.addCell(cell);

//                cell = new PdfPCell();
//                cell.setBorderWidth(1);
//                paragraph = new Paragraph("IFSC Code :", font8bold);
//                paragraph.setAlignment(Element.ALIGN_LEFT);
//                cell.addElement(paragraph);
//                cell.setRowspan(1);
//                cell.setFixedHeight(5);
//                cell.setBorder(PdfPCell.RIGHT );
//                table.addCell(cell);
//
//                cell = new PdfPCell();
//                cell.setBorderWidth(1);
//                paragraph = new Paragraph("Branch :", font8bold);
//                paragraph.setAlignment(Element.ALIGN_LEFT);
//                cell.addElement(paragraph);
//                cell.setRowspan(1);
//                cell.setFixedHeight(5);
//                cell.setBorder(PdfPCell.RIGHT );
//                table.addCell(cell);


                document.add(table);

//5%
                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("5%", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setRowspan(1);
                table.addCell(cell);



                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_taxtotal_5), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(0), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_tot_5), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_grossamnt_5), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("IFSC Code : "+str_ifsc, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT );
                table.addCell(cell);




                document.add(table);


                //12%
                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("12%", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_taxtotal_12), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(0), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(0), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph =  new Paragraph(""+getAmount(dbl_cgst_tot_12), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_grossamnt_12), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("Branch : "+str_bankbranch, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingLeft(5);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT);
                table.addCell(cell);


                document.add(table);

//18%
                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("18%", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_taxtotal_18), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(0), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(0), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_tot_18), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_grossamnt_18), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT);
                table.addCell(cell);



                document.add(table);

                //TOtal
                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("Total", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_taxtotal_gross), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_total), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_total), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(total_grossamnt), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.BOTTOM );
                table.addCell(cell);



                document.add(table);

                //////////////////////
//                Declaration



                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});

                cell = new PdfPCell();
                paragraph = new Paragraph("Declaration:", font6);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );


                table.addCell(cell);
                document.add(table);

                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});

                cell = new PdfPCell();
                paragraph = new Paragraph("  We declare that this invoice shows the actual price of the goods described and that all particulars are true and correct.", font6);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );

                table.addCell(cell);
                document.add(table);


                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});

                cell = new PdfPCell();
                paragraph = new Paragraph("For Farmle Food Trading", font8bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );

                table.addCell(cell);
                document.add(table);

                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5,5});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT | PdfPCell.BOTTOM );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(5);

                //            customer Address  label
                paragraph = new Paragraph("Customer's Seal and Signatory " , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(5);

                //            customer Address  label
                paragraph = new Paragraph("Authorised Signatory " , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                document.add(table);

                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.NO_BORDER );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(1);

                //            customer Address  label
                paragraph = new Paragraph("  "+strDate , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setPaddingTop(1);
                cell.setRowspan(10);

                table.addCell(cell);



                document.add(table);

            }

            // %%%%%%%%%%%%%%%%%%%%%%%%%%% **************** %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            // setBackground(document);
            document.close();


            printPDF(myFile);  //Print PDF File




        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Log.d(TAG, "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    private void printwithbackgroundInvoice(List<PdfModel> pdfList) {

        File myFile = null;

        PdfWriter writer;
        try {
            String compName ="";

            compName = sessionValue.getCompanyDetails_login().get(PREF_COMPANY_NAME_LOGIN);
            if(compName.equals("")) {
                compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
            }
            String compNameArab = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME_ARAB);  //name get from session
            String address1Str = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
            String address1ArabStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1_ARAB);  //address get from session
            String address2Str = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_2);  //address get from session
            String address2ArabStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_2_ARAB);  //address get from session
            String compEmailStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_EMAIL);  //address get from session
            String mobileStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_MOBILE);  //address get from session
            String comp_web = sessionValue.getCompanyDetails().get(PREF_COMPANY_WEBSITE);  //address get from session
            String compDateStr = strDate;  //date get from session
            String compbillStr = strBillNumber;  //date get from session

            String compny_phone =sessionValue.getCompanyDetails().get(PREF_COMPANY_MOBILE);
            String comp_mail =  sessionValue.getCompanyDetails().get(PREF_COMPANY_EMAIL);  //address get from session

            String compny_name = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);
            String address_new = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);


            String compRegisterStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_CR);
            String companyVatStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
            //haris added on 06-11-2020
            String companyPan_No = sessionValue.getCompanyDetails().get(PREF_COMPANY_PAN_NO);

            Log.e("companyPan_No",""+companyPan_No);

            String execName = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
            String execMob = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
            String routeMob = sessionValue.getRegisteredMobile();  //route mob get from session
            String drivername = sessionValue.getdrivername();
            String vehicleno = sessionValue.getvehicleno();
            // Create New Blank Document

            Document document = new Document(PageSize.A4); //A4

            writer = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH));

            myFile = new File(FILE_PATH);

            document.open();


//            BaseFont bf = BaseFont.createFont("/assets/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont bf = BaseFont.createFont("/assets/dejavu_sans_condensed.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);


            Font font20 = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);

            Font font18 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

            Font font14 = new Font(Font.FontFamily.TIMES_ROMAN, 14);


            Font font10Bold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);

            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
            Font font8Italicunderline = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.ITALIC);
            Font font8bold = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.BOLD);

            Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);

            Font font12Bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);


            LanguageProcessor arabicPro = new ArabicLigaturizer();
            Font fontArb8 = new Font(bf, 8);
            Font fontArb10 = new Font(bf, 10);
            Font fontcompany10 = new Font(bf, 10, Font.BOLD);
            Font fontArb14 = new Font(bf, 14);
            Font fontArb10bl = new Font(bf, 10);
            fontArb10bl.setColor(BaseColor.BLACK);
            for (int i = 0; i < pdfList.size(); i++) {


                PdfModel pdfData = pdfList.get(i);

                List<CartItem> cartList = pdfData.getCartItems();

                String netTotal = "", discount = "",  roundOff = "", hsn_code_total ="";

                String grandTotal = "";
                String paid = "";
                String balance = "";
                //haris
                String central_tax_rate = "";
                String central_tax_amnt = "";
                String state_tax_rate = "";
                String state_tax_amnt = "";
                String taxable_total = "";
                String total_tax = "";
                String total_discount = "";
                String stdb_grandtot ="";
                String discount_percntge = "";
                String st_data = "";
                String val_in_english = "";
                String val_in_english_vat ="";
                String valtaxableamnt_in_english = "";
                String val_in_Arabic = "";
                double taxable_amnt =0;


                String total_discountnw = "";
                String totalVat = "";

                if (pdfList.size() == i + 1) {

                    netTotal = getAmount(getNetTotal()-(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount()));

                    if(SELECTED_RETURN.getRoundoff_value()!=0){
                        grandTotal = ""+SELECTED_RETURN.getRoundofftot();
                    }
                    else{
                        grandTotal = getAmount(SELECTED_RETURN.getWithTaxTotal());
                    }

                    stdb_grandtot = ""+getAmount(db_grandtot);
                    paid = getAmount(paid_amount) + " " + CURRENCY;
                    discount = getAmount(SELECTED_RETURN.getDiscount());
                    balance = getAmount(SELECTED_RETURN.getTotal() - paid_amount) + " " + CURRENCY;
                    totalVat =  getAmount(getTaxTotal());
                    roundOff = ""+SELECTED_RETURN.getRoundoff_value();
//                     st_data = "<?xml version=\"1.0\"?>\n" +
//                            "<invoice>\n" +
//                            "    <SellerName>"+ compName+"</SellerName>\n" +
//                            "    <VatNumber>" + str_kafeel_VatNo  +"</VatNumber>\n" +
//                            "    <DateTime>"+SELECTED_SALES.getDate() +"</DateTime>\n" +
//                            "    <VatTotal>"+ totalVat +"</VatTotal>\n" +
//                            "    <TotalAmount>"+getAmount(SELECTED_SALES.getWithTaxTotal())+"</TotalAmount>\n" +
//                            "</invoice>";
                    st_data = str_qrcodelink+""+strBillNumber;
                    taxable_total = getAmount(SELECTED_RETURN.getTaxable_total());
                    total_tax = getAmount(SELECTED_RETURN.getTaxAmount());
                    //total_discount = getAmount(SELECTED_SALES.getDiscount_value());
                    total_discount = ""+discnt;
                    total_discountnw = ""+discnt;
                    discount_percntge = getAmount(SELECTED_RETURN.getDiscount_percentage());
                    hsn_code_total = SELECTED_RETURN.getHsn_code();

                    String s = total_discount.replace(",", "");

                    taxable_amnt = Double.parseDouble(""+netTotal)-Double.parseDouble(s);

                    taxable_amnt=roundTwoDecimals(taxable_amnt);



                    //  new_Outstanding = getAmount(SELECTED_SHOPE.getOutStandingBalance());

                    Transaction t = myDatabase.getCustomerTransactionBalance(SELECTED_SHOPE.getShopId());

                    new_Outstanding = getAmount(t.getOutStandingAmount());

                    if (SELECTED_RETURN.getPaid() == 0){

                        double prevbal = t.getOutStandingAmount() - SELECTED_RETURN.getTotal();
                        str_Previous_balance = getAmount(prevbal);

                        Log.e("Credit Prev Bal" , ""+str_Previous_balance);
                        Log.e("New OutStand" , ""+new_Outstanding);

                    }else {

                        str_Previous_balance = new_Outstanding;

                        Log.e("Cash Prev Bal" , ""+str_Previous_balance);
                        Log.e("New OutStand" , ""+new_Outstanding);

                    }

                }

                Paragraph compNamePhone = new Paragraph("Contact No:"+compny_phone, font8);
                compNamePhone.setAlignment(Element.ALIGN_LEFT);

                Paragraph compMail = new Paragraph("E-Mail:"+comp_mail, font8);
                compMail.setAlignment(Element.ALIGN_LEFT);

                Paragraph compweb = new Paragraph(comp_web, font8);
                compweb.setAlignment(Element.ALIGN_LEFT);

                Paragraph comBillNohead = new Paragraph("Bill No:", font8bold);
                comBillNohead.setAlignment(Element.ALIGN_LEFT);

                Paragraph comBillNo = new Paragraph("Bill No:"+SELECTED_RETURN.getInvoiceCode(), font8);
                comBillNo.setAlignment(Element.ALIGN_LEFT);

                Paragraph comBillDatehead = new Paragraph("Bill Date:", font8bold);
                comBillDatehead.setAlignment(Element.ALIGN_LEFT);

                Paragraph comBillDate = new Paragraph("Bill Date:"+SELECTED_RETURN.getDate(), font8bold);
                comBillDate.setAlignment(Element.ALIGN_LEFT);

                Paragraph customeridhead = new Paragraph("Customer ID:", font8bold);
                customeridhead.setAlignment(Element.ALIGN_LEFT);

                Paragraph customerid = new Paragraph("Customer ID:"+SELECTED_SHOPE.getShopCode(), font8bold);
                customerid.setAlignment(Element.ALIGN_LEFT);

                Paragraph customerroute = new Paragraph("Route : "+SELECTED_SHOPE.getRoute(), font8bold);
                customerroute.setAlignment(Element.ALIGN_LEFT);

                Paragraph customerbeathead = new Paragraph("Beat : "+SELECTED_SHOPE.getGroup(), font8bold);
                customerbeathead.setAlignment(Element.ALIGN_LEFT);



                Paragraph customerbeat = new Paragraph("Beat:", font8bold);
                customerbeat.setAlignment(Element.ALIGN_LEFT);

                Paragraph referhead = new Paragraph("Reference:", font8bold);
                referhead.setAlignment(Element.ALIGN_LEFT);

                Paragraph referid = new Paragraph("Reference:", font8bold);
                referid.setAlignment(Element.ALIGN_LEFT);

                Paragraph compname = new Paragraph(""+compny_name, font8bold);
                compname.setAlignment(Element.ALIGN_LEFT);

                Paragraph compaddress = new Paragraph(""+address_new, font8bold);
                compaddress.setAlignment(Element.ALIGN_LEFT);

                Paragraph compaddress2 = new Paragraph(address2Str, font8);
                compaddress2.setAlignment(Element.ALIGN_LEFT);



                Paragraph compPlaceTag = new Paragraph(address1Str, fontArb8);
                compPlaceTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compMobileTag = new Paragraph(address2Str+",Mob : "+mobileStr+", CR :"+compRegisterStr+" "+arabicPro.process(address2ArabStr), fontArb8);
                compMobileTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
                compEmailEng.setAlignment(Element.ALIGN_CENTER);

                Paragraph compdate = new Paragraph(compDateStr, font8);
                compdate.setAlignment(Element.ALIGN_RIGHT);

                Paragraph compGst = new Paragraph("GSTIN:"+companyVatStr +"  State Code : KA", font8);
                compGst.setAlignment(Element.ALIGN_LEFT);

                Paragraph compfssai = new Paragraph("FSSAI Number : "+companyFssai_no+companyPan_No , font8);
                compfssai.setAlignment(Element.ALIGN_LEFT);

                Paragraph secondcoln = new Paragraph(comBillNohead+""+comBillNo+"\n"+comBillDatehead+" "+comBillDate+"\n"+customeridhead+""+customerid+"\n"+customerbeathead+""+customerbeat+"\n"+referhead+""+referid, font8);
                secondcoln.setAlignment(Element.ALIGN_LEFT);

                Paragraph compVatTag = new Paragraph("                                                             VAT Number "+companyVatStr +"                Original For Customer", fontArb8);



                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (paid_amount == SELECTED_RETURN.getTotal()) {
                            //  paragraph = new Paragraph(arabicPro.process("الفاتورة النقدية - " + " CASH INVOICE"), fontArb14);
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                        } else {
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                            // paragraph = new Paragraph(arabicPro.process("بطاقة الائتمان - " + " CREDIT INVOICE"), fontArb14);
                        }


                        break;

                    case ActivityConstants.ACTIVITY_QUOTATION:

                        Paragraph  paragraph = new Paragraph(" QUOTATION", fontArb14);

                        break;
                }

                //////////////////////////////////////////

                PdfPCell cell;  //default cell

                //space cell
                PdfPCell cellSpace = new PdfPCell();
                cellSpace.setPadding(10);
                cellSpace.setBorder(PdfPCell.BOTTOM);
                cellSpace.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{5});

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP  );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                Paragraph paragraph = new Paragraph("CREDIT NOTE", font8bold);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);
                table.addCell(cell);


                document.add(table);

                //Create the table which will be 2 Columns wide and make it 100% of the page
                table = new PdfPTable(3);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{8, 8, 4});

                PdfPCell cellLogo = new PdfPCell();
                cellLogo.setBorder(PdfPCell.TOP | PdfPCell.RIGHT );
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setPaddingTop(10);
                cellLogo.setPaddingBottom(5);
//                cellLogo.setPaddingRight(5);
//                cellLogo.setPaddingLeft(5);

                try {

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icresp_newlogo);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image img = Image.getInstance(stream.toByteArray());
//                  img.setAbsolutePosition(25f, 735f);
                    img.scalePercent(35f);
                    img.setAlignment(Element.ALIGN_CENTER);
                    cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellLogo.addElement(img);


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                PdfPCell cellTitle = new PdfPCell();
                cellTitle.setBorder( PdfPCell.TOP );

                cellTitle.setPadding(1);

                paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICESSS"), fontArb14);

                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (paid_amount == SELECTED_RETURN.getTotal()) {
                            //  paragraph = new Paragraph(arabicPro.process("الفاتورة النقدية - " + " CASH INVOICE"), fontArb14);
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                        } else {
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                            // paragraph = new Paragraph(arabicPro.process("بطاقة الائتمان - " + " CREDIT INVOICE"), fontArb14);
                        }


                        break;
                    case ActivityConstants.ACTIVITY_SALE_REPORT:

                        if (paid_amount == SELECTED_RETURN.getTotal()) {
                            // paragraph = new Paragraph(arabicPro.process("الفاتورة النقدية - " + " CASH INVOICE"), fontArb14);
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                        } else {
                            //  paragraph = new Paragraph(arabicPro.process("بطاقة الائتمان - " + " CREDIT INVOICE"), fontArb14);
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                        }


                        break;
                    case ActivityConstants.ACTIVITY_QUOTATION:

                        paragraph = new Paragraph(arabicPro.process("" + " QUOTATION"), fontArb14);

                        break;
                }

                paragraph.setAlignment(Element.ALIGN_CENTER);
                cellTitle.setHorizontalAlignment(Element.ALIGN_LEFT);


                cellTitle.addElement(comBillNo);
                cellTitle.addElement(comBillDate);
                cellTitle.addElement(customerid);
                cellTitle.addElement(customerroute);
                cellTitle.addElement(customerbeathead);
                cellTitle.addElement(referid);
                // cellTitle.addElement(compweb);



                ////////////////3rd column

                PdfPCell cellthirdcolumn = new PdfPCell();
                cellthirdcolumn.setBorder(PdfPCell.TOP | PdfPCell.LEFT);
                cellthirdcolumn.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellthirdcolumn.setPadding(1);



                cellthirdcolumn.addElement(compname);
                cellthirdcolumn.addElement(compaddress);
                cellthirdcolumn.addElement(compaddress2);
                cellthirdcolumn.addElement(compMail);
                cellthirdcolumn.addElement(compGst);
                cellthirdcolumn.addElement(compfssai);
                cellthirdcolumn.addElement(compNamePhone);

                table.addCell(cellthirdcolumn);
                table.addCell(cellTitle);
                table.addCell(cellLogo);
                document.add(table);



                //second table


                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1,6});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.TOP | PdfPCell.LEFT | PdfPCell.BOTTOM);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //230

                //            customer Address  label
                paragraph = new Paragraph("Buyer (Bill To)  :" , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.TOP | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //230

                //            customer Address  label
                paragraph = new Paragraph(" " , font8);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                paragraph = new Paragraph("Name :" +SELECTED_SHOPE.getShopName(), font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                paragraph = new Paragraph("" , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                document.add(table);


                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{4});
                // customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(0);

                paragraph = new Paragraph("Address  :"+SELECTED_SHOPE.getShopAddress() , font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("" , font8);//strDate
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);
                cell.setPaddingRight(10);

                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("Contact No  :"+SELECTED_SHOPE.getShopMobile() , font8);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);


                table.addCell(cell);

                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(2);
                //230

                //            customer Address  label
                paragraph = new Paragraph("GSTIN  :"+SELECTED_SHOPE.getVatNumber() , font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);


                table.addCell(cell);



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(1);
                //230

                paragraph = new Paragraph("State Code :  "+SELECTED_SHOPE.getState_code() , font8);//SELECTED_SALES.getPayment_type()
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(1);

                table.addCell(cell);


                document.add(table);
                //Create the table which will be 8 Columns wide and make it 100% of the page
//                table = new PdfPTable(17);
//                table.setWidths(new int[]{2,3,6,3, 3,3, 3, 3, 3,3,3,2,3,2,3,2,3});
//                table.setWidthPercentage(100);

                table = new PdfPTable(15);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,4,2,3,2,3,3});
                table.setWidthPercentage(100);
//            table.setSpacingBefore(10);


                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Sl No\n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("HSN/SAC\n Code" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);




                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                paragraph = new Paragraph("Discription of Goods/Services \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                paragraph = new Paragraph("Qty\n(Kg)" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                paragraph = new Paragraph("Qty\n(Pcs)" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("MRP/Pc" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                //            temporary
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Rate/Kg" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Rate/Pc" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);

                table.addCell(cell);





                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Disc" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);





                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Taxable Amt \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);



                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("CGST \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setColspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("SGST \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setColspan(2);
                table.addCell(cell);

//                cell = new PdfPCell();
//                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//                paragraph = new Paragraph("IGST \n" + arabicPro.process(""), font8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//                cell.setColspan(2);
//                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Gross Amt \n" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("%" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Rs" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("%" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("Rs" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);



                String str_vatRate_tot="",str_unit_tot = " ",str_vatRate_total="",justifiedtax_5="",justifiedtax_12="",justifiedtax_18="";;

                float tot_qnty = 0;
                double  total_grossamnt =0 , total_taxamnt=0 , totalcgst=0 ,total_taxable_amnt =0 ,total_qtypcs=0 ,
                        total_grand =0 ,dbl_taxtotal_5 = 0,dbl_taxtotal_12 = 0, dbl_taxtotal_18 = 0 ,dbl_cgst_tot_5 =0 ,
                        dbl_cgst_tot_12 = 0,dbl_cgst_tot_18=0 ,dbl_grossamnt_5 =0 ,dbl_grossamnt_12 =0 ,dbl_grossamnt_18=0,
                        dbl_taxtotal_gross=0 ,dbl_cgst_total=0,total_gst =0,dbl_totalkg_qty=0;
                // double prod_discnt_totl=0;
                for (int j = 0; j < MAX_LINE; j++) {
                    double piece_price=0;

                    String strSl_No = " ", strP_Code = " ",strP_Name = " ", strP_Arabic = "  ", strQty = " ", strqty_pieces =" " ,strNetPrice = " ", strNetTotal = " ",
                            strAmount = " ", strTotalPrice = " ", str_vatAmount = " ",str_unit ="",str_vatRate ="",
                            str_prod_disc ="" , strbarcode ="",str_mfgdate ="",str_cgst_rate ="",str_cgst="",str_mrp="",
                            st_taxable_amnt="" ,strpieceprice ="" ,str_Grossamnt ="",str_tax_5="",
                            str_tax_12="",str_tax_18="" ,st_taxableamnt ="" ,str_rateperkg ="",str_hsncode="";

                    if (cartList.size() > j) {
                        CartItem cartItem = cartList.get(j);

                        int slNo = i * MAX_LINE + j + 1;
                        strSl_No = String.valueOf(slNo);
                        strP_Name = cartItem.getProductName();
                        strP_Arabic = cartItem.getArabicName();
                        strP_Code=cartItem.getProductCode();
                        str_unit = cartItem.getUnitselected();
                        str_unit_tot =cartItem.getUnitselected();
                        str_prod_disc = String.valueOf(cartItem.getProductDiscount());
                        // prod_discnt_totl = prod_discnt_totl +(cartItem.getProductDiscount()*cartItem.getPieceQuantity_nw());
                        strbarcode = cartItem.getBarcode();
                        str_mfgdate = cartItem.getMfg_date();
                        str_hsncode = cartItem.getProduct_hsncode();

                        Log.e("strP_Arabic",""+strP_Arabic);

                        if (strP_Arabic == null || TextUtils.isEmpty(strP_Arabic.trim()) || strP_Arabic.equals("null"))
                            strP_Arabic = "  ";

                        float pieces =0;

                        if(cartItem.getUnitid_selected().equals("1")){
                            strqty_pieces = ""+cartItem.getTypeQuantity();
                            pieces =cartItem.getTypeQuantity();
                            strQty = ""+getAmountthree(cartItem.getTypeQuantity()/Integer.valueOf(cartItem.getConfactr_kg()));
                            dbl_totalkg_qty =  (cartItem.getTypeQuantity()/Integer.valueOf(cartItem.getConfactr_kg()));
                        }
                        else {
                            strqty_pieces = ""+cartItem.getPieceQuantity();
                            strQty = ""+String.valueOf(getAmountthree(cartItem.getTypeQuantity())); // piece only
                            dbl_totalkg_qty = cartItem.getTypeQuantity();
                            pieces =cartItem.getPieceQuantity();
                        }


                        if(SELECTED_RETURN.getReturn_type().equals("with")){
                            str_cgst= ""+getAmount(roundTwoDecimalsbytwo((cartItem.getTaxValue()*cartItem.getTypeQuantity())/2));
                        }
                        else{
                            str_cgst= ""+getAmount(roundTwoDecimalsbytwo(cartItem.getTaxValue()/2));
                        }
                        str_cgst_rate = ""+roundTwoDecimalsbytwo(cartItem.getTax()/2);
                        //  str_cgst= ""+getAmount(roundTwoDecimalsbytwo(cartItem.getTaxValue())/2);

                        double mrp_price = cartItem.getMrprate();
                        str_mrp = ""+cartItem.getMrprate();
                        Log.e("mrppppp",""+cartItem.getMrprate());
                        Log.e("str_cgst",""+str_cgst);
                        total_gst =0;
                        if(SELECTED_RETURN.getReturn_type().equals("with")) {
                            total_gst = total_gst + (cartItem.getTaxValue()*cartItem.getTypeQuantity());
                            Log.e("total_gst",""+total_gst);
                        }
                        else{
                            total_gst = total_gst + cartItem.getTaxValue() ;
                        }
                        double dbl_vat5 = 0 , dbl_vat12=0,dbl_vat18 =0;
                        if(cartItem.getTax()==5 && cartItem.getTaxValue()>0){
                            dbl_taxtotal_5 = dbl_taxtotal_5 + cartItem.getProductTotal();

                            dbl_cgst_tot_5 = dbl_cgst_tot_5 + ((cartItem.getTaxValue()*cartItem.getTypeQuantity())/2) ;

                            dbl_vat5 = dbl_vat5 + cartItem.getTaxValue();
                            dbl_grossamnt_5 = dbl_taxtotal_5 + dbl_cgst_tot_5+dbl_cgst_tot_5;

                        }
                        if(cartItem.getTax()==12 && cartItem.getTaxValue()>0){
                            dbl_taxtotal_12 = dbl_taxtotal_12 + cartItem.getProductTotal();

                            dbl_cgst_tot_12 = dbl_cgst_tot_12 + ((cartItem.getTaxValue()*cartItem.getTypeQuantity())/2);

                            dbl_grossamnt_12 = dbl_taxtotal_12 +  dbl_cgst_tot_12+dbl_cgst_tot_12;
                            dbl_vat12 = dbl_vat12 + cartItem.getTaxValue();

                        }
                        if(cartItem.getTax()==18 && cartItem.getTaxValue()>0){
                            dbl_taxtotal_18 = dbl_taxtotal_18 + cartItem.getProductTotal();

                            dbl_cgst_tot_18 = dbl_cgst_tot_18 + ((cartItem.getTaxValue()*cartItem.getTypeQuantity())/2);

                            dbl_grossamnt_18 = dbl_taxtotal_18 +  dbl_cgst_tot_18+dbl_cgst_tot_18;
                            dbl_vat18 = dbl_vat18 + cartItem.getTaxValue();

                        }
                        dbl_taxtotal_gross = dbl_taxtotal_5+dbl_taxtotal_12+dbl_taxtotal_18;
                        dbl_cgst_total = dbl_cgst_tot_5 + dbl_cgst_tot_12 +dbl_cgst_tot_18;
                        Log.e("dbl_taxtotal_5",""+dbl_taxtotal_5);
                        Log.e("dbl_grossamnt_18",""+dbl_grossamnt_18);

                        double prod_taxable_amount = Double.parseDouble(getAmount(cartItem.getWithouttaxTotal()))-(cartItem.getProductDiscount()*cartItem.getTypeQuantity());
                        st_taxable_amnt = ""+prod_taxable_amount;
//                        double netPrice = cartItem.getNetPrice();
                        double netPrice = cartItem.getProductPrice();
                        Log.e("netpriceeeeeeeeeee",""+netPrice);

                        double net_total = cartItem.getProductTotal();

//                        if(cartItem.getTax_type().equals("TAX_INCLUSIVE")) {
//                            netPrice = cartItem.getProductTotal()/cartItem.getPieceQuantity_nw();
//                        }
                        // For case and piece

                        if (cartItem.getOrderType().equals(ConfigValue.PRODUCT_UNIT_CASE)) {
                            netPrice = netPrice * cartItem.getPiecepercart();
//                            strQty = cartItem.getTypeQuantity() + "/0";
                            strQty = cartItem.getTypeQuantity() +"";
                        }
                        //piece_price = ((cartItem.getProductPrice() * cartItem.getTypeQuantity())-(cartItem.getProductDiscount() * cartItem.getTypeQuantity())/cartItem.getPieceQuantity());


                        //  tot_qnty =tot_qnty + cartItem.getTypeQuantity();
                     //   tot_qnty = Float.parseFloat(getAmount(tot_qnty + (float) dbl_totalkg_qty));


//haris chaned on 04-08-2021
                        //  strNetTotal = getAmount(cartItem.getProductPrice() * cartItem.getPieceQuantity());

                        str_vatRate = String.valueOf(cartItem.getTax() + " %");
                        str_vatRate_tot= String.valueOf(cartItem.getTax() + " %");
                        str_vatRate_total= String.valueOf(SELECTED_RETURN.getTaxAmount());
                        // cartItem.setTaxValue(AmountCalculator.getTaxPrice(cartItem.getProductPrice(), cartItem.getTax(),cartItem.getTax_type()));

                        ///////////////////////added by haris on 24-02-22
                        strNetTotal = getAmount((cartItem.getProductPrice() * cartItem.getTypeQuantity())-(cartItem.getProductDiscount() * cartItem.getTypeQuantity()));
                        float total = (float) ((cartItem.getProductPrice() * cartItem.getTypeQuantity())-(cartItem.getProductDiscount() * cartItem.getTypeQuantity()));
                       // piece_price =total/ cartItem.getPieceQuantity();
                        piece_price = cartItem.getUnit_rate_pcs();
                        //  piece_price = cartItem.getPieceQuantity();

                        ///////////////////////////////////////////
                        double net = roundTwoDecimalsbytwo(cartItem.getNetPrice());
                        if(cartItem.getTax_type().equals("TAX_INCLUSIVE")){
                            net = (roundTwoDecimalsbytwo(cartItem.getProductTotal())/cartItem.getPieceQuantity_nw());
                            Log.e("tax invclusiveee",""+net);
                        }
                        Log.e("taxableeee",""+net);
                        // double d = net * cartItem.getPieceQuantity_nw();

                        //double d = cartItem.getProductPrice() * cartItem.getReturnQuantity();
                        double d = (net * cartItem.getTypeQuantity());
                        //   double d = roundTwoDecimalsbytwo(cartItem.getProductPrice() * cartItem.getTypeQuantity())-roundTwoDecimalsbytwo(cartItem.getTypeQuantity()*cartItem.getProductDiscount());
                        st_taxableamnt = ""+getAmount(d);

                        // double nettotalwithvat = roundTwoDecimalsbytwo(d+dbl_vat5+dbl_vat12+dbl_vat18);
                        double nettotalwithvat=0;
                        Log.e("total",""+getAmount(d));
                        Log.e("totalhrct",""+(d));
                        Log.e("totalharis",""+(d + (total_gst)));
//                        if(d+(total_gst)>0) {
//                             nettotalwithvat = roundTwoDecimalsbytwo(d + (total_gst));
//                        }
//                        else{
                        nettotalwithvat = (d + (total_gst));
                        Log.e("nettotalwithvat",""+(nettotalwithvat));
                        // }

                        ////////////////////////////////////////////////////////


                        // cartItem.setTaxValue(AmountCalculator.getTaxPrice(cartItem.getProductPrice(), cartItem.getTax(),cartItem.getTax_type()));
                        str_vatAmount = getAmount(cartItem.getTaxValue()* cartItem.getPieceQuantity_nw());

                        strpieceprice = getAmount(piece_price);
                        strTotalPrice = getAmount((cartItem.getProductPrice() * cartItem.getPieceQuantity_nw())+(cartItem.getTaxValue()* cartItem.getPieceQuantity_nw()));


                        total_taxamnt =0;
                        total_taxamnt = total_taxamnt +SELECTED_RETURN.getTaxAmount();

                        totalcgst = roundTwoDecimals(total_taxamnt/2);
                        str_Grossamnt = ""+roundTwoDecimalsbytwo(nettotalwithvat);
                        total_grossamnt = total_grossamnt + nettotalwithvat;
                        Log.e("total_grossamnt",""+(total_grossamnt));

                        total_taxable_amnt = total_taxable_amnt+d;
                        total_taxable_amnt = getNetTotal() -(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount());
                        total_qtypcs = total_qtypcs+pieces;

                        if(SELECTED_RETURN.getRoundoff_value()>0) {
                            Log.e("total_grossamnt if",""+(SELECTED_RETURN.getTotal() - (SELECTED_RETURN.getDiscount_value() - SELECTED_RETURN.getDiscount())));
                            total_grossamnt =(SELECTED_RETURN.getTotal() - (SELECTED_RETURN.getDiscount_value() - SELECTED_RETURN.getDiscount())) - SELECTED_RETURN.getRoundoff_value();
                        }
                        else{
                            Log.e("total_grossamnt else",""+(SELECTED_RETURN.getTotal() - (SELECTED_RETURN.getDiscount_value() - SELECTED_RETURN.getDiscount())));
                            total_grossamnt = (SELECTED_RETURN.getTotal() - (SELECTED_RETURN.getDiscount_value() - SELECTED_RETURN.getDiscount())) - SELECTED_RETURN.getRoundoff_value();
                        }
                        total_grand = SELECTED_RETURN.getTotal();
                        ////////////////////
                        strNetPrice = getAmount(netPrice);

                        str_rateperkg = getAmount(cartItem.getUnit_rate_kg());

                    }


                    if (strP_Name.length() > 40)
                        strP_Name = getMaximumChar(strP_Name, 40);



                    if (strP_Arabic.length() > 42)
                        strP_Arabic = getMaximumChar(strP_Arabic, 42);


                    String justifiedSlNo = String.format("%-3s", strSl_No);
                    String justifiedCode = String.format("%-5s", strP_Code);

                    String justifiedQuantity = String.format("%-5s", strQty);
                    String justifiedNetPrice = String.format("%-5s", strNetPrice);
                    String justifiedNetTotal = String.format("%-5s", strNetTotal);
                    String justifiedVatRate = String.format("%-5s", str_vatRate);
                    String justifiedVatAmount = String.format("%-5s", str_vatAmount);
                    String justifiedTotal = String.format("%-5s", strTotalPrice);
                    String justifiedunit = String.format("%-5s", str_unit);
                    String justifiedprod_disc = String.format("%-5s", str_prod_disc);
                    String justifiedQntyPieces = String.format("%-5s",strqty_pieces);
                    String justifiedPricePieces = String.format("%-5s",strpieceprice);
                    String justifiedGrossamnt = String.format("%-5s",str_Grossamnt);
                    String justifiedtaxablamnt = String.format("%-5s",st_taxableamnt);
                    String justifiedmrp = String.format("%-5s",str_mrp);
                    String justifiedmrpperkg = String.format("%-5s",str_rateperkg);
                    String justifiedhsncode = String.format("%-5s",str_hsncode);

                    if (strP_Name.length() > 40)
                        strP_Name = getMaximumChar(strP_Name, 40);

                    if (strP_Arabic.length() > 42)
                        strP_Arabic = getMaximumChar(strP_Arabic, 42);


                    //strSl_No

                    cell = new PdfPCell(new Phrase(strSl_No, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);

//sl number
                    cell = new PdfPCell(new Phrase(""+justifiedhsncode, font8));//strP_Code
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);





                    cell = new PdfPCell(new Phrase(""+strP_Name, font8));

                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    //Qty

//                    if(strQty.length()>0 && str_unit.length()>1) {
//                        cell = new PdfPCell(new Phrase(strQty , font8));
//                        cell.setBorder(Rectangle.RIGHT);
//                        cell.setPadding(2);
//                        cell.setRowspan(1);
//                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                        table.addCell(cell);
//                    }
//                    else{
                    cell = new PdfPCell(new Phrase(justifiedQuantity, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    //  }

                    //Qty pcs
                    cell = new PdfPCell(new Phrase(""+justifiedQntyPieces, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //mrp/pc
                    cell = new PdfPCell(new Phrase(justifiedmrp, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    //Rate/Kg
                    cell = new PdfPCell(new Phrase(""+justifiedmrpperkg, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //Rate/Pc
                    cell = new PdfPCell(new Phrase(""+justifiedPricePieces, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);




                    // discnt
                    cell = new PdfPCell(new Phrase(justifiedprod_disc, font8));//justifiedTotal //getTaxAmount
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    // taxable amnt
                    cell = new PdfPCell(new Phrase(""+justifiedtaxablamnt, font8));//justifiedTotal //getTaxAmount
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    // cgst rate
                    cell = new PdfPCell(new Phrase(""+str_cgst_rate, font8));//justifiedTotal //getTaxAmount
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    /////////////////////second row

                    // cgst mrp

                    cell = new PdfPCell(new Phrase(""+str_cgst, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);

                    //// sgst rate
                    cell = new PdfPCell(new Phrase(" "+str_cgst_rate, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);


                    // sgst mrp
                    cell = new PdfPCell(new Phrase(" "+str_cgst, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);




//  gross amnt
                    cell = new PdfPCell(new Phrase(""+justifiedGrossamnt, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);



                }

                document.add(table);
                double discnt_withoutprod_disc=0;
//                if(SELECTED_SALES.getDiscount_value()>0){
//                     discnt_withoutprod_disc = SELECTED_SALES.getDiscount_value()-prod_discnt_totl;
//
//                }
//                Log.e("getDiscount_value",""+SELECTED_SALES.getDiscount_value());
//                Log.e("prod_discnt_totl",""+prod_discnt_totl);

                if(SELECTED_RETURN.getDiscount_percentage()==0) {

                    if(discnt_withoutprod_disc>0) {
                        disc_percent = (discnt_withoutprod_disc * 100) / net_Amount;
                        Log.e("if dscnt", "" + disc_percent);
                    }
                }
                else{
                    disc_percent = SELECTED_RETURN.getDiscount_percentage();
                    Log.e("else dscnt",""+disc_percent);
                }

                disc_percent = roundTwoDecimals(disc_percent);
                val_in_english = convertNumberToEnglishWords(String.valueOf(total_grand));
                val_in_english_vat = convertNumberToEnglishWords(String.valueOf(str_vatRate_total));
                Log.e("number inn",""+db_grandtot);


                table = new PdfPTable(15);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,4,2,3,2,3,3});

                table.setWidthPercentage(100);

//1
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                table.addCell(cell);

                //2
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //3
                cell = new PdfPCell();


                paragraph = new Paragraph("Total" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);
//4
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" +getAmountthree(tot_qnty), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //5
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" +total_qtypcs, font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //6
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //9
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //10
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + getAmount(total_taxable_amnt), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12

                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" +getAmount(totalcgst), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);


                //13
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //14
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + getAmount(totalcgst), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



                //17
                //total_grossamnt = total_grossamnt + netPrice+SELECTED_SALES.getTaxAmount();
                //                //                        str_Grossamnt = ""+(netPrice+SELECTED_SALES.getTaxAmount());
                //                //                        total_taxamnt = total_taxamnt +SELECTED_SALES.getTaxAmount();
                //                //                        totalcgst = total_taxamnt/2;
                //                //                        total_taxable_amnt = total_taxable_amnt+netPrice;
                //                //                        total_qtypcs = total_qtypcs+cartItem.getPieceQuantity();
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + getAmount(total_grossamnt), font8bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);



                document.add(table);

                table = new PdfPTable(15);
                // table.setWidths(new int[]{2,3,8,3, 3,3, 3, 3, 3,3,3,2,3,2,3});
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,4,2,3,2,3,3});
                table.setWidthPercentage(100);


//second row
                //1
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //2

                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //3

                cell = new PdfPCell();
                paragraph = new Paragraph("Less:" + arabicPro.process(""), font8Italicunderline);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setBorder(PdfPCell.TOP |PdfPCell.RIGHT);
                table.addCell(cell);

                //4
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.addElement(paragraph);
                table.addCell(cell);

                //5
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //    6        temporary
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);


                //9
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



                //10
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //13
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //14
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM |PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

//                //15
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.BOTTOM);
//                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//                paragraph = new Paragraph("" + arabicPro.process(""), font8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//                table.addCell(cell);
//
//                //16
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.BOTTOM);
//                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//                paragraph = new Paragraph("" + arabicPro.process(""), font8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//                table.addCell(cell);

                //17
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP | PdfPCell.BOTTOM);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(15);
                //table.setWidths(new int[]{2,3,8,3, 3,3, 3, 3, 3,3,3,2,3,2,3});
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,4,2,3,2,3,3});
                table.setWidthPercentage(100);
                //third row

                //1
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //2
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //3

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP);
                paragraph = new Paragraph("TCS" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //4
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //5
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);
                //            temporary
                //6
                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //9

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);


                //10
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //13
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //14
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



                //17
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOX);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(15);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,4,2,3,2,3,3});
                table.setWidthPercentage(100);
                //fourth row

                //1
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                table.addCell(cell);

                //2
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                //3

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("Variable Disc" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //4
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                table.addCell(cell);

                //5
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                table.addCell(cell);

                //  6          temporary
                cell = new PdfPCell();

                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //9
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                //10
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //13
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);

                //14
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                //17
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOX);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph(""+getAmount(discnt), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                document.add(table);

                table = new PdfPTable(15);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,4,2,3,2,3,3});
                table.setWidthPercentage(100);
                //fifth row
                //1
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                table.addCell(cell);

                //2
                cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);



//3
                cell = new PdfPCell();
                paragraph = new Paragraph("Round off" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                cell = new PdfPCell();
//4
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);


                //5
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //    6        temporary
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);


                //9
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);


                //10

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //13
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.addElement(paragraph);
                table.addCell(cell);

                //14
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);



                //17
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.BOX);
                paragraph = new Paragraph(""+getAmount(SELECTED_RETURN.getRoundoff_value()), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);


                document.add(table);

                /////////6th row
                table = new PdfPTable(15);
                table.setWidths(new int[]{2,4,8,4, 3,3, 3, 3, 3,4,2,3,2,3,3});
                table.setWidthPercentage(100);

                //fifth row 1
                cell = new PdfPCell();

                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //2
                cell = new PdfPCell();

                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);


//3

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP | PdfPCell.RIGHT);
                paragraph = new Paragraph("Grand Total" + arabicPro.process(""), font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //4

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //5

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //     6       temporary
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //7
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //8
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //9
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);


                //10
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //11

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //12

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);

                //13

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                //14

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph("" + arabicPro.process(""), font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                table.addCell(cell);


                //17

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOX);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                paragraph = new Paragraph(""+getAmount(total_grand), font8bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);



                document.add(table);
                //new rows for output vat

                //////////////////////////////

                table = new PdfPTable(2);
                table.setWidths(new int[]{9,6});
                table.setWidthPercentage(100);

                cell = new PdfPCell();

                paragraph = new Paragraph("Amount in Words: \n"+val_in_english, font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingLeft(5);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.LEFT);
                table.addCell(cell);



                cell = new PdfPCell();
                paragraph = new Paragraph("Bank Details :", font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT );

                table.addCell(cell);


                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );

                table.addCell(cell);


                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT);

                table.addCell(cell);



                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);

                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );

                table.addCell(cell);


                cell = new PdfPCell();
                paragraph = new Paragraph("Name : "+str_bankaccname, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT );

                table.addCell(cell);
                document.add(table);


                table = new PdfPTable(2);
                table.setWidths(new int[]{9,6});
                table.setWidthPercentage(100);



                cell = new PdfPCell();

                paragraph = new Paragraph("GST Details:", font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT | PdfPCell.LEFT | PdfPCell.TOP);


                table.addCell(cell);



                cell = new PdfPCell();

                paragraph = new Paragraph("Bank :"+str_bankname, font8);
                cell.setBorder(PdfPCell.RIGHT );
                cell.setPaddingLeft(5);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("Tax Rate", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);

                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("Taxable Amount", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER );
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("CGST", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("SGST", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("IGST", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("Gross Amount", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);

                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);



                cell = new PdfPCell();
                //paragraph = new Paragraph("A/c No :\n IFSC CODE \n branch", font8bold);
                paragraph = new Paragraph("A/c No : "+str_accno, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT );
                table.addCell(cell);

//                cell = new PdfPCell();
//                cell.setBorderWidth(1);
//                paragraph = new Paragraph("IFSC Code :", font8bold);
//                paragraph.setAlignment(Element.ALIGN_LEFT);
//                cell.addElement(paragraph);
//                cell.setRowspan(1);
//                cell.setFixedHeight(5);
//                cell.setBorder(PdfPCell.RIGHT );
//                table.addCell(cell);
//
//                cell = new PdfPCell();
//                cell.setBorderWidth(1);
//                paragraph = new Paragraph("Branch :", font8bold);
//                paragraph.setAlignment(Element.ALIGN_LEFT);
//                cell.addElement(paragraph);
//                cell.setRowspan(1);
//                cell.setFixedHeight(5);
//                cell.setBorder(PdfPCell.RIGHT );
//                table.addCell(cell);


                document.add(table);

//5%
                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("5%", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setRowspan(1);
                table.addCell(cell);



                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_taxtotal_5), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_tot_5), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_tot_5), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_grossamnt_5), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("IFSC Code : "+str_ifsc, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setPaddingLeft(5);
                cell.setBorder(PdfPCell.RIGHT );
                table.addCell(cell);




                document.add(table);


                //12%
                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("12%", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_taxtotal_12), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_tot_12), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_tot_12), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_grossamnt_12), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("Branch : "+str_bankbranch, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingLeft(5);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT);
                table.addCell(cell);


                document.add(table);

//18%
                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("18%", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_taxtotal_18), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_tot_18), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_tot_18), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_grossamnt_18), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT);
                table.addCell(cell);



                document.add(table);

                //TOtal
                table = new PdfPTable(7);
                table.setWidths(new int[]{2,2,1,1,1,2,6});
                table.setWidthPercentage(100);

                ////columns
                cell = new PdfPCell();
                paragraph = new Paragraph("Total", font8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_taxtotal_gross), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_total), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(dbl_cgst_total), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("0.00", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(""+getAmount(total_grossamnt), font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM | PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("", font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.BOTTOM );
                table.addCell(cell);



                document.add(table);

                //////////////////////
//                Declaration



                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});

                cell = new PdfPCell();
                paragraph = new Paragraph("Declaration:", font6);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );


                table.addCell(cell);
                document.add(table);

                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});

                cell = new PdfPCell();
                paragraph = new Paragraph("  We declare that this invoice shows the actual price of the goods described and that all particulars are true and correct.", font6);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );

                table.addCell(cell);
                document.add(table);


                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});

                cell = new PdfPCell();
                paragraph = new Paragraph("For Farmle Food Trading", font8bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                cell.setBorder(PdfPCell.RIGHT |PdfPCell.LEFT );

                table.addCell(cell);
                document.add(table);

                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5,5});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT | PdfPCell.BOTTOM );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(5);

                //            customer Address  label
                paragraph = new Paragraph("Customer's Seal and Signatory " , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder( PdfPCell.RIGHT | PdfPCell.BOTTOM );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(5);

                //            customer Address  label
                paragraph = new Paragraph("Authorised Signatory " , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                table.addCell(cell);


                document.add(table);

                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.NO_BORDER );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(1);

                //            customer Address  label
                paragraph = new Paragraph("  "+strDate , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setPaddingTop(1);
                cell.setRowspan(10);

                table.addCell(cell);



                document.add(table);

            }

            // %%%%%%%%%%%%%%%%%%%%%%%%%%% **************** %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            // setBackground(document);
            document.close();


            printPDF(myFile);  //Print PDF File




        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Log.d(TAG, "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    private void setBackground(Document document) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.torros_printbg);
        bitmap.compress(Bitmap.CompressFormat.PNG , 100, stream);

        Image img;
        try {
            img = Image.getInstance(stream.toByteArray());
            img.setAbsolutePosition(0, 0);
            img.scaleToFit(595,842);


            document.add(img);
        } catch (BadElementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * *************PRINTER
     ********************/


    @Override
    public void onPause() {
        if (sessionValue.isPOSPrint()) {
            if (mBluetoothAdapter != null) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
            }

            if (mConnector != null) {
                try {
                    mConnector.disconnect();
                } catch (P25ConnectionException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (sessionValue.isPOSPrint())
            unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size = data.size();
        list = new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark, getArray(mDeviceList));

        adapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerDevice.setAdapter(adapter);
        spinnerDevice.setSelection(0);
    }

    private void showDisabled() {
        showToast("Bluetooth disabled");

        btnEnable.setVisibility(View.VISIBLE);
        btnConnect.setVisibility(View.GONE);
        spinnerDevice.setVisibility(View.GONE);
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");

        btnEnable.setVisibility(View.GONE);
        btnConnect.setVisibility(View.VISIBLE);
        spinnerDevice.setVisibility(View.VISIBLE);
    }

    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");

        btnConnect.setEnabled(false);
        btnPrint.setEnabled(false);
        spinnerDevice.setEnabled(false);
    }

    private void showConnected() {
        showToast("Connected");

        btnConnect.setText("Disconnect");
        btnConnect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth_disconnected, 0, 0, 0);

        btnConnect.setBackgroundDrawable(getResources().getDrawable(R.drawable.quotation_right_badge));


        btnPrint.setEnabled(true);

        spinnerDevice.setEnabled(false);
    }

    private void showDisonnected() {
        showToast("Disconnected");

        btnConnect.setText("Connect");
        btnConnect.setBackgroundDrawable(getResources().getDrawable(R.drawable.return_right_finish));
        btnConnect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth_connected, 0, 0, 0);

        btnPrint.setEnabled(false);

        spinnerDevice.setEnabled(true);
    }

    private void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = mDeviceList.get(spinnerDevice.getSelectedItemPosition());

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(device);
            } catch (Exception e) {
                showToast("Failed to pair device");

                return;
            }
        }

        try {
            if (!mConnector.isConnected()) {
                mConnector.connect(device);
            } else {
                mConnector.disconnect();

                showDisonnected();
            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void createBond(BluetoothDevice device) throws Exception {

        try {
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};

            Method method = cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    private void sendData(byte[] bytes) {
        try {
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    /****
     * POS Print
     * */
    private void printReturnPOS(ArrayList<CartItem> list) {

        try {

            String bilType = "CREDIT INVOICE";
            switch (callingActivity) {
                case ActivityConstants.ACTIVITY_INVOICE_RETURN:

                    bilType = "INVOICE RETURN";

                    break;
                case ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN:

                    bilType =  " WITHOUT INVOICE RETURN";


                    break;
                default:
            }

            String invoiceLeftContent = " ";

            String nextLine = "\n";

            String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
            String addressContentStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session

            String registerContentStr = "CR  : " + sessionValue.getCompanyDetails().get(PREF_COMPANY_CR) + "\nVAT : " + sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);

            String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
            String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
            String routeMob = "Route Mobile No : "+sessionValue.getRegisteredMobile();  //route mob get from session

            String line = "------------------------------------------";

            StringBuilder contentExecutive = new StringBuilder();

            contentExecutive.append(line).append(nextLine).append(execName).append(nextLine).append(execId).append(nextLine).append(execMob).append(nextLine).append(routeMob);

            String customerStr = line+nextLine+"Customer No   : " + SELECTED_SHOPE.getShopCode() + "\nCustomer Name : " + strShopName;

            long milis = System.currentTimeMillis();

            String date = DateUtil.timeMilisToString(milis, "dd-MMM-yyyy h:mm:ss a") + "\n\n\n";

            StringBuilder contentTableTitle = new StringBuilder();

            StringBuilder contentItems = new StringBuilder();

            contentTableTitle.append("Item").append("            ").append("Qty").append("    ").append("Price").append("      ").append("Total").append(" ");

            for (CartItem c : list) {

                String strSl_No = " ", strQty = " ", strPrice = " ", strTotalPrice = " ";

                StringBuilder strP_Name = new StringBuilder("");

                strQty="0/"+c.getTypeQuantity();

                double netPrice=c.getNetPrice();
                if (c.getOrderType().equals(PRODUCT_UNIT_CASE)) {
                    netPrice = netPrice * c.getPiecepercart();
                    strQty=c.getTypeQuantity()+"/0";
                }

                strPrice = getAmount(netPrice);
                strTotalPrice = getAmount(c.getNetPrice() * c.getPieceQuantity());

                String[] nameArr = splitToNChar(c.getProductName(), 13);

                for (int i = 0; i < nameArr.length; i++) {

                    String paddedName = String.format("%-13s", nameArr[i]);

                    strP_Name.append(paddedName);
                    if (i != nameArr.length - 1)
                        strP_Name.append(" \n");

                }

                String paddedQty = String.format("%-5s", strQty);

                String paddedPrice = String.format("%7s", strPrice);
                String paddedTotal = String.format("%10s", strTotalPrice);

                contentItems.append(strP_Name).append(" ").append(paddedQty).append(" ").append(paddedPrice).append("  ").append(paddedTotal).append("\n");
            }

            String items = contentItems.toString();

            if (items.endsWith("\n")) {
                items = items.substring(0, items.length() - 1);
            }

            String subTotal = getAmount(getNetTotal()) + " " + CURRENCY;
            String vat = getAmount(getTaxTotal()) + " " + CURRENCY;
            String grand = getAmount(getGrandTotal()) + " " + CURRENCY;

            String paddedSub = String.format("%18s", subTotal);
            String paddedVat = String.format("%18s", vat);
            String paddedGrandTotal = String.format("%18s", grand);

            StringBuilder strSubTotal = new StringBuilder();

            strSubTotal.append("Total       : ").append(paddedSub).append("\nVat         : ").append(paddedVat).append("\nGrand Total : ").append(paddedGrandTotal);



            switch (callingActivity) {
                case ActivityConstants.ACTIVITY_INVOICE_RETURN:

                    bilType = "INVOICE RETURN";
                    invoiceLeftContent = "INVC NO : " + strBillNumber;

                    break;
                case ActivityConstants.ACTIVITY_WITHOUT_INVOICE_RETURN:

                    bilType =  " WITHOUT INVOICE RETURN";
                    invoiceLeftContent = "" ;


                    break;
                default:
            }





            String message = "Thank you for shopping at "+compName + "\n";


            byte[] nextLineByte = Printer.printfont(nextLine, FontDefine.FONT_32PX, FontDefine.Align_LEFT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            byte[] titleNameByte = Printer.printfont(compName, FontDefine.FONT_32PX, FontDefine.Align_CENTER,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            byte[] titleContentByte = Printer.printfont(addressContentStr, FontDefine.FONT_24PX, FontDefine.Align_CENTER,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            byte[] contentInvoiceRightByte = Printer.printfont(registerContentStr, FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            byte[] contentExecutiveDetailsByte = Printer.printfont(contentExecutive.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            byte[] contentCustomerByte = Printer.printfont(customerStr, FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


            byte[] contentInvoiceLeftByte = Printer.printfont(invoiceLeftContent, FontDefine.FONT_24PX, FontDefine.Align_RIGHT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


            byte[] tableByte = Printer.printfont(contentTableTitle.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            byte[] typeByte = Printer.printfont(bilType, FontDefine.FONT_24PX_UNDERLINE, FontDefine.Align_CENTER,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            byte[] lineByte = Printer.printfont(line, FontDefine.FONT_24PX, FontDefine.Align_CENTER,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


            byte[] cartItemsByte = Printer.printfont(items, FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


            byte[] contentSubTotalByte = Printer.printfont(strSubTotal.toString(), FontDefine.FONT_24PX, FontDefine.Align_RIGHT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);





            byte[] messageByte = Printer.printfont(message, FontDefine.FONT_24PX, FontDefine.Align_LEFT, (byte) 0x1A,
                    PocketPos.LANGUAGE_ENGLISH);


            byte[] dateByte = Printer.printfont(date, FontDefine.FONT_24PX, FontDefine.Align_CENTER, (byte) 0x1A,
                    PocketPos.LANGUAGE_ENGLISH);

            byte[] totalByte = new byte[nextLineByte.length + titleNameByte.length + titleContentByte.length + contentInvoiceRightByte.length + contentExecutiveDetailsByte.length + contentCustomerByte.length + contentInvoiceLeftByte.length + typeByte.length + lineByte.length + tableByte.length + lineByte.length + cartItemsByte.length + lineByte.length + contentSubTotalByte.length + messageByte.length +
                    dateByte.length];


            int offset = 0;

            System.arraycopy(nextLineByte, 0, totalByte, offset, nextLineByte.length);
            offset += nextLineByte.length;

            System.arraycopy(titleNameByte, 0, totalByte, offset, titleNameByte.length);
            offset += titleNameByte.length;

            System.arraycopy(titleContentByte, 0, totalByte, offset, titleContentByte.length);
            offset += titleContentByte.length;

            System.arraycopy(contentInvoiceRightByte, 0, totalByte, offset, contentInvoiceRightByte.length);
            offset += contentInvoiceRightByte.length;


            System.arraycopy(contentExecutiveDetailsByte, 0, totalByte, offset, contentExecutiveDetailsByte.length);
            offset += contentExecutiveDetailsByte.length;


            System.arraycopy(contentCustomerByte, 0, totalByte, offset, contentCustomerByte.length);
            offset += contentCustomerByte.length;

            System.arraycopy(contentInvoiceLeftByte, 0, totalByte, offset, contentInvoiceLeftByte.length);
            offset += contentInvoiceLeftByte.length;

            System.arraycopy(typeByte, 0, totalByte, offset, typeByte.length);
            offset += typeByte.length;

            System.arraycopy(lineByte, 0, totalByte, offset, lineByte.length);
            offset += lineByte.length;

            System.arraycopy(tableByte, 0, totalByte, offset, tableByte.length);
            offset += tableByte.length;

            System.arraycopy(lineByte, 0, totalByte, offset, lineByte.length);
            offset += lineByte.length;


            System.arraycopy(cartItemsByte, 0, totalByte, offset, cartItemsByte.length);
            offset += cartItemsByte.length;


            System.arraycopy(lineByte, 0, totalByte, offset, lineByte.length);
            offset += lineByte.length;


            System.arraycopy(contentSubTotalByte, 0, totalByte, offset, contentSubTotalByte.length);
            offset += contentSubTotalByte.length;


            System.arraycopy(messageByte, 0, totalByte, offset, messageByte.length);
            offset += messageByte.length;

            System.arraycopy(dateByte, 0, totalByte, offset, dateByte.length);


            byte[] sendData = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);

            sendData(sendData);


        }catch (NullPointerException e){
            Toast.makeText(this, "wrong print Request", Toast.LENGTH_SHORT).show();
        }
    }

    /****
     * PDF Print
     * */
    private List<PdfModel> getPdfModels(ArrayList<CartItem> list) {

        final List<PdfModel> models = new ArrayList<>();

        try {

            List<CartItem> sublist;


            if (list.size() <= MAX_LINE) {

                PdfModel pdfModel = new PdfModel();
                pdfModel.setCartItems(list);
                models.add(pdfModel);

            } else {

                int count = list.size() / MAX_LINE;

                int SUBLIST_START_SIZE = 0, SUBLIST_END_SIZE = MAX_LINE;

                for (int i = 0; i < count; i++) {

                    if (list.size() >= SUBLIST_END_SIZE) {
                        sublist = list.subList(SUBLIST_START_SIZE, SUBLIST_END_SIZE);
                        PdfModel pdfModel = new PdfModel();
                        pdfModel.setCartItems(sublist);
                        models.add(pdfModel);

                    }

                    if (SUBLIST_END_SIZE < list.size()) {
//                        return models;
                        SUBLIST_START_SIZE = SUBLIST_START_SIZE + MAX_LINE;
                        SUBLIST_END_SIZE = SUBLIST_END_SIZE + MAX_LINE;

                    }

                }

                if (list.size() < SUBLIST_END_SIZE) {
                    sublist = list.subList(SUBLIST_START_SIZE, list.size());
                    PdfModel pdfModel = new PdfModel();
                    pdfModel.setCartItems(sublist);
                    models.add(pdfModel);
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return models;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void printReturnPDF(List<PdfModel> pdfList) {


        File myFile = null;

        PdfWriter writer;
        try {

            String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
            String compNameArab = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME_ARAB);  //name get from session
            String address1Str = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
            String address1ArabStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1_ARAB);  //address get from session
            String address2Str = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_2);  //address get from session
            String address2ArabStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_2_ARAB);  //address get from session
            String compEmailStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_EMAIL);  //address get from session
            String mobileStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_MOBILE);  //address get from session
            String compDateStr = strDate;  //date get from session
            String compbillStr = strBillNumber;  //date get from session





            String compRegisterStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_CR);
            String companyVatStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
            //haris added on 06-11-2020
            String companyPan_No = sessionValue.getCompanyDetails().get(PREF_COMPANY_PAN_NO);
            Log.e("companyPan_No",""+companyPan_No);

            String execName = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
            String execMob = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
            String routeMob = sessionValue.getRegisteredMobile();  //route mob get from session

            // Create New Blank Document
            Rectangle pagesize = new Rectangle(634, 835);
            Document document = new Document(pagesize); //A4
            // Document document = new Document(PageSize.A4); //A4

            writer = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH));

            myFile = new File(FILE_PATH);

            document.open();


//            BaseFont bf = BaseFont.createFont("/assets/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont bf = BaseFont.createFont("/assets/dejavu_sans_condensed.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);


            Font font20 = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);

            Font font18 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

            Font font14 = new Font(Font.FontFamily.TIMES_ROMAN, 14);


            Font font10Bold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);

            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);

            Font font8bold = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.BOLD);

            Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);

            Font font12Bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);


            LanguageProcessor arabicPro = new ArabicLigaturizer();
            Font fontArb8 = new Font(bf, 8);
            Font fontArb10 = new Font(bf, 10);
            Font fontcompany10 = new Font(bf, 10, Font.BOLD);
            Font fontArb14 = new Font(bf, 14);

            for (int i = 0; i < pdfList.size(); i++) {


                PdfModel pdfData = pdfList.get(i);

                List<CartItem> cartList = pdfData.getCartItems();

                String netTotal = "", discount = "",  roundOff = "", hsn_code_total ="";

                String grandTotal = "";
                String paid = "";
                String balance = "";
                //haris
                String central_tax_rate = "";
                String central_tax_amnt = "";
                String state_tax_rate = "";
                String state_tax_amnt = "";
                String taxable_total = "";
                String total_tax = "";
                String total_discount = "";
                String discount_percntge = "";
                String st_data = "";
                String val_in_english = "";
                String valtaxableamnt_in_english = "";
                String val_in_Arabic = "";
                double taxable_amnt =0;
                String st_taxable_amnt = "";
                String st_grandtot ="";

                String totalVat = "";

                if (pdfList.size() == i + 1) {

                    netTotal = getAmount(getNetTotal()-(SELECTED_RETURN.getDiscount_value()-SELECTED_RETURN.getDiscount()));

                    if(SELECTED_RETURN.getRoundoff_value()!=0){
                        grandTotal = ""+SELECTED_RETURN.getRoundofftot();
                    }
                    else{
                        grandTotal = getAmount(SELECTED_RETURN.getWithTaxTotal());
                    }


                    paid = getAmount(paid_amount) + " " + CURRENCY;
                    discount = getAmount(SELECTED_RETURN.getDiscount());
                    balance = getAmount(SELECTED_RETURN.getTotal() - paid_amount) + " " + CURRENCY;
                    totalVat =  getAmount(getTaxTotal());

                    taxable_total = getAmount(SELECTED_RETURN.getTaxable_total());
                    total_tax = getAmount(SELECTED_RETURN.getTaxAmount());
                    //total_discount = getAmount(SELECTED_SALES.getDiscount_value());
                    total_discount = ""+discnt;
//                    st_data = "<?xml version=\"1.0\"?>\n" +
//                            "<invoice>\n" +
//                            "    <SellerName>"+ compName+"</SellerName>\n" +
//                            "    <VatNumber>" + str_kafeel_VatNo  +"</VatNumber>\n" +
//                            "    <DateTime>"+SELECTED_RETURN.getDate() +"</DateTime>\n" +
//                            "    <VatTotal>"+ totalVat +"</VatTotal>\n" +
//                            "    <TotalAmount>"+getAmount(SELECTED_RETURN.getWithTaxTotal())+"</TotalAmount>\n" +
//                            "</invoice>";
                    st_data = str_qrcodelink+""+strBillNumber;

                    String s = total_discount.replace(",", "");
                    String net = netTotal.replace(",","");
                    taxable_amnt = Double.parseDouble(""+Double.parseDouble(net))-Double.parseDouble(s);
                    st_grandtot = getAmount(db_grandtot);
                    taxable_amnt=roundTwoDecimals(taxable_amnt);

                    st_taxable_amnt = ""+taxable_amnt;

                    //  new_Outstanding = getAmount(SELECTED_SHOPE.getOutStandingBalance());

                    Transaction t = myDatabase.getCustomerTransactionBalance(SELECTED_SHOPE.getShopId());

                    new_Outstanding = getAmount(t.getOutStandingAmount());

                    if (SELECTED_RETURN.getPaid() == 0){

                        double prevbal = t.getOutStandingAmount() - SELECTED_RETURN.getTotal();
                        str_Previous_balance = getAmount(prevbal);

                        Log.e("Credit Prev Bal" , ""+str_Previous_balance);
                        Log.e("New OutStand" , ""+new_Outstanding);

                    }else {

                        str_Previous_balance = new_Outstanding;

                        Log.e("Cash Prev Bal" , ""+str_Previous_balance);
                        Log.e("New OutStand" , ""+new_Outstanding);

                    }

                    val_in_english = convertNumberToEnglishWords(String.valueOf(getAmount(db_grandtot)));

                }

                Paragraph compNameTag = new Paragraph(compName, fontcompany10);
                compNameTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compPlaceTag = new Paragraph(address1Str, fontArb8);
                compPlaceTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compMobileTag = new Paragraph(address2Str+",Mob : "+mobileStr+", CR :"+compRegisterStr+" "+arabicPro.process(address2ArabStr), fontArb8);
                compMobileTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
                compEmailEng.setAlignment(Element.ALIGN_CENTER);

                Paragraph compdate = new Paragraph(compDateStr, font8);
                compdate.setAlignment(Element.ALIGN_RIGHT);

                Paragraph compVatTag = new Paragraph("                                                             VAT Number "+companyVatStr +"                Original For Customer", fontArb8);
                //compVatTag.setAlignment();


                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1});






                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (paid_amount == SELECTED_RETURN.getTotal()) {
                            //  paragraph = new Paragraph(arabicPro.process("الفاتورة النقدية - " + " CASH INVOICE"), fontArb14);
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                        } else {
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                            // paragraph = new Paragraph(arabicPro.process("بطاقة الائتمان - " + " CREDIT INVOICE"), fontArb14);
                        }


                        break;
                    case ActivityConstants.ACTIVITY_SALE_REPORT:

                        if (paid_amount == SELECTED_RETURN.getTotal()) {
                            // paragraph = new Paragraph(arabicPro.process("الفاتورة النقدية - " + " CASH INVOICE"), fontArb14);
                            Paragraph  paragraph = new Paragraph( " TAX INVOICE", fontArb14);
                        } else {
                            //  paragraph = new Paragraph(arabicPro.process("بطاقة الائتمان - " + " CREDIT INVOICE"), fontArb14);
                            Paragraph  paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                        }


                        break;
                    case ActivityConstants.ACTIVITY_QUOTATION:

                        Paragraph  paragraph = new Paragraph(" QUOTATION", fontArb14);

                        break;
                }

                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1});



                /////////////////*****  customer label ******/////
//            customer details
                PdfPCell cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingLeft(1);

                cell.setPaddingTop(80);//84
                //230

                //            customer Address  label
                Paragraph paragraph = new Paragraph("                                 " , font12Bold);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setPaddingLeft(1);

                cell.setPaddingTop(80);

                table.addCell(cell);

                document.add(table);
//Create the table which will be 3 Columns wide and make it 100% of the page
                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1,1});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("          "+strBillNumber , font12Bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);


                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                String justifiedDate = strDate.substring(0, 11);
                paragraph = new Paragraph(""+justifiedDate , font12Bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);
                cell.setPaddingRight(40);

                table.addCell(cell);


                document.add(table);


                //Create the table which will be 3 Columns wide and make it 100% of the page
                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(2);
                //230

                //            customer Address  label
                paragraph = new Paragraph("                       :  "+SELECTED_SHOPE.getShopName() , font12Bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(2);

                table.addCell(cell);

                document.add(table);

                //Create the table which will be 3 Columns wide and make it 100% of the page
                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(1);
                //230

                paragraph = new Paragraph("VAT NO        :  "+SELECTED_SHOPE.getVatNumber() , font12Bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(1);

                table.addCell(cell);

                document.add(table);

                //Create the table which will be 2 Columns wide and make it 100% of the page
                table = new PdfPTable(7);
                //table.setWidths(new int[]{3,15, 3,3, 4, 4, 4, 4, 3,4});
                table.setWidths(new int[]{5,28, 6,6, 6, 7, 9 });
                table.setWidthPercentage(100);
                //table.setPaddingTop(40);
                table.setSpacingBefore(75);//60



                for (int j = 0; j < MAX_LINE; j++) {

                    String strSl_No = " ", strP_Code = " ",strP_Name = " ", strP_Arabic = "  ", strQty = " ", strNetPrice = " ",
                            strNetTotal = " ", strAmount = " ", strTotalPrice = " ", str_vatRate = " ", str_vatAmount = " ",
                            str_hsncode = " ", str_sgst = " ", strunit = " ", str_saleprice =" ", strMrp_price =" " ;

                    if (cartList.size() > j) {
                        CartItem cartItem = cartList.get(j);

                        int slNo = i * MAX_LINE + j + 1;
                        strSl_No = String.valueOf(slNo);
                        strP_Name = cartItem.getProductName();
                        strP_Arabic = cartItem.getArabicName();
                        strP_Code=cartItem.getProductCode();



                        if (strP_Arabic == null || TextUtils.isEmpty(strP_Arabic.trim()) || strP_Arabic.equals("null"))
                            strP_Arabic = "  ";

                        strQty = "0/" + String.valueOf(cartItem.getTypeQuantity()); // case and piece

                        //  strQty = ""+String.valueOf(cartItem.getTypeQuantity()); // piece only

                        double netPrice = cartItem.getNetPrice()-cartItem.getProductDiscount();
//                        double mrp_price = cartItem.getMrprate();
                        double mrp_price = cartItem.getNetPrice()-cartItem.getProductDiscount();

                        double sale_price = cartItem.getSalePrice();

                        // For case and piece

                        if (cartItem.getOrderType().equals(PRODUCT_UNIT_CASE)) {
                            netPrice = netPrice * cartItem.getPiecepercart();
                            strQty = cartItem.getTypeQuantity() + "/0";
                        }


                        strNetTotal = getAmount(cartItem.getNetPrice() * cartItem.getPieceQuantity());

                        str_vatRate = String.valueOf(cartItem.getTax() + " %");
//
                        //str_vatAmount = getAmount(cartItem.getTaxValue()* cartItem.getPieceQuantity());
                        str_vatAmount = getAmount(cartItem.getTaxValue());

                        // strTotalPrice = getAmount(cartItem.getSalePrice() * cartItem.getPieceQuantity());
                        strTotalPrice = getAmount(cartItem.getProductTotal()+cartItem.getTaxValue());

                        strunit = cartItem.getUnitselected();


                        str_hsncode = String.valueOf(cartItem.getProduct_hsncode());

                        str_saleprice = getAmount(sale_price);


                        strNetPrice = getAmount(netPrice);
                        strMrp_price = getAmount(mrp_price);


                    }


                    if (strP_Name.length() > 40)
                        strP_Name = getMaximumChar(strP_Name, 40);



                    if (strP_Arabic.length() > 42)
                        strP_Arabic = getMaximumChar(strP_Arabic, 42);


                    String justifiedSlNo = String.format("%-3s", strSl_No);
                    String justifiedCode = String.format("%-5s", strP_Code);

                    String justifiedQuantity = String.format("%-5s", strQty);
                    String justifiedNetPrice = String.format("%-5s", strNetPrice);
                    String justifiedSalePrice = String.format("%-5s", str_saleprice);
                    String justifiedNetTotal = String.format("%-5s", strNetTotal);
                    String justifiedVatRate = String.format("%-5s", str_vatRate);
                    String justifiedVatAmount = String.format("%-5s", str_vatAmount);
                    String justifiedTotal = String.format("%-5s", strTotalPrice);
                    String justifiedhsncode = String.format("%-5s", str_hsncode);
                    String justifiedmrprice = String.format("%-5s", strMrp_price);
                    String justifiedunit = String.format("%-5s", strunit);




//sl number
                    cell = new PdfPCell(new Phrase(justifiedSlNo, font10));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setFixedHeight(3f);

                    table.addCell(cell);




//                    item desc
                    if (!TextUtils.isEmpty(strP_Arabic.trim()))
                        cell = new PdfPCell(new Phrase(strP_Name + "\n" + arabicPro.process(strP_Arabic), fontArb8));
                    else
                        cell = new PdfPCell(new Phrase(strP_Name +"\n"+ Chunk.NEWLINE, fontArb8));
                    cell.setBorder(Rectangle.NO_BORDER);

                    // cell.setPaddingTop(80);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);



                    //qnty
                    cell = new PdfPCell(new Phrase(justifiedQuantity, font10));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);

                    table.addCell(cell);

                    // unit
                    cell = new PdfPCell(new Phrase(justifiedunit, font10));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(2);

                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);

                    //  mrp
                    cell = new PdfPCell(new Phrase(justifiedmrprice, font10));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);

//  vat
                    cell = new PdfPCell(new Phrase(justifiedVatAmount, font10));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);



//total
                    cell = new PdfPCell(new Phrase(justifiedTotal, font10));
                    cell.setBorder(Rectangle.NO_BORDER);
//                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);


                }

                document.add(table);




                table = new PdfPTable(3); // 4
                table.setWidthPercentage(100.0f);
                // table.setWidths(new int[]{5,3, 5,5, 4, 5, 5, 5}); // 5,2,2,1
                table.setWidths(new int[]{15,4,4}); // 5,2,2,1




                //////////////////////******   total details ****//////////////////////



//                cell = new PdfPCell(new Phrase( arabicPro.process("المبلغ الصافى"), fontArb8));
                cell = new PdfPCell(new Phrase("" , font10)); // Net Amount
                cell.setPaddingTop(60);//12 //22 //30 //60
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("" , font10)); // Net Amount
                cell.setPaddingTop(60);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);



                cell = new PdfPCell(new Phrase("" , font10)); // Net Amount
                cell.setPaddingTop(60);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPaddingBottom(4);
                table.addCell(cell);

                document.add(table);



                //haris added on 300921

                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{2,2});


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER );
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingTop(50);

                cell.setPaddingLeft(4);

                // try {


                String stdb_grandtot1 = ""+getAmount(db_grandtot);
                String vatAmnt = ""+getAmount(getTaxTotal());
                if(compNameArab.equals("")&&compName.equals("")){
                    compNameArab = "No Name";
                }
                if(compNameArab.equals("")){
                    compNameArab = compName;
                }
                String qrBarcodeHash = QRBarcodeEncoder.encode(
                        new Seller(compNameArab),
                        new TaxNumber(str_kafeel_VatNo),
                        new InvoiceDate(strDate),
                        new InvoiceTotalAmount(stdb_grandtot1),
                        new InvoiceTaxAmount(vatAmnt)
                );

                MultiFormatWriter writr = new MultiFormatWriter();
                try{
                    BitMatrix matrix = writr.encode(qrBarcodeHash,BarcodeFormat.QR_CODE,350,350);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);


                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image img = Image.getInstance(stream.toByteArray());
                    img.setAbsolutePosition(15f, 100f);
                    img.scalePercent(25f);
                    //img.scalePercent(20f);
                    img.setAlignment(Element.ALIGN_LEFT);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.addElement(img);
                    //cellLogo.setBackgroundColor(colorGreen);

                }catch (Exception e){

                }


                table.addCell(cell);

                PdfPTable innertable = new PdfPTable(1);
                innertable.setWidthPercentage(100.0f);
                innertable.setWidths(new int[]{4});
                //innertable.setTableEvent(new MyTableEvent());

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.RIGHT);
                cell.setUseAscender(true);

                cell.setVerticalAlignment(Element.ALIGN_CENTER);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setUseAscender(true);
                cell.setPaddingTop(50);
                cell.setVerticalAlignment(Element.ALIGN_RIGHT);

                paragraph = new Paragraph(""+netTotal, font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                innertable.addCell(cell);

                cell = new PdfPCell(innertable);
                cell.setBorder(PdfPCell.NO_BORDER);

                cell.setPaddingTop(40);//15

                //second

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.RIGHT);
                cell.setUseAscender(true);

                cell.setVerticalAlignment(Element.ALIGN_RIGHT);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setUseAscender(true);
                cell.setPaddingTop(18);
                cell.setVerticalAlignment(Element.ALIGN_RIGHT);

                paragraph = new Paragraph("  "+total_discount, font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                innertable.addCell(cell);

                cell = new PdfPCell(innertable);
                cell.setBorder(PdfPCell.NO_BORDER);



                //second
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setUseAscender(true);
                cell.setPaddingTop(10);
                cell.setVerticalAlignment(Element.ALIGN_RIGHT);



                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setUseAscender(true);
                cell.setPaddingTop(18);
                cell.setVerticalAlignment(Element.ALIGN_RIGHT);

                paragraph = new Paragraph("  "+st_taxable_amnt, font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                innertable.addCell(cell);

                cell = new PdfPCell(innertable);
                cell.setBorder(PdfPCell.NO_BORDER);

                cell.setPaddingTop(15);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.RIGHT);
                cell.setUseAscender(true);

                cell.setVerticalAlignment(Element.ALIGN_RIGHT);



                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setUseAscender(true);
                cell.setPaddingTop(17);
                cell.setVerticalAlignment(Element.ALIGN_RIGHT);

                paragraph = new Paragraph(""+total_tax, font8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);

                innertable.addCell(cell);

                cell = new PdfPCell(innertable);
                cell.setBorder(PdfPCell.NO_BORDER);

                cell.setPaddingTop(15);

                table.addCell(cell);



                document.add(table);

                //////////////////////////////////////
                table = new PdfPTable(3); // 4
                table.setWidthPercentage(100.0f);
                // table.setWidths(new int[]{5,3, 5,5, 4, 5, 5, 5}); // 5,2,2,1
                table.setWidths(new int[]{15,4,4}); // 5,2,2,1



                cell = new PdfPCell(new Phrase(""+val_in_english , font10)); // Net Amount
                cell.setPaddingTop(15);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("" , font10)); // Net Amount
                cell.setPaddingTop(15);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);


                cell = new PdfPCell(new Phrase("  "+st_grandtot , font10)); // Net Amount
                cell.setPaddingTop(15);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);

                document.add(table);
                //setBackground(document);

            }



            // %%%%%%%%%%%%%%%%%%%%%%%%%%% **************** %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            // setBackground(document);
            document.close();


            printPDF(myFile);  //Print PDF File




        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Log.d(TAG, "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void printPDF(final File file) {


        PrintDocumentAdapter pda = new PrintDocumentAdapter() {

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                InputStream input = null;
                FileOutputStream output = null;

                try {

//                    File file = new File(FILE_PATH);
                    input = new FileInputStream(file);

                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (Exception e) {
                    //Catch exception
                    printLog(TAG, "Exception  printPDF   2  "+e.getMessage());
                } finally {
                    try {
                        assert input != null;
                        input.close();
                        assert output != null;
                        output.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        printLog(TAG, "Exception  printPDF   1 "+e.getMessage());
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {


                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }


                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                callback.onLayoutFinished(pdi, true);
            }
        };


        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String printName = FILE_PATH;
        assert printManager != null;
        printManager.print(printName, pda, null);


    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                printLog(TAG, "Permission is granted");
                return true;
            } else {

                printLog(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            printLog(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            printLog(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            //  printReturnPDF(getPdfModels(cartItems));
        }


    }

    private double roundTwoDecimals(double i) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return Double.valueOf(twoDForm.format(i));
    }
    private double roundTwoDecimalsbytwo(double i) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(i));
    }
}

