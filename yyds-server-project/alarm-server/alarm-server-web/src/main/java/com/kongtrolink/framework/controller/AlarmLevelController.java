package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:05
 * @Description:
 */
@Controller
@RequestMapping("/alarmLevelController/")
public class AlarmLevelController extends BaseController {

    @Autowired
    AlarmLevelService levelService;

    @RequestMapping("add")
    @ResponseBody
    public JsonResult add(@RequestBody  AlarmLevel alarmLevel){
        alarmLevel.setGenerate(Contant.MANUAL);
        boolean repeat = levelService.isRepeat(alarmLevel);
        if(repeat){
            return new JsonResult("告警等级已定义!", false);
        }
        levelService.save(alarmLevel);
        return new JsonResult("添加成功", true);
    }

    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(@RequestBody  AlarmLevelQuery alarmLevelQuery){
        boolean delete = levelService.delete(alarmLevelQuery.getId());
        if(delete){
            return new JsonResult("删除成功", true);
        }
        return new JsonResult("删除失败", false);
    }

    @RequestMapping("update")
    @ResponseBody
    public JsonResult update(@RequestBody  AlarmLevel alarmLevel){
        alarmLevel.setGenerate(Contant.MANUAL);
//        boolean repeat = levelService.isRepeat(alarmLevel);
//        if(repeat){
//            return new JsonResult("告警等级已定义!", false);
//        }
        alarmLevel.setUpdateTime(new Date());
        boolean update = levelService.update(alarmLevel);
        if(update){
            return new JsonResult("修改成功", true);
        }
        return new JsonResult("修改失败", false);
    }

    @RequestMapping("list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmLevelQuery levelQuery){
        List<AlarmLevel> list = levelService.list(levelQuery);
        int count = levelService.count(levelQuery);
        ListResult<AlarmLevel> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    @RequestMapping("/getByEntDevCodeList")
    @ResponseBody
    public JsonResult getByEntDevCodeList(@RequestBody AlarmLevelQuery alarmLevelQuery){
        List<AlarmLevel> byEntDevCodeList = levelService.getByEntDevCodeList(Arrays.asList(alarmLevelQuery.getEntDevCode()));
        return new JsonResult(byEntDevCodeList);
    }
}
