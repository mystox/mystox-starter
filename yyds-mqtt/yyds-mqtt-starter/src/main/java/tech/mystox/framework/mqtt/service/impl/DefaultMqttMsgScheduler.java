package tech.mystox.framework.mqtt.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.service.MsgHandler;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

import static tech.mystox.framework.common.util.MqttUtils.*;


//@Component("mqttMsgScheduler")
//@Lazy
public class DefaultMqttMsgScheduler implements MsgScheduler {

    private final IaContext iaContext;
    // @Autowired
    // @Qualifier("MqttHandler")
    DefaultMqttHandler iaHandler;
    private ApplicationContext applicationContext;
    private IaConf iaconf;
    private IaENV iaENV;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    private Logger logger = LoggerFactory.getLogger(DefaultMqttMsgScheduler.class);


    public DefaultMqttMsgScheduler(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    public DefaultMqttMsgScheduler(IaContext iaContext, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.iaContext = iaContext;
    }

    /**
     * @return void
     * @Date 0:22 2020/1/6
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 订阅统一AckTopic
     **/

    @Override
    public void build(IaENV iaENV) {
        this.iaENV = iaENV;
        this.iaconf = iaENV.getConf();
        this.groupCode = iaconf.getGroupCode();
        this.serverName = iaconf.getServerName();
        this.serverVersion = iaconf.getServerVersion();
        initMqttProperties();
        this.iaHandler = new DefaultMqttHandler(iaContext);
        try {
            this.iaHandler.getExecutorRunner().run(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //this.iaHandler = new MqttHandler(iaENV, applicationContext);
    }

    private void initMqttProperties() {
        //mqMsgProperties = new Properties();
        //从classpath路径下面查找文件
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        //加载成PropertySource对象，并添加到Environment环境中
        Resource resource = resourceLoader.getResource("classpath:mqtt.yml");
        EncodedResource encodedResource = new EncodedResource(resource);
        try {
            Properties properties = loadYamlIntoProperties(encodedResource);
            if (applicationContext != null) {
                Environment environment = applicationContext.getEnvironment();
                properties.putIfAbsent("mqtt.url", environment.getProperty("mqtt.url"));
                properties.putIfAbsent("mqtt.username", environment.getProperty("mqtt.username"));
                properties.putIfAbsent("mqtt.password", environment.getProperty("mqtt.password"));
                properties.putIfAbsent("mqtt.maxInflight", environment.getProperty("mqtt.maxInflight","100"));
            }
            iaconf.setMqMsgProperties(properties);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

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

    @Override
    public void unregister() {
        removerSubTopic(this.iaENV.getRegScheduler().getSubList());
    }

    private void ackTopic() {
        String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())));
        if (!iaHandler.isAckExists(ackTopicId))
            iaHandler.addAckTopic(ackTopicId, 2);
    }

    /**
     * @return void
     * @Date 0:22 2020/1/6
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 订阅订阅表信息
     **/
    public void subTopic(List<RegisterSub> subList) {
        subList.forEach(sub -> {
            String operaCode = sub.getOperaCode();
            String topicId = MqttUtils.preconditionSubTopicId(
                    preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())), operaCode);
            if (iaHandler != null) {
                if (!iaHandler.isExists(topicId))
                    //logger.info("订阅了:{} ",topicId);
                    iaHandler.addSubTopic(topicId, 2);
            }
        });
        ackTopic();
    }

    @Override
    public void removerSubTopic(List<RegisterSub> subList) {
        try {
            subList.forEach(sub -> {
                String operaCode = sub.getOperaCode();
                String topicId = MqttUtils.preconditionSubTopicId(
                        preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())), operaCode);
                if (iaHandler != null) {
                    if (iaHandler.isExists(topicId))
                        //logger.info("订阅了:{} ",topicId);
                        iaHandler.removeSubTopic(topicId);
                }
            });
            String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())));
            if (iaHandler.isAckExists(ackTopicId))
                iaHandler.removeAckSubTopic(ackTopicId);
        } catch (Exception e) {
            logger.error("remove sub topic list error...", e);
            if (logger.isDebugEnabled()) e.printStackTrace();
        }
    }

    @Override
    public void initCaller(OperaCall caller) {

    }

    @Override
    public MsgHandler getIaHandler() {
        return this.iaHandler;
    }

}
