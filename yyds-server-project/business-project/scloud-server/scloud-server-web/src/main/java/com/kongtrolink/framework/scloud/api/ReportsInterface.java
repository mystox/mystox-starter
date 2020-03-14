package com.kongtrolink.framework.scloud.api;

import com.alibaba.fastjson.JSONObject;
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

    List<JSONObject> getFsuSCloud(String msg);
}