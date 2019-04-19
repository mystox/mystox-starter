package com.kongtrolink.framework.jsonType;

/**
 * @author fengw
 * 终端绑定信息
 * 新建文件 2019-4-16 10:32:16
 * */
public class JsonStation {
    //fsuId
    private String fsuId;
    //sn
    private String sn;
    //站点名称
    private String name;
    //站点地址
    private String address;
    //站点描述
    private String desc;
    //信号字典表模式
    private int dictMode;
    //FSU所使用的VPN名称
    private String vpnName;
    //FSU的应用类型
    private String fsuClass;
    //是否解除绑定
    private boolean disabled;
    //解除绑定时间
    private long disabledTime;

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDictMode() {
        return dictMode;
    }

    public void setDictMode(int dictMode) {
        this.dictMode = dictMode;
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public String getFsuClass() {
        return fsuClass;
    }

    public void setFsuClass(String fsuClass) {
        this.fsuClass = fsuClass;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public long getDisabledTime() {
        return disabledTime;
    }

    public void setDisabledTime(long disabledTime) {
        this.disabledTime = disabledTime;
    }
}
