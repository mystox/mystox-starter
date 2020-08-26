package tech.mystox.framework.balancer.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/balancer")
public class BalancerController {


    @RequestMapping("/updateOperaRouteLocal")
    public List<String> updateOperaRouteLocal(String operaCode) {
        return null;
    }


    @RequestMapping("/updateOperaRouteBroadcast")
    public List<String> updateOperaRouteBroadcast(String operaCode) {
        return null;
    }
}
