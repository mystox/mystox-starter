package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.query.AlarmLevelQuery;
import com.kongtrolink.framework.scloud.service.AlarmBusinessService;
import com.kongtrolink.framework.scloud.service.AlarmLevelService;
import com.kongtrolink.framework.scloud.service.ExcelExportService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
            businessQuery.setOperatorId(user.getId());
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
        List<AlarmLevel> alarmLevelList = getLastUseLevels(businessQuery.getEnterpriseCode(), businessQuery.getServerCode());
        for(AlarmBusiness alarmBusiness : list){
            String levelName = AlarmLevel.getLevelName(alarmBusiness.getLevel(), alarmLevelList);
            alarmBusiness.setLevelName(levelName);
        }
        int count = businessService.count(uniqueCode, businessQuery);
        ListResult<AlarmBusiness> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/15 16:28
     * 功能描述:获取企业最后启用的告警等级
     */
    private List<AlarmLevel> getLastUseLevels(String enterpriseCode, String serverCode){
        AlarmLevelQuery alarmLevelQuery = new AlarmLevelQuery();
        alarmLevelQuery.setEnterpriseCode(enterpriseCode);
        alarmLevelQuery.setServerCode(serverCode);
        JSONObject lastUse = alarmLevelService.getLastUse(alarmLevelQuery);
        Boolean success = lastUse.getBoolean("success");
        List<AlarmLevel> alarmLevelList = null;
        if(success){
            alarmLevelList = JSONObject.parseArray(lastUse.getString("data"), AlarmLevel.class);
        }
        return alarmLevelList;
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
            businessQuery.setOperatorId(user.getId());
        }
        List<AlarmBusiness> list = businessService.list(uniqueCode, businessQuery);
        List<AlarmLevel> alarmLevelList = getLastUseLevels(businessQuery.getEnterpriseCode(), businessQuery.getServerCode());
        for(AlarmBusiness alarmBusiness : list){
            String levelName = AlarmLevel.getLevelName(alarmBusiness.getLevel(), alarmLevelList);
            alarmBusiness.setLevelName(levelName);
        }
        String tableTitle = businessQuery.getLevelName();
        String[] headsName = { "告警名称", "告警等级", "告警值","站点层级","站点名称","告警设备", "开始时间"};
        String[] properiesName = { "name", "levelName", "value" ,"tierName","siteName", "deviceName", "treport"};
        ExcelExportService.exportTwoLineHeadData(response, "实时告警统计表", list, properiesName, headsName, tableTitle);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/14 10:43
     * 功能描述:获取列表，暂时使用于实时告警统计列表
     */
    @RequestMapping("/countLevel")
    @ResponseBody
    public JsonResult countLevel(@RequestBody AlarmBusinessQuery businessQuery, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        if(StringUtil.isNUll(uniqueCode)){
            uniqueCode = "YYDS";
        }

        User user = getUser(request);
        if(null != user){
            businessQuery.setOperatorId(user.getId());
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
        businessQuery.setState(BaseConstant.ALARM_STATE_PENDING);
        List<Statistics> statisticss = businessService.countLevel(uniqueCode, CollectionSuffix.CUR_ALARM_BUSINESS, businessQuery);
        List<AlarmLevel> alarmLevelList = getLastUseLevels(businessQuery.getEnterpriseCode(), businessQuery.getServerCode());
        for(Statistics statistics : statisticss){
            String levelName = AlarmLevel.getLevelName(statistics.getIntPro(), alarmLevelList);
            statistics.setName(levelName);
        }
        return new JsonResult(statisticss);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/14 15:50
     * 功能描述:告警频发站，允许选择一个月时间跨度
     */
    @RequestMapping("/alarmSiteHistory")
    @ResponseBody
    public JsonResult alarmSiteHistory(@RequestBody AlarmBusinessQuery businessQuery){
        String uniqueCode = getUniqueCode();
        if(StringUtil.isNUll(uniqueCode)){
            uniqueCode = "YYDS";
        }
        List<Statistics> businessList = businessService.alarmSiteTopHistory(uniqueCode, businessQuery);
        return new JsonResult(businessList);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/14 15:50
     * 功能描述:告警频发站，允许选择一个月时间跨度
     */
    @RequestMapping("/alarmSiteHistoryExport")
    @ResponseBody
    public JsonResult alarmSiteHistoryExport(@RequestBody AlarmBusinessQuery businessQuery, HttpServletResponse response){
        String uniqueCode = getUniqueCode();
        if(StringUtil.isNUll(uniqueCode)){
            uniqueCode = "YYDS";
        }
        List<Statistics> businessList = businessService.alarmSiteTopHistory(uniqueCode, businessQuery);
        ExcelExportService.alarmSiteHistoryExport(response, "告警频发站点", businessList);
        return new JsonResult(businessList);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/15 11:32
     * 功能描述:站点分布
     */
    @RequestMapping(value = "/getAlarmTierList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getAlarmTierList(@RequestBody AlarmBusinessQuery businessQuery) {
        String uniqueCode = getUniqueCode();
        if(StringUtil.isNUll(uniqueCode)){
            uniqueCode = "YYDS";
        }
        List<Statistics> alarmTierList = businessService.getAlarmDistributeList(uniqueCode, businessQuery);
        return new JsonResult(alarmTierList);
    }
}
