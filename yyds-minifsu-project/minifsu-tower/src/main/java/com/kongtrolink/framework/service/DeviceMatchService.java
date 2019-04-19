package com.kongtrolink.framework.service;

import com.kongtrolink.framework.entity.RedisTable;
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

    private final static int CNTB_TYPE_START_INDEX = 7;
    private final static int CNTB_TYPE_END_INDEX = 9;
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
                        jsonDevice.getPort() == null) {
                    jsonDevice.setPort(curList.get(i).getPort());
                    jsonDevice.setType(curList.get(i).getType());
                    jsonDevice.setResNo(curList.get(i).getResNo());
                    break;
                }
            }
        }
    }

    /**
     * 获取内部设备类型对应的铁塔设备类型
     * @param type 内部设备类型
     * @return 铁塔设备类型，若未找到，返回null
     */
    private String getCntbType(int type) {
        if (!redisUtils.hasKey(RedisTable.CNTBTYPE_TYPE_HASH)) {
            Map<Integer, String > map = devTypeDao.getAll();
            Map<String, Object> tmp = new HashMap<>();
            //遍历map中的键
            for (Integer key : map.keySet()) {
                tmp.put(key.toString(), map.get(key));
            }
            if (!redisUtils.hmset(RedisTable.CNTBTYPE_TYPE_HASH, tmp, 0)) {
                //redis中添加失败，返回null
                return null;
            }
        }
        if (!redisUtils.hHasKey(RedisTable.CNTBTYPE_TYPE_HASH, String.valueOf(type))) {
            DevType devType = devTypeDao.getInfoByType(type);
            if (devType == null ||
                !redisUtils.hset(RedisTable.CNTBTYPE_TYPE_HASH, String.valueOf(type), devType.getCntbType())) {
                //找不到对应设备类型或redis中添加失败，返回null
                return null;
            }
        }
        return redisUtils.hget(RedisTable.CNTBTYPE_TYPE_HASH, String.valueOf(type)).toString();
    }

    /**
     * 根据铁塔设备ID对列表排序
     * @param devList 设备列表
     */
    private void sortCntbDevList(List<JsonDevice> devList) {
        Collections.sort(devList, (arg0, arg1) -> {
            String type0 = getCntbType(arg0.getDeviceId());
            String no0 = arg0.getDeviceId().substring(CNTB_TYPE_END_INDEX, CNTB_DEVICE_ID_LENGTH);

            String type1 = getCntbType(arg1.getDeviceId());
            String no1 = arg1.getDeviceId().substring(CNTB_TYPE_END_INDEX, CNTB_DEVICE_ID_LENGTH);

            if (type0.equals(type1)) {
                return no0.compareTo(no1);
            } else {
                return type0.compareTo(type1);
            }
        });
    }

    /**
     * 从铁塔设备Id中获取设备类型
     * @param deviceId 铁塔设备Id
     * @return 设备类型
     */
    private String getCntbType(String deviceId) {
        String result = deviceId.substring(CNTB_TYPE_START_INDEX, CNTB_TYPE_END_INDEX);
        if (result.equals("18")) {
            //若为18，则是环境变量，需再取一位作为设备类型
            result = deviceId.substring(CNTB_TYPE_START_INDEX, CNTB_TYPE_END_INDEX + 1);
        }
        return result;
    }

    /**
     * 清除列表中的匹配关系
     * @param devList 待清除列表
     */
    private void clearMatch(List<JsonDevice> devList) {
        for (int i = 0; i < devList.size(); ++i) {
            devList.get(i).setPort(null);
            devList.get(i).setType(-1);
            devList.get(i).setResNo(-1);
        }
    }

    /**
     * 根据内部设备类型与资源编号对列表排序
     * @param devList 设备列表
     */
    private void sortDevList(List<JsonDevice> devList) {
        Collections.sort(devList, (arg0, arg1) -> {
            if (arg0.getType() == arg1.getType()) {
                return Integer.valueOf(arg0.getResNo()).compareTo((Integer.valueOf(arg1.getResNo())));
            } else {
                return Integer.valueOf(arg0.getType()).compareTo(Integer.valueOf(arg1.getType()));
            }
        });
    }
}
