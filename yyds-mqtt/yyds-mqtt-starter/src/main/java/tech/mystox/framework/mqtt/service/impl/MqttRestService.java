package tech.mystox.framework.mqtt.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.config.OperaRouteConfig;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.ServiceScanner;
import tech.mystox.framework.entity.AckEnum;
import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.scheduler.RegScheduler;
import tech.mystox.framework.service.MsgHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static tech.mystox.framework.common.util.MqttUtils.*;
import static tech.mystox.framework.entity.UnitHead.*;

/**
 * Created by mystoxlol on 2019/9/11, 17:32.
 * company: mystox
 * description:
 * update record:
 */
@Service
public class MqttRestService {
    Logger logger = LoggerFactory.getLogger(MqttRestService.class);

    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

//    @Value("${server.mark:*}")
//    private String serverMark;

    @Value("${server.groupCode}")
    private String groupCode;
    @Value("${server.name}_${server.version}")
    private String serverCode;

    final IaContext iaContext;
    //
    //    @Autowired
    //    RegisterRunner registerRunner;
    //
    //    @Autowired
    //    ServiceRegistry serviceRegistry;

    final ServiceScanner jarServiceScanner;

    private OperaRouteConfig operaRouteConfig;

    public MqttRestService(IaContext iaContext, ServiceScanner jarServiceScanner) {
        this.iaContext = iaContext;
        this.jarServiceScanner = jarServiceScanner;
    }

    @Autowired
    public void setOperaRouteConfig(OperaRouteConfig operaRouteConfig) {
        this.operaRouteConfig = operaRouteConfig;
    }


    public JsonResult<String> registerSub(JSONObject subJson) {
        MsgHandler iahander = iaContext.getIaENV().getMsgScheduler().getIaHandler();
        RegScheduler regScheduler = iaContext.getIaENV().getRegScheduler();
        String operaCode = subJson.getString("operaCode");
        String executeUnit = subJson.getString("executeUnit");
        String ack = subJson.getString("ack");
        String head = "";

        if (executeUnit.startsWith(JAR)) head = JAR;
        if (executeUnit.startsWith(LOCAL)) head = LOCAL;
        if (executeUnit.startsWith(HTTP)) head = HTTP;

        RegisterSub sub = new RegisterSub();
        sub.setExecuteUnit(executeUnit);
        sub.setOperaCode(operaCode);
        sub.setAck(AckEnum.ACK.toString().equals(ack) ? AckEnum.ACK : AckEnum.NA);
        try {
            String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
            boolean b = false;
            if (executeUnit.startsWith(JAR)) {
                b = jarServiceScanner.addSub(sub);
            } else if (executeUnit.startsWith(LOCAL)) {
                return new JsonResult<>("暂未实现" + head, false);
            } else if (executeUnit.startsWith(HTTP)) {
                return new JsonResult<>("暂未实现" + head, false);
            }
            regScheduler.setDataToRegistry(sub);

            //暂时性的内部map
            if (iahander.isExists(topic)) {
                logger.info("add sub topic[{}] to mqtt broker...", topic);
                iahander.addSubTopic(topic, 2);
            }
            return new JsonResult<>("add sub " + (b ? "success" : "false"), b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonResult<>();
    }


    public JsonResult<String> deleteSub(JSONObject body) {
        MsgHandler iahander = iaContext.getIaENV().getMsgScheduler().getIaHandler();
        RegScheduler regScheduler = iaContext.getIaENV().getRegScheduler();
        String operaCode = body.getString("operaCode");
        String path = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        try {
            String data = regScheduler.getData(path);
            RegisterSub sub = JSONObject.parseObject(data, RegisterSub.class);
            String executeUnit = sub.getExecuteUnit();
            if (executeUnit.startsWith(JAR)) {
                //删除注册信息
                if (regScheduler.exists(path)) {
                    logger.info("delete register");
                    regScheduler.deleteNode(path);
                }
                //删除订阅信息
                if (iahander.isExists(path)) {
                    logger.info("delete mqtt sub");
                    iahander.removeSubTopic(path);
                }
                //删除jarRes.yml中的对应key：value
                boolean b = jarServiceScanner.deleteSub(operaCode);
                if (b) {
                    logger.info("delete jar res key");
                } else {
                    return new JsonResult<>("更新jar文件失败", false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        String unitHead = body.getString("unitHead");


        return new JsonResult<>();
    }


    public void updateOperaRoute(String operaCode, List<String> subGroupServerList) throws InterruptedException, IOException {
        RegScheduler regScheduler = iaContext.getIaENV().getRegScheduler();
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

            /*if (!file.exists()) {
                File directory = new File("./config");
                if (!directory.exists()) {
                    boolean mkdirs = directory.mkdirs();
                }
                boolean newFile = file.createNewFile();
            }*/
            //            yaml.dump(JSONObject.toJSON(operaRouteConfig), new FileWriter(file));
            String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
            String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
            if (!regScheduler.exists(routePath))
                regScheduler.create(routePath, JSON.toJSONBytes(subGroupServerList), IaConf.EPHEMERAL);
            else
                regScheduler.setData(routePath, JSON.toJSONBytes(subGroupServerList));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            operaRoute.put(operaCode, oldServerArr);
            yaml.dump(JSON.toJSON(operaRouteConfig), new FileWriter(file));
        }

    }

}
