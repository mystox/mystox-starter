package com.kongtrolink.framework.config;

import com.kongtrolink.framework.base.OperateConfig;
import com.kongtrolink.framework.mqtt.OperateEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/10/29 16:54
 * @Description:
 */
@Configuration
@ConfigurationProperties("reportOperate")
@RefreshScope
public class ReportOperateConfig extends OperateConfig{

}
