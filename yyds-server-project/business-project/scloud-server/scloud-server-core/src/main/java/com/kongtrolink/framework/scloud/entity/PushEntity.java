package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.constant.BaseConstant;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/16 09:04
 * @Description: jpush实体类
 */
public class PushEntity {
    private String title;               //标题
    private String content;             //内容
    private List<String> accountList;   //接收账号
    private String keyName;             //APP推送点击跳转参数名
    private String keyValue;            //APP推送点击跳转跳转参数值
    private String proName;             //第三方内部区分不同推送内容格式参数名。比如APP工单推送包含告警上报，告警消除，告警工单，巡检工单等。
    private Integer proValue;           //第三方内部区分不同推送内容格式参数值。比如APP工单推送包含告警上报，告警消除，告警工单，巡检工单等。

    private String pushType = BaseConstant.TEMPLATE_APP;        //推送类型MESSAGE/EMAIL/APP
    private String pushKey;             //第三方账号
    private String pushSecret;          //第三方秘钥
    private boolean product;            //false-开发环境; true-生产环境


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<String> accountList) {
        this.accountList = accountList;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public Integer getProValue() {
        return proValue;
    }

    public void setProValue(Integer proValue) {
        this.proValue = proValue;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getPushSecret() {
        return pushSecret;
    }

    public void setPushSecret(String pushSecret) {
        this.pushSecret = pushSecret;
    }

    public boolean isProduct() {
        return product;
    }

    public void setProduct(boolean product) {
        this.product = product;
    }

    public PushEntity(String title, String content, List<String> accountList, String pushType, String pushKey, String pushSecret, boolean product) {
        this.title = title;
        this.content = content;
        this.accountList = accountList;
        this.pushType = pushType;
        this.pushKey = pushKey;
        this.pushSecret = pushSecret;
        this.product = product;
    }

    @Override
    public String toString() {
        return "PushEntity{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", accountList=" + accountList +
                ", keyName='" + keyName + '\'' +
                ", keyValue='" + keyValue + '\'' +
                ", proName='" + proName + '\'' +
                ", proValue=" + proValue +
                ", pushType='" + pushType + '\'' +
                ", pushKey='" + pushKey + '\'' +
                ", pushSecret='" + pushSecret + '\'' +
                ", product=" + product +
                '}';
    }
}
