package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.dao.CompanyMongo;
import com.kongtrolink.framework.scloud.entity.CompanyEntity;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.model.CompanyModel;
import com.kongtrolink.framework.scloud.exception.ExcelParseException;
import com.kongtrolink.framework.scloud.service.CompanyService;
import com.kongtrolink.framework.scloud.service.SignalService;
import com.kongtrolink.framework.scloud.service.TypeMappingExcelService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

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
    SignalService signalService;
    @Autowired
    TypeMappingExcelService typeMappingExcelService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);

    /**
     * 获取企业信息、企业业务配置、企业告警提醒配置
     */
    @RequestMapping(value = "getCompany", method = RequestMethod.POST)
    public @ResponseBody JsonResult getCompany(){
        try {
            String uniqueCode = getUniqueCode();
            // TODO: 2020/2/11 从云管获取企业基本信息（云管该对外接口还没有开发好 ）

            // TODO: 2020/2/11 从云管获取企业下所有用户（云管该对外接口还没有开发好）

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
            String uniqueCode = getUniqueCode();
            companyService.updateCompany(uniqueCode, companyEntity);
            return new JsonResult("提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("提交失败", false);
        }
    }

    /**
     * 导入企业设备信号类型映射表
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "importSignalType", method = RequestMethod.POST)
    public synchronized @ResponseBody JsonResult importSignalType(MultipartFile file, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
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
            signalService.modifySignalType(uniqueCode, list);
            return new JsonResult(list);
        }else{
            return new JsonResult("Excel 文件解析结果为空", false);
        }
    }
}
