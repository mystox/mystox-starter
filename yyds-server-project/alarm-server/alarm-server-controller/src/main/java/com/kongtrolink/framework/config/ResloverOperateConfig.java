package com.kongtrolink.framework.config;

import com.kongtrolink.framework.base.OperateConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: liudd
 * @Date: 2019/10/31 19:11
 * @Description:告警消除操作
 */
@Configuration
@ConfigurationProperties("resloveOperate")
@RefreshScope
public class ResloverOperateConfig extends OperateConfig{

}
