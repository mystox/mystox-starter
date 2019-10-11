package com.kongtrolink.framework.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 14:19
 * @Description:日期工具
 */
public class DateUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Calendar calendar = Calendar.getInstance();

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public static Calendar getCalendar() {
        return calendar;
    }

    /**
     * @auther: liudd
     * @date: 2018/6/26 15:24
     * 功能描述:HHMMSS转换成数值
     */
    public static int HHMMSSToInt(String date){
        String[] dateStr = date.split(":");
        if(dateStr.length !=3){
            return -3;
        }

        String HH = dateStr[0];
        String mm = dateStr[1];
        String ss = dateStr[2];
        try{
            int intHH = Integer.parseInt(HH) * 10000;
            int intmm = Integer.parseInt(mm) * 100;
            int intSS = Integer.parseInt(ss);
            return (intHH + intmm + intSS);
        }catch (Exception e){
            return -3;
        }
    }
}
