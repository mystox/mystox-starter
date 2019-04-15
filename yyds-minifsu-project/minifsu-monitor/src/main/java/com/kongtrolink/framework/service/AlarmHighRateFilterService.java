package com.kongtrolink.framework.service;

import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/4/10 10:07
 * @Description:高频过滤service
 * 规则：
 * 初次产生告警，将告警点的highrateft设置为当前时间戳，highratec设置为1
 * 下次产生告警后，判断第一次告警产生时间差，
 *      如果属于规定时间段
 *          如果小于规定次数，则生成告警并更新highratec
 *          如果大于大于规定次数，则跳过
 *      如果在规定时间段外
 *          更新highrateft和重置highratec为1
 *
 */
@Service
public class AlarmHighRateFilterService {

    /**
     * @auther: liudd
     * @date: 2019/4/10 11:26
     * 功能描述:告警产生判定高频过滤
     */
    public Alarm checkAlarm(Alarm beforAlarm, AlarmSignalConfig alarmSignal, Date curDate){
        if(null == beforAlarm){
            return null;
        }
        long highRateFT = alarmSignal.getHighRateFT();
        int highRateC = alarmSignal.getHighRateC();
        int highRateI = alarmSignal.getHighRateI();
        int highRateT = alarmSignal.getHighRateT();
        long diff = curDate.getTime() - highRateFT;
        boolean inTime = diff < highRateI*1000;
        if(highRateFT == 0){
            alarmSignal.setHighRateFT(curDate.getTime());
            alarmSignal.setHighRateC(1);
            return beforAlarm;
        }
        if(inTime){
            if(highRateC >= highRateT){
                return null;
            }
            alarmSignal.setHighRateC(highRateC+1);
            return beforAlarm;
        }else{
            alarmSignal.setHighRateFT(curDate.getTime());
            alarmSignal.setHighRateC(1);
            return beforAlarm;
        }
    }
}
