package com.kongtrolink.framework.register.entity;

/**
 * Created by mystoxlol on 2019/9/30, 8:31.
 * company: kongtrolink
 * description: 注册的服务消息实体
 * update record:
 */
public class ServerMsg {
    private String host;
    private int port;
    private String serverName;
    private String serverVersion;
    private String routeMark;
    private String pageRoute;
    private String serviceUri;
    private String title;


    public ServerMsg(String host, int port, String serverName, String serverVersion, String routeMark, String pageRoute, String serviceUri, String title) {
        this.host = host;
        this.port = port;
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        this.routeMark = routeMark;
        this.pageRoute = pageRoute;
        this.serviceUri = serviceUri;
        this.title = title;
    }

    public ServerMsg() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRouteMark() {
        return routeMark;
    }

    public void setRouteMark(String routeMark) {
        this.routeMark = routeMark;
    }

    public String getPageRoute() {
        return pageRoute;
    }

    public void setPageRoute(String pageRoute) {
        this.pageRoute = pageRoute;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }
}
