package com.kongtrolink.yyjw.congtroller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.yyjw.service.MqttTestInterface;
import com.kongtrolink.yyjw.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/2/27, 17:21.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/mqtt")
public class MqttInterfaceController
{
    @Autowired
    MqttTestInterface mqttTestInterface;
    @RequestMapping("/sendMsg")
    public JsonResult getSignalList(@RequestBody(required = false) String requestBody)
    {
        JSONObject result = mqttTestInterface.sendMsg(requestBody);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                "0".equals(result.get("result")) ? new JsonResult("执行任务失败", false) :new JsonResult(result.get("data"));
    }
}
