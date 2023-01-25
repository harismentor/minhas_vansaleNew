package com.advanced.minhas.listener;

/**
 * Created by mentor on 23/11/17.
 */

public interface ActivityConstants {
    public static final int ACTIVITY_SALES = 1001; //call Sales Activity
    public static final int ACTIVITY_EDITSALE = 1004; //call Sales Activity
    public static final int ACTIVITY_QUOTATION = 1002; //call quotation Activity
    public static final int ACTIVITY_SALE_REPORT = 1003; //call Sales Activity

    public static final int ACTIVITY_OUTSTANDING_RECEIPT = 2001; //call ReceiptPreview Activity
    public static final int ACTIVITY_OUTSTANDING_HISTORY_RECEIPT = 2002; //call ReceiptPreview Activity


    int ACTIVITY_INVOICE_RETURN = 3001; //call invoice Return Preview Activity
    int ACTIVITY_WITHOUT_INVOICE_RETURN = 3002;//call without invoice Return Preview Activity
}
