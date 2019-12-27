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
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmQuery alarmQuery){
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        String serverCode = alarmQuery.getServerCode();
        ListResult<DBObject> listResult ;
        if(Contant.CURR_ALARM.equals(alarmQuery.getType())){
            List<DBObject> list= alarmService.list(alarmQuery, MongTable.ALARM_CURRENT);
            int count = list.size();
            listResult = new ListResult<>(list, count);
        }else{
            listResult = alarmService.historyAlarmList(alarmQuery);
        }
        JsonResult jsonResult = new JsonResult(listResult);
        Auxilary auxilary = auxilaryService.getByEnterServerCode(enterpriseCode, serverCode);
        jsonResult.setOtherInfo(auxilary);
        return jsonResult;
    }

    public void testIndex(){
        String table = "YYDS_TOWER_SERVER_1.0.0_alarm_history_2019_47";
        DBCollection collection = mongoTemplate.getCollection(table);
        System.out.println(collection);
        boolean hadIndex = hadIndex(collection);
        if(!hadIndex){
            collection.createIndex("key");
            collection.createIndex("treport");
            collection.createIndex("deviceType");
            collection.createIndex("deviceModel");
            collection.createIndex("targetLevel");
            hadIndex = hadIndex(collection);
            if(hadIndex){
                System.out.println("拥有两个index了");
            }
        }
    }
//
    public boolean hadIndex(DBCollection collection){
        List<DBObject> indexInfo = collection.getIndexInfo();
        for(DBObject dbObject : indexInfo){
            System.out.println(dbObject);
            Object key = dbObject.get("key");
            String keyStr = key.toString();
            if(keyStr.contains("key")){
                System.out.println("table:" + collection.getName() + " 包含索引key");
                return true;
            }
        }
        System.out.println("table:" + collection.getName() + " 不包含索引key");
        return false;
    }
}
