package com.kongtrolink.gateway.nb.ctcc.controller;


import com.kongtrolink.gateway.nb.ctcc.entity.QueryTest;
import com.kongtrolink.gateway.nb.ctcc.execute.SendTool;
import com.kongtrolink.gateway.nb.ctcc.iot.service.NbIotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mg on 2018/4/8.
 * RestController 用这个标签 直接返回Json
 */
@RestController
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);
    @Resource
    NbIotService nbIotService;
    @Autowired
    SendTool sendTool;

    @RequestMapping("/hello")
    public Map<String,Object> hello(@RequestBody QueryTest queryTest) {
        Map<String,Object> map = new HashMap<>();
        try{
            LOGGER.info("我进入了 ---- info -----");
            int type = queryTest.getType();
            String deviceId = queryTest.getDeviceId();
            map.put("type",type);
            map.put("deviceId",deviceId);
            if(type==1){
                LOGGER.info(" ---- 批量 获取设备信息-----");
                nbIotService.queryBatchDevice();
            }else if(type==2 && deviceNeed(deviceId)){
                LOGGER.info(" ---- 单个 获取设备信息-----");
                nbIotService.queryDevice(deviceId);
            }else if( type==3 && deviceNeed(deviceId)){
                LOGGER.info(" ---- 下发NB命令-----");
                nbIotService.postNbCommand(deviceId,"sendCmd","sendCmd","{\"你好\"}");
            }else{
                LOGGER.info(" ---- 输入的 type 不是 (1,2,3里面的)-----");
            }

            return map;
        }catch (Exception e){
            e.printStackTrace();
        }
       return map;
    }

    @RequestMapping("/nb01")
    public Map<String,Object> hello001(@RequestBody QueryTest queryTest) {
        Map<String,Object> map = new HashMap<>();
        try{
            LOGGER.info("我进入了 ---- hello001 -----");
            String deviceId = queryTest.getDeviceId();
            if(deviceId==null||"".equals(deviceId)){
                throw new Exception("deviceId 必填 但是 缺失!!!");
            }
            String serverId = queryTest.getServerId();
            String method =queryTest.getMethod();
            String pdu = queryTest.getPdu();
            if(serverId==null||"".equals(serverId)){
                serverId = "AirInfo";
            }
            if(method==null||"".equals(method)){
                method = "AIR_COM";
            }
            nbIotService.postNbCommand(deviceId,serverId,method,pdu);
            map.put("result","下发成功");
            return map;
        }catch (Exception e){
            map.put("result","下发失败");
            map.put("message",e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    @RequestMapping("/register")
    public Map<String,Object> register(@RequestBody QueryTest queryTest) {
        Map<String,Object> map = new HashMap<>();
        try{
            LOGGER.info("我进入了 ---- hello001 -----");
            String nodeId = queryTest.getNodeId();
            boolean flag  = nbIotService.register(nodeId);
            if(flag){
                map.put("result","下发成功");
            }else{
                map.put("result","下发失败");
            }
            return map;
        }catch (Exception e){
            map.put("result","下发失败");
            map.put("message",e.getMessage());
            e.printStackTrace();
        }
        return map;
    }


    @RequestMapping("/clear")
    public Map<String,Object> clearMap(@RequestBody QueryTest queryTest) {
        Map<String,Object> map = new HashMap<>();
        try{
            String deviceId = queryTest.getDeviceId();
            String clear = queryTest.getClear();
            LOGGER.info("clear :{}  deviceId :{} 缓存中的 " , clear,deviceId);
            if(clear !=null){
                sendTool.clearMap(deviceId,clear);
            }
            map.put("result","清空成功");
            return map;
        }catch (Exception e){
            map.put("result","下发失败");
            map.put("message",e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    private boolean deviceNeed(String deviceId){
        if(deviceId==null || deviceId.equals("")){
            System.out.println("device 为 必输项!!");
            return false;
        }
        return true;
    }





}
