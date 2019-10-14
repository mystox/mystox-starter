package com.kongtrolink.framework.mqtt.util;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.ModuleLog;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.service.MqttSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/10/12, 9:42.
 * company: kongtrolink
 * description: 消息日志处理类
 * update record:
 */
@Component
public class MqttLog {

    private Logger logger = LoggerFactory.getLogger(MqttLog.class);


    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Value("${logging.serverName:" + ServerName.LOG_SERVER + "}")
    private String logServerName;
    @Value("${logging.version:1.0.0}")
    private String logServerVersion;


    @Autowired
    MqttSender sender;


    public void ERROR(int stateCode, String operaCode, String targetServerCode) {
            String logServerCode = MqttUtils.preconditionServerCode(logServerName, logServerVersion);
        if (!logServerCode.equals(targetServerCode)) { //发送至日志服务产生的错误日志不重复发送至日志服务
            ModuleLog moduleLog = logBuilder(stateCode, operaCode, targetServerCode);
            sender.sendToMqtt(
                    MqttUtils.preconditionSubTopicId(
                            logServerCode,
                            operaCode),
                    OperaCode.MQLOG,
                    JSONObject.toJSONString(moduleLog));
        } else {
            //日志信息发送错误 不记录日志
            logger.warn("log msg send to log server exception...");
        }
    }

    /**
     * 构建日志信息
     * @param stateCode
     * @param operaCode
     * @param targetServerCode
     * @return
     */
    private ModuleLog logBuilder(int stateCode, String operaCode, String targetServerCode) {
        ModuleLog moduleLog = new ModuleLog();
        moduleLog.setOperaCode(operaCode);
        moduleLog.setStateCode(stateCode);
        moduleLog.setSourceServerCode(serverCode);
        moduleLog.setServerCode(targetServerCode);
        logger.debug("logger entity： " + JSONObject.toJSONString(moduleLog));
        return moduleLog;
    }


}
