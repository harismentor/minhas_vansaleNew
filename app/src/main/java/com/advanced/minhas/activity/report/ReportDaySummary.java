package com.advanced.minhas.activity.report;




import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;
import static com.advanced.minhas.config.ConfigSales.IS_POS_PRINT_80mm;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_ADDRESS_1;
import static com.advanced.minhas.session.SessionValue.PREF_COMPANY_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_ID;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_MOBILE;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_NAME;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.advanced.minhas.R;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.DailyReport;
import com.advanced.minhas.model.print.PrintHeadsModel;
import com.advanced.minhas.printerconnect.connecter.P25ConnectionException;
import com.advanced.minhas.printerconnect.connecter.P25Connector;
import com.advanced.minhas.printerconnect.pockdata.PocketPos;
import com.advanced.minhas.printerconnect.printutil.DataConstants;
import com.advanced.minhas.printerconnect.printutil.DateUtil;
import com.advanced.minhas.printerconnect.printutil.FontDefine;
import com.advanced.minhas.printerconnect.printutil.Printer;
import com.advanced.minhas.printerconnect.printutil.Sample_Print;
import com.advanced.minhas.printerconnect.printutil.StringUtil;
import com.advanced.minhas.printerconnect.printutil.Util;
import com.advanced.minhas.session.SessionValue;
import com.rey.material.widget.Button;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class ReportDaySummary extends AppCompatActivity implements View.OnClickListener {

    String TAG = "ReportDaySummary";

    String CURRENCY="";
    private Button  btnPrint, btnHome, btnConnect, btnEnable;
    Sample_Print sample;
    private AppCompatSpinner spinnerDevice;
    private double paid_amount = 0;
    private DailyReport DAILY_REPORT = null;
    private ViewGroup connectionView;

    private SessionValue sessionValue;
    private ArrayList<CartItem> cartItems = null;



    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;

    private BluetoothAdapter mBluetoothAdapter;
    private P25Connector mConnector;

    MyDatabase myDatabase;
    DailyReport dailyReport;
    private ImageButton ibBack;
    private LinearLayout lyt_share;
    TextView txt_cash_sale,txt_credit_sale,txt_return_sale,txt_cash_collection,txt_bank_collection,
            txt_cheque_collection ,txt_return_cash;


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
    int callingActivity = 0 , quotation_flag =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_day_summary);

        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        lyt_share =  findViewById(R.id.lyt_share);
        txt_cash_sale =  findViewById(R.id.txt_cash_sale);
        txt_credit_sale =  findViewById(R.id.txt_credit_sale);
        txt_return_sale =  findViewById(R.id.txt_return_sale);
        txt_cash_collection =  findViewById(R.id.txt_cash_collection);
        txt_bank_collection =  findViewById(R.id.txt_bank_collection);
        txt_cheque_collection =  findViewById(R.id.txt_cheque_collection);
        txt_return_cash =  findViewById(R.id.txt_return_cash);
        myDatabase = new MyDatabase(ReportDaySummary.this);
        dailyReport=myDatabase.getDaySummaryReport();
        sample = new Sample_Print(ReportDaySummary.this);
        lyt_share.setVisibility(View.GONE);
        ibBack.setOnClickListener(this);
        lyt_share.setOnClickListener(this);

        setDayReport();



        btnPrint = (Button) findViewById(R.id.button_preview_print);
        btnHome = (Button) findViewById(R.id.button_preview_home);
        btnConnect = (Button) findViewById(R.id.button_preview_connect);

        btnEnable = (Button) findViewById(R.id.button_preview_connection_enabled);


        spinnerDevice = (AppCompatSpinner) findViewById(R.id.spinner_preview_devices);
        connectionView=(ViewGroup)findViewById(R.id.connectionView);

        sessionValue =new SessionValue(ReportDaySummary.this);
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

        }
        else {
            connectionView.setVisibility(View.GONE);
            btnPrint.setEnabled(false);
            //btnPrint.setText("Print PDF");

        }







        btnPrint.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnEnable.setOnClickListener(this);

    }

    /****/

    public void setDayReport(){
      try {

          txt_cash_sale.setText(getAmount((TextUtils.isEmpty("" + dailyReport.getTotalCashSale()) ? 0 : Float.parseFloat("" + dailyReport.getTotalCashSale()))));
          //  txt_cash_sale.setText(getAmount(Double.parseDouble(dailyReport.getTotalCashSale())));
          txt_credit_sale.setText(getAmount(Double.parseDouble(dailyReport.getTotalCreditSale())));
          txt_return_sale.setText(getAmount(Double.parseDouble(dailyReport.getTotalReturnSale())));
          txt_cash_collection.setText(getAmount(Double.parseDouble(dailyReport.getTotalCashCollection())));
          txt_bank_collection.setText(getAmount(Double.parseDouble(dailyReport.getTotalBankCollection())));
          txt_cheque_collection.setText(getAmount(Double.parseDouble(dailyReport.getTotalChequeCollection())));
      }catch (Exception e){
          
      }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageButton_toolbar_back:

                onBackPressed();
                break;

            case R.id.button_preview_print:

                    printInvoice();
                //printDemoContent();


                break;

            case R.id.button_background_print :
                if (isStoragePermissionGranted())
                    //   printInvoice(getPdfModels(cartItems));


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

    private void printInvoice() {
        if(IS_POS_PRINT_80mm==true){

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
            prnthead.setRoute_mob(routeMob);

            DailyReport dly = new DailyReport();
            dly.setCustomer(dailyReport.getCustomer());
            dly.setTotalBankCollection(dailyReport.getTotalBankCollection());
            dly.setTotalCashCollection(dailyReport.getTotalCashCollection());
            dly.setTotalCashSale(dailyReport.getTotalCashSale());
            dly.setTotalChequeCollection(dailyReport.getTotalChequeCollection());
            dly.setTotalCreditSale(dailyReport.getTotalCreditSale());
            dly.setTotalReturnSale(dailyReport.getTotalReturnSale());


        //    try {
              //  sample.Print_dailysummary_80mm(dailyReport,prnthead);
//                Intent intent = new Intent(ReportDaySummary.this, Preview_Receipt.class);
//
//                intent.putExtra(CALLING_ACTIVITY_KEY, ActivityConstants.ACTIVITY_OUTSTANDING_RECEIPT);
//
//                intent.putExtra(SHOP_DAILYREPORT, dly);
//                intent.putExtra(PRINT_HEAD,  prnthead);
//                startActivity(intent);
//                finish();

//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        else {

            try {


            /*PrintBill sample = new PrintBill(ReportDaySummary.this);
            try {
                sample.sample2();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/


                String bilType = "DAY SUMMARY REPORT";


                String nextLine = "\n";


                String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
                String addressContentStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session

                String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
                String execId = "Code      : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
                String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session

                String routeMob = "Route Mobile No : " + sessionValue.getRegisteredMobile();  //route mob get from session

                String line = "-----------------------------------------";

                StringBuilder contentExecutive = new StringBuilder();

                String dailyReportData =
                        nextLine
                                + "Total Cash Sale          :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyReport.getTotalCashSale())))
                                + nextLine
                                + "Total Credit Sale        :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyReport.getTotalCreditSale())))
                                + nextLine
                                + "Total Return             :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyReport.getTotalReturnSale())))
                                + nextLine
                                + "Total Cash Collection    :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyReport.getTotalCashCollection())))
                                + nextLine
                                + "Total Bank Collection    :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyReport.getTotalBankCollection())))
                                + nextLine
                                + "Total Cheque Collection  :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyReport.getTotalChequeCollection())))
                                + nextLine
                                + "Total Collection  :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyReport.getTotalChequeCollection())));//total_cash_sale + total_collection

                contentExecutive.append(nextLine).append(nextLine).
                        append(Util.center(compName, 43)).
                        append(nextLine).
                        append(Util.center(addressContentStr, 43)).
                        append(line).

                        append(nextLine).
                        append(execName).
                        append(nextLine).
                        append(execId).
                        append(nextLine).
                        append(execMob).
                        append(nextLine).
                        append(routeMob).
                        append(nextLine).
                        append(line).
                        append(nextLine).
                        append(Util.center("Day Summary Report", 43)).
                        append(nextLine).
                        append(nextLine).append(dailyReportData).append(line).append(nextLine).append(nextLine);




           /* byte[] nextLineByte = Printer.printfont(nextLine, FontDefine.FONT_32PX, FontDefine.Align_LEFT,
                    (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);*/

                byte[] titleNameByte = Printer.printfont(compName, FontDefine.FONT_32PX, FontDefine.Align_CENTER,
                        (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

                byte[] titleContentByte = Printer.printfont(addressContentStr, FontDefine.FONT_24PX, FontDefine.Align_CENTER,
                        (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


                byte[] contentExecutiveDetailsByte = Printer.printfont(contentExecutive.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                        (byte) 0x2A, PocketPos.LANGUAGE_ENGLISH);

                byte[] contentDailyReportByte = Printer.printfont(dailyReportData, FontDefine.FONT_24PX, FontDefine.Align_LEFT,
                        (byte) 0x3A, PocketPos.LANGUAGE_ENGLISH);


                byte[] lineByte = Printer.printfont(line, FontDefine.FONT_24PX, FontDefine.Align_CENTER,
                        (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


                byte[] typeByte = Printer.printfont(bilType, FontDefine.FONT_24PX_UNDERLINE, FontDefine.Align_CENTER,
                        (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);


                byte[] totalByte = new byte[titleNameByte.length
                        + titleContentByte.length
                        + contentExecutiveDetailsByte.length
                        + typeByte.length
                        + contentDailyReportByte.length
                        + lineByte.length
                        ];


                int offset = 0;


                System.arraycopy(contentExecutiveDetailsByte, 0, totalByte, offset, contentExecutiveDetailsByte.length);


                byte[] sendData = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);

                sendData(sendData);

            } catch (NullPointerException e) {
                Toast.makeText(this, "wrong print Request", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void printDemoContent(){

        /*********** print head*******/
        String receiptHead = "************************"
                + "   P25/M Test Print"+"\n"
                + "************************"
                + "\n";

        long milis		= System.currentTimeMillis();

        String date		= DateUtil.timeMilisToString(milis, "MMM dd, yyyy");
        String time		= DateUtil.timeMilisToString(milis, "hh:mm a");

        String hwDevice	= Build.MANUFACTURER;
        String hwModel	= Build.MODEL;
        String osVer	= Build.VERSION.RELEASE;
        String sdkVer	= String.valueOf(Build.VERSION.SDK_INT);

        StringBuffer receiptHeadBuffer = new StringBuffer(100);

        receiptHeadBuffer.append(receiptHead);
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify(date, time, DataConstants.RECEIPT_WIDTH) + "\n");

        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("Device:", hwDevice, DataConstants.RECEIPT_WIDTH) + "\n");

        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("Model:",  hwModel, DataConstants.RECEIPT_WIDTH) + "\n");
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("OS ver:", osVer, DataConstants.RECEIPT_WIDTH) + "\n");
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("SDK:", sdkVer, DataConstants.RECEIPT_WIDTH));
        receiptHead = receiptHeadBuffer.toString();

        byte[] header = Printer.printfont(receiptHead + "\n", FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);


        /*********** print English text*******/
        StringBuffer sb = new StringBuffer();
        for(int i=1; i<128; i++)
            sb.append((char)i);
        String content = sb.toString().trim();

        byte[] englishchartext24 			= Printer.printfont(content + "\n",FontDefine.FONT_24PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] englishchartext32			= Printer.printfont(content + "\n",FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] englishchartext24underline	= Printer.printfont(content + "\n",FontDefine.FONT_24PX_UNDERLINE,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);

        //2D Bar Code
        byte[] barcode = StringUtil.hexStringToBytes("1d 6b 02 0d 36 39 30 31 32 33 34 35 36 37 38 39 32");


        /*********** print Tail*******/
        String receiptTail =  "Test Completed" + "\n"
                + "************************" + "\n";

        String receiptWeb =  "** www.londatiga.net ** " + "\n\n\n";

        byte[] foot = Printer.printfont(receiptTail,FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] web	= Printer.printfont(receiptWeb,FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);

        byte[] totladata =  new byte[header.length + englishchartext24.length + englishchartext32.length + englishchartext24underline.length +
                + barcode.length
                + foot.length + web.length
                ];
        int offset = 0;
        System.arraycopy(header, 0, totladata, offset, header.length);
        offset += header.length;

        System.arraycopy(englishchartext24, 0, totladata, offset, englishchartext24.length);
        offset+= englishchartext24.length;

        System.arraycopy(englishchartext32, 0, totladata, offset, englishchartext32.length);
        offset+=englishchartext32.length;

        System.arraycopy(englishchartext24underline, 0, totladata, offset, englishchartext24underline.length);
        offset+=englishchartext24underline.length;

        System.arraycopy(barcode, 0, totladata, offset, barcode.length);
        offset+=barcode.length;

        System.arraycopy(foot, 0, totladata, offset, foot.length);
        offset+=foot.length;

        System.arraycopy(web, 0, totladata, offset, web.length);
        offset+=web.length;

        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totladata, 0, totladata.length);

        sendData(senddata);
    }


    private void printText(String text) {
        byte[] line 	= Printer.printfont(text + "\n\n", FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A,
                PocketPos.LANGUAGE_ENGLISH);
        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, line, 0, line.length);

        sendData(senddata);
    }



    public String rightAlignedNumbers(String data){

        if(data.length()==1){
            data="               "+data;
        }
        if(data.length()==2){
            data="              "+data;
        }
        if(data.length()==3){
            data="             "+data;
        }
        if(data.length()==4){
            data="            "+data;
        }
        if(data.length()==5){
            data="           "+data;
        }
        if(data.length()==6){
            data="          "+data;
        }
        if(data.length()==7){
            data="         "+data;
        }
        if(data.length()==8){
            data="        "+data;
        }
        if(data.length()==9){
            data="       "+data;
        }
        if(data.length()==10){
            data="      "+data;
        }
        return  data;
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

            if(quotation_flag == 1){
                Log.e("Quotation","ok");
                // printInvoice_Quotation(getPdfModels(cartItems));
            }
            else {
                //  printInvoice(getPdfModels(cartItems));
            }
        }


    }

}