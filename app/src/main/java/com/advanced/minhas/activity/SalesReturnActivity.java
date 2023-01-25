package com.advanced.minhas.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.fragment.InvoiceFragment;
import com.advanced.minhas.fragment.WithoutInvoiceFragment;
import com.advanced.minhas.model.Shop;

import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.config.ConfigValue.FRAGMENT_INVOICE;
import static com.advanced.minhas.config.ConfigValue.FRAGMENT_WITOUTINVOICE;

public class SalesReturnActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG = "SalesReturnActivity";

    private FrameLayout fragmentContainer;
    private ViewGroup saleTypeLayout;

    private AppCompatSpinner spinnerShopType;

    private CheckBox cbWithoutInvoice;

    // Create new fragment and transaction
    private InvoiceFragment invoiceFragment;
    private WithoutInvoiceFragment withoutInvoiceFragment;

    private TextView tvToolBarShopName;

    private ImageButton ibBack;
    ImageView iv_scanbarcode;

    private Shop SELECTED_SHOP = null;
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return);

        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        iv_scanbarcode = (ImageView)findViewById(R.id.iv_scanbarcode);
        tvToolBarShopName=(TextView) findViewById(R.id.textView_toolbar_shopNameAndCode);
        cbWithoutInvoice = (CheckBox) findViewById(R.id.checked_textView_sales_return_withoutInvoice);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_sales_return_container);

        spinnerShopType = (AppCompatSpinner) findViewById(R.id.spinner_toolbar_ShopType);
        saleTypeLayout = (ViewGroup) findViewById(R.id.viewGroup_saleType);

        iv_scanbarcode.setVisibility(View.GONE);

        try
        {
            SELECTED_SHOP = (Shop) getIntent().getSerializableExtra(SHOP_KEY);
        } catch (Exception e) {
            e.getStackTrace();
        }

        if (SELECTED_SHOP==null){
            finish();
            return;
        }

        setFragmentToContainer();

        setSaleType();

        tvToolBarShopName.setText(String.valueOf(SELECTED_SHOP.getShopName()+"\t"+SELECTED_SHOP.getShopCode()));

        tvToolBarShopName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvToolBarShopName.setSelected(true);

        cbWithoutInvoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setFragmentToContainer();
                if (isChecked) {
                    saleTypeLayout.setVisibility(View.VISIBLE);

                } else {
                    saleTypeLayout.setVisibility(View.GONE);

                }
            }
        });

        ibBack.setOnClickListener(this);
        iv_scanbarcode.setOnClickListener(this);

    }

    //    setFragment to container
    private void setFragmentToContainer() {

        invoiceFragment = new InvoiceFragment().newInstance(SELECTED_SHOP);
 //        withoutInvoiceFragment = new WithoutInvoiceFragment().newInstance(customer, spinnerShopType.getSelectedItem().toString());
        withoutInvoiceFragment = new WithoutInvoiceFragment().newInstance(SELECTED_SHOP, "Retail");

        if (!cbWithoutInvoice.isChecked()) {

            // Create new fragment and transaction

            FragmentManager fm = getSupportFragmentManager();

            FragmentTransaction ft = fm.beginTransaction();

 //         ft.remove(withoutInvoiceFragment);
            ft.replace(R.id.fragment_sales_return_container, invoiceFragment, FRAGMENT_INVOICE);
 //         ft.addToBackStack(null);
            ft.commit();

        } else {

  //  Let's first dynamically add a fragment into a frame container
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_sales_return_container, withoutInvoiceFragment, FRAGMENT_WITOUTINVOICE).
                    commit();
        }

        fragmentContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imageButton_toolbar_back:

                onBackPressed();

                break;

            case R.id.iv_scanbarcode:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);
                }else
                {
                   /* Intent intent1 = new Intent(SalesActivity.this, ScannerActivity.class);
                    startActivity(intent1);*/
                    Intent intent1 = new Intent(SalesReturnActivity.this, ScannerActivity.class);
                    startActivityForResult(intent1, REQUEST_CODE);
                }
                break;
          }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE  && resultCode  == RESULT_OK) {

                String returned_barcode = data.getStringExtra("Result");

                if (!cbWithoutInvoice.isChecked()) {

                     ((InvoiceFragment)invoiceFragment).getScannedBarcode(returned_barcode);

                }else
                    {
                     ((WithoutInvoiceFragment)withoutInvoiceFragment).getScannedBarcode(returned_barcode);
                    }
            }else
            {
                Toast.makeText(SalesReturnActivity.this, "Error scanning barcode !",
                        Toast.LENGTH_SHORT).show();
                //((SalesFragment)salesFragment).getScannedBarcode("");
            }
        } catch (Exception ex) {
            Toast.makeText(SalesReturnActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void setSaleType() {

     /*
        //spinner_background
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.shop_type, R.layout.spinner_background);

        typeAdapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerShopType.setAdapter(typeAdapter);
        spinnerShopType.setSelection(0);

        spinnerShopType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                String item = parent.getSelectedItem().toString();

                if (withoutInvoiceFragment != null)
                    withoutInvoiceFragment.changeSaleType(item);

            }
        });*/

        //spinner_background
        ArrayAdapter<CharSequence> shopTypeAdapter = ArrayAdapter.createFromResource(this, R.array.shop_type, R.layout.spinner_background);

        shopTypeAdapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerShopType.setSelection(0);

        spinnerShopType.setAdapter(shopTypeAdapter);

        spinnerShopType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getSelectedItem().toString();

                if (withoutInvoiceFragment != null)
                    withoutInvoiceFragment.changeSaleType(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
