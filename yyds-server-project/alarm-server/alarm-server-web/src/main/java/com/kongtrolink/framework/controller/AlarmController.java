package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.service.AlarmService;
import com.kongtrolink.framework.service.AuxilaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:42
 * @Description:
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
        String table = enterpriseCode + serverCode + MongTable.ALARM_CURRENT;
        if(Contant.HIST_ALARM.equals(type)) {
            table = enterpriseCode + serverCode + MongTable.ALARM_HISTORY;
        }
        List<Alarm> list = alarmService.list(alarmQuery, table);
        int count = alarmService.count(alarmQuery, table);
        ListResult<Alarm> listResult = new ListResult<>(list, count);
        JsonResult jsonResult = new JsonResult(listResult);
        Auxilary auxilary = auxilaryService.getByEnterServerCode(enterpriseCode, serverCode);
        jsonResult.setOtherInfo(auxilary);
        return jsonResult;
    }



}
