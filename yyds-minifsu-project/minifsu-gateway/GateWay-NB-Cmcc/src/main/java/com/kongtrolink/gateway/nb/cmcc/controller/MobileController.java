package com.kongtrolink.gateway.nb.cmcc.controller;


import com.kongtrolink.gateway.nb.cmcc.entity.CommandInfo;
import com.kongtrolink.gateway.nb.cmcc.entity.DeviceInfo;
import com.kongtrolink.gateway.nb.cmcc.entity.base.BaseAck;
import com.kongtrolink.gateway.nb.cmcc.entity.base.JsonResult;
import com.kongtrolink.gateway.nb.cmcc.entity.iot.AddDeviceAck;
import com.kongtrolink.gateway.nb.cmcc.entity.iot.ResourceAck;
import com.kongtrolink.gateway.nb.cmcc.iot.service.NbIotService;
import com.kongtrolink.gateway.nb.cmcc.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Mg on 2018/4/8.
 * RestController 用这个标签 直接返回Json
 */
@RestController
public class MobileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileController.class);
    @Resource
    NbIotService nbIotService;

    @RequestMapping("/addDevice")
    public @ResponseBody
    JsonResult addDevice(@RequestBody DeviceInfo deviceInfo) {
        try{
            BaseAck value = nbIotService.addDevice(deviceInfo.getTitle(),deviceInfo.getImei(),deviceInfo.getImsi());
            AddDeviceAck ack = EntityUtil.getEntity(value,AddDeviceAck.class);
            System.out.println("id:"+ack.getDevice_id());
            System.out.println("psk:"+ack.getPsk());
            return new JsonResult(value.toString());
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("异常 "+e.getMessage(),false);
        }
    }

    @RequestMapping("/getResources")
    public @ResponseBody JsonResult getResources(@RequestBody DeviceInfo deviceInfo) {
        try{
            BaseAck value = nbIotService.getResources(deviceInfo.getImei());
            ResourceAck ack  = EntityUtil.getEntity(value,ResourceAck.class);
            return new JsonResult(ack);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("异常 "+e.getMessage(),false);
        }

    }

    @RequestMapping("/command")
    public @ResponseBody JsonResult command(@RequestBody CommandInfo commandInfo) {
        try{
            String command = commandInfo.getCommand();
            String imei = commandInfo.getImei();
            Integer objId = commandInfo.getObjId();
            Integer objInstId = commandInfo.getObjInstId();
            Integer executeResId = commandInfo.getExecuteResId();
            BaseAck value = nbIotService.command(command,imei,objId,objInstId,executeResId);
            return new JsonResult(value.toString());
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("异常 "+e.getMessage(),false);
        }

    }

}
