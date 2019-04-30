package cmcciot.nbapi.entity;

import cmcciot.nbapi.config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuocongbin
 * date 2018/3/16
 */
public class Device extends CommonEntity{
    private static final long serialVersionUID = 6538074222293846526L;
    // 设备名称，字符和数字组成的字符串，必填参数
    private String title;
    // 设备描述信息，可填参数
    private String desc;
    // 设备标签，可填参数
    private List<String> tags;
    // 设备接入协议，这里指定为: LWM2M，必填参数
    private String protocol;
    // 设备地理位置，格式为：{"lon": 106, "lat": 29, "ele": 370}，可填参数
    private DeviceLocation location;
    // 设备IMSI，必填参数
    private String imsi;
    // 设备接入平台是否启用自动订阅功能，可填参数
    private Boolean obsv;
    // 其他信息，可填参数
    private Object other;
    //测试设备与芯片类型对应关系（取值1-6）
    private Integer chip;

    private Map<String,String> auth_info;

    public Device() {
    }

    /**
     * @param title，有字符或者数字组成，必填
     * @param imei，要求在OneNET平台唯一，必填
     * @param imsi，必填
     */
    public Device(String title, String imei, String imsi) {
        this.title = title;
        this.auth_info = new HashMap<>();
        this.auth_info.put(imei,imsi);
        this.protocol = "LWM2M";
        if(imei!=null && imei.length()==4){
           this.chip = 1;
        }

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public DeviceLocation getLocation() {
        return location;
    }

    public void setLocation(DeviceLocation location) {
        this.location = location;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public Boolean getObsv() {
        return obsv;
    }

    public void setObsv(Boolean obsv) {
        this.obsv = obsv;
    }

    public Object getOther() {
        return other;
    }

    public void setOther(Object other) {
        this.other = other;
    }

    public Integer getChip() {
        return chip;
    }

    public void setChip(Integer chip) {
        this.chip = chip;
    }

    public Map<String, String> getAuth_info() {
        return auth_info;
    }

    public void setAuth_info(Map<String, String> auth_info) {
        this.auth_info = auth_info;
    }

    @Override
    public String toUrl() {
        StringBuilder url = new StringBuilder(Config.getDomainName());
        url.append("/devices");
        return url.toString();
    }
}
