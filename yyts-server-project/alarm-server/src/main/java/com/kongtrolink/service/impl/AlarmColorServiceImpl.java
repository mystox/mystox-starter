package com.kongtrolink.service.impl;

import com.kongtrolink.dao.AlarmColorDao;
import com.kongtrolink.enttiy.AlarmColor;
import com.kongtrolink.query.AlarmColorQuery;
import com.kongtrolink.service.AlarmColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/12 10:45
 * @Description:
 */
@Service
public class AlarmColorServiceImpl implements AlarmColorService {

    @Autowired
    AlarmColorDao colorDao;


    @Override
    public void add(AlarmColor alarmColor) {

    }

    @Override
    public boolean delete(String alarmColorId) {
        return false;
    }

    @Override
    public boolean update(AlarmColor alarmColor) {
        return false;
    }

    @Override
    public List<AlarmColor> list(AlarmColorQuery colorQuery) {
        return null;
    }

    @Override
    public int count(AlarmColorQuery colorQuery) {
        return 0;
    }
}
