package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.EnterpriseLevelDao;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.DeviceTypeLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:46
 * @Description:
 */
@Service
public class EnterpriseLevelServiceImpl implements EnterpriseLevelService{

    @Autowired
    EnterpriseLevelDao enterpriseLevelDao;
    @Autowired
    AlarmLevelService alarmLevelService;
    @Autowired
    DeviceTypeLevelService typeLevelService;

    @Override
    public void add(EnterpriseLevel enterpriseLevel) {
        List<String> levels = enterpriseLevel.getLevels();
        List<String> levelNames = enterpriseLevel.getLevelNames();
        List<String> colors = enterpriseLevel.getColors();
        enterpriseLevel.setLevels(null);
        enterpriseLevel.setLevelNames(null);
        enterpriseLevel.setColors(null);
        for(int i=0; i< levels.size(); i++){
            enterpriseLevel.setId(null);
            enterpriseLevel.setLevel(levels.get(i));
            enterpriseLevel.setLevelName(levelNames.get(i));
            enterpriseLevel.setColor(colors.get(i));
            enterpriseLevelDao.add(enterpriseLevel);
        }
    }

    @Override
    public boolean delete(String enterpriseLevelId) {
        //如果当前企业等级是启用状态，需要根据默认企业等级生成新得告警等级
        EnterpriseLevel enterpriseLevel = get(enterpriseLevelId);
        if(null == enterpriseLevel){
            return false;
        }
        boolean result = deleteByCode(enterpriseLevel.getCode());
        if(result) {
            if (Contant.USEING.equals(enterpriseLevel.getState())) {
                alarmLevelService.deleteList(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode(), null, null);
                addAlarmLevelByEnterpriseInfo(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode());
            }
        }
        return result;
    }

    @Override
    public boolean deleteByCode(String code) {
        boolean result = enterpriseLevelDao.deleteByCode(code);
        return result;
    }

    @Override
    public boolean update(EnterpriseLevel enterpriseLevel) {
        enterpriseLevel.setUpdateTime(new Date());
        //根据code删除
        boolean delRes = deleteByCode(enterpriseLevel.getCode());
        if(delRes){
            add(enterpriseLevel);
        }
        //如果该企业等级属于启用状态，需要重新生成告警等级
        String state = enterpriseLevel.getState();
        if(Contant.USEING.equals(state)){
            alarmLevelService.deleteList(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode(), null, null);
            addAlarmLevelByEnterpriseInfo(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode());
        }

        return delRes;
    }

    @Override
    public EnterpriseLevel get(String enterpriseLevelId) {
        return enterpriseLevelDao.get(enterpriseLevelId);
    }

    @Override
    public List<EnterpriseLevel> list(EnterpriseLevelQuery levelQuery) {
        return enterpriseLevelDao.list(levelQuery);
    }

    @Override
    public int count(EnterpriseLevelQuery levelQuery) {
        return enterpriseLevelDao.count(levelQuery);
    }

    @Override
    public EnterpriseLevel getOne(EnterpriseLevelQuery levelQuery) {
        return enterpriseLevelDao.getOne(levelQuery);
    }


    /**
     * @auther: liudd
     * @date: 2019/10/16 15:54
     * 功能描述:修改状态
     */
    @Override
    public boolean updateState(EnterpriseLevelQuery enterpriseLevelQuery) {
        Date curTime = new Date();
        String state = enterpriseLevelQuery.getState();
        String enterpriseCode = enterpriseLevelQuery.getEnterpriseCode();
        String serverCode = enterpriseLevelQuery.getServerCode();
        if(Contant.FORBIT.equals(state)){
            //如果禁用，需要删除原来的告警等级，然后使用默认企业等级生成告警等级
            alarmLevelService.deleteList(enterpriseCode, serverCode, null, null);
        }else {
            //先禁用原来的企业等级
            boolean result = forbitBefor(enterpriseCode, serverCode, curTime);
            if(!result){
                return false;
            }
            alarmLevelService.deleteList(enterpriseCode, serverCode, null, null);
        }
        addAlarmLevelByEnterpriseInfo(enterpriseCode, serverCode);
        return enterpriseLevelDao.updateState(enterpriseLevelQuery);
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 15:02
     * 功能描述:获取最后一次启用的企业告警，如果没有，则获取默认企业告警
     */
    @Override
    public List<EnterpriseLevel> getLastUse(String enterpriseCode, String serverCode) {
        return enterpriseLevelDao.getLastUse(enterpriseCode, serverCode);
    }

    @Override
    public List<EnterpriseLevel> getDefault() {
        return enterpriseLevelDao.getDefault();
    }

    @Override
    public void addAlarmLevelByEnterpriseInfo(String enterpriseCode, String serverCode) {
        List<DeviceTypeLevel> deviceTypeLevels = typeLevelService.listByEnterpriseInfo(enterpriseCode, serverCode);
        if(null == deviceTypeLevels || deviceTypeLevels.size() == 0){
            return ;
        }
        for(DeviceTypeLevel deviceTypeLevel : deviceTypeLevels){
            typeLevelService.addAlarmLevelByDeviceLevel(deviceTypeLevel);
        }
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 20:18
     * 功能描述:禁用原来的告警等级
     */
    @Override
    public boolean forbitBefor(String enterpriseCode, String serverCode, Date updateTime) {
        return enterpriseLevelDao.forbitBefor(enterpriseCode, serverCode, updateTime);
    }
}
