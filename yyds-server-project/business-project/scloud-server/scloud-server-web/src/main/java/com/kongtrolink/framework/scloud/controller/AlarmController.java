package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.AlarmService;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 14:53
 * @Description:
 */
@Controller
@RequestMapping("/alarm")
public class AlarmController {

    @Autowired
    AlarmService alarmService;
    @Autowired
    SiteService siteService;
    @Autowired
    DeviceService deviceService;

    /**
     * @auther: liudd
     * @date: 2020/2/26 14:59
     * 功能描述:列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        //1，获取用户管辖范围设备id列表
        List<String> siteIdList = alarmQuery.getSiteIdList();
        List<DeviceEntity> deviceEntities = deviceService.listBySiteIdList(siteIdList);
        List<String> deviceCodeList = deviceService.list2CodeList(deviceEntities);
        alarmQuery.setDeviceCodeList(deviceCodeList);
        //2，将设备id列表和其他参数传递过去

        return null;
    }

    /**
     * @auther: liudd
     * @date: 2020/2/26 15:20
     * 功能描述:手动消除告警
     */
    @RequestMapping("/reslove")
    @ResponseBody
    public JsonResult reslove(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        //调用中台告警模块，手动消除告警
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2020/2/26 15:21
     * 功能描述:关注/取消关注告警
     */
    @RequestMapping("/focus")
    @ResponseBody
    public JsonResult focus(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        //调用中台告警模块，关注/取消关注告警
        return null;
    }
}
