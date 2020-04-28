package com.kongtrolink.framework.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/8/14, 16:47.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class SpringContextUtil implements ApplicationContextAware{

    private static Map<String, String> serviceMap;
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        serviceMap = new HashMap<>();
        this.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     * @param name
     * @return
     */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    public static Map<String, String> getServiceMap() {
        return serviceMap;
    }

    public static void setServiceMap(Map<String, String> serviceMap) {
        SpringContextUtil.serviceMap = serviceMap;
    }
}
