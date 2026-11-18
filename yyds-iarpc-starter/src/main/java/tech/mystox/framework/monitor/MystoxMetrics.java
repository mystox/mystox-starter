package tech.mystox.framework.monitor;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.MsgRsp;
import tech.mystox.framework.mqtt.service.MessageBusOperator;
import tech.mystox.framework.mqtt.service.impl.CallSubpackageMsg;
import tech.mystox.framework.mqtt.service.impl.MqttHandler;
import tech.mystox.framework.mqtt.rpc.RabbitRpcHandler;

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
        var msgHandler = this.iaContext.getIaENV().getMsgScheduler().getIaHandler();
        if (msgHandler instanceof RabbitRpcHandler rpc) {
            Gauge.builder("mystox.rpc.inflight", rpc, RabbitRpcHandler::getPendingCount).register(registry);
        } else {
            MqttHandler handler = (MqttHandler) msgHandler;
            MessageBusOperator messageBusOperator = handler.getMessageBusOperator();
            Map<String, CallSubpackageMsg<MsgRsp>> callbacks = messageBusOperator.getCALLBACKS();
            Gauge.builder("mystox.rpc.inflight", callbacks, Map::size).register(registry);
        }

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
