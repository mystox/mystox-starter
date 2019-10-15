package com.kongtrolink.framework.base;

/**
 * @Auther: liudd
 * @Date: 2019/5/16 14:30
 * @Description:
 */
public class MongoUtil {

    /**
     * @auther: liudd
     * @date: 2019/5/16 14:30
     * 功能描述:转义正则特殊字符
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
