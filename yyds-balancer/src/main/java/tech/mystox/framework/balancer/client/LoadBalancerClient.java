package tech.mystox.framework.balancer.client;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡接口定义
 * @author mystox
 */
public interface LoadBalancerClient {
    void execute();

    Map<String, List<String>>  getOperaRouteMap();

    void setOperaRouteMap(Map<String, List<String>> operaRouteMap);

    void retryOpera(String operaCode);
}
