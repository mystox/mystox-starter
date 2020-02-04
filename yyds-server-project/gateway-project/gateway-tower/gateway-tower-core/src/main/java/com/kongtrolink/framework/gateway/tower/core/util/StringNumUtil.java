package com.kongtrolink.framework.gateway.tower.core.util;

/**
 * Created by Mg on 2018/5/29.
 */
public class StringNumUtil {

    public static String checkSignalNumber(Integer num){
        if(num ==null){
            return "000";
        }else{
            return String.valueOf(num);
        }
    }

    public static String checkSignalStr(String num){
        if(num ==null || "".equals(num)){
            return "000";
        }else{
            return String.valueOf(num);
        }
    }

}
