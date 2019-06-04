package com.kongtrolink.framework.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Device;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.dao.FsuDao;
import com.kongtrolink.framework.dao.FsuDevicesDao;
import com.kongtrolink.framework.dao.OperatorHistoryDao;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.model.OperatHistory;
import com.kongtrolink.framework.service.FsuService;
import com.kongtrolink.framework.util.LocationUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 10:46
 * \* Description:
 * \
 */
@Service
public class FsuServiceImpl implements FsuService {

    private Logger logger = LoggerFactory.getLogger(FsuServiceImpl.class);
    @Value("${compiler.server.url:http://omc.kongtrolink.com}")
    private String compilerServerUrl;

    @Value("${compiler.server.compilerPort}")
    private int compilerServerPort;

    @Value("${compiler.server.compilerDownloadPort}")
    private int compilerServerDownloadPort;

    @Value("${compiler.server.enginePort}")
    private int enginePort;

    @Value("${compiler.server.engineDownloadPort}")
    private int engineDownloadPort;


    private RestTemplate restTemplate;


    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    RpcModule rpcModule;
    @Autowired
    FsuDao fsuDao;
    @Autowired
    FsuDevicesDao fsuDevicesDao;

    @Autowired
    OperatorHistoryDao operatorHistoryDao;

    @Override
    public JSONObject setFsu(Map<String, Object> requestBody) {
        if (requestBody == null) return null;
        String sn = (String) requestBody.get("sn");

        ModuleMsg moduleMsg = new ModuleMsg(PktType.SET_TERMINAL, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }


    @Override
    public JSONArray getDeviceList(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_DEVICES, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONArray result = rpcModule.syncRequestData(moduleMsg, JSONArray.class);
        return result;
    }


    @Override
    public JSONObject getFsuStatus(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.TERMINAL_STATUS, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId) {
        return operatorHistoryDao.findByCondition(requestBody, fsuId);

    }

    @Override
    public Map<String, Integer> getFsuDeviceCountMap(List<String> fsuIds) {
        return fsuDevicesDao.getFsuDeviceCount(fsuIds);
    }

    @Override
    public JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_OP_LOG, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject logoutFsu(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.LOGOUT, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject saveTerminal(JSONArray snList) {
        if (snList == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.TERMINAL_SAVE);
        moduleMsg.setArrayPayload(snList);
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject terminalReboot(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.TERMINAL_REBOOT, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject setGprs(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.TERMINAL_REBOOT, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;

    }

    @Override
    public JSONObject saveSignalModelList(JSONArray signalModelList) {

        if (signalModelList == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.SIGNAL_MODEL_IMPORT);
        moduleMsg.setArrayPayload(signalModelList);
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject saveAlarmModelList(JSONArray alarmSignalList) {
        if (alarmSignalList == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.ALARM_MODEL_IMPORT);
        moduleMsg.setArrayPayload(alarmSignalList);
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject unbind(Map<String, Object> requestBody, String sn) {
        ModuleMsg moduleMsg = new ModuleMsg(PktType.FSU_UNBIND, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject getRunState(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) {
            return null;
        }
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_RUNSTATE, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }


    @Override
    public JSONObject getTerminalPayload(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) {
            return null;
        }
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_TERMINAL_LOG, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject getCompilerDeviceInfo(JSONObject compilerBody, String sn) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> forEntity = restTemplate.getForEntity(compilerServerUrl + ":" + compilerServerPort + "/DeviceInfo", String.class, entity);
        JSONObject compilerConfig = getCompilerConfig(compilerBody, sn);
        JSONObject result = new JSONObject();
        String body = forEntity.getBody();
        JSONObject compilerDeviceInfo = JSON.parseObject(body);
        if (compilerConfig.getInteger("result") ==null || compilerConfig.getInteger("result") != 0) {
            Integer fileVersionId = compilerConfig.getInteger("fileVersionId");
            Integer businessSceneId = compilerConfig.getInteger("businessSceneId");
            Integer productId = compilerConfig.getInteger("productId");
            JSONObject deviceNameDic = compilerDeviceInfo.getJSONObject("SignalInfoXlsHelperDic").
                    getJSONObject(fileVersionId + "").getJSONObject(businessSceneId + "").getJSONObject(productId + "")
                    .getJSONObject("DeviceNameDic");
            JSONArray deviceInfoXlsList = compilerDeviceInfo.getJSONObject("DeviceInfoXlsHelperDic").
                    getJSONObject(fileVersionId + "").getJSONObject(businessSceneId + "").getJSONObject(productId + "")
                    .getJSONArray("DeviceInfoXlsList");
            result.put("DeviceNameDic", deviceNameDic);
            result.put("DeviceInfoXlsList", deviceInfoXlsList);

        } else {
            result.put("result", 0);
            result.put("info", "compilerConfig can not find");
            return result;
        }
        return result;

    }

    @Override
    public JSONObject getEngineInfo(JSONObject compilerBody, String sn) {
        logger.info(compilerBody.toJSONString());
        JSONObject result = new JSONObject();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> forEntity = restTemplate.getForEntity(compilerServerUrl + ":" + enginePort + "/FileInfo", String.class, entity);
        String body = forEntity.getBody();
        JSONObject engineInfo = JSON.parseObject(body);
        JSONObject compilerConfig = getCompilerConfig(compilerBody, sn);
        if (compilerConfig.getInteger("result") ==null || compilerConfig.getInteger("result") != 0) {
            Integer fileVersionId = compilerConfig.getInteger("fileVersionId");
            Integer businessSceneId = compilerConfig.getInteger("businessSceneId");
            Integer productId = compilerConfig.getInteger("productId");
            return engineInfo.getJSONObject("EngineVerDic")
                    .getJSONObject(fileVersionId + "").getJSONObject(businessSceneId + "").getJSONObject(productId + "");
        } else {
            result.put("result", 0);
            result.put("info", "compilerConfig can not find");
            return result;
        }
    }


    @Override
    public JSONObject compilerFile(JSONObject compilerBody) {
        logger.info("compiler file body info: [{}]", compilerBody.toJSONString());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(compilerServerUrl + ":" + compilerServerPort + "/Compiler", compilerBody, String.class, entity);
        String body = responseEntity.getBody();
        return JSON.parseObject(body);
    }


    @Override
    public JSONObject getCompilerConfig(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) {
            return null;
        }
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_COMPILER_VERSION, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        return rpcModule.syncRequestData(moduleMsg, JSONObject.class);

    }

    @Override
    public JSONObject compiler(JSONObject requestBody, String sn) {
        if (requestBody == null) return null;
        int type = requestBody.getInteger("type"); //1 升级引擎 2 升级适配层
        JSONObject result = new JSONObject();
        String guid = UUID.randomUUID().toString();


        String md5 = "";
        String name = "";
        String url = "";
        if (type == 1) { //升级引擎
            JSONObject engineVerDic = getEngineInfo(requestBody, sn);
            if (engineVerDic.getInteger("result") ==null || engineVerDic.getInteger("result") != 0) {
                name = engineVerDic.getString("Name");
                engineVerDic.getString("Version");
                md5 = engineVerDic.getString("MD5");
                url = engineVerDic.getString("Url");
                url = compilerServerUrl + ":" + engineDownloadPort + "/" + url;
            }else {
                return engineVerDic;
            }
        }
        if (type == 2) { //升级适配层
            JSONArray deviceInfoList = requestBody.getJSONArray("deviceInfoList");
            if (deviceInfoList == null) { //如果设备信息没有传入,则说明由<============web发起数据库获取组装
                HashedMap deviceListSearch = new HashedMap();
                JSONArray deviceList = getDeviceList(deviceListSearch, sn);
                List<Device> devices = JSONArray.parseArray(deviceList.toJSONString(), Device.class);//获取device列表
                deviceInfoList = createDeviceInfoList(requestBody,sn, devices);   //编译设备信息列表生成
            }
            JSONObject compilerConfig = getCompilerConfig(requestBody, sn);
            Integer fileVersionId = compilerConfig.getInteger("fileVersionId");
            Integer businessSceneId = compilerConfig.getInteger("businessSceneId");
            Integer productId = compilerConfig.getInteger("productId");
            JSONObject compilerResult = compileBody(guid, fileVersionId, businessSceneId, productId, deviceInfoList);
            if (!compilerResult.getBoolean("Result")) {
                logger.error("编译服务器执行结果失败:{}",compilerResult.toJSONString());
                result.put("result", 0);
                result.put("info", compilerResult.toJSONString());
                return result;
            }
            md5 = compilerResult.getString("MD5");
            name = "Program.bin";
            url = compilerServerUrl + ":" + compilerServerDownloadPort + "/" + guid;
            url = url + "/" + name;
        }
        //根据编译结果生成内部编译文件下载逻辑
        JSONObject compilerRequest = new JSONObject(); //发完business 的请求体
        compilerRequest.put("url", url);
        compilerRequest.put("name", name);
        compilerRequest.put("type", type);
        compilerRequest.put("md5", md5);
        ModuleMsg moduleMsg = new ModuleMsg(PktType.COMPILER, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(compilerRequest));
        result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        result.put("name", name);
        result.put("GUID", guid);
        result.put("md5", md5);
        result.put("url", url);

        String path = "AppResources/sn/"+sn+"/"+name;
        result.put("path", path);
        return result;
    }

    private JSONArray createDeviceInfoList(JSONObject compilerBody, String sn, List<Device> devices) {
        JSONArray deviceInfoList = new JSONArray();
        JSONObject compilerDeviceInfo = getCompilerDeviceInfo(compilerBody, sn); //获取编译服务器的deviceInfo字典
        //提取DeviceNameDic 设备名称字典
        JSONObject deviceNameDic = compilerDeviceInfo.getJSONObject("DeviceNameDic");
        JSONArray COMArr = deviceNameDic.getJSONArray("COM"); //1
        JSONArray CANArr = deviceNameDic.getJSONArray("CAN"); //6
        JSONArray EPLCArr = deviceNameDic.getJSONArray("EPLC"); //0
        JSONArray DIArr = deviceNameDic.getJSONArray("DI"); //3
        JSONArray AIArr = deviceNameDic.getJSONArray("AI"); //2
        JSONArray DOArr = deviceNameDic.getJSONArray("DO"); //4

        //拼装设备信息
        devices.forEach((Device device) -> {
            Integer type = device.getType();
            Integer resNo = device.getResNo();
            Integer port = device.getPort();
            JSONObject deviceInfo = new JSONObject();
            if (getDeviceName(COMArr, type)) {
                deviceInfo.put("PortType", "COM");
            } else {
                if (getDeviceName(CANArr, type)) {
                    deviceInfo.put("PortType", "CAN");
                } else {
                    if (getDeviceName(EPLCArr, type)) {
                        deviceInfo.put("PortType", "EPLC");
                    } else {
                        if (getDeviceName(DIArr, type)) {
                            deviceInfo.put("PortType", "DI");
                        } else {
                            if (getDeviceName(AIArr, type)) {
                                deviceInfo.put("PortType", "AI");
                            } else {
                                if (getDeviceName(DOArr, type)) {
                                    deviceInfo.put("PortType", "DO");
                                } else
                                    return;
                            }
                        }
                    }
                }
            }
            deviceInfo.put("AttachPortList", new JSONArray());
            deviceInfo.put("Version", device.getVersion());
            deviceInfo.put("SerialCode", resNo);
            deviceInfo.put("Port", port);
            deviceInfo.put("Address", device.getSerialNumber());
            deviceInfoList.add(deviceInfo);
        });

        return deviceInfoList;
    }


    boolean getDeviceName(JSONArray jsonArray, int type) {
        for (Object o : jsonArray) {
            JSONObject dJson = (JSONObject) o;
            Integer code = dJson.getInteger("Code");
            if (code == type) return true;
        }
        return false;

    }

    JSONObject compileBody(String guid, int fileVersionId, int businessSceneId, int productId, JSONArray devList) {

        //生成compiler body
        JSONObject compilerBody = new JSONObject();
        JSONObject userInfo = new JSONObject();
        userInfo.put("Application", "微站App");
        userInfo.put("UserName", "admin");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String requestTime = sdf.format(new Date());
        userInfo.put("RequestTime", requestTime);
        userInfo.put("GUID", guid);
        compilerBody.put("UserInfo", userInfo);
        JSONObject fileVersionObj = new JSONObject();
        fileVersionObj.put("ID", fileVersionId);
        compilerBody.put("FileVersion", fileVersionObj);

        JSONObject businessScenceObj = new JSONObject();
        businessScenceObj.put("ID", businessSceneId);
        compilerBody.put("BusinessScene", businessScenceObj);

        JSONObject productObj = new JSONObject();
        productObj.put("ID", productId);
        compilerBody.put("Product", productObj);
        compilerBody.put("Infos", "");
        compilerBody.put("DeviceInfoList", devList);
        return compilerFile(compilerBody);     //请求服务器编译
    }

    @Override
    public JSONObject upgrade(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.UPGRADE, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class, 300000L);
        return result;
    }


    @Override
    public List<Fsu> getFsuListByCoordinate(Map fsuMap) {
        String coordinate = (String) fsuMap.get("coordinate");
        if (StringUtils.isBlank(coordinate))
            return null;
        String[] coordinateLocation = coordinate.split(",");

//        List<Fsu> fsuList = listFsu(null);
        List<Fsu> fsuList = new ArrayList<>();

        List<Fsu> result = new ArrayList<>();
        for (Fsu fsu : fsuList) {
            String fsuCoordinate = fsu.getCoordinate();
            if (StringUtils.isNotBlank(fsuCoordinate)) {
                String[] fsuCoordinateArr = fsuCoordinate.split(",");

                double distance = LocationUtils.getDistance(fsuCoordinateArr[0], fsuCoordinateArr[1], coordinateLocation[0], coordinateLocation[1]);
                if (distance < 2000) {
                    result.add(fsu);
                }
            }
        }
        return result;
    }

    @Override
    public JSONObject listFsu(Map<String, Object> requestBody) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_FSU);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;

    }

    @Override
    public List<Fsu> searchFsu(Map<String, Object> requestBody) {
        if (requestBody == null) return null;
        return fsuDao.findByCondition(requestBody);
    }

    @Override
    public Fsu getFsu(Map<String, Object> requestBody) {
        if (requestBody == null) return null;
        String fsuId = (String) requestBody.get("fsuId");
        return fsuDao.findByFsuId(fsuId);
    }
}