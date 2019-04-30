package com.kongtrolink.gateway.nb.ctcc.iot.controller;

/**
 * NB接口 接收消息
 * 旧协议
 * by Mag on 2018/11/21.
 */

public class NbIotControllerBak{
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(NbIotControllerBak.class);
//
//    private Map<String,List<PackageInfo>> map = new HashMap<>();
//
//    @Resource
//    MqttService mqttService;
//    @Resource
//    NbIotConfig nbIotConfig;
//
//    public void handleBody(String body) {
//        LOGGER.info("******* 获取数据 - handleBody *********:\n " + body);
//    }
//
//    /**
//     * 3.6.1 注册设备通知
//     */
//    public void handleDeviceAdded(NotifyDeviceAddedDTO body) {
//        LOGGER.info("========== 注册设备通知 deviceAdded  ========   " );
//
//        sendMqttOtherMessage(body);
//    }
//
//    /**
//     * 3.6.2 绑定设备通知
//     */
//    public void handleBindDevice(NotifyBindDeviceDTO body) {
//        LOGGER.info(" ========== 绑定设备通知  bindDevice ========  ");
//        sendMqttOtherMessage(body);
//    }
//
//    /**
//     * 3.6.4 设备数据变化通知
//     * **** 主要功能 ： 数据上报 *****
//     */
//    public void handleDeviceDataChanged(NotifyDeviceDataChangedDTO body) {
//        LOGGER.info("======== 设备数据变化通知 deviceDataChanged ========  " );
//        DeviceService service  = body.getService();
//        if(service==null){
//            LOGGER.error("设备数据变化通知 收到的 报文是 null");
//            return;
//        }
//        ObjectNode data = service.getData();
//        sendMqttMessage(data);
//    }
//    /**
//     * 3.6.5 批量设备数据变化通知
//     */
//    @Override
//    public void handleDeviceDatasChanged(NotifyDeviceDatasChangedDTO body) {
//        LOGGER.info("======== 批量设备数据变化通知 deviceDatasChanged ==== ");
//        List<DeviceService> list = body.getServices();
//        if(list==null || list.size()==0){
//            LOGGER.error("批量设备数据变化通知 收到的 报文是 null");
//            return;
//        }
//        List<ObjectNode> value = new ArrayList<>();
//        for(DeviceService data:list){
//            value.add(data.getData());
//        }
//        sendMqttMessage(value);
//    }
//    /**
//     * 3.6.9 设备命令响应通知
//     */
//    public void handleCommandRsp(NotifyCommandRspDTO body) {
//        LOGGER.info("======== 设备命令响应通知 commandRsp ========== " );
//        ObjectNode data = body.getBody();
//        sendMqttMessage(data);
//    }
//    /**
//     * 3.6.3 设备信息变化通知
//     */
//    public void handleDeviceInfoChanged(NotifyDeviceInfoChangedDTO body) {
//        LOGGER.info("设备信息变化通知 deviceInfoChanged ==> " + body);
//    }
//
//    /**
//     * 3.6.6 设备服务信息变化通知
//     */
//    @Override
//    public void handleServiceInfoChanged(NotifyServiceInfoChangedDTO body) {
//        LOGGER.info("设备服务信息变化通知 serviceInfoChanged ==> " + body);
//    }
//
//    /**
//     * 3.6.7 删除设备通知
//     */
//    @Override
//    public void handleDeviceDeleted(NotifyDeviceDeletedDTO body) {
//        LOGGER.info(" 删除设备通知 deviceDeleted ==> " + body);
//    }
//
//    /**
//     * 3.6.8 设备消息确认通知
//     */
//    @Override
//    public void handleMessageConfirm(NotifyMessageConfirmDTO body) {
//        LOGGER.info(" 设备消息确认通知 messageConfirm ==> " + body);
//    }
//
//    /**
//     * 3.6.10 设备事件通知
//     */
//    public void handleDeviceEvent(NotifyDeviceEventDTO body) {
//        LOGGER.info(" 设备事件通知 deviceEvent ==> " + body);
//    }
//    /**
//     * MQTT转发
//     * @param o 收到的报文
//     */
//    private void sendMqttOtherMessage(Object o) {
//        try{
//            if(o==null){
//                throw new Exception("接收到的参数为 null !!! ");
//            }
//            LOGGER.info("---> Object: " +o);
//            mqttService.sendOmcMessage(o);
//        }catch (Exception e){
//            LOGGER.error("下发MQTT报文失败:"+e.getMessage());
//            e.printStackTrace();
//        }
//    }
//    /**
//     * MQTT转发
//     * @param o 收到的报文
//     */
//    private void sendMqttMessage(Object o) {
//        try{
//            if(o==null){
//                throw new Exception("接收到的参数为  null !!! ");
//            }
//            LOGGER.info("---> Object: " +o);
//            String pdu = String.valueOf(o);
//            ProfileEntity profileEntity = JSONObject.parseObject(pdu,ProfileEntity.class);
//            String dataStr = profileEntity.getDataStr();
//            LOGGER.info("---> dataStr:  " +dataStr);
//            /**
//             * 判断 是否分包
//             * 分包 肯定 包含 DataPackAtt字段
//             * 不分包 没有字段
//             */
//            if(pdu.contains("DataPackAtt")){
//                LOGGER.info("-----分--分--分--分---数据包 分包处理---分----分----分----");
//                int infoCharIndex = dataStr.indexOf("\"info\":\"");//字符串"index"的位置
//                String infoChar = dataStr.substring(infoCharIndex+8,dataStr.length()-2); //截取 info的字符串 最后的 "} 不需要截取
//                LOGGER.info("dataStr字符串中截取info字段信息:"+infoChar);
//                String dataChar  = dataStr.substring(0,infoCharIndex-1)+"}";
//                LOGGER.info("dataStr字符串中截取 除了 info字段信息"+dataChar);
//                PackageData packageData  = JSONObject.parseObject(dataChar,PackageData.class);
//                DataPackAtt DataPackAtt  = packageData.getDataPackAtt();
//                packageData.setInfo(infoChar);
//                String name = packageData.getName();
//                int totalPackage = DataPackAtt.getDataPack();//总包数
//                PackageInfo packageInfo = new PackageInfo();
//                packageInfo.setCtime(new Date());
//                packageInfo.setData(infoChar);
//                packageInfo.setOrder(DataPackAtt.getDataInd());
//                if(map.containsKey(name)){
//                    List<PackageInfo> list = map.get(name);
//                    List<Integer> moveList = new ArrayList<>();
//                    /**
//                     * 超时无用包 判断
//                     */
//                    for(int i=0;i<list.size();i++){
//                        Date timeNow = new Date();
//                        Date time = list.get(i).getCtime();
//                        long bet = (timeNow.getTime()-time.getTime())/1000;//相差 30秒 唾弃
//                        if(bet>nbIotConfig.getPackageTime()){
//                            moveList.add(i);
//                        }
//                    }
//                    for(Integer index:moveList){
//                        list.remove(index);
//                    }
//                    list.add(packageInfo);
//                    if(list.size()==totalPackage){//如果包内容 = 总包数
//                        LOGGER.info("-----合--合--合--合---数据包 合包处理---合----合----合----");
//                        String value = getInfoChar(list);
//                        if(value==null){
//                            throw new Exception("拼凑包有误");
//                        }
//                        value = value.replaceAll("\\\\","");//把 \ 去掉
//                        LOGGER.info("分包最后和包的字符串是:"+value);
//                        JSONObject object = JSONObject.parseObject(value);
//                        //拼完包之后 发送
//                        mqttService.sendOmcMessage(object);
//                        //发送之后 置缓存为空
//                        map.remove(name);
//                    }else{
//                        map.put(name,list);
//                    }
//                }else{
//                    List<PackageInfo> list = new ArrayList<>();
//                    list.add(packageInfo);
//                    map.put(name,list);
//                }
//            }else{
//                JSONObject object = JSONObject.parseObject(dataStr);
//                mqttService.sendOmcMessage(object);
//            }
//        }catch (Exception e){
//            LOGGER.error("下发MQTT报文失败:"+e.getMessage());
//            e.printStackTrace();
//        }
//    }
//    /**
//     * 拼凑报文
//     */
//    private String getInfoChar(List<PackageInfo> list){
//        try{
//            String[] pduChar = new String[list.size()];
//            for(PackageInfo packageInfo:list){
//                int order = packageInfo.getOrder();
//                pduChar[order-1] = packageInfo.getData();
//            }
//            String str = "";
//            for(String s:pduChar){
//                if(s==null){
//                    continue;
//                }
//                str = str + s;
//            }
//            return str;
//        }catch (Exception e){
//            LOGGER.error("拼凑包 有误!!");
//            e.printStackTrace();
//        }
//        return null;
//    }
}
