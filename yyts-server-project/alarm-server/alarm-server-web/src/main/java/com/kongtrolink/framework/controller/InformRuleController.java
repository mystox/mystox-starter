package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.User;
import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.enttiy.InformRuleUser;
import com.kongtrolink.framework.query.InformRuleQuery;
import com.kongtrolink.framework.service.InformRuleService;
import com.kongtrolink.framework.service.InformRuleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 09:41
 * @Description:告警通知规则
 */
@Controller
@RequestMapping("/deliverController")
public class InformRuleController extends BaseController{

    @Autowired
    InformRuleService ruleService;
    @Autowired
    InformRuleUserService ruleUserService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    JsonResult add(@RequestBody InformRule informRule, HttpServletRequest request ){
        //后端判定规则名称是否存在
        String name = informRule.getName();
        InformRule byName = ruleService.getByName(name);
        if(null != byName){
            return new JsonResult("规则名称"+name+"已存在！", false);
        }
        User user = getUser(request);
        Date curDate = new Date();
        informRule.setCreator(new FacadeView(user.getId(), user.getName()));
        informRule.setUpdateTime(curDate);
        informRule.initDateInt();
        informRule.setType(Contant.MANUAL);
        boolean result = ruleService.save(informRule);
        if(result){
            return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_FAIL, false);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody JsonResult delete(@RequestBody InformRule informRule) {
        InformRule sourceRule = ruleService.get(informRule.get_id());
        if(null != sourceRule && Contant.MANUAL.equals(sourceRule.getType())){
            return new JsonResult("默认规则不允许删除", false);
        }
        boolean result = ruleService.delete(informRule.get_id());
        if (result) {
            //删除使用该规则的用户
            ruleUserService.deleteByRuleId(informRule.get_id());
            return new JsonResult(Contant.DELETED + Contant.RESULT_SUC, true);
        }else {
            return new JsonResult(Contant.DELETED + Contant.RESULT_FAIL, false);
        }
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 18:56
     * 功能描述:修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody JsonResult update(@RequestBody InformRule informRule) {
        informRule.initDateInt();
        if(null != informRule && Contant.MANUAL.equals(informRule.getType())){
            return new JsonResult("默认规则不允许修改", false);
        }
        informRule.setUpdateTime(new Date());
        boolean result = ruleService.update(informRule);
        if (result) {
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }else {
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
        }
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 18:59
     * 功能描述:获取列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public @ResponseBody JsonResult list(@RequestBody InformRuleQuery ruleQuery) {
        List<InformRule> list = ruleService.list(ruleQuery);
        int count = ruleService.count(ruleQuery);
        ListResult<InformRule> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 19:01
     * 功能描述:判定规则名称是否已在使用
     */
    @RequestMapping(value = "/checkName", method = RequestMethod.POST)
    public @ResponseBody JsonResult checkName(@RequestBody InformRuleQuery ruleQuery){
        InformRule informRule = ruleService.getByName(ruleQuery.getName());
        if(null != informRule){
            return new JsonResult("规则名称已存在", false);
        }
        return new JsonResult("规则名称可用", true);
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 19:07
     * 功能描述:启用或者禁用规则
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public @ResponseBody JsonResult enabled(@RequestBody InformRuleQuery ruleQuery) {
        boolean result = ruleService.updateStatus(ruleQuery.get_id(), ruleQuery.getStatus());
        if(result){
            return new JsonResult(ruleQuery.getStatus() + Contant.RESULT_SUC, true);
        }else{
            return new JsonResult(ruleQuery + Contant.RESULT_FAIL, false);
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/10/11 16:22
     * 功能描述:获取启用该通知规则的用户
     */
    @RequestMapping("/getUseList")
    public @ResponseBody JsonResult getUseList(@RequestBody InformRuleQuery ruleQuery){
        String id = ruleQuery.get_id();
        List<InformRuleUser> userList = ruleUserService.getByRuleId(id);
        return new JsonResult(userList);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/11 16:19
     * 功能描述:授权
     */
    @RequestMapping("/authUser")
    public @ResponseBody JsonResult authUser(@RequestBody InformRuleQuery ruleQuery){
        List<String> userIds = ruleQuery.getUserIds();
        if(null != userIds && userIds.size()>0){
            List<String> usernames = ruleQuery.getUsernames();
            Date curTime = new Date();
            List<InformRuleUser> ruleUserList = new ArrayList<>();
            for(int i=0; i<userIds.size(); i++){
                InformRuleUser ruleUser = new InformRuleUser();
                ruleUser.setUpdateTime(curTime);
                ruleUser.setInformRule(new FacadeView(ruleQuery.get_id(), ruleQuery.getName()));
                ruleUser.setUser(new FacadeView(userIds.get(i), usernames.get(i)));
                ruleUserList.add(ruleUser);
            }
            //删除原来数据
            boolean result = ruleUserService.deleteByRuleId(ruleQuery.get_id());
            if(result){
                ruleUserService.deleteByUserIds(userIds);
                ruleUserService.save(ruleUserList);
            }
        }
        return new JsonResult(Contant.OPE_AUTH + Contant.RESULT_SUC, true);
    }
}
