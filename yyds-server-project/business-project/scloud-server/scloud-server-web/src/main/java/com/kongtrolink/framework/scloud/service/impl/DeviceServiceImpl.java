package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.dao.DeviceMongo;
import com.kongtrolink.framework.scloud.dao.SiteMongo;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.DeviceSpecialInfoEntity;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicDeviceEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicSiteEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.CIResponseEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.AssetCIService;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.util.DateUtil;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 资产管理-设备资产管理 接口实现类
 * Created by Eric on 2020/2/12.
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    DeviceMongo deviceMongo;
    @Autowired
    SiteMongo siteMongo;
    @Autowired
    MqttOpera mqttOpera;
    @Autowired
    AssetCIService assetCIService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceImpl.class);

    /**
     * 获取站点下设备列表
     */
    @Override
    public List<DeviceModel>  findDeviceList(String uniqueCode, DeviceQuery deviceQuery) throws Exception{
        List<DeviceModel> list = new ArrayList<>();
        //根据条件，获取站点Map(key：站点编码，value：站点基本信息)
        Map<String, BasicSiteEntity> siteEntityMap = getAssetSiteByCodes(uniqueCode, deviceQuery);
        if (siteEntityMap.size() > 0) {
            List<String> siteCodes = new ArrayList<>(siteEntityMap.keySet());
            deviceQuery.setSiteCodes(siteCodes);

            //获取（业务平台数据库）站点下所有设备
            List<DeviceEntity> devices = deviceMongo.findDeviceList(uniqueCode, deviceQuery);
            if (devices != null && devices.size() > 0){
                List<String> deviceCodes = new ArrayList<>();   //设备编码
                Map<String, DeviceEntity> deviceEntityMap = new HashMap<>();
                for (DeviceEntity device : devices) {
                    deviceCodes.add(device.getCode());
                    deviceEntityMap.put(device.getCode(), device);  //key:设备编码，value：设备（扩展）信息
                }
                deviceQuery.setDeviceCodes(deviceCodes);

                //从【中台-资管】获取设备(基本信息)列表
                MsgResult msgResult = assetCIService.getAssetDeviceList(uniqueCode, deviceQuery);
                if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL){ //通信成功
                    CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
                    if (response.getResult() == CommonConstant.SUCCESSFUL) { //请求成功
                        LOGGER.info("【设备管理】，从【资管】获取设备列表成功");
                        Map<String, BasicDeviceEntity> map = new HashMap<>();
                        for (JSONObject jsonObject : response.getInfos()) {
                            BasicDeviceEntity basicDeviceEntity = JSONObject.parseObject(jsonObject.toJSONString(), BasicDeviceEntity.class);
                            map.put(basicDeviceEntity.getCode(), basicDeviceEntity);    //key:设备编码，value:设备基本信息
                        }
                        if (map.size() > 0){
                            for (String deviceCode : map.keySet()){
                                DeviceEntity device = deviceEntityMap.get(deviceCode);

                                DeviceModel deviceModel = getDeviceModel(device);
                                deviceModel.setName(map.get(deviceCode).getDeviceName());
                                deviceModel.setModel(map.get(deviceCode).getModel());
                                deviceModel.setSiteName(siteEntityMap.get(device.getSiteCode()).getName());

                                list.add(deviceModel);
                            }
                        }
                    } else {
                        LOGGER.error("【设备管理】，从【资管】获取设备列表失败");
                    }
                }else {
                    LOGGER.error("【设备管理】，从【资管】获取设备列表 MQTT通信失败");
                }
            }
        }
        return list;
    }

    /**
     * 获取特殊设备的特殊属性
     */
    @Override
    public DeviceSpecialInfoEntity getDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity) {
        DeviceSpecialInfoEntity entity = deviceMongo.findDeviceSpecialInfo(uniqueCode, deviceSpecialInfoEntity);
        return entity;
    }

    /**
     * 导出设备列表
     */
    @Override
    public HSSFWorkbook exportDeviceList(List<DeviceModel> list) {
        String[][] userSheet = getDeviceListAsTable(list);

        HSSFWorkbook workBook = ExcelUtil.getInstance().createWorkBook(
                new String[] {"设备信息列表"}, new String[][][] { userSheet });
        return workBook;
    }

    /**
     * 生成设备编码
     */
    @Override
    public String createDeviceCode(String uniqueCode, DeviceModel deviceModel) {
        SiteEntity site = siteMongo.findSiteByCode(uniqueCode, deviceModel.getSiteCode());
        String pre = site.getTierCode() + getSiteTypeIntVal(site.getSiteType()) + getTypeCode(deviceModel.getTypeCode());
        // 生成设备流水号
        Integer num = deviceMongo.countDeviceShortCode(uniqueCode, pre);
        String deviceCode = "";
        String format = "%05d";
        if(deviceModel.getTypeCode().startsWith("18")){
            format = "%04d";
        }
        do {
            num += 1;
            deviceCode = pre + String.format(format, num);
        } while (checkCodeExisted(uniqueCode, deviceCode));

        return deviceCode;
    }

    /**
     * 添加设备
     *
     * @return 设备ID
     */
    @Override
    public Integer addDevice(String uniqueCode, DeviceModel deviceModel) {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setTierCode(deviceModel.getTierCode());
        deviceEntity.setSiteCode(deviceModel.getSiteCode());
        deviceEntity.setSiteId(deviceModel.getSiteId());
        deviceEntity.setType(deviceModel.getType());
        deviceEntity.setTypeCode(deviceModel.getTypeCode());
        deviceEntity.setCode(deviceModel.getCode());
        deviceEntity.setCreateTime(deviceModel.getCreateTime());
        deviceEntity.setShelfLife(deviceModel.getShelfLife());
        deviceEntity.setIsInsurance(deviceModel.getIsInsurance());
        deviceEntity.setManufacturer(deviceModel.getManufacturer());
        deviceEntity.setBrand(deviceModel.getBrand());
        deviceEntity.setDeviceDesc(deviceModel.getDeviceDesc());

        //向【资管】下发（批量）添加设备的MQTT消息
        MsgResult msgResult = assetCIService.addAssetDevice(uniqueCode, deviceModel);
        if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL){ //通信成功
            CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
            if (response.getResult() == CommonConstant.SUCCESSFUL) {
                LOGGER.info("【设备管理】,向【资管】发送添加设备MQTT 请求成功");
                //保存设备（扩展信息）
                Integer deviceId = deviceMongo.saveDevice(uniqueCode, deviceEntity);
                return deviceId;
            }else {
                LOGGER.error("【设备管理】,向【资管】发送添加设备MQTT 请求失败");
                return null;
            }
        }else {
            LOGGER.error("【设备管理】,向【资管】发送添加设备MQTT 通信失败");
            return null;
        }
    }

    /**
     * 修改设备
     */
    @Override
    public boolean modifyDevice(String uniqueCode, DeviceModel deviceModel) {
        String isModified = deviceModel.getIsModified(); //修改设备时，是否修改了设备名称和设备型号
        boolean modifyResult = false;
        if (isModified.equals(CommonConstant.MODIFIED)){    //如果修改了设备的基本属性（即设备名称和设备型号）
            //向【资管】下发修改设备的MQTT消息
            MsgResult msgResult = assetCIService.modifyAssetDevice(uniqueCode, deviceModel);
            if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL) {
                CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
                if (response.getResult() == CommonConstant.SUCCESSFUL) {    //请求成功
                    LOGGER.info("向【资管】发送修改设备MQTT 请求成功");
                    //对平台端数据库中保存的设备扩展信息进行修改
                    modifyResult = deviceMongo.modifyDevice(uniqueCode, deviceModel);
                }else {
                    LOGGER.error("向【资管】发送修改设备MQTT 请求失败");
                }
            }else {
                LOGGER.error("向【资管】发送修改设备MQTT 通信失败");
            }
        }else {
            //对平台端数据库中保存的设备扩展信息进行修改
            modifyResult = deviceMongo.modifyDevice(uniqueCode, deviceModel);
        }

        return modifyResult;
    }

    /**
     * 删除设备
     */
    @Override
    public void deleteDevice(String uniqueCode, DeviceQuery deviceQuery) {
        //向【资管】下发删除（批量）设备的MQTT消息
        MsgResult msgResult = assetCIService.deleteAssetDevice(uniqueCode, deviceQuery);
        if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL){
            CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
            if (response.getResult() == CommonConstant.SUCCESSFUL) {
                LOGGER.info("向【资管】发送删除设备MQTT 请求成功");
                //（批量）删除平台端数据库中保存的设备
                deviceMongo.deleteDevices(uniqueCode, deviceQuery);

                //将设备从设备特殊属性表中(批量)删除
                deviceMongo.deleteDeviceSpecialInfo(uniqueCode, deviceQuery);
            }else {
                LOGGER.error("向【资管】发送删除设备MQTT 请求失败");
            }
        }else {
            LOGGER.error("向【资管】发送删除设备MQTT 通信失败");
        }
    }

    /**
     * 修改特殊设备特殊属性
     */
    @Override
    public void modifyDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity) {
        deviceMongo.modifyDeviceSpecialInfo(uniqueCode, deviceSpecialInfoEntity);
    }

    /**
     * 保存特殊设备的特殊属性
     */
    @Override
    public void saveDeviceSpecialInfo(String uniqueCode, DeviceSpecialInfoEntity deviceSpecialInfoEntity) {
        deviceMongo.saveDeviceSpecialInfo(uniqueCode, deviceSpecialInfoEntity);
    }

    private String getTypeCode(String typeCode){
        if(typeCode==null || typeCode.length()<=2){
            return typeCode;
        }else if(typeCode.startsWith("18")){
            return typeCode;
        }else{
            return typeCode.substring(1,3);
        }
    }

    //获取站点类型对应
    private int getSiteTypeIntVal(String siteType){
        int siteTypeIntVal = 0;
        switch (siteType){
            case "A级机房":
                siteTypeIntVal = 1;
                break;
            case "B级机房":
                siteTypeIntVal = 2;
                break;
            case "C级机房":
                siteTypeIntVal = 3;
                break;
            case "D级机房":
                siteTypeIntVal = 4;
                break;
        }
        return siteTypeIntVal;
    }

    private boolean checkCodeExisted(String uniqueCode,String deviceCode){
        return deviceMongo.findDeviceByCode(uniqueCode, deviceCode) != null;
    }

    private String[][] getDeviceListAsTable(List<DeviceModel> list) {
        int colNum = 11;
        int rowNum = list.size() + 1;
        DateUtil dateUtil = DateUtil.getInstance();

        String[][] tableDatas = new String[rowNum][colNum];

        for (int i = 0; i < rowNum; i++) {
            String[] row = tableDatas[i];
            if (i == 0) {
                row[0] = "设备编码";
                row[1] = "站点名称";
                row[2] = "设备类型";
                row[3] = "设备名称";
                row[4] = "区域层级";
                row[5] = "生产厂家";
                row[6] = "投入使用时间";
                row[7] = "注册状态";
                row[8] = "运行状态";
                row[9] = "IP地址";
                row[10] = "离线时间";
            } else {
                DeviceModel deviceModel = list.get(i - 1);
                row[0] = deviceModel.getCode();
                row[1] = deviceModel.getSiteName();
                row[2] = deviceModel.getType();
                row[3] = deviceModel.getName();
                row[4] = deviceModel.getTierName();
                row[5] = deviceModel.getManufacturer();
                row[6] = dateUtil.format(new Date(deviceModel.getCreateTime()), "yyyy-MM-dd");
                row[7] = deviceModel.getState();
                row[8] = deviceModel.getOperationState();
                row[9] = deviceModel.getIp();
                row[10] = deviceModel.getOfflineTime() != null? dateUtil.format(new Date(deviceModel.getOfflineTime())) : "-";
            }
        }
        return tableDatas;
    }

    //根据条件，获取站点Map(key：站点编码，value：站点基本信息)
    private Map<String, BasicSiteEntity> getAssetSiteByCodes(String uniqueCode, DeviceQuery deviceQuery) throws Exception{
        List<String> siteCodes = deviceQuery.getSiteCodes() == null? deviceQuery.getSiteCodes() : new ArrayList<>();
        List<String> tierCodes = deviceQuery.getTierCodes();
        List<String> siteCodeList = new ArrayList<>();
        if (tierCodes != null && tierCodes.size() > 0){ //如果用户选择了整个区域，查询区域下的站点
            List<SiteEntity> siteEntities = siteMongo.findSitesByTierCodes(uniqueCode, tierCodes);
            if (siteEntities != null && siteEntities.size() > 0){
                for (SiteEntity siteEntity : siteEntities){
                    siteCodeList.add(siteEntity.getCode()); //区域下站点的站点编码
                }
            }
        }
        if (siteCodeList.size() > 0){
            for (String code : siteCodeList){
                siteCodes.add(code);    //整合站点编码
            }
        }

        Map<String, BasicSiteEntity> map = new HashMap<>();
        SiteQuery siteQuery = new SiteQuery();
        siteQuery.setServerCode(deviceQuery.getServerCode());
        if (deviceQuery.getSiteName() != null && !deviceQuery.getSiteName().equals("")) {   //存在站点名称模糊搜索条件
            siteQuery.setSiteName(deviceQuery.getSiteName());
        }
        siteQuery.setSiteCodes(siteCodes);
        //从【中台-资管】获取站点信息
        MsgResult msgResult = assetCIService.getAssetSiteByCode(uniqueCode, siteQuery);
        if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL){ //通信成功
            CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
            if (response.getResult() == CommonConstant.SUCCESSFUL) {    //请求成功
                for (JSONObject jsonObject : response.getInfos()) {
                    BasicSiteEntity basicSiteEntity = JSONObject.parseObject(jsonObject.toJSONString(), BasicSiteEntity.class);
                    map.put(basicSiteEntity.getCode(), basicSiteEntity);    //key：code站点编码，value：站点基本信息
                }
            }else {
                throw new Exception("从资管获取站点,请求失败");
            }
        }else {
            throw new Exception("从资管获取站点,通信失败");
        }

        return map;
    }

    //将设备属性赋值给返回给前端的设备数据模型
    private DeviceModel getDeviceModel(DeviceEntity device){
        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setId(device.getId());
        deviceModel.setTierCode(device.getTierCode());
        deviceModel.setSiteCode(device.getSiteCode());
        deviceModel.setSiteId(device.getSiteId());
        deviceModel.setType(device.getType());
        deviceModel.setTypeCode(device.getTypeCode());
        deviceModel.setCode(device.getCode());
        deviceModel.setCreateTime(device.getCreateTime());
        deviceModel.setShelfLife(device.getShelfLife());
        deviceModel.setIsInsurance(device.getIsInsurance());
        deviceModel.setManufacturer(device.getManufacturer());
        deviceModel.setBrand(device.getBrand());
        deviceModel.setDeviceDesc(device.getDeviceDesc());
        deviceModel.setIp(device.getIp());
        deviceModel.setState(device.getState());
        deviceModel.setOfflineTime(device.getOfflineTime());
        deviceModel.setOperationState(device.getOperationState());

        return deviceModel;
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:获取设备实体类表，不包含名字，无语远程调用
     */
    @Override
    public List<DeviceEntity> listEntity(String uniqueCode, DeviceQuery deviceQuery) {
        return deviceMongo.listEntity(uniqueCode, deviceQuery);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:统计设备列表数量
     */
    @Override
    public int countEntity(String uniqueCode, DeviceQuery deviceQuery) {
        return 0;
    }

    /**
     * @param uniqueCode
     * @param code
     * @auther: liudd
     * @date: 2020/3/3 10:49
     * 功能描述:根据设备编码获取单个设备
     */
    @Override
    public DeviceModel getByCode(String uniqueCode, String code) {
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2020/3/3 10:49
     * 功能描述:根据设备编码获取设备列表
     */
    @Override
    public List<DeviceModel> getByCodeList(String uniqueCode, List<String> deviceCodeList) {
        return null;
    }

    @Override
    public List<String> entityList2CodeList(List<DeviceEntity> deviceEntityList) {
        if(null == deviceEntityList){
            return null;
        }
        List<String> deviceCodeList = new ArrayList<>();
        for(DeviceEntity deviceEntity : deviceEntityList){
            deviceCodeList.add(deviceEntity.getCode());
        }
        return deviceCodeList;
    }
}
