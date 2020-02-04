package com.kongtrolink.framework.gateway.tower.core.entity.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Time {
    @XmlElement(name = "Years")
    private int year;
    @XmlElement(name = "Month")
    private int month;
    @XmlElement(name = "Day")
    private int day;
    @XmlElement(name = "Hour")
    private int hour;
    @XmlElement(name = "Minute")
    private int minute;
    @XmlElement(name = "Second")
    private int second;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
