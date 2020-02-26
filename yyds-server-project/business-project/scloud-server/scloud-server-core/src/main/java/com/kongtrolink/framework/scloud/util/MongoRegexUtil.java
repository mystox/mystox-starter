package com.kongtrolink.framework.scloud.util;

/**
 * Mongo特殊字符处理 工具类
 * Created by Eric on 2020/2/10.
 */
public class MongoRegexUtil {

    /**
      * 转义正则特殊字符 （$()*+.[]?\^{},|）
      *
      * @param keyword
      * @return
      */
    public static String escapeExprSpecialWord(String keyword) {
        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
        for (String key : fbsArr) {
            if (keyword.contains(key)) {
                keyword = keyword.replace(key, "\\" + key);
            }
        }
        return keyword;
    }

}
