package com.kongtrolink.framework.node.controller.coordinate.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mystoxlol on 2019/2/20, 13:25.
 * company: kongtrolink
 * description: 可插拔插件类
 * update record:
 */
public class PluginLoader
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String pluginUrl;
    private String className;
    private String methodName;

    public PluginLoader(String pluginUrl, String className, String methodName)
        {
        this.pluginUrl = pluginUrl;
        this.className = className;
        this.methodName = methodName;
    }


    Object executeByJar(Object param)
    {
        logger.warn("负载算法插件 （todo.todo.1）");
        //TODO 插件jar执行器
        return null;
    }
}
