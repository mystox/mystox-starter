package com.kongtrolink.gateway.nb.ctcc.iot;

import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.*;
import com.iotplatform.client.invokeapi.Authentication;
import com.iotplatform.client.invokeapi.DeviceManagement;
import com.iotplatform.client.invokeapi.SubscriptionManagement;
import com.kongtrolink.gateway.nb.ctcc.iot.config.NbIotConfig;
import com.kongtrolink.gateway.nb.ctcc.util.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * xx
 * by Mag on 2018/11/20.
 */
@Component
public class NbIotServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NbIotServer.class);
    /**
     * 客户端信息
     */
    private NorthApiClient northApiClient;

    @Resource
    private NbIotConfig nbIotConfig;
    /**
     * 启动
     */
    public void start(){
        try{
            //设置NB电信平台的对接暗号
            northApiClient = AuthUtil.initApiClient(nbIotConfig);
            String version = northApiClient.getVersion();
            LOGGER.info("version:{} ",version);
            /**
             * 获取 token
             * 测试版 否则 直接进行 定时刷新token接口 3.1.3
             * */
            Authentication authentication = new Authentication(northApiClient);
            AuthOutDTO authOutDTO = authentication.getAuthToken();
            String accessToken  = authOutDTO.getAccessToken();
            LOGGER.info("accessToken:{} ",accessToken);
            // 开启 定时刷新功能 accessToken直接传入null即可（如果传入的accessToken不是null，则使用传入的accessToken）
            authentication.startRefreshTokenTimer();
            LOGGER.info("开启 定时刷新功能 accessToken  ");
            /**
             * 消息订阅 - 订阅设备注册
             * 注意这个IP 不是局域网的内部IP啊！
             *
             *   78.	bindDevice（绑定设备）
                 79.	deviceAdded（添加新设备）
                 81.	deviceDataChanged（设备数据变化）
                 82.    deviceDatasChanged （批量设备数据变化）
                 85.	commandRsp（命令响应）
                 86.	deviceEvent（设备事件）
             * */
             //已经订阅的就不需要要再次订阅了
            String localIp = nbIotConfig.getOutIp();
            String localPort = nbIotConfig.getOutPort();
            String callbackUrl = "http://"+localIp+":"+localPort+"/v1.0.0/messageReceiver";
            LOGGER.info("callbackUrl: {}" , callbackUrl);
            subScript(callbackUrl,"bindDevice","绑定设备");
            subScript(callbackUrl,"deviceAdded","添加新设备");
            subScript(callbackUrl,"deviceDataChanged","设备数据变化");
//            subScript(callbackUrl,"deviceDatasChanged","批量设备数据变化");
            subScript(callbackUrl,"commandRsp","命令响应");
            subScript(callbackUrl,"deviceEvent","设备事件");
            LOGGER.info(" 连接 电信 NB-IOT 平台 成功  ");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 开始订阅 6个
     * 78.	bindDevice（绑定设备）
       79.	deviceAdded（添加新设备）
       81.	deviceDataChanged（设备数据变化）
       85.	commandRsp（命令响应）
       86.	deviceEvent（设备事件）
     */
    private void subScript(String callbackUrl,String subName,String detail){
        SubscriptionManagement subscriptionManagement = new SubscriptionManagement(northApiClient);
        SubscriptionDTO subDTO = subDeviceData(subscriptionManagement, subName, callbackUrl, null);
        if(subDTO==null){
            LOGGER.error("订阅 :{} - {}  主题 失败 !!!! ",subName,detail);
        }else{
            LOGGER.info("订阅 :{} - {}  主题 成功 ...  ",subName,detail);
        }
    }

    public NorthApiClient getNorthApiClient() {
        return northApiClient;
    }

    public void setNorthApiClient(NorthApiClient northApiClient) {
        this.northApiClient = northApiClient;
    }

    private SubscriptionDTO subDeviceData(SubscriptionManagement subscriptionManagement,
                                                 String notifyType, String callbackUrl, String accessToken) {
        SubDeviceDataInDTO sddInDTO = new SubDeviceDataInDTO();
        sddInDTO.setNotifyType(notifyType);
        sddInDTO.setCallbackUrl(callbackUrl);
        try {
            return subscriptionManagement.subDeviceData(sddInDTO, null, accessToken);
        } catch (NorthApiException e) {
            String errorCode = e.getError_code();
            if("200002".equals(errorCode)){
                LOGGER.error("200002  已经被订阅。 无需再次订阅----");
            }
            System.out.println(e.toString());
        }
        return null;
    }
    public boolean register(String nodeId){
        DeviceManagement deviceManagement = new DeviceManagement(northApiClient);
        RegDirectDeviceOutDTO dto  = registerDevice(deviceManagement, null, nodeId);
        if(dto==null){
            return false;
        }
        return true;
    }

    /**
     * 注册用户
     */
    private RegDirectDeviceOutDTO registerDevice(DeviceManagement deviceManagement, String accessToken,String nodeid) {
        int timeout = 3000;
        RegDirectDeviceInDTO2 rddid = new RegDirectDeviceInDTO2();
        String verifyCode = nodeid;
        rddid.setNodeId(nodeid);
        rddid.setVerifyCode(verifyCode);
        rddid.setTimeout(timeout);
        try {
            RegDirectDeviceOutDTO value = deviceManagement.regDirectDevice(rddid, null, accessToken);
            System.out.println(value.toString());
            return value;
        } catch (NorthApiException e) {
            System.out.println(e.toString());
        }
        return null;
    }
}
