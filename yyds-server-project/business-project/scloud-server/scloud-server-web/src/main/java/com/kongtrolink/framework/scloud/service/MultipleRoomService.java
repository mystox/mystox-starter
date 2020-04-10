package com.kongtrolink.framework.scloud.service;


import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.model.RoomHistoryAlarmListModel;
import com.kongtrolink.framework.scloud.entity.multRoom.*;
import com.kongtrolink.framework.scloud.query.MultipleRoomQuery;
import com.kongtrolink.framework.scloud.query.RoomHistoryAlarmQuery;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import sun.misc.Signal;

import java.util.Date;
import java.util.List;

/**
 * 综合机房
 * by Mag on 6/20/2018.
 */
public interface MultipleRoomService {
    /**
     * 初始化要进行的数据
     * @param uniqueCode 企业编码
     */
    void initSignalType(String uniqueCode);
    /**
     * 获取 基本信息
     * @param uniqueCode 企业编码
     * @param siteId 站点ID
     * @return RoomSiteInfo
     */
    RoomSiteInfo getSiteInfo(String uniqueCode, int siteId);
    /**
     * 获取 设备列表
     * @param uniqueCode 企业编码
     * @param query 站点ID
     * @return 设备列表
     */
    List<RoomDevice> getRoomDevice(String uniqueCode, MultipleRoomQuery query) throws Exception;
    /**
     * 保存自定义信号点
     */
    boolean addShowSignalConfig(String uniqueCode, RoomSignalTypeConfig config);
    /**
     * 根据设备查询信号点配置 不包含默认值
     */
    RoomSignalTypeConfig queryRoomSignalTypeConfig(String uniqueCode, int deviceId);
    /**
     * 根据设备查询信号点配置包含默认值 前天界面用
     */
    RoomSignalTypeConfig queryRoomSignalTypeConfigShow(String uniqueCode, int deviceId);

}
