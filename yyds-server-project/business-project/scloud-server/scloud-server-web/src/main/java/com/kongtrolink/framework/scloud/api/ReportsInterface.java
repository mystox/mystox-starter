package com.kongtrolink.framework.scloud.api;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
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
    public List<SiteModel> getSiteListByEnterpriseCode(String msg);
    @OperaCode
    List<DeviceModel> getFsuSCloud(String msg);

    @OperaCode
    JSONObject stationElectricCountList(String msg);

    @OperaCode(description = "历史告警导出统计")
    public JSONObject exportAlarmHistory(String msg);
}