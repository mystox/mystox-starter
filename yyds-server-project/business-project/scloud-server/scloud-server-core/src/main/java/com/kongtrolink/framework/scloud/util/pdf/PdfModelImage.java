package com.kongtrolink.framework.scloud.util.pdf;

import com.itextpdf.text.Element;
import com.itextpdf.text.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * pdf 图片 对象
 * @author John
 *
 */
public class PdfModelImage extends PdfModel{
	

	private List<Image> images = new ArrayList<Image>();//相关 Images
	private String defaultValue;//若无图片显示内容
	private int scalePercent = 100;//图片缩放比例；// 默认100% 
	
	public PdfModelImage(){}
	
	public PdfModelImage(String title,Image images){
		this(title,images,"",100,Element.ALIGN_LEFT);
	}
	public PdfModelImage(String title,Image images,int scalePercent,int cellAlian){
		this(title,images,"",scalePercent,cellAlian);
	}
	public PdfModelImage(String title,Image images, String defaultValue){
		this(title,images,defaultValue,100,Element.ALIGN_LEFT);
	}
	
	public PdfModelImage(String title,Image images, String defaultValue,int scalePercent,int cellAlian){
		super.setTitle(title);
		super.setCellAlign(cellAlian);
		this.images.add(images);
		this.defaultValue = defaultValue;
		this.scalePercent = scalePercent;
	}
	
	
	public PdfModelImage(String title,List<Image> images){
		this(title,images,"",100,Element.ALIGN_LEFT);
	}
	
	public PdfModelImage(String title,List<Image> images, String defaultValue){
		this(title,images,defaultValue,100,Element.ALIGN_LEFT);
	}
	
	public PdfModelImage(String title,List<Image> images,int scalePercent,int cellAlian){
		this(title,images,"",scalePercent,cellAlian);
	}
	
	public PdfModelImage(String title,List<Image> images, String defaultValue,int scalePercent,int cellAlian) {
		super.setTitle(title);
		super.setCellAlign(cellAlian);
		this.images = images;
		this.defaultValue = defaultValue;
		this.scalePercent = scalePercent;
	}
	
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getScalePercent() {
		return scalePercent;
	}

	public void setScalePercent(int scalePercent) {
		this.scalePercent = scalePercent;
	}
	
	
}
