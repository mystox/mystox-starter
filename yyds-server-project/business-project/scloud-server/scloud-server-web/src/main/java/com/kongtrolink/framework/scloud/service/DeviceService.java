package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.DeviceSpecialInfoEntity;
import com.kongtrolink.framework.scloud.entity.RelatedDeviceInfo;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * 资产管理-设备资产管理 接口类
 * Created by Eric on 2020/2/12.
 */
public interface DeviceService {

    /**
     * 获取设备列表
     */
    List<DeviceModel> findDeviceList(String uniqueCode, DeviceQuery deviceQuery) throws Exception;

    /**
     * @auther: liudd
     * @date: 2020/4/7 14:07
     * 功能描述:获取设备列表
     */
    List<DeviceModel> listDeviceList(String uniqueCode, DeviceQuery deviceQuery) throws Exception;

    /**
     * 导出设备列表
     */
    HSSFWorkbook exportDeviceList(List<DeviceModel> list);

    /**
     * 生成设备编码
     */
    String createDeviceCode(String uniqueCode, DeviceModel deviceModel);

    /**
     * 添加设备
     *
     * @return 设备ID
     */
    Integer addDevice(String uniqueCode, DeviceModel deviceModel);

    /**
     * 修改设备
     */
    boolean modifyDevice(String uniqueCode, DeviceModel deviceModel);

    /**
     * 删除设备
     */
    void deleteDevice(String uniqueCode, DeviceQuery deviceQuery);

    /**
     * 获取特殊设备的特殊属性
     */
    DeviceSpecialInfoEntity getDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity);

    /**
     * 修改特殊设备特殊属性
     */
    void modifyDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity);

    /**
     * 保存特殊设备的特殊属性
     */
    void saveDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity);

    /**
     * 获取站点下FSU 列表
     */
    List<RelatedDeviceInfo> findFsuList(String uniqueCode, DeviceQuery deviceQuery);

    /**
     * 获取FSU下的关联设备
     */
    List<RelatedDeviceInfo> findRelatedDeviceList(String uniqueCode, DeviceQuery deviceQuery);

    /**
     * 获取站点下未关联FSU的设备 列表
     */
    List<RelatedDeviceInfo> findUnrelatedDeviceList(String uniqueCode, DeviceQuery deviceQuery);

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:获取设备实体类表，不包含名字，无语远程调用
     */
    List<DeviceEntity> listEntity(String uniqueCode, DeviceQuery deviceQuery);

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:统计设备列表数量
     */
    int countEntity(String uniqueCode, DeviceQuery deviceQuery);

    List<String> entityList2CodeList(List<DeviceEntity> deviceEntityList);
}
