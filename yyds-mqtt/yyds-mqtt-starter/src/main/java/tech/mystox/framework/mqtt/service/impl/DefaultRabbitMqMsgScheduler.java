package tech.mystox.framework.mqtt.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.service.MsgHandler;

import java.util.List;

import static tech.mystox.framework.common.util.MqttUtils.preconditionGroupServerCode;
import static tech.mystox.framework.common.util.MqttUtils.preconditionServerCode;
import static tech.mystox.framework.common.util.MqttUtils.preconditionSubACKTopicId;

public class DefaultRabbitMqMsgScheduler implements MsgScheduler {
    private final IaContext iaContext;
    private DefaultRabbitMqHandler iaHandler;
    private IaConf iaconf;
    private IaENV iaENV;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    private final Logger logger = LoggerFactory.getLogger(DefaultRabbitMqMsgScheduler.class);

    public DefaultRabbitMqMsgScheduler(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    @Override
    public void build(IaENV iaENV) {
        this.iaENV = iaENV;
        this.iaconf = iaENV.getConf();
        this.groupCode = iaconf.getGroupCode();
        this.serverName = iaconf.getServerName();
        this.serverVersion = iaconf.getServerVersion();
        this.iaHandler = new DefaultRabbitMqHandler(iaContext);
        try {
            this.iaHandler.getExecutorRunner().run(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister() {
        removerSubTopic(this.iaENV.getRegScheduler().getSubList());
        this.iaHandler.stop();
    }

    private void ackTopic() {
        String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())));
        if (!iaHandler.isAckExists(ackTopicId)) {
            iaHandler.addAckTopic(ackTopicId, 2);
        }
    }

    @Override
    public void subTopic(List<RegisterSub> subList) {
        subList.forEach(sub -> {
            String operaCode = sub.getOperaCode();
            String topicId = MqttUtils.preconditionSubTopicId(
                    preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())), operaCode);
            if (iaHandler != null && !iaHandler.isExists(topicId)) {
                iaHandler.addSubTopic(topicId, 2);
            }
        });
        ackTopic();
        iaHandler.startConsumers();
    }

    @Override
    public void removerSubTopic(List<RegisterSub> subList) {
        try {
            subList.forEach(sub -> {
                String operaCode = sub.getOperaCode();
                String topicId = MqttUtils.preconditionSubTopicId(
                        preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())), operaCode);
                if (iaHandler != null && iaHandler.isExists(topicId)) {
                    iaHandler.removeSubTopic(topicId);
                }
            });
            String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())));
            if (iaHandler.isAckExists(ackTopicId)) {
                iaHandler.removeAckSubTopic(ackTopicId);
            }
        } catch (Exception e) {
            logger.error("remove rabbitmq sub topic list error...", e);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public MsgHandler getIaHandler() {
        return this.iaHandler;
    }
}
