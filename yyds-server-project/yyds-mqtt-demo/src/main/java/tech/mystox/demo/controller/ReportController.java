package tech.mystox.demo.controller;

import com.alibaba.fastjson2.JSONObject;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.ServerName;
import tech.mystox.framework.service.MsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/10/30, 13:28.
 * company: mystox
 * description:
 * update record:
 */
@RestController
@RequestMapping("/reports")
public class ReportController {

//    @Autowired
//    MqttSender mqttSender;

    @Autowired
    IaContext iaContext;

    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    @RequestMapping("/testSyn")
    JsonResult reportsTest(@RequestBody(required = false) JSONObject body) {
        MsgHandler msgHandler=iaContext.getIaENV().getMsgScheduler().getIaHandler();
        String operaCode = body.getString("operaCode");
        body.put("serverCode", MqttUtils.preconditionServerCode(serverName, serverVersion));
//        body.put("asyn", true);//是否异步
        Boolean asyn = body.getBoolean("asyn");
//        if (asyn)
//            mqttSender.sendToMqtt(MqttUtils.preconditionServerCode(ServerName.REPORTS_SERVER, "1.0.0"), operaCode, body.toJSONString());
//        else {
            MsgResult msgResult = msgHandler.sendToMqttSync(MqttUtils.preconditionServerCode(ServerName.REPORTS_SERVER, "1.0.0"), operaCode, body.toJSONString());
            return new JsonResult(msgResult);
//        }
//        return new JsonResult();

    }

}
