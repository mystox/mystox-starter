package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.entity.RedefineAlarm;
import com.kongtrolink.framework.scloud.entity.RedefineRule;
import com.kongtrolink.framework.scloud.query.RedefineAlarmQuery;
import com.kongtrolink.framework.scloud.query.RedefineRuleQuery;
import com.kongtrolink.framework.scloud.service.RedefineAlarmService;
import com.kongtrolink.framework.scloud.service.RedefineRuleService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 14:04
 * @Description:
 */
@Controller
@RequestMapping("/redefineRule")
public class RedefineRuleController extends ExportController {

    @Autowired
    RedefineRuleService ruleService;
    @Autowired
    RedefineAlarmService redefineAlarmService;

    @RequestMapping("/add")
    @ResponseBody
    public synchronized JsonResult add(@RequestBody RedefineRule redefineRule, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        //先根据名称判定一次
        String name = redefineRule.getName();
        RedefineRule byName = ruleService.getByName(uniqueCode, name);
        if(null != byName){
            return new JsonResult("规则名称：" + name + "已存在", false);
        }
        User user = getUser(request);
        FacadeView creator = new FacadeView();
        if(null != user){
            creator.setStrId(user.getId());
            creator.setName(user.getName());
        }else{
            creator.setStrId("root");
            creator.setName("root");
        }
        redefineRule.setUpdateTime(new Date());
        boolean result = ruleService.add(uniqueCode, redefineRule);
        if(result){
            return new JsonResult(BaseConstant.OPERATE_ADD + BaseConstant.RESULT_SUCC, true);
        }
        return new JsonResult(BaseConstant.OPERATE_ADD + BaseConstant.RESULT_FAIL, true);
    }

    @RequestMapping("/getByName")
    @ResponseBody
    public JsonResult getByName(@RequestBody RedefineRule redefineRule){
        String uniqueCode = getUniqueCode();
        String name = redefineRule.getName();
        RedefineRule byName = ruleService.getByName(uniqueCode, name);
        if(null != byName){
            return new JsonResult("规则名称：" + name + "已存在", false);
        }
        return new JsonResult("规则名称：" + name + "可以使用", true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody RedefineRule redefineRule){
        String uniqueCode = getUniqueCode();
        String id = redefineRule.getId();
        boolean result = ruleService.delete(uniqueCode, id);
        if(result){
            return new JsonResult(BaseConstant.OPERATE_DELETE + BaseConstant.RESULT_SUCC, true);
        }
        return new JsonResult(BaseConstant.OPERATE_DELETE + BaseConstant.RESULT_FAIL, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public synchronized JsonResult update(@RequestBody RedefineRule redefineRule){
        String uniqueCode = getUniqueCode();
        //先根据名称判定一次
        String name = redefineRule.getName();
        RedefineRule byName = ruleService.getByName(uniqueCode, name);
        if(null != byName && !byName.getId().equals(redefineRule.getId())){
            return new JsonResult("规则名称：" + name + "已存在", false);
        }
        redefineRule.setUpdateTime(new Date());
        boolean update = ruleService.update(uniqueCode, redefineRule);
        if(update){
            return new JsonResult(BaseConstant.OPERATE_UPDATE + BaseConstant.RESULT_SUCC, true);
        }
        return new JsonResult(BaseConstant.OPERATE_UPDATE + BaseConstant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody RedefineRuleQuery ruleQuery){
        String uniqueCode = getUniqueCode();
        List<RedefineRule> list = ruleService.list(uniqueCode, ruleQuery);
        int count = ruleService.count(uniqueCode, ruleQuery);
        ListResult<RedefineRule> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/13 14:51
     * 功能描述:根据重定义规则id，获取被重定义的告警列表
     */
    @RequestMapping("/listRedefineAlarm")
    @ResponseBody
    public JsonResult listRedefineAlarm(@RequestBody RedefineAlarmQuery alarmQuery){
        String uniqueCode = getUniqueCode();
        List<RedefineAlarm> list = redefineAlarmService.list(uniqueCode, alarmQuery);
        int count = redefineAlarmService.count(uniqueCode, alarmQuery);
        ListResult<RedefineAlarm> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    @RequestMapping("/updateState")
    @ResponseBody
    public synchronized JsonResult updateState(@RequestBody RedefineRuleQuery redefineRuleQuery){
        String uniqueCode = getUniqueCode();
        redefineRuleQuery.setUpdateTime(new Date());
        String id = redefineRuleQuery.getId();
        if(StringUtil.isNUll(id)){
            return new JsonResult("重定义规则id为空", false);
        }
        Boolean enabled = redefineRuleQuery.getEnabled();
        if(null == enabled){
            return new JsonResult("重定义状态为空", false);
        }
        boolean result = ruleService.updateState(uniqueCode, redefineRuleQuery);
        String operate = BaseConstant.OPERATE_USE;
        if(!redefineRuleQuery.getEnabled()){
            operate = BaseConstant.OPERATE_FORBIT;
        }
        if(result){
            return new JsonResult(operate + BaseConstant.RESULT_SUCC, true);
        }
        return new JsonResult(operate + BaseConstant.RESULT_FAIL, false);
    }
}
