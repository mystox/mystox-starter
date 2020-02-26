package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.base.GeneratedValue;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 图片存放 类
 * Created by Eric on 2020/2/12.
 */
@Document(collection = "sys_image")
public class ImageFileInfo {
    @Field(value = "id")
    @GeneratedValue
    private Integer id; //主键

    private String type;    //图片类型："logo"
    private String uniqueCode;  //企业Id
    private FacadeView uploader;    //上传人
    private byte[] image;   //图片二进制

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public FacadeView getUploader() {
        return uploader;
    }

    public void setUploader(FacadeView uploader) {
        this.uploader = uploader;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
