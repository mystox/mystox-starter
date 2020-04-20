package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.entity.ShieldAlarm;
import com.kongtrolink.framework.scloud.entity.ShieldRule;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.query.ShieldAlarmQuery;
import com.kongtrolink.framework.scloud.query.ShieldRuleQuery;
import com.kongtrolink.framework.scloud.service.AlarmBusinessService;
import com.kongtrolink.framework.scloud.service.AlarmService;
import com.kongtrolink.framework.scloud.service.ShieldAlarmService;
import com.kongtrolink.framework.scloud.service.ShieldRuleService;
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
 * @Date: 2020/3/4 16:09
 * @Description:
 */
@Controller
@RequestMapping("/shieldRule")
public class ShieldRuleController extends BaseController{

    @Autowired
    ShieldRuleService ruleService;
    @Autowired
    AlarmService alarmService;
    @Autowired
    ShieldAlarmService shieldAlarmService;
    @Autowired
    AlarmBusinessService businessService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody ShieldRule shieldRule, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        Date curDate = new Date();
        User user = getUser(request);
        if(null != user) {
            shieldRule.setCreator(new FacadeView(user.getId(), user.getUsername()));
        }
        shieldRule.setUpdateTime(curDate);
        boolean add = ruleService.add(uniqueCode, shieldRule);
        if(add){
            return new JsonResult("添加成功");
        }
        return new JsonResult("添加失败");

    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody ShieldRule shieldRule){
        String uniqueCode = getUniqueCode();
        boolean delete = ruleService.delete(uniqueCode, shieldRule.getId());
        if(delete){
            return new JsonResult("删除成功", true);
        }
        return new JsonResult("删除失败", false);
    }

    @RequestMapping("/udate")
    @ResponseBody
    public JsonResult udate(@RequestBody ShieldRule shieldRule){
        String uniqueCode = getUniqueCode();
        shieldRule.setUpdateTime(new Date());
        boolean update = ruleService.add(uniqueCode, shieldRule);
        if(update){
            return new JsonResult(BaseConstant.OPERATE_UPDATE + BaseConstant.RESULT_SUCC);
        }
        return new JsonResult(BaseConstant.OPERATE_UPDATE + BaseConstant.RESULT_FAIL);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody ShieldRuleQuery ruleQuery){
        String uniqueCode = getUniqueCode();
        List<ShieldRule> list = ruleService.list(uniqueCode, ruleQuery);
        int count = ruleService.count(uniqueCode, ruleQuery);
        ListResult<ShieldRule> listResult = new ListResult<>(list, count);
        JsonResult jsonResult = new JsonResult(listResult);
        return jsonResult;
    }

    @RequestMapping("/detail")
    @ResponseBody
    public JsonResult detail(@RequestBody ShieldRuleQuery shieldRuleQuery){
        String uniqueCode = getUniqueCode();
        ShieldRule shieldRule = ruleService.get(uniqueCode, shieldRuleQuery.getId());
        if(null == shieldRule){
            return new JsonResult("规则不存在", false);
        }
        return new JsonResult(shieldRule);
    }

    @ResponseBody
    @RequestMapping("/getShieldAlarm")
    public JsonResult getShieldAlarm(@RequestBody ShieldAlarmQuery shieldAlarmQuery){
        String uniqueCode = getUniqueCode();
        AlarmBusinessQuery alarmBusinessQuery = new AlarmBusinessQuery();
        alarmBusinessQuery.setShield(true);
        alarmBusinessQuery.setShieldRuleId(shieldAlarmQuery.getRuleId());
        alarmBusinessQuery.setType(shieldAlarmQuery.getType());
        alarmBusinessQuery.setCurrentPage(shieldAlarmQuery.getCurrentPage());
        alarmBusinessQuery.setPageSize(shieldAlarmQuery.getPageSize());
        alarmBusinessQuery.setSkipSize(shieldAlarmQuery.getPageSize());
        alarmBusinessQuery.setSiteCodeList(shieldAlarmQuery.getSiteCodeList());
        List<AlarmBusiness> list = businessService.list(uniqueCode, alarmBusinessQuery);
        int count= businessService.count(uniqueCode, alarmBusinessQuery);
        ListResult<AlarmBusiness> listResult = new ListResult<>(list, count);
        JsonResult jsonResult = new JsonResult(listResult);
        return jsonResult;
    }

    @RequestMapping("/updateState")
    @ResponseBody
    public JsonResult updateState(@RequestBody ShieldRuleQuery ruleQuery){
        String uniqueCode = getUniqueCode();
        boolean result = ruleService.updateState(uniqueCode, ruleQuery.getId(), ruleQuery.getState());
        String operate = "禁用";
        if(null == ruleQuery.getState()){
            return new JsonResult("操作状态为空", false);
        }
        if(ruleQuery.getState()){
            operate = "启用";
        }
        if(result){
            return new JsonResult(operate + "成功", true);
        }
        return new JsonResult(operate + "失败", false);
    }

}
