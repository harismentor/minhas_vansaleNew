package com.advanced.minhas.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.advanced.minhas.R;
import com.advanced.minhas.fragment.ShopDashBoardFragment;
import com.advanced.minhas.fragment.ShopSearchFragment;
import com.advanced.minhas.listener.ShopClickListener;
import com.advanced.minhas.model.Shop;

import static com.advanced.minhas.config.ConfigKey.FRAGMENT_SHOP_DASHBOARD;
import static com.advanced.minhas.config.ConfigKey.FRAGMENT_SHOP_SEARCH;


;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ShopClickListener}
 * interface.
 */
public class OtherCustomerActivity extends AppCompatActivity implements ShopClickListener {

    String TAG="OtherCustomerActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_customer);


        final ShopSearchFragment shopSearchFragment = new ShopSearchFragment();

//        Removing all fragment from fragment Manager
        getSupportFragmentManager().popBackStack();

// Let's first dynamically add a fragment into a frame container
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container_other_customer, shopSearchFragment, FRAGMENT_SHOP_SEARCH)
                .commit();
    }


    @Override
    public void onShopClick(Shop shop) {


        // Let's first dynamically add a fragment into a frame container
        ShopDashBoardFragment shopDashBoardFragment = new ShopDashBoardFragment().newInstance(shop);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container_other_customer, shopDashBoardFragment, FRAGMENT_SHOP_DASHBOARD);
        ft.addToBackStack(null);
        ft.commit();




/*

        // Let's first dynamically add a fragment into a frame container
        ShopDashBoardFragment productDetailsFragment = new ShopDashBoardFragment().newInstance(shop);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container_other_customer, productDetailsFragment, FRAGMENT_SHOP_DASHBOARD);
        ft.addToBackStack(null);
        ft.commit();
*/

    }





    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            Log.d(TAG, "if : " + getSupportFragmentManager().getBackStackEntryCount());
            getSupportFragmentManager().popBackStack();
        } else if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {

        super.onBackPressed();

    }
    }


}
