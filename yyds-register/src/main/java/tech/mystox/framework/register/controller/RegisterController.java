package tech.mystox.framework.register.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.cloud.context.refresh.ContextRefresher;
import tech.mystox.framework.config.WebPrivFuncConfig;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.OperaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mystoxlol on 2019/8/29, 9:46.
 * company: mystox
 * description:
 * update record:
 */
@Lazy
@RestController
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    IaContext iaContext;
    Logger logger = LoggerFactory.getLogger(RegisterController.class);

    ContextRefresher contextRefresher;

    @Autowired
    @Lazy
    public void setContextRefresher(ContextRefresher contextRefresher) {
        this.contextRefresher = contextRefresher;
    }

    WebPrivFuncConfig webPrivFuncConfig;

    @Autowired
    public void setWebPrivFuncConfig(WebPrivFuncConfig webPrivFuncConfig) {
        this.webPrivFuncConfig = webPrivFuncConfig;
    }


    @RequestMapping("/getPath")
    public JsonResult getZookeeper(@RequestParam(required = false) String path) {
        IaENV iaENV = iaContext.getIaENV();
        String sessionId = iaENV.getConf().getMyId();
        JSONObject result = new JSONObject();
        result.put("zookeeperSession", sessionId);
        List<String> children = null;
        try {
            String data = iaENV.getRegScheduler().getData(path);
            logger.info("data: [{}]", data);
            result.put("data", JSON.parse(data));
            children = iaENV.getRegScheduler().getChildren(path);
            result.put("children", children);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonResult(result);
    }


    @RequestMapping("/refreshWebConfig")
    public JsonResult testConfigRefresh() {
        IaENV iaENV = iaContext.getIaENV();
        Set<String> strings = contextRefresher.refresh();
        logger.info(JSONObject.toJSONString(strings));
        OperaResult operaResult = null;
        try {
            iaENV.getRegScheduler().registerWebPriv(iaENV.getConf().getWebPrivFuncConfig());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonResult("");
    }
}
