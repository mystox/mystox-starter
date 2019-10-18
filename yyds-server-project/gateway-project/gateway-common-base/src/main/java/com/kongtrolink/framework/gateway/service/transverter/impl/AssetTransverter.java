package com.kongtrolink.framework.gateway.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.service.DeviceTypeConfig;
import com.kongtrolink.framework.gateway.service.transverter.TransverterHandler;
import com.kongtrolink.framework.gateway.tower.entity.assent.DeviceReport;
import com.kongtrolink.framework.gateway.tower.entity.rec.PushDeviceAsset;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushDeviceAssetDevice;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushDeviceAssetDeviceList;
import com.kongtrolink.framework.stereotype.Transverter;
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
@Transverter(name = "asset")
public class AssetTransverter extends TransverterHandler {
    @Value("gateway.assetReport.version:1.0.0")
    private String assetServerVersion;
    @Autowired
    private DeviceTypeConfig deviceTypeConfig;
    @Autowired
    RedisUtils redisUtils;

    private static final Logger logger = LoggerFactory.getLogger(AssetTransverter.class);

    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {
        try{
            String payLoad = parseProtocol.getPayload();//
            String sn = parseProtocol.getSn();
            PushDeviceAsset alarmReport = JSONObject.parseObject(payLoad,PushDeviceAsset.class);
            PushDeviceAssetDeviceList deviceAssetDeviceList = alarmReport.getPayload();
            if(deviceAssetDeviceList==null || deviceAssetDeviceList.getDevices()==null || deviceAssetDeviceList.getDevices().size()==0){
                logger.info("推送设备资产信息为空 payLoad:{} ",payLoad);
                return;
            }
            DeviceReport deviceReport = new DeviceReport();
            deviceReport.setSn(sn);
            deviceReport.setEnterpriseCode(getEnterpriseCode());
            deviceReport.setServerCode(getBusinessCode());
            deviceReport.setGatewayServerCode(MqttUtils.preconditionServerCode(getServerName(),getServerVersion()));
            deviceReport.setRegionCode(getRegionCode());
            deviceReport.setDeviceType(deviceTypeConfig.getAssentDeviceType(getFsuType()));
            List<DeviceReport> childDevices = new ArrayList<>();
            String redisKey = deviceTypeConfig.getDeviceRedisKey(); //获取设备存储的redisKey
            for(PushDeviceAssetDevice device:deviceAssetDeviceList.getDevices()){
                DeviceReport deviceReportChild = new DeviceReport();
                deviceReportChild.setSn(sn +"_" + device.getId());
                deviceReportChild.setEnterpriseCode(getEnterpriseCode());
                deviceReportChild.setServerCode(getBusinessCode());
                deviceReportChild.setGatewayServerCode(MqttUtils.preconditionServerCode(getServerName(),getServerVersion()));
                deviceReportChild.setRegionCode(getRegionCode());
                deviceReportChild.setDeviceType(deviceTypeConfig.getAssentDeviceType(device.getType()));
                deviceReportChild.setExtend(device);
                childDevices.add(deviceReportChild);
                //将上报的设备保持到redis中 key是 sn_deviceId
                redisUtils.hset(redisKey,sn +"_" + device.getId(),device);
            }
            deviceReport.setChildDevices(childDevices);
            String jsonResult = JSONObject.toJSONString(deviceReport);
            reportMsg(MqttUtils.preconditionServerCode(ServerName.ASSET_MANAGEMENT_SERVER,assetServerVersion),
                    OperaCode.DEVICE_REPORT,jsonResult);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
