package com.kongtrolink;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.service.AlarmService;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/10/23 10:40
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlarmTest.class)
@ImportResource("application.yml")
public class AlarmTest {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    AlarmDao alarmDao;
    @Autowired
    AlarmService alarmService;

    private String enterpriseCode = "meitainuo";
    private String serverCode = "zhyd";
    private String deviceType = "kgdy";
    private String deviceModel = "kgdyARCM001";
    private String deviceId = "kgdyid001";
    private String signalId = "gwgj001";
    private String currentTable = MongTable.ALARM_CURRENT;
    private String historyTable = MongTable.ALARM_HISTORY;

    @Test
    public void add(){
        Alarm alarm = new Alarm();
        alarm.setEnterpriseCode(enterpriseCode);
        alarm.setServerCode(serverCode);
        alarm.setDeviceType(deviceType);
        alarm.setDeviceModel(deviceModel);
        alarm.setDeviceId(deviceId+"001");
        alarm.setSignalId(signalId+"001");
        alarm.setName("超低温告警");
        alarm.setValue(-25);
        alarm.setSerial("52");
        alarm.setLevel(2);
        alarm.setTargetLevel(1);
        alarm.setTargetLevelName("紧急告警");
        alarm.setColor(Contant.COLOR_RED);
        alarm.setTreport(new Date());
        alarm.setState(Contant.PENDING);
        alarm.setTrecover(new Date());
        alarmDao.save(alarm, enterpriseCode + Contant.UNDERLINE + serverCode + Contant.UNDERLINE + historyTable);
    }

    @Test
    public void auxilary(){
        Map<String, String> auxiMap = new HashMap<>();
        auxiMap.put("auxi001", "good");
        auxiMap.put("testauxi555", "money");
        auxiMap.put("abcdk", "250");
        alarmDao.updateAuxilary(deviceType, deviceModel, deviceId, signalId,"115", auxiMap, currentTable);
    }

    @Test
    public void list(){
        AlarmQuery alarmQuery = new AlarmQuery();
        alarmQuery.setEnterpriseCode(enterpriseCode);
        alarmQuery.setServerCode(serverCode);
        List<DBObject> list = alarmDao.listCurrent(alarmQuery, currentTable);
        System.out.println(list);
    }

    @Test
    public void saveDeviceInfo(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", "deviceId001");
        jsonObject.put("deviceName", "设备001");
        jsonObject.put("enterprise", "服务001");
        Criteria criteria = Criteria.where("_id").is("5dafc66c7ac75110500250fd");
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("deviceInfos", jsonObject);
        mongoTemplate.updateFirst(query, update, currentTable);
    }

    @Test
    public void get(){
        Criteria criteria = Criteria.where("_id").is("5dafc66c7ac75110500250fd");
        Query query = Query.query(criteria);
        Alarm alarm = mongoTemplate.findOne(query, Alarm.class, currentTable);

        System.out.println(alarm.getDeviceInfos());
        Map<String, String> deviceInfos = alarm.getDeviceInfos();
        for(String key : deviceInfos.keySet()){
            System.out.println(key + ":" + deviceInfos.get(key));
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/11/13 19:29
     * 功能描述:测试历史告警列表页分表分页查询
     */
    @Test
    public void getHistory(){
        try {
            SimpleDateFormat simpleDateFormat = DateUtil.getSimpleDateFormat();
            String beginTimeStr = "2019-10-12 06:22:25";
            String endTimeStr = "2019-11-13 10:22:25";
            Date beginTime = simpleDateFormat.parse(beginTimeStr);
            Date endTime = simpleDateFormat.parse(endTimeStr);
            System.out.println("beginTime year_week:" + DateUtil.getYear_week(beginTime) + "; endTime year_week:" + DateUtil.getYear_week(endTime));
            int curPage = 2;
            int pageSize = 15;
            AlarmQuery alarmQuery = new AlarmQuery();
            alarmQuery.setCurrentPage(curPage);
            alarmQuery.setPageSize(pageSize);
            alarmQuery.setEnterpriseCode("yytd");
            alarmQuery.setServerCode("TOWER_SERVER");
            alarmQuery.setType(Contant.HIST_ALARM);
            alarmQuery.setStartBeginTime(beginTime);
            List<DBObject> list = alarmService.list(alarmQuery);
            System.out.println(list.toString());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("出异常罗");
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/11/13 19:31
     * 功能描述:测试动态添加索引
     */
    @Test
    public void testIndex(){
        String table = "yytd_TOWER_SERVER_alarm_history_2019_46";
        DBCollection collection = mongoTemplate.getCollection(table);
        System.out.println(collection);
    }
}
