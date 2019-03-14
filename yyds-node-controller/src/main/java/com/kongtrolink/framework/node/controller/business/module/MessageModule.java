package com.kongtrolink.framework.node.controller.business.module;

import com.kongtrolink.framework.core.mq.mqtt.AbstractMqttService;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.node.controller.connector.module.RouteModule;
import com.kongtrolink.framework.node.controller.connector.module.RpcModule;
import com.kongtrolink.framework.node.controller.entity.Route;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/2/20, 21:56.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class MessageModule extends AbstractMqttService implements ModuleInterface
{
    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${mqtt.broker.host}")
    private String broker;
    @Value("${mqtt.sub.topic}")
    private String subTopic;


    @Autowired
    RouteModule routeModule;

    @Autowired
    RpcModule rpcModule;

    @Override
    public boolean init()
    {
        logger.info("connector-message module init");
        String[] topic = getTopic();
        super.start(broker, subTopic, topic);
        return true;
    }

    private String[] getTopic()
    {
        List<String> list = new ArrayList<>();
        list.add(subTopic);
        String[] topics = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            topics[i] = list.get(i);
        }
        return topics;
    }

    @Override
    public void setMessageArrived(String topic, MqttMessage message) throws UnsupportedEncodingException
    {
        String messageStr = new String(message.getPayload(), "UTF-8");
        sendMessage(topic, messageStr);

    }

    public void sendMessage(String mark, String message)
    {
        //获取路由
        Route[] routes = routeModule.getRouteByMark(mark);
        //发送消息
        Route route = routes[0];
        InetSocketAddress addr = new InetSocketAddress(route.getIp(),route.getPort());
        try
        {
            //发送消息
            rpcModule.postMsg(addr, message);
        } catch (IOException e)
        {
            //异常熔断
            routeModule.circuitBreaker(mark);
            e.printStackTrace();
        }

    }

}
