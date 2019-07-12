package com.kongtrolink.framework.execute.module.model;

import com.kongtrolink.framework.jsonType.JsonLoginParam;
import com.kongtrolink.framework.jsonType.JsonRegistry;
import com.kongtrolink.framework.jsonType.JsonStation;

/**
 * @author fengw
 * redis中的终端在线记录
 * 新建文件 2019-4-17 16:22:15
 */
public class RedisOnlineInfo {
    //sn
    private String sn;
    //fsuId
    private String fsuId;
    //FSU的应用类型
    private String fsuClass;
    //信号量字典版本
    private int dictMode;
    //内部服务Ip
    private String innerIp;
    //内部服务端口
    private int innerPort;
    //铁塔在线状态
    private boolean online = false;
    //心跳间隔
    private int heartbeatInterval;
    //心跳超时次数
    private int heartbeatTimeoutLimit;
    //最后一次收到铁塔报文时间
    private long lastTimeRecvTowerMsg = 0;
    //上次注册时间
    private long lastTimeLogin;
    //注册间隔
    private int loginInterval;
    //告警间隔
    private int alarmInterval;
    //告警上报次数上限
    private int alarmReportLimit;
    //历史数据保存间隔(分钟)
    private int dataSaveInterval;
    //注册机Ip
    private String loginIp;
    //注册机端口
    private int loginPort;
    //本地VPN名称
    private String localName;
    //上报告警IP(该IP为注册成功时铁塔下发)
    private String alarmIp;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getFsuClass() {
        return fsuClass;
    }

    public void setFsuClass(String fsuClass) {
        this.fsuClass = fsuClass;
    }

    public int getDictMode() {
        return dictMode;
    }

    public void setDictMode(int dictMode) {
        this.dictMode = dictMode;
    }

    public String getInnerIp() {
        return innerIp;
    }

    public void setInnerIp(String innerIp) {
        this.innerIp = innerIp;
    }

    public int getInnerPort() {
        return innerPort;
    }

    public void setInnerPort(int innerPort) {
        this.innerPort = innerPort;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getHeartbeatTimeoutLimit() {
        return heartbeatTimeoutLimit;
    }

    public void setHeartbeatTimeoutLimit(int heartbeatTimeoutLimit) {
        this.heartbeatTimeoutLimit = heartbeatTimeoutLimit;
    }

    public long getLastTimeRecvTowerMsg() {
        return lastTimeRecvTowerMsg;
    }

    public void setLastTimeRecvTowerMsg(long lastTimeRecvTowerMsg) {
        this.lastTimeRecvTowerMsg = lastTimeRecvTowerMsg;
    }

    public long getLastTimeLogin() {
        return lastTimeLogin;
    }

    public void setLastTimeLogin(long lastTimeLogin) {
        this.lastTimeLogin = lastTimeLogin;
    }

    public int getLoginInterval() {
        return loginInterval;
    }

    public void setLoginInterval(int loginInterval) {
        this.loginInterval = loginInterval;
    }

    public int getAlarmInterval() {
        return alarmInterval;
    }

    public void setAlarmInterval(int alarmInterval) {
        this.alarmInterval = alarmInterval;
    }

    public int getAlarmReportLimit() {
        return alarmReportLimit;
    }

    public void setAlarmReportLimit(int alarmReportLimit) {
        this.alarmReportLimit = alarmReportLimit;
    }

    public int getDataSaveInterval() {
        return dataSaveInterval;
    }

    public void setDataSaveInterval(int dataSaveInterval) {
        this.dataSaveInterval = dataSaveInterval;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public int getLoginPort() {
        return loginPort;
    }

    public void setLoginPort(int loginPort) {
        this.loginPort = loginPort;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getAlarmIp() {
        return alarmIp;
    }

    public void setAlarmIp(String alarmIp) {
        this.alarmIp = alarmIp;
    }

    /**
     * 设置平台绑定的注册信息
     * @param jsonStation 绑定信息
     */
    public void setStation(JsonStation jsonStation) {
        this.fsuId = jsonStation.getFsuId();
        this.fsuClass = jsonStation.getFsuClass();
        this.dictMode = jsonStation.getDictMode();
    }

    /**
     * 设置平台发送的注册信息
     * @param jsonRegistry 注册信息
     */
    public void setJsonRegistry(JsonRegistry jsonRegistry) {
        this.sn = jsonRegistry.getSn();
        this.innerIp = jsonRegistry.getInnerIp();
        this.innerPort = jsonRegistry.getInnerPort();
    }

    /**
     * 设置注册参数信息
     * @param jsonLoginParam 注册参数
     */
    public void setLoginParam(JsonLoginParam jsonLoginParam) {
        this.heartbeatInterval = jsonLoginParam.getHeartbeatInterval();
        this.heartbeatTimeoutLimit = jsonLoginParam.getHeartbeatTimeoutLimit();
        this.loginInterval = jsonLoginParam.getLoginInterval();
        this.alarmInterval = jsonLoginParam.getAlarmReportInterval();
        this.alarmReportLimit = jsonLoginParam.getAlarmReportLimit();
        this.dataSaveInterval = jsonLoginParam.getDataSaveInterval();
    }

    /**
     * 设置Vpn信息
     * @param vpn Vpn信息
     */
    public void setVpn(Vpn vpn) {
        this.localName = vpn.getLocalName();
        this.loginIp = vpn.getLoginIp();
        this.loginPort = vpn.getLoginPort();
    }
}
