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
package com.kongtrolink.framework.core.entity.session;

import java.io.Serializable;
import java.util.List;

/**
 *页面服务的实体,用来页面显示服务选择菜单数据及其服务基本标识
 * @author Mosaico
 */
public class WebPageInfo implements Serializable
{

    private static final long serialVersionUID = 99349880817197110L;
    private String name; //服务名
    private String uri; //页面路径
    private int priority = -1; //优先级编码
    private List<WebPageInfo> nextMenuList;  //子菜单集合，用于前端生成树
    private String code;    //菜单编码
    private String superCode;   //父菜单编码
    private String mark;    //后台请求路由标记
    private String serverCode;  //服务编码
    private String pageRoute;   //页面路由
    private String type;    //服务类型
    private boolean backMark;   //服务是否返回管理页面标记
    private String hierarchyName; //权限层级名

    private boolean isRoot = false; //是否管理页面


    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }


    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public boolean isBackMark()
    {
        return backMark;
    }

    public void setBackMark(boolean backMark)
    {
        this.backMark = backMark;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPageRoute()
    {
        return pageRoute;
    }

    public void setPageRoute(String pageRoute)
    {
        this.pageRoute = pageRoute;
    }

    public String getServerCode()
    {
        return serverCode;
    }

    public void setServerCode(String serverCode)
    {
        this.serverCode = serverCode;
    }

    public String getMark()
    {
        return mark;
    }

    public void setMark(String mark)
    {
        this.mark = mark;
    }

    public WebPageInfo()
    {
    }

    public WebPageInfo(String name, String uri, String code)
    {
        this.name = name;
        this.uri = uri;
        this.code = code;
    }

    public WebPageInfo(String name, String uri, String code, String superCode)
    {
        this.name = name;
        this.uri = uri;
        this.code = code;
        this.superCode = superCode;
    }

    public WebPageInfo(String name, String uri, int priority, String code)
    {
        this.name = name;
        this.uri = uri;
        this.priority = priority;
        this.code = code;
    }

    public WebPageInfo(String name, String uri, int priority, String code, String superCode)
    {
        this.name = name;
        this.uri = uri;
        this.priority = priority;
        this.code = code;
        this.superCode = superCode;
    }

    /**
     * 订阅服务
     * @param name
     * @param uri
     * @param code
     * @param mark
     * @param priority
     */
    public WebPageInfo(String name, String uri, String code, String mark, int priority)
    {
        this.name = name;
        this.uri = uri;
        this.code = code;
        this.mark = mark;
        this.priority = priority;
    }

    /**
     * @param name
     * @param uri
     * @param mark
     * @param priority
     * @param code
     * @param superCode
     * @param serverCode
     */
    public WebPageInfo(String name, String uri, String mark, int priority, String code, String superCode, String serverCode)
    {
        this.name = name;
        this.uri = uri;
        this.mark = mark;
        this.priority = priority;
        this.code = code;
        this.superCode = superCode;
        this.serverCode = serverCode;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getSuperCode()
    {
        return superCode;
    }

    public void setSuperCode(String superCode)
    {
        this.superCode = superCode;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public List<WebPageInfo> getNextMenuList()
    {
        return nextMenuList;
    }

    public void setNextMenuList(List<WebPageInfo> nextMenuList)
    {
        this.nextMenuList = nextMenuList;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }


}
