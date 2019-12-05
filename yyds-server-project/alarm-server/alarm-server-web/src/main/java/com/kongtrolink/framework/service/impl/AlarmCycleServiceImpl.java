package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.AlarmCycleDao;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.query.AlarmCycleQuery;
import com.kongtrolink.framework.service.AlarmCycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:00
 * @Description:
 */
@Service
public class AlarmCycleServiceImpl implements AlarmCycleService {

    @Autowired
    AlarmCycleDao cycleDao;
    private static final Logger logger = LoggerFactory.getLogger(AlarmCycleServiceImpl.class);

    @Override
    public boolean save(AlarmCycle alarmCycle) {
        cycleDao.save(alarmCycle);
        if(!StringUtil.isNUll(alarmCycle.getId())){
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String alarmCycleId) {
        AlarmCycle alarmCycle = get(alarmCycleId);
        boolean delete = cycleDao.delete(alarmCycleId);
        if(delete){
            //如果删除成功，先判定企业有没有启用状态的告警周期
            alarmCycle.setState(Contant.USEING);
            AlarmCycleQuery alarmCycleQuery = AlarmCycleQuery.alarmCycle2Query(alarmCycle);
            AlarmCycle one = getOne(alarmCycleQuery);
            if(null == one){
                alarmCycleQuery.setState(Contant.FORBIT);
                AlarmCycle lastUpdateOne = getLastUpdateOne(alarmCycleQuery);
                if(null != lastUpdateOne) {
                    alarmCycleQuery.setId(lastUpdateOne.getId());
                    alarmCycleQuery.setState(Contant.USEING);
                    updateState(alarmCycleQuery);
                }
            }
        }
        return delete;
    }

    @Override
    public boolean update(AlarmCycle alarmCycle) {
        return cycleDao.update(alarmCycle);
    }

    @Override
    public AlarmCycle get(String alarmCycleId) {
        return cycleDao.get(alarmCycleId);
    }

    @Override
    public List<AlarmCycle> list(AlarmCycleQuery cycleQuery) {
        return cycleDao.list(cycleQuery);
    }

    @Override
    public int count(AlarmCycleQuery cycleQuery) {
        return cycleDao.count(cycleQuery);
    }

    @Override
    public AlarmCycle getOne(AlarmCycleQuery alarmCycleQuery) {
        return cycleDao.getOne(alarmCycleQuery);
    }

    @Override
    public Map<String, AlarmCycle> entity2CodeSerrviceMap(List<AlarmCycle> alarmCycleList) {
        Map<String, AlarmCycle> map = new HashMap<>();
        if(null == alarmCycleList || alarmCycleList.size() == 0){
            return map;
        }
        for(AlarmCycle alarmCycle : alarmCycleList){
            map.put(alarmCycle.getEnterpriseCode() + Contant.UNDERLINE + alarmCycle.getServerCode(), alarmCycle);
        }
        return map;
    }

    @Override
    public boolean updateState(AlarmCycleQuery cycleQuery) {
        //如果是禁用，直接禁用；如果是启用，需要先禁用当前启用的规则
        String state = cycleQuery.getState();
        String enterpriseCode = cycleQuery.getEnterpriseCode();
        String serverCode = cycleQuery.getServerCode();
        Date curTime = new Date();
        if(Contant.USEING.equals(state)){
            cycleDao.forbitBefor(enterpriseCode, serverCode, curTime, cycleQuery.getOperator());
        }
        boolean result = cycleDao.updateState(enterpriseCode, serverCode, cycleQuery.getId(), state, curTime, cycleQuery.getOperator());
        return result;
    }

    @Override
    public AlarmCycle getLastUpdateOne(AlarmCycleQuery alarmCycleQuery) {
        return cycleDao.getLastUpdateOne(alarmCycleQuery);
    }

    @Override
    public AlarmCycle getSystemCycle() {
        return cycleDao.getSystemCycle();
    }

    /**
     * @auther: liudd
     * @date: 2019/10/28 16:57
     * 功能描述:初始化默认告警周期
     */
    @Override
    public void initAlarmCycle() {
        //获取系统默认告警周期
        AlarmCycle systemCycle = getSystemCycle();
        if(null == systemCycle){
            logger.info("默认告警周期不存在，准备初始化");
            systemCycle = new AlarmCycle();
            systemCycle.setName("系统默认告警周期");
            systemCycle.setDiffTime(24);
            systemCycle.setUpdateTime(new Date());
            systemCycle.setState(Contant.USEING);
            systemCycle.setCycleType(Contant.SYSTEM);
            systemCycle.setEnterpriseServer(Contant.SYSTEM);
        }
        logger.info("默认告警周期存在，无需初始化：{}", systemCycle.toString());
        cycleDao.save(systemCycle);
    }
}
