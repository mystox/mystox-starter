package com.kongtrolink.framework.service;

import com.kongtrolink.framework.config.RedisConfig;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.dao.DevTypeDao;
import com.kongtrolink.framework.execute.module.model.DevType;
import com.kongtrolink.framework.jsonType.JsonDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fengw
 * 内部设备与铁塔设备ID匹配服务类
 * 新建文件 2019-4-17 08:46:28
 */
@Service
public class DeviceMatchService {

    @Autowired
    DevTypeDao devTypeDao;

    @Autowired
    RedisUtils redisUtils;

    private final static int CNTB_TYPE_START_INDEX = 6;
    private final static int CNTB_TYPE_END_INDEX = 10;
    private final static int CNTB_DEVICE_ID_LENGTH = 14;

    /**
     * 匹配内部设备ID与铁塔设备ID
     * @param cntbList 当前需要进行匹配的铁塔设备ID列表
     * @param curList 当前已知内部设备列表
     */
    public void matchingDevice(List<JsonDevice> cntbList, List<JsonDevice> curList) {
        if (curList == null || curList.size() == 0) {
            return;
        }

        clearMatch(cntbList);
        sortCntbDevList(cntbList);
        sortDevList(curList);

        //todo 遍历curList，在cntbList中查找对应的铁塔设备ID，并将信息填入cntbList中
        for (int i = 0; i < curList.size(); ++i) {
            String cntbType = getCntbType(curList.get(i).getType());
            if (cntbType == null) {
                //获取不到铁塔设备类型，跳过
                continue;
            }
            for (int j = 0; j < cntbList.size(); ++j) {
                JsonDevice jsonDevice = cntbList.get(j);
                if (jsonDevice.getDeviceId().indexOf(cntbType) == CNTB_TYPE_START_INDEX &&
                        jsonDevice.getType().equals(null)) {
                    jsonDevice.setPort(curList.get(i).getPort());
                    jsonDevice.setType(curList.get(i).getType());
                    jsonDevice.setResNo(curList.get(i).getResNo());
                    continue;
                }
            }
        }
    }

    /**
     * 获取内部设备类型对应的铁塔设备类型
     * @param type 内部设备类型
     * @return 铁塔设备类型，若未找到，返回null
     */
    private String getCntbType(String type) {
        if (type == null) {
            return null;
        }
        if (redisUtils.hasKey(RedisConfig.CNTBTYPE_TYPE_HASH)) {
            Map<String, String> map = devTypeDao.getAll();
            Map<String, Object> tmp = new HashMap<>();
            //遍历map中的键
            for (String key : map.keySet()) {
                tmp.put(key, map.get(key));
            }
            if (!redisUtils.hmset(RedisConfig.CNTBTYPE_TYPE_HASH, tmp, 0)) {
                //redis中添加失败，返回null
                return null;
            }
        }
        if (!redisUtils.hHasKey(RedisConfig.CNTBTYPE_TYPE_HASH, type)) {
            DevType devType = devTypeDao.getInfoByType(type);
            if (!redisUtils.hset(RedisConfig.CNTBTYPE_TYPE_HASH, type, devType.getCntbType())) {
                //redis中添加失败，返回null
                return null;
            }
        }
        return redisUtils.hget(RedisConfig.CNTBTYPE_TYPE_HASH, type).toString();
    }

    /**
     * 根据铁塔设备ID对列表排序
     * @param devList 设备列表
     */
    private void sortCntbDevList(List<JsonDevice> devList) {
        Collections.sort(devList, (arg0, arg1) -> {
            String type0 = arg0.getDeviceId().substring(CNTB_TYPE_START_INDEX, CNTB_TYPE_END_INDEX);
            String no0 = arg0.getDeviceId().substring(CNTB_TYPE_END_INDEX, CNTB_DEVICE_ID_LENGTH);

            String type1 = arg1.getDeviceId().substring(CNTB_TYPE_START_INDEX, CNTB_TYPE_END_INDEX);
            String no1 = arg1.getDeviceId().substring(CNTB_TYPE_END_INDEX, CNTB_DEVICE_ID_LENGTH);
            if (type0.equals(type1)) {
                return no0.compareTo(no1);
            } else {
                return type0.compareTo(type1);
            }
        });
    }

    /**
     * 清除列表中的匹配关系
     * @param devList 待清除列表
     */
    private void clearMatch(List<JsonDevice> devList) {
        for (int i = 0; i < devList.size(); ++i) {
            devList.get(i).setPort(null);
            devList.get(i).setResNo(null);
            devList.get(i).setType(null);
        }
    }

    /**
     * 根据内部设备类型与资源编号对列表排序
     * @param devList 设备列表
     */
    private void sortDevList(List<JsonDevice> devList) {
        Collections.sort(devList, (arg0, arg1) -> {
            if ((arg0.getType() == null && arg1.getType() == null) || (arg0.getType().equals(arg1.getType()))) {
                if (arg0.getResNo() == null && arg1.getResNo() == null) {
                    return 0;
                } else if (arg0.getResNo() == null) {
                    return -1;
                } else if (arg1.getResNo() == null) {
                    return 1;
                } else {
                    return Integer.valueOf(arg0.getResNo()).compareTo((Integer.valueOf(arg1.getResNo())));
                }
            } else {
                return arg0.getType().compareTo(arg1.getType());
            }
        });
    }
}
