package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.ByteUtil;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.service.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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


    @Autowired
    RedisUtils redisUtils;


    @Override
    public byte[] fileGet(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        String msgId = moduleMsg.getMsgId();
        JSONObject payload = moduleMsg.getPayload();//设备信息包的报文

        //获取redis 信息增加升级状态
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        if (value == null) {
            logger.error("[{}] sn [{}] communication msg is null [{}] ", msgId, sn, payload);
            return new byte[]{0};
        }

        Integer status = value.getInteger("STATUS");
        if (status != null && status != 3) {
            if (status == 0) {
                logger.error("[{}] sn [{}] communication status is 0 [{}] ", msgId, sn, payload);
                return new byte[]{0};
            } else if (status == 2) {
                logger.warn("[{}] sn [{}] communication status set to 3 [{}] ", msgId, sn, payload);
                value.put("STATUS", 3); //新增升级状态
            }
        }
        int expiredTime = value.getInteger("expired");
        redisUtils.set(key, value, expiredTime);

        logger.info("[{}] sn [{}]  get file [{}] ", msgId, sn, payload);
        JSONObject fileMsg = (JSONObject) payload.get("file");
        String filename = (String) fileMsg.get("fileName");
        Integer fileType = (Integer) fileMsg.get("type");
        Integer fileNum = (Integer) fileMsg.get("fileNum");
        Integer startIndex = (Integer) fileMsg.get("startIndex");
        Integer endIndex = (Integer) fileMsg.get("endIndex");
        try {
            File file = ResourceUtils.getFile(snPath + File.separator + sn + File.separator + filename);
            if (!file.exists()) {
                logger.error("[{}] sn [{}]  FILE [{}] not exists!!!", msgId, sn, file.getAbsoluteFile());
                return new byte[]{0};
            }
            long fileLen = file.length();
            if (startIndex ==0) //锁定文件
                file.setWritable(false);
            if (fileLen > Integer.MAX_VALUE)
                logger.error("[{}] sn [{}]  FILE [{}] is to large then MAX_INTEGER!!!", msgId, sn, file.getAbsoluteFile());
            if (fileLen < startIndex + 1) {// 起始index 大于文件长度返回错误
                logger.error("[{}] sn [{}]  FILE [{}] fileLen[{}] < startIndex[{}]+ 1!!!", msgId, sn, fileLen, startIndex);
                return new byte[]{0};
            }
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


            if (fileLen == startIndex + 1) //升级最后一包释放文件锁
                file.setWritable(true);
            return bytes;
        } catch (FileNotFoundException e) {
            logger.error("[{}] sn [{}]  FILE exception [{}] !!!", msgId, sn, e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("[{}] sn [{}]  FILE exception [{}] !!!", msgId, sn, e.toString());
            e.printStackTrace();
        }

        return new byte[]{0};
    }

    @Override
    public JSONObject getCompilerFile(ModuleMsg moduleMsg) {


        String sn = moduleMsg.getSN();
        String msgId = moduleMsg.getMsgId();
        JSONObject param = moduleMsg.getPayload();
        logger.info("[{}] sn [{}]  get compilerFile [{}] ", msgId, sn, param);
        String urlStr = (String) param.get("url");
        if (StringUtils.isNotBlank(urlStr))
        {
            urlStr=  urlStr.replace("\\", "/"); //系统容错
        }
        String name = (String) param.get("name");
        InputStream is = null;
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(urlStr);
            URLConnection urlc = url.openConnection();
            urlc.setConnectTimeout(100000);
            urlc.setReadTimeout(100000);
            is = urlc.getInputStream();
            File dir = ResourceUtils.getFile(snPath + File.separator + sn + File.separator);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = ResourceUtils.getFile(snPath + File.separator + sn + File.separator + name);

            if (file.exists()) {
                if(file.canWrite()) {
                    file.deleteOnExit();
                }
                else {
                    logger.error("[{}] sn [{}] file can not write..");
                    jsonObject.put("result", 0);
                    jsonObject.put("info", "file can not write");
                    return jsonObject;
                }
            }
            file.createNewFile();
            FileUtils.copyInputStreamToFile(is, file);
            jsonObject.put("totalLen", file.length());
            jsonObject.put("fileName", file.getName());
            jsonObject.put("result", 1);
        } catch (IOException e) {
            jsonObject.put("result", 0);
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

}
