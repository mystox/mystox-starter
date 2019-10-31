package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.enttiy.MsgTemplate;
import com.kongtrolink.framework.query.MsgTemplateQuery;
import com.kongtrolink.framework.service.InformRuleService;
import com.kongtrolink.framework.service.MsgTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/23 15:24
 * @Description:
 */
@Controller
@RequestMapping("/msgTemplateController")
public class MsgTemplateController {

    @Autowired
    MsgTemplateService templateService;
    @Autowired
    InformRuleService ruleService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody MsgTemplate msgTemplate){
        msgTemplate.setUpdateTime(new Date());
        String name = msgTemplate.getName();
        msgTemplate.setTemplateType(Contant.MANUAL);
        MsgTemplate byName = templateService.getByName(msgTemplate.getEnterpriseCode(), msgTemplate.getServerCode(), name);
        if(null != byName){
            return new JsonResult(Contant.OPE_ADD  + Contant.RESULT_FAIL+", 模板名称：" + name + "已存在", false);
        }
        boolean result = templateService.save(msgTemplate);
        if(result){
            return new JsonResult(Contant.OPE_ADD  + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_ADD  + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody MsgTemplateQuery msgTemplateQuery){
        String id = msgTemplateQuery.get_id();
        MsgTemplate msgTemplate = templateService.get(id);
        if(null != msgTemplate){
            List<InformRule> informRuleList = ruleService.getByTemplateIdAndType(id, msgTemplate.getType());
            int size = informRuleList.size();
            if(size >0 ){
                return new JsonResult(Contant.OPE_DELETE  + Contant.RESULT_FAIL+", 还有 " + size + "条通知规则使用该模板", false);
            }
        }
        boolean delete = templateService.delete(id);
        if(delete){
            return new JsonResult(Contant.OPE_DELETE  + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_DELETE  + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(@RequestBody MsgTemplate msgTemplate){
        msgTemplate.setTemplateType(Contant.MANUAL);
        MsgTemplate byName = templateService.getByName(msgTemplate.getEnterpriseCode(), msgTemplate.getServerCode(), msgTemplate.getName());
        if(null != byName && !byName.get_id().equals(msgTemplate.get_id())){
            return new JsonResult(Contant.OPE_UPDATE  + Contant.RESULT_FAIL+", 模板名称：" + msgTemplate.getName() + "已存在", false);
        }
        msgTemplate.setUpdateTime(new Date());
        boolean result = templateService.update(msgTemplate);
        if(result){
            //修改所有启用该模板的告警通知规则
            List<InformRule> informRuleList = ruleService.getByTemplateIdAndType(msgTemplate.get_id(), msgTemplate.getType());
            for(InformRule informRule : informRuleList){
                informRule.initTemplate(msgTemplate);
                ruleService.update(informRule);
            }
            return new JsonResult(Contant.OPE_UPDATE  + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE  + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody MsgTemplateQuery msgTemplateQuery){
        List<MsgTemplate> list = templateService.list(msgTemplateQuery);
        int count = templateService.count(msgTemplateQuery);
        ListResult<MsgTemplate> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

}
