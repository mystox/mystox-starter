package tech.mystox.framework.monitor;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.MsgRsp;
import tech.mystox.framework.mqtt.service.impl.CallSubpackageMsg;
import tech.mystox.framework.mqtt.service.impl.ChannelSenderImpl;

import java.util.Map;

@Component
public class MystoxMetrics {


   private final IaContext iaContext;
    private final MeterRegistry registry;

    public MystoxMetrics(IaContext iaContext, MeterRegistry registry) {
        this.iaContext = iaContext;
        this.registry = registry;
    }

    public void register() {
        ChannelSenderImpl mqttSender = (ChannelSenderImpl) this.iaContext.getIaENV().getMsgScheduler();
        Map<String, CallSubpackageMsg<MsgRsp>> callbacks = mqttSender.getCALLBACKS();
        Gauge.builder("mystox.rpc.inflight",
                        callbacks,
                        Map::size)
                .register(registry);

        //Gauge.builder("mystox.rpc.limiter.available",
        //        limiter,
        //        Semaphore::availablePermits)
        //     .register(registry);

        Gauge.builder("mystox.thread.virtual.count",
                this,
                MystoxMetrics::virtualThreadCount)
             .register(registry);
    }

    private long virtualThreadCount() {
        return Thread.getAllStackTraces()
                .keySet()
                .stream()
                .filter(Thread::isVirtual)
                .count();
    }
}