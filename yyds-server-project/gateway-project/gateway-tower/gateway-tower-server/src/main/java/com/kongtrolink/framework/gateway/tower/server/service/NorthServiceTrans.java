package com.kongtrolink.framework.gateway.tower.server.service;


import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.*;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *  返回对象转义
 */
public class NorthServiceTrans {

    private static final Logger logger = LoggerFactory.getLogger(NorthServiceTrans.class);
    /**
     * 向FSU下发请求监控点数据
     * 当前只支持FSU查询或者FSU单个设备查询数据
     */
    public static GetDataAckMessage getDataAckTrans(GetDataAckMessage message,GetDataAck data){
        Value value = data.getValue();
        if(0==data.getResult()){//返回失败
            logger.error("数据 结果result 返回失败");
            message.setResult(0);
            return message;
        }
        DeviceIdEntity deviceIdEntity = new DeviceIdEntity();
        if(value!=null && value.getDeviceListList()!=null && value.getDeviceListList().size()>0){
            for(XmlList device:value.getDeviceListList()){
                if(device.getDeviceList()!=null && device.getDeviceList().size()>0){
                    List<DeviceIdInfo> deviceIdInfos = new ArrayList<>();//结果返回的设备对象
                    for(Device device1:device.getDeviceList()){
                        DeviceIdInfo deviceIdInfo = new DeviceIdInfo();
                        List<TSemaphore> signalList = device1.gettSemaphoreList();
                        List<SignalIdInfo> ids = new ArrayList<>();
                        if(signalList!=null){
                            for(TSemaphore tSemaphore:signalList){
                                SignalIdInfo signalIdInfo = new SignalIdInfo();
                                signalIdInfo.setId(tSemaphore.getId());
                                signalIdInfo.setValue(tSemaphore.getMeasuredVal());
                                ids.add(signalIdInfo);
                            }
                        }
                        deviceIdInfo.setDeviceId(device1.getId());
                        deviceIdInfo.setIds(ids);
                        deviceIdInfos.add(deviceIdInfo);
                    }
                    deviceIdEntity.setDeviceIds(deviceIdInfos);
                }

            }
        }
        message.setPayload(deviceIdEntity);
        return message;
    }

    /**
     * 向FSU下发请求监控点门限值
     */
    public static GetThresholdAckMessage getThresholdAckTrans(GetThresholdAckMessage message,GetThresholdAck data){
        Value value = data.getValue();
        if(0==data.getResult()){//返回失败
            logger.error("数据 结果result 返回失败");
            message.setResult(0);
            return message;
        }
        DeviceIdEntity deviceIdEntity = new DeviceIdEntity();
        if(value!=null && value.getDeviceListList()!=null && value.getDeviceListList().size()>0){
            for(XmlList device:value.getDeviceListList()){
                if(device.getDeviceList()!=null && device.getDeviceList().size()>0){
                    List<DeviceIdInfo> deviceIdInfos = new ArrayList<>();//结果返回的设备对象
                    for(Device device1:device.getDeviceList()){
                        DeviceIdInfo deviceIdInfo = new DeviceIdInfo();
                        List<TThreshold> tThresholds = device1.gettThresholdList();
                        List<SignalIdInfo> ids = new ArrayList<>();
                        if(tThresholds!=null){
                            for(TThreshold tThreshold:tThresholds){
                                SignalIdInfo signalIdInfo = new SignalIdInfo();
                                signalIdInfo.setId(tThreshold.getId());
                                signalIdInfo.setThreshold(tThreshold.getThreshold());
                                ids.add(signalIdInfo);
                            }
                        }
                        deviceIdInfo.setDeviceId(device1.getId());
                        deviceIdInfo.setIds(ids);
                        deviceIdInfos.add(deviceIdInfo);
                    }
                    deviceIdEntity.setDeviceIds(deviceIdInfos);
                }

            }
        }
        message.setPayload(deviceIdEntity);
        return message;
    }

    /**
     * 向FSU下发设置监控点值
     */
    public static SetPointAckMessage setPointAckTrans(SetPointAckMessage message, SetPointAck data){
        if(0==data.getResult()){//返回失败
            logger.error("数据 结果result 返回失败");
            message.setResult(0);
            return message;
        }
        DeviceIdEntity deviceIdEntity = getDeviceIdEntity(data.getDeviceList());
        message.setPayload(deviceIdEntity);
        return message;
    }

    /**
     * 向FSU下发设置门限值
     */
    public static SetThresholdAckMessage setThresholdAckTrans(SetThresholdAckMessage message, SetThresholdAck data){
        if(0==data.getResult()){//返回失败
            logger.error("数据 结果result 返回失败");
            message.setResult(0);
            return message;
        }
        DeviceIdEntity deviceIdEntity = getDeviceIdEntity(data.getDeviceList());
        message.setPayload(deviceIdEntity);
        return message;
    }

    private static DeviceIdEntity getDeviceIdEntity(SetDeviceList setDeviceList){
        DeviceIdEntity deviceIdEntity = new DeviceIdEntity();
        if (setDeviceList != null && setDeviceList.getDeviceList()!=null) {
            List<DeviceIdInfo> deviceIdInfos = new ArrayList<>();//结果返回的设备对象
            for (Device device : setDeviceList.getDeviceList()) {
                DeviceIdInfo deviceIdInfo = new DeviceIdInfo();
                List<SignalIdInfo> ids = new ArrayList<>();
                SetResultList successList = device.getSuccessList();
                SetResultList failList = device.getFailList();
                if (successList != null){
                    List<String> signalIdList = successList.getIdList();
                    if (signalIdList != null && signalIdList.size() > 0){
                        for (String signalId : signalIdList){
                            SignalIdInfo signalIdInfo = new SignalIdInfo();
                            signalIdInfo.setId(signalId);
                            signalIdInfo.setResult(1);
                            ids.add(signalIdInfo);
                        }
                    }
                }
                if (failList != null){
                    List<String> signalIdList = failList.getIdList();
                    if (signalIdList != null && signalIdList.size() > 0){
                        for (String signalId : signalIdList){
                            SignalIdInfo signalIdInfo = new SignalIdInfo();
                            signalIdInfo.setId(signalId);
                            signalIdInfo.setResult(0);
                            ids.add(signalIdInfo);
                        }
                    }
                }
                deviceIdInfo.setDeviceId(device.getId());
                deviceIdInfo.setIds(ids);
                deviceIdInfos.add(deviceIdInfo);
            }
            deviceIdEntity.setDeviceIds(deviceIdInfos);
        }
        return deviceIdEntity;
    }

}
