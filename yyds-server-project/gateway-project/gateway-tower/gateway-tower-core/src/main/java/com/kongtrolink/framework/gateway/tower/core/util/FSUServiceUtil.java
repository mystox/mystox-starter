/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.gateway.tower.core.util;


import com.chinatowercom.fsuservice.FSUServiceServiceStub;
import com.chinatowercom.fsuservice.Invoke;
import com.chinatowercom.fsuservice.InvokeResponse;
import com.kongtrolink.framework.gateway.tower.core.entity.base.CntbPktTypeTable;
import com.kongtrolink.framework.gateway.tower.core.entity.base.Info;
import com.kongtrolink.framework.gateway.tower.core.entity.base.Message;
import com.kongtrolink.framework.gateway.tower.core.entity.base.MessageResp;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 注意1： 传参中的 fsuId 为 B接口协议中的Fsuid 也就是对应数据库中 code字段 并非是 数据库的主键ID
 * 注意2： 传参中的 devicesId 为 B接口协议中的device id 也就是对应数据库中 code字段 并非是 数据库的主键ID
 * 注意3： 传参中的 signalIds 为 B接口协议中的Signal id 也就是对应数据库中 code字段 并非是 数据库的主键ID
 * @author Mag 下发命令
 */
public class FSUServiceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FSUServiceUtil.class);


    //	5.2.11.3　用户请求监控点数据
    public static MessageResp getData(String fsuId, String deviceId, List<String> signalIds, String ip, int port) {
        GetData getData = new GetData();
        getData.setFsuId(fsuId);
        getData.setFsuCode(fsuId);
        XmlList xmlLists = new XmlList();
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setCode(deviceId);
        device.setId(deviceId);
        device.setIdList(signalIds);
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        getData.setDeviceList(xmlLists);
        Message req = new Message(getData);

        return getMessageResp(req,ip,port);
    }

    //	5.2.11.3　用户请求监控点数据
    public static MessageResp getDataDevice(String fsuId, String deviceId, String ip, int port) {
        GetData getData = new GetData();
        getData.setFsuId(fsuId);
        getData.setFsuCode(fsuId);
        XmlList xmlLists = new XmlList();
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setCode(deviceId);
        device.setId(deviceId);
        List<String> signalIds = new ArrayList<>();
        //当为全9时（即“9999999999”），则返回该设备的所有监控点的数据
        signalIds.add("9999999999");
        device.setIdList(signalIds);
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        getData.setDeviceList(xmlLists);
        Message req = new Message(getData);

        return getMessageResp(req,ip,port);
    }


    //5.2.11.3　用户请求监控点数据 根据FSU获取 批量device
    public static MessageResp getDataByFsu(String fsuId,String ip,int port) {
        GetData getData = new GetData();
        getData.setFsuId(fsuId);
        getData.setFsuCode(fsuId);
        XmlList xmlLists = new XmlList();
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        //当为全9时（即“99999999999999”），则返回该FSU所监控的所有设备的监控点的值；这种情况下，忽略IDs参数（即监控点ID列表）
        device.setCode("99999999999999");
        device.setId("99999999999999");
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        getData.setDeviceList(xmlLists);
        Message req = new Message(getData);
        return getMessageResp(req,ip,port);
    }


    //5.2.11.5　用户请求写监控点的设置值 SET_POINT 这边只做单个设置
    public static MessageResp setPoint(String fsuId, String deviceId, TSemaphore tSemaphore, String ip, int port) {
        SetPoint setPoint = new SetPoint();
        setPoint.setFsuCode(fsuId);
        setPoint.setFsuId(fsuId);
        Value value = new Value();
        List<XmlList> deviceListList = new ArrayList<>();
        XmlList xmlLists = new XmlList();
        List<TSemaphore> tSemaphores = new ArrayList<>();
        tSemaphores.add(tSemaphore);
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setCode(deviceId);
        device.setId(deviceId);
        device.settSemaphoreList(tSemaphores);
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        deviceListList.add(xmlLists);
        value.setDeviceListList(deviceListList);
        setPoint.setValue(value);
        Message req = new Message(setPoint);
        return getMessageResp(req,ip,port);
    }

    //5.2.11.5　用户请求写监控点的设置值 SET_POINT 这边只做单个设置
    public static MessageResp setPoints(String fsuId, String deviceId, List<TSemaphore> tSemaphores, String ip, int port) {
        SetPoint setPoint = new SetPoint();
        setPoint.setFsuCode(fsuId);
        setPoint.setFsuId(fsuId);
        Value value = new Value();
        List<XmlList> deviceListList = new ArrayList<>();
        XmlList xmlLists = new XmlList();
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setCode(deviceId);
        device.setId(deviceId);
        device.settSemaphoreList(tSemaphores);
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        deviceListList.add(xmlLists);
        value.setDeviceListList(deviceListList);
        setPoint.setValue(value);
        Message req = new Message(setPoint);
        return getMessageResp(req,ip,port);
    }

    //5.2.11.6　用户请求监控点门限数据 单个设备 GET_THRESHOLD
    public static MessageResp getThresholdSignal(String fsuId,String deviceId,List<String> signalIds,String ip,int port) {
        GetThreshold getThreshold = new GetThreshold();
        getThreshold.setFsuId(fsuId);
        getThreshold.setFsuCode(fsuId);
        XmlList xmlLists = new XmlList();
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setCode(deviceId);
        device.setId(deviceId);
        device.setIdList(signalIds);
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        getThreshold.setDeviceList(xmlLists);
        Message req = new Message(getThreshold);
        return getMessageResp(req,ip,port);
    }



    //	5.6.5 请求监控点门限数据 一个设备 GET_THRESHOLD
    public static MessageResp getThresholdDevice(String fsuId,String deviceId,String ip,int port) {
        GetThreshold getThreshold = new GetThreshold();
        getThreshold.setFsuId(fsuId);
        getThreshold.setFsuCode(fsuId);
        XmlList xmlLists = new XmlList();
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setCode(deviceId);
        device.setId(deviceId);
        List<String> signalIds = new ArrayList<>();
        //当为全9时（即“9999999999”），则返回该设备的所有监控点的门限数据
        signalIds.add("9999999999");
        device.setIdList(signalIds);
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        getThreshold.setDeviceList(xmlLists);
        Message req = new Message(getThreshold);
        return getMessageResp(req,ip,port);
    }
    public static MessageResp getThresholdByFsu(String fsuId,String ip,int port) {
        GetThreshold getThreshold = new GetThreshold();
        getThreshold.setFsuId(fsuId);
        getThreshold.setFsuCode(fsuId);
        XmlList xmlLists = new XmlList();
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        //当为全9时（即“99999999999999”），则返回该FSU所监控的所有设备的监控点的值；这种情况下，忽略IDs参数（即监控点ID列表）
        device.setCode("99999999999999");
        device.setId("99999999999999");
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        getThreshold.setDeviceList(xmlLists);
        Message req = new Message(getThreshold);
        return getMessageResp(req,ip,port);
    }

    //5.2.11.7　用户请求写监控点门限数据 SET_THRESHOLD
    public static MessageResp setThreshold(String fsuId,String deviceId,TThreshold tThreshold,String ip,int port) {
        SetThreshold setThreshold = new SetThreshold();
        setThreshold.setFsuCode(fsuId);
        setThreshold.setFsuId(fsuId);
        Value value = new Value();
        List<XmlList> deviceListList = new ArrayList<>();
        XmlList xmlLists = new XmlList();
        List<TThreshold> tThresholds = new ArrayList<>();
        tThresholds.add(tThreshold);
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setCode(deviceId);
        device.setId(deviceId);
        device.settThresholdList(tThresholds);
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        deviceListList.add(xmlLists);
        value.setDeviceListList(deviceListList);
        setThreshold.setValue(value);
        Message req = new Message(setThreshold);
        return getMessageResp(req,ip,port);
    }
    public static MessageResp setThresholds(String fsuId,String deviceId,List<TThreshold> tThresholds,String ip,int port) {
        SetThreshold setThreshold = new SetThreshold();
        setThreshold.setFsuCode(fsuId);
        setThreshold.setFsuId(fsuId);
        Value value = new Value();
        List<XmlList> deviceListList = new ArrayList<>();
        XmlList xmlLists = new XmlList();
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setCode(deviceId);
        device.setId(deviceId);
        device.settThresholdList(tThresholds);
        deviceList.add(device);
        xmlLists.setDeviceList(deviceList);
        deviceListList.add(xmlLists);
        value.setDeviceListList(deviceListList);
        setThreshold.setValue(value);
        Message req = new Message(setThreshold);
        return getMessageResp(req,ip,port);
    }
    //5.2.11.12　时间同步 TIME_CHECK
    public static MessageResp timeCheck(Time time, String ip, int port) {
        TimeCheck timeCheck = new TimeCheck();
        timeCheck.setTime(time);
        Message req = new Message(timeCheck);
        return getMessageResp(req,ip,port);
    }

    //5.6.12 获取FSU状态信息 GET_FSUINFO
    public static MessageResp getFsuInfo(String fsuId,String ip,int port) {
        GetFsuInfo getFsuInfo = new GetFsuInfo();
        getFsuInfo.setFsuCode(fsuId);
        getFsuInfo.setFsuId(fsuId);
        Message req = new Message(getFsuInfo);
        return getMessageResp(req,ip,port);
    }

    
    private static String sendReq(Message message,String ip,int port) {
        try {
            org.apache.axis2.databinding.types.soapencoding.String param = 
                    new org.apache.axis2.databinding.types.soapencoding.String();
            param.setString(MessageUtil.messageToString(message));
            LOGGER.info("--------------------------****************---------------------------------");
            LOGGER.info("[LSCService Web Client]  request...\n"+param.getString());
            LOGGER.info("--------------------------****************---------------------------------");
			Invoke invokeRequest = new Invoke();
            invokeRequest.setXmlData(param);
            FSUServiceServiceStub stub = new FSUServiceServiceStub(ip,port);
            InvokeResponse invokeResponse = stub.invoke(invokeRequest);
            LOGGER.info("--------------------------****************---------------------------------");
            LOGGER.info("[LSCService Web Client]  response...\n"+invokeResponse.getInvokeReturn());
//            if(invokeResponse.getInvokeReturn()!=null && invokeResponse.getInvokeReturn().toString().length()<1024){
//                LOGGER.info("[LSCService Web Client]  response...\n"+invokeResponse.getInvokeReturn());
//            }else{
//                LOGGER.debug("response..数据太大 不进行展现 请用debug模式");
//            }
            LOGGER.info("--------------------------****************---------------------------------");
            return invokeResponse.getInvokeReturn().toString();
        }  catch (Exception ex) {
            LOGGER.error("异常：数据回复!!! " );
        }

		return null;
    }

	private static MessageResp getMessageResp(Message req,String ip,int port){
		MessageResp respMessage = null;
		Info respInfo = null;
		try {
			Document reqDocument  = MessageUtil.stringToDocument(sendReq(req,ip,port));
			String pkType = reqDocument.getElementsByTagName("Name").item(0).getTextContent();

            LOGGER.info("Receive Message Response PkType:{}!",pkType);
			String respInfoStr = MessageUtil.infoNodeToString(reqDocument.getElementsByTagName("Info").item(0));
			if(pkType==null||pkType.trim().equals("")) {
                LOGGER.error("ERROR!NO PKTYPE:{} ",pkType);
			}else {
                     if(pkType.equals(CntbPktTypeTable.GET_DATA_ACK)) {
				        respInfo = (GetDataAck) MessageUtil.stringToMessage(respInfoStr,GetDataAck.class);
			        }else if(pkType.equals(CntbPktTypeTable.SET_POINT_ACK)) {
                         respInfo = (SetPointAck)MessageUtil.stringToMessage(respInfoStr,SetPointAck.class);
                     }else if(pkType.equals(CntbPktTypeTable.GET_THRESHOLD_ACK)) {
                         respInfo = (GetThresholdAck)MessageUtil.stringToMessage(respInfoStr,GetThresholdAck.class);
                     }else if(pkType.equals(CntbPktTypeTable.SET_THRESHOLD_ACK)) {
                         respInfo = (SetThresholdAck)MessageUtil.stringToMessage(respInfoStr,SetThresholdAck.class);
                     }else if(pkType.equals(CntbPktTypeTable.TIME_CHECK_ACK)) {
                         respInfo = (TimeCheckAck)MessageUtil.stringToMessage(respInfoStr,TimeCheckAck.class);
                     }else if(pkType.equals(CntbPktTypeTable.GET_FSUINFO_ACK)) {
                         respInfo = (GetFsuInfoAck)MessageUtil.stringToMessage(respInfoStr,GetFsuInfoAck.class);
                     }
				    respMessage = new MessageResp(respInfo);
			}
		} catch (Exception e) {

		}
		return respMessage;
	}
}
