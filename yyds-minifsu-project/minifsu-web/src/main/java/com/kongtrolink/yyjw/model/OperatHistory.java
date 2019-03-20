package com.kongtrolink.yyjw.model;

import java.io.Serializable;

/**
 * Created by mystoxlol on 2018/12/24, 10:52.
 * company: kongtrolink
 * description:
 * update record:
 */
public class OperatHistory implements Serializable
{
    private String _id;
    private String ipId;
    private String fsuId;
    private Integer status;
    private Long opTime;
    private Integer opCode;
    private String operator;
    private Integer operatorType;
    private String reqInfo;
    private String rspInfo;

    public String get_id()
    {
        return _id;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String getIpId()
    {
        return ipId;
    }

    public void setIpId(String ipId)
    {
        this.ipId = ipId;
    }

    public String getFsuId()
    {
        return fsuId;
    }

    public void setFsuId(String fsuId)
    {
        this.fsuId = fsuId;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Long getOpTime()
    {
        return opTime;
    }

    public void setOpTime(Long opTime)
    {
        this.opTime = opTime;
    }

    public Integer getOpCode()
    {
        return opCode;
    }

    public void setOpCode(Integer opCode)
    {
        this.opCode = opCode;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public Integer getOperatorType()
    {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType)
    {
        this.operatorType = operatorType;
    }

    public String getReqInfo()
    {
        return reqInfo;
    }

    public void setReqInfo(String reqInfo)
    {
        this.reqInfo = reqInfo;
    }

    public String getRspInfo()
    {
        return rspInfo;
    }

    public void setRspInfo(String rspInfo)
    {
        this.rspInfo = rspInfo;
    }
}
