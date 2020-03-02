package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.query.AlarmCycleQuery;
import com.kongtrolink.framework.service.AlarmCycleService;
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
 * @Date: 2019/9/21 11:17
 * @Description:
 * 如果没有启用的自定义周期，默认已消除告警为历史告警
 */
@Controller
@RequestMapping("/alarmCycleController")
public class AlarmCycleController extends BaseController {

    @Autowired
    AlarmCycleService cycleService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody AlarmCycle alarmCycle){
        alarmCycle.setUpdateTime(new Date());
        alarmCycle.setState(Contant.FORBIT);
        alarmCycle.setCycleType(Contant.MANUAL);
        alarmCycle.initEnterpirseServer();
        cycleService.save(alarmCycle);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody AlarmCycleQuery cycleQuery){
        boolean delete = cycleService.delete(cycleQuery.getId());
        if(delete){
            return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(@RequestBody AlarmCycle alarmCycle, HttpServletRequest request){
        alarmCycle.setUpdateTime(new Date());
        alarmCycle.setCycleType(Contant.MANUAL);
        alarmCycle.setUpdateTime(new Date());
        alarmCycle.initEnterpirseServer();
        User user = getUser(request);
        if(null != user){
            alarmCycle.setOperator(new FacadeView(user.getId(), user.getUsername()));
        }
        boolean update = cycleService.update(alarmCycle);
        if(update){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmCycleQuery alarmCycleQuery){
        //判定企业默认告警周期是否存在
        cycleService.handleUniqueDefault(alarmCycleQuery.getEnterpriseCode(), alarmCycleQuery.getServerCode());
        List<AlarmCycle> list = cycleService.list(alarmCycleQuery);
        int count = cycleService.count(alarmCycleQuery);
        ListResult<AlarmCycle> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    @RequestMapping("/updateState")
    @ResponseBody
    public JsonResult updateState(@RequestBody AlarmCycleQuery cycleQuery, HttpServletRequest request){
        String state = cycleQuery.getState();
        String cycleType = cycleQuery.getCycleType();
        if(Contant.FORBIT.equals(state) && cycleType.equals(Contant.SYSTEM)){
            return new JsonResult("企业默认告警周期不能手动禁用", false);
        }
        User user = getUser(request);
        if(null != user){
            cycleQuery.setOperator(new FacadeView(user.getId(), user.getUsername()));
            cycleQuery.setOperatorName(user.getName());
        }
        boolean result = cycleService.updateState(cycleQuery);
        if(result){
            return new JsonResult(state + Contant.RESULT_SUC, true);
        }
        return new JsonResult(state + Contant.RESULT_FAIL, true);
    }

    /**
     * @auther: liudd
     * @date: 2019/12/27 14:58
     * 功能描述:判定规则名称是否已存在，用于添加规则或者修改规则
     */
    @RequestMapping
    @ResponseBody
    public JsonResult checkName(@RequestBody AlarmCycleQuery cycleQuery){
        AlarmCycle byName = cycleService.getByName(cycleQuery.getEnterpriseCode(), cycleQuery.getServerCode(), cycleQuery.getName());
        if(null == byName){
            return new JsonResult("该规则名称可以使用", true);
        }
        return new JsonResult("该规则名称已存在", false);
    }
}
