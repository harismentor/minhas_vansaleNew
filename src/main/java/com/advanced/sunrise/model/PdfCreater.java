package com.advanced.minhas.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.session.SessionValue;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;
import com.itextpdf.text.pdf.languages.LanguageProcessor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;

/**
 * Created by mentor on 5/12/17.
 */

public class PdfCreater {

    Context context;

    String CURRENCY="";
    SessionValue sessionValue;

    private  String FILE_PATH = Environment.getExternalStorageDirectory()+ "/idreesJoma.pdf";


    Font font14 = new Font(Font.FontFamily.TIMES_ROMAN, 14);


    Font font10Bold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 10);

    Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
    Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 8);

    Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);


    public PdfCreater(Context context) {
        this.context = context;
    }

    public File pdfGenerate() throws IOException, DocumentException {
//        File file = new File(FILE_PATH);
//        file.getParentFile().mkdirs();


       return createPdf();


    }



    public class PdfHeader extends PdfPageEventHelper {


//        protected PdfPTable table;
        protected float tableHeight;


        public PdfHeader( Document document) {

            sessionValue = new SessionValue(context);
            CURRENCY = ""+ sessionValue.getControllSettings().get(PREF_CURRENCY);

            try {

                BaseFont bf = BaseFont.createFont("/assets/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                LanguageProcessor arabicPro = new ArabicLigaturizer();
                Font fontArb8 = new Font(bf, 8);
                Font fontArb10 = new Font(bf, 10);
                Font fontArb14 = new Font(bf, 14);

            try {



                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.idreesjoma_logo);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image img = Image.getInstance(stream.toByteArray());
                img.setAbsolutePosition(15f, 730f);
                img.scalePercent(40f);
//                table.addCell(img);

                document.add(img);


            } catch (IOException | BadElementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Paragraph paraJomaTitleArab = new Paragraph(arabicPro.process("مؤسسة ادريس جمهة"), fontArb10);
            paraJomaTitleArab.setAlignment(Element.ALIGN_RIGHT);


            Paragraph paraJomaTitleEng = new Paragraph("IDREES JOMA EST", font8);
            paraJomaTitleEng.setAlignment(Element.ALIGN_RIGHT);


            Paragraph paraJomaTagArab1 = new Paragraph(arabicPro.process("بللتجارة المواد الغدائية  و احلويات"), fontArb10);
            paraJomaTagArab1.setAlignment(Element.ALIGN_RIGHT);

            Paragraph paraJomaTagArab2 = new Paragraph(arabicPro.process("تجارة – استيراد – تصدير "), fontArb10);
            paraJomaTagArab2.setAlignment(Element.ALIGN_RIGHT);

            Paragraph paraJomaTagEng = new Paragraph("FOR FOOD STUFF & CONFECTIONERY\nGLOBAL TRADE – EXPORTS & IMPORTS", font8);
            paraJomaTagEng.setAlignment(Element.ALIGN_RIGHT);


            PdfPCell cell;  //default cell

            //space cell
            PdfPCell cellSpace = new PdfPCell();
            cellSpace.setPadding(10);
            cellSpace.setBorder(PdfPCell.NO_BORDER);
            cellSpace.setHorizontalAlignment(Element.ALIGN_CENTER);


            //Create the table which will be 2 Columns wide and make it 100% of the page
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100.0f);


            PdfPCell cellTitle = new PdfPCell();

            Paragraph paragraph = new Paragraph(arabicPro.process("كاش / كريديت إنفواس"), fontArb14);
            paragraph.setAlignment(Element.ALIGN_CENTER);

            cellTitle.addElement(paragraph);

                    paragraph = new Paragraph("CASH/CREDIT INVOICE", font14);



            paragraph.setAlignment(Element.ALIGN_CENTER);

            cellTitle.addElement(paragraph);
            cellTitle.setBorder(Rectangle.NO_BORDER);
            cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTitle.setPaddingBottom(3);


            PdfPCell cellIdreesTag = new PdfPCell();


            cellIdreesTag.addElement(paraJomaTitleArab);
            cellIdreesTag.addElement(paraJomaTitleEng);
            cellIdreesTag.addElement(paraJomaTagArab1);
            cellIdreesTag.addElement(paraJomaTagArab2);
            cellIdreesTag.addElement(paraJomaTagEng);
            cellIdreesTag.setBorder(Rectangle.NO_BORDER);
            cellIdreesTag.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellIdreesTag.setPaddingBottom(3);


            //Add the two cells to the table
            table.addCell(cellSpace);
            table.addCell(cellTitle);
            table.addCell(cellIdreesTag);

//Add the PdfPTable to the table
            document.add(table);


            //Create the table which will be 2 Columns wide and make it 100% of the page
            table = new PdfPTable(3);
            table.setWidthPercentage(100.0f);
            table.setWidths(new int[]{17, 1, 11});


            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 8);
            f.setColor(BaseColor.RED);
            paragraph = new Paragraph("123", f);
            paragraph.setAlignment(Element.ALIGN_LEFT);


            PdfPCell cellInvoiceNo = new PdfPCell();
            cellInvoiceNo.setBorder(PdfPCell.NO_BORDER);
            cellInvoiceNo.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellInvoiceNo.addElement(paragraph);


            PdfPTable pt = new PdfPTable(3);

            cell = new PdfPCell(new Phrase("Customer\nNo :", font10));
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);

            pt.addCell(cell);
            cell = new PdfPCell(new Phrase(arabicPro.process("رقم \nالعميل : "), fontArb10));
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pt.addCell(cellSpace);
            pt.addCell(cell);



            table.addCell(cellInvoiceNo);
            table.addCell(cellSpace);
            table.addCell(pt);

            document.add(table);


            //Create the table which will be 2 Columns wide and make it 100% of the page
            table = new PdfPTable(3);
            table.setWidthPercentage(100.0f);
            table.setSpacingBefore(10);
            table.setWidths(new int[]{17, 1, 11});


            PdfPCell cellCustomerName = new PdfPCell(new Phrase("M/S : "+"shopr", font10));
            cellCustomerName.setPadding(10);
            cellCustomerName.setBorder(PdfPCell.BOX);
            cellCustomerName.setHorizontalAlignment(Element.ALIGN_LEFT);


            pt = new PdfPTable(2);

            cell = new PdfPCell(new Phrase("Date : "+"12-12-2017", font10));
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setPadding(5);
            pt.addCell(cell);

            cell = new PdfPCell(new Phrase(arabicPro.process(" تاريخ :"), fontArb10));
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPadding(5);
//                pt.addCell(cellSpace);
            pt.addCell(cell);


            //Add the two cells to the table
            table.addCell(cellCustomerName);
            table.addCell(cellSpace);
            table.addCell(pt);
            document.add(table);


            table = new PdfPTable(5);
            table.setWidths(new int[]{1, 8, 2, 3, 3});
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);


            cell = new PdfPCell();
            cell.setPaddingBottom(5);
            paragraph = new Paragraph(arabicPro.process("ر.س.") + "\nSl.No", fontArb10);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(paragraph);
            cell.setRowspan(2);
            table.addCell(cell);


            cell = new PdfPCell();
            cell.setPaddingBottom(5);
            paragraph = new Paragraph("Description", font10);
            paragraph.setAlignment(Element.ALIGN_LEFT);

            PdfPCell subCell = new PdfPCell(paragraph);
            subCell.setBorder(PdfPCell.NO_BORDER);
            subCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            pt = new PdfPTable(2);
            pt.setWidthPercentage(100.0f);
            pt.setHorizontalAlignment(Element.ALIGN_CENTER);
            //Add the two cells to the table
            pt.addCell(subCell);

            paragraph = new Paragraph(arabicPro.process("فصو"), fontArb10);
            paragraph.setAlignment(Element.ALIGN_LEFT);

            subCell = new PdfPCell(paragraph);
            subCell.setBorder(Rectangle.NO_BORDER);
            subCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            pt.addCell(subCell);


            cell.addElement(pt);
            cell.setRowspan(2);

            table.addCell(cell);


            cell = new PdfPCell();
            cell.setPaddingBottom(5);
            paragraph = new Paragraph(arabicPro.process("كمية") + "\nQty", fontArb10);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(paragraph);
            cell.setRowspan(2);

            table.addCell(cell);

            cell = new PdfPCell();
            cell.setPaddingBottom(5);
            paragraph = new Paragraph(arabicPro.process("سعر الوحدة") + "\nUnit Price", fontArb10);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(paragraph);
            cell.setRowspan(2);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setPaddingBottom(5);
            paragraph = new Paragraph(arabicPro.process("مجموع") + "\nTotal", fontArb10);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(paragraph);

            cell.setRowspan(2);
            table.addCell(cell);

            document.add(table);



        } catch (DocumentException |IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error, unable to write to file\n" + e.getMessage(),  Toast.LENGTH_SHORT).show();

        }









        }

       /* public float getTableHeight() {
            return tableHeight;
        }

*/
       /* public void onEndPage(PdfWriter writer, Document document) {
            table.writeSelectedRows(0, -1,
                    document.left(),
                    document.top() + ((document.topMargin() + tableHeight) / 2),
                    writer.getDirectContent());
        }
*/
    }




    public class PdfFooter extends PdfPageEventHelper {




        public PdfFooter( Document document) {



            try {

                BaseFont bf = BaseFont.createFont("/assets/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                LanguageProcessor arabicPro = new ArabicLigaturizer();
                Font fontArb8 = new Font(bf, 8);
                Font fontArb10 = new Font(bf, 10);
                Font fontArb14 = new Font(bf, 14);







                PdfPTable table,subTable;//default table
                PdfPCell cell;  //default cell

                Paragraph paragraph ;//




//            add blank table
                 table = new PdfPTable(1);
                    table.setWidths(new int[]{1});
                    table.setWidthPercentage(100.0f);
                    cell = new PdfPCell();
                    cell.setFixedHeight(15);
                    cell.setBorder(Rectangle.BOX);
                    table.addCell(cell);
                    document.add(table);


                    table = new PdfPTable(2);
                    table.setWidths(new int[]{20, 2});
                    table.setWidthPercentage(100);

                subTable = new PdfPTable(2);
                    cell = new PdfPCell(new Phrase("Total : ", font10Bold));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setPadding(5);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                subTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(arabicPro.process(" مجموع : "), fontArb10));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPadding(5);
                subTable.addCell(cell);

                    table.addCell(subTable);



                    String total="";

                        total=getAmount(1200)+" "+CURRENCY;

                    cell = new PdfPCell(new Phrase(total, font10Bold));
                    cell.setBorder(PdfPCell.BOX);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);

                    document.add(table);

                    //            add blank table
                    table = new PdfPTable(1);
                    table.setWidths(new int[]{100});
                    table.setWidthPercentage(100);

//          add blank cell
                    cell = new PdfPCell();
                    cell.setFixedHeight(50);
                    cell.setBorder(Rectangle.BOX);
                    table.addCell(cell);

                    document.add(table);

                    table = new PdfPTable(4);
                    table.setWidths(new int[]{2, 2, 2, 2});
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(50);

                    cell = new PdfPCell();
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setPadding(5);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);

                    paragraph = new Paragraph(" Salesman : ", font10);
                    paragraph.setAlignment(Element.ALIGN_LEFT);
                    cell.addElement(paragraph);
                    table.addCell(cell);

                    cell = new PdfPCell();
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPadding(5);
                    paragraph = new Paragraph(arabicPro.process("بائع : "), fontArb10);
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);
                    table.addCell(cell);

                    cell = new PdfPCell();
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setPadding(5);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    paragraph = new Paragraph("Received By : ", font10);
                    paragraph.setAlignment(Element.ALIGN_LEFT);
                    cell.addElement(paragraph);
                    table.addCell(cell);

                    cell = new PdfPCell();
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPadding(5);
                    paragraph = new Paragraph(arabicPro.process(" استلمت من قبل : "), fontArb10);
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);
                    table.addCell(cell);

                    document.add(table);


            } catch (DocumentException |IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error, unable to write to file\n" + e.getMessage(),  Toast.LENGTH_SHORT).show();

            }









        }

    }

        public File createPdf() throws IOException, DocumentException {


            File myFile = null;

            // step 1
//            Document document = new Document(PageSize.A4, 36, 36, 20 + event.getTableHeight(), 36);
            Document document = new Document(PageSize.A4);

            // step 2
//            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));

            PdfWriter  writer = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH));

            myFile = new File(FILE_PATH);

            // step 3
            document.open();
            PdfHeader event = new PdfHeader(document);
            writer.setPageEvent(event);
            // step 4
            for (int i = 0; i < 50; i++)
                document.add(new Paragraph("Hello World.....!"));
            document.newPage();
            document.add(new Paragraph("Hello World!"));
            document.newPage();
            document.add(new Paragraph("Hello World!"));



            PdfFooter footerEvent = new PdfFooter(document);
            writer.setPageEvent(footerEvent);


            // step 5
            document.close();


            return myFile;
        }
}
