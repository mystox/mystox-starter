package com.kongtrolink.framework.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 14:19
 * @Description:日期工具
 */
public class DateUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat otherDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public static Calendar calendar = Calendar.getInstance();

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
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

    /**
     * @auther: liudd
     * @date: 2018/7/5 14:30
     * 功能描述:根据时间获取HH:mm:ss的数值型
     */
    public static int timeToInt(Date date){
        String format = simpleDateFormat.format(date);
        String substring = format.substring(11);
        return HHMMSSToInt(substring);
    }

    public static int getWeek(Date date){
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static Date parse(String str){
        try {
         return    simpleDateFormat.parse(str);
        }catch (Exception e){
            return null;
        }
    }

    public static String format(Date date){
        return simpleDateFormat.format(date);
    }

    public static String getYMD(Date date){
        return otherDateFormat.format(date).substring(0, 8);
    }

    public static String getYear_week(Date date){
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) + Contant.UNDERLINE + calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static void main(String[] a){
        String date = "2020-01-26 00:00:00";
        Date parse = DateUtil.parse(date);
        System.out.println(parse + "; int:" + parse.getTime());
        Date curTime = new Date();
        System.out.println(curTime.getTime());
        String ymd = getYMD(curTime);
        System.out.println("ymd:" + ymd);

        calendar.setTime(curTime);
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        System.out.println("year:" + year+ "; week:" + week);
    }
}
