package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.model.Document;
import com.kongtrolink.framework.service.ResourceService;
import com.kongtrolink.framework.vo.DirectoryVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/12 13:54
 * \* Description:
 * \
 */
@Service
@Component
public class ResourceServiceImpl implements ResourceService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Value("${app.code.yiyi}")
    String appCodeYiyi;
    @Value("${app.code.yiyijw}")
    String appCodeYiyijw;
    @Value("${app.code.xyl}")
    String appCodeXYL;
    @Value("${app.code.liheng}")
    String appCodeLiHeng;
    @Value("${app.version}")
    String versionPath;
    @Value("${app.android.download.yiyijw}")
    String yiyijwDownLoad;

    @Override
    public List<String> compareResources(List<String> fileNames) {
        Map<String, String> fileMap = getResourceMap(fileNames);

        List<String> resultNames = new ArrayList<>();
        try {
            File directory = ResourceUtils.getFile("AppResources/config");
            if (directory != null) {
                File[] fileList = directory.listFiles();
                for (File file : fileList) {
                    String filename = file.getName();
                    if (StringUtils.isNotBlank(filename)) {
                        int index = filename.lastIndexOf("_");
                        if (index == -1)
                            continue;
                        String name = filename.substring(0, index);
                        String resourceVersion = filename.substring(index + 1, filename.length());

                        if (!fileMap.containsKey(name)) { //不包含则添加
                            resultNames.add(filename);
                            continue;
                        }
                        //包含则判断
                        String fileVersion = fileMap.get(name);
                        if (StringUtils.isBlank(fileVersion)) {
                            if (!fileVersion.equals(resourceVersion)) {//版本不一致则加进更新
                                resultNames.add(filename);
                            }
                        }
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resultNames;
    }

    Map<String, String> getResourceMap(List<String> fileNames) {
        Map<String, String> resourceMap = new HashMap<>();

        for (String filename : fileNames) {
            if (StringUtils.isNotBlank(filename)) {
                int index = filename.lastIndexOf("_");
                if (index != -1)
                    resourceMap.put(filename.substring(0, index), filename.substring(index + 1, filename.length()));
            }
        }
        return resourceMap;

    }

    @Override
    public String getAppVersion(String appId, String os) {
        try {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new URL(versionPath).openStream())))
            {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line == null || "".equals(line.trim()) || "\n".equals(line.trim())) {
                        continue;
                    }
                    String paramKey = line.split("=")[0];
                    String version = line.split("=")[1];

                    String[] keys = paramKey.split("\\.");
                    if (keys.length <= 0 || keys.length > 2) {
                        continue;
                    }

                    // 特殊：YiYi app 的版本号 key 中不包含 appId 标识
                    if (keys.length == 1 && appId.equals(appCodeYiyi.toLowerCase()) && os.equals(keys[0])) {
                        return version;
                    }

                    if (appId.equals(keys[0].toLowerCase()) && os.equals(keys[1])) {
                        return version;
                    }
                }
                logger.error("App发送的系统参数不正确");
                return null;
            }

        } catch (FileNotFoundException ex) {
            logger.error("App版本信息文件未找到 {}", ex.getMessage());
        } catch (IOException ex) {
            logger.error("App版本信息文件读取异常 {}", ex.getMessage());
        } finally {

        }
        return null;
    }

    @Override
    public String getDownLoadUrl(String appId) {
        if (StringUtils.isNotBlank(appId)) {
            if (appId.equalsIgnoreCase(appCodeYiyijw))
                return yiyijwDownLoad;
        }

        return null;

    }

    @Override
    public List<DirectoryVo> getDocumentList() {
        List<DirectoryVo> result = new ArrayList<>();
        try {

            File directory = ResourceUtils.getFile("AppResources/document");
            if (directory != null) {

                File[] fileList = directory.listFiles();
                if (fileList != null && fileList.length > 0) {
                    for (File dir2 : fileList) {
                        List<Document> list = new ArrayList<>();
                        File[] files = dir2.listFiles();
                        for (File document : files) {
                            String filename = document.getName();
                            list.add(new Document(filename,
                                    filename.contains(".") ? filename.split("\\.")[1] : "",
                                    document.getPath()));
                        }
                        DirectoryVo vo = new DirectoryVo();
                        vo.setName(dir2.getName());
                        vo.setNumber(files.length);
                        vo.setList(list);
                        result.add(vo);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

        return result;

    }

   /* public static void main(String[] args)
    {
        try {

            File directory = ResourceUtils.getFile("static/AppResources");
            System.out.println(directory.getCanonicalPath());
            System.out.println(directory.getPath());
            System.out.println(directory.getAbsolutePath());

            File[] fileList = directory.listFiles();
            File classpath = ResourceUtils.getFile("classpath:");
            System.out.println(classpath.getPath());

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }*/
}