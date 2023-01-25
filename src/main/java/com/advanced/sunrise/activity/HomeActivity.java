package com.advanced.minhas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.advanced.minhas.R;
import com.advanced.minhas.fragment.HomeDashBoardLeftFragment;
import com.advanced.minhas.fragment.ShopDashBoardFragment;
import com.advanced.minhas.fragment.ShopDashBoardSupplier;
import com.advanced.minhas.fragment.ShopFragment;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.listener.ShopClickListener;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.session.SessionValue;

import static com.advanced.minhas.config.ConfigKey.FRAGMENT_DASHBOARD_LEFT;
import static com.advanced.minhas.config.ConfigKey.FRAGMENT_SHOPFRAGMENT;
import static com.advanced.minhas.config.ConfigKey.FRAGMENT_SHOP_DASHBOARD;
import static com.advanced.minhas.config.PrintConsole.printLog;

public class HomeActivity extends AppCompatActivity implements  OnNotifyListener,ShopClickListener {

    String TAG = "HomeActivity";

    BottomSheetBehavior mBottomBehaviour;

    private SessionValue sessionValue;
    RelativeLayout rl_bottom_fragment;
    private SessionAuth sessionAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

         this.sessionValue =new SessionValue(HomeActivity.this);
        this.sessionAuth = new SessionAuth(HomeActivity.this);
         // *********************************************888
         rl_bottom_fragment = (RelativeLayout)findViewById(R.id.fragment_dash_board_left);

        /* mBottomBehaviour = BottomSheetBehavior.from(rl_bottom_fragment);


        mBottomBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                      //  btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                       // btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
*/

         // ****************************************************************8

         final   ShopFragment shopFragment = new ShopFragment();

//      Removing all fragment from fragment Manager
        getSupportFragmentManager().popBackStack();

//      Let's first dynamically add a fragment into a frame container
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_home, shopFragment, FRAGMENT_SHOPFRAGMENT)
                .commit();
/*
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_dash_board_left, myFragment, FRAGMENT_TAG)
                .commit();*/

//        getSupportFragmentManager().findFragmentById(R.id.fragment_dash_board_left);


        onNotified();

    }


/*

//    no need this method
    @Override
    public void onShopClick(Shop item) {



        // Let's first dynamically add a fragment into a frame container

        final ShopDashBoardFragment shopDashBoardFragment = new ShopDashBoardFragment();

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_home, shopDashBoardFragment, FRAGMENT_SHOP_DASHBOARD);
        ft.addToBackStack(null);
        ft.commit();


//        getSupportFragmentManager().beginTransaction().
//                replace(R.id.fragment_home, shopDashBoardFragment, FRAGMENT_SHOP_DASHBOARD)
//                .commit();

    }

*/
/*
    @Override
    public void onBackPressed() {

//          super.onBackPressed(); //back keyPress dont close
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
    }
*/

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            printLog(TAG, "if : " + getSupportFragmentManager().getBackStackEntryCount());
            getSupportFragmentManager().popBackStack();
            rl_bottom_fragment.setVisibility(View.VISIBLE);

        } else if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {

            printLog(TAG, "else if : " + getSupportFragmentManager().getBackStackEntryCount());
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
        }
    }

    @Override

    public void onNotified() {

        try {

            Log.e("on notified", "Fragment click");

            final HomeDashBoardLeftFragment dashBoardLeftFragment = new HomeDashBoardLeftFragment();

//        Removing all fragment from fragment Manager
//        getSupportFragmentManager().popBackStack();

// Let's first dynamically add a fragment into a frame container
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_dash_board_left, dashBoardLeftFragment, FRAGMENT_DASHBOARD_LEFT)
                    .commit();


        }catch (Exception e){
            e.getLocalizedMessage();
        }

    }
/*

    @Override
    protected void onRestart() {
        super.onRestart();
        onNotified();
    }
*/


    @Override
    public void onShopClick(Shop shop) {

        // Let's first dynamically add a fragment into a frame container

        if(sessionAuth.getExecutiveRole().equals("Executive")) {

            ShopDashBoardFragment shopDashBoardFragment = new ShopDashBoardFragment().newInstance(shop);

            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_home, shopDashBoardFragment, FRAGMENT_SHOP_DASHBOARD);
            ft.addToBackStack(null);
            ft.commit();
            rl_bottom_fragment.setVisibility(View.GONE);
        }
        else{
            ShopDashBoardSupplier shopDashBoardFragment_supplier = new ShopDashBoardSupplier().newInstance(shop);

            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_home, shopDashBoardFragment_supplier, FRAGMENT_SHOP_DASHBOARD);
            ft.addToBackStack(null);
            ft.commit();
            rl_bottom_fragment.setVisibility(View.GONE);
        }

    }



    /*

    @Override
    public void onBackPressed() {

//        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {


            Log.d(TAG, "if : " + getSupportFragmentManager().getBackStackEntryCount());
            getSupportFragmentManager().popBackStack();
        } else if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {

            Log.d(TAG, "else if : " + getSupportFragmentManager().getBackStackEntryCount());
            super.onBackPressed();
        }
    }


    */
}
