package com.kongtrolink.gateway.nb.cmcc.iot.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.gateway.nb.cmcc.entity.PackageData;
import com.kongtrolink.gateway.nb.cmcc.entity.PackageInfo;
import com.kongtrolink.gateway.nb.cmcc.entity.ResponseInfo;
import com.kongtrolink.gateway.nb.cmcc.entity.iot.DataPushAck;
import com.kongtrolink.gateway.nb.cmcc.execute.SendTool;
import com.kongtrolink.gateway.nb.cmcc.iot.config.NbIotConfig;
import com.kongtrolink.gateway.nb.cmcc.util.EntityUtil;
import com.kongtrolink.gateway.nb.cmcc.util.Util;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * NB接口 接收消息
 * 新协议接口
 * //中国铁塔动环监控系统统一互联适配接口技术规范20181206.docx
 * by Mag on 2019/02/14.
 */
@RestController
@EnableAutoConfiguration
public class NbIotController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NbIotController.class);
    @Resource
    SendTool sendTool;
    @Resource
    NbIotConfig nbIotConfig;

    private static Logger logger = LoggerFactory.getLogger(NbIotController.class);
    /**
     * 功能描述：第三方平台数据接收。<p>
     *           <ul>注:
     *               <li>1.OneNet平台为了保证数据不丢失，有重发机制，如果重复数据对业务有影响，数据接收端需要对重复数据进行排除重复处理。</li>
     *               <li>2.OneNet每一次post数据请求后，等待客户端的响应都设有时限，在规定时限内没有收到响应会认为发送失败。
     *                    接收程序接收到数据时，尽量先缓存起来，再做业务逻辑处理。</li>
     *           </ul>
     * @param body 数据消息
     * @return 任意字符串。OneNet平台接收到http 200的响应，才会认为数据推送成功，否则会重发。
     */
    @RequestMapping(value = "/receive",method = RequestMethod.POST)
    @ResponseBody
    public String receive(@RequestBody String body) throws Exception {
        logger.info("data receive: body ===> " + body);
        /************************************************
         *  解析数据推送请求，非加密模式。
         *  如果是明文模式使用以下代码
         **************************************************/
        Util.BodyObj obj = JSONObject.parseObject(body,Util.BodyObj.class);
        if (obj != null){
            DataPushAck ack = new DataPushAck();
            ack.initDataPush(obj);
            messageArrived(ack);
        }else {
            logger.info("data receive: body empty error");
        }
        return "ok";
    }


    private void messageArrived(DataPushAck ack){
        if(ack ==null || ack.getMsg()==null){
            logger.info("data receive: body empty error");
            return;
        }
        Object value = ack.getMsg().getValue(); //获取具体报文值
        try{
            String valueChar = String.valueOf(value);
//            if(valueChar.contains("page") && valueChar.contains("json") ){
                logger.info("data receive: message: {}",valueChar);
                valueChar = StringEscapeUtils.unescapeJava(valueChar);
                logger.info("data receive: JSON: {}",valueChar);
                PackageData packageData = EntityUtil.getEntityString(valueChar,PackageData.class);
                sendMqttMessage(valueChar,ack.getMsg().getImei());
//            }
        }catch (Exception e){
            logger.error("data 出错: " + value );
            e.printStackTrace();
        }

    }

    /**
     *
     * @param value 终端协议报文
     * @param imei
     */
    private void sendMqttMessage(String value,String imei) {

        sendTool.sendRpcMessage(value,imei);
    }
    /**
     * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
     * @param msg 验证消息
     * @param nonce 随机串
     * @param signature 签名
     * @return msg值
     */

    @RequestMapping(value = "/receive", method = RequestMethod.GET)
    @ResponseBody
    public String check(@RequestParam(value = "msg") String msg,
                        @RequestParam(value = "nonce") String nonce,
                        @RequestParam(value = "signature") String signature) throws UnsupportedEncodingException {

        logger.info("url&token check: msg:{} nonce:{} signature:{}",msg,nonce,signature);
        return msg;
    }

   /* *//**
     * MQTT转发
     * @param o 收到的报文
     *//*
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
    }*/
    /**
     * MQTT转发
     * @param  packageData 收到的报文
     */
    private void sendMqttMessage(PackageData packageData,String imei) {
        try{
            Map<String, List<PackageInfo>> map = sendTool.getMap();
            if(packageData ==null){
                throw new Exception("接收到的参数为 packageData 为 null !!! ");
            }
            LOGGER.info("---> dataStr json:  " +packageData.getJson());
            int totalPackage = packageData.getSum();
            String msgid = packageData.getMsgid();
            int page = packageData.getPage();
            String mapKey = msgid+"#"+imei;
            String json = packageData.getJson();
            /**
             * 判断 是否分包
             * 分包 肯定 包含 DataPackAtt字段
             * 不分包 没有字段
             */
            if(totalPackage!=1){
                LOGGER.info("-----分--分--分--分---数据包 分包处理---分----分----分----");
                PackageInfo packageInfo = new PackageInfo();
                packageInfo.setCtime(new Date());
                packageInfo.setData(packageData.getJson());
                packageInfo.setOrder(page);
                packageInfo.setTotal(totalPackage);
                if(map.containsKey(mapKey)){
                    List<PackageInfo> list = map.get(mapKey);
                    /**
                     * 超时无用包 判断
                     */
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
                        sendTool.sendOmcMessage(object,imei);
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
                sendTool.sendOmcMessage(object,imei);
            }
        }catch (Exception e){
            LOGGER.error("下发MQTT报文失败:"+e.getMessage());
            e.printStackTrace();
        }
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
