package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.service.DeviceService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.kongtrolink.framework.scloud.controller.base.ExportController.export;

/**
 * 资产管理-设备资产管理 控制器
 * Created by Eric on 2020/2/12.
 */
@Controller
@RequestMapping(value = "/device/", method = RequestMethod.POST)
public class DeviceController extends BaseController{

    @Autowired
    DeviceService deviceService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);

    /**
     * 获取单个站点下设备列表
     */
    @RequestMapping(value = "getDeviceList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getDeviceList(@RequestBody DeviceQuery deviceQuery){
        try{
            String uniqueCode = getUniqueCode();
            List<DeviceModel> list = deviceService.findDeviceList(uniqueCode, deviceQuery);

            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取设备列表异常", false);
        }
    }

    /**
     * 导出设备列表
     */
    @RequestMapping(value = "exportDeviceList", method = RequestMethod.POST)
    public void exportDeviceList(@RequestBody DeviceQuery deviceQuery, HttpServletResponse response){
        try{
            String uniqueCode = getUniqueCode();
            List<DeviceModel> list = deviceService.findDeviceList(uniqueCode, deviceQuery);
            HSSFWorkbook workbook = deviceService.exportDeviceList(list);
            export(response, workbook, "站点资产信息列表");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 生成设备编码
     */
    @RequestMapping(value = "createDeviceCode", method = RequestMethod.POST)
    public @ResponseBody JsonResult createDeviceCode(@RequestBody DeviceModel deviceModel){
        try {
            String uniqueCode = getUniqueCode();
            String code = deviceService.createDeviceCode(uniqueCode, deviceModel);
            return new JsonResult(code);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("生成设备编码失败", false);
        }
    }

    /**
     * 添加设备
     */
    @RequestMapping(value = "addDevice", method = RequestMethod.POST)
    public @ResponseBody JsonResult addDevice(@RequestBody DeviceModel deviceModel){
        try{
            String uniqueCode = getUniqueCode();

            return new JsonResult("添加成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("添加失败", false);
        }
    }

    /**
     * 批量导入设备
     */
    @RequestMapping(value = "importDeviceList", method = RequestMethod.POST)
    public @ResponseBody JsonResult importDeviceList(@RequestBody MultipartFile multipartFile){
        try{

            return new JsonResult("批量导入设备成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("批量导入设备失败", false);
        }
    }

    /**
     * 修改设备
     */
    @RequestMapping(value = "modifyDevice", method = RequestMethod.POST)
    public @ResponseBody JsonResult modifyDevice(@RequestBody DeviceModel deviceModel){
        try{

            return new JsonResult("修改成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("修改失败", false);
        }
    }

    /**
     * 删除设备
     */
    @RequestMapping(value = "deleteDevice", method = RequestMethod.POST)
    public @ResponseBody JsonResult deleteDevice(@RequestBody DeviceEntity deviceEntity){
        try{

            return new JsonResult("删除成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("删除失败", false);
        }
    }

    /**
     * 获取企业所有设备类型
     */
    @RequestMapping(value = "getDeviceTypeList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getDeviceTypeList(@RequestBody DeviceEntity deviceEntity){
        try {
            String uniqueCode = getUniqueCode();
            List<DeviceType> list = new ArrayList<>();

            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取设备类型列表失败", false);
        }
    }

}
