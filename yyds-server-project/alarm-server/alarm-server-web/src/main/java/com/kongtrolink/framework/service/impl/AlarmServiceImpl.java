package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.service.AlarmService;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:42
 * @Description:
 */
@Service
public class AlarmServiceImpl implements AlarmService{

    @Autowired
    AlarmDao alarmDao;
    Calendar calendar = DateUtil.calendar;

    /**
     * @param alarmQuery
     * @param table
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:列表
     */
    @Override
    public List<DBObject> list(AlarmQuery alarmQuery, String table) {
        return alarmDao.list(alarmQuery, table);
    }

    /**
     * @param alarmQuery
     * @param table
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:统计
     */
    @Override
    public int count(AlarmQuery alarmQuery, String table) {
        return alarmDao.count(alarmQuery, table);
    }

    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2019/11/13 16:59
     * 功能描述:前端获取历史告警表
     */
    @Override
    public ListResult<DBObject> getHistoryAlarmList(AlarmQuery alarmQuery) {
        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + MongTable.ALARM_HISTORY + Contant.UNDERLINE;
        //1，确定时间。如果开始接结束都没有选择，开始结束为今天到一个月前的今天
        alarmQuery = initTime(alarmQuery);
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        System.out.println(weeks.toString());

        //根据周数，获取所有数据
        int allCount = 0;       //计算总数
        int[] weekCounts = new int[weeks.size()];
        for(int i=0; i<weeks.size(); i++){
            String table = tablePrefix + weeks.get(i);
            weekCounts[i] = count(alarmQuery, table);
            allCount += weekCounts[i];
        }
        //判定每个表是否能满足当前分页数据
        List<DBObject> allList = new ArrayList<>();
        int currentPage = alarmQuery.getCurrentPage();
        int pageSize = alarmQuery.getPageSize();
        int beginNum = (currentPage - 1) * pageSize;
        int nextBegin = beginNum;   //下一个表分页起始数据
        int nextSize = pageSize;    //下一个表获取数量
        for(int i=0; i<weeks.size(); i++){
            int endNum = nextBegin + nextSize;
            int tempCount = weekCounts[i];
            if(tempCount > nextBegin){
                alarmQuery.setRealBeginNum(nextBegin);
                alarmQuery.setRealLimit(nextSize);
                List<DBObject> tempList = list(alarmQuery, tablePrefix + weeks.get(i));
                allList.addAll(tempList);
                if(tempCount >= endNum){     //如果当前表能满足剩余所有分页数据获取
                    break;
                }else{
                    nextBegin = 0;
                    nextSize = endNum - tempCount;
                }
            }else{
                nextBegin = nextBegin - tempCount;
            }
        }
        ListResult<DBObject> listResult = new ListResult<>(allList, allCount);
        return listResult;
    }

    /**
     * @auther: liudd
     * @date: 2019/11/13 16:36
     * 功能描述:确定开始结束时间
     */
    private AlarmQuery initTime(AlarmQuery alarmQuery){
        Date startBeginTime = alarmQuery.getStartBeginTime();
        Date startEndTime = alarmQuery.getStartEndTime();
        Date curDate = new Date();
        if(null == startBeginTime && null == startEndTime){
            startEndTime = curDate;
            calendar.setTime(startEndTime);
            calendar.add(Calendar.MONTH, -1);   //时间跨度写死一个月，不要问为什么，我就是这么浪
            startBeginTime = calendar.getTime();
        }else if(null == startBeginTime && null != startEndTime){
            if(startEndTime.getTime() > curDate.getTime()){
                startEndTime = curDate;
            }
            calendar.setTime(startEndTime);
            calendar.add(Calendar.MONTH, -1);
            startBeginTime = calendar.getTime();
        }else if(null != startBeginTime && null == startEndTime){
            calendar.setTime(startBeginTime);
            calendar.add(Calendar.MONTH, 1);
            startEndTime = calendar.getTime();
        }else if(null != startBeginTime && null != startEndTime){
            calendar.setTime(startBeginTime);
            calendar.add(Calendar.MONTH, 1);
            Date countEndTIme = calendar.getTime();
            if(startEndTime.getTime() > countEndTIme.getTime()){
                startEndTime = countEndTIme;
            }
        }
        alarmQuery.setStartBeginTime(startBeginTime);
        alarmQuery.setStartEndTime(startEndTime);
        return alarmQuery;
    }

    private List<String> getWeeks(AlarmQuery alarmQuery){
        List<String> weekList = new ArrayList<>();
        Date startBeginTime = alarmQuery.getStartBeginTime();
        Date startEndTime = alarmQuery.getStartEndTime();
        calendar.setTime(startBeginTime);
        while(startBeginTime.getTime() <= startEndTime.getTime()){
            String year_week = DateUtil.getYear_week(startBeginTime);
            if(!weekList.contains(year_week)) {
                weekList.add(year_week);
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            startBeginTime = calendar.getTime();
        }
        String endWeek = DateUtil.getYear_week(startEndTime);
        if(!weekList.contains(endWeek)){
            weekList.add(endWeek);
        }
        return weekList;
    }
}
