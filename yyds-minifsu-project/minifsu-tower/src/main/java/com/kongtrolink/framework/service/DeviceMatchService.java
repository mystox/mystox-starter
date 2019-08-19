package com.kongtrolink.framework.service;

import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.execute.module.model.DevType;
import com.kongtrolink.framework.utils.CommonUtils;
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
    CommonUtils commonUtils;

    private final static int CNTB_TYPE_START_INDEX = 7;
    private final static int CNTB_TYPE_END_INDEX = 9;
    private final static int CNTB_DEVICE_ID_LENGTH = 14;

    /**
     * 匹配内部设备ID与铁塔设备ID
     * @param cntbList 当前需要进行匹配的铁塔设备ID列表
     * @param curList 当前已知内部设备列表
     */
    void matchingDevice(List<JsonDevice> cntbList, List<JsonDevice> curList) {

        clearMatch(cntbList);
        sortCntbDevList(cntbList);

        if (curList == null || curList.size() == 0) {
            return;
        }

        sortDevList(curList);

        //遍历curList，在cntbList中查找对应的铁塔设备ID，并将信息填入cntbList中
        for (JsonDevice curDevice : curList) {
            DevType devType = commonUtils.getDevTypeByType(curDevice.getType());
            if (devType == null) {
                //获取不到铁塔设备类型，跳过
                continue;
            }
            for (JsonDevice jsonDevice : cntbList) {
                if (jsonDevice.getDeviceId().substring(CNTB_TYPE_START_INDEX).startsWith(devType.getCntbType()) &&
                        jsonDevice.getPort() == null) {
                    jsonDevice.setPort(curDevice.getPort());
                    jsonDevice.setType(curDevice.getType());
                    jsonDevice.setResNo(curDevice.getResNo());
                    break;
                }
            }
        }
    }

    /**
     * 根据铁塔设备ID对列表排序
     * @param devList 设备列表
     */
    private void sortCntbDevList(List<JsonDevice> devList) {
        devList.sort((arg0, arg1) -> {
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
    public String getCntbType(String deviceId) {
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
        for (JsonDevice jsonDevice : devList) {
            jsonDevice.setPort(null);
            jsonDevice.setType(-1);
            jsonDevice.setResNo(-1);
        }
    }

    /**
     * 根据内部设备类型与资源编号对列表排序
     * @param devList 设备列表
     */
    private void sortDevList(List<JsonDevice> devList) {
        devList.sort((arg0, arg1) -> {
            if (arg0.getType() == arg1.getType()) {
                return Integer.compare(arg0.getResNo(), arg1.getResNo());
            } else {
                return Integer.compare(arg0.getType(), arg1.getType());
            }
        });
    }
}
