/**
 * *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************
 */
package com.kongtrolink.framework.scloud.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    
    public static final String UID = "UID";
    

    public static Cookie createCookie(boolean savePassword, String key, String value) {
        Cookie cookie = new Cookie(key, value);
        if (savePassword) {
            cookie.setMaxAge(24 * 30 * 60 * 60);
        }
        cookie.setPath("/");
        return cookie;
    }
    

    public static void destroy(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        response.setContentType("text/html");
        for (Cookie c : cookies) {
            c.setValue("");
            c.setMaxAge(0);
            c.setPath("/");
            response.addCookie(c);
        }
    }
    
}
