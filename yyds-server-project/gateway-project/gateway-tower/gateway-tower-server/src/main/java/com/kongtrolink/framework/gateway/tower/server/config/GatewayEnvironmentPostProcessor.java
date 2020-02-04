package com.kongtrolink.framework.gateway.tower.server.config;

import com.kongtrolink.framework.register.config.RegisterEnvironmentPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Created by mystoxlol on 2019/11/11, 13:57.
 * company: kongtrolink
 * description:
 * update record:
 */
public class GatewayEnvironmentPostProcessor extends RegisterEnvironmentPostProcessor {

    Logger logger = LoggerFactory.getLogger(GatewayEnvironmentPostProcessor.class);

    private String[] profiles = {
            "classpath:gateway-transverterRelation.yml",
            "file:config/gateway-transverterRelation.yml"
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        loadResources(environment, application,this.profiles);
        if (logger.isInfoEnabled())
            logger.info("load gateway environment post processor success");
        else
            System.out.println("load gateway environment post processor success");
    }

}
