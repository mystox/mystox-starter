package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 14:53
 * @Description:
 */
@Controller
@RequestMapping("/alarm")
public class AlarmController extends ExportController{

    @Autowired
    AlarmService alarmService;
    @Autowired
    SiteService siteService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    MqttOpera mqttOpera;
    @Autowired
    AlarmFocusService alarmFocusService;
    @Autowired
    FilterRuleService filterRuleService;

    /**
     * @auther: liudd
     * @date: 2020/2/26 14:59
     * 功能描述:列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        try {
            //具体查询历史还是实时数据，由中台告警模块根据参数判定
            JsonResult jsonResult = alarmService.list(alarmQuery);
            return jsonResult;
        }catch (ParameterException paraException){
            return new JsonResult(paraException.getMessage(), false);
        }catch (Exception e) {
            return new JsonResult(e.getMessage(), false);
        }
    }

    /**
     * @auther: liudd
     * @date: 2020/2/27 11:54
     * 功能描述:导出
     */
    @RequestMapping("/export")
    @ResponseBody
    public JsonResult export(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request, HttpServletResponse response){
        try {
            JsonResult jsonResult = alarmService.list(alarmQuery);
            List<Alarm> alarmList = (List<Alarm>)jsonResult.getData();
            String title = alarmQuery.getType() + "告警列表";
            String[] headsName = { "告警名称","告警值","设备ID","告警状态", "告警等级","设备信息", "告警确认状态", "告警确认状态"};
            String[] properiesName = { "name", "value", "deviceId" ,"state","targetLevelName", "deviceInfos", "checkState"};
            export(response, alarmList, properiesName, headsName, title);
            return new JsonResult("导出成功", true);
        } catch (Exception e) {
        }
        return new JsonResult("导出数据失败", false);
    }

    /**
     * @auther: liudd
     * @date: 2020/3/2 13:12
     * 远程操作功能描述:
     * 确认告警：enterpriseCode, serverCode, type, idList, treportList, operateTime，operateUserId, operateUsername, operateDesc,operate
     * 告警消除：enterpriseCode, serverCode, type, idList, treportList, operateTime,operate
     */
    @RequestMapping("/operate")
    @ResponseBody
    public JsonResult operate(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        User user = getUser(request);
        if(null != user){
            alarmQuery.setOperateUserId(user.getId());
            alarmQuery.setOperateUsername(user.getUsername());
        }else{
            alarmQuery.setOperateUserId("admin");
            alarmQuery.setOperateUsername("超级管理员");
        }
        try {
            JsonResult jsonResult = alarmService.operate(alarmQuery);
            return jsonResult;
        }catch (ParameterException paraException){
            return new JsonResult(paraException.getMessage(), false);
        } catch (Exception e) {
            return new JsonResult(e.getMessage(), false);
        }
    }
}
