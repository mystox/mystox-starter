package com.kongtrolink.service;

import com.kongtrolink.enttiy.DeviceTypeLevel;
import com.kongtrolink.query.DeviceTypeLevelQuery;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 16:41
 * @Description:
 */
public interface DeviceTypeLevelService {

    void add(DeviceTypeLevel deviceTypeLevel);

    boolean delete(String deviceTypeLevelId);

    boolean update(DeviceTypeLevel deviceTypeLevel);

    List<DeviceTypeLevel> list(DeviceTypeLevelQuery levelQuery);

    int count(DeviceTypeLevelQuery levelQuery);

    DeviceTypeLevel getOne(DeviceTypeLevelQuery levelQuery);

    boolean isReprat(DeviceTypeLevel typeLevel);
}
