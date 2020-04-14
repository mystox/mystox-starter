package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.AlarmLevel;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.query.AlarmLevelQuery;
import com.kongtrolink.framework.scloud.service.AlarmBusinessService;
import com.kongtrolink.framework.scloud.service.AlarmLevelService;
import com.kongtrolink.framework.scloud.service.ExcelExportService;
import com.kongtrolink.framework.scloud.util.DateUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/14 10:17
 * @Description:告警业务信息控制层。
 */
@Controller
@RequestMapping("/alarmBusiness")
public class AlarmBusinessController extends ExportController{

    @Autowired
    AlarmBusinessService businessService;
    @Autowired
    AlarmLevelService alarmLevelService;

    /**
     * @auther: liudd
     * @date: 2020/4/14 10:43
     * 功能描述:获取列表，暂时使用于实时告警统计列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmBusinessQuery businessQuery, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        if(StringUtil.isNUll(uniqueCode)){
            uniqueCode = "YYDS";
        }
        User user = getUser(request);
        if(null != user){
            businessQuery.setOperateUserId(user.getId());
        }
        Date startBeginTime = businessQuery.getStartBeginTime();
        Date startEndTime = businessQuery.getStartEndTime();
        if(null == startEndTime || null == startBeginTime){
            startEndTime = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startEndTime);
            calendar.set(Calendar.MONTH, -1);
            startBeginTime = calendar.getTime();
            businessQuery.setStartBeginTime(startBeginTime);
            businessQuery.setStartEndTime(startEndTime);
        }
        List<AlarmBusiness> list = businessService.list(uniqueCode, businessQuery);
        AlarmLevelQuery alarmLevelQuery = new AlarmLevelQuery();
        alarmLevelQuery.setEnterpriseCode(businessQuery.getEnterpriseCode());
        alarmLevelQuery.setServerCode(businessQuery.getServerCode());
        JSONObject lastUse = alarmLevelService.getLastUse(alarmLevelQuery);
        Boolean success = lastUse.getBoolean("success");
        List<AlarmLevel> alarmLevelList = null;
        if(success){
            alarmLevelList = JSONObject.parseArray(lastUse.getString("data"), AlarmLevel.class);
        }
        for(AlarmBusiness alarmBusiness : list){
            AlarmLevel.initLevelName(alarmBusiness, alarmLevelList);
        }
        int count = businessService.count(uniqueCode, businessQuery);
        ListResult<AlarmBusiness> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/14 10:43
     * 功能描述:获取列表，暂时使用于实时告警统计列表
     */
    @RequestMapping("/export")
    @ResponseBody
    public void export(@RequestBody AlarmBusinessQuery businessQuery, HttpServletRequest request, HttpServletResponse response){
        String uniqueCode = getUniqueCode();
        if(StringUtil.isNUll(uniqueCode)){
            uniqueCode = "YYDS";
        }
        User user = getUser(request);
        if(null != user){
            businessQuery.setOperateUserId(user.getId());
        }
        List<AlarmBusiness> list = businessService.list(uniqueCode, businessQuery);
        AlarmLevelQuery alarmLevelQuery = new AlarmLevelQuery();
        JSONObject lastUse = alarmLevelService.getLastUse(alarmLevelQuery);
        Boolean success = lastUse.getBoolean("success");
        List<AlarmLevel> alarmLevelList = null;
        if(success){
            alarmLevelList = JSONObject.parseArray(lastUse.getString("data"), AlarmLevel.class);
        }
        for(AlarmBusiness alarmBusiness : list){
            AlarmLevel.initLevelName(alarmBusiness, alarmLevelList);
        }
        String tableTitle = businessQuery.getLevelName();
        String[] headsName = { "告警名称", "告警等级", "告警值","站点层级","站点名称","告警设备", "开始时间"};
        String[] properiesName = { "name", "levelName", "value" ,"tierName","siteName", "deviceName", "treport"};
        ExcelExportService.exportTwoLineHeadData(response, "实时告警统计表", list, properiesName, headsName, tableTitle);
    }
}
