package com.kongtrolink.gateway.nb.ctcc.iot.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iotplatform.client.dto.*;
import com.iotplatform.client.invokeapi.PushMessageReceiver;
import com.kongtrolink.gateway.nb.ctcc.entity.PackageInfo;
import com.kongtrolink.gateway.nb.ctcc.execute.SendTool;
import com.kongtrolink.gateway.nb.ctcc.iot.config.NbIotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * NB接口 接收消息
 * 新协议接口
 * //中国铁塔动环监控系统统一互联适配接口技术规范20181206.docx
 * by Mag on 2018/11/21.
 */
@RestController
@EnableAutoConfiguration
public class NbIotController extends PushMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(NbIotController.class);

    @Resource
    SendTool sendTool;
    @Resource
    NbIotConfig nbIotConfig;

    public void handleBody(String body) {
//        LOGGER.info("******* 获取数据 - handleBody *********:\n " + body);
    }

    /**
     * 3.6.1 注册设备通知
     */
    public void handleDeviceAdded(NotifyDeviceAddedDTO body) {
        LOGGER.info("========== 注册设备通知 deviceAdded  ========   " );

        sendMqttOtherMessage(body);
    }

    /**
     * 3.6.2 绑定设备通知
     */
    public void handleBindDevice(NotifyBindDeviceDTO body) {
        LOGGER.info(" ========== 绑定设备通知  bindDevice ========  ");
        sendMqttOtherMessage(body);
    }

    /**
     * 3.6.4 设备数据变化通知
     * **** 主要功能 ： 数据上报 *****
     */
    public void handleDeviceDataChanged(NotifyDeviceDataChangedDTO body) {
        LOGGER.info("======== 设备数据变化通知 deviceDataChanged ========  " );
        DeviceService service  = body.getService();
        if(service==null){
            LOGGER.error("设备数据变化通知 收到的 报文是 null");
            return;
        }
        ObjectNode data = service.getData();
        sendMqttMessage(data,body.getDeviceId());
    }
    /**
     * 3.6.5 批量设备数据变化通知
     */
    @Override
    public void handleDeviceDatasChanged(NotifyDeviceDatasChangedDTO body) {
        LOGGER.info("======== 批量设备数据变化通知 deviceDatasChanged ==== ");
        List<DeviceService> list = body.getServices();
        if(list==null || list.size()==0){
            LOGGER.error("批量设备数据变化通知 收到的 报文是 null");
            return;
        }
        List<ObjectNode> value = new ArrayList<>();
        for(DeviceService data:list){
            value.add(data.getData());
        }
        sendMqttMessage(value,body.getDeviceId());
    }
    /**
     * 3.6.9 设备命令响应通知
     */
    public void handleCommandRsp(NotifyCommandRspDTO body) {
        LOGGER.info("======== 设备命令响应通知 commandRsp ========== " );
//        ObjectNode data = body.getBody();
//        sendMqttMessage(data,body.getDeviceId());
    }
    /**
     * 3.6.3 设备信息变化通知
     */
    public void handleDeviceInfoChanged(NotifyDeviceInfoChangedDTO body) {
        LOGGER.info("设备信息变化通知 deviceInfoChanged ==> " + body);
    }

    /**
     * 3.6.6 设备服务信息变化通知
     */
    @Override
    public void handleServiceInfoChanged(NotifyServiceInfoChangedDTO body) {
        LOGGER.info("设备服务信息变化通知 serviceInfoChanged ==> " + body);
    }

    /**
     * 3.6.7 删除设备通知
     */
    @Override
    public void handleDeviceDeleted(NotifyDeviceDeletedDTO body) {
        LOGGER.info(" 删除设备通知 deviceDeleted ==> " + body);
    }

    /**
     * 3.6.8 设备消息确认通知
     */
    @Override
    public void handleMessageConfirm(NotifyMessageConfirmDTO body) {
        LOGGER.info(" 设备消息确认通知 messageConfirm ==> " + body);
    }

    /**
     * 3.6.10 设备事件通知
     */
    public void handleDeviceEvent(NotifyDeviceEventDTO body) {
        LOGGER.info(" 设备事件通知 deviceEvent ==> " + body);
    }
    /**
     * MQTT转发
     * @param o 收到的报文
     */
    private void sendMqttOtherMessage(Object o) {
        try{
            if(o==null){
                throw new Exception("接收到的参数为 null !!! ");
            }
            LOGGER.info("---> Object: " +o);
            sendTool.sendOmcMessage(o,null);
        }catch (Exception e){
            LOGGER.error("下发MQTT报文失败:"+e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * MQTT转发
     * @param o 收到的报文
     */
    private synchronized void sendMqttMessage(Object o,String deviceId) {
        try {
            if(o==null){
                throw new Exception("接收到的参数为 null !!! ");
            }
            LOGGER.info("---> Object: " +o);
            String pdu = String.valueOf(o);
            JSONObject object = JSONObject.parseObject(pdu);
            JSONObject msg = (JSONObject) object.get("dataStr");
            sendTool.sendRpcMessage(msg.toJSONString(),deviceId);
        } catch (Exception e) {
            LOGGER.error("下发MQTT报文失败:"+e.getMessage());
            e.printStackTrace();
        }


        /*try{
            Map<String, List<PackageInfo>> map = sendTool.getMap();

            ProfileEntity profileEntity = JSONObject.parseObject(pdu,ProfileEntity.class);
            PackageData packageData = profileEntity.getDataStr();
            if(packageData==null){
                throw new Exception("接收到的 dataStr 内容为 null !!! ");
            }
            LOGGER.info("---> dataStr json:  " +packageData.getJson());
            int totalPackage = packageData.getSum();
            String msgid = packageData.getMsgid();
            int page = packageData.getPage();
            String mapKey = msgid+"#"+deviceId;
            String json = packageData.getJson();
            *//**
             * 判断 是否分包
             * 分包 肯定 包含 DataPackAtt字段
             * 不分包 没有字段
             *//*
            if(totalPackage>1){
                LOGGER.info("-----分--分--分--分---数据包 分包处理---分----分----分----");
                PackageInfo packageInfo = new PackageInfo();
                packageInfo.setCtime(new Date());
                packageInfo.setData(packageData.getJson());
                packageInfo.setOrder(page);
                packageInfo.setTotal(totalPackage);
                if(map.containsKey(mapKey)){
                    List<PackageInfo> list = map.get(mapKey);
                    *//**
                     * 超时无用包 判断
                     *//*
                    if (list == null){
                        LOGGER.info("当前key:{}  没有缓存 信息 ",mapKey);
                        list = new ArrayList<>();
                    }else{
                        LOGGER.info("当前key:{} 当前分包缓存个数: {} ",mapKey,list.size());
                    }
                    List<Integer> moveList = new ArrayList<>();
                    for(int i=0;i<list.size();i++){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date timeNow = new Date();
                        Date time = list.get(i).getCtime();
                        long bet = (timeNow.getTime()-time.getTime())/1000;//相差 150秒 唾弃
                        if(bet>nbIotConfig.getPackageTime()){
                            moveList.add(i);
                            LOGGER.info("当前key:{} 移除超时缓存 index: {},当前时间:{} 缓存时间:{},超时限制:{} ",mapKey,i,
                                    sdf.format(timeNow),sdf.format(time),nbIotConfig.getPackageTime());
                        }
                    }
                    for(Integer index:moveList){
                        list.remove((int)index);
                        LOGGER.info("当前key:{} 移除超时缓存 index: {} 成功 ",mapKey,index);

                    }
                    list.add(packageInfo);
                    LOGGER.info("-> 当前key:{} 存放page:{}  到缓存:",mapKey,page);
                    if(list.size()==totalPackage){//如果包内容 = 总包数
                        LOGGER.info("-----合--合--合--合---数据包 合包处理---合----合----合----");
                        String value = getInfoChar(list);
                        if(value==null){
                            throw new Exception("拼凑包有误");
                        }
                        value = value.replaceAll("\\\\","");//把 \ 去掉
                        LOGGER.info("分包最后和包的字符串是:"+value);
//                        JSONObject object = JSONObject.parseObject(value);
                        packageData.setJson(value);
                        //拼完包之后 发送
                        ResponseInfo object = new ResponseInfo();
                        object.initInfo(packageData);
                        sendTool.sendOmcMessage(object,deviceId);
                        //发送之后 置缓存为空
                        map.remove(mapKey);
                    }else{
                        map.put(mapKey,list);
                    }
                }else{
                    List<PackageInfo> list = new ArrayList<>();
                    list.add(packageInfo);
                    map.put(mapKey,list);
                    LOGGER.info("-> 当前key:{} 存放page:{}  到缓存:",mapKey,page);
                }
            }else{
                ResponseInfo object = new ResponseInfo();
                object.initInfo(packageData);
                sendTool.sendOmcMessage(object,deviceId);
            }
        }catch (Exception e){
            LOGGER.error("下发MQTT报文失败:"+e.getMessage());
            e.printStackTrace();
        }*/
    }
    /**
     * 拼凑报文
     */
    private String getInfoChar(List<PackageInfo> list){
        try{
            String[] pduChar = new String[list.size()];
            for(PackageInfo packageInfo:list){
                int order = packageInfo.getOrder();
                pduChar[order-1] = packageInfo.getData();
            }
            String str = "";
            for(String s:pduChar){
                if(s==null){
                    continue;
                }
                str = str + s;
            }
            return str;
        }catch (Exception e){
            LOGGER.error("拼凑包 有误!!");
            e.printStackTrace();
        }
        return null;
    }

}
