package com.kongtrolink.framework.scloud.util.pdf;

import java.util.List;
import java.util.Map;

/**
 * pdf table 对象
 * @author John
 *
 */
public class PdfModelTable extends PdfModel{
	
	/**
	 * table 数据源
	 */
	private List<Object> data;  
	/**
	 * table 的 列标题 满足 复合标题(当前 只满足 二级列名设置)
	 * demo：{"编号","姓名","年龄","班级#现有班级,调班前班级","备注#备注(℃),备注(Ω)"};
	 * 主列名 与 子列 之间 用 # 号进行 间隔
	 * 一个主列下的 子列与子列之间 用 , 号 进行间隔
	 */
	private String[] tableHeader;
	/**
	 * 列 对应数据源的字段属性 与 tableHeader 的length 一样 ；一一对应
	 * tableHeader 若有子列 按照子列的length  一样
	 */
	private String[] fields;
	/**
	 * 列值为null时 对应的默认值
	 */
	private String colDefaultNull = "-";
	/**
	 * 对应列 的 单位值 (key:第几列-从0开始编号。value：单位符号)
	 * 注意 编号位置  tableHeader 若有子列 按照子列位置进行设置
	 */
	private Map<Integer,String> unitMap;
	/**
	 * 对应列 的水平位置 (靠左 居中 靠右) --默认居中(key:第几列-从0开始编号。value：位置)
	 * 注意 编号位置  tableHeader 若有子列 按照子列位置进行设置
	 */
	private Map<Integer,Integer> alian;
	/**
	 * 对应列 的计算规则 v1-v2 模式 v1,v2表示 第1个参数 第2个参数 
	 * 暂未实现 可参看 java jep类库
	 */
	private Map<Integer,String> rule;
	
	
	public PdfModelTable(){}
	
	
	public PdfModelTable(String title,List<Object> data, String[] tableHeader, String[] fields) {
		super.setTitle(title);
		this.data = data;
		this.tableHeader = tableHeader;
		this.fields = fields;
	}
	
	public List<Object> getData() {
		return data;
	}
	public void setData(List<Object> data) {
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
	public String getColDefaultNull() {
		return colDefaultNull;
	}
	public void setColDefaultNull(String colDefaultNull) {
		this.colDefaultNull = colDefaultNull;
	}
	public Map<Integer, String> getUnitMap() {
		return unitMap;
	}
	public void setUnitMap(Map<Integer, String> unitMap) {
		this.unitMap = unitMap;
	}
	public Map<Integer, String> getRule() {
		return rule;
	}
	public void setRule(Map<Integer, String> rule) {
		this.rule = rule;
	}

	public Map<Integer, Integer> getAlian() {
		return alian;
	}

	public void setAlian(Map<Integer, Integer> alian) {
		this.alian = alian;
	}
	
	
	
}
