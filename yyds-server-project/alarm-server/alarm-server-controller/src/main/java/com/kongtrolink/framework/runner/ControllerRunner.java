package com.kongtrolink.framework.runner;

import com.kongtrolink.framework.config.ReportOperateConfig;
import com.kongtrolink.framework.config.ResloverOperateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2019/11/6 15:38
 * @Description:
 */
@Service
public class ControllerRunner implements ApplicationRunner {

    @Autowired
    ReportOperateConfig reportOperateConfig;
    @Autowired
    ResloverOperateConfig resloverOperateConfig;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        reportOperateConfig.initConfigMap();
        resloverOperateConfig.initConfigMap();
    }
}
