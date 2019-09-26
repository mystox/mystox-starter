package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.mqtt.MqttService;
import com.kongtrolink.framework.query.DeviceTypeLevelQuery;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.DeviceTypeLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
public class DeviceTypeLevelController {

    @Autowired
    DeviceTypeLevelService typeLevelService;
    @Autowired
    MqttService mqttService;
    @Autowired
    EnterpriseLevelService enterpriseLevelService;
    @Autowired
    AlarmLevelService alarmLevelService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(DeviceTypeLevel deviceTypeLevel){
        //liuddtodo 添加前做等级重复等判定
        boolean reprat = typeLevelService.isReprat(deviceTypeLevel);
        if(reprat){
            return new JsonResult("设备等级以存在!", false);
        }
        typeLevelService.add(deviceTypeLevel);
        String level = deviceTypeLevel.getLevel();
        EnterpriseLevelQuery enterpriseLevelQuery = new EnterpriseLevelQuery();
        enterpriseLevelQuery.setUniqueCode(deviceTypeLevel.getUniqueCode());
        enterpriseLevelQuery.setService(deviceTypeLevel.getService());
        enterpriseLevelQuery.setLevel(level);
        EnterpriseLevel maxLevelSinceLevel = enterpriseLevelService.getMaxLevelSinceLevel(enterpriseLevelQuery);
        if(null != maxLevelSinceLevel){
            AlarmLevel alarmLevel = alarmLevelService.createAlarmLevel(maxLevelSinceLevel, deviceTypeLevel);
            if(null != alarmLevel){
                alarmLevelService.save(alarmLevel);
            }
        }

        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(DeviceTypeLevelQuery typeLevelQuery){
        boolean delete = typeLevelService.delete(typeLevelQuery.getId());
        if(delete){
            return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_DELETE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(DeviceTypeLevel deviceTypeLevel){
        //liuddtodo 修改前做等级重复等判定
        boolean reprat = typeLevelService.isReprat(deviceTypeLevel);
        if(reprat){
            return new JsonResult("设备等级以存在!", false);
        }
        boolean update = typeLevelService.update(deviceTypeLevel);
        if(update){
            String level = deviceTypeLevel.getLevel();
            EnterpriseLevelQuery enterpriseLevelQuery = new EnterpriseLevelQuery();
            enterpriseLevelQuery.setUniqueCode(deviceTypeLevel.getUniqueCode());
            enterpriseLevelQuery.setService(deviceTypeLevel.getService());
            enterpriseLevelQuery.setLevel(level);
            EnterpriseLevel maxLevelSinceLevel = enterpriseLevelService.getMaxLevelSinceLevel(enterpriseLevelQuery);
            if(null != maxLevelSinceLevel){
                AlarmLevel alarmLevel = alarmLevelService.createAlarmLevel(maxLevelSinceLevel, deviceTypeLevel);
                if(null != alarmLevel){
                    alarmLevelService.save(alarmLevel);
                }
            }
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(DeviceTypeLevelQuery deviceTypeLevelQuery){
        List<DeviceTypeLevel> list = typeLevelService.list(deviceTypeLevelQuery);
        int count = typeLevelService.count(deviceTypeLevelQuery);
        ListResult<DeviceTypeLevel> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    /**
     * @auther: liudd
     * @date: 2019/9/25 14:03
     * 功能描述:获取设备信号信息
     * 参数：uniqueCode, service
     */
    @RequestMapping("/getDeviceTypeList")
    @ResponseBody
    public String getDeviceTypeList(DeviceTypeLevelQuery deviceTypeLevelQuery){
        JSON deviceType = mqttService.getDeviceType(deviceTypeLevelQuery.getUniqueCode(), deviceTypeLevelQuery.getService());
        return deviceType.toJSONString();
    }
}
