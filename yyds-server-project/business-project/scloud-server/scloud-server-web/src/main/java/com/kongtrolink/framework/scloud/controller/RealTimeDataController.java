package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.Device;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 数据监控 - 实时数据
 * @author Mag
 */
@Controller
@RequestMapping(value = "/realTimeData", method = RequestMethod.POST)
public class RealTimeDataController extends BaseController {

    @Autowired
    private RealTimeDataService realTimeDataService;
    /**
     * 获取设备列表,查询站点或组设备下的设备，根据上级对象 oid 和上级对象描述，
     * 分页查询设备列表。
     */
    @RequestMapping(value = "/getList")
    public @ResponseBody
    JsonResult getDeviceList(@RequestBody DeviceQuery query) {
        try{
            String uniqueCode = getUniqueCode();
            ListResult<DeviceModel> deviceResult = realTimeDataService.getDeviceList(uniqueCode, query);
            return new JsonResult(deviceResult);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("查询失败",false);
        }

    }
}
