/**
 * *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************
 */
package com.kongtrolink.framework.entity;

public class JsonResult {
    
    private String info = "请求成功！";
    private Boolean success = true;
    private Object data;
    private int count;
    private Object otherInfo;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public JsonResult() {
        printResult();
    }

    public JsonResult(Object data) {
        this.data = data;
        printResult();
    }

    public JsonResult(Object data, int count){
        this.data = data;
        this.count = count;
        printResult();
    }

    public JsonResult(String info, Boolean success) {
        this.info = info;
        this.success = success;
        printResult();
    }

    public Boolean getSuccess() {
        return success;
    }

    public Object getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(Object otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonResult{" + "info=" + info + ", success=" + success + ", data=" + data + '}';
    }
    
    private void printResult() {
//    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println(sdf.format(new Date())+" >> "+this);
    }
    
}
