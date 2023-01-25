package com.advanced.minhas.printerconnect.printutil;



import static com.advanced.minhas.config.ConfigPrinter.ENABLE_ITEM_VAT;
import static com.advanced.minhas.config.CustomConverter.convertNumberToEnglishWords;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.config.Generic.splitToNChar;
import static com.advanced.minhas.printerconnect.printutil.Util.rightAlignedNumbers;

import static java.text.NumberFormat.Field.CURRENCY;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

import com.advanced.minhas.config.ConfigPrinter;
import com.advanced.minhas.config.ConfigValue;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.model.DailyReport;
import com.advanced.minhas.model.Receipt;
import com.advanced.minhas.model.print.PosPrintModel;
import com.advanced.minhas.model.print.PrintHeadsModel;
import com.advanced.minhas.qrtags.InvoiceDate;
import com.advanced.minhas.qrtags.InvoiceTaxAmount;
import com.advanced.minhas.qrtags.InvoiceTotalAmount;
import com.advanced.minhas.qrtags.QRBarcodeEncoder;
import com.advanced.minhas.qrtags.Seller;
import com.advanced.minhas.qrtags.TaxNumber;
import com.advanced.minhas.session.SessionValue;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Sample_Print {

    private final char ESC = ESCPOS.ESC;

    private ESCPOSPrinter escposPrinter;
    private int rtn;
    private Context context;
    private SessionValue sessionValue;
    public Sample_Print(Context context)
    {
        // escposPrinter = new ESCPOSPrinter("EUC-KR"); // Korean.
        // escposPrinter = new ESCPOSPrinter("BIG5"); // BIG5.
        // escposPrinter = new ESCPOSPrinter("GB2312"); // GB2312.
        // escposPrinter = new ESCPOSPrinter("Shift_JIS"); // Japanese.
        escposPrinter = new ESCPOSPrinter();
        context=context;
    }

    public int Print_Sample_1() throws InterruptedException
    {

        try
        {
            rtn = escposPrinter.printerSts();
            Log.e("POS", "printerSts = " + rtn);
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
/*
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|1CNormal\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|2CDouble width\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|3CDouble height\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|4CDouble width/height\n");
*/
            escposPrinter.printNormal(ESC+"|cA"+ESC+"|2CReceipt\r\n\r\n\r\n");

            escposPrinter.printNormal(ESC+"|rATEL (123)-456-7890\n\n\n");
            escposPrinter.printNormal(ESC+"|cAThank you for coming to our shop!\n");
            escposPrinter.printNormal(ESC+"|cADate\n\n");
            escposPrinter.printNormal("Chicken                             $10.00\n");
            escposPrinter.printNormal("Hamburger                           $20.00\n");
            escposPrinter.printNormal("Pizza                               $30.00\n");
            escposPrinter.printNormal("Lemons                              $40.00\n");
            escposPrinter.printNormal("Drink                               $50.00\n");
            escposPrinter.printNormal("Excluded tax                       $150.00\n");
            escposPrinter.printNormal(ESC+"|uCTax(5%)                              $7.50\n");
            escposPrinter.printNormal(ESC+"|bC"+ESC+"|2CTotal         $157.50\n\n");
            escposPrinter.printNormal("Payment                            $200.00\n");
            escposPrinter.printNormal("Change                              $42.50\n\n");
            escposPrinter.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode
            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_ReceiptInvoce_80mm() throws InterruptedException
    {

//        try
//        {
//            rtn = escposPrinter.printerSts();
//
//      //      if( rtn != 0 )  return rtn;
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//            return rtn;
//        }

        try
        {
/*
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|1CNormal\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|2CDouble width\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|3CDouble height\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|4CDouble width/height\n");
*/

            String nextLine = "\n";
            String next2Line = "\n\n";
            // String line="--------------------------------------------------------------";
            String line="-----------------------------------------------";
            String itemHead=
                    Util.leftJustify("NO",3)
                            +Util.leftJustify("ITEM",10)
                            +Util.center("QTY",5)
                            +Util.center("UOM",5)
                            +Util.center("PRICE",6);
            if(ConfigPrinter.ENABLE_ITEM_DISCOUNT){
                itemHead=itemHead+Util.center("DISC",7);
            }if(ENABLE_ITEM_VAT){
            itemHead=itemHead+Util.center("VAT",7);
        }
            itemHead=itemHead+Util.rightJustify("TOTAL",7);



            String itemDetails="";
            int slNo=0;

            /*****TOTAL DETAILS******/

            String total="",discount="",afterDiscount="",roundoff="",vat="",grandTotal="" , new_outstandings="";





            String totalDetails=total
                    +nextLine
                    +discount

                    //+afterDiscount

                    //+vat
                    +nextLine
                    +roundoff
                    +nextLine
                    +grandTotal
                    +nextLine
                    +new_outstandings;




            escposPrinter.printText("Quotation       \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
    public int Print_dailysummary_58mm(DailyReport dailyrprt, PrintHeadsModel prnthead) throws InterruptedException{

        try
        {
            rtn = escposPrinter.printerSts();

            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            String nextLine = "\n";
            String next2Line = "\n\n";
            // String line="--------------------------------------------------------------";
            String line="--------------------------------";
            double cash_sale_tot = TextUtils.isEmpty(dailyrprt.getTotalCashSale()) ? 0 : Double.valueOf(dailyrprt.getTotalCashSale());
            double cash_cash_collctn_tot = TextUtils.isEmpty(dailyrprt.getTotalCashCollection()) ? 0 : Double.valueOf(dailyrprt.getTotalCashCollection());

            String dailyReportData =
                    nextLine
                            + "Tot Cash Sale       : " + (getAmount(Double.parseDouble(dailyrprt.getTotalCashSale())))
                            + nextLine
                            + "Tot Credit Sale     : " + (getAmount(Double.parseDouble(dailyrprt.getTotalCreditSale())))
                            + nextLine
                            + "Tot Return          : " + (getAmount(Double.parseDouble(dailyrprt.getTotalReturnSale())))
                            + nextLine
                            + "Tot Cash Clctn      : " + (getAmount(Double.parseDouble(dailyrprt.getTotalCashCollection())))
                            + nextLine
                            + "Tot Bank Clctn      : " + (getAmount(Double.parseDouble(dailyrprt.getTotalBankCollection())))
                            + nextLine
                            + "Tot Cheque Clctn    : " + (getAmount(Double.parseDouble(dailyrprt.getTotalChequeCollection())))
                            + nextLine
                            + "Tot Cash in hand    : " + (getAmount(cash_sale_tot+cash_cash_collctn_tot));




            escposPrinter.printText("   Daily Summary       \r\n\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.company_name+" \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.company_address+" \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);



            escposPrinter.printText(prnthead.getExec_name()+" \r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.getExec_id()+" \r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.getExec_mob()+" \r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.getRoute_mob()+" \r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.printText(dailyReportData+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_dailysummary_80mm(DailyReport dailyrprt, PrintHeadsModel prnthead) throws InterruptedException{

        try
        {
            rtn = escposPrinter.printerSts();

            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            String nextLine = "\n";
            String next2Line = "\n\n";
            // String line="--------------------------------------------------------------";
            String line="-----------------------------------------------";


            String strBalance = " ", strPaid = " ";

            String bilType = "DAY SUMMARY REPORT";



//            String compName = sessionValue.getCompanyDetails().get(PREF_COMPANY_NAME);  //name get from session
//            String addressContentStr = sessionValue.getCompanyDetails().get(PREF_COMPANY_ADDRESS_1);  //address get from session
//
//            String execName = "Executive : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
//            String execId = "Code      : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_ID);  //id get from session
//            String execMob = "Mobile    : " + sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_MOBILE);  //mob get from session
//
//            String routeMob = "Route Mobile No : " + sessionValue.getRegisteredMobile();  //route mob get from session

            double cash_sale_tot = TextUtils.isEmpty(dailyrprt.getTotalCashSale()) ? 0 : Double.valueOf(dailyrprt.getTotalCashSale());
            double cash_cash_collctn_tot = TextUtils.isEmpty(dailyrprt.getTotalCashCollection()) ? 0 : Double.valueOf(dailyrprt.getTotalCashCollection());

            String dailyReportData =
                    nextLine
                            + "Total Cash Sale          :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyrprt.getTotalCashSale())))
                            + nextLine
                            + "Total Credit Sale        :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyrprt.getTotalCreditSale())))
                            + nextLine
                            + "Total Return             :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyrprt.getTotalReturnSale())))
                            + nextLine
                            + "Total Cash Collection    :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyrprt.getTotalCashCollection())))
                            + nextLine
                            + "Total Bank Collection    :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyrprt.getTotalBankCollection())))
                            + nextLine
                            + "Total Cheque Collection  :" + rightAlignedNumbers(getAmount(Double.parseDouble(dailyrprt.getTotalChequeCollection())))
                            + nextLine
                            + "Tot Cash in hand         : " + rightAlignedNumbers(getAmount(cash_sale_tot+cash_cash_collctn_tot));



            escposPrinter.printText("Daily Summary       \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.company_name+" \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.company_address+" \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);



            escposPrinter.printText(prnthead.getExec_name()+" \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.getExec_id()+" \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.getExec_mob()+" \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(prnthead.getRoute_mob()+" \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.printText(dailyReportData+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
    public int Print_Receipt_80mm(Receipt recpt,String st_customername) throws InterruptedException{

        try
        {
            rtn = escposPrinter.printerSts();

            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            String nextLine = "\n";
            String next2Line = "\n\n";
            // String line="--------------------------------------------------------------";
            String line="-----------------------------------------------";


            String strBalance = " ", strPaid = " ";


            strBalance = getAmount(recpt.getCurrentBalanceAmount()) + " " + CURRENCY;
            strPaid = getAmount(recpt.getReceivedAmount()) + " " + CURRENCY;


            String paddedBalance = String.format("%-15s", strBalance);

            String paddedPaid = String.format("%15s", strPaid);

            String grand =  getAmount(recpt.getReceivedAmount()) ;

            String st_date =  "Date : "+recpt.getLogDate();
            String str_receiptno =  "No : "+recpt.getReceiptNo();
            String st_name =  "Name : "+st_customername;
            String st_receivedcash = "Received Cash with Thanks "+grand;
            String paddedGrandTotal = String.format("%15s", grand);
            String st_balamnt =  "Bal Amt : "+recpt.getCurrentBalanceAmount() ;
            String val_in_english = convertNumberToEnglishWords(String.valueOf(grand));
            String st_amntinwords =  "Amount in words : "+val_in_english ;
            String st_receivedby =  "Received by : " ;
            String st_total =  "Total : "+grand;


            String total= "Total  : "+paddedGrandTotal;

            /*****TOTAL DETAILS******/

            String st_lastrow="",receipt_no="",st_fifthrow="",vat="",st_fourthrow="" , st_thirdrow="",st_firstrow ="",st_scndrow ="",balance_amnt="";
            st_firstrow=Util.leftJustify(""+st_date,10)+Util.rightJustify(""+str_receiptno,30);
            st_scndrow=Util.leftJustify(""+st_name,10)+Util.rightJustify("",30);
           // receipt_no=Util.leftJustify("Receipt No :",10)+Util.rightJustify(""+recpt.getReceiptNo(),30);
            //st_name=Util.leftJustify("Name",10)+Util.rightJustify(recpt.getCustomername(),30);
            //afterDiscount=Util.leftJustify("AFTER DISCOUNT",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getTotalAmount(),16);
            //vat=Util.leftJustify("VAT TOTAL",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getVat_gstTotal(),20);
            st_thirdrow=Util.leftJustify(st_receivedcash,50);
            st_fourthrow=Util.leftJustify(st_balamnt,30)+Util.rightJustify(st_total,30);
            st_fifthrow = Util.leftJustify(st_amntinwords,40)+Util.rightJustify("",5);
            st_lastrow = Util.rightJustify(st_receivedby,40);

//            if(ConfigPrinter.ENABLE_LOGO){
//                escposPrinter.printBitmap(printModel.getCompanyDetailsPrintModel().getLogo(),LKPrint.LK_ALIGNMENT_CENTER,400);
//            }



            escposPrinter.printText("Receipt Voucher       \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.printText(st_firstrow+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(st_scndrow+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
           // escposPrinter.printText(st_name+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(st_thirdrow+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(st_fourthrow+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.printText(st_fifthrow+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(st_lastrow+"\r\n", LKPrint.LK_ALIGNMENT_RIGHT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_Invoce_80mm(PosPrintModel printModel,String vat_status,String st_newoutstanding) throws InterruptedException
    {

        try
        {
            rtn = escposPrinter.printerSts();

            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
/*
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|1CNormal\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|2CDouble width\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|3CDouble height\n");
			escposPrinter.printNormal(ESC+"|bC"+ESC+"|4CDouble width/height\n");
*/

            String nextLine = "\n";
            String next2Line = "\n\n";
            // String line="--------------------------------------------------------------";
            String line="-----------------------------------------------";
            String itemHead=
                    Util.leftJustify("NO",3)
                            +Util.leftJustify("ITEM",10)
                            +Util.center("QTY",5)
                            +Util.center("UOM",5)
                            +Util.center("PRICE",6);
            if(ConfigPrinter.ENABLE_ITEM_DISCOUNT){
                itemHead=itemHead+Util.center("DISC",7);
            }if(ConfigPrinter.ENABLE_ITEM_VAT){
            itemHead=itemHead+Util.center("VAT",7);
        }
            itemHead=itemHead+Util.rightJustify("TOTAL",7);



            String itemDetails="";
            int slNo=0;
            for (CartItem c : printModel.getItemDetails()) {


                String itemQty="",itemPrice="",itemTotalPrice="";

                StringBuilder itemName = new StringBuilder("");

                double netPrice=c.getProductPrice();


                //  String[] nameArr = splitToNChar(c.getProductName()+"("+c.getDescription()+")", 15);
                String[] nameArr = splitToNChar(c.getP_name(), 15);

                for (int i = 0; i < nameArr.length; i++) {

                    String paddedName = String.format("%-12s", nameArr[i]);
                    //
                    itemName.append(paddedName);
                    if (i != nameArr.length - 1)
                        itemName.append("\n");

                    if(paddedName.length()<15){
                        for(i=0;i<15-paddedName.length();i++){

                            itemName.append(" ");
                        }
                    }

                }
                itemQty=""+c.getTypeQuantity();
                itemPrice= getAmount(netPrice);

//                if (c.getOrderType().equals(ConfigValue.PRODUCT_UNIT_CASE)) {
//                    netPrice = netPrice * c.getPiecepercart();
//                    itemQty=c.getTypeQuantity()+"/0";
//                }
                // itemTotalPrice = getAmount(c.getProductPrice() * c.getTypeQuantity());

                itemTotalPrice = getAmount((c.getProductPrice() * c.getPieceQuantity_nw())-(c.getProductDiscount() * c.getPieceQuantity_nw()));



                slNo=slNo+1;
                String itemDetail=nextLine
                        +Util.leftJustify(String.valueOf(slNo),3)
                        +Util.leftJustify(itemName.toString(),10)
                        +Util.center(itemQty,5)
                        +Util.leftJustify(c.getOrderType(),5)
                        +Util.rightJustify(itemPrice,6);
                if(ConfigPrinter.ENABLE_ITEM_DISCOUNT){
                    itemDetail=itemDetail +Util.rightJustify(getAmount(c.getProductDiscount()),7);
                }if(ConfigPrinter.ENABLE_ITEM_VAT){
                    itemDetail=itemDetail+Util.rightJustify(getAmount(c.getTaxValue()),7);
                }
                itemDetail=itemDetail+Util.rightJustify(itemTotalPrice,7)+nextLine;

                itemDetails+=itemDetail;

            }
            String st_vat = printModel.getInvoiceTotalDetailsPrintModel().getVat_gstTotal();
            Log.e("vattot",st_vat);
            Log.e("date",printModel.getInvoiceDetailsPrintModel().getInvoiceDate());
            Log.e("totalamnt",""+printModel.getInvoiceTotalDetailsPrintModel().getTotalAmount());
            Log.e("gst",""+printModel.getInvoiceTotalDetailsPrintModel().getVat_gstTotal());
            double dbl_vattot = TextUtils.isEmpty(st_vat) ? 0 : Float.valueOf(st_vat);


            String qrBarcodeHash = QRBarcodeEncoder.encode(
                    new Seller(printModel.getCompanyDetailsPrintModel().getCompName()),
                    new TaxNumber(""+dbl_vattot),
                    new InvoiceDate("12-12-2022"),
                    new InvoiceTotalAmount(printModel.getInvoiceTotalDetailsPrintModel().getTotalAmount()),
                    new InvoiceTaxAmount(printModel.getInvoiceTotalDetailsPrintModel().getVat_gstTotal())
            );
            //  String st_data = str_qrcodelink+""+strBillNumber;
            MultiFormatWriter writr = new MultiFormatWriter();
            Bitmap bitmap =null;
            try{
                BitMatrix matrix = writr.encode(qrBarcodeHash, BarcodeFormat.QR_CODE,350,350);
                BarcodeEncoder encoder = new BarcodeEncoder();
                bitmap = encoder.createBitmap(matrix);

                // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.kanzologo);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image img = Image.getInstance(stream.toByteArray());
                img.setAbsolutePosition(15f, 100f);
                img.scalePercent(25f);
                //img.scalePercent(20f);
                img.setAlignment(Element.ALIGN_LEFT);


            }catch (Exception e){

            }


            /*****TOTAL DETAILS******/

            String total="",discount="",afterDiscount="",roundoff="",vat="",grandTotal="" , new_outstandings="";
            total=Util.leftJustify("TOTAL",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getItemTotal(),30);
            discount=Util.leftJustify("DISCOUNT",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getDiscountTotal(),30);
            //afterDiscount=Util.leftJustify("AFTER DISCOUNT",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getTotalAmount(),16);
            //vat=Util.leftJustify("VAT TOTAL",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getVat_gstTotal(),20);
            roundoff=Util.leftJustify("RoundOff",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getRoundoff(),30);
            grandTotal=Util.leftJustify("Net Amount",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getNetAmount(),30);
            new_outstandings=Util.leftJustify("Outstanding bal",10)+Util.rightJustify(st_newoutstanding,27);




            String totalDetails=total
                    +nextLine
                    +discount

                    //+afterDiscount

                    //+vat
                    +nextLine
                    +roundoff
                    +nextLine
                    +grandTotal
                    +nextLine
                    +new_outstandings;


//            if(ConfigPrinter.ENABLE_LOGO){
//                escposPrinter.printBitmap(printModel.getCompanyDetailsPrintModel().getLogo(),LKPrint.LK_ALIGNMENT_CENTER,400);
//            }

            if(vat_status.equals("Vat")){
                escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getCompName()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH);
                escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getCompanyAddress()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
                if(printModel.getCompanyDetailsPrintModel().getCompanyEmail().length()>1){
                    escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getCompanyEmail()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
                }
                if(printModel.getCompanyDetailsPrintModel().getCompanyPhone().length()>1){
                    escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getCompanyPhone()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

                }
            }


//            if(printModel.getCompanyDetailsPrintModel().getVat_gst_no().length()>1){
//                escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getVat_gst_no()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//
//            }
//            escposPrinter.printNormal(line+"\n");

            escposPrinter.printText("Quotation       \r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Customer       :"+printModel.getCustomerDetailsPrintModel().getCustomerName()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            // escposPrinter.printText("Customer TRN   :"+printModel.getCustomerDetailsPrintModel().getCustomerVat()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            //escposPrinter.printText("Customer Address"+printModel.getCustomerDetailsPrintModel().getCustomerAddress()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Invoice No     :"+printModel.getInvoiceDetailsPrintModel().getInvoiceNo()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Invoice Date   :"+printModel.getInvoiceDetailsPrintModel().getInvoiceDate()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Payment Type   :"+printModel.getSale_type()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            // escposPrinter.printText("Location       :"+printModel.getCustomerDetailsPrintModel().getCustomerAddress()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            // escposPrinter.printText("Salesman       :"+printModel.getCompanyDetailsPrintModel().getExecName()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            // escposPrinter.printText("Mobile No      :"+printModel.getCompanyDetailsPrintModel().getExecMob()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            String paymnt_type = Util.leftJustify("Sale Type",10)+Util.rightJustify(printModel.getSale_type(),30);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.printText(itemHead+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(itemDetails+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(totalDetails+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            if(ConfigPrinter.ENABLE_LOGO){
                escposPrinter.printBitmap(bitmap,LKPrint.LK_ALIGNMENT_CENTER,400);
            }
            escposPrinter.printText(printModel.getFooterDetailsPrintModel().getThanksMessage()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);


            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_Invoce_58mm(PosPrintModel printModel) throws InterruptedException
    {

        try
        {
            rtn = escposPrinter.printerSts();

            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {


            String nextLine = "\n";
            String next2Line = "\n\n";
            //String line="--------------------------------------------------------------";
            String line="-------------------------------";
            String itemHead=
                    Util.leftJustify("NO",3)
                            +Util.leftJustify("Description",14)
                            +Util.leftJustify("QTY",5)
                          //  +Util.center("UOM",5)
                            +Util.leftJustify("PRICE",7);
//            if(ConfigPrinter.ENABLE_ITEM_DISCOUNT){
//                itemHead=itemHead+Util.center("DISC",10);
            //}
        if(ENABLE_ITEM_VAT){
            itemHead=itemHead+Util.leftJustify("VAT",7);
        }
            itemHead=itemHead+Util.leftJustify("TOTAL",7);
        String str = printModel.getInvoiceDetailsPrintModel().getInvoiceDate();
            str =str.substring(0, 11);

            String customerHead=
                    Util.leftJustify("Inv No : "+printModel.getInvoiceDetailsPrintModel().getInvoiceNo(),10)
                            +Util.rightJustify("     Inv Date : "+str,10)
            +Util.rightJustify("\n\nMob No : "+printModel.getCompanyDetailsPrintModel().getExecMob(),10)
                            +Util.rightJustify("     Trn No  : "+printModel.getCustomerDetailsPrintModel().getCustomerVat(),10);
//            if(ConfigPrinter.ENABLE_ITEM_DISCOUNT){
//                itemHead=itemHead+Util.center("DISC",10);
            //}




            String itemDetails="";
            int slNo=0;
            for (CartItem c : printModel.getItemDetails()) {


                String itemQty="",itemPrice="",itemTotalPrice="";

                StringBuilder itemName = new StringBuilder("");

                double netPrice=c.getProductPrice();


                String[] nameArr = splitToNChar(c.getProductName()+"("+c.getDescription()+")", 10);

                for (int i = 0; i < nameArr.length; i++) {

                    String paddedName = String.format("%-10s", nameArr[i]);
                    //
                    itemName.append(paddedName);
                    if (i != nameArr.length - 1)
                        itemName.append("\n");

                    if(paddedName.length()<15){
                        for(i=0;i<15-paddedName.length();i++){

                            itemName.append(" ");
                        }
                    }

                }
                itemQty=""+c.getTypeQuantity();
                itemPrice= getAmount(netPrice);

                if (c.getOrderType().equals(ConfigValue.PRODUCT_UNIT_CASE)) {
                    netPrice = netPrice * c.getPiecepercart();
                    itemQty=c.getTypeQuantity()+"/0";
                }
                // itemTotalPrice = getAmount(c.getProductPrice() * c.getTypeQuantity());

                itemTotalPrice = getAmount((c.getProductPrice() * c.getPieceQuantity_nw())-(c.getProductDiscount() * c.getPieceQuantity_nw()));



                slNo=slNo+1;
                String itemDetail=nextLine
                        +Util.leftJustify(String.valueOf(slNo),3)
                        +Util.leftJustify(itemName.toString(),9)
                        +Util.leftJustify(itemQty,4)
                        //+Util.leftJustify(c.getOrderType(),10)
                        +Util.leftJustify(itemPrice,6);
//                if(ConfigPrinter.ENABLE_ITEM_DISCOUNT){
//                    itemDetail=itemDetail +Util.rightJustify(getAmount(c.getProductDiscount()),10);
               // }
            if(ENABLE_ITEM_VAT){
                    itemDetail=itemDetail+Util.leftJustify(getAmount(c.getTaxValue()),7);
                }
                itemDetail=itemDetail+Util.leftJustify(itemTotalPrice,7)+nextLine;

                itemDetails+=itemDetail;

            }

            /*****TOTAL DETAILS******/

            String total="",discount="",afterDiscount="",roundoff="",vat="",grandTotal="";
            total=Util.leftJustify("TOTAL",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getItemTotal(),20);
            discount=Util.leftJustify("DISCOUNT",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getDiscountTotal(),20);
            afterDiscount=Util.leftJustify("AFTER DISCOUNT",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getTotalAmount(),16);
            vat=Util.leftJustify("VAT TOTAL",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getVat_gstTotal(),20);
            roundoff=Util.leftJustify("RoundOff",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getRoundoff(),20);
            grandTotal=Util.rightJustify("Net Amount",10)+Util.rightJustify(printModel.getInvoiceTotalDetailsPrintModel().getNetAmount(),20);



//            String totalDetails=total
//                    +nextLine
//                    +discount
//                    +nextLine
//                    +afterDiscount
//                    +nextLine
//                    +vat
//                    +nextLine
//                    +roundoff
//                    +nextLine
//                    +grandTotal;

            String totalDetails=grandTotal;


            if(ConfigPrinter.ENABLE_LOGO){
                escposPrinter.printBitmap(printModel.getCompanyDetailsPrintModel().getLogo(),LKPrint.LK_ALIGNMENT_CENTER,400);
            }

            //escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getCompName()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH);
            //escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getCompanyAddress()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//            if(printModel.getCompanyDetailsPrintModel().getCompanyEmail().length()>1){
//                escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getCompanyEmail()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//            }
//            if(printModel.getCompanyDetailsPrintModel().getCompanyPhone().length()>1){
//                escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getCompanyPhone()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//
//            }
            if(printModel.getCompanyDetailsPrintModel().getVat_gst_no().length()>1){
                escposPrinter.printText(printModel.getCompanyDetailsPrintModel().getVat_gst_no()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            }
           // escposPrinter.printNormal(line+"\n");

            escposPrinter.printText(""+printModel.getCustomerDetailsPrintModel().getCustomerName()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            //escposPrinter.printText("Customer TRN   :"+printModel.getCustomerDetailsPrintModel().getCustomerVat()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(""+printModel.getCustomerDetailsPrintModel().getCustomerAddress()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//            escposPrinter.printText("Inv No :"+printModel.getInvoiceDetailsPrintModel().getInvoiceNo()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//            escposPrinter.printText("Inv Date :"+printModel.getInvoiceDetailsPrintModel().getInvoiceDate()+"\r\n", LKPrint.LK_ALIGNMENT_RIGHT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//            escposPrinter.printText("Location       :"+printModel.getCustomerDetailsPrintModel().getCustomerAddress()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//            escposPrinter.printText("Salesman       :"+printModel.getCompanyDetailsPrintModel().getExecName()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
//            escposPrinter.printText("Mobile No      :"+printModel.getCompanyDetailsPrintModel().getExecMob()+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
         //   escposPrinter.printNormal(line+"\n");
            escposPrinter.printNormal("\n\n");
            escposPrinter.printText(customerHead+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_FONTB, LKPrint.LK_FAIL);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

            escposPrinter.printText(itemHead+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_FONTB, LKPrint.LK_FAIL);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(itemDetails+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_FONTB, LKPrint.LK_FAIL);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(totalDetails+"\r\n", LKPrint.LK_ALIGNMENT_RIGHT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printNormal("\n\n");
            escposPrinter.printText(printModel.getFooterDetailsPrintModel().getThanksMessage()+"\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
           // escposPrinter.printText(line+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printNormal("\n\n");

            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }


    public int Print_Sample_2(ArrayList<CartItem> list, Bitmap logo) throws InterruptedException
    {
        try
        {
            rtn = escposPrinter.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {




            escposPrinter.printBitmap(logo,LKPrint.LK_ALIGNMENT_CENTER);
            escposPrinter.setAlignment(LKPrint.LK_ALIGNMENT_RIGHT);
            escposPrinter.printNormal("Right");
            /*escposPrinter.printText("Receipt\r\n\r\n\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH|LKPrint.LK_TXT_2HEIGHT);
            escposPrinter.printText("TEL (123)-456-7890\r\n", LKPrint.LK_ALIGNMENT_RIGHT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Thank you for coming to our shop!\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Chicken                             $10.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Hamburger                           $20.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Pizza                               $30.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Lemons                              $40.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Drink                               $50.00\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Excluded tax                       $150.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Tax(5%)                              $7.50\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_UNDERLINE, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Total         $157.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH);
            escposPrinter.printText("Payment                            $200.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printText("Change                              $42.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            // Reverse print
            //posPtr.printText("Change                              $42.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT | LKPrint.LK_FNT_REVERSE, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode
            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();*/
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_Image() throws InterruptedException
    {
        try
        {
            rtn = escposPrinter.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            escposPrinter.printBitmap("//sdcard//temp//test//car_s.jpg", LKPrint.LK_ALIGNMENT_CENTER);
            escposPrinter.printBitmap("//sdcard//temp//test//danmark_windmill.jpg", LKPrint.LK_ALIGNMENT_LEFT);
            escposPrinter.printBitmap("//sdcard//temp//test//denmark_flag.jpg", LKPrint.LK_ALIGNMENT_RIGHT);
            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
    public int Print_westernLatinCharTest() throws InterruptedException
    {
        final char [] diff = {0x23,0x24,0x40,0x5B,0x5C,0x5D,0x5E,0x6C,0x7B,0x7C,0x7D,0x7E,
                0xA4,0xA6,0xA8,0xB4,0xB8,0xBC,0xBD,0xBE};
        try
        {
            rtn = escposPrinter.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            String ad = new String(diff);
            escposPrinter.printText(ad+"\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_1D_Barcode() throws InterruptedException
    {
        String barCodeData = "123456789012";

        try
        {
            rtn = escposPrinter.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            escposPrinter.printString("UPCA\r\n");
            escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_UPCA, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.printString("UPCE\r\n");
            escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_UPCE, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.printString("EAN8\r\n");
            escposPrinter.printBarCode("1234567", ESCPOSConst.LK_BCS_EAN8, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.printString("EAN13\r\n");
            escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_EAN13, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.printString("CODE39\r\n");
            escposPrinter.printBarCode("ABCDEFGHI", ESCPOSConst.LK_BCS_Code39, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.printString("ITF\r\n");
            escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_ITF, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.printString("CODABAR\r\n");
            escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_Codabar, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.printString("CODE93\r\n");
            escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_Code93, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.printString("CODE128\r\n");
            escposPrinter.printBarCode("{BNo.{C4567890120", ESCPOSConst.LK_BCS_Code128, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_2D_Barcode() throws InterruptedException
    {
        String data = "ABCDEFGHIJKLMN";
        try
        {
            rtn = escposPrinter.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            escposPrinter.printString("PDF417\r\n");
            escposPrinter.printPDF417(data, data.length(), 0, 10, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printString("QRCode\r\n");
            escposPrinter.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_Android_Font() throws InterruptedException
    {
        String data = "Receipt";
//    	String data = "영수증";
        Typeface typeface = null;

        try
        {
            rtn = escposPrinter.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            escposPrinter.printAndroidFont(data, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);
            escposPrinter.lineFeed(2);
            escposPrinter.printAndroidFont("Left Alignment", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont("Center Alignment", 512, 24, ESCPOSConst.LK_ALIGNMENT_CENTER);
            escposPrinter.printAndroidFont("Right Alignment", 512, 24, ESCPOSConst.LK_ALIGNMENT_RIGHT);

            escposPrinter.lineFeed(2);
            escposPrinter.printAndroidFont(Typeface.SANS_SERIF, "SANS_SERIF : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SERIF, "SERIF : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(typeface.MONOSPACE, "MONOSPACE : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

            escposPrinter.lineFeed(2);
            escposPrinter.printAndroidFont(Typeface.SANS_SERIF, "SANS : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SANS_SERIF, true, "SANS BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SANS_SERIF, true, false, "SANS BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SANS_SERIF, false, true, "SANS ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SANS_SERIF, true, true, "SANS BOLD ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SANS_SERIF, true, true, true, "SANS B/I/U : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

            escposPrinter.lineFeed(2);
            escposPrinter.printAndroidFont(Typeface.SERIF, "SERIF : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SERIF, true, "SERIF BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SERIF, true, false, "SERIF BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SERIF, false, true, "SERIF ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SERIF, true, true, "SERIF BOLD ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.SERIF, true, true, true, "SERIF B/I/U : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

            escposPrinter.lineFeed(2);
            escposPrinter.printAndroidFont(Typeface.MONOSPACE, "MONOSPACE : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.MONOSPACE, true, "MONO BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.MONOSPACE, true, false, "MONO BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.MONOSPACE, false, true, "MONO ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.MONOSPACE, true, true, "MONO BOLD ITALIC: 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            escposPrinter.printAndroidFont(Typeface.MONOSPACE, true, true, true, "MONO B/I/U: 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_Multilingual() throws InterruptedException
    {
        String Koreandata = "영수증";
        String Turkishdata = "Turkish(İ,Ş,Ğ)";
        String Russiandata = "Получение";
        String Arabicdata = "الإيصال";
        String Greekdata = "Παραλαβή";
        String Japanesedata = "領収書";
        String GB2312data = "收据";
        String BIG5data = "收據";

        try
        {
            rtn = escposPrinter.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            escposPrinter.printAndroidFont("Korean Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            // Korean 100-dot size font in android device.
            escposPrinter.printAndroidFont(Koreandata, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

            escposPrinter.printAndroidFont("Turkish Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            // Turkish 50-dot size font in android device.
            escposPrinter.printAndroidFont(Turkishdata, 512, 50, ESCPOSConst.LK_ALIGNMENT_CENTER);

            escposPrinter.printAndroidFont("Russian Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            // Russian 60-dot size font in android device.
            escposPrinter.printAndroidFont(Russiandata, 512, 60, ESCPOSConst.LK_ALIGNMENT_CENTER);

            escposPrinter.printAndroidFont("Arabic Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            // Arabic 100-dot size font in android device.
            escposPrinter.printAndroidFont(Arabicdata, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

            escposPrinter.printAndroidFont("Greek Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            // Greek 60-dot size font in android device.
            escposPrinter.printAndroidFont(Greekdata, 512, 60, ESCPOSConst.LK_ALIGNMENT_CENTER);

            escposPrinter.printAndroidFont("Japanese Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            // Japanese 100-dot size font in android device.
            escposPrinter.printAndroidFont(Japanesedata, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

            escposPrinter.printAndroidFont("GB2312 Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            // GB2312 100-dot size font in android device.
            escposPrinter.printAndroidFont(GB2312data, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

            escposPrinter.printAndroidFont("BIG5 Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
            // BIG5 100-dot size font in android device.
            escposPrinter.printAndroidFont(BIG5data, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

            escposPrinter.lineFeed(4);
            escposPrinter.cutPaper();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    public int Print_PDF() throws InterruptedException
    {
        try
        {
            rtn = escposPrinter.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            escposPrinter.printPDFFile("//sdcard//temp//test//PDF_Sample.pdf", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_PAPER_POS_3INCH, 2);
            escposPrinter.lineFeed(3);
            escposPrinter.cutPaper();

            //escposPrinter.printPDFFile("//sdcard//temp//test//PDF_Sample.pdf", LKPrint.LK_ALIGNMENT_LEFT, 500, 2);  //custom size
            //escposPrinter.lineFeed(3);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        return 0;
    }
}
