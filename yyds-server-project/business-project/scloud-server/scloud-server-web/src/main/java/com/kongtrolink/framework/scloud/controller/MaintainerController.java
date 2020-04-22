package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.entity.MaintainerEntity;
import com.kongtrolink.framework.scloud.entity.model.MaintainerModel;
import com.kongtrolink.framework.scloud.exception.ExcelParseException;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicUserEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicUserRoleEntity;
import com.kongtrolink.framework.scloud.query.MaintainerQuery;
import com.kongtrolink.framework.scloud.service.MaintainerExcelService;
import com.kongtrolink.framework.scloud.service.MaintainerService;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.fileupload.disk.DiskFileItem;
import com.kongtrolink.framework.scloud.util.StringUtil;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kongtrolink.framework.scloud.controller.base.ExportController.export;

/**
 * 系统管理-用户管理-维护用户 控制器
 * Created by Eric on 2020/2/28.
 */
@Controller
@RequestMapping(value = "/maintainer/", method = RequestMethod.POST)
public class MaintainerController extends BaseController {

    @Autowired
    MaintainerService maintainerService;
    @Autowired
    MaintainerExcelService maintainerExcelService;
    @Autowired
    MqttOpera mqttOpera;

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainerController.class);

    /**
     * @auther: liudd
     * @date: 2020/4/21 9:37
     * 功能描述:列表
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult list(@RequestBody MaintainerQuery maintainerQuery){
        try{
            String uniqueCode = getUniqueCode();
            if(StringUtil.isNUll(uniqueCode)){
                uniqueCode = "YYDS";
            }
            List<MaintainerEntity> entityList = maintainerService.list(uniqueCode, maintainerQuery);
            List<MaintainerModel> modelList = maintainerService.listModelsFromEntities(uniqueCode, entityList, maintainerQuery);
            return new JsonResult(modelList);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取维护用户列表异常", false);
        }
    }

    /**
     * 获取维护用户列表
     */
    @RequestMapping(value = "getMaintainerList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getMaintainerList(@RequestBody MaintainerQuery maintainerQuery){
        try{
            String uniqueCode = getUniqueCode();
            List<MaintainerModel> list = maintainerService.getMaintainerList(uniqueCode, maintainerQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取维护用户列表异常", false);
        }
    }

    /**
     * 导出维护用户列表
     */
    @RequestMapping(value = "exportMaintainerList", method = RequestMethod.POST)
    public void exportMaintainerList(@RequestBody MaintainerQuery maintainerQuery, HttpServletResponse response){
        try{
            String uniqueCode = getUniqueCode();
            List<MaintainerModel> list = maintainerService.getMaintainerList(uniqueCode, maintainerQuery);
            HSSFWorkbook workbook = maintainerService.exportMaintainerList(list);
            export(response, workbook, "维护用户表");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加维护用户
     */
    @RequestMapping(value = "addMaintainer", method = RequestMethod.POST)
    public @ResponseBody JsonResult addMaintainer(@RequestBody MaintainerModel maintainerModel){
        try{
            String uniqueCode = getUniqueCode();
            if (maintainerService.isMaintainerExist(uniqueCode, maintainerModel.getUsername())){
                return new JsonResult("该账号已存在", false);
            }
            String userId = maintainerService.addMaintainer(uniqueCode, maintainerModel);
            if (userId != null){
                return new JsonResult(userId);
            }else {
                return new JsonResult("添加失败", true);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("添加异常", false);
        }
    }

    /**
     * 批量导入维护用户
     */
    @RequestMapping(value = "importMaintainerList", method = RequestMethod.POST)
    public @ResponseBody JsonResult importMaintainerList(@RequestBody MultipartFile file){
        String uniqueCode = getUniqueCode();

        Map<String, String> userRoleMap = new HashMap<>();  //key：角色名称，value：角色Id

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", uniqueCode);

        //获取当前企业下的角色列表
        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_ROLE_LIST, JSON.toJSONString(jsonObject));
        if (CommonConstant.SUCCESSFUL == msgResult.getStateCode()){
            List<BasicUserRoleEntity> basicUserRoleEntityList = JSONArray.parseArray(msgResult.getMsg(), BasicUserRoleEntity.class);
            if (basicUserRoleEntityList != null && basicUserRoleEntityList.size() > 0){
                for (BasicUserRoleEntity role : basicUserRoleEntityList){
                    userRoleMap.put(role.getName(), role.getId());
                }
                if (!userRoleMap.containsKey(CommonConstant.ROLE_MAINTAINER)){
                    return new JsonResult("当前不存在【维护人员】角色，请先添加该角色", false);
                }
            }else {
                return new JsonResult("当前不存在【维护人员】角色，请先添加该角色", false);
            }
        }else {
            return new JsonResult("获取维护人员角色异常", false);
        }

        //解析Excel文件
        CommonsMultipartFile cmf = (CommonsMultipartFile) file;
        DiskFileItem dfi = (DiskFileItem)cmf.getFileItem();
        File f = dfi.getStoreLocation();
        List<MaintainerModel> list = null;
        try {
            list = maintainerExcelService.read(f, uniqueCode);
        }catch (ExcelParseException ex){
            LOGGER.error(ex.getMessage());
            return new JsonResult(ex.getMessage(), false);
        }
        if (list == null || list.size() <= 0){
            return new JsonResult("维护用户信息解析失败", false);
        }

        //批量添加维护用户
        String maintainerRoleId = userRoleMap.get(CommonConstant.ROLE_MAINTAINER);
        maintainerService.addMaintainerList(uniqueCode, list, maintainerRoleId, CommonConstant.ROLE_MAINTAINER);

        return new JsonResult("批量导入维护用户成功", true);
    }

    /**
     * 修改维护用户
     */
    @RequestMapping(value = "modifyMaintainer", method = RequestMethod.POST)
    public @ResponseBody JsonResult modifyMaintainer(@RequestBody MaintainerModel maintainerModel){
        try{
            String uniqueCode = getUniqueCode();
            boolean modifyResult = maintainerService.modifyMaintainer(uniqueCode, maintainerModel);
            if (modifyResult) {
                return new JsonResult("修改成功", true);
            }else {
                return new JsonResult("修改失败", false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("修改维护用户异常", false);
        }
    }

    /**
     * 删除维护用户(批量)
     */
    @RequestMapping(value = "deleteMaintainer", method = RequestMethod.POST)
    public @ResponseBody JsonResult deleteMaintainer(@RequestBody MaintainerQuery maintainerQuery){
        try {
            String uniqueCode = getUniqueCode();
            if (maintainerQuery.getUserIds() != null && maintainerQuery.getUserIds().size() > 0) {
                maintainerService.deleteMaintainer(uniqueCode, maintainerQuery);
                return new JsonResult("删除成功", true);
            }else {
                return new JsonResult("未选择用户", false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("删除失败" ,false);
        }
    }

}
