package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.AlarmFocus;
import com.kongtrolink.framework.scloud.query.AlarmFocusQuery;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 13:12
 * @Description:
 */
public interface AlarmFocusService {

    boolean add(String uniqueCode, AlarmFocus alarmFocus);

    boolean delete(String uniqueCode, String id);

    List<AlarmFocus> list(String uniqueCode, AlarmFocusQuery alarmFocusQuery);

    int count(String uniqueCode, AlarmFocusQuery alarmFocusQuery);
}
