package com.kongtrolink.framework.entity;

/**
 * Created by mystoxlol on 2019/8/28, 14:16.
 * company: kongtrolink
 * description: 订阅表实体
 * update record:
 */
public class RegisterSub {

    private String operaCode; //操作码
    private String executeUnit; //执行单元
    private AckEnum ack; //返回类型  NA/ACK

    public RegisterSub() {
        this.ack = AckEnum.NA;
    }

    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public String getExecuteUnit() {
        return executeUnit;
    }

    public void setExecuteUnit(String executeUnit) {
        this.executeUnit = executeUnit;
    }

    public AckEnum getAck() {
        return ack;
    }

    public void setAck(AckEnum ack) {
        this.ack = ack;
    }

    @Override
    public String toString() {
        return "RegisterSub{" +
                "operaCode='" + operaCode + '\'' +
                ", executeUnit='" + executeUnit + '\'' +
                ", ack=" + ack +
                '}';
    }
}
