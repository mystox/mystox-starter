package com.kongtrolink.framework.node.controller.coordinate.module;

/**
 * Created by mystoxlol on 2019/2/20, 13:25.
 * company: kongtrolink
 * description: 可插拔插件类
 * update record:
 */
public class PluginLoader
{
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
        //TODO 插件jar执行器
        return null;
    }
}
