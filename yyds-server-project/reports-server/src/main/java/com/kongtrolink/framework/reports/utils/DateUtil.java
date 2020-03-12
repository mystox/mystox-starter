package com.kongtrolink.framework.reports.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/4 10:16
 * \* Description:
 * \
 */
public class DateUtil {
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat defaultFormat;
    private DateUtil() {
        defaultFormat = new SimpleDateFormat(DEFAULT_PATTERN);
    }

    public static DateUtil getInstance() {
        return SingleInstance.instance;
    }

    private static class SingleInstance{
        private final static DateUtil instance = new DateUtil();
    }


    public String format(Date date) {
        return defaultFormat.format(date);
    }
    public String format(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
    public Date parse(String resource) {
        Date date = null;
        try {
            date = defaultFormat.parse(resource);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    public Date parse(String resource, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(resource);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
    public  Date getFirstDayOfMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,0);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    public  Date getLastDayOfMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,0);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }
    public static void main(String[] args)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = DateUtil.getInstance().getLastDayOfMonth();
        System.out.println(format.format(time));
        System.out.println(time.getTime());


    }
}