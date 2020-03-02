package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.PullDownQuery;
import com.kongtrolink.framework.scloud.service.PullDownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 下拉选择框
 */
@Controller
@RequestMapping(value = "/downPull", method = RequestMethod.POST)
public class PullDownController  extends ExportController {

    @Autowired
    PullDownService pullDownService;

    /**
     * 设备类型 下拉框
     * @return 设备类型列表
     */
    @RequestMapping(value = "/getDeviceTypeList")
    public @ResponseBody JsonResult  getDeviceTypeList() {
        try{
            String uniqueCode = getUniqueCode();
            List<DeviceType> list = pullDownService.getDeviceTypeList(uniqueCode);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }

    /**
     * 根据设备类型 获取该设备下的信号点列表
     *
     * @param query deviceType 设备类型
     * @param query type       遥测等类型
     * @return 信号点列表
     */
    @RequestMapping(value = "/getSignalTypeList")
    public @ResponseBody JsonResult  getSignalTypeList(@RequestBody PullDownQuery query) {
        try{
            String uniqueCode = getUniqueCode();
            List<SignalType> list = pullDownService.getSignalTypeList(uniqueCode,query);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }


}
