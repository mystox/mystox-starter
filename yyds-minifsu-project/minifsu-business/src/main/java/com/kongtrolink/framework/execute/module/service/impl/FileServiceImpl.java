package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.utils.ByteUtil;
import com.kongtrolink.framework.execute.module.service.FileService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by mystoxlol on 2019/4/9, 19:16.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class FileServiceImpl implements FileService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${resource.path.sn}")
    private String snPath;

    @Override
    public byte[] fileGet(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();//设备信息包的报文
        JSONObject fileMsg = (JSONObject) payload.get("file");
        String filename = (String) fileMsg.get("filename");
        Integer fileType = (Integer) fileMsg.get("type");
        Integer fileNum = (Integer) fileMsg.get("fileNum");
        Integer startIndex = (Integer) fileMsg.get("startIndex");
        Integer endIndex = (Integer) fileMsg.get("endIndex");

        try {
            File file = ResourceUtils.getFile(snPath + File.separator + sn + File.separator + filename);
            System.out.println(file.length());
            long fileLen = file.length();
            if (fileLen > Integer.MAX_VALUE)
                logger.error("FILE" + file.getAbsoluteFile() + "is to large then MAX_INTEGER!!!");
            if (fileLen < endIndex + 1)
                endIndex = Math.toIntExact(fileLen - 1);
            int requestLen = endIndex - startIndex + 1;
            int responseLen = requestLen + 6;
            byte[] bytes = new byte[responseLen]; //增加文件报文的头尾信息
            bytes[0] = fileType.byteValue();
            if (fileNum <= 0xFF) {
                bytes[3] = fileNum.byteValue();
            } else if (fileNum <= 0xFFFF) {
                bytes[3] = (byte) (fileNum & 0xFF);
                bytes[2] = (byte) (fileNum >> 8 & 0xFF);
            } else if (fileNum <= 0xFFFFFF) {
                bytes[3] = (byte) (fileNum & 0xFF);
                bytes[2] = (byte) (fileNum >> 8 & 0xFF);
                bytes[1] = (byte) (fileNum >> 8 * 2 & 0xFF);
            }


            byte[] fileBytes = FileUtils.readFileToByteArray(file);
            byte[] filePayload = new byte[requestLen];
            System.arraycopy(fileBytes, startIndex, filePayload, 0, requestLen);
            System.arraycopy(filePayload, 0, bytes, 4, requestLen);
            int CRC = ByteUtil.getCRC(filePayload); // 获取CRC16(MODBUS) 校验码
            bytes[responseLen - 2] = (byte) (CRC & 0xFF);
            bytes[responseLen - 1] = (byte) (CRC >> 8 & 0xFF);
            return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

}
