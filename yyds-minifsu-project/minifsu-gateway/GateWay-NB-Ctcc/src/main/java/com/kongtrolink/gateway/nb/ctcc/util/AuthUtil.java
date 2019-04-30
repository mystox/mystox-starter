package com.kongtrolink.gateway.nb.ctcc.util;

import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.ClientInfo;
import com.kongtrolink.gateway.nb.ctcc.iot.config.NbIotConfig;

/**
 * xx
 * by Mag on 2018/11/22.
 */
public class AuthUtil {

    private static NorthApiClient northApiClient = null;

    public static NorthApiClient initApiClient(NbIotConfig config) {
        if (northApiClient != null) {
            return northApiClient;
        }
        northApiClient = new NorthApiClient();
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setPlatformIp(config.getPlatformIp());
        clientInfo.setPlatformPort(config.getPlatformPort());
        clientInfo.setAppId(config.getAppId());
        clientInfo.setSecret(config.getSecret());
        try {
            northApiClient.setClientInfo(clientInfo);
            northApiClient.initSSLConfig();
        } catch (NorthApiException e) {
            System.out.println(e.toString());
        }

        return northApiClient;
    }

    /**
     * 16进制报文进行AED加密
     * 这边直接是加密文件 不需要这一步
     */
//    public static String getAesPropertyValue(String propertyName) {
//        String aesPwd = "123987";
//        byte[] secret = HexParser.parseHexStr2Byte(PropertyUtil.getProperty(propertyName));
//        return new String(AesUtil.decrypt(secret, aesPwd));
//    }
}
