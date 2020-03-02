package com.kongtrolink.framework.reports.entity.fsuOfflineDetails;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/2 10:19
 * \* Description:
 * \
 */
public class FsuOfflineDetailsTemp {
    private String id;
    private Integer year;
    private Integer month;
    private Date tempDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Date getTempDate() {
        return tempDate;
    }

    public void setTempDate(Date tempDate) {
        this.tempDate = tempDate;
    }
}