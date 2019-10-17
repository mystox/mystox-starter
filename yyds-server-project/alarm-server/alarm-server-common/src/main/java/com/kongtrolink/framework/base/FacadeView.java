package com.kongtrolink.framework.base;

/**
 * @Auther: liudd
 * @Date: 2019/9/16 13:42
 * @Description:
 */
public class FacadeView {

    private String strId;
    private String name;

    public FacadeView(String strId, String name) {
        this.strId = strId;
        this.name = name;
    }

    public FacadeView() {
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
