package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.AlarmBusinessDao;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.AlarmBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 18:43
 * @Description:
 */
@Service
public class AlarmBusinessServiceImpl implements AlarmBusinessService{

    @Autowired
    AlarmBusinessDao businessDao;

    @Override
    public void add(String uniqueCode, String table, AlarmBusiness business) {
        businessDao.add(uniqueCode, table, business);
    }

    @Override
    public void add(String uniqueCode, String table, List<AlarmBusiness> businessList) {
        businessDao.add(uniqueCode, table, businessList);
    }

    @Override
    public List<AlarmBusiness> listByKeyList(String uniqueCode, String table, List<String> keyList) {
        return businessDao.listByKeyList(uniqueCode, table, keyList);
    }
}
