/**
 * *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 * <p>
 * This file is part of Kongtrolink techology Co.Ltd property.
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * *****************************************************
 */
package com.kongtrolink.framework.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class SessionUtil {
    public static HttpSession getSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getSession();
    }
    public static <T> T getAttribute(String sessionKey, Class<T> clazz) {
        Object attribute = getSession().getAttribute(sessionKey);
        return getObject(attribute, clazz);
    }

    public static <T> T getAttribute(HttpSession session, String sessionKey, Class<T> clazz) {
        Object attribute = session.getAttribute(sessionKey);
        return getObject(attribute, clazz);
    }

    public static <T> List<T> getAttributeArray(HttpSession session, String sessionKey, Class<T> clazz) {
        Object attribute = session.getAttribute(sessionKey);
        return getArray(attribute, clazz);

    }

    public static <T> List<T> getAttributeArray(String sessionKey, Class<T> clazz) {
        Object attribute = getSession().getAttribute(sessionKey);
        return getArray(attribute, clazz);
    }

    static <T> T getObject(Object attribute, Class<T> clazz) {
        if (attribute != null) {
            if (clazz.isInstance(attribute)) {
                return (T) attribute;
            }
            if (attribute instanceof JSONObject)
                return JSONObject.toJavaObject((JSON) attribute, clazz);
        }
        return null;
    }

    static <T> List<T> getArray(Object attribute, Class<T> clazz) {
        if (attribute != null) {
            if (clazz.isInstance(attribute)) {
                return (List<T>) attribute;
            }
            if (attribute instanceof JSONArray)
                return JSONArray.parseArray(JSON.toJSONString(attribute), clazz);
        }
        return null;
    }


}
