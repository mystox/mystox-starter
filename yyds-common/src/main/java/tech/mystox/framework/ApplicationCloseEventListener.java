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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        logger.debug("Server close to unregister msg ability....[{}]",event);
        iaContext.getIaENV().setServerStatus(ServerStatus.UNREGISTER);
        //检测接收线程池
        String onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(iaConf.getGroupCode(),
                        preconditionServerCode(iaConf.getServerName(), iaConf.getServerVersion(), iaConf.getSequence())));
        iaContext.getIaENV().getRegScheduler().deleteNode(onlineStatus);//关闭服务注册发现
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if (mqttExecutor.getActiveCount() == 0 && mqttSenderAckExecutor.getActiveCount() == 0)
                executorService.shutdown();
            else
                logger.warn("MqttExecutor active count [{}], MqttSenderAckExecutor active count [{}]",
                        mqttExecutor.getActiveCount(), mqttSenderAckExecutor.getActiveCount());
        }, 10, 500, TimeUnit.MILLISECONDS);
        try {
            if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.info("Server closed successfully!!");
            }
        } catch (InterruptedException e) {
            logger.error("MqttExecutor active count [{}], MqttSenderAckExecutor active count [{}]",
                    mqttExecutor.getActiveCount(), mqttSenderAckExecutor.getActiveCount());
        }
        iaContext.getIaRegister().unregister();
        iaContext.getIaENV().setServerStatus(ServerStatus.OFFLINE);
    }
}
