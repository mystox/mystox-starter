package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.AlarmLevelDao;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:06
 * @Description:
 */
@Service
public class AlarmLevelServiceImpl implements AlarmLevelService {
    @Autowired
    AlarmLevelDao alarmLevelDao;
    @Autowired
    EnterpriseLevelService enterpriseLevelService;

    @Override
    public boolean save(AlarmLevel alarmLevel) {
        alarmLevelDao.save(alarmLevel);
        if(!StringUtil.isNUll(alarmLevel.getId())){
            return true ;
        }
        return false;
    }

    @Override
    public boolean save(List<AlarmLevel> alarmLevelList) {
        return alarmLevelDao.save(alarmLevelList);
    }

    @Override
    public boolean delete(String alarmLevelId) {
        return alarmLevelDao.delete(alarmLevelId);
    }

    @Override
    public boolean update(AlarmLevel alarmLevel) {
        //删除原有告警等级
        int resNum = deleteList(alarmLevel.getEnterpriseCode(), alarmLevel.getServerCode(), alarmLevel.getDeviceType(), alarmLevel.getDeviceModel());
        boolean result = resNum > 0 ? true : false;
        if(result){
            List<String> sourceLevelList = alarmLevel.getSourceLevelList();
            List<String> targetLevelList = alarmLevel.getTargetLevelList();
            List<String> targetLevelNameList = alarmLevel.getTargetLevelNameList();
            List<String> colorList = alarmLevel.getColorList();
            alarmLevel.setSourceLevelList(null);
            alarmLevel.setTargetLevelList(null);
            alarmLevel.setTargetLevelNameList(null);
            alarmLevel.setColorList(null);
            for(int i=0; i<sourceLevelList.size(); i++){
                alarmLevel.setId(null);
                alarmLevel.setSourceLevel(sourceLevelList.get(i));
                alarmLevel.setTargetLevel(targetLevelList.get(i));
                alarmLevel.setTargetLevelName(targetLevelNameList.get(i));
                alarmLevel.setColor(colorList.get(i));
                save(alarmLevel);
            }
        }
        return result;
    }

    @Override
    public List<AlarmLevel> list(AlarmLevelQuery levelQuery) {
        return alarmLevelDao.list(levelQuery);
    }

    @Override
    public int count(AlarmLevelQuery levelQuery) {
        return alarmLevelDao.count(levelQuery);
    }

    @Override
    public AlarmLevel getOne(AlarmLevelQuery alarmLevelQuery) {
        return alarmLevelDao.getOne(alarmLevelQuery);
    }

    @Override
    public boolean isRepeat(AlarmLevel alarmLevel) {
        AlarmLevelQuery levelQuery = AlarmLevelQuery.entity2Query(alarmLevel);
        AlarmLevel one = getOne(levelQuery);
        if(null == one){
            return false;
        }
        if(one.getId().equals(alarmLevel.getId())){
            return false;
        }
        return true;
    }

    /**
     * @param levelQuery
     * @auther: liudd
     * @date: 2019/9/21 9:59
     * 功能描述：根据uniqueCode， service，deviceType， sourceLevel获取告警等级
     * 如果没有，则使用该系统默认告警点等级
     * 并将新告警等级和告警颜色等存入告警
     */
    @Override
    public AlarmLevel getAlarmLevel(AlarmLevelQuery levelQuery) {
        AlarmLevel one = getOne(levelQuery);
        if(null == one) {
            //没有找到匹配的等级自定义，使用系统默认等级
            EnterpriseLevelQuery enterpriseLevelQuery = new EnterpriseLevelQuery();
            enterpriseLevelQuery.setEnterpriseCode(levelQuery.getEnterpriseCode());
            enterpriseLevelQuery.setServerCode(levelQuery.getServerCode());
            enterpriseLevelQuery.setDefaultLevel(Contant.YES);
            EnterpriseLevel enterpriseLevel = enterpriseLevelService.getOne(enterpriseLevelQuery);
            if (null != enterpriseLevel) {
                one = new AlarmLevel();
                one.setSourceLevel(levelQuery.getSourceLevel());
//                one.setTargetLevel(enterpriseLevel.getLevel());
//                one.setColor(enterpriseLevel.getColor());
            }
        }
        return one;
    }

    @Override
    public AlarmLevel createAlarmLevel(EnterpriseLevel enterpriseLevel, DeviceTypeLevel deviceTypeLevel) {
        if(null == enterpriseLevel || null == deviceTypeLevel) {
            return null;
        }
        AlarmLevel alarmLevel = new AlarmLevel();
        alarmLevel.setEnterpriseCode(enterpriseLevel.getEnterpriseCode());
        alarmLevel.setServerCode(enterpriseLevel.getServerCode());
        alarmLevel.setDeviceType(deviceTypeLevel.getDeviceType());
        alarmLevel.setDeviceModel(deviceTypeLevel.getDeviceModel());
//        alarmLevel.setSourceLevel(deviceTypeLevel.getLevel());
//        alarmLevel.setTargetLevel(enterpriseLevel.getLevel());
//        alarmLevel.setColor(enterpriseLevel.getColor());
        alarmLevel.setGenerate(Contant.SYSTEM);
        return alarmLevel;
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @param deviceType
     * @param deviceModel
     * @auther: liudd
     * @date: 2019/10/16 15:41
     * 功能描述:根据设备类型信息删除告警等级
     */
    @Override
    public int deleteList(String enterpriseCode, String serverCode, String deviceType, String deviceModel) {
        return alarmLevelDao.deleteList(enterpriseCode, serverCode, deviceType, deviceModel);
    }

    public List<AlarmLevel> getByEntDevCodeList(List<String> entDevCodeList){
        return alarmLevelDao.getByEntDevCodeList(entDevCodeList);
    }
}
