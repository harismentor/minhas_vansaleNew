package com.advanced.minhas.activity;

import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.PRODUCT_UNIT_CASE;
import static com.advanced.minhas.config.ConfigValue.SALES_VALUE_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.CustomConverter.convertNumberToEnglishWords;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getMaximumChar;
import static com.advanced.minhas.config.Generic.splitToNChar;
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
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME_LOGIN;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_PAN_NO;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_TAXCARD;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_VAT;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_WEBSITE;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_ID;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_QRCODE_LINK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.os.AsyncTask;
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
import com.advanced.minhas.config.ConfigSales;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.PdfModel;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.Transaction;
import com.advanced.minhas.model.print.CompanyDetailsPrintModel;
import com.advanced.minhas.model.print.CustomerDetailsPrintModel;
import com.advanced.minhas.model.print.FooterDetailsPrintModel;
import com.advanced.minhas.model.print.InvoiceDetailsPrintModel;
import com.advanced.minhas.model.print.InvoiceTotalDetailsPrintModel;
import com.advanced.minhas.model.print.PosPrintModel;
import com.advanced.minhas.printerconnect.pockdata.PocketPos;
import com.advanced.minhas.printerconnect.printutil.DateUtil;
import com.advanced.minhas.printerconnect.printutil.FontDefine;
import com.advanced.minhas.printerconnect.printutil.Printer;
import com.advanced.minhas.printerconnect.printutil.Sample_Print;
import com.advanced.minhas.printerconnect.printutil.Util;
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
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;
import com.itextpdf.text.pdf.languages.LanguageProcessor;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import com.rey.material.widget.Button;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class PreviewActivity_FF extends AppCompatActivity implements View.OnClickListener {

    String TAG = "PreviewActivity_FF";
    String strShopName = "", strShopNameArabic = "", strCustomerVat = "", strCustomerNo = "", strBillNumber = "",
            strShopLocation = "", strDate = "", justifiedVatNumbernew = "", cust_outstanding = "",
            new_Outstanding = "", str_Previous_balance = "", CURRENCY = "", st_sizelist_stock = "", str_paymenttype = "",
            str_kafeel_VatNo = "", str_qrcodelink = "", str_companytaxcard = " ", net = "", net_tot = "", flag = "",st_vatrate ="";
    double after_disc = 0, disc_percent = 0, net_Amount = 0;
    int callingActivity = 0, quotation_flag = 0;
    private Shop SELECTED_SHOPE = null;
    private Sales SELECTED_SALES = null;
    private RecyclerView recyclerView;
    private Button btnPrint, btnHome, btnConnect, btnEnable, btnbackground_print;
    private TextView tvTitle, tvNetTotal, tvInvoiceNo, tvDate, tvShopDetails, tv_grandTotal,
            tv_Vat, tv_discount, tv_afterdscnt, tv_roundoff;
    private double paid_amount = 0;
    float fl_discount = 0;
    int flag_moreline = 0 , tot_qnty = 0;
    double discnt = 0, db_grandtot = 0;
    int int_net_totalqnty = 0;
    float fl_total_amount = 0;
    float fl_totalwithtax = 0;
    String st_netqnty_total = "";
    private ViewGroup connectionView;

    private SessionValue sessionValue;

    private ArrayList<CartItem> cartItems = null;

    private static String FILE_PATH = Environment.getExternalStorageDirectory() + "/icresp_invoice.pdf";
    private static String FILE_PATH1 = Environment.getExternalStorageDirectory() + "/hrct.pdf";
    private static String FILE_PATH2 = Environment.getExternalStorageDirectory() + "/icresp_invoice.pdf";
    //private final int MAX_LINE = 19;
    private final int MAX_LINE = 10;//17
    private final int MAX_LINE1 = 17;
    private final int MAX_LINE_QT = 30;
    private final int MAX_POS_DIGIT = 42;
    MyDatabase myDatabase;

    /*PRINTER SDK*/
    private AppCompatSpinner spinnerDevice;

    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp";
    private static final String fileName = dir + "/BTPrinter";

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int BT_PRINTER = 1536;

    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver connectDevice;

    private ArrayList<BluetoothDevice> remoteDevices;

    private BluetoothDevice btDev;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothPort bluetoothPort;
    private CheckTypesTask BTtask;
    private ExcuteDisconnectBT BTdiscon;
    private Thread btThread;
    Sample_Print sample;

    ArrayAdapter<String> adapter;
    boolean searchflags;
    private boolean disconnectflags;

    private String str_SavedBT = "";

    private void loadSettingFile() {
        String line;
        BufferedReader fReader;
        try {
            fReader = new BufferedReader(new FileReader(fileName));
            while ((line = fReader.readLine()) != null) {
                str_SavedBT = line;
                break;
            }
            fReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSettingFile() {
        try {
            File tempDir = new File(dir);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            BufferedWriter fWriter = new BufferedWriter(new FileWriter(fileName));

            fWriter.write(str_SavedBT);
            fWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearBtDevData() {
        remoteDevices = new ArrayList<BluetoothDevice>();
    }

    private void bluetoothSetup() {
        // Initialize
        clearBtDevData();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {

            showUnsupported();
            // Device does not support Bluetooth
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {

            showDisabled();


        } else {


            try {
                BTtask = new CheckTypesTask();
                BTtask.execute();
            } catch (Exception e) {

            }

        }
    }

    public void ConnectionFailedDevice() {


        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert
                .setTitle("Error")
                .setMessage("The Bluetooth connection is lost.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void Init_BluetoothSet() {
        bluetoothSetup();

        connectDevice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                    if (state == BluetoothAdapter.STATE_ON) {
                        try {
                            BTtask = new CheckTypesTask();
                            BTtask.execute();
                        } catch (Exception e) {

                        }

                    } else if (state == BluetoothAdapter.STATE_OFF) {
                        showDisabled();
                    }
                }
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

                    showConnected();
                    // addPairedDevices();

                    Toast.makeText(getApplicationContext(), "BlueTooth Connect", Toast.LENGTH_SHORT).show();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                    showEnabled();

                    Toast.makeText(getApplicationContext(), "ACTION_DISCOVERY_FINISHED", Toast.LENGTH_SHORT).show();
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    try {
                        if (bluetoothPort.isConnected())
                            bluetoothPort.disconnect();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if ((btThread != null) && (btThread.isAlive())) {
                        btThread.interrupt();
                        btThread = null;
                    }

                    ConnectionFailedDevice();

                    //Toast.makeText(getApplicationContext(), "BlueTooth Disconnect", Toast.LENGTH_SHORT).show();
                }
            }
        };

        discoveryResult = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String key;
                boolean bFlag = true;
                BluetoothDevice btDev;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (remoteDevice != null) {
                    int devNum = remoteDevice.getBluetoothClass().getMajorDeviceClass();

                    if (devNum != BT_PRINTER)
                        return;

                    if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                    } else {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                    }
                    if (bluetoothPort.isValidAddress(remoteDevice.getAddress())) {
                        for (int i = 0; i < remoteDevices.size(); i++) {
                            btDev = remoteDevices.get(i);
                            if (remoteDevice.getAddress().equals(btDev.getAddress())) {
                                bFlag = false;
                                break;
                            }
                        }
                        if (bFlag) {


                            remoteDevices.clear();
                            remoteDevices.add(remoteDevice);
                            registerReceiver(connectDevice, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

                            /*showEnabled();
                            try {
                                btConn(remoteDevices.get(0));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }*/
                            // adapter.add(key);

                        }
                    }
                }
            }
        };

        registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        searchStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(mainView, "???????????? ?????? ?????? ??????", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));

        searchFinish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchflags = true;
            }
        };
        registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void addPairedDevices() {
        BluetoothDevice pairedDevice;
        Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();

        String key = "";

        while (iter.hasNext()) {
            pairedDevice = iter.next();
            if (bluetoothPort.isValidAddress(pairedDevice.getAddress())) {
                int deviceNum = pairedDevice.getBluetoothClass().getMajorDeviceClass();

                if (deviceNum == BT_PRINTER) {
                    remoteDevices.add(pairedDevice);

                    key = pairedDevice.getName() + "\n[" + pairedDevice.getAddress() + "] [Paired]";

                    adapter = new ArrayAdapter<String>(this, R.layout.spinner_background_dark, getArray(remoteDevices));
                    adapter.setDropDownViewResource(R.layout.spinner_list);

                    spinnerDevice.setAdapter(adapter);
                    spinnerDevice.setSelection(0);
                }
            }
        }
        IntentFilter filter = new IntentFilter();

        //filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //filter.addAction(BluetoothDevice.ACTION_FOUND);
        //filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        //registerReceiver(discoveryResult, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        registerReceiver(connectDevice, filter);


    }

    private void SearchingBTDevice() {
        adapter.clear();
        adapter.notifyDataSetChanged();

        clearBtDevData();
        mBluetoothAdapter.startDiscovery();
    }

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(PreviewActivity_FF.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Searching the Printer...");
            asyncDialog.setCancelable(false);
            asyncDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            searchflags = true;
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    });
            asyncDialog.show();
            SearchingBTDevice();
            super.onPreExecute();
        }

        ;

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                while (true) {
                    if (searchflags)
                        break;

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (asyncDialog.isShowing())
                asyncDialog.dismiss();

            searchflags = false;


            addPairedDevices();
            super.onPostExecute(result);
        }

        ;
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        new connBT().execute(btDev);
    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(PreviewActivity_FF.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(PreviewActivity_FF.this);

        String str_temp = "";

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;

            try {
                bluetoothPort.connect(params[0]);
                str_temp = params[0].getAddress();

                retVal = Integer.valueOf(0);
            } catch (IOException e) {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (dialog.isShowing())
                dialog.dismiss();

            if (result.intValue() == 0)    // Connection success.
            {
                RequestHandler rh = new RequestHandler();
                btThread = new Thread(rh);
                btThread.start();

                str_SavedBT = str_temp;
                //  edit_input.setText(str_SavedBT);

                saveSettingFile();

                registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                showConnected();

                /*Intent in = new Intent(Bluetooth_Activity.this, MenuActivity.class);
                in.putExtra("Connection", "BlueTooth");
                startActivity(in);*/
            } else    // Connection failed.
            {
                alert
                        .setTitle("Error")
                        .setMessage("Failed to connect Bluetooth device.")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            super.onPostExecute(result);
        }
    }

    public void DisconnectDevice() {
        try {
            bluetoothPort.disconnect();

            unregisterReceiver(connectDevice);

            if ((btThread != null) && (btThread.isAlive()))
                btThread.interrupt();

            disconnectflags = true;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ExcuteDisconnect() {
        BTdiscon = new ExcuteDisconnectBT();
        BTdiscon.execute();
    }


    private class ExcuteDisconnectBT extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(PreviewActivity_FF.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Disconnecting Device...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
            super.onPreExecute();
        }

        ;

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                DisconnectDevice();

                while (true) {
                    if (disconnectflags)
                        break;

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            disconnectflags = false;

            showDisonnected();

            super.onPostExecute(result);
        }

        ;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview2);

        loadSettingFile();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_preview);

        btnPrint = (Button) findViewById(R.id.button_preview_print);
        btnHome = (Button) findViewById(R.id.button_preview_home);
        btnConnect = (Button) findViewById(R.id.button_preview_connect);
        btnbackground_print = findViewById(R.id.button_background_print);

        btnEnable = (Button) findViewById(R.id.button_preview_connection_enabled);

        tvTitle = (TextView) findViewById(R.id.textView_preview_title);
        tvNetTotal = (TextView) findViewById(R.id.textView_preview_netTotal);
        tvShopDetails = (TextView) findViewById(R.id.textView_preview_shopDetails);
        tvInvoiceNo = (TextView) findViewById(R.id.textView_preview_invoiceNo);
        tvDate = (TextView) findViewById(R.id.textView_preview_date);
        tv_discount = (TextView) findViewById(R.id.textView_preview_discount);
        tv_afterdscnt = (TextView) findViewById(R.id.textView_afterdiscount);
        tv_grandTotal = (TextView) findViewById(R.id.textView_grand_total);
        tv_Vat = (TextView) findViewById(R.id.textView_sales_vat);
        tv_roundoff = findViewById(R.id.textView_sales_roundoff);

        spinnerDevice = (AppCompatSpinner) findViewById(R.id.spinner_preview_devices);
        connectionView = (ViewGroup) findViewById(R.id.connectionView);


        /*PRINTER SDK INTEGRATION STARTING*/


        adapter = new ArrayAdapter<String>(this, R.layout.spinner_list, getArray(remoteDevices));
        adapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerDevice.setAdapter(adapter);
        spinnerDevice.setSelection(0);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        searchflags = false;
        disconnectflags = false;


        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);


        //not using mac address filtering









        /*PRINTER SDK INTEGRATION ENDING*/

        sessionValue = new SessionValue(PreviewActivity_FF.this);

        myDatabase = new MyDatabase(this);


        CURRENCY = "" + sessionValue.getControllSettings().get(PREF_CURRENCY);

        if (sessionValue.isPOSPrint()) {
            connectionView.setVisibility(View.VISIBLE);
            Init_BluetoothSet();
            sample = new Sample_Print(PreviewActivity_FF.this);

            btnPrint.setText("Print POS");

        } else {
            connectionView.setVisibility(View.GONE);
            btnPrint.setEnabled(true);
            btnPrint.setText("Print PDF");

        }

        /******/

        try {
            SELECTED_SHOPE = (Shop) getIntent().getSerializableExtra(SHOP_VALUE_KEY);

            SELECTED_SALES = (Sales) (getIntent().getSerializableExtra(SALES_VALUE_KEY));

            cust_outstanding = "" + SELECTED_SHOPE.getOutStandingBalance();
            // Log.e("Outstanding", ""+cust_outstanding);

            str_kafeel_VatNo = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
            strShopName = SELECTED_SHOPE.getShopName();
            strShopLocation = SELECTED_SHOPE.getShopAddress();
            str_qrcodelink = sessionValue.getCompanyDetails().get(PREF_QRCODE_LINK);
            str_companytaxcard = sessionValue.getCompanyDetails().get(PREF_COMPANY_TAXCARD);
            if (SELECTED_SHOPE.getShopArabicName() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopArabicName()))
                strShopNameArabic = SELECTED_SHOPE.getShopArabicName();

            if (SELECTED_SHOPE.getVatNumber() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getVatNumber()))
                strCustomerVat = SELECTED_SHOPE.getVatNumber();

            if (SELECTED_SHOPE.getShopCode() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopCode()))
                strCustomerNo = SELECTED_SHOPE.getShopCode();

            if (SELECTED_SALES == null)
                finish();
            try {
                final Date date = stringToDate(SELECTED_SALES.getDate());
//            String st_date = ""+date;
//            String output = st_date.substring(
//
//            0, 10);  // Output : 2012/01/20

//            strDate = output;

                Date d = new Date();
                String dateWithoutTime = d.toString().substring(0, 10);

                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                Date todayDate = new Date();
                String thisDate = currentDate.format(date);
                strDate = thisDate;
            } catch (Exception e) {

            }
            if (isStoragePermissionGranted()) {

            }
            Log.e("discnt amnt", "" + SELECTED_SALES.getDiscount());
            Log.e("discnt amnt vl", "" + SELECTED_SALES.getDiscount_value());
            Log.e("discnt amnt %", "" + SELECTED_SALES.getDiscount_percentage());


            cartItems = SELECTED_SALES.getCartItems();
            strBillNumber = SELECTED_SALES.getInvoiceCode();
            str_paymenttype = SELECTED_SALES.getPayment_type();

            paid_amount = SELECTED_SALES.getPaid();
            Log.e("paid_amount", "" + paid_amount);
            Log.e("str_paymenttype", "" + str_paymenttype);

           /* for (CartItem t : cartItems) {
                Log.e("Cartitems", "" + t.get);
            }*/

            callingActivity = getIntent().getIntExtra(CALLING_ACTIVITY_KEY, 0);
            switch (callingActivity) {
                case ActivityConstants.ACTIVITY_SALES:
                    tvTitle.setText(getString(R.string.invoice));

                    if (paid_amount == SELECTED_SALES.getTotal())
                        tvTitle.setText(" INVOICE");
                    else
                        tvTitle.setText(" INVOICE");

                    break;
                case ActivityConstants.ACTIVITY_QUOTATION:
                    quotation_flag = 1;

                    tvTitle.setText(getString(R.string.quotation));

                    break;
                case ActivityConstants.ACTIVITY_SALE_REPORT:
                    tvTitle.setText(getString(R.string.invoice));

                    if (paid_amount == SELECTED_SALES.getTotal())
                        tvTitle.setText(" INVOICE");
                    else
                        tvTitle.setText(" INVOICE");

                    break;
                default:

            }
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException exception   " + e.getMessage());
        }

        try {

            String datearr[] = strDate.split("\\s+");
            tvDate.setText(datearr[0] + "\n" + datearr[1]);
        } catch (Exception e) {
            tvDate.setText(strDate);
        }


//        String companyVatString = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
        String companyVatString = SELECTED_SHOPE.getVatNumber();

        tvShopDetails.setText(strShopName + "\n" + strShopLocation + "\nVAT :" + companyVatString);
        tvInvoiceNo.setText(strBillNumber);

        if (cartItems != null)
            setRecyclerView();

        btnPrint.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnEnable.setOnClickListener(this);
        btnbackground_print.setOnClickListener(this);

    }

    /****/

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

//        String net = String.valueOf("TOTAL     : " + getAmount(getNetTotal()) + " " + CURRENCY);
        // net = String.valueOf("TOTAL     : " + getAmount(getNetTotal() -(SELECTED_SALES.getDiscount_value()-SELECTED_SALES.getDiscount())) + " " + CURRENCY);
        net = String.valueOf("TOTAL     : " + getAmount(getNetTotal() - (SELECTED_SALES.getDiscount_value() - SELECTED_SALES.getDiscount())) + " " + CURRENCY);
        net_Amount = getNetTotal() - (SELECTED_SALES.getDiscount_value() - SELECTED_SALES.getDiscount());
        net_tot = String.valueOf("" + getAmount(getNetTotal() - (SELECTED_SALES.getDiscount_value() - SELECTED_SALES.getDiscount())));
        ///String vat = "VAT    : " + getAmount(getTaxTotal()) + " " + CURRENCY;
        String vat = "VAT    : " + getAmount(SELECTED_SALES.getTaxAmount()) + " " + CURRENCY;

        Log.e("getNetTotal prev", "" + getNetTotal());
        String grandTotal = String.valueOf("GRAND TOTAL : " + getAmount(SELECTED_SALES.getTotal()) + " " + CURRENCY);

        //  tvNetTotal.setText(String.valueOf(net + ",\t\t" + vat + ",\t\t" + grandTotal));
        Log.e("total prev", "" + SELECTED_SALES.getTotal());

        tvNetTotal.setText("" + net);
        tv_Vat.setText("" + vat);

        Log.e("fl_discount else", "" + fl_discount);
        fl_discount = SELECTED_SALES.getDiscount();
        //fl_discount = (float)SELECTED_SALES.getDiscount_value();
        discnt = roundTwoDecimals(fl_discount);
        Log.e("fl_discount else 2", "" + discnt);
        Log.e("getNetTotal ", "" + getNetTotal());
        //discnt = discnt
        tv_discount.setText("DISCOUNT   : " + discnt + " " + CURRENCY);

        after_disc = getNetTotal() - (SELECTED_SALES.getDiscount_value() - SELECTED_SALES.getDiscount()) - discnt;

        tv_afterdscnt.setText("AFTER DISCOUNT : " + getAmount(after_disc) + " " + CURRENCY);
        // }
        tv_roundoff.setText("RoundOff      :  " + getAmount(SELECTED_SALES.getRoundoff_value()));

        String st_roundoffvalue = "" + SELECTED_SALES.getRoundoff_value();

//        if(!st_roundoffvalue.equals("0.0")){
//            Log.e("condtn 1",""+SELECTED_SALES.getRoundoff_value());
//            Log.e("st_roundoffvalue 1",""+st_roundoffvalue);
//
//            tv_grandTotal.setText(""+ getAmount(SELECTED_SALES.getRoundofftot()) + " " + CURRENCY);
//
//        }
//        else {

        // tv_grandTotal.setText("" + getAmount(SELECTED_SALES.getTotal()) + " " + CURRENCY);
        //db_grandtot = d + SELECTED_SALES.getTaxAmount();
        db_grandtot = after_disc + Double.parseDouble(getAmount(SELECTED_SALES.getTaxAmount()));
        // db_grandtot = (float)db_grandtot + SELECTED_SALES.getRoundoff_value();

//        if (SELECTED_SALES.getRoundoff_type().equals("plus")) {
//            db_grandtot = (float)db_grandtot + SELECTED_SALES.getRoundoff_value();
//        }
//        else{
//            db_grandtot = (float)db_grandtot - SELECTED_SALES.getRoundoff_value();
//        }
        db_grandtot = (float) db_grandtot + SELECTED_SALES.getRoundoff_value();
        tv_grandTotal.setText("" + getAmount(db_grandtot) + " " + CURRENCY);
        //}


        Log.e("discount prev", "" + SELECTED_SALES.getDiscount_value());
        Log.e("discount% prev", "" + SELECTED_SALES.getDiscount_percentage());


        //    printer connect
       /* if (sessionValue.isPOSPrint())
            connect();*/
    }

    public double getNetTotal() {

        double netTotal = 0;

        for (CartItem cartItem : cartItems) {
            Log.e("getProductPrice out", "" + cartItem.getProductPrice());
            Log.e("getPieceQuantity_nw out", "" + cartItem.getPieceQuantity_nw());
            Log.e("getNetprice_draft net", "" + cartItem.getNetprice_draft());
            Log.e("getNetPrice net", "" + cartItem.getNetPrice());
            if (cartItem.getProductPrice() != 0.0) {

                Log.e("getProductPrice net", "" + cartItem.getProductPrice());
                Log.e("getPieceQuantity_nw net", "" + cartItem.getPieceQuantity_nw());
                double net = cartItem.getProductPrice();
//                if(cartItem.getTax_type().equals("TAX_INCLUSIVE")){
//                     net = cartItem.getProductTotal()/cartItem.getPieceQuantity_nw();
//                }
                double d = (net * cartItem.getPieceQuantity_nw()) - (cartItem.getProductDiscount() * cartItem.getPieceQuantity_nw());
                netTotal += d;
            }
        }
        return netTotal;
    }

    public double getTaxTotal() {
        double totalTax = 0.0;

        if (!cartItems.isEmpty()) {
            for (CartItem c : cartItems) {
                if (c.getTaxValue() != 0.0) {
                    double f = c.getTaxValue() * c.getPieceQuantity_nw();
                    totalTax += f;
                }
            }
        }
        return totalTax;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.button_preview_print:
                Log.e("placeofsupply", "" + SELECTED_SHOPE.getPlace_ofsupply());

                if (sessionValue.isPOSPrint())
                    try {


                        printInvoice(cartItems);


                    } catch (Exception e) {

                    }
                else {

//                        if(quotation_flag == 1){
//                            Log.e("Quotation","ok");
//                            printInvoice_Quotation(getPdfModels(cartItems));
//
//                        }
//                    else{
                    if (isStoragePermissionGranted()) {
                        Log.e("Quotation", "else" + ActivityConstants.ACTIVITY_QUOTATION);
                        try {
                            final Date date = stringToDate(SELECTED_SALES.getDate());
//            String st_date = ""+date;
//            String output = st_date.substring(0, 10);  // Output : 2012/01/20
//            strDate = output;

                            Date d = new Date();
                            String dateWithoutTime = d.toString().substring(0, 10);

                            SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
                            Date todayDate = new Date();
                            // String thisDate = currentDate.format(date);
                            String thisDate = currentDate.format(new Date());
                            strDate = thisDate;
                        } catch (Exception e) {

                        }
                        printwithbackgroundInvoice(getPdfModels(cartItems));
                    }
                    // printInvoice(getPdfModels(cartItems));
                    //}

                }


                break;

            case R.id.button_background_print:
                if (isStoragePermissionGranted())
                    //   printInvoice(getPdfModels(cartItems));


                    break;


            case R.id.button_preview_home:

                onBackPressed();

                break;
            case R.id.button_preview_connect:


                if (bluetoothPort.isConnected()) {
                    ExcuteDisconnect();
                } else {
                    btDev = remoteDevices.get(spinnerDevice.getSelectedItemPosition());

                    try {
                        btConn(btDev);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


                break;

            case R.id.button_preview_connection_enabled:

                if (sessionValue.isPOSPrint()) {

                    IntentFilter filter = new IntentFilter();

                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(connectDevice, filter);


                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);



                    /*Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1000);*/
                }

                break;


        }
    }

//    private void printInvoice_Quotation(List<PdfModel> pdfList) {
//        Log.e("reached","ok");
//
//
//        File myFile = null;
//
//        PdfWriter writer;
//        try {
//
//            String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
//            String compNameArab = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME_ARAB);  //name get from session
//            String address1Str = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
//            String address1ArabStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1_ARAB);  //address get from session
//            String address2Str = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_2);  //address get from session
//            String address2ArabStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_2_ARAB);  //address get from session
//            String compEmailStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_EMAIL);  //address get from session
//            String mobileStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_MOBILE);  //address get from session
//            String compDateStr = strDate;  //date get from session
//            String compbillStr = strBillNumber;  //date get from session
//
//            String compRegisterStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_CR);
//            String companyVatStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
//            //haris added on 06-11-2020
//            String companyPan_No = sessionValue.getCompanyDetails().get(PREF_COMPANY_PAN_NO);
//            Log.e("companyPan_No",""+companyPan_No);
//
//            String execName = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
//            String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
//            String execMob = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
//            String routeMob = sessionValue.getRegisteredMobile();  //route mob get from session
//
//            // Create New Blank Document
//
//            Document document = new Document(PageSize.A4); //A4
//
//            writer = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH));
//
//            myFile = new File(FILE_PATH);
//
//            document.open();
//
//
////            BaseFont bf = BaseFont.createFont("/assets/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//            BaseFont bf = BaseFont.createFont("/assets/dejavu_sans_condensed.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//
//
//            Font font20 = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
//
//            Font font18 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
//
//            Font font14 = new Font(Font.FontFamily.TIMES_ROMAN, 14);
//
//
//            Font font10Bold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
//            Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
//
//            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
//            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
//
//            Font font8bold = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.BOLD);
//
//            Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
//
//            Font font12Bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
//
//
//            LanguageProcessor arabicPro = new ArabicLigaturizer();
//            Font fontArb8 = new Font(bf, 8);
//            Font fontArb10 = new Font(bf, 10);
//            Font fontcompany10 = new Font(bf, 10, Font.BOLD);
//            Font fontArb14 = new Font(bf, 14);
//
//            for (int i = 0; i < pdfList.size(); i++) {
//
//
//                PdfModel pdfData = pdfList.get(i);
//
//                List<CartItem> cartList = pdfData.getCartItems();
//
//                String netTotal = "", discount = "",  roundOff = "", hsn_code_total ="";
//
//                String grandTotal = "";
//                String paid = "";
//                String balance = "";
//                //haris
//                String central_tax_rate = "";
//                String central_tax_amnt = "";
//                String state_tax_rate = "";
//                String state_tax_amnt = "";
//                String taxable_total = "";
//                String total_tax = "";
//                String total_discount = "";
//                String stdb_grandtot ="";
//                String discount_percntge = "";
//                String st_data = "";
//                String val_in_english = "";
//                String valtaxableamnt_in_english = "";
//                String val_in_Arabic = "";
//                double taxable_amnt =0;
//
//                String st_taxable_amnt = "";
//                String total_discountnw = "";
//                String totalVat = "";
//
//                if (pdfList.size() == i + 1) {
//
//                    netTotal = getAmount(getNetTotal()-(SELECTED_SALES.getDiscount_value()-SELECTED_SALES.getDiscount()));
//
//                    if(SELECTED_SALES.getRoundoff_value()!=0){
//                        grandTotal = ""+SELECTED_SALES.getRoundofftot();
//                    }
//                    else{
//                        grandTotal = getAmount(SELECTED_SALES.getWithTaxTotal());
//                    }
//
//                    stdb_grandtot = ""+getAmount(db_grandtot);
//                    paid = getAmount(paid_amount) + " " + CURRENCY;
//                    discount = getAmount(SELECTED_SALES.getDiscount());
//                    balance = getAmount(SELECTED_SALES.getTotal() - paid_amount) + " " + CURRENCY;
//                    totalVat =  getAmount(getTaxTotal());
////                     st_data = "<?xml version=\"1.0\"?>\n" +
////                            "<invoice>\n" +
////                            "    <SellerName>"+ compName+"</SellerName>\n" +
////                            "    <VatNumber>" + str_kafeel_VatNo  +"</VatNumber>\n" +
////                            "    <DateTime>"+SELECTED_SALES.getDate() +"</DateTime>\n" +
////                            "    <VatTotal>"+ totalVat +"</VatTotal>\n" +
////                            "    <TotalAmount>"+getAmount(SELECTED_SALES.getWithTaxTotal())+"</TotalAmount>\n" +
////                            "</invoice>";
//                    st_data = str_qrcodelink+""+strBillNumber;
//                    taxable_total = getAmount(SELECTED_SALES.getTaxable_total());
//                    total_tax = getAmount(SELECTED_SALES.getTaxAmount());
//                    //total_discount = getAmount(SELECTED_SALES.getDiscount_value());
//                    total_discount = ""+discnt;
//                    total_discountnw = ""+discnt;
//                    discount_percntge = getAmount(SELECTED_SALES.getDiscount_percentage());
//                    hsn_code_total = SELECTED_SALES.getHsn_code();
//
//                    String s = total_discount.replace(",", "");
//
//                    taxable_amnt = Double.parseDouble(""+netTotal)-Double.parseDouble(s);
//
//                    taxable_amnt=roundTwoDecimals(taxable_amnt);
//
//                    st_taxable_amnt = ""+taxable_amnt;
//
//                    //  new_Outstanding = getAmount(SELECTED_SHOPE.getOutStandingBalance());
//
//                    Transaction t = myDatabase.getCustomerTransactionBalance(SELECTED_SHOPE.getShopId());
//
//                    new_Outstanding = getAmount(t.getOutStandingAmount());
//
//                    if (SELECTED_SALES.getPaid() == 0){
//
//                        double prevbal = t.getOutStandingAmount() - SELECTED_SALES.getTotal();
//                        str_Previous_balance = getAmount(prevbal);
//
//                        Log.e("Credit Prev Bal" , ""+str_Previous_balance);
//                        Log.e("New OutStand" , ""+new_Outstanding);
//
//                    }else {
//
//                        str_Previous_balance = new_Outstanding;
//
//                        Log.e("Cash Prev Bal" , ""+str_Previous_balance);
//                        Log.e("New OutStand" , ""+new_Outstanding);
//
//                    }
//
//                    //val_in_english = convertNumberToEnglishWords(String.valueOf(SELECTED_SALES.getTaxable_total()));
//                    val_in_english = convertNumberToEnglishWords(String.valueOf(grandTotal));
//
//                }
//
//                Paragraph compNameTag = new Paragraph(compName, fontcompany10);
//                compNameTag.setAlignment(Element.ALIGN_CENTER);
//
//                Paragraph compPlaceTag = new Paragraph(address1Str, fontArb8);
//                compPlaceTag.setAlignment(Element.ALIGN_CENTER);
//
//                Paragraph compMobileTag = new Paragraph(address2Str+",Mob : "+mobileStr+", CR :"+compRegisterStr+" "+arabicPro.process(address2ArabStr), fontArb8);
//                compMobileTag.setAlignment(Element.ALIGN_CENTER);
//
//                Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
//                compEmailEng.setAlignment(Element.ALIGN_CENTER);
//
//                Paragraph compdate = new Paragraph(compDateStr, font8);
//                compdate.setAlignment(Element.ALIGN_RIGHT);
//
//                Paragraph compVatTag = new Paragraph("                                                             VAT Number "+companyVatStr +"                Original For Customer", fontArb8);
//                //compVatTag.setAlignment();
//
//
//                PdfPTable table = new PdfPTable(1);
//                table.setWidthPercentage(100.0f);
//                table.setWidths(new int[]{1});
//
//
//
//
//
//
//                switch (callingActivity) {
//                    case ActivityConstants.ACTIVITY_SALES:
//
//                        if (paid_amount == SELECTED_SALES.getTotal()) {
//                            //  paragraph = new Paragraph(arabicPro.process("???????????????? ?????????????? - " + " CASH INVOICE"), fontArb14);
//                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
//                        } else {
//                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
//                            // paragraph = new Paragraph(arabicPro.process("?????????? ???????????????? - " + " CREDIT INVOICE"), fontArb14);
//                        }
//
//                        break;
//                    case ActivityConstants.ACTIVITY_QUOTATION:
//
//                        Paragraph  paragraph = new Paragraph(" QUOTATION", fontArb14);
//
//                        break;
//                }
//
//                table = new PdfPTable(1);
//                table.setWidthPercentage(100.0f);
//                table.setWidths(new int[]{1});
//
//
//
//                /////////////////*****  customer label ******/////
////            customer details
//                PdfPCell cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
//                cell.setPaddingLeft(1);
//
//                cell.setPaddingTop(104);
//                //230
//
//                //            customer Address  label
//                Paragraph paragraph = new Paragraph("                                 "+str_paymenttype , font12Bold);
//                paragraph.setAlignment(Element.ALIGN_CENTER);
//                cell.addElement(paragraph);
//                cell.setPaddingLeft(1);
//
//                cell.setPaddingTop(104);
//
//                table.addCell(cell);
//
//                document.add(table);
////Create the table which will be 3 Columns wide and make it 100% of the page
//                table = new PdfPTable(2);
//                table.setWidthPercentage(100.0f);
//                table.setWidths(new int[]{1,1});
//
//
//
//                /////////////////*****  customer label ******/////
////            customer details
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
//                cell.setPaddingTop(0);
//                //230
//
//                //            customer Address  label
//                paragraph = new Paragraph("          "+strBillNumber , font12Bold);
//                paragraph.setAlignment(Element.ALIGN_LEFT);
//                cell.addElement(paragraph);
//                cell.setPaddingTop(0);
//
//
//                table.addCell(cell);
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
//                cell.setPaddingTop(0);
//                //230
//
//                //            customer Address  label
//                paragraph = new Paragraph(""+strDate , font12Bold);
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//                cell.setPaddingTop(0);
//                cell.setPaddingRight(40);
//
//                table.addCell(cell);
//
//
//                document.add(table);
//
//
//                //Create the table which will be 3 Columns wide and make it 100% of the page
//                table = new PdfPTable(1);
//                table.setWidthPercentage(100.0f);
//                table.setWidths(new int[]{1});
//
//
//
//                /////////////////*****  customer label ******/////
////            customer details
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
//
//                cell.setPaddingTop(2);
//                //230
//
//                //            customer Address  label
//                paragraph = new Paragraph("                       :  "+SELECTED_SHOPE.getShopName() , font12Bold);
//                paragraph.setAlignment(Element.ALIGN_LEFT);
//                cell.addElement(paragraph);
//                cell.setPaddingBottom(1);
//                cell.setPaddingTop(2);
//
//                table.addCell(cell);
//
//                document.add(table);
//
//                //Create the table which will be 3 Columns wide and make it 100% of the page
//                table = new PdfPTable(1);
//                table.setWidthPercentage(100.0f);
//                table.setWidths(new int[]{1});
//
//
//
//                /////////////////*****  customer label ******/////
////            customer details
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
//
//                cell.setPaddingTop(1);
//                //230
//
//                paragraph = new Paragraph("VAT NO        :  "+SELECTED_SHOPE.getVatNumber() , font12Bold);
//                paragraph.setAlignment(Element.ALIGN_LEFT);
//                cell.addElement(paragraph);
//                cell.setPaddingBottom(1);
//                cell.setPaddingTop(1);
//
//                table.addCell(cell);
//
//                document.add(table);
//
//                //Create the table which will be 2 Columns wide and make it 100% of the page
//                table = new PdfPTable(8);
//                //table.setWidths(new int[]{3,15, 3,3, 4, 4, 4, 4, 3,4});
//                table.setWidths(new int[]{5,28, 5,5, 6, 7, 9,6 });
//                table.setWidthPercentage(100);
//                //table.setPaddingTop(40);
//                table.setSpacingBefore(60);
//
//
//
//                for (int j = 0; j < MAX_LINE; j++) {
//
//                    String strSl_No = " ", strP_Code = " ",strP_Name = " ", strP_Arabic = "  ", strQty = " ", strNetPrice = " ",
//                            strNetTotal = " ", strAmount = " ", strTotalPrice = " ", str_vatRate = " ", str_vatAmount = " ",
//                            str_hsncode = " ", str_sgst = " ", strunit = " ", str_saleprice =" ", strMrp_price =" " ;
//
//                    if (cartList.size() > j) {
//                        CartItem cartItem = cartList.get(j);
//
//                        int slNo = i * MAX_LINE + j + 1;
//                        strSl_No = String.valueOf(slNo);
//                        strP_Name = cartItem.getProductName();
//                        strP_Arabic = cartItem.getArabicName();
//                        strP_Code=cartItem.getProductCode();
//
//
//
//                        if (strP_Arabic == null || TextUtils.isEmpty(strP_Arabic.trim()) || strP_Arabic.equals("null"))
//                            strP_Arabic = "  ";
//
//                        strQty = "0/" + String.valueOf(cartItem.getTypeQuantity()); // case and piece
//
//                        //  strQty = ""+String.valueOf(cartItem.getTypeQuantity()); // piece only
//
//                        double netPrice = cartItem.getNetPrice()-cartItem.getProductDiscount();
////                        double mrp_price = cartItem.getMrprate();
//                        double mrp_price = cartItem.getNetPrice()-cartItem.getProductDiscount();
//
//                        double sale_price = cartItem.getSalePrice();
//
//                        // For case and piece
//
//                        if (cartItem.getOrderType().equals(PRODUCT_UNIT_CASE)) {
//                            netPrice = netPrice * cartItem.getPiecepercart();
//                            strQty = cartItem.getTypeQuantity() + "/0";
//                        }
//
//
//                        strNetTotal = getAmount(cartItem.getNetPrice() * cartItem.getPieceQuantity());
//
//                        str_vatRate = String.valueOf(cartItem.getTax() + " %");
////
//                        //str_vatAmount = getAmount(cartItem.getTaxValue()* cartItem.getPieceQuantity());
//                        str_vatAmount = getAmount(cartItem.getTaxValue());
//
//                        // strTotalPrice = getAmount(cartItem.getSalePrice() * cartItem.getPieceQuantity());
//                        strTotalPrice = getAmount(cartItem.getProductTotal()+cartItem.getTaxValue());
//
//                        strunit = cartItem.getUnitselected();
//
//
//                        str_hsncode = String.valueOf(cartItem.getProduct_hsncode());
//
//                        str_saleprice = getAmount(sale_price);
//
//
//                        strNetPrice = getAmount(netPrice);
//                        strMrp_price = getAmount(mrp_price);
//
//
//                    }
//
//
//
////                    if(!strP_Name.equals("")) {
////                        if (strP_Name.length() > 40) {
////                            strP_Name = getMaximumChar(strP_Name, 40);
////                        }
////                    }
////                    if(!strP_Name.equals("")) {
////
////                        if (strP_Arabic.length() > 42) {
////                            strP_Arabic = getMaximumChar(strP_Arabic, 42);
////                        }
////                    }
//
//
//                    String justifiedSlNo = String.format("%-3s", strSl_No);
//                    String justifiedCode = String.format("%-5s", strP_Code);
//
//                    String justifiedQuantity = String.format("%-5s", strQty);
//                    String justifiedNetPrice = String.format("%-5s", strNetPrice);
//                    String justifiedSalePrice = String.format("%-5s", str_saleprice);
//                    String justifiedNetTotal = String.format("%-5s", strNetTotal);
//                    String justifiedVatRate = String.format("%-5s", str_vatRate);
//                    String justifiedVatAmount = String.format("%-5s", str_vatAmount);
//                    String justifiedTotal = String.format("%-5s", strTotalPrice);
//                    String justifiedhsncode = String.format("%-5s", str_hsncode);
//                    String justifiedmrprice = String.format("%-5s", strMrp_price);
//                    String justifiedunit = String.format("%-5s", strunit);
//
//
//
//
////sl number
//                    cell = new PdfPCell(new Phrase(justifiedSlNo, font8));
//                    cell.setBorder(Rectangle.NO_BORDER);
//                    cell.setPadding(2);
//                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    cell.setFixedHeight(3f);
//
//                    table.addCell(cell);
//
//
//
//
////                    item desc
//                    if (!TextUtils.isEmpty(strP_Arabic.trim()))
//                        cell = new PdfPCell(new Phrase(strP_Name + "\n" + arabicPro.process(strP_Arabic), fontArb8));
//                    else
//                        cell = new PdfPCell(new Phrase(strP_Name +"\n"+ Chunk.NEWLINE, fontArb8));
//                    cell.setBorder(Rectangle.NO_BORDER);
//
//                    // cell.setPaddingTop(80);
//                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                    table.addCell(cell);
//
//
//
//                    //qnty
//                    cell = new PdfPCell(new Phrase(justifiedQuantity, font8));
//                    cell.setBorder(Rectangle.NO_BORDER);
//                    cell.setPadding(2);
//                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    cell.setFixedHeight(3f);
//
//                    table.addCell(cell);
//
//                    // unit
//                    cell = new PdfPCell(new Phrase(justifiedunit, font8));
//                    cell.setBorder(Rectangle.NO_BORDER);
//                    cell.setPadding(2);
//
//                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    cell.setFixedHeight(3f);
//                    table.addCell(cell);
//
//                    //  mrp
//                    cell = new PdfPCell(new Phrase(justifiedmrprice, font8));
//                    cell.setBorder(Rectangle.NO_BORDER);
//                    cell.setPadding(2);
//                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                    table.addCell(cell);
//
////  vat
//                    cell = new PdfPCell(new Phrase(justifiedVatAmount, font8));
//                    cell.setBorder(Rectangle.NO_BORDER);
//                    cell.setPadding(2);
//                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    table.addCell(cell);
//
//
////total
//                    cell = new PdfPCell(new Phrase(justifiedTotal, font8));
//                    cell.setBorder(Rectangle.NO_BORDER);
////                    cell.setPadding(2);
//                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    table.addCell(cell);
//
//
//                }
//
//                document.add(table);
//
//
//
//
//                table = new PdfPTable(3); // 4
//                table.setWidthPercentage(100.0f);
//                // table.setWidths(new int[]{5,3, 5,5, 4, 5, 5, 5}); // 5,2,2,1
//                table.setWidths(new int[]{15,4,4}); // 5,2,2,1
//
//
//                //////////////////////******   total details ****//////////////////////
//
//
//
////                cell = new PdfPCell(new Phrase( arabicPro.process("???????????? ????????????"), fontArb8));
//                cell = new PdfPCell(new Phrase(""+val_in_english , font8)); // Net Amount
//                cell.setPaddingTop(10);
//                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                cell.setBorder(PdfPCell.NO_BORDER);
//                table.addCell(cell);
//
//                cell = new PdfPCell(new Phrase("" , font8)); // Net Amount
//                cell.setPaddingTop(10);
//                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                cell.setBorder(PdfPCell.NO_BORDER);
//                table.addCell(cell);
//
//
//
//                cell = new PdfPCell(new Phrase(""+netTotal , font8)); // Net Amount
//                cell.setPaddingTop(10);
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setPaddingBottom(4);
//                table.addCell(cell);
//
//                document.add(cell);
//
//                //haris added on 300921
//
//                table = new PdfPTable(2);
//                table.setWidthPercentage(100.0f);
//                table.setWidths(new int[]{2,2});
//
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER );
//                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                cell.setPaddingTop(15);
//
//                cell.setPaddingLeft(4);
//
//
//                // if(str_kafeel_showlogo.equals("YES")) {
//
//                // try {
//
//                MultiFormatWriter writr = new MultiFormatWriter();
//                try{
//                    BitMatrix matrix = writr.encode(st_data,BarcodeFormat.QR_CODE,350,350);
//                    BarcodeEncoder encoder = new BarcodeEncoder();
//                    Bitmap bitmap = encoder.createBitmap(matrix);
//
//                    // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.kanzologo);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    Image img = Image.getInstance(stream.toByteArray());
//                    img.setAbsolutePosition(15f, 100f);
//                    img.scalePercent(25f);
//                    //img.scalePercent(20f);
//                    img.setAlignment(Element.ALIGN_LEFT);
//                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    cell.addElement(img);
//                    //cellLogo.setBackgroundColor(colorGreen);
//
//                }catch (Exception e){
//
//                }
//
//
//                table.addCell(cell);
//
//                PdfPTable innertable = new PdfPTable(1);
//                innertable.setWidthPercentage(100.0f);
//                innertable.setWidths(new int[]{4});
//                //innertable.setTableEvent(new MyTableEvent());
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.RIGHT);
//                cell.setUseAscender(true);
//
//                cell.setVerticalAlignment(Element.ALIGN_CENTER);
//
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setUseAscender(true);
//
//                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
//
//                paragraph = new Paragraph(""+netTotal, font8);
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//
//                innertable.addCell(cell);
//
//                cell = new PdfPCell(innertable);
//                cell.setBorder(PdfPCell.NO_BORDER);
//
//                cell.setPaddingTop(15);
//
//                //second
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.RIGHT);
//                cell.setUseAscender(true);
//
//                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
//
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setUseAscender(true);
//                cell.setPaddingTop(18);
//                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
//
//                paragraph = new Paragraph("  "+total_discount, font8);//total_discount
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//
//                innertable.addCell(cell);
//
//                cell = new PdfPCell(innertable);
//                cell.setBorder(PdfPCell.NO_BORDER);
//
//
//
//                //second
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setUseAscender(true);
//                cell.setPaddingTop(10);
//                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
//
//
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setUseAscender(true);
//                cell.setPaddingTop(18);
//                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
//
//                paragraph = new Paragraph(" "+st_taxable_amnt, font8);
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//
//                innertable.addCell(cell);
//
//                cell = new PdfPCell(innertable);
//                cell.setBorder(PdfPCell.NO_BORDER);
//
//                cell.setPaddingTop(15);
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.RIGHT);
//                cell.setUseAscender(true);
//
//                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
//
//
//
//                cell = new PdfPCell();
//                cell.setBorder(PdfPCell.NO_BORDER);
//                cell.setUseAscender(true);
//                cell.setPaddingTop(17);
//                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
//
//                paragraph = new Paragraph(""+total_tax, font8);
//                paragraph.setAlignment(Element.ALIGN_RIGHT);
//                cell.addElement(paragraph);
//
//                innertable.addCell(cell);
//
//                cell = new PdfPCell(innertable);
//                cell.setBorder(PdfPCell.NO_BORDER);
//
//                cell.setPaddingTop(15);
//
//                table.addCell(cell);
//
//
//
//                document.add(table);
//
//                /////////////////////////
//
//
//                table = new PdfPTable(3); // 4
//                table.setWidthPercentage(100.0f);
//                // table.setWidths(new int[]{5,3, 5,5, 4, 5, 5, 5}); // 5,2,2,1
//                table.setWidths(new int[]{15,4,4}); // 5,2,2,1
//
//
//
//                cell = new PdfPCell(new Phrase(""+val_in_english , font8)); // Net Amount
//                cell.setPaddingTop(12);
//                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                cell.setBorder(PdfPCell.NO_BORDER);
//                table.addCell(cell);
//
//                cell = new PdfPCell(new Phrase("" , font8)); // Net Amount
//                cell.setPaddingTop(15);
//                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                cell.setBorder(PdfPCell.NO_BORDER);
//                table.addCell(cell);
//
//
//                cell = new PdfPCell(new Phrase(""+stdb_grandtot , font8)); // Net Amount
//                cell.setPaddingTop(15);
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorder(PdfPCell.NO_BORDER);
//                table.addCell(cell);
//
//
//
//
//                document.add(table);
//                setBackground(document);
//
//            }
//
//
//
//
//            // %%%%%%%%%%%%%%%%%%%%%%%%%%% **************** %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//
//            // setBackground(document);
//            document.close();
//
//
//            printPDF(myFile);  //Print PDF File
//
//
//
//
//        } catch (DocumentException | IOException e) {
//            e.printStackTrace();
//            Log.d(TAG, "exception  " + e.getMessage());
//            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//        }
//
//
//    }


    /**
     * *************  PRINTER
     ******************/


    @Override
    public void onPause() {
        if (sessionValue.isPOSPrint()) {


        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (sessionValue.isPOSPrint()) {
            try {

                if (bluetoothPort.isConnected()) {
                    bluetoothPort.disconnect();
                    unregisterReceiver(connectDevice);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if ((btThread != null) && (btThread.isAlive())) {
                btThread.interrupt();
                btThread = null;
            }

            unregisterReceiver(searchFinish);
            unregisterReceiver(searchStart);
            unregisterReceiver(discoveryResult);
        }

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


    private void showDisabled() {
        showToast("Bluetooth disabled");

        btnEnable.setVisibility(View.VISIBLE);
        btnConnect.setVisibility(View.GONE);
        spinnerDevice.setVisibility(View.GONE);
    }

    private void showEnabled() {
        //showToast("Bluetooth enabled");

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


    /****
     * POS Print
     * */
    private void printInvoice(ArrayList<CartItem> list) {

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
            invoiceDetailsPrintModel.setInvoiceNo(SELECTED_SALES.getInvoiceCode());
            invoiceDetailsPrintModel.setInvoiceDate(date);
            invoiceDetailsPrintModel.setLocationID(String.valueOf(SELECTED_SALES.getLocId()));


            /*****TOTAL DETAILS******/

            String total = "", discount = "", afterDiscount = "", roundoff = "", vat = "", grandTotal = "";

            total = getAmount(getNetTotal() - (SELECTED_SALES.getDiscount_value() - SELECTED_SALES.getDiscount()));
            discount = getAmount(discnt);
            afterDiscount = getAmount(after_disc);
            vat = getAmount(SELECTED_SALES.getTaxAmount());
            roundoff = getAmount(SELECTED_SALES.getRoundoff_value());
            grandTotal = getAmount(db_grandtot);


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


          //  sample.Print_Invoce_80mm(posPrintModel);
            sample.Print_Invoce_58mm(posPrintModel);


        } catch (NullPointerException | InterruptedException e) {
            Toast.makeText(this, "wrong print Request", Toast.LENGTH_SHORT).show();
        }
    }


    private void printInvoicev1(ArrayList<CartItem> list) {

        try {
            String nextLine = "\n";
            String next2Line = "\n\n";

            String line = "-----------------------------------------";
            String lines = "-----------------------------------------";
            String liness = "------------------------------------------";


            /***************Company Details******************/

            String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
            String companyAddress = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
            String thanksMessage = "Thank you for shopping @" + compName.toUpperCase(Locale.ROOT);  //name get from session
            String vat_gst_no = ConfigSales.IS_TAX_ENABLED ? "VAT : " : "GST  :" + sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
            String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
            // String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session


            /******************INVOICE DETAILS*********************/


            String bilType = Util.center("SALE INVOICE", MAX_POS_DIGIT);


            String customerName = "Customer Name : " + SELECTED_SHOPE.getShopName();
            String customerCode = "Customer Code :" + SELECTED_SHOPE.getShopCode();
            String customerVat = "Customer Vat :" + SELECTED_SHOPE.getVatNumber();
            String customerAddress = "Customer Address :" + SELECTED_SHOPE.getShopAddress();

            strBillNumber = SELECTED_SALES.getInvoiceCode();

            String invoiceNo = "Invc No : " + SELECTED_SALES.getInvoiceCode();
            long milis = System.currentTimeMillis();
            String date = "Date : " + DateUtil.timeMilisToString(milis, "dd-MMM-yyyy h:mm:ss a");

            String invoiceLeftContent = " ";


            String companyDetails = nextLine
                    + Util.center(compName, MAX_POS_DIGIT)
                    + nextLine
                    + Util.center(companyAddress, MAX_POS_DIGIT)
                    + next2Line
                    + vat_gst_no
                    + next2Line
                    + execName
                    + next2Line
                    + execMob
                    + next2Line;

            String cutomerDetails = customerName
                    + next2Line
                    + customerCode
                    + next2Line
                    + customerVat
                    + next2Line
                    + customerAddress
                    + next2Line
                    + lines;

            String invoiceDetails = nextLine
                    + invoiceNo
                    + nextLine
                    + date;

            String itemHead = lines
                    + nextLine
                    + Util.leftJustify("Item", 15)
                    + Util.center("Qty", 4)
                    + Util.center("Price", 8)
                    + Util.rightJustify("Total Price", 15)
                    + nextLine
                    + lines;


            /*****Item List********/

            String itemDetails = "";

            for (CartItem c : list) {


                String itemQty = "", itemPrice = "", itemTotalPrice = "";

                StringBuilder itemName = new StringBuilder("");

                double netPrice = c.getProductPrice();


                String[] nameArr = splitToNChar(c.getProductName(), 15);

                for (int i = 0; i < nameArr.length; i++) {

                    String paddedName = String.format("%-12s", nameArr[i]);
                    //
                    itemName.append(paddedName);
                    if (i != nameArr.length - 1)
                        itemName.append("\n");

                    if (paddedName.length() < 15) {
                        for (i = 0; i < 15 - paddedName.length(); i++) {

                            itemName.append(" ");
                        }
                    }

                }
                itemQty = "" + c.getTypeQuantity();
                itemPrice = getAmount(netPrice);

                if (c.getOrderType().equals(PRODUCT_UNIT_CASE)) {
                    netPrice = netPrice * c.getPiecepercart();
                    itemQty = c.getTypeQuantity() + "/0";
                }
                itemTotalPrice = getAmount(c.getProductPrice() * c.getTypeQuantity());


                String itemDetail = nextLine
                        + Util.leftJustify(itemName.toString(), 15)
                        + Util.rightJustify(itemQty, 4)
                        + Util.rightJustify(itemPrice, 8)
                        + Util.rightJustify(itemTotalPrice, 15)
                        + nextLine;

                itemDetails += itemDetail;

            }


            /*****TOTAL DETAILS******/

            String total = "", discount = "", afterDiscount = "", roundoff = "", vat = "", grandTotal = "";
            total = Util.leftJustify("TOTAL", 21) + Util.rightJustify(getAmount(getNetTotal() - (SELECTED_SALES.getDiscount_value() - SELECTED_SALES.getDiscount())) + " " + CURRENCY, 21);
            discount = Util.leftJustify("DISCOUNT", 21) + Util.rightJustify(getAmount(discnt) + " " + CURRENCY, 21);
            afterDiscount = Util.leftJustify("AFTER DISCOUNT", 21) + Util.rightJustify(getAmount(after_disc) + " " + CURRENCY, 21);
            vat = Util.leftJustify("VAT", 21) + Util.rightJustify(getAmount(SELECTED_SALES.getTaxAmount()) + " " + CURRENCY, 21);
            roundoff = Util.leftJustify("RoundOff", 21) + Util.rightJustify(getAmount(SELECTED_SALES.getRoundoff_value()) + " " + CURRENCY, 21);
            String grandTotalText = Util.center("GRAND TOTAL", MAX_POS_DIGIT);
            grandTotal = Util.center(getAmount(db_grandtot) + " " + CURRENCY, MAX_POS_DIGIT);


            String totalDetails = total
                    + nextLine
                    + discount
                    + afterDiscount
                    + nextLine
                    + vat
                    + nextLine
                    + roundoff
                    + nextLine;


            StringBuilder printContent = new StringBuilder();


            printContent
                    .append(companyDetails)
                    .append(line)
                    .append(nextLine)
                    .append(cutomerDetails)
                    .append(next2Line)
                    .append(Util.center(bilType, MAX_POS_DIGIT))
                    .append(next2Line)
                    .append(invoiceDetails)
                    .append(nextLine)
                    .append(itemHead)
                    .append(nextLine)
                    .append(itemDetails)
                    .append(nextLine)
                    .append(lines)
                    .append(next2Line)
                    .append(totalDetails)
                    .append(next2Line)
                    .append(lines)
                    .append(nextLine)
                    .append(grandTotalText)
                    .append(nextLine)
                    .append(grandTotal)
                    .append(nextLine)
                    .append(lines)
                    .append(next2Line)
                    .append(Util.center(thanksMessage, MAX_POS_DIGIT))
                    .append(nextLine)
                    .append(lines)
                    .append(next2Line);

            byte[] printContentByte = Printer.printfont(printContent.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                    (byte) 0x2A, PocketPos.LANGUAGE_ENGLISH);

            byte[] totalByte = new byte[printContentByte.length];
            int offset = 0;
            System.arraycopy(printContentByte, 0, totalByte, offset, printContentByte.length);
            byte[] sendData = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);


            //sendData(sendData);

        } catch (NullPointerException e) {
            Toast.makeText(this, "wrong print Request", Toast.LENGTH_SHORT).show();
        }
    }

    private void printInvoiceb(ArrayList<CartItem> list) {

        try {

            String line = "-----------------------------------------";
            String lines = "-----------------------------------------";
            String liness = "------------------------------------------";


            /***************Company Details******************/

            String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
            String addressContentStrn = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
            String compName_thank = compName.toUpperCase(Locale.ROOT);  //name get from session
            String registerContentStr = ConfigSales.IS_TAX_ENABLED ? "VAT : " : "GST  :" + sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
            String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
            // String execId = "Code     : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session


            /******************INVOICE DETAILS*********************/


            String bilType = "SALE INVOICE";


            String customerName = "Customer Name : " + SELECTED_SHOPE.getShopName();
            String customerCode = "Customer Code :" + SELECTED_SHOPE.getShopCode();
            String customerVat = "Customer Vat :" + SELECTED_SHOPE.getVatNumber();
            String customerAddress = "Customer Address :" + SELECTED_SHOPE.getShopAddress();

            strBillNumber = SELECTED_SALES.getInvoiceCode();


            String invoiceLeftContent = " ";

            String nextLine = " \n";


            //String routeMob = "Route Mobile No : "+sessionValue.getRegisteredMobile();  //route mob get from session


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


            for (CartItem c : list) {

                String strSl_No = " ", strQty = " ", strPrice = " ", strTotalPrice = " ", str_retqnty = " ", st_prod_hsncode = " ";


                StringBuilder strP_Name = new StringBuilder("");

                strQty = "" + c.getTypeQuantity();
                st_prod_hsncode = "HSN CODE ";

                //haris added on 04-08-21
                double netPrice = c.getProductPrice();


                if (c.getOrderType().equals(PRODUCT_UNIT_CASE)) {
                    netPrice = netPrice * c.getPiecepercart();
                    strQty = c.getTypeQuantity() + "/0";
                }

                strPrice = getAmount(netPrice);
                strTotalPrice = getAmount(c.getProductPrice() * c.getTypeQuantity());

                String[] nameArr = splitToNChar(c.getProductName(), 13);

//            for (String s:nameArr){
                for (int i = 0; i < nameArr.length; i++) {

                    String paddedName = String.format("%-12s", nameArr[i]);
                    //
                    strP_Name.append(paddedName);
                    if (i != nameArr.length - 1)
                        strP_Name.append(" \n\n");
                }

//                String paddedQty = String.format("%-5s", strQty);
                //String paddedQty = String.format("%-4s", strQty);
                String paddedQty = "", paddedPrice = "", paddedTotal = "";
                try {
                    paddedQty = " " + strQty;
                } catch (Exception e) {
                    Toast.makeText(this, "wrong 1", Toast.LENGTH_SHORT).show();
                }
                //String paddedPrice = String.format("%6s", strPrice);
                try {
                    paddedPrice = " " + strPrice;
                    //String paddedTotal = String.format("%10s", strTotalPrice);
                } catch (Exception e) {
                    Toast.makeText(this, "wrong 2", Toast.LENGTH_SHORT).show();
                }
                //try {
                paddedTotal = " " + strTotalPrice;
                Log.e("paddedTotal", "" + paddedTotal);
//                }catch (Exception e){
//                    Toast.makeText(this, "wrong 3", Toast.LENGTH_SHORT).show();
//                }
                // st_prod_hsncode ="1234";
                try {


                    if (st_prod_hsncode.equals("") || st_prod_hsncode.equals(null) || st_prod_hsncode.equals("null") || st_prod_hsncode.length() == 0) {
                        st_prod_hsncode = "00";
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "wrong 4", Toast.LENGTH_SHORT).show();
                }

                try {
                    if (st_prod_hsncode.length() < 8) {
                        if (st_prod_hsncode.length() == 4) {
                            st_prod_hsncode = st_prod_hsncode + "    ";
                        }
                        if (st_prod_hsncode.length() == 6) {

                            st_prod_hsncode = st_prod_hsncode + "  ";
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "wrong 5", Toast.LENGTH_SHORT).show();
                }


                contentItems.append(strP_Name).append(paddedQty).append("   ").append(st_prod_hsncode).append("").append(paddedPrice).append("  ").append(paddedTotal).append("\n\n");
            }

            String items = contentItems.toString();

            if (items.endsWith("\n")) {
                items = items.substring(0, items.length() - 1);
            }

            //haris added on 10-08-21
//            String subTotal=SELECTED_SALES.getTotal() + " " + CURRENCY;
            String subTotal = "", grand = "", return_tot = "", paid = "", balance = "", cgst_sale = "", sgst_sale = "";
            try {
                subTotal = "" + SELECTED_SALES.getTotal();
                ////////


                String vat = getAmount(getTaxTotal()) + " " + CURRENCY;
                // String grand = SELECTED_SALES.getTotal()-(SELECTED_SALES.getReturn_total()+SELECTED_SALES.getReturn_tax())+ " " + CURRENCY;
                grand = "" + getAmount(11.0);
                //String return_tot = (SELECTED_SALES.getReturn_total()+ SELECTED_SALES.getReturn_tax()) + " " + CURRENCY;
                return_tot = getAmount(12.0);
                double ret_tot = roundTwoDecimals(Double.parseDouble(return_tot));
                return_tot = "" + ret_tot;
                paid = getAmount(paid_amount) + " " + CURRENCY;
                balance = getAmount(SELECTED_SALES.getTotal() - paid_amount) + " " + CURRENCY;
                cgst_sale = "13.0";
                sgst_sale = "14.0";


            } catch (Exception e) {
                Toast.makeText(this, "wrong 6", Toast.LENGTH_SHORT).show();
            }
//space rmvd

            //strSubTotal.append("Total  : ").append(subTotal).append(" \n\nCGST  : ").append(cgst_sale).append(" \nSGST : ").append(sgst_sale).append("\nGrand Total  : ").append(grand).append("       ");
            //haris added on 05-08-2021
            StringBuilder strSubTotal = new StringBuilder();
            strSubTotal.append("Total  : ").append(subTotal).append(" \n\nCGST  : ").append(cgst_sale).append(" \n\nSGST : ").append(sgst_sale).append("\n\nReturn Total  : ").append(return_tot).append("\n\nGrand Total  : ").append(grand).append("       ");
            switch (callingActivity) {
                case ActivityConstants.ACTIVITY_SALES:

                    if (paid_amount == SELECTED_SALES.getTotal())
                        bilType = " SALE INVOICE";
                    else {
                        bilType = " SALE INVOICE";
                        strSubTotal.append(" \nPaid Amount : ").append(paid).append("\nBalance     : ").append(balance);

                    }
                    invoiceLeftContent = "INVC NO : " + strBillNumber;

                    break;

                case ActivityConstants.ACTIVITY_SALE_REPORT:

                    if (paid_amount == SELECTED_SALES.getTotal())
                        bilType = "SALE INVOICE";
                    else {
                        bilType = "SALE INVOICE";

                        strSubTotal.append("\nBalance     : ").append(balance);

                    }
                    invoiceLeftContent = "INVC NO : " + strBillNumber;

                    break;
                case ActivityConstants.ACTIVITY_QUOTATION:
                    bilType = "QUOTATION";
                    invoiceLeftContent = "QTN NO : " + "__";
                    break;
            }
            String message = "Thank you for shopping at " + compName_thank + "";

            invoiceLeftContent = "INVC NO : " + SELECTED_SALES.getInvoiceCode();


            // double subTota=roundTwoDecimals(Double.parseDouble(subTotal));
            try {

                try {


                    companyaddress_content.append(nextLine).append(nextLine).append("               " + compName).append(nextLine).append(nextLine).append("                 " + addressContentStrn).append(nextLine).append(nextLine).
                            append(registerContentStr).append(nextLine).append(nextLine).append(execName).append(nextLine).append(nextLine).append(nextLine).append(nextLine).append(execMob).append(nextLine).append(liness).
                            append("Customer No   : " + SELECTED_SHOPE.getShopCode()).append(nextLine).append(nextLine).append("Customer Name : " + strShopName).append(nextLine).append(nextLine).append("Customer GST : " + SELECTED_SHOPE.getVatNumber()).append(nextLine).append(nextLine).append("Customer Address : " + SELECTED_SHOPE.getShopAddress()).append(nextLine).append(nextLine).append("               SALE INVOICE").
                            append(nextLine).append(nextLine).append("" + invoiceLeftContent).append(nextLine).append(nextLine).append("Item").append("         ").
                            append("Qty").append("  ").append("HSN").append("     ").append("Price").append("   ").append("Total").append(" ").append(nextLine).append(liness).append(items).append(nextLine).append(liness).append(nextLine).
                            append(" \n\n                   IGST      : ").append("igst_dbl").append("\n\n                   Total     : ").append("" + subTotal).append("\n\n                   Return Tot: ").append(return_tot).append("\n\n                   Grand Tot : ").append(grand).
                            append(" ").append(nextLine).append(nextLine).append(message).append(nextLine).append(nextLine).append(date);

                } catch (Exception e) {
                    Toast.makeText(this, "wrong 8", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "wrong 9", Toast.LENGTH_SHORT).show();
            }


            //haris added on 27-05-21
            StringBuilder contentcustomer = new StringBuilder();

            contentcustomer.append(line).append(nextLine).append("Customer No   : " + SELECTED_SHOPE.getShopCode()).append(nextLine).append(nextLine).append("Customer Name : " + strShopName).append(nextLine).append(nextLine).append("" + invoiceLeftContent).append(nextLine);


            StringBuilder cashtype_content = new StringBuilder();

            cashtype_content.append(bilType);

            StringBuilder contentmsgs = new StringBuilder();

            contentmsgs.append(" ").append(nextLine).append(nextLine).append(message).append(nextLine).append(nextLine).append(nextLine).append(nextLine).append(date);


            byte[] titleContentByte = Printer.printfont(companyaddress_content.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            byte[] totalByte = new byte[titleContentByte.length];

            int offset = 0;


            System.arraycopy(titleContentByte, 0, totalByte, offset, titleContentByte.length);
            offset += titleContentByte.length;

            byte[] sendData = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);

            //sendData(sendData);

        } catch (NullPointerException e) {
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

                int count = list.size() / MAX_LINE1;

                int SUBLIST_START_SIZE = 0, SUBLIST_END_SIZE = MAX_LINE1;

                for (int i = 0; i < count; i++) {

                    if (list.size() >= SUBLIST_END_SIZE) {
                        sublist = list.subList(SUBLIST_START_SIZE, SUBLIST_END_SIZE);
                        PdfModel pdfModel = new PdfModel();
                        pdfModel.setCartItems(sublist);
                        models.add(pdfModel);

                    }

                    if (SUBLIST_END_SIZE < list.size()) {
//                        return models;
                        SUBLIST_START_SIZE = SUBLIST_START_SIZE + MAX_LINE1;
                        SUBLIST_END_SIZE = SUBLIST_END_SIZE + MAX_LINE1;

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
    private void printwithbackgroundInvoice(List<PdfModel> pdfList) {

        flag = "0";
        File myFile = null;

        PdfWriter writer;
        try {
            String compName = "";

            compName = sessionValue.getCompanyDetails_login().get(PREF_COMPANY_NAME_LOGIN);
            if (compName.equals("")) {
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

            String compny_phone = sessionValue.getCompanyDetails().get(PREF_COMPANY_MOBILE);
            String comp_mail = sessionValue.getCompanyDetails().get(PREF_COMPANY_EMAIL);  //address get from session

            String compny_name = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);
            String address_new = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);


            String compRegisterStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_CR);
            String companyVatStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
            //haris added on 06-11-2020
            String companyPan_No = sessionValue.getCompanyDetails().get(PREF_COMPANY_PAN_NO);
            Log.e("companyPan_No", "" + companyPan_No);

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


            BaseFont bf = BaseFont.createFont("/assets/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
       //     BaseFont bf = BaseFont.createFont("/assets/dejavu_sans_condensed.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);


            Font font20 = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);

            Font font18 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

            Font font14 = new Font(Font.FontFamily.TIMES_ROMAN, 14);


            Font font10Bold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);

            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);

            Font font8bold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);

            Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);

            Font font12Bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);


            LanguageProcessor arabicPro = new ArabicLigaturizer();
            Font fontArb8 = new Font(bf, 8);
            Font fontArb10 = new Font(bf, 10);
            Font fontcompany10 = new Font(bf, 8, Font.BOLD);
            Font fontArb14 = new Font(bf, 14);
            Font fontArb10bl = new Font(bf, 10);
            fontArb10bl.setColor(BaseColor.BLACK);
            for (int i = 0; i < pdfList.size(); i++) {
                int total_cart = cartItems.size();


                PdfModel pdfData = pdfList.get(i);

                List<CartItem> cartList = pdfData.getCartItems();

                String netTotal = "", discount = "", roundOff = "", hsn_code_total = "";

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
                String stdb_grandtot = "";
                String discount_percntge = "";
                String st_data = "";
                String val_in_english = "";
                String val_in_english_vat = "";
                String valtaxableamnt_in_english = "";
                String val_in_Arabic = "";
                double taxable_amnt = 0;

                String st_taxable_amnt = "";
                String total_discountnw = "";
                String totalVat = "";
                double db_totalvat =0;

                if (pdfList.size() == i + 1) {

                    netTotal = getAmount(getNetTotal() - (SELECTED_SALES.getDiscount_value() - SELECTED_SALES.getDiscount()));

                    if (SELECTED_SALES.getRoundoff_value() != 0) {
                        grandTotal = "" + SELECTED_SALES.getRoundofftot();
                    } else {
                        grandTotal = getAmount(SELECTED_SALES.getWithTaxTotal());
                    }

                    stdb_grandtot = "" + getAmount(db_grandtot);
                    paid = getAmount(paid_amount) + " " + CURRENCY;
                    discount = getAmount(SELECTED_SALES.getDiscount());
                    balance = getAmount(SELECTED_SALES.getTotal() - paid_amount) + " " + CURRENCY;
                    totalVat = getAmount(getTaxTotal() / 2);

                    roundOff = "" + SELECTED_SALES.getRoundoff_value();
//                     st_data = "<?xml version=\"1.0\"?>\n" +
//                            "<invoice>\n" +
//                            "    <SellerName>"+ compName+"</SellerName>\n" +
//                            "    <VatNumber>" + str_kafeel_VatNo  +"</VatNumber>\n" +
//                            "    <DateTime>"+SELECTED_SALES.getDate() +"</DateTime>\n" +
//                            "    <VatTotal>"+ totalVat +"</VatTotal>\n" +
//                            "    <TotalAmount>"+getAmount(SELECTED_SALES.getWithTaxTotal())+"</TotalAmount>\n" +
//                            "</invoice>";
                    st_data = str_qrcodelink + "" + strBillNumber;
                    taxable_total = getAmount(SELECTED_SALES.getTaxable_total());
                    total_tax = getAmount(SELECTED_SALES.getTaxAmount());
                    //total_discount = getAmount(SELECTED_SALES.getDiscount_value());
                    total_discount = "" + discnt;
                    total_discountnw = "" + discnt;
                    discount_percntge = getAmount(SELECTED_SALES.getDiscount_percentage());
                    hsn_code_total = SELECTED_SALES.getHsn_code();

                    String s = total_discount.replace(",", "");

                    taxable_amnt = Double.parseDouble("" + netTotal) - Double.parseDouble(s);

                    taxable_amnt = roundTwoDecimals(taxable_amnt);

                    st_taxable_amnt = "" + taxable_amnt;

                    //  new_Outstanding = getAmount(SELECTED_SHOPE.getOutStandingBalance());

                    Transaction t = myDatabase.getCustomerTransactionBalance(SELECTED_SHOPE.getShopId());

                    new_Outstanding = getAmount(t.getOutStandingAmount());

                    if (SELECTED_SALES.getPaid() == 0) {

                        double prevbal = t.getOutStandingAmount() - SELECTED_SALES.getTotal();
                        str_Previous_balance = getAmount(prevbal);

                        Log.e("Credit Prev Bal", "" + str_Previous_balance);
                        Log.e("New OutStand", "" + new_Outstanding);

                    } else {

                        str_Previous_balance = new_Outstanding;

                        Log.e("Cash Prev Bal", "" + str_Previous_balance);
                        Log.e("New OutStand", "" + new_Outstanding);

                    }


                }

                Paragraph compNamePhone = new Paragraph(compny_phone, fontcompany10);
                compNamePhone.setAlignment(Element.ALIGN_LEFT);

                Paragraph compMail = new Paragraph(comp_mail, fontcompany10);
                compMail.setAlignment(Element.ALIGN_LEFT);

                Paragraph compweb = new Paragraph(comp_web, fontcompany10);
                compweb.setAlignment(Element.ALIGN_LEFT);

                Paragraph compnamearab = new Paragraph(compNameArab, fontcompany10);
                compnamearab.setAlignment(Element.ALIGN_RIGHT);

                Paragraph compaddress1arab = new Paragraph(address1ArabStr, fontcompany10);
                compaddress1arab.setAlignment(Element.ALIGN_RIGHT);

                Paragraph compaddress2arab = new Paragraph(address2ArabStr, fontcompany10);
                compaddress2arab.setAlignment(Element.ALIGN_RIGHT);

                Paragraph compname = new Paragraph(compny_name, fontcompany10);
                compname.setAlignment(Element.ALIGN_LEFT);

                Paragraph compvatno = new Paragraph("VAT No : " + companyVatStr, fontcompany10);
                compvatno.setAlignment(Element.ALIGN_LEFT);

                Paragraph compmob = new Paragraph("Mobile No : " + compny_phone, fontcompany10);
                compmob.setAlignment(Element.ALIGN_LEFT);


                Paragraph compaddress = new Paragraph(address_new, fontcompany10);
                compaddress.setAlignment(Element.ALIGN_LEFT);

                Paragraph compaddress2 = new Paragraph(address2Str, fontcompany10);
                compaddress2.setAlignment(Element.ALIGN_LEFT);


                Paragraph compPlaceTag = new Paragraph(address1Str, fontArb8);
                compPlaceTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compMobileTag = new Paragraph(address2Str + ",Mob : " + mobileStr + ", CR :" + compRegisterStr + " " + arabicPro.process(address2ArabStr), fontArb8);
                compMobileTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
                compEmailEng.setAlignment(Element.ALIGN_CENTER);

                Paragraph compdate = new Paragraph(compDateStr, font8);
                compdate.setAlignment(Element.ALIGN_RIGHT);

                Paragraph compVatTag = new Paragraph("                                                             VAT Number " + companyVatStr + "                Original For Customer", fontArb8);


                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (paid_amount == SELECTED_SALES.getTotal()) {
                            //  paragraph = new Paragraph(arabicPro.process("???????????????? ?????????????? - " + " CASH INVOICE"), fontArb14);
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                        } else {
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                            // paragraph = new Paragraph(arabicPro.process("?????????? ???????????????? - " + " CREDIT INVOICE"), fontArb14);
                        }


                        break;

                    case ActivityConstants.ACTIVITY_QUOTATION:

                        Paragraph paragraph = new Paragraph(" QUOTATION", fontArb14);

                        break;
                }

                //////////////////////////////////////////

                PdfPCell cell;  //default cell

                //space cell
                PdfPCell cellSpace = new PdfPCell();
                cellSpace.setPadding(1);
                cellSpace.setBorder(PdfPCell.BOTTOM);
                cellSpace.setHorizontalAlignment(Element.ALIGN_CENTER);

                //Create the table which will be 2 Columns wide and make it 100% of the page
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{5, 5});

                PdfPCell cellLogo = new PdfPCell();
                cellLogo.setBorder(PdfPCell.NO_BORDER);
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setPaddingTop(1);
                cellLogo.setPaddingBottom(1);
//                cellLogo.setPaddingRight(5);
//                cellLogo.setPaddingLeft(5);

//                try {
//
//                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.print_logo_pdf);
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    Image img = Image.getInstance(stream.toByteArray());
////                  img.setAbsolutePosition(25f, 735f);
//                    img.scalePercent(25f);
//                    img.setAlignment(Element.ALIGN_RIGHT);
//                    cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    cellLogo.addElement(img);
//
//
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

                PdfPCell cellTitle = new PdfPCell();
                cellTitle.setBorder(PdfPCell.NO_BORDER);

                cellTitle.setPadding(1);

                Paragraph paragraph = new Paragraph(arabicPro.process("(?? ????????????)" + " TAX INVOICE"), fontArb14);

                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (paid_amount == SELECTED_SALES.getTotal()) {
                            //  paragraph = new Paragraph(arabicPro.process("???????????????? ?????????????? - " + " CASH INVOICE"), fontArb14);
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                        } else {
                            paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);
                            // paragraph = new Paragraph(arabicPro.process("?????????? ???????????????? - " + " CREDIT INVOICE"), fontArb14);
                        }


                        break;

                    case ActivityConstants.ACTIVITY_QUOTATION:

                        paragraph = new Paragraph(arabicPro.process("" + " QUOTATION"), fontArb14);

                        break;
                }

                paragraph.setAlignment(Element.ALIGN_CENTER);
                cellTitle.setHorizontalAlignment(Element.ALIGN_LEFT);


                cellTitle.addElement(compname);
                cellTitle.addElement(compaddress);
                cellTitle.addElement(compaddress2);
                cellTitle.addElement(compmob);
                cellTitle.addElement(compvatno);

//                cellTitle.addElement(compweb);
//                cellTitle.addElement(compNamePhone);


                ////////////////3rd column

                PdfPCell cellthirdcolumn = new PdfPCell();
                cellthirdcolumn.setBorder(PdfPCell.NO_BORDER);
                cellthirdcolumn.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellthirdcolumn.setPadding(1);


                cellthirdcolumn.addElement(compnamearab);
                cellthirdcolumn.addElement(compaddress1arab);
                cellthirdcolumn.addElement(compaddress2arab);
//                cellthirdcolumn.addElement(compaddress2);

                table.addCell(cellTitle);
                table.addCell(cellthirdcolumn);
                //  table.addCell(cellthirdcolumn);

                document.add(table);

                ///////////////////////////////////////////


                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{1});

                PdfPCell celltitle = new PdfPCell();
                celltitle.setBorder(PdfPCell.NO_BORDER);

                paragraph = new Paragraph(" TAX INVOICE " + arabicPro.process("(???????????? ????????????)"), fontArb10bl);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                celltitle.addElement(paragraph);
                celltitle.setPaddingTop(1);
                celltitle.setPaddingBottom(5);
                celltitle.setPaddingRight(5);
                celltitle.setPaddingLeft(5);

                table.addCell(celltitle);

                document.add(table);


                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{5, 5});

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //
                paragraph = new Paragraph("Bill To : ", font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //            customer Address  label
                paragraph = new Paragraph("Invoice No   :" + SELECTED_SALES.getInvoiceCode(), font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //
                paragraph = new Paragraph("" + strShopName, font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //            customer Address  label
                paragraph = new Paragraph("Date : " + strDate, font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //
                paragraph = new Paragraph("", font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //            customer Address  label
                paragraph = new Paragraph("Invoice Type : " + SELECTED_SALES.getPayment_type(), font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                document.add(table);

                //second table


                //Create the table which will be 8 Columns wide and make it 100% of the page
                table = new PdfPTable(8);
                table.setWidths(new int[]{3, 11, 4, 3, 4, 4, 4, 4});
                table.setWidthPercentage(100);
//            table.setSpacingBefore(10);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("SL No \n" + arabicPro.process("??????????"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                cell.setRowspan(2);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Description/Item name \n" + arabicPro.process("????????????"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Qty \n" + arabicPro.process("????????"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                cell.addElement(paragraph);
                cell.setRowspan(2);

                table.addCell(cell);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Unit \n" + arabicPro.process("????????"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Price \n" + arabicPro.process("??????"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                //discnt
                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Discount \n" + arabicPro.process("??????????"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Tax \n" + arabicPro.process("????????????"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("Total \n" + arabicPro.process("??????????"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                String str_vatRate_tot = "", str_unit_tot = " ", str_vatRate_total = "";

                double prod_discnt_totl = 0, db_taxable_nettotal = 0, db_prod_discnt_total = 0;
                int max_line = cartList.size();

                    flag_moreline =0;
                    for (int j = 0; j < max_line; j++) {
                        //  if(MAX_LINE>max_line){

                        String strSl_No = " ", strP_Code = " ", strP_Name = " ", strP_Arabic = "  ", strQty = " ", strNetPrice = " ", strNetTotal = " ",
                                strAmount = " ", strTotalPrice = " ", str_vatAmount = " ", str_unit = "", str_vatRate = "",
                                str_prod_disc = "", strbarcode = "", str_mfgdate = "", str_descrptn = "";

                        if (cartList.size() > j) {
                            CartItem cartItem = cartList.get(j);
                            int slNo =0;
                            if (cartItems.size() > MAX_LINE) {
                                slNo = i * MAX_LINE1 + j + 1;
                            }
                            else{
                                slNo = i * MAX_LINE + j + 1;
                            }

                            strSl_No = String.valueOf(slNo);
                            strP_Name = cartItem.getProductName();
                            strP_Arabic = cartItem.getArabicName();
                            strP_Code = cartItem.getProductCode();
                            str_unit = cartItem.getUnitselected();
                            str_descrptn = cartItem.getDescription();
                            str_unit_tot = cartItem.getUnitselected();
                            str_prod_disc = String.valueOf(cartItem.getProductDiscount() * cartItem.getPieceQuantity_nw());
                            prod_discnt_totl = prod_discnt_totl + (cartItem.getProductDiscount() * cartItem.getPieceQuantity_nw());
                            strbarcode = cartItem.getBarcode();
                            str_mfgdate = cartItem.getMfg_date();
                            // Log.e("strP_Arabic",""+strP_Arabic);

                            if (strP_Arabic == null || TextUtils.isEmpty(strP_Arabic.trim()) || strP_Arabic.equals("null"))
                                strP_Arabic = "  ";

                            // strQty = "0/" + String.valueOf(cartItem.getTypeQuantity()); // case and piece

                            strQty = "" + String.valueOf(cartItem.getTypeQuantity()); // piece only

//                        double netPrice = cartItem.getNetPrice();
                            double netPrice = cartItem.getProductPrice();


                            if (cartItem.getOrderType().equals(PRODUCT_UNIT_CASE)) {
                                netPrice = netPrice * cartItem.getPiecepercart();
//                            strQty = cartItem.getTypeQuantity() + "/0";
                                strQty = cartItem.getTypeQuantity() + "";
                            }
                            tot_qnty = (int) (tot_qnty + cartItem.getTypeQuantity());

                            str_vatRate = String.valueOf(cartItem.getTax() + " %");
                            str_vatRate_tot = String.valueOf(cartItem.getTax() + " %");
                            str_vatRate_total = String.valueOf(SELECTED_SALES.getTaxAmount());

                            ///////////////////////added by haris on 24-02-22
                            strNetTotal = getAmount((cartItem.getProductPrice() * cartItem.getPieceQuantity_nw()) - (cartItem.getProductDiscount() * cartItem.getPieceQuantity_nw()) + (cartItem.getTaxValue() / 2) * cartItem.getPieceQuantity_nw());
                            db_taxable_nettotal = db_taxable_nettotal + ((cartItem.getProductPrice() * cartItem.getPieceQuantity_nw()) - cartItem.getProductDiscount() * cartItem.getPieceQuantity_nw());
                            db_prod_discnt_total = db_prod_discnt_total + (cartItem.getProductDiscount() * cartItem.getPieceQuantity_nw());
                            // cartItem.setTaxValue(AmountCalculator.getTaxPrice(cartItem.getProductPrice(), cartItem.getTax(),cartItem.getTax_type()));
                            str_vatAmount = getAmount((cartItem.getTaxValue() / 2) * cartItem.getPieceQuantity_nw());

                            strTotalPrice = getAmount((cartItem.getProductPrice() * cartItem.getPieceQuantity_nw()) + (cartItem.getTaxValue() * cartItem.getPieceQuantity_nw()));


                            ////////////////////
                            strNetPrice = getAmount(netPrice);

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
                        if (strP_Name.length() > 40)
                            strP_Name = getMaximumChar(strP_Name, 40);

                        if (strP_Arabic.length() > 42)
                            strP_Arabic = getMaximumChar(strP_Arabic, 42);


                        //strSl_No

                        cell = new PdfPCell(new Phrase("\n" + justifiedSlNo + "\n", font8));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setPadding(2);
                        cell.setRowspan(2);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setFixedHeight(30f);
                        table.addCell(cell);


                        if (str_descrptn.length() > 0) {
                            cell = new PdfPCell(new Phrase("\n" + strP_Name + " ( " + str_descrptn + " ) \n", font8));

                            cell.setBorder(Rectangle.NO_BORDER);
                            cell.setPadding(2);
                            cell.setRowspan(2);
                            cell.setFixedHeight(30f);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Phrase("\n" + strP_Name, font8));

                            cell.setBorder(Rectangle.NO_BORDER);
                            cell.setPadding(2);
                            cell.setRowspan(2);
                            cell.setFixedHeight(30f);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);
                        }


                        //Qty

                        if (strQty.length() > 0 && str_unit.length() > 1) {
                            cell = new PdfPCell(new Phrase("\n" + strQty + "\n", font8));
                            cell.setBorder(Rectangle.NO_BORDER);
                            cell.setPadding(2);
                            cell.setFixedHeight(30f);
                            cell.setRowspan(2);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Phrase("\n" + justifiedQuantity + "\n", font8));
                            cell.setBorder(Rectangle.NO_BORDER);
                            cell.setPadding(2);
                            cell.setRowspan(2);
                            cell.setFixedHeight(30f);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cell);
                        }


                        cell = new PdfPCell(new Phrase("\n" + justifiedunit + "\n", font8));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setPadding(2);
                        cell.setRowspan(2);
                        cell.setFixedHeight(30f);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);

//net total

                        cell = new PdfPCell(new Phrase("\n" + justifiedNetPrice + "\n", font8));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setPadding(2);
                        cell.setRowspan(2);
                        cell.setFixedHeight(30f);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);

//discount
                        cell = new PdfPCell(new Phrase("\n" + justifiedprod_disc + "\n", font8));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setPadding(2);
                        cell.setRowspan(2);
                        cell.setFixedHeight(30f);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);


                        // total
                        cell = new PdfPCell(new Phrase("\n" + justifiedVatAmount + "\n", font8));//justifiedTotal //getTaxAmount
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setPadding(2);
                        cell.setRowspan(2);
                        cell.setFixedHeight(30f);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);

                        // total
                        cell = new PdfPCell(new Phrase("\n" + justifiedNetTotal + "\n", font8));//justifiedTotal //getTaxAmount
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setPadding(2);
                        cell.setRowspan(2);
                        cell.setFixedHeight(30f);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);

                        //  }

                    }
                    document.add(table);
                if (cartItems.size() > MAX_LINE) {
                    table = new PdfPTable(1);
                    table.setWidths(new int[]{1});
                    table.setWidthPercentage(100);

                    cell = new PdfPCell(new Phrase("  ", font8));
                    cell.setBorder(Rectangle.TOP);
                    cell.setPaddingTop(50);

                    table.addCell(cell);

                    document.add(table);
                }

                if (cartItems.size() <= MAX_LINE) {
                    table = new PdfPTable(1);
                    table.setWidths(new int[]{1});
                    table.setWidthPercentage(100);

                    cell = new PdfPCell(new Phrase("  ", font8));
                    cell.setBorder(Rectangle.TOP);

                    table.addCell(cell);

                    document.add(table);

                    /////////////////////////////////////////////////////////////


                    table = new PdfPTable(8);
                    table.setWidths(new int[]{3, 11, 4, 3, 4, 4, 4, 4});
                    table.setWidthPercentage(100);

                    cell = new PdfPCell(new Phrase("", font8));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight(15f);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("Total " + arabicPro.process("(??????????)"), fontArb10bl));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight(15f);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("" + tot_qnty, font8bold));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight(15f);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("", font8));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight(15f);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);


//disnt total


                    cell = new PdfPCell(new Phrase("", font8bold));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight(15f);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("" + discnt, font8bold));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight(15f);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    // total
                    cell = new PdfPCell(new Phrase("" + SELECTED_SALES.getTaxAmount(), font8bold));//justifiedTotal //getTaxAmount
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight(15f);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);

                    // total
                    cell = new PdfPCell(new Phrase("" + getAmount(db_grandtot), font8bold));//justifiedTotal //getTaxAmount
                    cell.setBorder(Rectangle.NO_BORDER);

                    cell.setFixedHeight(15f);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);

                    document.add(table);
                    /////////////////////////////////////////////////////////////

                    table = new PdfPTable(1);
                    table.setWidths(new int[]{3});
                    table.setWidthPercentage(100);

                    cell = new PdfPCell(new Phrase(" ", font8));
                    cell.setBorder(Rectangle.BOTTOM);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                    table.addCell(cell);

                    document.add(table);


                    val_in_english = convertNumberToEnglishWords(String.valueOf(db_grandtot));
                    val_in_english_vat = convertNumberToEnglishWords(String.valueOf(str_vatRate_total));

                    //new rows for output vat

                    //////////////////////////////


                    table = new PdfPTable(4); // 4
                    table.setWidthPercentage(100.0f);
                    table.setWidths(new int[]{7, 2, 2, 2}); // 5,2,2,1

                    //            Amount
                    cell = new PdfPCell();
                    cell = new PdfPCell(new Phrase("INVOICE AMOUNT IN WORDS  : ", font8)); // Net Amount
                    cell.setBorder(PdfPCell.TOP);
                    cell.setPaddingTop(10);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                    cell.setRowspan(1);


                    table.addCell(cell);


                    //////////////////////******   total details ****//////////////////////

                    //      net total amount

                    cell = new PdfPCell(new Phrase("Sub Total", font8)); // Net Amount
                    cell.setPadding(3);
                    cell.setPaddingTop(10);
                    cell.setBorder(PdfPCell.TOP);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("" + arabicPro.process("???????????? ????????????????"), fontArb8));
                    cell.setPadding(3);
                    cell.setPaddingTop(10);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("" + net_tot, font8));
                    cell.setPadding(3);
                    cell.setPaddingTop(10);
                    cell.setBorder(PdfPCell.TOP);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);

                    /////////////////////////////


                    //            Amount
                    cell = new PdfPCell();
                    cell = new PdfPCell(new Phrase(" ", font8)); // Net Amount
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setPaddingTop(3);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                    cell.setRowspan(1);

                    table.addCell(cell);

                    //      net total amount

                    cell = new PdfPCell(new Phrase("Discount", font8)); // Net Amount
                    cell.setPadding(3);
                    cell.setPaddingTop(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("" + arabicPro.process("???????????? ????????????"), fontArb8));
                    cell.setPadding(3);
                    cell.setPaddingTop(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("" + fl_discount, font8));
                    cell.setPadding(3);
                    cell.setPaddingTop(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    ////////////////////////////


                    //      discount amount

                    cell = new PdfPCell(new Phrase("" + val_in_english, font8)); // Net Amount
                    cell.setPadding(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("VAT @ 15 %", font8));
                    cell.setPadding(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("" + arabicPro.process("??????????????"), fontArb8));
                    cell.setPadding(3);
                    cell.setPaddingTop(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                    //      vat amount

                    cell = new PdfPCell(new Phrase(" " + totalVat, font8)); // Net Amount
                    cell.setPadding(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("", font8));
                    cell.setPadding(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    //      Round off

                    cell = new PdfPCell(new Phrase("Total  ", font8bold)); // Net Amount
                    cell.setPadding(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("" + arabicPro.process("???????????? ????????????"), fontArb8));
                    cell.setPadding(3);
                    cell.setPaddingTop(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase("" + getAmount(db_grandtot), font8bold));
                    cell.setPadding(3);
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);


                    document.add(table);

                    table = new PdfPTable(2);
                    table.setWidthPercentage(100.0f);
                    table.setWidths(new int[]{5, 5});


                    cellLogo = new PdfPCell();
                    cellLogo.setBorder(PdfPCell.NO_BORDER);
                    cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellLogo.setPaddingTop(1);

                    cellLogo.setPaddingLeft(4);

                    if (compName.equals("") && compNameArab.equals("")) {
                        compName = "No Name";
                    }
                    if (compNameArab.equals("")) {
                        compNameArab = "";
                    }

                    String qrBarcodeHash = QRBarcodeEncoder.encode(
                            new Seller(compNameArab),
                            new TaxNumber("00" + companyVatStr),
                            new InvoiceDate(strDate),
                            new InvoiceTotalAmount("" + db_grandtot),
                            new InvoiceTaxAmount("" + SELECTED_SALES.getTax_amount())

                    );
                    Log.e("qrBarcodeHash", "" + qrBarcodeHash);

                    //  String st_data = str_qrcodelink+""+strBillNumber;
                    MultiFormatWriter writr = new MultiFormatWriter();
                    try {
                        BitMatrix matrix = writr.encode(qrBarcodeHash, BarcodeFormat.QR_CODE, 350, 350);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        Bitmap bitmap = encoder.createBitmap(matrix);


                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        Image img = Image.getInstance(stream.toByteArray());
                        img.setAbsolutePosition(15f, 100f);
                        img.scalePercent(25f);
                        //img.scalePercent(20f);
                        img.setAlignment(Element.ALIGN_LEFT);
                        cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellLogo.addElement(img);
                        //cellLogo.setBackgroundColor(colorGreen);

                    } catch (Exception e) {

                    }

                    table.addCell(cellLogo);

                    cell = new PdfPCell();
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                    paragraph = new Paragraph("For, " + compName, font8);//strBillNumber
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);


                    table.addCell(cell);


                    cell = new PdfPCell();
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                    paragraph = new Paragraph("", font8);//strBillNumber
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);


                    table.addCell(cell);

                    cell = new PdfPCell();
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                    paragraph = new Paragraph("Authorized Signatory", font8);//strBillNumber
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
                    cell.setBorder(PdfPCell.BOTTOM);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                    cell.setPaddingTop(60);

                    //            customer Address  label
                    paragraph = new Paragraph("   ", font8bold);//strBillNumber
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
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                    cell.setPaddingTop(1);

                    //            customer Address  label
                    paragraph = new Paragraph("  " + strDate, font8bold);//strBillNumber
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(paragraph);
                    cell.setPaddingTop(1);
                    cell.setRowspan(10);
                    table.addCell(cell);


                    document.add(table);
                }
                else{
                    if(i==pdfList.size()-1){
                        table = new PdfPTable(1);
                        table.setWidths(new int[]{1});
                        table.setWidthPercentage(100);

                        cell = new PdfPCell(new Phrase("  ", font8));
                        cell.setBorder(Rectangle.TOP);

                        table.addCell(cell);

                        document.add(table);

                        /////////////////////////////////////////////////////////////


                        table = new PdfPTable(8);
                        table.setWidths(new int[]{3, 11, 4, 3, 4, 4, 4, 4});
                        table.setWidthPercentage(100);

                        cell = new PdfPCell(new Phrase("", font8));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setFixedHeight(15f);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("Total " + arabicPro.process("??????????"), font10Bold));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setFixedHeight(15f);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("" + tot_qnty, font8bold));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setFixedHeight(15f);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("", font8));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setFixedHeight(15f);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);


//disnt total


                        cell = new PdfPCell(new Phrase("", font8bold));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setFixedHeight(15f);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("" + discnt, font8bold));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setFixedHeight(15f);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);


                        // total
                        cell = new PdfPCell(new Phrase("" + SELECTED_SALES.getTaxAmount(), font8bold));//justifiedTotal //getTaxAmount
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setFixedHeight(15f);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);

                        // total
                        cell = new PdfPCell(new Phrase("" + getAmount(db_grandtot), font8bold));//justifiedTotal //getTaxAmount
                        cell.setBorder(Rectangle.NO_BORDER);

                        cell.setFixedHeight(15f);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);

                        document.add(table);
                        /////////////////////////////////////////////////////////////

                        table = new PdfPTable(1);
                        table.setWidths(new int[]{3});
                        table.setWidthPercentage(100);

                        cell = new PdfPCell(new Phrase(" ", font8));
                        cell.setBorder(Rectangle.BOTTOM);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                        table.addCell(cell);

                        document.add(table);


                        val_in_english = convertNumberToEnglishWords(String.valueOf(db_grandtot));
                        val_in_english_vat = convertNumberToEnglishWords(String.valueOf(str_vatRate_total));

                        //new rows for output vat

                        //////////////////////////////


                        table = new PdfPTable(4); // 4
                        table.setWidthPercentage(100.0f);
                        table.setWidths(new int[]{7, 2, 2, 2}); // 5,2,2,1

                        //            Amount
                        cell = new PdfPCell();
                        cell = new PdfPCell(new Phrase("INVOICE AMOUNT IN WORDS  : ", font8)); // Net Amount
                        cell.setBorder(PdfPCell.TOP);
                        cell.setPaddingTop(10);
                        cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                        cell.setRowspan(1);


                        table.addCell(cell);


                        //////////////////////******   total details ****//////////////////////

                        //      net total amount

                        cell = new PdfPCell(new Phrase("Sub Total", font8)); // Net Amount
                        cell.setPadding(3);
                        cell.setPaddingTop(10);
                        cell.setBorder(PdfPCell.TOP);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("" + arabicPro.process("???????????? ????????????????"), fontArb8));
                        cell.setPadding(3);
                        cell.setPaddingTop(10);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);



                        cell = new PdfPCell(new Phrase("" + net_tot, font8));
                        cell.setPadding(3);
                        cell.setPaddingTop(10);
                        cell.setBorder(PdfPCell.TOP);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);

                        /////////////////////////////


                        //            Amount
                        cell = new PdfPCell();
                        cell = new PdfPCell(new Phrase(" ", font8)); // Net Amount
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setPaddingTop(3);
                        cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                        cell.setRowspan(1);

                        table.addCell(cell);

                        //      net total amount

                        cell = new PdfPCell(new Phrase("Discount", font8)); // Net Amount
                        cell.setPadding(3);
                        cell.setPaddingTop(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("" + arabicPro.process("???????????? ????????????"), fontArb8));
                        cell.setPadding(3);
                        cell.setPaddingTop(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("" + fl_discount, font8));
                        cell.setPadding(3);
                        cell.setPaddingTop(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);


                        ////////////////////////////


                        //      discount amount

                        cell = new PdfPCell(new Phrase("" + val_in_english, font8)); // Net Amount
                        cell.setPadding(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("VAT @ 15 %", font8));
                        cell.setPadding(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("" + arabicPro.process("??????????????"), fontArb8));
                        cell.setPadding(3);
                        cell.setPaddingTop(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);

                        //      vat amount

                        cell = new PdfPCell(new Phrase(" " + totalVat, font8)); // Net Amount
                        cell.setPadding(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("", font8));
                        cell.setPadding(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);


                        //      Round off

                        cell = new PdfPCell(new Phrase("Total  ", font8bold)); // Net Amount
                        cell.setPadding(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("" + arabicPro.process("???????????? ????????????"), fontArb8));
                        cell.setPadding(3);
                        cell.setPaddingTop(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);


                        cell = new PdfPCell(new Phrase("" + getAmount(db_grandtot), font8bold));
                        cell.setPadding(3);
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell);


                        document.add(table);

                        table = new PdfPTable(2);
                        table.setWidthPercentage(100.0f);
                        table.setWidths(new int[]{5, 5});


                        cellLogo = new PdfPCell();
                        cellLogo.setBorder(PdfPCell.NO_BORDER);
                        cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cellLogo.setPaddingTop(1);

                        cellLogo.setPaddingLeft(4);

                        if (compName.equals("") && compNameArab.equals("")) {
                            compName = "No Name";
                        }
                        if (compNameArab.equals("")) {
                            compNameArab = "";
                        }

                        String qrBarcodeHash = QRBarcodeEncoder.encode(
                                new Seller(compNameArab),
                                new TaxNumber("00" + companyVatStr),
                                new InvoiceDate(strDate),
                                new InvoiceTotalAmount("" + db_grandtot),
                                new InvoiceTaxAmount("" + SELECTED_SALES.getTax_amount())

                        );
                        Log.e("qrBarcodeHash", "" + qrBarcodeHash);

                        //  String st_data = str_qrcodelink+""+strBillNumber;
                        MultiFormatWriter writr = new MultiFormatWriter();
                        try {
                            BitMatrix matrix = writr.encode(qrBarcodeHash, BarcodeFormat.QR_CODE, 350, 350);
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            Bitmap bitmap = encoder.createBitmap(matrix);


                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            Image img = Image.getInstance(stream.toByteArray());
                            img.setAbsolutePosition(15f, 100f);
                            img.scalePercent(25f);
                            //img.scalePercent(20f);
                            img.setAlignment(Element.ALIGN_LEFT);
                            cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cellLogo.addElement(img);
                            //cellLogo.setBackgroundColor(colorGreen);

                        } catch (Exception e) {

                        }

                        table.addCell(cellLogo);

                        cell = new PdfPCell();
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                        paragraph = new Paragraph("For, " + compName, font8);//strBillNumber
                        paragraph.setAlignment(Element.ALIGN_RIGHT);
                        cell.addElement(paragraph);


                        table.addCell(cell);


                        cell = new PdfPCell();
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                        paragraph = new Paragraph("", font8);//strBillNumber
                        paragraph.setAlignment(Element.ALIGN_RIGHT);
                        cell.addElement(paragraph);


                        table.addCell(cell);

                        cell = new PdfPCell();
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                        paragraph = new Paragraph("Authorized Signatory", font8);//strBillNumber
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
                        cell.setBorder(PdfPCell.BOTTOM);
                        cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                        cell.setPaddingTop(60);

                        //            customer Address  label
                        paragraph = new Paragraph("   ", font8bold);//strBillNumber
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
                        cell.setBorder(PdfPCell.NO_BORDER);
                        cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                        cell.setPaddingTop(1);

                        //            customer Address  label
                        paragraph = new Paragraph("  " + strDate, font8bold);//strBillNumber
                        paragraph.setAlignment(Element.ALIGN_CENTER);
                        cell.addElement(paragraph);
                        cell.setPaddingTop(1);
                        cell.setRowspan(10);
                        table.addCell(cell);


                        document.add(table);
                    }
                }




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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void printInvoice(List<PdfModel> pdfList) {


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
            Log.e("companyPan_No", "" + companyPan_No);

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

            Font font8bold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);

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

                String netTotal = "", discount = "", roundOff = "", hsn_code_total = "";

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
                double taxable_amnt = 0;
                String st_taxable_amnt = "";
                String st_grandtot = "";

                String totalVat = "";

                if (pdfList.size() == i + 1) {

                    netTotal = getAmount(getNetTotal() - (SELECTED_SALES.getDiscount_value() - SELECTED_SALES.getDiscount()));

                    if (SELECTED_SALES.getRoundoff_value() != 0) {
                        grandTotal = "" + SELECTED_SALES.getRoundofftot();
                    } else {
                        grandTotal = getAmount(SELECTED_SALES.getWithTaxTotal());
                    }


                    paid = getAmount(paid_amount) + " " + CURRENCY;
                    discount = getAmount(SELECTED_SALES.getDiscount());
                    balance = getAmount(SELECTED_SALES.getTotal() - paid_amount) + " " + CURRENCY;
                    totalVat = getAmount(getTaxTotal());

                    taxable_total = getAmount(SELECTED_SALES.getTaxable_total());
                    total_tax = getAmount(SELECTED_SALES.getTaxAmount());
                    //total_discount = getAmount(SELECTED_SALES.getDiscount_value());
                    total_discount = "" + discnt;
//                     st_data = "<?xml version=\"1.0\"?>\n" +
//                            "<invoice>\n" +
//                            "    <SellerName>"+ compName+"</SellerName>\n" +
//                            "    <VatNumber>" + str_kafeel_VatNo  +"</VatNumber>\n" +
//                            "    <DateTime>"+SELECTED_SALES.getDate() +"</DateTime>\n" +
//                            "    <VatTotal>"+ totalVat +"</VatTotal>\n" +
//                            "    <TotalAmount>"+getAmount(SELECTED_SALES.getWithTaxTotal())+"</TotalAmount>\n" +
//                            "</invoice>";
                    st_data = str_qrcodelink + "" + strBillNumber;
                    String s = total_discount.replace(",", "");
                    String net = netTotal.replace(",", "");
                    taxable_amnt = Double.parseDouble("" + Double.parseDouble(net)) - Double.parseDouble(s);
                    st_grandtot = getAmount(db_grandtot);
                    taxable_amnt = roundTwoDecimals(taxable_amnt);

                    st_taxable_amnt = "" + taxable_amnt;

                    //  new_Outstanding = getAmount(SELECTED_SHOPE.getOutStandingBalance());

                    Transaction t = myDatabase.getCustomerTransactionBalance(SELECTED_SHOPE.getShopId());

                    new_Outstanding = getAmount(t.getOutStandingAmount());

                    if (SELECTED_SALES.getPaid() == 0) {

                        double prevbal = t.getOutStandingAmount() - SELECTED_SALES.getTotal();
                        str_Previous_balance = getAmount(prevbal);

                        Log.e("Credit Prev Bal", "" + str_Previous_balance);
                        Log.e("New OutStand", "" + new_Outstanding);

                    } else {

                        str_Previous_balance = new_Outstanding;

                        Log.e("Cash Prev Bal", "" + str_Previous_balance);
                        Log.e("New OutStand", "" + new_Outstanding);

                    }

                    val_in_english = convertNumberToEnglishWords(String.valueOf(getAmount(db_grandtot)));

                }

                Paragraph compNameTag = new Paragraph(compName, fontcompany10);
                compNameTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compPlaceTag = new Paragraph(address1Str, fontArb8);
                compPlaceTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compMobileTag = new Paragraph(address2Str + ",Mob : " + mobileStr + ", CR :" + compRegisterStr + " " + arabicPro.process(address2ArabStr), fontArb8);
                compMobileTag.setAlignment(Element.ALIGN_CENTER);

                Paragraph compEmailEng = new Paragraph(compEmailStr, font8);
                compEmailEng.setAlignment(Element.ALIGN_CENTER);

                Paragraph compdate = new Paragraph(compDateStr, font8);
                compdate.setAlignment(Element.ALIGN_RIGHT);

                Paragraph compVatTag = new Paragraph("                                                             VAT Number " + companyVatStr + "                Original For Customer", fontArb8);
                //compVatTag.setAlignment();


                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1});


                switch (callingActivity) {
                    case ActivityConstants.ACTIVITY_SALES:

                        if (paid_amount == SELECTED_SALES.getTotal()) {
                            //  paragraph = new Paragraph(arabicPro.process("???????????????? ?????????????? - " + " CASH INVOICE"), fontArb14);
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                        } else {
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                            // paragraph = new Paragraph(arabicPro.process("?????????? ???????????????? - " + " CREDIT INVOICE"), fontArb14);
                        }


                        break;
                    case ActivityConstants.ACTIVITY_SALE_REPORT:

                        if (paid_amount == SELECTED_SALES.getTotal()) {
                            // paragraph = new Paragraph(arabicPro.process("???????????????? ?????????????? - " + " CASH INVOICE"), fontArb14);
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                        } else {
                            //  paragraph = new Paragraph(arabicPro.process("?????????? ???????????????? - " + " CREDIT INVOICE"), fontArb14);
                            Paragraph paragraph = new Paragraph(" TAX INVOICE", fontArb14);
                        }


                        break;
                    case ActivityConstants.ACTIVITY_QUOTATION:

                        Paragraph paragraph = new Paragraph(" QUOTATION", fontArb14);

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
                Paragraph paragraph = new Paragraph("                                 " + str_paymenttype, font12Bold);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setPaddingLeft(1);

                cell.setPaddingTop(80);

                table.addCell(cell);

                document.add(table);
//Create the table which will be 3 Columns wide and make it 100% of the page
                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{1, 1});


                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("          " + strBillNumber, font12Bold);
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
                paragraph = new Paragraph("" + strDate, font12Bold);
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
                paragraph = new Paragraph("                       :  " + SELECTED_SHOPE.getShopName(), font12Bold);
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

                paragraph = new Paragraph("VAT NO        :  " + SELECTED_SHOPE.getVatNumber(), font12Bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(1);

                table.addCell(cell);

                document.add(table);

                //Create the table which will be 2 Columns wide and make it 100% of the page
                table = new PdfPTable(7);
                //table.setWidths(new int[]{3,15, 3,3, 4, 4, 4, 4, 3,4});
                table.setWidths(new int[]{5, 28, 6, 6, 6, 7, 9});
                table.setWidthPercentage(100);
                //table.setPaddingTop(40);
                table.setSpacingBefore(75);//60


                for (int j = 0; j < MAX_LINE; j++) {

                    String strSl_No = " ", strP_Code = " ", strP_Name = " ", strP_Arabic = "  ", strQty = " ", strNetPrice = " ",
                            strNetTotal = " ", strAmount = " ", strTotalPrice = " ", str_vatRate = " ", str_vatAmount = " ",
                            str_hsncode = " ", str_sgst = " ", strunit = " ", str_saleprice = " ", strMrp_price = " ";

                    if (cartList.size() > j) {
                        CartItem cartItem = cartList.get(j);

                        int slNo = i * MAX_LINE + j + 1;
                        strSl_No = String.valueOf(slNo);
                        strP_Name = cartItem.getProductName();
                        strP_Arabic = cartItem.getArabicName();
                        Log.e("strP_Arabic", "" + strP_Arabic);
                        strP_Code = cartItem.getProductCode();


                        if (strP_Arabic == null || TextUtils.isEmpty(strP_Arabic.trim()) || strP_Arabic.equals("null"))
                            strP_Arabic = "  ";

                        strQty = "0/" + String.valueOf(cartItem.getTypeQuantity()); // case and piece

                        //  strQty = ""+String.valueOf(cartItem.getTypeQuantity()); // piece only

                        double netPrice = cartItem.getNetPrice() - cartItem.getProductDiscount();
//                        double mrp_price = cartItem.getMrprate();
                        double mrp_price = cartItem.getNetPrice() - cartItem.getProductDiscount();

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
                        strTotalPrice = getAmount(cartItem.getProductTotal() + cartItem.getTaxValue());

                        strunit = cartItem.getUnitselected();


                        str_hsncode = String.valueOf(cartItem.getProduct_hsncode());

                        str_saleprice = getAmount(sale_price);


                        strNetPrice = getAmount(netPrice);
                        strMrp_price = getAmount(mrp_price);


                    }


//                    if(!strP_Name.equals("")) {
//                    if (strP_Name.length() > 40) {
//                        strP_Name = getMaximumChar(strP_Name, 40);
//                    }
//                    }
//                    if(!strP_Name.equals("")) {
//
//                    if (strP_Arabic.length() > 42) {
//                        strP_Arabic = getMaximumChar(strP_Arabic, 42);
//                    }
//                    }


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
                        cell = new PdfPCell(new Phrase(strP_Name + "\n" + Chunk.NEWLINE, fontArb8));
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
                table.setWidths(new int[]{15, 4, 4}); // 5,2,2,1


                //////////////////////******   total details ****//////////////////////


//                cell = new PdfPCell(new Phrase( arabicPro.process("???????????? ????????????"), fontArb8));
                cell = new PdfPCell(new Phrase("", font10)); // Net Amount
                cell.setPaddingTop(60);//12 //22 //30 //60
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font10)); // Net Amount
                cell.setPaddingTop(60);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);


                cell = new PdfPCell(new Phrase("", font10)); // Net Amount
                cell.setPaddingTop(60);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPaddingBottom(4);
                table.addCell(cell);

                document.add(table);


                //haris added on 300921

                table = new PdfPTable(2);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{2, 2});


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingTop(50);

                cell.setPaddingLeft(4);

                // try {

                String tot = "" + getAmount(SELECTED_SALES.getWithTaxTotal());

                String vatAmnt = "" + getAmount(getTaxTotal());
                if (compNameArab.equals("") && compName.equals("")) {
                    compNameArab = "No Name";
                }
                if (compNameArab.equals("")) {
                    compNameArab = compName;
                }
                String qrBarcodeHash = QRBarcodeEncoder.encode(
                        new Seller(compNameArab),
                        new TaxNumber(str_kafeel_VatNo),
                        new InvoiceDate(strDate),
                        new InvoiceTotalAmount(tot),
                        new InvoiceTaxAmount(vatAmnt)
                );


                MultiFormatWriter writr = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writr.encode(qrBarcodeHash, BarcodeFormat.QR_CODE, 350, 350);
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

                } catch (Exception e) {

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

                paragraph = new Paragraph("" + netTotal, font8);
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

                paragraph = new Paragraph("  " + total_discount, font8);
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

                paragraph = new Paragraph("  " + st_taxable_amnt, font8);
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

                paragraph = new Paragraph("" + total_tax, font8);
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
                table.setWidths(new int[]{15, 4, 4}); // 5,2,2,1


                cell = new PdfPCell(new Phrase("" + val_in_english, font10)); // Net Amount
                cell.setPaddingTop(15);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font10)); // Net Amount
                cell.setPaddingTop(15);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);


                cell = new PdfPCell(new Phrase("  " + st_grandtot, font10)); // Net Amount
                cell.setPaddingTop(15);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);

                document.add(table);
                //setBackground(document);


                table = new PdfPTable(2); // 4
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{2, 2});// 5,2,2,1

                cell = new PdfPCell(new Phrase("  ", font10)); // Net Amount
                cell.setPaddingTop(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.LEFT | PdfPCell.TOP | PdfPCell.RIGHT);
                cell.setRowspan(5);
                table.addCell(cell);


                cell = new PdfPCell(new Phrase("  ", font10)); // Net Amount
                cell.setPaddingTop(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.LEFT | PdfPCell.TOP | PdfPCell.RIGHT);
                cell.setRowspan(5);
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        Image img;
        try {
            img = Image.getInstance(stream.toByteArray());
            img.setAbsolutePosition(0, 0);
            img.scaleToFit(595, 842);


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
                    Log.v(TAG, "Exception  printPDF   2  " + e.getMessage());
                } finally {
                    try {
                        assert input != null;
                        input.close();
                        assert output != null;
                        output.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.v(TAG, "Exception  printPDF   1 " + e.getMessage());
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {


                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

//                try {
//                    if(!flag.equals("1")) {
//                        manipulatePdf(FILE_PATH, FILE_PATH1);
//                    }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (DocumentException e) {
//                        e.printStackTrace();
//                    }
                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                callback.onLayoutFinished(pdi, true);
            }
        };


        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String printName = FILE_PATH;
        assert printManager != null;
        printManager.print(printName, pda, null);


    }

    private void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        Log.e("src", src);
        Log.e("dest", dest);
        // Log.e("check insde",""+flag);
        PdfReader reader = new PdfReader(src);
        int n = reader.getNumberOfPages();
        Log.e("pagenos", "" + n);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        Log.e("pagenos 2", "" + n);
        // text watermark
        Font f = new Font(Font.FontFamily.HELVETICA, 50);

        Phrase p = new Phrase("KENZO TECH", f);
        Log.e("pagenos 3", "" + n);
        // image watermark
//        Image img = Image.getInstance(IMG);
//        float w = img.getScaledWidth();
//        float h = img.getScaledHeight();
        // transparency
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.2f);
        // properties
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        // loop over every page
        for (int i = 1; i <= n; i++) {
            Log.e("pagenos 4", "" + n);
            pagesize = reader.getPageSizeWithRotation(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
            over = stamper.getOverContent(i);
            over.saveState();
            over.setGState(gs1);
            if (i % 2 == 1)
                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
            else {
                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
            }
            //over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
            over.restoreState();
        }
        Log.e("finished", "" + n);
        stamper.close();
        reader.close();
        File myFile = null;
        FILE_PATH2 = FILE_PATH;
        //FILE_PATH=FILE_PATH1;
        myFile = new File(FILE_PATH1);
        flag = "1";
        printPDF(myFile);  //Print PDF File


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
            Log.e(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission

            if (quotation_flag == 1) {
                Log.e("Quotation", "ok");
                // printInvoice_Quotation(getPdfModels(cartItems));
            } else {
                //  printInvoice(getPdfModels(cartItems));
            }
        }


    }

    private double roundTwoDecimals(double i) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return Double.valueOf(twoDForm.format(i));
    }

    public class MyTableEvent implements PdfPTableEvent {
        public void tableLayout(PdfPTable table, float[][] width, float[] height,
                                int headerRows, int rowStart, PdfContentByte[] canvas) {
            float widths[] = width[0];
            float x1 = widths[0];
            float x2 = widths[widths.length - 1];
            float y1 = height[0];
            float y2 = height[height.length - 1];
            PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
            cb.rectangle(x1, y1, x2 - x1, y2 - y1);
            cb.stroke();
            cb.resetRGBColorStroke();
        }
    }


}