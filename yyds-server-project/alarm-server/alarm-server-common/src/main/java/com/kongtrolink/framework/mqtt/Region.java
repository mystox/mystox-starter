package com.kongtrolink.framework.mqtt;

/**
 * @Auther: liudd
 * @Date: 2019/10/31 14:12
 * @Description: 地区实体对象，用于从云管获取兑取名称
 */
public class Region {
    private String code;
    private String id;
    private String latitude;
    private String longitude;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Region{" +
                "code='" + code + '\'' +
                ", id='" + id + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", name=" + name ;
    }
}
