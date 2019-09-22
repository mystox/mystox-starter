package com.kongtrolink.controller;

import com.kongtrolink.enttiy.AlarmLevel;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.query.AlarmLevelQuery;
import com.kongtrolink.service.AlarmLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:05
 * @Description:
 */
@Controller
@RequestMapping("/alarmLevelController/")
public class AlarmLevelController {

    @Autowired
    AlarmLevelService levelService;

    @RequestMapping("add")
    @ResponseBody
    public JsonResult add(AlarmLevel alarmLevel){
        boolean repeat = levelService.isRepeat(alarmLevel);
        if(repeat){
            return new JsonResult("告警等级已定义!", false);
        }
        levelService.save(alarmLevel);
        return new JsonResult("添加成功", true);
    }

    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(AlarmLevelQuery alarmLevelQuery){
        boolean delete = levelService.delete(alarmLevelQuery.getId());
        if(delete){
            return new JsonResult("删除成功", true);
        }
        return new JsonResult("删除失败", false);
    }

    @RequestMapping("update")
    @ResponseBody
    public JsonResult update(AlarmLevel alarmLevel){
        boolean repeat = levelService.isRepeat(alarmLevel);
        if(repeat){
            return new JsonResult("告警等级已定义!", false);
        }
        boolean update = levelService.update(alarmLevel);
        if(update){
            return new JsonResult("修改成功", true);
        }
        return new JsonResult("修改失败", false);
    }

    @RequestMapping("list")
    @ResponseBody
    public JsonResult list(AlarmLevelQuery levelQuery){
        List<AlarmLevel> list = levelService.list(levelQuery);
        int count = levelService.count(levelQuery);
        ListResult<AlarmLevel> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }
}
