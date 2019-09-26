package com.kongtrolink.framework.core.entity.session;

/**
 * Created by mystox on 2018/6/26. 权限服务的session集合
 */
public class SessionConstant {
    public static final String SERVER_URI = "SERVER_URI"; //所属业务的uri List<WebPageInfo>
    public static final String CURRENT_SERVICE = "CURRENT_SERVICE"; //所属业务的服务
    public static final String CURRENT_MAIN_MENU = "CURRENT_MAIN_MENU"; //当前菜单
    public static final String CURRENT_MENU = "CURRENT_MENU";
    public static final String CURRENT_USER_MENU = "CURRENT_USER_MENU";//当前用户的菜单
    public static final String CURRENT_PRIV = "CURRENT_PRIV";//当前的权限集
    public static final String CURRENT_USER_PRIV = "CURRENT_USER_PRIV";//当前用户的权限集
    public static final String CURRENT_ROLE_ID = "CURRENT_ROLE_ID"; //角色ID
    public static final String CURRENT_ROLE_NAME = "CURRENT_ROLE_NAME"; //角色名称
    public static final String CURRENT_ORG_ID = "CURRENT_ORG_ID"; //角色名称
    public static final String CURRENT_ORG_TYPE = "CURRENT_ORG_TYPE"; //当前组织类型
    public static final String ROLES = "ROLES";//用户所属角色集
    public static final String LOGIN_USER_ORGANIZATION_ID = "LOGIN_USER_ORGANIZATION_ID";//用户所属组织
    public static final String LOGIN_USER_ORGANIZATION_TYPE = "LOGIN_USER_ORGANIZATION_TYPE";//用户所属组织类型管理□，企业○，部门△


    /*
     * 当前用户组织树
     * Set<ExtTreeItem>
     */

    public static final String CURRENT_ORG_TREE = "CURRENT_ORG_TREE";//当前有用户组织树
    public static final String USER_MANAGER_LIST = "USER_MANAGER_LIST";//当前用户管辖的用户列表

    public static final String LOGIN_USER_SUB_SERVICE = "LOGIN_USER_SUB_SERVICE";//存放当前用户订阅的服务

    public static final String IS_ADMIN = "IS_ADMIN";
    public static final String IS_ROOT = "IS_ROOT";//是否全局管理员

    public static final String TOKEN = "TOKEN";

    public static final String AGENCY_UNIQUE_CODE_LIST = "AGENCY_CODE_LIST";//代理企业的uniquecode列表
    public static final String MANAGER_UNIQUE_CODE_LIST = "MANAGER_UNIQUE_CODE_LIST";//管辖的企业code列表

    public static final String IS_AGENCY = "IS_AGENCY";
    public static final String IS_MANAGER = "IS_MANAGER";

    public static final String CLIENT_VERSION = "CLIENT_VERSION";

    public static final String CURRENT_AUTH_SERVER_URI = "CURRENT_AUTH_SERVER_URI";


    /**
     * 短信验证的的session
     */
    public static final String CHECK_CODE = "CHECK_CODE";
}
