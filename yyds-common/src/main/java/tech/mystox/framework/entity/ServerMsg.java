package tech.mystox.framework.entity;

import java.util.Map;

/**
 * Created by mystoxlol on 2019/9/30, 8:31.
 * company: mystox
 * description: 注册的服务消息实体
 * update record:
 */
public class ServerMsg {


    private String myid;
    private String host;
    private int port;
    private String serverName;
    private String serverVersion;
    private String routeMark;
    private String pageRoute;
    private String serviceUri;
    private String title;
    private String groupCode; //对应云服务编码
    //    private String serverMark; //服务端标识 对应云管的serviceVersion 默认为*
    private Map<String, Object> extension;
    private Long sequence;

    public ServerMsg(String host, int port, String serverName, String serverVersion, String routeMark,
                     String pageRoute, String serviceUri, String title, String groupCode, String myid) {
        this.myid = myid;
        this.host = host;
        this.port = port;
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        this.routeMark = routeMark;
        this.pageRoute = pageRoute;
        this.serviceUri = serviceUri;
        this.title = title;
        this.groupCode = groupCode;
        //        this.serverMark = serverMark;
    }


    public ServerMsg(String host, int port, String serverName, String serverVersion, String routeMark,
                     String pageRoute, String serviceUri, String title, String groupCode) {
        this.myid = myid;
        this.host = host;
        this.port = port;
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        this.routeMark = routeMark;
        this.pageRoute = pageRoute;
        this.serviceUri = serviceUri;
        this.title = title;
        this.groupCode = groupCode;
        //        this.serverMark = serverMark;
    }

    public ServerMsg() {
    }

    public Map<String, Object> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, Object> extension) {
        this.extension = extension;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }


    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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
