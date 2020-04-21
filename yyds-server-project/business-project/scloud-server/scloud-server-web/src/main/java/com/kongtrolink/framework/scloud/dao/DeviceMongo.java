package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.DeviceSpecialInfoEntity;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备资产 相关数据查询类
 * Created by Eric on 2020/2/12.
 */
@Repository
public class DeviceMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 根据查询条件，获取设备列表
     */
    public List<DeviceEntity> findDeviceList(String uniqueCode, DeviceQuery deviceQuery){
        List<String> siteCodes = deviceQuery.getSiteCodes();    //站点编码
        String deviceTypeCode = deviceQuery.getDeviceTypeCode();	//设备类型编码
        String deviceCode = deviceQuery.getDeviceCode();	//设备编码（模糊搜索）
        List<String> deviceCodeList = deviceQuery.getDeviceCodes();
        String manufacturer = deviceQuery.getManufacturer();	//设备厂家(模糊搜索)
        Long startTime = deviceQuery.getStartTime();	//开始时间
        Long endTime = deviceQuery.getEndTime();	//结束时间
        String state = deviceQuery.getState();	//注册状态（FSU类型、摄像机类型）：在线、离线
        String operationState = deviceQuery.getOperationState();	//运行状态（FSU类型）：工程态、测试态、交维态
        Criteria criteria = new Criteria();
        if(null != siteCodes) {
             criteria = Criteria.where("siteCode").in(siteCodes);
        }
        if (!StringUtil.isNUll(deviceTypeCode)){
            criteria.and("typeCode").is(deviceTypeCode);
        }
        if (!StringUtil.isNUll(deviceCode)){
            deviceCode = MongoRegexUtil.escapeExprSpecialWord(deviceCode);
            criteria.and("code").regex("^" + deviceCode + ".*?");
        }
        if(null != deviceCodeList){
            criteria.and("code").in(deviceCodeList);
        }
        if (!StringUtil.isNUll(manufacturer)){
            manufacturer = MongoRegexUtil.escapeExprSpecialWord(manufacturer);
            criteria.and("manufacturer").regex(".*?" + manufacturer + ".*?");
        }
        if (!StringUtil.isNUll(state)){
            criteria.and("state").is(state);
        }
        if (!StringUtil.isNUll(operationState)){
            criteria.and("operationState").is(operationState);
        }
        if (startTime != null){
            if (endTime == null){
                criteria.and("createTime").gte(startTime);
            }else {
                criteria.and("createTime").gte(startTime).lte(endTime);
            }
        }else {
            if (endTime != null){
                criteria.and("createTime").lte(endTime);
            }
        }

        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "createTime"));

        return mongoTemplate.find(query, DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 统计设备数量
     */
    public Integer countDeviceShortCode(String uniqueCode, String preCode) {
        Criteria criteria = Criteria.where("code").regex("^" + preCode + ".*?");
        Query query = new Query(criteria);
        return (int)mongoTemplate.count(query, DeviceEntity.class,
                uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 通过设备编码查找设备
     */
    public DeviceEntity findDeviceByCode(String uniqueCode, String deviceCode) {
        return mongoTemplate.findOne(
                new Query(Criteria.where("code").is(deviceCode)),
                DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 添加保存设备
     */
    public Integer saveDevice(String uniqueCode, DeviceEntity deviceEntity){
        mongoTemplate.save(deviceEntity, uniqueCode + CollectionSuffix.DEVICE);
        return deviceEntity.getId();
    }

    /**
     * 修改设备
     */
    public boolean modifyDevice(String uniqueCode, DeviceModel deviceModel){
        Criteria criteria = Criteria.where("code").is(deviceModel.getCode());

        Update update = new Update();
        update.set("shelfLife", deviceModel.getShelfLife());
        update.set("isInsurance", deviceModel.getIsInsurance());
        update.set("manufacturer", deviceModel.getManufacturer());
        update.set("brand", deviceModel.getBrand());
        update.set("deviceDesc", deviceModel.getDeviceDesc());

        WriteResult result = mongoTemplate.updateFirst(new Query(criteria), update, uniqueCode + CollectionSuffix.DEVICE);
        return result.getN()>0;
    }

    /**
     * （批量）删除设备
     */
    public void deleteDevices(String uniqueCode, DeviceQuery deviceQuery){
        List<String> deviceCodes = deviceQuery.getDeviceCodes();

        Criteria criteria = new Criteria();
        if (deviceCodes != null && deviceCodes.size() > 0){
            criteria.and("code").in(deviceCodes);
        }
        mongoTemplate.remove(new Query(criteria), DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * （批量）删除特殊设备特殊属性
     */
    public void deleteDeviceSpecialInfo(String uniqueCode, DeviceQuery deviceQuery){
        List<String> deviceCodes = deviceQuery.getDeviceCodes();

        Criteria criteria = new Criteria();
        if (deviceCodes != null && deviceCodes.size() > 0){
            criteria.and("deviceCode").in(deviceCodes);
        }
        mongoTemplate.remove(new Query(criteria), DeviceSpecialInfoEntity.class, uniqueCode + CollectionSuffix.DEVICE_SPECIAL_INFO);
    }

    /**
     * 修改特殊设备特殊属性
     */
    public void modifyDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity){
        Criteria criteria = Criteria.where("deviceId").is(deviceSpecialInfoEntity.getDeviceId())
                .and("deviceCode").is(deviceSpecialInfoEntity.getDeviceCode());

        Update update = new Update();
        //FSU动环主机("038")属性
        update.set("adaptation", deviceSpecialInfoEntity.getAdaptation());
        update.set("engine", deviceSpecialInfoEntity.getEngine());
        update.set("sMinitor", deviceSpecialInfoEntity.getsMinitor());

        //开关电源("006")属性
        update.set("rectifacationCount", deviceSpecialInfoEntity.getRectifacationCount());
        update.set("rectifacationModel", deviceSpecialInfoEntity.getRectifacationModel());
        update.set("outVoltage", deviceSpecialInfoEntity.getOutVoltage());
        update.set("monitorState", deviceSpecialInfoEntity.getMonitorState());
        update.set("antiThunderState", deviceSpecialInfoEntity.getAntiThunderState());
        update.set("moduleState", deviceSpecialInfoEntity.getModuleState());

        //油机（柴油发电机组）("005")
        update.set("ratedVoltage", deviceSpecialInfoEntity.getRatedVoltage());
        update.set("ratedPower", deviceSpecialInfoEntity.getRatedPower());
        update.set("startMode", deviceSpecialInfoEntity.getStartMode());
        update.set("coolingMode", deviceSpecialInfoEntity.getCoolingMode());
        update.set("tankVolume", deviceSpecialInfoEntity.getTankVolume());

        //蓄电池组("007")
        update.set("groupCapacity", deviceSpecialInfoEntity.getGroupCapacity());
        update.set("monVoltageLevel", deviceSpecialInfoEntity.getMonVoltageLevel());
        update.set("groupCellCount", deviceSpecialInfoEntity.getGroupCellCount());

        //普通空调("015")
        update.set("refrigerationEffect", deviceSpecialInfoEntity.getRefrigerationEffect());
        update.set("unitState", deviceSpecialInfoEntity.getUnitState());
        update.set("drainpipeState", deviceSpecialInfoEntity.getDrainpipeState());
        update.set("selfStart", deviceSpecialInfoEntity.getSelfStart());
        update.set("refrigeratingCapacity", deviceSpecialInfoEntity.getRefrigeratingCapacity());
        update.set("inputRatedPower", deviceSpecialInfoEntity.getInputRatedPower());

        //UPS设备("008")
        update.set("inputRatedVoltage", deviceSpecialInfoEntity.getInputRatedVoltage());
        update.set("outputRatedVoltage", deviceSpecialInfoEntity.getOutputRatedVoltage());

        mongoTemplate.updateFirst(new Query(criteria), update, uniqueCode + CollectionSuffix.DEVICE_SPECIAL_INFO);
    }

    /**
     * 查找获取特殊设备的特殊属性
     */
    public DeviceSpecialInfoEntity findDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity){
        int deviceId = deviceSpecialInfoEntity.getDeviceId();
        String deviceCode = deviceSpecialInfoEntity.getDeviceCode();

        Criteria criteria = Criteria.where("deviceCode").is(deviceCode)
                .and("deviceId").is(deviceId);

        return mongoTemplate.findOne(new Query(criteria), DeviceSpecialInfoEntity.class, uniqueCode + CollectionSuffix.DEVICE_SPECIAL_INFO);
    }

    /**
     * 保存特殊设备特殊属性
     */
    public void saveDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity){
        mongoTemplate.save(deviceSpecialInfoEntity, uniqueCode + CollectionSuffix.DEVICE_SPECIAL_INFO);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:获取设备实体类表，不包含名字，无语远程调用
     */
    public List<DeviceEntity> listEntity(String uniqueCode, DeviceQuery deviceQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, deviceQuery);
        Query query = Query.query(criteria);
        query.skip( (deviceQuery.getCurrentPage()-1) * deviceQuery.getPageSize() ).limit(deviceQuery.getPageSize());
        return mongoTemplate.find(query, DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:统计设备列表数量
     */
    public int countEntity(String uniqueCode, DeviceQuery deviceQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, deviceQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + CollectionSuffix.DEVICE);
    }

    Criteria baseCriteria(Criteria criteria, DeviceQuery deviceQuery){
        List<String> siteCodeList = deviceQuery.getSiteCodes();
        if(null != siteCodeList){
            criteria.and("siteCode").in(siteCodeList);
        }
        String operationState = deviceQuery.getOperationState();
        if(!StringUtil.isNUll(operationState)){
            criteria.and("operationState").is(operationState);
        }
        String deviceTypeCode = deviceQuery.getDeviceTypeCode();
        if(!StringUtil.isNUll(deviceTypeCode)){
            criteria.and("typeCode").is(deviceTypeCode);
        }
        return criteria;
    }

    /**
     * @param uniqueCode
     * @param siteCodeList
     * @auther: liudd
     * @date: 2020/3/6 9:36
     * 功能描述:根据站点编码列表获取设备列表，不需要设备名称，无需远程调用
     */
    public List<DeviceEntity> getBySiteCodeList(String uniqueCode, List<String> siteCodeList) {
        Criteria criteria = Criteria.where("siteCode").in(siteCodeList);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 更新FSU注册状态及相关属性
     * 【使用场景】：网关上报FSU在线(注册)/离线
     */
    public void updateFsu(DeviceEntity deviceEntity){
        Criteria criteria = Criteria.where("code").is(deviceEntity.getCode());

        Update update = new Update();
        update.set("state", deviceEntity.getState());
        update.set("ip", deviceEntity.getIp());
        update.set("enterpriseCode", deviceEntity.getEnterpriseCode());
        update.set("serverCode", deviceEntity.getServerCode());
        update.set("gatewayServerCode", deviceEntity.getGatewayServerCode());
        if (!StringUtil.isNUll(deviceEntity.getIp())){
            update.set("ip", deviceEntity.getIp());
        }
        if (deviceEntity.getOfflineTime() != null) {
            update.set("offlineTime", deviceEntity.getOfflineTime());
        }

        mongoTemplate.updateFirst(new Query(criteria), update, DeviceEntity.class, deviceEntity.getEnterpriseCode() + CollectionSuffix.DEVICE);
    }

    public boolean isExistFsu(String uniqueCode,String code){
        Criteria criteria = Criteria.where("code").is(code).and("typeCode").is(CommonConstant.DEVICE_TYPE_CODE_FSU);
        return mongoTemplate.count(new Query(criteria),uniqueCode + CollectionSuffix.DEVICE) > 0;
    }

    /**
     * 更新FSU运行状态
     */
    public void updateFsuOperationState(String uniqueCode, String fsuCode, String fsuOperationState){
        Criteria criteria = Criteria.where("code").is(fsuCode);

        Update update = new Update();
        if (fsuOperationState != null){
            update.set("operationState", fsuOperationState);
        }
        mongoTemplate.updateFirst(new Query(criteria), update, DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 根据设备类型编码, 查询出所有指定设备
     */
    public List<DeviceEntity> findSpecificDevices(String uniqueCode, String deviceTypeCode){
        Criteria criteria = Criteria.where("typeCode").is(deviceTypeCode);
        return mongoTemplate.find(new Query(criteria), DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }
}
