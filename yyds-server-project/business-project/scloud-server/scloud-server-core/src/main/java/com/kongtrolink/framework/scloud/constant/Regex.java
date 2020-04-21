package com.kongtrolink.framework.scloud.constant;

/**
 * 正则表达式常量，主要用于Excel解析
 * Created by Eric on 2020/4/16.
 */
public class Regex {

    public static final String DEFAULT = "^[\\u4e00-\\u9fa5\\w-]{1,20}$";
    public static final String TIER_CODE = "^([0-9]{2}){1,3}$";
    public static final String RESOURCE_NAME = "^[\\u4e00-\\u9fa5\\w-]{1,20}$";
    public static final String ADDRESS = "^[\\u4e00-\\u9fa5\\w-]{1,50}$";
    public static final String COORDINATE = "^(-)?(([1-9]?[0-9]|1[0-7][0-9])(\\.[0-9]{1,6})?),(-)?(([1-6]?[0-9]|7[0-3])(\\.[0-9]{1,6})?)$"; //经度 (-180,180) 纬度(-74,74)
    public static final String SITE_TYPE = "^(A级机房|B级机房|C级机房|D级机房)$";
    public static final String SITE_CODE = "^\\d{10}$";
    public static final String PERSON_NAME = "^[\\u4e00-\\u9fa5a-zA-Z0-9·]{2,20}$";
    public static final String CELLPHONE = "^((13\\d)|(15\\d)|(18\\d)|147)[0-9]{8}$";
    public static final String USERNAME = "^\\w{2,20}$";
    public static final String EMAIL = "^[\\w-]+(.[\\w-]+)+@[\\w-]+(.[\\w-]+)+$";
    public static final String JOB_STATUS = "^(在职|离职)$";
    public static final String EDUCATION = "^(小学|初中|高中|初中中专|高中中专|大专|本科|研究生)$";
    public static final String IP = "^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})){3}$";
}
