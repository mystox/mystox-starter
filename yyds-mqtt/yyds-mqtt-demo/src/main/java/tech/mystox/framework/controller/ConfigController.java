package tech.mystox.framework.controller;

import com.alibaba.fastjson.JSONObject;
import tech.mystox.framework.config.WebPrivFuncConfig;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.PrivFuncEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/11/6, 20:50.
 * company: mystox
 * description:
 * update record:
 */
@RestController
@RequestMapping("/configTest")
@RefreshScope
public class ConfigController {

    @Value("${test.config}")
    private String value;

    @Autowired
    IaContext iaContext;
//    @Autowired
//    WebPrivFuncConfig webPrivFuncConfig;
    @RequestMapping("/getWebPrivFuncTest")
    public JsonResult getWebPrivFuncTest() {
        WebPrivFuncConfig webPrivFuncConfig=iaContext.getIaENV().getConf().getWebPrivFuncConfig();
        System.out.println(value);
        PrivFuncEntity privFuncEntity = webPrivFuncConfig.getPrivFunc();
        System.out.println(webPrivFuncConfig);
        return new JsonResult(JSONObject.parseObject(JSONObject.toJSONString(privFuncEntity)));
    }
//    @Autowired
//    @Lazy
//    GenericPostableMvcEndpoint genericPostableMvcEndpoint;
//    @RequestMapping("/refreshConfig")
//    public String testConfigRefresh() {
//        genericPostableMvcEndpoint.invoke();
////        parseService.configTransverterInit();
//        return JSONObject.toJSONString(transverterConfig);
//    }

}


