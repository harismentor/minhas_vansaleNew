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
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.Product;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Size;
import com.advanced.minhas.model.Stock_PdfModel;
import com.advanced.minhas.printerconnect.connecter.P25ConnectionException;
import com.advanced.minhas.printerconnect.connecter.P25Connector;
import com.advanced.minhas.printerconnect.pockdata.PocketPos;
import com.advanced.minhas.printerconnect.printutil.DateUtil;
import com.advanced.minhas.printerconnect.printutil.FontDefine;
import com.advanced.minhas.printerconnect.printutil.Printer;
import com.advanced.minhas.session.SessionValue;
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
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;
import static com.advanced.minhas.config.ConfigValue.SALES_VALUE_KEY;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getMaximumChar;
import static com.advanced.minhas.config.Generic.splitToNChar;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_1;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_1_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_2;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_2_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_CR;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_EMAIL;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME_ARAB;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_PAN_NO;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_VAT;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_ID;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_NAME;

public class StockPreview extends AppCompatActivity implements View.OnClickListener{
    String TAG = "StockPreview";
    String strShopName = "",strShopNameArabic = "",strCustomerVat="",strCustomerNo="", strBillNumber = "",
            strShopLocation = "", strDate = "", justifiedVatNumbernew="", cust_outstanding ="",
            new_Outstanding = "", str_Previous_balance="", CURRENCY = "", st_vehicleno="",st_group="",
            execName="",st_route="",st_drivername="";
    int int_net_totalqnty = 0;
    float fl_total_amount = 0;
    String st_netqnty_total ="";
    ArrayList<Product>arr_products = new ArrayList<>();
    int callingActivity = 0;
    private Shop SELECTED_SHOPE = null;
    private Product SELECTED_SALES = null;
    private RecyclerView recyclerView;
    private Button btnPrint, btnHome, btnConnect, btnEnable;
    private TextView tvTitle, tvNetTotal, tvInvoiceNo, tvDate, tvShopDetails, tv_grandTotal, tv_Vat,tv_discount;
    private AppCompatSpinner spinnerDevice;
    private double paid_amount = 0;

    private ViewGroup connectionView;

    private SessionValue sessionValue;

    private ArrayList<Product> cartItems = null;

    private static String FILE_PATH = Environment.getExternalStorageDirectory() + "/icresp_invoice.pdf";

    //private final int MAX_LINE = 19;
    private final int MAX_LINE = 28;

    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;

    private BluetoothAdapter mBluetoothAdapter;
    private P25Connector mConnector;
    final ArrayList<Size> array_sizefull=new ArrayList<>();
    MyDatabase myDatabase;

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
        setContentView(R.layout.activity_stock_preview);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_preview);

        btnPrint = (Button) findViewById(R.id.button_preview_print);
        btnHome = (Button) findViewById(R.id.button_preview_home);
        btnConnect = (Button) findViewById(R.id.button_preview_connect);

        btnEnable = (Button) findViewById(R.id.button_preview_connection_enabled);

        tvTitle = (TextView) findViewById(R.id.textView_preview_title);
        tvNetTotal = (TextView) findViewById(R.id.textView_preview_netTotal);
        tvShopDetails = (TextView) findViewById(R.id.textView_preview_shopDetails);
        tvInvoiceNo = (TextView) findViewById(R.id.textView_preview_invoiceNo);
        tvDate = (TextView) findViewById(R.id.textView_preview_date);
        tv_discount = (TextView) findViewById(R.id.textView_preview_discount);

        tv_grandTotal = (TextView) findViewById(R.id.textView_grand_total);
        tv_Vat = (TextView) findViewById(R.id.textView_sales_vat);

        spinnerDevice = (AppCompatSpinner) findViewById(R.id.spinner_preview_devices);
        connectionView = (ViewGroup) findViewById(R.id.connectionView);

        sessionValue = new SessionValue(StockPreview.this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        myDatabase = new MyDatabase(this);

        CURRENCY = "" + sessionValue.getControllSettings().get(PREF_CURRENCY);
        //haris added on 09-12-2020
//        String execName = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
//        String route = sessionValue.getr().get(PREF_EXECUTIVE_NAME);  //name get from session
        ////

         st_vehicleno=sessionValue.getvehicleno();
         st_group =   sessionValue.getgroupname();
         st_drivername = sessionValue.getdrivername();
         execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
         st_route =   sessionValue.getroute();


        Log.e("st_vehicleno",""+st_vehicleno);
        Log.e("st_group",""+st_group);
        Log.e("st_route",""+st_route);

        if (sessionValue.isPOSPrint()) {
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

        } else {
            connectionView.setVisibility(View.GONE);
            btnPrint.setEnabled(true);
            btnPrint.setText("Print PDF");

        }

        /******/

        try {
            //SELECTED_SHOPE = (Shop) getIntent().getSerializableExtra(SHOP_VALUE_KEY);

            //SELECTED_SALES = (Product) (getIntent().getSerializableExtra(SALES_VALUE_KEY));
            arr_products = (ArrayList<Product>) getIntent().getSerializableExtra(SALES_VALUE_KEY);

            for (Product p : arr_products) {
                Log.e("getProductName hr", p.getProductName());
            }

            cust_outstanding = "" + SELECTED_SHOPE.getOutStandingBalance();
            Log.e("Outstanding", "" + cust_outstanding);


            strShopName = SELECTED_SHOPE.getShopName();
            strShopLocation = SELECTED_SHOPE.getShopAddress();

            if (SELECTED_SHOPE.getShopArabicName() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopArabicName()))
                strShopNameArabic = SELECTED_SHOPE.getShopArabicName();

            if (SELECTED_SHOPE.getVatNumber() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getVatNumber()))
                strCustomerVat = SELECTED_SHOPE.getVatNumber();

            if (SELECTED_SHOPE.getShopCode() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopCode()))
                strCustomerNo = SELECTED_SHOPE.getShopCode();

            if (SELECTED_SALES == null)
                finish();

            //////// final Date date = stringToDate(SELECTED_SALES.get());

            ///////// strDate = getPrintDate(date);

//            cartItems = SELECTED_SALES.getCartItems();
//            strBillNumber = SELECTED_SALES.getInvoiceCode();
//
//            paid_amount = SELECTED_SALES.getPaid();


        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException exception   " + e.getMessage());
        }

        try {

            String datearr[] = strDate.split("\\s+");
            tvDate.setText(datearr[0] + "\n" + datearr[1]);
        } catch (Exception e) {
            tvDate.setText(strDate);
        }


        String companyVatString = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);

        tvShopDetails.setText(strShopName + "\n" + strShopLocation + "\nVAT :" + companyVatString);
        tvInvoiceNo.setText(strBillNumber);

      //  if (cartItems != null)
            //setRecyclerView();



            btnPrint.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnEnable.setOnClickListener(this);

        if (isStoragePermissionGranted())
            Log.e("reached", "1");
        printInvoice(getPdfModels(arr_products));




    }

    /****/

    @SuppressLint("SetTextI18n")
//    private void setRecyclerView() {
//
//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        //        Item Divider in recyclerView
//        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
//                .showLastDivider()
////                .color(getResources().getColor(R.color.divider))
//                .build());
//
//        recyclerView.setAdapter(new RvPreviewCartAdapter(cartItems));
//
//
//
//        tvNetTotal.setText("");
//        tv_Vat.setText("");
//      //  tv_discount.setText("Discount  : "+SELECTED_SALES.getDiscount_value());
//
//      //  tv_grandTotal.setText(""+ getAmount(SELECTED_SALES.getTotal()) + " " + CURRENCY);
//
//
//
//
//
//        //    printer connect
//        if (sessionValue.isPOSPrint())
//            connect();
//    }

    public double getNetTotal() {

        double netTotal = 0;

       // for (Product cartItem : cartItems) {

                double d = 10;
                netTotal += d;

       // }
        return netTotal;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.button_preview_print:
                Log.e("reached","0");

                if (sessionValue.isPOSPrint())
                    printInvoice(cartItems);
                else{
                    if (isStoragePermissionGranted())
                        Log.e("reached","1");
                        printInvoice(getPdfModels(arr_products));
                }


                break;
            case R.id.button_preview_home:

                onBackPressed();

                break;
            case R.id.button_preview_connect:
                connect();
                break;

            case R.id.button_preview_connection_enabled:

                if (sessionValue.isPOSPrint()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1000);
                }

                break;


        }
    }

    /**
     * *************  PRINTER  ******************/


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
    private void printInvoice(ArrayList<Product> list) {

        try {


            String bilType = "CREDIT INVOICE";
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

            String cust_outstanding = ""+SELECTED_SHOPE.getOutStandingBalance();

            long milis = System.currentTimeMillis();

            String date = DateUtil.timeMilisToString(milis, "dd-MMM-yyyy h:mm:ss a") + "\n\n\n";


            StringBuilder contentTableTitle = new StringBuilder();

            StringBuilder contentItems = new StringBuilder();


            contentTableTitle.append("Item").append("            ").append("Qty").append("    ").append("Price").append("      ").append("Total").append(" ");


            for (Product c : list) {

                String strSl_No = " ", strQty = " ", strPrice = " ", strTotalPrice = " ";


                StringBuilder strP_Name = new StringBuilder("");

               // strQty="0/"+c.getTypeQuantity();

                double netPrice=10;


                strPrice = getAmount(netPrice);
                strTotalPrice ="100";

                String[] nameArr = splitToNChar(c.getProductName(), 13);

//            for (String s:nameArr){
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

           // String grand = getAmount(SELECTED_SALES.getTotal()) + " " + CURRENCY;
            String paid = getAmount(paid_amount) + " " + CURRENCY;
           // String balance = getAmount(SELECTED_SALES.getTotal()-paid_amount) + " " + CURRENCY;

            //haris



            String paddedSub = String.format("%18s", subTotal);

            //String paddedGrandTotal = String.format("%18s", grand);

            String paddedPaid = String.format("%18s", paid);
           // String paddedBalance = String.format("%18s", balance);

            //   new_Outstanding = getAmount(Double.parseDouble(cust_outstanding)); // String.format("%18s", cust_outstanding);

            StringBuilder strSubTotal = new StringBuilder();

            //strSubTotal.append("Total       : ").append(paddedSub).append("\nVat         : ").append(paddedVat).append("\nGrand Total : ").append(paddedGrandTotal);



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
    private List<Stock_PdfModel> getPdfModels(ArrayList<Product> list) {


        final List<Stock_PdfModel> models = new ArrayList<>();


        try {

            List<Product> sublist;


            if (list.size() <= MAX_LINE) {

                Stock_PdfModel pdfModel = new Stock_PdfModel();
                pdfModel.setCartItems(list);
                models.add(pdfModel);

            } else {

                int count = list.size() / MAX_LINE;

                int SUBLIST_START_SIZE = 0, SUBLIST_END_SIZE = MAX_LINE;

                for (int i = 0; i < count; i++) {

                    if (list.size() >= SUBLIST_END_SIZE) {
                        sublist = list.subList(SUBLIST_START_SIZE, SUBLIST_END_SIZE);
                        Stock_PdfModel pdfModel = new Stock_PdfModel();
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
                    Stock_PdfModel pdfModel = new Stock_PdfModel();
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
    private void printInvoice(List<Stock_PdfModel> pdfList) {


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
            //haris added on 06-11-2020
            String companyPan_No = sessionValue.getCompanyDetails().get(PREF_COMPANY_PAN_NO);
            Log.e("companyPan_No",""+companyPan_No);

            String execName = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
            String execMob = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
            String routeMob = sessionValue.getRegisteredMobile();  //route mob get from session

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

            Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);


            LanguageProcessor arabicPro = new ArabicLigaturizer();
            Font fontArb8 = new Font(bf, 8);
            Font fontArb10 = new Font(bf, 10);
            Font fontcompany10 = new Font(bf, 10, Font.BOLD);
            Font fontArb14 = new Font(bf, 14);

            for (int i = 0; i < pdfList.size(); i++) {


                Stock_PdfModel pdfData = pdfList.get(i);

                List<Product> cartList = pdfData.getCartItems();

                String balance = "";



                Paragraph compNameTag = new Paragraph(compName, fontcompany10);
                compNameTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compPlaceTag = new Paragraph(address1Str, fontArb8);
                compPlaceTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compMobileTag = new Paragraph(address2Str+",Mob : "+mobileStr+", CR :"+compRegisterStr+" "+arabicPro.process(address2ArabStr), fontArb8);
                compMobileTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
                compEmailEng.setAlignment(Element.ALIGN_CENTER);

                Paragraph compVatTag = new Paragraph("                                                             VAT Number "+companyVatStr +"                Original For Customer", fontArb8);
                //compVatTag.setAlignment();
                //haris added on 11-12-2020
                Paragraph compcehicledtls = new Paragraph("Vehicle No: "+ st_vehicleno, fontArb8);
                compcehicledtls.setAlignment(Element.ALIGN_CENTER);

                Paragraph compgroup = new Paragraph("Group : "+ st_group, fontArb8);
                compgroup.setAlignment(Element.ALIGN_CENTER);

                Paragraph compexec = new Paragraph( execName, fontArb8);
                compexec.setAlignment(Element.ALIGN_CENTER);




                PdfPCell cell;  //default cell

                //space cell
                PdfPCell cellSpace = new PdfPCell();
                cellSpace.setPadding(10);
                cellSpace.setBorder(PdfPCell.NO_BORDER);
                cellSpace.setHorizontalAlignment(Element.ALIGN_CENTER);

                //Create the table which will be 2 Columns wide and make it 100% of the page
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{1, 4});

                PdfPCell cellLogo = new PdfPCell();
                cellLogo.setBorder(PdfPCell.LEFT | PdfPCell.TOP);
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setPaddingTop(30);
                cellLogo.setPaddingBottom(5);
                cellLogo.setPaddingRight(5);
                cellLogo.setPaddingLeft(5);

                try {

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mabry_logo);

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
                cellTitle.setBorder(Rectangle.BOX);
                cellTitle.setPadding(5);

                Paragraph paragraph = new Paragraph(" STOCK SUMMARY ", fontArb14);



                paragraph.setAlignment(Element.ALIGN_CENTER);

                cellTitle.addElement(paragraph);
                cellTitle.addElement(compNameTag);
                cellTitle.addElement(compPlaceTag);
                cellTitle.addElement(compMobileTag);
                cellTitle.addElement(compEmailEng);
                cellTitle.addElement(compVatTag);
                cellTitle.addElement(compgroup);
                cellTitle.addElement(compexec);
                cellTitle.addElement(compcehicledtls);



                table.addCell(cellLogo);
                table.addCell(cellTitle);


//Add the PdfPTable to the table
                document.add(table);



                //Create the table which will be 2 Columns wide and make it 100% of the page
                table = new PdfPTable(16);
                //table.setWidths(new int[]{3,15, 3,3, 4, 4, 4, 4, 3,4});
                table.setWidths(new int[]{4,10,3,3,3,3,3,3,3,3,3,3,3,3,5,5});
                table.setWidthPercentage(100);
//            table.setSpacingBefore(10);


                cell = new PdfPCell();

                paragraph = new Paragraph("SL No", fontArb8 );
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);



                cell = new PdfPCell();


                paragraph = new Paragraph("Product", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                cell = new PdfPCell();

                paragraph = new Paragraph("Size" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                //cell.setRowspan(1);
                cell.setColspan(12);
                table.addCell(cell);




                cell = new PdfPCell();

                paragraph = new Paragraph("Total Qty " , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


//            temporary
                cell = new PdfPCell();

                paragraph = new Paragraph("Total Amount" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);



                //sub haris 1
                cell = new PdfPCell();
                paragraph = new Paragraph("1", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);


                //sub haris central tax
                cell = new PdfPCell();
                paragraph = new Paragraph("2", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);


                cell = new PdfPCell();

                paragraph = new Paragraph("3" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("4" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("5" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("6" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("7" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("8" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("9" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("10" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("11" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("12" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);



                final ArrayList<CartItem> array_sizelist=new ArrayList<>();

                String st_qnty1 ="",st_qnty2 ="",st_qnty3 ="",st_qnt4 ="",st_qnt5 ="",st_qnt6="",st_qnt7 ="",
                        st_qnt8 ="",st_qnt9 ="",st_qnt10 ="",st_qnt11 ="",st_qnt12 ="",st_mrp = "", st_product_total_mrp = "";

                float fl_mrp=0;
                float fl_product_mrp = 0;



                int total_qty = 0;
                for (int j = 0; j < MAX_LINE; j++) {

                    String strSl_No = " ", strP_Code = " ",strP_Name = " ", strP_Arabic = "  ", strQty = " ", strNetPrice = " ",
                           str_hsncode = " ", str_prodcode = " ", st_sizelist = " ",st_total_qty ="" ;

                    if (cartList.size() > j) {
                        Product cartItem = cartList.get(j);

                        int slNo = i * MAX_LINE + j + 1;
                        strSl_No = String.valueOf(slNo);
                        strP_Name = cartItem.getProductName();
                        str_prodcode = cartItem.getProductCode();
                        strP_Arabic = cartItem.getArabicName();
                        strP_Code=cartItem.getProductCode();
                        fl_mrp = cartItem.getRetailPrice();
                        st_mrp = ""+fl_mrp;

                        array_sizelist.clear();
                        array_sizelist.addAll(myDatabase.getProduct_sizelist(Integer.parseInt(str_prodcode)));
                        st_sizelist = array_sizelist.get(0).getSizelist();
                        array_sizefull.clear();
                        get_sizelist(st_sizelist);
                        if(array_sizefull.size()>0 ){
                            total_qty =0;
                            st_total_qty="";
                            fl_product_mrp =0;
                            st_product_total_mrp = "";
                            for(Size s : array_sizefull ){

                                float fl_qty = Float.parseFloat(s.getQuantity());
                                int int_available_qty = Math.round(fl_qty);
                                total_qty = total_qty + int_available_qty;
                                st_total_qty =""+total_qty;
                                int_net_totalqnty = int_net_totalqnty + int_available_qty;
                                st_netqnty_total = ""+int_net_totalqnty;

                                fl_product_mrp = fl_mrp * total_qty;
                                st_product_total_mrp = ""+fl_product_mrp;
                                fl_total_amount = fl_total_amount +fl_mrp * fl_qty;

                                if(s.getSizeId()==1) {
                                     st_qnty1 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==2) {
                                    st_qnty2 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==3) {
                                    st_qnty3 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==4) {
                                    st_qnt4 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==5) {
                                    st_qnt5 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==6) {
                                    st_qnt6 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==7) {
                                    st_qnt7 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==8) {
                                    st_qnt8 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==9) {
                                    st_qnt9 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==10) {
                                    st_qnt10 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==11) {
                                    st_qnt11 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==12) {
                                    st_qnt12 = ""+int_available_qty;
                                }

                            }

                        }
                        else{
                            st_qnty1 ="";
                            st_qnty2 ="";
                            st_qnty3 ="";
                            st_qnt4 ="";
                            st_qnt5 ="";
                            st_qnt6="";
                            st_qnt7 ="";
                            st_qnt8 ="";
                            st_qnt9 ="";
                            st_qnt10 ="";
                            st_qnt11 ="";
                            st_qnt12 ="";
                            st_total_qty ="";
                            st_mrp ="";
                        }

                    }
                    else{
                        st_qnty1 ="";
                        st_qnty2 ="";
                        st_qnty3 ="";
                        st_qnt4 ="";
                        st_qnt5 ="";
                        st_qnt6="";
                        st_qnt7 ="";
                        st_qnt8 ="";
                        st_qnt9 ="";
                        st_qnt10 ="";
                        st_qnt11 ="";
                        st_qnt12 ="";
                        st_total_qty ="";
                        st_mrp = "";
                        fl_product_mrp =0;
                        st_product_total_mrp = "";
                    }


                    if (strP_Name.length() > 40)
                        strP_Name = getMaximumChar(strP_Name, 40);



                    if (strP_Arabic.length() > 42)
                        strP_Arabic = getMaximumChar(strP_Arabic, 42);


                    String justifiedSlNo = String.format("%-3s", strSl_No);




//sl number
                    cell = new PdfPCell(new Phrase(justifiedSlNo, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);



//                    item Product name
                    if (!TextUtils.isEmpty(strP_Arabic.trim()))
                        cell = new PdfPCell(new Phrase(strP_Name + "\n" + arabicPro.process(strP_Arabic), fontArb8));
                    else
                        cell = new PdfPCell(new Phrase(strP_Name +"\n"+ Chunk.NEWLINE, fontArb8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);



                    //  SIZE 1
                    cell = new PdfPCell(new Phrase(st_qnty1, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 2
                    cell = new PdfPCell(new Phrase(st_qnty2, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 3
                    cell = new PdfPCell(new Phrase(st_qnty3, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 4
                    cell = new PdfPCell(new Phrase(st_qnt4, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 5
                    cell = new PdfPCell(new Phrase(st_qnt5, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 6
                    cell = new PdfPCell(new Phrase(st_qnt6, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 7
                    cell = new PdfPCell(new Phrase(st_qnt7, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 8
                    cell = new PdfPCell(new Phrase(st_qnt8, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 9
                    cell = new PdfPCell(new Phrase(st_qnt9, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 10
                    cell = new PdfPCell(new Phrase(st_qnt10, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 11
                    cell = new PdfPCell(new Phrase(st_qnt11, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 12
                    cell = new PdfPCell(new Phrase(st_qnt12, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);



                    //total qty
                    cell = new PdfPCell(new Phrase(st_total_qty, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);

                    //  total amnt //st_mrp
                    cell = new PdfPCell(new Phrase(st_product_total_mrp, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);


                }

                document.add(table);

                //////Amount haris

                table = new PdfPTable(3);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{50,5,5});


                cell = new PdfPCell();


                paragraph = new Paragraph("                                                                                                                                                           Total \n", font10Bold );
                cell.addElement(paragraph);
                cell.setBorder(PdfPCell.BOX );
                cell.setRowspan(6);
                cell.setPadding(5);
                paragraph.setAlignment(Element.ALIGN_CENTER);

                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);


                cell = new PdfPCell(new Phrase(st_netqnty_total , font10)); // amnt
                cell.setPadding(3);
                cell.setRowspan(4);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""+fl_total_amount , font10)); //hr
                cell.setPadding(3);
                cell.setRowspan(4);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);


                document.add(table);
            }


            // %%%%%%%%%%%%%%%%%%%%%%%%%%% **************** %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



            for (int i = 0; i < pdfList.size(); i++) {


                Stock_PdfModel pdfData = pdfList.get(i);

                List<Product> cartList = pdfData.getCartItems();

                String balance = "";



                Paragraph compNameTag = new Paragraph(compName, fontcompany10);
                compNameTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compPlaceTag = new Paragraph(address1Str, fontArb8);
                compPlaceTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compMobileTag = new Paragraph(address2Str+",Mob : "+mobileStr+", CR :"+compRegisterStr+" "+arabicPro.process(address2ArabStr), fontArb8);
                compMobileTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
                compEmailEng.setAlignment(Element.ALIGN_CENTER);

                Paragraph compVatTag = new Paragraph("                                                             VAT Number "+companyVatStr +"                Original For Accounts", fontArb8);
                //compVatTag.setAlignment();

                //haris added on 11-12-2020
                Paragraph compcehicledtls = new Paragraph("Vehicle No: "+ st_vehicleno, fontArb8);
                compcehicledtls.setAlignment(Element.ALIGN_CENTER);

                Paragraph compgroup = new Paragraph("Group : "+ st_group, fontArb8);
                compgroup.setAlignment(Element.ALIGN_CENTER);

                Paragraph compexec = new Paragraph( execName, fontArb8);
                compexec.setAlignment(Element.ALIGN_CENTER);



                PdfPCell cell;  //default cell

                //space cell
                PdfPCell cellSpace = new PdfPCell();
                cellSpace.setPadding(10);
                cellSpace.setBorder(PdfPCell.NO_BORDER);
                cellSpace.setHorizontalAlignment(Element.ALIGN_CENTER);

                //Create the table which will be 2 Columns wide and make it 100% of the page
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{1, 4});

                PdfPCell cellLogo = new PdfPCell();
                cellLogo.setBorder(PdfPCell.LEFT | PdfPCell.TOP);
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setPaddingTop(30);
                cellLogo.setPaddingBottom(5);
                cellLogo.setPaddingRight(5);
                cellLogo.setPaddingLeft(5);

                try {

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mabry_logo);

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
                cellTitle.setBorder(Rectangle.BOX);
                cellTitle.setPadding(5);

                Paragraph paragraph = new Paragraph(" STOCK SUMMARY ", fontArb14);



                paragraph.setAlignment(Element.ALIGN_CENTER);

                cellTitle.addElement(paragraph);
                cellTitle.addElement(compNameTag);
                cellTitle.addElement(compPlaceTag);
                cellTitle.addElement(compMobileTag);
                cellTitle.addElement(compEmailEng);
                cellTitle.addElement(compVatTag);
                cellTitle.addElement(compgroup);
                cellTitle.addElement(compexec);
                cellTitle.addElement(compcehicledtls);


                table.addCell(cellLogo);
                table.addCell(cellTitle);


//Add the PdfPTable to the table
                document.add(table);

                 int_net_totalqnty = 0;
                 fl_total_amount = 0;
                 st_netqnty_total ="";

                //Create the table which will be 2 Columns wide and make it 100% of the page
                table = new PdfPTable(16);
                //table.setWidths(new int[]{3,15, 3,3, 4, 4, 4, 4, 3,4});
                table.setWidths(new int[]{4,10,3,3,3,3,3,3,3,3,3,3,3,3,5,5});
                table.setWidthPercentage(100);
//            table.setSpacingBefore(10);


                cell = new PdfPCell();

                paragraph = new Paragraph("SL No", fontArb8 );
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);



                cell = new PdfPCell();


                paragraph = new Paragraph("Product", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                cell = new PdfPCell();

                paragraph = new Paragraph("Size" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                //cell.setRowspan(1);
                cell.setColspan(12);
                table.addCell(cell);




                cell = new PdfPCell();

                paragraph = new Paragraph("Total Qty " , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


//            temporary
                cell = new PdfPCell();

                paragraph = new Paragraph("Total Amount" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);



                //sub haris 1
                cell = new PdfPCell();
                paragraph = new Paragraph("1", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);


                //sub haris central tax
                cell = new PdfPCell();
                paragraph = new Paragraph("2", fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);


                cell = new PdfPCell();

                paragraph = new Paragraph("3" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("4" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("5" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("6" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("7" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("8" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("9" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("10" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("11" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);

                cell = new PdfPCell();

                paragraph = new Paragraph("12" , fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(1);
                table.addCell(cell);



                final ArrayList<CartItem> array_sizelist=new ArrayList<>();

                String st_qnty1 ="",st_qnty2 ="",st_qnty3 ="",st_qnt4 ="",st_qnt5 ="",st_qnt6="",st_qnt7 ="",
                        st_qnt8 ="",st_qnt9 ="",st_qnt10 ="",st_qnt11 ="",st_qnt12 ="",st_mrp = "";

                float fl_mrp=0;



                int total_qty = 0;
                for (int j = 0; j < MAX_LINE; j++) {

                    String strSl_No = " ", strP_Code = " ",strP_Name = " ", strP_Arabic = "  ", strQty = " ", strNetPrice = " ",
                            str_hsncode = " ", str_prodcode = " ", st_sizelist = " ",st_total_qty ="", st_product_total_mrp = "";
                    float fl_product_mrp =0;


                    if (cartList.size() > j) {
                        Product cartItem = cartList.get(j);

                        int slNo = i * MAX_LINE + j + 1;
                        strSl_No = String.valueOf(slNo);
                        strP_Name = cartItem.getProductName();
                        str_prodcode = cartItem.getProductCode();
                        strP_Arabic = cartItem.getArabicName();
                        strP_Code=cartItem.getProductCode();
                        fl_mrp = cartItem.getRetailPrice();
                        st_mrp = ""+fl_mrp;

                        array_sizelist.clear();
                        array_sizelist.addAll(myDatabase.getProduct_sizelist(Integer.parseInt(str_prodcode)));
                        st_sizelist = array_sizelist.get(0).getSizelist();
                        array_sizefull.clear();
                        get_sizelist(st_sizelist);
                        if(array_sizefull.size()>0 ){
                            total_qty =0;
                            st_total_qty="";
                            fl_product_mrp =0;
                            st_product_total_mrp = "";
                            for(Size s : array_sizefull ){

                                float fl_qty = Float.parseFloat(s.getQuantity());
                                int int_available_qty = Math.round(fl_qty);
                                total_qty = total_qty + int_available_qty;
                                st_total_qty =""+total_qty;
                                int_net_totalqnty = int_net_totalqnty + int_available_qty;
                                st_netqnty_total = ""+int_net_totalqnty;

                                fl_product_mrp = fl_mrp * total_qty;
                                st_product_total_mrp = ""+fl_product_mrp;
                                fl_total_amount = fl_total_amount +fl_mrp * fl_qty;

                                if(s.getSizeId()==1) {
                                    st_qnty1 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==2) {
                                    st_qnty2 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==3) {
                                    st_qnty3 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==4) {
                                    st_qnt4 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==5) {
                                    st_qnt5 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==6) {
                                    st_qnt6 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==7) {
                                    st_qnt7 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==8) {
                                    st_qnt8 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==9) {
                                    st_qnt9 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==10) {
                                    st_qnt10 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==11) {
                                    st_qnt11 = ""+int_available_qty;
                                }
                                if(s.getSizeId()==12) {
                                    st_qnt12 = ""+int_available_qty;
                                }

                            }

                        }
                        else{
                            st_qnty1 ="";
                            st_qnty2 ="";
                            st_qnty3 ="";
                            st_qnt4 ="";
                            st_qnt5 ="";
                            st_qnt6="";
                            st_qnt7 ="";
                            st_qnt8 ="";
                            st_qnt9 ="";
                            st_qnt10 ="";
                            st_qnt11 ="";
                            st_qnt12 ="";
                            st_total_qty ="";
                            st_mrp ="";
                        }

                    }
                    else{
                        st_qnty1 ="";
                        st_qnty2 ="";
                        st_qnty3 ="";
                        st_qnt4 ="";
                        st_qnt5 ="";
                        st_qnt6="";
                        st_qnt7 ="";
                        st_qnt8 ="";
                        st_qnt9 ="";
                        st_qnt10 ="";
                        st_qnt11 ="";
                        st_qnt12 ="";
                        st_total_qty ="";
                        st_mrp = "";
                        fl_product_mrp =0;
                        st_product_total_mrp = "";
                    }


                    if (strP_Name.length() > 40)
                        strP_Name = getMaximumChar(strP_Name, 40);



                    if (strP_Arabic.length() > 42)
                        strP_Arabic = getMaximumChar(strP_Arabic, 42);


                    String justifiedSlNo = String.format("%-3s", strSl_No);




//sl number
                    cell = new PdfPCell(new Phrase(justifiedSlNo, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);



//                    item Product name
                    if (!TextUtils.isEmpty(strP_Arabic.trim()))
                        cell = new PdfPCell(new Phrase(strP_Name + "\n" + arabicPro.process(strP_Arabic), fontArb8));
                    else
                        cell = new PdfPCell(new Phrase(strP_Name +"\n"+ Chunk.NEWLINE, fontArb8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);



                    //  SIZE 1
                    cell = new PdfPCell(new Phrase(st_qnty1, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 2
                    cell = new PdfPCell(new Phrase(st_qnty2, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 3
                    cell = new PdfPCell(new Phrase(st_qnty3, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 4
                    cell = new PdfPCell(new Phrase(st_qnt4, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 5
                    cell = new PdfPCell(new Phrase(st_qnt5, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 6
                    cell = new PdfPCell(new Phrase(st_qnt6, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 7
                    cell = new PdfPCell(new Phrase(st_qnt7, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 8
                    cell = new PdfPCell(new Phrase(st_qnt8, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 9
                    cell = new PdfPCell(new Phrase(st_qnt9, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 10
                    cell = new PdfPCell(new Phrase(st_qnt10, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 11
                    cell = new PdfPCell(new Phrase(st_qnt11, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    //  SIZE 12
                    cell = new PdfPCell(new Phrase(st_qnt12, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);



                    //total qty
                    cell = new PdfPCell(new Phrase(st_total_qty, font8));
                    cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(3f);
                    table.addCell(cell);

                    //  total amnt //st_mrp
                    cell = new PdfPCell(new Phrase(st_product_total_mrp, font8));
                    cell.setBorder(Rectangle.RIGHT);
                    cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);


                }

                document.add(table);

                //////Amount haris

                table = new PdfPTable(3);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{50,5,5});


                cell = new PdfPCell();

                paragraph = new Paragraph("                                                                                                                                                           Total \n", font10Bold );

                cell.addElement(paragraph);
                cell.setBorder(PdfPCell.BOX );
                cell.setRowspan(6);
                cell.setPadding(5);
                paragraph.setAlignment(Element.ALIGN_CENTER);


                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);


//                cell = new PdfPCell(new Phrase("Total" , font8)); //Total
//                cell.setPadding(3);
//                cell.setBorder(PdfPCell.BOX );
//                cell.setRowspan(4);
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                table.addCell(cell);


                cell = new PdfPCell(new Phrase(st_netqnty_total , font8)); // amnt
                cell.setPadding(3);
                cell.setRowspan(4);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""+fl_total_amount , font8)); //hr
                cell.setPadding(3);
                cell.setRowspan(4);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);


                document.add(table);
            }



            // %%%%%%%%%%%%%%%%%%%%%%%%%%% **************** %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


            document.close();


            printPDF(myFile);  //Print PDF File


        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Log.d(TAG, "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    private void get_sizelist(String st_sizelist) {

            try {

                JSONArray arr_size = new JSONArray(st_sizelist);

                array_sizefull.clear();

                for (int i =0;i<arr_size.length(); i++){

                    Size size = new Size();

                    JSONObject jObj = arr_size.getJSONObject(i);
                    Integer size_st = jObj.getInt("sizeId");
                    String qnty = jObj.getString("quantity");
                    String available_qty = jObj.getString("available_stock");

                    Log.e("size_st",""+size_st);
                    Log.e("qnty",""+qnty);

                    Size s = new Size();
                    s.setQuantity(qnty);
                    s.setSizeId(size_st);
                    s.setTotal_qty("0");
                    s.setSelected_sizes("");
                    array_sizefull.add(s);


                }
            } catch (JSONException e) {
                e.printStackTrace();
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
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback callback, Bundle extras) {


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
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
            printInvoice(getPdfModels(cartItems));
        }


    }

}