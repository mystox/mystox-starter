package com.kongtrolink.framework.scloud.util;

import com.kongtrolink.framework.scloud.exception.ExcelParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Excel工具类
 * @author Jasper
 */
public class ExcelUtil {

    private static ExcelUtil instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    private ExcelUtil() {
    }

    public static ExcelUtil getInstance() {
        if (instance == null) {
            instance = new ExcelUtil();
        }
        return instance;
    }

    /**
     * 获取 Excel 文件的 Sheet 列表
     * @param file
     * @return
     * @throws ExcelParseException
     */
    public List<HSSFSheet> getHSSFSheets(File file) throws ExcelParseException {
        List<HSSFSheet> sheets = new LinkedList<>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            int sheetCount = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetCount; i++) {
                sheets.add(workbook.getSheetAt(i));
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            throw new ExcelParseException("Excel文件读取或解析异常");
        }
        return sheets;
    }
    /**
     * 获取 Excel 文件的 Sheet 列表
     * @param file
     * @return
     * @throws ExcelParseException
     */
    public List<XSSFSheet> getXSSFSheets(File file) throws ExcelParseException {
        List<XSSFSheet> sheets = new LinkedList<>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            int sheetCount = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetCount; i++) {
                sheets.add(workbook.getSheetAt(i));
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            throw new ExcelParseException("Excel文件读取或解析异常");
        }
        return sheets;
    }
    /**
     * 获取 Sheet 中每个单元格的字符串（二维数组）
     * @param sheet
     * @return
     * @throws ExcelParseException
     */
    public String[][] getCellString(HSSFSheet sheet) throws ExcelParseException {
        int currentRowNum = 0;
        int firstDimensionLength = sheet.getLastRowNum() + 1;   // getLashRowNum()从0开始
        int secondDimensionLength = sheet.getRow(currentRowNum).getLastCellNum();	// getLastCellNum()从1开始

        // 忽略首行（标题行）
        String[][] cells = new String[firstDimensionLength - 1][secondDimensionLength];
        HSSFCell cell = null;
        for (int i = 0; i < firstDimensionLength - 1 ; i++) {
            boolean isAllNull = true;
            HSSFRow row = sheet.getRow(i + 1);
            if (row != null) {
                for (int j = 0; j < secondDimensionLength ; j++) {
                    // 忽略首行
                    cell = row.getCell(j);
                    if (cell == null || cell.toString().replaceAll("\\s*", "").equals("")||"-".equals(cell.toString())) {
                        cells[i][j] = null;
                    } else {
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cells[i][j] = cell.toString().replaceAll("\\s*", "");
                        isAllNull = false;
                    }
                }
            }
            if(isAllNull){
                throw new ExcelParseException("存在空行：第" + (i + 2) + "行，总行数：" + firstDimensionLength);
            }
        }
        return cells;
    }

    /**
     * 获取 Sheet 中每个单元格的字符串（二维数组）
     * @param sheet
     * @return
     * @throws ExcelParseException
     */
    public String[][] getCellString(XSSFSheet sheet) throws ExcelParseException {
        int currentRowNum = 0;
        int firstDimensionLength = sheet.getLastRowNum() + 1;   // getLashRowNum()从0开始
        int secondDimensionLength = sheet.getRow(currentRowNum).getLastCellNum();	// getLastCellNum()从1开始

        // 忽略首行（标题行）
        String[][] cells = new String[firstDimensionLength - 1][secondDimensionLength];
        XSSFCell cell = null;
        for (int i = 0; i < firstDimensionLength - 1 ; i++) {
            boolean isAllNull = true;
            XSSFRow row = sheet.getRow(i + 1);
            if (row != null) {
                for (int j = 0; j < secondDimensionLength ; j++) {
                    // 忽略首行
                    cell = row.getCell(j);
                    if (cell == null || cell.toString().replaceAll("\\s*", "").equals("")||"-".equals(cell.toString())) {
                        cells[i][j] = null;
                    } else {
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cells[i][j] = cell.toString().replaceAll("\\s*", "");
                        isAllNull = false;
                    }
                }
            }
            if(isAllNull){
                throw new ExcelParseException("存在空行：第" + (i + 2) + "行，总行数：" + firstDimensionLength);
            }
        }
        return cells;
    }

    public String[][] getCellsFromFile(File file) throws ExcelParseException{
        String[][] array = null;
        try {
            List<HSSFSheet> hssfSheets = ExcelUtil.getInstance().getHSSFSheets(file);
            array = ExcelUtil.getInstance().getCellString(hssfSheets.get(0));
        } catch (OfficeXmlFileException e){
            try {
                List<XSSFSheet> xssfSheets = ExcelUtil.getInstance().getXSSFSheets(file);
                array = ExcelUtil.getInstance().getCellString(xssfSheets.get(0));
            } catch (ExcelParseException e1) {
                e1.printStackTrace();
                throw e1;
            }
        } catch (ExcelParseException e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * 空行检测
     * @param array
     * @param nullable
     * @param regex
     * @throws ExcelParseException
     */
    public void checkExcelRow(String[][] array, boolean[] nullable, String[] regex)
            throws ExcelParseException {
        for (int i = 0; i < array.length; i++) {
            String[] row = array[i];
            for (int j = 0; j < nullable.length; j++) {
                if (nullable[j] == false) {
                    if (row[j] == null) {
                        throw new ExcelParseException("第" + (i + 2) + "行第" + (j + 1) + "列：必填内容为空");
                    }
                }
                if (row[j] != null && (row[j].matches(regex[j]) == false)) {
                    throw new ExcelParseException("第" + (i + 2) + "行第" + (j + 1) + "列数据格式不正确，数据：" + row[j]);
                }
            }
        }
    }

    /**
     * 数组内列数据重复监测
     * @param array
     * @param indexCol
     * @throws ExcelParseException
     */
    public void checkArrayColumnDuplicated(String[][] array, int indexCol)
            throws ExcelParseException {
        if (array.length == 0 || array[0].length < indexCol) {
            throw new ExcelParseException("数据重复性验证失败");
        }
        Set set = new HashSet();
        for (int i = 0; i < array.length; i++) {
            String string = array[i][indexCol];
            set.add(string);
        }
        if (set.size() < array.length) {
            throw new ExcelParseException("第" + (indexCol + 1) + "列：不允许数据重复的列出现重复数据");
        }
    }

    public HSSFWorkbook createWorkBook(String[] sheetName, String[][][] tables){
        HSSFWorkbook workbook = new HSSFWorkbook();
        for(int i = 0 ; i < tables.length && (tables[i] != null); i++){
            writeSheet(workbook, sheetName[i], tables[i]);
        }
        return workbook;
    }

    private void writeSheet(HSSFWorkbook workbook,String sheetName, String[][] datas){
        HSSFSheet sheet = workbook.createSheet(sheetName);
        for(int iRow = 0; iRow < datas.length; iRow ++){
            HSSFRow row = sheet.createRow(iRow);
            String[] rowDatas = datas[iRow];
            for (int iCol = 0; iCol < rowDatas.length; iCol ++) {
                HSSFCell cell = row.createCell(iCol);
                String str = rowDatas[iCol];
                if(str == null || "null".equals(str)){
                    str = "-";
                }
                cell.setCellValue(str);
            }
        }
    }

}
