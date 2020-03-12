package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONArray;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.dao.CompanyMongo;
import com.kongtrolink.framework.scloud.entity.CompanyEntity;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.DeviceTypeExport;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicUserEntity;
import com.kongtrolink.framework.scloud.entity.model.CompanyModel;
import com.kongtrolink.framework.scloud.exception.ExcelParseException;
import com.kongtrolink.framework.scloud.service.CompanyService;
import com.kongtrolink.framework.scloud.service.DeviceSignalTypeService;
import com.kongtrolink.framework.scloud.service.TypeMappingExcelService;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.kongtrolink.framework.scloud.controller.base.ExportController.export;

/**
 * 系统管理-企业管理 控制器
 * Created by Eric on 2020/2/5.
 */
@Controller
@RequestMapping(value = "/company/", method = RequestMethod.POST)
public class CompanyController extends BaseController{

    @Autowired
    CompanyMongo companyMongo;
    @Autowired
    CompanyService companyService;
    @Autowired
    DeviceSignalTypeService deviceSignalTypeService;
    @Autowired
    TypeMappingExcelService typeMappingExcelService;
    @Autowired
    MqttOpera mqttOpera;

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);
    private String uniqueCode = "YYDS"; //写死，为了自测用

    /**
     * 获取企业信息、企业业务配置、企业告警提醒配置
     */
    @RequestMapping(value = "getCompany", method = RequestMethod.POST)
    public @ResponseBody JsonResult getCompany(){
        try {
//            String uniqueCode = getUniqueCode();
            CompanyEntity companyEntity = companyMongo.findCompanyInfo(uniqueCode);
            if (companyEntity != null) {
                CompanyModel companyModel = companyService.getCompanyModel(companyEntity);
                return new JsonResult(companyModel);
            } else {
                return new JsonResult("企业不存在，获取企业信息失败", false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取企业信息失败",false);
        }
    }

    /**
     * 修改企业扩展信息、企业业务配置、企业告警提醒配置
     * @param companyEntity 企业信息及配置信息
     * @return 提交成功 或 提交失败
     */
    @RequestMapping(value = "updateCompany", method = RequestMethod.POST)
    public @ResponseBody JsonResult updateCompany(@RequestBody CompanyEntity companyEntity){
        try {
//            String uniqueCode = getUniqueCode();
            companyService.updateCompany(uniqueCode, companyEntity);
            return new JsonResult("提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("提交失败", false);
        }
    }

    /**
     * 导入企业设备信号类型映射表
     */
    @RequestMapping(value = "importSignalType", method = RequestMethod.POST)
    public synchronized @ResponseBody JsonResult importSignalType(MultipartFile file){
//        String uniqueCode = getUniqueCode();
        CommonsMultipartFile cmf = (CommonsMultipartFile)file;
        DiskFileItem dfi = (DiskFileItem)cmf.getFileItem();
        File f = dfi.getStoreLocation();
        List<DeviceType> list = null;
        try {
            list = typeMappingExcelService.read(f);
        } catch (ExcelParseException ex) {
            LOGGER.error(ex.getMessage());
            return new JsonResult(ex.getMessage(), false);
        }

        if(list != null && list.size() > 0){
            deviceSignalTypeService.modifySignalType(uniqueCode, list);
            return new JsonResult(list);
        }else{
            return new JsonResult("Excel 文件解析结果为空", false);
        }
    }

    /**
     * 获取企业联系人列表
     */
    @RequestMapping(value = "getContacts", method = RequestMethod.POST)
    public @ResponseBody JsonResult getContacts(){
        try {
//            String uniqueCode = getUniqueCode();
            //从【云管】获取企业下所有用户
            MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_USER_LIST_BY_ENTERPRISE_CODE, uniqueCode);
            int stateCode = msgResult.getStateCode();
            if (1 == stateCode){
                List<BasicUserEntity> basicUserEntityList = JSONArray.parseArray(msgResult.getMsg(), BasicUserEntity.class);
                return new JsonResult(basicUserEntityList);
            }else {
                return new JsonResult("获取企业联系人列表失败", false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取企业联系人列表失败",false);
        }
    }

    /**
     * 获取企业设备信号类型映射表
     */
    @RequestMapping(value = "getSignalType", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSignalType(){
        try {
//            String uniqueCode = getUniqueCode();
            List<DeviceType> deviceTypes = deviceSignalTypeService.querySignalType(uniqueCode);
            return new JsonResult(deviceTypes);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取信号类型映射表失败", false);
        }
    }

    /**
     * 导出企业设备信号类型映射表
     */
    @RequestMapping(value = "exportSignalType", method = RequestMethod.GET)
    public @ResponseBody void exportSignalType(HttpServletResponse response){
        try{
//            String uniqueCode = getUniqueCode();
            List<DeviceTypeExport> list = deviceSignalTypeService.getDeviceTypeExport(uniqueCode);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String title = "信号点映射表"+sdf.format(new Date());
            String[] headsName = {"设备类型","设备类型编号","信号名称","信号类型编号","单位（遥测/遥调）","信号ID","信号类型","通信故障告警标识"};
            String[] propertiesName = {"deviceTypeName","deviceTypeCode","signalTypeName","signalTypeCode","measurement", "cntbId","type","communicationError"};
            export(response, list, propertiesName, headsName, title);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
