package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.service.AlarmService;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${alarm.currentLimit:5}")
    private int currentLimit;
    private static final String current_table = MongTable.ALARM_CURRENT;
    private static final String history_table = MongTable.ALARM_HISTORY;


    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2020/2/28 13:54
     * 功能描述:获取列表
     */
    @Override
    public List<DBObject> list(AlarmQuery alarmQuery) {
        if(Contant.CURR_ALARM.equals(alarmQuery.getType())){
            return listCurrent(alarmQuery);
        }else{
            return listHistory(alarmQuery);
        }
    }

    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:列表
     */
    private List<DBObject> listCurrent(AlarmQuery alarmQuery) {
        return alarmDao.listCurrent(alarmQuery, MongTable.ALARM_CURRENT);
    }

    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2019/12/5 19:20
     * 功能描述:历史告警伪分页
     */
    public List<DBObject> listHistory(AlarmQuery alarmQuery) {
        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + history_table + Contant.UNDERLINE;
        //1，确定时间。如果开始接结束都没有选择，开始结束为今天到一个月前的今天
        alarmQuery = initTime(alarmQuery);
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        System.out.println(weeks.toString());
        List<DBObject> allList = new ArrayList<>();
        int currentPage = alarmQuery.getCurrentPage();
        int pageSize = alarmQuery.getPageSize();
        int allSize = pageSize * (currentLimit+1);
        int beginNum = (currentPage - 1) * pageSize;
        int nextBegin = beginNum;   //下一个表分页起始数据
        int nextSize = allSize;    //下一个表获取数量
        //从各个表获取数据
        for(int i=weeks.size()-1; i>=0; i--){
            if(nextSize <= 0){
                break;
            }
            String table = tablePrefix + weeks.get(i);
            alarmQuery.setRealBeginNum(nextBegin);
            alarmQuery.setRealLimit(nextSize);
            List<DBObject> historyAlarmList = alarmDao.listHistory(alarmQuery, table);
            int tempCount = historyAlarmList.size();
            if(nextBegin > tempCount){
                nextBegin = nextSize - tempCount;
            }else {
                for(int m = nextBegin; m< tempCount; m++){
                    allList.add(historyAlarmList.get(m));
                }
                nextBegin = 0;
                nextSize = nextSize+nextBegin - tempCount;
            }
        }
        return allList;
    }

    /**0
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

    /**
     * @param table
     * @param date
     * @auther: liudd
     * @date: 2019/12/28 15:06
     * 功能描述:确认告警
     */
    @Override
    public boolean check(String key,  String table, Date date, String checkContant, FacadeView checker) {
        return alarmDao.check(key, table, date, checkContant, checker);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 12:10
     * 功能描述:根据告警类型和告警上报时间获取单个告警所在表名
     */
    private String getTable(String enterpriseCode, String serverCode, String type, Date treport){
        String table = current_table;
        if(Contant.HIST_ALARM.equals(type)){
            String tablePrefix = enterpriseCode + Contant.UNDERLINE + serverCode + Contant.UNDERLINE + MongTable.ALARM_HISTORY + Contant.UNDERLINE;
            String year_week = DateUtil.getYear_week(treport);
            table = tablePrefix + year_week;
        }
        return table;
    }

    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2020/3/2 13:28
     * 功能描述:告警确认，后期将中台的告警确认方法合并过来
     */
    @Override
    public boolean check(AlarmQuery alarmQuery) {
        String table = getTable(alarmQuery.getEnterpriseCode(), alarmQuery.getServerCode(), alarmQuery.getType(), alarmQuery.getTreport());
        return alarmDao.check(table, alarmQuery);
    }
}
