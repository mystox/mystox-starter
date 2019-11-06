package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.config.WebOperateConfig;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.mqtt.OperateEntity;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import com.kongtrolink.framework.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 11:01
 * @Description:
 */
@Controller
@RequestMapping("/enterpriseLevelController")
public class EnterpriseLevelController extends BaseController {

    @Autowired
    EnterpriseLevelService enterpriseLevelService;
    @Autowired
    MqttSender mqttSender;
    @Autowired
    WebOperateConfig webOperateConfig;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody EnterpriseLevel enterpriseLevel){
        Date curTime = new Date();
        String code = StringUtil.getCode(curTime);
        enterpriseLevel.setUpdateTime(curTime);
        enterpriseLevel.setCode(code);
        enterpriseLevel.setState(Contant.FORBIT);
        enterpriseLevel.setLevelType(Contant.MANUAL);
        enterpriseLevelService.add(enterpriseLevel);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody EnterpriseLevelQuery enterpriseLevelQuery){
        boolean delete = enterpriseLevelService.delete(enterpriseLevelQuery.getId());
        if(delete){
            return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(@RequestBody EnterpriseLevel enterpriseLevel){
        enterpriseLevel.setUpdateTime(new Date());
        enterpriseLevel.setLevelType(Contant.MANUAL);
        boolean update = enterpriseLevelService.update(enterpriseLevel);
        if(update){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody EnterpriseLevelQuery levelQuery){
        List<EnterpriseLevel> list = enterpriseLevelService.list(levelQuery);
        int count = enterpriseLevelService.count(levelQuery);
        ListResult<EnterpriseLevel> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/16 14:46
     * 功能描述:修改状态
     * 需要修改该企业等级相关的告警等级状态
     */
    @RequestMapping("/updateState")
    @ResponseBody
    public JsonResult updateState(@RequestBody EnterpriseLevelQuery levelQuery){
        String state = levelQuery.getState();
        boolean result = enterpriseLevelService.updateState(levelQuery);
        if(result){
            return new JsonResult(state + Contant.RESULT_SUC, true);
        }
        return new JsonResult(state + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/getDeviceTypeList")
    @ResponseBody
    public String getDeviceTypeList(@RequestBody EnterpriseLevelQuery enterpriseLevelQuery){

        String mqttServerCode = "ASSET_MANAGEMENT_SERVER_1.0.0";
        String operaCode = "getCIModel";
        webOperateConfig.initConfigMap();
        List<OperateEntity> operate = webOperateConfig.getOperate();
        if(null != operate && operate.size() > 0){
            OperateEntity operateEntity = operate.get(0);
            mqttServerCode = operateEntity.getServerVerson();
            operaCode = operateEntity.getOperaCode();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", enterpriseLevelQuery.getEnterpriseCode());
        jsonObject.put("serverCode", enterpriseLevelQuery.getServerCode());
        MsgResult msgResult = mqttSender.sendToMqttSyn(mqttServerCode, operaCode, jsonObject.toJSONString());
        System.out.println(msgResult);
        return msgResult.getMsg();
    }

    /**
     * @auther: liudd
     * @date: 2019/10/17 13:56
     * 功能描述:获取最后启用的企业告警
     */
    @RequestMapping("/getLastUse")
    @ResponseBody
    public JsonResult getLastUse(@RequestBody EnterpriseLevelQuery enterpriseLevelQuery){
        List<EnterpriseLevel> lastUse = enterpriseLevelService.getLastUse(enterpriseLevelQuery.getEnterpriseCode(), enterpriseLevelQuery.getServerCode());
        return new JsonResult(lastUse);
    }
}
