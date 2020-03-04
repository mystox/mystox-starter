package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.ComAttachmentsEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件上传接口类
 * Created by Eric on 2020/3/3.
 */
public interface ComAttachmentsService {

    /**
     * 保存 附件信息
     * @param uniqueCode 企业识别码
     * @param file 附件信息
     * @return 附件信息
     */
    ComAttachmentsEntity saveMultipartFile(String uniqueCode, MultipartFile file, boolean flag);

    /**
     * 文件下载
     */
    void downloadMultipartFile(String uniqueCode, int fileId, HttpServletResponse response);
}
