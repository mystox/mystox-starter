package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.EnumLevelName;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.mqtt.InnerMqttService;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 11:01
 * @Description:
 */
@Controller
@RequestMapping("/enterpriseLevelController")
public class EnterpriseLevelController {

    @Autowired
    EnterpriseLevelService enterpriseLevelService;
    @Autowired
    InnerMqttService mqttService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(EnterpriseLevel enterpriseLevel){
        //判定告警等级是否重复
        boolean repeat = enterpriseLevelService.isRepeat(enterpriseLevel);
        if(repeat){
            return new JsonResult("该等级已存在!", false);
        }
        String levelName = enterpriseLevel.getLevelName();
        if(StringUtil.isNUll(levelName)){
            levelName = EnumLevelName.getNameByLevel(enterpriseLevel.getLevel());
        }
        enterpriseLevel.setLevelName(levelName);
        enterpriseLevelService.add(enterpriseLevel);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(EnterpriseLevelQuery enterpriseLevelQuery){
        boolean delete = enterpriseLevelService.delete(enterpriseLevelQuery.getId());
        if(delete){
            return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(EnterpriseLevel enterpriseLevel){
        //判定告警等级是否重复
        boolean repeat = enterpriseLevelService.isRepeat(enterpriseLevel);
        if(repeat){
            return new JsonResult("该等级已存在!", false);
        }
        String levelName = enterpriseLevel.getLevelName();
        if(StringUtil.isNUll(levelName)){
            levelName = EnumLevelName.getNameByLevel(enterpriseLevel.getLevel());
        }
        enterpriseLevel.setLevelName(levelName);
        boolean update = enterpriseLevelService.update(enterpriseLevel);
        if(update){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(EnterpriseLevelQuery levelQuery){
        List<EnterpriseLevel> list = enterpriseLevelService.list(levelQuery);
        int count = enterpriseLevelService.count(levelQuery);
        ListResult<EnterpriseLevel> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    @RequestMapping("/updateDefault")
    @ResponseBody
    public JsonResult updateDefault(EnterpriseLevelQuery enterpriseLevelQuery){
        boolean result = enterpriseLevelService.updateDefault(enterpriseLevelQuery);
        if(result){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, true);
    }

    /**
     * @auther: liudd
     * @date: 2019/9/25 14:02
     * 功能描述:获取企业和服务信息
     */
    @RequestMapping("/getUniqueServiceList")
    @ResponseBody
    public String getUniqueServiceList(){
        JSON uniqueService = mqttService.getUniqueService();
        return uniqueService.toJSONString();
    }
}
