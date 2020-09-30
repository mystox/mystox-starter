package tech.mystox.demo.config;

import tech.mystox.demo.entity.OperaResourceTest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@RefreshScope
public class OperaRouteConfigTest {

    private OperaResourceTest groupRoute;

    private OperaResourceTest rootRoute;


    public OperaResourceTest getGroupRoute() {
        return groupRoute;
    }

    public void setGroupRoute(OperaResourceTest groupRoute) {
        this.groupRoute = groupRoute;
    }

    public OperaResourceTest getRootRoute() {
        return rootRoute;
    }

    public void setRootRoute(OperaResourceTest rootRoute) {
        this.rootRoute = rootRoute;
    }
}