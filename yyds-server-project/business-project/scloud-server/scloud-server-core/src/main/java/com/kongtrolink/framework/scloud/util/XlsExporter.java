/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.scloud.util;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mag
 */
public class XlsExporter {

    public static final int PAGE_SIZE = 60000;
    public static final int LIMIT = 200000;

    public static SXSSFWorkbook export(List data, String[] properiesName, String[] headsName, String sheetName) throws IOException {
        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        wb.setCompressTempFiles(true); 
        Sheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(10);
		Row row = null;
		Cell cell = null;
		int rows_max = createHeader(sheet,getHeaderStyle(wb),headsName);
        int start = 0, limit = LIMIT;
        do {
            for(int i=0;i<data.size();i++){
    			row = sheet.createRow(i+rows_max+start);
    			Object stat = data.get(i);
    			// 在每一列添加数据
    			for (int iCol = 0; iCol < properiesName.length; iCol++) {
    				try {
    					cell = row.createCell(iCol);
    					Object obj = getFieldValue(stat, properiesName[iCol]);
    					if (obj == null) {
    						//cell.setCellValue("-");
    						continue;
    					}
    					// 对导入 Excel 的属性进行类型判断
    					String str = String.valueOf(getCellValue(obj,true));
    					cell.setCellValue(str);
    				} catch (Exception ex) {
    					ex.printStackTrace();
    				}
    			}
           }
            start += limit;
        } while (!data.isEmpty() && data.size() == limit);
        return wb;
    }
    private static CellStyle getDataCellStyle(SXSSFWorkbook wb){
		CellStyle style = wb.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN); 
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		return style;
	}
    private static CellStyle getTitleStyle(SXSSFWorkbook wb){
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);   
		font.setFontHeightInPoints((short) 14 );
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
		return style;
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
		return style;
	}
    private static int createHeader(Sheet sheet,CellStyle style,String[] headsName) {
		Row row = null;
		Cell cell = null;
		int rows_max =0;
		for (int i = 0; i < headsName.length; i++) {
			String h = headsName[i];
			if (h.split("\\|").length > rows_max) {
				rows_max = h.split("\\|").length;
			}
		}
		Map map = new HashMap();
		for (int k = 0; k < rows_max; k++) {
			row = sheet.createRow(k);
			for (int i = 0; i < headsName.length; i++) {
				String headerTemp = headsName[i];
				String[] s = headerTemp.split("\\|");
				String sk = "";
				int num = i;
				if (s.length == 1) {
					cell = row.createCell(num);
					cell.setCellStyle(style);
					sk = headerTemp;
					cell.setCellValue(sk);
					sheet.addMergedRegion(new CellRangeAddress(1, rows_max,num,num));
				} else {
					cell = row.createCell(num);
					cell.setCellStyle(style);
					int cols = 0;
					if (map.containsKey(headerTemp)) {
						continue;
					}
					for (int d = 0; d <= k; d++) {
						if (d != k) {
							sk += s[d] + "|";
						} else {
							sk += s[d];
						}
					}
					if (map.containsKey(sk)) {
						continue;
					}
					for (int j = 0; j < headsName.length; j++) {
						if (headsName[j].indexOf(sk) != -1) {
							cols++;
						}
					}
					cell.setCellValue(s[k]);
					sheet.addMergedRegion(new CellRangeAddress(k +1, k + 1,num,(num + cols - 1)));
					if (sk.equals(headerTemp)) {
						sheet.addMergedRegion(new CellRangeAddress(k + 1, k + 1 + rows_max - s.length,num,num));
					}
				}
				if (s.length > k) {
					if (!map.containsKey(sk)) {
						String key = "";
						if (k > 0) {
							key = sk;
						} else {
							key = s[k];
						}
						map.put(key, null);
					}
				}
			}
		}
		row = null;
		cell = null;
		return rows_max;
	}
    public static Object getFieldValue(Object o, String fields) {
		try {
			Class<?> clazz = o.getClass();
			Field field1 = null;
			try{
				field1 = clazz.getDeclaredField(fields);
			}catch (NoSuchFieldException e){
				Class superclass = clazz.getSuperclass();//该属性 可能来源与 父类
				field1 = superclass.getDeclaredField(fields);
			}

			Method getMethod = null;
			PropertyDescriptor pd = null;
			/**
			 * 对于 field 映射获取 值问题 字段名 为 首字母小写。第二个字母大写的 字段名 ， 自动生成的 set get 方法 异常
			 * 正常的 用 PropertyDescriptor
			 */
			if (isAcronym(fields)) {
				getMethod = clazz.getMethod("get" + fields);

			} else {
				pd = new PropertyDescriptor(field1.getName(), clazz);
				getMethod = pd.getReadMethod();
			}
			if (getMethod.invoke(o) != null) {
				Object ret = getMethod.invoke(o);
				return ret;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;// 对 该类未含有该属性值时 产生的 异常 返回 null
		}
	}

    public static Object getCellValue(Object obj,boolean ifCheckList){
		if (obj == null) {
			return "-";
		}
		// 对导入 Excel 的属性进行类型判断
		if (obj instanceof String) { // String
			return (String) obj;
		} else if (obj instanceof Integer) { // Integer
			return (Integer) obj;
		} else if (obj instanceof Double) { // Double
			return (Double) obj;
		} else if (obj instanceof Boolean) { // Boolean
			return(Boolean) obj;
		} else if (obj instanceof Date) { // Date
			return DateUtil.getInstance().format((Date) obj);
		} else if (obj instanceof String[]) { // String[]
			String[] arr = (String[]) obj;
			StringBuffer bu = new StringBuffer("");
			if (arr.length > 0) {
				for (int i = 0; i < arr.length; i++) {
					bu.append(arr[i]);
					if (i < arr.length - 1) {
						bu.append(",");
					}
				}
				return bu.toString();
			} else {
				return "-";
			}
		} else if (obj instanceof List && ifCheckList) { // List
			List list = (List)obj;
			if(list.size()>0){
				StringBuffer bu = new StringBuffer("");
				for (int i = 0; i < list.size(); i++) {
					bu.append(String.valueOf(getCellValue(list.get(i),false)));
					if (i < list.size() - 1) {
						bu.append(",");
					}
				}
				return bu.toString();
			}else {
				return "-";
			}
			
		} else if (obj instanceof FacadeView) { // FacadeView
			FacadeView arr = (FacadeView) obj;
			return arr.getName();
		} else {
			String str = JSON.toJSONString(obj);
			return str;
		}
	}
	/**
	 * 对于 field 映射获取 值问题 字段名 为 首字母小写。第二个字母大写的 字段名 ， 自动生成的 set get 方法 异常 需要 另外获取
	 * 该 set get 方法
	 * 
	 * @param word
	 * @return
	 */
	public static  boolean isAcronym(String word) {
		char first = word.charAt(0);
		char second = word.charAt(1);
		if (Character.isLowerCase(first) && (Character.isUpperCase(second))) {
			return true;
		}
		return false;
	}
}
