package com.kongtrolink.framework.congtroller;

import com.kongtrolink.framework.service.ResourceService;
import com.kongtrolink.framework.util.JsonResult;
import com.kongtrolink.framework.vo.DirectoryVo;
import com.kongtrolink.framework.vo.VerifyResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/12 10:56
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {



    @Autowired
    ResourceService resourceService;

    @RequestMapping("/verify")
    public JsonResult verify(@RequestBody Map<String,Object> requestBody)
    {

        String appId = (String) requestBody.get("appId");
        String os = (String) requestBody.get("os");
        List<String> filenames = (List<String>) requestBody.get("filenames");
        //app版本验证
        String appVersion = resourceService.getAppVersion(appId,os);
        String downUrl = "";
        if (!"ios".equalsIgnoreCase(os))
        {
            downUrl = resourceService.getDownLoadUrl(appId);
        }
        List<String> fileNames = filenames;
        //对比资源文件包
//        if (StringUtils.isNotBlank(filenames))
//        {
//        fileNames = JSONArray.parseArray(filenames, String.class);
//
//        }

        List<String> downFiles = resourceService.compareResources(fileNames);
        VerifyResponse verifyResponse = new VerifyResponse(appVersion,true,downUrl,downFiles);
        return new JsonResult(verifyResponse);
    }

    @RequestMapping(value = "/download/")
    public void download(@RequestParam(required = false) String filename, HttpServletResponse response) {
        File file = FileUtils.getFile("AppResources/config" + "/"+filename);
        outFile(file,filename,response);
    }

    void outFile(File file,String filename, HttpServletResponse response) {
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + filename+".txt");// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @RequestMapping("/getPath")
    public String getPath() {

        try {
            File file = ResourceUtils.getFile("AppResources/config");
            System.out.println(file.getAbsolutePath());
            File[] files = file.listFiles();
            System.out.println("111");
            System.out.println(files.length);
            System.out.println(file.listFiles());
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/getDocumentList")
    public JsonResult getDocumentList() {
        List<DirectoryVo> result = resourceService.getDocumentList();
        return new JsonResult(result);
    }


    @RequestMapping(value = "/documentDownload")
    public void documentDownload(@RequestParam String filename, @RequestParam String path, HttpServletResponse response) {
        File file = FileUtils.getFile(path);
        outFile(file,filename,response);
    }


}