package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.gateway.tower.core.util.IDGeneratorUtil;
import com.kongtrolink.framework.scloud.dao.ComAttachmentsMongo;
import com.kongtrolink.framework.scloud.entity.ComAttachmentsEntity;
import com.kongtrolink.framework.scloud.service.ComAttachmentsService;
import com.kongtrolink.framework.scloud.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;

/**
 * 文件上传 接口实现类
 * Created by Eric on 2020/3/3.
 */
@Service
public class ComAttachmentsServiceImpl implements ComAttachmentsService{

    @Autowired
    ComAttachmentsMongo comAttachmentsMongo;
    @Value("${file.savePath}")
    private String savePath;

    /**
     * 保存 附件信息
     *
     * @param uniqueCode 企业识别码
     * @param file 附件信息
     * @return 附件信息
     */
    @Override
    public ComAttachmentsEntity saveMultipartFile(String uniqueCode, MultipartFile file, boolean flag) {
        try{
            // 解析文件
            String fileName = file.getOriginalFilename(); //file Name
            String prefix = fileName.substring(fileName.lastIndexOf(".") + 1); //fileType
            ComAttachmentsEntity attachment = new ComAttachmentsEntity();
            attachment.setFileName(fileName);
            attachment.setFileType(prefix);
            attachment.setSize(file.getSize());
            String aliName = IDGeneratorUtil.getMessageID()+"."+prefix;
            String filePath = savePath + "/" + aliName;
            attachment.setAliName(aliName);

            byte[] data = ImageUtil.getFileBinary(file);
            if(data==null){
                throw new Exception("解析文件异常");
            }
            attachment.setData(data);
            attachment.setCreateTime(new Date().getTime());
            comAttachmentsMongo.saveComAttachmentsEntity(uniqueCode, attachment);
            ComAttachmentsEntity returnValue = new ComAttachmentsEntity();
            /**
             * returnValue need only id in database
             *  so create new object and set the id
             */
            returnValue.setId(attachment.getId());
            if(flag){
                File saveFile = new File(filePath);
                if(!saveFile.getParentFile().exists()){
                    saveFile.mkdirs();
                }
                file.transferTo(saveFile);
            }
            return attachment;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param fileId
     * @param response
     */
    @Override
    public void downloadMultipartFile(String uniqueCode, int fileId, HttpServletResponse response) {
        OutputStream outputStream = null;
        try {
            ComAttachmentsEntity attachment = comAttachmentsMongo.getById(uniqueCode, fileId);
            if(attachment==null){
                throw new Exception("The file is empty.");
            }
            String fileName  = attachment.getFileName();
            String suffix = attachment.getFileType();//获取文件类型
            String[] images = {"jpg","jpeg","bmp","gif","png"};
            byte[] data = attachment.getData();
            if (data != null) {
                //文件下载设置
                response.reset();
                //判断该文件是否是图片
                if(suffix!=null && useList(images,suffix.toLowerCase())){
                    response.setContentType("image/jpg");
                }else{
                    response.setContentType("application/force-download;charset=utf-8");
                }
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
                outputStream = response.getOutputStream();
                outputStream.write(data);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //使用List
    private boolean useList(String[] arr,String targetValue){
        return Arrays.asList(arr).contains(targetValue);
    }
}
