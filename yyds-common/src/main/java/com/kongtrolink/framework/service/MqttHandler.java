package com.kongtrolink.framework.service;

/**
 * Created by mystoxlol on 2019/8/20, 10:08.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface MqttHandler {

    /**
     * 增加topic订阅
     * @param topic
     * @param qos
     */
    public void addSubTopic(String topic, int qos);

    /**
     * 增加topic订阅
     * @param topic
     */
    public void addSubTopic(String... topic);

    public void removeSubTopic(String... topic);
    public boolean isExists(String topic);

}
