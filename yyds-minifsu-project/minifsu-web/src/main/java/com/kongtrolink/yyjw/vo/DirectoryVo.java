package com.kongtrolink.yyjw.vo;

import com.kongtrolink.yyjw.model.Document;

import java.io.Serializable;
import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/25 10:44
 * \* Description:
 * \
 */
public class DirectoryVo implements Serializable {
    private List<Document> list;
    private String name;
    private Integer number;

    public List<Document> getList() {
        return list;
    }

    public void setList(List<Document> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}