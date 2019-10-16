package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.enttiy.Alarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/15 17:08
 * @Description:
 */
@Service
public class AlarmDeliverService {

    @Autowired

    public static List<String> alarmStrList = new ArrayList<>();

    public void handleDeliver(){

        for(String alarmStr : alarmStrList){
            Alarm alarm = JSONObject.parseObject(alarmStr, Alarm.class);

        }
    }

}
