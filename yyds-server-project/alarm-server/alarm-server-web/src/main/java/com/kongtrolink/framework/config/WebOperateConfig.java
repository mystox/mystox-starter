package com.kongtrolink.framework.config;

import com.kongtrolink.framework.base.OperateConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: liudd
 * @Date: 2019/11/1 10:42
 * @Description: web端第三方接口配置文件实体类
 */
@Configuration
@ConfigurationProperties("webOperate")
@RefreshScope
public class WebOperateConfig extends OperateConfig{

}
