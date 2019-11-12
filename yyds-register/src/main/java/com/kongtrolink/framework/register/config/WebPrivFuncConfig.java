package com.kongtrolink.framework.register.config;

import com.kongtrolink.framework.register.entity.PrivFuncEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/11/6, 20:27.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
//@PropertySource(value = "privFuncConfig.yml",
//        ignoreResourceNotFound = true,factory = YamlPropertySourceFactory.class)
@ConfigurationProperties/*(prefix = ServerName.LOG_SERVER)*/
@RefreshScope
public class WebPrivFuncConfig {

    private PrivFuncEntity privFunc;

    public PrivFuncEntity getPrivFunc() {
        return privFunc;
    }
    public void setPrivFunc(PrivFuncEntity privFunc) {
        this.privFunc = privFunc;
    }

}
