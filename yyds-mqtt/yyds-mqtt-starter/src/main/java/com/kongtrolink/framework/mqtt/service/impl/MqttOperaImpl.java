package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.kongtrolink.framework.common.util.MqttUtils.*;

/**
 * \* @Author: mystox
 * \* Date: 2020/1/2 14:41
 * \* Description:
 * \
 */
@Service
public class MqttOperaImpl implements MqttOpera {

    Logger logger = LoggerFactory.getLogger(MqttOperaImpl.class);

    @Value("${server.groupCode}")
    private String groupCode;

    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    private MqttSender mqttSender;

    private ServiceRegistry serviceRegistry;

    @Autowired
    public void setMqttSender(MqttSender mqttSender) {
        this.mqttSender = mqttSender;
    }

    @Autowired
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public MsgResult opera(String operaCode, String msg) {
        return opera(operaCode, msg, 1, 0, null, false);

    }

    @Override
    public MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit) {
        return opera(operaCode, msg, qos, timeout, timeUnit, true);
    }

    /**
     * @return com.kongtrolink.framework.entity.MsgResult
     * @Date 16:12 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description
     **/
    private MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag) {
        MsgResult result;
        // 获取operaCode 路由表 /mqtt/operaRoute/groupCode/serverCode/operaCode
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
        String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
        try {
            if (!serviceRegistry.exists(routePath))
                serviceRegistry.create(routePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            String data = serviceRegistry.getData(routePath);
            List<String> topicArr = JSONArray.parseArray(data, String.class);
            if (CollectionUtils.isEmpty(topicArr)) {
                //根据订阅表获取整合的订阅信息 <operaCode,[subTopic1,subTopic2]>
                List<String> subTopicArr = buildOperaMap(operaCode);
                serviceRegistry.setData(routePath, JSONArray.toJSONBytes(subTopicArr));
                topicArr = subTopicArr;
            }
            //如果路由配置只有一个元素，则默认直接选择单一元素进行发送
            int size = topicArr.size();
            if (CollectionUtils.isEmpty(topicArr)) {
                logger.error("route topic list size is null error...");
                return new MsgResult(StateCode.OPERA_ROUTE_EXCEPTION, "route topic list size is null error...");
            }
            if (size == 1) {
                String groupServerCode = topicArr.get(0);
                result =  setFlag ? mqttSender.sendToMqttSyn(groupServerCode, operaCode, qos, msg, timeout, timeUnit)
                        : mqttSender.sendToMqttSyn(groupServerCode, operaCode, msg);
                return result;
            }
            if (size > 1) { //默认数组多于1的情况下，识别为负载均衡
                Random r = new Random();
                int i = r.nextInt(size);
                String groupServerCode = topicArr.get(i);
                result =  setFlag ? mqttSender.sendToMqttSyn(groupServerCode, operaCode, qos, msg, timeout, timeUnit)
                        : mqttSender.sendToMqttSyn(groupServerCode, operaCode, msg);
                if (result.getStateCode() != StateCode.SUCCESS)
                {
                    //移除路由
                    topicArr.remove(i);
                    serviceRegistry.setData(routePath, JSONArray.toJSONBytes(topicArr));
                    //重新请求
                    opera(operaCode, msg, qos, timeout, timeUnit, setFlag);
                }
                return result;
            }
        } catch (KeeperException | InterruptedException e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
            logger.error("[{}] operaCode executor error [{}]", operaCode, e.toString());
        }
        result = new MsgResult(StateCode.OPERA_ROUTE_EXCEPTION,"route request failed");
        return result;
    }


    /**
     * @return java.util.List<java.lang.String>
     * @Date 14:05 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 构建默认组装信息
     **/
    private List<String> buildOperaMap(String operaCode) throws KeeperException, InterruptedException {
        List<String> result = new ArrayList<>();
        String subPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupCode);
        List<String> serverArr = serviceRegistry.getChildren(subPath); //获取订阅表的服务列表
        //遍历订阅服务列表
        for (String serverCode : serverArr) {
            String groupServerCode = preconditionGroupServerCode(groupCode, serverCode);
            String serverPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupServerCode);
            List<String> serverOperaCodeArr = serviceRegistry.getChildren(serverPath);
            if (serverOperaCodeArr.contains(operaCode)) {
                result.add(groupServerCode);
            }
        }
        //ROOT 节点获取服务接口信息即云管等
        String rootSubPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, GroupCode.ROOT);
        List<String> rootServerArr = serviceRegistry.getChildren(rootSubPath); //获取订阅表的服务列表
        //遍历订阅服务列表
        for (String serverCode : rootServerArr) {
            String groupServerCode = preconditionGroupServerCode(GroupCode.ROOT, serverCode);
            String serverPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupServerCode);
            List<String> serverOperaCodeArr = serviceRegistry.getChildren(serverPath);
            if (serverOperaCodeArr.contains(operaCode)) {
                result.add(groupServerCode);
            }
        }
        return result;
    }

    @Override
    public void broadcast(String operaCode, String msg) {
        broadcast(operaCode, msg, 0,false);
    }

    /**
     * @return com.kongtrolink.framework.entity.MsgResult
     * @Date 16:12 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description
     **/
    private void broadcast(String operaCode, String msg, int qos,boolean setFlag) {
        // 获取operaCode 路由表 /mqtt/operaRoute/groupCode/serverCode/operaCode
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
        String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
        try {
            if (!serviceRegistry.exists(routePath))
                serviceRegistry.create(routePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            String data = serviceRegistry.getData(routePath);
            List<String> topicArr = JSONArray.parseArray(data, String.class);
            if (CollectionUtils.isEmpty(topicArr)) {
                //根据订阅表获取整合的订阅信息 <operaCode,[subTopic1,subTopic2]>
                List<String> subTopicArr = buildOperaMap(operaCode);
                serviceRegistry.setData(routePath, JSONArray.toJSONBytes(subTopicArr));
                topicArr = subTopicArr;
            }
            //全部广播发送
            topicArr.forEach(groupServerCode -> {
                if (setFlag) mqttSender.sendToMqtt(groupServerCode, operaCode, qos, msg);
                else mqttSender.sendToMqtt(groupServerCode, operaCode, msg);
            });

        } catch (KeeperException | InterruptedException e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
            logger.error("[{}] operaCode executor error [{}]", operaCode, e.toString());
        }
    }

    @Override
    public MsgResult slogin(String registerGroupServerCode, String sLoginPayload) {
        MsgResult slogin = mqttSender.sendToMqttSyn(registerGroupServerCode,
                OperaCode.SLOGIN, 2, sLoginPayload, 30000L, TimeUnit.MILLISECONDS);
        return slogin;
    }
}