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
        return cycleDao.delete(alarmCycleId);
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
        boolean result ;
        String state = cycleQuery.getState();
        String enterpriseCode = cycleQuery.getEnterpriseCode();
        String serverCode = cycleQuery.getServerCode();
        Date curTime = new Date();
        if(Contant.USEING.equals(state)){
            //如果是启用，需要禁用当前所有告警周期
            result = cycleDao.forbitBefor(enterpriseCode, serverCode, curTime, cycleQuery.getOperator());
            if(result) {
                result = cycleDao.updateState(enterpriseCode, serverCode, cycleQuery.getId(), state, curTime, cycleQuery.getOperator());
            }
            return result;
        }
        //如果是禁用，需要启动当前企业默认告警周期
        //1,获取企业默认告警周期
        AlarmCycle systemCycle = cycleDao.getSystemCycle(enterpriseCode, serverCode);
        if(null == systemCycle){
            return false;
        }
        result = cycleDao.updateState(enterpriseCode, serverCode, cycleQuery.getId(), state, curTime, cycleQuery.getOperator());
        if(result) {
            //禁用后，启用系统默认告警周期
            result = cycleDao.updateState(enterpriseCode, serverCode, systemCycle.getId(), Contant.USEING, curTime, cycleQuery.getOperator());
        }
        return result;
    }

    @Override
    public AlarmCycle getLastUpdateOne(AlarmCycleQuery alarmCycleQuery) {
        return cycleDao.getLastUpdateOne(alarmCycleQuery);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/28 16:57
     * 功能描述:初始化默认告警周期
     */
    @Override
    public void initAlarmCycle() {
        //获取系统默认告警周期
        AlarmCycle systemCycle = cycleDao.getSystemCycle(null, null);
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

    /**
     * @param enterpriseServer
     * @param serverCode
     * @auther: liudd
     * @date: 2019/12/27 11:09
     * 功能描述:处理企业默认告警周期
     */
    @Override
    public void handleUniqueDefault(String enterpriseServer, String serverCode) {
        //判断企业默认告警是否存在
        AlarmCycle enterpriseSystemCycle = cycleDao.getSystemCycle(enterpriseServer, serverCode);
        if(null == enterpriseSystemCycle){
            //如果企业默认告警周期不存在，使用系统默认告警周期初始化企业默认告警周期
            AlarmCycle systemCycle = cycleDao.getSystemCycle(null, null);
            if(null == systemCycle){
                logger.error("系统默认告警周期不存在，请重启服务");
                return;
            }
            systemCycle.setId(null);
            systemCycle.setName("企业默认告警周期");
            systemCycle.setEnterpriseCode(enterpriseServer);
            systemCycle.setServerCode(serverCode);
            systemCycle.setEnterpriseServer(enterpriseServer+Contant.UNDERLINE+serverCode);
            systemCycle.setUpdateTime(new Date());
            systemCycle.setState(Contant.USEING);
            cycleDao.save(systemCycle);
        }
    }

    /**
     * @param enterpriseServer
     * @param serverCode
     * @param name
     * @auther: liudd
     * @date: 2019/12/27 14:55
     * 功能描述:根据名称获取
     */
    @Override
    public AlarmCycle getByName(String enterpriseServer, String serverCode, String name) {
        return cycleDao.getByName(enterpriseServer, serverCode, name);
    }
}
