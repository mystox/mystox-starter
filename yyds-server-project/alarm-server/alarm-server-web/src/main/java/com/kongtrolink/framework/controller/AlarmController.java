package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.service.AlarmService;
import com.kongtrolink.framework.service.AuxilaryService;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:42
 * @Description:实时表不分表，历史表分表
 */
@Controller
@RequestMapping("/alarmController/")
public class AlarmController extends BaseController {

    @Autowired
    AlarmService alarmService;
    @Autowired
    AuxilaryService auxilaryService;

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmQuery alarmQuery){
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        String serverCode = alarmQuery.getServerCode();
        String type = alarmQuery.getType();
        String table = MongTable.ALARM_CURRENT;
        ListResult<DBObject> listResult = null;
        if(Contant.CURR_ALARM.equals(type)){
            List<DBObject> list= alarmService.list(alarmQuery, table);
            int count = alarmService.count(alarmQuery, table);
            listResult = new ListResult<>(list, count);
        }else{

            listResult = getHistoryAlarmList(alarmQuery);
        }

        JsonResult jsonResult = new JsonResult(listResult);
        Auxilary auxilary = auxilaryService.getByEnterServerCode(enterpriseCode, serverCode);
        jsonResult.setOtherInfo(auxilary);
        return jsonResult;
    }

    //历史告警多张表分页查询
    private  ListResult<DBObject> getHistoryAlarmList(AlarmQuery alarmQuery){
        //1，确定时间。如果开始接结束都没有选择，开始结束为今天到一个月前的今天
        Date startBeginTime = alarmQuery.getStartBeginTime();
        Date startEndTime = alarmQuery.getStartEndTime();
        Calendar calendar = DateUtil.calendar;
        if(null == startBeginTime && null == startEndTime){
            startEndTime = new Date();

        }
        return null;
    }

}
