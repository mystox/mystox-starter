package com.kongtrolink.framework.config;

import com.kongtrolink.framework.enttiy.Operate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/29 16:54
 * @Description:
 */
//@Configuration
//@ConfigurationProperties("userServices")
public class OperateConfig {

    private List<Operate> operate;

    public List<Operate> getOperate() {
        return operate;
    }

    public void setOperate(List<Operate> operate) {
        this.operate = operate;
    }
}
