package com.kongtrolink.framework.node.controller.business.module;

import com.kongtrolink.framework.node.controller.connector.ConnectorService;
import com.kongtrolink.framework.node.controller.coordinate.CoordinateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/22, 9:59.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class CommandModule
{
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ConnectorService connectorService;
    @Autowired
    CoordinateService coordinateService;

    public String apiExecute(String[] args)
    {
        String order = args[0];
        logger.info("获取接口命令:{}",order);
        switch (order)
        {
            //todo 命令设计:
            case "connector":
                return connectorService.commandExecute(args[1],"");
            case "coordinate":
                return coordinateService.commandExecute(args[1], "");
        }
        logger.error("{} command was not found!");
        return order+"command was not founded!";

    }
}
