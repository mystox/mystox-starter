package com.kongtrolink.framework.scloud.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串工具类
 */
public class StringUtil {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdfSSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public static boolean isNUll(String str){
        if(null == str){
            return true ;
        }
        if("".equals(str) || "".equals(str.trim())){
            return true ;
        }
        return false ;
    }

    //根据时间产生流水号
    public static  String createCodeByDate(Date curTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(curTime);
    }

    /**
     * @auther: liudd
     * @date: 2019/1/16 15:19
     * 功能描述:根据前缀和后缀生成新时间，专用于巡检工单使用巡检开始时间的年月日时 + 当前时间的分秒，毫秒
     * 生成新时间，然后生成流水号
     */
    public static Date preSufDate(Date preDate, Date sufDate){
        String fixFormat = sdfSSS.format(preDate);
        String preStr = fixFormat.substring(0, 13);
        String subFormat = sdfSSS.format(sufDate);
        String sufStr = subFormat.substring(13);
        try {
            Date parseDate = sdfSSS.parse(preStr + sufStr);
            return parseDate;
        }catch (Exception e){
            return sufDate;
        }
    }

    //将数字转换为指定长度的字符串，不足高位补0
    public static String createStrByNum(long val, int len) {
        if(len <1){
            return null ;
        }
        String str = String.valueOf(val);
        if(str.length() > len){
            return str.substring(0, len);
        }
        while (str.length() < len){
            str = "0" + str;
        }
        return str;
    }

    /**
     * 时分秒之间转换成int型。要求时间格式HH:mm，
     * 字符串为空返回-1，格式错误返回-2，数据问题返回-3
     * @param date
     * @return
     */
    public static int dateToInt(String date){
        if(isNUll(date)){
            return -1;
        }
        String[] dateStr = date.split(":");
        if(dateStr.length != 2){
            return -2;
        }
        String HH = dateStr[0];
        String mm = dateStr[1];
        try{
            int intHH = Integer.parseInt(HH) * 10000;
            int intmm = Integer.parseInt(mm) * 100;
            return (intHH + intmm);
        }catch (Exception e){
            return -3;
        }
    }

    /**
     * 将int型转换成时分秒 HH:mm
     * @param date
     * @return
     */
    public static String intToDate(int date){
        int HH = date / 10000;
        String strHH = HH+"";
        if(HH <10) {
            strHH = "0" + HH;
        }
        int modMM = date % 10000;
        int mm = modMM / 100;
        String strmm = mm + "";
        if(mm < 10) {
            strmm = "0" + mm;
        }

        return strHH + ":" + strmm;
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
    public static int dateToInt(Date date){
        String format = sdf.format(date);
        String substring = format.substring(11);
        return HHMMSSToInt(substring);
    }

    /**
     * @auther: liudd
     * @date: 2018/6/26 15:23
     * 功能描述:数值转换成HHMMSS
     */
    public static String intToHHMMSS(int value){
        int HH = value / 10000;
        String strHH = HH+"";
        if(HH <10) {
            strHH = "0" + HH;
        }
        int modMM = value % 10000;
        int mm = modMM / 100;
        String strmm = mm + "";
        if(mm < 10) {
            strmm = "0" + mm;
        }
        int ss = modMM % 100;
        String strSS = ss + "";
        if(ss < 10){
            strSS = "0" +  strSS;
        }
        return strHH + ":" + strmm + ":" + strSS;
    }

    /**
     * 字符串转换成数字(DD:HH)
     * @param date
     * @return
     */
    public static int HHDateToInt(String date){
        String[] dateStr = date.split(":");
        if(dateStr.length != 2){
            return -2;
        }
        String DD = dateStr[0];
        String HH = dateStr[1];
        try{
            int intHH = Integer.parseInt(DD) * 100;
            int intmm = Integer.parseInt(HH) ;
            return (intHH + intmm);
        }catch (Exception e){
            return -3;
        }
    }

    /**
     * 获取日期的年月日信息（YYYY-MM-dd）
     * @param date
     * @return
     */
    public static String getYMD(Date date){
        String format = sdf.format(date);
        String ymd = format.substring(0, 10);
        return ymd;
    }

    /**
     * @auther: liudd
     * @date: 2018/6/28 16:01
     * 功能描述:获取时间字符串格式
     */
    public static String getDateStr(Date date){
        return sdf.format(date);
    }
}
