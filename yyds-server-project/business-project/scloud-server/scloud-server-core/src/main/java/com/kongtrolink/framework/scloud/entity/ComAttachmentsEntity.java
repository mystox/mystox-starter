package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.base.GeneratedValue;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 附件
 * Created by Eric on 2020/3/3.
 */
public class ComAttachmentsEntity {
    @Field(value="id")
    @GeneratedValue
    private int id;// 主键

    private String fileName;    //文件名
    private Long size;  //文件大小
    private String fileType;    //文件类型
    private byte[] data;   //文件内容
    private String aliName; //文件别名
    private Long createTime;    //上传时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getAliName() {
        return aliName;
    }

    public void setAliName(String aliName) {
        this.aliName = aliName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
