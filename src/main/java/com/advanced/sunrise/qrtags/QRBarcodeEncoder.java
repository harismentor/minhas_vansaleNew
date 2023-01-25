package com.advanced.minhas.qrtags;

import android.util.Base64;
import android.util.Log;

/**
 * @author AbdelWuadoud Rasmi
 *
 * This class is edited to used android Base64 class, to work on android devices
 */
public class QRBarcodeEncoder {

    private QRBarcodeEncoder() {
        //Factory method pattern
    }

    public static String encode(
            com.advanced.minhas.qrtags.Seller seller,
            com.advanced.minhas.qrtags.TaxNumber taxNumber,
            com.advanced.minhas.qrtags.InvoiceDate invoiceDate,
            com.advanced.minhas.qrtags.InvoiceTotalAmount invoiceTotalAmount,
            com.advanced.minhas.qrtags.InvoiceTaxAmount invoiceTaxAmount) {
        return toBase64(toTLV(seller, taxNumber, invoiceDate, invoiceTotalAmount, invoiceTaxAmount));
    }

    private static String toTLV(
            com.advanced.minhas.qrtags.Seller seller,
            com.advanced.minhas.qrtags.TaxNumber taxNumber,
            com.advanced.minhas.qrtags.InvoiceDate invoiceDate,
            com.advanced.minhas.qrtags.InvoiceTotalAmount invoiceTotalAmount,
            com.advanced.minhas.qrtags.InvoiceTaxAmount invoiceTaxAmount) {
        return seller.toString()
                + taxNumber.toString()
                + invoiceDate.toString()
                + invoiceTotalAmount.toString()
                + invoiceTaxAmount.toString();
    }

    private static String toBase64(String tlvString) {
        Log.e("tlvString",tlvString);
        return Base64.encodeToString(tlvString.getBytes(), 0);
    }

}
