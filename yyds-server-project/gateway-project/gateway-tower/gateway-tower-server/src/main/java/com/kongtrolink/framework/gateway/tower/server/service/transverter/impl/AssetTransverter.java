package com.kongtrolink.framework.gateway.tower.server.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinatowercom.scservice.InvokeResponse;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.iaiot.core.assent.DeviceReport;
import com.kongtrolink.framework.gateway.iaiot.core.assent.DeviceReportExtend;
import com.kongtrolink.framework.gateway.iaiot.core.assent.DeviceReportExtendInfo;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.Device;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.XmlList;
import com.kongtrolink.framework.gateway.tower.core.util.GatewayTowerUtil;
import com.kongtrolink.framework.gateway.tower.server.entity.DeviceConfigEntity;
import com.kongtrolink.framework.gateway.tower.server.entity.Transverter;
import com.kongtrolink.framework.gateway.tower.server.mqtt.GatewayMqttSenderNative;
import com.kongtrolink.framework.gateway.tower.server.service.DeviceTypeConfig;
import com.kongtrolink.framework.gateway.tower.server.service.transverter.TransverterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/10/16, 15:28.
 * company: kongtrolink
 * description: 资产协议转换器
 * update record:
 */
@Service
public class AssetTransverter extends TransverterHandler {
    @Value("${gateway.assetReport.version:1.0.0}")
    private String assetServerVersion;
    @Autowired
    private DeviceTypeConfig deviceTypeConfig;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    GatewayMqttSenderNative gatewayMqttSenderNative;
    private static final Logger logger = LoggerFactory.getLogger(AssetTransverter.class);

    public void transferExecute(String payload,String sn) {
        try{
            XmlList deviceList = JSONObject.parseObject(payload,XmlList.class);
            String jsonResult = getAssetServerResult(deviceList,sn);
            if(jsonResult==null){
                return;
            }
            logger.info(jsonResult);
            reportMsg(MqttUtils.preconditionServerCode(ServerName.ASSET_MANAGEMENT_SERVER,assetServerVersion),
                    OperaCode.DEVICE_REPORT,jsonResult);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private String getAssetServerResult(XmlList deviceList, String sn){
        try{
            if(deviceList==null || deviceList.getDeviceList()==null || deviceList.getDeviceList().size()==0){
                logger.info("推送设备资产信息为空 ");
                return null;
            }
            DeviceReport deviceReport = new DeviceReport();
            deviceReport.setSn(sn);
            deviceReport.setUser(getUser());
            deviceReport.setEnterpriseCode(getEnterpriseCode());
            deviceReport.setServerCode(getBusinessCode());
            deviceReport.setGatewayServerCode(MqttUtils.preconditionServerCode(getServerName(),getServerVersion()));
            deviceReport.setRegionCode(getRegionCode());
            DeviceConfigEntity deviceConfigEntity = deviceTypeConfig.getAssentDeviceType(getFsuType());
            if(deviceConfigEntity!=null){
                deviceReport.setType(deviceConfigEntity.getAssentType());
            }
            DeviceReportExtend extend= new DeviceReportExtend();
            extend.setModel(deviceReport.getType());
            deviceReport.setExtend(extend);
            List<DeviceReport> childDevices = new ArrayList<>();
            String redisKey = deviceTypeConfig.getDeviceRedisKey(); //获取设备存储的redisKey
            for(Device device:deviceList.getDeviceList()){
                DeviceReport deviceReportChild = new DeviceReport();
                deviceReportChild.setSn(sn +"_" + device.getId());
                deviceReportChild.setEnterpriseCode(getEnterpriseCode());
                deviceReportChild.setUser(getUser());
                deviceReportChild.setServerCode(getBusinessCode());
                deviceReportChild.setGatewayServerCode(MqttUtils.preconditionServerCode(getServerName(),getServerVersion()));
                deviceReportChild.setRegionCode(getRegionCode());
                String deviceType = GatewayTowerUtil.getDeviceTypeFromId(device.getId());
                DeviceConfigEntity deviceConfig = deviceTypeConfig.getAssentDeviceType(deviceType);
                String type = deviceType;
                if(deviceConfig !=null){
                    type = deviceConfig.getAssentType();
                }
                deviceReportChild.setType(type);
                DeviceReportExtendInfo extendInfo = new DeviceReportExtendInfo();
                extendInfo.setModel(type);
                deviceReportChild.setExtend(extendInfo);
                childDevices.add(deviceReportChild);
                //将上报的设备保持到redis中 key是 sn_deviceId
                redisUtils.hset(redisKey,sn +"_" + device.getId(),device);
            }
            deviceReport.setChildDevices(childDevices);
            logger.debug("上报资管的 数据: \n");
            logger.debug(JSONObject.toJSONString(deviceReport));
            logger.debug("\n");
            return JSONObject.toJSONString(deviceReport);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
