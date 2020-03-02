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

    /**
     * @auther: liudd
     * @date: 2020/3/2 15:53
     * 功能描述:根据批量删除
     */
    boolean deleteByIdList(String uniqueCode, List<String> idList);

    List<AlarmFocus> list(String uniqueCode, AlarmFocusQuery alarmFocusQuery);

    int count(String uniqueCode, AlarmFocusQuery alarmFocusQuery);

    List<String> list2AlarmIdList(List<AlarmFocus> alarmFocusList);

}
