package tech.mystox.demo.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by mystoxlol on 2019/11/11, 13:57.
 * company: mystox
 * description:
 * update record:
 */
public class RegisterEnvironmentPostProcessor implements EnvironmentPostProcessor {

    Logger logger = LoggerFactory.getLogger(RegisterEnvironmentPostProcessor.class);

    protected boolean ignoreFileNotFound = true;
    //Properties对象
    protected final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    String[] profiles = {
            "classpath:config/privFuncConfig.yml",
            "file:config/privFuncConfig.yml",
            "classpath:config/operaRoute.yml",
            "file:config/operaRoute.yml",
            "classpath:config/operaRoute-test.yml",
            "file:config/operaRoute-test.yml",
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        loadResources(environment, application,this.profiles);
        if (logger.isInfoEnabled())
            logger.info("load register environment post processor success...");
        else
            System.out.println("load register environment post processor success...");
    }

    protected void loadResources(ConfigurableEnvironment environment, SpringApplication application,String[] profiles) {
        //循环添加
        for (String profile : profiles) {
            //从classpath路径下面查找文件
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            //加载成PropertySource对象，并添加到Environment环境中
            Resource resource = resourceLoader.getResource(profile);
            if (!resource.exists()) {
                if (ignoreFileNotFound) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("ignore file not exists..{}", profile);
                    } else {
//                        System.out.println("ignore file not exists..{}" + profile);
                    }
                    continue;
                }
                throw new IllegalArgumentException("resource " + resource + " in not exists");
            }
            try {
                String s = "";
                InputStream inputStream = null;
                try {
                    inputStream = resource.getInputStream();
                    if (inputStream != null) {
                        s = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
                        if (StringUtils.isNotBlank(s)) {
                            environment.getPropertySources().addLast(loadProfiles(resource).get(0));
                            continue;
                        }
                    }
                } finally {
                    if (inputStream != null)
                        inputStream.close();
                }
                if (logger.isWarnEnabled()) logger.warn("file is blank {}", resource.getFilename());
                else System.out.println("file is blank " + resource.getFilename());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    //加载单个配置文件
    private List<PropertySource<?>> loadProfiles(Resource resource) {
        try {
            //从输入流中加载一个Properties对象
            String filename = resource.getFilename();
            return this.loader.load(filename, resource);
        } catch (IOException ex) {
            throw new IllegalStateException("load Yaml Property Source error" + resource, ex);
        }
    }

}
