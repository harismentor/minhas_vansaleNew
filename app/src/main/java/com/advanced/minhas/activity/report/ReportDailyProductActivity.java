package com.advanced.minhas.activity.report;

import static com.advanced.minhas.config.ConfigKey.WRITE_REQUEST_CODE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.adapter.DailyProductReportAdapter;
import com.advanced.minhas.listener.ActivityConstants;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.DailyProductReport;
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReportDailyProductActivity extends AppCompatActivity implements View.OnClickListener{
    private static String FILE_PATH = Environment.getExternalStorageDirectory() + "/daily_product_report.pdf";


    private MyDatabase myDatabase;

    private DailyProductReportAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<DailyProductReport> list = new ArrayList<>();
    private ImageButton ibBack;
    private LinearLayout lyt_share;

    private PdfPCell cell;
    private Image imgReportLogo;

    LinearLayout ly_sync;

    BaseColor headColor = WebColors.getRGBColor("#DEDEDE");
    //BaseColor tableHeadColor = WebColors.getRGBColor("#F5ABAB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_daily_products);

        ibBack = (ImageButton) findViewById(R.id.imageButton_toolbar_back);
        ly_sync = findViewById(R.id.ly_sync);
        lyt_share =  findViewById(R.id.lyt_share);
        recyclerView = findViewById(R.id.recyclerview_daily_report);
        myDatabase = new MyDatabase(ReportDailyProductActivity.this);
        adapter = new DailyProductReportAdapter(list);
        list=myDatabase.getDailyProductReport();

        Log.e("sizelist",""+list.size());
        // sales.addAll(myDatabase.getFullSales());

        setrecyclerview();

        ly_sync.setVisibility(View.GONE);
        lyt_share.setVisibility(View.VISIBLE);
        ibBack.setOnClickListener(this);
        lyt_share.setOnClickListener(this);


    }


    private void setrecyclerview() {
        adapter = new DailyProductReportAdapter(list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        //        Item Divider in recyclerView
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ReportDailyProductActivity.this)
                .showLastDivider()
                .build());
        recyclerView.setLayoutManager(new LinearLayoutManager(ReportDailyProductActivity.this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imageButton_toolbar_back:

                onBackPressed();
                break;
            case R.id.lyt_share:

                if (isStoragePermissionGranted()) {
                    Log.e("Quotation", "else" + ActivityConstants.ACTIVITY_QUOTATION);


                    createPDF(list);
                    // printwithbackgroundInvoice(getPdfModels(cartItems));
                }


                //createExcelSheet(list);
                break;



        }
    }


    private void createExcelSheet(List<DailyProductReport> dataList)
    {


        boolean isExcelGenerated = ExcelUtils.exportDailyProductReportIntoWorkbook(ReportDailyProductActivity.this,
                "EXCEL_FILE_NAME", dataList);


        if(isExcelGenerated){
            Toast.makeText(ReportDailyProductActivity.this,"Execel Generated",Toast.LENGTH_LONG).show();

            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            if (!hasPermissions(ReportDailyProductActivity.this, PERMISSIONS)) {

                ActivityCompat.requestPermissions(ReportDailyProductActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 786);

            }else{
                shareFile();

            }
        }else{
            Toast.makeText(ReportDailyProductActivity.this,"Execel Not Generated",Toast.LENGTH_LONG).show();

        }


    }

    private void shareFile() {

        // We assume the file we want to load is in the documents/ subdirectory
// of the internal storage
        File documentsPath = new File(ReportDailyProductActivity.this.getFilesDir(), "Vansale_App");
       // File file = new File(documentsPath, "DailyReport.xls");
// This can also in one line of course:
// File file = new File(Context.getFilesDir(), "documents/sample.pdf");
        String Fnamexls="DailyProductReport.xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File (sdCard.getAbsolutePath() + "/Vansale_App/"+Fnamexls);
        Uri uri = FileProvider.getUriForFile(ReportDailyProductActivity.this,  getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = ShareCompat.IntentBuilder.from(ReportDailyProductActivity.this)
                .setType("application/xls")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        ReportDailyProductActivity.this.startActivity(intent);

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

    public void createPDF(List<DailyProductReport> dataList)  {
        File myFile = null;
        PdfWriter writer;

        //Create document file
        Document document = new Document();
        try {


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
            Drawable logo = ReportDailyProductActivity.this.getResources().getDrawable(android.R.mipmap.sym_def_app_icon);
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
                Chunk titleChunk = new Chunk("Daily Product Report", titleFont);
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

                cell = new PdfPCell(new Phrase("Products"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Sale Qty"));
                // cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Return Qty"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Sale %"));
                // cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Return %"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("FOC"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Remarks"));
                //cell.setBackgroundColor(tableHeadColor);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(7);

                for (int i = 0; i < list.size(); i++) {
                    table.addCell(list.get(i).getProduct());
                    table.addCell(String.valueOf(list.get(i).getSaleQty()));
                    table.addCell(list.get(i).getReturnQty());
                    table.addCell(list.get(i).getSalePercentage());
                    table.addCell(list.get(i).getReturnPercentage());
                    table.addCell(list.get(i).getFoc());
                    table.addCell(list.get(i).getRemarks());

                }


                document.add(table);
                document.close();
                printPDF(myFile);  //P

            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                // Log.d(TAG, "exception  " + e.getMessage());
                Toast.makeText(this, "Error, unable to write to file\n" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


}