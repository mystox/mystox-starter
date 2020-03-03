package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.constant.Const;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.scloud.entity.AlarmFocus;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
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
    @Value("${alarmModule.list:alarmRemoteList}")
    private String remoteList;
    @Value("${alarmModule.check:alarmRemoteOperate}")
    private String remoteOperate;


    /**
     * @auther: liudd
     * @date: 2020/2/26 14:59
     * 功能描述:列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        try {
            listCommon(alarmQuery, request);
            //具体查询历史还是实时数据，由中台告警模块根据参数判定
            MsgResult msgResult = mqttOpera.opera(remoteList, JSONObject.toJSONString(alarmQuery));
            String msg = msgResult.getMsg();
            JsonResult jsonResult = JSONObject.parseObject(msg, JsonResult.class);
            return jsonResult;
        }catch (ParameterException paraException){
            return new JsonResult(paraException.getMessage(), false);
        }catch (Exception e) {
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSONString(alarmQuery), remoteList, e.getMessage());
        }
        return new JsonResult("获取数据失败", false);
    }

    private void listCommon(AlarmQuery alarmQuery, HttpServletRequest request)throws ParameterException{
        checkPara(alarmQuery, request);
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
        try {
            listCommon(alarmQuery, request);
            //具体查询历史还是实时数据，由中台告警模块根据参数判定
            MsgResult msgResult = mqttOpera.opera(remoteList, JSONObject.toJSONString(alarmQuery));
            String msg = msgResult.getMsg();
            JsonResult jsonResult = JSONObject.parseObject(msg, JsonResult.class);
            Object data = jsonResult.getData();

            //liuddtodo 导出相关的代码待定
            return new JsonResult("导出成功", true);
        } catch (Exception e) {
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSON(alarmQuery), remoteList, e.getMessage());
        }
        return new JsonResult("导出数据失败", false);
    }

    /**
     * @auther: liudd
     * @date: 2020/3/2 13:12
     * 远程操作功能描述:
     * 确认告警：enterpriseCode, serverCode, type, idList, treportList, operateTime，operateUserId, operateUsername, operateDesc
     * 告警消除：enterpriseCode, serverCode, type, idList, treportList, operateTime
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
            checkOperatePara(alarmQuery, request);
            Date curTime = new Date();
            alarmQuery.setOperateTime(curTime);
            String jsonStr = JSONObject.toJSONString(alarmQuery);
            MsgResult msgResult = mqttOpera.opera(remoteOperate, jsonStr);
            String msg = msgResult.getMsg();
            logger.error(" remote call result, msg:{}, operate:{}", JSONObject.toJSONString(msgResult), remoteOperate);
            JsonResult jsonResult = JSONObject.parseObject(msg, JsonResult.class);
            return jsonResult;
        }catch (ParameterException paraException){
            return new JsonResult(paraException.getMessage(), false);
        } catch (Exception e) {
            //打印调用失败消息
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSONString(alarmQuery), remoteOperate, e.getMessage());
        }
        return new JsonResult("确认失败", false);
    }

    private void checkPara(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request)throws ParameterException{
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        if(StringUtil.isNUll(enterpriseCode)){
            throw new ParameterException(Const.ALARM_LIST_ENTERPRISECODE + Const.PARA_MISS);
        }
        if(StringUtil.isNUll(alarmQuery.getServerCode())){
            throw new ParameterException(Const.ALARM_LIST_SERVERCODE + Const.PARA_MISS);
        }
        if(StringUtil.isNUll(alarmQuery.getType())){
            throw new ParameterException(Const.ALARM_LIST_TYPE + Const.PARA_MISS);
        }
    }

    private void checkOperatePara(AlarmQuery alarmQuery, HttpServletRequest request)throws ParameterException{
        checkPara(alarmQuery, request);
        //判定告警id，上报时间
        List<String> idList = alarmQuery.getIdList();
        List<Date> treportList = alarmQuery.getTreportList();
        if(null == idList || idList.size() == 0){
            throw new ParameterException("告警id为空");
        }
        if(null == treportList || treportList.size() == 0){
            throw new ParameterException("上报时间为空");
        }
        if(idList.size() != treportList.size()){
            throw new ParameterException("告警id和告警上报时间数量一致");
        }
        if(StringUtil.isNUll(alarmQuery.getOperate())){
            throw new ParameterException("操作类型为空");
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
        List<String> idList = alarmQuery.getIdList();
        if(Const.ALARM_OPERATE_FOCUS.equals(operate)){
            for(String id : idList) {
                AlarmFocus alarmFocus = new AlarmFocus();
                alarmFocus.setAlarmId(id);
                alarmFocus.setFocusTime(curTime);
                if (null != user) {
                    alarmFocus.setUserId(user.getId());
                    alarmFocus.setUsername(user.getUsername());
                }
                alarmFocusService.add(uniqueCode, alarmFocus);
            }
        }else{
            alarmFocusService.deleteByIdList(uniqueCode, idList);
        }
        return new JsonResult(operate + Const.RESULT_SUCC, true);
    }
}
