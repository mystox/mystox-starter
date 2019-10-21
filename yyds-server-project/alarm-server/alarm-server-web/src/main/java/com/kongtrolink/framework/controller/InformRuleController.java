package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
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
public class InformRuleController extends BaseController {

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

        User user = new User();
        user.setId("admin");
        user.setName("超级管理员");
        Date curDate = new Date();
        informRule.setOperator(new FacadeView(user.getId(), user.getName()));
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
        if(null != sourceRule && Contant.SYSTEM.equals(sourceRule.getType())){
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
        if(null != informRule && Contant.SYSTEM.equals(informRule.getType())){
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
    @RequestMapping(value = "/list")
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
    @RequestMapping(value = "/checkName")
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
    @RequestMapping(value = "/updateStatus")
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
            //删除原来数据
            ruleUserService.deleteByRuleId(ruleQuery.get_id());
            ruleUserService.deleteByUserIds(userIds);
            List<String> usernames = ruleQuery.getUsernames();
            Date curTime = new Date();
            for(int i=0; i<userIds.size(); i++){
                InformRuleUser ruleUser = new InformRuleUser();
                ruleUser.setUpdateTime(curTime);
                ruleUser.setInformRule(new FacadeView(ruleQuery.get_id(), ruleQuery.getName()));
                ruleUser.setUser(new FacadeView(userIds.get(i), usernames.get(i)));
                ruleUserService.save(ruleUser);
            }
        }
        return new JsonResult(Contant.OPE_AUTH + Contant.RESULT_SUC, true);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/17 16:36
     * 功能描述:获取企业和服务下所有用户
     */
    @RequestMapping("/getUserList")
    @ResponseBody
    public JsonResult getUserList(@RequestBody InformRuleQuery informRuleQuery){
        String enterpriseCode = informRuleQuery.getEnterpriseCode();
        String serverCode = informRuleQuery.getServerCode();
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId("user0001");
        user1.setUsername("dadd");
        user1.setName("大冬冬");
        user1.setPhone("15267071111");
        user1.setEmail("152222QQ.com");
        userList.add(user1);

        User user2 = new User();
        user2.setId("user0002");
        user2.setUsername("dagg");
        user2.setName("大刚哥");
        user2.setPhone("15267072222");
        user2.setEmail("152222QQ.com");
        userList.add(user2);
        return new JsonResult(userList);
    }
}
