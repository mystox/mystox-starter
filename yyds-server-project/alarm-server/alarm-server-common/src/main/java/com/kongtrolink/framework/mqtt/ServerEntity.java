package com.kongtrolink.framework.mqtt;

/**
 * @Auther: liudd
 * @Date: 2019/10/16 09:29
 * @Description:
 */
public class ServerEntity {

    private String code;
    private String title;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ServerEntity(String code, String title) {
        this.code = code;
        this.title = title;
    }
}
