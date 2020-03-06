package com.kongtrolink.framework.scloud.init;

import com.kongtrolink.framework.scloud.util.JobFactory;
import com.kongtrolink.framework.scloud.util.QuartzManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author Mag
 **/
@Component
public class QuartzConfig {

    @Bean("testSchedulerFactoryBean")
    public SchedulerFactoryBean testSchedulerFactoryBean(JobFactory jobFactory){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        return schedulerFactoryBean;
    }

}
