package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.scloud.query.AlarmLevelQuery;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.AlarmLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: liudd
 * @Date: 2020/4/3 08:29
 * @Description:
 */
@Controller
@RequestMapping("/alarmLevel")
public class AlarmLevelController extends BaseController {

    @Autowired
    AlarmLevelService alarmLevelService;

    /**
     * @auther: liudd
     * @date: 2020/2/26 14:59
     * 功能描述:列表
     */
    @RequestMapping("/getLastUse")
    @ResponseBody
    public JSONObject getLastUse(@RequestBody AlarmLevelQuery alarmLevelQuery){
        JSONObject lastUse = alarmLevelService.getLastUse(alarmLevelQuery);
        return lastUse;
    }

    @RequestMapping("/getDeviceTypeList")
    @ResponseBody
    public JSONObject getDeviceTypeList(@RequestBody AlarmLevelQuery alarmLevelQuery){
        return alarmLevelService.getDeviceTypeList(alarmLevelQuery);
    }

}
