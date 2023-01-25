package com.advanced.minhas.config;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mentor on 14/8/17.
 */

public abstract class Generic {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
    public static SimpleDateFormat PRINT_FORMAT = new SimpleDateFormat("dd-MMM-yyyy h:mm:ss a", Locale.ENGLISH);
    public static SimpleDateFormat PRINT_FORMATNEW = new SimpleDateFormat("dd-MMM-yyyy ", Locale.ENGLISH);
    public static SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static SimpleDateFormat dbDateFormatnew = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a", Locale.getDefault());


//    //    double value to amount format
//    public static String getAmount(double amount) {
//        String str = "";
//        try {
//            DecimalFormat formatter = new DecimalFormat("##,##,##,###.00");
//            str = formatter.format(amount);
//        } catch (IllegalArgumentException e) {
//            e.fillInStackTrace();
//        }
//        return str;
//    }
    //    double value to amount format
    public static String getAmount(double amount) {
        String str = "";
        try {
            DecimalFormat formatter = new DecimalFormat("##,##,##,###.00");
            str = formatter.format(amount);
            if (str.contains(",")){
                str = str.replaceAll(",","");
            }
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return str;
    }

    //    double value to amount format
    public static String getAmountthree(double amount) {
        String str = "";
        try {
            DecimalFormat formatter = new DecimalFormat("##,##,##,###.000");
            str = formatter.format(amount);
            if (str.contains(",")){
                str = str.replaceAll(",","");
            }
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return str;
    }

    public static String getAmountNew(double amount) {
        String str = "";
        try {
            DecimalFormat formatter = new DecimalFormat("##,##,##,###.00");
            str = formatter.format(amount);
            if (str.contains(",")){
                str = str.replaceAll(",","");
            }
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return str;
    }
    //    double value to amount format

    public static String getAmountFullView(double amount) {
        String str = "";
        try {
            DecimalFormat formatter = new DecimalFormat("##,##,##,###.00#######");
            str = formatter.format(amount);
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return str;
    }

    //    double value to amount format
    public static float getRoundOfAmount(float amount) {
        float val = 0;
        try {

            double roundedValue = Math.round(amount * 100.0) / 100.0;

            val = Float.parseFloat(String.valueOf(roundedValue));

        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
            val = amount;

        }
        return val;
    }


    private static String getDateTime() {
//         SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm:ss a", Locale.getDefault());

//        public static SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("dd-MM-yyyy h:mm:ss a zz", Locale.ENGLISH);


        Date date = new Date();
        return dbDateFormat.format(date);
    }

    /**
     * Split text into n number of characters.
     *
     * @param text the text to be split.
     * @param size the split size.
     * @return an array of the split text.
     */
    public static String[] splitToNChar(String text, int size) {
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts.toArray(new String[0]);
    }

    /*

        //    create percentage value
        public static float getTaxAmount(float total, float percentage) {
            float taxAmount = 0.0f;
            try {

    //         Percentage  calculate total amount
                taxAmount = (total * percentage) / 100;

                taxAmount = (taxAmount / (total + taxAmount) * 100); //getting tax value

                taxAmount = (total * taxAmount) / 100;  //get unit price

            } catch (IllegalArgumentException e) {
                e.fillInStackTrace();
            }
            return taxAmount;
        }


        //    convert  unit price from mrp
    //    create remove percentage
        public static float getWithoutTaxPrice(float salePrice, float tax) {
            float unitPrice = 0.0f;
            try {

    //        percentage  calculate total amount
                unitPrice = (salePrice * tax) / 100;
                unitPrice = (unitPrice / (salePrice + unitPrice) * 100); //getting tax value
                unitPrice = salePrice - (salePrice * unitPrice) / 100;  //get unit price
            } catch (IllegalArgumentException e) {
                e.fillInStackTrace();
            }
            return unitPrice;
        }

    */
    public static String generateInvoiceCode(String invPrefix, String invCode) {

        String newCode = "";
        try {

            if (!TextUtils.isEmpty(invCode) && invCode.length() > invPrefix.length()) {
                newCode = invCode.substring(invPrefix.length(), invCode.length());
            }


//        newCode = invCode.replaceAll("[^\\d.]", "");

            int invNumber = TextUtils.isEmpty(newCode) ? 0 : Integer.valueOf(newCode);

            newCode = String.valueOf(invPrefix + (invNumber + 1));
        } catch (NumberFormatException e) {
            e.getMessage();
        }
        return newCode;
    }

    public static String generateNewNumber(String invNo) {
        String newCode = "";
        try {
            int invNumber = TextUtils.isEmpty(invNo) ? 0 : Integer.valueOf(invNo);
            newCode = String.valueOf(invNumber + 1);

        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return newCode;
    }

    private static int getNumberFromString(String str) {

        int number = 0;

        Pattern pattern = Pattern.compile("\\w+([0-9]+)\\w+([0-9]+)");
        Matcher matcher = pattern.matcher(str);
        for (int i = 0; i < matcher.groupCount(); i++) {
            matcher.find();
            number = TextUtils.isEmpty(matcher.group()) ? 0 : Integer.valueOf(matcher.group());
        }

        return number;
    }

    private static String firstTwo(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }

    public static int getInvoiceNumberFromCode(String invCode) {

        String rephrase = "";
        if (!TextUtils.isEmpty(invCode) && invCode.length() > 3) {
            rephrase = invCode.substring(2, invCode.length());
        }

        int invNumber = TextUtils.isEmpty(rephrase) ? 0 : Integer.valueOf(rephrase);
//        assertEquals("RON: Legacy", rephrase);


        return invNumber;
    }


    //trim string
    public static String getMaximumChar(String text, int maximumSize) {

        int maxLength = (text.length() < maximumSize) ? text.length() : maximumSize;
        text = text.substring(0, maxLength);

        return text;
    }


    /**
     * Convert string to date.
     *
     * @param date String to convert with format yyyy-MM-dd
     * @return Date
     */
    public static Date stringToDate(String date) {
        if (date == null || date.equals("")) return null;

     //   DateFormat df = new SimpleDateFormat("dd-MM-yyyy");


        Date today = null;

        try {
            today = dbDateFormat.parse(date);

           // today=df.parse(date);



        } catch (ParseException e) {
        }

        return today;
    }


    /**
     * Convert date to string with default format yyyy-MM-dd
     *
     * @param date Date to convert
     * @return String of date.
     */
    public static String dateToFormat(Date date) {

        return dateFormat.format(date);

    }

    public static String timeToFormat(Date date) {

        return timeFormat.format(date);

    }

    public static String getPrintDate(Date date) {

        return PRINT_FORMAT.format(date);
    }


}
