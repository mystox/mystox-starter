/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.scloud.util.pdf;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.kongtrolink.framework.scloud.util.DateUtil;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * PDF 方法
 */

public class PdfReportManager {

	private float contentWidth; // 内容文本宽度

	public PdfReportManager(float contentWidth) {
		this.contentWidth = contentWidth;
	}

	/**
	 * pdf 标题设置 居中
	 * @param document
	 * @param model
	 * @throws DocumentException
	 */
	public void addTitle(Document document, PdfModel model) throws DocumentException {
		PdfPCell cHead = new PdfPCell(new Phrase(model.getTitle(), model.getTitleFont()));
		cHead.setVerticalAlignment(Element.ALIGN_MIDDLE);//纵向居中
		cHead.setHorizontalAlignment(Element.ALIGN_CENTER);//水平居中
		cHead.setBorder(0);// 无 表框
		PdfPTable head = new PdfPTable(1);
		head.setWidthPercentage(model.getTableWidthPercent());
		head.setSpacingAfter(model.getSpace());
		head.addCell(cHead);
		document.add(head);
	}

	/**
	 * 添加 单个对象的数据信息 => 头信息与数据信息结合
	 */
	public void addEntity(Document document, PdfReportEntity pdfReportEntity) throws DocumentException {
		System.out.println("添加 entity 属性信息...");

		Object o = pdfReportEntity.getData();
		String[] tableHeader = pdfReportEntity.getTableHeader();
		String connect = pdfReportEntity.getConnect();
		String[] infos = pdfReportEntity.getFields();
		int type = pdfReportEntity.getType();

		if (pdfReportEntity.getIfTitle()) {// 小标题
			PdfPTable tableTitle = new PdfPTable(1);
			tableTitle.setWidthPercentage(100);
			tableTitle.setSpacingAfter(pdfReportEntity.getSpace());// 与下方间距
			PdfPCell ctitle = new PdfPCell(new Phrase(pdfReportEntity.getTitle(), pdfReportEntity.getTitleFont()));
			ctitle.setBorder(0);
			tableTitle.addCell(ctitle);
			document.add(tableTitle);
		}
		PdfPTable table = new PdfPTable(type > 0?1:infos.length);
		table.setWidthPercentage(pdfReportEntity.getTableWidthPercent());
		table.setSpacingAfter(pdfReportEntity.getSpace());	
		if (type > 0) {//纵向 展现 属性
			for (int i = 0; i < infos.length; i++) {
				String info = infos[i];
				Object fieldValue = getFieldValue(o, info);
				PdfPCell infoCell = new PdfPCell(new Phrase(tableHeader[i] + connect + String.valueOf(fieldValue),
						pdfReportEntity.getCellFont()));//字段名+连接符+字段值
				infoCell.setVerticalAlignment(Element.ALIGN_CENTER);
				infoCell.setHorizontalAlignment(pdfReportEntity.getCellAlign());
				infoCell.setBorder(pdfReportEntity.getTableBorder());
				table.addCell(infoCell);
			}
			
		} else {//横向 展现 属性
			for (int i = 0; i < infos.length; i++) {
				String info = infos[i];
				Object fieldValue = getFieldValue(o, info);
				PdfPCell infoCell = new PdfPCell(new Phrase(tableHeader[i] + connect + String.valueOf(fieldValue),
						pdfReportEntity.getCellFont()));
				infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				infoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				infoCell.setBorder(pdfReportEntity.getTableBorder());
				table.addCell(infoCell);
			}

		}
		document.add(table);
	}

	/**
	 * 添加 数据表
	 * 
	 * @param document
	 * @param pdfModelTable
	 * @throws DocumentException
	 */
	public void addTable(Document document, PdfModelTable pdfModelTable) throws DocumentException {
		System.out.println("添加 表 ");
		List<Object> tableDatas = pdfModelTable.getData();//数据源
		String[] tableHeader = pdfModelTable.getTableHeader();//列名
		String[] fields = pdfModelTable.getFields();//属性字段名
		String defaultNull = pdfModelTable.getColDefaultNull();//null 值时的默认值
		Map<Integer, String> unitMap = pdfModelTable.getUnitMap();// 列的单位
		Map<Integer, Integer> alian = pdfModelTable.getAlian();//列的布局(左，居中，右)

		if (pdfModelTable.getIfTitle()) {// 标题
			PdfPTable tableTitle = new PdfPTable(1);
			tableTitle.setWidthPercentage(pdfModelTable.getTableWidthPercent());
			tableTitle.setSpacingAfter(pdfModelTable.getSpace());// 与下方间距
			PdfPCell ctitle = new PdfPCell(new Phrase(pdfModelTable.getTitle(), pdfModelTable.getTitleFont()));
			ctitle.setBorder(0);
			tableTitle.addCell(ctitle);
			document.add(tableTitle);
		}
		/**
		 * 具体表格
		 */
		PdfPTable table = new PdfPTable(fields.length);
		table.setWidthPercentage(pdfModelTable.getTableWidthPercent());
		table.setSpacingAfter(pdfModelTable.getSpace());
		/**
		 * 表列名 满足 合并表头
		 */
		List<PdfPCell> cellList = new ArrayList<PdfPCell>();
		List<PdfPCell> childList = new ArrayList<PdfPCell>();
		for (String dataTitle : tableHeader) {
			if (dataTitle.indexOf("#") > 0) {
				String[] nomal = dataTitle.split("#");
				String[] child = nomal[1].split(",");
				PdfPCell dataCell = new PdfPCell(new Phrase(nomal[0], pdfModelTable.getHeadFont()));
				dataCell.setColspan(child.length);
				cellList.add(dataCell);
				for (String sd : child) {
					PdfPCell childCell = new PdfPCell(new Phrase(sd, pdfModelTable.getHeadFont()));
					childList.add(childCell);
				}
			} else {
				PdfPCell dataCell = new PdfPCell(new Phrase(dataTitle, pdfModelTable.getHeadFont()));
				dataCell.setRowspan(2);
				cellList.add(dataCell);
			}
		}
		cellList.addAll(childList); //itext的 cell 填充 table 规则 需要将 子列 依次放在 首列之后
		for (PdfPCell cell : cellList) {
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);// 垂直居中 -- 列名 默认
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 水平居中 -- 列名 默认
			table.addCell(cell);
		}

		for (int i = 0; i < tableDatas.size(); i++) {
			Object data = tableDatas.get(i);
			for (int j = 0; j < fields.length; j++) {
				String field = fields[j];
				String unit = null;
				int horizontalAlignment = Element.ALIGN_CENTER;
				if (unitMap != null && unitMap.size() > 0) {
					unit = unitMap.get(j);// 列的单位
				}
				if (alian != null && alian.size() > 0 && alian.containsKey(j)) {
					horizontalAlignment = alian.get(j);// 列的布局
				}
				Object value = "";
				if (field.equals("_id")) {
					value = (Integer) (i + 1);//若传入的 是 _id 则为 自增编号
				} else {
					Object fieldValue = getFieldValue(data, field);
					if (fieldValue == null) {
						value = defaultNull;
					} else {
						value = fieldValue + (unit == null ? "" : unit);//加入 单位
					}
				}
				PdfPCell dataCell = new PdfPCell(new Phrase(value.toString(), pdfModelTable.getCellFont()));
				dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);// 垂直居中
				dataCell.setHorizontalAlignment(horizontalAlignment);// 水平居中
				table.addCell(dataCell);
			}
		}
		document.add(table);
	}

	/**
	 * 添加 图片
	 * 
	 * @param document
	 * @param pdfModelImage
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void addImages(Document document, PdfModelImage pdfModelImage) throws DocumentException, IOException {
		System.out.println("添加 图片 image.... ");
		if (pdfModelImage.getNextPage()) {
			document.add(Chunk.NEXTPAGE);
		}
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(pdfModelImage.getTableWidthPercent());
		table.setSpacingAfter(pdfModelImage.getSpace());
		//标题
		if (pdfModelImage.getIfTitle()) {
			PdfPCell ctitle = new PdfPCell(new Phrase(pdfModelImage.getTitle(), pdfModelImage.getTitleFont()));
			PdfPCell cnewLine = new PdfPCell(new Phrase(Chunk.NEWLINE));
			ctitle.setBorder(0);
			cnewLine.setBorder(0);
			table.addCell(ctitle);
			table.addCell(cnewLine);
		}
		List<Image> images = pdfModelImage.getImages();
		if (images == null || images.isEmpty()) {
			PdfPCell cEmptyResult = new PdfPCell(
					new Phrase(pdfModelImage.getDefaultValue(), pdfModelImage.getCellFont()));
			cEmptyResult.setIndent(30F);
			table.addCell(cEmptyResult);
		} else {
			for (Image image : images) {
				if (image.getWidth() > contentWidth) {
					image.scalePercent(contentWidth / image.getWidth() * pdfModelImage.getScalePercent());// 依照比例缩放 后期可以设定图片大小
				}else{
					image.scalePercent(pdfModelImage.getScalePercent()); //暂时 不考虑 小图 放大。只要把  上方的  分子 分母 对调就好
				}
				PdfPCell cell = new PdfPCell(image);
				cell.setHorizontalAlignment(pdfModelImage.getCellAlign());// 水平居中
				cell.setBorder(0);// 照片 边框为 0
				table.addCell(cell);
			}
		}
		document.add(table);
	}

	/**
	 * 添加 文字说明
	 * 
	 * @param document
	 * @param pdfModelPhrase
	 * @throws DocumentException
	 */
	public void addPhrase(Document document, PdfModelPhrase pdfModelPhrase) throws DocumentException {
		System.out.println("添加 文字 段落 内容 ....");
		if (pdfModelPhrase.getNextPage()) {
			document.add(Chunk.NEXTPAGE);
		}
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(pdfModelPhrase.getTableWidthPercent());
		table.setSpacingAfter(pdfModelPhrase.getSpace());
		//标题
		if (pdfModelPhrase.getIfTitle()) {
			PdfPCell cEvalTitle = new PdfPCell(new Phrase(pdfModelPhrase.getTitle(), pdfModelPhrase.getTitleFont()));
			cEvalTitle.setBorder(0);
			table.addCell(cEvalTitle);
			PdfPCell newLine = new PdfPCell(new Phrase(Chunk.NEWLINE));
			newLine.setBorder(0);
			table.addCell(newLine);
		}
		List<PdfModelPhraseContext> context = pdfModelPhrase.getContext();
		//无内容的情况下 是否 设定默认值
		if ((context == null || context.size() == 0) && pdfModelPhrase.getDefaultNull()) {
			PdfPCell cEmptyResult = new PdfPCell(
					new Phrase(pdfModelPhrase.getDefaultValue(), pdfModelPhrase.getCellFont()));
			cEmptyResult.setIndent(pdfModelPhrase.getIndent());
			cEmptyResult.setBorder(pdfModelPhrase.getTableBorder());
			table.addCell(cEmptyResult);
		} else {
			for (int i = 0; i < context.size(); i++) {
				PdfModelPhraseContext pdfCont = context.get(i);
				Phrase phrase = new Phrase(pdfCont.getContext(), pdfCont.getCellFont());
				PdfPCell cEvalResult = new PdfPCell(phrase);
				cEvalResult.setHorizontalAlignment(pdfCont.getCellAlign());// 水平居中
				cEvalResult.setIndent(pdfModelPhrase.getIndent());// 缩进
				cEvalResult.setBorder(pdfCont.getTableBorder());// 边框
				table.addCell(cEvalResult);
			}
		}
		document.add(table);
	}

	/**
	 * 根据 属性字段名 映射取得 字段值
	 * @param o 实例对象
	 * @param fields 属性字段名
	 * @param formate 时间格式化- 后期可加入 Double格式化 等 其他格式化
	 * @return
	 */
	public Object getFieldValue(Object o, String fields, String formate) {
		try {
			Class<?> clazz = o.getClass();
			Field field1 = clazz.getDeclaredField(fields);
			PropertyDescriptor pd = new PropertyDescriptor(field1.getName(), clazz);
			Method getMethod = pd.getReadMethod();
			if (getMethod.invoke(o) != null) {
				Object ret = getMethod.invoke(o);
				if (ret instanceof Date) {
					return DateUtil.getInstance().format((Date) ret, formate);
				}
				return ret;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public  Object getFieldValue(Object o, String fields) {
		try {
			Class<?> clazz = o.getClass();
			Field field1 = clazz.getDeclaredField(fields);
			PropertyDescriptor pd = new PropertyDescriptor(field1.getName(), clazz);
			Method getMethod = pd.getReadMethod();
			if (getMethod.invoke(o) != null) {
				Object ret = getMethod.invoke(o);
				if (ret instanceof Date) {
					return DateUtil.getInstance().format((Date) ret, "yyyy-mm-dd");
				}
				return ret;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;//对 该类未含有该属性值时 产生的 异常 返回 null
		}
	}

	public float getContentWidth() {
		return contentWidth;
	}

	public void setContentWidth(float contentWidth) {
		this.contentWidth = contentWidth;
	}
}
