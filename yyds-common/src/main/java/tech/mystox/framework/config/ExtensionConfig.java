package tech.mystox.framework.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by mystox on 2021/4/16, 16:20.
 * company:
 * description: 注册一些扩展字段，扩展字段以key-v的形式在yaml中的register.extension字段下注册
 * update record:
 */
@Component
//@PropertySource(value = "privFuncConfig.yml",
//        ignoreResourceNotFound = true,factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "register")/*(prefix = ServerName.LOG_SERVER)*/
@RefreshScope
public class ExtensionConfig
{
    private Map<String, Object> extension;

    public Map<String, Object> getExtension()
    {
        return extension;
    }

    public void setExtension(Map<String, Object> extension)
    {
        this.extension = extension;
    }

}
