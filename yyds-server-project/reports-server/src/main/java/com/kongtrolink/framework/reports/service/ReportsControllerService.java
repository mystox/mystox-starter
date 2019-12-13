package com.kongtrolink.framework.reports.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.register.entity.PrivFuncEntity;
import com.kongtrolink.framework.reports.entity.ReportWebConfig;

import java.util.List;

/**
 * Created by mystoxlol on 2019/10/31, 9:36.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface ReportsControllerService {
    JSONObject getReportsTask(JSONObject body);


    JSONObject getReportsTaskResult(JSONObject body);

    List<JSONObject> getReportsOperaCodeList();

    void saveConfigData(JSONObject data);

    ReportWebConfig getConfigData(String serverCode, String enterpriseCode, String funcPrivCode);

    PrivFuncEntity getPrivData();
}
