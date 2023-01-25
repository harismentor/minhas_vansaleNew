package com.advanced.minhas.activity;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.session.SessionAuth;


public class SplashActivity extends AppCompatActivity {

    int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                  //     String str = android.os.Build.MODEL;
                  new SessionAuth(SplashActivity.this).checkLogin();

                   /* if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE || (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                        // on a large screen device ...
                        new SessionAuth(SplashActivity.this).checkLogin();

                    } else
                        {
                        showWarningDialog();
                    }*/

                } catch (RuntimeException e) {

                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }


    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

        builder.setTitle(getString(R.string.no_support_dialog_title));
        builder.setMessage(getString(R.string.no_support_dialog_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        finish();
                    }
                });


        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

}
