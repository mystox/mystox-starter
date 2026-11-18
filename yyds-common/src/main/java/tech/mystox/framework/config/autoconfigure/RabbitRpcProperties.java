package tech.mystox.framework.config.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.nio.charset.StandardCharsets;

/** Settings for the opt-in register.type=none RabbitMQ RPC protocol. */
@ConfigurationProperties(prefix = "rabbitmq.rpc")
public class RabbitRpcProperties {
    private String contractVersion = "v1";
    private String exchange = "yyds.rpc.requests";
    private String queuePrefix = "yyds.rpc";
    private String queueType = "quorum";
    private int requestTimeout = 30000;
    private int confirmTimeout = 5000;
    private int maxPending = 1000;
    private int concurrency = 4;
    private int prefetch = 1;
    private int maxPayloadBytes = 1048576;

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String value) { contractVersion = value; }
    public String getExchange() { return exchange; }
    public void setExchange(String value) { exchange = value; }
    public String getQueuePrefix() { return queuePrefix; }
    public void setQueuePrefix(String value) { queuePrefix = value; }
    public String getQueueType() { return queueType; }
    public void setQueueType(String value) { queueType = value; }
    public int getRequestTimeout() { return requestTimeout; }
    public void setRequestTimeout(int value) { requestTimeout = value; }
    public int getConfirmTimeout() { return confirmTimeout; }
    public void setConfirmTimeout(int value) { confirmTimeout = value; }
    public int getMaxPending() { return maxPending; }
    public void setMaxPending(int value) { maxPending = value; }
    public int getConcurrency() { return concurrency; }
    public void setConcurrency(int value) { concurrency = value; }
    public int getPrefetch() { return prefetch; }
    public void setPrefetch(int value) { prefetch = value; }
    public int getMaxPayloadBytes() { return maxPayloadBytes; }
    public void setMaxPayloadBytes(int value) { maxPayloadBytes = value; }

    public void validate() {
        if (contractVersion == null || contractVersion.isBlank()
                || exchange == null || exchange.isBlank() || exchange.startsWith("amq.")
                || queuePrefix == null || queuePrefix.isBlank() || queuePrefix.startsWith("amq.")) {
            throw new IllegalArgumentException("Rabbit RPC contract-version, exchange and queue-prefix must be nonblank; amq.* is reserved");
        }
        if (!"quorum".equals(queueType) && !"classic".equals(queueType)) {
            throw new IllegalArgumentException("rabbitmq.rpc.queue-type must be quorum or classic");
        }
        if ((exchange + ".dlx").getBytes(StandardCharsets.UTF_8).length > 255
                || (queuePrefix + ".reply." + "x".repeat(36)).getBytes(StandardCharsets.UTF_8).length > 255) {
            throw new IllegalArgumentException("Rabbit RPC exchange/queue-prefix exceeds AMQP name limits");
        }
        if (requestTimeout <= 0 || confirmTimeout <= 0 || maxPending <= 0
                || concurrency <= 0 || prefetch <= 0 || maxPayloadBytes < 256) {
            throw new IllegalArgumentException("Rabbit RPC timeouts, limits and concurrency must be positive; max-payload-bytes must be >= 256");
        }
    }
}
