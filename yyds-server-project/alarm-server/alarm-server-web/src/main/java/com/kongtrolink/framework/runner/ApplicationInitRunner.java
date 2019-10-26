package com.kongtrolink.framework.runner;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.dao.MsgTemplateDao;
import com.kongtrolink.framework.enttiy.MsgTemplate;
import com.kongtrolink.framework.service.MsgTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Auther: liudd
 * @Date: 2019/10/26 15:59
 * @Description:系统启动时，初始化方法
 */
@Component
public class ApplicationInitRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInitRunner.class);
    @Autowired
    MsgTemplateService msgTemplateService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        msgTemplateService.initMsgTemplate();
    }

}
