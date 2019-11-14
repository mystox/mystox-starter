package com.kongtrolink.framework.gateway.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/10/18, 19:07.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
//@PropertySource(value = "classpath:gateway-transverterRelation.yml",factory = YamlPropertySourceFactory.class)
@ConfigurationProperties
@RefreshScope
public class TransverterConfig {
    private Map<String, List<String>> transverter;


    public Map<String, List<String>> getTransverter() {
        return transverter;
    }

    public void setTransverter(Map<String, List<String>> transverter) {
        this.transverter = transverter;
    }
}
