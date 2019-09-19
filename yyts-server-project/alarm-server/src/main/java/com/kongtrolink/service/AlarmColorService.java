package com.kongtrolink.service;

import com.kongtrolink.enttiy.AlarmColor;
import com.kongtrolink.query.AlarmColorQuery;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/12 10:45
 * @Description:
 */
public interface AlarmColorService {

    void add(AlarmColor alarmColor);

    boolean delete(String alarmColorId);

    boolean update(AlarmColor alarmColor);

    List<AlarmColor> list(AlarmColorQuery colorQuery);

    int count(AlarmColorQuery colorQuery);
}
