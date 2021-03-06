package tech.mystox.framework.core;

import com.alibaba.fastjson.JSONObject;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.entity.MqttLog;
import tech.mystox.framework.entity.OperaCode;
import tech.mystox.framework.entity.ServerName;
import tech.mystox.framework.service.IaOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Created by mystoxlol on 2019/10/12, 9:42.
 * company: mystox
 * description: 消息日志处理类
 * update record:
 */
@Component
public class MqttLogUtil {

    private Logger logger = LoggerFactory.getLogger(MqttLogUtil.class);
    @Value("${server.name}_${server.version}")
    private String serverCode;
    @Value("${logging.serverName:" + ServerName.LOG_SERVER + "}")
    private String logServerName;
    @Value("${logging.version:1.0.0}")
    private String logServerVersion;
    @Value("${logging.mqtt.flag.error:true}")
    private Boolean mqttErrorFlag;
    @Autowired
    private ThreadPoolTaskExecutor logExecutor;
    @Autowired
    IaContext iaContext;


    @Autowired
    IaOpera iaOpera;


    public void ERROR(String msgId, int stateCode, String operaCode, String targetServerCode) {
        if (mqttErrorFlag) return;
        String logServerCode = MqttUtils.preconditionServerCode(logServerName, logServerVersion);
        if (!logServerCode.equals(targetServerCode)/* && !OperaCode.SLOGIN.equals(operaCode)*/) { //发送至日志服务产生的错误日志不重复发送至日志服务
            MqttLog mqttLog = logBuilder(msgId, stateCode, operaCode, targetServerCode);
            logExecutor.execute(() ->
                    iaOpera.operaAsync(OperaCode.MQLOG, JSONObject.toJSONString(mqttLog)));
        } else {
            //日志信息发送错误的错误日志 不记录日志
            logger.warn("log msg send to log server exception...");
        }
    }

    public void OPERA_ERROR(int stateCode, String operaCode) {
        if (OperaCode.MQLOG.equals(operaCode)) {
            logger.error("mqtt log server is not running, skip save log...");
            return;
        }
        MqttLog mqttLog = operaRouteLogBuilder(UUID.randomUUID().toString(), stateCode, operaCode);
            logExecutor.execute(() ->
                    iaOpera.operaAsync(OperaCode.MQLOG, JSONObject.toJSONString(mqttLog)));
    }


    /**
     * 构建日志信息
     *
     * @param stateCode
     * @param operaCode
     * @param targetServerCode
     * @return
     */
    private MqttLog logBuilder(String msgId, int stateCode, String operaCode, String targetServerCode) {
        MqttLog mqttLog = new MqttLog();
        mqttLog.setMsgId(msgId);
        mqttLog.setOperaCode(operaCode);
        mqttLog.setStateCode(stateCode);
        mqttLog.setSourceServerCode(serverCode);
        mqttLog.setServerCode(targetServerCode);
        mqttLog.setRecordTime(new Date());
        logger.debug("logger entity： " + JSONObject.toJSONString(mqttLog));
        return mqttLog;
    }

    private MqttLog operaRouteLogBuilder(String msgId, int stateCode, String operaCode) {
        MqttLog mqttLog = new MqttLog();
        mqttLog.setMsgId(msgId);
        mqttLog.setOperaCode(operaCode);
        mqttLog.setStateCode(stateCode);
        mqttLog.setSourceServerCode(serverCode);
        mqttLog.setRecordTime(new Date());
        logger.debug("logger entity： " + JSONObject.toJSONString(mqttLog));
        return mqttLog;
    }


}
