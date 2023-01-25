package com.advanced.minhas.config;

/**`
 * Created by Hr CT on 10/10/2020.
 * Edited on 29-01-2020 (Contra Voucher)
 * Edited on 10-02-2020 (Contra, Day close km error handling) by HK
 * Edited by HR on 15-02-2020 ()
 */

public class ConfigURL {

    //private static final String URL_IDREESJOMA = "http://3.108.136.200/faamle/Settings/";
    //private static final String URL_IDREESJOMA = "http://3.108.136.200/faamle_test/Settings/";
    //private static final String URL_ZAHRAT = "https://sunrise.icresp.in/Settings/";

    //private static final String URL_ZAHRAT = "https://demo.icresp.in/Settings/";

   // private static final String URL_ZAHRAT = "https://zahrat.icresp.in/Settings/";
    //private static final String URL_ZAHRAT = "https://triplerose.icresp.in/Settings/";
  // private static final String URL_ZAHRAT = "http://3.108.136.200/icrespdemo/Settings/";

    private static final String URL_ZAHRAT = "https://zahrattest.icresp.in/Settings/";

    public static final String URL_LOGIN = URL_ZAHRAT + "executive_login";

    public static final String URL_GROUPS = URL_ZAHRAT + "api_get_route_group_list";

    public static final String URL_GROUPS_REGISTER = URL_ZAHRAT + "api_executive_day_register";

    public static final String URL_GROUPSBYCUSTOMERLIST = URL_ZAHRAT + "api_get_customer_info_by_search";

    public static final String URL_DAY_CLOSE = URL_ZAHRAT + "api_executive_day_close";

    public static final String URL_PRODUCT_TYPE = URL_ZAHRAT + "api_get_product_type";

    public static final String URL_VAN_STOCK = URL_ZAHRAT + "api_get_van_stock_search";

    public static final String URL_VAN_MASTERSTOCK = URL_ZAHRAT + "api_get_van_stock_search_all";

    public static final String URL_REGISTERED_SHOP = URL_ZAHRAT + "api_get_customer_info";

    public static final String URL_ALLROUTE_SHOP = URL_ZAHRAT + "api_get_route_customer_search";

    public static final String URL_PLACE_ORDER = URL_ZAHRAT + "api_multiple_order_place";

    public static final String URL_MULTIPLE_RETURN = URL_ZAHRAT + "api_multiple_sale_return";

    public static final String URL_QUOTATION_ORDER = URL_ZAHRAT + "api_multiple_quotation";

    public static final String URL_GET_PENDING_RECEIPT = URL_ZAHRAT + "api_get_pending_reciept";

    public static final String URL_GET_ALL_RECEIPT = URL_ZAHRAT + "api_get_reciept";

    public static final String URL_PAID_RECEIPT = URL_ZAHRAT + "api_receipt_payment";

    public static final String URL_INVOICE_DETAILS = URL_ZAHRAT + "api_get_invoice_item_details";

    public static final String URL_SALE_RETURN = URL_ZAHRAT + "api_sale_return";

    public static final String URL_GET_CUSTOMER_TYPE = URL_ZAHRAT + "api_get_division_type_list";

    public static final String URL_ADD_CUSTOMER = URL_ZAHRAT + "api_customer_creation";

    public static final String URL_CHANGE_PASSWORD = URL_ZAHRAT + "executive_reset_password";

    public static final String URL_NO_SALE = URL_ZAHRAT + "api_multiple_no_sale";

    public static final String URL_UPDATE_CUSTOMER = URL_ZAHRAT + "api_gps_location";

    public static final String URL_GET_BANK = URL_ZAHRAT + "api_get_bank_list";

    public static final String URL_PAID_CHEQUE_RECEIPT = URL_ZAHRAT + "api_save_cheque_details";

    public static final String URL_GET_BONUS_REPORT = URL_ZAHRAT + "api_executive_bonus_report";

    public static final String URL_GET_PRODUCTWISE_REPORT = URL_ZAHRAT + "api_product_wise_report";

    public static final String URL_GET_OUTSTANDING_REPORT = URL_ZAHRAT + "api_customer_outstanding_report";

    // public static final String URL_OFFLINE_RETURN = URL_IDREESJOMA + "api_multiple_sale_return";
    public static final String URL_OFFLINE_RETURN = URL_ZAHRAT + "api_invoice_multiple_sale_return";

    public static final String URL_GET_ALL_EXPENSE = URL_ZAHRAT + "api_get_expense";

    public static final String URL_EXPENSE_ENTRY = URL_ZAHRAT + "api_save_expense";

    public static final String URL_CONTRA_VOUCHER = URL_ZAHRAT + "api_contra_voucher";

    public static final String URL_GET_CASHIN_HAND = URL_ZAHRAT + "api_get_cashinhand";

    public static final String URL_SETTINGS_PARAMETERS = URL_ZAHRAT + "SystemParameter";

    public static final String URL_GETLEDGER = URL_ZAHRAT + "Ledger_get_ajax";//Ledger_get_ajax

    //haris added on 07-12-2020
    public static final String URL_VAN_TO_WAREHOUSE_APPROVE = URL_ZAHRAT + "api_van_branch_stock_transfer";

    //haris added on 05-07-2020
    public static final String URL_GET_EXPENSE_DETAILS = URL_ZAHRAT + "api_get_expense_list";

    public static final String URL_EDIT_CUSTOMER = URL_ZAHRAT + "api_customer_edit";

    public static final String URL_STOCK_TRANSFER_APPROVE = URL_ZAHRAT + "api_branch_van_transfer_items_approve";

    public static final String URL_STOCK_TRANSFER_DETAILS = URL_ZAHRAT + "api_branch_van_transfer_items";

    public static final String URL_STOCK_TRANSFER = URL_ZAHRAT + "api_branch_van_transfer";

    public static final String URL_GET_VEHICLE_LIST = URL_ZAHRAT + "api_get_vehicle_list";

    public static final String URL_GET_CUSTOMER_BYCUSTID = URL_ZAHRAT + "api_customer_detailsby_custid";

    public static final String URL_GET_ALL_PENDING_INVOICES = URL_ZAHRAT + "api_get_pending_invoice_list";

    public static final String URL_BILLWISE_RECEIPT = URL_ZAHRAT + "api_save_receipt_billwise";

    public static final String URL_GET_INVOICEWISE_REPORT = URL_ZAHRAT + "api_invoice_wise_report";

    public static final String URL_SAVE_ORDER = URL_ZAHRAT + "customer_production_order";

    public static final String URL_STOCK_FETCH_LIVE = URL_ZAHRAT + "api_branch_van_transfer_order_list";

    public static final String URL_ORDER_TRANSFER_DETAILS = URL_ZAHRAT + "api_order_transfer_items";
}
