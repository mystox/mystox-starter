package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.config.RedisConfig;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.dao.LoginParamDao;
import com.kongtrolink.framework.execute.module.dao.StationDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.VpnDao;
import com.kongtrolink.framework.execute.module.model.Vpn;
import com.kongtrolink.framework.jsonType.JsonStation;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonLoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    DeviceMatchService deviceMatchService;

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

    /**
     * 绑定业务
     * @param request 绑定信息
     * @return 绑定结果
     */
    public boolean FsuBind(JSONObject request) {
        boolean result = false;

        JsonStation info = getStationInfo(request);

        //更新数据库中的注册参数
        updateLoginParam(info.getSn(), request);

        //更新数据库中的设备列表
        JSONArray array = request.getJSONArray("deviceList");
        updateDeviceList(array, info.getFsuId());

        result = updateStation(info);

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
     * 更新指定FSU的注册参数信息
     * @param sn sn
     * @param request 请求信息
     */
    private void updateLoginParam(String sn, JSONObject request) {
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
        loginParamDao.upsertInfoByFsuId(loginParam);
        String key = RedisConfig.TERMINAL_HASH + sn;
        if (redisUtils.hasKey(key)) {
            long time = redisUtils.getExpire(key);
            redisUtils.hset(key, "heartbeatInterval", loginParam.getHeartbeatInterval(), time);
            redisUtils.hset(key, "heartbeatTimeoutLimit", loginParam.getHeartbeatTimeoutLimit(), time);
            redisUtils.hset(key, "loginLimit", loginParam.getLoginLimit(), time);
            redisUtils.hset(key, "loginInterval", loginParam.getLoginInterval(), time);
            redisUtils.hset(key, "alarmReportLimit", loginParam.getAlarmReportLimit(), time);
            redisUtils.hset(key, "alarmReportInterval", loginParam.getAlarmReportInterval(), time);
        }
    }

    /**
     * 更新数据库中的设备列表
     * @param array 接收到的设备列表
     * @param fsuId fsuId
     */
    private void updateDeviceList(JSONArray array, String fsuId) {
        List<JsonDevice> list = new ArrayList<>();
        for (int i = 0; i < array.size(); ++i) {
            list.add(new JsonDevice(fsuId, array.getString(i)));
        }
        //取出对应设备ID列表，重新进行设备匹配，删除原有设备信息后将新设备信息写入数据库
        List<JsonDevice> curDeviceList = deviceDao.getListByFsuId(fsuId);
        deviceMatchService.matchingDevice(list, curDeviceList);
        deviceDao.deleteListByFsuId(fsuId);
        deviceDao.insertListByFsuId(list);
    }

    /**
     * 更新绑定信息
     * @param info 绑定信息
     * @return 更新结果
     */
    private boolean updateStation(JsonStation info) {
        JsonStation curStation = stationDao.getInfoByFsuId(info.getFsuId());
        if (curStation != null) {
            //该FSU在数据库中存在绑定关系
            if (curStation.getSn().equals(info.getSn())) {
                //若数据库存在记录且SN相同，则更新数据库中该条记录对应的信息
                boolean result = stationDao.updateInfoByFsuIdAndSn(info);
                String key = RedisConfig.TERMINAL_HASH + info.getSn();
                if (redisUtils.hasKey(key)) {
                    Vpn vpn = vpnDao.getInfoByName(info.getVpnName());
                    if (vpn != null) {
                        long time = redisUtils.getExpire(key);
                        redisUtils.hset(key, "localName", vpn.getLocalName(), time);
                        redisUtils.hset(key, "loginIp", vpn.getLoginIp(), time);
                        redisUtils.hset(key, "loginPort", vpn.getLoginPort(), time);
                    }
                }
                return result;
            } else {
                //若数据库存在记录且SN不同，则将该条绑定状态置为解绑，并记录解绑时间
                boolean unbindResult = stationDao.unbindByFsuIdAndSn(curStation);
                if (!unbindResult) {
                    //若解绑失败？

                }
            }
        }

        stationDao.insertInfo(info);
        return true;
    }

    public boolean Login() {
        boolean result = false;

        return result;
    }
}
