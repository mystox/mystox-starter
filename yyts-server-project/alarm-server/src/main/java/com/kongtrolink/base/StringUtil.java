package com.kongtrolink.base;

/**
 * @Auther: liudd
 * @Date: 2019/9/16 14:10
 * @Description:
 */
public class StringUtil {

    public static boolean isNUll(String str){
        if(null == str){
            return true ;
        }
        if("".equals(str) || "".equals(str.trim())){
            return true ;
        }
        return false ;
    }
}
