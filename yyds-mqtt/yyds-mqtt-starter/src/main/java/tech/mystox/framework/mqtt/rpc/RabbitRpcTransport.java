package tech.mystox.framework.mqtt.rpc;

import com.alibaba.fastjson2.JSON;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.config.autoconfigure.RabbitRpcProperties;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.MsgRsp;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.StateCode.StateCodeEnum;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** AMQP-native request/reply, independent of registry paths and MQTT topics. */
final class RabbitRpcTransport implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(RabbitRpcTransport.class);
    private final IaConf conf;
    private final RabbitRpcProperties settings;
    private final CachingConnectionFactory connectionFactory;
    private final RabbitAdmin admin;
    private final RabbitTemplate template;
    private final Queue replies;
    private final Semaphore pendingSlots;
    private final ConcurrentMap<String, CompletableFuture<MsgRsp>> pending = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean();
    private final AtomicBoolean started = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicLong connectionGeneration = new AtomicLong();
    private SimpleMessageListenerContainer requestConsumer;
    private SimpleMessageListenerContainer replyConsumer;
    private LocalRpcDispatcher dispatcher;

    RabbitRpcTransport(IaContext context) {
        conf = context.getConf();
        settings = conf.getRabbitRpcProperties();
        settings.validate();
        Properties p = conf.getMqMsgProperties() == null ? new Properties() : conf.getMqMsgProperties();
        connectionFactory = new CachingConnectionFactory(property(p, "spring.rabbitmq.host", "rabbitmq.host", "127.0.0.1"));
        connectionFactory.setPort(Integer.parseInt(property(p, "spring.rabbitmq.port", "rabbitmq.port", "5672")));
        connectionFactory.setUsername(property(p, "spring.rabbitmq.username", "rabbitmq.username", "guest"));
        connectionFactory.setPassword(property(p, "spring.rabbitmq.password", "rabbitmq.password", "guest"));
        connectionFactory.setVirtualHost(property(p, "spring.rabbitmq.virtual-host", "rabbitmq.virtualHost", "/"));
        connectionFactory.setConnectionTimeout(settings.getConfirmTimeout());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        connectionFactory.addConnectionListener(new ConnectionListener() {
            @Override public void onCreate(Connection connection) { }
            @Override public void onClose(Connection connection) {
                connectionGeneration.incrementAndGet();
                failPending("RabbitMQ connection closed; RPC outcome may be unknown");
            }
        });
        admin = new RabbitAdmin(connectionFactory);
        admin.setRedeclareManualDeclarations(true);
        admin.afterPropertiesSet();
        template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        template.setReturnsCallback(returned -> log.debug("Unroutable RPC publication: exchange={}, key={}", returned.getExchange(), returned.getRoutingKey()));
        replies = new Queue(settings.getQueuePrefix() + ".reply." + UUID.randomUUID(), false, true, true);
        pendingSlots = new Semaphore(settings.getMaxPending());
    }

    synchronized void start(List<RegisterSub> subscriptions, LocalRpcDispatcher localDispatcher) {
        if (closed.get() || !started.compareAndSet(false, true)) throw new IllegalStateException("Rabbit RPC already started or closed");
        dispatcher = localDispatcher;
        try {
            DirectExchange requests = new DirectExchange(settings.getExchange(), true, false);
            DirectExchange deadLetters = new DirectExchange(settings.getExchange() + ".dlx", true, false);
            Queue deadQueue = QueueBuilder.durable(settings.getQueuePrefix() + ".dead").withArgument("x-queue-type", settings.getQueueType()).build();
            admin.declareExchange(requests);
            admin.declareExchange(deadLetters);
            admin.declareQueue(deadQueue);
            admin.declareBinding(BindingBuilder.bind(deadQueue).to(deadLetters).with("failed"));
            admin.declareQueue(replies);
            replyConsumer = container(AcknowledgeMode.AUTO, 1);
            replyConsumer.setQueues(replies);
            replyConsumer.setMessageListener((MessageListener) this::receiveReply);
            replyConsumer.afterPropertiesSet();
            replyConsumer.start();
            if (replyConsumer.getActiveConsumerCount() != 1) throw new IllegalStateException("Rabbit RPC reply consumer did not start");
            if (!subscriptions.isEmpty()) {
                requestConsumer = container(AcknowledgeMode.MANUAL, settings.getConcurrency());
                for (RegisterSub sub : subscriptions) {
                    String key = RpcRouting.key(conf.getGroupCode(), settings.getContractVersion(), sub.getOperaCode());
                    Queue queue = QueueBuilder.durable(RpcRouting.queue(settings.getQueuePrefix(), key))
                            .withArgument("x-queue-type", settings.getQueueType())
                            .deadLetterExchange(deadLetters.getName()).deadLetterRoutingKey("failed").build();
                    admin.declareQueue(queue);
                    admin.declareBinding(BindingBuilder.bind(queue).to(requests).with(key));
                    requestConsumer.addQueues(queue);
                }
                requestConsumer.setMessageListener((ChannelAwareMessageListener) this::receiveRequest);
                requestConsumer.afterPropertiesSet();
                requestConsumer.start();
                if (requestConsumer.getActiveConsumerCount() != settings.getConcurrency()) throw new IllegalStateException("Rabbit RPC request consumers did not start");
            }
            running.set(true);
            log.info("Rabbit RPC without registry started: group={}, contract={}, operations={}, replyQueue={}",
                    conf.getGroupCode(), settings.getContractVersion(), subscriptions.size(), replies.getName());
        } catch (RuntimeException e) {
            close();
            throw e;
        }
    }

    MsgResult call(String group, String code, String payload, long timeoutMillis, boolean expectsReply) {
        if (!running.get()) return new MsgResult(StateCodeEnum.CONNECT_INTERRUPT, "Rabbit RPC is not running");
        if (!pendingSlots.tryAcquire()) return new MsgResult(StateCodeEnum.CALLBACK_FULL, "Rabbit RPC pending limit reached");
        String id = UUID.randomUUID().toString();
        CompletableFuture<MsgRsp> future = new CompletableFuture<>();
        long deadlineNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
        try {
            if (timeoutMillis <= 0) throw new IllegalArgumentException("RPC timeout must be positive");
            String key = RpcRouting.key(group, settings.getContractVersion(), code);
            RpcRouting.queue(settings.getQueuePrefix(), key);
            long deadline = Math.addExact(System.currentTimeMillis(), timeoutMillis);
            RpcRequest request = new RpcRequest(code, payload, conf.getGroupCode() + "/" + conf.getServerName() + "_" + conf.getServerVersion() + "/" + conf.getMyId(), deadline);
            MessageProperties properties = new MessageProperties();
            properties.setCorrelationId(id);
            properties.setMessageId(id);
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            properties.setExpiration(Long.toString(timeoutMillis));
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            if (expectsReply) { properties.setReplyTo(replies.getName()); pending.put(id, future); }
            if (!running.get()) return new MsgResult(StateCodeEnum.CONNECT_INTERRUPT, "Rabbit RPC stopped");
            Message message = new Message(JSON.toJSONBytes(request), properties);
            if (message.getBody().length > settings.getMaxPayloadBytes()) {
                return new MsgResult(StateCodeEnum.PACKAGE_ERROR, "RPC request exceeds max-payload-bytes; fragmentation is disabled");
            }
            publish(settings.getExchange(), key, message, remaining(deadlineNanos));
            if (!expectsReply) return new MsgResult(StateCodeEnum.SUCCESS, "Request accepted by RabbitMQ; business result is not awaited");
            MsgRsp response = future.get(remaining(deadlineNanos), TimeUnit.NANOSECONDS);
            return new MsgResult(response.getStateCode(), response.getPayload());
        } catch (UnroutableException e) {
            return new MsgResult(StateCodeEnum.OPERA_ROUTE_EXCEPTION, "No RPC queue is bound for this group, contract and operaCode");
        } catch (TimeoutException e) {
            return new MsgResult(StateCodeEnum.TIMEOUT, "RPC deadline/confirm timeout exceeded; business outcome may be unknown");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new MsgResult(StateCodeEnum.CONNECT_INTERRUPT, "RPC caller interrupted; business outcome may be unknown");
        } catch (ExecutionException e) {
            return new MsgResult(StateCodeEnum.CONNECT_INTERRUPT, e.getCause().toString());
        } catch (RuntimeException e) {
            log.warn("RPC send failed [{}]", id, e);
            return new MsgResult(StateCodeEnum.MESSAGE_EXCEPTION, "RPC publish failed; outcome may be unknown: " + e.getMessage());
        } finally {
            pending.remove(id);
            pendingSlots.release();
        }
    }

    private void receiveReply(Message message) {
        String id = message.getMessageProperties().getCorrelationId();
        CompletableFuture<MsgRsp> future = id == null ? null : pending.get(id);
        if (future == null) return;
        try {
            if (message.getBody().length > settings.getMaxPayloadBytes()) throw new IllegalArgumentException("Oversized RPC response");
            MsgRsp response = JSON.parseObject(message.getBody(), MsgRsp.class);
            if (response == null || response.getStateCode() == null || !id.equals(response.getMsgId())) throw new IllegalArgumentException("Invalid RPC response correlation");
            future.complete(response);
        } catch (RuntimeException e) {
            future.complete(LocalRpcDispatcher.response(id, StateCodeEnum.MESSAGE_EXCEPTION, "Invalid RPC response"));
        }
    }

    private void receiveRequest(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        long generation = connectionGeneration.get();
        try {
            MessageProperties incoming = message.getMessageProperties();
            String id = incoming.getCorrelationId();
            if (id == null || message.getBody().length > settings.getMaxPayloadBytes()) { channel.basicReject(tag, false); return; }
            RpcRequest request = JSON.parseObject(message.getBody(), RpcRequest.class);
            if (request == null || request.operaCode() == null || request.payload() == null
                    || request.sourceAddress() == null || request.deadline() <= 0
                    || !RpcRouting.key(conf.getGroupCode(), settings.getContractVersion(), request.operaCode()).equals(incoming.getReceivedRoutingKey())) {
                channel.basicReject(tag, false);
                return;
            }
            MsgRsp response = dispatcher.execute(id, request);
            if (!currentDelivery(channel, generation)) return;
            String replyTo = incoming.getReplyTo();
            if (replyTo != null && !replyTo.isBlank()) {
                MessageProperties properties = new MessageProperties();
                properties.setCorrelationId(id);
                properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                properties.setExpiration(Integer.toString(settings.getRequestTimeout()));
                byte[] body = JSON.toJSONBytes(response);
                if (body.length > settings.getMaxPayloadBytes()) body = JSON.toJSONBytes(LocalRpcDispatcher.response(id, StateCodeEnum.PACKAGE_ERROR, "RPC response exceeds max-payload-bytes"));
                publish("", replyTo, new Message(body, properties), TimeUnit.MILLISECONDS.toNanos(settings.getConfirmTimeout()));
            }
            if (currentDelivery(channel, generation)) channel.basicAck(tag, false);
        } catch (Exception e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            log.warn("RPC delivery rejected to dead letter queue; business outcome may be unknown", e);
            if (currentDelivery(channel, generation)) channel.basicReject(tag, false);
        }
    }
    private boolean currentDelivery(Channel channel, long generation) {
        return generation == connectionGeneration.get() && channel.isOpen();
    }
    private void publish(String exchange, String key, Message message, long remainingNanos)
            throws ExecutionException, InterruptedException, TimeoutException {
        CorrelationData data = new CorrelationData(UUID.randomUUID().toString());
        template.send(exchange, key, message, data);
        CorrelationData.Confirm confirm = data.getFuture().get(Math.min(remainingNanos, TimeUnit.MILLISECONDS.toNanos(settings.getConfirmTimeout())), TimeUnit.NANOSECONDS);
        if (!confirm.isAck()) throw new IllegalStateException("RabbitMQ publisher nack: " + confirm.getReason());
        if (data.getReturned() != null) throw new UnroutableException();
    }
    private SimpleMessageListenerContainer container(AcknowledgeMode mode, int concurrency) {
        SimpleMessageListenerContainer c = new SimpleMessageListenerContainer(connectionFactory);
        c.setAmqpAdmin(admin);
        c.setAutoDeclare(true);
        c.setMissingQueuesFatal(true);
        c.setDefaultRequeueRejected(false);
        c.setAcknowledgeMode(mode);
        c.setConcurrentConsumers(concurrency);
        c.setPrefetchCount(settings.getPrefetch());
        c.setShutdownTimeout(settings.getConfirmTimeout());
        c.setRecoveryInterval(1000);
        return c;
    }
    private static long remaining(long deadlineNanos) throws TimeoutException {
        long nanos = deadlineNanos - System.nanoTime();
        if (nanos <= 0) throw new TimeoutException("RPC deadline exceeded");
        return nanos;
    }
    private void failPending(String message) { pending.values().forEach(f -> f.completeExceptionally(new IllegalStateException(message))); }
    int pendingCount() { return pending.size(); }
    @Override public synchronized void close() {
        if (!closed.compareAndSet(false, true)) return;
        running.set(false);
        failPending("Rabbit RPC stopped; business outcome may be unknown");
        try { if (requestConsumer != null) requestConsumer.destroy(); }
        finally {
            try { if (replyConsumer != null) replyConsumer.destroy(); }
            finally { connectionFactory.destroy(); }
        }
    }
    private static String property(Properties p, String primary, String fallback, String value) { return p.getProperty(primary, p.getProperty(fallback, value)); }
    private static final class UnroutableException extends RuntimeException { }
}
