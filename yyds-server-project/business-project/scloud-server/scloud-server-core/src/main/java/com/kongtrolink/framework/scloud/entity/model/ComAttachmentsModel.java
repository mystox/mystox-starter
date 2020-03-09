package com.kongtrolink.framework.scloud.entity.model;

import java.io.Serializable;

/**
 * 附件上传后返回给前端的
 * Created by Eric on 2020/3/3.
 */
public class ComAttachmentsModel implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -7392748527415312820L;

    private int fileId; //附件表中的主键ID
    private String fileName;    //文件名称

    public ComAttachmentsModel(int fileId, String fileName) {
        this.fileId = fileId;
        this.fileName = fileName;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
