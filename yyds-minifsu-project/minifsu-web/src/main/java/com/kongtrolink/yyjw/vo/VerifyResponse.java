package com.kongtrolink.yyjw.vo;

import java.io.Serializable;
import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/12 14:04
 * \* Description:
 * \
 */
public class VerifyResponse implements Serializable{
    private String version;
    private Boolean available;
    private String url;
    private List<String> resourceList;

    public VerifyResponse(String version, Boolean available, String url, List<String> resourceList) {
        this.version = version;
        this.available = available;
        this.url = url;
        this.resourceList = resourceList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<String> resourceList) {
        this.resourceList = resourceList;
    }
}