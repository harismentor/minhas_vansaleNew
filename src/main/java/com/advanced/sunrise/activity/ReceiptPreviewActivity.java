package com.advanced.minhas.activity;

import android.Manifest;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.print.CompanyDetailsPrintModel;
import com.advanced.minhas.model.print.CustomerDetailsPrintModel;
import com.advanced.minhas.model.print.FooterDetailsPrintModel;
import com.advanced.minhas.model.print.InvoiceDetailsPrintModel;
import com.advanced.minhas.model.print.InvoiceTotalDetailsPrintModel;
import com.advanced.minhas.model.print.PosPrintModel;
import com.advanced.minhas.printerconnect.connecter.P25ConnectionException;
import com.advanced.minhas.printerconnect.connecter.P25Connector;
import com.advanced.minhas.printerconnect.pockdata.PocketPos;
import com.advanced.minhas.printerconnect.printutil.DateUtil;
import com.advanced.minhas.printerconnect.printutil.FontDefine;
import com.advanced.minhas.printerconnect.printutil.Printer;
import com.advanced.minhas.printerconnect.printutil.Sample_Print;
import com.advanced.minhas.session.SessionValue;
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
import com.rey.material.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.RECEIPT_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.CustomConverter.convertNumberToArabicWords;
import static com.advanced.minhas.config.CustomConverter.convertNumberToEnglishWords;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getMaximumChar;
import static com.advanced.minhas.config.Generic.getPrintDate;
import static com.advanced.minhas.config.Generic.stringToDate;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_1;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_1_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_2;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_2_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_CR;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_EMAIL;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_VAT;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_ID;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_NAME;

public class ReceiptPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "ReceiptPreviewActivity";
    int callingActivity = 0;
    String strShopName = "",strShopNameArabic = "",strCustomerVat="",strCustomerCode="",
            strShopLocation = "",strDate="", strReceiptNo="", CURRENCY = "";
    Sample_Print sample;
    private Button btnPrint, btnHome, btnConnect, btnEnable;
    private TextView tvOutstandingBalance,tvCurrentBalance, tvBalancePaid, tvTitle, tvReceiptNo, tvDate, tvShopDetails;
    private ViewGroup connectionView;
    private Shop SELECTED_SHOPE = null;
    private Receipt receipt = null;

    private static String FILE_PATH = Environment.getExternalStorageDirectory() + "/icresp_receipt.pdf";


    private SessionValue sessionValue;

    private AppCompatSpinner spinnerDevice;


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
        setContentView(R.layout.activity_receipt_preview);
        btnPrint = (Button) findViewById(R.id.button_receipt_preview_print);
        btnHome = (Button) findViewById(R.id.button_receipt_preview_home);

        btnConnect = (Button) findViewById(R.id.button_preview_connect);
        btnEnable = (Button) findViewById(R.id.button_preview_connection_enabled);

        tvTitle = (TextView) findViewById(R.id.textView_receipt_preview_title);

        tvShopDetails = (TextView) findViewById(R.id.textView_receipt_preview_shopDetails);
        tvReceiptNo = (TextView) findViewById(R.id.textView_receipt_preview_receiptNo);
        tvDate = (TextView) findViewById(R.id.textView_receipt_preview_date);

        connectionView=(ViewGroup)findViewById(R.id.connectionView_receipt);
        tvOutstandingBalance = (TextView) findViewById(R.id.textView_balance_outstanding);
        tvCurrentBalance = (TextView) findViewById(R.id.textView_currentBalance);
        tvBalancePaid = (TextView) findViewById(R.id.textView_balance_paid);
        spinnerDevice = (AppCompatSpinner) findViewById(R.id.spinner_preview_devices);
        sessionValue = new SessionValue(ReceiptPreviewActivity.this);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);

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


        callingActivity = getIntent().getIntExtra(CALLING_ACTIVITY_KEY, 0);
        switch (callingActivity) {

            case ActivityConstants.ACTIVITY_OUTSTANDING_HISTORY_RECEIPT:


                try {


                    tvTitle.setText("RECEIPT VOUCHER");

                    SELECTED_SHOPE = (Shop) getIntent().getSerializableExtra(SHOP_VALUE_KEY);
                    strShopName = SELECTED_SHOPE.getShopName();

                    if (SELECTED_SHOPE.getShopArabicName() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopArabicName()))
                        strShopNameArabic = SELECTED_SHOPE.getShopArabicName();

                    if (SELECTED_SHOPE.getVatNumber() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getVatNumber()))
                        strCustomerVat = SELECTED_SHOPE.getVatNumber();

                    if (SELECTED_SHOPE.getShopCode() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopCode()))
                        strCustomerCode = SELECTED_SHOPE.getShopCode();


                    strShopLocation = SELECTED_SHOPE.getShopAddress();
                    receipt = (Receipt) getIntent().getSerializableExtra(RECEIPT_KEY);

                    tvOutstandingBalance.setText(String.valueOf("Outstanding Balance\n" + getAmount(receipt.getCurrentBalanceAmount()+receipt.getReceivedAmount()) + " " + CURRENCY));
                    tvBalancePaid.setText(String.valueOf("Paid\n" + getAmount(receipt.getReceivedAmount()) + " " + CURRENCY));
                    tvCurrentBalance.setText(String.valueOf("Balance\n" + getAmount(receipt.getCurrentBalanceAmount()) + " " + CURRENCY));

                    strReceiptNo=receipt.getReceiptNo();



                    connect();

                } catch (NullPointerException e) {
                    Log.d(TAG, "exception   " + e.getMessage());
                }
                break;
            case ActivityConstants.ACTIVITY_OUTSTANDING_RECEIPT:

                try {
                    tvTitle.setText("RECEIPT VOUCHER");


                    SELECTED_SHOPE = (Shop) getIntent().getSerializableExtra(SHOP_VALUE_KEY);
                    strShopName = SELECTED_SHOPE.getShopName();
                    strShopLocation = SELECTED_SHOPE.getShopAddress();
                    receipt = (Receipt) getIntent().getSerializableExtra(RECEIPT_KEY);



                    tvOutstandingBalance.setText(String.valueOf("Outstanding Balance\n" + getAmount(receipt.getCurrentBalanceAmount()+receipt.getReceivedAmount()) + " " + CURRENCY));
                    tvBalancePaid.setText(String.valueOf("Paid\n" + getAmount(receipt.getReceivedAmount()) + " " + CURRENCY));
                    tvCurrentBalance.setText(String.valueOf("Balance\n" + getAmount(receipt.getCurrentBalanceAmount()) + " " + CURRENCY));

                    strReceiptNo=receipt.getReceiptNo();




                    connect();


                } catch (RuntimeException e) {
                    Log.d(TAG, "ReceiptPreviewActivity exception   " + e.getMessage());
                }

                break;
            default:


        }


        try {




        final Date date = stringToDate(receipt.getLogDate());

            strDate=getPrintDate(date);

            tvDate.setText("\n\n"+strDate);


        tvShopDetails.setText(String.valueOf(strShopName + "\n" + strShopLocation));

        if (!TextUtils.isEmpty(strReceiptNo))
        tvReceiptNo.setText(String.valueOf("Receipt "+strReceiptNo));


        } catch (Exception e) {
            Log.d(TAG, "ReceiptPreviewActivity exception   " + e.getMessage());
        }

        btnPrint.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnEnable.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_receipt_preview_home:

                onBackPressed();

                break;
            case R.id.button_receipt_preview_print:


                if (sessionValue.isPOSPrint()) {
                   // printReceipt_POS();
                   // printpos_80();

                    printPos_80mm();
                }
                else{
                    if (isStoragePermissionGranted()) {

                        Log.e("printpdf", "print");
                        printReceipt_PDF();
                    }
                }




                break;
            case R.id.button_preview_connect:
                connect();
                break;

            case R.id.button_preview_connection_enabled:

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                startActivityForResult(intent, 1000);

                break;


        }
    }

    private void printPos_80mm() {
        {
            sample = new Sample_Print(ReceiptPreviewActivity.this);
            ArrayList<CartItem>list = new ArrayList<>();
            for(int i= 0;i<=1;i++){
                CartItem l = new CartItem();
                l.setProductName("hr");
                l.setProductPrice(100);
                l.setTypeQuantity(1);
                l.setPieceQuantity_nw(2);
                l.setProductDiscount(10);
                l.setOrderType("Ctn");
                list.add(l);
            }


            try {


                /***************Company Details******************/

                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.icresp_newlogo);


                CompanyDetailsPrintModel companyDetailsPrintModel = new CompanyDetailsPrintModel();

                companyDetailsPrintModel.setLogo(logo);
                companyDetailsPrintModel.setCompName(sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME));
                companyDetailsPrintModel.setCompanyAddress(sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1));
                companyDetailsPrintModel.setVat_gst_no(sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT));
                companyDetailsPrintModel.setExecName(sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME));
                companyDetailsPrintModel.setExecId(sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID));
                companyDetailsPrintModel.setExecMob(sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE));
                String compny_phone = sessionValue.getCompanyDetails().get(PREF_COMPANY_MOBILE);
                String comp_mail = sessionValue.getCompanyDetails().get(PREF_COMPANY_EMAIL);
                companyDetailsPrintModel.setCompanyEmail(comp_mail);
                companyDetailsPrintModel.setCompanyPhone(compny_phone);


                /******************Customer DETAILS*********************/


                CustomerDetailsPrintModel customerDetailsPrintModel = new CustomerDetailsPrintModel();
                customerDetailsPrintModel.setCustomerName(SELECTED_SHOPE.getShopName());
                customerDetailsPrintModel.setCustomerCode(SELECTED_SHOPE.getShopCode());
                customerDetailsPrintModel.setCustomerVat(SELECTED_SHOPE.getVatNumber());
                customerDetailsPrintModel.setCustomerAddress(SELECTED_SHOPE.getShopAddress());


                /******************Invoice DETAILS*********************/

                long milis = System.currentTimeMillis();
                String date = DateUtil.timeMilisToString(milis, "dd-MMM-yyyy h:mm:ss a");

                InvoiceDetailsPrintModel invoiceDetailsPrintModel = new InvoiceDetailsPrintModel();
                invoiceDetailsPrintModel.setInvoiceNo("123");
                invoiceDetailsPrintModel.setInvoiceDate(date);
                invoiceDetailsPrintModel.setLocationID(String.valueOf("1"));


                /*****TOTAL DETAILS******/

                String total = "", discount = "", afterDiscount = "", roundoff = "", vat = "", grandTotal = "";

                total = getAmount(100 );
                discount = getAmount(10);
                afterDiscount = getAmount(90);
                vat = getAmount(0);
                roundoff = getAmount(0);
                grandTotal = getAmount(90);


                InvoiceTotalDetailsPrintModel invoiceTotalDetailsPrintModel = new InvoiceTotalDetailsPrintModel();

                invoiceTotalDetailsPrintModel.setItemTotal(total);
                invoiceTotalDetailsPrintModel.setDiscountTotal(discount);
                invoiceTotalDetailsPrintModel.setTotalAmount(afterDiscount);
                invoiceTotalDetailsPrintModel.setVat_gstTotal(vat);
                invoiceTotalDetailsPrintModel.setRoundoff(roundoff);
                invoiceTotalDetailsPrintModel.setNetAmount(grandTotal);


                FooterDetailsPrintModel footerDetailsPrintModel = new FooterDetailsPrintModel();
                //String thanksMessage = "Thank you for shopping @" + sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME).toUpperCase(Locale.ROOT);  //name get from session
                String thanksMessage = "!! Thank you !!";  //name get from session

                footerDetailsPrintModel.setThanksMessage(thanksMessage);


                PosPrintModel posPrintModel = new PosPrintModel();
                posPrintModel.setCompanyDetailsPrintModel(companyDetailsPrintModel);
                posPrintModel.setCustomerDetailsPrintModel(customerDetailsPrintModel);
                posPrintModel.setInvoiceDetailsPrintModel(invoiceDetailsPrintModel);
                posPrintModel.setItemDetails(list);
                posPrintModel.setInvoiceTotalDetailsPrintModel(invoiceTotalDetailsPrintModel);
                posPrintModel.setFooterDetailsPrintModel(footerDetailsPrintModel);
                posPrintModel.setSale_type("Cash");



                sample.Print_Invoce_80mm(posPrintModel,"Vat","1000");


            } catch (NullPointerException | InterruptedException e) {
                Toast.makeText(this, "wrong print Request", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void printpos_80(){

            try {


                String bilType = " SALE INVOICE";
                String invoiceLeftContent = " ";

                String nextLine = " \n";



                String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session

                String addressContentStrn = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session

                String registerContentStr = "GST : " + sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);

                String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
                // String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
                String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
                //String routeMob = "Route Mobile No : "+sessionValue.getRegisteredMobile();  //route mob get from session

                String line = "-----------------------------------------";
                String lines = "-----------------------------------------";
                String liness = "------------------------------------------";


                StringBuilder line_content = new StringBuilder();

                line_content.append(lines);

//            StringBuilder nextLine_content = new StringBuilder();
//
//            nextLine_content.append(nextLines);

////////////////////////////////////////////////////////////////////
                StringBuilder company_content = new StringBuilder();

                company_content.append(compName);
//
//
                StringBuilder address_content = new StringBuilder();

                address_content.append(addressContentStrn);
//

//////////////////////////////////////////////////////////// new
                StringBuilder companyaddress_content = new StringBuilder();



                StringBuilder rgstr_content = new StringBuilder();

                rgstr_content.append(nextLine).append(registerContentStr).append(nextLine).append(line).append(nextLine);
//////////////////////////////////////////////////
                StringBuilder contentExecutive = new StringBuilder();

                //contentExecutive.append(nextLine).append(line).append(nextLine).append(execName).append(nextLine).append(nextLine).append(execMob);
                contentExecutive.append(execName).append(nextLine).append(nextLine).append(execMob).append(nextLine);

                long milis = System.currentTimeMillis();

                String date = DateUtil.timeMilisToString(milis, "dd-MMM-yyyy h:mm:ss a") + "\n\n";


                StringBuilder contentTableTitle = new StringBuilder();

                StringBuilder contentItems = new StringBuilder();


                contentTableTitle.append("Item").append("     ").append("Qty").append(" ").append("HSN").append("      ").append("Price").append("   ").append("Total");


                String strBalance = " ", strPaid = " ";


                strBalance = getAmount(receipt.getCurrentBalanceAmount()) + " " + CURRENCY;
                strPaid = getAmount(receipt.getReceivedAmount()) + " " + CURRENCY;


                String paddedBalance = String.format("%-15s", strBalance);

                String paddedPaid = String.format("%15s", strPaid);

                contentItems.append(paddedBalance).append("   ").append(paddedPaid);

                String grand =  getAmount(receipt.getReceivedAmount()) + " " + CURRENCY;

                String paddedGrandTotal = String.format("%15s", grand);

                String total= "Total  : "+paddedGrandTotal;

                String message = "Thank you for shopping at "+compName + "\n";

                String items = contentItems.toString();

                if (items.endsWith("\n")) {
                    items = items.substring(0, items.length() - 1);
                }

                StringBuilder strSubTotal = new StringBuilder();

                String val_in_english = convertNumberToEnglishWords(String.valueOf(paddedPaid));
                // double subTota=roundTwoDecimals(Double.parseDouble(subTotal));
                try {

                            companyaddress_content.append(nextLine).append(nextLine).append("               " + "Receipt Voucher").append(nextLine).append(nextLine).

                                    append("Date   : " + strDate).append(nextLine).append(nextLine).append("Name : " + strShopName).append(nextLine).append(nextLine).append(lines).append("Customer GST : " + SELECTED_SHOPE.getVatNumber()).
                                    append(" \n\n                   Received Cash      : ").append(paddedPaid).append("\n\n                   Balance Amt     : ").append("" + paddedBalance).append("\n\nAmount in words : ").append(val_in_english).
                                    append(" ").append(nextLine).append(nextLine).append(" \n\n                   Received by       ");


                }catch (Exception e){
                    Toast.makeText(this, "wrong 9", Toast.LENGTH_SHORT).show();
                }


                //haris added on 27-05-21
                StringBuilder contentcustomer = new StringBuilder();

                contentcustomer.append(line).append(nextLine).append("Customer No   : " +  SELECTED_SHOPE.getShopCode()).append(nextLine).append(nextLine).append("Customer Name : " + strShopName).append(nextLine).append(nextLine).append("" + invoiceLeftContent).append(nextLine);


                StringBuilder cashtype_content = new StringBuilder();

                cashtype_content.append(bilType);

                StringBuilder contentmsgs = new StringBuilder();




                byte[] titleContentByte = Printer.printfont(companyaddress_content.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                        (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

                byte[] totalByte = new byte[ titleContentByte.length ];

                int offset = 0;


                System.arraycopy(titleContentByte, 0, totalByte, offset, titleContentByte.length);
                offset += titleContentByte.length;

                byte[] sendData = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);

                sendData(sendData);

            }catch (NullPointerException e){
                Toast.makeText(this, "wrong print Request", Toast.LENGTH_SHORT).show();
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
    private void printReceipt_POS() {

        try {

        if (receipt == null) {
            showToast("Some wrong");
            return;
        }


        String bilType = "RECEIPT VOUCHER";


        String nextLine = "\n";


        String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
        String addressContentStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session

        String registerContentStr = "CR  : " + sessionValue.getCompanyDetails().get(PREF_COMPANY_CR) + "\nVAT : " + sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);

        String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
        String execId = "Code      : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
        String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session

            String routeMob = "Route Mobile No : "+sessionValue.getRegisteredMobile();  //route mob get from session

            String line = "------------------------------------------";

        StringBuilder contentExecutive = new StringBuilder();

            contentExecutive.append(line).append(nextLine).append(execName).append(nextLine).append(execId).append(nextLine).append(execMob).append(nextLine).append(routeMob).append(nextLine).append("Receipt : ").append(strReceiptNo);



        String customerStr =line+nextLine+ "Customer No   : " + SELECTED_SHOPE.getShopCode() + "\nCustomer Name : " + strShopName;


        long milis = System.currentTimeMillis();

        String date = DateUtil.timeMilisToString(milis, "dd-MMM-yyyy h:mm:ss a") + "\n\n\n";



//            final Date date = stringToDate(receipt.getLogDate()); //receipt created date
//            date_= PRINT_FORMAT.format(date);

        StringBuilder contentTableTitle = new StringBuilder();

        StringBuilder contentItems = new StringBuilder();


        contentTableTitle.append("Opening Balance").append("          ").append("Paid").append(" ");


        String strBalance = " ", strPaid = " ";


        strBalance = getAmount(receipt.getCurrentBalanceAmount()) + " " + CURRENCY;
        strPaid = getAmount(receipt.getReceivedAmount()) + " " + CURRENCY;


        String paddedBalance = String.format("%-15s", strBalance);

        String paddedPaid = String.format("%15s", strPaid);

        contentItems.append(paddedBalance).append("   ").append(paddedPaid);

        String grand =  getAmount(receipt.getReceivedAmount()) + " " + CURRENCY;

        String paddedGrandTotal = String.format("%15s", grand);

        String total= "Total  : "+paddedGrandTotal;

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


        byte[] tableByte = Printer.printfont(contentTableTitle.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

        byte[] typeByte = Printer.printfont(bilType, FontDefine.FONT_24PX_UNDERLINE, FontDefine.Align_CENTER,
                (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

        byte[] lineByte = Printer.printfont(line, FontDefine.FONT_24PX, FontDefine.Align_CENTER,
                (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


        byte[] cartItemsByte = Printer.printfont(contentItems.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


        byte[] contentSubTotalByte = Printer.printfont(total, FontDefine.FONT_24PX, FontDefine.Align_RIGHT,
                (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


        byte[] messageByte = Printer.printfont(message, FontDefine.FONT_24PX, FontDefine.Align_CENTER, (byte) 0x1A,
                PocketPos.LANGUAGE_ENGLISH);


        byte[] dateByte = Printer.printfont(date, FontDefine.FONT_24PX, FontDefine.Align_CENTER, (byte) 0x1A,
                PocketPos.LANGUAGE_ENGLISH);

        byte[] totalByte = new byte[nextLineByte.length + titleNameByte.length + titleContentByte.length + contentInvoiceRightByte.length + contentExecutiveDetailsByte.length + contentCustomerByte.length + typeByte.length + lineByte.length + tableByte.length + lineByte.length + cartItemsByte.length + lineByte.length + contentSubTotalByte.length + messageByte.length +
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void printReceipt_PDF() {


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

            String compRegisterStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_CR);
            String companyVatStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);

            String execName = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
            String execMob = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
            String routeMob = sessionValue.getRegisteredMobile();  //route mob get from session



            // Create New Blank Document
            Document document = new Document(PageSize.A4);

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

            Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);


            LanguageProcessor arabicPro = new ArabicLigaturizer();
            Font fontArb8 = new Font(bf, 8);
            Font fontArb10 = new Font(bf, 10);
            Font fontArb14 = new Font(bf, 14);





            Paragraph compNameTag = new Paragraph(compName+" "+arabicPro.process(compNameArab), fontArb10);
            compNameTag.setAlignment(Element.ALIGN_CENTER);



            Paragraph compPlaceTag = new Paragraph(address1Str+" "+arabicPro.process(address1ArabStr) + " , "+address2Str, fontArb8);
            compPlaceTag.setAlignment(Element.ALIGN_CENTER);


            Paragraph compMobileTag = new Paragraph("Mob : "+mobileStr+", CR :"+compRegisterStr+" "+arabicPro.process(address2ArabStr), fontArb8);
            compMobileTag.setAlignment(Element.ALIGN_CENTER);



            Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
            compEmailEng.setAlignment(Element.ALIGN_CENTER);

                Paragraph compVatTag = new Paragraph(arabicPro.process(""+"VAT Number "+companyVatStr), fontArb8);
                compVatTag.setAlignment(Element.ALIGN_CENTER);


                PdfPCell cell;  //default cell

                //space cell
                PdfPCell cellSpace = new PdfPCell();
                cellSpace.setPadding(10);
                cellSpace.setBorder(PdfPCell.NO_BORDER);
                cellSpace.setHorizontalAlignment(Element.ALIGN_CENTER);


                //Create the table which will be 2 Columns wide and make it 100% of the page
                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
//            table.setSpacingBefore(10);
                table.setWidths(new int[]{1});


                PdfPCell cellLogo = new PdfPCell();
                cellLogo.setBorder(PdfPCell.LEFT | PdfPCell.TOP);
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setPaddingTop(20);
                cellLogo.setPaddingBottom(5);
                cellLogo.setPaddingRight(5);
                cellLogo.setPaddingLeft(5);


                try {

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icresp_newlogo);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image img = Image.getInstance(stream.toByteArray());
//                img.setAbsolutePosition(25f, 735f);
                    img.scalePercent(35f);
                    img.setAlignment(Element.ALIGN_CENTER);
                    cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                   // cellLogo.addElement(img);


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                ////


                PdfPCell cellTitle = new PdfPCell();
                cellTitle.setBorder( Rectangle.RIGHT | Rectangle.TOP | Rectangle.LEFT );
                cellTitle.setPadding(5);

                Paragraph paragraph = new Paragraph(arabicPro.process("" + " RECEIPT VOUCHER"), fontArb14);



                paragraph.setAlignment(Element.ALIGN_CENTER);
            Paragraph paragraph1 = new Paragraph(arabicPro.process(""), fontArb14);



            paragraph1.setAlignment(Element.ALIGN_CENTER);

                cellTitle.addElement(paragraph);
                cellTitle.addElement(paragraph1);
                cellTitle.addElement(compNameTag);
                cellTitle.addElement(compPlaceTag);
                cellTitle.addElement(compMobileTag);
                cellTitle.addElement(compEmailEng);
//                cellTitle.addElement(compVatTag);

                //table.addCell(cellLogo);
                table.addCell(cellTitle);


//Add the PdfPTable to the table
                document.add(table);

             table = new PdfPTable(3);
            table.setWidthPercentage(100.0f);
//            table.setSpacingBefore(10);
            table.setWidths(new int[]{2,4,4});

            cell = new PdfPCell();
            cell.setPaddingBottom(5);
           // paragraph = new Paragraph("", fontArb8);
            paragraph = new Paragraph("Receipt No :"+strReceiptNo, fontArb8);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            cell.addElement(paragraph);
            cell.setBorder( Rectangle.BOTTOM| Rectangle.LEFT );
            cell.setRowspan(2);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setPaddingBottom(5);
            paragraph = new Paragraph("", fontArb8);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            cell.setBorder( Rectangle.BOTTOM  );
            cell.addElement(paragraph);
            cell.setRowspan(2);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setPaddingBottom(5);
            paragraph = new Paragraph("Date :" +strDate, fontArb8);
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            cell.addElement(paragraph);
            cell.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
            cell.setRowspan(2);
            table.addCell(cell);

            document.add(table);


                //Create the table which will be 3 Columns wide and make it 100% of the page
                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{2});


                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOX);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);




//            customer name label
               // paragraph = new Paragraph("To : ", font8);
            paragraph = new Paragraph(new Phrase("To  " + arabicPro.process("اسم العميل"), fontArb8));
           // paragraph = new Paragraph(arabicPro.process("اسم العميل"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);



                //            customer No label
                paragraph = new Paragraph(""+strShopName, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);


                //            vat number label
            //            vat number
            String justifiedVatNumber = strCustomerVat;
            if (justifiedVatNumber.length() < 44)
                justifiedVatNumber = String.format("%-63s", justifiedVatNumber);
            else
                justifiedVatNumber = getMaximumChar(justifiedVatNumber, 44);

                paragraph = new Paragraph("VAT No : "+justifiedVatNumber, font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);

                //            customer Address  label
            String justifiedCustomerAddress = strShopLocation;
            if (justifiedCustomerAddress.length() < 44)
                justifiedCustomerAddress = String.format("%-63s", justifiedCustomerAddress);
            else
                justifiedCustomerAddress = getMaximumChar(justifiedCustomerAddress, 60);

            paragraph = new Paragraph("Customer Address : "+justifiedCustomerAddress , font8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);


            paragraph = new Paragraph("Mobile No : "+SELECTED_SHOPE.getShopMobile(), font8);
            // paragraph = new Paragraph(arabicPro.process("اسم العميل"), fontArb8);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            cell.addElement(paragraph);

                table.addCell(cell);

                /////////////////*****  customer data ******/////

//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
//
//
////            customer name
//                String nam = strShopName ;
//                nam = getMaximumChar(nam, 60);
//                paragraph = new Paragraph( nam, fontArb8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//
//
//
//                //            customer No
//                String justifiedCustomerNumber = strCustomerCode;
//                if (justifiedCustomerNumber.length() < 44)
//                    justifiedCustomerNumber = String.format("%-63s", justifiedCustomerNumber);
//                else
//                    justifiedCustomerNumber = getMaximumChar(justifiedCustomerNumber, 44);
//
//                paragraph = new Paragraph(justifiedCustomerNumber, font8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//
//
//                //            vat number
//                 justifiedVatNumber = strCustomerVat;
//                if (justifiedVatNumber.length() < 44)
//                    justifiedVatNumber = String.format("%-63s", justifiedVatNumber);
//                else
//                    justifiedVatNumber = getMaximumChar(justifiedVatNumber, 44);
//
//                paragraph = new Paragraph(justifiedVatNumber, font8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//
//
//
//                //            customer Address
//                 justifiedCustomerAddress = strShopLocation;
//                if (justifiedCustomerAddress.length() < 44)
//                    justifiedCustomerAddress = String.format("%-63s", justifiedCustomerAddress);
//                else
//                    justifiedCustomerAddress = getMaximumChar(justifiedCustomerAddress, 60);
//
//                paragraph = new Paragraph(justifiedCustomerAddress , fontArb8);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//
//
//
//
//                table.addCell(cell);


//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.RIGHT | PdfPCell.TOP | PdfPCell.BOTTOM);
//                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
//
//
//                /////////////////*****  arabic label for customer/////
//
//
//
////            customer name label
//                paragraph = new Paragraph(arabicPro.process("اسم العميل"), fontArb8);
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//
//
//
//                //            customer No label
//                paragraph = new Paragraph(arabicPro.process("رقم العميل"), fontArb8);
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//
//
//                //            vat number label
//                paragraph = new Paragraph(arabicPro.process("رقم الضريبة"), fontArb8);
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//
//                //            customer Address label
//                paragraph = new Paragraph(arabicPro.process("عنوان"), fontArb8);
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//
//                //           invoice number label
////                paragraph = new Paragraph(arabicPro.process("رقم الفاتورة"), fontArb8);
////                paragraph.setAlignment(Element.ALIGN_RIGHT);
////                cell.addElement(paragraph);
////
////                //            date label
////                paragraph = new Paragraph(arabicPro.process("التاريخ"), fontArb8);
////                paragraph.setAlignment(Element.ALIGN_RIGHT);
////                cell.addElement(paragraph);
//
//
//
//
//                table.addCell(cell);

                document.add(table);





                //Create the table which will be 2 Columns wide and make it 100% of the page
                table = new PdfPTable(4);
                table.setWidths(new int[]{2,2,3, 2});
                table.setWidthPercentage(100);
//            table.setSpacingBefore(10);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Total Amount", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


            cell = new PdfPCell();
            cell.setPaddingBottom(5);
            paragraph = new Paragraph("Payment Method", fontArb8);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(paragraph);
            cell.setRowspan(2);
            table.addCell(cell);


// item code
                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Paid Amount", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);





                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Balance Amount", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);

                table.addCell(cell);


                    String strTotalAmount = " ", strBalance = " ",strPaid = " ";



                        strTotalAmount= getAmount(receipt.getCurrentBalanceAmount()+receipt.getReceivedAmount()) + " " + CURRENCY;
                        strBalance = getAmount(receipt.getCurrentBalanceAmount()) + " " + CURRENCY;
                        strPaid = getAmount(receipt.getReceivedAmount()) + " " + CURRENCY;



            String val_in_english = convertNumberToEnglishWords(String.valueOf(receipt.getReceivedAmount()));
            String val_in_Arabic = convertNumberToArabicWords(String.valueOf(receipt.getReceivedAmount()));


                    String justifiedTotal = String.format("%-5s", strTotalAmount);
                    String justifiedBalance = String.format("%-5s", strBalance);
                    String justifiedPaid = String.format("%-5s", strPaid);



//total amount
                    cell = new PdfPCell(new Phrase(justifiedTotal, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(2);
                    cell.setPaddingTop(5);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);


//  method
            cell = new PdfPCell(new Phrase(""+receipt.getReceipt_type(), font8));
            cell.setBorder(Rectangle.RIGHT);
            cell.setPadding(2);
            cell.setRowspan(2);
            cell.setPaddingTop(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);



//  paid Amount
                    cell = new PdfPCell(new Phrase(justifiedPaid, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(2);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPaddingTop(5);
                    table.addCell(cell);





// balance amount
                    cell = new PdfPCell(new Phrase(justifiedBalance, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setRowspan(2);
                    cell.setPaddingTop(5);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);




                document.add(table);





                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1,4});


                //            Amount
                cell = new PdfPCell(new Phrase("Amount In Words " , font8));
                cell.setBorder(PdfPCell.BOX);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);



                table.addCell(cell);


                //////////////////////******   total details ****//////////////////////

//                total amount

                cell = new PdfPCell();
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);



            paragraph = new Paragraph(val_in_english + "", font8);
            paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);


//            paragraph = new Paragraph(arabicPro.process(val_in_Arabic+" ريال فقط "), fontArb8);
//            paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);

                table.addCell(cell);

//                cell = new PdfPCell(new Phrase( arabicPro.process(" المبلغ بالكلمات"), fontArb8));
//                cell.setPadding(3);
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorder(PdfPCell.BOX);
//                table.addCell(cell);



                document.add(table);

                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1,1});

                //            Amount
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOX);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingBottom(10);


                paragraph = new Paragraph("Received By :                                                                              " , fontArb8);
                cell.addElement(paragraph);




                table.addCell(cell);



                //            Amount
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.BOX);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setColspan(2);
                cell.setPaddingBottom(10);


            paragraph = new Paragraph("Salesman:                                                                                       " , fontArb8);
            cell.addElement(paragraph);


            String justifiedExecutiveWords = String.format("%-40s", execName);


                paragraph = new Paragraph(justifiedExecutiveWords, font8);
                paragraph.setPaddingTop(20);
                cell.addElement(paragraph);




//                route mobile
            paragraph = new Paragraph("Route Mobile No :                                                                 " , fontArb8);
            paragraph.setPaddingTop(5);
            cell.addElement(paragraph);


            String justifiedRouteMobile=routeMob;
            if (justifiedRouteMobile.length() > 30)
                justifiedRouteMobile = getMaximumChar(justifiedRouteMobile, 30);

            paragraph = new Paragraph("  "+justifiedRouteMobile, fontArb8);
            cell.addElement(paragraph);




            table.addCell(cell);

                document.add(table);






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

        Log.v(TAG, " printPDF   test ");

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
                    Log.v(TAG, "Exception  printPDF   2  "+e.getMessage());
                } finally {
                    try {
                        assert input != null;
                        input.close();
                        assert output != null;
                        output.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.v(TAG, "Exception  printPDF   1 "+e.getMessage());
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
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            printReceipt_PDF();
        }


    }




}
