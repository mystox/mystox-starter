package com.kongtrolink.framework.gateway.iaiot.core.assent;

import java.io.Serializable;
import java.util.List;

/**
 * 3.6.1.6.2.1.	上报设备信息
 * Created by Mag on 2019/10/16.
 */
public class DeviceReport implements Serializable {
    private static final long serialVersionUID = 6851617040826052930L;
    /**
     * sn的规则，如果接入为FSU终端，则上报该终端和该终端所连接设备信息，
     * 应该为终端SN_所接设备编码NUM 组成如果接入终端为单设备接入，则以接入的唯一识别码为SN",
     */
    private String sn;
    private String enterpriseCode;//该接入sn所属企业的code，网关配置文件获取
    private String serverCode;//对应该接入sn所属的业务服务的code，businessCode
    private String gatewayServerCode;//接入网关的服务code，提供设备向请求使用
    private String regionCode; //区域code默认值为 000000 其获取的顺序为 1.设备报文获取生成 2.服务配置文件获取 3.后期配置
    private String type;//需要一张类型映射的类型映射表，实现实际设备类型 -- 资管设备类型映射表
    private String user;
    private DeviceReportExtend extend; //以必须的方式存在将扩展属性中，如果终端消息不存在，则默认deviceType
    private List<DeviceReport> childDevices;//子设备信息

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

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

    public String getGatewayServerCode() {
        return gatewayServerCode;
    }

    public void setGatewayServerCode(String gatewayServerCode) {
        this.gatewayServerCode = gatewayServerCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeviceReportExtend getExtend() {
        return extend;
    }

    public void setExtend(DeviceReportExtend extend) {
        this.extend = extend;
    }

    public List<DeviceReport> getChildDevices() {
        return childDevices;
    }

    public void setChildDevices(List<DeviceReport> childDevices) {
        this.childDevices = childDevices;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
