package tech.mystox.framework.balancer.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.config.OperaRouteConfig;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.scheduler.RegScheduler;
import tech.mystox.framework.service.common.OperaRouteService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperaRouteUpdateImpl implements OperaRouteService {


    @Autowired
    IaContext iaContext;


    @Override
    public List<String> broadcastOperaRoute(String operaCode,List<String> subGroupServerList) throws IOException {
        RegScheduler regScheduler=iaContext.getIaENV().getRegScheduler();
        IaConf conf = iaContext.getIaENV().getConf();
        OperaRouteConfig operaRouteConfig = conf.getOperaRouteConfig();
        Map<String, List<String>> operaRoute = operaRouteConfig.getOperaRoute();
        if (operaRoute == null) {
            operaRoute = new LinkedHashMap<>();
            operaRouteConfig.setOperaRoute(operaRoute);
        }
        List<String> oldServerArr = operaRoute.get(operaCode);
        operaRoute.put(operaCode, subGroupServerList);
        //写入文件
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        File file = FileUtils.getFile("./config/operaRoute.yml");
        try {
            if (!file.exists()) {
                File directory = new File("./config");
                if (!directory.exists()) {
                    boolean mkdirs = directory.mkdirs();
                }
                boolean newFile = file.createNewFile();
            }
            yaml.dump(JSONObject.toJSON(operaRouteConfig), new FileWriter(file));
//            String groupCodeServerCode = preconditionGroupServerCode(conf.getGroupCode(),
//                    preconditionServerCode(conf.getServerName(), conf.getServerVersion()));
//            String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
//            if (!regScheduler.exists(routePath))
//                regScheduler.create(routePath, JSONArray.toJSONBytes(subGroupServerList), IaConf.EPHEMERAL);
//            else
//                regScheduler.setData(routePath, JSONArray.toJSONBytes(subGroupServerList));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            operaRoute.put(operaCode, oldServerArr);
            yaml.dump(JSONObject.toJSON(operaRouteConfig), new FileWriter(file));
        }






//        Map<String, List<String>> operaRoute = iaENV.getConf().getOperaRouteConfig().getOperaRoute();
//        if (operaRoute != null && operaRoute.containsKey(operaCode)) {
//            //todo operaCode
//            if(iaENV.getConf().getOperaRouteConfig().getOperaRoute().containsKey(operaCode))
//            {
//
//
//            }
//            return iaENV.getRegScheduler().buildOperaMap(operaCode);
//        }
        return new ArrayList<>();
    }
}
