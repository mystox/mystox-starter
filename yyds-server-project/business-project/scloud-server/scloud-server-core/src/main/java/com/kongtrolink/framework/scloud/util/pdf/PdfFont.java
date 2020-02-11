package com.kongtrolink.framework.scloud.util.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

import java.io.IOException;

/**
 * 字体 维护
 * @author John
 *
 */
public class PdfFont {
	
	static BaseFont init(){
		 try {
	            // 中文字体支持
	          return  BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
	        } catch (DocumentException | IOException ex) {
	            ex.getMessage();
	        }
		return null;
	}
	
	/**
	 * 黑色加粗
	 */
	public static final Font fontBold10 = new Font(init(), 10, Font.BOLD, BaseColor.BLACK);
	public static final Font fontBold12 = new Font(init(), 12, Font.BOLD, BaseColor.BLACK);
	public static final Font fontBold14 = new Font(init(), 14, Font.BOLD, BaseColor.BLACK);
	public static final Font fontBold16 = new Font(init(), 16, Font.BOLD, BaseColor.BLACK);
	public static final Font fontBold18 = new Font(init(), 18, Font.BOLD, BaseColor.BLACK);
	public static final Font fontBold20 = new Font(init(), 20, Font.BOLD, BaseColor.BLACK);
	/**
	 * 普通字体
	 */
	public static final Font font10 = new Font(init(), 10, Font.NORMAL, BaseColor.BLACK);
	public static final Font font12 = new Font(init(), 12, Font.NORMAL, BaseColor.BLACK);
	public static final Font font14 = new Font(init(), 14, Font.NORMAL, BaseColor.BLACK);
	public static final Font font16 = new Font(init(), 16, Font.NORMAL, BaseColor.BLACK);
	public static final Font font18 = new Font(init(), 18, Font.NORMAL, BaseColor.BLACK);
	public static final Font font20 = new Font(init(), 20, Font.NORMAL, BaseColor.BLACK);
	/**
	 * 红色粗体
	 */
	public static final Font fontRed12 = new Font(init(), 12, Font.BOLD, BaseColor.RED);
	public static final Font fontRed14 = new Font(init(), 14, Font.BOLD, BaseColor.RED);
	public static final Font fontRed16 = new Font(init(), 16, Font.BOLD, BaseColor.RED);
}
