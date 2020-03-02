package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.scloud.entity.ImageFileInfo;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 图片附件 接口类
 * Created by Eric on 2020/2/12.
 */
public interface ImageFileService {

    /**
     * 保存 上传的 图片信息
     * @param info 图片信息-二进制保存
     */
    void saveImageFile(ImageFileInfo info);

    /**
     * 指定压缩比保存图片
     * @return 图片Id
     */
    String saveImg(String uniqueCode, CommonsMultipartFile file, User uploader, String typeInfo, int width, int height);

    /**
     * 根据 主键ID 查询 图片信息
     */
    ImageFileInfo getInfo(String imgType, Integer id);

    void write(byte[] data, HttpServletResponse response);
}
