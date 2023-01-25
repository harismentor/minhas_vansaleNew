package com.advanced.minhas.config;


import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.advanced.minhas.config.ConfigValue.TAX_EXCLUSIVE;
import static com.advanced.minhas.config.ConfigValue.TAX_INCLUSIVE;
import static com.advanced.minhas.config.PrintConsole.printLog;

public abstract class AmountCalculator {

    private static String TAX_TYPE = TAX_EXCLUSIVE;


    public static double getTaxPrice(double price, float percentage,String taxtype) {
        Log.e("price",""+price);
        Log.e("percentage",""+percentage);
        Log.e("taxtype",""+taxtype);
        taxtype ="TAX_INCLUSIVE";
        double taxPrice = 0;
        try {
            if (taxtype.equals("TAX_INCLUSIVE")) {
                Log.e("if", "" + taxtype);
                taxPrice = getInclusiveTaxAmount(price, percentage);
            }
            else{
            Log.e("else", "" + taxtype);
            taxPrice = getExclusiveTaxAmount(price, percentage);
        }
            taxPrice = getExclusiveTaxAmount(price, percentage);
        } catch (NumberFormatException e) {
            printLog("AmountCalculator", "getTaxPrice  Exception " + e.getMessage());
            e.printStackTrace();
        }
        Log.e("taxPrice hr",""+taxPrice);
        return taxPrice;
    }
    public static double productprice_inclusive(double rate,double vatpercentage){
        double unit_price =0;
        unit_price = rate-((rate/(100+vatpercentage))*vatpercentage);

        return unit_price;
    }

    public static float getTaxPricefl(double price, float percentage,String taxtype) {
        Log.e("price",""+price);
        Log.e("percentage",""+percentage);
        Log.e("taxtype",""+taxtype);
       // taxtype ="TAX_INCLUSIVE";
        float taxPrice = 0;
        try {
            if (taxtype.equals("TAX_INCLUSIVE")) {
                Log.e("if", "" + taxtype);
                taxPrice = (float) getInclusiveTaxAmountfl(price, percentage);
            }
            else{
                Log.e("else", "" + taxtype);
                taxPrice = (float) getExclusiveTaxAmountfl(price, percentage);
            }
           // taxPrice = getExclusiveTaxAmount(price, percentage);
        } catch (NumberFormatException e) {
            printLog("AmountCalculator", "getTaxPrice  Exception " + e.getMessage());
            e.printStackTrace();
        }
        Log.e("taxPrice hr",""+taxPrice);
        return taxPrice;
    }


    public static double getWithoutTaxPrice(double price, float percentage) {
        double taxPrice = 0;
        try {
            if (TAX_TYPE.equals(TAX_INCLUSIVE))
                taxPrice = getInclusiveUnitPrice(price, percentage);
            else
                taxPrice = price;
        } catch (Exception e) {
            printLog("getWithoutTaxPrice", "getTaxPrice  Exception " + e.getMessage());
            e.printStackTrace();
        }
        return taxPrice;
    }


    public static double getBonusAmount(double price, float percentage){
        double bonusamount = 0;
        try {

            bonusamount = (price*percentage)/100;

           }catch (Exception e){

           }
           return bonusamount;

    }

    public static double getSalePrice(double price, float percentage, String taxtype) {
        double taxPrice = 0;
        if(taxtype.equals("")){
            taxtype="TAX_EXCLUSIVE";
        }
        Log.e("price",""+price);
        try {
            if (taxtype.equals("TAX_INCLUSIVE")) {
                Log.e("taxtype if",""+taxtype);
                taxPrice = price;
            }
            else {
                Log.e("taxtype else",""+taxtype);
                taxPrice = getExclusiveSalePrice(price, percentage);
            }
        } catch (Exception e) {
            printLog("AmountCalculator", "getSalePrice  Exception " + e.getMessage());
            e.printStackTrace();
        }
        Log.e("taxPrice hr",""+taxPrice);
        return taxPrice;
    }


    //    find  percentage  calculate
    public static double getPercentage(double total, double percentageValue) {
        double percentage = 0.0f;
        try {
            percentage = (percentageValue / total) * 100;
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return percentage;
    }


    //    find  percentage value calculate
    public static double getPercentageValue(double value, float percentage) {
        double percentageAmount = 0;
        try {
            percentageAmount = (value * percentage) / 100;//         percentage  calculate
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return percentageAmount;
    }


    /***
     * tax calculation
     * */

    //    find from mrp amount for tax amount(exclusive)
    private static double getExclusiveTaxAmount(double withTaxPrice, float percentage) {
        Log.e("withTaxPrice",""+withTaxPrice);
        Log.e("percentage",""+percentage);
        double taxAmount = 0.0f;
        try {
            taxAmount = (withTaxPrice * percentage) / 100;//         percentage  calculate total amount
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        Log.e("taxAmount",""+taxAmount);
        return taxAmount;
    }

    private static double getExclusiveTaxAmountfl(double withTaxPrice, float percentage) {
        Log.e("withTaxPrice exc",""+withTaxPrice);
        Log.e("percentage",""+percentage);
        float taxAmount = 0.0f;
        try {
            taxAmount =(float) (withTaxPrice * percentage) / 100;//         percentage  calculate total amount
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        Log.e("taxAmount",""+taxAmount);
        return taxAmount;
    }

    //    find for sale price from price amount (exclusive)
    private static double getExclusiveSalePrice(double price, float percentage) {
        double salePrice = 0.0f;
        try {
            salePrice = (price * percentage) / 100;//        Percentage  calculate total amount
            salePrice = price + salePrice; //added tax value to price
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return salePrice;
    }

    //     find  for tax amount from price(Inclusive)
    private static double getInclusiveTaxAmount(double price, float percentage) {
        Log.e("priceinc",""+price);
        Log.e("percentageincl",""+percentage);
        double taxAmount = 0.0f;
        try {
//            taxAmount = (price * percentage) / (100 + percentage);  //getting tax value
            taxAmount = price / (100 + percentage)*percentage;  //getting tax value
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return taxAmount;
    }

    //     find  for tax amount from price(Inclusive)
    private static double getInclusiveTaxAmountfl(double price, float percentage) {
        Log.e("priceinc",""+price);
        Log.e("percentageincl",""+percentage);
        float taxAmount = 0.0f;
        try {
//            taxAmount = (price * percentage) / (100 + percentage);  //getting tax value
            taxAmount = (float)price / (100 + percentage)*percentage;  //getting tax value
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return taxAmount;
    }

    //    find  for without tax amount from sale price(Inclusive)
    private static double getInclusiveUnitPrice(double price, float percentage) {
        double unitPrice = 0.0f;
        try {
            unitPrice = (price * percentage) / (100 + percentage);  //getting tax value
            unitPrice = price - unitPrice;  //get unit price
        } catch (IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return unitPrice;
    }

    class DecimalDigitsInputFilter implements InputFilter {
        private Pattern mPattern;
        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }
}
