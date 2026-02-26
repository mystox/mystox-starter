package tech.mystox.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaRegister;

@Component
public class ApplicationCloseEventListener implements ApplicationListener<ContextClosedEvent> {

    Logger logger = LoggerFactory.getLogger(ApplicationCloseEventListener.class);

    final IaContext iaContext;
    final IaConf iaConf;
    //final ThreadPoolTaskExecutor mqttExecutor;
    //final ThreadPoolTaskExecutor mqttSenderAckExecutor;

    public ApplicationCloseEventListener(IaContext iaContext, IaConf iaConf
                                         //@Qualifier("mqttExecutor") ThreadPoolTaskExecutor mqttExecutor,
                                         //@Qualifier("mqttSenderAckExecutor") ThreadPoolTaskExecutor mqttSenderAckExecutor
    ) {
        this.iaContext = iaContext;
        this.iaConf = iaConf;
        //this.mqttExecutor = mqttExecutor;
        //this.mqttSenderAckExecutor = mqttSenderAckExecutor;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("Server close to unregister msg ability....[{}]", event);
        //iaContext.getIaENV().setServerStatus(ServerStatus.UNREGISTER);
        //检测接收线程池
        //String onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
        //        preconditionGroupServerCode(iaConf.getGroupCode(),
        //                preconditionServerCode(iaConf.getServerName(), iaConf.getServerVersion(), iaConf.getSequence())));
        //RegScheduler regScheduler = iaContext.getIaENV().getRegScheduler();
        //if (regScheduler == null) {
        //    logger.warn("Register scheduler is null...");
        //    throw new RegisterException("Register scheduler is null...");
        //}
        //regScheduler.deleteNode(onlineStatus);//关闭服务注册发现
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
        IaRegister iaRegister = iaContext.getIaRegister();
        if (iaRegister != null) {
            iaRegister.unregister();
        }
        //iaContext.getIaENV().setServerStatus(ServerStatus.OFFLINE);
    }
}
