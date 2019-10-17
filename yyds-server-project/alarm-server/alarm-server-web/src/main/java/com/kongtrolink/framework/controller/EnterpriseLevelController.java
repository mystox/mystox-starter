package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import com.kongtrolink.framework.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

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
    MqttService mqttService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody EnterpriseLevel enterpriseLevel){
        Date curTime = new Date();
        String code = StringUtil.getCode(curTime);
        enterpriseLevel.setUpdateTime(curTime);
        enterpriseLevel.setCode(code);
        enterpriseLevel.setState(Contant.FORBIT);
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

    /**
     * @auther: liudd
     * @date: 2019/9/25 14:02
     * 功能描述:获取企业和服务信息
     */
    @RequestMapping("/getUniqueServiceList")
    @ResponseBody
    public String getUniqueServiceList(){
        JSON uniqueService = mqttService.getEnterpriseMsgAll();
        return uniqueService.toJSONString();
    }

    @RequestMapping("/getDeviceTypeList")
    @ResponseBody
    public String getDeviceTypeList(String enterpriseCode, String serverCode){
        JSON deviceTypeList = mqttService.getDeviceTypeList(enterpriseCode, serverCode);
        return deviceTypeList.toJSONString();
    }
}
