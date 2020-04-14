package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomDevice;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomSignalTypeConfig;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomSiteInfo;
import com.kongtrolink.framework.scloud.query.MultipleRoomQuery;
import com.kongtrolink.framework.scloud.service.MultipleRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 基本信息
     * getSiteInfo
     */
    @RequestMapping(value = "/getSiteInfo", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSiteInfo(@RequestBody MultipleRoomQuery query) {
        try{
            String uniqueCode = getUniqueCode();
            RoomSiteInfo roomSiteInfo = multipleRoomService.getSiteInfo(uniqueCode,query.getSiteId());
            return new JsonResult(roomSiteInfo);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取数据失败", false);
        }
    }
    /**
     * 初始化要进行的数据
     *
     */
    @RequestMapping(value = "/initType", method = RequestMethod.POST)
    public @ResponseBody JsonResult initSignalType() {
        try{
            String uniqueCode = getUniqueCode();
            multipleRoomService.initSignalType(uniqueCode);
            return new JsonResult("初始化成功");
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("初始化失败", false);
        }
    }
    /**
     * 获取 设备列表
     *
     * @param query uniqueCode 企业编码
     *  siteId  站点ID
     *  返回 设备列表
     */
    @RequestMapping(value = "/getDevice", method = RequestMethod.POST)
    public @ResponseBody JsonResult getRoomDevice(@RequestBody MultipleRoomQuery query) {
        try{
            String uniqueCode = getUniqueCode();
            List<RoomDevice> list = multipleRoomService.getRoomDevice(uniqueCode,query);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取数据失败", false);
        }
    }

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
