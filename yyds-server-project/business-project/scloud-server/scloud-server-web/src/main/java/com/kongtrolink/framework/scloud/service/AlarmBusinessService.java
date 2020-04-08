package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.query.AlarmQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 18:42
 * @Description:
 */
public interface AlarmBusinessService {

    void add(String uniqueCode, String table, AlarmBusiness business);

    void add(String uniqueCode, String table, List<AlarmBusiness> businessList);

    List<AlarmBusiness> listByKeyList(String uniqueCode, String table, List<String> keyList);
}
