package com.advanced.minhas.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.advanced.minhas.model.DailyProductReport;
import com.advanced.minhas.model.DailyReport;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtils {

    private static final String TAG = "FILE";

    private static Workbook workbook;
    private static  Sheet sheet = null;
    private static  String EXCEL_SHEET_NAME = "Sheet1";
    private static  Cell cell = null;
    private  static  CellStyle headerCellStyle;
    public static boolean exportDailyCustomerProductReportIntoWorkbook(Context context, String fileName, List<DailyProductReport> dataList) {
        boolean isWorkbookWrittenIntoStorage;

        // Check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        // Creating a New HSSF Workbook (.xls format)
        workbook = new HSSFWorkbook();

        setHeaderCellStyle();

        // Creating a New Sheet and Setting width for each column
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        sheet.setColumnWidth(0, (15 * 400));
        sheet.setColumnWidth(1, (15 * 400));
        sheet.setColumnWidth(2, (15 * 400));
        sheet.setColumnWidth(3, (15 * 400));
        sheet.setColumnWidth(4, (15 * 400));
        sheet.setColumnWidth(5, (15 * 400));
        sheet.setColumnWidth(6, (15 * 400));
        sheet.setColumnWidth(7, (15 * 400));
        sheet.setColumnWidth(8, (15 * 400));

        setDailyCustomerProductReportHeaderRow();
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getShop());

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getProduct());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getSaleQty());



            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getReturnQty());

            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getModeOfPay());

            cell = rowData.createCell(5);
            cell.setCellValue(dataList.get(i).getSalePercentage());

            cell = rowData.createCell(6);
            cell.setCellValue(dataList.get(i).getReturnPercentage());

            cell = rowData.createCell(7);
            cell.setCellValue(dataList.get(i).getFoc());

            cell = rowData.createCell(8);
            cell.setCellValue(dataList.get(i).getRemarks());
        }
        // fillDataIntoExcel(dataList);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }


    public static boolean exportDailyProductReportIntoWorkbook(Context context, String fileName, List<DailyProductReport> dataList) {
        boolean isWorkbookWrittenIntoStorage;

        // Check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        // Creating a New HSSF Workbook (.xls format)
        workbook = new HSSFWorkbook();

        setHeaderCellStyle();

        // Creating a New Sheet and Setting width for each column
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        sheet.setColumnWidth(0, (15 * 400));
        sheet.setColumnWidth(1, (15 * 400));
        sheet.setColumnWidth(2, (15 * 400));
        sheet.setColumnWidth(3, (15 * 400));
        sheet.setColumnWidth(4, (15 * 400));
        sheet.setColumnWidth(5, (15 * 400));
        sheet.setColumnWidth(6, (15 * 400));

        setDailyProductReportHeaderRow();
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getShop());

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getProduct());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getSaleQty());



            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getReturnQty());

            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getModeOfPay());

            cell = rowData.createCell(5);
            cell.setCellValue(dataList.get(i).getSalePercentage());

            cell = rowData.createCell(6);
            cell.setCellValue(dataList.get(i).getReturnPercentage());


        }
        // fillDataIntoExcel(dataList);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }


    public static boolean exportDailyReportIntoWorkbook(Context context, String fileName, List<DailyReport> dataList) {
        boolean isWorkbookWrittenIntoStorage;

        // Check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        // Creating a New HSSF Workbook (.xls format)
        workbook = new HSSFWorkbook();

        setHeaderCellStyle();

        // Creating a New Sheet and Setting width for each column
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        sheet.setColumnWidth(0, (15 * 400));
        sheet.setColumnWidth(1, (15 * 400));
        sheet.setColumnWidth(2, (15 * 400));
        sheet.setColumnWidth(3, (15 * 400));
        sheet.setColumnWidth(4, (15 * 400));
        sheet.setColumnWidth(5, (15 * 400));
        sheet.setColumnWidth(6, (15 * 400));


        setDailyReportHeader();
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getCustomer());

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getTotalCashSale());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getTotalCreditSale());



            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getTotalReturnSale());

            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getTotalCashSale());

            cell = rowData.createCell(5);
            cell.setCellValue(dataList.get(i).getTotalBankCollection());

            cell = rowData.createCell(6);
            cell.setCellValue(dataList.get(i).getTotalChequeCollection());

        }
        // fillDataIntoExcel(dataList);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }


    /**
     * Checks if Storage is READ-ONLY
     *
     * @return boolean
     */
    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    /**
     * Checks if Storage is Available
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    /**
     * Setup header cell style
     */
    private static   void setHeaderCellStyle() {

        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    }

    /**
     * Setup Header Row
     */
    private static void setDailyCustomerProductReportHeaderRow() {
        Row headerRow = sheet.createRow(0);

        cell = headerRow.createCell(0);
        cell.setCellValue("OUTLET");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("PRODUCTS");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(2);
        cell.setCellValue("SALE QTY");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(3);
        cell.setCellValue("RETURN QTY");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(4);
        cell.setCellValue("MODE OF PAY");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(5);
        cell.setCellValue("SALE PERCENTAGE");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(6);
        cell.setCellValue("RETURN PERCENTAGE");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(7);
        cell.setCellValue("FOC");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(8);
        cell.setCellValue("REMARKS");
        cell.setCellStyle(headerCellStyle);


    }

    private static void setDailyProductReportHeaderRow() {
        Row headerRow = sheet.createRow(0);



        cell = headerRow.createCell(0);
        cell.setCellValue("PRODUCTS");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("SALE QTY");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(2);
        cell.setCellValue("RETURN QTY");
        cell.setCellStyle(headerCellStyle);



        cell = headerRow.createCell(3);
        cell.setCellValue("SALE PERCENTAGE");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(4);
        cell.setCellValue("RETURN PERCENTAGE");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(5);
        cell.setCellValue("FOC");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(6);
        cell.setCellValue("REMARKS");
        cell.setCellStyle(headerCellStyle);


    }

    private static void setDailyReportHeader() {
        Row headerRow = sheet.createRow(0);

        cell = headerRow.createCell(0);
        cell.setCellValue("CUSTOMER");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("TOTAL CASH SALE");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(2);
        cell.setCellValue("TOTAL CREDIT SALE");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(3);
        cell.setCellValue("TOTAL RETURN SALE");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(4);
        cell.setCellValue("TOTAL CASH COLLECTION");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(5);
        cell.setCellValue("TOTAL BANK COLLECTION");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(6);
        cell.setCellValue("TOTAL CHEQUE COLLECTION");
        cell.setCellStyle(headerCellStyle);



    }

    /**
     * Fills Data into Excel Sheet
     * <p>
     * NOTE: Set row index as i+1 since 0th index belongs to header row
     *
     * @param dataList - List containing data to be filled into excel
     */
    private static  void fillDataIntoExcel(List<DailyProductReport> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getShop());

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getProduct());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getSaleQty());



            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getReturnQty());

            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getModeOfPay());

            cell = rowData.createCell(5);
            cell.setCellValue(dataList.get(i).getSalePercentage());

            cell = rowData.createCell(6);
            cell.setCellValue(dataList.get(i).getReturnPercentage());

            cell = rowData.createCell(7);
            cell.setCellValue(dataList.get(i).getFoc());

            cell = rowData.createCell(8);
            cell.setCellValue(dataList.get(i).getRemarks());
        }
    }

    /**
     * Store Excel Workbook in external storage
     *
     * @param context  - application context
     * @param fileName - name of workbook which will be stored in device
     * @return boolean - returns state whether workbook is written into storage or not
     */
    private static boolean storeExcelInStorage(Context context, String fileName) {
        boolean isSuccess;

        String Fnamexls="DailyReport.xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/Vansale_App");
        directory.mkdirs();
        File file = new File(directory, Fnamexls);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e(TAG, "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return isSuccess;
    }
}
