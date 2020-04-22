package com.kongtrolink.framework.reports.utils;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.TimePeriod;
import com.kongtrolink.framework.reports.entity.query.FsuEntity;
import com.kongtrolink.framework.reports.entity.query.FsuOperationState;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/10 13:28
 * \* Description:
 * \
 */
public class CommonCheck {
    /**
     * @Date 13:48 2020/3/10
     * @Param No such property: code for class: Script1
     * @return java.lang.String
     * @Author mystox
     * @Description //判断交维态
     **/
    public static String fsuOperaStateCheck(List<FsuEntity> fsuEntityList) {

        for(FsuEntity fsuEntity: fsuEntityList)
        {
            if (FsuOperationState.PROJECT.equals(fsuEntity.getOperationState()))
                return FsuOperationState.PROJECT;
        }
        for(FsuEntity fsuEntity: fsuEntityList)
        {
            if (FsuOperationState.TEST.equals(fsuEntity.getOperationState()))
                return FsuOperationState.TEST;
        }
        return FsuOperationState.MAINTENANCE;
    }

    public static String aggregateTierName(JSONObject jsonObject) {
        String province = jsonObject.getString("province");
        String municipality = jsonObject.getString("municipality");
        String county = jsonObject.getString("county");
        String tierName = province;
        if (StringUtils.isNotBlank(municipality)) tierName = tierName + "-" + municipality;
        if (StringUtils.isNotBlank(county)) tierName = tierName + "-" + county;
        return tierName;
    }


    public static TimePeriod getTimePeriod(JSONObject statisticPeriod) {
        String period = "";
        TimePeriod timePeriod;
        if (statisticPeriod == null) {
            period = "月报表";
            timePeriod = new TimePeriod();
            timePeriod.setEndTime(new Date(System.currentTimeMillis()));
            timePeriod.setStartTime(DateUtil.getInstance().getFirstDayOfMonth());
        } else {
            period = statisticPeriod.getString("dimension");
            timePeriod = statisticPeriod.getObject("timePeriod", TimePeriod.class);
        }
        timePeriod.setDimension(period);
        return timePeriod;
    }


    public static int getYear(Integer alarmCycle) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (alarmCycle != null) {
            int cycleDay = alarmCycle / 24 + 1;
            if (day <= cycleDay)  //每月前几天周期统计上月的历史数据
                if (month == 1) {
                    year -= 1;
                }
        }
        return year;
    }


    public static int getMonth(Integer alarmCycle) {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (alarmCycle != null) {
            int cycleDay = alarmCycle / 24 + 1;
            if (day <= cycleDay)  //每月前几天周期统计上月的历史数据
                if (month == 1) {
                    month = 12;
                } else month -= 1;
        }
        return month;
    }
}