package com.kongtrolink.framework.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Auther: liudd
 * @Date: 2019/9/16 14:10
 * @Description:
 */
public class StringUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static Random random = new Random();
    public static boolean isNUll(String str){
        if(null == str){
            return true ;
        }
        if("".equals(str) || "".equals(str.trim())){
            return true ;
        }
        return false ;
    }

    public static String getCode(Date date){
        String format = simpleDateFormat.format(date);
        String ranInt = random.nextInt(10) + "";
        return format + ranInt;
    }

    public static int getIndexNum(List<String> numList, String val){
        int intVal = getIntVal(val);
        int size = numList.size();
        int index = 0;
        for(int i=size-1; i>=0;i--){
            String num = numList.get(i);
            System.out.println("num:" + num + "; val:" + val);
            int intNum = getIntVal(num);
            if(intNum > intVal){
                continue;
            }
            index = i;
            break;
        }
        return index;
    }

    public static int getIntVal(String val){
        switch (val){
            case "一":
                return 1;
            case "二":
                return 2;
            case "三":
                return 3;
            case "四":
                return 4;
            case "五":
                return 5;
            case "六":
                return 6;
            case "七" :
                return 7;
            case "八":
                return 8;
            default:
                return 0;
        }
    }
}
