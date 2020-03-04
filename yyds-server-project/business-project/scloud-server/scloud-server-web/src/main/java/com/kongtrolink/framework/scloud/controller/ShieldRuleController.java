package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.entity.ShieldRule;
import com.kongtrolink.framework.scloud.query.ShieldRuleQuery;
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

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody ShieldRule shieldRule, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        User user = getUser(request);
        Date curDate = new Date();
        shieldRule.setCreator(new FacadeView(user.getId(), user.getUsername()));
        shieldRule.setUpdateTime(curDate);
        boolean add = ruleService.add(uniqueCode, shieldRule);
        if(add){
            return new JsonResult("添加成功");
        }
        return new JsonResult("添加失败");

    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody ShieldRule shieldRule, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        int delete = ruleService.delete(uniqueCode, shieldRule.getId());
        if(delete > 0){
            return new JsonResult("删除成功", true);
        }
        return new JsonResult("删除失败", false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody ShieldRuleQuery ruleQuery, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        List<ShieldRule> list = ruleService.list(uniqueCode, ruleQuery);
        int count = ruleService.count(uniqueCode, ruleQuery);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(list);
        jsonResult.setCount(count);
        return jsonResult;
    }

    @RequestMapping("/updateState")
    @ResponseBody
    public JsonResult updateState(ShieldRuleQuery ruleQuery){
        String uniqueCode = getUniqueCode();
        boolean result = ruleService.updateState(uniqueCode, ruleQuery.getId(), ruleQuery.getState());
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
