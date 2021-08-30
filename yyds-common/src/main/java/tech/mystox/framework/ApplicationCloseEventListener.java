package tech.mystox.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.ServerStatus;
import tech.mystox.framework.entity.TopicPrefix;

import static tech.mystox.framework.common.util.MqttUtils.*;

@Component
public class ApplicationCloseEventListener implements ApplicationListener<ContextClosedEvent> {

    Logger logger = LoggerFactory.getLogger(ApplicationCloseEventListener.class);

    final IaContext iaContext;
    final IaConf iaConf;
    final ThreadPoolTaskExecutor mqttExecutor;
    final ThreadPoolTaskExecutor mqttSenderAckExecutor;

    public ApplicationCloseEventListener(IaContext iaContext, IaConf iaConf,
                                         @Qualifier("mqttExecutor") ThreadPoolTaskExecutor mqttExecutor,
                                         @Qualifier("mqttSenderAckExecutor") ThreadPoolTaskExecutor mqttSenderAckExecutor) {
        this.iaContext = iaContext;
        this.iaConf = iaConf;
        this.mqttExecutor = mqttExecutor;
        this.mqttSenderAckExecutor = mqttSenderAckExecutor;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("server close to unregister msg ability....");
        iaContext.getIaENV().setServerStatus(ServerStatus.UNREGISTER);
        //检测接收线程池
        String onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(iaConf.getGroupCode(),
                        preconditionServerCode(iaConf.getServerName(), iaConf.getServerVersion(), iaConf.getSequence())));
        iaContext.getIaENV().getRegScheduler().deleteNode(onlineStatus);//关闭服务注册发现
        do {
            try {
                logger.warn("mqttExecutor active count [{}], mqttSenderAckExecutor active count [{}]",
                        mqttExecutor.getActiveCount(), mqttSenderAckExecutor.getActiveCount());
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (mqttExecutor.getActiveCount() > 0 && mqttSenderAckExecutor.getActiveCount() > 0);
        iaContext.getIaRegister().unregister();
        iaContext.getIaENV().setServerStatus(ServerStatus.OFFLINE);
    }
}
