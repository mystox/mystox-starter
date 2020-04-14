package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.util.XlsExporter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/11/21 16:19
 * @Description: 导出excel文档service，后期希望将所有导出都在这里实现
 * 普通导出可以直接使用export方法,特殊数据导出对excel格式有要求，则需要单独写方法
 */
@Service
public class ExcelExportService {

    /**
     * @auther: liudd
     * @date: 2020/3/3 8:41
     * 功能描述:普通导出方法
     */
    public static void export(HttpServletResponse response, List data, String[] properiesName, String[] headsName, String fileName) {
        OutputStream out = null;
        try {
            String sheetName = fileName;
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName+".xlsx");
            out= response.getOutputStream();
            SXSSFWorkbook wb = XlsExporter.export(data, properiesName, headsName, sheetName);
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @auther: liudd
     * @date: 2020/4/14 13:29
     * 功能描述:导出表头为两行的数据
     */
    public static String exportTwoLineHeadData(HttpServletResponse response, String fileName,
                                               List data, String[] properiesName, String[] headsName,  String tableTitle){
        if(null == data || data.size() == 0){
            return "导出失败，数据为空";
        }
        if(null == response){
            return "导出失败，response为空";
        }
        try {
            String urlFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + urlFileName + ".xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook(100);
            wb.setCompressTempFiles(true);
            Sheet sheet = wb.createSheet(fileName);
            sheet.setDefaultColumnWidth(20);
            //拼接表头
            createTwoLineHead(wb, sheet, headsName, tableTitle);
            //填充数据
            Row row ;
            Cell cell;
            //设置单元格样式
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //水平居中
            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
            for(int i=0; i<data.size(); i++){
                //创建行
                row = sheet.createRow(i + 2);
                Object object = data.get(i);
                //创建单元格
                for(int j=0; j<headsName.length; j++){
                    cell = row.createCell(j);   //第一列是序号，所有行从第二列开始填充数据
                    cell.setCellStyle(cellStyle);
                    //填充单元格值
                    Object obj = XlsExporter.getFieldValue(object, properiesName[j]);
                    if (obj == null) {
                        cell.setCellValue("-");
                        continue;
                    }
                    // 对导入 Excel 的属性进行类型判断
                    String str = String.valueOf(XlsExporter.getCellValue(obj,true));
                    cell.setCellValue(str);
                }
            }
            OutputStream out= response.getOutputStream();
            wb.write(out);

        }catch (Exception e){
            e.printStackTrace();
        }
        return "导出成功";
    }

    private static void createTwoLineHead(SXSSFWorkbook workbook, Sheet sheet, String[] headsName, String tableTitle){
        //设置单元格基本样式
        CellStyle cellStyle = getHeaderStyle(workbook);
        //创建两行头
        //先创建两行表头需要的单元格2行，9列，行和列下标都从0开始
        for(int i=0; i<2; i++){
            Row rowI = sheet.createRow(i);
            for(int j=0; j<headsName.length; j++){
                Cell cell = rowI.createCell(j);
                cell.setCellStyle(cellStyle);
            }
        }
        //合并和设置第0行的值
        Row row_0 = sheet.getRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headsName.length-1));
        //获取0行上下标为1的列
        Cell cell0_1 = row_0.getCell(0);
        cell0_1.setCellValue(tableTitle);
        cell0_1.setCellStyle(cellStyle);
        //设置第一行表头的值
        Row row_1 = sheet.getRow(1);
        for(int i=0; i<headsName.length; i++) {
            Cell cell = row_1.getCell(i);
            cell.setCellValue(headsName[i]);
        }
    }

    private static CellStyle getHeaderStyle(SXSSFWorkbook wb){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 10 );
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFColor.BLACK.index);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(HSSFColor.BLACK.index);
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); //垂直对齐
        return style;
    }
}
