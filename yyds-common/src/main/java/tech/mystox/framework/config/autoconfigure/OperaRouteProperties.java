package tech.mystox.framework.config.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * \* @author: mystox
 * \* Date: 2020/1/5 18:34
 * \* Description:
 * \
 */
@ConfigurationProperties(prefix = "balance")
public class OperaRouteProperties {
    Map<String, List<String>> operaRoute;

    public Map<String, List<String>> getOperaRoute() {
        return operaRoute;
    }

    public void setOperaRoute(Map<String, List<String>> operaRoute) {
        this.operaRoute = operaRoute;
    }
}