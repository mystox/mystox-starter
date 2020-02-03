package com.kongtrolink.framework.gateway.iaiot.server.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.iaiot.server.entity.DeviceConfigEntity;
import com.kongtrolink.framework.gateway.iaiot.server.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.iaiot.server.entity.Transverter;
import com.kongtrolink.framework.gateway.iaiot.server.mqtt.base.MqttPubTopic;
import com.kongtrolink.framework.gateway.iaiot.server.service.TopicConfig;
import com.kongtrolink.framework.gateway.iaiot.server.service.transverter.TransverterHandler;
import com.kongtrolink.framework.gateway.iaiot.server.mqtt.GatewayMqttSenderNative;
import com.kongtrolink.framework.gateway.iaiot.server.service.DeviceTypeConfig;
import com.kongtrolink.framework.gateway.iaiot.core.assent.DeviceReport;
import com.kongtrolink.framework.gateway.iaiot.core.assent.DeviceReportExtend;
import com.kongtrolink.framework.gateway.iaiot.core.assent.DeviceReportExtendInfo;
import com.kongtrolink.framework.gateway.iaiot.core.rec.PushDeviceAsset;
import com.kongtrolink.framework.gateway.iaiot.core.rec.info.PushDeviceAssetDevice;
import com.kongtrolink.framework.gateway.iaiot.core.rec.info.PushDeviceAssetDeviceList;
import com.kongtrolink.framework.gateway.iaiot.core.send.AckBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/10/16, 15:28.
 * company: kongtrolink
 * description: 资产协议转换器
 * update record:
 */
@Transverter("asset")
public class AssetTransverter extends TransverterHandler {
    @Value("${gateway.assetReport.version:1.0.0}")
    private String assetServerVersion;
    @Autowired
    private DeviceTypeConfig deviceTypeConfig;
    @Autowired
    private TopicConfig topicConfig;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    GatewayMqttSenderNative gatewayMqttSenderNative;
    private static final Logger logger = LoggerFactory.getLogger(AssetTransverter.class);

    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {
        try{
            String payLoad = parseProtocol.getPayload();//
            String sn = parseProtocol.getSn();
            PushDeviceAsset alarmReport = JSONObject.parseObject(payLoad,PushDeviceAsset.class);
            String jsonResult = getAssetServerResult(alarmReport.getPayload(),sn);
            if(jsonResult==null){
                return;
            }
            logger.info(jsonResult);
            reportMsg(MqttUtils.preconditionServerCode(ServerName.ASSET_MANAGEMENT_SERVER,assetServerVersion),
                    OperaCode.DEVICE_REPORT,jsonResult);
            assentAck(sn,alarmReport.getMsgId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 返回FSU 响应消息体
     * @param sn sn
     * @param msgId 消息唯一标识
     */
    private void assentAck(String sn,String msgId){
        AckBase ackBase = new AckBase(msgId);
        String messageAck = JSONObject.toJSONString(ackBase);
        gatewayMqttSenderNative.sendToMqtt(messageAck,topicConfig.getFsuTopic(sn, MqttPubTopic.PushDeviceAssetAck));

    }
    public String getAssetServerResult(PushDeviceAssetDeviceList deviceAssetDeviceList,String sn){
        try{
            if(deviceAssetDeviceList==null || deviceAssetDeviceList.getDevices()==null || deviceAssetDeviceList.getDevices().size()==0){
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
            for(PushDeviceAssetDevice device:deviceAssetDeviceList.getDevices()){
                DeviceReport deviceReportChild = new DeviceReport();
                deviceReportChild.setSn(sn +"_" + device.getId());
                deviceReportChild.setEnterpriseCode(getEnterpriseCode());
                deviceReportChild.setUser(getUser());
                deviceReportChild.setServerCode(getBusinessCode());
                deviceReportChild.setGatewayServerCode(MqttUtils.preconditionServerCode(getServerName(),getServerVersion()));
                deviceReportChild.setRegionCode(getRegionCode());
                DeviceConfigEntity deviceConfig = deviceTypeConfig.getAssentDeviceType(device.getType());
                if(deviceConfig==null){
                    deviceReportChild.setType(String.valueOf(device.getType()));
                }else{
                    deviceReportChild.setType(deviceConfig.getAssentType());
                }
                DeviceReportExtendInfo extendInfo = initDeviceReportExtendInfo(device);
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

    private DeviceReportExtendInfo initDeviceReportExtendInfo(PushDeviceAssetDevice device) {
        DeviceReportExtendInfo info = new DeviceReportExtendInfo();
        info.setModel(device.getModel());
        info.setName(device.getName());
        info.setResourceNo(device.getResourceNo());
        info.setVersionMajor(device.getVersionMajor());
        info.setVersionMinor(device.getVersionMinor());
        info.setVersionRevision(device.getVersionRevision());
        return info;
    }
}
