package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomSignalTypeConfig;
import com.kongtrolink.framework.scloud.service.MultipleRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 综合机房
 * @author Mag
 **/
@Controller
@RequestMapping(value = "/multipleRoom", method = RequestMethod.POST)
public class MultipleRoomController  extends BaseController {
    @Autowired
    MultipleRoomService multipleRoomService;
    /**
     * 保存自定义信号点显示配置
     */
    @RequestMapping(value = "/addSignalConfig", method = RequestMethod.POST)
    public @ResponseBody
    JsonResult addShowSignalConfig(@RequestBody RoomSignalTypeConfig config) {
        try{
            String uniqueCode = getUniqueCode();
            boolean result = multipleRoomService.addShowSignalConfig(uniqueCode,config);
            if(result){
                return new JsonResult("保存成功");
            }else{
                return new JsonResult("保存失败", false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("保存失败", false);
        }
    }
    /**
     * 根据设备查询信号点配置包含默认值 前天界面用
     */
    @RequestMapping(value = "/queryRoomConfigShow", method = RequestMethod.POST)
    public @ResponseBody JsonResult queryRoomConfigShow(@RequestBody RoomSignalTypeConfig config) {
        try{
            String uniqueCode = getUniqueCode();
            int deviceId = config.getDeviceId();
            RoomSignalTypeConfig result = multipleRoomService.queryRoomSignalTypeConfigShow(uniqueCode,deviceId);
            return new JsonResult(result);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("查询失败", false);
        }
    }
}
