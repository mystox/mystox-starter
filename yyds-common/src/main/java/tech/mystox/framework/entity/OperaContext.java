package tech.mystox.framework.entity;

import tech.mystox.framework.scheduler.LoadBalanceScheduler;

import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2020/6/10, 11:11.
 * company: kongtrolink
 * description:
 * update record:
 */
public class OperaContext {
    private String operaCode;
    private String msg;
    private int qos;
    private long timeout;
    private TimeUnit timeUnit;
    private LoadBalanceScheduler loadBalanceScheduler;
    private boolean setFlag;
    private boolean async;

    public OperaContext() {
    }

    public OperaContext(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, LoadBalanceScheduler loadBalanceScheduler, boolean setFlag, boolean async) {
        this.operaCode = operaCode;
        this.msg = msg;
        this.qos = qos;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.loadBalanceScheduler = loadBalanceScheduler;
        this.setFlag = setFlag;
        this.async = async;
    }

    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSetFlag() {
        return setFlag;
    }

    public void setSetFlag(boolean setFlag) {
        this.setFlag = setFlag;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public LoadBalanceScheduler getLoadBalanceScheduler() {
        return loadBalanceScheduler;
    }

    public void setLoadBalanceScheduler(LoadBalanceScheduler loadBalanceScheduler) {
        this.loadBalanceScheduler = loadBalanceScheduler;
    }
}
