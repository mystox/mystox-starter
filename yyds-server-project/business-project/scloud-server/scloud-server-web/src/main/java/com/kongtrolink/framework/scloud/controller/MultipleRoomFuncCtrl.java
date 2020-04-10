package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.multRoom.*;
import com.kongtrolink.framework.scloud.query.MultipleRoomQuery;
import com.kongtrolink.framework.scloud.query.RoomHistoryAlarmQuery;
import com.kongtrolink.framework.scloud.service.MultipleRoomService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.Signal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;


/**
 * 综合机房
 * Created by Mag on 2020年4月10日
 */
@Controller
@RequestMapping(value = "/multipleRoom", method = RequestMethod.POST)
public class MultipleRoomFuncCtrl  extends ExportController {

    @Autowired
    MultipleRoomService multipleRoomService;
    /**
     * 初始化要进行的数据
     *
     */
    @RequestMapping(value = "/initType", method = RequestMethod.POST)
    public @ResponseBody JsonResult initSignalType(@RequestBody MultipleRoomQuery query) {
        try{
            String uniqueCode = query.getUniqueCode();
            multipleRoomService.initSignalType(uniqueCode);
            return new JsonResult("初始化成功");
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("初始化失败", false);
        }
    }
    /**
     * 基本信息
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
     * 获取 告警统计
     */
    @RequestMapping(value = "/getRoomHistoryAlarm", method = RequestMethod.POST)
    public @ResponseBody JsonResult getRoomHistoryAlarm(@RequestBody MultipleRoomQuery query, HttpServletRequest request) {
        try{
            return new JsonResult(null);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取数据失败", false);
        }
    }




}
