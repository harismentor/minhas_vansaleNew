package com.advanced.minhas.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.DailyReport;
import com.advanced.minhas.model.PdfModel;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.model.print.FooterDetailsPrintModel;
import com.advanced.minhas.model.print.PrintHeadsModel;
import com.advanced.minhas.printerconnect.printutil.Sample_Print;
import com.advanced.minhas.session.SessionValue;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

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

import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;
import static com.advanced.minhas.config.ConfigSales.IS_POS_PRINT_80mm;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;
import static com.advanced.minhas.config.ConfigValue.RECEIPT_KEY;
import static com.advanced.minhas.config.ConfigValue.SHOP_VALUE_KEY;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.getPrintDate;
import static com.advanced.minhas.config.Generic.stringToDate;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_1;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_TAXCARD;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_VAT;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_ID;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_QRCODE_LINK;

public class Dailysummary_Preview extends AppCompatActivity implements View.OnClickListener {
    String TAG = "Dailysummary_Preview";
    String strShopName = "", strShopNameArabic = "", strCustomerVat = "", strCustomerNo = "", strBillNumber = "",
            strShopLocation = "", strDate = "", justifiedVatNumbernew = "", cust_outstanding = "",
            new_Outstanding = "", str_Previous_balance = "", CURRENCY = "", st_sizelist_stock = "", str_paymenttype = "",
            str_kafeel_VatNo = "", str_qrcodelink = "", str_companytaxcard = " ", net = "", strReceiptNo = "", flag = "",st_vatrate ="";
    double after_disc = 0, disc_percent = 0, net_Amount = 0;
    int callingActivity = 0, quotation_flag = 0;
    private Shop SELECTED_SHOPE = null;

    private Receipt receipt = null;
    private RecyclerView recyclerView;
    private Button btnPrint, btnHome, btnConnect, btnEnable, btnbackground_print;
    private TextView tvTitle;
    private TextView tvOutstandingBalance,tvCurrentBalance, tvBalancePaid, tvReceiptNo, tvDate, tvShopDetails;
    TextView txt_cash_sale,txt_credit_sale,txt_return_sale,txt_cash_collection,txt_bank_collection,txt_cheque_collection ,txt_cashinhand,
            txt_return_cash;
    private double paid_amount = 0;
    float fl_discount = 0;
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
    private final int MAX_LINE = 22;//17
    private final int MAX_LINE1 = 25;
    private final int MAX_LINE_QT = 30;
    private final int MAX_POS_DIGIT = 42;
    MyDatabase myDatabase;
    DailyReport dailyReport;
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
    private Dailysummary_Preview.CheckTypesTask BTtask;
    private Dailysummary_Preview.ExcuteDisconnectBT BTdiscon;
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
                BTtask = new Dailysummary_Preview.CheckTypesTask();
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
                            BTtask = new Dailysummary_Preview.CheckTypesTask();
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
                //Toast.makeText(mainView, "블루투스 기기 검색 시작", Toast.LENGTH_SHORT).show();
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

        ProgressDialog asyncDialog1 = new ProgressDialog(Dailysummary_Preview.this);

        @Override
        protected void onPreExecute() {
            asyncDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog1.setMessage("Searching the Printer...");
            asyncDialog1.setCancelable(false);
            asyncDialog1.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            searchflags = true;
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    });
            asyncDialog1.show();
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
            if (asyncDialog1.isShowing())
                asyncDialog1.dismiss();

            searchflags = false;


            addPairedDevices();
            super.onPostExecute(result);
        }

        ;
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        new Dailysummary_Preview.connBT().execute(btDev);
    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(Dailysummary_Preview.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(Dailysummary_Preview.this);

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
        BTdiscon = new Dailysummary_Preview.ExcuteDisconnectBT();
        BTdiscon.execute();
    }


    private class ExcuteDisconnectBT extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(Dailysummary_Preview.this);

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
        setContentView(R.layout.activity_dailysummary_preview);

        loadSettingFile();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_preview);

        btnPrint = (Button) findViewById(R.id.button_preview_printsummary);
        btnHome = (Button) findViewById(R.id.button_preview_home);
        btnConnect = (Button) findViewById(R.id.button_preview_connect);
        btnbackground_print = findViewById(R.id.button_background_print);

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
        sessionValue = new SessionValue(Dailysummary_Preview.this);

        txt_cash_sale =  findViewById(R.id.txt_cash_sale);
        txt_credit_sale =  findViewById(R.id.txt_credit_sale);
        txt_return_sale =  findViewById(R.id.txt_return_sale);
        txt_cash_collection =  findViewById(R.id.txt_cash_collection);
        txt_bank_collection =  findViewById(R.id.txt_bank_collection);
        txt_cheque_collection =  findViewById(R.id.txt_cheque_collection);
        txt_cashinhand = findViewById(R.id.txt_cashinhand);
        txt_return_cash = findViewById(R.id.txt_return_cash);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);
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

        sessionValue = new SessionValue(Dailysummary_Preview.this);

        myDatabase = new MyDatabase(this);


        CURRENCY = "" + sessionValue.getControllSettings().get(PREF_CURRENCY);

        if (sessionValue.isPOSPrint()) {
            connectionView.setVisibility(View.VISIBLE);
            Init_BluetoothSet();
            sample = new Sample_Print(Dailysummary_Preview.this);

            btnPrint.setText("Print POS");

        } else {
            connectionView.setVisibility(View.GONE);
            btnPrint.setEnabled(true);
            btnPrint.setText("Print PDF");

        }

        /******/
        dailyReport=myDatabase.getDaySummaryReport();
        setDayReport();
        try {
            SELECTED_SHOPE = (Shop) getIntent().getSerializableExtra(SHOP_VALUE_KEY);

            //SELECTED_SALES = (Sales) (getIntent().getSerializableExtra(SALES_VALUE_KEY));
            receipt = (Receipt) getIntent().getSerializableExtra(RECEIPT_KEY);
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

//                if (SELECTED_SALES == null)
//                    finish();
            try {
                // final Date date = stringToDate(SELECTED_SALES.getDate());
                //  final Date date ="01-01-2022";
//            String st_date = ""+date;
//            String output = st_date.substring(
//
//            0, 10);  // Output : 2012/01/20

//            strDate = output;

                Date d = new Date();
                String dateWithoutTime = d.toString().substring(0, 10);

                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                Date todayDate = new Date();
                // String thisDate = currentDate.format(date);
                // strDate = thisDate;
            } catch (Exception e) {

            }
            if (isStoragePermissionGranted()) {

            }


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

//                            if (SELECTED_SHOPE.getShopCode() != null && !TextUtils.isEmpty(SELECTED_SHOPE.getShopCode()))
//                                strCustomerCode = SELECTED_SHOPE.getShopCode();


                        strShopLocation = SELECTED_SHOPE.getShopAddress();
                        receipt = (Receipt) getIntent().getSerializableExtra(RECEIPT_KEY);

                        tvOutstandingBalance.setText(String.valueOf("Outstanding Balance\n" + getAmount(receipt.getCurrentBalanceAmount()+receipt.getReceivedAmount()) + " " + CURRENCY));
                        tvBalancePaid.setText(String.valueOf("Paid\n" + getAmount(receipt.getReceivedAmount()) + " " + CURRENCY));
                        tvCurrentBalance.setText(String.valueOf("Balance\n" + getAmount(receipt.getCurrentBalanceAmount()) + " " + CURRENCY));

                        strReceiptNo=receipt.getReceiptNo();



                    } catch (NullPointerException e) {
                        Log.d(TAG, "exception   " + e.getMessage());
                    }
                    break;
                case ActivityConstants.ACTIVITY_OUTSTANDING_RECEIPT:
                    tvTitle.setText("RECEIPT VOUCHER");


                    SELECTED_SHOPE = (Shop) getIntent().getSerializableExtra(SHOP_VALUE_KEY);
                    strShopName = SELECTED_SHOPE.getShopName();
                    strShopLocation = SELECTED_SHOPE.getShopAddress();
                    receipt = (Receipt) getIntent().getSerializableExtra(RECEIPT_KEY);



                    tvOutstandingBalance.setText(String.valueOf("Outstanding Balance\n" + getAmount(receipt.getCurrentBalanceAmount()+receipt.getReceivedAmount()) + " " + CURRENCY));
                    tvBalancePaid.setText(String.valueOf("Paid\n" + getAmount(receipt.getReceivedAmount()) + " " + CURRENCY));
                    tvCurrentBalance.setText(String.valueOf("Balance\n" + getAmount(receipt.getCurrentBalanceAmount()) + " " + CURRENCY));

                    strReceiptNo=receipt.getReceiptNo();
                    break;

                default:

            }
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException exception   " + e.getMessage());
        }



//        String companyVatString = sessionValue.getCompanyDetails().get(PREF_COMPANY_VAT);
       // String companyVatString = SELECTED_SHOPE.getVatNumber();
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

    }

    public double getNetTotal() {

        double netTotal = 0;

        for (CartItem cartItem : cartItems) {

            if (cartItem.getProductPrice() != 0.0) {


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
                    // double f = c.getTaxValue() * c.getPieceQuantity_nw();
                    double f = c.getTaxValue();
                    totalTax += f;
                }
            }
        }
        return totalTax;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.button_preview_printsummary:
                if (sessionValue.isPOSPrint())
                    try {

                        //printInvoice(cartItems);
                        if(IS_POS_PRINT_80mm==true) {
                            printPos_80mm();
                        }
                        else{
                            print_pos_58mm();
                        }


                    } catch (Exception e) {

                    }
                else {


                    if (isStoragePermissionGranted()) {
                        Log.e("Quotation", "else" + ActivityConstants.ACTIVITY_QUOTATION);

                        //printwithbackgroundInvoice(getPdfModels(cartItems));
                    }
                    // printInvoice(getPdfModels(cartItems));
                    //}

                }


                break;

//            case R.id.button_background_print:
//                if (isStoragePermissionGranted())
//                    //   printInvoice(getPdfModels(cartItems));
//
//
//                    break;


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

    private void print_pos_58mm() {

        try{
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.icresp_newlogo);



        FooterDetailsPrintModel footerDetailsPrintModel = new FooterDetailsPrintModel();
        //String thanksMessage = "Thank you for shopping @" + sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME).toUpperCase(Locale.ROOT);  //name get from session
        String thanksMessage = "!! Thank you !!";  //name get from session

        footerDetailsPrintModel.setThanksMessage(thanksMessage);

        String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
        String addressContentStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
        String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
        String execId = "Code      : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
        String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
        String routeMob = "Route Mobile No : " + sessionValue.getRegisteredMobile();  //route mob get from session

        PrintHeadsModel prnthead = new PrintHeadsModel();
        prnthead.setCompany_name(compName);
        prnthead.setCompany_address(addressContentStr);
        prnthead.setExec_name(execName);
        prnthead.setExec_mob(execMob);
        prnthead.setExec_id(execId);
        prnthead.setRoute_mob(routeMob);


        sample.Print_dailysummary_58mm(dailyReport,prnthead);

    } catch (NullPointerException | InterruptedException e) {
        Toast.makeText(this, "wrong print Request", Toast.LENGTH_SHORT).show();
    }

    }


    private void printPos_80mm() {
        {
            sample = new Sample_Print(Dailysummary_Preview.this);

            try {


                /***************Company Details******************/

                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.icresp_newlogo);



                FooterDetailsPrintModel footerDetailsPrintModel = new FooterDetailsPrintModel();
                //String thanksMessage = "Thank you for shopping @" + sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME).toUpperCase(Locale.ROOT);  //name get from session
                String thanksMessage = "!! Thank you !!";  //name get from session

                footerDetailsPrintModel.setThanksMessage(thanksMessage);

                String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
                String addressContentStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
                String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
                String execId = "Code      : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
                String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
                String routeMob = "Route Mobile No : " + sessionValue.getRegisteredMobile();  //route mob get from session

                PrintHeadsModel prnthead = new PrintHeadsModel();
                prnthead.setCompany_name(compName);
                prnthead.setExec_name(execName);
                prnthead.setExec_mob(execMob);
                prnthead.setExec_id(execId);
                prnthead.setCompany_address(addressContentStr);
                prnthead.setRoute_mob(routeMob);


                sample.Print_dailysummary_80mm(dailyReport,prnthead);
                //sample.Print_Receipt_80mm(receipt,strShopName);


            } catch (NullPointerException | InterruptedException e) {
                Toast.makeText(this, "wrong print Request", Toast.LENGTH_SHORT).show();
            }
        }

    }

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
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback callback, Bundle extras) {


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
    public void setDayReport(){
        String st_cash_return ="0.00";
        try{
             st_cash_return = myDatabase.get_salereturn_cash();
        }catch (Exception e){

        }
        try {

            double cash_sale_tot = TextUtils.isEmpty(dailyReport.getTotalCashSale()) ? 0 : Double.valueOf(dailyReport.getTotalCashSale());
            double cash_cash_collctn_tot = TextUtils.isEmpty(dailyReport.getTotalCashCollection()) ? 0 : Double.valueOf(dailyReport.getTotalCashCollection());

            txt_cash_sale.setText(getAmount((TextUtils.isEmpty("" + dailyReport.getTotalCashSale()) ? 0 : Float.parseFloat("" + dailyReport.getTotalCashSale()))));
            //  txt_cash_sale.setText(getAmount(Double.parseDouble(dailyReport.getTotalCashSale())));
            txt_credit_sale.setText(getAmount(Double.parseDouble(dailyReport.getTotalCreditSale())));
            txt_return_sale.setText(getAmount(Double.parseDouble(dailyReport.getTotalReturnSale())));
            txt_cash_collection.setText(getAmount(Double.parseDouble(dailyReport.getTotalCashCollection())));
            txt_bank_collection.setText(getAmount(Double.parseDouble(dailyReport.getTotalBankCollection())));
            txt_cheque_collection.setText(getAmount(Double.parseDouble(dailyReport.getTotalChequeCollection())));
            txt_cashinhand.setText(getAmount(Double.parseDouble(getAmount(cash_sale_tot+cash_cash_collctn_tot))));
            txt_return_cash.setText(getAmount(Double.parseDouble(st_cash_return)));
        }catch (Exception e){

        }
    }

}