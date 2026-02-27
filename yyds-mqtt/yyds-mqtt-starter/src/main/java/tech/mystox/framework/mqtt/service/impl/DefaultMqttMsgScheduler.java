package tech.mystox.framework.mqtt.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.mqtt.service.ExecutorRunner;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.service.MsgHandler;

import java.util.List;

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
    private final Logger logger = LoggerFactory.getLogger(DefaultMqttMsgScheduler.class);


    public DefaultMqttMsgScheduler(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    public DefaultMqttMsgScheduler(IaContext iaContext, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.iaContext = iaContext;
    }

    /**
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
        this.iaHandler = new DefaultMqttHandler(iaContext);
        try {
            this.iaHandler.getExecutorRunner().run(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //this.iaHandler = new MqttHandler(iaENV, applicationContext);
    }




    @Override
    public void unregister() {
        ExecutorRunner executorRunner = this.iaHandler.getExecutorRunner();
        //Map<String, CallSubpackageMsg<MsgRsp>> callbacks = executorRunner.getMqttSender().getCALLBACKS();

        //ThreadPoolTaskExecutor mqttExecutor = executorRunner.getMqttExecutor();
        //ThreadPoolTaskExecutor mqttSenderAckExecutor = executorRunner.getMqttSenderAckExecutor();
        //ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(() -> {
        //    if (mqttExecutor.getActiveCount() == 0 && mqttSenderAckExecutor.getActiveCount() == 0)
        //        executorService.shutdown();
        //    else
        //        logger.warn("MqttExecutor active count [{}], MqttSenderAckExecutor active count [{}]",
        //                mqttExecutor.getActiveCount(), mqttSenderAckExecutor.getActiveCount());
        //}, 10, 500, TimeUnit.MILLISECONDS);
        //try {
        //    if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
        //        logger.info("Server closed successfully!!");
        //    }
        //} catch (InterruptedException e) {
        //    logger.error("MqttExecutor active count [{}], MqttSenderAckExecutor active count [{}]",
        //            mqttExecutor.getActiveCount(), mqttSenderAckExecutor.getActiveCount());
        //}
        removerSubTopic(this.iaENV.getRegScheduler().getSubList());
        this.iaHandler.stop();
    }

    private void ackTopic() {
        String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaconf.getSequence())));
        if (!iaHandler.isAckExists(ackTopicId))
            iaHandler.addAckTopic(ackTopicId, 2);
    }

    /**
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
