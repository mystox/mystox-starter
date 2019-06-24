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
package com.kongtrolink.framework.model.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抽象类 BaseController 表示基本控制器，所有控制器（包括页面控制器
 * 和功能控制器）都需要继承并实现该抽象类。
 * <p>
 * <p> BaseController 类包含默认的服务器错误提示信息，以及页面重定向方法、从 Session
 * 中获取用户信息的方法。
 * <p>
 * <p> BaseController 类继承了 {}
 * 接口中的方法，继承自该类的 Controller 类必须实现 getLogger() 方法。
 *
 * @author Mosaico
 */

public abstract class BaseController {
    private HttpSession session;
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * 默认的服务器错误信息。
     * <p>
     * 提示用户服务器产生错误或异常的原因。
     */
    public static final String DEFAULT_ERROR_MESSAGE =
            "服务器产生未知异常，请稍后再试。";

    /**
     * 获取页面重定向代码，让服务器实现页面跳转。
     *
     * @param uri 重定向的 URI
     * @return "redirect:" + uri
     */
    public static String redirect(String uri) {
        return "redirect:" + uri;
    }

    /**
     * 从页面请求中获取发送该请求用户当前所在页面的企业唯一码。
     *
     * @return 企业唯一码。
     */
    public String getUniqueCode() {
        return (String) getSession().getAttribute(Const.SESSION_CURRENT_STAMP);
    }

    /**
     * 从页面请求中获取发送该请求用户的 oid 。
     *
     * @param request Http 页面请求。
     * @return 用户 oid 。
     */
    public String getUserId(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Const.SESSION_USER);
        return user.getId();
    }


    /**
     * 从页面请求中获取用户信息 。
     *
     * @param request Http 页面请求。
     * @return
     */
    public User getUser(HttpServletRequest request) {
        return getUser();
    }

    public User getUser() {
        Object attribute = getSession().getAttribute(Const.SESSION_USER);
        if (attribute != null)
            return JSONObject.toJavaObject((JSON) JSON.toJSON(attribute), User.class);
        return null;
    }

    public List<String> getManagerUsers() {
        Object attribute = getSession().getAttribute(SessionConstant.USER_MANAGER_LIST);
        if (attribute != null)
            return JSONArray.parseArray(JSON.toJSONString(attribute), String.class);
        return null;
    }

    public String getClientVersion() {
        return (String) getSession().getAttribute(SessionConstant.CLIENT_VERSION);
    }

    public String getUserId() {
        return getUser().getId();
    }

    public String getCurrentOrgId() {
        return (String) getSession().getAttribute(SessionConstant.CURRENT_ORG_ID);
    }

    public String getCurrentOrgType() {
        return (String) getSession().getAttribute(SessionConstant.CURRENT_ORG_TYPE);
    }

    public String getLoginUserOrgId() {
        return (String) getSession().getAttribute(SessionConstant.LOGIN_USER_ORGANIZATION_ID);
    }

    public String getLoginUserOrgType() {
        return (String) getSession().getAttribute(SessionConstant.LOGIN_USER_ORGANIZATION_TYPE);
    }

    public String getCurrentRoleId() {
        return (String) getSession().getAttribute(SessionConstant.CURRENT_ROLE_ID);
    }

    public String getCurrentRoleName() {
        return (String) getSession().getAttribute(SessionConstant.CURRENT_ROLE_NAME);
    }

    public WebPageInfo getCurrentService() {
        Object attribute = getSession().getAttribute(SessionConstant.CURRENT_SERVICE);
        if (attribute != null)
            return JSONObject.toJavaObject((JSON) attribute, WebPageInfo.class);
        return null;
//        return (WebPageInfo) getSession().getAttribute(SessionConstant.CURRENT_SERVICE);
    }

    public WebPageInfo getCurrentMenu() {
        Object attribute = getSession().getAttribute(SessionConstant.CURRENT_MENU);
//        if (attribute != null)
//            return JSONObject.toJavaObject((JSON) attribute, WebPageInfo.class);
//        return null;
        return (WebPageInfo) attribute;
//        return (WebPageInfo) getSession().getAttribute(SessionConstant.CURRENT_MENU);
    }

    public List<String> getManagerUniqueCode() {
        Object attribute = getSession().getAttribute(SessionConstant.MANAGER_UNIQUE_CODE_LIST);
        if (attribute != null)
            return JSONArray.parseArray(JSON.toJSONString(attribute), String.class);
        return null;
//        return (List<String>) attribute;
    }

    public List<String> getAgengcyUniqueCode() {
        Object attribute = getSession().getAttribute(SessionConstant.AGENCY_UNIQUE_CODE_LIST);
        if (attribute != null)
            return JSONArray.parseArray(JSON.toJSONString(attribute), String.class);
        return null;
//        return (List<String>) getSession().getAttribute(SessionConstant.AGENCY_UNIQUE_CODE_LIST);
    }

    public WebPageInfo getCurrentUserMenu() {
        Object attribute = getSession().getAttribute(SessionConstant.CURRENT_USER_MENU);
        if (attribute != null)
            return JSONObject.toJavaObject((JSON) attribute, WebPageInfo.class);
        return null;
//        return (WebPageInfo) getSession().getAttribute(SessionConstant.CURRENT_USER_MENU);
    }

    public List<WebPageInfo> getCurrentUserPriv() {
        Object attribute = getSession().getAttribute(SessionConstant.CURRENT_USER_PRIV);
        if (attribute != null)
            return JSONArray.parseArray(JSON.toJSONString(attribute), WebPageInfo.class);
        return null;
//        return null;
//        return (List<WebPageInfo>) getSession().getAttribute(SessionConstant.CURRENT_USER_PRIV);
    }

    public List<WebPageInfo> getCurrentPriv() {
        Object attribute = getSession().getAttribute(SessionConstant.CURRENT_PRIV);
        if (attribute != null)
            return JSONArray.parseArray(JSON.toJSONString(attribute), WebPageInfo.class);
        return null;
//        return (List<WebPageInfo>) getSession().getAttribute(SessionConstant.CURRENT_PRIV);
    }

    public WebPageInfo getCurrentMainMenu() {
        Object attribute = getSession().getAttribute(SessionConstant.CURRENT_MAIN_MENU);
        if (attribute != null)
            return JSONObject.toJavaObject((JSON) attribute, WebPageInfo.class);
        return null;
//        return (WebPageInfo) getSession().getAttribute(SessionConstant.CURRENT_MAIN_MENU);
    }

    public List<WebPageInfo> getServerUri() {
        Object attribute = getSession().getAttribute(SessionConstant.SERVER_URI);
        if (attribute != null)
            return JSONArray.parseArray(JSON.toJSONString(attribute), WebPageInfo.class);
        return null;
//        return (List<WebPageInfo> ) getSession().getAttribute(SessionConstant.SERVER_URI);
    }

    public boolean isAdmin() {
        Object o = getSession().getAttribute(SessionConstant.IS_ADMIN);
        if (o == null)
            return false;
        else
            return (Boolean) o;
    }

    public boolean isAgency() {
        Object o = getSession().getAttribute(SessionConstant.IS_AGENCY);
        if (o == null)
            return false;
        else
            return (Boolean) o;
    }

    public boolean isManager() {
        Object o = getSession().getAttribute(SessionConstant.IS_MANAGER);
        if (o == null)
            return false;
        return (Boolean) o;
    }

    public boolean isRoot() {
        Object o = getSession().getAttribute(SessionConstant.IS_ROOT);
        if (o == null)
            return false;
        return (Boolean) o;
    }


    public HttpSession getSession() {
        return this.session == null ? getHttpServletRequest().getSession() : this.session;

    }

    public HttpServletRequest getHttpServletRequest() {
        return this.request == null ? ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest() : this.request;
    }

    public HttpServletResponse getHttpServletResponse() {
        return this.request == null ? ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse() : this.response;
    }


    protected String getRequestHost() {
        String url = getHttpServletRequest().getRequestURL().toString();
        if (StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils.startsWithIgnoreCase(url, "http://")) {
            Pattern p = Pattern.compile("(?<=//)((\\w)+\\.)+\\w+");
            Matcher m = p.matcher(url);
            if (m.find()) {
                return m.group();
            }
        }
        return "";
    }

}
