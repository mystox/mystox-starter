package tech.mystox.framework.config.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by mystox on 2026/2/25, 16:47.
 * company:
 * description:
 * update record:
 */
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    private String url;
    private String username;
    private String password;
    private int maxInflight = 100;
    private int executorCorePoolSize = 10;
    private int executorMaxPoolSize = 10000;
    private int senderCount = 10;
    private String consumerDefaultTopic = "topic";
    private String producerDefaultTopic = "topic";
    private int payloadLimit = 47 * 1024;

    private int callbackMaxCount = 10000;
    private int packageMsgTimeout = 30;
    public String getProducerDefaultTopic() {
        return producerDefaultTopic;
    }

    public void setProducerDefaultTopic(String producerDefaultTopic) {
        this.producerDefaultTopic = producerDefaultTopic;
    }

    private int completionTimeout = 3000;

    public int getCompletionTimeout() {
        return completionTimeout;
    }

    public void setCompletionTimeout(int completionTimeout) {
        this.completionTimeout = completionTimeout;
    }

    public String getConsumerDefaultTopic() {
        return consumerDefaultTopic;
    }

    public void setConsumerDefaultTopic(String consumerDefaultTopic) {
        this.consumerDefaultTopic = consumerDefaultTopic;
    }

    public int getSenderCount() {
        return senderCount;
    }

    public void setSenderCount(int senderCount) {
        this.senderCount = senderCount;
    }

    public int getExecutorMaxPoolSize() {
        return executorMaxPoolSize;
    }

    public void setExecutorMaxPoolSize(int executorMaxPoolSize) {
        this.executorMaxPoolSize = executorMaxPoolSize;
    }

    public int getExecutorCorePoolSize() {
        return executorCorePoolSize;
    }

    public void setExecutorCorePoolSize(int executorCorePoolSize) {
        this.executorCorePoolSize = executorCorePoolSize;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMaxInflight(int maxInflight) {
        this.maxInflight = maxInflight;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxInflight() {
        return maxInflight;
    }

    // getter/setter
}