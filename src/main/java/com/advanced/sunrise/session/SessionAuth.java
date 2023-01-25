package com.advanced.minhas.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.advanced.minhas.activity.HomeActivity;
import com.advanced.minhas.activity.LoginActivity;
import com.advanced.minhas.localdb.MyDatabase;

import java.util.HashMap;

import static com.advanced.minhas.config.ConfigKey.PREF_IS_LOGIN;
import static com.advanced.minhas.config.ConfigKey.PREF_KEY_DAY_OPEN_TIME;
import static com.advanced.minhas.config.ConfigKey.PREF_KEY_ID;
import static com.advanced.minhas.config.ConfigKey.PREF_KEY_PASSWORD;
import static com.advanced.minhas.config.ConfigKey.PREF_KEY_USER_NAME;
import static com.advanced.minhas.config.ConfigKey.PREF_NAME;
import static com.advanced.minhas.config.ConfigKey.REQ_ANY_TYPE;
import static com.advanced.minhas.session.SessionValue.PREF_CASH_IN_HAND;
import static com.advanced.minhas.session.SessionValue.PREF_DAILY_BONUS;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_NAME;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_ROLE;
import static com.advanced.minhas.session.SessionValue.PREF_MONTHLY_BONUS;

/**
 * Created by mentor on 26/10/17.
 */

public class SessionAuth {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;




    // Constructor
    public SessionAuth(Context context){
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();


    }



    /**
     * Create login session
     * */


    public void createLoginSession(String userName, String password,String regId,String executiveName,String user_role){

        // After logout redirect user to Loing Activity
        Intent i = new Intent(context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Clearing all data from Shared Preferences
        editor.clear();

        // Storing login value as TRUE
        editor.putBoolean(PREF_IS_LOGIN, true);

        // Storing name in pref
        editor.putString(PREF_KEY_USER_NAME, userName);

        // Storing email in pref
        editor.putString(PREF_KEY_PASSWORD, password);

        editor.putString(PREF_KEY_ID,regId);
        editor.putString(PREF_EXECUTIVE_NAME,executiveName);
        editor.putString(PREF_EXECUTIVE_ROLE,user_role);

        // commit changes
        editor.commit();


        context.startActivity(new Intent(context, HomeActivity.class));
        ((Activity)context).finish();
    }



    /**
     * update Password session
     * */


    public void updatePassword(String password){

        // Storing email in pref
        editor.putString(PREF_KEY_PASSWORD, password);
        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
//     not using this App

    public void checkLogin(){

        Intent i;
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            i = new Intent(context, LoginActivity.class);
        }
        else {
            i = new Intent(context, HomeActivity.class);

        }
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        context.startActivity(i);
        ((Activity)context).finish();

    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(PREF_KEY_USER_NAME, preferences.getString(PREF_KEY_USER_NAME, ""));

        // user email id
        user.put(PREF_KEY_PASSWORD, preferences.getString(PREF_KEY_PASSWORD, ""));

        // user email id
        user.put(PREF_KEY_ID, preferences.getString(PREF_KEY_ID, ""));
        user.put(PREF_EXECUTIVE_NAME, preferences.getString(PREF_EXECUTIVE_NAME, ""));
        user.put(PREF_EXECUTIVE_ROLE, preferences.getString(PREF_EXECUTIVE_ROLE, ""));

        // return user
        return user;
    }


    /**
     * Get stored session data
     * */
    public String getExecutiveId(){

        if (isLoggedIn())
            return preferences.getString(PREF_KEY_ID,"");// return user id
        else
            logoutUser();


        return "";

    }
    public String getExecutiveName(){

        if (isLoggedIn())
            return preferences.getString(PREF_EXECUTIVE_NAME,"");// return user name
        else
            logoutUser();


        return "";

    }

    public String getExecutiveRole(){

            return preferences.getString(PREF_EXECUTIVE_ROLE,"");// return user name


    }

    // ------------ Daily Bonus  -----------
    public void store_bonus(float bonus){

        editor.putFloat(PREF_DAILY_BONUS, 0);

    }

    public void updateBonus(float bonus){

        // Storing email in pref
        editor.putFloat(PREF_DAILY_BONUS, bonus);
        // commit changes
        editor.commit();
    }

    public Float getBonus()
    {
            return preferences.getFloat(PREF_DAILY_BONUS,0);// return Bonus
    }

    // ------------ Monthly Bonus  -----------

    public void store_Monthlybonus(float bonus){

        editor.putFloat(PREF_MONTHLY_BONUS, bonus);
        editor.commit();
    }

    public Float getMonthlyBonus()
    {
        return preferences.getFloat(PREF_MONTHLY_BONUS,0);// return user name
    }

    // ----------------------------------


    // ------------ Daily Bonus  -----------
    public void store_opening_time(String time){

        editor.putString(PREF_KEY_DAY_OPEN_TIME, time);

    }

    // ------------ Cash in Hand  -----------
    public void store_CashinHand(float cashinhand){

        editor.putFloat(PREF_CASH_IN_HAND, cashinhand);
        editor.commit();

    }

    public void updateCashinHand(float cashinhand){

        // Storing
        editor.putFloat(PREF_CASH_IN_HAND, cashinhand);
        // commit changes
        editor.commit();
    }

    public Float getCashinHand()
    {
        return preferences.getFloat(PREF_CASH_IN_HAND,0);// return Cash in hand
    }


    public String getOpeningTime(){
        return preferences.getString(PREF_KEY_DAY_OPEN_TIME, "");
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        new MyDatabase(context).deleteTableRequest(REQ_ANY_TYPE);

        new SessionValue(context).clearCode();
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();



        // After logout redirect user to Loing Activity
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        context.startActivity(i);
        ((Activity)context).finish();

    }




    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return preferences.getBoolean(PREF_IS_LOGIN, false);
    }

}
