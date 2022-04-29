package tech.mystox.demo.controller;

import org.springframework.web.bind.annotation.*;
import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.service.IaOpera;
import org.springframework.beans.factory.annotation.Autowired;

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

    private final IaOpera msgHandler;

    @Autowired(required = false)
    public MqttOperaRestController(IaOpera msgHandler) {
        this.msgHandler = msgHandler;
    }

    // @Autowired
    // IaContext iaContext;
    @RequestMapping(value = "/operaSync", method = RequestMethod.POST)
    public JsonResult operaSync(@RequestParam String operaCode, @RequestBody String message) {
        // MsgHandler msgHandler =iaContext.getIaENV().getMsgScheduler().getIaHandler();
        // MsgResult opera = msgHandler.opera(operaCode,message,2,10, TimeUnit.SECONDS);
        MsgResult opera = msgHandler.opera(operaCode, message);
        return new JsonResult(opera);
    }

    @RequestMapping(value = "/operaAsync", method = RequestMethod.POST)
    public JsonResult operaAsync(@RequestParam String operaCode, @RequestBody String message) {
        // MsgHandler msgHandler =iaContext.getIaENV().getMsgScheduler().getIaHandler();
        msgHandler.operaAsync(operaCode, message);
        return new JsonResult();
    }

    @RequestMapping(value = "/broadcast", method = RequestMethod.POST)
    public JsonResult broadcast(@RequestParam String operaCode, @RequestBody String message) {
        // MsgHandler msgHandler =iaContext.getIaENV().getMsgScheduler().getIaHandler();
        msgHandler.broadcast(operaCode, message);
        return new JsonResult("ok");
    }

}