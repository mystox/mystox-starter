package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.dao.MultipleRoomDao;
import com.kongtrolink.framework.scloud.dao.RealTimeDataDao;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.RelatedDeviceInfo;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.multRoom.*;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.MultipleRoomQuery;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.MultipleRoomService;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.Signal;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mag
 **/
@Service
public class MultipleRoomServiceImpl implements MultipleRoomService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleRoomServiceImpl.class);
    @Autowired
    MultipleRoomDao multipleRoomDao;
    @Autowired
    RealTimeDataDao realTimeDataDao;
    @Autowired
    RealTimeDataService realTimeDataService;
    @Autowired
    DeviceService deviceService;
    /**
     * 初始化要进行的数据
     *
     * @param uniqueCode 企业编码
     */
    @Override
    public void initSignalType(String uniqueCode) {
        LOGGER.info("初始化 综合机房默认配置---------("+uniqueCode+")-------------");
        multipleRoomDao.removeRoomDeviceType(uniqueCode);
        updateRoomDeviceType(uniqueCode);
    }

    /**
     * 获取 基本信息
     *
     * @param uniqueCode 企业编码
     * @param siteId     站点ID
     * @return RoomSiteInfo
     */
    @Override
    public RoomSiteInfo getSiteInfo(String uniqueCode, int siteId) {
        SiteEntity site =  multipleRoomDao.findSite(uniqueCode, siteId);
        RoomSiteInfo roomSiteInfo = new RoomSiteInfo();
        roomSiteInfo.inputSite(site);
        int deviceNum = 0;
        int signalNum = 0;
        List<DeviceEntity> deviceList = multipleRoomDao.getDeviceList(uniqueCode,siteId);//监控设备总数
        Map<String,Integer> deviceTypeMap = realTimeDataDao.queryDeviceType(uniqueCode);
        //根据设备类型取得该设备类型有多少信号点
        if(deviceList!=null){
            deviceNum = deviceList.size();
           for(DeviceEntity deviceEntity:deviceList){
               String deviceType = deviceEntity.getTypeCode();
               if(deviceTypeMap.containsKey(deviceType)){
                   signalNum = signalNum + deviceTypeMap.get(deviceType);
               }
           }
        }
        roomSiteInfo.setDeviceNum(deviceNum);
        roomSiteInfo.setSignalNum(signalNum);
        return roomSiteInfo;
    }

    /**
     * 获取 设备列表
     *
     * @param uniqueCode 企业编码
     * @param query     站点ID
     * @return 设备列表
     */
    @Override
    public List<RoomDevice> getRoomDevice(String uniqueCode, MultipleRoomQuery query) throws Exception{
        //判断该企业是否进行了信号点初始化 若没有 则进行初始化
        int roomShow = multipleRoomDao.countRoomDeviceType(uniqueCode);
        if(roomShow==0){
            updateRoomDeviceType(uniqueCode);
        }
        String siteCode = query.getSiteCode();
        List<String> siteCodes = new ArrayList<>();
        siteCodes.add(siteCode);
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setSiteCodes(siteCodes);
        List<DeviceModel> deviceList =  deviceService.findDeviceList(uniqueCode, deviceQuery);
        List<RoomDevice> list = getRoomDeviceList(uniqueCode,deviceList,siteCode,query.getServerCode());
        return list;
    }

    private List<RoomDevice> getRoomDeviceList(String uniqueCode, List<DeviceModel> deviceList,String siteCode,String serverCode){
        Map<String,List<RoomDeviceInfo>> map = new HashMap<>();
        Map<String,String> deviceFsuMap = new HashMap<>();//DEVICE所属FSU的map
        List<String> deviceTypeCodeList = new ArrayList<>();
        Map<String, DeviceModel> fsuMap = new HashMap<>();
        for(DeviceModel device : deviceList){
            String typeCode = device.getTypeCode();
            deviceTypeCodeList.add(typeCode);
            if("038".equals(typeCode)){
                fsuMap.put(device.getCode(),device);
                DeviceQuery deviceQuery = new DeviceQuery();
                deviceQuery.setDeviceCode(device.getCode());
                deviceQuery.setServerCode(serverCode);
                List<RelatedDeviceInfo> relatedDeviceInfos = deviceService.findRelatedDeviceList(uniqueCode, deviceQuery);
                if(relatedDeviceInfos!=null){
                    for(RelatedDeviceInfo deviceInfo:relatedDeviceInfos){
                        deviceFsuMap.put(deviceInfo.getDeviceCode(),device.getCode());
                    }
                }
            }
        }
        List<RoomDeviceType> roomDeviceTypeList = multipleRoomDao.getByDeviceTypeList(uniqueCode, deviceTypeCodeList);
        Map<String, List<RoomSignalType>> deviceTypeCodeSignalListMap = multipleRoomDao.deviceTypeList2CodeSignalMap(roomDeviceTypeList);
        Map<String, Integer> deviceIdAlarmCountMap = multipleRoomDao.deviceAlarmCountMap(uniqueCode, siteCode);
        for(DeviceModel device:deviceList){
            String type = device.getType();
            String deviceType = device.getTypeCode();
            RoomDeviceInfo deviceInfo = new RoomDeviceInfo();
            deviceInfo.setDeviceId(device.getId());
            deviceInfo.setDeviceName(device.getName());
            deviceInfo.setDeviceCode(device.getCode());
            int alarmNum = deviceIdAlarmCountMap.get(device.getId());
            if(alarmNum>0){
                deviceInfo.setStatus("告警");
            }else{
                //设备的在线状态 - FSU的在线状态
                if(deviceFsuMap.containsKey(device.getCode())){
                    String fsuCode = deviceFsuMap.get(device.getCode());
                    if(fsuMap.containsKey(fsuCode)){
                        deviceInfo.setStatus(fsuMap.get(fsuCode).getState());
                    }
                }
            }
            //查询 该设备是否有自定义配置
            RoomSignalTypeConfig signalConfig =  queryRoomSignalTypeConfig(uniqueCode,device.getId());
            List<RoomSignalType> signalTypeList = deviceTypeCodeSignalListMap.get(deviceType);
            deviceInfo = findSignalList(uniqueCode,deviceInfo,signalTypeList,signalConfig);
            String key = type+"#"+deviceType;
            //根据设备类型进行汇总
            if(map.containsKey(key)){
                map.get(key).add(deviceInfo);
            }else{
                List<RoomDeviceInfo> list = new ArrayList<>();
                list.add(deviceInfo);
                map.put(key,list);
            }
        }
        List<RoomDevice> roomDeviceList = new ArrayList<>();
        for(String key : map.keySet()){
            RoomDevice roomDevice = new RoomDevice();
            String[] x = key.split("#");
            roomDevice.setType(x[0]);
            roomDevice.setCode(x[1]);
            roomDevice.setInfoList(map.get(key));
            roomDeviceList.add(roomDevice);
        }
        return roomDeviceList;
    }
    /**
     * 保存自定义信号点
     *
     * @param uniqueCode
     * @param config
     */
    @Override
    public boolean addShowSignalConfig(String uniqueCode, RoomSignalTypeConfig config) {
        try{
            //先删除原先设备的配置
            multipleRoomDao.delShowSignalConfig(uniqueCode,config.getDeviceId());
            //保存最新的设备配置
            multipleRoomDao.addShowSignalConfig(uniqueCode, config);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据设备查询信号点配置 不包含默认值
     *
     */
    @Override
    public RoomSignalTypeConfig queryRoomSignalTypeConfig(String uniqueCode, int deviceId) {
        return multipleRoomDao.queryRoomSignalTypeConfig(uniqueCode, deviceId);
    }

    /**
     * 根据设备查询信号点配置包含默认值 前天界面用
     */
    @Override
    public RoomSignalTypeConfig queryRoomSignalTypeConfigShow(String uniqueCode, int deviceId) {
        RoomSignalTypeConfig config =  multipleRoomDao.queryRoomSignalTypeConfig(uniqueCode, deviceId);
        //如果用户没有配置过该配置下 则显示默认的数据
        if(config==null || config.getId()==0){
            config = new RoomSignalTypeConfig();
            config.setDeviceId(deviceId);
            List<RoomSignalTypeDevice> signals = new ArrayList<>();
            DeviceEntity device = multipleRoomDao.findDeviceById(uniqueCode,deviceId);
            //在表中查询出该设备类型要展现的信号点类型
            List<RoomSignalType> signalTypeList = getShowSignal(uniqueCode,device.getTypeCode());;
            if(signalTypeList !=null){
                for(RoomSignalType typed:signalTypeList){
                    RoomSignalTypeDevice roomSignalTypeDevice = new RoomSignalTypeDevice();
                    roomSignalTypeDevice.setMeasurement(typed.getMeasurement());
                    roomSignalTypeDevice.setType(typed.getType());
                    roomSignalTypeDevice.setSignalName(typed.getTypeName());
                    roomSignalTypeDevice.setCntbId(typed.getCntbId());
                    roomSignalTypeDevice.setOrder(typed.getOrder());
                    signals.add(roomSignalTypeDevice);
                }
            }
            config.setSignals(signals);
        }
        return config;
    }

    /**
     * 根据企业编码-设备类型 获取 要展现的信号点数据
     * @param uniqueCode 企业编码
     * @param deviceType 设备类型
     * @return 信号点类型
     */
    private List<RoomSignalType> getShowSignal(String uniqueCode, String deviceType){
        RoomDeviceType list = multipleRoomDao.getShowSignal(uniqueCode,deviceType);
        //没有需要展现的信号点数据
        if(list==null){
            return  null;
        }
        List<RoomSignalType> signalTypeList = list.getSignalTypeList();
        return  signalTypeList;
    }
    /**
     * 需要默认的信号点东西
     */
    private List<String> reFlashDeviceTypeShow(){
        List<String> cntbIds = new ArrayList<>();
        /* 读取数据 */
        InputStream inputStream = null;
        BufferedReader br = null;
        try {
            inputStream = this.getClass().getResourceAsStream("/config/signalShow.txt");
            br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String lineTxt;
            while ((lineTxt = br.readLine()) != null) {//数据以逗号分隔
                try{
                    //格式 设备名称,上报的设备类型,资管的设备类型
                    String cntbId = lineTxt;
                    cntbIds.add(cntbId);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("read errors :" + e);
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LOGGER.info("初始化 模拟类型映射表 完成  !! ");
        return cntbIds;
    }

    private void updateRoomDeviceType(String uniqueCode){
        List<RoomDeviceType> list =  multipleRoomDao.queryRoomDeviceType(uniqueCode);
        List<RoomDeviceType> value = new ArrayList<>();
        List<String> cList = reFlashDeviceTypeShow();
        for(RoomDeviceType deviceType:list){
            List<RoomSignalType> signalTypeList = deviceType.getSignalTypeList();
            List<RoomSignalType> sig = new ArrayList<>();
            int i=1;
            for(RoomSignalType signalType:signalTypeList){
                String cntbId = signalType.getCntbId();
                if(cList.contains(cntbId)){
                    signalType.setOrder(i);
                    if("遥控".equals(signalType.getType())){
                        signalType.setOrder(0);
                    }
                    sig.add(signalType);
                    i++;
                }
            }
            deviceType.setSignalTypeList(sig);
            deviceType.setUniqueCode(uniqueCode);
            if(sig.size()>0){
                value.add(deviceType);
                multipleRoomDao.saveRoomDeviceType(deviceType);
            }

        }
        LOGGER.info("初始化 uniqueCode:{} 综合机房展现成功",uniqueCode);
    }

    /**
     * 获取 信号点列表
     * @param signalTypeList 信号点类型列表
     * @return 返回 MAP key=信号点类型编码,value 对应的值
     */
    private RoomDeviceInfo findSignalList(String uniqueCode,
                                          RoomDeviceInfo deviceInfo,
                                          List<RoomSignalType> signalTypeList,
                                          RoomSignalTypeConfig signalConfig) {
        String deviceCode = deviceInfo.getDeviceCode();
        String doValue = null;//值1 (存DO值)
        String doSin = null;
        String doSinName = null;
        String aiSin01 = null;
        String aiSin02 = null;
        String aiUnit1 = null;
        String aiUnit2 = null;
        String aiName1 = null;
        String aiName2 = null;
        boolean ifConfig = false;//是否采用设置的设备处理
        if(signalConfig!=null && signalConfig.getSignals()!=null){
            ifConfig = true;
        }
        if(signalTypeList!=null){
            //再次循环 信号点列表 把 值填充进去
            for(int i=0;i<signalTypeList.size();i++){
                RoomSignalType roomSignalType = signalTypeList.get(i);
                String cntbId = roomSignalType.getCntbId();
                int order = roomSignalType.getOrder();
                if(order==0){ //遥控数据 从 数据库中获取
                    doValue = realTimeDataService.getRealDateCntbId(uniqueCode,deviceCode,cntbId);
                    doSin = roomSignalType.getCntbId();
                    doSinName = roomSignalType.getTypeName();
                }else if(order==1){
                    aiSin01 = roomSignalType.getCntbId();
                    aiName1 = roomSignalType.getTypeName();
                    aiUnit1 = roomSignalType.getMeasurement();
                }else if(order==2){
                    aiSin02 = roomSignalType.getCntbId();
                    aiName2 = roomSignalType.getTypeName();
                    aiUnit2 = roomSignalType.getMeasurement();
                }
            }
        }
        //如果有设定的 采用设定的数据
        if(ifConfig){
            List<RoomSignalTypeDevice> signals = signalConfig.getSignals();
            for(int i=0;i<signals.size();i++){
                RoomSignalTypeDevice config = signals.get(i);
                int order = config.getOrder();
                if(order==1){
                    aiSin01 = config.getCntbId();
                    aiName1 = config.getSignalName();
                    aiUnit1 = config.getMeasurement();
                }else if(order==2){
                    aiSin02 = config.getCntbId();
                    aiName2 = config.getSignalName();
                    aiUnit2 = config.getMeasurement();
                }
            }
        }

        String aiValue1 = realTimeDataService.getRealDateCntbId(uniqueCode,deviceCode,aiSin01);
        String aiValue2 = realTimeDataService.getRealDateCntbId(uniqueCode,deviceCode,aiSin02);
        deviceInfo.setDoSinName(doSinName);
        deviceInfo.setAiUnit1(aiUnit1);
        deviceInfo.setAiUnit2(aiUnit2);
        deviceInfo.setDoSinId(doSin);
        deviceInfo.setDoValue(doValue);
        deviceInfo.setAiValue1(aiValue1);
        deviceInfo.setAiSinId1(aiSin01);
        deviceInfo.setAiValue2(aiValue2);
        deviceInfo.setAiSinId2(aiSin02);
        deviceInfo.setAiName1(aiName1);
        deviceInfo.setAiName2(aiName2);
        return deviceInfo;
    }

}
