package com.kongtrolink.framework.scloud.util.pdf;

/**
 * 单个属性列表的数据展现
 * @author John
 *
 */
public class PdfReportEntity extends PdfModel{

	private Object data; // table 数据源
	private String[] tableHeader;//table 的 列标题
	private String[] fields;// 列 对应数据源的字段属性 与 tableHeader 的length 一样 ；一一对应
	private String connect=":";//针对 数据的拼接字符
	private int type = 0;//对于属性展现  0  一行展现，1 一列展现
	
	public PdfReportEntity(){}
	
	public PdfReportEntity(String title,Object data, String[] tableHeader, String[] fields) {
		super.setTitle(title);
		this.data = data;
		this.tableHeader = tableHeader;
		this.fields = fields;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String[] getTableHeader() {
		return tableHeader;
	}
	public void setTableHeader(String[] tableHeader) {
		this.tableHeader = tableHeader;
	}
	public String[] getFields() {
		return fields;
	}
	public void setFields(String[] fields) {
		this.fields = fields;
	}
	public String getConnect() {
		return connect;
	}
	public void setConnect(String connect) {
		this.connect = connect;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
	
}
