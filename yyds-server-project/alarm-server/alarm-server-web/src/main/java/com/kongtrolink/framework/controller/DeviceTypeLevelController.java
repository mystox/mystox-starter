package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.query.DeviceTypeLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.DeviceTypeLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 16:54
 * @Description:
 */
@Controller
@RequestMapping("/deviceTypeLevelController")
public class DeviceTypeLevelController extends BaseController {

    @Autowired
    DeviceTypeLevelService typeLevelService;
    @Autowired
    EnterpriseLevelService enterpriseLevelService;
    @Autowired
    AlarmLevelService alarmLevelService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody DeviceTypeLevel deviceTypeLevel){
        boolean reprat = typeLevelService.isRepeat(deviceTypeLevel);
        if(reprat){
            return new JsonResult("设备等级已经存在!", false);
        }
        typeLevelService.add(deviceTypeLevel);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody DeviceTypeLevelQuery typeLevelQuery){
        boolean delete = typeLevelService.delete(typeLevelQuery.getId());
        if(delete){
            return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(@RequestBody DeviceTypeLevel deviceTypeLevel){
        boolean update = typeLevelService.update(deviceTypeLevel);
        if(update){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody DeviceTypeLevelQuery deviceTypeLevelQuery){
        List<DeviceTypeLevel> list = typeLevelService.list(deviceTypeLevelQuery);
        int count = typeLevelService.count(deviceTypeLevelQuery);
        ListResult<DeviceTypeLevel> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }
}
