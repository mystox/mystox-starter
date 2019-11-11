package com.kongtrolink.framework.register.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * Created by mystoxlol on 2019/11/11, 13:57.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration
public class MyEnvironmentPostProcessor implements EnvironmentPostProcessor {

    Logger logger = LoggerFactory.getLogger(MyEnvironmentPostProcessor.class);

    private boolean ignoreFileNotFound = true;
    //Properties对象
    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//自定义配置文件
        String[] profiles = {
                "classpath:config/privFuncConfig.yml",
                "file:config/privFuncConfig.yml"/*,
                "yyy.properties",
                "zzz.yml",*/
        };
        //循环添加
        for (String profile : profiles) {
            //从classpath路径下面查找文件
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(profile);
            //加载成PropertySource对象，并添加到Environment环境中
            if (!resource.exists()) {
                if (ignoreFileNotFound) {
                    logger.warn("ignore file not exists..{}", profile);
                    continue;
                }
                throw new IllegalArgumentException("resource " + resource + " in not exists");
            }
            environment.getPropertySources().addLast(loadProfiles(resource));
        }
    }

    //加载单个配置文件
    private PropertySource<?> loadProfiles(Resource resource) {
        try {
            //从输入流中加载一个Properties对象
            String filename = resource.getFilename();
            return this.loader.load(filename, resource, null);
        } catch (IOException ex) {
            throw new IllegalStateException("load Yaml Property Source error" + resource, ex);
        }
    }
}
