package com.kongtrolink.framework.scloud.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.query.AuxilaryQuery;

/**
 * @Auther: liudd
 * @Date: 2020/4/3 09:31
 * @Description:
 */
public interface AuxilaryService {

    JSONObject getByEnterServerCode(AuxilaryQuery auxilaryQuery);

    JSONObject delete(AuxilaryQuery auxilaryQuery);

}
