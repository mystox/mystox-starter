package com.kongtrolink.framework.gateway.tower.server.service;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.gateway.tower.core.entity.RedisFsuInfo;
import com.kongtrolink.framework.gateway.tower.core.entity.base.MessageResp;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.*;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.*;
import com.kongtrolink.framework.gateway.tower.core.util.FSUServiceUtil;
import com.kongtrolink.framework.gateway.tower.server.service.parse.impl.LoginParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *  北向命令：往FSU下发命令
 *  GetData.class, 获取实时数据
    SetPoint.class, 设置值
    GetThreshold.class, 取得阈值
    SetThreshold.class, 设置阈值
 * by Eric on 2019/6/11.
 */
@Service
public class NorthService {


    @Value("${gateway.fsuPort}")
    private int fsuPort; //FSu默认端口

    @Autowired
    private LoginParse loginParse;

    private static final Logger logger = LoggerFactory.getLogger(NorthService.class);

    /**
     * 向FSU下发请求监控点数据
     * 当前只支持FSU查询或者FSU单个设备查询数据
     */
    public GetDataAckMessage getData(String message){
        logger.info("---------[Start]向FSU下发请求监控点数据命令------- ---");
        GetDataMessage getDataMessage = JSON.parseObject(message, GetDataMessage.class);
        String msgId = getDataMessage.getMsgId();
        String fsuId = getDataMessage.getFsuId();

        GetDataAckMessage getDataAckMessage = new GetDataAckMessage();
        getDataAckMessage.setMsgId(msgId);
        getDataAckMessage.setFsuId(fsuId);
        getDataAckMessage.setResult(1);
        try {
            String ip = getFsuIp(fsuId);
            if(ip==null){
                getDataAckMessage.setResult(0);
                return getDataAckMessage;
            }
            DeviceIdEntity payload = getDataMessage.getPayload();
            MessageResp value = null;
            if (payload==null || payload.getDeviceIds()==null || payload.getDeviceIds().size()==0){
                value = FSUServiceUtil.getDataByFsu(fsuId, ip,fsuPort);
            }else {
                DeviceIdInfo deviceIdInfos = payload.getDeviceIds().get(0);
                List<SignalIdInfo> ids = deviceIdInfos.getIds();
                String deviceId = deviceIdInfos.getDeviceId();
                if(ids ==null || ids.size()==0){
                    value = FSUServiceUtil.getDataDevice(fsuId,deviceId,ip,fsuPort);
                }else{
                    List<String> signalIds = new ArrayList<>();
                    for(SignalIdInfo signalIdInfo:ids){
                        signalIds.add(signalIdInfo.getId());
                    }
                    value = FSUServiceUtil.getData(fsuId,deviceId,signalIds,ip,fsuPort);
                }
            }
            if(value !=null){
                GetDataAck data = (GetDataAck)value.getInfo();
                getDataAckMessage = NorthServiceTrans.getDataAckTrans(getDataAckMessage,data);
            }

        }catch (Exception e){
            getDataAckMessage.setResult(0);
            e.printStackTrace();
        }
        return getDataAckMessage;
    }

    /**
     * 向FSU下发请求监控点门限值
     */
    public  GetThresholdAckMessage getThreshold(String message){
        logger.info("---------[Start]向FSU下发请求监控点门限值命令----------");

        GetThresholdMessage getThresholdMessage = JSON.parseObject(message, GetThresholdMessage.class);
        String msgId = getThresholdMessage.getMsgId();
        String fsuId = getThresholdMessage.getFsuId();

        GetThresholdAckMessage getThresholdAckMessage = new GetThresholdAckMessage();
        getThresholdAckMessage.setMsgId(msgId);
        getThresholdAckMessage.setFsuId(fsuId);
        getThresholdAckMessage.setResult(1);
        try {
            String ip = getFsuIp(fsuId);
            if(ip==null){
                getThresholdAckMessage.setResult(0);
                return getThresholdAckMessage;
            }
            DeviceIdEntity payload = getThresholdMessage.getPayload();
            MessageResp value = null;
            if (payload==null || payload.getDeviceIds()==null || payload.getDeviceIds().size()==0){
                value = FSUServiceUtil.getThresholdByFsu(fsuId, ip,fsuPort);
            }else {
                DeviceIdInfo deviceIdInfos = payload.getDeviceIds().get(0);
                List<SignalIdInfo> ids = deviceIdInfos.getIds();
                String deviceId = deviceIdInfos.getDeviceId();
                if(ids ==null || ids.size()==0){
                    value = FSUServiceUtil.getDataDevice(fsuId,deviceId,ip,fsuPort);
                }else{
                    List<String> signalIds = new ArrayList<>();
                    for(SignalIdInfo signalIdInfo:ids){
                        signalIds.add(signalIdInfo.getId());
                    }
                    value = FSUServiceUtil.getThresholdSignal(fsuId,deviceId,signalIds,ip,fsuPort);
                }
            }
            if(value !=null){
                GetThresholdAck data = (GetThresholdAck)value.getInfo();
                getThresholdAckMessage = NorthServiceTrans.getThresholdAckTrans(getThresholdAckMessage,data);
            }

        }catch (Exception e){
            getThresholdAckMessage.setResult(0);
            e.printStackTrace();
        }
        return getThresholdAckMessage;
    }

    /**
     * 向FSU下发设置监控点值
     */
    public SetPointAckMessage setPoint(String message){
        logger.info("---------[Start]向FSU下发设置监控点值命令----------");

        SetPointMessage setPointMessage = JSON.parseObject(message, SetPointMessage.class);
        String msgId = setPointMessage.getMsgId();
        String fsuId = setPointMessage.getFsuId();
        DeviceIdEntity payload = setPointMessage.getPayload();
        SetPointAckMessage setPointAckMessage = new SetPointAckMessage();
        setPointAckMessage.setMsgId(msgId);
        setPointAckMessage.setFsuId(fsuId);
        setPointAckMessage.setResult(1);
        try {
            String ip = getFsuIp(fsuId);
            if(ip==null){
                setPointAckMessage.setResult(0);
                return setPointAckMessage;
            }
            DeviceIdInfo deviceIdInfos = payload.getDeviceIds().get(0);
            List<SignalIdInfo> ids = deviceIdInfos.getIds();
            String deviceId = deviceIdInfos.getDeviceId();
            if(ids !=null &&  ids.size()==0){
                List<TSemaphore> tSemaphores = new ArrayList<>();
                for(SignalIdInfo signalIdInfo:ids){
                    TSemaphore tSemaphore = new TSemaphore();
                    tSemaphore.setId(signalIdInfo.getId());
                    tSemaphore.setSetupVal(signalIdInfo.getValue());
                    tSemaphores.add(tSemaphore);
                }
                MessageResp value = FSUServiceUtil.setPoints(fsuId,deviceId,tSemaphores,ip,fsuPort);
                if(value !=null){
                    SetPointAck data = (SetPointAck)value.getInfo();
                    setPointAckMessage = NorthServiceTrans.setPointAckTrans(setPointAckMessage,data);
                }
            }

        }catch (Exception e){
            setPointAckMessage.setResult(0);
            e.printStackTrace();
        }
        return setPointAckMessage;
    }

    /**
     * 向FSU下发设置门限值
     */
    public SetThresholdAckMessage setThreshold(String message){
        logger.info("---------[Start]向FSU下发设置门限值命令----------");
        SetThresholdMessage setThresholdMessage = JSON.parseObject(message, SetThresholdMessage.class);
        String msgId = setThresholdMessage.getMsgId();
        String fsuId = setThresholdMessage.getFsuId();
        DeviceIdEntity payload = setThresholdMessage.getPayload();
        SetThresholdAckMessage setThresholdAckMessage = new SetThresholdAckMessage();
        setThresholdAckMessage.setMsgId(msgId);
        setThresholdAckMessage.setFsuId(fsuId);
        setThresholdAckMessage.setResult(1);
        try {
            String ip = getFsuIp(fsuId);
            if(ip==null){
                setThresholdAckMessage.setResult(0);
                return setThresholdAckMessage;
            }
            DeviceIdInfo deviceIdInfos = payload.getDeviceIds().get(0);
            List<SignalIdInfo> ids = deviceIdInfos.getIds();
            String deviceId = deviceIdInfos.getDeviceId();
            if(ids !=null &&  ids.size()==0){
                List<TThreshold> tThresholds = new ArrayList<>();
                for(SignalIdInfo signalIdInfo:ids){
                    TThreshold tThreshold = new TThreshold();
                    tThreshold.setId(signalIdInfo.getId());
                    tThreshold.setThreshold(signalIdInfo.getThreshold());
                    tThresholds.add(tThreshold);
                }
                MessageResp value = FSUServiceUtil.setThresholds(fsuId,deviceId,tThresholds,ip,fsuPort);
                if(value !=null){
                    SetThresholdAck data = (SetThresholdAck)value.getInfo();
                    setThresholdAckMessage = NorthServiceTrans.setThresholdAckTrans(setThresholdAckMessage,data);
                }
            }

        }catch (Exception e){
            setThresholdAckMessage.setResult(0);
            e.printStackTrace();
        }
        return setThresholdAckMessage;
    }

    private String getFsuIp(String fsuId){
        RedisFsuInfo redisFsuInfo = loginParse.getRedisFsuInfo(fsuId);
        if(redisFsuInfo==null){
            return null;
        }
        return redisFsuInfo.getIp();
    }
}
