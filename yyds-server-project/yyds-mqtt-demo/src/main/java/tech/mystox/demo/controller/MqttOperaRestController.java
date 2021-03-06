package tech.mystox.demo.controller;

import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.service.IaOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * \* @Author: mystox
 * \* Date: 2020/1/4 16:15
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/operaRest")
public class MqttOperaRestController {

    //    @Autowired
//    MqttOpera mqttOpera;

    final
    IaOpera msgHandler;

    @Autowired(required = false)
    public MqttOperaRestController(IaOpera msgHandler) {
        this.msgHandler = msgHandler;
    }

    // @Autowired
    // IaContext iaContext;
    @RequestMapping("/operaSync")
    public JsonResult operaSync(@RequestParam String operaCode, @RequestBody String message) {
        // MsgHandler msgHandler =iaContext.getIaENV().getMsgScheduler().getIaHandler();
        // MsgResult opera = msgHandler.opera(operaCode,message,2,10, TimeUnit.SECONDS);
        MsgResult opera = msgHandler.opera(operaCode, message);
        return new JsonResult(opera);
    }

    @RequestMapping("/operaAsync")
    public JsonResult operaAsync(@RequestParam String operaCode, @RequestBody String message) {
        // MsgHandler msgHandler =iaContext.getIaENV().getMsgScheduler().getIaHandler();
        msgHandler.operaAsync(operaCode, message);
        return new JsonResult();
    }

    @RequestMapping("/broadcast")
    public JsonResult broadcast(@RequestParam String operaCode, @RequestBody String message) {
        // MsgHandler msgHandler =iaContext.getIaENV().getMsgScheduler().getIaHandler();
        msgHandler.broadcast(operaCode, message);
        return new JsonResult("ok");
    }

}