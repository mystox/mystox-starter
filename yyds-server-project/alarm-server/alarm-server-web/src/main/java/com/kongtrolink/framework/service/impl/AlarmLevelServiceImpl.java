package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.AlarmLevelDao;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
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
        //删除原有告警等级
        int resNum = deleteList(alarmLevel.getEnterpriseCode(), alarmLevel.getServerCode(), alarmLevel.getDeviceType(), alarmLevel.getDeviceModel());
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
}
