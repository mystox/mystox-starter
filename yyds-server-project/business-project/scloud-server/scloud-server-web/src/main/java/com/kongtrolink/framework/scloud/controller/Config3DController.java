/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.scloud.controller;


import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.config3d.Config3dScene;
import com.kongtrolink.framework.scloud.entity.config3d.ConfigAppLocateMap;
import com.kongtrolink.framework.scloud.service.Config3dService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * copy from scloud
 *  组态功能
 * @author Mag
 */
@Controller
@RequestMapping(value = "/config3d")
public class Config3DController extends BaseController {
    
    @Autowired 
    private Config3dService config3dService;

    /**
     * 存储3D场景数据
     */
    @RequestMapping(value = "/saveScene", method = RequestMethod.POST)
    public @ResponseBody
    JsonResult saveScene(@RequestBody Config3dScene scene) {
        try {
            String uniqueCode = getUniqueCode();
            config3dService.upsert(uniqueCode, scene);
            return new JsonResult("保存成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("存储失败",false);

    }
    /**
     * 加载3D场景数据
     */
    @RequestMapping(value = "/loadScene", method = RequestMethod.POST)
    public @ResponseBody JsonResult loadScene(@RequestBody Config3dScene scene) {
        try {
            String uniqueCode = getUniqueCode();
            Config3dScene config3dScene = config3dService.find(uniqueCode, scene.getSiteId());
            return new JsonResult(config3dScene);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }
    

    /**
     * 存储app坐标映射数据
     */
    @RequestMapping(value = "/saveAppLocateMap", method = RequestMethod.POST)
    public @ResponseBody JsonResult saveAppLocateMap(@RequestBody ConfigAppLocateMap map){
        String uniqueCode = getUniqueCode();
        config3dService.upsertConfigAppLocateMap(uniqueCode, map);
        return new JsonResult();
    }
    
    /**
     * 存储app站点组态背景图
     */
//    @RequestMapping(value = "/saveAppBackground", method = RequestMethod.POST)
//    public @ResponseBody JsonResult saveAppBackground(String siteId, MultipartFile file){
//        String uniqueCode = getUniqueCode();
//        try {
//            File dir = new File(pathConfigService.getConfig3dAppSitePicSavePath() + "/" + uniqueCode);
//            FileUtils.forceMkdir(dir);
//
//            String upFileName = file.getOriginalFilename();
//            String suffix = upFileName.substring(upFileName.lastIndexOf("."));
//            String fileName = siteId + suffix;
//            File destFile = new File(dir.getAbsolutePath() + "/" + fileName);
//            file.transferTo(destFile);
//            return new JsonResult("组态背景图存储成功", true);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return new JsonResult("出现异常，存储失败", false);
//    }
    
}
