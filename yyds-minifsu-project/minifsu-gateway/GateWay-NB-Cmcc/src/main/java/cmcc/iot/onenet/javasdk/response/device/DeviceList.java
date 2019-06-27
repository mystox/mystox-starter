package cmcc.iot.onenet.javasdk.response.device;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DeviceList {
    @JsonProperty("total_count")
    private int totalcount;
    @JsonProperty("per_page")
    private int perpage;
    @JsonProperty("page")
    private int page;
    @JsonProperty("devices")
    private ArrayList<DeviceItem> devices;

    @JsonCreator
    public DeviceList(@JsonProperty("total_count") int totalcount, @JsonProperty("per_page") int perpage, @JsonProperty("page") int page, @JsonProperty("devices") ArrayList<DeviceItem> devices) {
        this.totalcount = totalcount;
        this.perpage = perpage;
        this.page = page;
        this.devices = devices;
    }

    public static class DeviceItem {
        @JsonProperty("id")
        private String id;
        @JsonProperty("title")
        private String title;
        @JsonProperty("protocol")
        private String protocol;
        @JsonProperty("desc")
        private String desc;
        @JsonProperty("private")
        private Boolean isPrivate;
        @JsonProperty("auth_info")
        private Object authInfo;
        @JsonProperty("online")
        private Boolean isonline;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("create_time")
        private Date createTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("act_time")
        private Date actTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("last_login")
        private Date lastLogin;
        @JsonProperty("location")
        private Location location;
        @JsonProperty("rg_id")
        private String rgid;
        @JsonProperty("interval")
        private Integer interval;
        @JsonProperty("tags")
        private List<String> tags;
        @JsonProperty("obsv")
        private Boolean obsv;
        @JsonProperty("obsv_st")
        private Boolean obsvSt;
        @JsonProperty("chip")
        private Integer chip;
        @JsonProperty("other")
        private Map<String, Object> other;

        @JsonCreator
        public DeviceItem(@JsonProperty("id") String id, @JsonProperty("title") String title, @JsonProperty("chip") Integer chip,
                          @JsonProperty("obsv") Boolean obsv, @JsonProperty("protocol") String protocol, @JsonProperty("desc") String desc,
                          @JsonProperty("private") Boolean isPrivate, @JsonProperty("auth_info") Object authInfo, @JsonProperty("online") Boolean isonline,
                          @JsonProperty("create_time") Date createTime, @JsonProperty("location") Location location, @JsonProperty("rg_id") String rgid,
                          @JsonProperty("interval") Integer interval, @JsonProperty("tags") List<String> tags, @JsonProperty("other") Map<String, Object> other,
                                          @JsonProperty("obsv_st")  Boolean obsvSt, @JsonProperty("act_time") Date actTime, @JsonProperty("last_login") Date lastLogin) {
            super();
            this.id = id;
            this.title = title;
            this.protocol = protocol;
            this.desc = desc;
            this.isPrivate = isPrivate;
            this.authInfo = authInfo;
            this.isonline = isonline;
            this.createTime = createTime;
            this.location = location;
            this.rgid = rgid;
            this.interval = interval;
            this.tags = tags;
            this.other = other;
            this.chip = chip;
            this.obsv = obsv;
            this.obsvSt = obsvSt;
            this.actTime = actTime;
            this.lastLogin = lastLogin;
        }


        public Date getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(Date lastLogin) {
            this.lastLogin = lastLogin;
        }

        public Date getActTime() {
            return actTime;
        }

        public void setActTime(Date actTime) {
            this.actTime = actTime;
        }

        public Boolean getObsv() {
            return obsv;
        }

        public void setObsv(Boolean obsv) {
            this.obsv = obsv;
        }

        public Boolean getObsvSt() {
            return obsvSt;
        }

        public void setObsvSt(Boolean obsvSt) {
            this.obsvSt = obsvSt;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Boolean getIsPrivate() {
            return isPrivate;
        }

        public void setIsPrivate(Boolean isPrivate) {
            this.isPrivate = isPrivate;
        }

        public Object getAuthInfo() {
            return authInfo;
        }

        public void setAuthInfo(Object authInfo) {
            this.authInfo = authInfo;
        }

        public Boolean getIsonline() {
            return isonline;
        }

        public void setIsonline(Boolean isonline) {
            this.isonline = isonline;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Integer getChip() {
            return chip;
        }

        public void setChip(Integer chip) {
            this.chip = chip;
        }

        public static class Location {
            @JsonProperty("ele")
            private double ele;
            @JsonProperty("lat")
            private double lat;//纬度
            @JsonProperty("lon")
            private double lon;//经度

            @JsonCreator
            public Location(@JsonProperty("ele") double ele, @JsonProperty("lat") double lat, @JsonProperty("lon") double lon) {
                this.ele = ele;
                this.lat = lat;
                this.lon = lon;
            }

            public double getEle() {
                return ele;
            }

            public void setEle(double ele) {
                this.ele = ele;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLon() {
                return lon;
            }

            public void setLon(double lon) {
                this.lon = lon;
            }
        }

        public String getRgid() {
            return rgid;
        }

        public void setRgid(String rgid) {
            this.rgid = rgid;
        }

        public Integer getInterval() {
            return interval;
        }

        public void setInterval(Integer interval) {
            this.interval = interval;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public Map<String, Object> getOther() {
            return other;
        }

        public void setOther(Map<String, Object> other) {
            this.other = other;
        }
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public int getPerpage() {
        return perpage;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<DeviceItem> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<DeviceItem> devices) {
        this.devices = devices;
    }

}
