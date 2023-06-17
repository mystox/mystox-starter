package tech.mystox.demo.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mystox.framework.balancer.BaseLoadBalancer;
import tech.mystox.framework.config.WebPrivFuncConfig;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.PrivFuncEntity;

import java.util.List;
import java.util.Map;

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
    @GetMapping("/getWebPrivFuncTest")
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

    @ApiOperation(value = "获取baseloadbalancer路由信息")
    @GetMapping("/getOperaRouteMap")
    public JsonResult getRouteMap() {
        BaseLoadBalancer loadBalanceScheduler = (BaseLoadBalancer) iaContext.getIaENV().getLoadBalanceScheduler();
        Map<String, List<String>> operaRouteMap = loadBalanceScheduler.getLoadBalancerClient().getOperaRouteMap();
        return new JsonResult(operaRouteMap);
    }

}


