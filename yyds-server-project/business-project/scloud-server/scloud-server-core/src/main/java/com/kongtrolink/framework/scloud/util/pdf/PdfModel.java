package com.kongtrolink.framework.scloud.util.pdf;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;

/**
 * 一些 pdf 常用属性 基础类
 * @author John
 *
 */
public class PdfModel {
	
	private int id;// 主键ID
	private String title;//文档标题
	private Font titleFont;//标题字体配置
	private Font headFont;//列名 字体配置
	private Font cellFont;//普通 字体配置
    private int tableBorder;    // 表格边框模式：无边框
	private float tableWidthPercent;  // 表格宽度占比
	private float space;       // 表格后留白行高度
	private Boolean nextPage;//是否新增一页
	private Boolean ifTitle;//是否是带标题的文本 还是普通文本
	private int tableAlign;//表格 水平布局
	private int cellAlign;//内容水平布局
	
	
	public PdfModel(){
        this.titleFont = PdfFont.fontBold16;
        this.headFont = PdfFont.fontBold14;
        this.cellFont= PdfFont.font12;
        this.tableBorder = 0;//无边框
        this.tableWidthPercent = 100;
        this.space = 15;
        this.nextPage = true;//默认 开新一页
        this.ifTitle = true;//默认 带标题
        this.tableAlign = Element.ALIGN_CENTER;//居中
        this.cellAlign = Element.ALIGN_CENTER;//居中
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Font getTitleFont() {
		return titleFont;
	}
	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}
	public Font getCellFont() {
		return cellFont;
	}
	public void setCellFont(Font cellFont) {
		this.cellFont = cellFont;
	}
	public int getTableBorder() {
		return tableBorder;
	}
	public void setTableBorder(int tableBorder) {
		this.tableBorder = tableBorder;
	}
	public float getTableWidthPercent() {
		return tableWidthPercent;
	}
	public void setTableWidthPercent(float tableWidthPercent) {
		this.tableWidthPercent = tableWidthPercent;
	}
	public float getSpace() {
		return space;
	}
	public void setSpace(float space) {
		this.space = space;
	}
	public Boolean getNextPage() {
		return nextPage;
	}
	public void setNextPage(Boolean nextPage) {
		this.nextPage = nextPage;
	}
	public Font getHeadFont() {
		return headFont;
	}
	public void setHeadFont(Font headFont) {
		this.headFont = headFont;
	}
	public Boolean getIfTitle() {
		return ifTitle;
	}
	public void setIfTitle(Boolean ifTitle) {
		this.ifTitle = ifTitle;
	}
	public int getTableAlign() {
		return tableAlign;
	}
	public void setTableAlign(int tableAlign) {
		this.tableAlign = tableAlign;
	}
	public int getCellAlign() {
		return cellAlign;
	}
	public void setCellAlign(int cellAlign) {
		this.cellAlign = cellAlign;
	}
	
}
