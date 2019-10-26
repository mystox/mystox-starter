package com.kongtrolink.message;

/**
 * Created by gipplelake on 2015/4/15.
 */

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.utils.CommonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author dengqg
 */
public class SmsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsUtil.class);

    public static <T extends ReqBaseMessage> String createSignature(T reqMessage) {
        try {
            String smsKey = reqMessage.getSmsKey();
            Class cls = Class.forName(reqMessage.toString().split("@")[0]);//加载类
            Field[] fields = cls.getDeclaredFields();
            HashMap<String, String> map = new HashMap<>();
            if (fields != null) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.get(reqMessage) != null) {
                        String value = field.get(reqMessage).toString();
                        map.put(field.getName(), value);
                    }
                }
            }
            if (cls.getGenericSuperclass() != null) {
                Class superClass = cls.getSuperclass();
                Field[] superFields = superClass.getDeclaredFields();
                if (superFields != null) {
                    for (Field superField : superFields) {
                        if (!superField.getName().equals("smsKey") && !superField.getName().equals("signature")) {
                            superField.setAccessible(true);
                            if (superField.get(reqMessage) != null) {
                                map.put(superField.getName(), superField.get(reqMessage).toString());
                            }
                        }
                    }
                }
            }
            
            String paramStr = CommonUtil.FormatBizQueryParaMap(map, false);
            String finalStr = smsKey + "&" + paramStr + "&" + smsKey;
            return DigestUtils.md5Hex(finalStr).toLowerCase();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }

    private static <T extends ReqBaseMessage> String createPostString(T message) throws IllegalAccessException, UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        Class cls = message.getClass();
        Field[] fields = cls.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(message) != null) {
                    String value = field.get(message).toString();
                    value = URLEncoder.encode(value, "UTF-8");
                    stringBuilder.append(URLEncoder.encode(field.getName(), "UTF-8")).append("=").append(value).append("&");
                }
            }
        }
        if (cls.getGenericSuperclass() != null) {
            Class superCls = cls.getSuperclass();
            Field[] superFields = superCls.getDeclaredFields();
            if (superFields != null) {
                for (Field superField : superFields) {
                    if (!superField.getName().equals("smsKey")) {
                        superField.setAccessible(true);
                        if (superField.get(message) != null) {
                            stringBuilder.append(URLEncoder.encode(superField.getName(), "UTF-8")).append("=").append(URLEncoder.encode(String.valueOf(superField.get(message)), "UTF-8")).append("&");
                        }
                    }
                }
            }
        }
        if (stringBuilder.indexOf("&") != -1) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static <E extends ReqBaseMessage> RespMessage sendMessage(E message) throws IllegalAccessException, UnsupportedEncodingException {
        String url = "";
        if (message instanceof ReqSingleMessage) {
            url = "http://sendcloud.sohu.com/smsapi/send";
        } else if (message instanceof ReqMultipleMessage) {
            url = "http://sendcloud.sohu.com/smsapi/sendn";
        }
        String requestParams = createPostString(message);
        JSONObject responseObject = CommonUtil.httpRequest(url, "POST", requestParams);
        LOGGER.info("Send Cloud Msg (JSON): {}", responseObject);
        RespMessage respMessage = JSONObject.toJavaObject(responseObject, RespMessage.class);
        return respMessage;

    } 
}
