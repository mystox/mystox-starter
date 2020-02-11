package com.kongtrolink.framework.scloud.util.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2018/6/28 14:22
 * @Description:生成PDF文件工具类
 */
public class PDFUtil {

    /**
     * @auther: liudd
     * @date: 2018/6/28 14:29
     * 功能描述:生成单纯报表PDF
     */
    public static boolean createTable(OutputStream outputStream,
                               String title, List<Object> data, String[] tableHeader, String[] fields){
        boolean result = true;
        try{
            Document document = new Document(PageSize.A4);
            float cont = PageSize.A4.getWidth() - 75;
            PdfReportManager manager = new PdfReportManager(cont);
            PdfWriter.getInstance(document, outputStream);
            document.addCreationDate();//设置文件创建时间
            document.open();
            PdfModelTable table = new PdfModelTable(title, data, tableHeader, fields);
            manager.addTable(document, table);
            document.close();
            System.out.println("生成 pdf 成功");
        }catch (Exception e){
            System.out.println("生成 pdf 失败");
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
