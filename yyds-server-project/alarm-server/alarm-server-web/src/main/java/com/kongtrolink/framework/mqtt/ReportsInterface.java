package com.kongtrolink.framework.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/2 13:19
 * \* Description:
 * \
 */
@Register
public interface ReportsInterface {

    @OperaCode
    public List<JSONObject> getAlarmCountByDeviceIdList(String msg);


    @OperaCode
    public List<JSONObject> getAlarmsByDeviceList(String msg);


    public List<JSONObject> getAlarmCategoryByDeviceIdList(String msg);

}