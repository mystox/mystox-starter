package com.kongtrolink.framework.core.mq.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MQTT 连接订阅 抽象类
 * @author Mag
 */
public abstract class AbstractMqttService
{

    private final int qos = 2;	// 服务质量
    private String mqttHost;
    private String clientId;
    private String[] topics;
    private MqttClient mqttClient;
    private ScheduledExecutorService scheduler;
    private EnumMqttConnStatus connStatus = EnumMqttConnStatus.UNCONNECT;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMqttService.class);


    /**
     * 配置数据
     * @param mqttHost MQTT服务机地址
     * @param clientId 主题订阅 接收源
     */
    public  void initHost(String mqttHost, String clientId){
        this.mqttHost = mqttHost;
        this.clientId = clientId;
    }


    /**
     * MQTT 服务初始化
     */
    public void start(String mqttHost, String clientId, String[] topics) {
        try {
            this.topics = topics;
            initHost(mqttHost, clientId);
            LOGGER.info("MQTT 服务初始化 mqttHost：{}, clientId:{}, topics:{}",mqttHost ,clientId, Arrays.asList(topics));
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(mqttHost, clientId, persistence);
            System.out.println("我获取了mqttClient："+mqttClient);
            connect();
            subscribe(this.topics);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        startReconnect(this.topics);
    }

    /**
     * MQTT 客户机重连定时器，当客户机断开连接时，每10秒进行一次重连
     */
    private void startReconnect(final String[] topic) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (mqttClient.isConnected() == false) {
                    try {
                        connect();
                    } catch (MqttException ex) {
                        ex.printStackTrace();
                    }
                }

                if (mqttClient.isConnected() == true && connStatus != EnumMqttConnStatus.SUBSCRIBED) {
                    try {
                        subscribe(topic);
                    } catch (MqttException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * MQTT 客户机设置连接参数、遗言，并建立连接
     * @throws MqttException
     */
    private void connect() throws MqttException {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        mqttClient.connect(connOpts);
        if (mqttClient.isConnected()) {
            connStatus = EnumMqttConnStatus.CONNECTED;
        }
        LOGGER.info("[MQTT] Client connected to {}, clientId: {}, state: {}",
                mqttHost, clientId, mqttClient.isConnected());
    }

    /**
     * MQTT 客户机订阅主题，并设置报文到达时的回调方法
     * @throws MqttException
     */
    private void subscribe(String[] topics) throws MqttException{
        LOGGER.info("[MQTT] topics:{}",topics);
        int[] subQos = new int[topics.length];
        Arrays.fill(subQos, qos);
        mqttClient.subscribe(topics, subQos);
        LOGGER.info("[MQTT] Subscribe topics {}.", Arrays.toString(topics));
        mqttClient.setCallback(new MqttCallback() {

            public void connectionLost(Throwable cause) {
                connStatus = EnumMqttConnStatus.UNCONNECT;
                LOGGER.info("[MQTT] Connection Lost.");
            }

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                try{
                    setMessageArrived(topic,message);
                }catch (Exception e){
                    LOGGER.error("topic:{} 发生异常 ,{} ",topic,message.toString());
                    e.printStackTrace();
                }
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
                LOGGER.info("[MQTT] Delivery complete, messageId: {}", token.getMessageId());
            }

        });
        connStatus = EnumMqttConnStatus.SUBSCRIBED;
    }

    /**
     * 接收到订阅消息时处理方法
     * @param topic
     * @param message
     * @throws Exception
     */
    public abstract void  setMessageArrived(String topic, MqttMessage message)  throws Exception ;








}
