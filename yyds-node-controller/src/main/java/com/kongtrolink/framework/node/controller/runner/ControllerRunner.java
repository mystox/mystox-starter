package com.kongtrolink.framework.node.controller.runner;

import com.kongtrolink.framework.node.controller.business.ApiService;
import com.kongtrolink.framework.node.controller.connector.ConnectorService;
import com.kongtrolink.framework.node.controller.coordinate.CoordinateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/2/19, 8:57.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class ControllerRunner implements ApplicationRunner
{
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    CoordinateService coordinateService;
    @Autowired
    ConnectorService connectorService;
    @Autowired
    ApiService apiService;

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (coordinateService.startService())
            if (connectorService.startService())
                if (apiService.startService())
                    logger.info("controller service start successfully!!!");
                else logger.error("apiService start error...");
            else logger.error("connectorService start error...");
        else logger.error("coordinateService start error...");
    }
}
