package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.ComAttachmentsEntity;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.ComAttachmentsModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.ComAttachmentsService;
import com.kongtrolink.framework.scloud.service.SiteService;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.kongtrolink.framework.scloud.controller.base.ExportController.export;

/**
 * 资产管理-站点资产管理 控制器
 * Created by Eric on 2020/2/12.
 */
@Controller
@RequestMapping(value = "/site/", method = RequestMethod.POST)
public class SiteController extends BaseController{

    @Autowired
    SiteService siteService;
    @Autowired
    ComAttachmentsService comAttachmentsService;

    private String uniqueCode = "YYDS"; //写死，为了自测用
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteController.class);

    /**
     * 获取站点列表
     */
    @RequestMapping(value = "getSiteList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSiteList(@RequestBody SiteQuery siteQuery){
        try{
//            String uniqueCode = getUniqueCode();
            siteQuery.setCurrentRoot(isCurrentRoot());
            siteQuery.setUserId(getUserId());
            List<SiteModel> list = siteService.findSiteList(uniqueCode, siteQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取站点列表失败", false);
        }
    }

    /**
     * 导出站点列表
     */
    @RequestMapping(value = "exportSiteList", method = RequestMethod.POST)
    public void exportSiteList(@RequestBody SiteQuery siteQuery, HttpServletResponse response){
        try{
//            String uniqueCode = getUniqueCode();
            siteQuery.setCurrentRoot(isCurrentRoot());
            siteQuery.setUserId(getUserId());
            List<SiteModel> list = siteService.findSiteList(uniqueCode, siteQuery);
            HSSFWorkbook workbook = siteService.exportSiteList(list);
            export(response, workbook, "站点资产信息列表");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 生成站点编码
     */
    @RequestMapping(value = "createSiteCode", method = RequestMethod.POST)
    public @ResponseBody JsonResult createSiteCode(@RequestBody SiteEntity siteEntity){
//        String uniqueCode = getUniqueCode();
        String siteCode = siteService.createSiteCode(uniqueCode, siteEntity.getTierCode());

        return new JsonResult(siteCode);
    }

    /**
     * 添加站点
     */
    @RequestMapping(value = "addSite", method = RequestMethod.POST)
    public @ResponseBody JsonResult addSite(@RequestBody SiteModel siteModel){
        try{
//            String uniqueCode = getUniqueCode();
            siteService.addSite(uniqueCode, siteModel);

            return new JsonResult("添加成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("添加失败",false);
        }
    }

    /**
     * 批量导入站点
     */
    @RequestMapping(value = "importSiteList/{serverCode}", method = RequestMethod.POST)
    public @ResponseBody JsonResult importSiteList(@PathVariable String serverCode, @RequestBody MultipartFile file){
        try {
//            String uniqueCode = getUniqueCode();

            CommonsMultipartFile cmf = (CommonsMultipartFile) file;
            DiskFileItem dfi = (DiskFileItem)cmf.getFileItem();
            File f = dfi.getStoreLocation();
            try{
                // TODO: 2020/2/12 解析Excel

            }catch (Exception e){
                e.printStackTrace();
            }

            // TODO: 2020/2/12 向资管下发添加站点的MQTT消息

            return new JsonResult("批量导入站点成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("批量导入站点失败", false);
        }
    }

    /**
     * 上传站点图纸
     */
    @RequestMapping(value = "uploadSiteDrawing", method = RequestMethod.POST)
    public @ResponseBody JsonResult uploadSiteDrawing(@RequestBody MultipartFile file){
        try{
//            String uniqueCode = getUniqueCode();
            if (file == null || file.isEmpty()){
                return new JsonResult("文件为空", false);
            }
            ComAttachmentsEntity attachment = comAttachmentsService.saveMultipartFile(uniqueCode, file, false);
            if(attachment==null){
                LOGGER.error("保存文件失败");
                throw new Exception();
            }

            ComAttachmentsModel comAttachmentsModel = new ComAttachmentsModel(attachment.getId(),attachment.getFileName());
            return new JsonResult(comAttachmentsModel);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("上传站点图纸失败", false);
        }
    }

    /**
     * 下载站点图纸
     */
    @RequestMapping(value = "downloadSiteDrawing/{id}", method = RequestMethod.GET)
    public @ResponseBody void downloadSiteDrawing(@PathVariable int id, HttpServletResponse response){
        try{
//            String uniqueCode = getUniqueCode();
            comAttachmentsService.downloadMultipartFile(uniqueCode, id, response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取资产管理员列表
     */
    @RequestMapping(value = "getRespList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getRespList(@RequestBody SiteQuery siteQuery){
        try{
//            String uniqueCode = getUniqueCode();
            List<String> respNames = siteService.getRespList(uniqueCode, siteQuery);
            return new JsonResult(respNames);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取资产管理员列表失败", false);
        }
    }

    /**
     * 修改站点
     */
    @RequestMapping(value = "modifySite", method = RequestMethod.POST)
    public @ResponseBody JsonResult modifySite(@RequestBody SiteModel siteModel){
        try {
//            String uniqueCode = getUniqueCode();
            boolean modifyResult = siteService.modifySite(uniqueCode, siteModel);
            if (modifyResult) {
                return new JsonResult("修改成功", true);
            }else {
                return new JsonResult("修改失败", false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("修改站点异常", false);
        }
    }

    /**
     * 删除站点（批量）
     */
    @RequestMapping(value = "deleteSite", method = RequestMethod.POST)
    public @ResponseBody JsonResult deleteSite(@RequestBody SiteQuery siteQuery){
        try {
//            String uniqueCode = getUniqueCode();
            if (siteQuery.getSiteCodes() != null && siteQuery.getSiteCodes().size() > 0) {
                siteService.deleteSite(uniqueCode, siteQuery);

                return new JsonResult("删除成功", true);
            }else {
                return new JsonResult("未选中站点", false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("删除异常，删除失败", false);
        }
    }

    /**
     * 获取站点维护人员列表
     */
    @RequestMapping(value = "getSiteMaintainers", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSiteMaintainers(@RequestBody SiteQuery siteQuery){
        return null;
    }

    /**
     * 获取简化版站点列表
     *  根据query.getSimplifiedSitekeys()获取到的简化版站点所需参数的key，删减Site中不需要的参数，仅保留Site中所传需要的参数
     */
    @RequestMapping(value = "/getSimplifiedSiteList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSimplifiedSiteList(@RequestBody SiteQuery siteQuery){
//        String uniqueCode = getUniqueCode();
        siteQuery.setCurrentRoot(isCurrentRoot());
        siteQuery.setUserId(getUserId());
        List<SiteModel> siteModelList = siteService.findSiteList(uniqueCode, siteQuery);
        List<JSONObject> objectList = siteService.getSimplifiedSiteList(siteModelList,siteQuery);
        return new JsonResult(objectList);
    }
}
