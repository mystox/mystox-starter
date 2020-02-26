package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.ImageFileInfo;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.ImageFileService;
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
    ImageFileService imageFileService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteController.class);

    /**
     * 获取站点列表
     */
    @RequestMapping(value = "getSiteList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSiteList(@RequestBody SiteQuery siteQuery){
        try{
            String uniqueCode = getUniqueCode();
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
            String uniqueCode = getUniqueCode();
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
        String uniqueCode = getUniqueCode();
        String siteCode = siteService.createSiteCode(uniqueCode, siteEntity.getTierCode());

        return new JsonResult(siteCode);
    }

    /**
     * 添加站点
     */
    @RequestMapping(value = "addSite", method = RequestMethod.POST)
    public @ResponseBody JsonResult addSite(@RequestBody SiteModel siteModel){
        try{
            String uniqueCode = getUniqueCode();
            siteService.addSite(uniqueCode, siteModel);

            return new JsonResult("添加成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("添加失败",false);
        }
    }

    /**
     * 导入站点(批量)
     */
    @RequestMapping(value = "importSiteList", method = RequestMethod.POST)
    public @ResponseBody JsonResult importSiteList(@RequestBody MultipartFile file){
        try {
            String uniqueCode = getUniqueCode();

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
     * 上传站点图片
     */
    @RequestMapping(value = "importSiteImage", method = RequestMethod.POST)
    public @ResponseBody JsonResult importSiteImage(@RequestParam("file") CommonsMultipartFile file){
        try{
            String uniqueCode = getUniqueCode();
            User uploader = getUser();
            String imgId = imageFileService.saveImg(uniqueCode, file, uploader, "siteImg", 800, 800);
            return new JsonResult(imgId);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("上传图片失败", false);
        }
    }

    /**
     * 查询站点图片
     */
    @RequestMapping(value = "querySiteImage", method = RequestMethod.GET)
    public @ResponseBody void querySiteImage(@RequestBody String imgId, HttpServletResponse response) {
        ImageFileInfo info = imageFileService.getInfo("clusterImg", Integer.valueOf(imgId));
        if(info==null){
            System.out.println("未找到相关图片");
            return ;
        }
        byte[] data = info.getImage();
        if (data != null) {
            imageFileService.write(data, response);
        }
    }

    /**
     * 获取资产管理员列表
     */
    @RequestMapping(value = "getRespList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getRespList(@RequestBody SiteQuery siteQuery){
        try{
            String uniqueCode = getUniqueCode();
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
            String uniqueCode = getUniqueCode();
            siteService.modifySite(uniqueCode, siteModel);
            return new JsonResult("修改成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("修改异常，修改失败", false);
        }
    }

    /**
     * 删除站点（批量）
     */
    @RequestMapping(value = "deleteSite", method = RequestMethod.POST)
    public @ResponseBody JsonResult deleteSite(@RequestBody SiteEntity siteEntity){
        try {
            String uniqueCode = getUniqueCode();
            String code = siteEntity.getCode();
            siteService.deleteSite(uniqueCode, code);

            return new JsonResult("删除成功", true);
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
}
