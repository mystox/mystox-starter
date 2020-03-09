package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.dao.DeviceMongo;
import com.kongtrolink.framework.scloud.dao.SiteMongo;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
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

import java.text.SimpleDateFormat;
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
     * 获取单个站点下设备列表
     */
    @Override
    public List<DeviceModel> findDeviceList(String uniqueCode, DeviceQuery deviceQuery) {
        List<DeviceModel> list = new ArrayList<>();

        //获取（业务平台端）站点下所有设备
        List<DeviceEntity> devices = deviceMongo.findDevicesBySiteCodes(uniqueCode, deviceQuery);
        if (devices != null && devices.size() > 0) {
            List<String> deviceCodes = new ArrayList<>();   //设备编码
            for (DeviceEntity device : devices) {
                deviceCodes.add(device.getCode());
            }
            deviceQuery.setDeviceCodes(deviceCodes);

            //从【中台-资管】获取设备(基本信息)列表
            MsgResult msgResult = assetCIService.getAssetDeviceList(uniqueCode, deviceQuery);
            int stateCode = msgResult.getStateCode();
            if (stateCode == 1) {   //通信成功
                CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
                if (response.getResult() == CommonConstant.SUCCESSFUL) { //请求成功
                    LOGGER.info("【设备管理】，从【资管】获取设备列表成功");
                    Map<String, BasicDeviceEntity> map = new HashMap<>();
                    for (JSONObject jsonObject : response.getInfos()) {
                        BasicDeviceEntity basicDeviceEntity = JSONObject.toJavaObject(jsonObject, BasicDeviceEntity.class);
                        map.put(basicDeviceEntity.getCode(), basicDeviceEntity);    //key:设备编码，value:设备基本信息
                    }

                    List<DeviceEntity> deviceEntityList = deviceMongo.findDeviceList(uniqueCode, deviceQuery);
                    Map<String, BasicSiteEntity> siteEntityMap = getAssetSiteByCodes(uniqueCode, deviceQuery, deviceEntityList);
                    for (DeviceEntity deviceEntity : deviceEntityList){
                        DeviceModel deviceModel = new DeviceModel();
                        deviceModel.setName(map.get(deviceEntity.getCode()).getDeviceName());
                        deviceModel.setModel(map.get(deviceEntity.getCode()).getModel());
                        if (siteEntityMap != null && siteEntityMap.containsKey(deviceEntity.getSiteCode())) {
                            deviceModel.setSiteName(siteEntityMap.get(deviceEntity.getSiteCode()).getName());
                        }

                        list.add(deviceModel);
                    }
                } else {
                    LOGGER.error("【设备管理】，从【资管】获取设备列表失败");
                }
            } else {
                LOGGER.error("【设备管理】，从【资管】获取设备列表 MQTT通信失败");
            }
        }

        return list;
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
        String pre = site.getTierCode()+site.getSiteType()+getTypeCode(deviceModel.getTypeCode());
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

    private String getTypeCode(String typeCode){
        if(typeCode==null || typeCode.length()<=2){
            return typeCode;
        }else if(typeCode.startsWith("18")){
            return typeCode;
        }else{
            return typeCode.substring(1,3);
        }

    }

    private boolean checkCodeExisted(String uniqueCode,String deviceCode){
        return deviceMongo.findDeviceByShortCode(uniqueCode, deviceCode) != null;
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
                row[10] = dateUtil.format(new Date(deviceModel.getOfflineTime()));
            }
        }
        return tableDatas;
    }

    //从【中台-资管】获取站点信息
    private Map<String, BasicSiteEntity> getAssetSiteByCodes(String uniqueCode, DeviceQuery deviceQuery, List<DeviceEntity> deviceEntityList){
        List<String> siteCodes = new ArrayList<>();
        for(DeviceEntity deviceEntity : deviceEntityList){
            siteCodes.add(deviceEntity.getSiteCode());
        }
        SiteQuery siteQuery = new SiteQuery();
        siteQuery.setServerCode(deviceQuery.getServerCode());
        siteQuery.setSiteCodes(siteCodes);

        //向【中台-资管】请求获取站点信息
        MsgResult msgResult = assetCIService.getAssetSiteByCode(uniqueCode, siteQuery);
        int stateCode = msgResult.getStateCode();
        if (stateCode == CommonConstant.SUCCESSFUL) {    //通信成功
            CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
            if (response.getResult() == CommonConstant.SUCCESSFUL) {    //请求成功
                Map<String, BasicSiteEntity> map = new HashMap<>();
                for (JSONObject jsonObject : response.getInfos()) {
                    BasicSiteEntity basicSiteEntity = JSONObject.toJavaObject(jsonObject, BasicSiteEntity.class);
                    map.put(basicSiteEntity.getCode(), basicSiteEntity);    //key：code站点编码，value：站点基本信息
                }
                return map;
            }else {
                return null;
            }
        }else{
            return null;
        }
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
