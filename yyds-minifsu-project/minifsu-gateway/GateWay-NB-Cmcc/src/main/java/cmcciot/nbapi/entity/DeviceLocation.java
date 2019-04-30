package cmcciot.nbapi.entity;

import java.io.Serializable;

/** “纬度”，“精度”，“高度”
 * lon": 106, "lat": 29, "ele
 * date 2018/3/16
 */
public class DeviceLocation implements Serializable{

    // 纬度
    private Integer lon;
    // 经度
    private Integer lat;
    // 高度
    private Integer ele ;

    public Integer getLon() {
        return lon;
    }

    public void setLon(Integer lon) {
        this.lon = lon;
    }

    public Integer getLat() {
        return lat;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    public Integer getEle() {
        return ele;
    }

    public void setEle(Integer ele) {
        this.ele = ele;
    }
}
