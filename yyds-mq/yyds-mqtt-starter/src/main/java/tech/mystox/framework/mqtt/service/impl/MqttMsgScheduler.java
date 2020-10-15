package tech.mystox.framework.mqtt.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.service.MsgHandler;

import java.util.List;

import static tech.mystox.framework.common.util.MqttUtils.*;


@Component("mqttMsgScheduler")
@Lazy
public class MqttMsgScheduler implements ApplicationContextAware, MsgScheduler {

    // @Autowired
    // @Qualifier("MqttHandler")
    MsgHandler iaHandler;

    private ApplicationContext applicationContext;
    private IaConf iaconf;
    private IaENV iaENV;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    private Logger logger = LoggerFactory.getLogger(MqttMsgScheduler.class);


    public MqttMsgScheduler() {

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
        this.iaHandler = new MqttHandler(iaENV, applicationContext);
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
    }

    @Override
    public void initCaller(OperaCall caller) {

    }

    @Override
    public MsgHandler getIaHandler() {
        return this.iaHandler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
