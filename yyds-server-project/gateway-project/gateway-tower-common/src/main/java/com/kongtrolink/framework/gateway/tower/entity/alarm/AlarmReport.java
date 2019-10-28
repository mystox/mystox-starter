package com.kongtrolink.framework.gateway.tower.entity.alarm;

import java.io.Serializable;
import java.util.List;

/**
 * 3.6.1.6.1.1.	上报告警消息
 * Created by Mag on 2019/10/16.
 */
public class AlarmReport implements Serializable{

    private static final long serialVersionUID = -2720331749514184743L;

    private String enterpriseCode;	//企业
    private String serverCode;  		//服务
    private List<AlarmReportInfo> alarms;

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public List<AlarmReportInfo> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<AlarmReportInfo> alarms) {
        this.alarms = alarms;
    }
}
