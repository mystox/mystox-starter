package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.RedisTable;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.*;
import com.kongtrolink.framework.execute.module.model.RedisFsuBind;
import com.kongtrolink.framework.execute.module.model.RedisOnlineInfo;
import com.kongtrolink.framework.execute.module.model.Vpn;
import com.kongtrolink.framework.jsonType.JsonRegistry;
import com.kongtrolink.framework.jsonType.JsonStation;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonLoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengw
 * 处理铁塔业务逻辑
 * 新建文件 2019-4-15 17:04:42
 */
@Service
public class TowerService {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    StationDao stationDao;
    @Autowired
    VpnDao vpnDao;
    @Autowired
    LoginParamDao loginParamDao;
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    CarrierDao carrierDao;
    @Autowired
    DeviceMatchService deviceMatchService;
    @Autowired
    private RpcModule rpcModule;
    @Autowired
    RpcClient rpcClient;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Value("${tower.gateway.hostname}")
    private String towerGatewayHostname;
    @Value("${tower.gateway.port}")
    private int towerGatewayPort;

    //信号字典表模式
    @Value("${tower.binding.dictMode}")
    private int dictMode;
    //是否解除绑定
    @Value("${tower.binding.disabled}")
    private boolean disabled;
    //解除绑定时间
    @Value("${tower.binding.disabledTime}")
    private long disabledTime;

    //铁塔平台心跳周期(秒)
    @Value("${tower.loginParam.heartbeatInterval}")
    private int heartbeatInterval;
    //心跳超时次数上限
    @Value("${tower.loginParam.heartbeatTimeoutLimit}")
    private int heartbeatTimeoutLimit;
    //注册次数上限
    @Value("${tower.loginParam.loginLimit}")
    private int loginLimit;
    //注册间隔(秒)
    @Value("${tower.loginParam.loginInterval}")
    private int loginInterval;
    //单条告警上报次数上限
    @Value("${tower.loginParam.alarmReportLimit}")
    private int alarmReportLimit;
    //告警上报间隔(秒)
    @Value("${tower.loginParam.alarmReportInterval}")
    private int alarmReportInterval;

    //终端在线超时时间(秒)
    @Value("${tower.registry.timeout}")
    private int registryTimeout;

    /**
     * 绑定业务
     * @param request 绑定信息
     * @return 绑定结果
     */
    public boolean FsuBind(JSONObject request) {
        boolean result = false;

        JsonStation jsonStation = getStationInfo(request);

        //更新数据库中的注册参数
        JsonLoginParam jsonLoginParam = getLoginParam(request);

        //更新数据库中的设备列表
        JSONArray array = request.getJSONArray("deviceList");
        List<JsonDevice> jsonDeviceList = getDeviceList(array, jsonStation.getFsuId());

        updateBindRedis(jsonStation, jsonLoginParam, jsonDeviceList);

        //更新基站信息
        result = updateStationBindDB(jsonStation, jsonLoginParam, jsonDeviceList);

        return result;
    }

    /**
     * 获取基站信息
     * @param request 请求信息
     * @return 基站信息
     */
    private JsonStation getStationInfo(JSONObject request) {
        JsonStation info = JSONObject.parseObject(request.toJSONString(), JsonStation.class);
        if (!request.containsKey("dictMode")) {
            info.setDictMode(dictMode);
        }
        if (!request.containsKey("disabled")) {
            info.setDisabled(disabled);
        }
        if (!request.containsKey("disabledTime")) {
            info.setDisabledTime(disabledTime);
        }
        return info;
    }

    /**
     * 获取FSU的注册参数信息
     * @param request 请求信息
     */
    private JsonLoginParam getLoginParam(JSONObject request) {
        JsonLoginParam loginParam = JSONObject.parseObject(request.toJSONString(), JsonLoginParam.class);
        if (!request.containsKey("heartbeatInterval")) {
            loginParam.setHeartbeatInterval(this.heartbeatInterval);
        }
        if (!request.containsKey("heartbeatTimeoutLimit")) {
            loginParam.setHeartbeatTimeoutLimit(this.heartbeatTimeoutLimit);
        }
        if (!request.containsKey("loginLimit")) {
            loginParam.setLoginLimit(this.loginLimit);
        }
        if (!request.containsKey("loginInterval")) {
            loginParam.setLoginInterval(this.loginInterval);
        }
        if (!request.containsKey("alarmReportLimit")) {
            loginParam.setAlarmReportLimit(this.alarmReportLimit);
        }
        if (!request.containsKey("alarmReportInterval")) {
            loginParam.setAlarmReportInterval(this.alarmReportInterval);
        }
        return loginParam;
    }

    /**
     * 更新数据库中的设备列表
     * @param array 接收到的设备列表
     * @param fsuId fsuId
     */
    private List<JsonDevice> getDeviceList(JSONArray array, String fsuId) {
        List<JsonDevice> result = new ArrayList<>();
        for (int i = 0; i < array.size(); ++i) {
            result.add(new JsonDevice(fsuId, array.getString(i)));
        }
        //取出对应设备ID列表，重新进行设备匹配，删除原有设备信息后将新设备信息写入数据库
        List<JsonDevice> curDeviceList = deviceDao.getListByFsuId(fsuId);
        deviceMatchService.matchingDevice(result, curDeviceList);
        return result;
    }

    /**
     * 更新数据库中的绑定信息
     * @param jsonStation 基站信息
     * @param jsonLoginParam 注册参数信息
     * @param jsonDeviceList 设备列表
     * @return 更新结果
     */
    private boolean updateStationBindDB(JsonStation jsonStation,
                                        JsonLoginParam jsonLoginParam,
                                        List<JsonDevice> jsonDeviceList) {

        loginParamDao.upsertInfoByFsuId(jsonLoginParam);

        deviceDao.deleteListByFsuId(jsonStation.getFsuId());
        deviceDao.insertListByFsuId(jsonDeviceList);

        JsonStation curStation = stationDao.getInfoByFsuId(jsonStation.getFsuId());
        if (curStation != null) {
            //该FSU在数据库中存在绑定关系
            if (curStation.getSn().equals(jsonStation.getSn())) {
                //若数据库存在记录且SN相同，则更新数据库中该条记录对应的信息
                return stationDao.updateInfoByFsuIdAndSn(jsonStation);
            } else {
                //若数据库存在记录且SN不同，则将该条绑定状态置为解绑，并记录解绑时间
                boolean unbindResult = stationDao.unbindByFsuIdAndSn(curStation);
                if (!unbindResult) {
                    //todo 若解绑失败？

                }
            }
        }

        stationDao.insertInfo(jsonStation);
        return true;
    }

    /**
     * 更新redis中的绑定信息
     * @param jsonStation 基站信息
     * @param jsonLoginParam 注册参数信息
     * @param jsonDeviceList 设备列表
     */
    private void updateBindRedis(JsonStation jsonStation,
                                 JsonLoginParam jsonLoginParam,
                                 List<JsonDevice> jsonDeviceList) {
        String key = RedisTable.TERMINAL_HASH + jsonStation.getSn();
        if (redisUtils.hasKey(key)) {
            RedisOnlineInfo onlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            onlineInfo.setStation(jsonStation);
            onlineInfo.setLoginParam(jsonLoginParam);

            Vpn vpn = vpnDao.getInfoByName(jsonStation.getVpnName());
            if (vpn != null) {
                onlineInfo.setVpn(vpn);
            } else {
                onlineInfo.setLocalName("");
                onlineInfo.setLoginIp("");
                onlineInfo.setLoginPort(0);
            }

            long time = redisUtils.getExpire(key);
            redisUtils.set(key, onlineInfo, time);
        }

        RedisFsuBind redisFsuBind = new RedisFsuBind();
        redisFsuBind.setSn(jsonStation.getSn());
        redisFsuBind.setFsuId(jsonStation.getFsuId());
        for (int i = 0; i < jsonDeviceList.size(); ++i) {
            redisFsuBind.getDeviceIdList().add(jsonDeviceList.get(i).getDeviceId());
        }
        //更新redis中fsuId和sn的映射关系
        redisUtils.hset(RedisTable.FSU_BIND_HASH, jsonStation.getFsuId(), redisFsuBind);
    }

    /**
     * 平台注册终端信息
     * @return
     */
    public boolean login(JSONObject request) {
        boolean result = false;

        JsonRegistry jsonRegistry = getRegistryInfo(request);
        String key = RedisTable.TERMINAL_HASH + jsonRegistry.getSn();

        if (!redisUtils.hasKey(key)) {
            //若redis中不存在sn记录，则在数据库中查找
            JsonStation jsonStation = stationDao.getInfoBySn(jsonRegistry.getSn());
            if (jsonStation == null) {
                //若数据库中不存在sn记录，则说明该sn未绑定fsuId，直接向内部服务返回0
                return false;
            }
            //若数据库中存在记录，读取相关信息，写入redis中
            JsonLoginParam jsonLoginParam = loginParamDao.getInfoByFsuId(jsonStation.getFsuId());
            Vpn vpn = vpnDao.getInfoByName(jsonStation.getVpnName());

            RedisOnlineInfo redisOnlineInfo = new RedisOnlineInfo();
            redisOnlineInfo.setStation(jsonStation);
            redisOnlineInfo.setJsonRegistry(jsonRegistry);
            redisOnlineInfo.setLoginParam(jsonLoginParam);
            redisOnlineInfo.setVpn(vpn);

            result = redisUtils.set(key, redisOnlineInfo, registryTimeout);

            if (!result) {
                //todo log 写redis失败
            }
        }

        if (redisUtils.hasKey(key)) {
            redisUtils.expire(key, registryTimeout);
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            List<JsonDevice> curList = new ArrayList<>();
            List<JsonDevice> deviceList = deviceDao.getListByFsuId(redisOnlineInfo.getFsuId());
            JSONArray array = request.getJSONArray("devList");
            for (int i = 0; i < array.size(); ++i) {
                curList.add(new JsonDevice(array.getString(i)));
            }
            deviceMatchService.matchingDevice(deviceList, curList);

            deviceDao.deleteListByFsuId(redisOnlineInfo.getFsuId());
            deviceDao.insertListByFsuId(deviceList);
        }

        checkOnline(key);

        return result;
    }

    /**
     * 解析内部服务上报的注册信息
     * @param request 上报信息
     * @return 注册信息
     */
    private JsonRegistry getRegistryInfo(JSONObject request) {
        JsonRegistry jsonRegistry = JSONObject.parseObject(request.toJSONString(), JsonRegistry.class);
        JSONArray array = request.getJSONArray("devList");
        for (int i = 0; i < array.size(); ++i) {
            String[] dev = array.get(i).toString().split("-");

            JsonDevice jsonDevice = new JsonDevice();
            jsonDevice.setType(Integer.parseInt(dev[0]));
            jsonDevice.setPort(dev[1]);
            jsonDevice.setResNo(Integer.parseInt(dev[3]));

            jsonRegistry.getDeviceList().add(jsonDevice);
        }
        return jsonRegistry;
    }

    /**
     * 检查终端是否铁塔在线，若离线，启动线程执行注册流程
     * @param key 终端在redis中的key
     * @return 终端在redis中的状态是否正常
     */
    private void checkOnline(String key) {
        if (redisUtils.hasKey(key)) {
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            if (System.currentTimeMillis()/1000 - redisOnlineInfo.getLastTimeRecvTowerMsg() >
                    redisOnlineInfo.getHeartbeatInterval() * redisOnlineInfo.getHeartbeatTimeoutLimit()) {
                //若当前时间 - 上次收到铁塔报文时间 > 心跳间隔 * 心跳超时次数，说明铁塔平台离线
                redisOnlineInfo.setOnline(false);
                long time = redisUtils.getExpire(key);
                redisUtils.set(key, redisOnlineInfo, time);
            }
        }

        if (redisUtils.hasKey(key)) {
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            if (!redisOnlineInfo.isOnline() &&
                redisUtils.hasKey(RedisTable.VPN_HASH) &&
                redisUtils.hHasKey(RedisTable.VPN_HASH, redisOnlineInfo.getLocalName())) {
                //若铁塔离线且本地VPN连接正常，启动线程执行注册流程
                taskExecutor.execute(new CntbLoginService(redisOnlineInfo.getSn(),
                        towerGatewayHostname, towerGatewayPort, rpcModule, redisUtils, rpcClient,
                        carrierDao));
                System.out.println(taskExecutor.getActiveCount());
            }
        }
    }
}
