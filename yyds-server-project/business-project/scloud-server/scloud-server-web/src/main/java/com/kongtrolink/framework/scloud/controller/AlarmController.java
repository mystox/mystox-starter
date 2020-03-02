package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.constant.Const;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmFocus;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.Paging;
import com.kongtrolink.framework.scloud.service.AlarmFocusService;
import com.kongtrolink.framework.scloud.service.AlarmService;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.SiteService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    AlarmFocusService alarmFocusService;

    private static final Logger logger = LoggerFactory.getLogger(AlarmController.class);
    @Value("${alarmModule.list:remoteList}")
    private String remoteList;
    @Value("${alarmModule.check:alarmRemoteCheck}")
    private String remoteCheck;


    /**
     * @auther: liudd
     * @date: 2020/2/26 14:59
     * 功能描述:列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        listCommon(alarmQuery);
        String jsonStr = JSONObject.toJSONString(alarmQuery);
        try {
            //具体查询历史还是实时数据，由中台告警模块根据参数判定
            MsgResult msgResult = mqttOpera.opera(remoteList, jsonStr);
            logger.error(" remote call result, msg:{}, operate:{}", JSONObject.toJSONString(msgResult), remoteList);
            //liuddtodo 20200227可能需要补充拓展字段
            String msg = msgResult.getMsg();
            JsonResult jsonResult = JSONObject.parseObject(msg, JsonResult.class);
            //liuddtodo 可能需要补充其他信息
            return jsonResult;
        } catch (Exception e) {
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", jsonStr, remoteList, e.getMessage());
        }
        return new JsonResult("获取数据失败", false);
    }

    private void listCommon(AlarmQuery alarmQuery){
        //1，获取用户管辖范围设备id列表
        List<Integer> siteIdList = alarmQuery.getSiteIdList();
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setSiteIds(siteIdList);
        deviceQuery.setOperationState(alarmQuery.getOperationState());
        List<DeviceEntity> deviceEntityList = deviceService.list(deviceQuery);
        List<String> deviceCodeList = deviceService.list2CodeList(deviceEntityList);
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
     * @date: 2020/3/2 13:12
     * 功能描述:确认告警
     */
    @RequestMapping("/check")
    @ResponseBody
    public JsonResult check(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        Date curTime = new Date();
        alarmQuery.setOperateTime(curTime);
        User user = getUser(request);
        if(null != user){
            alarmQuery.setOperateUserId(user.getId());
            alarmQuery.setOperateUsername(user.getUsername());
        }else{
            alarmQuery.setOperateUserId("admin");
            alarmQuery.setOperateUsername("超级管理员");
        }
        alarmQuery.setOperate(Const.ALARM_OPERATE_CHECK);
        String jsonStr = JSONObject.toJSONString(alarmQuery);
        try {
            MsgResult msgResult = mqttOpera.opera(remoteCheck, jsonStr);
            String msg = msgResult.getMsg();
            logger.error(" remote call result, msg:{}, operate:{}", JSONObject.toJSONString(msgResult), remoteCheck);
            JsonResult jsonResult = JSONObject.parseObject(msg, JsonResult.class);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", jsonStr, remoteCheck, e.getMessage());
        }
        return new JsonResult("确认失败", false);
    }


    /**
     * @auther: liudd
     * @date: 2020/2/26 15:20
     * 功能描述:手动消除告警，消除后需发送推送
     */
    @RequestMapping("/reslove")
    @ResponseBody
    public JsonResult reslove(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        //消除时间，消除用户，
        Date resloveTime = new Date();
        User user = getUser(request);
        //需要参数1，告警id， 2，告警上报时间，3，告警类型
        String operaCode = "reslove";
        alarmQuery.setOperateTime(resloveTime);
        try {
            MsgResult msgResult = mqttOpera.opera(operaCode, JSONObject.toJSONString(alarmQuery));
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
        Date curTime = new Date();
        User user = getUser(request);
        String uniqueCode = getUniqueCode();
        String operate = alarmQuery.getOperate();
        boolean result ;
        if(Const.ALARM_OPERATE_FOCUS.equals(operate)){
            AlarmFocus alarmFocus = new AlarmFocus();
            alarmFocus.setAlarmId(alarmQuery.getId());
            alarmFocus.setFocusTime(curTime);
            if(null != user){
                alarmFocus.setUserId(user.getId());
                alarmFocus.setUsername(user.getUsername());
            }
            result = alarmFocusService.add(uniqueCode, alarmFocus);
        }else{
            result = alarmFocusService.delete(uniqueCode, alarmQuery.getId());
        }
        if(result){
            return new JsonResult(operate + Const.RESULT_SUCC, true);
        }
        return new JsonResult(operate + Const.RESULT_FAIL, false);
    }
}
