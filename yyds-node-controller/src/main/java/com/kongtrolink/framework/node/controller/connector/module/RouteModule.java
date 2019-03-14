package com.kongtrolink.framework.node.controller.connector.module;

import com.kongtrolink.framework.node.controller.coordinate.module.LoaderBalancerModule;
import com.kongtrolink.framework.node.controller.entity.Route;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2019/2/20, 21:56.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class RouteModule implements ModuleInterface
{
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    LoaderBalancerModule loaderBalancerModule;

    private Map<String, Route[]> routeMap; //路由表


    @Value("${route.period.time}")
    private Long routePeriodTime; //路由更新周期时间
    @Value("${route.expired.time}")
    private Long routeExpiredTime; //路由过期时间


    @Autowired
    @Qualifier(value = "routeSchedule")
    ScheduledExecutorService scheduledExecutorService;//路由调度控制器


    @Override
    public boolean init()
    {
        logger.info("connector-route module init");
        scheduledExecutorService.scheduleWithFixedDelay(() ->
        {
            logger.debug("路由表更新...");
            if (routeMap == null) routeMap = new HashMap<String, Route[]>();
            updateRoute();
        }, 1, routePeriodTime, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 路由表更新
     * @return
     */
    private boolean updateRoute()
    {
        //TODO 路由表更新策略
        return true;
    }

    /**
     * 根据标识获取
     * @param mark
     * @return
     */
    public Route[] getRouteByMark(String mark)
    {
        logger.debug("根据mark{}获取路由信息...",mark);

        Route[] routes = routeMap.get(mark);
        if (routes == null)
            //TODO

        logger.info("路由信息: {} --> {}",mark, routes);
        return null;
    }

    /**
     * 熔断方法
     */
    public void circuitBreaker(String mark)
    {
        logger.warn("mark{}发生熔断...",mark);
        //todo 熔断策略
    }

}
