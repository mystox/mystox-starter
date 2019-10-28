package com.kongtrolink.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author dengqg
 */
public class ConfUtil {

    private static final Logger logger  =  LoggerFactory.getLogger(ConfUtil.class);
    public static String FILE = "message.properties";
    public static String APIUSER = "email.api_user";
    public static String APIKEY = "email.api_key";
    public static String FROMADDRESS = "email.from_address";
    public static String FROMNAME="email.from_name";
    public static String SMSUSER = "sms.sms_user";
    public static String SMSKEY = "sms.sms_key";

    public static String SMSREGISTERTEMPLATEID = "sms.register_template_id";
    public static String SMSFINDPSWTEMPLATEID = "sms.findpsw_template_id";
    private String apiUser;
    private String apiKey;
    private String fromAddress;
    private String fromName;
    private String smsUser;
    private String smsKey;
    private Integer smsRegisterTemplateId;
    private Integer smsFindpswTemplateId;

    public String getApiUser() {
        return apiUser;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getSmsUser() {
        return smsUser;
    }

    public String getSmsKey() {
        return smsKey;
    }

    public Integer getSmsRegisterTemplateId() {
        return smsRegisterTemplateId;
    }

    public Integer getSmsFindpswTemplateId() {
        return smsFindpswTemplateId;
    }

    public static ConfUtil instance = new ConfUtil();

    public static ConfUtil getInstance() {
        return instance;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    private ConfUtil() {
        load();
    }

    private void load() {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE);
            Properties p = new Properties();
            p.load(in);
            apiUser = p.getProperty(APIUSER);
            apiKey = p.getProperty(APIKEY);
            fromAddress = p.getProperty(FROMADDRESS);
            fromName=p.getProperty(FROMNAME);
            smsUser = p.getProperty(SMSUSER);
            smsKey = p.getProperty(SMSKEY);
            smsRegisterTemplateId = Integer.parseInt(p.getProperty(SMSREGISTERTEMPLATEID));
            smsFindpswTemplateId = Integer.parseInt(p.getProperty(SMSFINDPSWTEMPLATEID));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
