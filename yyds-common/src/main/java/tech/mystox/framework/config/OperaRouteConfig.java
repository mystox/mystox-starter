package tech.mystox.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2020/1/5 18:34
 * \* Description:
 * \
 */
@Component
@ConfigurationProperties
@RefreshScope
public class OperaRouteConfig {
    Map<String, List<String>> operaRoute;

    public Map<String, List<String>> getOperaRoute() {
        return operaRoute;
    }

    public void setOperaRoute(Map<String, List<String>> operaRoute) {
        this.operaRoute = operaRoute;
    }
}