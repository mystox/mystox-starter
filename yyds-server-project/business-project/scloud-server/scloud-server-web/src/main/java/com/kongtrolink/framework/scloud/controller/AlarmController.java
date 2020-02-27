package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.AlarmService;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.SiteService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @Date: 2020/2/26 14:53
 * @Description:
 */
@Controller
@RequestMapping("/alarm")
public class AlarmController extends BaseController{

    @Autowired
    AlarmService alarmService;
    @Autowired
    SiteService siteService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    MqttOpera mqttOpera;
    private static final Logger logger = LoggerFactory.getLogger(AlarmController.class);

    /**
     * @auther: liudd
     * @date: 2020/2/26 14:59
     * 功能描述:列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        listCommon(alarmQuery);
        //操作模块待定
        String operaCode = "";
        //2，将设备id列表和其他参数传递过去
        try {
            //具体查询历史还是实时数据，由中台告警模块根据参数判定
            MsgResult msgResult = mqttOpera.opera(operaCode, JSONObject.toJSONString(alarmQuery));
            //liuddtodo 20200227可能需要补充拓展字段
            String msg = msgResult.getMsg();
            JSONObject jsonObject = (JSONObject)JSONObject.parse(msg);
            String list = jsonObject.getString("list");
            List<Alarm> alarmList = JSONObject.parseArray(list, Alarm.class);
            return new JsonResult(alarmList);
        } catch (Exception e) {
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSON(alarmQuery), operaCode, e.getMessage());
        }
        return new JsonResult("获取数据失败", false);
    }

    private void listCommon(AlarmQuery alarmQuery){
        //1，获取用户管辖范围设备id列表
        List<String> siteIdList = alarmQuery.getSiteIdList();
        //liuddtodo 还需要根据根据状态过滤
        //liuddtodo 需要根据FSU运行状态，获取站点id列表，与前端的站点id列表取交集
        List<DeviceEntity> deviceEntities = deviceService.listBySiteIdList(siteIdList);
        List<String> deviceCodeList = deviceService.list2CodeList(deviceEntities);
        alarmQuery.setDeviceCodeList(deviceCodeList);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/27 11:54
     * 功能描述:导出
     */
    @RequestMapping("/export")
    @ResponseBody
    public JsonResult export(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        listCommon(alarmQuery);
        String operaCode = "";
        try {
            MsgResult msgResult = mqttOpera.opera(operaCode, JSONObject.toJSONString(alarmQuery));
            //liuddtodo 20200227可能需要补充拓展字段
            String msg = msgResult.getMsg();
            JSONObject jsonObject = (JSONObject)JSONObject.parse(msg);
            String list = jsonObject.getString("list");
            List<Alarm> alarmList = JSONObject.parseArray(list, Alarm.class);
            //liuddtodo 导出相关的代码待定
            return new JsonResult("导出成功", true);
        } catch (Exception e) {
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSON(alarmQuery), operaCode, e.getMessage());
        }
        return new JsonResult("导出数据失败", false);
    }


    /**
     * @auther: liudd
     * @date: 2020/2/26 15:20
     * 功能描述:手动消除告警
     */
    @RequestMapping("/reslove")
    @ResponseBody
    public JsonResult reslove(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        //消除时间，消除用户，
        Date resloveTime = new Date();
        User user = getUser(request);
        //liuddtodo 告警消除的操作码待定
        String operaCode = "reslove";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", alarmQuery.getId());
        //必须要传递告警上报时间，帮助判定告警所在表
        jsonObject.put("tReport", alarmQuery.getBeginTime().getTime());
        jsonObject.put("userId", user.getId());
        jsonObject.put("username", user.getUsername());
        jsonObject.put("time", resloveTime.getTime());
        try {
            MsgResult msgResult = mqttOpera.opera(operaCode, jsonObject.toString());
            int stateCode = msgResult.getStateCode();
            //liuddtodo 需要确认返回值含义

            return new JsonResult("告警消除成功", true);
        }catch (Exception e) {
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSON(alarmQuery), operaCode, e.getMessage());
            return new JsonResult("告警消除失败", false);
        }
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
        String operaCode = "focus";
        Date focueTime = new Date();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", alarmQuery.getId());
        jsonObject.put("tReport", alarmQuery.getBeginTime().getTime());
        jsonObject.put("focus", alarmQuery.getFocus());
        jsonObject.put("time", focueTime.getTime());
        try {
            MsgResult msgResult = mqttOpera.opera(operaCode, jsonObject.toString());
            int stateCode = msgResult.getStateCode();
            //liuddtodo 需要确认返回值含义

            return new JsonResult(alarmQuery.getFocus() + "成功", true);
        }catch (Exception e) {
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSON(alarmQuery), operaCode, e.getMessage());
            return new JsonResult(alarmQuery.getFocus() + "失败", false);
        }
    }
}
