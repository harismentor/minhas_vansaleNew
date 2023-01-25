package com.advanced.minhas.activity.report;

import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.advanced.minhas.R;
import com.advanced.minhas.adapter.DailyReportAdapter;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.DailyReport;
import com.advanced.minhas.utils.ExcelUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;
import com.itextpdf.text.pdf.languages.LanguageProcessor;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class Report_Daily extends AppCompatActivity implements View.OnClickListener{

    private static String FILE_PATH = Environment.getExternalStorageDirectory() + "/report.pdf";


    private MyDatabase myDatabase;

    private DailyReportAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<DailyReport> list = new ArrayList<>();
    private ImageButton ibBack;
    private LinearLayout lyt_share,ly_sync;
    DailyReport dailyReport;
    TextView txt_cash_sale,txt_credit_sale,txt_return_sale,txt_cash_collection,txt_bank_collection,txt_cheque_collection;

    private PdfPCell cell;
    private Image imgReportLogo;

    BaseColor headColor = WebColors.getRGBColor("#DEDEDE");
    //BaseColor tableHeadColor = WebColors.getRGBColor("#F5ABAB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_daily);


        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        lyt_share =  findViewById(R.id.lyt_share);
        ly_sync =  findViewById(R.id.ly_sync);
        recyclerView = findViewById(R.id.recyclerview_daily_report);

        txt_cash_sale =  findViewById(R.id.txt_cash_sale);
        txt_credit_sale =  findViewById(R.id.txt_credit_sale);
        txt_return_sale =  findViewById(R.id.txt_return_sale);
        txt_cash_collection =  findViewById(R.id.txt_cash_collection);
        txt_bank_collection =  findViewById(R.id.txt_bank_collection);
        txt_cheque_collection =  findViewById(R.id.txt_cheque_collection);

        myDatabase = new MyDatabase(Report_Daily.this);
        adapter = new DailyReportAdapter(list);
        list=myDatabase.getDailyReport();
        dailyReport=myDatabase.getDaySummaryReport();


        Log.e("sizelist",""+list.size());
        // sales.addAll(myDatabase.getFullSales());

        setrecyclerview();
        setDayReport();

        ly_sync.setVisibility(View.GONE);
        lyt_share.setVisibility(View.VISIBLE);
        ibBack.setOnClickListener(this);
        lyt_share.setOnClickListener(this);

    }

    public void setDayReport(){
        txt_cash_sale.setText(dailyReport.getTotalCashSale());
        txt_credit_sale.setText(dailyReport.getTotalCreditSale());
        txt_return_sale.setText(dailyReport.getTotalReturnSale());
        txt_cash_collection.setText(dailyReport.getTotalCashCollection());
        txt_bank_collection.setText(dailyReport.getTotalBankCollection());
        txt_cheque_collection.setText(dailyReport.getTotalChequeCollection());
    }
    private void setrecyclerview() {
        adapter = new DailyReportAdapter(list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(Report_Daily.this)
                .showLastDivider()
                .build());
        recyclerView.setLayoutManager(new LinearLayoutManager(Report_Daily.this));
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imageButton_toolbar_back:

                onBackPressed();
                break;
            case R.id.lyt_share:

                //createExcelSheet(list);


                Log.e("Clicked","Clicked");
                if (isStoragePermissionGranted()) {
                    Log.e("Quotation", "else" + ActivityConstants.ACTIVITY_QUOTATION);


                    createPDF(list,dailyReport);
                   // printwithbackgroundInvoice(getPdfModels(cartItems));
                }
                break;



        }
    }

    private void createExcelSheet(List<DailyReport> dataList)
    {


        boolean isExcelGenerated = ExcelUtils.exportDailyReportIntoWorkbook(Report_Daily.this,
                "EXCEL_FILE_NAME", dataList);


        if(isExcelGenerated){
            Toast.makeText(Report_Daily.this,"Execel Generated",Toast.LENGTH_LONG).show();

            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            if (!hasPermissions(Report_Daily.this, PERMISSIONS)) {

                ActivityCompat.requestPermissions(Report_Daily.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 786);

            }else{
                shareFile();

            }
        }else{
            Toast.makeText(Report_Daily.this,"Execel Not Generated",Toast.LENGTH_LONG).show();

        }


    }

    private void printwithbackgroundInvoice(List<DailyReport> dataList) {


        File myFile = null;

        PdfWriter writer;
        try {
            String compName ="";

            compName = "Nihal";


            Document document = new Document(PageSize.A4); //A4

            writer = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH));

            myFile = new File(FILE_PATH);

            document.open();


//            BaseFont bf = BaseFont.createFont("/assets/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont bf = BaseFont.createFont("/assets/dejavu_sans_condensed.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);


            Font font20 = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);

            Font font18 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

            Font font14 = new Font(Font.FontFamily.TIMES_ROMAN, 14);


            Font font10Bold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);

            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);

            Font font8bold = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.BOLD);

            Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);

            Font font12Bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);


            LanguageProcessor arabicPro = new ArabicLigaturizer();
            Font fontArb8 = new Font(bf, 8);
            Font fontArb10 = new Font(bf, 10);
            Font fontcompany10 = new Font(bf, 10, Font.BOLD);
            Font fontArb14 = new Font(bf, 14);
            Font fontArb10bl = new Font(bf, 10);
            fontArb10bl.setColor(BaseColor.BLACK);




                String netTotal = "", discount = "",  roundOff = "", hsn_code_total ="";

                String grandTotal = "";
                String paid = "";
                String balance = "";
                //haris
                String central_tax_rate = "";
                String central_tax_amnt = "";
                String state_tax_rate = "";
                String state_tax_amnt = "";
                String taxable_total = "";
                String total_tax = "";
                String total_discount = "";
                String stdb_grandtot ="";
                String discount_percntge = "";
                String st_data = "";
                String val_in_english = "";
                String val_in_english_vat ="";
                String valtaxableamnt_in_english = "";
                String val_in_Arabic = "";
                double taxable_amnt =0;

                String st_taxable_amnt = "";
                String total_discountnw = "";
                String totalVat = "";


                Paragraph compNamePhone = new Paragraph(compName, fontcompany10);
                compNamePhone.setAlignment(Element.ALIGN_LEFT);






                //////////////////////////////////////////

                PdfPCell cell;  //default cell

                //space cell
                PdfPCell cellSpace = new PdfPCell();
                cellSpace.setPadding(10);
                cellSpace.setBorder(PdfPCell.BOTTOM);
                cellSpace.setHorizontalAlignment(Element.ALIGN_CENTER);

                //Create the table which will be 2 Columns wide and make it 100% of the page
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{5, 5, 5});

                PdfPCell cellLogo = new PdfPCell();
                cellLogo.setBorder(PdfPCell.TOP );
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setPaddingTop(10);
                cellLogo.setPaddingBottom(5);
//                cellLogo.setPaddingRight(5);
//                cellLogo.setPaddingLeft(5);

                try {

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icresp_newlogo);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image img = Image.getInstance(stream.toByteArray());
//                  img.setAbsolutePosition(25f, 735f);
                    img.scalePercent(25f);
                    img.setAlignment(Element.ALIGN_CENTER);
                    cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellLogo.addElement(img);


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                PdfPCell cellTitle = new PdfPCell();
                cellTitle.setBorder(PdfPCell.LEFT | PdfPCell.TOP );

                cellTitle.setPadding(1);

                Paragraph paragraph = new Paragraph(arabicPro.process("" + " TAX INVOICE"), fontArb14);


                paragraph.setAlignment(Element.ALIGN_CENTER);
                cellTitle.setHorizontalAlignment(Element.ALIGN_LEFT);



                cellTitle.addElement(compNamePhone);




                ////////////////3rd column



                table.addCell(cellTitle);
                table.addCell(cellLogo);

                document.add(table);

                ///////////////////////////////////////////


                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
//              table.setSpacingBefore(10);
                table.setWidths(new int[]{1});

                PdfPCell celltitle = new PdfPCell();
                celltitle.setBorder(PdfPCell.LEFT | PdfPCell.BOTTOM |PdfPCell.RIGHT);
                celltitle.setHorizontalAlignment(Element.ALIGN_CENTER);
                paragraph = new Paragraph(  " CREDIT INVOICE " + arabicPro.process(""), font10Bold);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                celltitle.addElement(paragraph);
                celltitle.setPaddingTop(1);
                celltitle.setPaddingBottom(5);
                celltitle.setPaddingRight(5);
                celltitle.setPaddingLeft(5);

                table.addCell(celltitle);

                document.add(table);


                //second table


                //Create the table which will be 3 Columns wide and make it 100% of the page
                table = new PdfPTable(3);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{4,4,4});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.TOP | PdfPCell.LEFT );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //230

                //            customer Address  label
                paragraph = new Paragraph("Mr/Ms   :"+"Shana" , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.NO_BORDER );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                //230

                //            customer Address  label
                paragraph = new Paragraph("" , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);



                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.TOP | PdfPCell.RIGHT );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);




                table.addCell(cell);

                // customer details


                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("" , font8bold);//strDate
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);
                cell.setPaddingRight(10);

                table.addCell(cell);


                cell = new PdfPCell();
                cell.setBorder( PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label


                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(2);
                //230

                //            customer Address  label
                paragraph = new Paragraph("Address  :" , font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);


                table.addCell(cell);



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(1);
                //230

                paragraph = new Paragraph("" , font8bold);//SELECTED_SALES.getPayment_type()
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(1);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("Driver Name  :" , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);


                table.addCell(cell);

                //validity

                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(2);


                //            customer Address  label
                paragraph = new Paragraph("VAT NO : " , font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);


                table.addCell(cell);



                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(1);
                //230

                paragraph = new Paragraph("", font8bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("Vehicle No : ", font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);


                table.addCell(cell);




                cell = new PdfPCell();
                cell.setBorder(PdfPCell.LEFT );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);

                ///contact
                //            customer Address  label
                paragraph = new Paragraph("Tel No :" , font8bold);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);


                table.addCell(cell);



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);

                cell.setPaddingTop(0);
                //230

                paragraph = new Paragraph("", font8bold);
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setPaddingBottom(1);
                cell.setPaddingTop(0);

                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(PdfPCell.RIGHT );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(0);
                //230

                //            customer Address  label
                paragraph = new Paragraph("          " , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setPaddingTop(0);


                table.addCell(cell);


                document.add(table);

                //Create the table which will be 8 Columns wide and make it 100% of the page
                table = new PdfPTable(7);
                table.setWidths(new int[]{3,11,4, 3, 4, 4,4});
                table.setWidthPercentage(100);
//            table.setSpacingBefore(10);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("SL No\n" + arabicPro.process("رقم مسلسل"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Description\n" + arabicPro.process("البيان"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);



                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Unit\n" + arabicPro.process("وحدة"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);

                table.addCell(cell);


                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Qty\n" + arabicPro.process("كمية"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);




                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Rate\n" + arabicPro.process("سعر"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);





                cell = new PdfPCell();
                cell.setPaddingBottom(5);
                paragraph = new Paragraph("Discount \n" + arabicPro.process(""), fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph("Amount\n" + arabicPro.process("فيمة"), fontArb8);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setRowspan(2);
                table.addCell(cell);





                //////////////////////////////


                table = new PdfPTable(4); // 4
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5,2,2,2}); // 5,2,2,1

                //            Amount
                cell = new PdfPCell();
                cell = new PdfPCell(new Phrase("Amount In Words  : "+val_in_english , font8)); // Net Amount

                cell.setBorder(PdfPCell.BOX);
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setRowspan(6);


                table.addCell(cell);


                //////////////////////******   total details ****//////////////////////

                //      net total amount

                cell = new PdfPCell(new Phrase("Total Amount" , font8)); // Net Amount
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase( arabicPro.process("المبلغ الصافى"), fontArb8));
                cell.setPadding(3);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""+netTotal, font8));
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);


                //      discount amount

                cell = new PdfPCell(new Phrase("Discount " , font8)); // Net Amount
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase( arabicPro.process("الخصم"), fontArb8));
                cell.setPadding(3);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.BOTTOM);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""+discount, font8));
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);

                //      vat amount

                cell = new PdfPCell(new Phrase("Vat " , font8)); // Net Amount
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase( arabicPro.process("الضريبة نسبة"), fontArb8));
                cell.setPadding(3);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.BOTTOM);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""+totalVat, font8));
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);


                //      Round off

                cell = new PdfPCell(new Phrase("Round Off " , font8)); // Net Amount
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase( arabicPro.process("الجولة نهاية"), fontArb8));
                cell.setPadding(3);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.BOTTOM);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""+roundOff, font8));
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);

                //      Total amnt

                cell = new PdfPCell(new Phrase("Bill Amount " , font8)); // Net Amount
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase( arabicPro.process("اجمالى القيمة"), fontArb8));
                cell.setPadding(3);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.BOTTOM);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font8));
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);

                //      Previous bal

                cell = new PdfPCell(new Phrase("Previous Bal " , font8)); // Net Amount
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase( arabicPro.process("الرصيد السابق"), fontArb8));
                cell.setPadding(3);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.BOTTOM);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font8));
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);

                //      Previous bal

                cell = new PdfPCell(new Phrase("Total Outstanding " , font8)); // Net Amount
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase( arabicPro.process("كمية رهيبة"), fontArb8));
                cell.setPadding(3);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(PdfPCell.BOTTOM);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font8));
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);

                document.add(table);


                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(50);

                //            customer Address  label
                paragraph = new Paragraph("  Signatory " , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);


                table.addCell(cell);


                document.add(table);

                table = new PdfPTable(1);
                table.setWidthPercentage(100.0f);
                table.setWidths(new int[]{5});



                /////////////////*****  customer label ******/////
//            customer details
                cell = new PdfPCell();
                cell.setBorder( PdfPCell.NO_BORDER );
                cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                cell.setPaddingTop(1);

                //            customer Address  label
                paragraph = new Paragraph("  " , font8bold);//strBillNumber
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(paragraph);
                cell.setPaddingTop(1);
                cell.setRowspan(10);
                table.addCell(cell);



                document.add(table);



            // %%%%%%%%%%%%%%%%%%%%%%%%%%% **************** %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            // setBackground(document);
            document.close();


            printPDF(myFile);  //Print PDF File




        } catch (DocumentException | IOException e) {
            e.printStackTrace();
           // Log.d(TAG, "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }


    private void shareFile() {

        // We assume the file we want to load is in the documents/ subdirectory
// of the internal storage
        File documentsPath = new File(Report_Daily.this.getFilesDir(), "Vansale_App");
        // File file = new File(documentsPath, "DailyReport.xls");
// This can also in one line of course:
// File file = new File(Context.getFilesDir(), "documents/sample.pdf");
        String Fnamexls="DailyReport.xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File (sdCard.getAbsolutePath() + "/Vansale_App/"+Fnamexls);
        Uri uri = FileProvider.getUriForFile(Report_Daily.this,  getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = ShareCompat.IntentBuilder.from(Report_Daily.this)
                .setType("application/xls")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Report_Daily.this.startActivity(intent);

        /*String Fnamexls="DailyReport.xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File (sdCard.getAbsolutePath() + "/Vansale_App/"+Fnamexls);
        Uri photoURI = FileProvider.getUriForFile(ReportDailyActivity.this, ReportDailyActivity.this.getApplicationContext().getPackageName() + ".provider", file);
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, photoURI);

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        startActivity(Intent.createChooser(intentShareFile, "Share File"));*/



    }

    public void printPDF(final File file) {

        //Log.v(TAG, " printPDF   test ");

        PrintDocumentAdapter pda = new PrintDocumentAdapter() {

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                InputStream input = null;
                FileOutputStream output = null;

                try {

//                    File file = new File(FILE_PATH);
                    input = new FileInputStream(file);

                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (Exception e) {
                    //Catch exception
                    //   Log.v(TAG, "Exception  printPDF   2  "+e.getMessage());
                } finally {
                    try {
                        assert input != null;
                        input.close();
                        assert output != null;
                        output.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        // Log.v(TAG, "Exception  printPDF   1 "+e.getMessage());
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {


                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

//                try {
//                    if(!flag.equals("1")) {
//                        manipulatePdf(FILE_PATH, FILE_PATH1);
//                    }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (DocumentException e) {
//                        e.printStackTrace();
//                    }
                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                callback.onLayoutFinished(pdi, true);
            }
        };


        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String printName = FILE_PATH;
        assert printManager != null;
        printManager.print(printName, pda, null);


    }

    public void createPDF(List<DailyReport> dataList,DailyReport dailyReport)  {

        final ProgressDialog pd = ProgressDialog.show(Report_Daily.this, null, "Generating PDF...", false, false);

        File myFile = null;
        PdfWriter writer;

        //Create document file
        Document document = new Document();
        try {


            pd.create();
            //Open the document

            writer = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH));

            myFile = new File(FILE_PATH);

            document.open();

            document.setPageSize(PageSize.A4);
            document.addCreationDate();


            //Create Header table
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            float[] fl = new float[]{20, 45, 35};
           // header.setWidths(fl);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);

            //Set Logo in Header Cell
            Drawable logo = Report_Daily.this.getResources().getDrawable(android.R.mipmap.sym_def_app_icon);
            Bitmap bitmap = ((BitmapDrawable) logo).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapLogo = stream.toByteArray();
            try {
                imgReportLogo = Image.getInstance(bitmapLogo);
                imgReportLogo.setAbsolutePosition(330f, 642f);

                //cell.addElement(imgReportLogo);
                header.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);

                // Heading
                //BaseFont font = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
                Font titleFont = new Font(Font.FontFamily.COURIER, 22.0f, Font.BOLD, BaseColor.BLACK);

                //Creating Chunk
                Chunk titleChunk = new Chunk("Day Report", titleFont);
                //Paragraph
                Paragraph titleParagraph = new Paragraph(titleChunk);
                titleParagraph.setAlignment(Element.ALIGN_CENTER);

                cell.addElement(titleParagraph);
                /*cell.addElement(new Paragraph("Simple PDF Report"));
                cell.addElement(new Paragraph("Date: "));*/
                header.addCell(cell);

                cell = new PdfPCell(new Paragraph(""));
                cell.setBorder(Rectangle.NO_BORDER);
                header.addCell(cell);

                PdfPTable pTable = new PdfPTable(1);
                pTable.setWidthPercentage(100);
                cell = new PdfPCell();
                cell.setColspan(1);
                cell.addElement(header);
                pTable.addCell(cell);

                PdfPTable table = new PdfPTable(7);
                float[] columnWidth = new float[]{40,30, 30, 30, 20, 20, 30};
                table.setWidths(columnWidth);
                cell = new PdfPCell();
                cell.setBackgroundColor(headColor);
                cell.setColspan(7);
                cell.addElement(pTable);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(" "));
                cell.setColspan(7);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(7);
               // cell.setBackgroundColor(tableHeadColor);

                cell = new PdfPCell(new Phrase("Outlet"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Total Cash Sale"));
               // cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Total Credit Sale"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Total Return"));
               // cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Cash Collection"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Bank Collection"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Cheque Collection"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(7);

                for (int i = 0; i < list.size(); i++) {
                    table.addCell(list.get(i).getCustomer());
                    table.addCell(list.get(i).getTotalCashSale());
                    table.addCell(list.get(i).getTotalCreditSale());
                    table.addCell(list.get(i).getTotalReturnSale());
                    table.addCell(list.get(i).getTotalCashCollection());
                    table.addCell(list.get(i).getTotalBankCollection());
                    table.addCell(list.get(i).getTotalChequeCollection());

                }

               

                cell = new PdfPCell(new Phrase(" "));
                cell.setColspan(7);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(7);
                // cell.setBackgroundColor(tableHeadColor);

                cell = new PdfPCell(new Phrase("Total"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(dailyReport.getTotalCashSale()));
                // cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(dailyReport.getTotalCreditSale()));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(dailyReport.getTotalReturnSale()));
                // cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(dailyReport.getTotalCashCollection()));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(dailyReport.getTotalBankCollection()));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(dailyReport.getTotalChequeCollection()));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                document.add(table);
                document.close();
                pd.dismiss();
                printPDF(myFile);  //P

            } catch (DocumentException | IOException e) {
                pd.dismiss();
                e.printStackTrace();
               // Log.d(TAG, "exception  " + e.getMessage());
                Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            pd.dismiss();

            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
              //  Log.v(TAG, "Permission is granted");
                return true;
            } else {

                //Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           // Log.e(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission


        }


    }

}