package com.advanced.minhas.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.dialog.SaleSettingsDialog;
import com.advanced.minhas.fragment.SalesEditFragment;
import com.advanced.minhas.fragment.SalesFragment;
import com.advanced.minhas.listener.SalesettingsClickListner;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;
import com.rey.material.widget.Button;

import static com.advanced.minhas.config.ConfigKey.FRAGMENT_SALE;
import static com.advanced.minhas.config.ConfigKey.FRAGMENT_SALEEDIT;
import static com.advanced.minhas.config.ConfigKey.VAT_STATUS;
import static com.advanced.minhas.config.ConfigSales.IS_SALE_SETTINGS;
import static com.advanced.minhas.config.ConfigValue.CALLING_ACTIVITY_KEY;

import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigValue.SALES_VALUE_KEY;

public class SalesActivity extends AppCompatActivity implements View.OnClickListener {


    protected DrawerLayout drawer;
    protected RelativeLayout relativeDrawer;
    String TAG = "SalesActivity";
    LinearLayout ly_settings;
    String EXECUTIVE_ID = "";
    private ViewGroup viewSaleType;
    private AppCompatSpinner spinnerSaleType;
    private Button btnSaleList;
    private TextView tvToolBarShopName, tvShopName, tvShopType, tvShopCreditLimit, tvShopAddress, tvShopEmail, tvShopPhone, tvBalance, tvCredit, tvDrawerTitle;
    private ImageButton ibSettings;
    TextView txt_settings;
    private ImageButton ibDrawer, ibBack;
    ImageView iv_scanbarcode;
    private SessionValue sessionValue;
    public static final int REQUEST_CODE = 1;
    private FrameLayout fragmentContainer;
    private Shop SELECTED_SHOP = null;
    private Sales SELECTED_SALES = null;
    private SalesFragment salesFragment;
    private SalesEditFragment saleseditFragment;
    int CALLING_ACTIVITY =0;
    String VAT_STATU= "";
    String barcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_sales);
        sessionValue= new SessionValue(this);
        relativeDrawer = (RelativeLayout) findViewById(R.id.relativeLayout_drawer);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_sales_container);
        ibDrawer = (ImageButton) findViewById(R.id.imageButton_drawer);
        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        iv_scanbarcode = (ImageView) findViewById(R.id.iv_scanbarcode);

        viewSaleType = (ViewGroup) findViewById(R.id.viewGroup_saleType);

        spinnerSaleType = (AppCompatSpinner) findViewById(R.id.spinner_toolbar_ShopType);

        btnSaleList = (Button) findViewById(R.id.button_sales_view);

        tvDrawerTitle = (TextView) findViewById(R.id.textView_drawer_title);

        tvToolBarShopName = (TextView) findViewById(R.id.textView_toolbar_shopNameAndCode);

        tvShopName = (TextView) findViewById(R.id.textView_sales_ShopName);
        tvShopType = (TextView) findViewById(R.id.textView_sales_ShopType);
        tvShopCreditLimit = (TextView) findViewById(R.id.textView_sales_ShopCreditLimit);

        tvShopAddress = (TextView) findViewById(R.id.textView_sales_address);
        tvShopEmail = (TextView) findViewById(R.id.textView_sales_email);
        tvShopPhone = (TextView) findViewById(R.id.textView_sales_phone);

        tvCredit = (TextView) findViewById(R.id.textView_percentageBar_credit);
        tvBalance = (TextView) findViewById(R.id.textView_percentageBar_balance);

        viewSaleType.setVisibility(View.VISIBLE);
        ly_settings = findViewById(R.id.ly_settings);
        ibSettings = findViewById(R.id.textView_toolbar_sale_settings);
        txt_settings = findViewById(R.id.txt_settings);
        ly_settings.setVisibility(View.VISIBLE);

        if(IS_SALE_SETTINGS==true){
            ly_settings.setVisibility(View.VISIBLE);
        }
        else {
            ly_settings.setVisibility(View.GONE);
        }

        FragmentManager fm = getSupportFragmentManager();

        //if you added fragment via layout xml
   //     salesFragment = (SalesFragment) fm.findFragmentById(R.id.fragment_sales);

        iv_scanbarcode.setVisibility(View.VISIBLE);

        try {
            SELECTED_SHOP = (Shop) getIntent().getSerializableExtra(SHOP_KEY);
            EXECUTIVE_ID = new SessionAuth(SalesActivity.this).getExecutiveId();
            CALLING_ACTIVITY = (int) getIntent().getSerializableExtra(CALLING_ACTIVITY_KEY);
            VAT_STATU = (String) getIntent().getSerializableExtra(VAT_STATUS);

            Log.e("callingactivityyyyy",""+CALLING_ACTIVITY);
        } catch (Exception e) {
            e.getStackTrace();
        }

        try{
            SELECTED_SALES = (Sales) (getIntent().getSerializableExtra(SALES_VALUE_KEY));

        }catch (Exception e){

        }
        if (SELECTED_SHOP == null) {
            finish();
            return;
        }

        setDrawerLayout();
        setFragmentToContainer();
        setShopView(SELECTED_SHOP);

        setShopTypeSpinner();

        ibDrawer.setOnClickListener(this);

        btnSaleList.setOnClickListener(this);

        tvDrawerTitle.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        iv_scanbarcode.setOnClickListener(this);

        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked","ok");


                final SaleSettingsDialog salesettingsDialog = new SaleSettingsDialog(SalesActivity.this, new SalesettingsClickListner() {
                    @Override
                    public void salesettingsclick(String vat_status) {
                        Log.e("vat_status",""+vat_status);
                        try {


                            if (salesFragment != null)
                                salesFragment.change_vat_status(vat_status);
                        }catch (Exception e){

                        }

                    }
                });

                salesettingsDialog.show();
            }
        });


    }

    private void setFragmentToContainer() {
        salesFragment = new SalesFragment().newInstance(SELECTED_SHOP ,VAT_STATU);

        saleseditFragment = new SalesEditFragment().newInstance(SELECTED_SHOP,SELECTED_SALES);
        if(CALLING_ACTIVITY==1004){
            Log.e("if","1004");

            // Create new fragment and transaction

            FragmentManager fm = getSupportFragmentManager();

            FragmentTransaction ft = fm.beginTransaction();

            //         ft.remove(withoutInvoiceFragment);
            ft.replace(R.id.fragment_sales_container, saleseditFragment, FRAGMENT_SALEEDIT);
            //         ft.addToBackStack(null);
            ft.commit();

        } else {
            Log.e("else","00");
            //  Let's first dynamically add a fragment into a frame container
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_sales_container, salesFragment, FRAGMENT_SALE).
                    commit();
        }

        fragmentContainer.setVisibility(View.VISIBLE);

    }

    //nav Drawer layout
    private void setDrawerLayout() {
        /*
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            */
        /** Called when a drawer has settled in a completely closed state. *//*

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here
            }
        };
        // Set the drawer toggle as the DrawerListener
                drawer.addDrawerListener(mDrawerToggle);
        */

        int width = getResources().getDisplayMetrics().widthPixels / 2;
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) relativeDrawer.getLayoutParams();
        params.width = width;
        relativeDrawer.setLayoutParams(params);

    }


    private void setShopTypeSpinner() {

        //spinner_background
        ArrayAdapter<CharSequence> shopTypeAdapter = ArrayAdapter.createFromResource(this, R.array.shop_type, R.layout.spinner_background);

        shopTypeAdapter.setDropDownViewResource(R.layout.spinner_list);

        tvShopType.setText(shopTypeAdapter.getItem(0));

        spinnerSaleType.setAdapter(shopTypeAdapter);

        spinnerSaleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final String item = parent.getSelectedItem().toString();
                tvShopType.setText(item);

                changeSaleType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       /* //spinner_background
        ArrayAdapter<CharSequence> shopTypeAdapter = ArrayAdapter.createFromResource(this, R.array.shop_type, R.layout.spinner_background);

        shopTypeAdapter.setDropDownViewResource(R.layout.spinner_list);

        tvShopType.setText(shopTypeAdapter.getItem(0));

        spinnerSaleType.setAdapter(shopTypeAdapter);

        spinnerSaleType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {

                final String item = parent.getSelectedItem().toString();
                tvShopType.setText(item);

                changeSaleType();
            }
        });*/
    }

    //    delivered report graph (custom view)
    public void setBalanceGraph(float total, float balanced) {

        float percentage = 0.0f;

//        get Percentage  payment in TotalAmount
        percentage = ((balanced * 100) / total);

        /**
         Dividing  percentage value in 100
         * this value use view(Custom Graph Layout) width
         */
        percentage = percentage / 100;

        View view = findViewById(R.id.view_child_balance);

        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) view.getLayoutParams();
// This will currently return null, if it was not constructed from XML.
        PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
        info.widthPercent = percentage;
        view.requestLayout();

        tvBalance.setText(String.valueOf(balanced));

    }

    //    delivered report graph (custom view)
        public void setCreditGraph(float total, float credit) {

        float percentage = 0.0f;

    //  get Percentage  payment in TotalAmount
        percentage = ((credit * 100) / total);

        /**
         Dividing  percentage value in 100
         * this value use view(Custom Graph Layout) width
         */
        percentage = percentage / 100;

        View view = findViewById(R.id.view_child_credit);
        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) view.getLayoutParams();
// This will currently return null, if it was not constructed from XML.
        PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
        info.widthPercent = percentage;
        view.requestLayout();

        tvCredit.setText(String.valueOf(credit));

    }

    //    shops
    private void setShopView(Shop shop) {
        sessionValue.storeShopid( ""+shop.getShopId());

        tvToolBarShopName.setText(String.valueOf(SELECTED_SHOP.getShopName() + "\t" + SELECTED_SHOP.getShopCode()));
        tvToolBarShopName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvToolBarShopName.setSelected(true);

        salesFragment.initSelectedCustomer(shop);

        tvShopName.setText(shop.getShopName());
        tvShopCreditLimit.setText("");

        tvShopEmail.setText(shop.getShopMail());
        tvShopAddress.setText(shop.getShopAddress());
        tvShopPhone.setText(shop.getShopMobile());

        setCreditGraph(shop.getCredit() + shop.getDebit(), shop.getCredit());
        setBalanceGraph(shop.getCredit() + shop.getDebit(), shop.getDebit());

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageButton_drawer:
                if (drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
                else
                    drawer.openDrawer(GravityCompat.START);

                break;
            case R.id.textView_drawer_title:

                if (drawer.isDrawerOpen(Gravity.START))
                    drawer.closeDrawer(GravityCompat.START);
                break;


            case R.id.imageButton_toolbar_back:
                onBackPressed();
                break;

            case R.id.iv_scanbarcode:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
                } else {
                   /* Intent intent1 = new Intent(SalesActivity.this, ScannerActivity.class);
                    startActivity(intent1);*/
                    Intent intent1 = new Intent(SalesActivity.this, ScannerActivity.class);
                    startActivityForResult(intent1, REQUEST_CODE);

                }
                break;

            case R.id.button_sales_view:

                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra(SHOP_KEY, SELECTED_SHOP);
                startActivity(intent);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

                String returned_barcode = data.getStringExtra("Result");

              //  ((SalesFragment) salesFragment).getScannedBarcode(returned_barcode);

                //Toast.makeText(getApplicationContext(), ": "+returned_barcode, Toast.LENGTH_LONG).show();
            } else {

                //((SalesFragment) salesFragment).getScannedBarcode("");
            }
        } catch (Exception ex) {
            Toast.makeText(SalesActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void changeSaleType() {
        try {

            if (salesFragment != null)
                salesFragment.changeSaleType(spinnerSaleType.getSelectedItem().toString());
        }catch (Exception e){

        }

        try {

            if (saleseditFragment != null)
                saleseditFragment.changeSaleType(spinnerSaleType.getSelectedItem().toString());
        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

/*    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //Add more code...

        Toast.makeText(getApplicationContext(), "Scanned : clicked", Toast.LENGTH_SHORT).show();

        if (event.getCharacters() != null && !event.getCharacters().isEmpty()){

            Toast.makeText(getApplicationContext(), "Scanned : "+event.getCharacters(), Toast.LENGTH_SHORT).show();
        }
        return super.dispatchKeyEvent(event);
    }*/


    /*@Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if(e.getAction()==KeyEvent.ACTION_DOWN
                && e.getKeyCode() != KeyEvent.KEYCODE_ENTER){ //Not Adding ENTER_KEY to barcode String
            char pressedKey = (char) e.getUnicodeChar();
            barcode += pressedKey;
        }
        if (e.getAction()==KeyEvent.ACTION_DOWN
                && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            Log.i(TAG,"Barcode Read: "+barcode);
          //  barcodeLookup(barcode);// or Any method handling the data
            barcode="";
        }
        return false;
    }*/


       /* public interface ActivityConstants {
          public static final int ACTIVITY_SALES = 1001; //call Sales Activity
          public static final int ACTIVITY_QUOTATION = 1002; //call quotation Activity
        }*/

}