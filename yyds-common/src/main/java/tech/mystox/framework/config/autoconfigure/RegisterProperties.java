package tech.mystox.framework.config.autoconfigure;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "register")
public class RegisterProperties {

    private String scanBasePackage = "";
    private boolean duplicate = true;
    private int sessionTimeout = 100;
    private String type = "";
    @NotBlank(message = "register.url must be configured")
    private String url;
    //private String serverName;
    //private String version = "1.0.0";
    private String balancer = "BASE";
    private String msgType = "";
    private List<String> webExtension = List.of("classpath:config/vueRouter.js", "file:config/vueRouter.js");
    private Map<String, Object> extension = new HashMap<>();


    public String getScanBasePackage() {
        return scanBasePackage;
    }

    public void setScanBasePackage(String scanBasePackage) {
        this.scanBasePackage = scanBasePackage;
    }

    public Map<String, Object> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, Object> extension) {
        this.extension = extension;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBalancer() {
        return balancer;
    }

    public void setBalancer(String balancer) {
        this.balancer = balancer;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public List<String> getWebExtension() {
        return webExtension;
    }

    public void setWebExtension(List<String> webExtension) {
        this.webExtension = webExtension;
    }
}
