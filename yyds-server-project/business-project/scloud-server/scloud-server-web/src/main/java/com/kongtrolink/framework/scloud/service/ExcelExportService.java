package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.util.XlsExporter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
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
 * 普通导出可以直接使用export方法，有些数据导出，有些数据导出对excel格式有要求，则需要单独写方法
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

    private static void createRepairsHead(SXSSFWorkbook workbook, Sheet sheet, String[] headsName){
        //设置单元格基本样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //水平居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        //设置边框
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框    
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框   
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        Font font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);   //黑体
        cellStyle.setFont(font);
        //创建两行头
        //先创建两行表头需要的单元格2行，9列，行和列下标都从0开始
        for(int i=0; i<2; i++){
            Row rowI = sheet.createRow(i);
            for(int j=0; j<9; j++){
                Cell cell = rowI.createCell(j);
                cell.setCellStyle(cellStyle);
            }
        }
        //合并和设置第0行的值
        Row row_0 = sheet.getRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        //获取0行上下标为1的列
        Cell cell0_1 = row_0.getCell(0);
        cell0_1.setCellValue("报修工单分类统计表");
        cell0_1.setCellStyle(cellStyle);
        //设置第一行表头的值
        Row row_1 = sheet.getRow(1);
        Cell cell1_0 = row_1.getCell(0);
        cell1_0.setCellValue("序号");
        for(int i=1; i<headsName.length+1; i++) {
            Cell cell = row_1.getCell(i);
            cell.setCellValue(headsName[i-1]);
        }
    }
}
