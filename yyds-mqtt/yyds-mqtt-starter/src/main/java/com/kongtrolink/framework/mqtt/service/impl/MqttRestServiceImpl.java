package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.AckEnum;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.entity.UnitHead;
import com.kongtrolink.framework.mqtt.service.MqttRestService;
import com.kongtrolink.framework.register.runner.RegisterRunner;
import com.kongtrolink.framework.register.runner.ServiceScanner;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.service.MqttHandler;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.kongtrolink.framework.entity.UnitHead.*;

/**
 * Created by mystoxlol on 2019/9/11, 17:32.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class MqttRestServiceImpl implements MqttRestService {
    Logger logger = LoggerFactory.getLogger(MqttRestServiceImpl.class);

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Autowired
    @Qualifier("mqttHandlerImpl")
    MqttHandler mqttHandlerImpl;

    @Autowired
    RegisterRunner registerRunner;

    @Autowired
    ServiceRegistry serviceRegistry;

    @Autowired
    ServiceScanner jarServiceScanner;

    @Override
    public JsonResult registerSub(JSONObject subJson) {
        String operaCode = subJson.getString("operaCode");
        String executeUnit = subJson.getString("executeUnit");
        String ack = subJson.getString("ack");
        String head = "";

        if (executeUnit.startsWith(JAR)) head = JAR;
        if (executeUnit.startsWith(LOCAL)) head = LOCAL;
        if (executeUnit.startsWith(HTTP)) head = HTTP;

        RegisterSub sub = new RegisterSub();
        sub.setExecuteUnit(executeUnit);
        sub.setOperaCode(operaCode);
        sub.setAck(AckEnum.ACK.toString().equals(ack) ? AckEnum.ACK : AckEnum.NA);
        try {
            String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
            boolean b = false;
            if (executeUnit.startsWith(JAR)) {
                b = jarServiceScanner.addSub(sub);
            } else if (executeUnit.startsWith(LOCAL)) {
                return new JsonResult("暂未实现" + head, false);
            } else if (executeUnit.startsWith(HTTP)) {
                return new JsonResult("暂未实现" + head, false);
            }
            registerRunner.setDataToRegistry(sub);

            //暂时性的内部map
            if (!mqttHandlerImpl.isExists(topic)) {
                logger.info("add sub topic[{}] to mqtt broker...", topic);
                mqttHandlerImpl.addSubTopic(topic, 2);
            }
            return new JsonResult("add sub " + (b ? "success" : "false"), b);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new JsonResult();
    }

    @Override
    public JsonResult deleteSub(JSONObject body) {

        String operaCode = body.getString("operaCode");
        String path = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        try {
            String data = serviceRegistry.getData(path);
            RegisterSub sub = JSONObject.parseObject(data, RegisterSub.class);
            String executeUnit = sub.getExecuteUnit();
            if (executeUnit.startsWith(UnitHead.JAR)) {
                //删除注册信息
                if (serviceRegistry.exists(path)) {
                    logger.info("delete register");
                    serviceRegistry.deleteNode(path);
                }
                //删除订阅信息
                if (mqttHandlerImpl.isExists(path)) {
                    logger.info("delete mqtt sub");
                    mqttHandlerImpl.removeSubTopic(path);
                }
                //删除jarRes.yml中的对应key：value
                boolean b = jarServiceScanner.deleteSub(operaCode);
                if (b) {
                    logger.info("delete jar res key");
                } else {
                    return new JsonResult("更新jar文件失败", false);
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        String unitHead = body.getString("unitHead");



        return new JsonResult();
    }
}
