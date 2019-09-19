package com.kongtrolink.enttiy;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/12 10:29
 * @Description:
 */
public class AlarmColor {
    private String id;
    private String uniqueCode;
    private String level;
    private String color;
    private Date tCreate;
    private String creator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date gettCreate() {
        return tCreate;
    }

    public void settCreate(Date tCreate) {
        this.tCreate = tCreate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
