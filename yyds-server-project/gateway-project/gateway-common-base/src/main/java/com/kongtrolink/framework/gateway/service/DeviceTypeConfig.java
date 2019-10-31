package com.kongtrolink.framework.gateway.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushDeviceAssetDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 设备资产映射表
 * Created by Mag on 2019/10/18.
 */
@Service
public class DeviceTypeConfig {

    private Logger logger = LoggerFactory.getLogger(DeviceTypeConfig.class);
    @Value("${gateway.businessCode}")
    private String businessCode; //必须配置
    @Value("${gateway.enterpriseCode}")
    private String enterpriseCode; //必须配置
    @Autowired
    RedisUtils redisUtils;
    /**
     * 读取deviceType映射表
     */
    public  void reFlashRedisDeviceType(){
        /* 读取数据 */
        InputStream inputStream = null;
        BufferedReader br = null;
        try {

            inputStream = this.getClass().getResourceAsStream("/deviceType.txt");
            br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String lineTxt;
            String redisKey = getDeviceTypeRedisKey();
            //删除原先数据
            redisUtils.del(redisKey);
            while ((lineTxt = br.readLine()) != null) {//数据以逗号分隔
                try{
                    //格式 设备名称,上报的设备类型,资管的设备类型
                    String[] names = lineTxt.split(",");
                    String deviceName = names[0];
                    String reportType = names[1];
                    String assentType = names[2];
                    //更新最新数据
                    redisUtils.hset(redisKey,reportType,assentType);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("read errors :" + e);
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("初始化 资管设备类型映射表 完成  !! ");
    }

    /**
     * 获取转换过资管要求的设备的类型
     * @param deviceType 上报的设备类型
     */
    public String getAssentDeviceType(String deviceType){
        if(deviceType==null){
            return null;
        }
        String redisKey = getDeviceTypeRedisKey();
        Object o = redisUtils.hget(redisKey,deviceType);
        if(o==null){
            return null;
        }
        try{
            return String.valueOf(o);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取 设备信息
     * @param sn SN_deviceId
     */
    public PushDeviceAssetDevice getDeviceInfo(String sn){
        if(sn==null){
            return null;
        }
        String redisKey = getDeviceRedisKey();
        Object redisValue = redisUtils.hget(redisKey,sn);
        if(redisValue==null){
            return null;
        }
        try{
            JSONObject jsonObject = (JSONObject)redisValue;
            return  JSON.toJavaObject(jsonObject,PushDeviceAssetDevice.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public String getAssentDeviceType(int deviceType){
        try{
            return getAssentDeviceType(String.valueOf(deviceType));
        }catch (Exception e){
            return null;
        }
    }

    public String getDeviceTypeRedisKey(){
        return "gw_"+businessCode+"_"+enterpriseCode+"_devType";
    }

    public String getDeviceRedisKey(){
        return "gw_"+businessCode+"_"+enterpriseCode+"_device";
    }

}
