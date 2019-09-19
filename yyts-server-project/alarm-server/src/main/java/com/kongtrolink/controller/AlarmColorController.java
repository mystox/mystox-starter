package com.kongtrolink.controller;

import com.kongtrolink.base.Contant;
import com.kongtrolink.enttiy.AlarmColor;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.query.AlarmColorQuery;
import com.kongtrolink.service.AlarmColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/12 10:45
 * @Description:
 */
@Controller
@RequestMapping("/alarmColorController/")
public class AlarmColorController {

    @Autowired
    AlarmColorService colorService;

    @RequestMapping("add")
    @ResponseBody
    public JsonResult add(AlarmColor alarmColor){
        colorService.add(alarmColor);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(AlarmColorQuery colorQuery){
        boolean delete = colorService.delete(colorQuery.getId());
        if(delete){
            return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("update")
    @ResponseBody
    public JsonResult update(AlarmColor alarmColor){
        boolean update = colorService.update(alarmColor);
        if(update){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("list")
    @ResponseBody
    public JsonResult list(AlarmColorQuery colorQuery){
        List<AlarmColor> list = colorService.list(colorQuery);
        int count = colorService.count(colorQuery);
        return new JsonResult(new ListResult<>(list, count));
    }
}
