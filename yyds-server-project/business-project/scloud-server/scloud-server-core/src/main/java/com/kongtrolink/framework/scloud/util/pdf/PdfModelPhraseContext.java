package com.kongtrolink.framework.scloud.util.pdf;

import com.itextpdf.text.Element;

/**
 * 属于 PdfModelPhrase 内文字的具体设定 如 文字内容,文字字体,首行缩进 等属性
 * 
 * @author John
 *
 */
public class PdfModelPhraseContext extends PdfModel {

	private String context;// 具体的文本内容

	public PdfModelPhraseContext(String context) {
		this(context,Element.ALIGN_LEFT);//默认 靠左
	}

	public PdfModelPhraseContext(String context, int alian) {
		super.setCellAlign(alian);
		this.context = context;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
