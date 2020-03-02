package com.kongtrolink.framework.business.scloud.his.api;


import com.kongtrolink.framework.business.scloud.his.service.HistoryService;
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
    HistoryService historyService;
    /**
     * 历史数据处理
     */
    @Override
    public void scloudHistory(String message) {
        logger.info("receive: {}",message);
        historyService.scloudHistory(message);
    }




}
