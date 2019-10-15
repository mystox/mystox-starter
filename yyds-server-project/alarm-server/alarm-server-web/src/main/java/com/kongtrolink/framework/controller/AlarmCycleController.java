package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.query.AlarmCycleQuery;
import com.kongtrolink.framework.service.AlarmCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:17
 * @Description:
 * 如果没有启用的自定义周期，默认已消除告警为历史告警
 */
@Controller
@RequestMapping("/alarmCycleController")
public class AlarmCycleController extends BaseController{

    @Autowired
    AlarmCycleService cycleService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(AlarmCycle alarmCycle){
        cycleService.save(alarmCycle);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(AlarmCycleQuery cycleQuery){
        boolean delete = cycleService.delete(cycleQuery.getId());
        if(delete){
            return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(AlarmCycle alarmCycle){
        boolean update = cycleService.update(alarmCycle);
        if(update){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(AlarmCycleQuery alarmCycleQuery){
        List<AlarmCycle> list = cycleService.list(alarmCycleQuery);
        int count = cycleService.count(alarmCycleQuery);
        ListResult<AlarmCycle> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    @RequestMapping("/updateState")
    @ResponseBody
    public JsonResult updateState(AlarmCycleQuery cycleQuery){
        boolean result = cycleService.updateState(cycleQuery);
        String state = cycleQuery.getState();
        if(result){
            return new JsonResult(state + Contant.RESULT_SUC, true);
        }
        return new JsonResult(state + Contant.RESULT_FAIL, true);
    }
}
