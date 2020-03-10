package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.DeviceSpecialInfoEntity;
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
     * 获取特殊设备的特殊属性
     */
    DeviceSpecialInfoEntity getDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity);

    /**
     * 导出设备列表
     */
    HSSFWorkbook exportDeviceList(List<DeviceModel> list);

    /**
     * 生成设备编码
     */
    String createDeviceCode(String uniqueCode, DeviceModel deviceModel);

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

    /**
     * @auther: liudd
     * @date: 2020/3/3 10:49
     * 功能描述:根据设备编码获取单个设备
     */
    DeviceModel getByCode(String uniqueCode, String code);

    /**
     * @auther: liudd
     * @date: 2020/3/3 10:49
     * 功能描述:根据设备编码获取设备列表
     */
    List<DeviceModel> getByCodeList(String uniqueCode, List<String> deviceCodeList);

    List<String> entityList2CodeList(List<DeviceEntity> deviceEntityList);
}
