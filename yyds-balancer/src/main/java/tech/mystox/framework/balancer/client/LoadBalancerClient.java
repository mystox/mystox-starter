package tech.mystox.framework.balancer.client;

import java.util.List;
import java.util.Map;

public interface LoadBalancerClient {
    public void execute();

    public Map<String, List<String>>  getOperaRouteMap();

    public void setOperaRouteMap(Map<String, List<String>> operaRouteMap);
}
