package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
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
    AlarmBusinessService businessService;
    @Autowired
    MqttOpera mqttOpera;

    /**
     * @auther: liudd
     * @date: 2020/2/26 14:59
     * 功能描述:列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmQuery alarmQuery, HttpServletRequest request){
        try {
            User user = getUser(request);
            if(null != user){
                alarmQuery.setOperateUserId(user.getId());
            }
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
    public JsonResult operate(@RequestBody AlarmBusinessQuery businessQuery, HttpServletRequest request){
        JsonResult jsonResult = new JsonResult();
        JSONObject jsonObject = new JSONObject();
        String uniqueCode = getUniqueCode();
        String table = CollectionSuffix.CUR_ALARM_BUSINESS;
        String type = businessQuery.getType();
        if(BaseConstant.ALARM_TYPE_HISTORY.equals(type)){
            table = CollectionSuffix.HIS_ALARM_BUSINESS;
        }
        User user = getUser(request);
        businessQuery.setOperateTime(new Date());
        if(null != user){
            businessQuery.setOperateUserId(user.getId());
            businessQuery.setOperateUsername(user.getUsername());
        }else{
            businessQuery.setOperateUserId("admin");
            businessQuery.setOperateUsername("超级管理员");
        }
        String operate = businessQuery.getOperate();
        if(BaseConstant.ALARM_OPERATE_CHECK.equals(operate)){
            jsonObject = businessService.check(uniqueCode, table, businessQuery);
            jsonResult.setData(jsonObject);
        }else if(BaseConstant.ALARM_OPERATE_NOTCHECK.equals(operate)){
            jsonObject = businessService.unCheck(uniqueCode, table, businessQuery);
            jsonResult.setData(jsonObject);
        }else if(BaseConstant.ALARM_OPERATE_RESOLVE.equals(operate)){
            List<String> realKeyList = new ArrayList<>();
            List<Date> realReportList = new ArrayList<>();
            List<String> keyList = businessQuery.getKeyList();
            List<Date> treportList = businessQuery.getTreportList();
            for(int i = 0; i< keyList.size(); i++){
                String key = keyList.get(i);
                businessQuery.setKey(key);
                Date treport = treportList.get(i);
                boolean resolve = businessService.resolve(uniqueCode, table, businessQuery);
                if(resolve){
                    realKeyList.add(key);
                    realReportList.add(treport);
                }
            }
            if(realKeyList.size() > 0){
                AlarmQuery alarmQuery = new AlarmQuery();
                alarmQuery.setEnterpriseCode(businessQuery.getEnterpriseCode());
                alarmQuery.setServerCode(businessQuery.getServerCode());
                alarmQuery.setType(businessQuery.getType());
                alarmQuery.setOperate(businessQuery.getOperate());
                alarmQuery.setOperateTime(businessQuery.getOperateTime());
                alarmQuery.setTreportList(realReportList);
                alarmQuery.setKeyList(realKeyList);
                try {
                    jsonObject = alarmService.operate(alarmQuery);
                    String failKeyListStr = jsonObject.getString(BaseConstant.REMOTE_OPERATE_FAILKEYS);
                    List<String> failKeyList = JSONObject.parseArray(failKeyListStr, String.class);
                    String data = businessQuery.getOperate() + "成功" + (keyList.size() - failKeyList.size()) + "个，失败" + failKeyList.size() + "个";
                    if(failKeyList.size() > 0){
                        businessService.unResolveByKeys(uniqueCode, table, failKeyList);
                    }
                    jsonObject.put("data", data);
                } catch (Exception e) {
                    //回滚本地
                    jsonObject.put("success", false);
                    jsonObject.put("info", "远程调用失败");
                    businessService.unResolveByKeys(uniqueCode, table, keyList);
                    return new JsonResult(e.getMessage(), false);
                }
            }
        }
        jsonResult.setData(jsonObject);
        return jsonResult;
    }
}
