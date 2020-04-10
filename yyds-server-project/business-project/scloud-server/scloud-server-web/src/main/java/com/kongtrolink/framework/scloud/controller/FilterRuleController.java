package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.entity.FilterRule;
import com.kongtrolink.framework.scloud.query.FilterRuleQuery;
import com.kongtrolink.framework.scloud.service.FilterRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 14:59
 * @Description:
 */
@Controller
@RequestMapping("/filterRule")
public class FilterRuleController extends BaseController {

    @Autowired
    FilterRuleService ruleService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody FilterRule filterRule){
        String uniqueCode = getUniqueCode();
        User user = getUser();
        if(null != user){
            filterRule.setCreator(new FacadeView(user.getId(), user.getUsername()));
        }
        Date curDate = new Date();
        filterRule.setUpdateTime(curDate);
        boolean result = ruleService.add(uniqueCode, filterRule);
        if(result){
            return new JsonResult("添加成功", true);
        }
        return new JsonResult("添加失败", false);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody FilterRule filterRule){
        String uniqueCode = getUniqueCode();
        boolean result = ruleService.delete(uniqueCode, filterRule.getId());
        if(result){
            return new JsonResult("删除成功", true);
        }
        return new JsonResult("删除失败", false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult udpate(@RequestBody FilterRule filterRule){
        String uniqueCode = getUniqueCode();
        Date updateTime = new Date();
        filterRule.setUpdateTime(updateTime);
        boolean result = ruleService.update(uniqueCode, filterRule);
        if(result){
            return new JsonResult("修改成功", true);
        }
        return new JsonResult("修改失败", false);
    }

    @ResponseBody
    @RequestMapping("/list")
    public JsonResult list(@RequestBody FilterRuleQuery ruleQuery){
        String uniqueCode = getUniqueCode();
        List<FilterRule> list = ruleService.list(uniqueCode, ruleQuery);
        int count = ruleService.count(uniqueCode, ruleQuery);
        ListResult<FilterRule> listResult = new ListResult<>(list, count);
        JsonResult jsonResult = new JsonResult(listResult);
        return jsonResult;
    }

    /**
     * @auther: liudd
     * @date: 2020/3/5 15:22
     * 功能描述:启用，禁用。一个用户只能有一个过滤规则启用状态
     */
    @ResponseBody
    @RequestMapping("/updateState")
    public JsonResult updateState(@RequestBody FilterRuleQuery ruleQuery){
        String uniqueCode = getUniqueCode();
        ruleQuery.setUpdateTime(new Date());
        if(null == ruleQuery.getState()){
            return new JsonResult("启用状态不能为空", false);
        }
        User user = getUser();
        if(null != user){
            ruleQuery.setCreatorId(user.getId());
        }
        boolean result = ruleService.updateState(uniqueCode, ruleQuery);
        String operate = "禁用";
        if(ruleQuery.getState()){
            operate = "启用";
        }
        if(result){
            return new JsonResult(operate + "成功", true);
        }
        return new JsonResult(operate + "失败", false);
    }
}
