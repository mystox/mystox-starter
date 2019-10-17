package com.kongtrolink.framework.gateway.common.entity.send;

/**
 * 注册回复
 * Created by Mag on 2019/10/14.
 */
public class RegisterAck {

    private int result;//	number	否	注册结果（0失败，1 成功，2 未注册）
    private int time;//	number	否	注册结果成功时附带，用于同步时间
    private int delay;//	number	否	注册结果失败时附带，用于指定下一次注册间隔延时（单位s）
    private int heartbeatInterval;//	number	否	注册成功附带心跳间隔时间（单位s）

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
}
