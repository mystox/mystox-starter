package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.MaintainerEntity;
import com.kongtrolink.framework.scloud.entity.model.MaintainerModel;
import com.kongtrolink.framework.scloud.query.MaintainerQuery;
import com.kongtrolink.framework.scloud.service.MaintainerService;
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
import java.util.List;

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

    private String uniqueCode = "YYDS"; //写死，为了自测用
    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainerController.class);

    /**
     * 获取维护用户列表
     */
    @RequestMapping(value = "getMaintainerList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getMaintainerList(@RequestBody MaintainerQuery maintainerQuery){
        try{
//            String uniqueCode = getUniqueCode();
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
//            String uniqueCode = getUniqueCode();
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
//            String uniqueCode = getUniqueCode();
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
    public @ResponseBody JsonResult importMaintainerList(@RequestBody MultipartFile multipartFile){
        try{
//            String uniqueCode = getUniqueCode();

            return new JsonResult(null);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("导入失败", false);
        }
    }

    /**
     * 修改维护用户
     */
    @RequestMapping(value = "modifyMaintainer", method = RequestMethod.POST)
    public @ResponseBody JsonResult modifyMaintainer(@RequestBody MaintainerModel maintainerModel){
        try{
//            String uniqueCode = getUniqueCode();
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
//            String uniqueCode = getUniqueCode();
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
