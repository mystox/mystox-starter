package com.kongtrolink.framework.scloud.controller.base;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.scloud.util.XlsExporter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

public abstract class ExportController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExportController.class);

	//List->WorkBook写入到response中返回
	public static void export(HttpServletResponse response,
			List data,
			String[] properiesName,
			String[] headsName,
			String fileName) {
		OutputStream  out = null;  
		try {
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName+".xlsx");
			out= response.getOutputStream();
			SXSSFWorkbook wb = XlsExporter.export(data, properiesName, headsName, fileName);
			wb.write(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }

    //WorkBook写入到response中返回
    public static void export(HttpServletResponse response, HSSFWorkbook workBook, String fileName){
		OutputStream out = null;
		try {
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
			out = response.getOutputStream();
			workBook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//WorkBook生成文件存储在服务器上，返回filePath
	public static String save(String path, String fileName, HSSFWorkbook workbook){
		File file = new File(path);
		if(!file.exists() || !file.isDirectory()){
			file.mkdirs();
		}
		String filePath = path + "/" + fileName + ".xls";
		LOGGER.info("file path {}", filePath);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filePath);
			workbook.write(fos);
			fos.close();
		} catch (FileNotFoundException ex) {
			LOGGER.error(ex.getMessage());
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage());
		}
		return filePath;
	}
}
