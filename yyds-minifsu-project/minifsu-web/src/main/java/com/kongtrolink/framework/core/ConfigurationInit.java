package com.kongtrolink.framework.core;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.model.signal.SignalCode;
import com.kongtrolink.framework.model.station.StationCode;
import com.kongtrolink.framework.model.tier.TierNode;
import com.kongtrolink.framework.util.StationCodeUtil;
import com.kongtrolink.framework.util.TierTreeUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/4 10:46
 * \* Description:
 * \
 */
@Component
public class ConfigurationInit implements ApplicationRunner
{
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Value("classpath:config/tier_data.json")
    private Resource areaRes;
    @Value("classpath:config/d_station_type_code.json")
    private Resource dStation;
    @Value("classpath:config/room_station_type_code.json")
    private Resource roomStation;


    void initTier()
    {
        try
        {
            logger.info("初始化区域层级配置信息... ...");
            String areaData = IOUtils.toString(areaRes.getInputStream(), Charset.forName("UTF-8").toString());
            List<TierNode> tierNodeList = JSONObject.parseArray(areaData, TierNode.class);
            Map<String, String> map = TierTreeUtil.getTireName(tierNodeList, "", "", null);
            ControllerInstance.getInstance().setTierMap(map);
            logger.info("tiercode size is " + map.size());
        } catch (IOException e)
        {
            logger.error("初始化区域层级配置信息失败... ...");
            e.printStackTrace();
        }
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception
    {
//        initTier();
//        initStationType();
//        initSignalCode();
    }

    private void initSignalCode()
    {
        logger.info("初始化信号点code... ...");
//        Resource resource = new ClassPathResource("config/signal");
        try
        {
        File directory = ResourceUtils.getFile("AppResources/config/signal");
            Map<String, SignalCode> signalMap = new HashMap<>();
            File[] files = directory.listFiles();
            logger.info("signal files size is " + files.length);
            for (File file : files)
            {
                List<String> result = FileUtils.readLines(file, Charset.forName("UTF-8").toString());
                for (String signalString : result)
                {
                    if (signalString == null || !signalString.contains(","))
                        continue;
                    String[] signalStrArr = signalString.split(",");
                    SignalCode signalCode = new SignalCode();
                    if (signalStrArr.length > 9)
                    {
                        signalCode.setUnit(signalStrArr[9]);
                    }
                    signalCode.setName(signalStrArr[3]);
                    String signalId = signalStrArr[1];
                    signalMap.put(signalId, signalCode);
                }
            }
            ControllerInstance.getInstance().setSignalMap(signalMap);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void initStationType()
    {
        logger.info("初始化局站code... ...");
        try
        {
            String dStationJson = IOUtils.toString(dStation.getInputStream(), Charset.forName("UTF-8").toString());
            List<StationCode> stationCodes = JSONObject.parseArray(dStationJson, StationCode.class);
            Map<String, String> d_map = StationCodeUtil.toCodeMap(stationCodes);
            ControllerInstance.getInstance().setdStationMap(d_map);
            logger.info("d_station code size is " + d_map.size());
            String roomStationJson = IOUtils.toString(roomStation.getInputStream(), Charset.forName("UTF-8").toString());
            List<StationCode> roomStationCodes = JSONObject.parseArray(roomStationJson, StationCode.class);
            Map<String, String> room_map = StationCodeUtil.toCodeMap(roomStationCodes);
            ControllerInstance.getInstance().setRoomStationMap(room_map);
            logger.info("room_station code size is " + room_map.size());

        } catch (IOException e)
        {
            logger.error("初始化局站code失败......");
            e.printStackTrace();
        }
    }
}