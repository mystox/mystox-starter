package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.*;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.FsuDeviceEntity;
import com.kongtrolink.framework.scloud.entity.model.SignalModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfo;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SignalDiInfoQuery;
import com.kongtrolink.framework.scloud.query.SignalQuery;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import com.kongtrolink.framework.scloud.util.pdf.PDFUtil;
import org.apache.commons.collections.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 数据监控 - 实时数据
 * @author Mag
 */
@Controller
@RequestMapping(value = "/realTimeData", method = RequestMethod.POST)
public class RealTimeDataController extends ExportController {

    @Autowired
    private RealTimeDataService realTimeDataService;
    /**
     * 获取设备列表,查询站点或组设备下的设备，根据上级对象 oid 和上级对象描述，
     * 分页查询设备列表。
     */
    @RequestMapping(value = "/getList")
    public @ResponseBody JsonResult getDeviceList(@RequestBody DeviceQuery query) {
        try{
            String uniqueCode = getUniqueCode();
            ListResult<DeviceModel> deviceResult = realTimeDataService.getDeviceList(uniqueCode, query);
            return new JsonResult(deviceResult);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("查询失败",false);
        }
    }

    /**
     * 根据 设备code 获取 该设备所属的FSU
     */
    @RequestMapping(value = "/fsuInfo")
    public @ResponseBody JsonResult fsuInfo(@RequestBody DeviceQuery query) {
        try{
            String uniqueCode = getUniqueCode();
            DeviceEntity deviceEntity = realTimeDataService.getFsuInfoByDeviceCode(uniqueCode, query);
            return new JsonResult(deviceEntity);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("未找到关联的FSU信息",false);
        }
    }

    /**
     * 获取实时数据列表
     */
    @RequestMapping(value = "/getData")
    public @ResponseBody JsonResult getData(@RequestBody SignalQuery query) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
            SignalModel signalModel = realTimeDataService.getData(uniqueCode, query,userId);
            return new JsonResult(signalModel);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("查询失败",false);
        }
    }
    @RequestMapping(value = "/getDataDetail")
    public @ResponseBody JsonResult getDataDetail(@RequestBody SignalQuery query) {
        try{
            String uniqueCode = getUniqueCode();
            Object value  = realTimeDataService.getDataDetail(uniqueCode, query);
            return new JsonResult(value);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("查询失败",false);
        }
    }
    /**
     * 设置值
     *
     */
    @RequestMapping(value = "/setPoint")
    public @ResponseBody JsonResult setPoint(@RequestBody SignalQuery query) {
        try{
            SetPointAckMessage ack = realTimeDataService.setPoint(query);
            return new JsonResult(ack);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("设置失败",false);
        }
    }


    /**
     * 设置阈值
     */
    @RequestMapping(value = "/setThreshold")
    public @ResponseBody JsonResult setThreshold(@RequestBody SignalQuery query) {
        try{
            SetThresholdAckMessage ack = realTimeDataService.setThreshold(query);
            return new JsonResult(ack);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("设置失败",false);
        }
    }

    /**
     * 根据查询 某一个遥测信号值列表 -分页
     * @param query 查询参数
     */
    @RequestMapping(value = "/getSignalDiInfo", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSignalDiInfo(@RequestBody SignalDiInfoQuery query) {
        try {
            String uniqueCode = getUniqueCode();
            List<SignalDiInfo> list = realTimeDataService.getSignalDiInfo(uniqueCode,query);
            int count = realTimeDataService.getSignalDiInfoNum(uniqueCode,query);
            ListResult<SignalDiInfo> value = new ListResult(list,count);
            return new JsonResult(value);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new JsonResult("查询失败", false);
        }
    }

    /**
     * 根据查询 某一个遥测信号值列表 导出
     */
    @RequestMapping(value = "/exportSignalDiInfo", method = RequestMethod.POST)
    public void exportSignalDiInfo(@RequestBody SignalDiInfoQuery query, HttpServletResponse response) {
        String uniqueCode = getUniqueCode();
        query.setPageSize(Integer.MAX_VALUE);
        query.setCurrentPage(1);
        List<SignalDiInfo> list = realTimeDataService.getSignalDiInfo(uniqueCode,query);
        String title = "遥测信号点统计表";
        String[] headsName = { "站点层级", "站点名称", "站点类型", "站点编号", "设备名称", "设备编号", "信号点值"};
        String[] propertiesName = { "tier", "siteName", "siteType", "siteCode", "deviceName", "deviceCode","value"};
        export(response,list,propertiesName, headsName, title);
    }

    /**
     * 导出遥测信号值列表pdf
     */
    @RequestMapping(value = "/exportSignalDiInfoPDF", method = RequestMethod.POST)
    public void exportSignalDiInfoPDF(@RequestBody SignalDiInfoQuery query, HttpServletRequest request,HttpServletResponse response){
        String uniqueCode = getUniqueCode();
        query.setPageSize(Integer.MAX_VALUE);
        query.setCurrentPage(1);
        List<SignalDiInfo> list = realTimeDataService.getSignalDiInfo(uniqueCode,query);

        String fileName = "遥测信号统计表.pdf";
        String[] headsName = { "站点层级", "站点名称", "站点类型", "站点编号", "设备名称", "设备编号", "信号点值"};
        String[] propertiesName = { "tier", "siteName", "siteType", "siteCode", "deviceName", "deviceCode","value"};
        List<Object> signalDiInfoList = new ArrayStack();

        for(SignalDiInfo signalDiInfo : list){
            signalDiInfo.setTier(signalDiInfo.getTier());
            signalDiInfo.setSiteName(signalDiInfo.getSiteName());
            signalDiInfo.setSiteType(signalDiInfo.getSiteType());
            signalDiInfo.setSiteCode(signalDiInfo.getSiteCode());
            signalDiInfo.setDeviceName(signalDiInfo.getDeviceName());
            signalDiInfo.setDeviceCode(signalDiInfo.getDeviceCode());
            signalDiInfo.setValue(signalDiInfo.getValue());

            signalDiInfoList.add(signalDiInfo);
        }
        OutputStream outputStream ;
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName+".pdf");
            outputStream =  response.getOutputStream();
            PDFUtil.createTable(outputStream, "遥测信号统计表", signalDiInfoList, headsName, propertiesName);
        }catch (Exception e){

        }
    }
}
