package com.kongtrolink.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/12 8:55
 * \* Description: 获取配置文件
 * \
 */
@Component
@ConfigurationProperties(prefix = "login")
//@PropertySource("classpath:/config/user-table.yml")
public class LoginConfig {


    Map<String,String> users;

    public Map<String, String> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }
}