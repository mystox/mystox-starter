package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.scloud.dao.ImageFileMongo;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.entity.ImageFileInfo;
import com.kongtrolink.framework.scloud.service.ImageFileService;
import com.kongtrolink.framework.scloud.util.ImageUtil;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片附件 接口实现类
 * Created by Eric on 2020/2/12.
 */
@Service
public class ImageFileServiceImpl implements ImageFileService{

    @Autowired
    ImageFileMongo imageFileMongo;

    /**
     * 保存 上传的 图片信息
     *
     * @param info 图片信息-二进制保存
     */
    @Override
    public void saveImageFile(ImageFileInfo info) {
        imageFileMongo.saveImageFile(info);
    }

    /**
     * 指定压缩比保存图片
     */
    @Override
    public String saveImg(String uniqueCode, CommonsMultipartFile file, User uploader, String typeInfo, int width, int height) {
        String imgId = saveImgWH(uniqueCode, file, uploader, typeInfo, width, height);
        return imgId;
    }

    /**
     * 根据 主键ID 查询 图片信息
     *
     * @param imgType 图片类型
     * @param id 图片Id
     */
    @Override
    public ImageFileInfo getInfo(String imgType, Integer id) {
        return imageFileMongo.getInfo(imgType, id);
    }

    @Override
    public void write(byte[] data, HttpServletResponse response) {
        if(null == data || data.length == 0){
            return ;
        }
        OutputStream outputStream = null;
        try{
            response.setContentType("image/jpg");
            outputStream = response.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String saveImgWH(String uniqueCode, CommonsMultipartFile file, User uploader, String typeInfo, int width, int height) {
        if(width == -1 && height == -1){
            //都为-1，表示按照原图存储
        }else if(width < 1 || height < 1){
            System.out.println("图片压缩比错误， width:"+width + ", height:"+height);
            width = 400;
            height = 400;
        }
        DiskFileItem dfi = (DiskFileItem)file.getFileItem();
        File f = dfi.getStoreLocation();
        ImageFileInfo info = new ImageFileInfo();
        info.setUniqueCode(uniqueCode);
        info.setType(typeInfo);
        info.setImage(ImageUtil.getImageBinaryWH(f, width, height));
        info.setUploader(new FacadeView(uploader.getId(), uploader.getUsername()));
        saveImageFile(info);
        return String.valueOf(info.getId());
    }
}
