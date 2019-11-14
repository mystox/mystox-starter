package com.kongtrolink;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.MsgTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/26 11:29
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MsgTemplateTest {
    private String enterpriseCode = "meitainuo";
    private String serverCode = "zhyd";
    private String table = MongTable.MSG_TEMPLATE;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void list(){
        Criteria enterpriseCri = new Criteria();
        enterpriseCri.and("enterpriseCode").is(enterpriseCode);
        enterpriseCri.and("serverCode").is(serverCode);

        Criteria defalutCri = Criteria.where("enterpriseCode").exists(false);
        Criteria criteria = new Criteria();
        criteria.orOperator(defalutCri, enterpriseCri);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "enterpriseCode"));
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        List<MsgTemplate> msgTemplateList = mongoTemplate.find(query, MsgTemplate.class, table);
        for(MsgTemplate msgTemplate : msgTemplateList){
            System.out.println(msgTemplate.getEnterpriseCode() + ":" + msgTemplate.getEnterpriseName() + ";"
                + msgTemplate.getTemplateType() + ";" + msgTemplate.getType() + DateUtil.format(msgTemplate.getUpdateTime()));
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/11/13 15:11
     * 功能描述:模拟按周生成数据
     */
    @Test
    public void testData(){
        String enterpriseCode = "yytd";
        String serverCode = "TOWER_SERVER";
        String name = "温度过高告警";
        float value = 99.0f;
        String deviceType = "yy6";
        String deviceModel = "YY006";
        String deviceId = "10010_1021006";
        String signalId = "014001";
        int level = 1;
        int targetLevel = 1;
        String targetLevelName = "紧急告警";
        String color = "#DB001B";
        Alarm alarm = new Alarm();
        alarm.setEnterpriseCode(enterpriseCode);
        alarm.setServerCode(serverCode);
        alarm.setName(name);
        alarm.setValue(value);
        alarm.setDeviceType(deviceType);
        alarm.setDeviceModel(deviceModel);
        alarm.setDeviceId(deviceId);
        alarm.setSignalId(signalId);
        alarm.setLevel(level);
        alarm.setTargetLevel(targetLevel);
        alarm.setTargetLevelName(targetLevelName);
        alarm.setColor(color);
        String table = "yytd_TOWER_SERVER_alarm_history_2019_40";
        int beformDay = 1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d H:m:s");
        Calendar calendar = Calendar.getInstance();
        try {
            int day ;
            for (int i = 4001; i < 5000; i++) {
                alarm.setId(null);
                alarm.setTrecover(null);
                alarm.setSerial(i + "");
                alarm.initKey();
                int random = (int) (Math.random() * 5)+1;
                day = beformDay + random;
                //生成日期
                String dateStr = "2019-10-" + day + " 9:4:5";
                Date treport = simpleDateFormat.parse(dateStr);
                alarm.setTreport(treport);
                alarm.setState(Contant.PENDING);
                if(random%2 == 0){
                    alarm.setState(Contant.RESOLVE);
                    calendar.setTime(treport);
                    calendar.add(Calendar.DAY_OF_YEAR, random);
                    alarm.setTrecover(calendar.getTime());
                }
                mongoTemplate.save(alarm, table);
            }
        }catch (Exception e){
            System.out.println("日期转换异常");
        }

    }
}
