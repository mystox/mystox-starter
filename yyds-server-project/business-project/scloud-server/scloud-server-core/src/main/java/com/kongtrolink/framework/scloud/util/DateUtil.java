package com.kongtrolink.framework.scloud.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 时间/日期 工具类
 * Created by Eric on 2020/2/5.
 */
public class DateUtil {
    private static DateUtil instance;

    private final SimpleDateFormat defaultFormat;

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateUtil() {
        defaultFormat = new SimpleDateFormat(DEFAULT_PATTERN);
    }

    public static DateUtil getInstance() {
        if (instance == null) {
            instance = new DateUtil();
        }
        return instance;
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

    /**
     * UTC时间 改成 本地时间
     * @param utcTime
     * @param patten
     * @return
     */
    public static String utc2Local(String utcTime, String patten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(patten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(patten);
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }
    public static String LocalToUtc(String localTime, String patten) {
        SimpleDateFormat localFormater = new SimpleDateFormat(patten);
        localFormater.setTimeZone(TimeZone.getDefault());
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = localFormater.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat utcFormater = new SimpleDateFormat(patten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = utcFormater.format(gpsUTCDate.getTime());
        return utcTime;
    }
    public static Date utc2Local(Date date,String patten) {
        SimpleDateFormat sdf = new SimpleDateFormat(patten);
        String utcTime = sdf.format(date);
        String localTime = utc2Local(utcTime,patten);
        Date localDate = null;
        try {
            localDate = sdf.parse(localTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localDate;
    }
    public static Date LocalToUtc(Date date,String patten) {
        SimpleDateFormat sdf = new SimpleDateFormat(patten);
        String utcTime = sdf.format(date);
        String localTime = LocalToUtc(utcTime,patten);
        Date localDate = null;
        try {
            localDate = sdf.parse(localTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localDate;
    }
}
