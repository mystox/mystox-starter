package com.kongtrolink.framework.scloud.api;


import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.his.JobMessageAckEntity;
import com.kongtrolink.framework.scloud.entity.his.JobMessageEntity;
import com.kongtrolink.framework.scloud.service.JobUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/9/9, 13:03.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class TerminalCommandServiceImpl implements TerminalCommandService {

    private static final Logger logger = LoggerFactory.getLogger(TerminalCommandServiceImpl.class);

    @Autowired
    JobUpdateService jobUpdateService;
    /**
     * 历史数据处理
     */
    @Override
    public String updateFsuPolling(String message) {
        logger.info("receive: {}",message);
        JobMessageEntity jobMessageEntity = JSONObject.parseObject(message,JobMessageEntity.class);
        JobMessageAckEntity value = jobUpdateService.onMessage(jobMessageEntity);
        return JSONObject.toJSONString(value);
    }




}
