package com.kongtrolink.framework.jsonType;

/**
 * @Auther: liudd
 * @Date: 2019/4/10 10:31
 * @Description:一次告警内第一次异常时间，用于解决某次一次异常后，告警值一直不变化，导致终端变量上报时没有将异常数值上报问题
 */
public class DelayAlarm {
    private int delay;          //产生延时或消除延迟间隔
    private long delayFT;       //产生延迟或消除延迟第一次时间
    private float value;        //告警值

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public DelayAlarm() {
    }

    public DelayAlarm(int delay, long delayFT) {
        this.delayFT = delayFT;
        this.delay = delay;
    }

    public long getDelayFT() {
        return delayFT;
    }

    public void setDelayFT(long delayFT) {
        this.delayFT = delayFT;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
