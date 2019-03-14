package com.kongtrolink.framework.core.config;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by mystoxlol on 2019/2/19, 22:06.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration
public class ScheduleExecutorConfig
{
    @Bean(autowire = Autowire.BY_NAME, name = "coordinateSchedule")
    public ScheduledExecutorService getCoordinateSchedule()
    {
        return Executors.newSingleThreadScheduledExecutor();
    }
    @Bean(autowire = Autowire.BY_NAME, name = "routeSchedule")
    public ScheduledExecutorService getRouteSchedule()
    {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean(autowire = Autowire.BY_NAME, name = "reconnectSchedule")
    public ScheduledExecutorService getReconnectSchedule()
    {
        return Executors.newSingleThreadScheduledExecutor();
    }


}
