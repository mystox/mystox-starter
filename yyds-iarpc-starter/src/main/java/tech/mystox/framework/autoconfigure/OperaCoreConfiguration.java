package tech.mystox.framework.autoconfigure;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.config.autoconfigure.*;
import tech.mystox.framework.core.BeanProvider;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.service.IaOpera;
import tech.mystox.framework.service.Impl.IaOperaImpl;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        ServerProperties.class,
        RegisterProperties.class,
        OperaRouteProperties.class,
        WebPrivFuncConfig.class,
        MqttProperties.class,
})
public class OperaCoreConfiguration {
    @Bean
    public IaConf iaConf(
            ServerProperties server,
            RegisterProperties register,
            WebPrivFuncConfig webPrivFuncConfig,
            OperaRouteProperties operaRouteProperties,
            ApplicationContext applicationContext) {
        IaConf iaConf = new IaConf(register, server, operaRouteProperties, webPrivFuncConfig);
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        //加载成PropertySource对象，并添加到Environment环境中
        Resource resource = resourceLoader.getResource("classpath:mqtt.yml");
        EncodedResource encodedResource = new EncodedResource(resource);
        try {
            Properties properties = loadYamlIntoProperties(encodedResource);
            if (applicationContext != null) {
                Environment environment = applicationContext.getEnvironment();
                properties.putIfAbsent("mqtt.url", environment.getProperty("mqtt.url",""));
                properties.putIfAbsent("mqtt.username", environment.getProperty("mqtt.username",""));
                properties.putIfAbsent("mqtt.password", environment.getProperty("mqtt.password",""));
                properties.putIfAbsent("mqtt.maxInflight", environment.getProperty("mqtt.maxInflight","100"));
                properties.putIfAbsent("rabbitmq.host", getRabbitProperty(environment, "spring.rabbitmq.host", "rabbitmq.host", "127.0.0.1"));
                properties.putIfAbsent("rabbitmq.port", getRabbitProperty(environment, "spring.rabbitmq.port", "rabbitmq.port", "5672"));
                properties.putIfAbsent("rabbitmq.username", getRabbitProperty(environment, "spring.rabbitmq.username", "rabbitmq.username", "guest"));
                properties.putIfAbsent("rabbitmq.password", getRabbitProperty(environment, "spring.rabbitmq.password", "rabbitmq.password", "guest"));
                properties.putIfAbsent("rabbitmq.virtualHost", getRabbitProperty(environment, "spring.rabbitmq.virtual-host", "rabbitmq.virtualHost", "/"));
                properties.putIfAbsent("rabbitmq.exchange", environment.getProperty("rabbitmq.exchange", "yyds.rpc"));
                properties.putIfAbsent("rabbitmq.queuePrefix", environment.getProperty("rabbitmq.queuePrefix", "yyds"));
                properties.putIfAbsent("rabbitmq.prefetch", environment.getProperty("rabbitmq.prefetch", "50"));
                properties.putIfAbsent("rabbitmq.queueExpires", environment.getProperty("rabbitmq.queueExpires", "600000"));
                properties.putIfAbsent("spring.rabbitmq.host", getRabbitProperty(environment, "spring.rabbitmq.host", "rabbitmq.host", "127.0.0.1"));
                properties.putIfAbsent("spring.rabbitmq.port", getRabbitProperty(environment, "spring.rabbitmq.port", "rabbitmq.port", "5672"));
                properties.putIfAbsent("spring.rabbitmq.username", getRabbitProperty(environment, "spring.rabbitmq.username", "rabbitmq.username", "guest"));
                properties.putIfAbsent("spring.rabbitmq.password", getRabbitProperty(environment, "spring.rabbitmq.password", "rabbitmq.password", "guest"));
                properties.putIfAbsent("spring.rabbitmq.virtual-host", getRabbitProperty(environment, "spring.rabbitmq.virtual-host", "rabbitmq.virtualHost", "/"));
            }
            iaConf.setMqMsgProperties(properties);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return iaConf;
    }
    private void initMqttProperties() {
        //mqMsgProperties = new Properties();
        //从classpath路径下面查找文件


    }

    private String getRabbitProperty(Environment environment, String springKey, String frameworkKey, String defaultValue) {
        return environment.getProperty(springKey, environment.getProperty(frameworkKey, defaultValue));
    }

    private Properties loadYamlIntoProperties(EncodedResource resource) throws FileNotFoundException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return factory.getObject();
        } catch (IllegalStateException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FileNotFoundException)
                throw (FileNotFoundException) e.getCause();
            throw e;
        }
    }
    @Bean
    public IaENV iaENV(BeanProvider beanProvider, ApplicationContext context) {
        List<String> basePackages = AutoConfigurationPackages.get(context);
        return new IaENV(basePackages, beanProvider);
    }

    @Bean
    public IaContext iaContext(IaConf iaConf, IaENV iaENV) {

        return new IaContext(iaConf, iaENV);
    }

    @Bean
    public IaOpera iaOpera(IaContext iaContext) {
        return new IaOperaImpl(iaContext);
    }

    @Bean
    public BeanProvider beanProvider(ApplicationContext context) {
        return context::getBean;
    }

    @Bean
    public IaApplicationRunner iaApplicationRunner(IaContext iaContext) {
        return new IaApplicationRunner(iaContext);
    }

    //@Bean
    //public OperaInvoker operaInvoker(
    //        IaContext iaContext,
    //        List<OperaFilter> filters) {
    //
    //    OperaInvoker target = new OperaSyncInterceptor(iaContext);
    //
    //    return new OperaFilterChain(filters, target);
    //}
}
