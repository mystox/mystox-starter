package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.AlarmLevelDao;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.DeviceTypeLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    @Autowired
    DeviceTypeLevelService deviceTypeLevelService;

    @Override
    public boolean save(AlarmLevel alarmLevel) {
        alarmLevelDao.save(alarmLevel);
        if(!StringUtil.isNUll(alarmLevel.getId())){
            return true ;
        }
        return false;
    }

    @Override
    public boolean update(AlarmLevel alarmLevel) {
        String enterpriseCode = alarmLevel.getEnterpriseCode();
        String serverCode = alarmLevel.getServerCode();
        String deviceType = alarmLevel.getDeviceType();
        String deviceModel = alarmLevel.getDeviceModel();
        //先获取需要删除的告警等级
        String deleteKey = getDeleteKey(enterpriseCode, serverCode, deviceType, deviceModel);
        //删除原有告警等级
        int resNum = deleteList(enterpriseCode, serverCode, deviceType, deviceModel);
        boolean result = resNum > 0 ? true : false;
        if(result){
            List<Integer> sourceLevelList = alarmLevel.getSourceLevelList();
            List<Integer> targetLevelList = alarmLevel.getTargetLevelList();
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
        //修改等级模块告警等级信息
        String key = enterpriseCode + Contant.EXCLAM + serverCode + Contant.EXCLAM + deviceType + Contant.EXCLAM + deviceModel;
        deviceTypeLevelService.updateAlarmLevelModel(Contant.UPDATE, Contant.DEVICELEVEL, key, deleteKey);
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

    /**
     * @param enterpriseCode
     * @param serverCode
     * @param deviceType
     * @param deviceModel
     * @auther: liudd
     * @date: 2019/12/4 19:57
     * 功能描述:根据设备信息获取
     */
    @Override
    public List<AlarmLevel> getByInfo(String enterpriseCode, String serverCode, String deviceType, String deviceModel) {
        return alarmLevelDao.getByInfo(enterpriseCode, serverCode, deviceType, deviceModel);
    }

    @Override
    public String getDeleteKey(String enterpriseCode, String serverCode, String deviceType, String deviceModel) {
        String deleteKey = null;
        //删除设备等级，先根据设备等级获取所有告警等级
        List<AlarmLevel> alarmLevelList = getByInfo(enterpriseCode, serverCode, deviceType, deviceModel);
        if(null != alarmLevelList && alarmLevelList.size()>0){
            StringBuilder stringBuilder = new StringBuilder();
            for(AlarmLevel alarmLevel : alarmLevelList) {
                stringBuilder.append(alarmLevel.initKey()).append(",");
            }
            deleteKey = stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
        }
        return deleteKey;
    }
}
