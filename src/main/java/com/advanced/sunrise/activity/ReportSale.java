package com.advanced.minhas.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.SaleReportAdapter;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Sales;
import com.advanced.minhas.session.SessionValue;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;
import static com.advanced.minhas.session.SessionValue.PREF_EXECUTIVE_NAME;

public class ReportSale extends AppCompatActivity implements View.OnClickListener {

    private static String DIR_PATH = "/Icresp/Report";
    private static String ReportName = "Sale Report";
    private static String FileType = "pdf";
    private static String FileName = ReportName+"."+FileType;

    private MyDatabase myDatabase;

    private SaleReportAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<Sales> list = new ArrayList<>();

    private ImageButton ibBack;
    private LinearLayout lyt_share;
    LinearLayout ly_sync;

    private SessionValue sessionValue;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_sale);
        sessionValue = new SessionValue(ReportSale.this);

        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        ly_sync = findViewById(R.id.ly_sync);
        lyt_share =  findViewById(R.id.lyt_share);

        recyclerView = findViewById(R.id.recyclerview_sale_report);
        myDatabase = new MyDatabase(ReportSale.this);
        adapter = new SaleReportAdapter(list);
        list=myDatabase.getFullSales();
        Log.e("sizelist",""+list.size());
        // sales.addAll(myDatabase.getFullSales());

        setrecyclerview();
        ly_sync.setVisibility(View.GONE);
        lyt_share.setVisibility(View.VISIBLE);
        ibBack.setOnClickListener(this);
        lyt_share.setOnClickListener(this);
    }
    private void setrecyclerview() {
        adapter = new SaleReportAdapter(list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ReportSale.this)
                .showLastDivider()
                .build());
        recyclerView.setLayoutManager(new LinearLayoutManager(ReportSale.this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_preview_home:

                onBackPressed();

                break;

            case R.id.imageButton_toolbar_back:

                onBackPressed();
                break;
            case R.id.lyt_share:

                if (isStoragePermissionGranted()) {
                    Log.e("Quotation", "else" + ActivityConstants.ACTIVITY_QUOTATION);


                    createandDisplayPdf();
                }


                break;
        }
    }

    public void setBlankSpace(Document document){


        try {

            Font font8Normal = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.NORMAL);
            int paddingTop=10;
            int paddingBottom=10;
            int paddingRight=0;
            int paddingLeft=0;

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100.0f);
            table.setWidths(new int[]{100});

            PdfPCell cellSpace = new PdfPCell();
            cellSpace.setBorder(PdfPCell.NO_BORDER);
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);

            Paragraph paragraphSpace = new Paragraph("    ", font8Normal);
            paragraphSpace.setAlignment(Element.ALIGN_LEFT);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);







            document.add(table);




        }catch (DocumentException e) {
            e.printStackTrace();
            Log.e( "exception  " , e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    public void setReportCreationDetails(Document document,String value){


        try {

            Font font8Normal = new Font(Font.FontFamily.TIMES_ROMAN, 10,Font.NORMAL);
            int paddingTop=0;
            int paddingBottom=2;
            int paddingRight=10;
            int paddingLeft=10;

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100.0f);
            table.setWidths(new int[]{100});

            PdfPCell cellSpace = new PdfPCell();
            cellSpace.setBorder(PdfPCell.NO_BORDER);
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);

            Paragraph paragraphSpace = new Paragraph(value, font8Normal);
            paragraphSpace.setAlignment(Element.ALIGN_LEFT);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);







            document.add(table);




        }catch (DocumentException e) {
            e.printStackTrace();
            Log.d("TAG", "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }
    public void setSaleDetails(Document document, String slNo, String date, String invoiceNo, String shopName, String shopCode, String saleTotal){


        try {

            Font font8Normal = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.NORMAL);
            int paddingTop=0;
            int paddingBottom=2;
            int paddingRight=10;
            int paddingLeft=10;

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100.0f);
            table.setWidths(new int[]{9,18,18,25,15,15});

            PdfPCell cellSpace = new PdfPCell();
            //cellSpace.setBorder(PdfPCell.NO_BORDER);
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);

            Paragraph paragraphSpace = new Paragraph(slNo, font8Normal);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);


            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(date, font8Normal);
            paragraphSpace.setAlignment(Element.ALIGN_LEFT);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);

            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(invoiceNo, font8Normal);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);

            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(shopName, font8Normal);
            paragraphSpace.setAlignment(Element.ALIGN_LEFT);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);

            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(shopCode, font8Normal);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);



            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(saleTotal, font8Normal);
            paragraphSpace.setAlignment(Element.ALIGN_RIGHT);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);


            document.add(table);




        }catch (DocumentException e) {
            e.printStackTrace();
            Log.d("TAG", "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }
    public void setSaleHeading(Document document, String slNo, String date, String invoiceNo, String shopName, String shopCode, String saleTotal){


        try {

            Font font8Bold = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.BOLD);
            int paddingTop=0;
            int paddingBottom=2;
            int paddingRight=10;
            int paddingLeft=10;

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100.0f);
            table.setWidths(new int[]{9,18,18,25,15,15});

            PdfPCell cellSpace = new PdfPCell();
            //cellSpace.setBorder(PdfPCell.NO_BORDER);
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);

            Paragraph paragraphSpace = new Paragraph(slNo, font8Bold);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);


            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(date, font8Bold);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);

            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(invoiceNo, font8Bold);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);

            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(shopName, font8Bold);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);

            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(shopCode, font8Bold);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);



            cellSpace = new PdfPCell();
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(saleTotal, font8Bold);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);


            document.add(table);




        }catch (DocumentException e) {
            e.printStackTrace();
            Log.d("TAG", "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }
    public void setSaleTotal(Document document, String total){


        try {

            Font font8Bold = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.BOLD);
            int paddingTop=5;
            int paddingBottom=10;
            int paddingRight=10;
            int paddingLeft=10;

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100.0f);
            table.setWidths(new int[]{50,50});

            PdfPCell cellSpace = new PdfPCell();
            cellSpace.setBorder(PdfPCell.LEFT|PdfPCell.TOP|PdfPCell.BOTTOM);
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);

            Paragraph paragraphSpace = new Paragraph("Total", font8Bold);
            paragraphSpace.setAlignment(Element.ALIGN_LEFT);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);


            cellSpace = new PdfPCell();
            cellSpace.setBorder(PdfPCell.RIGHT|PdfPCell.TOP|PdfPCell.BOTTOM);

            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);
            paragraphSpace = new Paragraph(total, font8Bold);
            paragraphSpace.setAlignment(Element.ALIGN_RIGHT);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);




            document.add(table);




        }catch (DocumentException e) {
            e.printStackTrace();
            Log.d("TAG", "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    public void setHeader(Document document){


        try {
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22.0f, Font.BOLD, BaseColor.BLACK);

            Font font8Normal = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.NORMAL);
            int paddingTop=10;
            int paddingBottom=12;
            int paddingRight=0;
            int paddingLeft=0;

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100.0f);
            table.setWidths(new int[]{100});

            PdfPCell cellSpace = new PdfPCell();
            cellSpace.setBorder(PdfPCell.NO_BORDER);
            cellSpace.setVerticalAlignment(Element.ALIGN_CENTER);
            cellSpace.setPaddingLeft(paddingLeft);
            cellSpace.setPaddingTop(paddingTop);
            cellSpace.setPaddingBottom(paddingBottom);
            cellSpace.setPaddingRight(paddingRight);

            Paragraph paragraphSpace = new Paragraph(ReportName, titleFont);
            paragraphSpace.setAlignment(Element.ALIGN_CENTER);
            cellSpace.addElement(paragraphSpace);
            table.addCell(cellSpace);








            document.add(table);




        }catch (DocumentException e) {
            e.printStackTrace();
            Log.d("TAG", "exception  " + e.getMessage());
            Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    public void createandDisplayPdf() {

        Document document = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + DIR_PATH;

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, FileName);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(document, fOut);

            //open the document
            document.open();

            document.setPageSize(PageSize.A4);
            document.addCreationDate();

            String execName = sessionValue.getExecutiveDetails().get(PREF_EXECUTIVE_NAME);  //name get from session
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault());
            String formattedDate = df.format(c);

            setHeader(document);
            setBlankSpace(document);

            setReportCreationDetails(document,"Executive Name: "+execName);
            setReportCreationDetails(document,"Date : "+formattedDate);
            setBlankSpace(document);

            setSaleHeading(document,
                    "Sl.No",
                    "Date",
                    "Invoice No",
                    "Shop Name",
                    "Shop Code",
                    "Sale Total");

            float total = 0;

            for(int i=0;i<list.size();i++){
                setSaleDetails(document,
                        String.valueOf(i+1),
                        list.get(i).getDate(),
                        String.valueOf(list.get(i).getInvoice_no()),
                        list.get(i).getShopname(),
                        list.get(i).getShopcode(),
                        String.valueOf(list.get(i).getWithTaxTotal()));
                total += list.get(i).getWithTaxTotal();
            }

            setSaleTotal(document,String.valueOf(total));


        }
        catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            document.close();
        }

        viewPdf(FileName, DIR_PATH);
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        // Uri path = Uri.fromFile(pdfFile);
        Uri path = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission


        }


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}