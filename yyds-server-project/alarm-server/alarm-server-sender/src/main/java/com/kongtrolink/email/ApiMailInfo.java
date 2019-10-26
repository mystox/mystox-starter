package com.kongtrolink.email;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: liudd
 * @date: 2019/10/26 9:09
 * 功能描述:
 */
public class ApiMailInfo {
    private String url;             //发送rul
    protected String apiUser;
    protected String apiKey;
    private String to;              //接受者
    protected String from;
    protected String subject;//邮件标题
    protected String xsmtpapi;//邮件地址及参数
    protected String from_name;//邮件前缀
    protected String templateInvokeName;//模板代码
    private String html;                //HTML内容
    private Map<String, InputStream> inputStreamMap = new HashMap<>();       //文件名和文件流

    public ApiMailInfo(String apiUser, String apiKey, String from, String from_name) {
        this.apiUser = apiUser;
        this.apiKey = apiKey;
        this.from = from;
        this.from_name = from_name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Map<String, InputStream> getInputStreamMap() {
        return inputStreamMap;
    }

    public void setInputStreamMap(Map<String, InputStream> inputStreamMap) {
        this.inputStreamMap = inputStreamMap;
    }

    public String getApiUser() {
        return apiUser;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getXsmtpapi() {
        return xsmtpapi;
    }

    public void setXsmtpapi(String xsmtpapi) {
        this.xsmtpapi = xsmtpapi;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTemplateInvokeName() {
        return templateInvokeName;
    }

    public void setTemplateInvokeName(String templateInvokeName) {
        this.templateInvokeName = templateInvokeName;
    }

    @Override
    public String toString() {
        return "ApiMailInfo{" + "apiUser=" + apiUser + ", apiKey=" + apiKey + ", from=" + from + ", subject=" + subject + ", xsmtpapi=" + xsmtpapi + ", from_name=" + from_name + ", templateInvokeName=" + templateInvokeName + '}';
    }

 



}
