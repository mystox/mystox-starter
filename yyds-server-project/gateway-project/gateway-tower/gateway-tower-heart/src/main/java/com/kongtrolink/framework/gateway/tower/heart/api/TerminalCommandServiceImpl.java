package com.kongtrolink.framework.gateway.tower.heart.api;


import com.kongtrolink.framework.gateway.tower.heart.service.FsuHeartService;
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
    FsuHeartService fsuHeartService;
    /**
     * 心跳处理
     */
    @Override
    public void towerFsuHeart(String message) {
        logger.info("receive: {}",message);
        fsuHeartService.heartBeatFsu(message);
    }




}
