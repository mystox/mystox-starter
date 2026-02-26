package tech.mystox.framework.config.autoconfigure;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "server")
public class ServerProperties {

    @NotBlank(message = "server.name must be configured")
    private String name;
    @NotBlank(message = "server.version must be configured")
    private String version;
    private String mark = "*";
    @NotBlank(message = "server.groupCode must be configured")
    private String groupCode;
    @NotBlank(message = "server.host must be configured")
    private String host;
    @NotNull(message = "server.port must be configured")
    private int port;
    private String title = "";
    private String serverUri = "";
    private String pageRoute = "";
    private String routeMark = "";
    private String msgBus = "mqtt";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public String getPageRoute() {
        return pageRoute;
    }

    public void setPageRoute(String pageRoute) {
        this.pageRoute = pageRoute;
    }

    public String getRouteMark() {
        return routeMark;
    }

    public void setRouteMark(String routeMark) {
        this.routeMark = routeMark;
    }

    public String getMsgBus() {
        return msgBus;
    }

    public void setMsgBus(String msgBus) {
        this.msgBus = msgBus;
    }
}
