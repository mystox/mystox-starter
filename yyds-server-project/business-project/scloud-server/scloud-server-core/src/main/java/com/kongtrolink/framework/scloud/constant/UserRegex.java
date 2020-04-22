package com.kongtrolink.framework.scloud.constant;

public class UserRegex {

    public static final String USERNAME = "^\\w{2,20}$";
    public static final String DEFAULT = "^[\\u4e00-\\u9fa5\\w-]{1,20}$";
    public static final String PERSON_NAME = "^[\\u4e00-\\u9fa5\\w-]{2,20}$";
    public static final String CELLPHONE = "^((13\\d)|(15\\d)|(18\\d)|147)[0-9]{8}$";
    public static final String EMAIL = "^[\\w-]+(.[\\w-]+)+@[\\w-]+(.[\\w-]+)+$";
    public static final String USER_TIME = "^(长期|临时)$";
    public static final String VALID_TIME = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
}
