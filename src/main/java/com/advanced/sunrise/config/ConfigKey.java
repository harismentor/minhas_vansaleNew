package com.advanced.minhas.config;

/**
 * Created by mentor on 24/10/17.
 */

public class ConfigKey {

    /***
     * Session key
     */

    // Shared pref mode
    public static final int PRIVATE_MODE = 0;

    // Sharedpref file name
    public static final String PREF_NAME = "idrees_joma_AndroidPref";
    public static final String CODE_PREF_NAME = "icresp_codePref";

    // All Shared Preferences Keys
    public static final String PREF_IS_LOGIN = "IsLoggedIn_pref";

    // User name (make variable public to access from outside)
    public static final String PREF_KEY_USER_NAME = "user_name_prf";

    // Password (make variable public to access from outside)
    public static final String PREF_KEY_PASSWORD = "password_pref";

    // registrationId (make variable public to access from outside)
    public static final String PREF_KEY_ID = "regi_id_pref";

    // group registration (make variable public to access from outside)
    public static final String PREF_GROUP_IS_REGISTERED = "group_regi_key_pref";
    public static final double PRIVATE_VERSION_CODE = 5.0;//last 10-01-2023
    /***
     * fragments key
     */
    public static final String FRAGMENT_DASHBOARD_LEFT = "key_dash_left_fragment";
    public static final String FRAGMENT_SHOPFRAGMENT = "key_shop_fragment";
    public static final String FRAGMENT_SHOP_DASHBOARD = "key_shop_dash_board_fragment";
    public static final String FRAGMENT_SHOP_SEARCH = "key_shop_search_fragment";
    public static final String FRAGMENT_REPORT = "key_report_fragment";
    public static final String FRAGMENT_SALE = "key_saleFragment";
    public static final String FRAGMENT_SALEEDIT = "key_saleeditFragment";
    /***
     * value passing key
     */

    public static final String SHOP_KEY = "key_shop"; //ShopClass
    public static final int WRITE_REQUEST_CODE=1;
    public static final String VAT_STATUS = "vat_status";

    /***
     * webservice key
     */

    public static final String EXECUTIVE_KEY = "executive_id"; //String
    public static final String DAY_REGISTER_KEY = "day_register_id"; //String

    public static final String USERNAME_KEY = "user_name"; //String
    public static final String PASSWORD_KEY = "password";  //String

    public static final String CUSTOMER_KEY = "customer_id";  //String

    public static final String PRODUCT_TYPE_KEY = "product_type_id";  //String

    public static final String PRODUCT_BRAND_KEY = "brand_id";  //String

    public static final String INVOICE_NO_KEY = "invoice_no";  //String

    public static final String PREF_KEY_DAY_OPEN_TIME = "day_open_time";

    //    SQLLite action type
    public static final int REQ_SALE_TYPE = 1;
    public static final int REQ_QUOTATION_TYPE = 2;
    public static final int REQ_RETURN_TYPE = 3;
    public static final int REQ_RECEIPT_TYPE = 4;
    public static final int REQ_NO_SALE_TYPE = 5;
    public static final int REQ_CUSTOMER_TYPE = 6;
    public static final int REQ_ANY_TYPE = 0;
    public static final int REQ_EDIT_TYPE = 16;

    public static final int REQ_BANK_DETAILS = 7;
    public static final int REQ_EXPENSE = 8;
    public static final int REQ_STATE = 9;
    public static final int REQ_DISTRICT = 10;
    public static final int REQ_VEHICLE_DETAILS = 11;
    public static final int REQ_STOCK_APROVAL = 12;
    public static final int REQ_RECEIPT_BILLWISE = 13;
    public static final int REQ_RECEIPT_CHEQUE = 14;
    public static final int REQ_QUOTATION_TEMP = 15;
    public static final int REQ_QUOTATION_TEMPEDIT = 17;
    public static final int REQ_VAN_STOCK = 18;

    /**
     * LAYOUT VISIBILITIES
     * */

    public static final int VIEW_PROGRESSBAR=1;
    public static final int VIEW_RECYCLERVIEW=2;
    public static final int VIEW_ERRORVIEW=3;
    public static final int VIEW_DATAVIEW=4;
}
