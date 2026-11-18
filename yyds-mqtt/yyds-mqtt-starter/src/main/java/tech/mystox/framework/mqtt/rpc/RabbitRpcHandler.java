package tech.mystox.framework.mqtt.rpc;

import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.*;
import tech.mystox.framework.service.MsgHandler;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class RabbitRpcHandler implements MsgHandler, AutoCloseable {
    private final IaContext context;
    private final RabbitRpcTransport transport;
    public RabbitRpcHandler(IaContext context) { this.context = context; transport = new RabbitRpcTransport(context); }
    void start(List<RegisterSub> subscriptions) {
        transport.start(subscriptions, new LocalRpcDispatcher(subscriptions, context.getIaENV().getBeanProvider()));
    }
    @Override public MsgResult opera(String code, String msg) {
        return invoke(context.getConf().getGroupCode(), code, msg, defaultTimeout(), true);
    }
    @Override public MsgResult operaGroupCode(String group, String code, String msg) {
        return invoke(group, code, msg, defaultTimeout(), true);
    }
    @Override public MsgResult opera(String code, String msg, int qos, long timeout, TimeUnit unit) {
        return invoke(context.getConf().getGroupCode(), code, msg, timeout(timeout, unit), true);
    }
    @Override public MsgResult opera(OperaContext call) {
        String group = call.getGroupCode() == null || call.getGroupCode().isBlank() ? context.getConf().getGroupCode() : call.getGroupCode();
        return invoke(group, call.getOperaCode(), call.getMsg(), call.isSetFlag() ? timeout(call.getTimeout(), call.getTimeUnit()) : defaultTimeout(), !call.isAsync());
    }
    @Override public void operaAsync(String code, String msg) {
        MsgResult result = invoke(context.getConf().getGroupCode(), code, msg, defaultTimeout(), false);
        if (result.getStateCode() != StateCode.StateCodeEnum.SUCCESS.getCode()) throw new IllegalStateException(result.toString());
    }
    private MsgResult invoke(String group, String code, String msg, long timeout, boolean reply) {
        return transport.call(group, code, msg, timeout, reply);
    }
    private long defaultTimeout() { return context.getConf().getRabbitRpcProperties().getRequestTimeout(); }
    private long timeout(long value, TimeUnit unit) {
        if (value == 0) return defaultTimeout();
        if (value < 0 || unit == null || unit.toMillis(value) <= 0) throw new IllegalArgumentException("RPC timeout must be >= 1 millisecond");
        return unit.toMillis(value);
    }
    public int getPendingCount() { return transport.pendingCount(); }
    @Override public void close() { transport.close(); }
    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("register.type=none supports opera, operaGroup and operaAsync; registry, target-instance, broadcast and dynamic subscriptions are not supported");
    }
    @Override public void broadcast(String code, String msg) { throw unsupported(); }
    @Override public RegisterMsg getRegisterMsg() { throw unsupported(); }
    @Override public void addSubTopic(String topic, int qos) { throw unsupported(); }
    @Override public void addAckTopic(String topic, int qos) { throw unsupported(); }
    @Override public boolean isAckExists(String topic) { throw unsupported(); }
    @Override public boolean isExists(String topic) { throw unsupported(); }
    @Override public void removeSubTopic(String... topics) { throw unsupported(); }
    @Override public void removeAckSubTopic(String... topics) { throw unsupported(); }
    @Override public void sendToMqtt(String server, String code, String payload) { throw unsupported(); }
    @Override public void sendToMqtt(String server, String code, int qos, String payload) { throw unsupported(); }
    @Override public boolean sendToMqttBoolean(String server, String code, int qos, String payload) { throw unsupported(); }
    @Override public MsgResult sendToMqttSync(String server, String code, String payload) { throw unsupported(); }
    @Override public MsgResult sendToMqttSync(String server, String code, int qos, String payload, long timeout, TimeUnit unit) { throw unsupported(); }
}
