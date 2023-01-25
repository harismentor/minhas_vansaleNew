package com.advanced.minhas.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.advanced.minhas.model.Route;
import com.advanced.minhas.model.RouteGroup;

import java.util.HashMap;

import static com.advanced.minhas.config.ConfigKey.CODE_PREF_NAME;
import static com.advanced.minhas.config.ConfigKey.PREF_GROUP_IS_REGISTERED;
import static com.advanced.minhas.config.ConfigKey.PREF_NAME;
import static com.advanced.minhas.config.ConfigKey.PRIVATE_MODE;

/**
 * Created by mentor on 26/10/17.
 */

public class SessionValue {


    public static final String PREF_SELECTED_GROUP = "group_pref";
    public static final String PREF_SELECTED_GROUP_ID = "group_id_pref";
    public static final String PREF_SELECTED_ROUTE = "route_pref";
    public static final String PREF_SELECTED_ROUTE_ID = "route_pref_id";
    public static final String PREF_DAY_REGISTER_ID = "day_reg_pref_id";

    public static final String PREF_DAY_REGISTER_KM = "day_reg_km_pref";
    public static final String PREF_ROUTE_MOBILE = "route_mobile_pref";
    public static final String PREF_VEHICLE_NO = "vehicle_no";
    public static final String PREF_VEHICLE_DRIVERNAME = "vehicle_drivername";

    //    Executive details
    public static final String PREF_EXECUTIVE_NAME = "executive_name_pref";
    public static final String PREF_EXECUTIVE_ID = "executive_id_pref";
    public static final String PREF_EXECUTIVE_MOBILE = "executive_mobile_pref";
    public static final String PREF_EXECUTIVE_ROLE = "executive_role_pref";



    //    Main details
    public static final String PREF_COMPANY_NAME = "main_name_pref";
    public static final String PREF_COMPANY_NAME_ARAB = "main_name_arab_pref";
    public static final String PREF_COMPANY_ADDRESS_1 = "main_address1_pref";
    public static final String PREF_COMPANY_ADDRESS_2 = "main_address2_pref";
    public static final String PREF_COMPANY_ADDRESS_1_ARAB = "main_address1_arab_pref";
    public static final String PREF_COMPANY_ADDRESS_2_ARAB = "main_address2_arab_pref";
    public static final String PREF_COMPANY_EMAIL = "main_email_pref";
    public static final String PREF_COMPANY_MOBILE = "main_mob_pref";
    public static final String PREF_COMPANY_CR = "main_cr_pref";
    public static final String PREF_COMPANY_VAT = "main_vat_pref";
    public static final String PREF_COMPANY_PAN_NO = "main_pan_no";
    public static final String PREF_COMPANY_PRODUCT_TYPE = "product_type";
    private static final String PREF_INVOICE_CODE = "invoice_code";
    private static final String PREF_RECEIPT_CODE = "receipt_code";
    private static final String PREF_RETURN_CODE = "return_code";
    private static final String PREF_ROUTE_CODE = "route_code";
    public static final String PREF_QRCODE_LINK = "qrcode_link";
    public static final String PREF_COMPANY_TAXCARD = "taxcard";
    private static final String IS_POS_ACTIVE = "isPdf_pref_key";
    private static final String PREF_APPROVE_FLAG = "approve_flag";
    public static final String PREF_EXECUTIVE_CODE = "executive_code";
    public static final String PREF_SALE_TARGET = "sale_target_pref";
    public static final String PREF_COMPANY_WEBSITE = "website";
    public static final String PREF_COMPANY_FSSAI_NO = "fssai_no";
    public static final String PREF_SELECTED_SHOPID = "selected_shopid";
    public static final String PREF_COMPANY_BANKNAME = "bank_name";
    public static final String PREF_COMPANY_BANKACCNAME = "bank_accname";
    public static final String PREF_COMPANY_ACCNO = "bank_accno";
    public static final String PREF_COMPANY_IFSC = "bank_ifsc";
    public static final String PREF_COMPANY_BANKBRANCH = "bank_bankbranch";



    public static final String PREF_DAILY_BONUS = "executive_daily_bonus";
    public static final String PREF_MONTHLY_BONUS = "executive_monthly_bonus";

    public static final String PREF_CASH_IN_HAND = "executive_cash_in_hand";
    public static final String PREF_TOTAL_AMNT_RECEIPT = "total_receipt_billwise";
    //    controll details
    public static final String PREF_CURRENCY = "control_currency";
    public static final String PREF_MODE = "control_mode";
    public static final String PREF_CASEPIECE = "control_casepiece";
    public static final String PREF_VATPERCENT = "control_casepiece";

    //    company details login
    public static final String PREF_COMPANY_ID = "company_id";
    public static final String PREF_COMPANY_NAME_LOGIN = "company_name_login";
    public static final String PREF_COMPANY_MOBILE_LOGIN = "company_mobile_login";
    public static final String PREF_COMPANY_ADDRESS_LOGIN = "company_address_login";
    public static final String PREF_COMPANYL_VATNO_LOGIN = "company_vat_login";
    public static final String PREF_COMPANY_CRNO_LOGIN = "company_crno_login";
    public static final String PREF_COMPANY_CUST_CARE = "company_cust_care_login";


    //  Latitude and longitude

    public static final String PREF_LATITUDE = "latitude_pref";
    public static final String PREF_LONGITUDE = "longitude_pref";


    private SharedPreferences preferences, codePref;
    private SharedPreferences.Editor editor, codeEditor;
    private Context context;
    private static SessionValue valueInstance;


    // Constructor
    @SuppressLint({"CommitPrefEdits", "WrongConstant"})
    public SessionValue(Context context) {
        this.context = context;

        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        codePref = this.context.getSharedPreferences(CODE_PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
        codeEditor = codePref.edit();

    }

    //haris created on 18-06-21

    public void save_latitude_and_longitude(String lat, String longt) {
        editor.putString(PREF_LATITUDE, lat);
        editor.putString(PREF_LONGITUDE, longt);

        // commit changes
        editor.commit();

    }

    public void save_receipt_totalamnt(String total) {
        editor.putString(PREF_TOTAL_AMNT_RECEIPT, total);

        editor.commit();

        // editor.apply();
    }

    public HashMap<String, String> get_billwise_total_amnt() {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(PREF_TOTAL_AMNT_RECEIPT, preferences.getString(PREF_TOTAL_AMNT_RECEIPT, ""));
        return values;
    }
    /**
     * get latitude and longtude details
     */

    public HashMap<String, String> get_map_details() {
        HashMap<String, String> values = new HashMap<String, String>();

        values.put(PREF_LATITUDE, preferences.getString(PREF_LATITUDE, ""));
        values.put(PREF_LONGITUDE, preferences.getString(PREF_LONGITUDE, ""));

        // return values
        return values;
    }

    /**
     * Get stored register status data
     */
    public boolean isGroupRegister() {

        if (new SessionAuth(context).isLoggedIn())
            return preferences.getBoolean(PREF_GROUP_IS_REGISTERED, false);// return register status
        else
            new SessionAuth(context).logoutUser();


        return false;

    }

    /**
     * set stored register status data
     */
    private void setGroupRegister(boolean status) {

        if (new SessionAuth(context).isLoggedIn()) {
            editor.putBoolean(PREF_GROUP_IS_REGISTERED, status);
            // commit changes
            editor.commit();
        } else
            new SessionAuth(context).logoutUser();


    }

    /******************** Create Kafeel *********************/

    public void createcompanydetails_login(String id, String name, String mobile, String address, String vatNo, String Crno, String custcare) {
        editor.putString(PREF_COMPANY_ID, id);
        editor.putString(PREF_COMPANY_NAME_LOGIN, name);
        editor.putString(PREF_COMPANY_MOBILE_LOGIN, mobile);
        editor.putString(PREF_COMPANY_ADDRESS_LOGIN, address);
        editor.putString(PREF_COMPANYL_VATNO_LOGIN, vatNo);
        editor.putString(PREF_COMPANY_CRNO_LOGIN, Crno);
        editor.putString(PREF_COMPANY_CUST_CARE, custcare);


        // commit changes
        editor.commit();

        //  editor.apply();
    }

    public HashMap<String, String> getCompanyDetails_login() {
        HashMap<String, String> values = new HashMap<String, String>();

        values.put(PREF_COMPANY_ID, preferences.getString(PREF_COMPANY_ID, ""));
        values.put(PREF_COMPANY_NAME_LOGIN, preferences.getString(PREF_COMPANY_NAME_LOGIN, ""));
        values.put(PREF_COMPANY_MOBILE_LOGIN, preferences.getString(PREF_COMPANY_MOBILE_LOGIN, ""));
        values.put(PREF_COMPANY_ADDRESS_LOGIN, preferences.getString(PREF_COMPANY_ADDRESS_LOGIN, ""));
        values.put(PREF_COMPANYL_VATNO_LOGIN, preferences.getString(PREF_COMPANYL_VATNO_LOGIN, ""));
        values.put(PREF_COMPANY_CRNO_LOGIN, preferences.getString(PREF_COMPANY_CRNO_LOGIN, ""));
        values.put(PREF_COMPANY_CUST_CARE, preferences.getString(PREF_COMPANY_CUST_CARE, ""));


        // return values
        return values;
    }


    /**
     * set store register  data
     */
    public void storeGroupAndRoute(RouteGroup group, Route route, float target, String dayRegisterId, String fromKM, String routeMobile, String vehicleno , String drivername) {

        if (new SessionAuth(context).isLoggedIn()) {

//Store data
            if (group != null && route != null) {

                editor.putFloat(PREF_SALE_TARGET, target);
                editor.putBoolean(PREF_GROUP_IS_REGISTERED, true);
                editor.putString(PREF_SELECTED_GROUP, group.getGroupName());
                editor.putString(PREF_SELECTED_GROUP_ID, String.valueOf(group.getGroupId()));
                editor.putString(PREF_SELECTED_ROUTE, route.getRoute());
                editor.putString(PREF_SELECTED_ROUTE_ID, String.valueOf(route.getId()));
                editor.putString(PREF_DAY_REGISTER_ID, dayRegisterId);
                editor.putString(PREF_DAY_REGISTER_KM, fromKM);
                editor.putString(PREF_ROUTE_MOBILE, routeMobile);
                editor.putString(PREF_VEHICLE_NO, vehicleno);
                editor.putString(PREF_VEHICLE_DRIVERNAME, drivername);

            } else {

                editor.putBoolean(PREF_GROUP_IS_REGISTERED, false);
                editor.putString(PREF_SELECTED_GROUP, "");
                editor.putString(PREF_SELECTED_GROUP_ID, "");
                editor.putString(PREF_SELECTED_ROUTE, "");
                editor.putString(PREF_SELECTED_ROUTE_ID, "");
                editor.putString(PREF_DAY_REGISTER_ID, "");
                editor.putFloat(PREF_SALE_TARGET, 0);
                editor.putString(PREF_DAY_REGISTER_KM, "0");
                editor.putString(PREF_ROUTE_MOBILE, "");
                editor.putString(PREF_VEHICLE_NO, "");
                editor.putString(PREF_VEHICLE_DRIVERNAME, drivername);
            }
            // commit changes
            editor.commit();
        } else
            new SessionAuth(context).logoutUser();


    }


    /**
     * set store sale target
     */
    public void storeSaleTarget(float target) {

        if (new SessionAuth(context).isLoggedIn()) {

//Store data

            editor.putFloat(PREF_SALE_TARGET, target);
            // commit changes
            editor.commit();
        } else
            new SessionAuth(context).logoutUser();


    }

    /**
     * Get stored sale target
     */

    public float getSaleTarget() {

        if (new SessionAuth(context).isLoggedIn())
            return preferences.getFloat(PREF_SALE_TARGET, 0.0f);// return stock amount
        else
            new SessionAuth(context).logoutUser();


        return 0.0f;

    }


    /**
     * Get store register  data
     */

    public String getDayRegisterId() {

        // return values
        return preferences.getString(PREF_DAY_REGISTER_ID, "0");
    }

    public String getvehicleno() {

        // return values
        return preferences.getString(PREF_VEHICLE_NO, "0");
    }
    public String getdrivername() {

        // return values
        return preferences.getString(PREF_VEHICLE_DRIVERNAME, "0");
    }

    public String getgroupname() {

        // return values
        return preferences.getString(PREF_SELECTED_GROUP, "0");
    }

    public String getroute() {

        // return values
        return preferences.getString(PREF_SELECTED_ROUTE, "0");
    }

    public HashMap<String, String> getStoredValuesDetails() {
        HashMap<String, String> values = new HashMap<String, String>();
        // group
        values.put(PREF_SELECTED_GROUP, preferences.getString(PREF_SELECTED_GROUP, ""));
        values.put(PREF_SELECTED_GROUP_ID, preferences.getString(PREF_SELECTED_GROUP_ID, "0"));

        // route
        values.put(PREF_SELECTED_ROUTE, preferences.getString(PREF_SELECTED_ROUTE, ""));

        values.put(PREF_SELECTED_ROUTE_ID, preferences.getString(PREF_SELECTED_ROUTE_ID, "0"));
        values.put(PREF_ROUTE_MOBILE, preferences.getString(PREF_ROUTE_MOBILE, ""));

        // return values
        return values;
    }

    public String getRegisteredMobile() {
        // return values
        return preferences.getString(PREF_ROUTE_MOBILE, "");
    }

    public String getRegisteredKM() {
        // return values
        return preferences.getString(PREF_DAY_REGISTER_KM, "0");
    }

    /**
     * Create executive details
     */

    public void createExecutiveDetails(String exeName, String exeId, String exeMob) {
        editor.putString(PREF_EXECUTIVE_NAME, exeName);
        editor.putString(PREF_EXECUTIVE_ID, exeId);
        editor.putString(PREF_EXECUTIVE_MOBILE, exeMob);


        // commit changes
        editor.commit();

    }

    /**
     * get executive details
     */

    public HashMap<String, String> getExecutiveDetails() {
        HashMap<String, String> values = new HashMap<String, String>();

        values.put(PREF_EXECUTIVE_NAME, preferences.getString(PREF_EXECUTIVE_NAME, "Executive"));
        values.put(PREF_EXECUTIVE_ID, preferences.getString(PREF_EXECUTIVE_ID, ""));
        values.put(PREF_EXECUTIVE_MOBILE, preferences.getString(PREF_EXECUTIVE_MOBILE, ""));
        // return values
        return values;
    }
////////////////


    /**
     * Create main details
     */

//    public void createCompanyDetails(String companyName, String companyNameArab, String address1, String address1Arab, String address2, String address2Arab, String companyMobile, String companyEmail, String companyCR, String companyVAT,String companyPan_No) {
//        editor.putString(PREF_COMPANY_NAME, companyName);
//        editor.putString(PREF_COMPANY_NAME_ARAB, companyNameArab);
//        editor.putString(PREF_COMPANY_ADDRESS_1, address1);
//
//        editor.putString(PREF_COMPANY_ADDRESS_1_ARAB, address1Arab);
//        editor.putString(PREF_COMPANY_ADDRESS_2, address2);
//        editor.putString(PREF_COMPANY_ADDRESS_2_ARAB, address2Arab);
//        editor.putString(PREF_COMPANY_MOBILE, companyMobile);
//        editor.putString(PREF_COMPANY_EMAIL, companyEmail);
//        editor.putString(PREF_COMPANY_CR, companyCR);
//        editor.putString(PREF_COMPANY_VAT, companyVAT);
//        editor.putString(PREF_COMPANY_PAN_NO, companyPan_No);
//        // commit changes
//        editor.commit();
//
//    }
    public void createCompanyDetails(String companyName, String companyNameArab, String address1, String address1Arab, String address2, String address2Arab, String companyMobile, String companyEmail, String companyCR, String companyVAT, String companyPan_No,
                                     String producttype, String qrcodelink, String taxcard ,String companywebsite, String fssai_no,
                                     String comp_bankname,String comp_bankaccname,String comp_bankaccno,String comp_bankifsc,String comp_bankbranch) {
        editor.putString(PREF_COMPANY_NAME, companyName);
        editor.putString(PREF_COMPANY_NAME_ARAB, companyNameArab);
        editor.putString(PREF_COMPANY_ADDRESS_1, address1);

        editor.putString(PREF_COMPANY_ADDRESS_1_ARAB, address1Arab);
        editor.putString(PREF_COMPANY_ADDRESS_2, address2);
        editor.putString(PREF_COMPANY_ADDRESS_2_ARAB, address2Arab);
        editor.putString(PREF_COMPANY_MOBILE, companyMobile);
        editor.putString(PREF_COMPANY_EMAIL, companyEmail);
        editor.putString(PREF_COMPANY_CR, companyCR);
        editor.putString(PREF_COMPANY_VAT, companyVAT);
        editor.putString(PREF_COMPANY_PAN_NO, companyPan_No);
        editor.putString(PREF_COMPANY_PRODUCT_TYPE, producttype);
        editor.putString(PREF_QRCODE_LINK, qrcodelink);
        editor.putString(PREF_COMPANY_TAXCARD, taxcard);
        editor.putString(PREF_COMPANY_WEBSITE,companywebsite);
        editor.putString(PREF_COMPANY_FSSAI_NO,fssai_no);

        editor.putString(PREF_COMPANY_BANKNAME,comp_bankname);
        editor.putString(PREF_COMPANY_BANKACCNAME,comp_bankaccname);
        editor.putString(PREF_COMPANY_ACCNO,comp_bankaccno);
        editor.putString(PREF_COMPANY_IFSC,comp_bankifsc);
        editor.putString(PREF_COMPANY_BANKBRANCH,comp_bankbranch);

        // commit changes
        editor.commit();

    }


    /**
     * get Main details
     */

    public HashMap<String, String> getCompanyDetails() {
        HashMap<String, String> values = new HashMap<String, String>();

        values.put(PREF_COMPANY_NAME, preferences.getString(PREF_COMPANY_NAME, "Idrees"));
        values.put(PREF_COMPANY_NAME_ARAB, preferences.getString(PREF_COMPANY_NAME_ARAB, ""));
        values.put(PREF_COMPANY_ADDRESS_1, preferences.getString(PREF_COMPANY_ADDRESS_1, ""));
        values.put(PREF_COMPANY_ADDRESS_1_ARAB, preferences.getString(PREF_COMPANY_ADDRESS_1_ARAB, ""));

        values.put(PREF_COMPANY_ADDRESS_2, preferences.getString(PREF_COMPANY_ADDRESS_2, ""));
        values.put(PREF_COMPANY_ADDRESS_2_ARAB, preferences.getString(PREF_COMPANY_ADDRESS_2_ARAB, ""));
        values.put(PREF_COMPANY_CR, preferences.getString(PREF_COMPANY_CR, ""));
        values.put(PREF_COMPANY_MOBILE, preferences.getString(PREF_COMPANY_MOBILE, ""));
        values.put(PREF_COMPANY_EMAIL, preferences.getString(PREF_COMPANY_EMAIL, ""));
        values.put(PREF_COMPANY_VAT, preferences.getString(PREF_COMPANY_VAT, ""));
        values.put(PREF_COMPANY_PAN_NO, preferences.getString(PREF_COMPANY_PAN_NO, ""));
        values.put(PREF_COMPANY_PRODUCT_TYPE, preferences.getString(PREF_COMPANY_PRODUCT_TYPE, ""));
        values.put(PREF_QRCODE_LINK, preferences.getString(PREF_QRCODE_LINK, ""));
        values.put(PREF_COMPANY_TAXCARD, preferences.getString(PREF_COMPANY_TAXCARD, ""));
        values.put(PREF_COMPANY_WEBSITE, preferences.getString(PREF_COMPANY_WEBSITE, ""));
        values.put(PREF_COMPANY_FSSAI_NO, preferences.getString(PREF_COMPANY_FSSAI_NO, ""));

        values.put(PREF_COMPANY_BANKNAME, preferences.getString(PREF_COMPANY_BANKNAME, ""));
        values.put(PREF_COMPANY_BANKACCNAME, preferences.getString(PREF_COMPANY_BANKACCNAME, ""));
        values.put(PREF_COMPANY_ACCNO, preferences.getString(PREF_COMPANY_ACCNO, ""));
        values.put(PREF_COMPANY_IFSC, preferences.getString(PREF_COMPANY_IFSC, ""));
        values.put(PREF_COMPANY_BANKBRANCH, preferences.getString(PREF_COMPANY_BANKBRANCH, ""));

        // return values
        return values;
    }

    /****
     * invoice code store and get and clear
     * */

//     store invoice code
    public void
    storeInvoiceCode(String strKey, String strValue) {
        Log.e("invoiceno hrrr",""+strValue);
        Log.e("strKey hrrr",""+strKey);
        codeEditor.putString(PREF_INVOICE_CODE + strKey, strValue);
        codeEditor.commit();
    }


    public String getInvoiceCode(String strKey) {
        return codePref.getString(PREF_INVOICE_CODE + strKey, "110");// return
    }


    public  String getexecutivecode() {
        return codePref.getString(PREF_EXECUTIVE_CODE , "0");// return
    }

    //haris added on 10-02-2021
    public String getPrefRoutecode() {

        // return values
        return preferences.getString(PREF_ROUTE_CODE, "0");
    }

    /****
     * receipt code store and get and clear
     * */

    public void storeReceiptCode(String strKey, String strValue) {
        codeEditor.putString(PREF_RECEIPT_CODE + strKey, strValue);
        codeEditor.commit();
    }

    public void storeExecutiveCode(String strValue) {
        Log.e("executivecode",""+strValue);
        codeEditor.putString(PREF_EXECUTIVE_CODE, strValue);
        codeEditor.commit();
    }

    public void storeShopid(String strValue) {
        Log.e("shopid",""+strValue);
        codeEditor.putString(PREF_SELECTED_SHOPID, strValue);
        codeEditor.commit();
    }

    public  String getselected_shopid() {
        return codePref.getString(PREF_SELECTED_SHOPID , "0");// return
    }
    public String getReceiptCode(String strKey){
        return codePref.getString(PREF_RECEIPT_CODE+strKey,"110");// return
    }

    public void storeApproveFlag(String strValue){
        codeEditor.putString(PREF_APPROVE_FLAG, strValue);
        codeEditor.commit();
    }


    public String getApproveFlag(){
        return codePref.getString(PREF_APPROVE_FLAG,"false");// return
    }
    /****
     * return code store and get and clear
     * */

    public void storeReturnCode(String strKey, String strValue){
        Log.e("strKey",strKey);
        Log.e("strValue",strValue);
        codeEditor.putString(PREF_RETURN_CODE+strKey, strValue);
        codeEditor.commit();
    }
    //haris added on 10-02-2021
    //     store invoice code
    public void storeRoutecode(String routecode){
        editor.putString(PREF_ROUTE_CODE, routecode);
    }


    public String getReturnCode(String strKey){
        return codePref.getString(PREF_RETURN_CODE+strKey,"110");// return
    }

    /******************** Create Control Settings *********************/

    public void createControllSettings(String currency, String mode, String casepiece , String vatpercentage) {
        editor.putString(PREF_CURRENCY, currency);
        editor.putString(PREF_MODE, mode);
        editor.putString(PREF_CASEPIECE, casepiece);
        editor.putString(PREF_VATPERCENT, vatpercentage);
        // commit changes
        editor.commit();

        // editor.apply();
    }

    /**
     ************** get controll settings ***************
     */

    public HashMap<String, String> getControllSettings() {
        HashMap<String, String> values = new HashMap<String, String>();

        values.put(PREF_CURRENCY, preferences.getString(PREF_CURRENCY, ""));
        values.put(PREF_MODE, preferences.getString(PREF_MODE, ""));
        values.put(PREF_CASEPIECE, preferences.getString(PREF_CASEPIECE, ""));
        values.put(PREF_VATPERCENT, preferences.getString(PREF_VATPERCENT, ""));
        // return values
        return values;
    }


    public void setPOSPrint(boolean isPos){

        // Storing boolean value in pref
        editor.putBoolean(IS_POS_ACTIVE, isPos);
        // commit changes
        editor.commit();
    }

    // Get Login State
    public boolean isPOSPrint(){
        return preferences.getBoolean(IS_POS_ACTIVE, false);
    }



    public  void clearCode(){
        // Clearing all data from Shared Preferences
        codeEditor.clear();
        codeEditor.commit();
    }
}
